package data;

import data.impl.Poll_Impl;

/**
 * The {@code Poll} interface.
 *
 * @see Poll_Impl
 * @author Eric Lakhter
 */
public interface Poll {
    /**
     * Converts all fields into a JSON String compatible with {@code org.bson.Document.parse()},
     * formatted to fit the {@code poll} collection.
     *
     * @return JSON String with all poll results.
     * @author Eric Lakhter
     */
    String toJson();
}
