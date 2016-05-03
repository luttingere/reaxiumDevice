package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.reflect.TypeToken;
import com.soundcloud.android.crop.Crop;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.UserSecurityTabsAdapter;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.UsersListAdapter;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnUserClickListener;
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
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class UserPanelFragment extends GGMainFragment implements OnUserClickListener {



    private EditText mSearchField;
    private ImageView mSearchIcon;
    private Button mGoButton;
    private List<User> mListUsers = new ArrayList<>();
    private RecyclerView mUserRecyclerList;
    private LinearLayoutManager mLinearLayoutManager;
    private UsersListAdapter mUsersListAdapter;
    private TextWatcher searchUserWatcher;
    private String searchText;
    private ImageView deleteTextButton;


    //Add or Edit user Panel
    private static final int REQUEST_A_PICTURE = 100;
    private EditText userName;
    private EditText userSecondName;
    private EditText userLastName;
    private EditText userSecondLastName;
    private EditText userDocumentId;
    private EditText userEmail;
    private EditText userBirthDate;
    private Button saveUserButton;
    private Button editUserButton;
    private Button showCalendarPicker;
    private ImageView userPhoto;
    private Bitmap userPhotoBitmap;
    private ScrollView addOrEditUserContainer;
    private FloatingActionButton addUserButton;
    private User user;
    private ProgressBar mUserPhotoLoader;
    private static ImageLoader mImageLoader;
    private RelativeLayout searchUserContainer;

    

    @Override
    public String getMyTag() {
        return UserPanelFragment.class.getName();
    }

    @Override
    public Integer getToolbarTitle() {
        return R.string.add_or_edit_a_user_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.user_panel_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        if(searchUserContainer.getVisibility() != View.VISIBLE){
            hideAddUserContainer();
        }else{
            ((AdminActivity)getActivity()).runMyFragment(new AdminFragment(),null,R.id.action_menu_home);
        }
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {

        ((AdminActivity)getActivity()).showBackButton();

        //FloatingActionButton
        addUserButton = (FloatingActionButton) view.findViewById(R.id.add_user_button);


        //User search container
        mSearchField = (EditText) view.findViewById(R.id.user_search_text);
        mSearchIcon = (ImageView) view.findViewById(R.id.search_icon);
        mGoButton = (Button) view.findViewById(R.id.go_button);
        mGoButton.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        deleteTextButton = (ImageView) view.findViewById(R.id.delete_text);


        //User list container
        mUserRecyclerList = (RecyclerView) view.findViewById(R.id.user_list);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mUserRecyclerList.setLayoutManager(mLinearLayoutManager);
        mUsersListAdapter = new UsersListAdapter(getContext(),this,null,"");
        mUserRecyclerList.setAdapter(mUsersListAdapter);


        //Add or Edit User panel
        userName = (EditText) view.findViewById(R.id.user_first_name);
        userSecondName = (EditText) view.findViewById(R.id.user_second_name);
        userLastName = (EditText) view.findViewById(R.id.user_last_name);
        userSecondLastName = (EditText) view.findViewById(R.id.user_second_last_name);
        userDocumentId = (EditText) view.findViewById(R.id.user_document_id);
        userBirthDate = (EditText) view.findViewById(R.id.user_birth_date);
        userEmail = (EditText) view.findViewById(R.id.user_email);
        saveUserButton = (Button) view.findViewById(R.id.save_user_button);
        showCalendarPicker = (Button) view.findViewById(R.id.show_calendar_picker);
        userPhoto = (ImageView) view.findViewById(R.id.user_photo);
        addOrEditUserContainer = (ScrollView) view.findViewById(R.id.add_or_edit_user_container);

        mImageLoader = MySingletonUtil.getInstance(getActivity()).getImageLoader();
        mUserPhotoLoader = (ProgressBar) view.findViewById(R.id.user_photo_loader);
        mUserPhotoLoader.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);
        editUserButton = (Button) view.findViewById(R.id.edit_user_button);
        searchUserContainer = (RelativeLayout) view.findViewById(R.id.search_user_container);

    }

    @Override
    protected void setViewsEvents() {

        setRetainInstance(Boolean.TRUE);

        //User search container
        searchUserWatcher = loadUserSearchWatcher();
        mSearchField.addTextChangedListener(searchUserWatcher);
        mSearchField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchField.setCursorVisible(Boolean.TRUE);
                return false;
            }
        });

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUsers();
            }
        });

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

        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAValidForm() && user != null) {
                    editUser(user);
                }else{
                    if(user == null){
                        GGUtil.showAToast(getActivity(),"Not valid user");
                    }
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

        deleteTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchField.setText("");
                clearUserList();
            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserContainer();
            }
        });
    }

    private void showAddUserContainer(){
        clearUserList();
        clearAllViewComponents();
        editUserButton.setVisibility(View.GONE);
        saveUserButton.setVisibility(View.VISIBLE);
        addUserButton.setVisibility(View.GONE);
        addOrEditUserContainer.setVisibility(View.VISIBLE);
        searchUserContainer.setVisibility(View.GONE);
    }

    private void hideAddUserContainer(){
        clearAllViewComponents();
        addUserButton.setVisibility(View.VISIBLE);
        addOrEditUserContainer.setVisibility(View.GONE);
        searchUserContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearAllViewComponents() {
        userName.setText("");
        userSecondName.setText("");
        userLastName.setText("");
        userSecondLastName.setText("");
        userDocumentId.setText("");
        userBirthDate.setText("");
        userEmail.setText("");
        userPhoto.setImageResource(R.drawable.user_avatar);
    }

    private TextWatcher loadUserSearchWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                searchText = text.toString();
                if (searchText.length() > 4) {
                    mGoButton.setVisibility(View.VISIBLE);
                } else {
                    mGoButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }


    private void searchUsers() {

        if (GGUtil.isNetworkAvailable(getActivity())) {

            showProgressDialog(getString(R.string.progress_dialog_message));

            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressDialog();
                    Type responseType = new TypeToken<ApiResponse<User>>() {}.getType();
                    ApiResponse<User> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if(apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE){
                        mListUsers = apiResponse.getReaxiumResponse().getObject();
                        mUsersListAdapter.refreshList(mListUsers);
                    }else{
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

            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.SEARCH_USERS_BY_FILTER), loadRequestParameters(), responseListener, errorListener);
            jsonObjectRequest.setShouldCache(false);
            MySingletonUtil.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        } else {
            GGUtil.showAToast(getActivity(), R.string.no_network_available);
        }
    }


    private JSONObject loadRequestParameters(){
        JSONObject requestObject = new JSONObject();
        try {
            JSONObject reaxiumParameters = new JSONObject();
            JSONObject users = new JSONObject();
            users.put("filter",searchText);
            reaxiumParameters.put("Users",users);
            requestObject.put("ReaxiumParameters",reaxiumParameters);
        }catch (Exception e){
            Log.e(TAG,"",e);
        }
        return requestObject;
    }

   private void clearUserList() {
        mListUsers.clear();
        mUsersListAdapter.refreshList(mListUsers);
    }

    @Override
    public void onUserClicked(int position) {
        user = mListUsers.get(position);
        fillFields(user);
        addUserButton.setVisibility(View.GONE);
        addOrEditUserContainer.setVisibility(View.VISIBLE);
        saveUserButton.setVisibility(View.GONE);
        editUserButton.setVisibility(View.VISIBLE);
        searchUserContainer.setVisibility(View.GONE);
    }

    @Override
    public void onUserClicked(int position, String listName) {

    }

    private void fillFields(User user){
        userName.setText(user.getFirstName());
        userSecondName.setText(user.getSecondName());
        userLastName.setText(user.getFirstLastName());
        userSecondLastName.setText(user.getSecondLastName());
        userDocumentId.setText(user.getDocumentId());
        userBirthDate.setText(user.getBirthDate());
        userEmail.setText(user.getEmail());
        loadUserImage(user);
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
                        hideAddUserContainer();
                        clearUserList();
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
                if(!"".equals(userSecondLastName.getText().toString().trim())){
                    parameters.put("userSecondLastName", userSecondLastName.getText().toString().trim());
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

    private void editUser(User user){
        if(GGUtil.isNetworkAvailable(getActivity())){
            showProgressDialog("Editing  the user...");
            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressDialog();
                    Type responseType = new TypeToken<ApiResponse<User>>() {}.getType();
                    ApiResponse<User> apiResponse = JsonUtil.getEntityFromJSON(response, responseType);
                    if (apiResponse.getReaxiumResponse().getCode() == GGGlobalValues.SUCCESSFUL_API_RESPONSE_CODE) {
                        hideAddUserContainer();
                        clearUserList();
                        GGUtil.showAToast(getActivity(),  "User edited successfully");
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
                if(!"".equals(userSecondLastName.getText().toString().trim())){
                    parameters.put("userSecondLastName", userSecondLastName.getText().toString().trim());
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
                parameters.put("userId",user.getUserId());

            } catch (Exception e) {
                Log.e(TAG,"Error loading paremeters",e);
            }
            JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.POST, APPEnvironment.createURL(GGGlobalValues.EDIT_A_USER), parameters, responseListener, errorListener);
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

    private  void loadUserImage(User user) {
        userPhoto.setImageResource(R.drawable.user_avatar);
        if(user.getPhoto() != null && !"".equals(user.getPhoto())){
            mUserPhotoLoader.setVisibility(View.VISIBLE);
            mImageLoader.get(user.getPhoto(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        userPhotoBitmap = bitmap;
                        mUserPhotoLoader.setVisibility(View.GONE);
                        userPhoto.setImageBitmap(bitmap);
                    }else{
                        userPhotoBitmap = null;
                    }
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    mUserPhotoLoader.setVisibility(View.GONE);
                    userPhotoBitmap = null;
                }
            });
        }else{
            userPhotoBitmap = null;
        }
    }

}
