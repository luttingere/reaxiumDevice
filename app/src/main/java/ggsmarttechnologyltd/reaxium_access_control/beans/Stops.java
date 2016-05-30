package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class Stops extends AppBean {

    @SerializedName("id_stop")
    private Long stopId;

    @SerializedName("stop_number")
    private String stopNumber;

    @SerializedName("stop_name")
    private String stopName;

    @SerializedName("stop_latitude")
    private String stopLatitude;

    @SerializedName("stop_longitude")
    private String stopLongitude;

    @SerializedName("stop_address")
    private String stopAddress;

    @SerializedName("stop_order")
    private Integer stopOrder;

    @SerializedName("users")
    private List<User> users;


    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(String stopNumber) {
        this.stopNumber = stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopLatitude() {
        return stopLatitude;
    }

    public void setStopLatitude(String stopLatitude) {
        this.stopLatitude = stopLatitude;
    }

    public String getStopLongitude() {
        return stopLongitude;
    }

    public void setStopLongitude(String stopLongitude) {
        this.stopLongitude = stopLongitude;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }
}
