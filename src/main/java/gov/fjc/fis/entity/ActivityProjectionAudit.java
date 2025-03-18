package gov.fjc.fis.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNullElse;

@JmixEntity
@Table(name = "FIS_ACTIVITY_PROJECTION_AUDIT", indexes = {
        @Index(name = "IDX_FIS_ACTIVITY_PROJECTION_AUDIT_OBJECT_CLASS", columnList = "OBJECT_CLASS_ID"),
        @Index(name = "IDX_FIS_ACTIVITY_PROJECTION_AUDIT_ACTIVITY", columnList = "ACTIVITY_ID")
})
@Entity(name = "fis_ActivityProjectionAudit")
public class ActivityProjectionAudit {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Activity activity;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "OBJECT_CLASS_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ObjectClass objectClass;

    @Column(name = "OLD_AMOUNT", precision = 19, scale = 2)
    private BigDecimal oldAmount;
    @Column(name = "NEW_AMOUNT", precision = 19, scale = 2)
    private BigDecimal newAmount;
    @NotNull
    @Column(name = "CHANGE_TYPE", nullable = false, length = 1)
    private String changeType;
    @Column(name = "VERSION", nullable = false)
    @Version
    private Integer version;
    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;
    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    public void setChangeType(AuditChangeType changeType) {
        this.changeType = changeType == null ? null : changeType.getId();
    }

    public AuditChangeType getChangeType() {
        return changeType == null ? null : AuditChangeType.fromId(changeType);
    }

    @DependsOnProperties({"createdDate"})
    @JmixProperty
    public String getCreatedDateString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm");
        return f.format(createdDate);
    }

    @JmixProperty
    public String getChangeTypeString() {
        return switch (changeType) {
            case "I" -> "Insert";
            case "U" -> "Update";
            case "D" -> "Delete";
            default -> null;
        };
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @JmixProperty
    public BigDecimal getChangeAmount() {
        return requireNonNullElse(newAmount, BigDecimal.ZERO).subtract(requireNonNullElse(oldAmount, BigDecimal.ZERO));
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(BigDecimal newAmount) {
        this.newAmount = newAmount;
    }

    public BigDecimal getOldAmount() {
        return oldAmount;
    }

    public void setOldAmount(BigDecimal oldAmount) {
        this.oldAmount = oldAmount;
    }

    public ObjectClass getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClass objectClass) {
        this.objectClass = objectClass;
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
}