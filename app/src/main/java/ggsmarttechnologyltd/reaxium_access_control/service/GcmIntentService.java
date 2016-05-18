package ggsmarttechnologyltd.reaxium_access_control.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Eduardo Luttinger
 */
public class GcmIntentService  extends IntentService {

    /**
     * Indicador de finalizacion de la carrera desde el sistema de notificaciones push
     */
    private static Boolean RIDE_IS_OVER = Boolean.FALSE;

    /**
     * Descrioncion de la clase para propositos de log
     */
    private static final String TAG = GcmIntentService.class.getName();

    /**
     * BroadCastSender between this service and any activity
     */
    private LocalBroadcastManager mLocalBroadcastManager;

    /**
     * Constructor
     */
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initBroadCast();
        handleTheNotification(intent.getExtras());
    }

    /**
     * Initialize the broadcast manager
     */
    private void initBroadCast(){
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(GcmIntentService.this);
    }

    /**
     * Handle all Push Notification Operations
     *
     * @param pushParameters (Map of parameters)
     */
    private void handleTheNotification(Bundle pushParameters) {
        try {
            Gson gson = new Gson();
//            MellevasPush mellevasPush = gson.fromJson(pushParameters.getString("message"), MellevasPush.class);
//            switch (mellevasPush.getType()){
//                case 2:
//                    RIDE_IS_OVER = Boolean.FALSE;
//                    runTheMapAsNotification(mellevasPush);
//                    break;
//                case 3:
//                    runUpdatePosition(mellevasPush);
//                    break;
//                case 4:
//                    RIDE_IS_OVER = Boolean.TRUE;
//                    runTheRatePage(mellevasPush);
//                    break;
//                case 11:
//                    if(Util.getTogglePreferences(this, getString(R.string.ride_confirmations))) {
//                        runTheConfirmation(mellevasPush);
//                    }
//                    break;
//                case 12:
//                    if(Util.getTogglePreferences(this, getString(R.string.ride_cancellations))) {
//                        runTheCancellations(mellevasPush);
//                    }
//                    break;
//                case 10:
//                    if(Util.getTogglePreferences(this, getString(R.string.admin_messages))) {
//                        Bundle bundle = new Bundle();
//                        //bundle.putSerializable(MeLlevasGlobalValues.BROADCAST_PARAM,mellevasPush);
//                        PushUtil.loadAnAppNotification(this, MainActivity.class,bundle,105,mellevasPush.getMessage());
//                        break;
//                    }
//            }

        }catch(Exception e){
            Log.e(TAG,"Error manejando la notificacion PUSH",e);
        }
    }


}