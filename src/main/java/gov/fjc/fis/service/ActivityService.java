package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.ActivityDto;
import gov.fjc.fis.entity.dto.ActivityProjectionDto;
import gov.fjc.fis.entity.dto.ActivityReimbursementDto;
import gov.fjc.fis.entity.dto.ObligationDto;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static gov.fjc.fis.entity.ActivityFundingType.*;

@Component("fis_ActivityService")
public class ActivityService {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FundService fundService;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private ObligationService obligationService;

    /**
     * Determines whether a generic activity exists for the supplied activity. Generic activities are only used
     * for training projects that have been assigned a branch.
     *
     * @param activity any valid Activity
     * @return either a generic activity, if exists, or the original activity
     */
    public Activity getGenericActivity(Activity activity) {
        if (!activity.getTrainingProject() || activity.getBranch() == null) {
            return activity;
        }
        String genericNumber = activity.getBranch().getBranchCode().concat("00");
        Optional<Activity> genericActivity = dataManager.load(Activity.class)
                .query("SELECT a FROM fis_Activity a"
                        + " WHERE a.division = :division"
                        + " AND a.activityNumber = :genericActivity")
                .parameter("division", activity.getDivision())
                .parameter("genericActivity", genericNumber)
                .optional();
        return genericActivity.orElse(activity);
    }

    public BigDecimal sumProjections(Activity activity) {
        BigDecimal projections = dataManager.load(BigDecimal.class)
                .query("SELECT coalesce(sum(p.amount),0)"
                        + " FROM fis_ActivityProjection p"
                        + " WHERE p.activity = :activity")
                .parameter("activity", activity)
                .one();
        return BigDecimal.ZERO;
    }

    public Activity getTrainingActivity(Division division, String activityNumber) {
        return dataManager.load(Activity.class)
                .query("SELECT a FROM fis_Activity a"
                        + " WHERE a.division = :division"
                        + " AND a.trainingProject = true"
                        + " AND a.activityNumber = :activityNumber")
                .parameter("division", division)
                .parameter("activityNumber", activityNumber)
                .optional()
                .orElse(null);
    }

    public List<Activity> getActivities(Division division) {
        return getActivities(null, null, division, null, null);
    }

    public List<Activity> getActivities(Branch branch) {
        return getActivities(null, null, null, branch, null);
    }

    public List<Activity> getActivities(Group group) {
        return getActivities(null, null, null, null, group);
    }

    public List<Activity> getActivities(Division division, Branch branch) {
        return getActivities(null, null, division, branch, null);
    }

    /**
     * getActivities - method should remain private
     *
     * @param fund          Fund or null
     * @param appropriation Appropriation or null
     * @param division      Division or null
     * @param branch        Branch or null
     * @param group         Group or Null
     * @return List of Activity entities
     */
    private List<Activity> getActivities(Fund fund,
                                         Appropriation appropriation,
                                         Division division,
                                         Branch branch,
                                         Group group) {

        return dataManager.load(Activity.class)
                .query("SELECT a FROM fis_Activity a"
                        + " INNER JOIN fis_Division dv ON dv = a.division"
                        + " WHERE (:anyFund = true OR a.fund = :fund)"
                        + " AND (:anyAppropriation = true OR dv.appropriation = :appropriation)"
                        + " AND (:anyDivision = true OR a.division = :division)"
                        + " AND (:anyBranch = true OR a.branch = :branch)"
                        + " AND (:anyGroup = true OR a.group = :group)"
                        + " ORDER BY dv.divisionCode, a.startDate")
                .parameter("anyFund", fund == null)
                .parameter("fund", fund)
                .parameter("anyAppropriation", appropriation == null)
                .parameter("appropriation", appropriation)
                .parameter("anyDivision", division == null)
                .parameter("division", division)
                .parameter("anyBranch", branch == null)
                .parameter("branch", branch)
                .parameter("anyGroup", group == null)
                .parameter("group", group)
                .list();
    }


    /**
     * retrieves all programs taking place during fiscal year of Division. Returns empty List if mismatched parameters.
     *
     * @param division Division
     * @param branch   Branch or null
     * @return List of Activity entities
     */
    public List<Activity> fetchBiFiscalActivities(Division division, Branch branch) {
        Appropriation currentYearAppropriation = division.getAppropriation();
        String branchCode = branch == null ? null : branch.getBranchCode();
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.load(Activity.class)
                .query("SELECT a FROM fis_Activity a"
                        + " LEFT JOIN fis_Branch bch ON bch = a.branch"
                        + " INNER JOIN fis_Division dv ON dv = a.division"
                        + " INNER JOIN fis_Appropriation app ON app = dv.appropriation"
                        + " INNER JOIN fis_Fund fund ON fund = a.fund"
                        + " WHERE dv.divisionCode = :divisionCode"
                        + " AND (:anyBranch = true OR bch.branchCode = :branchCode)"
                        + " AND ((dv.appropriation = :currentYear AND a.fund = :oneYearFund)"
                        + " OR (dv.appropriation = :priorYear AND a.fund = :twoYearFund AND a.endDate >= :bfyStartDate)"
                        + " OR (dv.appropriation = :currentYear AND a.fund = :twoYearFund AND (a.endDate IS NULL OR a.endDate <= :bfyEndDate)))"
                        + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, bch.sortCode, bch.branchCode, a.activityNumber")
                .parameter("divisionCode", division.getDivisionCode())
                .parameter("anyBranch", branch == null)
                .parameter("branchCode", branchCode)
                .parameter("currentYear", currentYearAppropriation)
                .parameter("priorYear", priorYearAppropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("bfyStartDate", bfyStartDate)
                .parameter("bfyEndDate", bfyEndDate)
                .list();
    }


    /**
     * used by obligation lookup screen to find activities for given appropriation year and division
     *
     * @param appropriation required
     * @param division      null allowed
     * @param foundation    boolean to indicate whether to use FJC Foundation fund
     * @return List of Activities
     */
    public List<Activity> getObligationActivities(Appropriation appropriation, Division division, boolean foundation) {
        Fund fund = fundService.getFoundationFund();
        return dataManager.load(Activity.class)
                .query("SELECT distinct act FROM fis_Activity act"
                        + " INNER JOIN fis_Division dv ON dv = act.division"
                        + " INNER JOIN fis_Obligation obl ON obl.activity = act"
                        + " WHERE act.division.appropriation= :appropriation"
                        + " AND (:divisionNull = true OR act.division = :division)"
                        + " AND ((:foundation = true AND act.fund = :fund) "
                        + " OR (:foundation = false AND act.fund <> :fund))"
                        + " ORDER BY dv.divisionCode, act.activityNumber")
                .parameter("appropriation", appropriation)
                .parameter("divisionNull", division == null)
                .parameter("division", division)
                .parameter("foundation", foundation)
                .parameter("fund", fund)
                .list();
    }

    public List<Activity> fetchBiFiscalActivities(Appropriation currentYearAppropriation) {
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.load(Activity.class)
                .query("SELECT a FROM fis_Activity a"
                        + " INNER JOIN fis_Division dv ON dv = a.division"
                        + " INNER JOIN fis_Appropriation app ON app = dv.appropriation"
                        + " INNER JOIN fis_Fund fund ON fund = a.fund"
                        + " WHERE (app = :currentYear AND fund = :oneYearFund)"
                        + " OR (app = :priorYear AND fund = :twoYearFund AND a.endDate >= :bfyStartDate)"
                        + " OR (app = :currentYear AND fund = :twoYearFund AND (a.endDate IS NULL OR a.endDate <= :bfyEndDate))"
                        + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, a.activityNumber")
                .parameter("currentYear", currentYearAppropriation)
                .parameter("priorYear", priorYearAppropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("bfyStartDate", bfyStartDate)
                .parameter("bfyEndDate", bfyEndDate)
                .list();
    }

    // NEW - should fetch KV, not entities
    public List<KeyValueEntity> fetchBiFiscalActivitiesNew(Appropriation currentYearAppropriation) {
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.loadValues(
                        "SELECT act.id, fund.id, fund.fundCode, app.id, app.budgetFiscalYear, dv.id, dv.divisionCode, act.activityNumber,"
                                + " act.title, act.startDate, act.endDate, bch.id, bch.branchCode, bch.title, grp.id, grp.groupCode, grp.title,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN :priorTwoYearFund"
                                + "      WHEN dv.appropriation = :currentYear AND act.fund = :twoYearFund THEN :currentTwoYearFund"
                                + "      ELSE :currentOneYearFund"
                                + " END"
                                + " FROM fis_Activity act"
                                + " LEFT JOIN fis_Branch bch ON bch=act.branch"
                                + " LEFT JOIN fis_Group grp ON grp=act.group"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " WHERE (dv.appropriation = :currentYear AND act.fund = :oneYearFund)"
                                + " OR (dv.appropriation = :priorYear AND act.fund = :twoYearFund AND act.endDate >= :bfyStartDate)"
                                + " OR (dv.appropriation = :currentYear AND act.fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate))"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, act.activityNumber")
                .parameter("priorYear", priorYearAppropriation)
                .parameter("currentYear", currentYearAppropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("bfyStartDate", bfyStartDate)
                .parameter("bfyEndDate", bfyEndDate)
                .parameter("priorTwoYearFund", PRIOR_TWO_YEAR_FUND.getId())
                .parameter("currentOneYearFund", CURRENT_ONE_YEAR_FUND.getId())
                .parameter("currentTwoYearFund", CURRENT_TWO_YEAR_FUND.getId())
                .properties("id", "fundId", "fundCode", "appropriationId", "budgetFiscalYear", "divisionId", "divisionCode", "activityNumber", "title", "startDate", "endDate", "branchId", "branchCode", "branchTitle", "groupId", "groupCode", "groupTitle", "fundingType")
                .list();
    }

    public List<KeyValueEntity> fetchBiFiscalActivitiesNew(Division division, Branch branch) {
        Appropriation currentYearAppropriation = division.getAppropriation();
        String branchCode = branch == null ? null : branch.getBranchCode();
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.loadValues(
                        "SELECT act.id, fund.id, fund.fundCode, app.id, app.budgetFiscalYear, dv.id, dv.divisionCode, act.activityNumber,"
                                + " act.title, act.startDate, act.endDate, bch.id, bch.branchCode, bch.title, grp.id, grp.groupCode, grp.title,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN :priorTwoYearFund"
                                + "      WHEN dv.appropriation = :currentYear AND act.fund = :twoYearFund THEN :currentTwoYearFund"
                                + "      ELSE :currentOneYearFund"
                                + " END"
                                + " FROM fis_Activity act"
                                + " LEFT JOIN fis_Branch bch ON bch=act.branch"
                                + " LEFT JOIN fis_Group grp ON grp=act.group"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " WHERE dv.divisionCode = :divisionCode"
                                + " AND (:anyBranch = true OR bch.branchCode = :branchCode)"
                                + " AND ((dv.appropriation = :currentYear AND act.fund = :oneYearFund)"
                                + " OR (dv.appropriation = :priorYear AND act.fund = :twoYearFund AND act.endDate >= :bfyStartDate)"
                                + " OR (dv.appropriation = :currentYear AND act.fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate)))"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, bch.sortCode, bch.branchCode, act.activityNumber")
                .parameter("divisionCode", division.getDivisionCode())
                .parameter("anyBranch", branch == null)
                .parameter("branchCode", branchCode)
                .parameter("currentYear", currentYearAppropriation)
                .parameter("priorYear", priorYearAppropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("bfyStartDate", bfyStartDate)
                .parameter("bfyEndDate", bfyEndDate)
                .parameter("priorTwoYearFund", PRIOR_TWO_YEAR_FUND.getId())
                .parameter("currentOneYearFund", CURRENT_ONE_YEAR_FUND.getId())
                .parameter("currentTwoYearFund", CURRENT_TWO_YEAR_FUND.getId())
                .properties("id", "fundId", "fundCode", "appropriationId", "budgetFiscalYear", "divisionId", "divisionCode", "activityNumber", "title", "startDate", "endDate", "branchId", "branchCode", "branchTitle", "groupId", "groupCode", "groupTitle", "fundingType")
                .list();
    }


    /**
     * Query database for activity related data for an Appropriation, Division, or Division and Branch
     *
     * @param appropriation persisted Appropriation entity, required if division parameter is null
     * @param division      persisted Division entity, required if appropriation parameter is null
     * @param branch        Branch entity, null allowed
     * @return List of KeyValue Entities containing matching Activities
     */
    private List<KeyValueEntity> fetchBiFiscalActivitiesNew2(Appropriation appropriation, Division division, Branch branch) {
        Appropriation currentYearAppropriation = appropriation == null ? division.getAppropriation() : appropriation;
        String divisionCode = division == null ? "" : division.getDivisionCode();
        String branchCode = branch == null ? "" : branch.getBranchCode();
        Appropriation priorYearAppropriation = appropriationService.getPreviousFiscalYear(currentYearAppropriation);
        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        Date bfyStartDate = appropriationService.getFirstDayOfAppropriationBfy(currentYearAppropriation);
        Date bfyEndDate = appropriationService.getLastDayOfAppropriationBfy(currentYearAppropriation);

        return dataManager.loadValues(
                        "SELECT act.id, fund.id, fund.fundCode, app.id, app.budgetFiscalYear, dv.id, dv.divisionCode,"
                                + " act.activityNumber, act.title, act.startDate, act.endDate, act.city, act.state, bch.id,"
                                + " bch.branchCode, bch.title, grp.id, grp.groupCode, grp.title,"
                                + " CASE WHEN dv.appropriation = :priorYear AND act.fund = :twoYearFund THEN :priorTwoYearFund"
                                + "      WHEN dv.appropriation = :currentYear AND act.fund = :twoYearFund THEN :currentTwoYearFund"
                                + "      ELSE :currentOneYearFund"
                                + " END"
                                + " FROM fis_Activity act"
                                + " LEFT JOIN fis_Branch bch ON bch=act.branch"
                                + " LEFT JOIN fis_Group grp ON grp=act.group"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " WHERE (:anyDivision = true OR dv.divisionCode = :divisionCode)"
                                + " AND (:anyBranch = true OR bch.branchCode = :branchCode)"
                                + " AND ((dv.appropriation = :currentYear AND act.fund = :oneYearFund)"
                                + " OR (dv.appropriation = :priorYear AND act.fund = :twoYearFund AND act.endDate >= :bfyStartDate)"
                                + " OR (dv.appropriation = :currentYear AND act.fund = :twoYearFund AND (act.endDate IS NULL OR act.endDate <= :bfyEndDate)))"
                                + " ORDER BY app.budgetFiscalYear, fund.fundCode, dv.divisionCode, bch.sortCode, bch.branchCode, act.activityNumber")
                .parameter("anyDivision", division == null)
                .parameter("divisionCode", divisionCode)
                .parameter("anyBranch", branch == null)
                .parameter("branchCode", branchCode)
                .parameter("currentYear", currentYearAppropriation)
                .parameter("priorYear", priorYearAppropriation)
                .parameter("oneYearFund", oneYearFund)
                .parameter("twoYearFund", twoYearFund)
                .parameter("bfyStartDate", bfyStartDate)
                .parameter("bfyEndDate", bfyEndDate)
                .parameter("priorTwoYearFund", PRIOR_TWO_YEAR_FUND.getId())
                .parameter("currentOneYearFund", CURRENT_ONE_YEAR_FUND.getId())
                .parameter("currentTwoYearFund", CURRENT_TWO_YEAR_FUND.getId())
                .properties("id", "fundId", "fundCode", "appropriationId", "budgetFiscalYear", "divisionId", "divisionCode",
                        "activityNumber", "title", "startDate", "endDate", "city", "state", "branchId", "branchCode",
                        "branchTitle", "groupId", "groupCode", "groupTitle", "fundingType")
                .list();
    }

    // used by reports
    private List<ActivityDto> convertBiFiscalEntitiesToActivityDtos(List<KeyValueEntity> activities) {
        List<ActivityDto> activityDtos = new ArrayList<>();
        ActivityDto dto;

        for (var kvEntity : activities) {
            dto = dataManager.create(ActivityDto.class);
            dto.setId(kvEntity.getValue("id"));
            dto.setFundId(kvEntity.getValue("fundId"));
            dto.setFundCode(kvEntity.getValue("fundCode"));
            dto.setAppId(kvEntity.getValue("appropriationId"));
            dto.setBudgetFiscalYear(kvEntity.getValue("budgetFiscalYear"));
            dto.setDivisionId(kvEntity.getValue("divisionId"));
            dto.setDivisionCode(kvEntity.getValue("divisionCode"));
            dto.setActivityNumber(kvEntity.getValue("activityNumber"));
            dto.setTitle(kvEntity.getValue("title"));
            dto.setStartDate(kvEntity.getValue("startDate"));
            dto.setCity(kvEntity.getValue("city"));
            dto.setState(kvEntity.getValue("state"));
            dto.setEndDate(kvEntity.getValue("endDate"));
            dto.setBranchId(kvEntity.getValue("branchId"));
            dto.setBranchCode(kvEntity.getValue("branchCode"));
            dto.setBranchTitle(kvEntity.getValue("branchTitle"));
            dto.setGroupId(kvEntity.getValue("groupId"));
            dto.setGroupCode(kvEntity.getValue("groupCode"));
            dto.setGroupTitle(kvEntity.getValue("groupTitle"));
            dto.setFundingType(ActivityFundingType.fromId(kvEntity.getValue("fundingType")));
            activityDtos.add(dto);
        }
        return activityDtos;
    }

    /**
     * Return bifiscal ActivityDTO entities for specified Appropriation
     *
     * @param appropriation persisted Appropriation entity
     * @return List of ActivityDTO objects
     */
    public List<ActivityDto> getBiFiscalActivityDtos(Appropriation appropriation) {
        List<KeyValueEntity> activities = fetchBiFiscalActivitiesNew2(appropriation, null, null);
        List<ActivityDto> activityDtos = convertBiFiscalEntitiesToActivityDtos(activities);
        return activityDtos;
    }

    /**
     * Return bifiscal ActivityDTO entities for specified division and branch
     *
     * @param division persisted Division entity
     * @param branch   Branch entity or null
     * @return List of ActivityDTO objects
     */
    public List<ActivityDto> getBiFiscalActivityDtos(Division division, Branch branch) {
        List<KeyValueEntity> activities = fetchBiFiscalActivitiesNew2(null, division, branch);
        List<ActivityDto> activityDtos = convertBiFiscalEntitiesToActivityDtos(activities);
        return activityDtos;
    }

    public List<ActivityDto> updateActivityAmounts(List<ActivityDto> activityDtos,
                                                   List<ObligationDto> obligations,
                                                   List<ActivityProjectionDto> projections,
                                                   List<ActivityReimbursementDto> reimbursements) {
        for (ActivityDto act : activityDtos) {
            var obs = obligations.stream().filter(obligationDto -> obligationDto.getActivityId().equals(act.getId())).toList();
            act.setTotalObligated(obs.stream().map(ObligationDto::getObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setPriorTwoYearObligations(obs.stream().map(ObligationDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setCurrentOneYearObligations(obs.stream().map(ObligationDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setCurrentTwoYearObligations(obs.stream().map(ObligationDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            var proj = projections.stream().filter(projectionDto -> projectionDto.getActivityId().equals(act.getId())).toList();
            act.setTotalProjected(proj.stream().map(ActivityProjectionDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setPriorTwoYearProjections(proj.stream().map(ActivityProjectionDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setCurrentOneYearProjections(proj.stream().map(ActivityProjectionDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setCurrentTwoYearProjections(proj.stream().map(ActivityProjectionDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            var reim = reimbursements.stream().filter(reimbursementDto -> reimbursementDto.getActivityId().equals(act.getId())).toList();
            act.setTotalReimbursed(reim.stream().map(ActivityReimbursementDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setPriorTwoYearReimbursements(reim.stream().map(ActivityReimbursementDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setCurrentOneYearReimbursements(reim.stream().map(ActivityReimbursementDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            act.setCurrentTwoYearReimbursements(reim.stream().map(ActivityReimbursementDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return activityDtos;
    }
}