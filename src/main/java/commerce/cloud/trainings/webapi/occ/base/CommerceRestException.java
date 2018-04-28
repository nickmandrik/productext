package commerce.cloud.trainings.webapi.occ.base;


/**
 * @author Nick Mandrik
 */


public class CommerceRestException extends Exception {

    public CommerceRestException(Exception e) {
        super(e);
    }

    public CommerceRestException(String message) {
        super(message);
    }

    public CommerceRestException(String message, Exception e) {
        super(message, e);
    }
}
