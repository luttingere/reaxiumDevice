package ggsmarttechnologyltd.reaxium_access_control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import cn.com.aratek.dev.Terminal;
import cn.com.aratek.fp.Bione;
import cn.com.aratek.util.Result;

import cn.com.aratek.fp.FingerprintScanner;
import ggsmarttechnologyltd.reaxium_access_control.service.BiometricScannerService;


/**
 * Created by Eduardo Lutttinger on 11/04/2016.
 */
public class App extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private static final String TAG  = "ReaxiumDeviceApp";
    private static Context mContext;
    private boolean isDevelopment = Boolean.TRUE;
    public static FingerprintScanner fingerprintScanner;
    public static Bione bione;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getContext();
    }

    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
