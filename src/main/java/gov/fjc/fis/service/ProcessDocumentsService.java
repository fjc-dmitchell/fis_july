package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import io.jmix.core.Metadata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private Metadata metadata;

    private final String educationDivisionCode = "2";
    private final String twoYearFundCode = "09280M";


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

    private List<Activity> getActivity(Division division, String activityNumber) {
        return entityManager.createQuery("SELECT a FROM fis_Activity a"
                        + " INNER JOIN fis_Division d ON d=a.division"
                        + " WHERE a.division = :division AND a.activityNumber = :activityNumber")
                .setParameter("division", division)
                .setParameter("activityNumber", activityNumber)
                .getResultList();
    }

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

    @Transactional
    public void processDocuments() {

//        Map<String, Fund> fundMap = fundService.getFundList().stream().collect(Collectors.toMap(Fund::getFundCode, item -> item));
        var fundMap = getFundMap();
        var twoYearFund = getTwoYearFund();

        var appropriations = getOpenAppropriations();

        Fund fund;
        Division division;
        Activity activity;
        ActivityProjection activityProjection;
        Category category;
        ObjectClass objectClass;
        Obligation obligation;

        for (var appropriation : appropriations) {
            var bbfy = appropriation.getBudgetFiscalYear();
            List<Division> divisionList = getAllDivisionsWithBudgetOrgs(appropriation);
            Division education = getEducationDivision(appropriation);
//            List<ObjectClass> objectClasses = objectClassService.getObjectClasses(appropriation, true);
//            Map<String, ObjectClass> bocMap = objectClassService.getObjectClasses(appropriation, true).stream().collect(Collectors.toMap(ObjectClass::getBudgetObjectClass, item -> item));
            Map<String, ObjectClass> bocMap = getObjectClassMap(appropriation, true);

            // debugging - show list of divisions
            for (var d : divisionList) {
                System.out.println(d.getAppropriation().getBudgetFiscalYear() + " " + d.getDivisionCode() + " " + d.getBudgetOrg() + " " + d.getFund().getFundCode());
            }

            int offset = 0;
            int max = 100; // process 100 documents per batch
            List<Document> documents;
            while ((documents = getDocuments(bbfy, offset, max)).size() > 0) {

                for (var document : documents) {

                    DocumentAudit documentAudit = metadata.create(DocumentAudit.class);

                    setDocumentFields(documentAudit, document);
                    documentAudit.setProcessDate(new Date());

                    // validate Fund
                    fund = fundMap.get(document.getFundCode());
                    if (fund == null) {
                        documentAudit.setProcessStatus("R");
                        documentAudit.setLoggedChanges("REJECTED: invalid fund '" + document.getFundCode() + "'");
                        System.out.println(documentAudit.getLoggedChanges());
                        entityManager.persist(documentAudit);
                        continue;
                    }

                    // validate Division
                    List<Division> foundDivisions = divisionList.stream()
                            .filter(d -> d.getBudgetOrg().equals(document.getBudgetOrg()) &&
                                    (d.getFund().getFundCode().equals(document.getFundCode()) ||
                                            (d.equals(education) &&
                                                    document.getFundCode().equals(twoYearFund.getFundCode())
                                            )
                                    )
                            ).toList();

                    if (foundDivisions.isEmpty()) {
                        documentAudit.setProcessStatus("R");
                        documentAudit.setLoggedChanges("REJECTED: no division found for '" + document.getBudgetOrg() + "'");
                        System.out.println(documentAudit.getLoggedChanges());
                        entityManager.persist(documentAudit);
                        continue;
                    }
                    if (foundDivisions.size() > 1) {
                        documentAudit.setLoggedChanges("Warning: more than one division found for '" + document.getBudgetOrg() + "'");
                        // add: using division code X, or reject
                        System.out.println(documentAudit.getLoggedChanges());
                    }
                    division = foundDivisions.get(0);

                    // validate ObjectClass
                    objectClass = bocMap.get(document.getBudgetObjectClass());
                    if (objectClass == null) {
                        documentAudit.setProcessStatus("R");
                        documentAudit.setLoggedChanges("REJECTED: invalid objectClass '" + document.getBudgetObjectClass() + "'");
                        entityManager.persist(documentAudit);
                        System.out.println(documentAudit.getLoggedChanges());
                        continue;
                    }

                    // validate activity
                    var activityList = getActivity(division, document.getProject());
                    if (activityList.isEmpty()) {
                        documentAudit.setProcessStatus("R");
                        documentAudit.setLoggedChanges("REJECTED: no activity found for '" + document.getProject() + "'");
                        System.out.println(documentAudit.getLoggedChanges());
                        entityManager.persist(documentAudit);
                        continue;
                    }
                    activity = activityList.get(0);
//                    System.out.println(activity.getActivityNumber());


                    // validate Allocation & insert if necessary


                    // validate Obligation & insert if necessary
                    var obligationList = getObligation(activity, objectClass, document.getDocumentNumber());
                    if (obligationList.isEmpty()) {
                        System.out.println("no document found: " + document.getDocumentNumber());
                        continue;
                    }

                    // create FCN if obligation amount updated
                    // validate projection & insert if necessary

                }
                offset += documents.size();
            }
        }
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
}