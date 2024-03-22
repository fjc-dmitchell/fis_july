package gov.fjc.fis.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;

import static gov.fjc.fis.FisUtilities.getCreatedModifiedString;
import static gov.fjc.fis.FisUtilities.toUpperNullAllowed;
import static java.util.Objects.requireNonNullElse;

@JmixEntity
@Table(name = "FIS_GROUP", indexes = {
        @Index(name = "IDX_FIS_GROUP_DIVISION", columnList = "DIVISION_ID")
})
@Entity(name = "fis_Group")
public class Group {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "DIVISION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Division division;

    @Column(name = "GROUP_CODE", nullable = false, length = 2)
    @NotNull
    private String groupCode;

    @InstanceName
    @Column(name = "TITLE", nullable = false)
    @NotNull
    private String title;

    @Column(name = "SORT_CODE", nullable = false)
    @NotNull
    private Integer sortCode = 0;

    @Composition
    @OrderBy("activityNumber")
    @OneToMany(mappedBy = "group")
    private List<Activity> activities;

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

    public Integer getSortCode() {
        return sortCode;
    }

    public void setSortCode(Integer sortCode) {
        this.sortCode = requireNonNullElse(sortCode, 0);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    @DependsOnProperties({"title", "groupCode"})
    @JmixProperty
    public String getTitleAndCode() {
        return String.format("%s (%s)", title, groupCode);
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = toUpperNullAllowed(groupCode);
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
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