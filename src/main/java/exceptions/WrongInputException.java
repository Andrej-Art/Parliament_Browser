package exceptions;

import java.io.IOException;

/**
 * A {@code WrongInputException} signals that a parameter passed to a method or constructor was faulty in some way.
 * @author DavidJordan
 */
public class WrongInputException extends IOException {

    /**
     * Constructs a {@code WrongInputException} with {@code null}
     * as its error detail message.
     * @author DavidJordan
     */
    public WrongInputException(){
    }

    /**
     * Constructs a {@code WrongInputException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     * @author DavidJordan
     */
    public WrongInputException(String message) {
        super(message);
    }
}
