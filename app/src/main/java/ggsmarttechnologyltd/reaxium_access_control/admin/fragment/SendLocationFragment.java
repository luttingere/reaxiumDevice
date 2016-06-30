package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.service.AdminSendLocationService;
import ggsmarttechnologyltd.reaxium_access_control.service.SendLocationService;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 02/06/2016.
 */
public class SendLocationFragment extends GGMainFragment {

    @Override
    public String getMyTag() {
        return SendLocationFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.send_location_screen_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.send_location_fragment_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        ((AdminActivity)getActivity()).runMyFragment(new AdminFragment(),null,R.id.action_menu_home);
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        ((AdminActivity)getActivity()).showBackButton();
    }

    @Override
    protected void setViewsEvents() {

    }

    public void locationSentSuccessfully(){
        Log.i(TAG,"locationSentSuccessfully");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GGUtil.showAShortToast(getActivity(),"Location sent successfully");
            }
        });
    }

    /**
     * Inicia el servicio de notificacion de ubicacion satelital
     */
    private void startNotificationService(){
        ((AdminActivity)getActivity()).startNotificationService();
    }

    /**
     * Detiene el proceso de envio de ubicacion
     */
    private void stopNotificationService(){
        ((AdminActivity)getActivity()).stopService();
    }

    @Override
    public void onResume() {
        startNotificationService();
        super.onResume();
    }

    @Override
    public void onPause() {
        stopNotificationService();
        super.onPause();
    }

    @Override
    public void clearAllViewComponents() {

    }
}
