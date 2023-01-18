package exceptions;

import utility.PollScraper;

/**
 * A {@code NoPollException} is used to tell the {@code PollScraper} that the poll it tries to read is not a real poll.
 * @author Eric Lakhter
 * @see PollScraper
 */
public class NoPollException extends NullPointerException {

    /**
     * Constructs a {@code NoPollException} with no detail message.
     */
    public NoPollException() {
        super();
    }

    /**
     * Constructs a {@code NoPollException} with the specified
     * detail message.
     *
     * @param   message   the detail message.
     */
    public NoPollException(String message) {
        super(message);
    }
}
