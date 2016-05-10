package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.os.Handler;
import android.os.Message;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public abstract class ScannersActivityHandler extends Handler {

    public static final int UPDATE_FINGER_PRINT_IMAGE = 1000;
    public static final int SAVE_FINGER_PRINT = 1001;
    public static final int VALIDATE_SCANNER_RESULT = 1003;
    public static final int ERROR_ROUTINE = 1004;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case UPDATE_FINGER_PRINT_IMAGE:
                updateFingerPrintImageHolder((FingerprintImage) msg.obj);
                break;
            case SAVE_FINGER_PRINT:
                saveFingerPrint((FingerPrint) msg.obj);
                break;
            case VALIDATE_SCANNER_RESULT:
                validateScannerResult((AppBean) msg.obj);
                break;
            case ERROR_ROUTINE:
                errorRoutine((String)msg.obj);
                break;
        }
    }

    protected abstract void updateFingerPrintImageHolder(FingerprintImage fingerprintImage);
    protected abstract void saveFingerPrint(FingerPrint fingerPrint);
    protected abstract void validateScannerResult(AppBean bean);
    protected abstract void errorRoutine(String message);

}
