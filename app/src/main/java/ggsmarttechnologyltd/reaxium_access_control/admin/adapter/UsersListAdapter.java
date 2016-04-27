package ggsmarttechnologyltd.reaxium_access_control.admin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.AccessControlFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.holder.UserHolder;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnUserClickListener;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UserHolder> {

    private Context mContext;
    private List<User> mListOfUsers = new ArrayList<>();
    private OnUserClickListener onUserClickListener;
    private ImageLoader mImageLoader;
    private String name = "";


    public UsersListAdapter(Context mContext,OnUserClickListener onUserClickListener ,List<User> mListOfUsers, String listName) {
        this.mContext = mContext;
        this.onUserClickListener = onUserClickListener;
        this.mListOfUsers = mListOfUsers == null?this.mListOfUsers:mListOfUsers;
        mImageLoader = MySingletonUtil.getInstance(mContext).getImageLoader();
        this.name = listName;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = null;
        switch (this.name){
            case AccessControlFragment.LIST_IN_NAME:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_in_list_item, null);
                break;
            case AccessControlFragment.LIST_OUT_NAME:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_out_list_item, null);
                break;
            default:
                itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, null);
                break;
        }
        return new UserHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        User user = mListOfUsers.get(position);
        final int actualPosition = position;
        if (user != null) {
            holder.userFullName.setText(user.getFirstName() + " " + user.getSecondName() + " " + user.getFirstLastName() + " " + user.getSecondLastName());
            holder.userIdNumber.setText(user.getDocumentId());
            holder.mUserInfoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"".equals(name)) {
                        onUserClickListener.onUserClicked(actualPosition, name);
                    } else {
                        onUserClickListener.onUserClicked(actualPosition);
                    }
                }
            });
            if(holder.accessTime != null){
                holder.accessTime.setText(user.getAccessTime());
            }
            if(user.getPhoto() != null && !"".equals(user.getPhoto())){
                mImageLoader.get(user.getPhoto(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        Bitmap bitmap = response.getBitmap();
                        if (bitmap != null) {
                            holder.userPhoto.setImageBitmap(bitmap);
                        }
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListOfUsers.size();
    }

    public void refreshList(List<User> userList) {
        mListOfUsers = userList;
        notifyDataSetChanged();
    }
}
