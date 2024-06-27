package gov.fjc.fis.service;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.ActivityReimbursement;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Fund;
import gov.fjc.fis.entity.dto.ActivityDto;
import gov.fjc.fis.entity.dto.ActivityReimbursementDto;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlanRepository;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("fis_ActivityReimbursementService")
public class ActivityReimbursementService {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private FundService fundService;

    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private FetchPlanRepository fetchPlanRepository;

    public BigDecimal sumReimbursements(Activity activity) {
        return dataManager.loadValue("SELECT coalesce(sum(r.amount),0)"
                        + " FROM fis_ActivityReimbursement r"
                        + " WHERE r.activity=:activity", BigDecimal.class)
                .parameter("activity", activity)
                .one();
    }

    public List<ActivityReimbursementDto> getReimbursementDtosForMasterObjectClass(List<ActivityReimbursementDto> reimbursementDtos, String masterObjectClass) {
        return reimbursementDtos.stream().filter(reimbursementDto -> reimbursementDto.getMasterObjectClass().equals(masterObjectClass)).toList();
    }

    public List<ActivityReimbursementDto> getReimbursementDtosForBudgetObjectClass(List<ActivityReimbursementDto> reimbursementDtos, String budgetObjectClass) {
        return reimbursementDtos.stream().filter(reimbursementDto -> reimbursementDto.getBudgetObjectClass().equals(budgetObjectClass)).toList();
    }

    public List<ActivityReimbursementDto> getReimbursementDtosForDivisionCode(List<ActivityReimbursementDto> reimbursementDtos, String divisionCode) {
        return reimbursementDtos.stream().filter(reimbursementDto -> reimbursementDto.getDivisionCode().equals(divisionCode)).toList();
    }

//    public List<ActivityReimbursement> getActivityReimbursements(Appropriation currentBfy, ActivityFundingType activityFundingType) {
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
//
//        FetchPlan fetchPlan = fetchPlanRepository.getFetchPlan(ActivityReimbursement.class, "full");
//
//        return dataManager.load(ActivityReimbursement.class)
//                .query("SELECT r FROM fis_ActivityReimbursement r" +
//                        " INNER JOIN fis_Activity act ON act = r.activity" +
//                        " INNER JOIN fis_Fund fund ON fund = act.fund" +
//                        " INNER JOIN fis_Division dv ON dv = act.division" +
//                        " INNER JOIN fis_Appropriation app ON app = dv.appropriation" +
//                        " WHERE (:currentOneYear = true AND app = :currentBfy AND fund = :oneYearFund)" +
//                        " OR (:priorTwoYear = true AND app = :priorBfy AND fund = :twoYearFund AND act.endDate >= :bfyStartDate)" +
//                        " OR (:currentTwoYear = true AND app = :currentBfy AND fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate))")
//                .parameter("currentBfy", currentBfy)
//                .parameter("priorBfy", priorBfy)
//                .parameter("oneYearFund", oneYearFund)
//                .parameter("twoYearFund", twoYearFund)
//                .parameter("currentOneYear", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND))
//                .parameter("priorTwoYear", activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("currentTwoYear", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("bfyStartDate", bfyStartDate)
//                .parameter("bfyEndDate", bfyEndDate)
//                .fetchPlan(fetchPlan)
//                .list();
//    }

//    public List<KeyValueEntity> aggregateReimbursementsByDivisionCode(Appropriation appropriation,
//                                                                      Fund fund,
//                                                                      ActivityFundingType activityFundingType) {
//        if (!(activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND)
//                || activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY)
//                || activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))) {
//            throw new RuntimeException("FIS coding error: ActivityFundingType not allowed");
//        }
//
//        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(appropriation);
//        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(appropriation);
//
//        return dataManager.loadValues(
//                        "SELECT dv.divisionCode, sum(r.amount)" +
//                                " FROM fis_ActivityReimbursement r" +
//                                " INNER JOIN fis_Activity act ON act = r.activity" +
//                                " INNER JOIN fis_Division dv ON dv = act.division" +
//                                " WHERE dv.appropriation = :appropriation" +
//                                " AND act.fund = :fund" +
//                                " AND (:currentOneYearFund = true" +
//                                " OR (:priorTwoYearFund = true AND act.endDate >= :bfyStartDate)" +
//                                " OR (:currentTwoYearFund = true AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate)))" +
//                                " GROUP BY dv.divisionCode" +
//                                " ORDER BY dv.divisionCode")
//                .parameter("appropriation", appropriation)
//                .parameter("fund", fund)
//                .parameter("currentOneYearFund", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND))
//                .parameter("priorTwoYearFund", activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("currentTwoYearFund", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("bfyStartDate", bfyStartDate)
//                .parameter("bfyEndDate", bfyEndDate)
//                .properties("divcode", "reimbursed")
//                .list();
//    }

//    public List<KeyValueEntity> aggregateReimbursementsByDivisionCode(Appropriation currentBfy,
//                                                                      ActivityFundingType activityFundingType) {
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
//
//        return dataManager.loadValues(
//                        "SELECT dv.divisionCode, coalesce(sum(r.amount),0)" +
//                                " FROM fis_ActivityReimbursement r" +
//                                " INNER JOIN fis_Activity act ON act = r.activity" +
//                                " INNER JOIN fis_Fund fund ON fund = act.fund" +
//                                " INNER JOIN fis_Division dv ON dv = act.division" +
//                                " INNER JOIN fis_Appropriation app ON app = dv.appropriation" +
//                                " WHERE ((:currentOneYearFund = true AND app = :currentBfy AND fund = :oneYearFund)" +
//                                " OR (:priorTwoYearFund = true AND app = :priorBfy AND fund = :twoYearFund AND act.endDate >= :bfyStartDate)" +
//                                " OR (:currentTwoYearFund = true AND app = :currentBfy AND fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate)))" +
//                                " GROUP BY dv.divisionCode" +
//                                " ORDER BY dv.divisionCode")
//                .parameter("currentBfy", currentBfy)
//                .parameter("priorBfy", priorBfy)
//                .parameter("oneYearFund", oneYearFund)
//                .parameter("twoYearFund", twoYearFund)
//                .parameter("currentOneYearFund", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_1YR_FUND))
//                .parameter("priorTwoYearFund", activityFundingType.equals(ActivityFundingType.PRIOR_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("currentTwoYearFund", activityFundingType.equals(ActivityFundingType.CURRENT_YEAR_2YR_FUND_ACTIVITIES_CURRENT_FY))
//                .parameter("bfyStartDate", bfyStartDate)
//                .parameter("bfyEndDate", bfyEndDate)
//                .properties("divcode", "reimbursed")
//                .list();
//    }

    public List<KeyValueEntity> aggregateReimbursementsByActivity(List<Activity> activities) {
        return dataManager.loadValues(
                        "SELECT r.activity, COALESCE(SUM(r.amount),0)"
                                + " FROM fis_ActivityReimbursement r"
                                + " WHERE r.activity IN :activities"
                                + " GROUP BY r.activity")
                .parameter("activities", activities)
                .properties("activity", "amount")
                .list();
    }

    /**
     * get reimbursements
     *
     * @param appropriation Appropriation, used only to place amount in proper property
     * @param activities    List of Activity entities
     * @return List of KeyValueEntity
     */
    public List<KeyValueEntity> getReimbursements(Appropriation appropriation, List<Activity> activities) {
        Appropriation priorYear = appropriationService.getPreviousFiscalYear(appropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        return dataManager.loadValues(
                        "SELECT fund.fundCode, app.budgetFiscalYear, dv.divisionCode, act.activityNumber,"
                                + " act.title, cat.masterObjectClass, obj.budgetObjectClass, r.documentNumber,"
                                + " r.source, r.amount,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN r.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :oneYearFund THEN r.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :twoYearFund THEN r.amount ELSE 0 END"
                                + " FROM fis_ActivityReimbursement r"
                                + " INNER JOIN fis_Activity act ON act=r.activity"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " INNER JOIN fis_ObjectClass obj ON obj=r.objectClass"
                                + " INNER JOIN fis_Category cat ON cat=obj.category"
                                + " WHERE r.activity IN :activities"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, act.activityNumber, obj.budgetObjectClass")
                .parameter("priorYear", priorYear)
                .parameter("appropriation", appropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("activities", activities)
                .properties("fund", "bfy", "division", "actnum", "title", "moc", "boc", "docnum", "source", "amount", "prioryear", "oneyear", "twoyear")
                .list();
    }

    /**
     * get reimbursements
     *
     * @param appropriation Appropriation, used only to place amount in proper property
     * @param activities    List of Activity ids
     * @return List of KeyValueEntity
     */
    public List<KeyValueEntity> getReimbursementsNew(Appropriation appropriation, List<ActivityDto> activities) {
        Appropriation priorYear = appropriationService.getPreviousFiscalYear(appropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        var activityIds = activities.stream().map(ActivityDto::getId).toList();
        return dataManager.loadValues(
                        "SELECT r.id, fund.id, fund.fundCode, app.id, app.budgetFiscalYear, dv.id, dv.divisionCode, act.id, act.activityNumber,"
                                + " act.title, cat.id, cat.masterObjectClass, obj.id, obj.budgetObjectClass, r.documentNumber,"
                                + " r.source, r.amount,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN r.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :oneYearFund THEN r.amount ELSE 0 END,"
                                + " CASE WHEN dv.appropriation = :appropriation AND act.fund = :twoYearFund THEN r.amount ELSE 0 END"
                                + " FROM fis_ActivityReimbursement r"
                                + " INNER JOIN fis_Activity act ON act=r.activity"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " INNER JOIN fis_ObjectClass obj ON obj=r.objectClass"
                                + " INNER JOIN fis_Category cat ON cat=obj.category"
                                + " WHERE r.activity.id IN :activities"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, act.activityNumber, obj.budgetObjectClass")
                .parameter("priorYear", priorYear)
                .parameter("appropriation", appropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("activities", activityIds)
                .properties("id", "fundId", "fundCode", "appropriationId", "budgetFiscalYear", "divisionId", "divisionCode", "activityId", "activityNumber", "activityTitle", "categoryId", "masterObjectClass", "objectClassId", "budgetObjectClass", "documentNumber", "source", "amount", "prioryear", "oneyear", "twoyear")
                .list();
    }

    public List<KeyValueEntity> sumReimbursements(Appropriation appropriation, List<Fund> funds) {
        return dataManager.loadValues(
                        "SELECT cat.masterObjectClass, div.divisionCode, act.fund, sum(reim.amount)"
                                + " FROM fis_ActivityReimbursement reim"
                                + " INNER JOIN fis_ObjectClass obj ON obj = reim.objectClass"
                                + " INNER JOIN fis_Category cat ON cat = obj.category"
                                + " INNER JOIN fis_Activity act ON act = reim.activity"
                                + " INNER JOIN fis_Division div ON div = act.division"
                                + " WHERE div.appropriation = :appropriation"
                                + " AND act.fund in :funds"
                                + " GROUP BY cat.masterObjectClass, div.divisionCode, act.fund"
                                + " ORDER BY cat.masterObjectClass, div.divisionCode, act.fund.fundCode")
                .properties("moc", "divcode", "fund", "amount")
                .parameter("appropriation", appropriation)
                .parameter("funds", funds)
                .list();
    }

    public List<ActivityReimbursement> fetchBiFiscalActivityReimbursements(Appropriation currentYearAppropriation) {
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.load(ActivityReimbursement.class)
                .query("SELECT r FROM fis_ActivityReimbursement r"
                        + " WHERE r.activity IN "
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
                .fetchPlan("extended")
                .list();
    }

    public List<ActivityReimbursementDto> getReimbursementDtos(Appropriation appropriation, List<ActivityDto> activityDtos) {
        var reimbursements = getReimbursementsNew(appropriation, activityDtos);
        List<ActivityReimbursementDto> reimbursementDtos = new ArrayList<>();
        ActivityReimbursementDto dto;

        for (var kvEntity : reimbursements) {
            dto = dataManager.create(ActivityReimbursementDto.class);
            dto.setId(kvEntity.getValue("id"));
            dto.setFundId(kvEntity.getValue("fundId"));
            dto.setFundCode(kvEntity.getValue("fundCode"));
            dto.setAppropriationId(kvEntity.getValue("appropriationId"));
            dto.setBudgetFiscalYear(kvEntity.getValue("budgetFiscalYear"));
            dto.setDivisionId(kvEntity.getValue("divisionId"));
            dto.setDivisionCode(kvEntity.getValue("divisionCode"));
            dto.setActivityId(kvEntity.getValue("activityId"));
            dto.setActivityNumber(kvEntity.getValue("activityNumber"));
            dto.setActivityTitle(kvEntity.getValue("activityTitle"));
            dto.setCategoryId(kvEntity.getValue("categoryId"));
            dto.setMasterObjectClass(kvEntity.getValue("masterObjectClass"));
            dto.setObjectClassId(kvEntity.getValue("objectClassId"));
            dto.setBudgetObjectClass(kvEntity.getValue("budgetObjectClass"));
            dto.setDocumentNumber(kvEntity.getValue("documentNumber"));
            dto.setSource(kvEntity.getValue("source"));
            dto.setAmount(kvEntity.getValue("amount"));
            dto.setPriorTwoYearAmount(kvEntity.getValue("prioryear"));
            dto.setCurrentOneYearAmount(kvEntity.getValue("oneyear"));
            dto.setCurrentTwoYearAmount(kvEntity.getValue("twoyear"));
            reimbursementDtos.add(dto);
        }
        return reimbursementDtos;
    }
}