package ggsmarttechnologyltd.reaxium_access_control.database;

import android.provider.BaseColumns;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class StopsContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * create table sentence
     */
    public static final String SQL_CREATE_STOP_TABLE =
            "CREATE TABLE " + ReaxiumStop.TABLE_NAME + " (" +
                    ReaxiumStop._ID + " INTEGER PRIMARY KEY," +
                    ReaxiumStop.COLUMN_NAME_STOP_ID + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_ORDER + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumStop.COLUMN_NAME_STOP_ROUTE_ID + INTEGER_TYPE  + " )";

    /**
     * Delete table sentence
     */
    public static final String SQL_DELETE_SOP_TABLE =
            "DROP TABLE IF EXISTS " + ReaxiumStop.TABLE_NAME;



    public StopsContract(){}



    /**
     * ReaxiumStop Table
     */
    public static abstract class ReaxiumStop implements BaseColumns {
        public static final String TABLE_NAME = "reaxium_stop";
        public static final String COLUMN_NAME_STOP_ID = "id_stop";
        public static final String COLUMN_NAME_STOP_ORDER = "stop_order";
        public static final String COLUMN_NAME_STOP_NUMBER = "stop_number";
        public static final String COLUMN_NAME_STOP_NAME = "stop_name";
        public static final String COLUMN_NAME_STOP_ADDRESS = "stop_address";
        public static final String COLUMN_NAME_STOP_LATITUDE = "stop_latitude";
        public static final String COLUMN_NAME_STOP_LONGITUDE = "stop_longitude";
        public static final String COLUMN_NAME_STOP_ROUTE_ID = "id_route";
    }

}
