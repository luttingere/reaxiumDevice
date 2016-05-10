package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.location.Location;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;

/**
 * Created by Eduardo Luttinger on 09/05/2016.
 */
public class ShowMapFragment extends GGMainFragment {

    private float mRouteMapZoom = 16.0f;
    private LatLng reaxiumDeviceLocation;
    private GoogleMap mRouteMap;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange (Location location) {
            Log.i("TEST", "Location changed");
            reaxiumDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(reaxiumDeviceLocation, mRouteMapZoom));
        }
    };


    @Override
    public String getMyTag() {
        return ShowMapFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.menu_drawer_show_map;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.route_control_activity;
    }

    @Override
    public Boolean onBackPressed() {
        ((AdminActivity) getActivity()).runMyFragment(new AdminFragment(), null, R.id.action_menu_home);
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        mRouteMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
        ((AdminActivity) getActivity()).showBackButton();
    }

    @Override
    protected void setViewsEvents() {
        mRouteMap.setMyLocationEnabled(Boolean.TRUE);
        mRouteMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    @Override
    public void clearAllViewComponents() {

    }
}
