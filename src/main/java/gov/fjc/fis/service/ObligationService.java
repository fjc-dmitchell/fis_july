package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.ActivityDto;
import gov.fjc.fis.entity.dto.DivisionDto;
import gov.fjc.fis.entity.dto.ObligationDto;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static gov.fjc.fis.FisUtilities.getCurrentDateMinusDays;

@Component("fis_ObligationService")
public class ObligationService {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private FundService fundService;
    @Autowired
    private AppropriationService appropriationService;

    // Todo: refactor as getObligationsByEntity(Entity entity)

    public List<Obligation> getObligations(ObjectClass objectClass) {
        return dataManager.load(Obligation.class)
                .query("select o from fis_Obligation o "
                        + "inner join o.activity a "
                        + "where o.objectClass = :objectClassId "
                        + "order by o.documentNumber")
                .parameter("objectClassId", objectClass)
                .list();
    }

    public List<Obligation> getObligations(Activity activity) {
        return dataManager.load(Obligation.class)
                .query("select o from fis_Obligation o "
                        + "where o.activity = :activityId "
                        + "order by o.documentNumber")
                .parameter("activityId", activity)
                .list();
    }

    public List<Obligation> getObligations(Division division) {
        return dataManager.load(Obligation.class)
                .query("select o from fis_Obligation o "
                        + "inner join o.activity a "
                        + "where a.division = :divisionId "
                        + "order by o.documentNumber")
                .parameter("divisionId", division)
                .list();
    }

    public List<Obligation> getObligations(Branch branch) {
        return dataManager.load(Obligation.class)
                .query("select o from fis_Obligation o "
                        + "inner join o.activity a "
                        + "where a.branch = :branchId order by o.documentNumber")
                .parameter("branchId", branch)
                .list();
    }

    public List<Obligation> getObligations(Group group) {
        return dataManager.load(Obligation.class)
                .query("select o from fis_Obligation o "
                        + "inner join o.activity a "
                        + "where a.group = :groupId order by o.documentNumber")
                .parameter("groupId", group)
                .list();
    }

    public List<Obligation> getObligationSuggestion(
            String budgetFiscalYear, String docidSearchString, boolean foundation) {
        return dataManager.load(Obligation.class)
                .query("select o from fis_Obligation o" +
                        " where o.activity.division.appropriation.budgetFiscalYear = :bfy" +
                        " and o.documentNumber like :docid" +
                        " and ((:foundation = true and o.activity.fund.fundCode = '812300') " +
                        " or (:foundation = false and o.activity.fund.fundCode <> '812300'))" +
                        " order by o.documentNumber")
                .parameter("bfy", budgetFiscalYear)
                .parameter("docid", "(?i)%" + docidSearchString + "%")
                .parameter("foundation", foundation)
                .list();
    }

//    public List<Obligation> getObligations(Appropriation currentBfy, List<Division> divisions, ActivityFundingType activityFundingType) {
//        if (!(activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND)
//                || activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY)
//                || activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))) {
//            throw new RuntimeException("FIS coding error: ActivityFundingType not allowed");
//        }
//        Fund oneYearFund = fundService.getAppropriationOneYearFund();
//        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
//        Appropriation priorBfy = appropriationService.getPreviousFiscalYear(currentBfy);
//        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentBfy);
//        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentBfy);
//        String divisionQuery;
//        if (divisions == null) {
//            divisionQuery = "(true=true)";
//        } else {
//            divisionQuery = "(dv in :divisions)";
//        }
//        return dataManager.load(Obligation.class)
//                .query("SELECT o FROM fis_Obligation o" +
//                        " INNER JOIN fis_Activity act ON act = o.activity" +
//                        " INNER JOIN fis_Fund fund ON fund = act.fund" +
//                        " INNER JOIN fis_Division dv ON dv = act.division" +
//                        " INNER JOIN fis_Appropriation app ON app = dv.appropriation" +
//                        " WHERE " + divisionQuery +
//                        " AND ((:currentOneYear = true AND app = :currentBfy AND fund = :oneYearFund)" +
//                        " OR (:priorTwoYear = true AND app = :priorBfy AND fund = :twoYearFund AND act.endDate >= :bfyStartDate)" +
//                        " OR (:currentTwoYear = true AND app = :currentBfy AND fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate)))")
//                .parameter("currentBfy", currentBfy)
//                .parameter("priorBfy", priorBfy)
//                .parameter("oneYearFund", oneYearFund)
//                .parameter("twoYearFund", twoYearFund)
//                .parameter("divisions", divisions)
//                .parameter("currentOneYear", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND))
//                .parameter("priorTwoYear", activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("currentTwoYear", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("bfyStartDate", bfyStartDate)
//                .parameter("bfyEndDate", bfyEndDate)
//                .list();
//    }

//    public List<KeyValueEntity> aggregateObligationsByDivisionCode(Appropriation currentBfy,
//                                                                   ActivityFundingType activityFundingType,
//                                                                   ObligationStatus status) {
//        if (!(activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND)
//                || activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY)
//                || activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))) {
//            throw new RuntimeException("FIS coding error: ActivityFundingType not allowed");
//        }
//        Fund oneYearFund = fundService.getAppropriationOneYearFund();
//        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
//        Appropriation priorBfy = appropriationService.getPreviousFiscalYear(currentBfy);
//        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentBfy);
//        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentBfy);
//        return dataManager.loadValues(
//                        "SELECT dv.divisionCode, coalesce(sum(o.amount),0)" +
//                                " FROM fis_Obligation o" +
//                                " INNER JOIN fis_Activity act ON act = o.activity" +
//                                " INNER JOIN fis_Fund fund ON fund = act.fund" +
//                                " INNER JOIN fis_Division dv ON dv = act.division" +
//                                " INNER JOIN fis_Appropriation app ON app = dv.appropriation" +
//                                " WHERE (:statusNotSpecified = true" +
//                                " OR (:statusObligated = true AND o.status=true)" +
//                                " OR (:statusDisbursed = true AND o.status=false))" +
//                                " AND ((:currentOneYearFund = true AND app = :currentBfy AND fund = :oneYearFund)" +
//                                " OR (:priorTwoYearFund = true AND app = :priorBfy AND fund = :twoYearFund AND act.endDate >= :bfyStartDate)" +
//                                " OR (:currentTwoYearFund = true AND app = :currentBfy AND fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate)))" +
//                                " GROUP BY dv.divisionCode" +
//                                " ORDER BY dv.divisionCode")
//                .parameter("currentBfy", currentBfy)
//                .parameter("priorBfy", priorBfy)
//                .parameter("oneYearFund", oneYearFund)
//                .parameter("twoYearFund", twoYearFund)
//                .parameter("statusNotSpecified", status.equals(ObligationStatus.OBLIGATED_OR_DISBURSED))
//                .parameter("statusObligated", status.equals(ObligationStatus.OBLIGATED))
//                .parameter("statusDisbursed", status.equals(ObligationStatus.DISBURSED))
//                .parameter("currentOneYearFund", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND))
//                .parameter("priorTwoYearFund", activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("currentTwoYearFund", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("bfyStartDate", bfyStartDate)
//                .parameter("bfyEndDate", bfyEndDate)
//                .properties("divcode", "obligated")
//                .list();
//    }

    /**
     * getOpenObligations returns all open obligations for a given appropriation, division, branch
     * that are older than endDate. If endDate is null, show all open obligations
     *
     * @param appropriation
     * @param division
     * @param branch
     * @param numberOfDays
     * @return List of KeyValueEntity containing obligation properties
     */
    public List<KeyValueEntity> getOpenObligations(Appropriation appropriation, Division division, Branch branch, int numberOfDays) {
        Date endDate = getCurrentDateMinusDays(numberOfDays);
        List<KeyValueEntity> obligations = dataManager.loadValues(
                        "SELECT o.amount, o.documentNumber, o.vendor, a.activityNumber, a.title, a.city,"
                                + " a.state, CASE WHEN (o.documentType = 'TA') THEN o.travelEndDate ELSE a.endDate END,"
                                + " o.lineNumber"
                                + " FROM fis_Obligation o"
                                + " INNER JOIN fis_Activity a ON a=o.activity"
                                + " INNER JOIN fis_Division d ON d=a.division"
                                + " INNER JOIN fis_Appropriation app ON app=d.appropriation"
                                + " WHERE app = :appropriation"
                                + " AND o.status=TRUE"
                                + " AND d = :division"
                                + " AND ((o.documentType = 'TA' AND :numberOfDays<>0 AND o.travelEndDate <= :endDate)"
                                + " OR (o.documentType <> 'TA' AND :numberOfDays<>0 AND a.endDate <= :endDate)"
                                + " OR (:numberOfDays=0))"
                                + " AND (:branch IS NULL OR a.branch = :branch)")
                .parameter("appropriation", appropriation)
                .parameter("division", division)
                .parameter("numberOfDays", numberOfDays)
                .parameter("endDate", endDate)
                .parameter("branch", branch)
                .properties("amount", "docid", "vendor", "actnum", "title", "city", "state", "enddate", "lineno")
                .list();

        obligations.sort(Comparator.comparing((KeyValueEntity o) -> (Date) o.getValue("enddate"),
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(o -> o.getValue("actnum"))
                .thenComparing(o -> o.getValue("docid"))
                .thenComparingInt(o -> o.getValue("lineno")));

        return obligations;
    }

    public List<KeyValueEntity> aggregateObligationsByActivity(Division division, Branch branch, ObligationStatus status) {
        if (division == null && branch == null) {
            throw new RuntimeException("FIS coding error: division and branch cannot both be null");
        }
        return dataManager.loadValues(
                        "SELECT o.activity, COALESCE(SUM(o.amount),0) from fis_Obligation o"
                                + " INNER JOIN fis_Activity a ON a=o.activity"
                                + " WHERE (:anyDivision = true OR a.division = :division)"
                                + " AND (:anyBranch = true OR a.branch = :branch)"
                                + " AND (:statusNotSpecified = true"
                                + " OR (:statusObligated = true AND o.status=true)"
                                + " OR (:statusDisbursed = true AND o.status=false))"
                                + " GROUP BY o.activity")
                .parameter("anyDivision", division == null)
                .parameter("division", division)
                .parameter("anyBranch", branch == null)
                .parameter("branch", branch)
                .parameter("statusNotSpecified", status.equals(ObligationStatus.OBLIGATED_OR_DISBURSED))
                .parameter("statusObligated", status.equals(ObligationStatus.OBLIGATED))
                .parameter("statusDisbursed", status.equals(ObligationStatus.DISBURSED))
                .properties("activity", "amount")
                .list();
    }

    public List<KeyValueEntity> aggregateObligationsByActivity(List<Activity> activities, ObligationStatus status) {
        return dataManager.loadValues(
                        "SELECT o.activity, COALESCE(SUM(o.amount),0)"
                                + " FROM fis_Obligation o"
                                + " WHERE o.activity IN :activities"
                                + " AND (:statusNotSpecified = true"
                                + " OR (:statusObligated=true AND o.status=true)"
                                + " OR (:statusDisbursed=true AND o.status=false))"
                                + " GROUP BY o.activity")
                .parameter("activities", activities)
                .parameter("statusNotSpecified", status.equals(ObligationStatus.OBLIGATED_OR_DISBURSED))
                .parameter("statusObligated", status.equals(ObligationStatus.OBLIGATED))
                .parameter("statusDisbursed", status.equals(ObligationStatus.DISBURSED))
                .properties("activity", "amount")
                .list();
    }

    /**
     * get obligations
     *
     * @param appropriation Appropriation, used only to place amount in proper property
     * @param activities    List of Activity entities
     * @return List of KeyValueEntity
     */
    public List<KeyValueEntity> getObligations(Appropriation appropriation, List<Activity> activities) {
        Appropriation priorYear = appropriationService.getPreviousFiscalYear(appropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        return dataManager.loadValues(
                        "SELECT fund.fundCode, app.budgetFiscalYear, dv.divisionCode, act.activityNumber,"
                                + " act.title, bch.branchCode, grp.groupCode, cat.masterObjectClass, obj.budgetObjectClass,"
                                + " o.documentNumber, o.documentDate, o.status, o.vendor, o.amount,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :oneYearFund THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :twoYearFund THEN o.amount ELSE 0 END"
                                + " FROM fis_Activity act"
                                + " INNER JOIN fis_Obligation o ON o.activity=act"
                                + " LEFT JOIN fis_Branch bch ON bch=act.branch"
                                + " LEFT JOIN fis_Group grp ON grp=act.group"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " INNER JOIN fis_ObjectClass obj ON obj=o.objectClass"
                                + " INNER JOIN fis_Category cat ON cat=obj.category"
                                + " WHERE act IN :activities"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, act.activityNumber, o.documentNumber, obj.budgetObjectClass")
                .parameter("priorYear", priorYear)
                .parameter("appropriation", appropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("activities", activities)
                .properties("fund", "bfy", "division", "actnum", "title", "branch", "group", "moc", "boc", "docnum", "date", "status", "vendor", "amount", "prioryear", "oneyear", "twoyear")
                .list();
    }

    /**
     * get obligations from list of obligationDtos that match activity id
     * @param obligationDtos list of obligationDtos that may contain activity id
     * @param activityId id of activity entity
     * @return list of matching obligationDtos
     */
    public List<ObligationDto> getObligationDtosForActivity(List<ObligationDto> obligationDtos, Integer activityId) {
        return obligationDtos.stream().filter(obligationDto -> obligationDto.getActivityId().equals(activityId)).toList();
    }

    // same as above but for categories, probably not needed. See method below
    public List<ObligationDto> getObligationDtosForCategory(List<ObligationDto> obligationDtos, Integer categoryId) {
        return obligationDtos.stream().filter(obligationDto -> obligationDto.getCategoryId().equals(categoryId)).toList();
    }
    public List<ObligationDto> getObligationDtosForMasterObjectClass(List<ObligationDto> obligationDtos, String masterObjectClass) {
        return obligationDtos.stream().filter(obligationDto -> obligationDto.getMasterObjectClass().equals(masterObjectClass)).toList();
    }

    public List<ObligationDto> getObligationDtosForBudgetObjectClass(List<ObligationDto> obligationDtos, String budgetObjectClass) {
        return obligationDtos.stream().filter(obligationDto -> obligationDto.getBudgetObjectClass().equals(budgetObjectClass)).toList();
    }

    public List<ObligationDto> getObligationDtosForDivisionCode(List<ObligationDto> obligationDtos, String divisionCode) {
        return obligationDtos.stream().filter(obligationDto -> obligationDto.getDivisionCode().equals(divisionCode)).toList();
    }

    /**
     * get obligations - used by BudgetRequest
     *
     * @param appropriation Appropriation, used only to place amount in proper property
     * @param activities    List of ActivityDto entities
     * @return List of KeyValueEntity
     */
    public List<KeyValueEntity> getObligationsNew(Appropriation appropriation, List<ActivityDto> activities) {
        var priorYear = appropriationService.getPreviousFiscalYear(appropriation);
        var oneYearFund = fundService.getAppropriationOneYearFund();
        var twoYearFund = fundService.getAppropriationTwoYearFund();
        var activityIds = activities.stream().map(ActivityDto::getId).toList();
        return dataManager.loadValues(
                        "SELECT o.id, fund.id, fund.fundCode, app.id, app.budgetFiscalYear, dv.id, dv.divisionCode, act.id, act.activityNumber,"
                                + " act.title, bch.id, bch.branchCode, grp.id, grp.groupCode, cat.id, cat.masterObjectClass, obj.id, obj.budgetObjectClass,"
                                + " o.documentNumber, o.documentDate, o.status, o.vendor, o.amount,"
                                + " CASE WHEN o.status=1 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN o.status=0 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund AND o.status=1 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund AND o.status=0 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :oneYearFund THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :oneYearFund AND o.status=1 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :oneYearFund AND o.status=0 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :twoYearFund THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :twoYearFund AND o.status=1 THEN o.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :twoYearFund AND o.status=0 THEN o.amount ELSE 0 END"
                                + " FROM fis_Activity act"
                                + " INNER JOIN fis_Obligation o ON o.activity=act"
                                + " LEFT JOIN fis_Branch bch ON bch=act.branch"
                                + " LEFT JOIN fis_Group grp ON grp=act.group"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " INNER JOIN fis_ObjectClass obj ON obj=o.objectClass"
                                + " INNER JOIN fis_Category cat ON cat=obj.category"
                                + " WHERE act.id IN :activities"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, act.activityNumber, o.documentNumber, obj.budgetObjectClass")
                .parameter("priorYear", priorYear)
                .parameter("appropriation", appropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("activities", activityIds)
                .properties("id", "fundId", "fundCode", "appropriationId", "budgetFiscalYear", "divisionId", "divisionCode", "activityId", "activityNumber",
                        "activityTitle", "branchId", "branchCode", "groupId", "groupCode", "categoryId", "masterObjectClass", "objectClassId",
                        "budgetObjectClass", "documentNumber", "documentDate", "status", "vendor", "amount", "obligated", "disbursed", "priorTwoYearAmount",
                        "priorTwoYearObligated", "priorTwoYearDisbursed", "currentOneYearAmount", "currentOneYearObligated", "currentOneYearDisbursed",
                        "currentTwoYearAmount", "currentTwoYearObligated", "currentTwoYearDisbursed")
                .list();
    }

    /**
     * get next line number for an obligation. Expects that docid will be unique within Appropriation year. Doug hates this.
     *
     * @param obligation
     * @return Integer
     */
    public Integer getNextObligationLineNumber(Obligation obligation) {
        if (obligation == null || obligation.getActivity() == null) {
            return 0;
        }
        Appropriation appropriation = obligation.getActivity().getDivision().getAppropriation();

        return dataManager.loadValue("SELECT COALESCE(MAX(o.lineNumber),0)+1"
                        + " FROM fis_Obligation o "
                        + " INNER JOIN fis_Activity act ON act=o.activity"
                        + " INNER JOIN fis_Division dv ON dv=act.division"
                        + " WHERE dv.appropriation = :appropriation"
                        + " AND o.documentNumber = :docid", Integer.class)
                .parameter("appropriation", appropriation)
                .parameter("docid", obligation.getDocumentNumber())
                .one();
    }

    /**
     * Determine whether obligation is unique by Budget Fiscal Year, Document Number and BOC
     *
     * @param obligation
     * @return Boolean
     */
    public boolean uniqueObligation(Obligation obligation) {
        if (obligation.getActivity() == null || obligation.getObjectClass() == null) {
            return false;
        }
        Integer count = dataManager.loadValue("SELECT count(o)"
                        + " FROM fis_Obligation o "
                        + " INNER JOIN fis_Activity act ON act=o.activity"
                        + " INNER JOIN fis_Division dv ON dv=act.division"
                        + " INNER JOIN fis_ObjectClass obj ON obj=o.objectClass"
                        + " WHERE dv.appropriation = :appropriation"
                        + " AND o.objectClass = :objectClass"
                        + " AND o.documentNumber = :docid", Integer.class)
                .parameter("appropriation", obligation.getActivity().getDivision().getAppropriation())
                .parameter("objectClass", obligation.getObjectClass())
                .parameter("docid", obligation.getDocumentNumber())
                .one();
        return (count == 0);
    }

    public List<KeyValueEntity> sumObligations(Appropriation appropriation, List<Fund> funds) {
        return dataManager.loadValues(
                        "SELECT obj.category.masterObjectClass, div.divisionCode, act.fund, sum(obl.amount)"
                                + " FROM fis_Obligation obl"
                                + " INNER JOIN fis_ObjectClass obj ON obj = obl.objectClass"
                                + " INNER JOIN fis_Activity act ON act = obl.activity"
                                + " INNER JOIN fis_Division div ON div = act.division"
                                + " WHERE div.appropriation = :appropriation"
                                + " AND act.fund in :funds"
                                + " GROUP BY obj.category.masterObjectClass, div.divisionCode, act.fund"
                                + " ORDER BY obj.category.masterObjectClass, div.divisionCode, act.fund.fundCode")
                .properties("moc", "divcode", "fund", "amount")
                .parameter("appropriation", appropriation)
                .parameter("funds", funds)
                .list();
    }

    public List<Obligation> fetchBiFiscalActivityObligations(Appropriation currentYearAppropriation) {
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.load(Obligation.class)
                .query("SELECT o FROM fis_Obligation o"
                        + " WHERE o.activity IN "
                        + " (SELECT a FROM fis_Activity a"
                        + " INNER JOIN fis_Division dv ON dv = a.division"
                        + " INNER JOIN fis_Fund fund ON fund = a.fund"
                        + " WHERE (dv.appropriation = :currentYear AND a.fund = :oneYearFund)"
                        + " OR (dv.appropriation = :priorYear AND a.fund = :twoYearFund AND a.endDate >= :bfyStartDate)"
                        + " OR (dv.appropriation = :currentYear AND a.fund = :twoYearFund AND (a.endDate IS NULL OR a.endDate <= :bfyEndDate)))")
                .parameter("currentYear", currentYearAppropriation)
                .parameter("priorYear", priorYearAppropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("bfyStartDate", bfyStartDate)
                .parameter("bfyEndDate", bfyEndDate)
//                .fetchPlan("extended")
                .list();
    }

    // probably need activity id, division id, etc. for aggregations
    // we probably don't need this - let's try activities first
//    public List<KeyValueEntity> getObligations(Appropriation currentYearAppropriation) {
//        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
//        Fund oneYearFund = fundService.getAppropriationOneYearFund();
//        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
//        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
//        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);
//
//        return dataManager.loadValues(
//                        "SELECT fund.fundCode, app.budgetFiscalYear, dv.divisionCode, act.activityNumber,"
//                                + " act.title, bch.branchCode, grp.groupCode, cat.masterObjectClass, obj.budgetObjectClass,"
//                                + " o.documentNumber, o.documentDate, o.status, o.vendor, o.amount,"
//                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN o.amount ELSE 0 END,"
//                                + " CASE WHEN dv.appropriation = :currentYear AND act.fund = :oneYearFund THEN o.amount ELSE 0 END,"
//                                + " CASE WHEN dv.appropriation = :currentYear AND act.fund = :twoYearFund THEN o.amount ELSE 0 END"
//                                + " FROM fis_Activity act"
//                                + " INNER JOIN fis_Obligation o ON o.activity=act"
//                                + " LEFT JOIN fis_Branch bch ON bch=act.branch"
//                                + " LEFT JOIN fis_Group grp ON grp=act.group"
//                                + " INNER JOIN fis_Division dv ON dv=act.division"
//                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
//                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
//                                + " INNER JOIN fis_ObjectClass obj ON obj=o.objectClass"
//                                + " INNER JOIN fis_Category cat ON cat=obj.category"
//                                + " WHERE (dv.appropriation = :currentYear AND act.fund = :oneYearFund)"
//                                + " OR (dv.appropriation = :priorYear AND act.fund = :twoYearFund AND act.endDate >= :bfyStartDate)"
//                                + " OR (dv.appropriation = :currentYear AND act.fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate))"
//                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, act.activityNumber, o.documentNumber, obj.budgetObjectClass")
//                .parameter("priorYear", priorYearAppropriation)
//                .parameter("currentYear", currentYearAppropriation)
//                .parameter("oneYearFund", oneYearFund)
//                .parameter("twoYearFund", twoYearFund)
//                .parameter("bfyStartDate", bfyStartDate)
//                .parameter("bfyEndDate", bfyEndDate)
//                .properties("fund", "bfy", "division", "actnum", "title", "branch", "group", "moc", "boc", "docnum", "date", "status", "vendor", "amount", "prioryear", "oneyear", "twoyear")
//                .list();
//    }

    public List<ObligationDto> getObligationDtos(Appropriation appropriation, List<ActivityDto> activityDtos) {
        var obligations = getObligationsNew(appropriation, activityDtos);
        List<ObligationDto> obligationDtos = new ArrayList<>();
        ObligationDto dto;

        for (var kvEntity : obligations) {
            dto = dataManager.create(ObligationDto.class);
            dto.setId(kvEntity.getValue("id"));
            dto.setActivityId(kvEntity.getValue("activityId"));
            dto.setFundId(kvEntity.getValue("fundId"));
            dto.setFundCode(kvEntity.getValue("fundCode"));
            dto.setAppropriationId(kvEntity.getValue("appropriationId"));
            dto.setBudgetFiscalYear(kvEntity.getValue("budgetFiscalYear"));
            dto.setDivisionId(kvEntity.getValue("divisionId"));
            dto.setDivisionCode(kvEntity.getValue("divisionCode"));
            dto.setActivityId(kvEntity.getValue("activityId"));
            dto.setActivityNumber(kvEntity.getValue("activityNumber"));
            dto.setActivityTitle(kvEntity.getValue("activityTitle"));
            dto.setBranchId(kvEntity.getValue("branchId"));
            dto.setBranchCode(kvEntity.getValue("branchCode"));
            dto.setGroupId(kvEntity.getValue("groupId"));
            dto.setGroupCode(kvEntity.getValue("groupCode"));
            dto.setCategoryId(kvEntity.getValue("categoryId"));
            dto.setMasterObjectClass(kvEntity.getValue("masterObjectClass"));
            dto.setObjectClassId(kvEntity.getValue("objectClassId"));
            dto.setBudgetObjectClass(kvEntity.getValue("budgetObjectClass"));
            dto.setDocumentNumber(kvEntity.getValue("documentNumber"));
            dto.setDocumentDate(kvEntity.getValue("documentDate"));
            dto.setStatus(kvEntity.getValue("status"));
            dto.setVendor(kvEntity.getValue("vendor"));
            dto.setAmount(kvEntity.getValue("amount"));
            dto.setObligated(kvEntity.getValue("obligated"));
            dto.setDisbursed(kvEntity.getValue("disbursed"));
            dto.setPriorTwoYearAmount(kvEntity.getValue("priorTwoYearAmount"));
            dto.setPriorTwoYearObligated(kvEntity.getValue("priorTwoYearObligated"));
            dto.setPriorTwoYearDisbursed(kvEntity.getValue("priorTwoYearDisbursed"));
            dto.setCurrentOneYearAmount(kvEntity.getValue("currentOneYearAmount"));
            dto.setCurrentOneYearObligated(kvEntity.getValue("currentOneYearObligated"));
            dto.setCurrentOneYearDisbursed(kvEntity.getValue("currentOneYearDisbursed"));
            dto.setCurrentTwoYearAmount(kvEntity.getValue("currentTwoYearAmount"));
            dto.setCurrentTwoYearObligated(kvEntity.getValue("currentTwoYearObligated"));
            dto.setCurrentTwoYearDisbursed(kvEntity.getValue("currentTwoYearDisbursed"));
            obligationDtos.add(dto);
        }
        return obligationDtos;
    }
}