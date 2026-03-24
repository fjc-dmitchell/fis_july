package gov.fjc.fis.job;

import io.jmix.email.*;
import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import gov.fjc.fis.entity.dto.PurchaseOrderDto;
import gov.fjc.fis.entity.dto.TravelAuthorizationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
public class LoadDocuments implements Job {
    @Autowired
    UnconstrainedQueries unconstrainedQueries;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private Emailer emailer;
    @Autowired
    Scheduler scheduler;

    private static final Logger log = LoggerFactory.getLogger(LoadDocuments.class);

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
                    unconstrainedQueries.createPurchaseDocument(dto);
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
                    unconstrainedQueries.createTravelDocument(dto);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAdminEmail(String subject, String body, List<EmailAttachment> emailAttachment) {
        // if addresses not configured, send email to all administrators
        if (jobStatusEmailAddresses == null) {
            jobStatusEmailAddresses = unconstrainedQueries.getEmailsByRoleAsDelimitedString("system-full-access");
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
}