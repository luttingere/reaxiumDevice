package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class AccessType extends AppBean {

    @SerializedName("access_type_id")
    private Long accessTypeId;

    @SerializedName("access_type_name")
    private String accessTypeName;


    public Long getAccessTypeId() {
        return accessTypeId;
    }

    public void setAccessTypeId(Long accessTypeId) {
        this.accessTypeId = accessTypeId;
    }

    public String getAccessTypeName() {
        return accessTypeName;
    }

    public void setAccessTypeName(String accessTypeName) {
        this.accessTypeName = accessTypeName;
    }
}
