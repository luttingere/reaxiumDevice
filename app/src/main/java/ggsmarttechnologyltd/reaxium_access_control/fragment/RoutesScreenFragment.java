package ggsmarttechnologyltd.reaxium_access_control.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.adapter.RouteListAdapter;
import ggsmarttechnologyltd.reaxium_access_control.beans.Routes;
import ggsmarttechnologyltd.reaxium_access_control.beans.Stops;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.RouteStopUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.listeners.OnRouteSelectedListener;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 19/05/2016.
 */
public class RoutesScreenFragment extends GGMainFragment implements OnRouteSelectedListener {

    private LinearLayoutManager layoutManager;
    private RecyclerView routeListView;
    private List<Routes> deviceRoutesList;
    private RouteListAdapter routeListAdapter;
    private User driverUser;
    private RouteStopUsersDAO routeStopUsersDAO;


    @Override
    public String getMyTag() {
        return RoutesScreenFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.route_screen_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.route_screen_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        Bundle arguments = new Bundle();
        arguments.putSerializable("USER_VALUE",driverUser);
        GGUtil.goToScreen(getActivity(), arguments, MainActivity.class);
        getActivity().finish();
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        routeStopUsersDAO = RouteStopUsersDAO.getInstance(getActivity());
        driverUser = (User) getArguments().getSerializable("USER_VALUE");
        layoutManager  = new LinearLayoutManager(getActivity());
        routeListView = (RecyclerView) view.findViewById(R.id.route_list);
        routeListView.setLayoutManager(layoutManager);
        loadRoutes();
        routeListAdapter = new RouteListAdapter(getActivity(),deviceRoutesList,this);
        routeListView.setAdapter(routeListAdapter);
        ((MainActivity) getActivity()).showBackButton();
    }

    @Override
    protected void setViewsEvents() {

    }

    @Override
    public void clearAllViewComponents() {

    }

    private void loadRoutes(){
        deviceRoutesList = routeStopUsersDAO.getRoutesRegistered();
    }

    @Override
    public void onRouteSelected(Routes route) {
        Log.i(TAG,"Route selected, running map");
        Bundle params = new Bundle();
        params.putSerializable("USER_VALUE",driverUser);
        params.putSerializable("ROUTE",route);
       ((MainActivity)getActivity()).runMyFragment(new BusScreenFragment(), params);
    }
}
