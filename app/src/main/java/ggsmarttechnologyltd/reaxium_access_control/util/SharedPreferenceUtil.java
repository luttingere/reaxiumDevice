package ggsmarttechnologyltd.reaxium_access_control.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import ggsmarttechnologyltd.reaxium_access_control.global.GGGlobalValues;


/**
 * @author Eduardo Luttinger GG Smart Technology
 *         <p/>
 *         Android Shared Preferences Manager
 *         <p/>
 *         Singleton Pattern
 */
public class SharedPreferenceUtil {

    /**
     *
     */
    private static SharedPreferenceUtil mSharedPreferenceUtil;

    /**
     *
     */
    private Context mContext;

    /**
     *
     */
    private SharedPreferences settings;

    /**
     *
     */
    private Editor editor;

    /**
     *
     */
    private SharedPreferenceUtil(Context context) {
        super();
        this.mContext = context;
        settings = mContext.getSharedPreferences(GGGlobalValues.PREFERENCE_FOLDER, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    /**
     * @param context
     * @return
     */
    public static SharedPreferenceUtil getInstance(Context context) {
        if (mSharedPreferenceUtil == null) {
            mSharedPreferenceUtil = new SharedPreferenceUtil(context);
        }
        return mSharedPreferenceUtil;
    }

    /**
     * @param key
     * @param value
     */
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * @param key
     * @param value
     */
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * @param key
     * @param value
     */
    public void save(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * @param key
     * @param value
     */
    public void save(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * @param key
     * @param value
     */
    public void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * @param key
     * @param value
     */
    public void save(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * @param key
     * @return
     */
    public String getString(String key) {
        return settings.getString(key, null);
    }

    /**
     * @param key
     * @return
     */
    public int getInt(String key) {
        return settings.getInt(key, 0);
    }

    /**
     * @param key
     * @return
     */
    public float getFloat(String key) {
        return settings.getFloat(key, 0.0f);
    }

    /**
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        return settings.getBoolean(key, false);
    }

    /**
     * @param key
     * @return
     */
    public long getLong(String key) {
        return settings.getLong(key, 0);
    }

    /**
     *
     */
    public void clearSharedPreference() {
        editor.clear();
        editor.commit();
    }

    /**
     * @param key
     */
    public void removeValue(String key) {
        editor.remove(key);
        editor.commit();
    }
}