package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.SuccessfulAccessPlayerSingleton;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class AutomaticFingerPrintValidationThread extends Thread {

    private static final String TAG = "AutomaticValidation";
    private FingerprintScanner fingerprintScanner;
    private static Boolean stop = Boolean.FALSE;
    private Result fingerCaptureResult;
    private FingerprintImage fingerprintImage;
    private FingerPrintHandler fingerPrintHandler;
    private byte[] fingerPrintFeat;
    private List<Integer> listOfUsersRegistered;
    private Context mContext;
    private Boolean capturedSuccessFully = Boolean.FALSE;

    public AutomaticFingerPrintValidationThread(FingerprintScanner fingerprintScanner, FingerPrintHandler fingerPrintHandler, Context context) {
        this.fingerprintScanner = fingerprintScanner;
        this.fingerPrintHandler = fingerPrintHandler;
        this.mContext = context;
        stop = Boolean.FALSE;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                if(capturedSuccessFully){
                    sleep(1000);
                    capturedSuccessFully = Boolean.FALSE;
                }
                //validate if there is a finger in the hole
                fingerCaptureResult = fingerprintScanner.hasFinger();

                //is every thing ok? move forward
                if (fingerCaptureResult.error == FingerprintScanner.RESULT_OK) {

                    //snap the finger
                    fingerCaptureResult = fingerprintScanner.capture();

                    //is every thing ok? Come on!!
                    if(fingerCaptureResult.error == FingerprintScanner.RESULT_OK){

                        showProgressDialog();

                        // withdraw the bytes of the captured image
                        fingerprintImage = (FingerprintImage) fingerCaptureResult.data;

                        //extract the important part of the bytes in an object
                        fingerCaptureResult = App.bione.extractFeature(fingerprintImage);

                        //is every thing ok? let's go
                        if (fingerCaptureResult.error == Bione.RESULT_OK) {

                            //get from the fingerResultObject the byte array
                            fingerPrintFeat = (byte[]) fingerCaptureResult.data;

                            //get from the local database all users registered with biometrical information
                            int userId = 0;
                            listOfUsersRegistered = ReaxiumUsersDAO.getInstance(mContext).getUsersWithBiometric();

                            //did you find some users right? keep moving
                            if (listOfUsersRegistered != null) {

                                //lookup between all users with biometric information in system
                                // compare their biometric against the current captured
                                for (Integer id : listOfUsersRegistered) {

                                    fingerCaptureResult = App.bione.verify(id, fingerPrintFeat);

                                    //MATCH???
                                    if (((Boolean) fingerCaptureResult.data)) {
                                        //keep this magic id for a moment xD
                                        userId = id;
                                        break;
                                    }
                                }
                                //if the user id is zero means that the validation not found any biometric that matched with the just captured
                                if (userId != 0) {

                                    capturedSuccessFully = Boolean.TRUE;

                                    dismissProgressDialog();

                                    SuccessfulAccessPlayerSingleton.getInstance(mContext).initRingTone();
                                    //save biometric in the server
                                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.VALIDATE_FINGER_PRINT, userId));

                                } else {
                                    dismissProgressDialog();
                                    FailureAccessPlayerSingleton.getInstance(mContext).initRingTone();
                                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "No user found."));
                                }
                            } else {
                                dismissProgressDialog();
                                 FailureAccessPlayerSingleton.getInstance(mContext).initRingTone();
                              //  fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "No users registered in this device."));
                            }
                        } else {
                            dismissProgressDialog();
                            //FailureAccessPlayerSingleton.getInstance(mContext).initRingTone();
                            //fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error with the image captured, error code" + fingerCaptureResult.error));
                        }
                    }else{
                        dismissProgressDialog();
                        ((AdminActivity)mContext).dismissProgressDialog();
                        Log.i(TAG,"validate whats happened if the biometric miss the finger after validate one on the hole");
                    }
                }
            } catch (Exception e) {
                dismissProgressDialog();
                stop = true;
                Log.i(TAG, "Error checking the biometric", e);
                fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Internal Error, Contact Reaxium Support"));
            }
        }
        Log.i(TAG, "The fingerprint capture proccess has ended");
    }

    private void showProgressDialog(){
        ((AdminActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AdminActivity) mContext).showProgressDialog("Processing Fingerprint");
            }
        });
    }
    private void dismissProgressDialog(){
        ((AdminActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AdminActivity) mContext).dismissProgressDialog();
            }
        });
    }

    public static void stopScanner() {
        stop = Boolean.TRUE;
    }
}
