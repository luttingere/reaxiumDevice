package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eduardo Luttinger on 24/05/2016.
 */
public class DirectionApiBean extends AppBean {

    @SerializedName("error_message")
    private String errorMessage;

    @SerializedName("status")
    private String status;

    @SerializedName("routes")
    List<DirectionRouteApiBean> directionRouteApiBeanList;


    public List<DirectionRouteApiBean> getDirectionRouteApiBeanList() {
        return directionRouteApiBeanList;
    }

    public void setDirectionRouteApiBeanList(List<DirectionRouteApiBean> directionRouteApiBeanList) {
        this.directionRouteApiBeanList = directionRouteApiBeanList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
