package ggsmarttechnologyltd.reaxium_access_control.login.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.ReaxiumDevice;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessData;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.routecontrol.activity.RouteControlActivity;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;
import ggsmarttechnologyltd.reaxium_access_control.viewprofile.activity.UserViewProfileActivity;

/**
 * Created by Eduardo Luttinger on 11/04/2016.
 *
 *  Reaxium device Login Screen
 *
 */
public class LoginActivity extends GGMainActivity {


    private EditText mUserNameInput;
    private EditText mPasswordInput;
    private Button mLoginFormSubmit;
    private SharedPreferenceUtil sharedPreferenceUtil;


    @Override
    protected Integer getMainLayout() {
        return R.layout.login_activity;
    }

    @Override
    protected void setViews() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(this);
        mUserNameInput = (EditText) findViewById(R.id.username_input);
        mPasswordInput = (EditText) findViewById(R.id.password_input);
        mLoginFormSubmit = (Button) findViewById(R.id.login_form_submit);
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
    }

    /**
     * Make a login to the system
     */
    private void login(){
        if(GGUtil.isNetworkAvailable(this)){
            if(!"".equals(mUserNameInput.getText().toString().trim()) && !"".equals(mPasswordInput.getText().toString().trim())){
                showProgressDialog(getString(R.string.progress_login_dialog_message));
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgressDialog();
                        Type responseType = new TypeToken<ApiResponse<UserAccessData>>() {}.getType();
                        ApiResponse<UserAccessData> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                        if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                            if(apiResponse.getReaxiumResponse().getObject() != null){
                                if(!apiResponse.getReaxiumResponse().getObject().isEmpty()){
                                    if(apiResponse.getReaxiumResponse().getObject().get(0).getUser() != null){
                                        storeUserLogin(apiResponse.getReaxiumResponse().getObject().get(0).getUser());
                                    }
                                }
                            }
                            GGUtil.goToScreen(LoginActivity.this,null, AdminActivity.class);
                        } else {
                            GGUtil.showAToast(LoginActivity.this, apiResponse.getReaxiumResponse().getMessage());
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        if (error.networkResponse != null && error.networkResponse.statusCode != 500) {
                            GGUtil.showAToast(LoginActivity.this, R.string.simple_exception_message);
                        }
                    }
                };
                JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.VALIDATE_ACCESS), loadLoginParameters(), responseListener, errorListener);
                jsonObjectRequest.setShouldCache(false);
                MySingletonUtil.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
            }else{
                GGUtil.showAToast(this,"Please fill login and password input fields");
            }
        }else{
            GGUtil.showAToast(this,R.string.no_network_available);
        }
    }


    private void storeUserLogin(User user){
        sharedPreferenceUtil.save(GGGlobalValues.USER_ID_IN_SESSION, user.getUserId());
        sharedPreferenceUtil.saveString(GGGlobalValues.USER_FULL_NAME_IN_SESSION, user.getFirstName() + " " + user.getFirstLastName());
        if(user.getUserType() != null){
            sharedPreferenceUtil.saveString(GGGlobalValues.USER_FULL_TYPE_IN_SESSION, user.getUserType().getUserTypeName());
        }
    }

    /**
     *
     * @return
     */
    private JSONObject loadLoginParameters(){
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject();
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject userAccessData = new JSONObject();
            if (sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID) != 0) {
                userAccessData.put("device_id",""+sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            }else{
                userAccessData.put("device_id",""+GGGlobalValues.DEFAULT_DEVICE_ID);
            }
            userAccessData.put("access_type_id",""+1);
            userAccessData.put("user_login_name",mUserNameInput.getText().toString());
            userAccessData.put("user_password",mPasswordInput.getText().toString());
            reaxiumParameters.put("UserAccessData",userAccessData);
            loginParams.put("ReaxiumParameters",reaxiumParameters);
        }catch (Exception e){
            GGUtil.showAToast(this,"Unknown error login, contact Reaxium Support");
            Log.e(TAG,"Error logint to the reaxium device",e);
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
}
