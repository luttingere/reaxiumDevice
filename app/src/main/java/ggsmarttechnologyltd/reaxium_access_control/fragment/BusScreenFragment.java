package ggsmarttechnologyltd.reaxium_access_control.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;
import java.util.TimerTask;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserINFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserOUTFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.AdminFragment;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 09/05/2016.
 */
public class BusScreenFragment extends GGMainFragment {

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



    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            Log.i("TEST", "Location changed");
//            reaxiumDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(reaxiumDeviceLocation, mRouteMapZoom));
        }
    };


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
        arguments.putSerializable("USER_VALUE",driverUser);
        GGUtil.goToScreen(getActivity(), arguments, MainActivity.class);
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        driverUser = (User) getArguments().getSerializable("USER_VALUE");
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.map));
        hiddenOptionPanel = (LinearLayout) view.findViewById(R.id.hidden_driver_option_container);
        hideDriverOptions = (ImageView) view.findViewById(R.id.first_touchable_arrow);
        driverOptionsPanel = (LinearLayout) view.findViewById(R.id.show_driver_option_container);
        studentsOnBoardContainer = (RelativeLayout) view.findViewById(R.id.students_on_board_container);
        studentsOnTheNextStopContainer = (RelativeLayout) view.findViewById(R.id.studentsOnTheNextStopContainer);
        studentsOnBoardInput = (TextView) view.findViewById(R.id.studentsInCount);
        studentsOnBoardInput.setText(""+studentsOnBoard);
        studentOnTheNextStopInput = (TextView)view.findViewById(R.id.studentsNextStopCount);
        studentOnTheNextStopInput.setText(""+studentsOnTheNextStop);
        ((MainActivity) getActivity()).showBackButton();
    }

    @Override
    protected void setViewsEvents() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mRouteMap = googleMap;
                mRouteMap.setMyLocationEnabled(Boolean.TRUE);
                mRouteMap.setOnMyLocationChangeListener(myLocationChangeListener);
                LatLng latLng = new LatLng(25.749711, -80.261160);
                mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mRouteMapZoom));
                mRouteMap.addMarker(new MarkerOptions().position(latLng).title("Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_reaxium_signal)));
                LatLng nextStop = new LatLng(25.733762, -80.263145);
                Polyline line = mRouteMap.addPolyline(new PolylineOptions()
                        .add(latLng, nextStop)
                        .width(5)
                        .geodesic(Boolean.TRUE)
                        .color(Color.RED));
                mRouteMap.addMarker(new MarkerOptions().position(nextStop).title("Next Stop"));
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

        studentsOnBoardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studentsOnTheNextStop > 0){
                    userINFoundDialog = new UserINFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userINFoundDialog.show();
                    studentsOnBoard++;
                    studentsOnTheNextStop--;
                    studentsOnBoardInput.setText(""+studentsOnBoard);
                    studentOnTheNextStopInput.setText(""+studentsOnTheNextStop);
                    delayedUserINDialogDismiss();
                }
            }
        });

        studentsOnTheNextStopContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studentsOnBoard > 0){
                    userOUTFoundDialog = new UserOUTFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userOUTFoundDialog.show();
                    studentsOnBoard--;
                    studentsOnTheNextStop++;
                    studentsOnBoardInput.setText(""+studentsOnBoard);
                    studentOnTheNextStopInput.setText(""+studentsOnTheNextStop);
                    delayedUserOUTDialogDismiss();
                }
            }
        });

    }

    @Override
    public void clearAllViewComponents() {

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
}
