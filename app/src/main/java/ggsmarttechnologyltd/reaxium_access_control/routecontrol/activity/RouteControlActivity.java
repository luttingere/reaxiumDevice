package ggsmarttechnologyltd.reaxium_access_control.routecontrol.activity;


import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger at G&G on 13/04/2016.
 */
public class RouteControlActivity extends GGMainActivity {

    private float mRouteMapZoom = 16.0f;
    private LatLng reaxiumDeviceLocation;
    private GoogleMap mRouteMap;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange (Location location) {
            Log.i("TEST","Location changed");
            reaxiumDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(reaxiumDeviceLocation, mRouteMapZoom));
        }
    };

    @Override
    protected Integer getMainLayout() {
            return R.layout.route_control_activity;
    }

    @Override
    protected void setViews() {
        mRouteMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


    }

    @Override
    protected void setViewsEvents() {
        mRouteMap.setMyLocationEnabled(Boolean.TRUE);
        mRouteMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    @Override
    protected GGMainFragment getMainFragment() {
        return null;
    }

    @Override
    protected Integer getMainToolbarId() {
        return null;
    }

}
