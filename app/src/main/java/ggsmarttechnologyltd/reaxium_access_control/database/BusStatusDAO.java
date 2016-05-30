package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ggsmarttechnologyltd.reaxium_access_control.beans.BusStatus;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 23/05/2016.
 */
public class BusStatusDAO {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private SQLiteDatabase database;
    private ReaxiumDbHelper dbHelper;
    private static BusStatusDAO busStatusDAO;
    private Context context;
    private ContentValues insertValues;

    private BusStatusDAO(Context context) {
        this.context = context;
        dbHelper = ReaxiumDbHelper.getInstance(context);
    }

    public static BusStatusDAO getInstance(Context context) {
        if (busStatusDAO == null) {
            busStatusDAO = new BusStatusDAO(context);
        }
        return busStatusDAO;
    }

    /**
     * delete all the values from the table reaxium_users
     */
    public void deleteAllValuesFromBusStatus() {
        database = dbHelper.getWritableDatabase();
        database.delete(BusStatusContract.BusStatusTable.TABLE_NAME, null, null);
    }

    /**
     * initialize a route in a bus
     *
     * @param routeId
     * @param stopOrder
     * @return the row ID of the newly inserted row, or null if an error occurred
     */
    public Long initBusRoute(Long routeId, Long stopOrder) {
        Long inserted = null;
        try {
            database = dbHelper.getWritableDatabase();
            insertValues = new ContentValues();
            insertValues.put(BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID, routeId);
            insertValues.put(BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_STOP_ORDER, stopOrder);
            insertValues.put(BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT, 0);
            inserted = database.insert(BusStatusContract.BusStatusTable.TABLE_NAME, null, insertValues);
            Log.i(TAG, "user access inserted successfully, newly inserted row id: " + inserted);
        } catch (Exception e) {
            inserted = null;
            Log.e(TAG, "Error inserting the access action of a user", e);
        } finally {
        }
        return inserted;
    }


    /**
     * aboard a user on the bus
     */
    public Integer aboardAUserOnTheBus(Long routeId) {
        Integer userCountResult = 0;
        try {
            database = dbHelper.getWritableDatabase();
            userCountResult = getUserCount(routeId) + 1;
            insertValues = new ContentValues();
            insertValues.put(BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT, userCountResult);
            String whereClause = BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID + "=?";
            String[] whereArguments = {"" + routeId.longValue()};
            Integer rowAffected = database.update(BusStatusContract.BusStatusTable.TABLE_NAME, insertValues, whereClause, whereArguments);
            Log.i(TAG,"row affected: "+rowAffected);
            Log.i(TAG, "bus status updated successfully (User aboard)");
        } catch (Exception e) {
            Log.e(TAG, "Error updating the bus status", e);
        } finally {
        }
        return userCountResult;
    }

    /**
     * aboard a user on the bus
     */
    public Integer dropAUserOffTheBus(Long routeId) {
        Integer userCountResult = 0;
        try {
            database = dbHelper.getWritableDatabase();
            userCountResult = getUserCount(routeId) - 1;
            if(userCountResult < 0){
                userCountResult = 0;
            }
            insertValues = new ContentValues();
            insertValues.put(BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT, userCountResult);
            String whereClause = BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID + "=?";
            String[] whereArguments = {"" + routeId.longValue()};
            database.update(BusStatusContract.BusStatusTable.TABLE_NAME, insertValues, whereClause, whereArguments);
            Log.i(TAG, "bus status updated successfully (User aboard)");
        } catch (Exception e) {
            Log.e(TAG, "Error updating the bus status", e);
        } finally {
        }
        return userCountResult;
    }

    /**
     * aboard a user on the bus
     */
    public void changeTheNextStop(Integer stopOrder) {
        try {
            database = dbHelper.getWritableDatabase();
            insertValues = new ContentValues();
            insertValues.put(BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_STOP_ORDER, stopOrder);
            String whereClause = AccessControlContract.AccessControlTable._ID + "=?";
            String[] whereArguments = {"" + 1};
            database.update(BusStatusContract.BusStatusTable.TABLE_NAME, insertValues, whereClause, whereArguments);
            Log.i(TAG, "bus status updated successfully (Stop changed)");
        } catch (Exception e) {
            Log.e(TAG, "Error updating the bus status", e);
        } finally {
        }
    }

    /**
     * get users aboard count
     *
     * @return user aborad count
     */
    public Integer getUserCount(Long routeId) {
        Cursor resultSet = null;
        Integer userCount = 0;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT};
            String where = BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID + "=?";
            String[] whereArguments = {""+routeId.longValue()};
            String orderBy = null;
            resultSet = database.query(BusStatusContract.BusStatusTable.TABLE_NAME, projection, where, whereArguments, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                userCount = resultSet.getInt(resultSet.getColumnIndex(BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT));
                Log.i(TAG,"User count result: "+userCount);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error obtaining the users aboard count", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return userCount;
    }

    /**
     * get users aboard count
     *
     * @return user aborad count
     */
    public BusStatus getBusStatus() {
        Cursor resultSet = null;
        BusStatus busStatus = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID,
                    BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_STOP_ORDER,
                    BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT};
            String selection = null;
            String[] selectionArguments = null;
            String orderBy = null;
            resultSet = database.query(BusStatusContract.BusStatusTable.TABLE_NAME, projection, selection, selectionArguments, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                busStatus = new BusStatus();
                busStatus.setRouteId(resultSet.getLong(resultSet.getColumnIndex(BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_ROUTE_ID)));
                busStatus.setStopOrder(resultSet.getInt(resultSet.getColumnIndex(BusStatusContract.BusStatusTable.COLUMN_NAME_LAST_STOP_ORDER)));
                busStatus.setUserCount(resultSet.getInt(resultSet.getColumnIndex(BusStatusContract.BusStatusTable.COLUMN_NAME_USER_ABOARD_COUNT)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error obtaining the bus status", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return busStatus;
    }
}
