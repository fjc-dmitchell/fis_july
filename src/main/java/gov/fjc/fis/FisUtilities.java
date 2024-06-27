package gov.fjc.fis;

import gov.fjc.fis.entity.OutputType;
import io.jmix.reports.entity.ReportOutputType;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNullElse;

public final class FisUtilities {

    private FisUtilities() {
        // don't allow this class to be instantiated
    }

    public static String getCreatedModifiedString(String createdBy, OffsetDateTime createdDate,
                                                  String lastModifiedBy, OffsetDateTime lastModifiedDate) {
        String createdByStr = "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("M/d/yyyy");

        if (createdDate != null) {
            createdByStr += "Created on " + f.format(createdDate);
            if (createdBy != null) {
                createdByStr += " by " + createdBy;
            }
            createdByStr += ". ";
        }

        if (lastModifiedDate != null && lastModifiedBy != null) {
            createdByStr += "Last modified on " + f.format(lastModifiedDate);
            createdByStr += " by " + lastModifiedBy;
            createdByStr += ". ";
        }

        if (createdByStr.isEmpty()) {
            return null;
        } else {
            return createdByStr;
        }
    }

    public static String getAoSyncDateString(Date aoSendDate) {
        SimpleDateFormat f = new SimpleDateFormat("M/d/yyyy");
        Date cutoff;
        try {
            cutoff = f.parse("10/1/2020");
        } catch (ParseException e) {

            throw new RuntimeException("Unable to parse date in getAoSendDateString");
        }
        if (aoSendDate == null) {
            return "";
        }
        if (aoSendDate.compareTo(cutoff) < 0) {
            return "Sent to AO on " + f.format(aoSendDate);
        } else {
            return "Received from AO on " + f.format(aoSendDate);
        }
    }

    public static String getLoadedByString(String loadedBy, OffsetDateTime loadDate) {
        String loadedByStr = "";
        SimpleDateFormat f = new SimpleDateFormat("M/d/yy");

        if (loadDate != null) {
            loadedByStr += "Loaded on " + f.format(loadDate);
            if (loadedBy != null) {
                loadedByStr += " by " + loadedBy;
            }
            loadedByStr += ".";
        }

        return loadedByStr.isEmpty() ? "" : loadedByStr;
    }

    /**
     * Jmix 2.x removed @CaseConversion annotation. Use this method until they replace it.
     *
     * @param string
     * @return String
     */
    public static String toUpperNullAllowed(String string) {
        return string == null ? string : string.toUpperCase();
    }

    /**
     * Computes total even if nulls passed. Required because user can leave amounts empty in user interface.
     *
     * @param firstAmount
     * @param secondAmount
     * @return BigDecimal
     */
    public static BigDecimal getTotalNullAllowed(BigDecimal firstAmount, BigDecimal secondAmount) {
        return requireNonNullElse(firstAmount, BigDecimal.ZERO).add(requireNonNullElse(secondAmount, BigDecimal.ZERO));
    }

    public static String getDateTimeReportString(LocalDateTime dateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");
        return dtf.format(dateTime);
    }

    public static String getDateTimeFilenameString(LocalDateTime dateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm a");
        return dtf.format(dateTime);
    }

    public static LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }

    public static ReportOutputType getReportOutputType(OutputType outputType) {
        if ((outputType == null) || outputType.equals(OutputType.PDF)) {
            return ReportOutputType.PDF;
        } else {
            return ReportOutputType.XLSX;
        }
    }

    public static String getLocation(String city, String state) {
        if (city == null) {
            city = state;
        } else {
            if (state != null) {
                city = city.concat(", ").concat(state);
            }
        }
        return city;
    }

//    public static boolean nonZero(BigDecimal... params) {
//        for (BigDecimal p : params) {
//            if (p != null && !p.equals(BigDecimal.ZERO)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * Determine presence of non-zero, non-null BigDecimal parameter
     * @param decimals one or more BigDecimal parameters, null values allowed but ignored
     * @return boolean representing presence of a negative or positive BigDecimal value
     */
    public static boolean nonZero(BigDecimal... decimals) {
        return Arrays.stream(decimals).filter(Objects::nonNull).anyMatch(bd -> bd.signum() != 0);
    }

    /**
     * calculates today less number of days, without time.
     *
     * @param days number of days to subtract from today
     * @return java.util.Date
     */
    public static Date getCurrentDateMinusDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -days);

        return calendar.getTime();
    }

    public static int getNumberOfDaysFromToday(Date date) {
        if (date == null) {
            return 0;
        }
        long diffInMillies = Math.abs(getCurrentDateMinusDays(0).getTime() - date.getTime());
        return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}
