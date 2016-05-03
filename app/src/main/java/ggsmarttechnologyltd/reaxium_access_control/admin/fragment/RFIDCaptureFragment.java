package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.content.pm.ActivityInfo;
import android.view.View;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class RFIDCaptureFragment extends GGMainFragment {

    @Override
    public String getMyTag() {
        return RFIDCaptureFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return null;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.rfid_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {

    }

    @Override
    protected void setViewsEvents() {

    }

    @Override
    public void clearAllViewComponents() {

    }

    public static RFIDCaptureFragment getInstance(){
        return new RFIDCaptureFragment();
    }
}
