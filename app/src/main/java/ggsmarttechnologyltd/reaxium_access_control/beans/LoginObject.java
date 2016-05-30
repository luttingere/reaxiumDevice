package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class LoginObject extends AppBean {

    @SerializedName("user")
    private User user;

    @SerializedName("deviceData")
    private DeviceData deviceData;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }
}
