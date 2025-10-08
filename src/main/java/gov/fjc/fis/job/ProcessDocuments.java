package gov.fjc.fis.job;

import gov.fjc.fis.entity.*;
import io.jmix.core.UnconstrainedDataManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Job to process JIFMS Documents in FIS 2.1. This will be triggered by cron job.
 * Uses queries without security constraints for processing. As this job does not
 * currently rely on other services, certain codes (e.g., Education division,
 * Two year fund, OBBBA budget Org) are hardcoded.
 * <p>
 * The business rules were specified by Mary Greiner and Nanticha Sansung
 * when JIFMS feeds went into production in September 2019.
 * <p>
 * ToDo: revisit thread safety
 *
 * @author Doug Mitchell
 * @version 2.1
 * @since 2.1
 */
@Component("fis_ProcessDocuments")
public class ProcessDocuments implements Job {
    @Autowired
    private UnconstrainedDataManager unconstrainedDataManager;
    @Autowired
    private UnconstrainedQueries unconstrainedQueries;

    private static final Logger log = LoggerFactory.getLogger(ProcessDocuments.class);

    private final String twoYearFundCode = "09280M";
    private final String educationDivisionCode = "2";
    private final String obbbaBudgetOrg = "JXXMAPP";
    private final List<String> travelDocumentTypes = List.of("TA", "TAJ", "JTA");
    private final List<String> purchaseDocumentTypes = List.of("MO", "MOJ");
    private final ZoneId timeZoneId = ZoneId.of("America/New_York");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var fundMap = unconstrainedQueries.getFundMap();
        var twoYearFund = unconstrainedQueries.getTwoYearFund(twoYearFundCode);
        var appropriations = unconstrainedQueries.getOpenAppropriations();
        List<Document> documents;

        for (var appropriation : appropriations) {

            List<Division> divisionList = unconstrainedQueries.getAllDivisionsWithBudgetOrgs(appropriation);
            Division educationDivision = unconstrainedQueries.getEducationDivision(appropriation, educationDivisionCode);
            Map<String, ObjectClass> objectClassMap = unconstrainedQueries.getObjectClassMap(appropriation, true);
            var bbfy = appropriation.getBudgetFiscalYear();

            // fetch document entities in small batches to reduce memory overhead
            int offset = 0;
            int max = 100; // process documents in small batches
            while (!(documents = unconstrainedQueries.getDocuments(bbfy, offset, max)).isEmpty()) {
//                System.out.println("bbfy: " + bbfy + " offset: " + offset + " max: " + max + " size: " + documents.size());

                for (var document : documents) {

                    var validator = new DocumentValidator(educationDivision, twoYearFund, document);
                    validator.validateFund(fundMap);
                    validator.validateDivision(divisionList);
                    validator.validateObjectClass(objectClassMap);
                    validator.validateActivity();
//                    validator.validDocumentNumber();
                    validator.validateDocumentNumber();
                    validator.validateObligation();


                    if (validator.getAuditState().equals(DocumentAuditState.REJECT)) {
                        log.info(validator.getAuditMessage());
                    } else {
//                        log.info("Processing Document {}", document.getDocumentNumber());
                    }

                    // when inserting audit record, check if an identical row exists. Don't log again if amount is zero

                }
                offset += documents.size();
            }
        }
    }

    class DocumentValidator {

        DocumentAuditState auditState = DocumentAuditState.IGNORE;
        //        StringBuffer auditMessage = new StringBuffer();
        StringBuilder auditMessage = new StringBuilder();

        Fund twoYearFund;
        Division educationDivision;
        Document document;

        Fund fund = null;
        Division division = null;
        ObjectClass objectClass = null;
        Activity activity = null;
        Activity genericActivity = null;
        Obligation obligation = null;

        BigDecimal oldAmount = null;
        ObjectClass oldObjectClass = null;
        Activity oldActivity = null;

        DocumentValidator(Division educationDivision, Fund twoYearFund, Document document) {
            this.educationDivision = educationDivision;
            this.twoYearFund = twoYearFund;
            this.document = document;
        }

        String getAuditMessage() {
            return auditMessage.toString();
        }

        DocumentAuditState getAuditState() {
            return auditState;
        }

        void validateFund(Map<String, Fund> fundMap) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                fund = fundMap.get(document.getFundCode());
                if (fund == null) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid fund %s. ", document.getFundCode()));
                }
            }
        }

        void validateDivision(List<Division> divisionList) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                var budgetOrg = document.getBudgetOrg();
                List<Division> foundDivisions = divisionList.stream()
                        .filter(d -> d.getBudgetOrg().equals(budgetOrg)
                                && (d.getFund().getFundCode().equals(fund.getFundCode())
                                || (d.equals(educationDivision)
                                && fund.getFundCode().equals(twoYearFund.getFundCode()))))
                        .toList();
                if (foundDivisions.size() != 1) {
                    auditState = DocumentAuditState.REJECT;
                    if (foundDivisions.isEmpty()) {
                        auditMessage.append(String.format("Invalid budgetOrg: %s. ", budgetOrg));
                    } else {
                        auditMessage.append(String.format("Multiple divisions matching budgetOrg: %s. ", budgetOrg));
                    }
                } else {
                    division = foundDivisions.getFirst();
                }
            }
        }

        void validateObjectClass(Map<String, ObjectClass> objectClassMap) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                var budgetObjectClass = document.getBudgetObjectClass();
                objectClass = objectClassMap.get(budgetObjectClass);
                if (objectClass == null) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid objectClass: %s.", budgetObjectClass));
                }
            }
        }

        void validateActivity() {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                var activityNumber = document.getProject();
                activity = unconstrainedQueries.getActivity(division, activityNumber);
                if (activity == null) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid activity: %s.", activityNumber));
                } else if (!activity.getFund().equals(fund)) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid activity fund: %s.", activity.getFund().getFundCode()));
                } else {
                    genericActivity = unconstrainedQueries.getGenericActivity(activity);
                }
            }
        }

        // replace with more explicit message
        void validDocumentNumber() {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                var documentNumber = document.getDocumentNumber();
                if (!(documentNumber != null && documentNumber.length() == 11
                        && documentNumber.toUpperCase().startsWith("FJC")
                        && documentNumber.substring(3, 4).equals(document.getBbfy().substring(2, 3))
                        && documentNumber.charAt(5) == '-'
                        && (document.getBudgetOrg().equals(obbbaBudgetOrg)
                        || documentNumber.substring(7, 8).equals(division.getDivisionCode()))
                        && ((documentNumber.charAt(6) == '7' && travelDocumentTypes.contains(document.getDocumentType()))
                        || (documentNumber.charAt(6) == '8' && purchaseDocumentTypes.contains(document.getDocumentType()))))) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid document number: %s. ", documentNumber));
                }
            }
        }

        void validateDocumentNumber() {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                var documentNumber = document.getDocumentNumber();
                StringBuilder message = new StringBuilder();
                if (documentNumber == null) {
                    message.append("-null");
                } else if (documentNumber.length() != 11) {
                    message.append("-length");
                } else if (!documentNumber.toUpperCase().startsWith("FJC")) {
                    message.append("-prefix");
                } else if (!documentNumber.substring(3, 4).equals(document.getBbfy().substring(2, 3))) {
                    message.append("-BFY");
                } else if (!(documentNumber.charAt(5) == '-')) {
                    message.append("-hyphen");
                } else if (!document.getBudgetOrg().equals(obbbaBudgetOrg)
                        && !documentNumber.substring(7, 8).equals(division.getDivisionCode())) {
                    message.append("-division");
                } else if (!(documentNumber.charAt(6) == '7' && travelDocumentTypes.contains(document.getDocumentType()))
                        && !(documentNumber.charAt(6) == '8' && purchaseDocumentTypes.contains(document.getDocumentType()))) {
                    message.append("-docType");
                }
                if (!message.isEmpty()) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid document number: %s. %s", documentNumber, message));
                }
            }
        }

        void validateObligation() {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                var documentNumber = document.getDocumentNumber();
                var budgetOrg = document.getBudgetOrg();
                var lineNumber = document.getLineNumber();
                obligation = unconstrainedQueries.getObligation(objectClass, documentNumber, null, null);
                if (obligation != null) {
                    if (Objects.equals(obligation.getLineNumber(), lineNumber)) {
                        hasObligationChanged(document, obligation);
//                        if (obligation.getActivity() == activity) {
//                            System.out.println("if projection exists, update and log changes");
//                            System.out.println("if projection does not exist, create and log");
//                        } else {
//                            System.out.println("if projection exists for old activity, revert and log");
//                            System.out.println("change activity and log");
//                            System.out.println("if projection does not exist, create and log");
//                        }
                    }
                    if (auditState.equals(DocumentAuditState.UPDATE)) {
                        log.info(auditMessage.toString());
                    }

                }
//                if (obligation == null) {
//                    // create obligation
//                    // update projection (if found)
//                    // ensure allocation record exists
//                } else {
//                    if (obligation.getLineNumber() == lineNumber) {
//                    }
//                }
            }
        }

        boolean hasObligationChanged(Document document, Obligation obligation) {
            StringBuilder changes = new StringBuilder();
            if ((document.getClosedDate() == null) ^ obligation.getStatus()) {
                // business rule per 2019 meeting, only change if FIS is open
                if (obligation.getStatus()) {
                    obligation.setStatus(document.getClosedDate() == null);
                    changes.append(" -status");
                }
            }

            // if Court-Activity is ever used for other values, it will be necessary to parse for BPO
            if (document.getFjc() == null) {
                if (obligation.getBlanketPurchaseOrder() && document.getBbfy().compareTo("2026") >= 0) {
                    obligation.setBlanketPurchaseOrder(false); // keep in sync starting in 2026
                }
            } else {
                if (obligation.getBlanketPurchaseOrder() ^ document.getFjc().equalsIgnoreCase("bpo")) {
                    obligation.setBlanketPurchaseOrder(document.getFjc().equalsIgnoreCase("bpo"));
                    changes.append(" -BPO");
                }
            }

            if (!Objects.equals(document.getAmount(), obligation.getAmount())) {
                oldAmount = obligation.getAmount();
                obligation.setAmount(document.getAmount());
                changes.append(" -amount");
            }
            if (!Objects.equals(document.getTitle(), obligation.getVendor())) {
                obligation.setVendor(document.getTitle());
                changes.append(" -vendor");
            }
            if (!activity.equals(obligation.getActivity())) {
                oldActivity = obligation.getActivity();
                obligation.setActivity(activity);
                changes.append(" -activity");
            }

            if (!Objects.equals(document.getTravelStartDate(), obligation.getTravelStartDate())) {
                obligation.setTravelStartDate(document.getTravelStartDate());
                changes.append(" -travelStartDate");
            }
            if (!Objects.equals(document.getTravelEndDate(), obligation.getTravelEndDate())) {
                obligation.setTravelEndDate(document.getTravelEndDate());
                changes.append(" -travelEndDate");
            }
            if (!Objects.equals(objectClass, obligation.getObjectClass())) {
                oldObjectClass = obligation.getObjectClass();
                obligation.setObjectClass(objectClass);
                changes.append(" -objectClass");
            }
            if (!Objects.equals(document.getVendorCode(), obligation.getVendorCode())) {
                obligation.setVendorCode(document.getVendorCode());
                changes.append(" -vendorCode");
            }
            if (!Objects.equals(document.getTaxId(), obligation.getEin())) {
                obligation.setEin(document.getTaxId());
                changes.append(" -taxId");
            }
            if (!changes.isEmpty()) {
                auditState = DocumentAuditState.UPDATE;
                auditMessage.append(String.format("%s UPDATED: %s. ", document.getDocumentNumber(), changes));
                return true;
            } else {
                return false;
            }
        }
    }
}