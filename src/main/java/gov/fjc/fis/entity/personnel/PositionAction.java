package gov.fjc.fis.entity.personnel;

import io.jmix.core.DeletePolicy;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static gov.fjc.fis.fisUtilities.getCreatedModifiedString;
import static gov.fjc.fis.fisUtilities.getTotalNullAllowed;

@JmixEntity
@Table(name = "FIS_POSITION_ACTION", indexes = {
        @Index(name = "IDX_FIS_POSITION_ACTION_PAY_PERIOD", columnList = "PAY_PERIOD_ID"),
        @Index(name = "IDX_FIS_POSITION_ACTION_ACTION_CODE", columnList = "ACTION_CODE_ID"),
        @Index(name = "IDX_FIS_POSITION_ACTION_POSITION", columnList = "POSITION_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_FIS_POSITION_ACTION_UNQ", columnNames = {"POSITION_ID", "PAY_PERIOD_ID", "ACTION_CODE_ID"})
})
@Entity(name = "fis_PositionAction")
public class PositionAction {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "POSITION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Position position;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "PAY_PERIOD_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PayPeriod payPeriod;

    @Column(name = "GVT_COMPRATE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal gvtComprate = BigDecimal.ZERO;

    @Column(name = "GVT_LOCALITY_ADJ", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal gvtLocalityAdj = BigDecimal.ZERO;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "ACTION_CODE_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ActionCode actionCode;

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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    @DependsOnProperties({"gvtComprate", "gvtLocalityAdj"})
    @JmixProperty
    public BigDecimal getTotalPay() {
        return getTotalNullAllowed(gvtComprate, gvtLocalityAdj);
    }

    public ActionCode getActionCode() {
        return actionCode;
    }

    public void setActionCode(ActionCode actionCode) {
        this.actionCode = actionCode;
    }

    public BigDecimal getGvtLocalityAdj() {
        return gvtLocalityAdj;
    }

    public void setGvtLocalityAdj(BigDecimal gvtLocalityAdj) {
        this.gvtLocalityAdj = gvtLocalityAdj;
    }

    public BigDecimal getGvtComprate() {
        return gvtComprate;
    }

    public void setGvtComprate(BigDecimal gvtComprate) {
        this.gvtComprate = gvtComprate;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(PayPeriod payPeriod) {
        this.payPeriod = payPeriod;
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
    @DependsOnProperties({"position", "actionCode", "payPeriod"})
    public String getInstanceName(MetadataTools metadataTools) {
        return String.format("%s %s %s",
                metadataTools.format(position),
                metadataTools.format(actionCode),
                metadataTools.format(payPeriod));
    }
}