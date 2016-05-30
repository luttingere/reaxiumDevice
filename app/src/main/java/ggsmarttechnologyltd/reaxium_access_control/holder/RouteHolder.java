package ggsmarttechnologyltd.reaxium_access_control.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger on 19/05/2016.
 */
public class RouteHolder extends RecyclerView.ViewHolder {

    public LinearLayout allContainer;
    public TextView routeInfo;
    public TextView routeLocation;
    public TextView routeSchedule;

    public RouteHolder(View itemView) {
        super(itemView);
        setViews(itemView);
    }

    private void setViews(View view){
        allContainer = (LinearLayout) view.findViewById(R.id.allContainer);
        routeInfo = (TextView) view.findViewById(R.id.routeInformation);
        routeLocation = (TextView) view.findViewById(R.id.routeLocation);
        routeSchedule = (TextView) view.findViewById(R.id.routeSchedule);
    }

}
