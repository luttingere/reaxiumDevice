package ggsmarttechnologyltd.reaxium_access_control.exceptions;

/**
 * Created by Eduardo Luttinger on 16/11/2015.
 */
public class NoContextSelectedForShareDialog extends Exception {

    public NoContextSelectedForShareDialog(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoContextSelectedForShareDialog(String detailMessage) {
        super(detailMessage);
    }
}
