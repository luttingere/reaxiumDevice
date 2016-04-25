package ggsmarttechnologyltd.reaxium_access_control.database;

import android.provider.BaseColumns;

/**
 *  Created by Eduardo Luttinger on 21/04/2016.
 *
 *  Contract reader por search reaxium users in the device
 *
 */
public final class ReaxiumUsersContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    /**
     * create table sentence
     */
    public static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + ReaxiumUsers.TABLE_NAME + " (" +
                    ReaxiumUsers._ID + " INTEGER PRIMARY KEY," +
                    ReaxiumUsers.COLUMN_NAME_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_STATUS + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_TYPE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_BIRTH_DATE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_PHOTO + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_BIOMETRIC_CODE + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_FINGERPRINT + TEXT_TYPE + COMMA_SEP +
                    ReaxiumUsers.COLUMN_NAME_USER_RFID_CODE + TEXT_TYPE  + " )";

    /**
     * Delete table sentence
     */
    public static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + ReaxiumUsers.TABLE_NAME;



    public ReaxiumUsersContract(){}



    /**
     * Reaxium Users Table
     */
    public static abstract class ReaxiumUsers implements BaseColumns {
        public static final String TABLE_NAME = "reaxium_users";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_USER_ACCESS_TYPE = "user_access_type";
        public static final String COLUMN_NAME_USER_NAME= "user_name";
        public static final String COLUMN_NAME_USER_SECOND_NAME= "user_second_name";
        public static final String COLUMN_NAME_USER_LAST_NAME= "user_last_name";
        public static final String COLUMN_NAME_USER_SECOND_LAST_NAME= "user_second_last_name";
        public static final String COLUMN_NAME_USER_DOCUMENT_ID = "user_document_id";
        public static final String COLUMN_NAME_USER_STATUS = "user_status";
        public static final String COLUMN_NAME_USER_TYPE = "user_type";
        public static final String COLUMN_NAME_USER_BIRTH_DATE = "user_birth_date";
        public static final String COLUMN_NAME_USER_PHOTO = "user_photo";
        public static final String COLUMN_NAME_USER_BUSINESS_NAME = "user_business_name";
        public static final String COLUMN_NAME_USER_BIOMETRIC_CODE= "user_biometric_code";
        public static final String COLUMN_NAME_USER_RFID_CODE= "user_rfid_code";
        public static final String COLUMN_NAME_USER_FINGERPRINT= "user_fingerprint";
    }
}
