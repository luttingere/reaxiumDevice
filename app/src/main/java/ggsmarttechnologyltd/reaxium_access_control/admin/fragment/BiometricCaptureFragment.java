package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.CaptureFingerPrintThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.TakeImagesUtil;

/**
 * Created by Eduardo Luttinger  on 19/04/2016.
 */
public class BiometricCaptureFragment extends GGMainFragment {


    private static ImageView mFingerPrint;
    private Button mCaptureFingerprint;
    private static User mUserSelected;
    private static ImageLoader mImageLoader;
    private static ProgressBar mProgressBar;
    public static BiometricCaptureHandler handler;

    @Override
    public String getMyTag() {
        return BiometricCaptureFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return null;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.biometric_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }


    @Override
    protected void setViews(View view) {
        mCaptureFingerprint = (Button) view.findViewById(R.id.capture_fingerprint);
        mFingerPrint = (ImageView) view.findViewById(R.id.fingerprint);
        mImageLoader = MySingletonUtil.getInstance(getActivity()).getImageLoader();
        mProgressBar = (ProgressBar) view.findViewById(R.id.fingerprint_loader);
        mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);
        handler = new BiometricCaptureHandler();
    }

    @Override
    protected void setViewsEvents() {
        mCaptureFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureFingerPrintThread captureFingerPrintThread = new CaptureFingerPrintThread(App.fingerprintScanner, handler);
                showProgressDialog("Capturing Fingerprint...");
                captureFingerPrintThread.start();
            }
        });
    }

    @Override
    public void clearAllViewComponents() {

    }



    public static BiometricCaptureFragment getInstance() {
        return new BiometricCaptureFragment();
    }

    public static void setUserSelected(User user) {
        mUserSelected = user;
        if (mUserSelected.getFingerprint() != null && !"".equals(mUserSelected.getFingerprint())) {
            loadFingerPrintImage();
        }
    }

    private static void loadFingerPrintImage() {
        mProgressBar.setVisibility(View.VISIBLE);
        mImageLoader.get(mUserSelected.getFingerprint(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mFingerPrint.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private void associateBiometricToAUser(FingerPrint fingerPrint, Long deviceId) {
        showProgressDialog("Saving the biometric info...");
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                Type responseType = new TypeToken<ApiResponse<Object>>() {}.getType();
                ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                    GGUtil.showAToast(getActivity(),  apiResponse.getReaxiumResponse().getMessage());
                } else {
                    GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                GGUtil.showAToast(getActivity(), R.string.bad_connection_message);
            }
        };
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("biometricImage", TakeImagesUtil.getStringBase64Image(fingerPrint.getFingerPrintImage()));
            parameters.put("biometricImageName", mUserSelected.getDocumentId() + "_biometric.jpg");
            parameters.put("biometricHexaCode", GGUtil.ByteArrayToHexString(fingerPrint.getFingerPrintFeature()));
            parameters.put("user_id", mUserSelected.getUserId());
            parameters.put("device_id", deviceId);
        } catch (Exception e) {
            Log.e(TAG,"Error loading paremeters",e);
        }
        JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.LOAD_USER_BIOMETRIC_INFO), parameters, responseListener, errorListener);
        jsonObjectRequest.setShouldCache(false);
        MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * FingerPrint Handler
     */
    private class BiometricCaptureHandler extends ScannersActivityHandler {

        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {
            byte[] fpBmp = null;
            Bitmap bitmap = null;
            if (fingerprintImage != null && (fpBmp = fingerprintImage.convert2Bmp()) != null && (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) != null) {
                mFingerPrint.setImageBitmap(bitmap);
            } else {
                mFingerPrint.setImageResource(R.drawable.nofinger);
            }
        }

        @Override
        protected void saveFingerPrint(final FingerPrint fingerPrint) {
            hideProgressDialog();
            new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
            .setTitle("Device Access Association")
            .setMessage("The system will save the biometric information in the server, do you wanna also associate this user fingerprint to this device?")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    associateBiometricToAUser(fingerPrint, getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
                    dialog.dismiss();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    associateBiometricToAUser(fingerPrint, null);
                    dialog.dismiss();
                }
            }).show();
        }

        @Override
        protected void validateScannerResult(AppBean bean) {

        }

        @Override
        protected void errorRoutine(String message) {
            hideProgressDialog();
            GGUtil.showAToast(getActivity(), message);
        }
    }

}
