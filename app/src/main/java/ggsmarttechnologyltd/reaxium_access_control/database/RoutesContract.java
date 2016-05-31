package ggsmarttechnologyltd.reaxium_access_control.database;

import android.provider.BaseColumns;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class RoutesContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * create table sentence
     */
    public static final String SQL_CREATE_ROUTE_TABLE =
            "CREATE TABLE " + ReaxiumRoutes.TABLE_NAME + " (" +
                    ReaxiumRoutes._ID + " INTEGER PRIMARY KEY," +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_ID + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_POLYLINE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_STOP_COUNT + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_TYPE + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_START_DATE + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumRoutes.COLUMN_NAME_ROUTE_END_DATE + INTEGER_TYPE  + " )";

    /**
     * Delete table sentence
     */
    public static final String SQL_DELETE_ROUTE_TABLE =
            "DROP TABLE IF EXISTS " + ReaxiumRoutes.TABLE_NAME;



    public RoutesContract(){}



    /**
     * ReaxiumRoutes Table
     */
    public static abstract class ReaxiumRoutes implements BaseColumns {
        public static final String TABLE_NAME = "reaxium_routes";
        public static final String COLUMN_NAME_ROUTE_ID = "id_route";
        public static final String COLUMN_NAME_ROUTE_TYPE = "route_type";
        public static final String COLUMN_NAME_ROUTE_NUMBER = "route_number";
        public static final String COLUMN_NAME_ROUTE_NAME = "route_name";
        public static final String COLUMN_NAME_ROUTE_ADDRESS = "route_address";
        public static final String COLUMN_NAME_ROUTE_POLYLINE = "route_polyline";
        public static final String COLUMN_NAME_ROUTE_STOP_COUNT = "routes_stops_count";
        public static final String COLUMN_NAME_ROUTE_START_DATE = "route_start_date";
        public static final String COLUMN_NAME_ROUTE_END_DATE = "route_end_date";
    }

}
