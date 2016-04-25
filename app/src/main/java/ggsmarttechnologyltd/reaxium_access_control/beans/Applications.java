package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class Applications extends AppBean {

    @SerializedName("application_id")
    private Long applicationId;

    @SerializedName("application_name")
    private String applicationName;

    @SerializedName("version")
    private String version;


    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
