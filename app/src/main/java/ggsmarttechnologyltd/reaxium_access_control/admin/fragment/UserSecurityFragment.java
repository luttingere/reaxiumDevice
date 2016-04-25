package ggsmarttechnologyltd.reaxium_access_control.admin.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.GGMainFragment;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.UserSecurityTabsAdapter;
import ggsmarttechnologyltd.reaxium_access_control.admin.adapter.UsersListAdapter;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnUserClickListener;
import ggsmarttechnologyltd.reaxium_access_control.beans.ApiResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.ReaxiumResponse;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.global.APPEnvironment;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonObjectRequestUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.JsonUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class UserSecurityFragment extends GGMainFragment implements OnUserClickListener {

    private EditText mSearchField;
    private ImageView mSearchIcon;
    private Button mGoButton;
    private List<User> mListUsers;
    private RecyclerView mUserRecyclerList;
    private LinearLayoutManager mLinearLayoutManager;
    public ViewPager viewPager;
    private TabLayout tabLayout;
    private int tabTitles[] = new int[]{R.string.user_security_panel_tab_fingerprint, R.string.user_security_panel_tab_rfid};
    private UserSecurityTabsAdapter tabsAdapter;
    private UsersListAdapter mUsersListAdapter;
    private TextWatcher searchUserWatcher;
    private String searchText;
    private RelativeLayout mUserSecurityInfoContainer;
    private TextView mUserFullName;
    private TextView mUserIDNumber;
    private TextView mUserType;
    private TextView mUserBusinessName;
    private ImageView mUserPhoto;
    private ProgressBar mUserPhotoLoader;
    private static ImageLoader mImageLoader;




    @Override
    public String getMyTag() {
        return UserSecurityFragment.class.getName();
    }

    @Override
    protected Integer getToolbarTitle() {
        return R.string.user_security_panel_title;
    }

    @Override
    protected Integer getFragmentLayout() {
        return R.layout.user_security_panel_fragment;
    }

    @Override
    public Boolean onBackPressed() {
        return Boolean.TRUE;
    }

    @Override
    protected void setViews(View view) {

        //User search container
        mSearchField = (EditText) view.findViewById(R.id.user_search_text);
        mSearchIcon = (ImageView) view.findViewById(R.id.search_icon);
        mGoButton = (Button) view.findViewById(R.id.go_button);


        //User list container
        mUserRecyclerList = (RecyclerView) view.findViewById(R.id.user_list);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mUserRecyclerList.setLayoutManager(mLinearLayoutManager);
        mUsersListAdapter = new UsersListAdapter(getContext(),this,null);
        mUserRecyclerList.setAdapter(mUsersListAdapter);


        //User security tabs container
        mUserSecurityInfoContainer = (RelativeLayout) view.findViewById(R.id.user_security_info_container);
        mUserFullName = (TextView) view.findViewById(R.id.user_full_name);
        mUserIDNumber = (TextView) view.findViewById(R.id.user_id_number);
        mUserType = (TextView) view.findViewById(R.id.user_type);
        mUserBusinessName = (TextView) view.findViewById(R.id.user_business_name);
        mImageLoader = MySingletonUtil.getInstance(getActivity()).getImageLoader();
        mUserPhoto = (ImageView) view.findViewById(R.id.user_photo);
        mUserPhotoLoader = (ProgressBar) view.findViewById(R.id.user_photo_loader);
        mUserPhotoLoader.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);


        viewPager = (ViewPager) view.findViewById(R.id.user_security_info_pager);
        tabsAdapter = new UserSecurityTabsAdapter(getChildFragmentManager(), getActivity(), tabTitles.length, tabTitles);
        viewPager.setAdapter(tabsAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

    }

    @Override
    protected void setViewsEvents() {
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

        //User list container


        //User security tabs container


    }

    @Override
    public void clearAllViewComponents() {

    }

    private  void loadFingerPrintImage(User user) {
        mUserPhoto.setImageResource(R.drawable.user_avatar);
        if(user.getPhoto() != null && !"".equals(user.getPhoto())){
            mUserPhotoLoader.setVisibility(View.VISIBLE);
            mImageLoader.get(user.getPhoto(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        mUserPhotoLoader.setVisibility(View.GONE);
                        mUserPhoto.setImageBitmap(bitmap);
                    }
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    mUserPhotoLoader.setVisibility(View.GONE);
                }
            });
        }
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
                    if (error.networkResponse != null && error.networkResponse.statusCode != 500) {
                        GGUtil.showAToast(getActivity(), R.string.simple_exception_message);
                    }
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
        User user = mListUsers.get(position);
        mUserSecurityInfoContainer.setVisibility(View.VISIBLE);
        mUserFullName.setText(user.getFirstName() + " " + user.getSecondName() + " " + user.getFirstLastName() + " " + user.getSecondLastName());
        mUserIDNumber.setText(user.getDocumentId());
        loadFingerPrintImage(user);
        BiometricCaptureFragment.setUserSelected(user);
    }
}
