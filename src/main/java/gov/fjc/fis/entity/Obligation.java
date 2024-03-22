package gov.fjc.fis.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import static gov.fjc.fis.fisUtilities.*;
import static java.util.Objects.requireNonNullElse;

@JmixEntity
@Table(name = "FIS_OBLIGATION", indexes = {
        @Index(name = "IDX_FIS_OBLIGATION_ACTIVITY", columnList = "ACTIVITY_ID"),
        @Index(name = "IDX_FIS_OBLIGATION_OBJECT_CLASS", columnList = "OBJECT_CLASS_ID"),
        @Index(name = "IDX_FIS_OBLIGATION_RESPONSIBLE_DIVISION", columnList = "RESPONSIBLE_DIVISION_ID"),
        @Index(name = "IDX_FIS_OBLIGATION", columnList = "ACTIVITY_ID, STATUS")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_FIS_OBLIGATION_UNQ", columnNames = {"ACTIVITY_ID", "OBJECT_CLASS_ID", "DOCID"})
})
@Entity(name = "fis_Obligation")
public class Obligation {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Activity activity;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "OBJECT_CLASS_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ObjectClass objectClass;

    @Column(name = "DOCID", nullable = false, length = 20)
    @NotNull
    private String documentNumber;

    @Column(name = "DOCUMENT_TYPE", length = 5)
    private String documentType;

    @Column(name = "LINE_NUMBER", nullable = false)
    @NotNull
    private Integer lineNumber = 1;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "DOCUMENT_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Obligation document date is required")
    private Date documentDate;

    @Column(name = "PROCESS_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Obligation process date is required")
    private Date processDate;

    @Column(name = "AO_SEND", nullable = false)
    @NotNull
    private Boolean aoSend = false;

    @Column(name = "AO_SYNC_DATE")
    @Temporal(TemporalType.DATE)
    private Date aoSyncDate;

    @Column(name = "VENDOR")
    private String vendor;

    @Column(name = "STATUS", nullable = false)
    @NotNull
    private Boolean status = false;

    @Column(name = "EIN", length = 10)
    private String ein;

    @Column(name = "EIN2", length = 15)
    private String ein2;

    @Column(name = "TRAVEL_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date travelStartDate;

    @Column(name = "TRAVEL_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date travelEndDate;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "RESPONSIBLE_DIVISION_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Division responsibleDivision;

    @Column(name = "RECONCILED", nullable = false)
    @NotNull
    private Boolean reconciled = false;

    @Column(name = "BPO", nullable = false)
    @NotNull
    private Boolean blanketPurchaseOrder = false;

    @Column(name = "UPDATED", nullable = false)
    @NotNull
    private Boolean updated = false;

    @Column(name = "BUDGET_ORG", length = 7)
    private String budgetOrg;

    @Column(name = "MEMO")
    @Lob
    private String memo;

    @OrderBy("invoiceDate, invoiceNumber")
    @Composition
    @OneToMany(mappedBy = "obligation")
    private List<Invoice> invoices;

    @OrderBy("fcnDate")
    @Composition
    @OneToMany(mappedBy = "obligation")
    private List<FundControlNotice> fundControlNotices;

    @Column(name = "VERSION", nullable = false, columnDefinition = "INT DEFAULT 1")
    @Version
    private Integer version;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    @DependsOnProperties({"aoSyncDate"})
    @JmixProperty
    public String getAoSyncString() {
        return getAoSyncDateString(aoSyncDate);
    }

    public Boolean getBlanketPurchaseOrder() {
        return blanketPurchaseOrder;
    }

    public void setBlanketPurchaseOrder(Boolean blanketPurchaseOrder) {
        this.blanketPurchaseOrder = blanketPurchaseOrder;
    }

    public Date getAoSyncDate() {
        return aoSyncDate;
    }

    public void setAoSyncDate(Date aoSyncDate) {
        this.aoSyncDate = aoSyncDate;
    }

    public List<FundControlNotice> getFundControlNotices() {
        return fundControlNotices;
    }

    public void setFundControlNotices(List<FundControlNotice> fundControlNotices) {
        this.fundControlNotices = fundControlNotices;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    @DependsOnProperties({"documentNumber", "objectClass", "vendor"})
    @JmixProperty
    public String getSuggestion() {
        return objectClass == null ?
                String.format("%s - %s", documentNumber, vendor) :
                String.format("%s (%s) - %s", documentNumber, objectClass.getBudgetObjectClass(), vendor);
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    @DependsOnProperties({"status"})
    @JmixProperty
    public String getStatusString() {
        return status ? "Open" : "Closed";
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getBudgetOrg() {
        return budgetOrg;
    }

    public void setBudgetOrg(String budgetOrg) {
        this.budgetOrg = budgetOrg;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public Boolean getReconciled() {
        return reconciled;
    }

    public void setReconciled(Boolean reconciled) {
        this.reconciled = reconciled;
    }

    public Division getResponsibleDivision() {
        return responsibleDivision;
    }

    public void setResponsibleDivision(Division responsibleDivision) {
        this.responsibleDivision = responsibleDivision;
    }

    public Date getTravelEndDate() {
        return travelEndDate;
    }

    public void setTravelEndDate(Date travelEndDate) {
        this.travelEndDate = travelEndDate;
    }

    public Date getTravelStartDate() {
        return travelStartDate;
    }

    public void setTravelStartDate(Date travelStartDate) {
        this.travelStartDate = travelStartDate;
    }

    public String getEin2() {
        return ein2;
    }

    public void setEin2(String ein2) {
        this.ein2 = ein2;
    }

    public String getEin() {
        return ein;
    }

    public void setEin(String ein) {
        this.ein = toUpperNullAllowed(ein);
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Boolean getAoSend() {
        return aoSend;
    }

    public void setAoSend(Boolean aoSend) {
        this.aoSend = aoSend;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = requireNonNullElse(amount, BigDecimal.ZERO);
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = toUpperNullAllowed(documentType);
    }

    public ObjectClass getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClass objectClass) {
        this.objectClass = objectClass;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = toUpperNullAllowed(documentNumber);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"documentNumber"})
    public String getInstanceName(MetadataTools metadataTools) {
        return metadataTools.format(documentNumber);
    }
}