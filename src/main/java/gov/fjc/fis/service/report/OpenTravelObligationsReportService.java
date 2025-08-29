package gov.fjc.fis.service.report;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.DocumentType;
import gov.fjc.fis.entity.dto.ObligationDto;
import gov.fjc.fis.reportdata.OpenObligationsReportData;
import gov.fjc.fis.reportdata.OpenTravelObligationsReportData;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static gov.fjc.fis.FisUtilities.getCurrentDateMinusDays;
import static gov.fjc.fis.FisUtilities.getNumberOfDaysFromToday;

@Component("fis_OpenTravelObligationsReportService")
public class OpenTravelObligationsReportService {
    @Autowired
    private DataManager dataManager;

    public List<KeyValueEntity> getOpenObligations(Set<Division> divisions, Branch branch, LocalDate bDate, LocalDate eDate) {
        Date beginDate = Date.from(bDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(eDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        var travelAuthorizationId = DocumentType.TRAVEL_AUTHORIZATION.getId();
        var obligations = dataManager.loadValues(
                        "SELECT o.amount, o.documentNumber, o.vendor, a.activityNumber, a.title, a.city,"
                                + " a.state, CASE WHEN (o.documentType = :ta) THEN o.travelEndDate ELSE a.endDate END,"
                                + " o.lineNumber"
                                + " FROM fis_Obligation o"
                                + " INNER JOIN fis_Activity a ON a=o.activity"
                                + " WHERE ((:anyBranch = true AND a.division IN :divisions)"
                                + " OR (:anyBranch = false AND a.branch = :branch))"
                                + " AND o.status=true"
                                + " AND o.documentType = :ta AND o.documentDate BETWEEN :beginDate AND :endDate"
                )
                .parameter("ta", travelAuthorizationId)
                .parameter("anyBranch", branch == null)
                .parameter("branch", branch)
                .parameter("divisions", divisions)
                .parameter("beginDate", beginDate)
                .parameter("endDate", endDate)
                .properties("amount", "docid", "vendor", "actnum", "title", "city", "state", "enddate", "lineno")
                .list();

        obligations.sort(Comparator.comparing((KeyValueEntity o) -> (Date) o.getValue("enddate"),
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(o -> o.getValue("actnum"))
                .thenComparing(o -> o.getValue("docid"))
                .thenComparingInt(o -> o.getValue("lineno")));

        return obligations;
    }

    public OpenTravelObligationsReportData generateReportData(Appropriation appropriation,
                                                              Set<Division> division,
                                                              Branch branch,
                                                              LocalDate beginDate,
                                                              LocalDate endDate,
                                                              boolean obbba) {

//        division = branch == null ? division : branch.getDivision();
//        var appropriation = division.getAppropriation();

        var reportData = new OpenObligationsReportData(appropriation, division, branch);

        var obligations = getOpenObligations(division, branch, beginDate, endDate);
        var totalObligated = BigDecimal.ZERO;
        long vDays = 0;

        List<ObligationDto> obligationDtos = new ArrayList<>();
        ObligationDto dto;
        for (var kvEntity : obligations) {
            dto = dataManager.create(ObligationDto.class);
            dto.setAmount(kvEntity.getValue("amount"));
            dto.setDocumentNumber(kvEntity.getValue("docid"));
            dto.setVendor(kvEntity.getValue("vendor"));
            dto.setActivityNumber(kvEntity.getValue("actnum"));
            dto.setActivityTitle(kvEntity.getValue("title"));
            dto.setTravelEndDate(kvEntity.getValue("enddate"));
            String city = kvEntity.getValue("city");
            String state = kvEntity.getValue("state");
            if (city == null) {
                dto.setLocation(state);
            } else if (state == null) {
                dto.setLocation(city);
            } else {
                dto.setLocation(city.concat(", ").concat(state));
            }
            obligationDtos.add(dto);
            totalObligated = totalObligated.add(dto.getAmount());
            vDays += getNumberOfDaysFromToday(dto.getTravelEndDate());
        }

        reportData.setObligations(obligationDtos);
        reportData.setTotalObligated(totalObligated);
//        reportData.setNumberOfDays(numberOfDays);
//        var endDate = getCurrentDateMinusDays(numberOfDays);
        reportData.setLatestTravelDate(endDate);
        if (reportData.getNumberOfObligations() == 0) {
            reportData.setAverageDays(0);
        } else {
            reportData.setAverageDays((float) vDays / reportData.getNumberOfObligations());
        }

        return reportData;
    }
}