package gov.fjc.fis.entity;

import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;

@JmixEntity
@Table(name = "FIS_PAY_GRADE", indexes = {
        @Index(name = "IDX_FIS_PAY_GRADE_UNQ", columnList = "SETID, SAL_ADMIN_PLAN, GRADE", unique = true)
})
@Entity(name = "fis_PayGrade")
public class PayGrade {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "SETID", nullable = false, length = 5)
    @NotNull
    private String setid;
    @Column(name = "SAL_ADMIN_PLAN", nullable = false, length = 4)
    @NotNull
    private String salAdminPlan;
    @Column(name = "GRADE", nullable = false, length = 3)
    @NotNull
    private String grade;
    @Column(name = "GRADE_TITLE_JPN")
    private String gradeTitleJpn;
    @Column(name = "LOCALITY_ENTITLED", nullable = false)
    @NotNull
    private Boolean localityEntitled = false;
    @Column(name = "LOCALITY_FORFEITURE", nullable = false)
    @NotNull
    private Boolean localityForfeiture = false;
    @Column(name = "DESCR")
    private String descr;
    @Column(name = "DESCRSHORT")
    private String descrshort;
    @OrderBy("effdate DESC")
    @Composition
    @OneToMany(mappedBy = "payGrade")
    private List<PayGradeRate> rates;
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

    @JmixProperty
    public String getLocalityString() {
        if (localityEntitled) {
            return localityForfeiture ? "Entitled / Forfeit" : "Entitled";
        } else {
            return "No Locality";
        }
    }

    public Boolean getLocalityForfeiture() {
        return localityForfeiture;
    }

    public void setLocalityForfeiture(Boolean localityForfeiture) {
        this.localityForfeiture = localityForfeiture;
    }

    public Boolean getLocalityEntitled() {
        return localityEntitled;
    }

    public void setLocalityEntitled(Boolean localityEntitled) {
        this.localityEntitled = localityEntitled;
    }

    public List<PayGradeRate> getRates() {
        return rates;
    }

    public void setRates(List<PayGradeRate> rates) {
        this.rates = rates;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getDescrshort() {
        return descrshort;
    }

    public void setDescrshort(String descrshort) {
        this.descrshort = descrshort;
    }

    public String getGradeTitleJpn() {
        return gradeTitleJpn;
    }

    public void setGradeTitleJpn(String gradeTitleJpn) {
        this.gradeTitleJpn = gradeTitleJpn;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSalAdminPlan() {
        return salAdminPlan;
    }

    public void setSalAdminPlan(String salAdminPlan) {
        this.salAdminPlan = salAdminPlan;
    }

    public String getSetid() {
        return setid;
    }

    public void setSetid(String setid) {
        this.setid = setid;
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
    @DependsOnProperties({"setid", "salAdminPlan", "grade"})
    public String getInstanceName(MetadataTools metadataTools) {
        return String.format("%s-%s-%s",
                metadataTools.format(setid),
                metadataTools.format(salAdminPlan),
                metadataTools.format(grade));
    }
}