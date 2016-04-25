package ggsmarttechnologyltd.reaxium_access_control;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
public class App extends MultiDexApplication {

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




}
