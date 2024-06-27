package gov.fjc.fis.reportdata;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.dto.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static gov.fjc.fis.FisUtilities.*;

public class BudgetRequestReportData {
    private final String budgetFiscalYear;
    private final String priorBudgetFiscalYear;

    private BigDecimal uncommittedPriorTwoYearBalance = BigDecimal.ZERO;
    private BigDecimal uncommittedCurrentOneYearBalance = BigDecimal.ZERO;
    private BigDecimal uncommittedCurrentTwoYearBalance = BigDecimal.ZERO;

    List<DivisionDto> divisions;
    List<ActivityDto> activities;
    List<ActivityProjectionDto> projections;
    List<ActivityReimbursementDto> reimbursements;
    List<ObligationDto> obligations;
    List<CategoryDto> categories;
    List<CategoryDto> hillPlanCategories;
    List<ObjectClassDto> objectClasses;

    private final LocalDateTime reportDateTime;

    public BudgetRequestReportData(Appropriation appropriation, Appropriation previousAppropriation) {
        this.budgetFiscalYear = appropriation.getBudgetFiscalYear();
        this.priorBudgetFiscalYear = previousAppropriation.getBudgetFiscalYear();
        reportDateTime = getDateTime();
    }

    public String getBudgetFiscalYear() {
        return budgetFiscalYear;
    }

    public String getPriorBudgetFiscalYear() {
        return priorBudgetFiscalYear;
    }

    public BigDecimal getUncommittedPriorTwoYearBalance() {
        return uncommittedPriorTwoYearBalance;
    }

    public void setUncommittedPriorTwoYearBalance(BigDecimal uncommittedPriorTwoYearBalance) {
        this.uncommittedPriorTwoYearBalance = uncommittedPriorTwoYearBalance;
    }

    public BigDecimal getUncommittedCurrentOneYearBalance() {
        return uncommittedCurrentOneYearBalance;
    }

    public void setUncommittedCurrentOneYearBalance(BigDecimal uncommittedCurrentOneYearBalance) {
        this.uncommittedCurrentOneYearBalance = uncommittedCurrentOneYearBalance;
    }

    public BigDecimal getUncommittedCurrentTwoYearBalance() {
        return uncommittedCurrentTwoYearBalance;
    }

    public void setUncommittedCurrentTwoYearBalance(BigDecimal uncommittedCurrentTwoYearBalance) {
        this.uncommittedCurrentTwoYearBalance = uncommittedCurrentTwoYearBalance;
    }

    public List<DivisionDto> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<DivisionDto> divisions) {
        this.divisions = divisions;
    }

    public List<ActivityDto> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDto> activities) {
        this.activities = activities;
    }

    public List<ActivityProjectionDto> getProjections() {
        return projections;
    }

    public void setProjections(List<ActivityProjectionDto> projections) {
        this.projections = projections;
    }

    public List<ActivityReimbursementDto> getReimbursements() {
        return reimbursements;
    }

    public void setReimbursements(List<ActivityReimbursementDto> reimbursements) {
        this.reimbursements = reimbursements;
    }

    public List<ObligationDto> getObligations() {
        return obligations;
    }


    public void setObligations(List<ObligationDto> obligations) {
        this.obligations = obligations;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public List<CategoryDto> getHillPlanCategories() {
        return hillPlanCategories;
    }

    public void setHillPlanCategories(List<CategoryDto> hillPlanCategories) {
        this.hillPlanCategories = hillPlanCategories;
    }

    public List<ObjectClassDto> getObjectClasses() {
        return objectClasses;
    }

    public void setObjectClasses(List<ObjectClassDto> objectClasses) {
        this.objectClasses = objectClasses;
    }

    public String getReportDateTime() {
        return getDateTimeReportString(reportDateTime);
    }

    public String getFileName() {
        return "Budget Request FY"
                .concat(budgetFiscalYear)
                .concat(" as of ")
                .concat(getDateTimeFilenameString(reportDateTime));
    }
}
