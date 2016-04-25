package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class ReaxiumDevice extends AppBean {

    @SerializedName("device_id")
    private Long deviceId;

    @SerializedName("device_name")
    private String deviceName;

    @SerializedName("device_description")
    private String deviceDescription;

    @SerializedName("status_id")
    private Long statusId;

    @SerializedName("status")
    private Status status;

    @SerializedName("applications")
    private List<Applications> applications;


    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Applications> getApplications() {
        return applications;
    }

    public void setApplications(List<Applications> applications) {
        this.applications = applications;
    }
}
