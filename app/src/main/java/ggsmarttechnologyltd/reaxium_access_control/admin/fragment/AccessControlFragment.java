package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.UsersListAdapter;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserINFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.dialog.UserOUTFoundDialog;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnUserClickListener;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticFingerPrintValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.FingerPrintHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.FailureAccessPlayerSingleton;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

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
    private AutomaticFingerPrintValidationThread automaticFingerPrintValidationThread;
    private AccessControlHandler handler;

    @Override
    public String getMyTag() {
        return AccessControlFragment.class.getName();
    }

    @Override
    protected Integer getToolbarTitle() {
        return R.string.access_control_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.access_control_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        if (App.fingerprintScanner == null) {
            App.fingerprintScanner = FingerprintScanner.getInstance();
        }
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
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            App.fingerprintScanner.powerOn();
        } catch (Exception e) {
            Log.i(TAG, "", e);
        }
        handler = new AccessControlHandler();
        automaticFingerPrintValidationThread = new AutomaticFingerPrintValidationThread(App.fingerprintScanner, handler, getActivity());
        automaticFingerPrintValidationThread.start();
    }

    @Override
    public void onPause() {
        automaticFingerPrintValidationThread.stopScanner();
        Log.i(TAG, "the access control finger print process has ended");
        try {
            App.fingerprintScanner.powerOff();
        } catch (Exception e) {
            Log.i(TAG, "", e);
        }
        Log.i(TAG, "Automatic detection of a fingerprint was stoped");
        super.onPause();
    }


    private void fillUserListINAndOutData() {
        ReaxiumUsersDAO dao = ReaxiumUsersDAO.getInstance(getActivity());
        List<User> listOfUserAssociated = dao.getAllUsersRegisteredInTheDevice();
        listOUT = listOfUserAssociated;
        Collections.sort(listOUT);
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
        //TODO: show user information
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

    /**
     * remove from a list an object
     *
     * @param usersList
     * @param userToRemove
     */
    public void removeUserFromList(List<User> usersList, User userToRemove) {
        int position = 0;
        for (User user : usersList) {
            if (userToRemove.getUserId() == user.getUserId()) {
                Log.i(TAG, "removing user: " + user.getUserId());
                usersList.remove(position);
                Collections.sort(usersList);
                break;
            }
            position++;
        }
    }

    public Boolean findAndRemoveUserFromList(List<User> usersList, User userToFindAndRemove) {
        int position = 0;
        Boolean found = Boolean.FALSE;
        for (User user : usersList) {
            Log.i(TAG, "comparando id:" + userToFindAndRemove.getUserId()+" con: "+user.getUserId());
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

    private class AccessControlHandler extends FingerPrintHandler {


        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateFingerPrint(Integer userId) {
            User user = ReaxiumUsersDAO.getInstance(getActivity()).getUserById("" + userId);
            if (user != null) {
                user.setAccessTime(timeFormat.format(new Date()));
                if (findAndRemoveUserFromList(listIN, user)) {
                    userOUTFoundDialog = new UserOUTFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userOUTFoundDialog.updateUserInfo(user);
                    userOUTFoundDialog.show();
                    delayedUserOutDialogDismiss();
                    listOUT.add(user);
                    Collections.sort(listOUT);
                } else if (findAndRemoveUserFromList(listOUT, user)) {
                    userINFoundDialog = new UserINFoundDialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
                    userINFoundDialog.updateUserInfo(user);
                    userINFoundDialog.show();
                    delayedUserInDialogDismiss();
                    listIN.add(user);
                    Collections.sort(listIN);
                }
                adapterIN.refreshList(listIN);
                adapterOUT.refreshList(listOUT);
            } else {
                FailureAccessPlayerSingleton.getInstance(getActivity()).initRingTone();
                GGUtil.showAToast(getActivity(), "Invalid user, not registered: " + userId);
            }
        }

        @Override
        protected void errorRoutine(String message) {
            GGUtil.showAToast(getActivity(), message);
        }
    }
}