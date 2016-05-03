package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 27/04/2016.
 */
public class AccessControlDAO {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private SQLiteDatabase database;
    private ReaxiumDbHelper dbHelper;
    private static AccessControlDAO accessControlDAO;
    private Context context;
    private ContentValues insertValues;

    private AccessControlDAO(Context context) {
        dbHelper = new ReaxiumDbHelper(context);
    }

    public static AccessControlDAO getInstance(Context context) {
        if (accessControlDAO == null) {
            accessControlDAO = new AccessControlDAO(context);
        }
        return accessControlDAO;
    }

    /**
     * delete all the values from the table reaxium_users
     */
    public void deleteAllValuesFromAccessControlTable() {
        database = dbHelper.getWritableDatabase();
        database.delete(AccessControlContract.AccessControlTable.TABLE_NAME, null, null);
    }

    /**
     * insert an access in system
     *
     * @param userId
     * @param userAccessType
     * @param accessType
     * @return
     */
    public Boolean insertUserAccess(Long userId, String userAccessType, String accessType) {
        Boolean success = Boolean.FALSE;
        try {
            database = dbHelper.getWritableDatabase();
            insertValues = new ContentValues();
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID, userId);
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE, userAccessType);
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE, accessType);
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE, new Date().getTime());
            long inserted = database.insert(AccessControlContract.AccessControlTable.TABLE_NAME, null, insertValues);
            success = Boolean.TRUE;
            Log.i(TAG, "user access inserted successfully insertion id: "+inserted);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting the access action of a user", e);
        }
        return success;
    }

    /**
     * get the last access associated to an specific user
     *
     * @return last user access
     */
    public AccessControl getLastAccess(String userId) {
        AccessControl accessControl = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE
            };
            String selection = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID + "=?";
            String[] seelcctionArgs = {userId};
            String orderBy = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE + " DESC";
            String limit = "1";
            resultSet = database.query(AccessControlContract.AccessControlTable.TABLE_NAME, projection, selection, seelcctionArgs, null, null, orderBy, limit);
            if (resultSet.moveToFirst()) {
                accessControl = new AccessControl();
                accessControl.setUserId(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID)));
                accessControl.setAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE)));
                accessControl.setUserAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE)));
                accessControl.setAccessDate(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving the last access user information from device db", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return accessControl;
    }


    /**
     * get all access associated to an specific user
     *
     * @return last user access
     */
    public List<AccessControl> getAllAccess(long userId) {
        List<AccessControl> accessControlList = new ArrayList<>();
        AccessControl accessControl = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE
            };
            String selection = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID + "=?";
            String[] seelcctionArgs = {"" + userId};
            String orderBy = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE + " DESC";
            resultSet = database.query(AccessControlContract.AccessControlTable.TABLE_NAME, projection, selection, seelcctionArgs, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                accessControlList = new ArrayList<>();
                while (resultSet.isAfterLast() == false) {
                    accessControl = new AccessControl();
                    accessControl.setUserId(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID)));
                    accessControl.setAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE)));
                    accessControl.setUserAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE)));
                    accessControl.setAccessDate(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE)));
                    accessControlList.add(accessControl);
                    resultSet.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving all user access data from device db", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return accessControlList;
    }


}
