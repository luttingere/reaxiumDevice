package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class InitFingerPrintThread extends Thread {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private Context mContext;
    private FingerPrintHandler fingerPrintHandler;
    private AutomaticFingerPrintValidationThread automaticFingerPrintValidationThread;

    public InitFingerPrintThread(Context context,FingerPrintHandler fingerPrintHandler) {
        this.mContext = context;
        this.fingerPrintHandler = fingerPrintHandler;
    }

    @Override
    public void run() {
        showProgressDialog();
        if(GGUtil.startFingerScannerService(mContext, fingerPrintHandler)){
            automaticFingerPrintValidationThread = new AutomaticFingerPrintValidationThread(App.fingerprintScanner,fingerPrintHandler,mContext);
            automaticFingerPrintValidationThread.start();
        }
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
