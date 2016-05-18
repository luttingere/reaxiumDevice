package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.makeramen.RoundedImageView;
import com.soundcloud.android.crop.Crop;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.TakeImagesUtil;

/**
 * Created by Eduardo Luttinger on 25/04/2016.
 */
public class AddUserFragment extends GGMainFragment {


    private static final int REQUEST_A_PICTURE = 100;

    private EditText userName;
    private EditText userSecondName;
    private EditText userLastName;
    private EditText userDocumentId;
    private EditText userEmail;
    private EditText userBirthDate;
    private Button saveUserButton;
    private Button showCalendarPicker;
    private ImageView userPhoto;
    private Bitmap userPhotoBitmap;

    @Override
    public String getMyTag() {
        return AddUserFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.add_user_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.add_user_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {
        userName = (EditText) view.findViewById(R.id.user_first_name);
        userSecondName = (EditText) view.findViewById(R.id.user_second_name);
        userLastName = (EditText) view.findViewById(R.id.user_last_name);
        userDocumentId = (EditText) view.findViewById(R.id.user_document_id);
        userBirthDate = (EditText) view.findViewById(R.id.user_birth_date);
        userEmail = (EditText) view.findViewById(R.id.user_email);
        saveUserButton = (Button) view.findViewById(R.id.save_user_button);
        showCalendarPicker = (Button) view.findViewById(R.id.show_calendar_picker);
        userPhoto = (ImageView) view.findViewById(R.id.user_photo);
    }

    @Override
    protected void setViewsEvents() {

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });

        saveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAValidForm()) {
                    saveUser();
                }
            }
        });

        showCalendarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = DateTime.now();
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        userBirthDate.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
                    }
                }, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
                datePicker.getDatePicker().setCalendarViewShown(Boolean.FALSE);
                datePicker.setTitle("Set Birth Date");
                datePicker.show();
            }
        });
    }

    @Override
    public void clearAllViewComponents() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_A_PICTURE:
                    beginCrop(data, resultCode);
                    break;
                case Crop.REQUEST_CROP:
                    handleCrop(resultCode, data);
                    break;
            }
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == getActivity().RESULT_OK) {
            userPhotoBitmap = TakeImagesUtil.getImageResized(getActivity(), Crop.getOutput(result));
            userPhoto.setImageBitmap(userPhotoBitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.e("TEST", "Error haciendo el crop de la imagen");
        }
    }

    private void saveUser(){
        if(GGUtil.isNetworkAvailable(getActivity())){
            showProgressDialog("Saving the user...");
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressDialog();
                    Type responseType = new TypeToken<ApiResponse<User>>() {}.getType();
                    ApiResponse<User> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        GGUtil.showAToast(getActivity(),  apiResponse.getReaxiumResponse().getMessage());
                    } else {
                        GGUtil.showAToast(getActivity(), apiResponse.getReaxiumResponse().getMessage());
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    if (error.networkResponse != null && error.networkResponse.statusCode != 500) {
                        Log.e(TAG, "Network Error Response", error);
                        GGUtil.showAToast(getActivity(), R.string.simple_exception_message);
                    }
                }
            };
            JSONObject parameters = new JSONObject();
            try {
                if(userPhotoBitmap != null){
                    parameters.put("userPhoto", TakeImagesUtil.getStringBase64Image(userPhotoBitmap));
                }
                if(!"".equals(userSecondName.getText().toString().trim())){
                    parameters.put("userSecondName", userSecondName.getText().toString().trim());
                }
                if(!"".equals(userEmail.getText().toString().trim())){
                    parameters.put("userEmail", userEmail.getText().toString().trim());
                }
                if(!"".equals(userBirthDate.getText().toString().trim())){
                    parameters.put("userBirthDate", userBirthDate.getText().toString().trim());
                }
                parameters.put("userName", userName.getText().toString().trim());
                parameters.put("userLastName", userLastName.getText().toString().trim());
                parameters.put("userDocumentId", userDocumentId.getText().toString().trim());

            } catch (Exception e) {
                Log.e(TAG,"Error loading paremeters",e);
            }
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SAVE_A_USER), parameters, responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        }else{
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }


    private void beginCrop(Intent data,int resultCode) {
        Uri source = TakeImagesUtil.getImageUriFromResult(getActivity(),resultCode,data);
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity());
    }


    private void showImagePicker(){
        getActivity().startActivityForResult(TakeImagesUtil.getPickImageIntent(getActivity()), REQUEST_A_PICTURE);
    }

    private boolean isAValidForm(){
        Boolean isAValidForm = Boolean.TRUE;
        if("".equals(userName.getText().toString().trim())){
            isAValidForm = Boolean.FALSE;
            GGUtil.showAToast(getActivity(),"The user name field cannot be empty");
        }else if("".equals(userLastName.getText().toString().trim())){
            isAValidForm = Boolean.FALSE;
            GGUtil.showAToast(getActivity(),"The user last name field cannot be empty");
        }else if("".equals(userDocumentId.getText().toString().trim())){
            isAValidForm = Boolean.FALSE;
            GGUtil.showAToast(getActivity(),"The user document field cannot be empty");
        }else if("".equals(userDocumentId.getText().toString().trim())){
            isAValidForm = Boolean.FALSE;
            GGUtil.showAToast(getActivity(),"The user document field cannot be empty");
        }
        return isAValidForm;
    }

}
