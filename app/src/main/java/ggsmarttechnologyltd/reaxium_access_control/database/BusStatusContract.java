package ggsmarttechnologyltd.reaxium_access_control.database;

import android.provider.BaseColumns;

/**
 *  Created by Eduardo Luttinger on 21/04/2016.
 *
 *  Bus status data contract
 *
 */
public final class BusStatusContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * create table sentence
     */
    public static final String SQL_CREATE_BUS_STATUS_TABLE =
            "CREATE TABLE " + BusStatusTable.TABLE_NAME + " (" +
                    BusStatusTable._ID + " INTEGER PRIMARY KEY," +
                    BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID + INTEGER_TYPE + COMMA_SEP +
                    BusStatusTable.COLUMN_NAME_LAST_STOP_ORDER + INTEGER_TYPE + COMMA_SEP +
                    BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT + INTEGER_TYPE  + " )";

    /**
     * Delete table sentence
     */
    public static final String SQL_DELETE_BUS_STATUS_TABLE =
            "DROP TABLE IF EXISTS " + BusStatusTable.TABLE_NAME;



    public BusStatusContract(){}



    /**
     * user access control table
     */
    public static abstract class BusStatusTable implements BaseColumns {
        public static final String TABLE_NAME = "bus_status";
        public static final String COLUMN_NAME_LAST_ROUTE_ID = "last_route_id";
        public static final String COLUMN_NAME_LAST_STOP_ORDER = "last_stop_order";
        public static final String COLUMN_NAME_USER_ABOARD_COUNT = "users_aboard";
    }
}

