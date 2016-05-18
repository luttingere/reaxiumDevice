package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;

import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class InitCardReaderThread extends Thread {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private Context mContext;
    private ScannersActivityHandler scannersActivityHandler;
    private AutomaticCardValidationThread automaticCardValidationThread;

    public InitCardReaderThread(Context context, ScannersActivityHandler scannersActivityHandler) {
        this.mContext = context;
        this.scannersActivityHandler = scannersActivityHandler;
    }

    @Override
    public void run() {
        showProgressDialog();
        if(GGUtil.openCardReader(mContext, scannersActivityHandler)){
            automaticCardValidationThread = new AutomaticCardValidationThread(App.cardReader, scannersActivityHandler,mContext);
            automaticCardValidationThread.start();
        }
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
