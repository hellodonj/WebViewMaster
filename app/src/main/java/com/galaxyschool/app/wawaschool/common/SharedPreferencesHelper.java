package com.galaxyschool.app.wawaschool.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author 作者 shouyi:
 * @version 创建时间：Jun 15, 2015 11:17:57 AM
 * 类说明
 */
public class SharedPreferencesHelper {

	public static final String SP_FILE = "wawatong.dat";
	public static final String PUSH_MESSAGE = "push_message";
	
	public static void setString(Context context, String key, String value) {
		if (context == null || TextUtils.isEmpty(key)) {
			return;
		}
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
	
	public static String getString(Context context, String key) {
		if (context == null || TextUtils.isEmpty(key)) {
			return null;
		}
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
	
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		if (context == null || TextUtils.isEmpty(key)) {
			return defValue;
		}
		SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}
	
	public static void setBoolean(Context context, String key, boolean value) {
		if (context == null || TextUtils.isEmpty(key)) {
			return;
		}
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void clearUserData(Context context) {
    	if (context == null) {
			return;
		}
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PrefsManager.PrefsItems.CONTACTS_SCHOOL_LIST_RESULT);
        editor.remove(PrefsManager.PrefsItems.CONTACTS_PERSONAL_LIST_RESULT);
        editor.remove("SchoolInfoList");
        editor.commit();
    }

}
