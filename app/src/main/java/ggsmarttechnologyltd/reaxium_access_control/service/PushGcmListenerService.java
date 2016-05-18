package ggsmarttechnologyltd.reaxium_access_control.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Eduardo Luttinger on 2/2/2016.
 */
public class PushGcmListenerService extends GcmListenerService {
    private static final String TAG = "PushGcmListener";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG, "Push notification received: " + from);
        Log.i(TAG, "Push notification Data: " + data.toString());
        if (from.startsWith("/topics/")) {

        } else {
            handleDefaultMessage(data);
        }
    }

    /**
     *
     * @param data
     */
    private void handleDefaultMessage(Bundle data){
        Intent gcmServiceIntent = new Intent(this,GcmIntentService.class);
        gcmServiceIntent.putExtras(data);
        startService(gcmServiceIntent);
    }
}
