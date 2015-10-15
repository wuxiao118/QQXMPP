package com.zyxb.qqxmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.fragment.ContactFragment;
import com.zyxb.qqxmpp.fragment.MassegeFragment;
import com.zyxb.qqxmpp.fragment.NewsFragment;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.ui.LoginActivity;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.MyExpressionUtil;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;


/**
 *
 * 仿QQ5.5.1版本
 *
 * 后台使用openfire作为服务器
 *
 * @author 吴小雄
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "MainActivity";
	private Context mContext;

	private DrawerLayout mDrawerLayout;

	private ImageButton ibMsg;
	private ImageButton ibConstact;
	private ImageButton ibNews;
	private TextView tvNewMsgs;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mBeginTransaction;

	private MassegeFragment mMessageFragment;
	private NewsFragment mNewsFragment;
	private ContactFragment mContactFragment;

	private int state = Const.FRAGMENT_STATE_MESSAGE;
	protected App app;
	protected DB3User user;
	protected DataEngine engine;

	private boolean isExit = false;

	// 未读消息数目
	private int unReadMessageCount = 0;

	// 监听DrawerLayout打开关闭
	private MainDrawerListener listener;

	// connect receiver处理连接,登陆消息
	private ConnectReceiver connReceiver;
	// message receiver处理联系人,消息变化
	private MessageReceiver msgReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mFragmentManager = getSupportFragmentManager();
		state = SharedPreferencesUtils.getInt(this, Const.SP_MAIN_FRAGMENT,
				Const.FRAGMENT_STATE_MESSAGE);
		app = (App) getApplication();
		app.add(this);
		isExit = false;
		mContext = this;

		if (checkUser()) {

			initView();
			initEvents();
			// 初始化newmessage数据,并保存到app,MessageFragment不必再查询
			initUnReadMessage();
		}

		// 初始化 表情名称与图片文件名对应关系
		MyExpressionUtil.initFaceNames(this);
	}

	protected boolean checkUser() {
		user = app.getUser();

		if (user == null) {

			// 进入登陆界面
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();

			return false;
		}

		engine = new DataEngine(this, user);

		return true;
	}

	public App getApp() {
		return app;
	}

	private void initUnReadMessage() {
		updateUnReadMessageCount(engine.getUnReadedMessage(user.getAccount()));
	}

	public int getUnReadMessageCount() {
		return unReadMessageCount;
	}

	public void updateUnReadMessageCount(int num) {
		unReadMessageCount = num;
		tvNewMsgs.setText(num + "");
		if (num == 0) {
			tvNewMsgs.setVisibility(View.INVISIBLE);
		} else {
			tvNewMsgs.setVisibility(View.VISIBLE);
		}
	}

	public DB3User getUser() {

		return app.getUser();
	}

	public DataEngine getEngine() {
		return engine;
	}

	private void initView() {

		ibMsg = findView(R.id.buttom_msg);
		ibConstact = findView(R.id.buttom_constact);
		ibNews = findView(R.id.buttom_news);
		tvNewMsgs = findView(R.id.tv_newmsg);

		mDrawerLayout = findView(R.id.id_drawerLayout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.RIGHT);

		loadFragment();
	}

	private void loadFragment() {
		switch (state) {
			case Const.FRAGMENT_STATE_MESSAGE:
				mMessageFragment = new MassegeFragment();
				replaceFragment(mMessageFragment);
				break;
			case Const.FRAGMENT_STATE_CONTACT:
				mContactFragment = new ContactFragment();
				replaceFragment(mContactFragment);
				break;
			case Const.FRAGMENT_STATE_NEWS:
				mNewsFragment = new NewsFragment();
				replaceFragment(mNewsFragment);
				break;
		}

		changeBottomState();
	}

	private void replaceFragment(Fragment f) {
		mBeginTransaction = mFragmentManager.beginTransaction();
		mBeginTransaction.replace(R.id.fl_content, f);
		mBeginTransaction.commit();
	}

	private void initEvents() {
		ibMsg.setOnClickListener(this);
		ibConstact.setOnClickListener(this);
		ibNews.setOnClickListener(this);

		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = drawerView;
				float scale = 1 - slideOffset;
				float rightScale = 0.8f + scale * 0.2f;

				if (drawerView.getTag().equals("LEFT")) {

					float leftScale = 1 - 0.3f * scale;

					ViewHelper.setScaleX(mMenu, leftScale);
					ViewHelper.setScaleY(mMenu, leftScale);
					ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
					ViewHelper.setTranslationX(mContent,
							mMenu.getMeasuredWidth() * (1 - scale));
					ViewHelper.setPivotX(mContent, 0);
					ViewHelper.setPivotY(mContent,
							mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				} else {
					ViewHelper.setTranslationX(mContent,
							-mMenu.getMeasuredWidth() * slideOffset);
					ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
					ViewHelper.setPivotY(mContent,
							mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				}

				//
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO 判断是否右边打开,右则打开时设置右边打开时icon
				boolean isRightOpen = mDrawerLayout.isDrawerOpen(Gravity.RIGHT);
				boolean isLeftOpen = mDrawerLayout.isDrawerOpen(Gravity.LEFT);

				if (isRightOpen && listener != null) {
					listener.open(Gravity.RIGHT);
				}

				if (isLeftOpen && listener != null) {
					listener.open(Gravity.LEFT);
				}
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);

				// mDrawerLayout.isDrawerVisible(Gravity.RIGHT);

				if (listener != null) {
					listener.close();
				}
			}
		});
	}

	public void setOnMainDrawerListener(MainDrawerListener listener) {
		this.listener = listener;
	}

	public interface MainDrawerListener {
		void open(int drawerGravity);

		void close();
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}

	public void OpenRightMenu(View view) {
		mDrawerLayout.openDrawer(Gravity.RIGHT);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
				Gravity.RIGHT);
	}

	public void OpenLeftMenu(View view) {
		mDrawerLayout.openDrawer(Gravity.LEFT);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
				Gravity.LEFT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttom_msg:
				if (state == Const.FRAGMENT_STATE_MESSAGE) {
					return;
				}

				isExit = false;
				state = Const.FRAGMENT_STATE_MESSAGE;
				if (mMessageFragment == null)
					mMessageFragment = new MassegeFragment();
				replaceFragment(mMessageFragment);
				break;
			case R.id.buttom_constact:
				if (state == Const.FRAGMENT_STATE_CONTACT) {
					return;
				}

				isExit = false;
				state = Const.FRAGMENT_STATE_CONTACT;
				if (mContactFragment == null)
					mContactFragment = new ContactFragment();
				replaceFragment(mContactFragment);
				break;
			case R.id.buttom_news:
				if (state == Const.FRAGMENT_STATE_NEWS) {
					return;
				}

				isExit = false;
				state = Const.FRAGMENT_STATE_NEWS;
				if (mNewsFragment == null)
					mNewsFragment = new NewsFragment();
				replaceFragment(mNewsFragment);
				break;
		}

		changeBottomState();
	}

	private void changeBottomState() {
		ibMsg.setBackgroundResource(R.drawable.skin_tab_icon_conversation_normal);
		ibConstact
				.setBackgroundResource(R.drawable.skin_tab_icon_contact_normal);
		ibNews.setBackgroundResource(R.drawable.skin_tab_icon_plugin_normal);

		switch (state) {
			case Const.FRAGMENT_STATE_MESSAGE:
				ibMsg.setBackgroundResource(R.drawable.skin_tab_icon_conversation_selected);
				break;
			case Const.FRAGMENT_STATE_CONTACT:
				ibConstact
						.setBackgroundResource(R.drawable.skin_tab_icon_contact_selected);
				break;
			case Const.FRAGMENT_STATE_NEWS:
				ibNews.setBackgroundResource(R.drawable.skin_tab_icon_plugin_selected);
				break;
		}

		SharedPreferencesUtils.setInt(this, Const.SP_MAIN_FRAGMENT, state);
	}

	@Override
	protected void onStop() {
		super.onStop();
		isExit = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// SharedPreferencesUtils.setInt(this, Const.SP_MAIN_FRAGMENT, state);
		app.remove(this);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !isExit) {
			isExit = true;
			Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 连接状况receiver
	 *
	 * @author Administrator
	 *
	 */
	private class ConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int reason = intent.getIntExtra("reason", -1);
			Logger.d(TAG, "action:" + action + ",reason:" + reason);

			switch (reason) {
				case ConnectService.SERVER_CONNECTED:
					Toast.makeText(mContext, "服务器连接成功", Toast.LENGTH_SHORT).show();
					break;
				case ConnectService.SERVER_DISCONNECTED:
					Toast.makeText(mContext, "服务器连接断开", Toast.LENGTH_SHORT).show();
					break;
				case ChatService.SERVER_CONNECTED_USER_LOGIN:
					Toast.makeText(mContext, "登陆服务器成功", Toast.LENGTH_SHORT).show();
					break;
				case ChatService.SERVER_CONNECTED_USER_LOGOUT:
					Toast.makeText(mContext, "退出登录", Toast.LENGTH_SHORT).show();
					break;
				case ChatService.SERVER_CONNECTED_USER_REJECTED:
					Toast.makeText(mContext, "用户名或密码不正确", Toast.LENGTH_SHORT)
							.show();
					break;
			}
		}

	}

	/**
	 * 联系人,消息状况receiver
	 *
	 * @author Administrator
	 *
	 */
	private class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logger.d(TAG, "action:" + action);

			if(action.equals(ChatService.USER_DATA_CHANGED)){

			}else if(action.equals(ChatService.MESSAGE_DATA_CHANGED)){

			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 注册receiver
		connReceiver = new ConnectReceiver();
		IntentFilter connFilter = new IntentFilter();
		connFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
		connFilter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
		connFilter.addAction(ConnectService.LOGIN_SERVER_RECONNECT);
		registerReceiver(connReceiver, connFilter);

		msgReceiver = new MessageReceiver();
		IntentFilter msgFilter = new IntentFilter();
		msgFilter.addAction(ChatService.MESSAGE_DATA_CHANGED);
		msgFilter.addAction(ChatService.USER_DATA_CHANGED);
		registerReceiver(msgReceiver, msgFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(connReceiver);
		unregisterReceiver(msgReceiver);
	}

}
