package ggsmarttechnologyltd.reaxium_access_control.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eduardo Luttinger on 01/07/2016.
 */
public class StopHistory extends AppBean {

    private Map<Integer, Integer> stopHistoryMap;

    public Map<Integer, Integer> getStopHistoryMap() {
        if(stopHistoryMap == null){
            stopHistoryMap = new HashMap<>();
        }
        return stopHistoryMap;
    }

    public void setStopHistoryMap(Map<Integer, Integer> stopHistoryMap) {
        this.stopHistoryMap = stopHistoryMap;
    }
}
