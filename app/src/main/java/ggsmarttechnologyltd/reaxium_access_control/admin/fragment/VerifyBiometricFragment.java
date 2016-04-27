package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticFingerPrintValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.FingerPrintHandler;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.VerifyFingerPrintThread;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SuccessfulAccessPlayerSingleton;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class VerifyBiometricFragment extends GGMainFragment {

    private TextView userName;
    private TextView userDocumentId;
    private TextView userBusinessName;
    private ProgressBar userPhotoLoader;
    private ImageView userPhoto;
    private Boolean isScanning = Boolean.FALSE;
    private ImageLoader mImageLoader;

    @Override
    public String getMyTag() {
        return VerifyBiometricFragment.class.getName();
    }

    @Override
    protected Integer getToolbarTitle() {
        return R.string.menu_drawer_verify_biometric;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.validate_fingerprint;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {

        if(App.fingerprintScanner == null){
            App.fingerprintScanner = FingerprintScanner.getInstance();
       }
        userName = (TextView) view.findViewById(R.id.username_input);
        userDocumentId = (TextView) view.findViewById(R.id.user_document_id);
        userBusinessName = (TextView) view.findViewById(R.id.user_business_name);
        userPhotoLoader = (ProgressBar) view.findViewById(R.id.user_image_loader);
        userPhoto = (ImageView)view.findViewById(R.id.user_photo);
        userPhotoLoader.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);
        mImageLoader = MySingletonUtil.getInstance(getActivity()).getImageLoader();

    }

    @Override
    protected void setViewsEvents() {

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            App.fingerprintScanner.powerOn();
        }catch (Exception e){
            Log.i(TAG,"",e);
        }
        isScanning = Boolean.TRUE;
        BiometricValidationHandler handler = new BiometricValidationHandler();
        AutomaticFingerPrintValidationThread automaticFingerPrintValidationThread = new AutomaticFingerPrintValidationThread(App.fingerprintScanner, handler,getActivity());
        automaticFingerPrintValidationThread.start();

    }

    @Override
    public void onPause() {
        AutomaticFingerPrintValidationThread.stopScanner();
        if (!isScanning) {
            App.fingerprintScanner.powerOff();
            Log.i(TAG, "Automatic detection of a fingerprint was stoped");
        }
        super.onPause();
    }


    private void validateFingerPrint(){
        try {
            App.fingerprintScanner.powerOn();
        }catch (Exception e){
            Log.i(TAG,"",e);
        }
        isScanning = Boolean.TRUE;
        BiometricValidationHandler handler = new BiometricValidationHandler();
        VerifyFingerPrintThread verifyFingerPrintThread = new VerifyFingerPrintThread(App.fingerprintScanner, handler,getActivity());
        showProgressDialog("Validating Fingerprint...");
        verifyFingerPrintThread.start();
    }

    @Override
    public void clearAllViewComponents() {

    }

    private class BiometricValidationHandler extends FingerPrintHandler {

        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateFingerPrint(Integer userId) {
            User user = ReaxiumUsersDAO.getInstance(getActivity()).getUserById(""+userId);
            hideProgressDialog();
            if(user !=null){
                userName.setText(user.getFirstName() + " " + user.getSecondName() + " " + user.getFirstLastName() + " " + user.getSecondLastName());
                userDocumentId.setText(user.getDocumentId());
                if(user.getBusiness() != null){
                    userBusinessName.setText(user.getBusiness().getBusinessName());
                }
                setUserPhoto(user);
                //SuccessfulAccessPlayerSingleton.getInstance(getActivity()).stopRingTone();
            }else{
                FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                GGUtil.showAToast(getActivity(), "Invalid user, not registered: "+userId);
            }
        }

        @Override
        protected void errorRoutine(String message) {
            hideProgressDialog();
            isScanning = Boolean.FALSE;
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
        }else{
            userPhotoLoader.setVisibility(View.INVISIBLE);
        }
    }

}
