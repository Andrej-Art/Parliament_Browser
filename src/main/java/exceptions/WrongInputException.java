package exceptions;

import java.io.IOException;

/**
 * A {@code WrongInputException} signals that
 * @author DavidJordan
 */

public class WrongInputException extends IOException {

    public WrongInputException(){
    }

    public WrongInputException(String sMessage) {
        super(sMessage);
    }
}
