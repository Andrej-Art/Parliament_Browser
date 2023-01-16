package exceptions;

public class WrongInputException extends Exception{

    public WrongInputException(){
    }

    public WrongInputException(String sMessage) { super(sMessage);}

}
