package ggsmarttechnologyltd.reaxium_access_control.admin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.AccessControlListAdapter;
import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.AccessControlDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 28/04/2016.
 */
public class UserINOUTInfoDialog extends Dialog {

    private TextView userName;
    private TextView userDocumentId;
    private ProgressBar userPhotoLoader;
    private ImageView userPhoto;
    private ImageLoader mImageLoader;
    private RelativeLayout closeDialogButton;
    private RecyclerView accessInfoList;
    private LinearLayoutManager linearLayoutManager;
    private List<AccessControl> accessControlList = new ArrayList<>();
    private User user;
    private AccessControlListAdapter accessControlListAdapter;
    private AccessControlDAO accessControlDAO;


    public UserINOUTInfoDialog(Context context) {
        super(context);
    }

    public UserINOUTInfoDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UserINOUTInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_access_control_information);
        setViews();
        setViewEvents();
    }

    private void setViews(){
        accessInfoList = (RecyclerView)findViewById(R.id.access_info_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        accessInfoList.setLayoutManager(linearLayoutManager);
        closeDialogButton = (RelativeLayout) findViewById(R.id.close_container);
        userName = (TextView) findViewById(R.id.user_name);
        userDocumentId = (TextView) findViewById(R.id.user_document_id);
        userPhotoLoader = (ProgressBar) findViewById(R.id.user_image_loader);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        userPhotoLoader.getIndeterminateDrawable().setColorFilter(getContext().getResources().getColor(R.color.colorPrimaryLight), android.graphics.PorterDuff.Mode.SRC_ATOP);
        mImageLoader = MySingletonUtil.getInstance(getContext()).getImageLoader();
        fillFields();
        accessControlListAdapter = new AccessControlListAdapter(getContext(),accessControlList);
        accessInfoList.setAdapter(accessControlListAdapter);
    }

    private void setViewEvents(){
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void updateUserInfo(User user) {
        this.user = user;
    }

    private void fillFields(){
        userName.setText(user.getFirstName() + " " + user.getSecondName() + " " + user.getFirstLastName() + " " + user.getSecondLastName());
        userDocumentId.setText(user.getDocumentId());
        setUserPhoto(user);
        accessControlDAO = AccessControlDAO.getInstance(getContext());
        accessControlList = accessControlDAO.getAllAccess(user.getUserId());
    }

    private void setUserPhoto(User user) {
        userPhoto.setImageResource(R.drawable.user_avatar);
        userPhotoLoader.setVisibility(View.VISIBLE);
        if (user.getPhoto() != null && !"".equals(user.getPhoto())) {
            mImageLoader.get(user.getPhoto(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        userPhotoLoader.setVisibility(View.GONE);
                        userPhoto.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    userPhotoLoader.setVisibility(View.GONE);
                }
            });
        } else {
            userPhotoLoader.setVisibility(View.INVISIBLE);
        }
    }

}
