package ggsmarttechnologyltd.reaxium_access_control.admin.threads;

import android.content.Context;

import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class InitScannersInAutoModeThread extends Thread {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private Context mContext;
    private ScannersActivityHandler scannersHandler;
    private ScannersActivityHandler cardReaderHandler;
    private AutomaticFingerPrintValidationThread automaticFingerPrintValidationThread;
    private AutomaticCardValidationThread automaticCardValidationThread;


    public InitScannersInAutoModeThread(Context context, ScannersActivityHandler scannersHandler) {
        this.mContext = context;
        this.scannersHandler = scannersHandler;
    }

    @Override
    public void run() {
        showProgressDialog();
        if(GGUtil.startFingerScannerService(mContext, scannersHandler)){
            automaticFingerPrintValidationThread = new AutomaticFingerPrintValidationThread(App.fingerprintScanner, scannersHandler,mContext);
            automaticFingerPrintValidationThread.start();
        }
//         if(GGUtil.openCardReader(mContext,scannersHandler)){
//             automaticCardValidationThread = new AutomaticCardValidationThread(App.cardReader,scannersHandler,mContext);
//             automaticCardValidationThread.start();
//         }

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
