package ggsmarttechnologyltd.reaxium_access_control.admin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 26/04/2016.
 */
public class UserINFoundDialog extends Dialog {

    /**
     * Time to wait for dismiss the dialog
     */
    public static final Integer SLEEP_TIME = 2000;

    /**
     * User owner the info showed in the dialog
     */
    private User user;

    /**
     * contexto del sistema
     */
    private Context context;


    private TextView userName;
    private TextView userDocumentId;
    private TextView userBusinessName;
    private ProgressBar userPhotoLoader;
    private ImageView userPhoto;
    private ImageLoader mImageLoader;
    private ImageView closeDialogButton;



    public UserINFoundDialog(Context context) {
        super(context);
        this.context = context;
    }

    public UserINFoundDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected UserINFoundDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_in_found_dialog);
        setViews();
        setEvents();
    }

    private void setViews() {
        closeDialogButton = (ImageView) findViewById(R.id.close_dialog);
        userName = (TextView) findViewById(R.id.username_input);
        userDocumentId = (TextView) findViewById(R.id.user_document_id);
        userBusinessName = (TextView) findViewById(R.id.user_business_name);
        userPhotoLoader = (ProgressBar) findViewById(R.id.user_image_loader);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        userPhotoLoader.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);
        mImageLoader = MySingletonUtil.getInstance(context).getImageLoader();
        fillFields();
    }

    private void setEvents(){
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
        if (user.getBusiness() != null) {
            userBusinessName.setText(user.getBusiness().getBusinessName());
        }
        setUserPhoto(user);
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
