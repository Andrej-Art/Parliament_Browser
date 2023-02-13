package exceptions;

/**
 * An {@code EditorException} is thrown when data submitted by the website's protocol editor
 * has error in some way.
 *
 * @author Eric Lakhter
 */
public class EditorException extends Exception {

    /**
     * Constructs a {@code EditorException} with no detail message.
     */
    public EditorException() {
        super();
    }

    /**
     * Constructs a {@code EditorException} with the specified
     * detail message.
     *
     * @param   message   the detail message.
     */
    public EditorException(String message) {
        super(message);
    }
}
