package ggsmarttechnologyltd.reaxium_access_control.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;

/**
 * Created by Eduardo Luttinger G&G
 * <p/>
 * Agregar aqui todas los objetos cuyo comportamiento sea de ambito de aplicacion
 * <p/>
 * <p/>
 * ------------------------------------------------------------------
 * Volley:
 * <p/>
 * Acceso al RequestQueue desde una Actividad Ej:
 * MySingletonUtil.getInstance(this.getApplicationContext()).getRequestQueue();
 * <p/>
 * Agregado de request al Queue Ej:
 * <p/>
 * MySingletonUtil.getInstance(this).addToRequestQueue(stringRequest);
 * ------------------------------------------------------------------
 */
public class MySingletonUtil {

    /**
     *
     */
    private static MySingletonUtil sMySingletonUtil;

    /**
     *
     */
    private RequestQueue mRequestQueue;

    /**
     *
     */
    private static Context mContext;

    /**
     *
     */
    private static Gson GSON;


    /**
     *
     */
    private static GsonBuilder GSON_BUILDER;

    /**
     *
     */
    private ImageLoader mImageLoader;

    private MySingletonUtil(Context context) {
        mContext = context;

        mRequestQueue = getRequestQueue();

        GSON_BUILDER = new GsonBuilder();

        GSON_BUILDER.setDateFormat(GGGlobalValues.STRING_DATE_PATTERN_WITH_TIMEZONE);
        GSON_BUILDER.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                String dateLiteral = json.getAsJsonPrimitive().getAsString();
                SimpleDateFormat format = new SimpleDateFormat(GGGlobalValues.STRING_DATE_PATTERN_WITH_TIMEZONE);
                Date jsonDate = null;
                try {
                    jsonDate = format.parse(dateLiteral);
                } catch (ParseException e) {
                    jsonDate = new Date(json.getAsJsonPrimitive().getAsLong());
                }
                return jsonDate;
            }
        });


        GSON = GSON_BUILDER.create();
    }

    /**
     * @param context
     * @return
     */
    public static synchronized MySingletonUtil getInstance(Context context) {
        if (sMySingletonUtil == null) {
            sMySingletonUtil = new MySingletonUtil(context);
        }
        return sMySingletonUtil;
    }

    /**
     * @param req
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public Gson getGSON() {
        if (GSON == null) {
            GSON = GSON_BUILDER.create();
        }
        return GSON;
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache() {
                @Override
                public void putBitmap(String key, Bitmap value) { }

                @Override
                public Bitmap getBitmap(String key) {
                    return null;
                }
            });
        }
        return this.mImageLoader;
    }

    public void cancelRequest(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
