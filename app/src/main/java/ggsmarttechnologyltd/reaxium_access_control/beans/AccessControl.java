package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 27/04/2016.
 */
public class AccessControl extends AppBean {

    private Long _ID;
    private Long userId;
    private String userAccessType;
    private String accessType;
    private Long accessDate;
    private Long deviceId;
    private Boolean onTheCloud;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAccessType() {
        return userAccessType;
    }

    public void setUserAccessType(String userAccessType) {
        this.userAccessType = userAccessType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public Long getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Long accessDate) {
        this.accessDate = accessDate;
    }

    public Boolean isOnTheCloud() {
        return onTheCloud;
    }

    public void setOnTheCloud(Boolean onTheCloud) {
        this.onTheCloud = onTheCloud;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getOnTheCloud() {
        return onTheCloud;
    }

    public Long get_ID() {
        return _ID;
    }

    public void set_ID(Long _ID) {
        this._ID = _ID;
    }
}
