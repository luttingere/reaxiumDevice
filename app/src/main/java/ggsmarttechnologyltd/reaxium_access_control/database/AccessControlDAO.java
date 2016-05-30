package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.admin.activity.AdminActivity;
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
        this.context = context;
        dbHelper = ReaxiumDbHelper.getInstance(context);
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
     * @return the row ID of the newly inserted row, or null if an error occurred
     */
    public Long insertUserAccess(Long userId, String userAccessType, String accessType) {
        Long inserted = null;
        try {
            database = dbHelper.getWritableDatabase();
            insertValues = new ContentValues();
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID, userId);
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE, userAccessType);
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE, accessType);
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE, new Date().getTime());
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD, 0);
            inserted = database.insert(AccessControlContract.AccessControlTable.TABLE_NAME, null, insertValues);
            Log.i(TAG, "user access inserted successfully, newly inserted row id: " + inserted);
        } catch (Exception e) {
            inserted = null;
            Log.e(TAG, "Error inserting the access action of a user", e);
        }finally {
        }
        return inserted;
    }

    /**
     * mark a record as registered in cloud
     *
     * @param accessId
     * @return the number of rows affected
     */
    public Integer markAsRegisteredInCloud(Long accessId) {
        Integer rowsAffected = null;
        try {
            database = dbHelper.getWritableDatabase();
            insertValues = new ContentValues();
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD, 1);
            String whereClause = AccessControlContract.AccessControlTable._ID + "=?";
            String[] whereArguments = {"" + accessId};
            rowsAffected = database.update(AccessControlContract.AccessControlTable.TABLE_NAME, insertValues, whereClause, whereArguments);
            Log.i(TAG, "user access inserted successfully,  number of rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting the access action of a user", e);
            rowsAffected = null;
        }finally {
        }
        return rowsAffected;
    }

    /**
     * mark all records out of sync as registered in cloud
     *
     * @param outOfSyncList
     * @return the number of rows affected
     */
    public Integer markAsRegisteredInCloudAsBulk(List<AccessControl> outOfSyncList) {
        Integer rowsAffected = 0;
        try {
            database = dbHelper.getWritableDatabase();
            database.beginTransaction();
            insertValues = new ContentValues();
            insertValues.put(AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD, 1);
            for (AccessControl control: outOfSyncList) {
                String whereClause = AccessControlContract.AccessControlTable._ID + "=?";
                String[] whereArguments = {"" + control.get_ID()};
                rowsAffected = rowsAffected + database.update(AccessControlContract.AccessControlTable.TABLE_NAME, insertValues, whereClause, whereArguments);
            }
            database.setTransactionSuccessful();
            Log.i(TAG, "all access records were updated successfully,  number of rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting the access action of a user", e);
            rowsAffected = null;
            database.endTransaction();
        }finally {
            try {
                database.endTransaction();
            }catch (Exception e){
                Log.e(TAG,"Error",e);
            }
        }
        return rowsAffected;
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
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD
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
                accessControl.setOnTheCloud(resultSet.getInt(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD)) != 0);
                accessControl.setDeviceId(((GGMainActivity)context).getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
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
     * get the all users in the bus
     *
     * @return last user access
     */
    public List<AccessControl> getAllAccessIN() {
        List<AccessControl> accessControlList = null;
        AccessControl accessControl = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD
            };
            String selection = AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE + "=?";
            String[] selectionArgs = {"IN"};
            String orderBy = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE + " DESC";
            resultSet = database.query(AccessControlContract.AccessControlTable.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                accessControlList = new ArrayList<>();
                while (resultSet.isAfterLast() == false){
                    accessControl = new AccessControl();
                    accessControl.setUserId(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID)));
                    accessControl.setAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE)));
                    accessControl.setUserAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE)));
                    accessControl.setAccessDate(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE)));
                    accessControl.setOnTheCloud(resultSet.getInt(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD)) != 0);
                    accessControl.setDeviceId(((GGMainActivity)context).getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
                    accessControlList.add(accessControl);
                    resultSet.moveToNext();
                }
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
        return accessControlList;
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
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD
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
                    accessControl.setOnTheCloud(resultSet.getInt(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD)) != 0);
                    accessControl.setDeviceId(((GGMainActivity)context).getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
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

    /**
     * get all access out of sync
     *
     * @return last user access
     */
    public List<AccessControl> getAllAccessOutOfSync() {
        List<AccessControl> accessControlList = new ArrayList<>();
        AccessControl accessControl = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    AccessControlContract.AccessControlTable._ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE
            };
            String selection = AccessControlContract.AccessControlTable.COLUMN_REGISTERED_IN_CLOUD + "=?";
            String[] seelcctionArgs = {"0"};
            String orderBy = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE + " DESC";
            resultSet = database.query(AccessControlContract.AccessControlTable.TABLE_NAME, projection, selection, seelcctionArgs, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                accessControlList = new ArrayList<>();
                while (resultSet.isAfterLast() == false) {
                    accessControl = new AccessControl();
                    accessControl.set_ID(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable._ID)));
                    accessControl.setUserId(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID)));
                    accessControl.setAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE)));
                    accessControl.setUserAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE)));
                    accessControl.setAccessDate(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE)));
                    accessControl.setDeviceId(((GGMainActivity)context).getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
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


    /**
     * get all access in the Device
     *
     * @return last user access
     */
    public List<AccessControl> getAllAccessInDevice() {
        List<AccessControl> accessControlList = new ArrayList<>();
        AccessControl accessControl = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    AccessControlContract.AccessControlTable._ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE,
                    AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE
            };
            String orderBy = AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE + " DESC";
            resultSet = database.query(AccessControlContract.AccessControlTable.TABLE_NAME, projection, null, null, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                accessControlList = new ArrayList<>();
                while (resultSet.isAfterLast() == false) {
                    accessControl = new AccessControl();
                    accessControl.set_ID(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable._ID)));
                    accessControl.setUserId(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ID)));
                    accessControl.setAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_ACCESS_TYPE)));
                    accessControl.setUserAccessType(resultSet.getString(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_TYPE)));
                    accessControl.setAccessDate(resultSet.getLong(resultSet.getColumnIndex(AccessControlContract.AccessControlTable.COLUMN_NAME_USER_ACCESS_DATE)));
                    accessControl.setDeviceId(((GGMainActivity)context).getSharedPreferences().getLong(GGGlobalValues.DEVICE_ID));
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
