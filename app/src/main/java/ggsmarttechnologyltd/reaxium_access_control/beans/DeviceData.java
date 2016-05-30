package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class DeviceData extends AppBean {

    @SerializedName("deviceAccessData")
    private List<UserAccessControl> deviceAccessData;

    @SerializedName("deviceRoutesInfo")
    private DeviceRouteInfo deviceRoutesInfo;


    public List<UserAccessControl> getDeviceAccessData() {
        return deviceAccessData;
    }

    public void setDeviceAccessData(List<UserAccessControl> deviceAccessData) {
        this.deviceAccessData = deviceAccessData;
    }

    public DeviceRouteInfo getDeviceRoutesInfo() {
        return deviceRoutesInfo;
    }

    public void setDeviceRoutesInfo(DeviceRouteInfo deviceRoutesInfo) {
        this.deviceRoutesInfo = deviceRoutesInfo;
    }
}
