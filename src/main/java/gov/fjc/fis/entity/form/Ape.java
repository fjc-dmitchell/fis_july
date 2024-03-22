package gov.fjc.fis.entity.form;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.Division;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import static gov.fjc.fis.fisUtilities.getCreatedModifiedString;

@JmixEntity
@Table(name = "FIS_APE", indexes = {
        @Index(name = "IDX_FIS_APE_DIVISION", columnList = "DIVISION_ID"),
        @Index(name = "IDX_FIS_APE_ACTIVITY", columnList = "ACTIVITY_ID")
})
@Entity(name = "fis_Ape")
public class Ape {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "DIVISION_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Division division;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "ACTIVITY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @Pattern(message = "Activity number must contain 4 digits", regexp = "^[0-9]{4}$")
    @Column(name = "ACTIVITY_NUMBER", length = 4)
    private String activityNumber;

    @InstanceName
    @Column(name = "TITLE", nullable = false)
    @NotNull
    private String title;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "DURATION")
    private Integer duration;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE", length = 2)
    private String state;

    @Column(name = "PROGRAM_LEADER")
    private String programLeader;

    @Column(name = "PROGRAM_SUPPORT")
    private String programSupport;

    @Column(name = "OBJECTIVES")
    @Lob
    private String objectives;

    @Column(name = "DELIVERY_RATIONALE")
    @Lob
    private String deliveryRationale;

    @Column(name = "SITE_RATIONALE")
    @Lob
    private String siteRationale;

    @Column(name = "COMPLIANCE", nullable = false)
    @NotNull
    private Boolean compliance = false;

    @NotNull
    @Column(name = "NUMBER_PARTICIPANTS", nullable = false)
    private Integer numberParticipants = 0;

    @Column(name = "NUMBER_FACULTY")
    private Integer numberFaculty = 0;

    @Column(name = "FJC_BOC_2125_LODGING", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2125_lodging = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_2125_TRAVEL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2125_travel = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_2125_SUBSISTENCE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2125_subsistence = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_2359", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2359 = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_2529", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2529 = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_2543", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2543 = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_2601", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_2601 = BigDecimal.ZERO;

    @NotNull
    @Column(name = "FJC_BOC_2603", nullable = false)
    private BigDecimal fjc_boc_2603 = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_OTHER", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_boc_other = BigDecimal.ZERO;

    @Column(name = "FJC_BOC_OTHER_DESCRIPTION")
    private String fjc_boc_other_description;

    @Column(name = "FJC_TOTAL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal fjc_total = BigDecimal.ZERO;

    @Column(name = "SPONSOR_AO", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal sponsor_ao = BigDecimal.ZERO;

    @Column(name = "SPONSOR_USSC", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal sponsor_ussc = BigDecimal.ZERO;

    @Column(name = "SPONSOR_DOJ", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal sponsor_doj = BigDecimal.ZERO;

    @Column(name = "SPONSOR_OTHER", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal sponsor_other = BigDecimal.ZERO;

    @Column(name = "SPONSOR_OTHER_DESCRIPTION")
    private String sponsor_other_description;

    @Column(name = "SPONSOR_TOTAL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal sponsor_total = BigDecimal.ZERO;

    @Column(name = "APE_TOTAL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal ape_total = BigDecimal.ZERO;

    @Column(name = "APE_COMMENT")
    @Lob
    private String apeComment;

    @Column(name = "DIRECTOR_STATEMENT")
    @Lob
    private String directorStatement;

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

    public String getProgramSupport() {
        return programSupport;
    }

    public void setProgramSupport(String programSupport) {
        this.programSupport = programSupport;
    }

    public void setNumberParticipants(Integer numberParticipants) {
        this.numberParticipants = numberParticipants;
    }

    public Integer getNumberParticipants() {
        return numberParticipants;
    }

    public String getDirectorStatement() {
        return directorStatement;
    }

    public void setDirectorStatement(String directorStatement) {
        this.directorStatement = directorStatement;
    }

    public String getApeComment() {
        return apeComment;
    }

    public void setApeComment(String apeComment) {
        this.apeComment = apeComment;
    }

    public BigDecimal getApe_total() {
        return ape_total;
    }

    public void setApe_total(BigDecimal ape_total) {
        this.ape_total = ape_total;
    }

    public BigDecimal getSponsor_total() {
        return sponsor_total;
    }

    public void setSponsor_total(BigDecimal sponsor_total) {
        this.sponsor_total = sponsor_total;
    }

    public String getSponsor_other_description() {
        return sponsor_other_description;
    }

    public void setSponsor_other_description(String sponsor_other_description) {
        this.sponsor_other_description = sponsor_other_description;
    }

    public BigDecimal getSponsor_other() {
        return sponsor_other;
    }

    public void setSponsor_other(BigDecimal sponsor_other) {
        this.sponsor_other = sponsor_other;
    }

    public BigDecimal getSponsor_doj() {
        return sponsor_doj;
    }

    public void setSponsor_doj(BigDecimal sponsor_doj) {
        this.sponsor_doj = sponsor_doj;
    }

    public BigDecimal getSponsor_ussc() {
        return sponsor_ussc;
    }

    public void setSponsor_ussc(BigDecimal sponsor_ussc) {
        this.sponsor_ussc = sponsor_ussc;
    }

    public BigDecimal getSponsor_ao() {
        return sponsor_ao;
    }

    public void setSponsor_ao(BigDecimal sponsor_ao) {
        this.sponsor_ao = sponsor_ao;
    }

    public BigDecimal getFjc_total() {
        return fjc_total;
    }

    public void setFjc_total(BigDecimal fjc_total) {
        this.fjc_total = fjc_total;
    }

    public String getFjc_boc_other_description() {
        return fjc_boc_other_description;
    }

    public void setFjc_boc_other_description(String fjc_boc_other_description) {
        this.fjc_boc_other_description = fjc_boc_other_description;
    }

    public void setFjc_boc_2603(BigDecimal fjc_boc_2603) {
        this.fjc_boc_2603 = fjc_boc_2603;
    }

    public BigDecimal getFjc_boc_2603() {
        return fjc_boc_2603;
    }

    public BigDecimal getFjc_boc_other() {
        return fjc_boc_other;
    }

    public void setFjc_boc_other(BigDecimal fjc_boc_other) {
        this.fjc_boc_other = fjc_boc_other;
    }

    public BigDecimal getFjc_boc_2601() {
        return fjc_boc_2601;
    }

    public void setFjc_boc_2601(BigDecimal fjc_boc_2601) {
        this.fjc_boc_2601 = fjc_boc_2601;
    }

    public BigDecimal getFjc_boc_2543() {
        return fjc_boc_2543;
    }

    public void setFjc_boc_2543(BigDecimal fjc_boc_2543) {
        this.fjc_boc_2543 = fjc_boc_2543;
    }

    public BigDecimal getFjc_boc_2529() {
        return fjc_boc_2529;
    }

    public void setFjc_boc_2529(BigDecimal fjc_boc_2529) {
        this.fjc_boc_2529 = fjc_boc_2529;
    }

    public BigDecimal getFjc_boc_2359() {
        return fjc_boc_2359;
    }

    public void setFjc_boc_2359(BigDecimal fjc_boc_2359) {
        this.fjc_boc_2359 = fjc_boc_2359;
    }

    public BigDecimal getFjc_boc_2125_subsistence() {
        return fjc_boc_2125_subsistence;
    }

    public void setFjc_boc_2125_subsistence(BigDecimal fjc_boc_2125_subsistence) {
        this.fjc_boc_2125_subsistence = fjc_boc_2125_subsistence;
    }

    public BigDecimal getFjc_boc_2125_travel() {
        return fjc_boc_2125_travel;
    }

    public void setFjc_boc_2125_travel(BigDecimal fjc_boc_2125_travel) {
        this.fjc_boc_2125_travel = fjc_boc_2125_travel;
    }

    public BigDecimal getFjc_boc_2125_lodging() {
        return fjc_boc_2125_lodging;
    }

    public void setFjc_boc_2125_lodging(BigDecimal fjc_boc_2125_lodging) {
        this.fjc_boc_2125_lodging = fjc_boc_2125_lodging;
    }

    public Integer getNumberFaculty() {
        return numberFaculty;
    }

    public void setNumberFaculty(Integer numberFaculty) {
        this.numberFaculty = numberFaculty;
    }

    public Boolean getCompliance() {
        return compliance;
    }

    public void setCompliance(Boolean compliance) {
        this.compliance = compliance;
    }

    public String getSiteRationale() {
        return siteRationale;
    }

    public void setSiteRationale(String siteRationale) {
        this.siteRationale = siteRationale;
    }

    public String getDeliveryRationale() {
        return deliveryRationale;
    }

    public void setDeliveryRationale(String deliveryRationale) {
        this.deliveryRationale = deliveryRationale;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getProgramLeader() {
        return programLeader;
    }

    public void setProgramLeader(String programLeader) {
        this.programLeader = programLeader;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(String activityNumber) {
        this.activityNumber = activityNumber;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
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