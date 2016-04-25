package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.content.Intent;
import android.view.View;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger on 15/04/2016.
 */
public class AdminFragment extends GGMainFragment {


    @Override
    public String getMyTag() {
        return AdminFragment.class.getName();
    }

    @Override
    protected Integer getToolbarTitle() {
        return R.string.admin_fragment_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.admin_fragment;
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

}
