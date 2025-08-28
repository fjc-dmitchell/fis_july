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

    @Column(name = "DOCUMENT_FUND", nullable = false, length = 6)
    @NotNull
    private String documentFund;

    @Column(name = "DOCUMENT_BBFY", nullable = false, length = 4)
    @NotNull
    private String documentBbfy;

    @Column(name = "DOCUMENT_EBFY", length = 4)
    private String documentEbfy;

    @Column(name = "DOCUMENT_BUDGETORG", nullable = false, length = 7)
    @NotNull
    private String documentBudgetorg;

    @Column(name = "DOCUMENT_COSTORG", nullable = false, length = 7)
    @NotNull
    private String documentCostorg;

    @Column(name = "DOCUMENT_DOCTYPE", nullable = false, length = 5)
    @NotNull
    private String documentDoctype;

    @Column(name = "DOCUMENT_DOCNUMBER", nullable = false, length = 50)
    @NotNull
    private String documentDocnumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_DOCDATE", nullable = false)
    @NotNull
    private Date documentDocdate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_CREATEDATE", nullable = false)
    @NotNull
    private Date documentCreatedate;

    @Column(name = "DOCUMENT_TITLE", length = 70)
    private String documentTitle;

    @Column(name = "DOCUMENT_BOC", nullable = false, length = 7)
    @NotNull
    private String documentBoc;

    @Column(name = "DOCUMENT_PROJECT", length = 4)
    private String documentProject;

    @Column(name = "DOCUMENT_AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal documentAmount;

    @Column(name = "DOCUMENT_LINENUMBER", nullable = false)
    @NotNull
    private Integer documentLinenumber;

    @Column(name = "DOCUMENT_TAXID", length = 9)
    private String documentTaxid;

    @Column(name = "DOCUMENT_TAXID_TYPE", length = 1)
    private String documentTaxidType;

    @Column(name = "DOCUMENT_ADDRESS_CODE", length = 15)
    private String documentAddressCode;

    @Column(name = "DOCUMENT_VENDOR_CODE", length = 10)
    private String documentVendorCode;

    @Column(name = "DOCUMENT_VENDOR_NAME", length = 70)
    private String documentVendorName;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_START_DATE")
    private Date documentStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_END_DATE")
    private Date documentEndDate;

    @Column(name = "DOCUMENT_EXPENDED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal documentExpendedAmount;

    @Column(name = "DOCUMENT_CLOSED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal documentClosedAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "DOCUMENT_CLOSED_DATE")
    private Date documentClosedDate;

    @Column(name = "DOCUMENT_MODUSER", nullable = false, length = 45)
    @NotNull
    private String documentModuser;

    @Column(name = "DOCUMENT_MOC", length = 2)
    private String documentMoc;

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
    @Column(name = "OBLIGATION_START_DATE")
    private Date obligationStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "OBLIGATION_END_DATE")
    private Date obligationEndDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OBLIGATION_MODIFIED_DATE")
    private Date obligationModifiedDate;

    @Column(name = "OBLIGATION_ACTIVITY_NUMBER", length = 4)
    private String obligationActivityNumber;

    @Column(name = "OBLIGATION_BOC", length = 4)
    private String obligationObjectClass;

    @Column(name = "OBLIGATION_DIVISION_CODE", length = 2)
    private String obligationDivisionCode;

    @Column(name = "OBLIGATION_MOC", length = 2)
    private String obligationCategory;

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

    public String getObligationCategory() {
        return obligationCategory;
    }

    public String getObligationDivisionCode() {
        return obligationDivisionCode;
    }

    public String getObligationObjectClass() {
        return obligationObjectClass;
    }

    public String getObligationActivityNumber() {
        return obligationActivityNumber;
    }

    public Date getObligationModifiedDate() {
        return obligationModifiedDate;
    }

    public Date getObligationEndDate() {
        return obligationEndDate;
    }

    public Date getObligationStartDate() {
        return obligationStartDate;
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

    public Date getDocumentEndDate() {
        return documentEndDate;
    }

    public Date getDocumentStartDate() {
        return documentStartDate;
    }

    public String getDocumentFjc() {
        return documentFjc;
    }

    public String getDocumentMoc() {
        return documentMoc;
    }

    public String getDocumentModuser() {
        return documentModuser;
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

    public String getDocumentTaxidType() {
        return documentTaxidType;
    }

    public String getDocumentTaxid() {
        return documentTaxid;
    }

    public Integer getDocumentLinenumber() {
        return documentLinenumber;
    }

    public BigDecimal getDocumentAmount() {
        return documentAmount;
    }

    public String getDocumentProject() {
        return documentProject;
    }

    public String getDocumentBoc() {
        return documentBoc;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Date getDocumentCreatedate() {
        return documentCreatedate;
    }

    public Date getDocumentDocdate() {
        return documentDocdate;
    }

    public String getDocumentDocnumber() {
        return documentDocnumber;
    }

    public String getDocumentDoctype() {
        return documentDoctype;
    }

    public String getDocumentCostorg() {
        return documentCostorg;
    }

    public String getDocumentBudgetorg() {
        return documentBudgetorg;
    }

    public String getDocumentEbfy() {
        return documentEbfy;
    }

    public String getDocumentBbfy() {
        return documentBbfy;
    }

    public String getDocumentFund() {
        return documentFund;
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