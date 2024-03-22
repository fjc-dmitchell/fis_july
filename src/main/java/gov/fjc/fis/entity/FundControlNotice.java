package gov.fjc.fis.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import io.jmix.core.metamodel.datatype.DatatypeFormatter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import static gov.fjc.fis.FisUtilities.*;

@JmixEntity
@Table(name = "FIS_FCN", indexes = {
        @Index(name = "IDX_FIS_FCN_OBLIGATION", columnList = "OBLIGATION_ID")
})
@Entity(name = "fis_FundControlNotice")
public class FundControlNotice {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "OBLIGATION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Obligation obligation;

    @Column(name = "FCN_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date fcnDate;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "TRAVEL_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date travelStartDate;

    @Column(name = "TRAVEL_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date travelEndDate;

    @Column(name = "BUDGET_ORG", length = 7)
    private String budgetOrg;

    @Column(name = "EIN", length = 10)
    private String ein;

    @Column(name = "MEMO")
    @Lob
    private String memo;

    @Column(name = "AO_SEND", nullable = false)
    @NotNull
    private Boolean aoSend = false;

    @Column(name = "AO_SYNC_DATE")
    @Temporal(TemporalType.DATE)
    private Date aoSyncDate;

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

    public Date getAoSyncDate() {
        return aoSyncDate;
    }

    public void setAoSyncDate(Date aoSyncDate) {
        this.aoSyncDate = aoSyncDate;
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    public Boolean getAoSend() {
        return aoSend;
    }

    public void setAoSend(Boolean aoSend) {
        this.aoSend = aoSend;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getEin() {
        return ein;
    }

    public void setEin(String ein) {
        this.ein = toUpperNullAllowed(ein);
    }

    public String getBudgetOrg() {
        return budgetOrg;
    }

    public void setBudgetOrg(String budgetOrg) {
        this.budgetOrg = budgetOrg;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getFcnDate() {
        return fcnDate;
    }

    public void setFcnDate(Date fcnDate) {
        this.fcnDate = fcnDate;
    }

    public Obligation getObligation() {
        return obligation;
    }

    public void setObligation(Obligation obligation) {
        this.obligation = obligation;
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
    @DependsOnProperties({"fcnDate"})
    public String getInstanceName(DatatypeFormatter datatypeFormatter) {
        return datatypeFormatter.formatDate(fcnDate);
    }
}