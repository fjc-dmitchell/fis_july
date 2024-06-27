package gov.fjc.fis.reportdata;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.dto.ActivityDto;
import gov.fjc.fis.entity.dto.ActivityProjectionDto;
import gov.fjc.fis.entity.dto.ActivityReimbursementDto;
import gov.fjc.fis.entity.dto.ObligationDto;

import java.time.LocalDateTime;
import java.util.List;

import static gov.fjc.fis.FisUtilities.*;

public class EducationProgramsReportData {

    private final String budgetFiscalYear;
    private String priorBudgetFiscalYear;
    private final String divisionTitle;
    private final String branchTitle;
    private final String divisionAndBranch;
    private final LocalDateTime reportDateTime;

    private List<ActivityDto> activities;
    private List<ActivityReimbursementDto> reimbursements;
    private List<ActivityProjectionDto> projections;
    private List<ObligationDto> obligations;

    public EducationProgramsReportData(Appropriation appropriation, Division division, Branch branch) {
        budgetFiscalYear = appropriation == null ? "" : appropriation.getBudgetFiscalYear();
        divisionTitle = division == null ? "" : division.getTitle();
        branchTitle = branch == null ? null : ": ".concat(branch.getTitle());
        divisionAndBranch = branch == null ? divisionTitle : divisionTitle.concat("-").concat(branch.getTitle());
        reportDateTime = getDateTime();
    }

    public void setPriorBudgetFiscalYear(Appropriation priorYearAppropriation) {
        priorBudgetFiscalYear = priorYearAppropriation == null ? null : priorYearAppropriation.getBudgetFiscalYear();
    }

    public String getPriorBudgetFiscalYear() {
        return priorBudgetFiscalYear;
    }

    public String getBudgetFiscalYear() {
        return budgetFiscalYear;
    }

    public String getDivisionAndBranch() {
        return divisionAndBranch;
    }

    public List<ActivityDto> getPriorTwoYearActivities() {
        return activities.stream().filter(ActivityDto::isPriorTwoYearActivity).toList();

    }

    public List<ActivityDto> getCurrentOneYearActivities() {
        return activities.stream().filter(ActivityDto::isCurrentOneYearActivity).toList();
    }

    public List<ActivityDto> getCurrentTwoYearActivities() {
        return activities.stream().filter(ActivityDto::isCurrentTwoYearActivity).toList();
    }

    public List<ActivityDto> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDto> activities) {
        this.activities = activities;
    }

    public List<ActivityReimbursementDto> getReimbursements() {
        return reimbursements;
    }

    public void setReimbursements(List<ActivityReimbursementDto> reimbursements) {
        this.reimbursements = reimbursements;
    }

    public List<ActivityProjectionDto> getProjections() {
        return projections;
    }

    public void setProjections(List<ActivityProjectionDto> projections) {
        this.projections = projections;
    }

    public List<ObligationDto> getObligations() {
        return obligations;
    }

    public void setObligations(List<ObligationDto> obligations) {
        this.obligations = obligations;
    }

    public String getReportDateTime() {
        return getDateTimeReportString(reportDateTime);
    }

    public String getFileName() {
        return divisionAndBranch
                .concat(" programs for FY ")
                .concat(budgetFiscalYear)
                .concat(" as of ")
                .concat(getDateTimeFilenameString(reportDateTime));
    }
}
