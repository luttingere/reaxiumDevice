package ggsmarttechnologyltd.reaxium_access_control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


import cn.com.aratek.dev.Terminal;
import cn.com.aratek.fp.Bione;
import cn.com.aratek.iccard.ICCardReader;


import cn.com.aratek.fp.FingerprintScanner;

import com.crashlytics.android.Crashlytics;

import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumDbHelper;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.service.PushUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Eduardo Luttinger on 11/04/2016.
 */
public class App extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    public static final String APP_NAME = "Reaxium School Bus Solution";
    public static final String APP_VERSION = "2.0 QA";
    private static final String TAG  = GGGlobalValues.TRACE_ID;
    private static Context mContext;
    private boolean isDevelopment = Boolean.TRUE;
    public static FingerprintScanner fingerprintScanner;
    public static Bione bione;
    public static ICCardReader cardReader;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ReaxiumDbHelper reaxiumDbHelper;

    @Override
    public void onCreate() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = getContext();
        registerPushNotification();
        registerActivityLifecycleCallbacks(this);
        registerDeviceSerialNumber();
        logUser();
        initTables();
    }

    public static Context getContext() {
        return mContext;
    }


    private void registerPushNotification(){
        Log.i(TAG,"registering for push notifications");
        if (PushUtil.checkGooglePlayServices(this)) {
            Log.i(TAG,"Google play is working fine");
            if(!PushUtil.isTheDeviceRegisteredForPushNotification(this)){
                PushUtil.registerInBackGround(this);
            }else{
                String registrationID = PushUtil.getRegistrationId(this);
                Log.i(TAG,"registration ID: "+registrationID);
            }
        }else{
            Log.e(TAG,"Google play its not working on the device");
        }
    }

    private void registerDeviceSerialNumber(){
        Log.i(TAG,"Device Serial Number:"+ Terminal.getSN());
        sharedPreferenceUtil.saveString(GGGlobalValues.DEVICE_SERIAL,Terminal.getSN());
    }

    private void logUser() {
        Crashlytics.setUserIdentifier("19044081");
        Crashlytics.setUserEmail("luttingere@gmail.com");
        Crashlytics.setUserName("Eduardo Luttinger");
    }

    private void initTables(){
        reaxiumDbHelper = ReaxiumDbHelper.getInstance(this);
        SQLiteDatabase database = reaxiumDbHelper.getWritableDatabase();
        database.close();
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
