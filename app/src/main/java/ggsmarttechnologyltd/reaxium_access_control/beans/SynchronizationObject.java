package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class SynchronizationObject extends AppBean {

    @SerializedName("deviceData")
    private DeviceData deviceData;


    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }
}
