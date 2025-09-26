package gov.fjc.fis.job.document;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LoadDocuments implements Job {

    @Autowired
    UnconstrainedDataManager unconstrainedDataManager;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private Emailer emailer;

    private static final Logger log = LoggerFactory.getLogger(LoadDocuments.class);

    // ToDo: ask AO to limit feeds to five years
    private final String startingYear = "2021";

    @Value("${jifms.purchase.file.path}")
    private String purchaseFilePath;
    @Value("${jifms.travel.file.path}")
    private String travelFilePath;
    @Value("${jifms.email.error.addresses}")
    private String emailErrorAddresses;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Load Documents has been executed: ".concat(purchaseFilePath).concat(", ").concat(travelFilePath));

        if (!Files.exists(Paths.get(purchaseFilePath))) {
            log.info("Purchase file does not exist: ".concat(purchaseFilePath));
        }
        if (!Files.exists(Paths.get(travelFilePath))) {
            log.info("Travel file does not exist: ".concat(travelFilePath));
        }

        fileNotFound();

        truncateDocuments(); // only do this if document files exist
        loadPurchaseOrders();
        loadTravelAuthorizations();

    }

    @Transactional
    public void truncateDocuments() {
        entityManager.createNativeQuery("TRUNCATE TABLE FIS_DOCUMENT").executeUpdate();
    }

    private void loadPurchaseOrders() {
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


    private void fileNotFound() {
        // if addresses not configured, send email to all administrators with email addresses
        if (emailErrorAddresses == null) {
            emailErrorAddresses = getEmailsByRoleAsDelimitedString("admin");
        }

        var ofmUsers = getEmailsByRoleAsDelimitedString("resources-ofm-user");
        var fullaccess = getEmailsByRoleAsDelimitedString("system-full-access");

        String body = "OFM Users: ".concat(ofmUsers).concat("\nFull access: ").concat(fullaccess);
        EmailInfo emailInfo = EmailInfoBuilder.create(fullaccess,
                        "JIFMS feed", body)
                .build();
        try {
            emailer.sendEmail(emailInfo);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    private void loadTravelAuthorizations() {
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
