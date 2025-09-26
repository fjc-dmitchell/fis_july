package gov.fjc.fis.job;

import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import gov.fjc.fis.entity.Document;
import gov.fjc.fis.entity.dto.PurchaseOrderDto;
import gov.fjc.fis.entity.dto.TravelAuthorizationDto;
import io.jmix.core.UnconstrainedDataManager;
import io.jmix.email.EmailException;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.fjc.fis.FisUtilities.getDateTime;
import static gov.fjc.fis.FisUtilities.getDateTimeFilenameString;

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

    // ToDo: ask AO to limit feeds to five years
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
                log.info("LoadDocuments failed to trigger ProcessDocuments");
                throw new JobExecutionException("LoadDocuments failed to trigger ProcessDocuments");
            }
        } else {
            sendFileNotFoundEmail(purchaseFileExists, travelFileExists, purchaseFilePath, travelFilePath);
        }
    }

    private void archiveFile(String fileName) {
        var dateTimeString = getDateTimeFilenameString(getDateTime());
        var newFileName = fileName.concat(".").concat(dateTimeString);

        var oldFilePath = feedDirectory.concat("/").concat(fileName);
        var newFilePath = archiveDirectory.concat("/").concat(newFileName);

        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);

        if (oldFile.renameTo(newFile)) {
            log.info(String.format("File %s renamed %s", oldFilePath, newFilePath));
        } else {
            log.info(String.format("File %s could not be renamed!", oldFilePath));
            // email should be sent. file should be removed and attached to email
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
                    createDocument(dto);
                    System.out.println(dto);
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
                        "SELECT u.email FROM fis_User u JOIN sec_RoleAssignmentEntity r ON u.username = r.username"
                                + " WHERE u.email IS NOT NULL AND r.roleCode = :roleCode"
                )
                .parameter("roleCode", roleCode)
                .properties("email")
                .list()
                .stream()
                .map(kv -> (String) kv.getValue("email"))
                .collect(Collectors.toList());
        return String.join(",", emails);
    }


    private void sendFileNotFoundEmail(boolean purchaseFileExists, boolean travelFileExists,
                                       String purchaseFilePath, String travelFilePath) {
        if (!purchaseFileExists || !travelFileExists) {
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

            // if addresses not configured, send email to all administrators
            if (jobStatusEmailAddresses == null) {
                jobStatusEmailAddresses = getEmailsByRoleAsDelimitedString("system-full-access");
            }
            if (jobStatusEmailAddresses != null) {

                EmailInfo emailInfo = EmailInfoBuilder.create()
                        .setAddresses(jobStatusEmailAddresses)
                        .setSubject("JIFMS feed processing ABENDED")
                        .setBody(body.toString())
                        .setBodyContentType("text/html; charset=UTF-8")
                        .build();

                try {
                    emailer.sendEmail(emailInfo);
                } catch (EmailException e) {
                    log.info("Email exception occurred:".concat(e.getMessage()));
                }
            }
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
                    createDocument(dto);
                    System.out.println(dto);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDocument(PurchaseOrderDto dto) {
        Document document = unconstrainedDataManager.create(Document.class);
        document.setFundCode(dto.getFundCode());
        document.setBbfy(dto.getBbfy());
        document.setEbfy(dto.getEbfy());
        document.setBudgetOrg(dto.getBudgetOrg());
        document.setCostOrg(dto.getCostOrg());
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setDocumentDate(dto.getDocumentDate());
        document.setDocumentCreationDate(dto.getDocumentCreationDate());
        document.setTitle(dto.getTitle());
        document.setBudgetObjectClass(dto.getBudgetObjectClass());
        document.setMasterObjectClass(dto.getMasterObjectClass());
        document.setProject(dto.getProject());
        document.setAmount(dto.getAmount());
        document.setLineNumber(dto.getLineNumber());
        document.setTaxId(dto.getTaxId());
        document.setTaxIdType(dto.getTaxIdType());
        document.setAddressCode(dto.getAddressCode());
        document.setVendorCode(dto.getVendorCode());
        document.setVendorName(dto.getVendorName());
//        document.setTravelStartDate(dto.getTravelStartDate());
//        document.setTravelEndDate(dto.getTravelEndDate());
        document.setExpendedAmount(dto.getExpendedAmount());
        document.setClosedAmount(dto.getClosedAmount());
        document.setClosedDate(dto.getClosedDate());
        document.setLastModifiedBy(dto.getLastModifiedBy());
        document.setFjc(dto.getFjc());
        document.setOrderedAmount(dto.getOrderedAmount());
        document.setOutstandingAmount(dto.getOutstandingAmount());
        document.setPrepaidAmount(dto.getPrepaidAmount());
        document.setRefundedAmount(dto.getRefundedAmount());
        document.setCreatedBy("dmitchell");
        document.setCreatedDate(OffsetDateTime.now());
        unconstrainedDataManager.save(document);
    }

    private void createDocument(TravelAuthorizationDto dto) {
        Document document = unconstrainedDataManager.create(Document.class);
        document.setFundCode(dto.getFundCode());
        document.setBbfy(dto.getBbfy());
        document.setEbfy(dto.getEbfy());
        document.setBudgetOrg(dto.getBudgetOrg());
        document.setCostOrg(dto.getCostOrg());
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setDocumentDate(dto.getDocumentDate());
        document.setDocumentCreationDate(dto.getDocumentCreationDate());
        document.setTitle(dto.getTitle());
        document.setBudgetObjectClass(dto.getBudgetObjectClass());
        document.setMasterObjectClass(dto.getMasterObjectClass());
        document.setProject(dto.getProject());
        document.setAmount(dto.getAmount());
        document.setLineNumber(dto.getLineNumber());
//        document.setTaxId(dto.getTaxId());
//        document.setTaxIdType(dto.getTaxIdType());
//        document.setAddressCode(dto.getAddressCode());
        document.setVendorCode(dto.getVendorCode());
        document.setVendorName(dto.getVendorName());
        document.setTravelStartDate(dto.getTravelStartDate());
        document.setTravelEndDate(dto.getTravelEndDate());
        document.setExpendedAmount(dto.getExpendedAmount());
        document.setClosedAmount(dto.getClosedAmount());
        document.setClosedDate(dto.getClosedDate());
        document.setLastModifiedBy(dto.getLastModifiedBy());
        document.setFjc(dto.getFjc());
        document.setOrderedAmount(dto.getOrderedAmount());
        document.setOutstandingAmount(dto.getOutstandingAmount());
        document.setPrepaidAmount(dto.getPrepaidAmount());
        document.setRefundedAmount(dto.getRefundedAmount());
        document.setCreatedBy("dmitchell");
        document.setCreatedDate(OffsetDateTime.now());
        unconstrainedDataManager.save(document);
    }
}