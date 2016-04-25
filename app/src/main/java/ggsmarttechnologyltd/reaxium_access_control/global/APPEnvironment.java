package ggsmarttechnologyltd.reaxium_access_control.global;

import java.io.Serializable;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class APPEnvironment implements Serializable {

    private static final String DEVELOPMENT_URL = "http://54.200.133.84/reaxium/";
    private static final String Environment = DEVELOPMENT_URL;

    public static String createURL(String serviceName){
        return Environment + serviceName;
    }

}
