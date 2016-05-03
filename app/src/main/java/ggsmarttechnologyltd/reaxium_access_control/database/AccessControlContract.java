package ggsmarttechnologyltd.reaxium_access_control.database;

import android.provider.BaseColumns;

/**
 *  Created by Eduardo Luttinger on 21/04/2016.
 *
 *  Contract reader por control the users access to the system
 *
 */
public final class AccessControlContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * create table sentence
     */
    public static final String SQL_CREATE_ACCESS_CONTROL_TABLE =
            "CREATE TABLE " + AccessControlTable.TABLE_NAME + " (" +
                    AccessControlTable._ID + " INTEGER PRIMARY KEY," +
                    AccessControlTable.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE + INTEGER_TYPE + COMMA_SEP +
                    AccessControlTable.COLUMN_NAME_ACCESS_TYPE + TEXT_TYPE  + " )";

    /**
     * Delete table sentence
     */
    public static final String SQL_DELETE_ACCESS_CONTROL_TABLE =
            "DROP TABLE IF EXISTS " + AccessControlTable.TABLE_NAME;



    public AccessControlContract(){}



    /**
     * user access control table
     */
    public static abstract class AccessControlTable implements BaseColumns {
        public static final String TABLE_NAME = "access_control";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_USER_ACCESS_TYPE = "user_access_type";
        public static final String COLUMN_NAME_USER_ACCESS_DATE = "access_date";
        public static final String COLUMN_NAME_ACCESS_TYPE = "access_type";    //----> IN OR OUT
    }
}

