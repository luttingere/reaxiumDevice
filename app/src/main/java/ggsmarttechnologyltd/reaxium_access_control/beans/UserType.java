package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class UserType extends AppBean {

    @SerializedName("user_type_id")
    private Long userTypeId;

    @SerializedName("user_type_name")
    private String userTypeName;


    public Long getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Long userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }
}
