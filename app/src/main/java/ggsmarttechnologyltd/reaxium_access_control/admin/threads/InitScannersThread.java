package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class InitScannersThread extends Thread {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private Context mContext;
    private ScannersActivityHandler biometricHandler;
    private ScannersActivityHandler rfidHandler;

    public InitScannersThread(Context context, ScannersActivityHandler biometricHandler, ScannersActivityHandler rfidHandler) {
        this.mContext = context;
        this.biometricHandler = biometricHandler;
        this.rfidHandler = rfidHandler;
    }

    @Override
    public void run() {
        showProgressDialog();
        GGUtil.startFingerScannerService(mContext, biometricHandler);
        //GGUtil.openCardReader(mContext,rfidHandler);
        dismissProgressDialog();
    }

    public void showProgressDialog(){
        ((GGMainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((GGMainActivity) mContext).showProgressDialog("Initializing Scanners...");
            }
        });
    }

    public void dismissProgressDialog(){
        ((GGMainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((GGMainActivity)mContext).dismissProgressDialog();
            }
        });
    }
}
