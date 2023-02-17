package data;

import data.impl.Poll_Impl;

import java.time.LocalDate;

/**
 * The {@code Poll} interface.
 *
 * @see Poll_Impl
 * @author Eric Lakhter
 */
public interface Poll {
    /**
     * Gets the date of the poll.
     * @return LocalDate of the poll.
     * @author Eric Lakhter
     */
    LocalDate getDate();

    /**
     * Converts all fields (except {@code date}) into a JSON String compatible with {@code org.bson.Document.parse()},
     * formatted to fit the {@code poll} collection.
     *
     * @return JSON String with all poll results.
     * @author Eric Lakhter
     */
    String toJson();
}
