package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.BiometricData;
import ggsmarttechnologyltd.reaxium_access_control.beans.ReaxiumDevice;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessData;
import ggsmarttechnologyltd.reaxium_access_control.database.BiometricDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class ConfigureDeviceFragment extends GGMainFragment {

    private EditText mDeviceIdInput;
    private Button mConfigureDeviceButton;
    private ImageButton mLockConfiguration;
    private TextView mDeviceName;
    private TextView mDeviceDescription;
    private TextView mDeviceStatus;
    private TextView mApplicationName;
    private TextView mApplicationVersion;
    private TextView mLastSync;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private RelativeLayout mSynchronizeButton;


    @Override
    public String getMyTag() {
        return ConfigureDeviceFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.menu_drawer_configure_device;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.configure_device_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        ((AdminActivity)getActivity()).runMyFragment(new AdminFragment(),null,R.id.action_menu_home);
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        ((AdminActivity)getActivity()).showBackButton();
        mDeviceIdInput = (EditText) view.findViewById(R.id.device_id_input);
        mConfigureDeviceButton = (Button) view.findViewById(R.id.configure_deice_button);
        mLockConfiguration = (ImageButton) view.findViewById(R.id.unlock_configuration);
        mDeviceName = (TextView) view.findViewById(R.id.reaxium_device_name);
        mDeviceDescription = (TextView) view.findViewById(R.id.reaxium_device_description);
        mDeviceStatus = (TextView) view.findViewById(R.id.reaxium_device_status);
        mApplicationName = (TextView) view.findViewById(R.id.reaxium_device_app_name);
        mApplicationVersion = (TextView) view.findViewById(R.id.reaxium_device_app_version);
        mLastSync = (TextView) view.findViewById(R.id.reaxium_device_last_sync);
        mSynchronizeButton = (RelativeLayout) view.findViewById(R.id.synchronize_device_container);
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(getActivity());
        fillFields();
    }

    @Override
    protected void setViewsEvents() {

        //Lock or unlock configuration routine
        mLockConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockOrUnlockConfiguration();
            }
        });

        mConfigureDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureDevice();
            }
        });

        mSynchronizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronizeDevice();
            }
        });


    }

    @Override
    public void clearAllViewComponents() {

    }

    /**
     * get all the reaxium users associated with this device and store all personal and biometric information the device local bd
     */
    private void synchronizeDevice() {
        if (GGUtil.isNetworkAvailable(getActivity())) {

            showProgressDialog(getString(R.string.synchronize_device_progress_message));

            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    changeProgressDialogMessage(getString(R.string.synchronize_device_users_progress_message));

                    Type responseType = new TypeToken<ApiResponse<UserAccessControl>>() {}.getType();

                    ApiResponse<UserAccessControl> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);

                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {

                        if(apiResponse.getReaxiumResponse().getObject() != null){

                            ReaxiumUsersDAO dao = ReaxiumUsersDAO.getInstance(getActivity());

                            Log.i(TAG,"prepared to clear all values from the reaxium users");
                            dao.deleteAllValuesFromReaxiumUserTable();

                            Log.i(TAG, "prepared to fill reaxium users table");
                            Boolean fillOk = dao.fillUsersTable(apiResponse.getReaxiumResponse().getObject());

                            if (fillOk) {
                                changeProgressDialogMessage(getString(R.string.synchronize_device_biometrics_progress_message));
                                Log.i(TAG, "prepared to extract biometric data to the device db");
                                List<BiometricData> biometricDataList = dao.getUsersBiometricData();

                                if (biometricDataList != null) {
                                    Log.i(TAG, "biometric data retrieved successfully, biometric count: " + biometricDataList.size());
                                    Boolean enrollOk = BiometricDAO.storeBiometrics(biometricDataList);

                                    if (enrollOk) {

                                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());

                                    } else {

                                        GGUtil.showAToast(getActivity(), "Error enrrolling biometric info in the device");

                                    }
                                } else {

                                    GGUtil.showAToast(getActivity(), "Error getting the biometric info from device db");

                                }
                            } else {

                                GGUtil.showAToast(getActivity(), "Error saving the reaxium users data in device db");

                            }
                        }else{
                            GGUtil.showAToast(getActivity(), "No users configured for this device");
                        }

                    } else {

                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());

                    }
                    hideProgressDialog();
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    if (error.networkResponse != null && error.networkResponse.statusCode != 500) {
                        GGUtil.showAToast(getActivity(), R.string.simple_exception_message);
                    }
                }
            };

            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SYNCHRONIZE_DEVICE), loadConfigureDeviceParatemers(), responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }


    /**
     * fill de fileds with the device configuration stored in cache
     */
    private void fillFields() {
        if (sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID) != 0) {
            mDeviceIdInput.setText("" + sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
            mDeviceName.setText(sharedPreferenceUtil.getString(GGGlobalValues.DEVICE_NAME));
            mDeviceDescription.setText(sharedPreferenceUtil.getString(GGGlobalValues.DEVICE_DESCRIPTION));
            mDeviceStatus.setText(sharedPreferenceUtil.getString(GGGlobalValues.DEVICE_STATUS));
            if (sharedPreferenceUtil.getString(GGGlobalValues.APPLICATION_NAME) != null) {
                mApplicationName.setText(sharedPreferenceUtil.getString(GGGlobalValues.APPLICATION_NAME));
                mApplicationVersion.setText(sharedPreferenceUtil.getString(GGGlobalValues.APPLICATION_VERSION));
            }
            if (sharedPreferenceUtil.getString(GGGlobalValues.LAST_SYNC) != null) {
                mLastSync.setText(sharedPreferenceUtil.getString(GGGlobalValues.LAST_SYNC));
            }
        }
    }

    /**
     * Store in the device cache all the device information
     *
     * @param device
     */
    private void saveDeviceData(ReaxiumDevice device) {
        sharedPreferenceUtil.save(GGGlobalValues.DEVICE_ID, device.getDeviceId());
        sharedPreferenceUtil.saveString(GGGlobalValues.DEVICE_NAME, device.getDeviceName());
        sharedPreferenceUtil.saveString(GGGlobalValues.DEVICE_DESCRIPTION, device.getDeviceDescription());
        sharedPreferenceUtil.saveString(GGGlobalValues.DEVICE_STATUS, device.getStatus().getStatusName());
        if (device.getApplications() != null) {
            if(!device.getApplications().isEmpty()){
                sharedPreferenceUtil.saveString(GGGlobalValues.APPLICATION_NAME, device.getApplications().get(0).getApplicationName());
                sharedPreferenceUtil.saveString(GGGlobalValues.APPLICATION_VERSION, device.getApplications().get(0).getVersion());
            }
        }
        fillFields();
    }

    /**
     * download all the device info and configure it to the device
     */
    private void configureDevice() {
        if (!"".equals(mDeviceIdInput.getText().toString().trim())) {

            if (GGUtil.isNetworkAvailable(getActivity())) {

                showProgressDialog(getString(R.string.configure_device_progress_message));

                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgressDialog();
                        Type responseType = new TypeToken<ApiResponse<ReaxiumDevice>>() {
                        }.getType();
                        ApiResponse<ReaxiumDevice> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                        if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                            mLockConfiguration.setImageResource(R.drawable.passwordlogin);
                            ReaxiumDevice device = apiResponse.getReaxiumResponse().getObject().get(0);
                            saveDeviceData(device);
                            GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                        } else {
                            GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                        }
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (error.networkResponse != null && error.networkResponse.statusCode != 500) {
                            GGUtil.showAToast(getActivity(), R.string.simple_exception_message);
                        }
                    }
                };

                JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.CONFIGURE_DEVICE), loadConfigureDeviceParatemers(), responseListener, errorListener);
                jsonObjectRequest.setShouldCache(false);
                MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

            } else {
                GGUtil.showAToast(getActivity(), R.string.no_network_available);
            }
        } else {
            GGUtil.showAToast(getActivity(), R.string.empty_device_id);
        }
    }

    private JSONObject loadConfigureDeviceParatemers() {
        JSONObject requestObject = new JSONObject();
        try {
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject reaxiumDevice = new JSONObject();
            reaxiumDevice.put("device_id", mDeviceIdInput.getText().toString().trim());
            reaxiumDevice.put("device_token", "temporal_device_token");
            reaxiumParameters.put("ReaxiumDevice", reaxiumDevice);
            requestObject.put("ReaxiumParameters", reaxiumParameters);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return requestObject;
    }

    private void lockOrUnlockConfiguration() {
        if (mDeviceIdInput.isEnabled()) {
            mLockConfiguration.setImageResource(R.drawable.passwordlogin);
            mDeviceIdInput.setEnabled(Boolean.FALSE);
            mConfigureDeviceButton.setEnabled(Boolean.FALSE);
        } else {
            mLockConfiguration.setImageResource(R.drawable.unlock);
            mDeviceIdInput.setEnabled(Boolean.TRUE);
            mConfigureDeviceButton.setEnabled(Boolean.TRUE);
        }
    }


}
