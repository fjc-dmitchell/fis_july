package gov.fjc.fis.entity;

import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import static gov.fjc.fis.FisUtilities.getLoadedByString;

@JmixEntity
@Table(name = "FIS_DOCUMENT")
@Entity(name = "fis_Document")
public class Document {
    @Column(name = "ID", nullable = false)
    @Id
    private Integer id;

    @Column(name = "FUND", nullable = false, length = 6)
    @NotNull
    private String fund;

    @Column(name = "BBFY", nullable = false, length = 4)
    @NotNull
    private String bbfy;

    @Column(name = "EBFY", length = 4)
    private String ebfy;

    @Column(name = "BUDGETORG", nullable = false, length = 7)
    @NotNull
    private String budgetorg;

    @Column(name = "COSTORG", nullable = false, length = 7)
    @NotNull
    private String costorg;

    @Column(name = "DOCTYPE", nullable = false, length = 5)
    @NotNull
    private String doctype;

    @Column(name = "DOCNUMBER", nullable = false, length = 50)
    @NotNull
    private String docnumber;

    @Column(name = "DOCDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date docdate;

    @Column(name = "CREATEDATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date createdate;

    @Column(name = "TITLE", length = 70)
    private String title;

    @Column(name = "BOC", nullable = false, length = 7)
    @NotNull
    private String boc;

    @Column(name = "PROJECT", length = 4)
    private String project;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal amount;

    @Column(name = "LINENUMBER", nullable = false)
    @NotNull
    private Integer linenumber;

    @Column(name = "TAXID", length = 9)
    private String taxid;

    @Column(name = "TAXID_TYPE", length = 1)
    private String taxidType;

    @Column(name = "ADDRESS_CODE", length = 15)
    private String addressCode;

    @Column(name = "VENDOR_CODE", length = 10)
    private String vendorCode;

    @Column(name = "VENDOR_NAME", length = 70)
    private String vendorName;

    @Column(name = "TRAVEL_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date travelStartDate;

    @Column(name = "TRAVEL_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date travelEndDate;

    @Column(name = "EXPENDED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal expendedAmount;

    @Column(name = "CLOSED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal closedAmount;

    @Column(name = "CLOSED_DATE")
    @Temporal(TemporalType.DATE)
    private Date closedDate;

    @Column(name = "MODUSER", nullable = false, length = 45)
    @NotNull
    private String moduser;

    @Column(name = "MOC", length = 2)
    private String moc;

    @Column(name = "FJC", length = 20)
    private String fjc;

    @Column(name = "ORDERED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal orderedAmount;

    @Column(name = "OUTSTANDING_AMOUNT", precision = 19, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "PREPAID_AMOUNT", precision = 19, scale = 2)
    private BigDecimal prepaidAmount;

    @Column(name = "REFUNDED_AMOUNT", precision = 19, scale = 2)
    private BigDecimal refundedAmount;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @DependsOnProperties({"createdBy", "createdDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getLoadedByString(createdBy, createdDate);
    }

    @DependsOnProperties({"bbfy", "ebfy"})
    @JmixProperty
    public String getBfyString() {
        return ebfy == null ? bbfy : bbfy.concat(" / ").concat(ebfy);
    }

    public String getBudgetorg() {
        return budgetorg;
    }

    public void setBudgetorg(String budgetorg) {
        this.budgetorg = budgetorg;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public BigDecimal getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(BigDecimal prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public BigDecimal getOrderedAmount() {
        return orderedAmount;
    }

    public void setOrderedAmount(BigDecimal orderedAmount) {
        this.orderedAmount = orderedAmount;
    }

    public String getFjc() {
        return fjc;
    }

    public void setFjc(String fjc) {
        this.fjc = fjc;
    }

    public String getMoc() {
        return moc;
    }

    public void setMoc(String moc) {
        this.moc = moc;
    }

    public String getModuser() {
        return moduser;
    }

    public void setModuser(String moduser) {
        this.moduser = moduser;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public BigDecimal getClosedAmount() {
        return closedAmount;
    }

    public void setClosedAmount(BigDecimal closedAmount) {
        this.closedAmount = closedAmount;
    }

    public BigDecimal getExpendedAmount() {
        return expendedAmount;
    }

    public void setExpendedAmount(BigDecimal expendedAmount) {
        this.expendedAmount = expendedAmount;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getTaxidType() {
        return taxidType;
    }

    public void setTaxidType(String taxidType) {
        this.taxidType = taxidType;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid(String taxid) {
        this.taxid = taxid;
    }

    public Integer getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(Integer linenumber) {
        this.linenumber = linenumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBoc() {
        return boc;
    }

    public void setBoc(String boc) {
        this.boc = boc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getDocdate() {
        return docdate;
    }

    public void setDocdate(Date docdate) {
        this.docdate = docdate;
    }

    public String getDocnumber() {
        return docnumber;
    }

    public void setDocnumber(String docnumber) {
        this.docnumber = docnumber;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getCostorg() {
        return costorg;
    }

    public void setCostorg(String costorg) {
        this.costorg = costorg;
    }

    public String getEbfy() {
        return ebfy;
    }

    public void setEbfy(String ebfy) {
        this.ebfy = ebfy;
    }

    public String getBbfy() {
        return bbfy;
    }

    public void setBbfy(String bbfy) {
        this.bbfy = bbfy;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"fund", "docnumber"})
    public String getInstanceName(MetadataTools metadataTools) {
        return String.format("%s-%s",
                metadataTools.format(fund),
                metadataTools.format(docnumber));
    }
}