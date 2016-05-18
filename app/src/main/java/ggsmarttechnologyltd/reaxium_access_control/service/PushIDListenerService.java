package ggsmarttechnologyltd.reaxium_access_control.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Eduardo Luttinger on 2/2/2016.
 */
public class PushIDListenerService extends InstanceIDListenerService {
    private static final String TAG = PushIDListenerService.class.getName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this,PushRegistrationService.class);
        startService(intent);
    }
}
