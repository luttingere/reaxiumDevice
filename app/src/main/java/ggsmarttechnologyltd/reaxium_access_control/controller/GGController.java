package ggsmarttechnologyltd.reaxium_access_control.controller;

import android.content.Context;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class GGController {

    public static final String TAG = GGGlobalValues.TRACE_ID;
    protected Context mContext;
    protected GGMainFragment ggMainFragment;
    protected GGMainActivity ggMainActivity;

    public GGController(Context context, GGMainFragment fragment) {
        this.mContext = context;
        this.ggMainFragment = fragment;
    }

    public GGController(Context context, GGMainActivity activity) {
        this.mContext = context;
        this.ggMainActivity = activity;
    }


    /**
     * show the activity progress dialog
     *
     * @param message
     */
    protected void showProgressDialog(String message) {
        if(ggMainFragment != null){
            ((GGMainActivity) ggMainFragment.getActivity()).showProgressDialog(message);
        }else{
            ggMainActivity.showProgressDialog(message);
        }

    }

    /**
     * dismiss the activity progress dialog
     */
    protected void hideProgressDialog() {
        try {
            if(ggMainFragment != null){
                ((GGMainActivity) ggMainFragment.getActivity()).dismissProgressDialog();
            }else{
                ggMainActivity.dismissProgressDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
