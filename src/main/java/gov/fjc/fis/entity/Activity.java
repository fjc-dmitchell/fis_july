package gov.fjc.fis.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.*;
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
import java.util.List;

import static gov.fjc.fis.FisUtilities.getCreatedModifiedString;

@JmixEntity
@Table(name = "FIS_ACTIVITY", indexes = {
        @Index(name = "IDX_FIS_ACTIVITY_DIVISION", columnList = "DIVISION_ID"),
        @Index(name = "IDX_FIS_ACTIVITY_DIVISION_FUND", columnList = "DIVISION_ID, FUND_ID"),
        @Index(name = "IDX_FIS_ACTIVITY_DIVISION_BRANCH", columnList = "DIVISION_ID, BRANCH_ID"),
        @Index(name = "IDX_FIS_ACTIVITY_DIVISION_GROUP", columnList = "DIVISION_ID, GROUP_ID"),
        @Index(name = "IDX_FIS_ACTIVITY_DIVISION_FUND_ENDDATE", columnList = "DIVISION_ID, FUND_ID, END_DATE")
}, uniqueConstraints = {
        @UniqueConstraint(name = "IDX_FIS_ACTIVITY_UNQ", columnNames = {"DIVISION_ID", "ACTIVITY_NUMBER"})
})
@Entity(name = "fis_Activity")
public class Activity {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "DIVISION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Division division;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "FUND_ID", nullable = false)
    private Fund fund;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "BRANCH_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Branch branch;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "GROUP_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @Pattern(message = "Activity number must contain 4 digits", regexp = "^[0-9]{4}$")
    @Column(name = "ACTIVITY_NUMBER", nullable = false, length = 4)
    @NotNull
    private String activityNumber;

    @Column(name = "TITLE", nullable = false)
    @NotNull
    private String title;

    @Column(name = "SHORT_TITLE")
    private String shortTitle;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE", length = 2)
    private String state;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "PROGRAM_DIRECTOR")
    private String programDirector;

    @Column(name = "NUMBER_PARTICIPANTS")
    private Integer numberParticipants;

    @Column(name = "NUMBER_PROGRAMS")
    private Integer numberPrograms;

    @Column(name = "NUMBER_FACULTY")
    private Integer numberFaculty;

    @Column(name = "SORT_CODE", nullable = false)
    @NotNull
    private Integer sortCode = 0;

    @Column(name = "MEMO")
    @Lob
    private String memo;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "TRAINING_PROJECT")
    private Boolean trainingProject;

    @NotNull
    @Column(name = "PROJECTED_AMOUNT", nullable = false, precision = 19, scale = 2)
    private BigDecimal projectedAmount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "REIMBURSED_AMOUNT", nullable = false, precision = 19, scale = 2)
    private BigDecimal reimbursedAmount = BigDecimal.ZERO;

    @Composition
    @OneToMany(mappedBy = "activity")
    private List<ActivityProjection> projections;

    @Composition
    @OneToMany(mappedBy = "activity")
    private List<ActivityReimbursement> reimbursements;

    @OrderBy("documentNumber")
    @Composition
    @OneToMany(mappedBy = "activity")
    private List<Obligation> obligations;

    @Column(name = "REPORT_NOTE")
    private String reportNote;

    @Column(name = "PARTICIPANT_FINAL")
    private Boolean participantFinal;

    @Column(name = "ADDED_TO_PLAN")
    private Boolean addedToPlan;

    @Column(name = "INITIAL_PROJECTION")
    private BigDecimal initialProjection;

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

    public void setInitialProjection(BigDecimal initialProjection) {
        this.initialProjection = initialProjection;
    }

    public BigDecimal getInitialProjection() {
        return initialProjection;
    }

    public Integer getSortCode() {
        return sortCode;
    }

    public void setSortCode(Integer sortCode) {
        this.sortCode = sortCode;
    }

    public List<ActivityReimbursement> getReimbursements() {
        return reimbursements;
    }

    public void setReimbursements(List<ActivityReimbursement> reimbursements) {
        this.reimbursements = reimbursements;
    }

    public List<ActivityProjection> getProjections() {
        return projections;
    }

    public void setProjections(List<ActivityProjection> projections) {
        this.projections = projections;
    }

    @DependsOnProperties({"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"})
    @JmixProperty
    public String getCreatedByString() {
        return getCreatedModifiedString(createdBy, createdDate, lastModifiedBy, lastModifiedDate);
    }

    @DependsOnProperties({"title", "activityNumber"})
    @JmixProperty
    public String getTitleAndCode() {
        return String.format("%s (%s)", title, activityNumber);
    }

    @DependsOnProperties({"activityNumber", "group"})
    @JmixProperty
    public Boolean getIsGeneric() {
        return group != null && activityNumber.equals(group.getGroupCode().concat("00"));
    }

    public Boolean getAddedToPlan() {
        return addedToPlan;
    }

    public void setAddedToPlan(Boolean addedToPlan) {
        this.addedToPlan = addedToPlan;
    }

    public Boolean getParticipantFinal() {
        return participantFinal;
    }

    public void setParticipantFinal(Boolean participantFinal) {
        this.participantFinal = participantFinal;
    }

    public String getReportNote() {
        return reportNote;
    }

    public void setReportNote(String reportNote) {
        this.reportNote = reportNote;
    }

    public BigDecimal getReimbursedAmount() {
        return reimbursedAmount;
    }

    public void setReimbursedAmount(BigDecimal reimbursedAmount) {
        this.reimbursedAmount = reimbursedAmount;
    }

    public BigDecimal getProjectedAmount() {
        return projectedAmount;
    }

    public void setProjectedAmount(BigDecimal projectedAmount) {
        this.projectedAmount = projectedAmount;
    }

    public Boolean getTrainingProject() {
        return trainingProject;
    }

    public void setTrainingProject(Boolean trainingProject) {
        this.trainingProject = trainingProject;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getNumberFaculty() {
        return numberFaculty;
    }

    public void setNumberFaculty(Integer numberFaculty) {
        this.numberFaculty = numberFaculty;
    }

    public Integer getNumberPrograms() {
        return numberPrograms;
    }

    public void setNumberPrograms(Integer numberPrograms) {
        this.numberPrograms = numberPrograms;
    }

    public Integer getNumberParticipants() {
        return numberParticipants;
    }

    public void setNumberParticipants(Integer numberParticipants) {
        this.numberParticipants = numberParticipants;
    }

    public String getProgramDirector() {
        return programDirector;
    }

    public void setProgramDirector(String programDirector) {
        this.programDirector = programDirector;
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

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Obligation> getObligations() {
        return obligations;
    }

    public void setObligations(List<Obligation> obligations) {
        this.obligations = obligations;
    }

    public String getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(String activityNumber) {
        this.activityNumber = activityNumber;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
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

    @InstanceName
    @DependsOnProperties({"activityNumber", "division"})
    public String getInstanceName(MetadataTools metadataTools) {
        return String.format("%s-%s-%s",
                metadataTools.format(activityNumber),
                metadataTools.format(division.getDivisionCode()),
                metadataTools.format(division.getAppropriation().getBudgetFiscalYear()));
    }
}