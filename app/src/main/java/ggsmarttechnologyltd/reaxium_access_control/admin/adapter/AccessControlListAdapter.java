package ggsmarttechnologyltd.reaxium_access_control.admin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.AccessControlFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.holder.AccessControlHolder;
import ggsmarttechnologyltd.reaxium_access_control.admin.holder.UserHolder;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnUserClickListener;
import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.AccessControlDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class AccessControlListAdapter extends RecyclerView.Adapter<AccessControlHolder> {

    private Context mContext;
    private List<AccessControl> accessControlList = new ArrayList<>();
    private static final int IN_TYPE = 0;
    private static final int OUT_TYPE = 1;
    private int viewType = IN_TYPE;
    private AccessControl accessControl;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a   -   MM-dd-yyyy");

    public AccessControlListAdapter(Context mContext, List<AccessControl> accessControlList) {
        this.mContext = mContext;
        this.accessControlList = accessControlList;
    }

    @Override
    public AccessControlHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = null;
        switch (viewType){
            case IN_TYPE:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_in_info_item, null);
                break;
            case OUT_TYPE:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_out_info_item, null);
                break;
        }
        return new AccessControlHolder(itemLayoutView);
    }

    @Override
    public int getItemViewType(int position) {
        viewType = IN_TYPE;
        if(accessControlList.get(position).getAccessType().equals(AccessControlFragment.LIST_OUT_NAME)){
            viewType = OUT_TYPE;
        }
        return viewType;
    }

    @Override
    public void onBindViewHolder(final AccessControlHolder holder, int position) {
        accessControl = accessControlList.get(position);
        holder.accessDateTime.setText(timeFormat.format(new Date(accessControl.getAccessDate())));
        if(!accessControl.isOnTheCloud()){
            holder.outOfSyncMark.setVisibility(View.VISIBLE);
        }else{
            holder.outOfSyncMark.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return accessControlList.size();
    }

    public void refreshList(List<AccessControl> accessControlList) {
        this.accessControlList = accessControlList;
        notifyDataSetChanged();
    }
}
