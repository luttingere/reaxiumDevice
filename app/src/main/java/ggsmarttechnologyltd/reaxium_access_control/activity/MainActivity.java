package ggsmarttechnologyltd.reaxium_access_control.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.AccessControlFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.AdminFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.ConfigureDeviceFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.ShowMapFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.UserPanelFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.UserSecurityFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.VerifyBiometricFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.AutomaticFingerPrintValidationThread;
import ggsmarttechnologyltd.reaxium_access_control.fragment.DriverScreenFragment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.login.activity.LoginActivity;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;

/**
 * Created by Eduardo Luttinger on 11/05/2016.
 */
public class MainActivity extends GGMainActivity {

    /**
     * Android utility layout to handle menus
     */
    private DrawerLayout mDrawerLayout;
    /**
     * Android menu drawer
     */
    private NavigationView mMenuDrawer;

    /**
     * fragment placed in the screen
     */
    private GGMainFragment mFragmentInScreen;

    /**
     * back button
     */
    private LinearLayout navigationBack;

    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected Integer getMainLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void setViews() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(this);
        navigationBack = (LinearLayout) findViewById(R.id.navigation_back);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.admin_menu_drawer);
        mMenuDrawer = (NavigationView) findViewById(R.id.nvView);
        View header = getLayoutInflater().inflate(R.layout.admin_menu_drawer_header, null);
        if(sharedPreferenceUtil.getString(GGGlobalValues.USER_FULL_NAME_IN_SESSION) != null){
            ((TextView)header.findViewById(R.id.user_full_name)).setText(sharedPreferenceUtil.getString(GGGlobalValues.USER_FULL_NAME_IN_SESSION));
            if(sharedPreferenceUtil.getString(GGGlobalValues.USER_FULL_TYPE_IN_SESSION) != null){
                ((TextView)header.findViewById(R.id.user_role)).setText(sharedPreferenceUtil.getString(GGGlobalValues.USER_FULL_TYPE_IN_SESSION));
            }
        }else{
            ((TextView)header.findViewById(R.id.user_full_name)).setText("Invited User");
        }
        mMenuDrawer.addHeaderView(header);
    }


    @Override
    protected void setViewsEvents() {
        //adding the select item event to the drawer menu
        mMenuDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        navigationBack.setVisibility(View.INVISIBLE);
        navigationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMainFragment() != null) {
                    Log.i(TAG, "back button pressed");
                    getMainFragment().onBackPressed();
                }
            }
        });
    }

    public void hideBackButton(){
        navigationBack.setVisibility(View.INVISIBLE);
    }
    public void showBackButton(){
        navigationBack.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.menu_slider;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void runMyFragment(GGMainFragment fragment, Bundle params,int drawerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AutomaticFingerPrintValidationThread.stopScanner();
        fragment.setArguments(params);
        setToolBarTitle(fragment.getToolbarTitle());
        mMenuDrawer.getMenu().findItem(drawerId).setChecked(Boolean.TRUE);
        transaction.replace(GGGlobalValues.FRAGMENT_CONTAINER, fragment).addToBackStack(null).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.END);
                return true;
            case R.id.action_drawer:
                mDrawerLayout.openDrawer(GravityCompat.END);
                GGUtil.hideKeyboard(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected GGMainFragment getMainFragment() {
        mFragmentInScreen = (GGMainFragment)getSupportFragmentManager().findFragmentById(GGGlobalValues.FRAGMENT_CONTAINER);
        if(mFragmentInScreen == null){
            mFragmentInScreen =new DriverScreenFragment();
        }
        return mFragmentInScreen;
    }

    @Override
    protected Integer getMainToolbarId() {
        return R.id.toolbar;
    }

    /**
     * actions for each click un the items placed on drawer menu
     *
     * @param menuItem
     */
    private void selectDrawerItem(MenuItem menuItem) {
        if (!menuItem.isChecked()) {
            switch (menuItem.getItemId()) {
                case R.id.action_logout:
                    AutomaticFingerPrintValidationThread.stopScanner();
                    sharedPreferenceUtil.removeValue(GGGlobalValues.USER_ID_IN_SESSION);
                    sharedPreferenceUtil.removeValue(GGGlobalValues.USER_FULL_NAME_IN_SESSION);
                    sharedPreferenceUtil.removeValue(GGGlobalValues.USER_FULL_TYPE_IN_SESSION);
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                    Intent goToLoginPage = new Intent(this, LoginActivity.class);
                    startActivity(goToLoginPage);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment myFragemnt = getSupportFragmentManager().findFragmentById(GGGlobalValues.FRAGMENT_CONTAINER);
        myFragemnt.onActivityResult(requestCode, resultCode, data);
    }
}
