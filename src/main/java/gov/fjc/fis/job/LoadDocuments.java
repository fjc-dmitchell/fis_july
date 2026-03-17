package gov.fjc.fis.job;

import io.jmix.email.*;
import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import gov.fjc.fis.entity.Document;
import gov.fjc.fis.entity.dto.PurchaseOrderDto;
import gov.fjc.fis.entity.dto.TravelAuthorizationDto;
import io.jmix.core.UnconstrainedDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static gov.fjc.fis.FisUtilities.cleanText;

/**
 * Job to load Document files (purchase and travel) into FIS 2.1 and trigger processing job.
 * Both the file directory and archive directory must exist with proper permissions and
 * have property keys defined. If email addresses properties are not configured,
 * job will attempt to send email to all administrators.
 * Dependencies: Quartz and Email libraries must be installed and configured (e.g., smtp server).
 *
 * @author Doug Mitchell
 * @version 2.1
 * @since 2.1
 */
@Component("fis_LoadDocuments")
public class LoadDocuments implements Job {
    @Autowired
    UnconstrainedDataManager unconstrainedDataManager;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private Emailer emailer;
    @Autowired
    Scheduler scheduler;

    private static final Logger log = LoggerFactory.getLogger(gov.fjc.fis.job.LoadDocuments.class);

    // ToDo: request to AO to limit feeds to five years
    private final String startingYear = "2021";

    @Value("${jifms.feed.directory}")
    private String feedDirectory;
    @Value("${jifms.archive.directory}")
    private String archiveDirectory;
    @Value("${jifms.travel.file}")
    private String travelFile;
    @Value("${jifms.purchase.file}")
    private String purchaseFile;
    @Value("${jifms.email.job-status.addresses}")
    private String jobStatusEmailAddresses;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        var purchaseFilePath = feedDirectory.concat("/").concat(purchaseFile);
        var travelFilePath = feedDirectory.concat("/").concat(travelFile);
        var purchaseFileExists = Files.exists(Paths.get(purchaseFilePath));
        var travelFileExists = Files.exists(Paths.get(travelFilePath));

        if (purchaseFileExists && travelFileExists) {
            jdbcTemplate.execute("TRUNCATE TABLE FIS_DOCUMENT");
            loadPurchaseOrders(purchaseFilePath);
            loadTravelAuthorizations(travelFilePath);
            log.info("Load Documents has been executed: ".concat(purchaseFilePath).concat(", ").concat(travelFilePath));
            archiveFile(purchaseFile);
            archiveFile(travelFile);

            try {
                JobKey jobKey = new JobKey("Process Documents", "JIFMS");
                scheduler.triggerJob(jobKey);
            } catch (SchedulerException e) {
                sendAdminEmail("JIFMS feed processing ABENDED",
                        "<strong>LoadDocuments</strong> failed to trigger <strong>ProcessDocuments</strong>",
                        null);
                throw new JobExecutionException("LoadDocuments failed to trigger ProcessDocuments");
            }
        } else {
            var body = new StringBuilder();
            if (!purchaseFileExists && !travelFileExists) {
                body.append("<strong>Neither purchase.CSV nor travel.CSV files were found:</strong><br /><br />");
                body.append(purchaseFilePath);
                body.append("<br />");
                body.append(travelFilePath);
                log.info("purchase.CSV and travel.CSV were not found. Processing aborted.");
            } else if (!purchaseFileExists) {
                body.append("<strong>purchase.CSV file was not found:</strong><br /><br />");
                body.append(purchaseFilePath);
                log.info("purchase.CSV was not found. Processing aborted.");
            } else {
                body.append("<strong>travel.CSV file was not found:</strong><br /><br />");
                body.append(purchaseFilePath);
                log.info("travel.CSV was not found. Processing aborted.");
            }
            body.append("<br /><br /><strong>Documents are untouched. Processing has been aborted.</strong>");
            sendAdminEmail("JIFMS feed processing ABENDED", body.toString(), null);
        }
    }

    private void archiveFile(String fileName) {
        var formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd_HHmm");
        var dateTimeString = LocalDateTime.now().format(formatter);

        var newFileName = fileName.concat(".").concat(dateTimeString);

        var oldFilePath = feedDirectory.concat("/").concat(fileName);
        var newFilePath = archiveDirectory.concat("/").concat(newFileName);

        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);

        if (oldFile.renameTo(newFile)) {
            log.info(String.format("File %s renamed %s", oldFilePath, newFilePath));
        } else {
            log.info(String.format("File %s could not be renamed!", oldFilePath));
            var error = String.format("Unable to rename file %s to %s", oldFilePath, newFilePath);
            Path path = Paths.get(oldFilePath);
            try {
                byte[] fileBytes = Files.readAllBytes(path);
                EmailAttachment attachment = new EmailAttachment(fileBytes, fileName);
                sendAdminEmail("JIFMS feed email problem", error, List.of(attachment));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadPurchaseOrders(String purchaseFilePath) {
        try (Reader reader = new FileReader(purchaseFilePath)) {
            CsvToBean<PurchaseOrderDto> csvToBean = new CsvToBeanBuilder<PurchaseOrderDto>(reader)
                    .withType(PurchaseOrderDto.class)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            for (PurchaseOrderDto dto : csvToBean) {
                if (dto.getFundCode().equals("51140X") && !dto.getBudgetOrg().equals("JXXXXXF")) {
                    continue;
                }
                if (dto.getBbfy().compareTo(startingYear) >= 0) {
                    createPurchaseDocument(dto);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTravelAuthorizations(String travelFilePath) {
        try (Reader reader = new FileReader(travelFilePath)) {
            CsvToBean<TravelAuthorizationDto> csvToBean = new CsvToBeanBuilder<TravelAuthorizationDto>(reader)
                    .withType(TravelAuthorizationDto.class)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            for (TravelAuthorizationDto dto : csvToBean) {
                if (dto.getFundCode().equals("51140X") && !dto.getBudgetOrg().equals("JXXXXXF")) {
                    continue;
                }
                if (dto.getBbfy().compareTo(startingYear) >= 0) {
                    createTravelDocument(dto);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmailsByRoleAsDelimitedString(String roleCode) {
        List<String> emails = unconstrainedDataManager.loadValues(
                        "SELECT u.email FROM fis_User u"
                                + " JOIN sec_RoleAssignmentEntity r ON u.username = r.username"
                                + " WHERE u.email IS NOT NULL AND r.roleCode = :roleCode")
                .parameter("roleCode", roleCode)
                .properties("email")
                .list()
                .stream()
                .map(kv -> (String) kv.getValue("email"))
                .collect(Collectors.toList());
        return String.join(",", emails);
    }

    private void sendAdminEmail(String subject, String body, List<EmailAttachment> emailAttachment) {
        // if addresses not configured, send email to all administrators
        if (jobStatusEmailAddresses == null) {
            jobStatusEmailAddresses = getEmailsByRoleAsDelimitedString("system-full-access");
        }
        if (jobStatusEmailAddresses != null) {
            EmailInfo emailInfo = EmailInfoBuilder.create()
                    .setAddresses(jobStatusEmailAddresses)
                    .setSubject(subject)
                    .setBody(body)
                    .setAttachments(emailAttachment)
                    .setBodyContentType("text/html; charset=UTF-8")
                    .build();
            try {
                emailer.sendEmail(emailInfo);
            } catch (EmailException e) {
                log.info("Email exception occurred:".concat(e.getMessage()));
            }
        }
    }

    private void createPurchaseDocument(PurchaseOrderDto dto) {
        Document purchaseDocument = unconstrainedDataManager.create(Document.class);
        purchaseDocument.setFundCode(dto.getFundCode());
        purchaseDocument.setBbfy(dto.getBbfy());
        purchaseDocument.setEbfy(dto.getEbfy());
        purchaseDocument.setBudgetOrg(dto.getBudgetOrg());
        purchaseDocument.setCostOrg(dto.getCostOrg());
        purchaseDocument.setDocumentType(dto.getDocumentType());
        purchaseDocument.setDocumentNumber(dto.getDocumentNumber());
        purchaseDocument.setDocumentDate(dto.getDocumentDate());
        purchaseDocument.setDocumentCreationDate(dto.getDocumentCreationDate());
        purchaseDocument.setTitle(cleanText(dto.getTitle()));
        purchaseDocument.setBudgetObjectClass(dto.getBudgetObjectClass());
        purchaseDocument.setMasterObjectClass(dto.getMasterObjectClass());
        purchaseDocument.setProject(dto.getProject());
        purchaseDocument.setAmount(dto.getAmount());
        purchaseDocument.setLineNumber(dto.getLineNumber());
        purchaseDocument.setTaxId(dto.getTaxId());
        purchaseDocument.setTaxIdType(dto.getTaxIdType());
        purchaseDocument.setAddressCode(dto.getAddressCode());
        purchaseDocument.setVendorCode(dto.getVendorCode());
        purchaseDocument.setVendorName(cleanText(dto.getVendorName()));
        purchaseDocument.setExpendedAmount(dto.getExpendedAmount());
        purchaseDocument.setClosedAmount(dto.getClosedAmount());
        purchaseDocument.setClosedDate(dto.getClosedDate());
        purchaseDocument.setLastModifiedBy(dto.getLastModifiedBy());
        purchaseDocument.setFjc(dto.getFjc());
        purchaseDocument.setOrderedAmount(dto.getOrderedAmount());
        purchaseDocument.setOutstandingAmount(dto.getOutstandingAmount());
        purchaseDocument.setPrepaidAmount(dto.getPrepaidAmount());
        purchaseDocument.setRefundedAmount(dto.getRefundedAmount());
        purchaseDocument.setCreatedBy("dmitchell");
        purchaseDocument.setCreatedDate(OffsetDateTime.now());
        unconstrainedDataManager.save(purchaseDocument);
    }

    private void createTravelDocument(TravelAuthorizationDto dto) {
        Document travelDocument = unconstrainedDataManager.create(Document.class);
        travelDocument.setFundCode(dto.getFundCode());
        travelDocument.setBbfy(dto.getBbfy());
        travelDocument.setEbfy(dto.getEbfy());
        travelDocument.setBudgetOrg(dto.getBudgetOrg());
        travelDocument.setCostOrg(dto.getCostOrg());
        travelDocument.setDocumentType(dto.getDocumentType());
        travelDocument.setDocumentNumber(dto.getDocumentNumber());
        travelDocument.setDocumentDate(dto.getDocumentDate());
        travelDocument.setDocumentCreationDate(dto.getDocumentCreationDate());
        travelDocument.setTitle(cleanText(dto.getTitle()));
        travelDocument.setBudgetObjectClass(dto.getBudgetObjectClass());
        travelDocument.setMasterObjectClass(dto.getMasterObjectClass());
        travelDocument.setProject(dto.getProject());
        travelDocument.setAmount(dto.getAmount());
        travelDocument.setLineNumber(dto.getLineNumber());
        travelDocument.setVendorCode(dto.getVendorCode());
        travelDocument.setVendorName(cleanText(dto.getVendorName()));
        travelDocument.setTravelStartDate(dto.getTravelStartDate());
        travelDocument.setTravelEndDate(dto.getTravelEndDate());
        travelDocument.setExpendedAmount(dto.getExpendedAmount());
        travelDocument.setClosedAmount(dto.getClosedAmount());
        travelDocument.setClosedDate(dto.getClosedDate());
        travelDocument.setLastModifiedBy(dto.getLastModifiedBy());
        travelDocument.setFjc(dto.getFjc());
        travelDocument.setOrderedAmount(dto.getOrderedAmount());
        travelDocument.setOutstandingAmount(dto.getOutstandingAmount());
        travelDocument.setPrepaidAmount(dto.getPrepaidAmount());
        travelDocument.setRefundedAmount(dto.getRefundedAmount());
        travelDocument.setCreatedBy("dmitchell");
        travelDocument.setCreatedDate(OffsetDateTime.now());
        unconstrainedDataManager.save(travelDocument);
    }
}