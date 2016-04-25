package ggsmarttechnologyltd.reaxium_access_control.splash;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.aratek.dev.Terminal;
import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.login.activity.LoginActivity;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger G&G on 11/04/2016.
 */
public class ReaxiumSplash extends GGMainActivity {

    /**
     *
     */
    private static final long SPLASH_SCREEN_DELAY = 2000;

    @Override
    protected Integer getMainLayout() {
        return R.layout.splash_activity;
    }

    @Override
    protected void setViews() {
    }

    @Override
    protected void setViewsEvents() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFingerScannerService();
    }

    @Override
    protected GGMainFragment getMainFragment() {
        return null;
    }

    @Override
    protected Integer getMainToolbarId() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void finishTheSplash() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent goToLoginPage = new Intent(ReaxiumSplash.this, LoginActivity.class);
                startActivity(goToLoginPage);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

    /**
     * Init the finger scanner
     */
    private void startFingerScannerService(){
        App.fingerprintScanner = FingerprintScanner.getInstance();
        App.fingerprintScanner.unInitialize();
        int error;
        if ((error = App.fingerprintScanner.initialize()) == FingerprintScanner.RESULT_OK) {
            Log.i(TAG,"FingerPrint Prepare code: "+App.fingerprintScanner.prepare());
            Log.i(TAG, "Inicializacion del FingerPrint Correcta");
            Result res = App.fingerprintScanner.getSN();

            Log.i(TAG, "FingerPrint Serial Number: " + (String) res.data);

            res = App.fingerprintScanner.getFirmwareVersion();
            Log.i(TAG, "FingerPrint Firmware Number: " + (String) res.data);

            Log.i(TAG, "FingerPrint SDK Number: " + Terminal.getSdkVersion());
        }else{
            Log.e(TAG,"***[*.*]*** Error initializing FingerPrint Scanner error code: "+error);
        }
        if((error = App.bione.initialize(this, GGGlobalValues.BION_DB_PATH)) == Bione.RESULT_OK){
            Log.i(TAG, "Bione version: "+Integer.toHexString(Bione.getVersion()));
        }else{
            Log.e(TAG,"Error initializing Bion algorith error code: "+error);
        }
        finishTheSplash();
    }
}
