package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;
import android.util.Log;

import cn.com.aratek.iccard.ICCardReader;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SuccessfulAccessPlayerSingleton;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class AutomaticCardValidationThread extends Thread {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private static Boolean stop = Boolean.FALSE;
    private Result cardReaderResult;
    private ScannersActivityHandler scannersActivityHandler;
    private Context mContext;
    private Boolean cardReadedSuccessfully = Boolean.FALSE;
    private ICCardReader cardReader;
    private SecurityObject securityObject;

    public AutomaticCardValidationThread(ICCardReader cardReader, ScannersActivityHandler scannersActivityHandler, Context context) {
        this.cardReader = cardReader;
        this.scannersActivityHandler = scannersActivityHandler;
        this.mContext = context;
        stop = Boolean.FALSE;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                if (cardReadedSuccessfully) {
                    sleep(1000);
                    cardReadedSuccessfully = Boolean.FALSE;
                }
                securityObject = GGUtil.scanRFID(cardReader);
                if (securityObject != null) {
                    if(securityObject.getUserId() != 0){
                        cardReadedSuccessfully = Boolean.TRUE;
                        SuccessfulAccessPlayerSingleton.getInstance(mContext).initRingTone();
                        scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.VALIDATE_SCANNER_RESULT, securityObject));
                    }else{
                        FailureAccessPlayerSingleton.getInstance(mContext).initRingTone();
                        scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "This card are not configured"));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG,"",e);
            }
        }
        Log.i(TAG, "The fingerprint capture proccess has ended");
    }

    private void showProgressDialog() {
        ((AdminActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AdminActivity) mContext).showProgressDialog("Processing Fingerprint");
            }
        });
    }

    private void dismissProgressDialog() {
        ((AdminActivity) mContext).runOnUiThread(new Runnable() {
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
