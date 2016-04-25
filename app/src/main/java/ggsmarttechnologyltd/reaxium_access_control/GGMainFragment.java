package ggsmarttechnologyltd.reaxium_access_control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.SharedPreferenceUtil;

/**
 * Created by Eduardo Luttinger at G&G on 15/04/2016.
 * <p/>
 * All Android Fragments Class should extends this Class, so treat the similar behaviors in one place
 */
public abstract class GGMainFragment extends Fragment implements Serializable {

    /**
     * trace id for LOG proposal
     */
    protected static final String TAG = GGGlobalValues.TRACE_ID;

    /**
     * Mandatory
     * override this method to set the fragment name, for fragment transactions proposals
     *
     * @return
     */
    abstract public String getMyTag();

    /**
     * Mandatory only if you set the Activity Toolbar
     * <p/>
     * <p>override this method to set the title of the child activity toolbar</p>
     *
     * @return Ej: null or R.string.my_toolbar_title
     */
    abstract protected Integer getToolbarTitle();

    /**
     * Mandatory
     * <p/>
     * override this method to set the main layout of the fragment
     *
     * @return Ej: R.layout.fragment_container
     */
    abstract protected Integer getFragmentLayout();

    /**
     * No Mandatory
     * <p/>
     * override this method to perform an action before the activity onBackPressed native behavior
     *
     * @return True to avoid the native activity behavior, False to let it be.
     */
    abstract public Boolean onBackPressed();

    /**
     * Mandatory
     * <p/>
     * override this method to initialize all fragment view components
     */
    abstract protected void setViews(View view);

    /**
     * Mandatory
     * override this method to set all fragment view components events
     */
    abstract protected void setViewsEvents();

    /**
     * No Mandatory
     * <p/>
     * override this method to clear all fragment view components
     */
    abstract public void clearAllViewComponents();


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        setViews(view);
        setViewsEvents();
        return view;
    }

    /**
     * show the activity progress dialog
     *
     * @param message
     */
    public void showProgressDialog(String message) {
        ((GGMainActivity) getActivity()).showProgressDialog(message);
    }

    /**
     * dismiss the activity progress dialog
     */
    public void hideProgressDialog() {
        try {
            ((GGMainActivity) getActivity()).dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * change the message of the progress dialog
     * @param message
     */
    public void changeProgressDialogMessage(String message){
        ((GGMainActivity) getActivity()).changeProgressDialogMessage(message);
    }


    /**
     * Android Cache Storage Manager object reference
     *
     * @return SharedPreferenceUtil Singleton Instance
     */
    protected SharedPreferenceUtil getSharedPreferences() {
        return SharedPreferenceUtil.getInstance(getActivity());
    }

}
