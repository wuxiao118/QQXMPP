package com.zyxb.qqxmpp.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zyxb.qqxmpp.App;
import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.MD5Encoder;
import com.zyxb.qqxmpp.util.NetUtil;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;
import com.zyxb.qqxmpp.util.ToastUtil;
import com.zyxb.qqxmpp.view.LoadingDialog;
import com.zyxb.qqxmpp.view.TextURLView;

public class LoginActivity extends Activity {
	private Context mContext;
	private RelativeLayout rl_user;
	private Button mLogin;
	private Button register;
	private TextURLView mTextViewURL;

	private EditText account, password;
	private LoadingDialog loadDialog;
	// private boolean isConnectedToServer = true;
	private ConnectReceiver connectReceiver;
	private IntentFilter connectFilter;

	private String username;// 用户名
	private String pwd;// 密码
	private String userType;
	private String host;

	private App app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		app = (App) getApplication();
		mContext = this;
		loadDialog = new LoadingDialog(this);
		loadDialog.setCancelable(false);
		loadDialog.setTitle("正在登录...");

		findView();
		initTvUrl();
		init();
	}

	private void initReceiver() {
		connectReceiver = new ConnectReceiver();
		connectFilter = new IntentFilter();
		connectFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
		connectFilter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
		connectFilter.addAction(ChatService.LOGIN);
		registerReceiver(connectReceiver, connectFilter);
	}

	private void findView() {
		rl_user = (RelativeLayout) findViewById(R.id.rl_user);
		mLogin = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		mTextViewURL = (TextURLView) findViewById(R.id.tv_forget_password);

		account = (EditText) findViewById(R.id.account);
		password = (EditText) findViewById(R.id.password);

	}

	private void init() {
		Animation anim = AnimationUtils.loadAnimation(mContext,
				R.anim.login_anim);
		anim.setFillAfter(true);
		rl_user.startAnimation(anim);
		mLogin.setOnClickListener(loginOnClickListener);
		register.setOnClickListener(registerOnClickListener);

		// 开启连接,则关闭
		if (SharedPreferencesUtils.getString(mContext, Const.SP_USER_TYPE,
				Const.USER_TYPE_LOCAL).equals(Const.USER_TYPE_XMPP)) {
			Intent intent = new Intent(ConnectService.CONNECT_CLOSE);
			sendBroadcast(intent);
		}
	}

	private void initTvUrl() {
		mTextViewURL.setText(R.string.forget_password);
	}

	/**
	 * 登录
	 */
	private OnClickListener loginOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			doLogin();
		}
	};

	/**
	 * 注册
	 */
	private OnClickListener registerOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, RegisterActivity.class);
			startActivity(intent);
		}
	};

	private void doLogin() {

		username = account.getText().toString();
		pwd = password.getText().toString();
		if (TextUtils.isEmpty(username)) {
			ToastUtil.showShortToast(mContext, "请输入您的账号");
			return;
		}
		if (TextUtils.isEmpty(pwd)) {
			ToastUtil.showShortToast(mContext, "请输入您的密码");
			return;
		}
		loadDialog.show();

		// 检测账号类型 [xxx@xx.xx.xx为xmpp,xxx为local]
		String[] un = username.split("@");
		if (un.length == 1) {
			// 本地测试账号,直接登陆
			userType = Const.USER_TYPE_LOCAL;
			host = "";

			// 关闭连接
			Intent stopConn = new Intent(ConnectService.CONNECT_CLOSE);
			sendBroadcast(stopConn);
			app.setUserType(userType);

			localLogin("使用测试数据登陆");

			return;
		} else {
			userType = Const.USER_TYPE_XMPP;
			host = un[1];

			// 注册receiver
			if (connectReceiver == null) {
				initReceiver();
			}
		}

		app.setUserType(userType);

		// 如果网络未开启,查询本地数据
		if (!NetUtil.checkNet(this)) {
			localLogin("当前网络不可用");

		} else {
			// 连接xmpp服务器
			SharedPreferencesUtils.setString(mContext, Const.SP_USERNAME,
					username);
			SharedPreferencesUtils.setString(mContext, Const.SP_PWD, pwd);
			SharedPreferencesUtils.setString(mContext, Const.SP_USER_TYPE,
					userType);
			SharedPreferencesUtils.setString(mContext, Const.XMPP_HOST, host);

			// 服务器改变重新连接
			Intent serverChangeIntent = new Intent(
					ConnectService.SERVER_CHANGED);
			sendBroadcast(serverChangeIntent);

			// 使用本地数据库登陆
			localXMPPLogin();

		}

	}

	private void localXMPPLogin() {
		DataEngine engine = new DataEngine(this);
		DB3User user = engine.login(username, MD5Encoder.encode(pwd));
		if (user != null) {
			loadDialog.dismiss();
			// 设置user
			app.setUser(user);
			app.setConnected(false);

			// 进入主界面
			Intent intent = new Intent(mContext, MainActivity.class);
			startActivity(intent);

			this.finish();
		} else {
			DB3User u = engine.findXMPPUserByName(username);
			if (u != null) {
				Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
				loadDialog.dismiss();
				account.setText("");
				password.setText("");
				account.requestFocus();
			} else {
				// 等待服务器回应
			}
		}
	}

	private void localLogin(String msg) {
		DataEngine engine = new DataEngine(this);
		DB3User user = engine.login(username, MD5Encoder.encode(pwd));
		if (user != null) {
			loadDialog.dismiss();
			Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
			SharedPreferencesUtils.setString(mContext, Const.SP_USERNAME,
					username);
			SharedPreferencesUtils.setString(mContext, Const.SP_PWD, pwd);
			SharedPreferencesUtils.setString(mContext, Const.SP_USER_TYPE,
					userType);
			SharedPreferencesUtils.setString(mContext, Const.XMPP_HOST, host);

			// 设置user
			app.setUser(user);
			app.setConnected(false);

			// 进入主界面
			Intent intent = new Intent(mContext, MainActivity.class);
			startActivity(intent);

			this.finish();
		} else {

			Toast.makeText(this, "用户名或密码不正确,请重新输入", Toast.LENGTH_LONG).show();
			loadDialog.dismiss();
			account.setText("");
			password.setText("");
			account.requestFocus();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (connectReceiver != null) {
			unregisterReceiver(connectReceiver);
		}
	}

	private class ConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int reason = intent.getIntExtra("reason", -1);

			String action = intent.getAction();

			if (action.equals(ConnectService.LOGIN_SERVER_CONNECTED)) {
				// 登陆
				// chatService.login(username.split("@")[0], pwd, "qqxmpp");

				// return;
			} else if (action.equals(ConnectService.LOGIN_SERVER_RECONNECT)) {
				loadDialog.dismiss();
				// 服务器连接失败
				Toast.makeText(mContext, "服务器连接失败,请稍后重试", Toast.LENGTH_LONG)
						.show();
				loadDialog.dismiss();
				account.setText("");
				password.setText("");
				account.requestFocus();

				// 断开连接
				Intent closeConnIntent = new Intent(
						ConnectService.CONNECT_CLOSE);
				sendBroadcast(closeConnIntent);

				return;
			}

			if (reason == ChatService.SERVER_CONNECTED_USER_LOGIN) {
				loadDialog.dismiss();
				// isLogin = true;

				if (username == null || username.trim().equals("")
						|| pwd == null || pwd.trim().equals("")) {
					return;
				}
				// 设置user信息,进入main
				// 查找本地用户是否存在,不存在添加
				DataEngine engine = new DataEngine(LoginActivity.this);
				XMPPUser ur = new XMPPUser();
				ur.setJid(username);
				ur.setNickname(username.split("@")[0]);
				ur.setStatusMessage(pwd);
				DB3User u = engine.getXMPPUser(ur);
				app.setUser(u);
				app.setConnected(true);

				// 发送用户消息保存完成广播
				Intent userAddIntent = new Intent(
						ChatService.USER_LOCAL_ADD_COMPLETE);
				sendBroadcast(userAddIntent);

				// 进入主界面
				Intent main = new Intent(context, MainActivity.class);
				startActivity(main);
				LoginActivity.this.finish();

			} else if (reason == ChatService.SERVER_CONNECTED_USER_REJECTED) {
				loadDialog.dismiss();
				Toast.makeText(LoginActivity.this, "用户名或密码错误,请重新登录",
						Toast.LENGTH_SHORT).show();
				// isLogin = false;
				account.setText("");
				password.setText("");
				account.requestFocus();

			} else {
				loadDialog.dismiss();
				Toast.makeText(LoginActivity.this, "网络连接错误", Toast.LENGTH_SHORT)
						.show();

				account.setText("");
				password.setText("");
				account.requestFocus();
			}
		}

	}

}
