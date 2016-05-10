package ggsmarttechnologyltd.reaxium_access_control.admin.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger on 28/04/2016.
 */
public class AccessControlHolder extends RecyclerView.ViewHolder {

    public TextView accessDateTime;
    public TextView outOfSyncMark;


    public AccessControlHolder(View itemView) {
        super(itemView);
        setViews(itemView);
    }

    private void setViews(View itemView){
        accessDateTime = (TextView) itemView.findViewById(R.id.access_time);
        outOfSyncMark = (TextView) itemView.findViewById(R.id.outOfSyncMark);
    }
}
