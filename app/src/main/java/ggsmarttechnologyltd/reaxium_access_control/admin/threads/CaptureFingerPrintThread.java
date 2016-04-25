package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.util.Log;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class CaptureFingerPrintThread extends Thread {
    private static final String TAG = "CaptureFinger";
    private FingerprintScanner fingerprintScanner;
    private Boolean captured = Boolean.FALSE;
    private static Boolean stop = Boolean.FALSE;
    private Result fingerCaptureResult;
    private FingerprintImage fingerprintImage;
    private FingerPrintHandler fingerPrintHandler;
    private byte[] fingerPrintFeat;
    private FingerPrint fingerPrint;

    public CaptureFingerPrintThread(FingerprintScanner fingerprintScanner, FingerPrintHandler fingerPrintHandler) {
        this.fingerprintScanner = fingerprintScanner;
        this.fingerPrintHandler = fingerPrintHandler;
    }

    @Override
    public void run() {
        while (!captured && !stop) {
            fingerCaptureResult = fingerprintScanner.capture();
            if (fingerCaptureResult.error == FingerprintScanner.RESULT_OK) {
                try {
                    captured = Boolean.TRUE;
                    // Capture fingerprint process
                    fingerprintImage = (FingerprintImage) fingerCaptureResult.data;
                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.UPDATE_FINGER_PRINT_IMAGE, fingerprintImage));

                    //get the byte information of the biometric
                    fingerCaptureResult =  App.bione.extractFeature(fingerprintImage);

                    if(fingerCaptureResult.error == Bione.RESULT_OK){

                        fingerPrintFeat = (byte[]) fingerCaptureResult.data;

                        fingerCaptureResult = App.bione.makeTemplate(fingerPrintFeat, fingerPrintFeat, fingerPrintFeat);

                        //Load a FingerPrint Object
                        fingerPrintFeat = (byte[])fingerCaptureResult.data;
                        fingerPrint = new FingerPrint();
                        fingerPrint.setFingerPrintFeature(fingerPrintFeat);
                        fingerPrint.setFingerPrintImage(GGUtil.fingerPrintImageToBitmap(fingerprintImage));

                        //save biometric in the server
                        fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.SAVE_FINGER_PRINT, fingerPrint));

                    }else{
                        fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error saving the fingerprint, error code: "+fingerCaptureResult.error));
                        stop = true;
                    }
                } catch (Exception e) {
                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error scanning the fingerprint"));
                    stop = true;
                    Log.e(TAG,"Error loading a biometric to user",e);
                }
            } else {
                stop = true;
                fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error scanning the fingerprint"));
                Log.i(TAG, "There was an error scanning the fingerprint");
            }
        }
        Log.i(TAG, "The fingerprint capture proccess has ended");
    }

    public static void stopScanner() {
        stop = Boolean.TRUE;
    }
}
