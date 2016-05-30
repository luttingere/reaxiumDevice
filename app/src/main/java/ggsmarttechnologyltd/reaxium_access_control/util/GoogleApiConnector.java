package ggsmarttechnologyltd.reaxium_access_control.util;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Eduardo Luttinger on 23/05/2016.
 */
public class GoogleApiConnector {

    private static GoogleApiClient googleApiClient;
    private static GoogleApiConnector googleApiConnector;

    private GoogleApiConnector(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }

    public static GoogleApiConnector getInstance(Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        if(googleApiConnector == null){
            googleApiConnector = new GoogleApiConnector(context,connectionCallbacks,onConnectionFailedListener);
        }
        return googleApiConnector;
    }

    public GoogleApiClient getClient(){
        return googleApiClient;
    }


}
