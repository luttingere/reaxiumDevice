package ggsmarttechnologyltd.reaxium_access_control.database;

import android.provider.BaseColumns;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class UserInStopContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * create table sentence
     */
    public static final String SQL_CREATE_USER_AT_STOP_TABLE =
            "CREATE TABLE " + ReaxiumUserAtStop.TABLE_NAME + " (" +
                    ReaxiumUserAtStop._ID + " INTEGER PRIMARY KEY," +
                    ReaxiumUserAtStop.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumUserAtStop.COLUMN_NAME_TIME_INIT + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumUserAtStop.COLUMN_NAME_TIME_END + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumUserAtStop.COLUMN_NAME_STOP_ID + INTEGER_TYPE  + " )";

    /**
     * Delete table sentence
     */
    public static final String SQL_DELETE_USER_AT_STOP_TABLE =
            "DROP TABLE IF EXISTS " + ReaxiumUserAtStop.TABLE_NAME;



    public UserInStopContract(){}



    /**
     * ReaxiumUserAtStop Table
     */
    public static abstract class ReaxiumUserAtStop implements BaseColumns {
        public static final String TABLE_NAME = "reaxium_user_at_stop";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_TIME_INIT = "date_init";
        public static final String COLUMN_NAME_TIME_END = "date_end";
        public static final String COLUMN_NAME_STOP_ID = "id_stop";
    }

}
