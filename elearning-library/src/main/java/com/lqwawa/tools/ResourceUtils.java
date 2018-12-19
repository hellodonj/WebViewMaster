package com.lqwawa.tools;

import java.lang.reflect.Field;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author 作者 shouyi
 * @version 创建时间：Jul 11, 2016 5:06:54 PM 类说明
 */
public class ResourceUtils {

	private static String PACKAGENAME = null;

	public static void setPackageName(String packageName) {
		PACKAGENAME = packageName;
	}

	public static String getPackageName(Context context) {
		return !TextUtils.isEmpty(PACKAGENAME) ? PACKAGENAME : context.getPackageName();
	}

	public static int getAnimId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "anim",
				paramContext.getPackageName());
	}

	public static int getDimenId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "dimen",
				paramContext.getPackageName());
	}

	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}

	public static int getStringId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string",
				paramContext.getPackageName());
	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", paramContext.getPackageName());
	}

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}

	public static int getColorId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color",
				paramContext.getPackageName());
	}

	public static int getArrayId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "array",
				paramContext.getPackageName());
	}

	public static int getRawId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "raw",
				paramContext.getPackageName());
	}

	public static int getStyleId(Context paramContext, String paramString) {
		// return paramContext.getResources().getIdentifier(paramString,
		// "style",
		// getPackageName(paramContext));
		Log.i("", "getStyleId start");
		Class style = null;
		try {
			String packageName = getPackageName(paramContext);
			style = Class.forName(packageName + ".R$style");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Field field = style.getField(paramString);
			Log.i("", "getStyleId  " + field + ", id " + field.getInt(paramString));
			return field.getInt(paramString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
