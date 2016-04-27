package ggsmarttechnologyltd.reaxium_access_control.admin.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class UserHolder extends RecyclerView.ViewHolder {

    public LinearLayout mUserInfoContainer;
    public ImageView userPhoto;
    public TextView userFullName;
    public TextView userIdNumber;
    public TextView accessTime;


    public UserHolder(View itemView) {
        super(itemView);
        setViews(itemView);
    }

    private void setViews(View view){
        mUserInfoContainer = (LinearLayout) view.findViewById(R.id.user_info_container);
        userPhoto = (ImageView) view.findViewById(R.id.user_photo);
        userFullName = (TextView) view.findViewById(R.id.user_full_name);
        userIdNumber = (TextView) view.findViewById(R.id.user_id_number);
        if(view.findViewById(R.id.access_time) != null){
            accessTime = (TextView) view.findViewById(R.id.access_time);
        }
    }

}
