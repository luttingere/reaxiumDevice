package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class ReaxiumDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reaxium.db";
    public static ReaxiumDbHelper reaxiumDbHelper;


    public static ReaxiumDbHelper getInstance(Context context){
        if(reaxiumDbHelper == null){
            reaxiumDbHelper = new ReaxiumDbHelper(context);
        }
        return reaxiumDbHelper;
    }

    private ReaxiumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ReaxiumUsersContract.SQL_CREATE_USER_TABLE);
        db.execSQL(AccessControlContract.SQL_CREATE_ACCESS_CONTROL_TABLE);
        db.execSQL(RoutesContract.SQL_CREATE_ROUTE_TABLE);
        db.execSQL(StopsContract.SQL_CREATE_STOP_TABLE);
        db.execSQL(UserInStopContract.SQL_CREATE_USER_AT_STOP_TABLE);
        db.execSQL(BusStatusContract.SQL_CREATE_BUS_STATUS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(ReaxiumUsersContract.SQL_DELETE_USER_TABLE);
        db.execSQL(AccessControlContract.SQL_DELETE_ACCESS_CONTROL_TABLE);
        db.execSQL(RoutesContract.SQL_DELETE_ROUTE_TABLE);
        db.execSQL(StopsContract.SQL_DELETE_SOP_TABLE);
        db.execSQL(UserInStopContract.SQL_DELETE_USER_AT_STOP_TABLE);
        db.execSQL(BusStatusContract.SQL_DELETE_BUS_STATUS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
