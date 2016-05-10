package ggsmarttechnologyltd.reaxium_access_control.global;

import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger at G&G on 11/04/2016.
 */
public class GGGlobalValues {

    public static String TRACE_ID = "REAXIUM_DEVICE";
    public static final String STRING_DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final String PREFERENCE_FOLDER = "ggsmarttechnologyltd.reaxiumdevice";
    public static final Integer TIME_OUT_SECONDS = 20000;
    public static final Integer FRAGMENT_CONTAINER = R.id.fragment_container;
    public static final Integer SUCCESSFUL_API_RESPONSE_CODE = 0;
    public static final String BION_DB_PATH = "/sdcard/fp.db";
    public static final Long DEFAULT_DEVICE_ID = 1l;


    //Device cache storage
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String DEVICE_DESCRIPTION = "DEVICE_DESCRIPTION";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String DEVICE_STATUS = "DEVICE_STATUS";
    public static final String APPLICATION_NAME = "APPLICATION_NAME";
    public static final String APPLICATION_VERSION = "APPLICATION_VERSION";
    public static final String LAST_SYNC = "LAST_SYNC";

    public static final String USER_ID_IN_SESSION = "USER_ID_IN_SESSION";
    public static final String USER_FULL_NAME_IN_SESSION = "USER_FULL_NAME_IN_SESSION";
    public static final String USER_FULL_TYPE_IN_SESSION = "USER_FULL_TYPE_IN_SESSION";
    public static final String DEFAULT_USER_TYPE = "Student";


    //RFID
    public static final int BYTE_SIZE = 16;
    public static final int BYTE_BLOCK = 4;



    //Service Names
    public static final String SEARCH_USERS_BY_FILTER = "Users/allUsersWithFilter";
    public static final String LOAD_USER_BIOMETRIC_INFO = "Biometric/biometricAccess";
    public static final String LOAD_USER_RFID_INFO = "RFID/saveRFIDInformation";
    public static final String CONFIGURE_DEVICE = "Device/configureDevice";
    public static final String SYNCHRONIZE_DEVICE = "Device/synchronizeDeviceAccess";
    public static final String VALIDATE_ACCESS = "Access/checkUserAccess";
    public static final String SAVE_A_USER = "Users/saveUserFromDevice";
    public static final String SAVE_ACCESS_IN_SERVER = "UserAccess/executeAnAccessOfAUser";
    public static final String EDIT_A_USER = "Users/editUserFromDevice";

}
