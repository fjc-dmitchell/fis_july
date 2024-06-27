package gov.fjc.fis.entity.dto;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.ActivityFundingType;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.Fund;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

@JmixEntity(name = "fis_ActivityDto")
public class ActivityDto {
    private Integer id;

    private String activityNumber;

    @InstanceName
    private String title;

    private String city;

    private String state;

    private Integer appId;

    private String budgetFiscalYear;

    private Integer fundId;

    private String fundCode;

    private Division division;

    private Integer divisionId;

    private String divisionCode;

    private Integer branchId;

    private String branchCode;

    private String branchTitle;

    private Integer groupId;

    private String groupCode;

    private String groupTitle;

    private Fund fund;

    private Integer fundingType;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Composition
    private List<ObligationDto> obligationDtos;

    @Composition
    private List<ObjectClassDto> objectClassDtos;

    private BigDecimal totalProjected = BigDecimal.ZERO;

    private BigDecimal totalReimbursed = BigDecimal.ZERO;

    private BigDecimal totalObligated = BigDecimal.ZERO;

    private BigDecimal totalDisbursed = BigDecimal.ZERO;

    private BigDecimal totalBalance = BigDecimal.ZERO;

    private BigDecimal totalInvoiced = BigDecimal.ZERO;

    private BigDecimal priorTwoYearProjections = BigDecimal.ZERO;

    private BigDecimal priorTwoYearReimbursements = BigDecimal.ZERO;

    private BigDecimal priorTwoYearObligations = BigDecimal.ZERO;

    private BigDecimal priorTwoYearDisbursements = BigDecimal.ZERO;

    private BigDecimal currentOneYearProjections = BigDecimal.ZERO;

    private BigDecimal currentOneYearReimbursements = BigDecimal.ZERO;

    private BigDecimal currentOneYearObligations = BigDecimal.ZERO;

    private BigDecimal currentOneYearDisbursements = BigDecimal.ZERO;

    private BigDecimal currentTwoYearProjections = BigDecimal.ZERO;

    private BigDecimal currentTwoYearReimbursements = BigDecimal.ZERO;

    private BigDecimal currentTwoYearObligations = BigDecimal.ZERO;

    private BigDecimal currentTwoYearDisbursements = BigDecimal.ZERO;

    public ActivityFundingType getFundingType() {
        return fundingType == null ? null : ActivityFundingType.fromId(fundingType);
    }

    public void setFundingType(ActivityFundingType fundingType) {
        this.fundingType = fundingType == null ? null : fundingType.getId();
    }

    public boolean isPriorTwoYearActivity() {
        return getFundingType().equals(ActivityFundingType.PRIOR_TWO_YEAR_FUND);
    }

    public boolean isCurrentOneYearActivity() {
        return getFundingType().equals(ActivityFundingType.CURRENT_ONE_YEAR_FUND);
    }

    public boolean isCurrentTwoYearActivity() {
        return getFundingType().equals(ActivityFundingType.CURRENT_TWO_YEAR_FUND);
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setDivisionId(Integer divisionId) {
        this.divisionId = divisionId;
    }

    public Integer getDivisionId() {
        return divisionId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getFundId() {
        return fundId;
    }

    public void setFundId(Integer fundId) {
        this.fundId = fundId;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void configureActivityDto(Activity activity) {
        if (activity != null) {
            this.activityNumber = activity.getActivityNumber();
            this.budgetFiscalYear = activity.getDivision().getAppropriation().getBudgetFiscalYear();
            this.divisionCode = activity.getDivision().getDivisionCode();
            this.fundCode = activity.getFund().getFundCode();
            this.branchCode = activity.getBranch() == null ? null : activity.getBranch().getBranchCode();
            this.branchTitle = activity.getBranch() == null ? null : activity.getBranch().getTitle();
            this.groupCode = activity.getGroup() == null ? null : activity.getGroup().getGroupCode();
            this.groupTitle = activity.getGroup() == null ? null : activity.getGroup().getTitle();
            this.title = activity.getTitle();
            this.city = activity.getCity();
            this.state = activity.getState();
            this.startDate = activity.getStartDate();
            this.endDate = activity.getEndDate();
        }
    }

    public BigDecimal getCurrentTwoYearDisbursements() {
        return currentTwoYearDisbursements;
    }

    public void setCurrentTwoYearDisbursements(BigDecimal currentTwoYearDisbursements) {
        this.currentTwoYearDisbursements = requireNonNullElse(currentTwoYearDisbursements, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentTwoYearObligations() {
        return currentTwoYearObligations;
    }

    public void setCurrentTwoYearObligations(BigDecimal currentTwoYearObligations) {
        this.currentTwoYearObligations = requireNonNullElse(currentTwoYearObligations, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentTwoYearReimbursements() {
        return currentTwoYearReimbursements;
    }

    public void setCurrentTwoYearReimbursements(BigDecimal currentTwoYearReimbursements) {
        this.currentTwoYearReimbursements = requireNonNullElse(currentTwoYearReimbursements, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentTwoYearProjections() {
        return currentTwoYearProjections;
    }

    public void setCurrentTwoYearProjections(BigDecimal currentTwoYearProjections) {
        this.currentTwoYearProjections = requireNonNullElse(currentTwoYearProjections, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentOneYearDisbursements() {
        return currentOneYearDisbursements;
    }

    public void setCurrentOneYearDisbursements(BigDecimal currentOneYearDisbursements) {
        this.currentOneYearDisbursements = requireNonNullElse(currentOneYearDisbursements, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentOneYearObligations() {
        return currentOneYearObligations;
    }

    public void setCurrentOneYearObligations(BigDecimal currentOneYearObligations) {
        this.currentOneYearObligations = requireNonNullElse(currentOneYearObligations, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentOneYearReimbursements() {
        return currentOneYearReimbursements;
    }

    public void setCurrentOneYearReimbursements(BigDecimal currentOneYearReimbursements) {
        this.currentOneYearReimbursements = requireNonNullElse(currentOneYearReimbursements, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentOneYearProjections() {
        return currentOneYearProjections;
    }

    public void setCurrentOneYearProjections(BigDecimal currentOneYearProjections) {
        this.currentOneYearProjections = requireNonNullElse(currentOneYearProjections, BigDecimal.ZERO);
    }

    public BigDecimal getPriorTwoYearDisbursements() {
        return priorTwoYearDisbursements;
    }

    public void setPriorTwoYearDisbursements(BigDecimal priorTwoYearDisbursements) {
        this.priorTwoYearDisbursements = requireNonNullElse(priorTwoYearDisbursements, BigDecimal.ZERO);
    }

    public BigDecimal getPriorTwoYearObligations() {
        return priorTwoYearObligations;
    }

    public void setPriorTwoYearObligations(BigDecimal priorTwoYearObligations) {
        this.priorTwoYearObligations = requireNonNullElse(priorTwoYearObligations, BigDecimal.ZERO);
    }

    public BigDecimal getPriorTwoYearReimbursements() {
        return priorTwoYearReimbursements;
    }

    public void setPriorTwoYearReimbursements(BigDecimal priorTwoYearReimbursements) {
        this.priorTwoYearReimbursements = requireNonNullElse(priorTwoYearReimbursements, BigDecimal.ZERO);
    }

    public BigDecimal getPriorTwoYearProjections() {
        return priorTwoYearProjections;
    }

    public void setPriorTwoYearProjections(BigDecimal priorTwoYearProjections) {
        this.priorTwoYearProjections = requireNonNullElse(priorTwoYearProjections, BigDecimal.ZERO);
    }

    public BigDecimal getTotalInvoiced() {
        return totalInvoiced;
    }

    public void setTotalInvoiced(BigDecimal totalInvoiced) {
        this.totalInvoiced = requireNonNullElse(totalInvoiced, BigDecimal.ZERO);
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = requireNonNullElse(totalBalance, BigDecimal.ZERO);
    }

    public BigDecimal getTotalDisbursed() {
        return totalDisbursed;
    }

    public void setTotalDisbursed(BigDecimal totalDisbursed) {
        this.totalDisbursed = requireNonNullElse(totalDisbursed, BigDecimal.ZERO);
    }

    public BigDecimal getTotalObligated() {
        return totalObligated;
    }

    public void setTotalObligated(BigDecimal totalObligated) {
        this.totalObligated = requireNonNullElse(totalObligated, BigDecimal.ZERO);
    }

    public BigDecimal getTotalReimbursed() {
        return totalReimbursed;
    }

    public void setTotalReimbursed(BigDecimal totalReimbursed) {
        this.totalReimbursed = requireNonNullElse(totalReimbursed, BigDecimal.ZERO);
    }

    public BigDecimal getTotalProjected() {
        return totalProjected;
    }

    public void setTotalProjected(BigDecimal totalProjected) {
        this.totalProjected = requireNonNullElse(totalProjected, BigDecimal.ZERO);
    }

    public List<ObligationDto> getObligationDtos() {
        return obligationDtos;
    }

    public void setObligationDtos(List<ObligationDto> obligationDtos) {
        this.obligationDtos = obligationDtos;
    }

    public List<ObjectClassDto> getObjectClassDtos() {
        return objectClassDtos;
    }

    public void setObjectClassDtos(List<ObjectClassDto> objectClassDtos) {
        this.objectClassDtos = objectClassDtos;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getBranchTitle() {
        return branchTitle;
    }

    public void setBranchTitle(String branchTitle) {
        this.branchTitle = branchTitle;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getBudgetFiscalYear() {
        return budgetFiscalYear;
    }

    public void setBudgetFiscalYear(String budgetFiscalYear) {
        this.budgetFiscalYear = budgetFiscalYear;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(String activityNumber) {
        this.activityNumber = activityNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBranchTitleAndCode() {
        return branchTitle == null ? "" : branchTitle.concat(" (").concat(branchCode).concat(")");
    }

    public String getGroupTitleAndCode() {
        return groupTitle == null ? "" : groupTitle.concat(" (").concat(groupCode).concat(")");
    }

    public String getTitleAndCode() {
        return title.concat(" (").concat(activityNumber).concat(")");
    }
}