package ggsmarttechnologyltd.reaxium_access_control.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.util.List;

import ggsmarttechnologyltd.reaxium_access_control.GGMainActivity;
import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.beans.AccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.BiometricData;
import ggsmarttechnologyltd.reaxium_access_control.beans.DeviceData;
import ggsmarttechnologyltd.reaxium_access_control.beans.Routes;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessControl;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessData;
import ggsmarttechnologyltd.reaxium_access_control.database.AccessControlDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.BiometricDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.ReaxiumUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.database.RouteStopUsersDAO;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 20/05/2016.
 */
public class SynchronizeController extends GGController {

    private AccessControlDAO accessControlDAO;
    private ReaxiumUsersDAO reaxiumUsersDAO;
    private RouteStopUsersDAO routeStopUsersDAO;

    public SynchronizeController(Context context, GGMainActivity activity) {
        super(context,activity);
        accessControlDAO = AccessControlDAO.getInstance(context);
        reaxiumUsersDAO = ReaxiumUsersDAO.getInstance(context);
        routeStopUsersDAO = RouteStopUsersDAO.getInstance(context);
    }

    /**
     * Synchronize the device with its information in the cloud (Users access, Routes, Stops, and Users in those stops)
     *
     * @param deviceData
     * @param outOfSyncAccess
     * @return TRUE if the synchronization end successfully, False id dont
     */
    public Boolean synchronizeDevice(DeviceData deviceData,List<AccessControl> outOfSyncAccess) {
        Boolean filledOk = Boolean.FALSE;
        try {
            Log.i(TAG, "prepared to clear reaxium user access table");
            clearTheUserAccessData();

            Log.i(TAG, "prepared to clear reaxium routes stops and user at the stop info table");
            clearTheRouteStopUserAtStopTables();

            Log.i(TAG, "prepared to fill reaxium user access table");
            Boolean userTableFilledOK = fillUsersTable(deviceData.getDeviceAccessData());

            Log.i(TAG, "prepared to fill reaxium route stop and user at stop tables");
            Boolean routeStopAndUserTableFilledOk = fillRouteStopAndUserTable(deviceData.getDeviceRoutesInfo().getRoutes());

            filledOk = userTableFilledOK && routeStopAndUserTableFilledOk;
            if (filledOk) {
                Log.i(TAG, "lookup for biometrical access info and store it in the fingerprint scanner system");
                storeBiometricInformation();
                updateOutOfSyncAccess(outOfSyncAccess);
            }
        } catch (Exception e) {
            Log.e(TAG,"",e);
        }
        return filledOk;
    }

    /**
     * store in the device biometric system the users biometrical information
     */
    private void storeBiometricInformation(){
        //Run the biometric store information process
        Log.i(TAG, "prepared to extract biometric data to the device db");
        List<BiometricData> biometricDataList = reaxiumUsersDAO.getUsersBiometricData();
        if (biometricDataList != null && !biometricDataList.isEmpty()) {
            Log.i(TAG, "biometric data retrieved successfully, biometric count: " + biometricDataList.size());
            Boolean enrollOk = BiometricDAO.storeBiometrics(biometricDataList, mContext);
            if (enrollOk) {
                Log.i(TAG, "biometric information stored successfully");
            } else {
                Log.i(TAG, "Error storing the biometric information in the device");
            }
        } else {
            Log.i(TAG, "No biometric information found");
        }
    }

    /**
     * mark As Registered in cloud each out of sync access record
     * @param outOfSyncAccess
     */
    private void updateOutOfSyncAccess(List<AccessControl> outOfSyncAccess){
        if(outOfSyncAccess != null && !outOfSyncAccess.isEmpty()){
            accessControlDAO.markAsRegisteredInCloudAsBulk(outOfSyncAccess);
        }
    }

    /**
     * inicializacion del objeto de acxceso a la tabla de usuarios con acceso al dispositivo
     */
    private void clearTheUserAccessData(){
        reaxiumUsersDAO.deleteAllValuesFromReaxiumUserTable();
    }

    private void clearTheRouteStopUserAtStopTables(){
        routeStopUsersDAO.deleteAllValuesFromTheTables();
    }

    private Boolean fillUsersTable(List<UserAccessControl> userAccessData){
        return reaxiumUsersDAO.fillUsersTable(userAccessData);
    }

    private Boolean fillRouteStopAndUserTable(List<Routes> routesList){
        return routeStopUsersDAO.saveRoutesStopsAndUsers(routesList);
    }


}
