package ggsmarttechnologyltd.reaxium_access_control.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger at G&G on 14/04/2016.
 */
public class DriverScreenFragment extends GGMainFragment {

    private TextView driverName;
    private TextView driverLicenceNumber;
    private TextView busNumber;
    private RelativeLayout mGoButton;
    private User driver;
    private ImageLoader imageLoader;
    private ImageView driverPhoto;
    private ProgressBar userPhotoLoader;


    @Override
    public String getMyTag() {
        return DriverScreenFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.driver_screen;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.driver_screen_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        ((MainActivity)getActivity()).hideBackButton();
        driverName = (TextView) view.findViewById(R.id.username_input);
        driverLicenceNumber = (TextView) view.findViewById(R.id.user_document_id);
        busNumber = (TextView) view.findViewById(R.id.bus_number);
        userPhotoLoader = (ProgressBar) view.findViewById(R.id.user_image_loader);
        driverPhoto = (ImageView) view.findViewById(R.id.user_photo);
        mGoButton = (RelativeLayout) view.findViewById(R.id.go_button);
        Bundle bundle = getArguments();
        driver = (User) bundle.getSerializable("USER_VALUE");
        driverName.setText(driver.getFirstName()+ " "+driver.getFirstLastName());
        driverLicenceNumber.setText(driver.getDocumentId());
        imageLoader = MySingletonUtil.getInstance(getActivity()).getImageLoader();
        userPhotoLoader.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);
        setDriverPhoto(driver);
    }

    @Override
    protected void setViewsEvents() {
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putSerializable("USER_VALUE",driver);
                ((MainActivity)getActivity()).runMyFragment(new RoutesScreenFragment(),params);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BusScreenFragment.busPositionRouteAlreadyPainted = Boolean.FALSE;
    }

    @Override
    public void clearAllViewComponents() {

    }

    private void setDriverPhoto(User user) {
        driverPhoto.setImageResource(R.drawable.driver_icon_profile);
        userPhotoLoader.setVisibility(View.VISIBLE);
        if (user.getPhoto() != null && !"".equals(user.getPhoto())) {
            imageLoader.get(user.getPhoto(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        userPhotoLoader.setVisibility(View.GONE);
                        driverPhoto.setImageBitmap(bitmap);
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
