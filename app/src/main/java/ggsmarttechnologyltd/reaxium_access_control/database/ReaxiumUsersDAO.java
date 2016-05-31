package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ggsmarttechnologyltd.reaxium_access_control.beans.BiometricData;
import ggsmarttechnologyltd.reaxium_access_control.beans.Business;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.beans.UserAccessControl;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 * <p/>
 * class in charge of execute selects, inserts and updates operations in the ReaxiumUsers table
 */
public class ReaxiumUsersDAO {

    public static final String TAG = GGGlobalValues.TRACE_ID;
    private SQLiteDatabase database;
    private ReaxiumDbHelper dbHelper;
    private static ReaxiumUsersDAO reaxiumUsersDAO;
    private ContentValues insertValues;

    private ReaxiumUsersDAO(Context context) {
        dbHelper = ReaxiumDbHelper.getInstance(context);
    }

    public static ReaxiumUsersDAO getInstance(Context context) {
        if (reaxiumUsersDAO == null) {
            reaxiumUsersDAO = new ReaxiumUsersDAO(context);
        }
        return reaxiumUsersDAO;
    }

    /**
     * delete all the values from the table reaxium_users
     */
    public void deleteAllValuesFromReaxiumUserTable() {
        database = dbHelper.getWritableDatabase();
        database.delete(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, null, null);

    }

    /**
     * fill the table reaxium_users
     *
     * @param userAccessControlList
     */
    public Boolean fillUsersTable(List<UserAccessControl> userAccessControlList) {
        Boolean succcess = Boolean.FALSE;
        try {
            database = dbHelper.getWritableDatabase();
            database.beginTransaction();
            for (UserAccessControl userAccessControl : userAccessControlList) {
                insertValues = new ContentValues();
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID, userAccessControl.getUserAccessData().getUserId());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE, userAccessControl.getUserAccessData().getAccessType().getAccessTypeName());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME, userAccessControl.getUserAccessData().getUser().getFirstName());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME, userAccessControl.getUserAccessData().getUser().getSecondName());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME, userAccessControl.getUserAccessData().getUser().getFirstLastName());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME, userAccessControl.getUserAccessData().getUser().getSecondLastName());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BIRTH_DATE, userAccessControl.getUserAccessData().getUser().getBirthDate());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME, userAccessControl.getUserAccessData().getUser().getBusiness().getBusinessName());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO, userAccessControl.getUserAccessData().getUser().getPhoto());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID, userAccessControl.getUserAccessData().getUser().getDocumentId());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_STATUS, userAccessControl.getUserAccessData().getUser().getStatus().getStatusName());
                if(userAccessControl.getUserAccessData().getUser().getUserType() == null){
                    insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_TYPE, GGGlobalValues.DEFAULT_USER_TYPE);
                }else{
                    insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_TYPE, userAccessControl.getUserAccessData().getUser().getUserType().getUserTypeName());
                }
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_CODE, userAccessControl.getUserAccessData().getDocumentId());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BIOMETRIC_CODE, userAccessControl.getUserAccessData().getBiometricCode());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_RFID_CODE, userAccessControl.getUserAccessData().getRfidCode());
                insertValues.put(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_FINGERPRINT, userAccessControl.getUserAccessData().getUser().getFingerprint());
                database.insert(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, null, insertValues);
            }
            succcess = Boolean.TRUE;
            Log.i(TAG, "Reaxium Users access data successfully stored in db");
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error saving the users dataaccess", e);
            database.endTransaction();
        } finally {
            database.endTransaction();
        }
        return succcess;
    }

    /**
     * @return biometric data
     */
    public List<BiometricData> getUsersBiometricData() {
        List<BiometricData> biometricDataList = null;
        BiometricData biometricData = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BIOMETRIC_CODE
            };
            String selection = ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE + "=?";
            String[] seelcctionArgs = {"Biometric"};
            resultSet = database.query(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, projection, selection, seelcctionArgs, null, null, null);
            if (resultSet.moveToFirst()) {
                biometricDataList = new ArrayList<>();
                Log.i(TAG, "Biometric data found in database");
                while (resultSet.isAfterLast() == false) {
                    biometricData = new BiometricData();
                    biometricData.setUserId(resultSet.getLong(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID)));
                    biometricData.setBiometricCode(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BIOMETRIC_CODE)));
                    biometricDataList.add(biometricData);
                    resultSet.moveToNext();
                }
            }
        }catch (Exception e){
            Log.e(TAG, "Error retrieving the biometric information data from device db", e);
        }finally {
            if(resultSet != null){
                if(!resultSet.isClosed()){
                    resultSet.close();
                }
            }
        }
        return biometricDataList;
    }

    /**
     * @return biometric data
     */
    public List<User> getAllUsersRegisteredInTheDevice() {
        List<User> userList = null;
        User user = null;
        Cursor resultSet = null;
        Map<Long,User> userFiltered = new HashMap<>();
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO
            };
            resultSet = database.query(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, projection, null, null, null, null, ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID+" DESC");
            if (resultSet.moveToFirst()) {
                Log.i(TAG, "Biometric data found in database");
                while (resultSet.isAfterLast() == false) {
                    user = new User();
                    user.setUserId(resultSet.getLong(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID)));
                    user.setFirstName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME)));
                    user.setSecondName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME)));
                    user.setFirstLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME)));
                    user.setSecondLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME)));
                    user.setPhoto(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO)));
                    user.setDocumentId(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID)));
                    userFiltered.put(user.getUserId(),user);
                    resultSet.moveToNext();
                }
            }
        }catch (Exception e){
            Log.e(TAG, "Error retrieving the biometric information data from device db", e);
        }finally {
            if(resultSet != null){
                if(!resultSet.isClosed()){
                    resultSet.close();
                }
            }
        }
        userList = new ArrayList<>();
        if(userFiltered.size() > 0){
            for(Map.Entry<Long,User> entry:userFiltered.entrySet()){
                userList.add(entry.getValue());
            }
        }
        return userList;
    }

    /**
     * @return biometric data
     */
    public List<Integer> getUsersWithBiometric() {
        List<Integer> userIdList = null;
        Integer userId = 0;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID};
            String selection = ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE + "=?";
            String[] seelcctionArgs = {"Biometric"};
            resultSet = database.query(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, projection, selection, seelcctionArgs, null, null, null);
            if (resultSet.moveToFirst()) {
                userIdList = new ArrayList<>();
                Log.i(TAG, "Biometric data found in database");
                while (resultSet.isAfterLast() == false) {
                    userId = resultSet.getInt(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID));
                    userIdList.add(userId);
                    resultSet.moveToNext();
                }
            }
        }catch (Exception e){
            Log.e(TAG,"Error retrieving the biometric information data from device db",e);
        }finally {
            if(resultSet != null){
                if(!resultSet.isClosed()){
                    resultSet.close();
                }
            }
        }
        return userIdList;
    }

    /**
     * @return user found
     */
    public User getUserById(String userId) {
        User user = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME
            };
            String selection = ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE + "=? and " + ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID + "=?";
            String[] seelcctionArgs = {"Biometric", userId};
            resultSet = database.query(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, projection, selection, seelcctionArgs, null, null, null);
            if (resultSet.moveToFirst()) {
                user = new User();
                user.setUserId(resultSet.getLong(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID)));
                user.setFirstName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME)));
                user.setSecondName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME)));
                user.setFirstLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME)));
                user.setSecondLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME)));
                user.setPhoto(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO)));
                user.setDocumentId(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID)));
                Business business = new Business();
                business.setBusinessName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME)));
                user.setBusiness(business);
            }
        }catch (Exception e){
            Log.e(TAG,"Error retrieving user information from the device db",e);
        }finally {
            if(resultSet != null){
                if(!resultSet.isClosed()){
                    resultSet.close();
                }
            }
        }
        return user;
    }

    /**
     *
     * @param userId
     * @param rfidCode
     * @return user found
     */
    public User getUserByIdAndRfidCode(String userId, String rfidCode) {
        User user = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME
            };
            String selection = ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE + "=? and " + ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_RFID_CODE + "=? and " +ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID + "=?";
            String[] seelcctionArgs = {"RFID",rfidCode, userId};
            resultSet = database.query(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, projection, selection, seelcctionArgs, null, null, null);
            if (resultSet.moveToFirst()) {
                user = new User();
                user.setUserId(resultSet.getLong(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID)));
                user.setFirstName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME)));
                user.setSecondName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME)));
                user.setFirstLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME)));
                user.setSecondLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME)));
                user.setPhoto(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO)));
                user.setDocumentId(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID)));
                Business business = new Business();
                business.setBusinessName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME)));
                user.setBusiness(business);
            }
        }catch (Exception e){
            Log.e(TAG,"Error retrieving user information from the device db",e);
        }finally {
            if(resultSet != null){
                if(!resultSet.isClosed()){
                    resultSet.close();
                }
            }
        }
        return user;
    }

    /**
     *
     * @param documentCode
     * @return user found
     */
    public User getUserByDocumentCode(String documentCode) {
        User user = null;
        Cursor resultSet = null;
        try {
            database = dbHelper.getReadableDatabase();
            String[] projection = {
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO,
                    ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME
            };
            String selection = ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ACCESS_TYPE + "=? and " + ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_CODE + "=?";
            String[] seelcctionArgs = {"DocumentID",documentCode};
            resultSet = database.query(ReaxiumUsersContract.ReaxiumUsers.TABLE_NAME, projection, selection, seelcctionArgs, null, null, null);
            if (resultSet.moveToFirst()) {
                user = new User();
                user.setUserId(resultSet.getLong(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_ID)));
                user.setFirstName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_NAME)));
                user.setSecondName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_NAME)));
                user.setFirstLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_LAST_NAME)));
                user.setSecondLastName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_SECOND_LAST_NAME)));
                user.setPhoto(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_PHOTO)));
                user.setDocumentId(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_DOCUMENT_ID)));
                Business business = new Business();
                business.setBusinessName(resultSet.getString(resultSet.getColumnIndex(ReaxiumUsersContract.ReaxiumUsers.COLUMN_NAME_USER_BUSINESS_NAME)));
                user.setBusiness(business);
            }
        }catch (Exception e){
            Log.e(TAG,"Error retrieving user information from the device db",e);
        }finally {
            if(resultSet != null){
                if(!resultSet.isClosed()){
                    resultSet.close();
                }
            }
        }
        return user;
    }


}
