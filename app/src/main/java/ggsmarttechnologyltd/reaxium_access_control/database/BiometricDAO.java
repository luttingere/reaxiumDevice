package ggsmarttechnologyltd.reaxium_access_control.database;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.com.aratek.fp.Bione;
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

    public static Boolean storeBiometrics(List<BiometricData> biometricData, Context context){
        Log.i(TAG,"prepared to enroll biometrics in system");
        byte[] biometricFeature;
        Result biometricResult;
        Boolean enrollOk = Boolean.FALSE;
        int result;
        try {

            int error = App.bione.initialize(context, GGGlobalValues.BION_DB_PATH);
            if(error == Bione.RESULT_OK){
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
                App.bione.exit();
            }else{
                Log.i(TAG,"Fingerprint Algorithm cannot be initialized, error code: "+error);
            }
        }catch (Exception e){
            Log.e(TAG,"Error enrrolling the biometric data in the device",e);
        }
        return enrollOk;
    }

}
