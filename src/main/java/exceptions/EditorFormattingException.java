package exceptions;

/**
 * An {@code EditorFormattingException} is thrown when a text submitted
 * through the webpage would throw an error while being parsed.
 *
 * @author Eric Lakhter
 */
public class EditorFormattingException extends Exception {

    /**
     * Constructs a {@code EditorFormattingException} with no detail message.
     */
    public EditorFormattingException() {
        super();
    }

    /**
     * Constructs a {@code EditorFormattingException} with the specified
     * detail message.
     *
     * @param   message   the detail message.
     */
    public EditorFormattingException(String message) {
        super(message);
    }
}
