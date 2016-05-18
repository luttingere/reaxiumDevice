package ggsmarttechnologyltd.reaxium_access_control.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;


/**
 * Created by Eduardo Luttinger on 05/02/2016.
 */
public class SendLocationService extends Service implements LocationListener {

    /**
     * tag for log proposals
     */
    private static final String TAG = GGGlobalValues.TRACE_ID;

    /**
     * 1 second in milliseconds
     */
    private final static int SECOND = 1000;

    /**
     * The minimum distance to change Updates in meters
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 5 * SECOND;

    /**
     * location manager
     */
    private LocationManager locationManager;

    /**
     * flag to know if the GPS of the device is enabled
     */
    private Boolean isGPSEnabled;

    /**
     * flag to knwo if the network of the device is enabled
     */
    private Boolean isNetworkEnabled;

    /**
     * flag to know if the proccess could get the location of the device
     */
    private Boolean canGetLocation;

    /**
     * Location Object (Latitude and Longitude)
     */
    private Location location;

    /**
     * latitude value
     */
    private double latitude = 0; // latitude

    /**
     * longitude value
     */
    private double longitude = 0; // longitude



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Se inicia el servicio de notificacion de ubicacion");
        Location location = getLocation();
        Log.i(TAG,"lOCATION"+location);
        if(location != null){
            notifyMyPosition(location.getLatitude(), location.getLongitude());
        }
        return START_STICKY;
    }

    /**
     * Envia la ubicacion al driver
     */
    private void notifyMyPosition(Double latitude, Double longitude) {
        Log.i(TAG, "Notificando posicion: Latitude: "+latitude+" Longitude: "+longitude);
    }




    /**
     * Find the last location knew by the device
     *
     * @return
     */
    public Location getLocation() {
        try {
                //location manager initialize
                locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled. DEFAULT COORDINATES
                    Log.i(TAG,"no network provider enabled");

                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        Log.i("LOCATION", "Latitude: " + latitude + "- Longitude: " + longitude);
        return location;
    }


    @Override
    public void onLocationChanged(Location location) {
       notifyMyPosition(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Status changed: Provider: " + provider + " , status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider disabled: " + provider);
    }
}
