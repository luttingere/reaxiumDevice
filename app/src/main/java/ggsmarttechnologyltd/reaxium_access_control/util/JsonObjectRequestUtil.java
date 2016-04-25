package ggsmarttechnologyltd.reaxium_access_control.util;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;


/**
 * Created by Eduardo Luttinger on 20/05/2015.
 */
public class JsonObjectRequestUtil extends JsonObjectRequest {

    private String user = "reaxium";
    private String pass = "reaxium";
    private Map<String,String> params;

    public JsonObjectRequestUtil(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(GGGlobalValues.TIME_OUT_SECONDS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (user + ":" + pass).getBytes(),
        Base64.NO_WRAP);
        params.put("Authorization",base64EncodedCredentials);
        return params;
    }
}
