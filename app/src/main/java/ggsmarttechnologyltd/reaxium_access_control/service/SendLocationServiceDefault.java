package ggsmarttechnologyltd.reaxium_access_control.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.io.Serializable;

import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.LocationObject;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 23/05/2016.
 */
public class SendLocationServiceDefault extends Service implements LocationListener {


    public static final float miles = 0;
    public static final long time = 0;
    private LocationManager locationManager;

    /**
     * BroadCastSender between this service and any activity
     */
    private LocalBroadcastManager mLocalBroadcastManager;

    /**
     * Initialize the broadcast manager
     */
    private void initBroadCast(){
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    /**
     * trace id
     */
    public static final String TAG = GGGlobalValues.TRACE_ID;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initBroadCast();
        initializeLocationService();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        removeLocationUpdates();
        super.onDestroy();
    }

    /**
     * remove the location update notification
     */
    private void removeLocationUpdates(){
        Log.i(TAG,"location updates has been removed");
        locationManager.removeUpdates(this);
    }

    /**
     *
     * @param params maps of params to be send to the activity
     * @param filter string used as Actions
     */
    private void sendCustomBroadCast(Serializable params, String filter ){
        Intent action = new Intent(filter);
        action.putExtra(GGGlobalValues.BROADCAST_PARAM,params);
        mLocalBroadcastManager.sendBroadcast(action);
    }

    /**
     * send the location to the right screen to be handle
     */
    private void notifyMyPosition(Double latitude, Double longitude) {
        Log.i(TAG, "Notificando posicion: Latitude: "+latitude+" Longitude: "+longitude);
        LocationObject locationObject = new LocationObject();
        locationObject.setLatitude(latitude);
        locationObject.setLongitude(longitude);
        GGGlobalValues.LAST_LOCATION = new LatLng(locationObject.getLatitude(),locationObject.getLongitude());
        sendCustomBroadCast(locationObject, MainActivity.LOCATION_CHANGED);
    }

    /**
     * Init location update request
     */
    private void initializeLocationService(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = null;
        Boolean isGPSEnabled =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean isNetworkEnabled =  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isGPSEnabled || isNetworkEnabled){

            if(isNetworkEnabled) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,time,miles,this);
            }
            if(isGPSEnabled){
                if(lastKnownLocation == null){
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,time,miles,this);
            }
            if(lastKnownLocation!= null){
                GGGlobalValues.LAST_LOCATION = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                Log.i(TAG,"lastKnownLocation: Latitude: "+lastKnownLocation.getLatitude()+"  Longitude: "+lastKnownLocation.getLongitude());
            }else{
                Log.i(TAG,"Unknown Last location");
            }
        }
        Log.i(TAG,"Request for location change sent");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG,"Locacion cambiada: Latitude: "+location.getLatitude()+"  Longitude: "+location.getLongitude());
        notifyMyPosition(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG,"onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG,"onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG,"onProviderDisabled");
    }
}
