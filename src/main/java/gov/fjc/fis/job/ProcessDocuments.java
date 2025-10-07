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

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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

                    var validator = new DocumentValidator(educationDivision, twoYearFund);
                    validator.validateFund(fundMap, document.getFundCode());
                    validator.validateDivision(divisionList, document.getBudgetOrg());
                    validator.validateObjectClass(objectClassMap, document.getBudgetObjectClass());
                    validator.validateObjectClass(objectClassMap, document.getBudgetObjectClass());
                    validator.validateActivity(document.getProject());
                    validator.validateObligation(document, document.getDocumentNumber(), document.getBudgetOrg(), document.getLineNumber());


                    if (validator.getAuditState().equals(DocumentAuditState.REJECT)) {
                        log.info(validator.getAuditMessage());
                    } else {
//                        log.info("Processing Document {}", document.getDocumentNumber());
                    }

                }
                offset += documents.size();
            }
        }
    }

    class DocumentValidator {

        DocumentAuditState auditState = DocumentAuditState.IGNORE;
        StringBuffer auditMessage = new StringBuffer();

        Fund twoYearFund;
        Division educationDivision;

        Fund fund = null;
        Division division = null;
        ObjectClass objectClass = null;
        Activity activity = null;
        Activity genericActivity = null;
        Obligation obligation = null;

        DocumentValidator(Division educationDivision, Fund twoYearFund) {
            this.educationDivision = educationDivision;
            this.twoYearFund = twoYearFund;
        }

        String getAuditMessage() {
            return auditMessage.toString();
        }

        DocumentAuditState getAuditState() {
            return auditState;
        }

        Fund validateFund(Map<String, Fund> fundMap, String fundCode) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                fund = fundMap.get(fundCode);
                if (fund == null) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid fund %s. ", fundCode));
                }
            }
            return fund;
        }

        Division validateDivision(List<Division> divisionList, String budgetOrg) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
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
            return division;
        }

        ObjectClass validateObjectClass(Map<String, ObjectClass> objectClassMap, String budgetObjectClass) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                objectClass = objectClassMap.get(budgetObjectClass);
                if (objectClass == null) {
                    auditState = DocumentAuditState.REJECT;
                    auditMessage.append(String.format("Invalid objectClass: %s.", budgetObjectClass));
                }
            }
            return objectClass;
        }

        Activity validateActivity(String activityNumber) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
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
            return activity;
        }

        Obligation validateObligation(Document document, String documentNumber, String budgetOrg, int lineNumber) {
            if (!auditState.equals(DocumentAuditState.REJECT)) {
                obligation = unconstrainedQueries.getObligation(objectClass, documentNumber, null, null);
                if (obligation != null) {
                    if (obligation.getLineNumber() == lineNumber) {
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
            return obligation;
        }

        boolean hasObligationChanged(Document document, Obligation obligation) {
            StringBuffer changes = new StringBuffer();
            if ((document.getClosedDate() == null) ^ obligation.getStatus()) {
                // business rule per 2019 meeting, only change if FIS is open
                if (obligation.getStatus()) {
                    obligation.setStatus(document.getClosedDate() == null);
                    changes.append(" -status");
                }
            }

            if (document.getFjc() == null) {
                // should we unset FIS bpo starting in 2026?
//                if (obligation.getBlanketPurchaseOrder()) {
//                    obligation.setBlanketPurchaseOrder(false);
//                    changes.append(" -BPO");
//                }
            } else {
                if (obligation.getBlanketPurchaseOrder() ^ document.getFjc().equalsIgnoreCase("bpo")) {
                    obligation.setBlanketPurchaseOrder(document.getFjc().equalsIgnoreCase("bpo"));
                    changes.append(" -BPO");
                }
            }

            if (!Objects.equals(document.getAmount(), obligation.getAmount())) {
                obligation.setAmount(document.getAmount());
                changes.append(" -amount");
            }
            if (!Objects.equals(document.getTitle(), obligation.getVendor())) {
                obligation.setVendor(document.getTitle());
                changes.append(" -vendor");
            }
            if(!activity.equals(obligation.getActivity())) {
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