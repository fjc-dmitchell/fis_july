package gov.fjc.fis.job;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.PurchaseOrderDto;
import gov.fjc.fis.entity.dto.TravelAuthorizationDto;
import io.jmix.core.DataManager;
import io.jmix.core.UnconstrainedDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.fjc.fis.FisUtilities.cleanText;

@Component("fis_UnconstrainedQueries")
class UnconstrainedQueries {
    private final UnconstrainedDataManager unconstrainedDataManager;
    private final ZoneId timeZoneId = ZoneId.of("America/New_York");

    UnconstrainedQueries(UnconstrainedDataManager unconstrainedDataManager) {
        this.unconstrainedDataManager = unconstrainedDataManager;
    }

    String getEmailsByRoleAsDelimitedString(String roleCode) {
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

    void createPurchaseDocument(PurchaseOrderDto dto) {
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

    void createTravelDocument(TravelAuthorizationDto dto) {
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

    DocumentAudit createDocumentAudit(Document document) {
        var documentAudit = unconstrainedDataManager.create(DocumentAudit.class);
        documentAudit.setProcessDate(new Date());
        documentAudit.setDocumentFundCode(document.getFundCode());
        documentAudit.setDocumentBbfy(document.getBbfy());
        documentAudit.setDocumentEbfy(document.getEbfy());
        documentAudit.setDocumentBudgetOrg(document.getBudgetOrg());
        documentAudit.setDocumentCostOrg(document.getCostOrg());
        documentAudit.setDocumentDocumentType(document.getDocumentType());
        documentAudit.setDocumentDocumentNumber(document.getDocumentNumber());
        documentAudit.setDocumentDocumentDate(document.getDocumentDate());
        documentAudit.setDocumentDocumentCreationDate(document.getDocumentCreationDate());
        documentAudit.setDocumentTitle(document.getTitle());
        documentAudit.setDocumentBudgetObjectClass(document.getBudgetObjectClass());
        documentAudit.setDocumentProject(document.getProject());
        documentAudit.setDocumentAmount(document.getAmount());
        documentAudit.setDocumentLineNumber(document.getLineNumber());
        documentAudit.setDocumentTaxId(document.getTaxId());
        documentAudit.setDocumentTaxIdType(document.getTaxIdType());
        documentAudit.setDocumentAddressCode(document.getAddressCode());
        documentAudit.setDocumentVendorCode(document.getVendorCode());
        documentAudit.setDocumentVendorName(document.getVendorName());
        documentAudit.setDocumentTravelStartDate(document.getTravelStartDate());
        documentAudit.setDocumentTravelEndDate(document.getTravelEndDate());
        documentAudit.setDocumentExpendedAmount(document.getExpendedAmount());
        documentAudit.setDocumentClosedAmount(document.getClosedAmount());
        documentAudit.setDocumentClosedDate(document.getClosedDate());
        documentAudit.setDocumentLastModifiedBy(document.getLastModifiedBy());
        documentAudit.setDocumentMasterObjectClass(document.getMasterObjectClass());
        documentAudit.setDocumentFjc(document.getFjc());
        return documentAudit;
    }

    void saveDocumentAudit(DocumentAudit documentAudit) {
        unconstrainedDataManager.save(documentAudit);
    }

    Map<String, Fund> getFundMap() {
        return unconstrainedDataManager.load(Fund.class)
                .query("SELECT f FROM fis_Fund f ORDER BY f.fundCode")
                .list()
                .stream()
                .collect(Collectors.toMap(Fund::getFundCode, fund -> fund));
    }

    Fund getTwoYearFund(String twoYearFundCode) {
        var results = unconstrainedDataManager.load(Fund.class)
                .query("SELECT e FROM fis_Fund e"
                        + " WHERE e.fundCode = :twoYearFundCode")
                .parameter("twoYearFundCode", twoYearFundCode)
                .list();
        return results.isEmpty() ? null : results.getFirst();
    }

    List<Appropriation> getOpenAppropriations() {
        return unconstrainedDataManager.load(Appropriation.class)
                .query("SELECT a FROM fis_Appropriation a"
                        + " WHERE a.status = TRUE"
                        + " ORDER BY a.budgetFiscalYear DESC")
                .list();
    }

    List<Division> getAllDivisionsWithBudgetOrgs(Appropriation appropriation) {
        return unconstrainedDataManager.load(Division.class)
                .query("SELECT d FROM fis_Division d"
                        + " JOIN FETCH d.fund JOIN FETCH d.appropriation WHERE d.appropriation = :appropriation"
                        + " AND d.budgetOrg IS NOT NULL AND d.budgetOrg <> ''"
                        + " ORDER BY d.divisionCode")
                .parameter("appropriation", appropriation)
                .list();
    }

    Division getEducationDivision(Appropriation appropriation, String educationDivisionCode) {
        var results = unconstrainedDataManager.load(Division.class)
                .query("SELECT e FROM fis_Division e"
                        + " JOIN FETCH e.fund WHERE e.appropriation = :appropriation"
                        + " AND e.divisionCode = :educationDivisionCode")
                .parameter("appropriation", appropriation)
                .parameter("educationDivisionCode", educationDivisionCode)
                .list();
        return results.isEmpty() ? null : results.getFirst();
    }

    Map<String, ObjectClass> getObjectClassMap(Appropriation appropriation, boolean includeGenerics) {
        return unconstrainedDataManager.load(ObjectClass.class)
                .query("SELECT o FROM fis_ObjectClass o"
                        + " JOIN FETCH o.category WHERE o.category.appropriation = :appropriation"
                        + " AND (:generic = true OR o.budgetObjectClass NOT LIKE '%00')"
                        + " ORDER BY o.budgetObjectClass")
                .parameter("appropriation", appropriation)
                .parameter("generic", includeGenerics)
                .list()
                .stream()
                .collect(Collectors.toMap(ObjectClass::getBudgetObjectClass, objectClass -> objectClass));
    }

    Activity getActivity(Division division, String activityNumber) {
        var results = unconstrainedDataManager.load(Activity.class)
                .query("SELECT a FROM fis_Activity a"
                        + " JOIN FETCH a.fund"
                        + " LEFT JOIN FETCH a.group"
                        + " WHERE a.division = :division AND a.activityNumber = :activityNumber")
                .parameter("division", division)
                .parameter("activityNumber", activityNumber)
                .list();
        return results.isEmpty() ? null : results.getFirst();
    }

    Activity getGenericActivity(Activity activity) {
        return activity.getGroup() == null ? null : getActivity(activity.getDivision(),
                activity.getGroup().getGroupCode().concat("00"));
    }

    ObjectClass getObjectClass(Category category, String budgetObjectClass) {
        var results = unconstrainedDataManager.load(ObjectClass.class)
                .query("SELECT o FROM fis_ObjectClass o"
                        + " WHERE o.category = :category"
                        + " AND o.budgetObjectClass = :budgetObjectClass")
                .parameter("category", category)
                .parameter("budgetObjectClass", budgetObjectClass)
                .list();
        return results.isEmpty() ? null : results.getFirst();
    }

    ActivityProjection getActivityProjection(Activity activity, ObjectClass objectClass) {
        String budgetObjectClass = activity.getGenericProjection() ?
                objectClass.getBudgetObjectClass().substring(0, 2).concat("00") :
                objectClass.getBudgetObjectClass();
        objectClass = getObjectClass(objectClass.getCategory(), budgetObjectClass);
        var results = unconstrainedDataManager.load(ActivityProjection.class)
                .query("SELECT p FROM fis_ActivityProjection p"
                        + " WHERE p.activity = :activity"
                        + " AND p.objectClass= :objectClass")
                .parameter("activity", activity)
                .parameter("objectClass", objectClass)
                .list();
        return results.isEmpty() ? null : results.getFirst();
    }

    void saveActivityProjection(ActivityProjection projection) {
        unconstrainedDataManager.save(projection);
    }

    Obligation getObligation(ObjectClass objectClass, String documentNumber,
                             String budgetOrg, Integer lineNumber) {
        var results = unconstrainedDataManager.load(Obligation.class)
                .query("SELECT o FROM fis_Obligation o"
                        + " JOIN FETCH o.objectClass JOIN FETCH o.activity"
                        + " WHERE o.objectClass = :objectClass AND o.documentNumber=:documentNumber"
                        + " AND (:anyLineNumber=true OR o.lineNumber = :lineNumber)"
                        + " AND (:anyBudgetOrg=true OR o.budgetOrg = :budgetOrg)")
                .parameter("objectClass", objectClass)
                .parameter("anyLineNumber", lineNumber == null)
                .parameter("lineNumber", lineNumber)
                .parameter("anyBudgetOrg", budgetOrg == null)
                .parameter("budgetOrg", budgetOrg)
                .parameter("documentNumber", documentNumber)
                .list();
        return results.isEmpty() ? null : results.getFirst();
    }

    // this was adapted from ProcessDocumentsService.java
    // should return string message
    Obligation createObligation(Activity activity, ObjectClass objectClass, Document document) {
        Obligation newObligation = unconstrainedDataManager.create(Obligation.class);
        newObligation.setActivity(activity);
        newObligation.setObjectClass(objectClass);
        newObligation.setDocumentNumber(document.getDocumentNumber());
        newObligation.setLineNumber(document.getLineNumber());
        newObligation.setAmount(document.getAmount());
        newObligation.setDocumentDate(document.getDocumentDate());
        newObligation.setProcessDate(document.getDocumentCreationDate());
        if (document.getFjc() != null) {
            newObligation.setBlanketPurchaseOrder(document.getFjc().equalsIgnoreCase("bpo"));
        }
        if (document.getDocumentType().toLowerCase().startsWith("mo")) {
            newObligation.setDocumentType(DocumentType.MISCELLANEOUS_OBLIGATION);
        } else {
            newObligation.setDocumentType(DocumentType.TRAVEL_AUTHORIZATION);
            newObligation.setTravelStartDate(document.getTravelStartDate());
            newObligation.setTravelEndDate(document.getTravelEndDate());
        }
        newObligation.setEin(document.getTaxId());
        newObligation.setVendor(document.getTitle());
        newObligation.setVendorCode(document.getVendorCode());

        // is it possible to set CreatedBy and CreatedDate?
        newObligation.setCreatedBy(document.getCreatedBy());
        newObligation.setCreatedDate(document.getDocumentCreationDate().toInstant().atZone(timeZoneId).toOffsetDateTime());

        // what is the purpose of these on insert?
//        newObligation.setLastModifiedBy(document.getLastModifiedBy());
//        newObligation.setLastModifiedDate(OffsetDateTime.now());

        // what about budget org and cost org fields? Should cost org warn about activity?

        newObligation.setAoSend(false);
        newObligation.setAoSyncDate(new Date());
        newObligation.setStatus(document.getClosedDate() == null);
        newObligation.setVersion(1); // will updates automatically increment or do it manually?
        return unconstrainedDataManager.save(newObligation);
    }

    /**
     * get Documents in specific order to allow for detection of duplicate obligation BOCs.
     * JIFMS allows duplicates; FIS does not! Each line represents a difference BOC.
     *
     * @param bbfy   String representing beginning budget fiscal year
     * @param offset starting point for retrieval
     * @param max    maximum number of Documents to be retrieved in batch
     * @return batched List of Documents for a bbfy
     */
    List<Document> getDocuments(String bbfy, int offset, int max) {
        return unconstrainedDataManager.load(Document.class)
                .query("SELECT d FROM fis_Document d"
                        + " WHERE d.bbfy = :bbfy AND NOT EXISTS (SELECT e FROM fis_DocumentException e"
                        + " WHERE e.bbfy = d.bbfy AND e.fundCode = d.fundCode AND e.budgetOrg = d.budgetOrg"
                        + " AND e.budgetObjectClass= d.budgetObjectClass AND e.documentNumber = d.documentNumber)"
                        + " ORDER BY d.bbfy, d.budgetOrg, d.documentNumber, d.budgetObjectClass")
                .parameter("bbfy", bbfy)
                .firstResult(offset)
                .maxResults(max)
                .list();
    }
}