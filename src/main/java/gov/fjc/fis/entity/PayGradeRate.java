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
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.Date;

import static java.util.Objects.requireNonNullElse;

@JmixEntity
@Table(name = "FIS_PAY_GRADE_RATE", indexes = {
        @Index(name = "IDX_FIS_PAY_GRADE_RATE_PAY_GRADE", columnList = "PAY_GRADE_ID"),
        @Index(name = "IDX_FIS_PAY_GRADE_RATE_UNQ", columnList = "PAY_GRADE_ID, EFFDATE", unique = true)
})
@Entity(name = "fis_PayGradeRate")
public class PayGradeRate {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "PAY_GRADE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PayGrade payGrade;
    @Temporal(TemporalType.DATE)
    @Column(name = "EFFDATE", nullable = false)
    @NotNull
    private Date effdate;
    @Column(name = "MIN_RT_ANNUAL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal minRtAnnual = BigDecimal.ZERO;
    @Column(name = "MAX_RT_ANNUAL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal maxRtAnnual = BigDecimal.ZERO;
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

    @DependsOnProperties({"minRtAnnual", "maxRtAnnual"})
    @JmixProperty
    public BigDecimal getQuartileAmount() {
        return requireNonNullElse(maxRtAnnual, BigDecimal.ZERO).subtract(requireNonNullElse(minRtAnnual, BigDecimal.ZERO)).divide(new BigDecimal(4), 0, BigDecimal.ROUND_HALF_UP);
    }

    @DependsOnProperties({"minRtAnnual"})
    @JmixProperty
    public BigDecimal getQuartile1Cap() {
        return requireNonNullElse(getMinRtAnnual(), BigDecimal.ZERO).add(getQuartileAmount());
    }

    @DependsOnProperties({"minRtAnnual"})
    @JmixProperty
    public BigDecimal getQuartile2Cap() {
        return requireNonNullElse(getMinRtAnnual(), BigDecimal.ZERO).add((getQuartileAmount().multiply(new BigDecimal(2))));
    }

    @DependsOnProperties({"minRtAnnual"})
    @JmixProperty
    public BigDecimal getQuartile3Cap() {
        return requireNonNullElse(getMinRtAnnual(), BigDecimal.ZERO).add((getQuartileAmount().multiply(new BigDecimal(3))));
    }

    @DependsOnProperties({"maxRtAnnual"})
    @JmixProperty
    public BigDecimal getQuartile4Cap() {
        return requireNonNullElse(maxRtAnnual, BigDecimal.ZERO);
    }

    @DependsOnProperties({"minRtAnnual"})
    @JmixProperty
    public String getQuartile1Range() {
        DecimalFormat df = new DecimalFormat("#,###");
        return String.format("%s - %s", df.format(minRtAnnual.intValue()), df.format(getQuartile1Cap()));
    }

    @JmixProperty
    public String getQuartile2Range() {
        DecimalFormat df = new DecimalFormat("#,###");
        return String.format("%s - %s", df.format(getQuartile1Cap().add(BigDecimal.ONE)), df.format(getQuartile2Cap()));
    }

    @JmixProperty
    public String getQuartile3Range() {
        DecimalFormat df = new DecimalFormat("#,###");
        return String.format("%s - %s", df.format(getQuartile2Cap().add(BigDecimal.ONE)), df.format(getQuartile3Cap()));
    }

    @JmixProperty
    public String getQuartile4Range() {
        DecimalFormat df = new DecimalFormat("#,###");
        return String.format("%s - %s", df.format(getQuartile3Cap().add(BigDecimal.ONE)), df.format(getQuartile4Cap()));
    }

    public Date getEffdate() {
        return effdate;
    }

    public void setEffdate(Date effdate) {
        this.effdate = effdate;
    }

    public BigDecimal getMaxRtAnnual() {
        return requireNonNullElse(maxRtAnnual, BigDecimal.ZERO);
    }

    public void setMaxRtAnnual(BigDecimal maxRtAnnual) {
        this.maxRtAnnual = maxRtAnnual;
    }

    public BigDecimal getMinRtAnnual() {
        return requireNonNullElse(minRtAnnual, BigDecimal.ZERO);
    }

    public void setMinRtAnnual(BigDecimal minRtAnnual) {
        this.minRtAnnual = minRtAnnual;
    }

    public PayGrade getPayGrade() {
        return payGrade;
    }

    public void setPayGrade(PayGrade payGrade) {
        this.payGrade = payGrade;
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
}