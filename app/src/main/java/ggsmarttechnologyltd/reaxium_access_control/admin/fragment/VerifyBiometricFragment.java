package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticCardValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticFingerPrintValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitScannersInAutoModeThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitFingerPrintThread;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class VerifyBiometricFragment extends GGMainFragment {

    private TextView userName;
    private TextView userDocumentId;
    private TextView userBusinessName;
    private ProgressBar userPhotoLoader;
    private ImageView userPhoto;
    private ImageLoader mImageLoader;
    private RelativeLayout userInfoContainer;
    private TextView infoText;
    private ScannerValidationHandler handler;

    @Override
    public String getMyTag() {
        return VerifyBiometricFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.menu_drawer_verify_biometric;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.validate_fingerprint;
    }

    @Override
    public Boolean onBackPressed() {
        if(userInfoContainer.getVisibility() == View.VISIBLE){
            userInfoContainer.setVisibility(View.GONE);
            infoText.setVisibility(View.VISIBLE);
        }else{
            ((AdminActivity) getActivity()).runMyFragment(new AdminFragment(), null, R.id.action_menu_home);
        }
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        ((AdminActivity) getActivity()).showBackButton();
        userName = (TextView) view.findViewById(R.id.username_input);
        userDocumentId = (TextView) view.findViewById(R.id.user_document_id);
        userBusinessName = (TextView) view.findViewById(R.id.user_business_name);
        userPhotoLoader = (ProgressBar) view.findViewById(R.id.user_image_loader);
        userPhoto = (ImageView) view.findViewById(R.id.user_photo);
        userPhotoLoader.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);
        mImageLoader = MySingletonUtil.getInstance(getActivity()).getImageLoader();
        userInfoContainer = (RelativeLayout) view.findViewById(R.id.user_info_container);
        infoText = (TextView) view.findViewById(R.id.info_text);
    }

    @Override
    protected void setViewsEvents() {

    }

    @Override
    public void onResume() {
        super.onResume();
        handler = new ScannerValidationHandler();
        InitScannersInAutoModeThread initScannersInAutoModeThread = new InitScannersInAutoModeThread(getActivity(),handler);
        initScannersInAutoModeThread.start();
    }

    @Override
    public void onPause() {
        //AutomaticCardValidationThread.stopScanner();
        AutomaticFingerPrintValidationThread.stopScanner();
        GGUtil.closeFingerPrint();
        //GGUtil.closeCardReader(getActivity(),handler);
        Log.i(TAG, "the scanner has been turned off successfully");
        super.onPause();
    }


    @Override
    public void clearAllViewComponents() {

    }

    private class ScannerValidationHandler extends ScannersActivityHandler {

        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateScannerResult(AppBean bean) {
            SecurityObject securityObject = (SecurityObject)bean;
            User user = ReaxiumUsersDAO.getInstance(getActivity()).getUserById("" + securityObject.getUserId());
            hideProgressDialog();
            if (user != null) {
                userInfoContainer.setVisibility(View.VISIBLE);
                infoText.setVisibility(View.GONE);
                userName.setText(user.getFirstName() + " " + user.getSecondName() + " " + user.getFirstLastName() + " " + user.getSecondLastName());
                userDocumentId.setText(user.getDocumentId());
                if (user.getBusiness() != null) {
                    userBusinessName.setText(user.getBusiness().getBusinessName());
                }
                setUserPhoto(user);
            } else {
                FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                GGUtil.showAToast(getActivity(), "Invalid user, not registered: " + securityObject.getUserId());
            }
        }

        @Override
        protected void errorRoutine(String message) {
            hideProgressDialog();
            GGUtil.showAToast(getActivity(), message);
        }
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
