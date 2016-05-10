package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.aratek.fp.FingerprintImage;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.UsersListAdapter;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserINFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserINOUTInfoDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserOUTFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnUserClickListener;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticCardValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticFingerPrintValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitScannersInAutoModeThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.InitFingerPrintThread;
import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.AccessControlDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 26/04/2016.
 */
public class AccessControlFragment extends GGMainFragment implements OnUserClickListener {

    private RecyclerView userINList;
    private LinearLayoutManager linearINLayoutManager;
    private RecyclerView userOUTList;
    private LinearLayoutManager linearOUTLayoutManager;
    private UsersListAdapter adapterIN;
    private UsersListAdapter adapterOUT;
    public final static String LIST_IN_NAME = "IN";
    public final static String LIST_OUT_NAME = "OUT";
    private List<User> listIN = new ArrayList<>();
    private List<User> listOUT = new ArrayList<>();
    private UserINFoundDialog userINFoundDialog;
    private UserOUTFoundDialog userOUTFoundDialog;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    UserINOUTInfoDialog userINOUTInfoDialog;
    private AccessControlHandler handler;
    private AccessControlDAO accessControlDAO;
    private ReaxiumUsersDAO reaxiumUsersDAO;

    @Override
    public String getMyTag() {
        return AccessControlFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.access_control_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.access_control_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        ((AdminActivity) getActivity()).runMyFragment(new AdminFragment(), null, R.id.action_menu_home);
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        setRetainInstance(Boolean.TRUE);
        fillUserListINAndOutData();
        userINList = (RecyclerView) view.findViewById(R.id.user_in_list);
        linearINLayoutManager = new LinearLayoutManager(getActivity());
        userINList.setLayoutManager(linearINLayoutManager);
        adapterIN = new UsersListAdapter(getActivity(), this, listIN, LIST_IN_NAME);
        userINList.setAdapter(adapterIN);

        userOUTList = (RecyclerView) view.findViewById(R.id.user_out_list);
        linearOUTLayoutManager = new LinearLayoutManager(getActivity());
        userOUTList.setLayoutManager(linearOUTLayoutManager);
        adapterOUT = new UsersListAdapter(getActivity(), this, listOUT, LIST_OUT_NAME);
        userOUTList.setAdapter(adapterOUT);
        ((AdminActivity) getActivity()).showBackButton();
        accessControlDAO = AccessControlDAO.getInstance(getActivity());
    }


    @Override
    public void onResume() {
        super.onResume();
        handler = new AccessControlHandler();
        InitScannersInAutoModeThread initScannersInAutoModeThread = new InitScannersInAutoModeThread(getActivity(), handler);
        initScannersInAutoModeThread.start();
        Log.i(TAG, "automatic detection has started");
    }

    @Override
    public void onPause() {
        //AutomaticCardValidationThread.stopScanner();
        AutomaticFingerPrintValidationThread.stopScanner();
        GGUtil.closeFingerPrint();
        //GGUtil.closeCardReader(getActivity(),handler);
        Log.i(TAG, "the scanners han bean turned off successfully");
        super.onPause();
    }

    private void fillUserListINAndOutData() {
        ReaxiumUsersDAO dao = ReaxiumUsersDAO.getInstance(getActivity());
        List<User> listOfUserAssociated = dao.getAllUsersRegisteredInTheDevice();
        accessControlDAO = AccessControlDAO.getInstance(getActivity());
        AccessControl accessControl = null;
        if (listOfUserAssociated != null) {
            for (User user : listOfUserAssociated) {
                accessControl = accessControlDAO.getLastAccess("" + user.getUserId());
                Log.i(TAG, "AccessControl: " + accessControl + " for user id: " + user.getUserId());
                if (accessControl != null) {
                    user.setAccessTime(timeFormat.format(new Date(accessControl.getAccessDate())));
                    if (accessControl.getAccessType().equals(LIST_IN_NAME)) {
                        listIN.add(user);
                    } else if (accessControl.getAccessType().equals(LIST_OUT_NAME)) {
                        listOUT.add(user);
                    }
                } else {
                    listOUT.add(user);
                }
            }
            Collections.sort(listIN);
            Collections.sort(listOUT);
        }

    }

    @Override
    protected void setViewsEvents() {

    }

    @Override
    public void clearAllViewComponents() {

    }

    @Override
    public void onUserClicked(int position) {

    }

    @Override
    public void onUserClicked(int position, String listName) {
        User user = null;
        if (listName.equals(LIST_IN_NAME)) {
            user = listIN.get(position);
        } else if (listName.equals(LIST_OUT_NAME)) {
            user = listOUT.get(position);
        }
        userINOUTInfoDialog = new UserINOUTInfoDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
        userINOUTInfoDialog.updateUserInfo(user);
        userINOUTInfoDialog.show();
    }

    public void delayedUserInDialogDismiss() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (userINFoundDialog != null) {
                    if (userINFoundDialog.isShowing()) {
                        userINFoundDialog.dismiss();
                        userINFoundDialog = null;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, userINFoundDialog.SLEEP_TIME);
    }


    public void delayedUserOutDialogDismiss() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (userOUTFoundDialog != null) {
                    if (userOUTFoundDialog.isShowing()) {
                        userOUTFoundDialog.dismiss();
                        userOUTFoundDialog = null;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, userOUTFoundDialog.SLEEP_TIME);
    }


    public Boolean findAndRemoveUserFromList(List<User> usersList, User userToFindAndRemove) {
        int position = 0;
        Boolean found = Boolean.FALSE;
        for (User user : usersList) {
            Log.i(TAG, "comparando id:" + userToFindAndRemove.getUserId() + " con: " + user.getUserId());
            if (userToFindAndRemove.getUserId().intValue() == user.getUserId().intValue()) {
                Log.i(TAG, "removing user: " + userToFindAndRemove.getUserId());
                usersList.remove(position);
                Collections.sort(usersList);
                found = Boolean.TRUE;
                break;
            }
            position++;
        }
        return found;
    }

    /**
     *
     */
    private void saveAccessInServer(final Long lastInsertedId,Long userId, String trafficType, Long deviceId, String accessType, String trafficInfo) {
        if (GGUtil.isNetworkAvailable(getActivity())) {
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Type responseType = new TypeToken<ApiResponse<Object>>() {}.getType();
                    ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        Integer result = accessControlDAO.markAsRegisteredInCloud(lastInsertedId);
                        Log.i(TAG,"Mark as registered result: "+result);
                        GGUtil.showAToast(getActivity(), "user access registered in cloud successfully");
                    } else {
                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    GGUtil.showAToast(getActivity(), "There was an error saving the access in the cloud");
                }
            };
            JSONObject parameters = loadRequestParameters(userId,trafficType,deviceId,accessType,trafficInfo);
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SAVE_ACCESS_IN_SERVER), parameters, responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            GGUtil.showAToast(getActivity(), "No internet connection, remember synchronize the device for send access record to the cloud");
        }
    }

    private JSONObject loadRequestParameters(Long userId, String trafficType, Long deviceId, String accessType, String trafficInfo) {
        JSONObject requestObject = new JSONObject();
        try {
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject usersAccess = new JSONObject();
            usersAccess.put("user_id", userId);
            usersAccess.put("traffic_type", trafficType);
            usersAccess.put("device_id", deviceId);
            usersAccess.put("access_type", accessType);
            usersAccess.put("traffic_info", trafficInfo);
            reaxiumParameters.put("UserAccess", usersAccess);
            requestObject.put("ReaxiumParameters", reaxiumParameters);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return requestObject;
    }

    private class AccessControlHandler extends ScannersActivityHandler {


        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateScannerResult(AppBean bean) {
            SecurityObject securityObject = (SecurityObject) bean;
            User user = null;
            if(securityObject.getCardId() == null){
                user =reaxiumUsersDAO.getUserById("" + securityObject.getUserId());
            }else{
                user =reaxiumUsersDAO.getUserByIdAndRfidCode("" + securityObject.getUserId(),""+securityObject.getCardId());
            }
            if (user != null) {
                user.setAccessTime(timeFormat.format(new Date()));
                Long lastInsertedId = null;
                String trafficTypeId = "1";
                if (findAndRemoveUserFromList(listIN, user)) {
                    trafficTypeId = "2";
                    lastInsertedId = accessControlDAO.insertUserAccess(user.getUserId(), "BIOMETRIC", LIST_OUT_NAME);
                    userOUTFoundDialog = new UserOUTFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userOUTFoundDialog.updateUserInfo(user);
                    userOUTFoundDialog.show();
                    delayedUserOutDialogDismiss();
                    listOUT.add(user);
                    Collections.sort(listOUT);
                } else if (findAndRemoveUserFromList(listOUT, user)) {
                    trafficTypeId = "1";
                    lastInsertedId = accessControlDAO.insertUserAccess(user.getUserId(), "BIOMETRIC", LIST_IN_NAME);
                    userINFoundDialog = new UserINFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userINFoundDialog.updateUserInfo(user);
                    userINFoundDialog.show();
                    delayedUserInDialogDismiss();
                    listIN.add(user);
                    Collections.sort(listIN);
                }
                adapterIN.refreshList(listIN);
                adapterOUT.refreshList(listOUT);
                saveAccessInServer(lastInsertedId,user.getUserId(),trafficTypeId,getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID),"2","");
            } else {
                FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                GGUtil.showAToast(getActivity(), "Invalid user, not registered: " + securityObject.getUserId());
            }
        }

        @Override
        protected void errorRoutine(String message) {
            GGUtil.showAToast(getActivity(), message);
        }
    }
}