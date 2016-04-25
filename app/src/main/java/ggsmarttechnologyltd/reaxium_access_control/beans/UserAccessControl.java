package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class UserAccessControl extends AppBean {

    @SerializedName("UserAccessData")
    private UserAccessData userAccessData;

    public UserAccessData getUserAccessData() {
        return userAccessData;
    }

    public void setUserAccessData(UserAccessData userAccessData) {
        this.userAccessData = userAccessData;
    }
}
