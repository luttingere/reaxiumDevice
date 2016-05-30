package ggsmarttechnologyltd.reaxium_access_control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.beans.Routes;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.holder.RouteHolder;
import ggsmarttechnologyltd.reaxium_access_control.listeners.OnRouteSelectedListener;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 19/05/2016.
 */
public class RouteListAdapter extends RecyclerView.Adapter<RouteHolder> {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private OnRouteSelectedListener routeListener;
    private static final int TYPE_ORANGE = 1;
    private static final int TYPE_YELLOW = 2;

    private Context context;
    private List<Routes> routeList;


    public RouteListAdapter(Context context,List<Routes> routeList, OnRouteSelectedListener routeListener) {
        this.routeList = routeList;
        if(this.routeList == null){
            this.routeList = new ArrayList<>();
        }
        this.context = context;
        this.routeListener = routeListener;
    }

    @Override
    public RouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = null;
        switch (viewType){
            case TYPE_ORANGE:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_to_be_picked_orange, null);
                break;
            case TYPE_YELLOW:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_to_be_picked_yellow, null);
                break;
            default:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_to_be_picked_orange, null);
                break;
        }
        return new RouteHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(RouteHolder holder, int position) {
        final Routes routes = routeList.get(position);
        holder.routeLocation.setText(routes.getRouteAddress());
        holder.routeInfo.setText("Route #" + routes.getRouteNumber() + ", Stops: " + routes.getStopCount());
        holder.routeSchedule.setText(GGUtil.getTimeFormatted(routes.getRouteDateInit())+"-"+GGUtil.getTimeFormatted(routes.getRouteDateEnd()));
        holder.allContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Click on the route id: "+routes.getRouteId());
                routeListener.onRouteSelected(routes);
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        int viewType = TYPE_ORANGE;
        if (GGUtil.isEven(position)) {
            viewType = TYPE_YELLOW;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

}
