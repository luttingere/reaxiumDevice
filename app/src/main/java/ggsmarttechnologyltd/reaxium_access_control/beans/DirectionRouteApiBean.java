package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 24/05/2016.
 */
public class DirectionRouteApiBean extends AppBean {

    @SerializedName("overview_polyline")
    private PolylineBean overViewPolyLine;


    public PolylineBean getOverViewPolyLine() {
        return overViewPolyLine;
    }

    public void setOverViewPolyLine(PolylineBean overViewPolyLine) {
        this.overViewPolyLine = overViewPolyLine;
    }
}
