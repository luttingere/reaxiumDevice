package ggsmarttechnologyltd.reaxium_access_control.database;

import android.util.Log;

import java.util.List;

import cn.com.aratek.util.Result;
import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.beans.BiometricData;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class BiometricDAO {

    public static final String TAG  = GGGlobalValues.TRACE_ID;

    public static Boolean storeBiometrics(List<BiometricData> biometricData){
        Log.i(TAG,"prepared to enroll biometrics in system");
        byte[] biometricFeature;
        Result biometricResult;
        Boolean enrollOk = Boolean.FALSE;
        int result;
        try {
            App.bione.clear();
            for (BiometricData biometric:biometricData) {
                biometricFeature = GGUtil.HexStringToByteArray(biometric.getBiometricCode());
                biometricResult = App.bione.makeTemplate(biometricFeature, biometricFeature, biometricFeature);
                Log.i(TAG,"make tamplate result: "+biometricResult.error);
                biometricFeature = (byte[]) biometricResult.data;
                result = App.bione.enroll(biometric.getUserId().intValue(),biometricFeature);
                Log.i(TAG,"result of the bion enroll: "+result);
                enrollOk = Boolean.TRUE;
            }
        }catch (Exception e){
            Log.e(TAG,"Error enrrolling the biometric data in the device",e);
        }
        return enrollOk;
    }

}
