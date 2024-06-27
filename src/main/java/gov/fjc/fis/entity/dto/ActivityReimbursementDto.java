package gov.fjc.fis.entity.dto;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNullElse;

@JmixEntity(name = "fis_ActivityReimbursementDto")
public class ActivityReimbursementDto {
    @JmixGeneratedValue
    @JmixId
    private Integer id;

    private Integer activityId;

    private String activityNumber;

    private String activityTitle;

    private Integer fundId;

    private String fundCode;

    private Integer appropriationId;

    private String budgetFiscalYear;

    private Integer divisionId;

    private String divisionCode;

    private Integer categoryId;

    private String masterObjectClass;

    private Integer objectClassId;

    private String budgetObjectClass;

    private String documentNumber;

    private String source;

    private BigDecimal amount = BigDecimal.ZERO;

    private BigDecimal currentOneYearAmount = BigDecimal.ZERO;

    private BigDecimal currentTwoYearAmount = BigDecimal.ZERO;

    private BigDecimal priorTwoYearAmount = BigDecimal.ZERO;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(Integer objectClassId) {
        this.objectClassId = objectClassId;
    }

    public Integer getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Integer divisionId) {
        this.divisionId = divisionId;
    }

    public Integer getAppropriationId() {
        return appropriationId;
    }

    public void setAppropriationId(Integer appropriationId) {
        this.appropriationId = appropriationId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getFundId() {
        return fundId;
    }

    public void setFundId(Integer fundId) {
        this.fundId = fundId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = requireNonNullElse(amount, BigDecimal.ZERO);
    }

    public BigDecimal getPriorTwoYearAmount() {
        return priorTwoYearAmount;
    }

    public void setPriorTwoYearAmount(BigDecimal priorTwoYearAmount) {
        this.priorTwoYearAmount = requireNonNullElse(priorTwoYearAmount, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentTwoYearAmount() {
        return currentTwoYearAmount;
    }

    public void setCurrentTwoYearAmount(BigDecimal currentTwoYearAmount) {
        this.currentTwoYearAmount = requireNonNullElse(currentTwoYearAmount, BigDecimal.ZERO);
    }

    public BigDecimal getCurrentOneYearAmount() {
        return currentOneYearAmount;
    }

    public void setCurrentOneYearAmount(BigDecimal currentOneYearAmount) {
        this.currentOneYearAmount = requireNonNullElse(currentOneYearAmount, BigDecimal.ZERO);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getBudgetObjectClass() {
        return budgetObjectClass;
    }

    public void setBudgetObjectClass(String budgetObjectClass) {
        this.budgetObjectClass = budgetObjectClass;
    }

    public String getMasterObjectClass() {
        return masterObjectClass;
    }

    public void setMasterObjectClass(String masterObjectClass) {
        this.masterObjectClass = masterObjectClass;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public String getBudgetFiscalYear() {
        return budgetFiscalYear;
    }

    public void setBudgetFiscalYear(String budgetFiscalYear) {
        this.budgetFiscalYear = budgetFiscalYear;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
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
}