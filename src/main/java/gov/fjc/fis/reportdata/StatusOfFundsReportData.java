package gov.fjc.fis.reportdata;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.dto.CategoryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static gov.fjc.fis.FisUtilities.*;

public class StatusOfFundsReportData {
    private final LocalDateTime reportDateTime;
    private Appropriation appropriation;
    private final String budgetFiscalYear;
    private BigDecimal oneYearAppropriation;
    private BigDecimal twoYearAppropriation;

    private List<CategoryDto> categories;

    public StatusOfFundsReportData(Appropriation appropriation) {
        this.budgetFiscalYear = appropriation.getBudgetFiscalYear();
        this.oneYearAppropriation = appropriation.getOneYearAmount();
        this.twoYearAppropriation = appropriation.getTwoYearAmount();
        this.appropriation = appropriation;
        reportDateTime = getDateTime();
    }

    public String getBudgetFiscalYear() {
        return budgetFiscalYear;
    }

    public BigDecimal getOneYearAppropriation() {
        return oneYearAppropriation;
    }

    public void setOneYearAppropriation(BigDecimal oneYearAppropriation) {
        this.oneYearAppropriation = oneYearAppropriation;
    }

    public BigDecimal getTwoYearAppropriation() {
        return twoYearAppropriation;
    }

    public void setTwoYearAppropriation(BigDecimal twoYearAppropriation) {
        this.twoYearAppropriation = twoYearAppropriation;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public Appropriation getAppropriation() {
        return appropriation;
    }

    public void setAppropriation(Appropriation appropriation) {
        this.appropriation = appropriation;
    }

    public BigDecimal getTotalOneYearAllocations(String divCode) {
        return categories.stream()
                .map(allocations -> allocations.getOneYearAllocations(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOneYearReimbursements(String divCode) {
        return categories.stream()
                .map(reimbursements -> reimbursements.getOneYearReimbursements(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOneYearProjections(String divCode) {
        return categories.stream()
                .map(projections -> projections.getOneYearProjections(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOneYearObligations(String divCode) {
        return categories.stream()
                .map(obligations -> obligations.getOneYearObligations(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearAllocations(String divCode) {
        return categories.stream()
                .map(allocations -> allocations.getTwoYearAllocations(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearReimbursements(String divCode) {
        return categories.stream()
                .map(reimbursements -> reimbursements.getTwoYearReimbursements(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearProjections(String divCode) {
        return categories.stream()
                .map(projections -> projections.getTwoYearProjections(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearObligations(String divCode) {
        return categories.stream()
                .map(obligations -> obligations.getTwoYearObligations(divCode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOneYearAllocations() {
        return categories.stream()
                .map(CategoryDto::getTotalOneYearAllocations)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearAllocations() {
        return categories.stream()
                .map(CategoryDto::getTotalTwoYearAllocations)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCombinedYearAllocations() {
        return getTotalOneYearAllocations().add(getTotalTwoYearAllocations());
    }

    public BigDecimal getTotalOneYearObligations() {
        return categories.stream()
                .map(CategoryDto::getTotalOneYearObligations)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearObligations() {
        return categories.stream()
                .map(CategoryDto::getTotalTwoYearObligations)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCombinedYearObligations() {
        return getTotalOneYearObligations().add(getTotalTwoYearObligations());
    }

    public BigDecimal getTotalOneYearProjections() {
        return categories.stream()
                .map(CategoryDto::getTotalOneYearProjections)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearProjections() {
        return categories.stream()
                .map(CategoryDto::getTotalTwoYearProjections)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCombinedYearProjections() {
        return getTotalOneYearProjections().add(getTotalTwoYearProjections());
    }


    public BigDecimal getTotalOneYearReimbursements() {
        return categories.stream()
                .map(CategoryDto::getTotalOneYearReimbursements)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTwoYearReimbursements() {
        return categories.stream()
                .map(CategoryDto::getTotalTwoYearReimbursements)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCombinedYearReimbursements() {
        return getTotalOneYearReimbursements().add(getTotalTwoYearReimbursements());
    }

    public String getReportDateTime() {
        return getDateTimeReportString(reportDateTime);
    }

    public String getFileName() {
        return "Status of Funds FY"
                .concat(budgetFiscalYear)
                .concat(" as of ")
                .concat(getDateTimeFilenameString(reportDateTime));
    }
}
