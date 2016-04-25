package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersContract;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class VerifyFingerPrintThread extends Thread {

    private static final String TAG = "ValidateFinger";
    private FingerprintScanner fingerprintScanner;
    private Boolean captured = Boolean.FALSE;
    private static Boolean stop = Boolean.FALSE;
    private Result fingerCaptureResult;
    private FingerprintImage fingerprintImage;
    private FingerPrintHandler fingerPrintHandler;
    private byte[] fingerPrintFeat;
    private List<Integer> listOfUsersRegistered;
    private Context mContext;

    public VerifyFingerPrintThread(FingerprintScanner fingerprintScanner, FingerPrintHandler fingerPrintHandler, Context context) {
        this.fingerprintScanner = fingerprintScanner;
        this.fingerPrintHandler = fingerPrintHandler;
        this.mContext = context;
        stop = Boolean.FALSE;
    }

    @Override
    public void run() {
        while (!captured && !stop) {
            try {
                fingerCaptureResult = fingerprintScanner.capture();
                if (fingerCaptureResult.error == FingerprintScanner.RESULT_OK) {
                    try {
                        captured = Boolean.TRUE;

                        // Capture fingerprint process
                        fingerprintImage = (FingerprintImage) fingerCaptureResult.data;

                        //get the byte information of the biometric
                        fingerCaptureResult = App.bione.extractFeature(fingerprintImage);

                        if (fingerCaptureResult.error == Bione.RESULT_OK) {

                            //Load a FingerPrint Object
                            fingerPrintFeat = (byte[]) fingerCaptureResult.data;
                            int userId = 0;
                            listOfUsersRegistered = ReaxiumUsersDAO.getInstance(mContext).getUsersWithBiometric();
                            if(listOfUsersRegistered != null){
                                for (Integer id : listOfUsersRegistered) {
                                    Log.i(TAG,"Verifying biometric with user id: "+id);
                                    fingerCaptureResult = App.bione.verify(id, fingerPrintFeat);
                                    if (((Boolean) fingerCaptureResult.data)) {
                                        userId = id;
                                        break;
                                    }
                                }
                                if(userId != 0){
                                    //save biometric in the server
                                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.VALIDATE_FINGER_PRINT, userId));
                                }else{
                                    stop = true;
                                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "No user found."));
                                }
                            }else{
                                stop = true;
                                fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "No users registered in this device."));
                            }
                        } else {
                            fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error saving the fingerprint, error code: " + fingerCaptureResult.error));
                            Log.e(TAG, "Error loading a biometric to user");
                            stop = true;
                        }
                    } catch (Exception e) {
                        stop = true;
                        fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error scanning the fingerprint: "+e.getMessage()));
                        Log.e(TAG, "Error loading a biometric to user", e);
                    }
                } else {
                    stop = true;
                    fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Cannot capture the biometric, error code: "+fingerCaptureResult.error));
                    Log.i(TAG, "There was an error scanning the fingerprint");
                }
            }catch (Exception e){
                Log.i(TAG,"Error verifying the biometric",e);
                fingerPrintHandler.sendMessage(fingerPrintHandler.obtainMessage(FingerPrintHandler.ERROR_ROUTINE, "Error scanning the fingerprint"));
                stop = true;
            }
        }
        Log.i(TAG, "The fingerprint capture proccess has ended");
    }

    public static void stopScanner() {
        stop = Boolean.TRUE;
    }
}
