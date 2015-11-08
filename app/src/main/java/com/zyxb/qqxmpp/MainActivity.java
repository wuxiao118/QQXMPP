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
import com.zyxb.qqxmpp.bean.po.DBUser;
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
 * 待研究问题:大量使用braodcast是否对性能影响大?使用接口回调如何?
 *
 * 下一步(xmpp下):
 * 发送文件功能(发送图片,视频,文件) 发送文件基本OK(如何获取对方是否接受文件?显示效果调整,接收文件)
 * 添加好友  OK
 * 聊天室
 * 日志
 * 最新消息改为滑动删除/长按消息弹出popWindow(删除,复制...)
 * 删除多余的代码及注释
 * 抽取broadcast部分,形成抽象公共基类,  MainActivity怎么处理?
 * 改造connectservice,chatservice通用化(接口回调/broadcast receiver)?
 * 使用注解改造dao,通用化
 *
 * 最后尝试:
 * 实时视频
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
	protected App mApp;
	protected DBUser mUser;
	protected DataEngine mEngine;

	private boolean isExit = false;

	// 未读消息数目
	private int unReadMessageCount = 0;

	// 监听DrawerLayout打开关闭
	private MainDrawerListener mListener;

	// connect receiver处理连接,登陆消息
	private ConnectReceiver mConnectReceiver;
	// message receiver处理联系人,消息变化
	//private MessageReceiver mMessageReceiver;

	//处理联系人及消息变化
	private ContactMessageChangedReceiver mContactMessageChangedReceiver;
	//处理添加好友、分组
	//private ContactFriendGroupAndFriendReceiver mContactFriendGroupAndFriendReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mFragmentManager = getSupportFragmentManager();
		state = SharedPreferencesUtils.getInt(this, Const.SP_MAIN_FRAGMENT,
				Const.FRAGMENT_STATE_MESSAGE);
		mApp = (App) getApplication();
		mApp.add(this);
		isExit = false;
		mContext = this;

		if (checkUser()) {

			initView();
			initEvents();
			// 初始化newmessage数据,并保存到app,MessageFragment不必再查询
			//initUnReadMessage();
		}

		// 初始化 表情名称与图片文件名对应关系
		MyExpressionUtil.initFaceNames(this);
	}

	protected boolean checkUser() {
		mUser = mApp.getmUser();

		if (mUser == null) {

			// 进入登陆界面
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();

			return false;
		}

		mEngine = new DataEngine(this, mUser);

		return true;
	}

	public App getmApp() {
		return mApp;
	}

	private void initUnReadMessage() {
		updateUnReadMessageCount(mEngine.getUnReadedMessage(mUser.getAccount()));
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

	public DBUser getmUser() {

		return mApp.getmUser();
	}

	public DataEngine getmEngine() {
		return mEngine;
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

				if (isRightOpen && mListener != null) {
					mListener.open(Gravity.RIGHT);
				}

				if (isLeftOpen && mListener != null) {
					mListener.open(Gravity.LEFT);
				}
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);

				// mDrawerLayout.isDrawerVisible(Gravity.RIGHT);

				if (mListener != null) {
					mListener.close();
				}
			}
		});
	}

	public void setOnMainDrawerListener(MainDrawerListener listener) {
		this.mListener = listener;
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

		mApp.remove(this);

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
					mApp.setConnected(true);
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
//	private class MessageReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			Logger.d(TAG, "action:" + action);
//
//			if(action.equals(ChatService.USER_DATA_CHANGED)){
//
//			}else if(action.equals(ChatService.MESSAGE_DATA_CHANGED)){
//
//			}
//		}
//	}

	@Override
	protected void onResume() {
		super.onResume();

		initUnReadMessage();

		// 注册receiver
		mConnectReceiver = new ConnectReceiver();
		IntentFilter connFilter = new IntentFilter();
		connFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
		connFilter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
		connFilter.addAction(ConnectService.LOGIN_SERVER_RECONNECT);
		connFilter.addAction(ChatService.LOGIN);
		registerReceiver(mConnectReceiver, connFilter);

		//消息联系人变化
		mContactMessageChangedReceiver = new ContactMessageChangedReceiver();
		IntentFilter cmFilter = new IntentFilter();
		cmFilter.addAction(ChatService.MESSAGE_DATA_CHANGED);
		cmFilter.addAction(ChatService.USER_DATA_CHANGED);
		registerReceiver(mContactMessageChangedReceiver,cmFilter);

		//好友、好友分组
		//mContactFriendGroupAndFriendReceiver = new ContactFriendGroupAndFriendReceiver();
		//IntentFilter cfFilter = new IntentFilter();
		//cfFilter.addAction(ChatService.USER_CREATE_FRIEND_GROUP_SUCCESS);
		//cfFilter.addAction(ChatService.USER_CREATE_FRIEND_GROUP_FAILED);
		//registerReceiver(mContactMessageChangedReceiver,cfFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mConnectReceiver);
		unregisterReceiver(mContactMessageChangedReceiver);
		//unregisterReceiver(mContactFriendGroupAndFriendReceiver);
	}

	private class ContactMessageChangedReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int reason = intent.getIntExtra("reason",-1);
			Logger.d(TAG,"MainActivity:contact or message changed:action=" + action + ",reason=" + reason);

			switch(reason){
				case ChatService.USER_ADD:
					break;
				case ChatService.USER_DELETE:
					break;
				case ChatService.USER_UPDATE:
					//更新好友状态信息
					if(state == Const.FRAGMENT_STATE_CONTACT){
						mContactFragment.updateContact();
					}

					break;
				case ChatService.MESSAGE_ADD:
					//更新未读消息数
					initUnReadMessage();

					//如果在消息界面，更新消息
					if(state == Const.FRAGMENT_STATE_MESSAGE){
						mMessageFragment.updateNewMessage();
					}

					break;
				case ChatService.MESSAGE_DELETE:
					break;
				case ChatService.MESSAGE_UPDATE:
					break;
			}

//			if(action.equals(ChatService.MESSAGE_DATA_CHANGED)){
//				//消息改变
//				switch
//			}else{
//				//联系人改变
//
//			}
		}
	}

	/**
	 * 添加好友分组和好友
	 */
//	private class ContactFriendGroupAndFriendReceiver extends BroadcastReceiver{
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//
//			if(action.equals(ChatService.USER_CREATE_FRIEND_GROUP_SUCCESS)){
//				//分组添加成功
//			}else if(action.equals(ChatService.USER_CREATE_FRIEND_GROUP_FAILED)){
//				//分组添加失败
//			}
//		}
//	}

}
