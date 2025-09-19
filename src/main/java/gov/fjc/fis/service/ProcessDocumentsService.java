package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import io.jmix.core.Metadata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to process JIFMS Documents in FIS 2.1. This will be triggered by cron job.
 * This service uses EntityManager, not DataManager, and does not rely on other Spring services.
 * The Education division code and Two Year Fund code are hardcoded in this service.
 * <p>
 * The business rules were specified by Mary Greiner and Nanticha Sansung
 * when JIFMS feeds went into production in September 2019.
 *
 * @author Doug Mitchell
 * @version 2.1
 * @since 2.1
 */
@Component("fis_ProcessDocumentsService")
public class ProcessDocumentsService {
    private static final Logger log = LoggerFactory.getLogger(ProcessDocumentsService.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private Metadata metadata;

    private final String educationDivisionCode = "2";
    private final String twoYearFundCode = "09280M";
    private final String obbbaBudgetOrg = "JXXMAPP";
    private final List<String> travelDocumentTypes = List.of("TA", "TAJ", "JTA");
    private final List<String> purchaseDocumentTypes = List.of("MO", "MOJ");
    private final ZoneId timeZoneId = ZoneId.of("America/New_York");
    private final Date today = new Date();
    private final String processingUser = "JIFMS-FIS processing";
    private AuditState auditState;

    class AuditState {
       // generic activity, etc.
    }

    private List<Document> getDocuments(String bbfy, int offset, int max) {
//        System.out.println("bbfy: " + bbfy + " offset: " + offset + " max: " + max);
        return entityManager.createQuery("SELECT d FROM fis_Document d"
                        + " WHERE d.bbfy = :bbfy AND NOT EXISTS (SELECT e FROM fis_DocumentException e"
                        + " WHERE e.bbfy = d.bbfy AND e.fundCode = d.fundCode AND e.budgetOrg = d.budgetOrg"
                        + " AND e.budgetObjectClass= d.budgetObjectClass AND e.documentNumber = d.documentNumber)")
                .setParameter("bbfy", bbfy)
                .setFirstResult(offset)
                .setMaxResults(max)
                .getResultList();
    }

    private Map<String, Fund> getFundMap() {
        return entityManager.createQuery("SELECT f FROM fis_Fund f ORDER BY f.fundCode", Fund.class)
                .getResultStream().collect(Collectors.toMap(Fund::getFundCode, fund -> fund));
    }

    private Fund getTwoYearFund() {
        TypedQuery<Fund> query = entityManager.createQuery("SELECT e FROM fis_Fund e" +
                " WHERE e.fundCode = :twoYearFundCode", Fund.class);
        query.setParameter("twoYearFundCode", twoYearFundCode);
        List<Fund> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private List<Appropriation> getOpenAppropriations() {
        return entityManager.createQuery("SELECT a FROM fis_Appropriation a"
                        + " WHERE a.status = TRUE ORDER BY a.budgetFiscalYear DESC")
                .getResultList();
    }

    private List<Division> getAllDivisionsWithBudgetOrgs(Appropriation appropriation) {
        return entityManager.createQuery("SELECT d FROM fis_Division d"
                        + " WHERE d.appropriation = :appropriation"
                        + " AND d.budgetOrg IS NOT NULL AND d.budgetOrg <> ''"
                        + " ORDER BY d.divisionCode")
                .setParameter("appropriation", appropriation)
                .getResultList();
    }

    private Division getEducationDivision(Appropriation appropriation) {
        TypedQuery<Division> query = entityManager.createQuery("SELECT e FROM fis_Division e"
                + " WHERE e.appropriation = :appropriation"
                + " AND e.divisionCode = :educationDivisionCode", Division.class);
        query.setParameter("appropriation", appropriation);
        query.setParameter("educationDivisionCode", educationDivisionCode);
        List<Division> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private DivisionAllocation getDivisionAllocation(Division division, Category category) {
        TypedQuery<DivisionAllocation> query = entityManager.createQuery("SELECT e FROM fis_DivisionAllocation e"
                + " WHERE e.division = :division AND e.category = :category", DivisionAllocation.class);
        query.setParameter("division", division);
        query.setParameter("category", category);
        List<DivisionAllocation> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public Map<String, ObjectClass> getObjectClassMap(Appropriation appropriation, boolean includeGenerics) {
        return entityManager.createQuery("SELECT o FROM fis_ObjectClass o"
                                + " INNER JOIN fis_Category c ON o.category = c"
                                + " WHERE c.appropriation = :appropriation"
                                + " AND (:generic = true OR o.budgetObjectClass NOT LIKE '%00')"
                                + " ORDER BY o.budgetObjectClass",
                        ObjectClass.class)

                .setParameter("appropriation", appropriation)
                .setParameter("generic", includeGenerics)
                .getResultStream()
                .collect(Collectors.toMap(ObjectClass::getBudgetObjectClass, objectClass -> objectClass));
    }

    private Activity getActivity(Division division, String activityNumber) {
        TypedQuery<Activity> query = entityManager.createQuery("SELECT a FROM fis_Activity a"
                + " INNER JOIN fis_Division d ON d=a.division"
                + " WHERE a.division = :division"
                + " AND a.activityNumber = :activityNumber", Activity.class);
        query.setParameter("division", division);
        query.setParameter("activityNumber", activityNumber);
        List<Activity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private Activity getGenericActivity(Division division, Group group) {
        var activityNumber = group == null ? "" : group.getGroupCode().concat("00");
        TypedQuery<Activity> query = entityManager.createQuery("SELECT a FROM fis_Activity a"
                + " INNER JOIN fis_Division d ON d=a.division"
                + " WHERE a.division = :division"
                + " AND a.group = :group AND a.activityNumber = :activityNumber", Activity.class);
        query.setParameter("division", division);
        query.setParameter("group", group);
        query.setParameter("activityNumber", activityNumber);
        List<Activity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // eliminate this
    private List<Obligation> getObligation(Activity activity, ObjectClass objectClass, String documentNumber) {
        return entityManager.createQuery("SELECT o FROM fis_Obligation o"
                        + " INNER JOIN fis_Activity a ON a=o.activity"
                        + " WHERE o.activity = :activity AND o.objectClass = :objectClass"
                        + " AND o.documentNumber = :documentNumber")
                .setParameter("activity", activity)
                .setParameter("objectClass", objectClass)
                .setParameter("documentNumber", documentNumber)
                .getResultList();
    }


    private Obligation getObligation(Activity activity, ObjectClass objectClass, String documentNumber, Integer lineNumber) {
        TypedQuery<Obligation> query = entityManager.createQuery("SELECT o FROM fis_Obligation o"
                        + " INNER JOIN fis_Activity a ON a=o.activity"
                        + " WHERE o.activity = :activity AND o.objectClass = :objectClass"
                        + " AND o.documentNumber = :documentNumber AND o.lineNumber = :lineNumber",
                Obligation.class);
        query.setParameter("activity", activity);
        query.setParameter("objectClass", objectClass);
        query.setParameter("documentNumber", documentNumber);
        query.setParameter("lineNumber", lineNumber);
        List<Obligation> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }


    /**
     * Process JIFMS Documents that have already been loaded and scrubbed (i.e., documents since FY2020,
     * JITF fund documents belonging to FJC budget org JXXXXXF, etc.)
     * <p>
     * This method will be triggered by Cron or Quartz job schedule.
     */
    @Transactional
    public void processDocuments() {

        // retain map of funds and list of open appropriations for duration of job
        var fundMap = getFundMap();
        var twoYearFund = getTwoYearFund();
        var appropriations = getOpenAppropriations();
        ZoneId newYorkZone = ZoneId.of("America/New_York");

        Fund fund;
        Division division;
        Activity activity;
        Activity genericActivity;
        ActivityProjection activityProjection;
        Category category;
        ObjectClass objectClass;
        Obligation obligation;
        List<Document> documents;

        for (var appropriation : appropriations) {
            List<Division> divisionList = getAllDivisionsWithBudgetOrgs(appropriation);
            Division education = getEducationDivision(appropriation);
            Map<String, ObjectClass> bocMap = getObjectClassMap(appropriation, true);

            var bbfy = appropriation.getBudgetFiscalYear();

            // fetch document entities in small batches to reduce memory overhead
            int offset = 0;
            int max = 100; // process 100 documents per batch
            while ((documents = getDocuments(bbfy, offset, max)).size() > 0) {

                for (var document : documents) {

                    var validDocument = true;
                    var processStatus = "";
                    var loggedChanges = new StringBuffer();
                    auditState = new AuditState();
                    division = null;
                    objectClass = null;
                    activity = null;
                    genericActivity = null;

                    // validate Fund
                    fund = fundMap.get(document.getFundCode());
                    if (fund == null) {
                        validDocument = false;
                        loggedChanges.append(String.format("invalid fund: %s.", document.getFundCode());
                    }

                    // validate Division
                    if (validDocument) {
                        List<Division> foundDivisions = divisionList.stream()
                                .filter(d -> d.getBudgetOrg().equals(document.getBudgetOrg())
                                        && (d.getFund().getFundCode().equals(document.getFundCode())
                                        || (d.equals(education)
                                        && document.getFundCode().equals(twoYearFund.getFundCode()))))
                                .toList();
                        if (foundDivisions.size() != 1) {
                            validDocument = false;
                            if (foundDivisions.isEmpty()) {
                                loggedChanges.append(String.format("invalid budgetOrg: %s.",
                                        document.getBudgetOrg()));
                            } else {
                                loggedChanges.append(String.format("multiple divisions matching budgetOrg: %s.",
                                        document.getBudgetOrg()));
                            }
                        } else {
                            division = foundDivisions.get(0);
                        }
                    }

                    // validate ObjectClass
                    if (validDocument) {
                        objectClass = bocMap.get(document.getBudgetObjectClass());
                        if (objectClass == null) {
                            validDocument = false;
                            loggedChanges.append(String.format("invalid objectClass: %s.",
                                    document.getBudgetObjectClass()));
                        }
                    }

                    // validate activity
                    if (validDocument) {
                        activity = getActivity(division, document.getProject());
                        if (activity == null) {
                            validDocument = false;
                            loggedChanges.append(String.format("invalid activity: %s.", document.getProject()));
                        } else if (activity.getFund() != fund) {
                            validDocument = false;
                            loggedChanges.append(String.format("invalid activity fund: %s.", activity.getFund()));
                        }
                        genericActivity = getGenericActivity(division, activity.getGroup());
                    }

                    // validate documentNumber
                    if (validDocument) {
                        if (!validDocumentNumber(division, document)) {
                            validDocument = false;
                            loggedChanges.append(String.format("invalid documentNumber: %s.",
                                    document.getDocumentNumber()));
                        }
                    }

                    // validate Obligation & insert if necessary
                    if(validDocument) {
                        obligation = getObligation(activity, objectClass, document.getDocumentNumber(), document.getLineNumber());
                        if(obligation == null) {
                            createObligation(activity, objectClass, document);
                            loggedChanges.append(String.format("NEW Obligation: %s.", document.getDocumentNumber()));
                        } else {
                            var log = updateObligation(activity, obligation, document);
                            loggedChanges.append(String.format("UPDATE Obligation: %s.", document.getDocumentNumber()));
                            // mention each field changed
                        }
                    }

                    // validate Allocation and insert if necessary
                    if (validDocument) {
                        category = objectClass.getCategory();
                        var allocation = getDivisionAllocation(division, category);
                        if (allocation == null) {
                            DivisionAllocation divisionAllocation = metadata.create(DivisionAllocation.class);
                            divisionAllocation.setDivision(division);
                            divisionAllocation.setCategory(category);
                            divisionAllocation.setOneYearAmount(BigDecimal.ZERO);
                            divisionAllocation.setTwoYearAmount(BigDecimal.ZERO);
                            entityManager.persist(divisionAllocation);
                            loggedChanges.append(String.format(" Created zero allocation for moc %s.",
                                    category.getMasterObjectClass()));
                        }
                    }

                    // create FCN if obligation amount updated
                    // validate projection & insert if necessary

                }
                offset += documents.size();
            }
        }
    }

    // should return string message
    private void createObligation(Activity activity, ObjectClass objectClass, Document document) {
        Obligation newObligation = metadata.create(Obligation.class);
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
        newObligation.setLastModifiedBy(document.getLastModifiedBy());
        newObligation.setLastModifiedDate(OffsetDateTime.now());

        // what about budget org and cost org fields? Should cost org warn about activity?

        newObligation.setAoSend(false);
        newObligation.setAoSyncDate(today);
        newObligation.setStatus(document.getClosedDate() == null);
        newObligation.setVersion(1); // will updates automatically increment or do it manually?
        entityManager.persist(newObligation);
    }

    public String updateObligation(Activity activity, ObjectClass objectClass, Obligation obligation, Document document) {
        String changes = "";
        if(obligation.getAmount()!=document.getAmount()) {
            obligation.setAmount(document.getAmount());
            changes += " -amount";
        }
        if(obligation.getVendor()!=document.getTitle()) {
            obligation.setVendor(document.getTitle());
            changes += " -title/vendor";
        }
        // what about fetch plan? add to obligation fetch?
        if(obligation.getActivity().getActivityNumber()!=document.getProject()) {
            obligation.setActivity(activity);
            changes += " -activity/project";
        }
        if(obligation.getTravelStartDate()!=document.getTravelStartDate()) {
            obligation.setTravelStartDate(document.getTravelStartDate());
            changes += " -travel/start_date";
        }
        if(obligation.getTravelEndDate()!=document.getTravelEndDate()) {
            obligation.setTravelEndDate(document.getTravelEndDate());
            changes += " -travel/end_date";
        }
        // rule from September 2019, if FIS closed and JIFMS open, keep closed. Why?
        var closed = document.getClosedDate()!=null;
        if(closed && !obligation.getStatus()) {
            obligation.setStatus(false);
            changes += " -status";
        }
        // what about fetch plan? add to obligation fetch?
        if(obligation.getObjectClass().getBudgetObjectClass() != document.getBudgetObjectClass()) {
           obligation.setObjectClass(objectClass);
           changes += " -BOC";
        }
        if(obligation.getVendorCode()!=document.getVendorCode()) {
            obligation.setVendorCode(document.getVendorCode());
            changes += " -vendor code";
        }

        // update amounts in audit state, to be used by projection/fcn
        // update create-FCN flag in audit state
        // update obligation boc change - affects projections
        // update activity change - affects projections

        return null;
    }

    private void setDocumentFields(DocumentAudit documentAudit, Document document) {
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
    }

    private void setObligationFields(DocumentAudit documentAudit, Obligation obligation) {
        documentAudit.setObligationDocumentNumber(obligation.getDocumentNumber());
//        documentAudit.setObligationDocumentType(obligation.getDocumentType());
        documentAudit.setObligationAmount(obligation.getAmount());
        documentAudit.setObligationDocumentDate(obligation.getDocumentDate());
        documentAudit.setObligationProcessDate(obligation.getProcessDate());
        documentAudit.setObligationVendor(obligation.getVendor());
        documentAudit.setObligationStatus(obligation.getStatus());
        documentAudit.setObligationEin(obligation.getEin());
        documentAudit.setObligationTravelStartDate(obligation.getTravelStartDate());
        documentAudit.setObligationTravelEndDate(obligation.getTravelEndDate());
//        documentAudit.setObligationModifiedDate(obligation.getLastModifiedDate());
        documentAudit.setObligationActivityNumber(obligation.getActivity().getActivityNumber());
        documentAudit.setObligationBudgetObjectClass(obligation.getObjectClass().getBudgetObjectClass());
        documentAudit.setObligationDivisionCode(obligation.getActivity().getDivision().getDivisionCode());
        documentAudit.setObligationMasterObjectClass(obligation.getObjectClass().getCategory().getMasterObjectClass());
//       documentAudit.setObligationAddressCode(obligation.getAddressCode());
        documentAudit.setObligationVendorCode(obligation.getVendorCode());

    }

    private void setProjectionFields(DocumentAudit documentAudit, ActivityProjection projection) {
//        documentAudit.setCurrentActivityNumber(projection.getActivity().getActivityNumber());
//        documentAudit.setCurrentProjectionBoc(projection.getObjectClass().getBudgetObjectClass());
//        documentAudit.setCurrentProjectionAmountBefore();
//        documentAudit.setCurrentProjectionAmountAfter();
//        documentAudit.setPreviousActivityNumber(projection.getActivity().getActivityNumber());
//        documentAudit.setPreviousProjectionBoc(projection.getObjectClass().getCategory().getMasterObjectClass());
//        documentAudit.setPreviousProjectionBoc(projection);
//        documentAudit.setPreviousProjectionAmountBefore();
//        documentAudit.setPreviousProjectionAmountAfter();
    }

    /**
     * logic agreed upon by OFM in 2019. Document numbers to be in form FJC20-81000.
     *
     * @param division
     * @param document
     * @return true if valid document number
     */
    private boolean validDocumentNumber(Division division, Document document) {
        var documentNumber = document.getDocumentNumber();
        return documentNumber != null && documentNumber.startsWith("FJC")
                && documentNumber.substring(3, 4).equals(document.getBbfy().substring(2, 3))
                && documentNumber.substring(5, 6).equals("-")
                && (document.getBudgetOrg().equals(obbbaBudgetOrg)
                || documentNumber.substring(7, 8).equals(division.getDivisionCode()))
                && ((documentNumber.substring(6, 7).equals("7") && travelDocumentTypes.contains(document.getDocumentType()))
                || (documentNumber.substring(6, 7).equals("8") && purchaseDocumentTypes.contains(document.getDocumentType())));
    }
}