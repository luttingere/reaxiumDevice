package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.beans.Routes;
import ggsmarttechnologyltd.reaxium_access_control.beans.Stops;
import ggsmarttechnologyltd.reaxium_access_control.beans.SynchronizationObject;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class RouteStopUsersDAO {

    public static final String TAG = GGGlobalValues.TRACE_ID;
    private SQLiteDatabase database;
    private ReaxiumDbHelper dbHelper;
    private static RouteStopUsersDAO routeStopUsersDAO;
    private Context context;

    private RouteStopUsersDAO(Context context) {
        this.context = context;
        dbHelper = ReaxiumDbHelper.getInstance(context);
    }

    public static RouteStopUsersDAO getInstance(Context context) {
        if (routeStopUsersDAO == null) {
            routeStopUsersDAO = new RouteStopUsersDAO(context);
        }
        return routeStopUsersDAO;
    }

    public void deleteAllValuesFromTheTables() {
        database = dbHelper.getWritableDatabase();
        database.delete(RoutesContract.ReaxiumRoutes.TABLE_NAME, null, null);
        database.delete(StopsContract.ReaxiumStop.TABLE_NAME, null, null);
        database.delete(UserInStopContract.ReaxiumUserAtStop.TABLE_NAME, null, null);
    }

    public Boolean saveRoutesStopsAndUsers(List<Routes> listOfRoutesStopsAndUsers) {
        Boolean result = Boolean.FALSE;
        ContentValues routeValues;
        ContentValues stopValues;
        ContentValues userValues;
        try {
            database = dbHelper.getWritableDatabase();
            database.beginTransaction();

            //Run Routes save process
            for (Routes routes : listOfRoutesStopsAndUsers) {

                routeValues = new ContentValues();
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_ID, routes.getRouteId());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_TYPE, routes.getRouteType());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_NAME, routes.getRouteName());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_NUMBER, routes.getRouteNumber());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_ADDRESS, routes.getRouteAddress());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_POLYLINE, routes.getRoutePolyLine());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_START_DATE, routes.getRouteDateInit().getTime());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_END_DATE, routes.getRouteDateEnd().getTime());
                routeValues.put(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_STOP_COUNT, routes.getStopCount());
                database.insert(RoutesContract.ReaxiumRoutes.TABLE_NAME, null, routeValues);

                //Run stop save process
                if (routes.getStops() != null && !routes.getStops().isEmpty()) {
                    for (Stops stops : routes.getStops()) {
                        stopValues = new ContentValues();
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ID, stops.getStopId());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ORDER, stops.getStopOrder());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ADDRESS, stops.getStopAddress());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_NUMBER, stops.getStopNumber());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_NAME, stops.getStopName());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_LATITUDE, stops.getStopLatitude());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_LONGITUDE, stops.getStopLongitude());
                        stopValues.put(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ROUTE_ID, routes.getRouteId());
                        database.insert(StopsContract.ReaxiumStop.TABLE_NAME, null, stopValues);

                        //Run userAtStop save process
                        if (stops.getUsers() != null && !stops.getUsers().isEmpty()) {
                            for (User user : stops.getUsers()) {
                                userValues = new ContentValues();
                                userValues.put(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_USER_ID, user.getUserId());
                                userValues.put(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_INIT, user.getUserAtStopDateInit().getTime());
                                userValues.put(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_END, user.getUserAtStopDateEnd().getTime());
                                userValues.put(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_STOP_ID, stops.getStopId());
                                database.insert(UserInStopContract.ReaxiumUserAtStop.TABLE_NAME, null, userValues);
                            }
                        }
                    }
                }
            }
            result = Boolean.TRUE;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "", e);
            database.endTransaction();
        } finally {
            try {
                database.endTransaction();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public List<Routes> getRoutesRegistered() {
        List<Routes> routesList = null;
        Routes route;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_ID,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_TYPE,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_NAME,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_NUMBER,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_ADDRESS,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_POLYLINE,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_START_DATE,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_END_DATE,
                    RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_STOP_COUNT};

            String selection = null;
            String[] arguments = null;
            String orderBy = RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_START_DATE + " DESC";

            resultSet = database.query(RoutesContract.ReaxiumRoutes.TABLE_NAME, projection, selection, arguments, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                routesList = new ArrayList<>();
                while (resultSet.isAfterLast() == false) {
                    route = new Routes();
                    route.setRouteId(resultSet.getLong(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_ID)));
                    route.setRouteType(resultSet.getInt(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_TYPE)));
                    route.setRouteName(resultSet.getString(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_NAME)));
                    route.setRouteAddress(resultSet.getString(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_ADDRESS)));
                    route.setRoutePolyLine(resultSet.getString(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_POLYLINE)));
                    route.setRouteNumber(resultSet.getString(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_NUMBER)));
                    route.setRouteDateInit(new Date(resultSet.getLong(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_START_DATE))));
                    route.setRouteDateEnd(new Date(resultSet.getLong(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_END_DATE))));
                    route.setStopCount(resultSet.getLong(resultSet.getColumnIndex(RoutesContract.ReaxiumRoutes.COLUMN_NAME_ROUTE_STOP_COUNT)));
                    routesList.add(route);
                    resultSet.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return routesList;
    }

    public List<Stops> getStopsOfARoute(Long routeId) {
        List<Stops> stopsList = null;
        Stops stops = null;
        Cursor resultSet = null;
        List<User> userList;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ID,
                    StopsContract.ReaxiumStop.COLUMN_NAME_STOP_NUMBER,
                    StopsContract.ReaxiumStop.COLUMN_NAME_STOP_NAME,
                    StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ADDRESS,
                    StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ORDER,
                    StopsContract.ReaxiumStop.COLUMN_NAME_STOP_LATITUDE,
                    StopsContract.ReaxiumStop.COLUMN_NAME_STOP_LONGITUDE};

            String selection = StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ROUTE_ID + "=?";
            String[] arguments = {"" + routeId.longValue()};
            String orderBy = StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ORDER + " ASC";

            resultSet = database.query(StopsContract.ReaxiumStop.TABLE_NAME, projection, selection, arguments, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                stopsList = new ArrayList<>();
                while (resultSet.isAfterLast() == false) {
                    stops = new Stops();
                    stops.setStopId(resultSet.getLong(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ID)));
                    stops.setStopOrder(resultSet.getInt(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ORDER)));
                    stops.setStopName(resultSet.getString(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_NAME)));
                    stops.setStopNumber(resultSet.getString(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_NUMBER)));
                    stops.setStopAddress(resultSet.getString(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ADDRESS)));
                    stops.setStopLatitude(resultSet.getString(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_LATITUDE)));
                    stops.setStopLongitude(resultSet.getString(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_LONGITUDE)));
                    userList = getUsersAtAStop(stops.getStopId());
                    if (userList != null) {
                        stops.setUsers(userList);
                    } else {
                        stops.setUsers(new ArrayList<User>());
                    }
                    stopsList.add(stops);
                    resultSet.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return stopsList;
    }


    public List<Long> getAllUsersIdOfARoute(Long routeId) {
        Long stopId = 0L;
        Cursor resultSet = null;
        List<Long> userIDList =  new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<User> temporalUserList = new ArrayList<>();
        try {

            database = dbHelper.getReadableDatabase();
            String[] projection = {StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ID};
            String selection = StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ROUTE_ID + "=?";
            String[] arguments = {"" + routeId.longValue()};
            String orderBy = StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ORDER + " ASC";

            resultSet = database.query(StopsContract.ReaxiumStop.TABLE_NAME, projection, selection, arguments, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                while (resultSet.isAfterLast() == false) {
                    stopId = resultSet.getLong(resultSet.getColumnIndex(StopsContract.ReaxiumStop.COLUMN_NAME_STOP_ID));
                    temporalUserList = getUsersAtAStop(stopId);
                    if(temporalUserList != null){
                        userList.addAll(temporalUserList);
                    }
                    resultSet.moveToNext();
                }
            }
            for (User user : userList) {
                userIDList.add(user.getUserId());
            }

        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return userIDList;
    }


    public List<User> getUsersAtAStop(Long stopId) {
        List<User> userAtStopList = null;
        User user = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_USER_ID,
                    UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_INIT,
                    UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_END};

            String selection = UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_STOP_ID + "=?";
            String[] arguments = {"" + stopId.longValue()};
            String orderBy = UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_INIT + " DESC";

            resultSet = database.query(UserInStopContract.ReaxiumUserAtStop.TABLE_NAME, projection, selection, arguments, null, null, orderBy);
            if (resultSet.moveToFirst()) {
                userAtStopList = new ArrayList<>();
                while (resultSet.isAfterLast() == false) {
                    user = new User();
                    user.setUserId(resultSet.getLong(resultSet.getColumnIndex(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_USER_ID)));
                    user.setUserAtStopDateInit(new Date(resultSet.getLong(resultSet.getColumnIndex(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_INIT))));
                    user.setUserAtStopDateEnd(new Date(resultSet.getLong(resultSet.getColumnIndex(UserInStopContract.ReaxiumUserAtStop.COLUMN_NAME_TIME_END))));
                    userAtStopList.add(user);
                    resultSet.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (resultSet != null) {
                if (!resultSet.isClosed()) {
                    resultSet.close();
                }
            }
        }
        return userAtStopList;
    }


}
