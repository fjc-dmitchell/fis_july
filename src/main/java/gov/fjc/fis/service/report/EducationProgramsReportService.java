package gov.fjc.fis.service.report;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.reportdata.EducationProgramsReportData;
import gov.fjc.fis.service.*;
import org.springframework.stereotype.Component;

@Component("fis_EducationProgramReportService")
public class EducationProgramsReportService {

    private final AppropriationService appropriationService;
    private final ActivityService activityService;
    private final ActivityProjectionService activityProjectionService;
    private final ActivityReimbursementService activityReimbursementService;
    private final ObligationService obligationService;

    public EducationProgramsReportService(AppropriationService appropriationService,
                                          ActivityService activityService,
                                          ActivityProjectionService activityProjectionService,
                                          ActivityReimbursementService activityReimbursementService,
                                          ObligationService obligationService) {
        this.appropriationService = appropriationService;
        this.activityService = activityService;
        this.activityProjectionService = activityProjectionService;
        this.activityReimbursementService = activityReimbursementService;
        this.obligationService = obligationService;
    }

    /**
     * Generate education programs report data. Requires a valid Branch or Division.
     *
     * @param division Division entity
     * @param branch   Branch entity, may be null
     * @return EducationProgramsReportData
     */
    public EducationProgramsReportData generateReportData(Division division,
                                                          Branch branch) {

        division = branch == null ? division : branch.getDivision();

        var appropriation = division.getAppropriation();
        var priorYearAppropriation = appropriationService.getPreviousFiscalYear(appropriation);

        var activities = activityService.getBiFiscalActivityDtos(division, branch);
        var obligations = obligationService.getObligationDtos(appropriation, activities);
        var projections = activityProjectionService.getProjectionDtos(appropriation, activities, false);
        var reimbursements = activityReimbursementService.getReimbursementDtos(appropriation, activities);
        activityService.updateActivityAmounts(activities, obligations, projections, reimbursements);

        // either create empty lists OR put all parameters in constructor. They are required.
        EducationProgramsReportData reportData = new EducationProgramsReportData(appropriation, division, branch);
        reportData.setPriorBudgetFiscalYear(priorYearAppropriation);
        reportData.setProjections(projections);
        reportData.setReimbursements(reimbursements);
        reportData.setObligations(obligations);
        reportData.setActivities(activities);

        return reportData;
    }
}
