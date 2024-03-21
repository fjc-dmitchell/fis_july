package gov.fjc.fis.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;

import static gov.fjc.fis.fisUtilities.getCreatedModifiedString;

@JmixEntity
@Table(name = "FIS_CATEGORY", indexes = {
        @Index(name = "IDX_FIS_CATEGORY_APPROPRIATION", columnList = "APPROPRIATION_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_FIS_CATEGORY_UNQ", columnNames = {"APPROPRIATION_ID", "MOC"})
})
@Entity(name = "fis_Category")
public class Category {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "APPROPRIATION_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Appropriation appropriation;

    @Length(message = "The MOC should be 2 characters", min = 2, max = 2)
    @Column(name = "MOC", nullable = false, length = 2)
    @NotNull
    private String masterObjectClass;

    @Column(name = "TITLE", nullable = false)
    @NotNull
    private String title;

    @Composition
    @OrderBy("budgetObjectClass")
    @OneToMany(mappedBy = "category")
    private List<ObjectClass> budgetObjectClasses;

    @Composition
    @OneToMany(mappedBy = "category")
    private List<DivisionAllocation> allocations;

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

    public List<DivisionAllocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<DivisionAllocation> allocations) {
        this.allocations = allocations;
    }

    public List<ObjectClass> getBudgetObjectClasses() {
        return budgetObjectClasses;
    }

    public void setBudgetObjectClasses(List<ObjectClass> budgetObjectClasses) {
        this.budgetObjectClasses = budgetObjectClasses;
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    @DependsOnProperties({"title", "masterObjectClass"})
    @JmixProperty
    public String getTitleAndCode() {
        return String.format("%s (%s)", title, masterObjectClass);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMasterObjectClass() {
        return masterObjectClass;
    }

    public void setMasterObjectClass(String masterObjectClass) {
        this.masterObjectClass = masterObjectClass;
    }

    public Appropriation getAppropriation() {
        return appropriation;
    }

    public void setAppropriation(Appropriation appropriation) {
        this.appropriation = appropriation;
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
    @DependsOnProperties({"appropriation", "masterObjectClass", "title"})
    public String getInstanceName(MetadataTools metadataTools) {
        return String.format("%s %s %s",
                metadataTools.format(appropriation),
                metadataTools.format(masterObjectClass),
                metadataTools.format(title));
    }
}