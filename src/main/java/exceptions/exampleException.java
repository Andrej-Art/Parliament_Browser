/**
 * Quelle Uebung 1 aus dem WS 21/22
 * 
 */
//Klasse exception vollständig übernommen.
package exceptions;

/**
 * This is an example of an exception as an example for further Usage.
 *
 * @author Giuseppe Abrami
 * 
 * @modifier Julian Ocker 
 */
public class exampleException extends Exception {

    public exampleException() {
    }

    public exampleException(Throwable pCause) {
        super(pCause);
    }

    public exampleException(String pMessage) {
        super(pMessage);
    }

    public exampleException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

}
/**
 * Quelle ende
 *
 */