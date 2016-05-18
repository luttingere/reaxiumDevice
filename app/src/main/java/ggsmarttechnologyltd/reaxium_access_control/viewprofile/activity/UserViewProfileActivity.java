package ggsmarttechnologyltd.reaxium_access_control.viewprofile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.routecontrol.activity.RouteControlActivity;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger at G&G on 14/04/2016.
 */
public class UserViewProfileActivity extends GGMainActivity {

    private TextView driverName;
    private TextView driverLicenceNumber;
    private TextView busNumber;
    private RelativeLayout mGoButton;
    private User driver;

    @Override
    protected Integer getMainLayout() {
        return R.layout.driver_screen_fragment;
    }

    @Override
    protected void setViews() {

        driverName = (TextView) findViewById(R.id.username_input);
        driverLicenceNumber = (TextView) findViewById(R.id.user_document_id);
        busNumber = (TextView) findViewById(R.id.bus_number);
        mGoButton = (RelativeLayout) findViewById(R.id.go_button);
        Bundle bundle = getIntent().getExtras();
        driver = (User) bundle.getSerializable("USER_VALUE");
        driverName.setText(driver.getFirstName()+ " "+driver.getFirstLastName());
        driverLicenceNumber.setText(driver.getDocumentId());

    }

    @Override
    protected void setViewsEvents() {
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GGUtil.goToScreen(UserViewProfileActivity.this, null, RouteControlActivity.class);
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
