package com.zyxb.qqxmpp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {

	public static boolean contains(Context context, String key) {
		return context.getSharedPreferences("config", Context.MODE_PRIVATE)
				.contains(key);
	}

	public static int getInt(Context context, String key, int defValue) {

		return context.getSharedPreferences("config", Context.MODE_PRIVATE)
				.getInt(key, defValue);
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static String getString(Context context, String key, String defValue) {

		return context.getSharedPreferences("config", Context.MODE_PRIVATE)
				.getString(key, defValue);
	}

	public static void setString(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {

		return context.getSharedPreferences("config", Context.MODE_PRIVATE)
				.getBoolean(key, defValue);
	}

	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static long getLong(Context context, String key, long defValue) {

		return context.getSharedPreferences("config", Context.MODE_PRIVATE)
				.getLong(key, defValue);
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static float getFloat(Context context, String key, float defValue) {

		return context.getSharedPreferences("config", Context.MODE_PRIVATE)
				.getFloat(key, defValue);
	}

	public static void setFloat(Context context, String key, float value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static void clear(Context context) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

}
