package ggsmarttechnologyltd.reaxium_access_control.util;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger on 26/10/2015.
 */
public class StringRequestUtil extends StringRequest {

    private String user = "reaxium";
    private String pass = "reaxium";

    public StringRequestUtil(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(GGGlobalValues.TIME_OUT_SECONDS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public StringRequestUtil(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(GGGlobalValues.TIME_OUT_SECONDS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP);
        params.put("Authorization", base64EncodedCredentials);
        return params;
    }
}
