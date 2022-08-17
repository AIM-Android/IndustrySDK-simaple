package com.advantech.simpale;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;


public class SharePreferenceUtil {
    public static final String KIOSK_STATUS = "kiosk_status";
    public static final String PRIVATEDATA = "privateDate";
    public static final String MQTTCONFIGKEY = "mqttConfig";
    public static final String PROFILE_SETTING = "profile_setting";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String FILE_NAME, int mode) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME,
                mode);
        editor = sharedPreferences.edit();
    }

    //put value
    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            Log.d("DeployService", "putLong: key = "+key+", val = "+ (Long) object);
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    //get value
    public Object getSharedPreference(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            Log.d("DeployService", "getLong: key = "+key);
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    //remove value by key
    public void remove(String key) {
        Log.d("DeployService", "remove: key = "+key);
        editor.remove(key);
        editor.commit();
    }

    //remove all value
    public void clear() {
        Log.d("DeployService", "clear: ");
        editor.clear();
        editor.commit();
    }

    //check value by key
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    //return map
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

}
