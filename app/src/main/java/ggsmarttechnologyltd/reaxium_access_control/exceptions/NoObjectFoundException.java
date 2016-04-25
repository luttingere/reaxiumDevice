package ggsmarttechnologyltd.reaxium_access_control.exceptions;

/**
 * Created by Eduardo Luttinger on 03/08/2015.
 *
 *  The system will throw this exception if a expected object in an utility method is null
 *
 */
public class NoObjectFoundException extends Exception {

    public NoObjectFoundException(String detailMessage) {
        super(detailMessage);
    }

    public NoObjectFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
