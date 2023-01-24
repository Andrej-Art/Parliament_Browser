package utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;

/**
 * The {@code TimeHelper} class contains useful methods for date/time related conversions.
 * @author Eric Lakhter
 */
public class TimeHelper {
    // Used in protocols
    private static final DateTimeFormatter DATE_FORMAT_1 = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    // Used on the german Bundestag's website
    private static final DateTimeFormatter DATE_FORMAT_2 = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMANY);
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("H:mm", Locale.GERMANY);


     // Private to restrict other classes from instantiating a TimeHelper.
    private TimeHelper(){}

    /**
     * Converts a Java {@code Date} object to an equivalent LocalDate.
     * @param date Date object form the database
     * @return LocalDate of this Date
     */
    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneOffset.of("Z")).toLocalDate();
    }

    /**
     * Converts a Java {@code Date} object to an equivalent LocalTime.
     * @param date Date object form the database
     * @return LocalTime of this Date
     */
    public static LocalTime dateToLocalTime(Date date) {
        return date.toInstant().atZone(ZoneOffset.of("Z")).toLocalTime();
    }

    /**
     * Converts Date objects to a String of format {@code dd.MM.yyyy}.
     * @param date Date object form the database
     * @return String with the date, e.g. 24.07.2017
     */
    public static String mongoDateToGermanDate(Date date) {
        return date.toInstant().atZone(ZoneOffset.of("Z")).toLocalDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMANY)
                + ", " +date.toInstant().atZone(ZoneOffset.of("Z")).toLocalDate().format(DATE_FORMAT_1);
    }

    /**
     * Converts dates of the pattern {@code dd.MM.yyyy} to a LocalDate.
     * @param date Date String.
     * @return Respective {@code LocalDate}.
     * @author Eric Lakhter
     */
    public static LocalDate convertToISOdate(String date){
        return convertToISOdate(date, 1);
    }

    /**
     * Converts date Strings to a LocalDate.
     * @param date Date String.
     * @param mode Undefined modes behave like mode 1.<br>
     *             - 1 means that dates of the pattern {@code dd.MM.yyyy} get converted.<br>
     *             - 2 means that dates of the pattern {@code d. MMMM yyyy} get converted.<br>
     *             - 3 means that dates of the pattern {@code yyyy-MM-dd} get converted.
     * @return Respective {@code LocalDate}.
     * @author Eric Lakhter
     */
    public static LocalDate convertToISOdate(String date, int mode){
        switch (mode) {
            case 3:
                return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            case 2:
                return LocalDate.parse(date, DATE_FORMAT_2);
            case 1:
            default:
                return LocalDate.parse(date, DATE_FORMAT_1);
        }
    }

    /**
     * Converts times of the pattern {@code H:mm} to a LocalTime.
     * @param time Time String.
     * @return Respective {@code LocalTime}.
     * @author Eric Lakhter
     */
    public static LocalTime convertToISOtime(String time){
        return LocalTime.parse(time.replace(".", ":").replace(" Uhr", ""), CLOCK_FORMAT);
    }

    /**
     * Calculates the time between two LocalTimes.
     * <p> If the end time is equal or earlier than the start time it counts
     * the end time as if it was from the next day.
     * @param begin Start time.
     * @param end End time.
     * @return The time between {@code begin} and {@code end} in minutes.
     * @author Eric Lakhter
     */
    public static long durationBetweenTimesInMinutes(Temporal begin, Temporal end) {
        long dauer = ChronoUnit.MINUTES.between(begin, end);
        if (dauer <= 0) {  // eg. begin = 11:00; end = 09:00
            dauer += 1440; // minutes in a day
        }
        return dauer;
    }
}
