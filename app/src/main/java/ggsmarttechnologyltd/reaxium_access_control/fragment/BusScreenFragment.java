package ggsmarttechnologyltd.reaxium_access_control.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.DocumentCodeAccessDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserINFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserOUTFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnValidateDocumentCodeListener;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticCardValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitScannersInAutoModeThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.BusStatus;
import ggsmarttechnologyltd.reaxium_access_control.beans.DirectionApiBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.LocationObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.Routes;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.StopHistory;
import ggsmarttechnologyltd.reaxium_access_control.beans.Stops;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.AccessControlDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.BusStatusDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.RouteStopUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.DistanceCalculator;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.LatLngInterpolator;
import ggsmarttechnologyltd.reaxium_access_control.util.MarkerAnimation;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.PolyUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SuccessfulAccessPlayerSingleton;

/**
 * Created by Eduardo Luttinger on 09/05/2016.
 */
public class BusScreenFragment extends GGMainFragment implements OnValidateDocumentCodeListener {


    private ImageView emergencyAlarmButton;
    private ImageView trafficAlarmButton;
    private ImageView carEngineFailureAlarmButton;
    private ImageView carCrashAlarmButton;
    private Button endRouteButton;
    private float mRouteMapZoom = 16.0f;
    private LatLng reaxiumDeviceLocation;
    private GoogleMap mRouteMap;
    private SupportMapFragment mapFragment;
    private LinearLayout driverOptionsPanel;
    private LinearLayout hiddenOptionPanel;
    private ImageView hideDriverOptions;
    private RelativeLayout studentsOnBoardContainer;
    private RelativeLayout studentsOnTheNextStopContainer;
    private User driverUser;
    private UserINFoundDialog userINFoundDialog;
    private UserOUTFoundDialog userOUTFoundDialog;
    private TextView studentsOnBoardInput;
    private TextView studentOnTheNextStopInput;
    private int studentsOnTheNextStop = 16;
    private int studentsOnBoard = 0;
    private Routes route;
    private List<Stops> stopsList;
    private List<User> usersAtStopList;
    private RouteStopUsersDAO routeStopUsersDAO;
    private Bundle savedInstanceState;
    private Marker mBusMarker;
    private TextView routeInfo;
    private TextView stopInfo;
    private AccessControlDAO accessControlDAO;
    private ReaxiumUsersDAO reaxiumUsersDAO;
    private BusStatusDAO busStatusDAO;
    private AccessControlHandler accessControlHandler;
    private List<AccessControl> accessINList;
    private LocationObject busLocation;
    private int activeStopOrder = 1;
    private BusStatus busStatus;
    private Stops stop;
    private Stops lastStop;
    private Stops actualStop = new Stops();
    private SharedPreferenceUtil sharedPreferenceUtil;
    private List<Long> usersIDSOFTheRoute;
    public static Boolean busPositionRouteAlreadyPainted = Boolean.FALSE;
    private DocumentCodeAccessDialog documentCodeAccessDialog;
    private List<LatLng> indications;
    private Map<Integer,List<LatLng>> radioMap;
    private Polyline mDriverPositionToTheStopPolyLine;
    private StopHistory stopHistory = new StopHistory();


    @Override
    public String getMyTag() {
        return BusScreenFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.route_screen;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.bus_screem_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        Bundle arguments = new Bundle();
        arguments.putSerializable("USER_VALUE", driverUser);
        GGUtil.goToScreen(getActivity(), arguments, MainActivity.class);
        getActivity().finish();
        return Boolean.TRUE;
    }


    @Override
    public void onResume() {
        super.onResume();
        BusScreenFragment.busPositionRouteAlreadyPainted = Boolean.FALSE;
        processInstanceState();
        loadScreenValues();
        initScanners();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanners();
    }

    /**
     * fill al variables used in the screen
     */
    private void loadScreenValues() {
        stopsList = routeStopUsersDAO.getStopsOfARoute(route.getRouteId());
        usersIDSOFTheRoute = routeStopUsersDAO.getAllUsersIdOfARoute(route.getRouteId());
        mergeUsersIDOfTheRouteWithTheUsersAboard();
        lastStop = stopsList.get(stopsList.size() - 1);
        usersAtStopList = stopsList.get(activeStopOrder - 1).getUsers();
        stop = stopsList.get(activeStopOrder - 1);
        stopInfo.setText("Next Stop: " + stop.getStopAddress());
        if (usersAtStopList != null) {
            studentsOnTheNextStop = usersAtStopList.size();
        } else {
            studentsOnTheNextStop = 0;
        }
        studentOnTheNextStopInput.setText("" + studentsOnTheNextStop);
        studentsOnBoardInput.setText("" + studentsOnBoard);
    }


    private void mergeUsersIDOfTheRouteWithTheUsersAboard(){
        Map<Long,Long> mergeMap = new HashMap<>();
        if(usersIDSOFTheRoute != null){
            for(Long userId: usersIDSOFTheRoute){
                mergeMap.put(userId,userId);
            }
        }else {
            usersIDSOFTheRoute = new ArrayList<>();
        }
        List<AccessControl> accessControlList = accessControlDAO.getAllAccessIN();
        if(accessControlList != null){
            for(AccessControl accessControl: accessControlList){
                mergeMap.put(accessControl.getUserId(),accessControl.getUserId());
            }
        }
        usersIDSOFTheRoute.clear();
        for(Map.Entry<Long,Long> entry: mergeMap.entrySet()){
            usersIDSOFTheRoute.add(entry.getKey());
        }
    }



    /**
     * retrieve all values from a restored state
     */
    private void processInstanceState() {
        if (savedInstanceState != null && savedInstanceState.getSerializable("ROUTE") != null) {
            route = (Routes) savedInstanceState.getSerializable("ROUTE");
            driverUser = (User) savedInstanceState.getSerializable("USER_VALUE");
            stopHistory = (StopHistory) savedInstanceState.getSerializable("STOP_HISTORY");
            activeStopOrder = savedInstanceState.getInt("STOP_ORDER_ACTIVE");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putSerializable("USER_VALUE", driverUser);
            outState.putSerializable("ROUTE", route);
            outState.putInt("STOP_ORDER_ACTIVE", activeStopOrder);
            outState.putSerializable("STOP_HISTORY", stopHistory);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    protected void setViews(View view) {
        accessControlHandler = new AccessControlHandler();
        routeStopUsersDAO = RouteStopUsersDAO.getInstance(getActivity());
        accessControlDAO = AccessControlDAO.getInstance(getActivity());
        reaxiumUsersDAO = ReaxiumUsersDAO.getInstance(getActivity());
        busStatusDAO = BusStatusDAO.getInstance(getActivity());
        sharedPreferenceUtil = getSharedPreferences();
        driverUser = (User) getArguments().getSerializable("USER_VALUE");
        route = (Routes) getArguments().getSerializable("ROUTE");
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.map));
        hiddenOptionPanel = (LinearLayout) view.findViewById(R.id.hidden_driver_option_container);
        hideDriverOptions = (ImageView) view.findViewById(R.id.first_touchable_arrow);
        driverOptionsPanel = (LinearLayout) view.findViewById(R.id.show_driver_option_container);
        studentsOnBoardContainer = (RelativeLayout) view.findViewById(R.id.students_on_board_container);
        studentsOnTheNextStopContainer = (RelativeLayout) view.findViewById(R.id.studentsOnTheNextStopContainer);
        studentsOnBoardInput = (TextView) view.findViewById(R.id.studentsInCount);
        emergencyAlarmButton = (ImageView) view.findViewById(R.id.emergency_alarm);
        trafficAlarmButton = (ImageView) view.findViewById(R.id.traffic_alarm);
        carEngineFailureAlarmButton = (ImageView) view.findViewById(R.id.car_engine_failure_alarm);
        endRouteButton = (Button) view.findViewById(R.id.end_route);
        carCrashAlarmButton = (ImageView) view.findViewById(R.id.car_crash_alarm);
        routeInfo = (TextView) view.findViewById(R.id.routeInformation);
        routeInfo.setText("Route: " + route.getRouteNumber() + ", " + route.getRouteName());
        stopInfo = (TextView) view.findViewById(R.id.stopInformation);
        studentOnTheNextStopInput = (TextView) view.findViewById(R.id.studentsNextStopCount);
        ((MainActivity) getActivity()).showBackButton();
        initializeRoute();
    }

    /**
     * initialize the route or get the last status of the route
     */
    private void initializeRoute() {
        String decodePoints = route.getRoutePolyLine();
        if(decodePoints != null && decodePoints.length() > 0){
            List<LatLng> polyLine = PolyUtil.decodePoly(decodePoints);
            indications = polyLine;
        }else{
            indications = new ArrayList<>();
        }
        busStatus = busStatusDAO.getBusStatus();
        if (busStatus != null) {
            if (busStatus.getRouteId() != route.getRouteId()) {
                busStatusDAO.deleteAllValuesFromBusStatus();
                busStatusDAO.initBusRoute(route.getRouteId(), 1L);
            } else {
                studentsOnBoard = busStatus.getUserCount();
                activeStopOrder = busStatus.getStopOrder();
            }
        } else {
            busStatusDAO.initBusRoute(route.getRouteId(), 1L);
            studentsOnBoard = 0;
            activeStopOrder = 1;
        }
    }

    private void endTheRoute(){
        new AlertDialog.Builder(getActivity())
                .setTitle("End Route Confirmation")
                .setMessage("¿Are you sure you want to end the Route?")
                .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopScanners();
                        busStatusDAO.deleteAllValuesFromBusStatus();
                        Bundle arguments = new Bundle();
                        arguments.putSerializable("USER_VALUE", driverUser);
                        GGUtil.goToScreen(getActivity(), arguments, MainActivity.class);
                        getActivity().finish();
                    }
                }).setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void setViewsEvents() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mRouteMap = googleMap;
                configureMap();
                loadStopsInTheMap();
                loadStopRouteOnTheMap();
            }
        });

        hiddenOptionPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenOptionPanel.setVisibility(View.GONE);
                driverOptionsPanel.setVisibility(View.VISIBLE);
            }
        });

        hideDriverOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverOptionsPanel.setVisibility(View.GONE);
                hiddenOptionPanel.setVisibility(View.VISIBLE);

            }
        });

        endRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTheRoute();
            }
        });



        emergencyAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNotificationProcess(GGGlobalValues.EMERGENCY_ALARM_ID, usersIDSOFTheRoute, sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            }
        });

        trafficAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNotificationProcess(GGGlobalValues.TRAFFIC_ALARM_ID, usersIDSOFTheRoute, sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            }
        });

        carEngineFailureAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNotificationProcess(GGGlobalValues.CHECK_ENGINE_ALARM_ID, usersIDSOFTheRoute, sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            }
        });

        carCrashAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNotificationProcess(GGGlobalValues.CAR_CRASH_ALARM_ID, usersIDSOFTheRoute, sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            }
        });

        studentsOnTheNextStopContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentCodeAccessDialog = new DocumentCodeAccessDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen, BusScreenFragment.this);
                documentCodeAccessDialog.show();
            }
        });
    }

    private void startNotificationProcess(final int notificationType, final List<Long> usersId, final Long deviceId) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Alarm Confirmation")
                .setMessage("¿Are you sure you want to send the alarm?")
                .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendAlarm(notificationType, usersId, deviceId);
                    }
                }).setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    private void sendNotification(int notificationType, List<Long> usersId, Long deviceId) {
        if (GGUtil.isNetworkAvailable(getActivity())) {

            //showProgressDialog("Sending the notification...");
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Type responseType = new TypeToken<ApiResponse<Object>>() {
                    }.getType();
                    ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        GGUtil.showAToast(getActivity(), "Next Stop Notification sent successfully");
                    } else {
                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    }
                    hideProgressDialog();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //hideProgressDialog();
                    GGUtil.showAToast(getActivity(), "There was an error sending the notifications, it is possible that you had a bad connection.");
                }
            };
            JSONObject notificationServiceParams = new JSONObject();
            try {
                JSONObject reaxiumParameters = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("device_id", deviceId);
                notification.put("notification_type", notificationType);
                notification.put("driver_name", sharedPreferenceUtil.getString(GGGlobalValues.USER_FULL_NAME_IN_SESSION));
                JSONArray jsonArray = new JSONArray();
                for (Long userId : usersId) {
                    jsonArray.put(userId);
                }
                notification.put("users_id", jsonArray);
                reaxiumParameters.put("Notification", notification);
                notificationServiceParams.put("ReaxiumParameters", reaxiumParameters);
            } catch (Exception e) {
            }

            JsonObjectRequestUtil routeRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SEND_NEXT_STOP_NOTIFICATIONS), notificationServiceParams, responseListener, errorListener);
            routeRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(routeRequest);

        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }

    private void sendAlarm(int notificationType, List<Long> usersId, Long deviceId) {
        if (GGUtil.isNetworkAvailable(getActivity())) {

            showProgressDialog("Sending the notification...");
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Type responseType = new TypeToken<ApiResponse<Object>>() {
                    }.getType();
                    ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        GGUtil.showAToast(getActivity(), "Notification sent successfully");
                    } else {
                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    }
                    hideProgressDialog();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    GGUtil.showAToast(getActivity(), "There was an error sending the notifications, it is possible that you had a bad connection.");
                }
            };
            JSONObject notificationServiceParams = new JSONObject();
            try {
                JSONObject reaxiumParameters = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("device_id", deviceId);
                notification.put("notification_type", notificationType);
                notification.put("driver_name", sharedPreferenceUtil.getString(GGGlobalValues.USER_FULL_NAME_IN_SESSION));
                JSONArray jsonArray = new JSONArray();
                for (Long userId : usersId) {
                    jsonArray.put(userId);
                }
                notification.put("users_id", jsonArray);
                reaxiumParameters.put("Notification", notification);
                notificationServiceParams.put("ReaxiumParameters", reaxiumParameters);
            } catch (Exception e) {
            }

            JsonObjectRequestUtil routeRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SEND_NOTIFICATIONS), notificationServiceParams, responseListener, errorListener);
            routeRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(routeRequest);

        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }

    private void loadStopRouteOnTheMap() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(R.color.orange);
        polylineOptions.visible(Boolean.TRUE);
        polylineOptions.geodesic(Boolean.TRUE);
        polylineOptions.width(10);
        for (LatLng location : indications) {
            polylineOptions.add(location);
        }
        mRouteMap.addPolyline(polylineOptions);
    }

    private void loadRouteInTheMap() {
        if (GGUtil.isNetworkAvailable(getActivity())) {
            showProgressDialog("Loading route in the Map");
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Type responseType = new TypeToken<DirectionApiBean>() {
                    }.getType();
                    DirectionApiBean directionApiBean = JsonUtil.getEntityFromJSON(response, responseType);
                    if (directionApiBean != null && directionApiBean.getErrorMessage() == null) {
                        if (!directionApiBean.getDirectionRouteApiBeanList().isEmpty()) {
                            String decodePoints = directionApiBean.getDirectionRouteApiBeanList().get(0).getOverViewPolyLine().getPoints();
                            List<LatLng> polyLine = PolyUtil.decodePoly(decodePoints);
                            indications.addAll(polyLine);
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.color(R.color.blue);
                            polylineOptions.visible(Boolean.TRUE);
                            polylineOptions.geodesic(Boolean.TRUE);
                            polylineOptions.width(7);
                            for (LatLng location : polyLine) {
                                polylineOptions.add(location);
                            }
                            mDriverPositionToTheStopPolyLine = mRouteMap.addPolyline(polylineOptions);

                            BusScreenFragment.busPositionRouteAlreadyPainted = Boolean.TRUE;
                            Log.i(TAG, "Route drawed in the map");
                        } else {
                            Log.i(TAG, "Empty polyline");
                        }
                    } else {
                        GGUtil.showAToast(getActivity(), directionApiBean.getErrorMessage());
                    }
                    hideProgressDialog();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    GGUtil.showAToast(getActivity(), R.string.bad_connection_message);
                }
            };
            if (GGGlobalValues.LAST_LOCATION != null) {
                String routeUrl = getRouteUrl(GGGlobalValues.GET_POLYLINE_ROUTE);
                JsonObjectRequestUtil routeRequest = new JsonObjectRequestUtil(Request.Method.GET, routeUrl, responseListener, errorListener);
                routeRequest.setShouldCache(false);
                MySingletonUtil.getInstance(getActivity()).addToRequestQueue(routeRequest);
            } else {
                GGUtil.showAToast(getActivity(), "No GPS Information Available");
            }
        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }

    private String getRouteUrl(String url) {
        String routeUrl = url;

        String origin = GGGlobalValues.LAST_LOCATION.latitude + "," + GGGlobalValues.LAST_LOCATION.longitude;
        Stops firstStop = stopsList.get(0);
        String destination = firstStop.getStopLatitude() + "," + firstStop.getStopLongitude();
//        StringBuffer wayPoints = new StringBuffer();
//        Stops stop;
//        for (int i = 0; i < stopsList.size() - 1; i++) {
//            stop = stopsList.get(i);
//            wayPoints.append(stop.getStopLatitude()+","+stop.getStopLongitude()+"|");
//        }
        routeUrl = routeUrl.replace("@ORIGIN@", origin);
        routeUrl = routeUrl.replace("@DESTINATION@", destination);
//        routeUrl = routeUrl.replace("@WAY_POINTS@",wayPoints.toString());
        return routeUrl;
    }

    @Override
    public void clearAllViewComponents() {

    }

    private void initScanners() {
        InitScannersInAutoModeThread initScannersInAutoModeThread = new InitScannersInAutoModeThread(getActivity(), accessControlHandler);
        initScannersInAutoModeThread.start();
    }

    private void stopScanners() {
        AutomaticCardValidationThread.stopScanner();
    }

    private void configureMap() {
        if (GGGlobalValues.LAST_LOCATION != null) {
            updateBusMarkerPosition(GGGlobalValues.LAST_LOCATION);
        }
    }

    public void updateMyPosition(final LocationObject locationObject) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "updateMyPosition method running");
                busLocation = locationObject;
                reaxiumDeviceLocation = new LatLng(locationObject.getLatitude(), locationObject.getLongitude());
                updateBusMarkerPosition(reaxiumDeviceLocation);
            }
        });
    }

    /**
     * @param busLocation
     */
    private void updateBusMarkerPosition(LatLng busLocation) {
        if (mBusMarker == null) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(busLocation);
            marker.title("Reaxium");
            marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
            mBusMarker = mRouteMap.addMarker(marker);
        } else {
            MarkerAnimation.animateMarkerToGB(mBusMarker, busLocation, new LatLngInterpolator.Spherical());
        }
        mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mBusMarker.getPosition(), 16));
        if (!BusScreenFragment.busPositionRouteAlreadyPainted) {
            loadRouteInTheMap();
            loadRadioMap();
        }
        reloadNextStopInfo(busLocation);
    }

    private void reloadNextStopInfo(LatLng busLocation) {
        actualStop = getNextStopByRadio(busLocation);
        if(actualStop == null){
            int stopOrder = stopHistory.getStopHistoryMap().size();
            if(stopOrder > 0 ){
                stopOrder = stopOrder + 1;
                if(stopsList.size() <= stopOrder){
                    stopOrder = stopsList.size() - 1;
                }
            }
            actualStop = stopsList.get(stopOrder);
        }else{
            if(actualStop.getStopOrder() != activeStopOrder ){
                try {
                    Stops futureStop = getTheFutureStop(actualStop);
                    if(!stopHistory.getStopHistoryMap().containsKey(futureStop.getStopOrder())){
                        sendNextStopNotification(futureStop);
                    }
                }catch (Exception e){
                    Log.e(TAG,"Error sending a notification",e);
                }
                busStatusDAO.changeTheNextStop(actualStop.getStopOrder());
            }
        }
        activeStopOrder = actualStop.getStopOrder();
        stopInfo.setText("Next Stop: " + actualStop.getStopAddress());
        Log.i(TAG, "Next Stop: " + actualStop.getStopName());
        if (actualStop.getUsers() != null) {
            studentsOnTheNextStop = actualStop.getUsers().size();
        } else {
            studentsOnTheNextStop = 0;
        }
        studentOnTheNextStopInput.setText("" + studentsOnTheNextStop);
    }


    private Stops getTheFutureStop(Stops nextStop){
        Stops futureStop = null;
        try {
            futureStop = stopsList.get(nextStop.getStopOrder());
        }catch (Exception e){
        }
        return futureStop;
    }


    private void sendNextStopNotification(Stops stops){
        if(stops != null){
            List<Long> usersId = new ArrayList<>();
            for (User user: stops.getUsers()){
                usersId.add(user.getUserId());
            }
            sendNotification(5, usersId, sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
        }
    }

    private void loadStopsInTheMap() {
        MarkerOptions marker;
        for (Stops stops : stopsList) {
            marker = new MarkerOptions();
            LatLng location = new LatLng(Double.parseDouble(stops.getStopLatitude()), Double.parseDouble(stops.getStopLongitude()));
            marker.position(location);
            marker.title("Stop: " + stops.getStopName() + " Est. Students: " + stops.getUsers().size());
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_icon));
            mRouteMap.addMarker(marker);
        }
    }


    private Stops getNextStopByRadio(LatLng reaxiumDeviceLocation) {
        Stops nextStop = null;
        List<LatLng> radioDeParadas;
        double distance = 0;
            for (Stops paradasFijas : stopsList) {
                radioDeParadas = radioMap.get(paradasFijas.getStopOrder());
                for (LatLng radio: radioDeParadas){
                     distance = DistanceCalculator.getDistance(reaxiumDeviceLocation.latitude,reaxiumDeviceLocation.longitude,radio.latitude,radio.longitude,"M");
                    if(distance < 0.017 && !stopHistory.getStopHistoryMap().containsKey(paradasFijas.getStopOrder())){
                        nextStop = paradasFijas;
                        stopHistory.getStopHistoryMap().put(nextStop.getStopOrder(),nextStop.getStopOrder());
                        break;
                    }
                }
                if(nextStop != null){
                    break;
                }
            }
        if(nextStop != null){
            if(mDriverPositionToTheStopPolyLine!= null){
                mDriverPositionToTheStopPolyLine.remove();
            }
            Log.i(TAG,"Next Stop Found: "+nextStop.getStopName());
        }else{

        }
        return nextStop;
    }


    private void loadRadioMap(){
        radioMap = new android.support.v4.util.ArrayMap<>();
        List<LatLng> radioDeParadas = null;
        for (Stops paradasFijas : stopsList) {
            radioDeParadas = new ArrayList<>();
            for (LatLng posibleParada : indications) {
                if(isTheRadioOfTheStop(paradasFijas,posibleParada)){
                    radioDeParadas.add(posibleParada);
                }
            }
            radioMap.put(paradasFijas.getStopOrder(),radioDeParadas);
        }
        Log.i(TAG,"RadioMap: ");
        for(Map.Entry<Integer,List<LatLng>> radioMapObject :radioMap.entrySet()){
            Log.i(TAG,"radioMapObject #: "+radioMapObject.getKey());
            for (LatLng latLng: radioMapObject.getValue()){
                Log.i(TAG,"Latitude: "+latLng.latitude+" - Longitude: "+latLng.longitude);
            }
        }
    }

    private Boolean isTheRadioOfTheStop(Stops paradasFijas,LatLng posibleParada){
        Boolean isTheRadio = Boolean.FALSE;

        Double fixedStopLatitude = Double.parseDouble(paradasFijas.getStopLatitude());
        Double fixedStopLongitude = Double.parseDouble(paradasFijas.getStopLongitude());
        Stops fixedNextStop= null;
        try {
             fixedNextStop = stopsList.get(paradasFijas.getStopOrder());
        }catch(IndexOutOfBoundsException e){
            fixedNextStop = null;
        }

        Double stopToTestLatitude = posibleParada.latitude;
        Double stopToTestLongitude = posibleParada.longitude;
        Double  nextStopLatitude = null;
        Double  nextStopLongitude = null;
        Double distance = DistanceCalculator.getDistance(stopToTestLatitude,stopToTestLongitude,fixedStopLatitude,fixedStopLongitude,"M");
        Double nextStopDistance = null;

        if(fixedNextStop != null){
            nextStopLatitude =  Double.parseDouble(fixedNextStop.getStopLatitude());
            nextStopLongitude =  Double.parseDouble(fixedNextStop.getStopLongitude());
            nextStopDistance =  DistanceCalculator.getDistance(nextStopLatitude,nextStopLongitude,stopToTestLatitude,stopToTestLongitude,"M");
        }

        Log.i(TAG,"Parada: "+paradasFijas.getStopName());
        Log.i(TAG,"Parada: Latitude: "+fixedStopLatitude);
        Log.i(TAG,"Parada: Longitude: "+fixedStopLongitude);
        Log.i(TAG,"Posible *: Latitude: "+stopToTestLatitude);
        Log.i(TAG,"Posible *: Longitude: "+stopToTestLongitude);
        Log.i(TAG,"NextStop *: Latitude: "+nextStopLatitude);
        Log.i(TAG,"NextStop *: Longitude: "+nextStopLongitude);
        Log.i(TAG,"distance: "+distance);
        Log.i(TAG,"nextStopDistance: "+nextStopDistance);

        if(distance <= 0.17 && (nextStopDistance != null && nextStopDistance >=0.17)){
            isTheRadio =  Boolean.TRUE;

        }else if(distance <= 0.17 && nextStopDistance== null){
            isTheRadio =  Boolean.TRUE;
        }

        Log.i(TAG,"isTheRadio: "+isTheRadio);

        return isTheRadio;
    }


    private Boolean isFarThanMe(LatLng myLocation, LatLng lastStop, LatLng stopToCompare) {
        Boolean isFarThanMe = Boolean.FALSE;
        double myDistance = DistanceCalculator.getDistance(myLocation.latitude, myLocation.longitude, lastStop.latitude, lastStop.longitude, "M");
        double stopDistance = DistanceCalculator.getDistance(stopToCompare.latitude, stopToCompare.longitude, lastStop.latitude, lastStop.longitude, "M");
        if (stopDistance > myDistance) {
            isFarThanMe = Boolean.TRUE;
        }
        return isFarThanMe;
    }

    public void delayedUserINDialogDismiss() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (userINFoundDialog != null) {
                    if (userINFoundDialog.isShowing()) {
                        userINFoundDialog.dismiss();
                        userINFoundDialog = null;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, userINFoundDialog.SLEEP_TIME);
    }

    public void delayedUserOUTDialogDismiss() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (userOUTFoundDialog != null) {
                    if (userOUTFoundDialog.isShowing()) {
                        userOUTFoundDialog.dismiss();
                        userOUTFoundDialog = null;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, userOUTFoundDialog.SLEEP_TIME);
    }

    /**
     *
     */
    private void saveAccessInServer(final Long lastInsertedId, Long userId, String trafficType, Long deviceId, String accessType, String trafficInfo) {
        if (GGUtil.isNetworkAvailable(getActivity())) {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Type responseType = new TypeToken<ApiResponse<Object>>() {}.getType();
                    ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        Integer result = accessControlDAO.markAsRegisteredInCloud(lastInsertedId);
                        Log.i(TAG, "Mark as registered result: " + result);
                        GGUtil.showAToast(getActivity(), "user access registered in cloud successfully");
                    } else {
                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    GGUtil.showAToast(getActivity(), "Bad Connection, Access not saved in cloud");
                }
            };
            JSONObject parameters = loadRequestParameters(userId, trafficType, deviceId, accessType, trafficInfo);
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SAVE_ACCESS_IN_SERVER), parameters, responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            GGUtil.showAToast(getActivity(), "No internet connection, remember synchronize the device for send access record to the cloud");
        }
    }

    private JSONObject loadRequestParameters(Long userId, String trafficType, Long deviceId, String accessType, String trafficInfo) {
        JSONObject requestObject = new JSONObject();
        try {
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject usersAccess = new JSONObject();
            usersAccess.put("user_id", userId);
            usersAccess.put("traffic_type", trafficType);
            usersAccess.put("device_id", deviceId);
            usersAccess.put("access_type", accessType);
            usersAccess.put("traffic_info", trafficInfo);
            reaxiumParameters.put("UserAccess", usersAccess);
            requestObject.put("ReaxiumParameters", reaxiumParameters);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return requestObject;
    }

    @Override
    public void validateDocument(String documentCode) {
        User user = reaxiumUsersDAO.getUserByDocumentCode(documentCode);
        String trafficInfoResult = "";
        String trafficInfo = "";
        String userAccessType = "DocumentID";
        String userAccessTypeID = "4";
        String deviceId  = GGUtil.getDeviceId(getActivity());
        if (user != null) {
            SuccessfulAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
            trafficInfo = user.getFirstName() + " " + user.getFirstLastName() + ", @IN_OR_OUT@, @Time@";
            user.setAccessTime(GGUtil.getTimeFormatted(new Date()));
            Long lastInsertedId;
            String trafficTypeId;
            AccessControl accessControl = accessControlDAO.getLastAccess("" + user.getUserId().longValue());

            if (accessControl != null && accessControl.getAccessType().equalsIgnoreCase("IN")) {
                studentsOnBoard = busStatusDAO.dropAUserOffTheBus(route.getRouteId());
                trafficTypeId = "2";
                trafficInfoResult = trafficInfo.replace("@IN_OR_OUT@", "has got off the bus / number "+deviceId+" at the stop "+actualStop.getStopNumber()+", Good Bye!");
                lastInsertedId = accessControlDAO.insertUserAccess(user.getUserId(), userAccessType, "OUT");
                userOUTFoundDialog = new UserOUTFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                userOUTFoundDialog.updateUserInfo(user);
                userOUTFoundDialog.show();
                delayedUserOUTDialogDismiss();
            } else {
                studentsOnBoard = busStatusDAO.aboardAUserOnTheBus(route.getRouteId());
                trafficTypeId = "1";
                trafficInfoResult = trafficInfo.replace("@IN_OR_OUT@", "has got on the bus / number "+deviceId+" at the stop "+actualStop.getStopNumber()+", Welcome!");
                lastInsertedId = accessControlDAO.insertUserAccess(user.getUserId(), userAccessType, "IN");
                userINFoundDialog = new UserINFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                userINFoundDialog.updateUserInfo(user);
                userINFoundDialog.show();
                delayedUserINDialogDismiss();
            }

            studentsOnBoardInput.setText("" + studentsOnBoard);
            saveAccessInServer(lastInsertedId, user.getUserId(), trafficTypeId, getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID), userAccessTypeID, trafficInfoResult);

        } else {
            FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
            GGUtil.showAToast(getActivity(), "Invalid student id, not registered: " + documentCode);
        }
    }

    private class AccessControlHandler extends ScannersActivityHandler {


        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateScannerResult(AppBean bean) {
            SecurityObject securityObject = (SecurityObject) bean;
            User user = null;
            String userAccessType = "";
            String userAccessTypeID = "";
            String trafficInfo = "";
            String deviceId  = GGUtil.getDeviceId(getActivity());
            if (securityObject.getCardId() == null) {
                userAccessType = GGGlobalValues.BIOMETRIC;
                userAccessTypeID = "2";
                user = reaxiumUsersDAO.getUserById("" + securityObject.getUserId());
            } else {
                userAccessType = GGGlobalValues.RFID;
                userAccessTypeID = "3";
                user = reaxiumUsersDAO.getUserByIdAndRfidCode("" + securityObject.getUserId(), "" + securityObject.getCardId());
            }
            String trafficInfoResult = "";
            if (user != null) {
                SuccessfulAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                trafficInfo = user.getFirstName() + " " + user.getFirstLastName() + ", @IN_OR_OUT@, @Time@";
                user.setAccessTime(GGUtil.getTimeFormatted(new Date()));
                Long lastInsertedId = null;
                String trafficTypeId = "1";
                AccessControl accessControl = accessControlDAO.getLastAccess("" + user.getUserId().longValue());
                if (accessControl != null && accessControl.getAccessType().equalsIgnoreCase("IN")) {

                    studentsOnBoard = busStatusDAO.dropAUserOffTheBus(route.getRouteId());
                    trafficTypeId = "2";
                    trafficInfoResult = trafficInfo.replace("@IN_OR_OUT@", "has got off the bus / number "+deviceId+" at the stop "+actualStop.getStopNumber()+", Good Bye!");
                    lastInsertedId = accessControlDAO.insertUserAccess(user.getUserId(), userAccessType, "OUT");
                    userOUTFoundDialog = new UserOUTFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userOUTFoundDialog.updateUserInfo(user);
                    userOUTFoundDialog.show();
                    delayedUserOUTDialogDismiss();

                } else {

                    studentsOnBoard = busStatusDAO.aboardAUserOnTheBus(route.getRouteId());
                    trafficTypeId = "1";
                    trafficInfoResult = trafficInfo.replace("@IN_OR_OUT@", "has got on the bus / number "+deviceId+" at the stop "+actualStop.getStopNumber()+", Welcome!");
                    lastInsertedId = accessControlDAO.insertUserAccess(user.getUserId(), userAccessType, "IN");
                    userINFoundDialog = new UserINFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userINFoundDialog.updateUserInfo(user);
                    userINFoundDialog.show();
                    delayedUserINDialogDismiss();

                }
                studentsOnBoardInput.setText("" + studentsOnBoard);
                saveAccessInServer(lastInsertedId, user.getUserId(), trafficTypeId, getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID), userAccessTypeID, trafficInfoResult);
            } else {
                FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                GGUtil.showAToast(getActivity(), "Invalid user, not registered: " + securityObject.getUserId());
            }
        }

        @Override
        protected void errorRoutine(String message) {
            GGUtil.showAToast(getActivity(), message);
        }
    }

}
