package ggsmarttechnologyltd.reaxium_access_control.admin.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.BiometricCaptureFragment;
import ggsmarttechnologyltd.reaxium_access_control.admin.fragment.RFIDCaptureFragment;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class UserSecurityTabsAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private int mCount;
    private int[] mTabTitles;

    public UserSecurityTabsAdapter(FragmentManager manager,Context context, int numbTabs, int mTabTitles[]) {
        super(manager);
        this.mContext = context;
        this.mCount = numbTabs;
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = RFIDCaptureFragment.getInstance();
                break;
//            case 1:
//                fragment = BiometricCaptureFragment.getInstance();
//                break;
//            default:
//                fragment = RFIDCaptureFragment.getInstance();
//                break;
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mTabTitles[position]);
    }
}
