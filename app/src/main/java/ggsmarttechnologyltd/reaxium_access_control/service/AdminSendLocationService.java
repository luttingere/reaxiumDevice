package ggsmarttechnologyltd.reaxium_access_control.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.LocationObject;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.GoogleApiConnector;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;


/**
 * Created by Eduardo Luttinger on 05/02/2016.
 */
public class AdminSendLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
    private static final long FASTED_RATE_TIME_BW_UPDATES =  500;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1 * SECOND;

    /**
     * Location Object (Latitude and Longitude)
     */
    private Location location;


    /**
     * Google api connector
     */
    private GoogleApiConnector googleConnector;

    /**
     * BroadCastSender between this service and any activity
     */
    private LocalBroadcastManager mLocalBroadcastManager;

    private SharedPreferenceUtil sharedPreferenceUtil;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Initialize the broadcast manager
     */
    private void initBroadCast(){
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(AdminSendLocationService.this);
    }

    /**
     * Initialize my custom GoogleApiConnector
     */
    private void initGoogleConnector(){
        googleConnector = GoogleApiConnector.getInstance(this,this,this);
        googleConnector.getClient().connect();
    }

    /**
     * Begin the
     */
    private void initGetLocationProcess(){
        location = LocationServices.FusedLocationApi.getLastLocation(googleConnector.getClient());
        if(location != null){
            GGGlobalValues.LAST_LOCATION = new LatLng(location.getLatitude(),location.getLongitude());
        }
        startLocationUpdates();
    }

    /**
     * stop location updates
     */
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleConnector.getClient(), this);
    }

    /**
     * start location update notifications
     */
    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleConnector.getClient(),getLocationRequest(),this);
    }

    /**
     * create and return a location request for location change notifications
     */
    protected LocationRequest getLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);
        mLocationRequest.setFastestInterval(FASTED_RATE_TIME_BW_UPDATES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
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

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        googleConnector.getClient().disconnect();
        Log.i(TAG,"Google Api Connection has been stopped");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Se inicia el servicio de notificacion de ubicacion");
        initBroadCast();
        initGoogleConnector();
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(this);
        return START_STICKY;
    }

    /**
     * Envia la ubicacion al driver
     */
    private void notifyMyPosition(Double latitude, Double longitude) {
        Log.i(TAG, "Notificando posicion: Latitude: "+latitude+" Longitude: "+longitude);
        GGGlobalValues.LAST_LOCATION = new LatLng(latitude,longitude);
        LocationObject locationObject = new LocationObject();
        locationObject.setLatitude(latitude);
        locationObject.setLongitude(longitude);
        notifyMyPositionInServer(latitude,longitude);
        GGUtil.showAShortToast(this,"Device Location sent successfully");
     //   sendCustomBroadCast(locationObject, AdminActivity.LOCATION_CHANGED);
    }

    private void notifyMyPositionInServer(Double latitude, Double longitude){
        if (GGUtil.isNetworkAvailable(this)) {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Type responseType = new TypeToken<ApiResponse<Object>>() {}.getType();
                    ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() != GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        GGUtil.showAShortToast(AdminSendLocationService.this, apiResponse.getReaxiumResponse().getMessage());
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    GGUtil.showAShortToast(AdminSendLocationService.this, "Bad connection with reaxium server");
                }
            };
            Log.i(TAG,"SENDING DEVICE LOCATION");
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.NOTIFY_POSITION), loadNotifyPositionParameters(latitude,longitude), responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } else {
            GGUtil.showAToast(this, R.string.no_network_available);
        }
    }

    private JSONObject loadNotifyPositionParameters(Double latitude, Double longitude){
        JSONObject parameters = new JSONObject();
        try {
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject deviceUpdateLocation = new JSONObject();
            deviceUpdateLocation.put("driver_user_id",sharedPreferenceUtil.getLong(GGGlobalValues.USER_ID_IN_SESSION));
            deviceUpdateLocation.put("device_id", sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            deviceUpdateLocation.put("latitude",latitude);
            deviceUpdateLocation.put("longitude",longitude);
            reaxiumParameters.put("DeviceUpdateLocation",deviceUpdateLocation);
            parameters.put("ReaxiumParameters",reaxiumParameters);
        } catch (JSONException e) {
            Log.e(TAG,"",e);
        }
        return parameters;
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initGetLocationProcess();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,"Google Play API connection FAILURE");
    }

    @Override
    public void onLocationChanged(Location location) {
        notifyMyPosition(location.getLatitude(),location.getLongitude());
    }
}
