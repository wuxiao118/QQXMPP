package com.zyxb.qqxmpp.ui;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zyxb.qqxmpp.App;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;

public class BaseActivity extends Activity {
	protected static String TAG = "BaseActivity";
	protected App mApp;
	protected Context mContext;
	protected DBUser mUser;
	protected DataEngine mEngine;

	// 连接服务器广播
	// private LoginReceiver loginReceiver;

	//登陆及连接广播
	private ConnectReceiver mConnectReceiver;
	private IntentFilter mConnectFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (App) getApplication();
		mApp.add(this);
		mContext = this;

		// 去掉title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 检查是否登陆
		if (!checkUser()) {
			return;
		}

		// // 注册login receiver
		// loginReceiver = new LoginReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(LoginService.LOGIN_SERVER_CONNECTED);
		// filter.addAction(LoginService.LOGIN_SERVER_DISCONNECTED);
		// filter.addAction(LoginService.LOGIN_SERVER_RECONNECT);
		// registerReceiver(loginReceiver, filter);

		//连接及登陆
		mConnectReceiver = new ConnectReceiver();
		mConnectFilter = new IntentFilter();
		mConnectFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
		mConnectFilter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
		mConnectFilter.addAction(ChatService.LOGIN);
		//registerReceiver(mConnectReceiver,mConnectFilter);
	}

	protected boolean checkUser() {
		mUser = mApp.getmUser();
		if (mUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			// TODO 登陆后，如何返回当前页面
			intent.putExtra("packageName", getPackageName());
			intent.putExtra("activitySimpleName", this.getClass()
					.getSimpleName());
			intent.putExtra("activityFullName", this.getClass().getName());
			startActivity(intent);

			finish();

			return false;
		}

		mEngine = new DataEngine(this, mApp.getmUser());

		return true;
	}

	protected void userExit() {
		// 发送注销广播
		Intent logoutIntent = new Intent(ChatService.LOGOUT);
		sendBroadcast(logoutIntent);
		Intent connCloseIntent = new Intent(ConnectService.CONNECT_CLOSE);
		sendBroadcast(connCloseIntent);

		mApp.finish();
		SharedPreferencesUtils.clear(this);
		mApp.setmUser(null);
		checkUser();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 注册login receiver
		// loginReceiver = new LoginReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(LoginService.LOGIN_SERVER_CONNECTED);
		// filter.addAction(LoginService.LOGIN_SERVER_DISCONNECTED);
		// filter.addAction(LoginService.LOGIN_SERVER_RECONNECT);
		// registerReceiver(loginReceiver, filter);
		registerReceiver(mConnectReceiver,mConnectFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//unregisterReceiver(loginReceiver);
		unregisterReceiver(mConnectReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApp.remove(this);
		mEngine = null;
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(View view, int id) {
		return (T) view.findViewById(id);
	}

	/**
	 * 弹出输入法窗口
	 */
	protected void showSoftInputView(final View v) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((InputMethodManager) v.getContext().getSystemService(
						Service.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 0);
	}

	/**
	 * 隐藏软键盘
	 */
	protected void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mApp.back();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 登陆receiver
	 *
	 * @author 吴小雄
	 *
	 */
	//@SuppressWarnings("unused")
	//private class LoginReceiver extends BroadcastReceiver {
	private class ConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// String action = intent.getAction();
			String action = intent.getAction();
			int reason = intent.getIntExtra("reason", -1);
			Logger.d(TAG, "base action:" + action + ",reason:" + reason);

			if (action.equals(ConnectService.LOGIN_SERVER_CONNECTED)) {
				//mApp.setConnected(true);
			} else if (action.equals(ConnectService.LOGIN_SERVER_DISCONNECTED)) {
				mApp.setConnected(false);
				Toast.makeText(mContext,"服务器连接断开",Toast.LENGTH_SHORT).show();

				return;
			} else if (action.equals(ConnectService.LOGIN_SERVER_RECONNECT)) {
				mApp.setConnected(false);

				Toast.makeText(mContext, "服务器连接失败,重新连接中...", Toast.LENGTH_LONG)
						.show();
				return;
			}

			switch (reason) {
				// 连接成功
				case ChatService.SERVER_CONNECTED_USER_LOGIN:
					// 登陆成功
					mApp.setConnected(true);

					break;
				case ConnectService.SERVER_CONNECTED:
					// 连接成功,未登录

					break;
				case ChatService.SERVER_CONNECTED_USER_REJECTED:
					// 用户名或密码错误,关闭所有activity,进入登陆界面
					Toast.makeText(mContext, "连接服务器成功,用户名或密码错误,请重新登录",
							Toast.LENGTH_SHORT).show();

					mApp.finish();
					Intent it = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(it);

					break;
				// 连接失败
				case ConnectService.SERVER_DISCONNECTED_CONNECTION_ERROR:
					Toast.makeText(mContext, "服务器连接错误", Toast.LENGTH_SHORT).show();

					break;
				case ConnectService.SERVER_DISCONNECTED_FAIL:
					Toast.makeText(mContext, "服务器连接失败", Toast.LENGTH_SHORT).show();

					break;
				case ConnectService.SERVER_DISCONNECTED_NOT_RESPONSE:
					Toast.makeText(mContext, "服务器无应答", Toast.LENGTH_SHORT).show();

					break;
				case ConnectService.SERVER_DISCONNECTED_PING_TIMEOUT:
					Toast.makeText(mContext, "服务器连接中断,重新连接", Toast.LENGTH_SHORT)
							.show();
					Intent reconnect = new Intent(ChatService.LOGIN);
					sendBroadcast(reconnect);

					break;
				case ConnectService.SERVER_DISCONNECTED_CONNECTION_CLOSED:
					Toast.makeText(mContext, "服务器连接失败", Toast.LENGTH_SHORT).show();

					break;
				case ConnectService.SERVER_DISCONNECTED_NET_DISCONNECTED:
					Toast.makeText(mContext, "网络未连接", Toast.LENGTH_SHORT).show();

					break;
			}

			// 连接失败处理
		}
	}
}
