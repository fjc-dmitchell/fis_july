package gov.fjc.fis.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@JmixEntity
@Table(name = "FIS_DOCUMENT_EXCEPTION")
@Entity(name = "fis_DocumentException")
public class DocumentException {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "FUND", nullable = false, length = 6)
    @NotNull
    private String fund;

    @NotNull
    @Column(name = "BBFY", nullable = false, length = 4)
    private String bbfy;

    @NotNull
    @Column(name = "BUDGETORG", nullable = false, length = 7)
    private String budgetorg;

    @Column(name = "BOC", nullable = false, length = 7)
    @NotNull
    private String boc;

    @Column(name = "DOCTYPE", nullable = false, length = 5)
    @NotNull
    private String doctype;

    @Column(name = "DOCNUMBER", nullable = false, length = 50)
    @NotNull
    private String docnumber;

    @Column(name = "VERSION", nullable = false)
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

    public String getDocnumber() {
        return docnumber;
    }

    public String getDoctype() {
        return doctype;
    }

    public String getBoc() {
        return boc;
    }

    public String getBudgetorg() {
        return budgetorg;
    }

    public String getBbfy() {
        return bbfy;
    }

    public String getFund() {
        return fund;
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