package com.zyxb.qqxmpp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;

public class NetUtil {

	public static boolean checkNet(Context context) {

		boolean isWifi = isWIFIConnected(context);
		boolean isMobile = isMobileConnected(context);

		if (!isWifi && !isMobile) {
			return false;
		}

		if (isWifi)
			return true;

		if (isMobile) {
			// setApnParam(context);//系统应用才能使用
		}

		return true;
	}

	public static boolean isWIFIConnected(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null && info.isConnected())
			return true;

		return false;
	}

	public static boolean isMobileConnected(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null && info.isConnected())
			return true;

		return false;
	}

	public static void setApnParam(Context context) {
		Uri uri = Uri.parse("content://telephony/carriers/preferapn");
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if (cursor != null && cursor.getCount() == 1) {
			if (cursor.moveToFirst()) {
				Const.PROXY_IP = cursor.getString(cursor
						.getColumnIndex("proxy"));
				Const.PROXY_PORT = cursor.getInt(cursor.getColumnIndex("port"));
			}
		}

	}

	public static final int NETWORN_NONE = 0;
	public static final int NETWORN_WIFI = 1;
	public static final int NETWORN_MOBILE = 2;

	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_WIFI;
		}

		// 3G
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_MOBILE;
		}
		return NETWORN_NONE;
	}
}
