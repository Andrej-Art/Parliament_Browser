package utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Locale;

/**
 * The {@code TimeHelper} class contains methods which convert dates of the pattern {@code dd.MM.yyyy}
 * and times of the pattern {@code H:mm} to the ISO-8601 calendar system.
 *
 * @author Eric Lakhter
 */
public class TimeHelper {
    // Used in protocols
    private static final DateTimeFormatter DATE_FORMAT_INPUT_1 = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    // Used on the german Bundestag's website
    private static final DateTimeFormatter DATE_FORMAT_INPUT_2 = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMANY);
    private static final DateTimeFormatter CLOCK_FORMAT_INPUT = DateTimeFormatter.ofPattern("H:mm", Locale.GERMANY);


     // Private to restrict other classes from instantiating a TimeHelper.
    private TimeHelper(){}

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
     *             - 2 means that dates of the pattern {@code d. MMMM yyyy} get converted.
     * @return Respective {@code LocalDate}.
     * @author Eric Lakhter
     */
    public static LocalDate convertToISOdate(String date, int mode){
        switch (mode) {
            case 2:
                return LocalDate.parse(date, DATE_FORMAT_INPUT_2);
            case 1:
            default:
                return LocalDate.parse(date, DATE_FORMAT_INPUT_1);
        }
    }

    /**
     * Converts times of the pattern {@code H:mm} to a LocalTime.
     * @param time Time String.
     * @return Respective {@code LocalTime}.
     * @author Eric Lakhter
     */
    public static LocalTime convertToISOtime(String time){
        return LocalTime.parse(time.replace(".", ":").replace(" Uhr", ""), CLOCK_FORMAT_INPUT);
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
