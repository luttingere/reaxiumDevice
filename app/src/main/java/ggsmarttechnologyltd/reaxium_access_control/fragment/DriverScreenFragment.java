package ggsmarttechnologyltd.reaxium_access_control.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.activity.MainActivity;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;

/**
 * Created by Eduardo Luttinger at G&G on 14/04/2016.
 */
public class DriverScreenFragment extends GGMainFragment {

    private TextView driverName;
    private TextView driverLicenceNumber;
    private TextView busNumber;
    private RelativeLayout mGoButton;
    private User driver;


    @Override
    public String getMyTag() {
        return DriverScreenFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.driver_screen;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.driver_screen_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        ((MainActivity)getActivity()).hideBackButton();
        driverName = (TextView) view.findViewById(R.id.username_input);
        driverLicenceNumber = (TextView) view.findViewById(R.id.user_document_id);
        busNumber = (TextView) view.findViewById(R.id.bus_number);
        mGoButton = (RelativeLayout) view.findViewById(R.id.go_button);
        Bundle bundle = getArguments();
        driver = (User) bundle.getSerializable("USER_VALUE");
        driverName.setText(driver.getFirstName()+ " "+driver.getFirstLastName());
        driverLicenceNumber.setText(driver.getDocumentId());
    }

    @Override
    protected void setViewsEvents() {
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putSerializable("USER_VALUE",driver);
                ((MainActivity)getActivity()).runMyFragment(new BusScreenFragment(),params);
            }
        });
    }

    @Override
    public void clearAllViewComponents() {

    }

}
