package gov.fjc.fis.entity;

import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

import static gov.fjc.fis.FisUtilities.nonZero;
import static java.util.Objects.requireNonNullElse;

@JmixEntity
@Table(name = "FIS_DOCUMENT_AUDIT")
@Entity(name = "fis_DocumentAudit")
public class DocumentAudit {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "PROCESS_STATUS", nullable = false)
    private String processStatus;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "PROCESS_DATE", nullable = false)
    private Date processDate;

    @Column(name = "DOCUMENT_FUND_CODE", nullable = false, length = 6)
    @NotNull
    private String documentFundCode;

    @Column(name = "DOCUMENT_BBFY", nullable = false, length = 4)
    @NotNull
    private String documentBbfy;

    @Column(name = "DOCUMENT_EBFY", length = 4)
    private String documentEbfy;

    @Column(name = "DOCUMENT_BUDGET_ORG", nullable = false, length = 7)
    @NotNull
    private String documentBudgetOrg;

    @Column(name = "DOCUMENT_COST_ORG", nullable = false, length = 7)
    @NotNull
    private String documentCostOrg;

    @Column(name = "DOCUMENT_DOCUMENT_TYPE", nullable = false, length = 5)
    @NotNull
    private String documentDocumentType;

    @Column(name = "DOCUMENT_DOCUMENT_NUMBER", nullable = false, length = 50)
    @NotNull
    private String documentDocumentNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_DOCUMENT_DATE", nullable = false)
    @NotNull
    private Date documentDocumentDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_DOCUMENT_CREATION_DATE", nullable = false)
    @NotNull
    private Date documentDocumentCreationDate;

    @Column(name = "DOCUMENT_TITLE", length = 70)
    private String documentTitle;

    @Column(name = "DOCUMENT_BOC", nullable = false, length = 7)
    @NotNull
    private String documentBudgetObjectClass;

    @Column(name = "DOCUMENT_PROJECT", length = 4)
    private String documentProject;

    @Column(name = "DOCUMENT_AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal documentAmount;

    @Column(name = "DOCUMENT_LINE_NUMBER", nullable = false)
    @NotNull
    private Integer documentLineNumber;

    @Column(name = "DOCUMENT_TAX_ID", length = 9)
    private String documentTaxId;

    @Column(name = "DOCUMENT_TAX_ID_TYPE", length = 1)
    private String documentTaxIdType;

    @Column(name = "DOCUMENT_ADDRESS_CODE", length = 15)
    private String documentAddressCode;

    @Column(name = "DOCUMENT_VENDOR_CODE", length = 10)
    private String documentVendorCode;

    @Column(name = "DOCUMENT_VENDOR_NAME", length = 70)
    private String documentVendorName;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_TRAVEL_START_DATE")
    private Date documentTravelStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_TRAVEL_END_DATE")
    private Date documentTravelEndDate;

    @Column(name = "DOCUMENT_EXPENDED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal documentExpendedAmount;

    @Column(name = "DOCUMENT_CLOSED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal documentClosedAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_CLOSED_DATE")
    private Date documentClosedDate;

    @Column(name = "DOCUMENT_LAST_MODIFIED_BY", nullable = false, length = 45)
    @NotNull
    private String documentLastModifiedBy;

    @Column(name = "DOCUMENT_MOC", length = 2)
    private String documentMasterObjectClass;

    @Column(name = "DOCUMENT_FJC", length = 20)
    private String documentFjc;

    @Column(name = "OBLIGATION_DOCID", length = 20)
    private String obligationDocumentNumber;

    @Column(name = "OBLIGATION_DOCUMENT_TYPE", length = 5)
    private String obligationDocumentType;

    @Column(name = "OBLIGATION_AMOUNT", precision = 19, scale = 2)
    private BigDecimal obligationAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "OBLIGATION_DOCUMENT_DATE")
    private Date obligationDocumentDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "OBLIGATION_PROCESS_DATE")
    private Date obligationProcessDate;

    @Column(name = "OBLIGATION_VENDOR")
    private String obligationVendor;

    @Column(name = "OBLIGATION_STATUS")
    private Boolean obligationStatus;

    @Column(name = "OBLIGATION_EIN", length = 10)
    private String obligationEin;

    @Temporal(TemporalType.DATE)
    @Column(name = "OBLIGATION_TRAVEL_START_DATE")
    private Date obligationTravelStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "OBLIGATION_TRAVEL_END_DATE")
    private Date obligationTravelEndDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OBLIGATION_MODIFIED_DATE")
    private Date obligationModifiedDate;

    @Column(name = "OBLIGATION_ACTIVITY_NUMBER", length = 4)
    private String obligationActivityNumber;

    @Column(name = "OBLIGATION_BOC", length = 4)
    private String obligationBudgetObjectClass;

    @Column(name = "OBLIGATION_DIVISION_CODE", length = 2)
    private String obligationDivisionCode;

    @Column(name = "OBLIGATION_MOC", length = 2)
    private String obligationMasterObjectClass;

    @Column(name = "OBLIGATION_ADDRESS_CODE", length = 15)
    private String obligationAddressCode;

    @Column(name = "OBLIGATION_VENDOR_CODE", length = 10)
    private String obligationVendorCode;

    @Column(name = "CURRENT_ACTIVITY_NUMBER", length = 4)
    private String currentActivityNumber;

    @Column(name = "CURRENT_PROJECTION_BOC", length = 4)
    private String currentProjectionBoc;

    @Column(name = "CURRENT_PROJECTION_AMOUNT_BEFORE", precision = 19, scale = 2)
    private BigDecimal currentProjectionAmountBefore;

    @Column(name = "CURRENT_PROJECTION_AMOUNT_AFTER", precision = 19, scale = 2)
    private BigDecimal currentProjectionAmountAfter;

    @Column(name = "PREVIOUS_ACTIVITY_NUMBER", length = 4)
    private String previousActivityNumber;

    @Column(name = "PREVIOUS_PROJECTION_BOC", length = 4)
    private String previousProjectionBoc;

    @Column(name = "PREVIOUS_PROJECTION_AMOUNT_BEFORE", precision = 19, scale = 2)
    private BigDecimal previousProjectionAmountBefore;

    @Column(name = "PREVIOUS_PROJECTION_AMOUNT_AFTER", precision = 19, scale = 2)
    private BigDecimal previousProjectionAmountAfter;

    @Column(name = "LOGGED_CHANGES")
    private String loggedChanges;

    public String getObligationBudgetObjectClass() {
        return obligationBudgetObjectClass;
    }

    public void setObligationBudgetObjectClass(String obligationBudgetObjectClass) {
        this.obligationBudgetObjectClass = obligationBudgetObjectClass;
    }

    public void setObligationMasterObjectClass(String obligationMasterObjectClass) {
        this.obligationMasterObjectClass = obligationMasterObjectClass;
    }

    public Date getObligationTravelEndDate() {
        return obligationTravelEndDate;
    }

    public void setObligationTravelEndDate(Date obligationTravelEndDate) {
        this.obligationTravelEndDate = obligationTravelEndDate;
    }

    public Date getObligationTravelStartDate() {
        return obligationTravelStartDate;
    }

    public void setObligationTravelStartDate(Date obligationTravelStartDate) {
        this.obligationTravelStartDate = obligationTravelStartDate;
    }

    public void setLoggedChanges(String loggedChanges) {
        this.loggedChanges = loggedChanges;
    }

    public void setPreviousProjectionAmountAfter(BigDecimal previousProjectionAmountAfter) {
        this.previousProjectionAmountAfter = previousProjectionAmountAfter;
    }

    public void setPreviousProjectionAmountBefore(BigDecimal previousProjectionAmountBefore) {
        this.previousProjectionAmountBefore = previousProjectionAmountBefore;
    }

    public void setPreviousProjectionBoc(String previousProjectionBoc) {
        this.previousProjectionBoc = previousProjectionBoc;
    }

    public void setPreviousActivityNumber(String previousActivityNumber) {
        this.previousActivityNumber = previousActivityNumber;
    }

    public void setCurrentProjectionAmountAfter(BigDecimal currentProjectionAmountAfter) {
        this.currentProjectionAmountAfter = currentProjectionAmountAfter;
    }

    public void setCurrentProjectionAmountBefore(BigDecimal currentProjectionAmountBefore) {
        this.currentProjectionAmountBefore = currentProjectionAmountBefore;
    }

    public void setCurrentProjectionBoc(String currentProjectionBoc) {
        this.currentProjectionBoc = currentProjectionBoc;
    }

    public void setCurrentActivityNumber(String currentActivityNumber) {
        this.currentActivityNumber = currentActivityNumber;
    }

    public void setObligationVendorCode(String obligationVendorCode) {
        this.obligationVendorCode = obligationVendorCode;
    }

    public void setObligationAddressCode(String obligationAddressCode) {
        this.obligationAddressCode = obligationAddressCode;
    }

    public void setObligationCategory(String obligationMasterObjectClass) {
        this.obligationMasterObjectClass = obligationMasterObjectClass;
    }

    public void setObligationDivisionCode(String obligationDivisionCode) {
        this.obligationDivisionCode = obligationDivisionCode;
    }

    public void setObligationActivityNumber(String obligationActivityNumber) {
        this.obligationActivityNumber = obligationActivityNumber;
    }

    public void setObligationModifiedDate(Date obligationModifiedDate) {
        this.obligationModifiedDate = obligationModifiedDate;
    }

    public void setObligationEin(String obligationEin) {
        this.obligationEin = obligationEin;
    }

    public void setObligationStatus(Boolean obligationStatus) {
        this.obligationStatus = obligationStatus;
    }

    public void setObligationVendor(String obligationVendor) {
        this.obligationVendor = obligationVendor;
    }

    public void setObligationProcessDate(Date obligationProcessDate) {
        this.obligationProcessDate = obligationProcessDate;
    }

    public void setObligationDocumentDate(Date obligationDocumentDate) {
        this.obligationDocumentDate = obligationDocumentDate;
    }

    public void setObligationAmount(BigDecimal obligationAmount) {
        this.obligationAmount = obligationAmount;
    }

    public void setObligationDocumentNumber(String obligationDocumentNumber) {
        this.obligationDocumentNumber = obligationDocumentNumber;
    }

    public void setDocumentFjc(String documentFjc) {
        this.documentFjc = documentFjc;
    }

    public void setDocumentMasterObjectClass(String documentMasterObjectClass) {
        this.documentMasterObjectClass = documentMasterObjectClass;
    }

    public void setDocumentLastModifiedBy(String documentLastModifiedBy) {
        this.documentLastModifiedBy = documentLastModifiedBy;
    }

    public void setDocumentClosedDate(Date documentClosedDate) {
        this.documentClosedDate = documentClosedDate;
    }

    public void setDocumentClosedAmount(BigDecimal documentClosedAmount) {
        this.documentClosedAmount = documentClosedAmount;
    }

    public void setDocumentExpendedAmount(BigDecimal documentExpendedAmount) {
        this.documentExpendedAmount = documentExpendedAmount;
    }

    public void setDocumentTravelEndDate(Date documentTravelEndDate) {
        this.documentTravelEndDate = documentTravelEndDate;
    }

    public void setDocumentTravelStartDate(Date documentTravelStartDate) {
        this.documentTravelStartDate = documentTravelStartDate;
    }

    public void setDocumentVendorName(String documentVendorName) {
        this.documentVendorName = documentVendorName;
    }

    public void setDocumentVendorCode(String documentVendorCode) {
        this.documentVendorCode = documentVendorCode;
    }

    public void setDocumentAddressCode(String documentAddressCode) {
        this.documentAddressCode = documentAddressCode;
    }

    public void setDocumentTaxIdType(String documentTaxIdType) {
        this.documentTaxIdType = documentTaxIdType;
    }

    public void setDocumentTaxId(String documentTaxId) {
        this.documentTaxId = documentTaxId;
    }

    public void setDocumentLineNumber(Integer documentLineNumber) {
        this.documentLineNumber = documentLineNumber;
    }

    public void setDocumentAmount(BigDecimal documentAmount) {
        this.documentAmount = documentAmount;
    }

    public void setDocumentProject(String documentProject) {
        this.documentProject = documentProject;
    }

    public void setDocumentBudgetObjectClass(String documentBudgetObjectClass) {
        this.documentBudgetObjectClass = documentBudgetObjectClass;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public void setDocumentDocumentCreationDate(Date documentDocumentCreationDate) {
        this.documentDocumentCreationDate = documentDocumentCreationDate;
    }

    public void setDocumentDocumentDate(Date documentDocumentDate) {
        this.documentDocumentDate = documentDocumentDate;
    }

    public void setDocumentDocumentNumber(String documentDocumentNumber) {
        this.documentDocumentNumber = documentDocumentNumber;
    }

    public void setDocumentDocumentType(String documentDocumentType) {
        this.documentDocumentType = documentDocumentType;
    }

    public void setDocumentCostOrg(String documentCostOrg) {
        this.documentCostOrg = documentCostOrg;
    }

    public void setDocumentBudgetOrg(String documentBudgetOrg) {
        this.documentBudgetOrg = documentBudgetOrg;
    }

    public void setDocumentEbfy(String documentEbfy) {
        this.documentEbfy = documentEbfy;
    }

    public void setDocumentBbfy(String documentBbfy) {
        this.documentBbfy = documentBbfy;
    }

    public void setDocumentFundCode(String documentFundCode) {
        this.documentFundCode = documentFundCode;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public Integer getDocumentLineNumber() {
        return documentLineNumber;
    }

    public Date getDocumentDocumentCreationDate() {
        return documentDocumentCreationDate;
    }

    public String getDocumentTaxIdType() {
        return documentTaxIdType;
    }

    public String getDocumentTaxId() {
        return documentTaxId;
    }

    public Date getDocumentTravelStartDate() {
        return documentTravelStartDate;
    }

    public Date getDocumentTravelEndDate() {
        return documentTravelEndDate;
    }

    public String getDocumentLastModifiedBy() {
        return documentLastModifiedBy;
    }

    public Date getDocumentDocumentDate() {
        return documentDocumentDate;
    }

    public String getDocumentDocumentNumber() {
        return documentDocumentNumber;
    }

    public String getDocumentDocumentType() {
        return documentDocumentType;
    }

    public String getDocumentCostOrg() {
        return documentCostOrg;
    }

    public String getDocumentBudgetOrg() {
        return documentBudgetOrg;
    }

    public String getDocumentFundCode() {
        return documentFundCode;
    }

    @DependsOnProperties({"id"})
    @JmixProperty
    public Integer getProcessId() {
        return getId();
    }

    @DependsOnProperties({"documentAmount", "obligationAmount"})
    @JmixProperty
    public BigDecimal getFcnAmount() {
        var value = documentAmount.subtract(requireNonNullElse(obligationAmount, BigDecimal.ZERO));
        return nonZero(value) ? value : null;
    }

    public String getObligationVendorCode() {
        return obligationVendorCode;
    }

    public String getLoggedChanges() {
        return loggedChanges;
    }

    public BigDecimal getPreviousProjectionAmountAfter() {
        return previousProjectionAmountAfter;
    }

    public BigDecimal getPreviousProjectionAmountBefore() {
        return previousProjectionAmountBefore;
    }

    public String getPreviousProjectionBoc() {
        return previousProjectionBoc;
    }

    public String getPreviousActivityNumber() {
        return previousActivityNumber;
    }

    public BigDecimal getCurrentProjectionAmountAfter() {
        return currentProjectionAmountAfter;
    }

    public BigDecimal getCurrentProjectionAmountBefore() {
        return currentProjectionAmountBefore;
    }

    public String getCurrentProjectionBoc() {
        return currentProjectionBoc;
    }

    public String getCurrentActivityNumber() {
        return currentActivityNumber;
    }

    public String getObligationAddressCode() {
        return obligationAddressCode;
    }

    public String getObligationMasterObjectClass() {
        return obligationMasterObjectClass;
    }

    public String getObligationDivisionCode() {
        return obligationDivisionCode;
    }

    public String getObligationActivityNumber() {
        return obligationActivityNumber;
    }

    public Date getObligationModifiedDate() {
        return obligationModifiedDate;
    }

    public String getObligationEin() {
        return obligationEin;
    }

    public Boolean getObligationStatus() {
        return obligationStatus;
    }

    public String getObligationVendor() {
        return obligationVendor;
    }

    public Date getObligationProcessDate() {
        return obligationProcessDate;
    }

    public Date getObligationDocumentDate() {
        return obligationDocumentDate;
    }

    public BigDecimal getObligationAmount() {
        return obligationAmount;
    }

    public String getObligationDocumentType() {
        return obligationDocumentType;
    }

    public void setObligationDocumentType(String obligationDocumentType) {
        this.obligationDocumentType = obligationDocumentType;
    }

    public String getObligationDocumentNumber() {
        return obligationDocumentNumber;
    }

    public String getDocumentVendorName() {
        return documentVendorName;
    }

    public String getDocumentFjc() {
        return documentFjc;
    }

    public String getDocumentMasterObjectClass() {
        return documentMasterObjectClass;
    }

    public Date getDocumentClosedDate() {
        return documentClosedDate;
    }

    public BigDecimal getDocumentClosedAmount() {
        return documentClosedAmount;
    }

    public BigDecimal getDocumentExpendedAmount() {
        return documentExpendedAmount;
    }

    public String getDocumentVendorCode() {
        return documentVendorCode;
    }

    public String getDocumentAddressCode() {
        return documentAddressCode;
    }

    public BigDecimal getDocumentAmount() {
        return documentAmount;
    }

    public String getDocumentProject() {
        return documentProject;
    }

    public String getDocumentBudgetObjectClass() {
        return documentBudgetObjectClass;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public String getDocumentEbfy() {
        return documentEbfy;
    }

    public String getDocumentBbfy() {
        return documentBbfy;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}