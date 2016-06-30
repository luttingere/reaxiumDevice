package ggsmarttechnologyltd.reaxium_access_control.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import cn.com.aratek.dev.Terminal;
import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.iccard.ICCardReader;
import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.admin.errormessages.RFIDErrorMessage;
import ggsmarttechnologyltd.reaxium_access_control.admin.threads.ScannersActivityHandler;
import ggsmarttechnologyltd.reaxium_access_control.beans.SecurityObject;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger at G&G on 13/04/2016.
 * <p/>
 * Utility methods for all android applications
 */
public class GGUtil {

    private static final String TAG = GGGlobalValues.TRACE_ID;
    private static final SimpleDateFormat time_format = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    /**
     * Navigate to any screen in the app
     *
     * @param context
     * @param arguments
     * @param screen
     */
    public static void goToScreen(Context context, Bundle arguments, Class screen) {
        try {
            Intent goToTheMain = new Intent(context, screen);
            if (arguments != null) {
                goToTheMain.putExtras(arguments);
            }
            context.startActivity(goToTheMain);
        }catch (Exception e){
            Log.e(TAG,"",e);
        }

    }

    public static String getTimeFormatted(Date date){
        return time_format.format(date);
    }


    public static String  getDeviceId(Context context){
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(context);
        String deviceId = "";
        if(sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID)> 0){
            deviceId = String.valueOf(sharedPreferenceUtil.getLong(GGGlobalValues.DEVICE_ID));
        };
        return deviceId;
    }

    /**
     * Init the finger scanner
     */
    public static Boolean startFingerScannerService(Context context, ScannersActivityHandler scannersActivityHandler) {
        Boolean success = Boolean.FALSE;
        App.fingerprintScanner = FingerprintScanner.getInstance();
        int error;

        if ((error = App.fingerprintScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
            scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "Error initializing finger scanner, error code: " + error + ", reset device"));
            Log.e(TAG, "***[*.*]*** Error initializing FingerPrint Scanner error code: " + error);
        }
        if ((error = App.fingerprintScanner.open()) == FingerprintScanner.RESULT_OK) {

            if ((error = App.bione.initialize(context, GGGlobalValues.BION_DB_PATH)) == Bione.RESULT_OK) {

                Log.i(TAG, "Inicializacion del FingerPrint Correcta");
                Result res = App.fingerprintScanner.getSN();

                Log.i(TAG, "FingerPrint Serial Number: " + (String) res.data);

                res = App.fingerprintScanner.getFirmwareVersion();
                Log.i(TAG, "FingerPrint Firmware Number: " + (String) res.data);

                Log.i(TAG, "FingerPrint SDK Number: " + Terminal.getSdkVersion());

                success = Boolean.TRUE;

            } else {
                Log.e(TAG, "***[*.*]*** Error initializing Fingerprint Algorithm error code: " + error);
                scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "Error initializing Fingerprint Algorithm, error code: " + error + ", reset device"));
            }
        } else {
            scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "Error initializing finger scanner, error code: " + error + ", reset device"));
            Log.e(TAG, "***[*.*]*** Error initializing FingerPrint Scanner error code: " + error);
        }

        return success;
    }

    public static Boolean openCardReader(Context context, ScannersActivityHandler scannersActivityHandler) {
        Boolean isOk = Boolean.FALSE;
        int retires = 3;
        int intent = 0;
        int error;
        while(intent < retires){
            intent++;
            try {
                App.cardReader = ICCardReader.getInstance();
                if ((error = App.cardReader.powerOn()) != ICCardReader.RESULT_OK) {
                 //   scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "the system fail turning on the card reader, error code: " + RFIDErrorMessage.getErrorMessage(error)));
                }
                if ((error = App.cardReader.open()) != ICCardReader.RESULT_OK) {
                    //scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "the system fail opening the card reader, error code: " + RFIDErrorMessage.getErrorMessage(error)));
                } else {
                    isOk = Boolean.TRUE;
                    intent = retires;
                }
            } catch (Exception e) {
                closeCardReader();
                Log.e(TAG, "Error initializing the card reader", e);
                if(intent == retires){
                    scannersActivityHandler.sendMessage(scannersActivityHandler.obtainMessage(ScannersActivityHandler.ERROR_ROUTINE, "the system fail opening the card reader, error code: " + e.getMessage()));
                }else{
                    try {Thread.sleep(500);}catch (Exception e1){}
                }
            }
        }
        return isOk;
    }

    public static void closeCardReader() {
        if(App.cardReader != null){
            App.cardReader.close();
            App.cardReader.powerOff();
            App.cardReader = null;
        }
    }

    public static SecurityObject scanRFID(ICCardReader cardReader) {
        Result res = cardReader.activate();
        Integer userId = null;
        SecurityObject securityObject = new SecurityObject();
        if (res.error == ICCardReader.RESULT_OK) {
            int error;
            long cardID = (Long) res.data;
            Log.i(TAG, "Crad reader number: " + cardID);
            error = cardReader.validateKey(cardID, ICCardReader.KEY_A, GGGlobalValues.BYTE_BLOCK, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
            if (error == ICCardReader.RESULT_OK) {
                res = cardReader.read(GGGlobalValues.BYTE_BLOCK);
                if (res.error == ICCardReader.RESULT_OK) {
                    byte[] data = (byte[]) res.data;
                    Log.i(TAG, "data read: " + Arrays.toString(data));
                    ByteBuffer wrapper = ByteBuffer.wrap(data);
                    userId = wrapper.getInt();
                    securityObject.setCardId(cardID);
                    securityObject.setUserId(userId);
                } else {
                    securityObject.setErrorCode(res.error);
                    securityObject.setErrorMessage(RFIDErrorMessage.getErrorMessage(res.error));
                }
            } else {
                securityObject.setErrorCode(error);
                securityObject.setErrorMessage(RFIDErrorMessage.getErrorMessage(error));
            }
        } else {
            securityObject.setErrorCode(res.error);
            securityObject.setErrorMessage(RFIDErrorMessage.getErrorMessage(res.error));
        }
        return securityObject;
    }


    /**
     *
     * @param context
     */
    public static void registerLastSynchronization(Context context){
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(context);
        sharedPreferenceUtil.save(GGGlobalValues.LAST_SYNCHRONIZATION_DATE, new Date().getTime());
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getLastSynchronization(Context context){
        String lastSynchronization = "";
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(context);
        Long timeLong = sharedPreferenceUtil.getLong(GGGlobalValues.LAST_SYNCHRONIZATION_DATE);
        if(timeLong > 0){
            Date lastSynchronizationDate = new Date(timeLong);
            lastSynchronization  = date_format.format(lastSynchronizationDate);
        }
        return lastSynchronization;
    }

    /**
     * Close fingerprint
     */
    public static void closeFingerPrint() {
        if( App.fingerprintScanner != null){
            App.fingerprintScanner.close();
            App.fingerprintScanner.powerOff();
            App.fingerprintScanner = null;
            App.bione.exit();
        }
    }

    /**
     * validate if a number is even
     *
     * @param number
     * @return
     */
    public static Boolean isEven(int number) {
        Boolean isEven = Boolean.FALSE;
        if ((number % 2) == 0) {
            isEven = Boolean.TRUE;
        }
        return isEven;
    }


    /**
     * hide the softKeyboard
     *
     * @param context
     */
    public static void hideKeyboard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Validate if the network connection is available
     *
     * @param context
     * @return TRUE or FALSE
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean isNetworkAvailable = Boolean.FALSE;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvailable = Boolean.TRUE;
        }
        return isNetworkAvailable;
    }


    /**
     * show a message as a toast
     *
     * @param context
     * @param message
     */
    public static void showAToast(Context context, Integer message) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG).show();
    }

    /**
     * show a message as a toast
     *
     * @param context
     * @param message
     */
    public static void showAToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * show a message as a toast
     *
     * @param context
     * @param message
     */
    public static void showAShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }


    /**
     * Utility method to convert a hexadecimal string to a byte string.
     * <p/>
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     *
     * @param first First array
     * @param rest  Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * Convert a FingerPrintImage Object to an Android Bitmap Object
     *
     * @param fingerprintImage
     * @return
     */
    public static Bitmap fingerPrintImageToBitmap(FingerprintImage fingerprintImage) {
        byte[] fpBmp = null;
        Bitmap bitmap = null;
        if (fingerprintImage != null) {
            fpBmp = fingerprintImage.convert2Bmp();
            bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length);
        }
        return bitmap;
    }

}
