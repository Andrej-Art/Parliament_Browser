package data.tex;

import utility.MongoDBHandler;

/**
 * The {@code Protocol_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Protocol_TeX {
    private static MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();
    private Protocol_TeX() {}

    public static String toTeX() {
        mongoDBHandler.getDB(); // something
        return "";
    }
}
