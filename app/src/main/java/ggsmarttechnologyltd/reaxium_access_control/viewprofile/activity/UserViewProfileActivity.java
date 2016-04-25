package ggsmarttechnologyltd.reaxium_access_control.viewprofile.activity;

import android.view.View;
import android.widget.ImageView;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.routecontrol.activity.RouteControlActivity;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger at G&G on 14/04/2016.
 */
public class UserViewProfileActivity extends GGMainActivity {

    private ImageView mGoButton;

    @Override
    protected Integer getMainLayout() {
        return R.layout.profile_view_activity;
    }

    @Override
    protected void setViews() {
        mGoButton = (ImageView) findViewById(R.id.go_button);
    }

    @Override
    protected void setViewsEvents() {
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GGUtil.goToScreen(UserViewProfileActivity.this,null, RouteControlActivity.class);
            }
        });
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
