package gov.fjc.fis.entity.form;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.Division;
import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
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
@Table(name = "FIS_DISTANCE_LEARNING", indexes = {
        @Index(name = "IDX_FIS_DISTANCE_LEARNING_DIVISION", columnList = "DIVISION_ID"),
        @Index(name = "IDX_FIS_DISTANCE_LEARNING_ACTIVITY", columnList = "ACTIVITY_ID")
})
@Entity(name = "fis_DistanceLearning")
public class DistanceLearning {
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

    @Column(name = "TYPE_AUDIO", nullable = false)
    @NotNull
    private Boolean typeAudio = false;

    @Column(name = "TYPE_VIDEO", nullable = false)
    @NotNull
    private Boolean typeVideo = false;

    @Column(name = "TYPE_WEBINAR", nullable = false)
    @NotNull
    private Boolean typeWebinar = false;

    @Column(name = "TYPE_ELEARNING", nullable = false)
    @NotNull
    private Boolean typeElearning = false;

    @Column(name = "PROGRAM_LEADER")
    private String programLeader;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "OBJECTIVES")
    @Lob
    private String objectives;

    @Column(name = "TOPICS")
    @Lob
    private String topics;

    @Column(name = "JUSTIFICATION")
    @Lob
    private String justification;

    @Column(name = "AUDIENCE_CHIEFJUDGE_CIRCUIT", nullable = false)
    @NotNull
    private Boolean audienceChiefjudgeCircuit = false;

    @Column(name = "AUDIENCE_CHIEFJUDGE_DISTRICT", nullable = false)
    @NotNull
    private Boolean audienceChiefjudgeDistrict = false;

    @Column(name = "AUDIENCE_CHIEFJUDGE_BANKRUPTCY", nullable = false)
    @NotNull
    private Boolean audienceChiefjudgeBankruptcy = false;

    @Column(name = "AUDIENCE_JUDGE_CIRCUIT", nullable = false)
    @NotNull
    private Boolean audienceJudgeCircuit = false;

    @Column(name = "AUDIENCE_JUDGE_DISTRICT", nullable = false)
    @NotNull
    private Boolean audienceJudgeDistrict = false;

    @Column(name = "AUDIENCE_JUDGE_BANKRUPTCY", nullable = false)
    @NotNull
    private Boolean audienceJudgeBankruptcy = false;

    @Column(name = "AUDIENCE_JUDGE_MAGISTRATE", nullable = false)
    @NotNull
    private Boolean audienceJudgeMagistrate = false;

    @Column(name = "AUDIENCE_CUE_CIRCUIT", nullable = false)
    @NotNull
    private Boolean audienceCueCircuit = false;

    @Column(name = "AUDIENCE_CUE_DISTRICT", nullable = false)
    @NotNull
    private Boolean audienceCueDistrict = false;

    @Column(name = "AUDIENCE_CUE_BANKRUPTCY", nullable = false)
    @NotNull
    private Boolean audienceCueBankruptcy = false;

    @Column(name = "AUDIENCE_CUE_PROBATION", nullable = false)
    @NotNull
    private Boolean audienceCueProbation = false;

    @Column(name = "AUDIENCE_FEDERAL_DEFENDERS", nullable = false)
    @NotNull
    private Boolean audienceFederalDefenders = false;

    @Column(name = "AUDIENCE_ATTORNEYS_CLERKS", nullable = false)
    @NotNull
    private Boolean audienceAttorneysClerks = false;

    @Column(name = "AUDIENCE_MANAGERS_SUPERVISORS", nullable = false)
    @NotNull
    private Boolean audienceManagersSupervisors = false;

    @Column(name = "AUDIENCE_COURTSTAFF_OFFICERS", nullable = false)
    @NotNull
    private Boolean audienceCourtstaffOfficers = false;

    @Column(name = "DISTRIBUTION_FJCONLINE", nullable = false)
    @NotNull
    private Boolean distributionFjconline = false;

    @Column(name = "DISTRIBUTION_YOUTUBE", nullable = false)
    @NotNull
    private Boolean distributionYoutube = false;

    @Column(name = "COST_TRAVEL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal costTravel = BigDecimal.ZERO;

    @Column(name = "COST_CONSULTANT", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal costConsultant = BigDecimal.ZERO;

    @Column(name = "COST_CREW", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal costCrew = BigDecimal.ZERO;

    @Column(name = "COST_ACTOR", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal costActor = BigDecimal.ZERO;

    @Column(name = "COST_OTHER", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal costOther = BigDecimal.ZERO;

    @Column(name = "COST_OTHER_DESCRIPTION")
    private String costOtherDescription;

    @Column(name = "COST_TOTAL", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal costTotal = BigDecimal.ZERO;

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

    public Boolean getAudienceChiefjudgeBankruptcy() {
        return audienceChiefjudgeBankruptcy;
    }

    public void setAudienceChiefjudgeBankruptcy(Boolean audienceChiefjudgeBankruptcy) {
        this.audienceChiefjudgeBankruptcy = audienceChiefjudgeBankruptcy;
    }

    public Boolean getAudienceChiefjudgeDistrict() {
        return audienceChiefjudgeDistrict;
    }

    public void setAudienceChiefjudgeDistrict(Boolean audienceChiefjudgeDistrict) {
        this.audienceChiefjudgeDistrict = audienceChiefjudgeDistrict;
    }

    public Boolean getAudienceChiefjudgeCircuit() {
        return audienceChiefjudgeCircuit;
    }

    public void setAudienceChiefjudgeCircuit(Boolean audienceChiefjudgeCircuit) {
        this.audienceChiefjudgeCircuit = audienceChiefjudgeCircuit;
    }

    public Boolean getDistributionFjconline() {
        return distributionFjconline;
    }

    public void setDistributionFjconline(Boolean distributionFjconline) {
        this.distributionFjconline = distributionFjconline;
    }

    public String getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(String activityNumber) {
        this.activityNumber = activityNumber;
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    public BigDecimal getCostTotal() {
        return costTotal;
    }

    public void setCostTotal(BigDecimal costTotal) {
        this.costTotal = costTotal;
    }

    public String getCostOtherDescription() {
        return costOtherDescription;
    }

    public void setCostOtherDescription(String costOtherDescription) {
        this.costOtherDescription = costOtherDescription;
    }

    public BigDecimal getCostOther() {
        return costOther;
    }

    public void setCostOther(BigDecimal costOther) {
        this.costOther = costOther;
    }

    public BigDecimal getCostActor() {
        return costActor;
    }

    public void setCostActor(BigDecimal costActor) {
        this.costActor = costActor;
    }

    public BigDecimal getCostCrew() {
        return costCrew;
    }

    public void setCostCrew(BigDecimal costCrew) {
        this.costCrew = costCrew;
    }

    public BigDecimal getCostConsultant() {
        return costConsultant;
    }

    public void setCostConsultant(BigDecimal costConsultant) {
        this.costConsultant = costConsultant;
    }

    public BigDecimal getCostTravel() {
        return costTravel;
    }

    public void setCostTravel(BigDecimal costTravel) {
        this.costTravel = costTravel;
    }

    public Boolean getDistributionYoutube() {
        return distributionYoutube;
    }

    public void setDistributionYoutube(Boolean distributionYoutube) {
        this.distributionYoutube = distributionYoutube;
    }

    public Boolean getAudienceCourtstaffOfficers() {
        return audienceCourtstaffOfficers;
    }

    public void setAudienceCourtstaffOfficers(Boolean audienceCourtstaffOfficers) {
        this.audienceCourtstaffOfficers = audienceCourtstaffOfficers;
    }

    public Boolean getAudienceManagersSupervisors() {
        return audienceManagersSupervisors;
    }

    public void setAudienceManagersSupervisors(Boolean audienceManagersSupervisors) {
        this.audienceManagersSupervisors = audienceManagersSupervisors;
    }

    public Boolean getAudienceAttorneysClerks() {
        return audienceAttorneysClerks;
    }

    public void setAudienceAttorneysClerks(Boolean audienceAttorneysClerks) {
        this.audienceAttorneysClerks = audienceAttorneysClerks;
    }

    public Boolean getAudienceFederalDefenders() {
        return audienceFederalDefenders;
    }

    public void setAudienceFederalDefenders(Boolean audienceFederalDefenders) {
        this.audienceFederalDefenders = audienceFederalDefenders;
    }

    public Boolean getAudienceCueProbation() {
        return audienceCueProbation;
    }

    public void setAudienceCueProbation(Boolean audienceCueProbation) {
        this.audienceCueProbation = audienceCueProbation;
    }

    public Boolean getAudienceCueBankruptcy() {
        return audienceCueBankruptcy;
    }

    public void setAudienceCueBankruptcy(Boolean audienceCueBankruptcy) {
        this.audienceCueBankruptcy = audienceCueBankruptcy;
    }

    public Boolean getAudienceCueDistrict() {
        return audienceCueDistrict;
    }

    public void setAudienceCueDistrict(Boolean audienceCueDistrict) {
        this.audienceCueDistrict = audienceCueDistrict;
    }

    public Boolean getAudienceCueCircuit() {
        return audienceCueCircuit;
    }

    public void setAudienceCueCircuit(Boolean audienceCueCircuit) {
        this.audienceCueCircuit = audienceCueCircuit;
    }

    public Boolean getAudienceJudgeMagistrate() {
        return audienceJudgeMagistrate;
    }

    public void setAudienceJudgeMagistrate(Boolean audienceJudgeMagistrate) {
        this.audienceJudgeMagistrate = audienceJudgeMagistrate;
    }

    public Boolean getAudienceJudgeBankruptcy() {
        return audienceJudgeBankruptcy;
    }

    public void setAudienceJudgeBankruptcy(Boolean audienceJudgeBankruptcy) {
        this.audienceJudgeBankruptcy = audienceJudgeBankruptcy;
    }

    public Boolean getAudienceJudgeDistrict() {
        return audienceJudgeDistrict;
    }

    public void setAudienceJudgeDistrict(Boolean audienceJudgeDistrict) {
        this.audienceJudgeDistrict = audienceJudgeDistrict;
    }

    public Boolean getAudienceJudgeCircuit() {
        return audienceJudgeCircuit;
    }

    public void setAudienceJudgeCircuit(Boolean audienceJudgeCircuit) {
        this.audienceJudgeCircuit = audienceJudgeCircuit;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
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

    public String getProgramLeader() {
        return programLeader;
    }

    public void setProgramLeader(String programLeader) {
        this.programLeader = programLeader;
    }

    public Boolean getTypeElearning() {
        return typeElearning;
    }

    public void setTypeElearning(Boolean typeElearning) {
        this.typeElearning = typeElearning;
    }

    public Boolean getTypeWebinar() {
        return typeWebinar;
    }

    public void setTypeWebinar(Boolean typeWebinar) {
        this.typeWebinar = typeWebinar;
    }

    public Boolean getTypeVideo() {
        return typeVideo;
    }

    public void setTypeVideo(Boolean typeVideo) {
        this.typeVideo = typeVideo;
    }

    public Boolean getTypeAudio() {
        return typeAudio;
    }

    public void setTypeAudio(Boolean typeAudio) {
        this.typeAudio = typeAudio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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