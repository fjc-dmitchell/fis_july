package gov.fjc.fis.service;

import gov.fjc.fis.entity.Appropriation;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.core.session.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("fis_AppropriationService")
public class AppropriationService {
    @Autowired
    private DataManager dataManager;

    public List<Appropriation> getAppropriations() {
        return dataManager.load(Appropriation.class)
                .query("SELECT a FROM fis_Appropriation a ORDER BY a.budgetFiscalYear DESC")
                .list();
    }

    public Appropriation getBfyEntryAppropriation(SessionData sessionData) {
        Appropriation bfyEntry = (Appropriation) sessionData.getAttribute("bfyEntry");
        if (bfyEntry == null) {
            throw new RuntimeException("Unable to fetch Appropriation for data entry");
        }
        return bfyEntry;
    }

    public List<Appropriation> getOpenAppropriations() {
        return dataManager.load(Appropriation.class)
                .query("SELECT a FROM fis_Appropriation a" +
                        " WHERE a.status = TRUE" +
                        " ORDER BY a.budgetFiscalYear DESC")
                .list();
    }

    public List<Appropriation> getBfySearchAppropriations(SessionData sessionData) {
        Appropriation entryBfy = getBfyEntryAppropriation(sessionData);
        List<Appropriation> searchBfys = (List<Appropriation>) sessionData.getAttribute("bfyPicker");
        if (searchBfys != null && searchBfys.size() > 0) {
            // ToDo: code Comparator class for here and in MainScreen.java
            searchBfys.sort((o1, o2) -> {
                String year1 = o1.getBudgetFiscalYear();
                String year2 = o2.getBudgetFiscalYear();
                if (year1 == null) {
                    return 1;
                } else if (year2 == null) {
                    return -1;
                }
                return year2.compareTo(year1);
            });
            return searchBfys;
        } else {
            List<Appropriation> entryCollection = new ArrayList<>();
            entryCollection.add(entryBfy);
            return entryCollection;
        }
    }

    public List<Appropriation> getBfyFilterField(SessionData sessionData) {
        Appropriation entryBfy = (Appropriation) sessionData.getAttribute("bfyEntry");
        // ToDo - unchecked cast here; should check type and throw exception (it'll never happen)
        List<Appropriation> searchBfys = (List<Appropriation>) sessionData.getAttribute("bfyPicker");
        if (searchBfys != null && searchBfys.size() > 0) {
            // ToDo: code Comparator class for here and in MainScreen.java
            searchBfys.sort((o1, o2) -> {
                String year1 = o1.getBudgetFiscalYear();
                String year2 = o2.getBudgetFiscalYear();
                if (year1 == null) {
                    return 1;
                } else if (year2 == null) {
                    return -1;
                }
                return year2.compareTo(year1);
            });
            return searchBfys;
        } else {
            List<Appropriation> entryCollection = new ArrayList<>();
            entryCollection.add(entryBfy);
            return entryCollection;
        }
    }

    public Date lastDayOfFiscalYear() {
        String cfy = getCurrentBfy();
        try {
            return new SimpleDateFormat("MM/dd/yyyy").parse("9/30/".concat(cfy));
        } catch (ParseException ex) {
            throw new RuntimeException("AppropriationService: unable to parse date");
        }
    }

    public String getCurrentBfy() {
        Date today = new Date();
        String thisYear = new SimpleDateFormat("yyyy").format(today);
        int thisYearInt = Integer.parseInt(new SimpleDateFormat("yyyy").format(today));

        try {
            Date firstOfCalendarYear = new SimpleDateFormat("MM/dd/yyyy").parse("1/1/" + thisYear);
            Date firstOfFiscalYear = new SimpleDateFormat("MM/dd/yyyy").parse("10/1/" + thisYear);
            if ((firstOfCalendarYear.compareTo(today)) * (today.compareTo(firstOfFiscalYear)) >= 0) {
                return thisYear;
            } else {
                return String.valueOf(thisYearInt + 1);
            }
        } catch (ParseException ex) {
            throw new RuntimeException("AppropriationService: unable to parse date");
        }
    }

    public String getLimitBfy() {
        // used to limit the number of fiscal years available to staff
        int year = Year.now().getValue() - 7;
        return Integer.toString(year);
    }

    public Appropriation getCurrentBudgetFiscalYear() {
        return dataManager.load(Appropriation.class)
                .query("select a from fis_Appropriation a" +
                        " where a.budgetFiscalYear = :bFy")
                .parameter("bFy", getCurrentBfy())
                .optional().orElse(null);
    }

    /**
     * get Appropriation for previous fiscal year
     *
     * @param appropriation starting fiscal year entity
     * @return previous fiscal year Appropriation
     */
    public Appropriation getPreviousFiscalYear(Appropriation appropriation) {
        int priorFiscalYear;
        try {
            priorFiscalYear = Integer.parseInt(appropriation.getBudgetFiscalYear()) - 1;
        } catch (NumberFormatException e) {
            priorFiscalYear = 0;
        }
        return dataManager.load(Appropriation.class)
                .query("select a from fis_Appropriation a" +
                        " where a.budgetFiscalYear = :priorFiscalYear")
                .parameter("priorFiscalYear", Integer.toString(priorFiscalYear))
                .optional()
                .orElse(dataManager.create(Appropriation.class));
    }

    /**
     * getReportFiscalYears provides a list of appropriations that are "open" or
     * are in the list of search years, obtained via session
     *
     * @param sessionData
     * @return List of Appropriations
     */
    public List<Appropriation> getReportFiscalYears(SessionData sessionData) {
        // ToDo - unchecked cast here; should check type and throw exception (it'll never happen)
        List<Appropriation> searchBfys = (List<Appropriation>) sessionData.getAttribute("bfyPicker");
        return dataManager.load(Appropriation.class)
                .query("SELECT a FROM fis_Appropriation a"
                        + " WHERE a.status = TRUE OR a IN :searchYears"
                        + " ORDER BY a.budgetFiscalYear DESC")
                .parameter("searchYears", searchBfys)
                .list();
    }

    /**
     * returns the first day of a given appropriation fiscal year (e.g. 10/1/2022)
     *
     * @param appropriation
     * @return java.util.Date to be used in JPQL
     */
    public Date getFirstDayOfAppropriationBfy(Appropriation appropriation) {
        Date day;
        try {
            int priorYear = Integer.parseInt(appropriation.getBudgetFiscalYear()) - 1;
            day = new SimpleDateFormat("MM/dd/yyyy").parse("10/1/" + priorYear);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("AppropriationService: unable to parse budget fiscal year");
        } catch (ParseException ex) {
            throw new RuntimeException("AppropriationService: unable to parse first day of fiscal year");
        }
        return day;
    }

    /**
     * returns the last day of a given appropriation fiscal year (e.g. 10/1/2022)
     *
     * @param appropriation
     * @return java.util.Date to be used in JPQL
     */
    public Date getLastDayOfAppropriationBfy(Appropriation appropriation) {
        Date day;
        try {
            day = new SimpleDateFormat("MM/dd/yyyy").parse("9/30/" + appropriation.getBudgetFiscalYear());
        } catch (ParseException ex) {
            throw new RuntimeException("AppropriationService: unable to parse current fiscal year");
        }
        return day;
    }

//    public BigDecimal getSpendingAuthority(Appropriation appropriation, AppropriationType appropriationType) {
//        List<KeyValueEntity> keyValueEntity = dataManager.loadValues(
//                        "SELECT a.oneYearAmount, a.twoYearAmount," +
//                                " COALESCE(SUM(adj.oneYearAmount),0), COALESCE(SUM(adj.twoYearAmount),0)" +
//                                " FROM fis_Appropriation a" +
//                                " LEFT JOIN fis_AppropriationAdjustment adj ON adj.appropriation=a" +
//                                " WHERE a = :appropriation" +
//                                " GROUP BY a.oneYearAmount, a.twoYearAmount")
//                .parameter("appropriation", appropriation)
//                .properties("oneYearAppropriation", "twoYearAppropriation", "oneYearAdjustments", "twoYearAdjustments")
//                .list();
//
//        BigDecimal spendingAuthority = BigDecimal.ZERO;
//
//        if (keyValueEntity.size() == 1) {
//            BigDecimal oneYearAppropriation = keyValueEntity.get(0).getValue("oneYearAppropriation");
//            BigDecimal oneYearAdjustments = keyValueEntity.get(0).getValue("oneYearAdjustments");
//            BigDecimal twoYearAppropriation = keyValueEntity.get(0).getValue("twoYearAppropriation");
//            BigDecimal twoYearAdjustments = keyValueEntity.get(0).getValue("twoYearAdjustments");
//            spendingAuthority = switch (appropriationType) {
//                case ONE_YEAR_FUND -> oneYearAppropriation.add(oneYearAdjustments);
//                case TWO_YEAR_FUND -> twoYearAppropriation.add(twoYearAdjustments);
//                default -> oneYearAppropriation.add(twoYearAppropriation)
//                        .add(oneYearAdjustments).add(twoYearAdjustments);
//            };
//            return spendingAuthority;
//        }
//        return spendingAuthority;
//    }

}