package ggsmarttechnologyltd.reaxium_access_control.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintScanner;

/**
 * Created by Eduardo Luttinger on 15/04/2016.
 */
public class BiometricScannerService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFingerPrint();
        return START_STICKY;
    }


    private void initFingerPrint(){

    }

}

