package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Eduardo Luttinger on 19/05/2016.
 */
public class Routes extends AppBean {


    @SerializedName("id_route")
    private Long routeId;

    @SerializedName("route_number")
    private String routeNumber;

    @SerializedName("route_name")
    private String routeName;

    @SerializedName("route_address")
    private String routeAddress;

    @SerializedName("routes_stops_count")
    private Long stopCount;

    @SerializedName("route_start_date")
    private Date routeDateInit;

    @SerializedName("route_end_date")
    private Date routeDateEnd;

    @SerializedName("route_type")
    private Integer routeType;

    @SerializedName("overview_polyline")
    private String routePolyLine;

    @SerializedName("stops")
    private List<Stops> stops;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteAddress() {
        return routeAddress;
    }

    public void setRouteAddress(String routeAddress) {
        this.routeAddress = routeAddress;
    }

    public Long getStopCount() {
        return stopCount;
    }

    public void setStopCount(Long stopCount) {
        this.stopCount = stopCount;
    }

    public Date getRouteDateInit() {
        return routeDateInit;
    }

    public void setRouteDateInit(Date routeDateInit) {
        this.routeDateInit = routeDateInit;
    }

    public Date getRouteDateEnd() {
        return routeDateEnd;
    }

    public void setRouteDateEnd(Date routeDateEnd) {
        this.routeDateEnd = routeDateEnd;
    }

    public List<Stops> getStops() {
        return stops;
    }

    public void setStops(List<Stops> stops) {
        this.stops = stops;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public String getRoutePolyLine() {
        return routePolyLine;
    }

    public void setRoutePolyLine(String routePolyLine) {
        this.routePolyLine = routePolyLine;
    }
}
