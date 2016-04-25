package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class Status extends AppBean {

    @SerializedName("status_id")
    private Long statusId;

    @SerializedName("status_name")
    private String statusName;


    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
