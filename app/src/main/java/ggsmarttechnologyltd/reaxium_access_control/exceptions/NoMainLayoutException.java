package ggsmarttechnologyltd.reaxium_access_control.exceptions;

/**
 * Created by Eduardo Luttinger on 27/07/2015.
 *
 * the system will throw this exception if an activity do not have a layout configured
 *
 */
public class NoMainLayoutException extends Exception {

    public NoMainLayoutException(String detailMessage) {
        super(detailMessage);
    }

    public NoMainLayoutException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
