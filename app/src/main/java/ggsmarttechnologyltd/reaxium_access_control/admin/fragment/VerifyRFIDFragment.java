package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticCardValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitScannersInAutoModeThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SuccessfulAccessPlayerSingleton;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class VerifyRFIDFragment extends GGMainFragment {

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
        return VerifyRFIDFragment.class.getName();
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
        if (userInfoContainer.getVisibility() == View.VISIBLE) {
            userInfoContainer.setVisibility(View.GONE);
            infoText.setVisibility(View.VISIBLE);
        } else {
             AutomaticCardValidationThread.stopScanner();
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
        InitScannersInAutoModeThread initScannersInAutoModeThread = new InitScannersInAutoModeThread(getActivity(), handler);
        initScannersInAutoModeThread.start();
    }

    @Override
    public void onPause() {
//        AutomaticFingerPrintValidationThread.stopScanner();
//        GGUtil.closeFingerPrint();
        AutomaticCardValidationThread.stopScanner();
        Log.i(TAG, "the scanner has been turned off successfully");
        super.onPause();
    }

    private void validateRFIDCard(String rfidCode) {
        if (GGUtil.isNetworkAvailable(getActivity())) {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressDialog();
                    Type responseType = new TypeToken<ApiResponse<User>>() {
                    }.getType();
                    ApiResponse<User> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        SuccessfulAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                        User user = apiResponse.getReaxiumResponse().getObject().get(0);
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
                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    }
                    initScanner();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    initScanner();
                    GGUtil.showAToast(getActivity(), R.string.bad_connection_message);
                }
            };
            showProgressDialog("Looking for this card in our cloud...");
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.VALIDATE_RFID_CARD), loadValidateRFIDParams(rfidCode), responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }

    private void initScanner() {
        InitScannersInAutoModeThread initScannersInAutoModeThread = new InitScannersInAutoModeThread(getActivity(), handler);
        initScannersInAutoModeThread.start();
    }

    private void resetRFIDCard(String rfidCode) {
        if (GGUtil.isNetworkAvailable(getActivity())) {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressDialog();
                    Type responseType = new TypeToken<ApiResponse<User>>() {
                    }.getType();
                    ApiResponse<User> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() != GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                    } else {
                        SuccessfulAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                    }
                    GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    initScanner();
                }

            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    GGUtil.showAToast(getActivity(), R.string.bad_connection_message);
                    initScanner();
                }
            };
            showProgressDialog("Restoring the rfid card in server...");
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.RESET_RFID_CARD), loadValidateRFIDParams(rfidCode), responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }

    private JSONObject loadValidateRFIDParams(String rfidCode) {
        JSONObject parameters = new JSONObject();
        try {
            JSONObject ReaxiumParameters = new JSONObject();
            JSONObject RFIDValidation = new JSONObject();
            RFIDValidation.put("rfid_code", rfidCode);
            ReaxiumParameters.put("RFIDValidation", RFIDValidation);
            parameters.put("ReaxiumParameters", ReaxiumParameters);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return parameters;
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
            final SecurityObject securityObject = (SecurityObject) bean;
            AutomaticCardValidationThread.stopScanner();
            GGUtil.closeCardReader();
            new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                    .setTitle("RFID MANAGEMENT")
                    .setMessage("Do you wanna check this RFID CARD or Reset it?")
                    .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            validateRFIDCard("" + securityObject.getCardId());
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetRFIDCard("" + securityObject.getCardId());
                                    dialog.dismiss();
                                }
                            }
                    ).setOnCancelListener(new DialogInterface.OnCancelListener() {
                         @Override
                        public void onCancel(DialogInterface dialog) {
                            initScanner();
                        }
            }).show();
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
