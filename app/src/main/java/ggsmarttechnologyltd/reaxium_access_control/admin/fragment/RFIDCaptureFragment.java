package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;

import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.iccard.ICCardReader;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.errormessages.RFIDErrorMessage;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.AppBean;
import ggsmarttechnologyltd.reaxium_access_control.beans.FingerPrint;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.TakeImagesUtil;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class RFIDCaptureFragment extends GGMainFragment {


    private Button writeRFIDCard;
    private TextView exampleText;
    private ImageView checkImage;
    public static RFIDCaptureHelper helper;
    private static User mSelectedUser;
    private Long cardId;


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
        exampleText = (TextView) view.findViewById(R.id.exampleText);
        checkImage = (ImageView) view.findViewById(R.id.checkImage);
        writeRFIDCard = (Button)view.findViewById(R.id.setRfidCard);
        helper = new RFIDCaptureHelper();
    }

    @Override
    protected void setViewsEvents() {
        writeRFIDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureRFID();
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void clearAllViewComponents() {

    }

    public static RFIDCaptureFragment getInstance() {
        return new RFIDCaptureFragment();
    }


    private void configureRFID(){
        showProgressDialog("Configuring the RFID Card...");
        if(writeUserIdInTheCard()){
            hideProgressDialog();
            new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                    .setTitle("Device Access Association")
                    .setMessage("The system will save the RFID information in the server, do you wanna also associate this user rfid to this device?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            associateRFIDToAUser(cardId,mSelectedUser.getUserId(),getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            associateRFIDToAUser(cardId,mSelectedUser.getUserId(),null);
                            dialog.dismiss();
                        }
                    }).show();
        }else{
            hideProgressDialog();
        }
    }

    private Boolean writeUserIdInTheCard(){
        Boolean isOk = Boolean.FALSE;
        Result res = App.cardReader.activate();
        int error;
        if(res.error == ICCardReader.RESULT_OK){
            cardId = (Long) res.data;
            Log.i(TAG,"Crad reader number: "+cardId);
            error = App.cardReader.validateKey(cardId, ICCardReader.KEY_A, GGGlobalValues.BYTE_BLOCK, new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff });
            if (error == ICCardReader.RESULT_OK) {
                byte[] bytes = ByteBuffer.allocate(GGGlobalValues.BYTE_SIZE).putInt(mSelectedUser.getUserId().intValue()).array();
                Log.i(TAG,"data to store: "+ Arrays.toString(bytes));
                error = App.cardReader.write(GGGlobalValues.BYTE_BLOCK, bytes);
                if (error == ICCardReader.RESULT_OK) {
                    exampleText.setVisibility(View.GONE);
                    checkImage.setVisibility(View.VISIBLE);
                    isOk = Boolean.TRUE;
                    GGUtil.showAToast(getActivity(),"Card successfully configured");
                } else {
                    GGUtil.showAToast(getActivity(),"System fail writing the rfid card, error code: "+ RFIDErrorMessage.getErrorMessage(error));
                }
            }else{
                GGUtil.showAToast(getActivity(),"error in the validation of the rfdi card, error code: "+RFIDErrorMessage.getErrorMessage(error));
            }
        }else{
            GGUtil.showAToast(getActivity(),"error activating the rfdi card, error code: "+RFIDErrorMessage.getErrorMessage(res.error));
        }
        return isOk;
    }

    private void associateRFIDToAUser(Long rfidCardNumber, Long userId, Long deviceId) {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                Type responseType = new TypeToken<ApiResponse<Object>>() {}.getType();
                ApiResponse<Object> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                    GGUtil.showAToast(getActivity(),  "Card configuration successfully saved");
                } else {
                    GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                GGUtil.showAToast(getActivity(), R.string.bad_connection_message);
            }
        };
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("rfidCardNumber", rfidCardNumber);
            parameters.put("user_id", userId);
            parameters.put("device_id", deviceId);
        } catch (Exception e) {
            Log.e(TAG,"Error loading paremeters",e);
        }
        showProgressDialog("Saving card configuration in the cloud...");
        JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.LOAD_USER_RFID_INFO), parameters, responseListener, errorListener);
        jsonObjectRequest.setShouldCache(false);
        MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }



    public static void setUserSelected(User user) {
        mSelectedUser = user;
    }


    private class RFIDCaptureHelper extends ScannersActivityHandler{

        @Override
        protected void updateFingerPrintImageHolder(FingerprintImage fingerprintImage) {

        }

        @Override
        protected void saveFingerPrint(FingerPrint fingerPrint) {

        }

        @Override
        protected void validateScannerResult(AppBean bean) {

        }

        @Override
        protected void errorRoutine(String message) {
            GGUtil.showAToast(getActivity(),message);
        }
    }



}
