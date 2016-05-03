package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;

import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class InitSimpleFingerPrintThread extends Thread {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private Context mContext;
    private FingerPrintHandler fingerPrintHandler;

    public InitSimpleFingerPrintThread(Context context, FingerPrintHandler fingerPrintHandler) {
        this.mContext = context;
        this.fingerPrintHandler = fingerPrintHandler;
    }

    @Override
    public void run() {
        showProgressDialog();
        GGUtil.startFingerScannerService(mContext, fingerPrintHandler);
        dismissProgressDialog();
    }

    public void showProgressDialog(){
        ((GGMainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((GGMainActivity) mContext).showProgressDialog("Initializing FingerPrint Scanner...");
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
