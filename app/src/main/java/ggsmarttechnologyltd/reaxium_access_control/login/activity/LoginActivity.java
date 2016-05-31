package ggsmarttechnologyltd.reaxium_access_control.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.BiometricCaptureFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.RFIDCaptureFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticCardValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitCardReaderThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitScannersThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.DeviceData;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.LoginObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.ReaxiumDevice;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessData;
import ggsmarttechnologyltd.reaxium_access_control.controller.SynchronizeController;
import ggsmarttechnologyltd.reaxium_access_control.database.AccessControlDAO;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

import ggsmarttechnologyltd.reaxium_access_control.service.PushUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SuccessfulAccessPlayerSingleton;


/**
 * Created by Eduardo Luttinger on 11/04/2016.
 * <p/>
 * Reaxium device Login Screen
 */
public class LoginActivity extends GGMainActivity {


    private EditText mUserNameInput;
    private EditText mPasswordInput;
    private RelativeLayout mLoginFormSubmit;
    private RelativeLayout allContainer;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private LoginWithRFIDHandler loginWithRFIDHandler;
    private static Boolean isRfidEnabled = Boolean.FALSE;
    private AccessControlDAO accessControlDAO;
    private List<AccessControl> outOfSyncList;
    private SynchronizeController synchronizeController;


    @Override
    protected Integer getMainLayout() {
        return R.layout.login_activity;
    }

    @Override
    protected void setViews() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(this);
        mUserNameInput = (EditText) findViewById(R.id.username_input);
        mPasswordInput = (EditText) findViewById(R.id.password_input);
        mLoginFormSubmit = (RelativeLayout) findViewById(R.id.login_container);
        allContainer = (RelativeLayout) findViewById(R.id.allContainer);
        loginWithRFIDHandler = new LoginWithRFIDHandler();
        synchronizeController = new SynchronizeController(this, this);
        GGUtil.hideKeyboard(this);
    }

    @Override
    protected void setViewsEvents() {
        /**
         * duties when the login submit button is clicked.
         */
        mLoginFormSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        allContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GGUtil.hideKeyboard(LoginActivity.this);
            }
        });
    }

    @Override
    protected void onStop() {
        closeRfidReader();
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        accessControlDAO = AccessControlDAO.getInstance(this);
        outOfSyncList = accessControlDAO.getAllAccessOutOfSync();
        InitCardReaderThread cardReaderThread = new InitCardReaderThread(this, loginWithRFIDHandler);
        cardReaderThread.start();
    }

    private void closeRfidReader() {
        try {
            AutomaticCardValidationThread.stopScanner();
            Log.i(TAG, "card reader closed successfully");
        } catch (Error e) {
            Log.e(TAG, "", e);
        }
    }

    /**
     * Make a login to the system
     */
    private void login() {
        if (GGUtil.isNetworkAvailable(this)) {
            if (!"".equals(mUserNameInput.getText().toString().trim()) && !"".equals(mPasswordInput.getText().toString().trim())) {
                showProgressDialog(getString(R.string.progress_login_dialog_message));
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialog();
                        Type responseType = new TypeToken<ApiResponse<LoginObject>>() {}.getType();
                        ApiResponse<LoginObject> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                        if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                            if (apiResponse.getReaxiumResponse().getObject() != null && !apiResponse.getReaxiumResponse().getObject().isEmpty()) {

                                LoginObject loginObject = apiResponse.getReaxiumResponse().getObject().get(0);

                                if (loginObject.getUser() != null) {

                                    final User user = apiResponse.getReaxiumResponse().getObject().get(0).getUser();
                                    storeUserLogin(user);


                                    if (user.getUserType().getUserTypeName().equalsIgnoreCase("ADMINISTRATOR")) {

                                        closeRfidReader();
                                        GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                                        GGUtil.goToScreen(LoginActivity.this, null, AdminActivity.class);

                                    } else if (user.getUserType().getUserTypeName().equalsIgnoreCase("DRIVER")) {

                                        closeRfidReader();
                                        Log.i(TAG,"running synchronize process");
                                        Boolean result = Boolean.TRUE;
                                        if(apiResponse.getReaxiumResponse().getObject().get(0).getDeviceData() != null){
                                            DeviceData deviceData = apiResponse.getReaxiumResponse().getObject().get(0).getDeviceData();
                                            result = synchronizeController.synchronizeDevice(deviceData, outOfSyncList);
                                        }
                                        if(!result){
                                            GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage()+" Synchronize Failure error.");
                                        }else{
                                            GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                                        }
                                        Bundle arguments = new Bundle();
                                        arguments.putSerializable("USER_VALUE", user);
                                        GGUtil.goToScreen(LoginActivity.this, arguments, MainActivity.class);

                                    } else {
                                        GGUtil.showAShortToast(LoginActivity.this, "Invalid User Account");
                                    }
                                } else {
                                    GGUtil.showAToast(LoginActivity.this, "Invalid user information, contact with Reaxium Support");
                                }
                            } else {
                                GGUtil.showAToast(LoginActivity.this, "Invalid user information, contact with Reaxium Support");
                            }
                        } else {
                            GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        GGUtil.showAToast(LoginActivity.this, R.string.bad_connection_message);
                    }
                };
                JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.VALIDATE_ACCESS), loadLoginParameters(), responseListener, errorListener);
                jsonObjectRequest.setShouldCache(false);
                MySingletonUtil.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
            } else {
                GGUtil.showAShortToast(this, "Please fill login and password input fields");
            }
        } else {
            GGUtil.showAToast(this, R.string.no_network_available);
        }
    }

    private void goToDriverScreen(User user) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("USER_VALUE", user);
        GGUtil.goToScreen(LoginActivity.this, arguments, MainActivity.class);
    }

    private void goToAdminScreen() {
        GGUtil.goToScreen(this, null, AdminActivity.class);
    }

    /**
     * Make a login to the system
     */
    private void loginWithRFID(Integer userId, Long cardId) {
        if (GGUtil.isNetworkAvailable(this)) {
            if (userId != null && cardId != null) {
                showProgressDialog(getString(R.string.progress_login_dialog_message));
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialog();
                        Type responseType = new TypeToken<ApiResponse<LoginObject>>() {}.getType();
                        ApiResponse<LoginObject> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                        if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                            if (apiResponse.getReaxiumResponse().getObject() != null && !apiResponse.getReaxiumResponse().getObject().isEmpty()) {

                                LoginObject loginObject = apiResponse.getReaxiumResponse().getObject().get(0);
                                if (loginObject.getUser() != null) {

                                    User user = loginObject.getUser();

                                    storeUserLogin(user);


                                    if (user.getUserType().getUserTypeName().equalsIgnoreCase("ADMINISTRATOR")) {

                                        closeRfidReader();
                                        SuccessfulAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                                        GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                                        goToAdminScreen();


                                    } else if (user.getUserType().getUserTypeName().equalsIgnoreCase("DRIVER")) {

                                        closeRfidReader();
                                        SuccessfulAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                                        Log.i(TAG,"running synchronize process");
                                        Boolean result = Boolean.TRUE;
                                        if(apiResponse.getReaxiumResponse().getObject().get(0).getDeviceData() != null){
                                            DeviceData deviceData = apiResponse.getReaxiumResponse().getObject().get(0).getDeviceData();
                                            result = synchronizeController.synchronizeDevice(deviceData, outOfSyncList);
                                        }
                                        if(!result){
                                            GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage()+" Synchronize Failure error.");
                                        }else{
                                            GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                                        }
                                        goToDriverScreen(user);

                                    } else {
                                        FailureAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                                        GGUtil.showAShortToast(LoginActivity.this, "Invalid User");
                                    }

                                }else{
                                    FailureAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                                    GGUtil.showAShortToast(LoginActivity.this, "Invalid user information, contact with Reaxium Support");
                                }
                            } else {
                                FailureAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                                GGUtil.showAShortToast(LoginActivity.this, "Invalid user");
                            }
                        } else {
                            FailureAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                            GGUtil.showAShortToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        GGUtil.showAToast(LoginActivity.this, R.string.bad_connection_message);
                    }
                };
                JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.VALIDATE_ACCESS_WITH_RFID), loadRFIDLoginParameters(userId, cardId), responseListener, errorListener);
                jsonObjectRequest.setShouldCache(false);
                MySingletonUtil.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
            } else {
                GGUtil.showAShortToast(this, "non user id found in the card");
            }
        } else {
            GGUtil.showAToast(this, R.string.no_network_available);
        }
    }


    private void storeUserLogin(User user) {
        sharedPreferenceUtil.save(GGGlobalValues.USER_ID_IN_SESSION, user.getUserId());
        sharedPreferenceUtil.saveString(GGGlobalValues.USER_FULL_NAME_IN_SESSION, user.getFirstName() + " " + user.getFirstLastName());
        if (user.getUserType() != null) {
            sharedPreferenceUtil.saveString(GGGlobalValues.USER_FULL_TYPE_IN_SESSION, user.getUserType().getUserTypeName());
        }
    }

    @Override
    public void onBackPressed() {
            finish();
    }

    /**
     * @return
     */
    private JSONObject loadLoginParameters() {
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject();
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject userAccessData = new JSONObject();
            if (sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID) != 0) {
                userAccessData.put("device_id", "" + sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            } else {
                userAccessData.put("device_id", "" + GGGlobalValues.DEFAULT_DEVICE_ID);
            }
            if (outOfSyncList != null && !outOfSyncList.isEmpty()) {
                JSONObject accessData;
                JSONArray accessDataBulk = new JSONArray();
                for (AccessControl accessControl : outOfSyncList) {
                    accessData = new JSONObject();
                    accessData.put("userId", accessControl.getUserId());
                    accessData.put("deviceId", accessControl.getDeviceId());
                    accessData.put("accessType", accessControl.getAccessType());
                    accessData.put("userAccessType", accessControl.getUserAccessType());
                    accessDataBulk.put(accessData);
                }
                userAccessData.put("accessBulkObject", accessDataBulk);
            }
            userAccessData.put("device_token", PushUtil.getRegistrationId(this));
            userAccessData.put("user_name", mUserNameInput.getText().toString());
            userAccessData.put("user_password", mPasswordInput.getText().toString());
            reaxiumParameters.put("UserAccess", userAccessData);
            loginParams.put("ReaxiumParameters", reaxiumParameters);
        } catch (Exception e) {
            Log.e(TAG, "Error logint to the reaxium device", e);
        }
        return loginParams;
    }


    /**
     * @return
     */
    private JSONObject loadRFIDLoginParameters(Integer userId, Long cardId) {
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject();
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject userAccess = new JSONObject();

            userAccess.put("user_id", userId.intValue());
            if (sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID) != 0) {
                userAccess.put("device_id", "" + sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            } else {
                userAccess.put("device_id", "" + GGGlobalValues.DEFAULT_DEVICE_ID);
            }
            userAccess.put("card_code", cardId.longValue());
            if (outOfSyncList != null && !outOfSyncList.isEmpty()) {

                JSONObject accessData;
                JSONArray accessDataBulk = new JSONArray();
                for (AccessControl accessControl : outOfSyncList) {
                    accessData = new JSONObject();
                    accessData.put("userId", accessControl.getUserId());
                    accessData.put("deviceId", accessControl.getDeviceId());
                    accessData.put("accessType", accessControl.getAccessType());
                    accessData.put("userAccessType", accessControl.getUserAccessType());
                    accessDataBulk.put(accessData);
                }
                userAccess.put("accessBulkObject", accessDataBulk);
            }
            userAccess.put("device_token", PushUtil.getRegistrationId(this));

            reaxiumParameters.put("UserAccess", userAccess);
            loginParams.put("ReaxiumParameters", reaxiumParameters);
        } catch (Exception e) {
            GGUtil.showAToast(this, "Unknown error login, contact Reaxium Support");
            Log.e(TAG, "Error logint to the reaxium device", e);
        }
        return loginParams;
    }

    @Override
    protected GGMainFragment getMainFragment() {
        return null;
    }

    @Override
    protected Integer getMainToolbarId() {
        return null;
    }

    private class LoginWithRFIDHandler extends ScannersActivityHandler {

        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateScannerResult(AppBean bean) {
            SecurityObject securityObject = (SecurityObject) bean;
            if (securityObject != null) {
                loginWithRFID(securityObject.getUserId(), securityObject.getCardId());
            } else {
                FailureAccessPlayerSingleton.getInstance(LoginActivity.this).initRingTone();
                GGUtil.showAShortToast(LoginActivity.this, "Invalid userId and CardId");
            }
        }

        @Override
        protected void errorRoutine(String message) {
            GGUtil.showAShortToast(LoginActivity.this, message);
        }
    }

}
