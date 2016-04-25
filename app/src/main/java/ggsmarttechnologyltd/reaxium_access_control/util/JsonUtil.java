package ggsmarttechnologyltd.reaxium_access_control.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import ggsmarttechnologyltd.reaxium_access_control.App;
import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 21/10/2015.
 */
public class JsonUtil {


    public static JSONArray getJsonArray(String objectName, JSONObject jsonObject){
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray(objectName);

        }catch(JSONException e){
            Log.e(GGGlobalValues.TRACE_ID,"Error traying to get a jsonarray with the name: "+objectName,e);
        }
        return jsonArray;
    }

    public static JSONObject getJsonObject(String objectName, JSONObject jsonObjectParam){
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonObjectParam.getJSONObject(objectName);

        }catch(JSONException e){
            Log.e(GGGlobalValues.TRACE_ID,"Error traying to get a jsonObject with the name: "+objectName,e);
        }
        return jsonObject;
    }

    public static <T> T getEntityFromJSON(JSONObject jsonObject,Type type){
        return MySingletonUtil.getInstance(App.getContext()).getGSON().fromJson(jsonObject.toString(),type);
    }

    public static <T> T getEntityFromJSON(String jsonObject, Type type){
        return MySingletonUtil.getInstance(App.getContext()).getGSON().fromJson(jsonObject,type);
    }
}
