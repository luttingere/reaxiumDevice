package ggsmarttechnologyltd.reaxium_access_control.beans;

/**
 * Created by Eduardo Luttinger on 23/05/2016.
 */
public class BusStatus extends AppBean {

    private Long routeId;
    private Integer stopOrder;
    private Integer userCount;


    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }
}
