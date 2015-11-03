package com.zyxb.qqxmpp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.util.AppShortCutUtil;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.NetUtil;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;

public class NetChangedReceiver extends BroadcastReceiver {
	private static final String TAG = "NetChangedReceiver";
	//public static final String STOP_LOGIN_SERVICE = "com.zyxb.qqxmpp.STOP_LOGIN_SERVICE";
	//public static final String STOP_XMPP_SERVICE = "com.zyxb.qqxmpp.STOP_XMPP_SERVICE";
	private Context mContext;
	private boolean isConnected;
	private DataEngine mEngine;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		isConnected = NetUtil.checkNet(context);
		mEngine = new DataEngine(context);

		Logger.d(TAG, "onReceive:" + intent.getAction() + ",net state:"
				+ isConnected);

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)
				|| action.equals(Intent.ACTION_SCREEN_ON)) {
			// 启动设置未读数量
			String username = SharedPreferencesUtils.getString(context,
					Const.SP_USERNAME, "");
			int num = mEngine.getUnReadedMessage(DBColumns.USER_NICKNAME,
					username);
			AppShortCutUtil.addNumShortCut(context, null, true, num + "", true);
		}

		// 未开启网络连接,如果服务开启,关闭服务
		// 锁屏下,关闭服务
		if (!isConnected || action.equals(Intent.ACTION_SCREEN_OFF)) {
			stopChatService();
			stopConnectService();
		} else if (isConnected || action.equals(Intent.ACTION_BOOT_COMPLETED)
				|| action.equals(Intent.ACTION_SCREEN_ON)) {
			Logger.d(TAG, "start login service");

			//开启服务
			Intent connService = new Intent(context,ConnectService.class);
			context.startService(connService);
			Intent chatService = new Intent(context,ChatService.class);
			context.startService(chatService);
		}
	}

	private void stopConnectService() {
		// 关闭服务并注销reciever		
		Logger.d(TAG, "stop xmpp service");
		Intent intent = new Intent(ConnectService.CONNECT_SERVICE_CLOSE);
		mContext.sendBroadcast(intent);
	}

	private void stopChatService() {
		Logger.d(TAG, "stop login service:");
		Intent intent = new Intent(ChatService.CHAT_SERVICE_CLOSE);
		mContext.sendBroadcast(intent);
	}

}
