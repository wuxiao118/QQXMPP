package com.zyxb.qqxmpp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.ChatAdapter;
import com.zyxb.qqxmpp.adapter.FaceVPAdapter;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBMessageDAO.OnMessageChangeListener;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.MyExpressionUtil;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.DropdownListView;
import com.zyxb.qqxmpp.view.DropdownListView.OnRefreshListenerHeader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity implements OnClickListener,
		OnRefreshListenerHeader, OnMessageChangeListener {
	private static final String TAG = "ChatActivity";
	// title
	private TextView tvTitleBack;
	private TextView tvTitleName;
	private ImageView ivTitlePhone;
	private ImageView ivTitleContact;

	// 消息列表
	private DropdownListView dlMsgList;
	private ChatAdapter mChatAdapter;

	// 底部
	// private LinearLayout llBottom;
	private ImageView ivFace;
	private ImageView ivAdd;
	private EditText etInput;
	private TextView tvSend;

	// 弹出
	private LinearLayout llFaceContainer;
	private LinearLayout llAddContainer;
	private ViewPager vpFace;
	private LinearLayout llDot;
	private TextView tvAddPic;
	private TextView tvAddCamera;
	private TextView tvAddLoc;

	// 底部face/add是否显示
	private boolean isShowFace = false;
	private boolean isShowAdd = false;

	// 表情图标每页6列4行
	private int columns = 6;
	private int rows = 4;
	// 每页显示的表情view
	private List<View> views = new ArrayList<View>();
	// 表情列表
	private List<String> staticFacesList;

	private LayoutInflater mInflater;

	// 消息
	private List<MessageInfo> messages;
	private DBUser user;
	private int messageType;
	private String account;
	private String toJid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.msg_chat);

		tvTitleBack = findView(R.id.tvMsgTitleLeft);
		tvTitleName = findView(R.id.tvMsgTitleName);
		ivTitlePhone = findView(R.id.ivMsgTitleSecond);
		ivTitleContact = findView(R.id.ivMsgTitleRight);

		dlMsgList = findView(R.id.dlMsgList);
		// llBottom = findView(R.id.llMsgBottom);

		ivFace = findView(R.id.ivMsgChatFace);
		ivAdd = findView(R.id.ivMsgChatAdd);
		etInput = findView(R.id.etMsgInput);
		tvSend = findView(R.id.tvMsgSend);

		llFaceContainer = findView(R.id.llMsgFaceContainer);
		llAddContainer = findView(R.id.llMsgAddContainer);
		vpFace = findView(R.id.vpMsgFaceContainer);
		llDot = findView(R.id.llMsgFaceDotContainer);
		tvAddPic = findView(R.id.tvMsgAddPic);
		tvAddCamera = findView(R.id.tvMsgAddCamera);
		tvAddLoc = findView(R.id.tvMsgAddLoc);

		initUI();
		initData();
		initEvent();
	}

	private void initUI() {
		isShowFace = false;
		isShowAdd = false;
		hide();

		mInflater = LayoutInflater.from(this);
		staticFacesList = MyExpressionUtil.initStaticFaces(this);

		// 初始化表情
		initViewPager();
	}

	private void initData() {
		// 获取信息
		user = mApp.getmUser();

		Intent intent = getIntent();
		messageType = intent.getIntExtra("type", -1);
		String fromAccount = intent.getStringExtra("fromAccount");
		String toAccount = intent.getStringExtra("toAccount");

		// System.out.println(DBColumns.MESSAGE_TYPES[messageType] + ":" +
		// fromAccount + "--->" +toAccount);
		Logger.d(TAG, DBColumns.MESSAGE_TYPES[messageType] + ":" + fromAccount
				+ "--->" + toAccount + ",current user:" + user.getAccount());

		switch (messageType) {
			case DBColumns.MESSAGE_TYPE_CONTACT:
				String contactAccount = fromAccount;
				if (contactAccount.equals(user.getAccount())) {
					contactAccount = toAccount;
				}
				account = contactAccount;
				messages = mEngine.getContactMessages(contactAccount, this);
				if (messages.size() > 0) {
					MessageInfo info = messages.get(0);
					String remark = null;
					if (info.getFrom().getAccount().equals(user.getAccount())) {
						remark = info.getTo().getComments() == null ? info.getTo()
								.getName() : info.getTo().getComments();
						toJid = info.getTo().getName();
					} else {
						remark = info.getFrom().getComments() == null ? info
								.getFrom().getName() : info.getFrom().getComments();
						toJid = info.getFrom().getName();
					}
					tvTitleName.setText(remark);
				} else {
					// 获取用户信息
					tvTitleName.setText(mEngine.getRemark(user.getAccount(),
							contactAccount));
				}

				break;
			case DBColumns.MESSAGE_TYPE_GROUP:
				account = toAccount;
				messages = mEngine.getGroupMessages(toAccount, this);
				if (messages.size() > 0) {
					MessageInfo info = messages.get(0);
					tvTitleName.setText(info.getTo().getName());
				} else {
					DBGroup groupInfo = mEngine.getGroupInfo(toAccount);
					tvTitleName.setText(groupInfo.getName());
				}
				break;
			case DBColumns.MESSAGE_TYPE_SYS:
				account = fromAccount;
				messages = mEngine.getSystemMessages(fromAccount, this);
				if (messages.size() > 0) {
					MessageInfo info = messages.get(0);
					tvTitleName.setText(info.getFrom().getName());
				}
				break;
		}

		mChatAdapter = new ChatAdapter(this, user, messages);
		dlMsgList.setAdapter(mChatAdapter);
		// 滚动到最下方 android:transcriptMode="alwaysScroll"
		// android:stackFromBottom="true"
		dlMsgList.setSelection(messages.size() - 1);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initEvent() {
		tvTitleBack.setOnClickListener(this);
		ivTitlePhone.setOnClickListener(this);
		ivTitleContact.setOnClickListener(this);

		ivFace.setOnClickListener(this);
		ivAdd.setOnClickListener(this);
		tvSend.setOnClickListener(this);

		tvAddPic.setOnClickListener(this);
		tvAddCamera.setOnClickListener(this);
		tvAddLoc.setOnClickListener(this);

		etInput.setOnClickListener(this);

		// 初始化消息列表
		dlMsgList.setOnRefreshListenerHead(this);
		dlMsgList.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					hideSoftInputView();
					if (isShowAdd || isShowFace) {
						hide();
					}
				}

				return false;
			}
		});

		vpFace.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 改变下方点
				for (int i = 0; i < llDot.getChildCount(); i++) {
					llDot.getChildAt(i).setSelected(false);
				}
				llDot.getChildAt(position).setSelected(true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});

	}

	/**
	 * 初始化表情
	 */
	private void initViewPager() {
		int pagesize = MyExpressionUtil.getPagerCount(staticFacesList.size(),
				columns, rows);
		// 获取页数
		for (int i = 0; i < pagesize; i++) {
			views.add(MyExpressionUtil.viewPagerItem(this, i, staticFacesList,
					columns, rows, etInput));
			LayoutParams params = new LayoutParams(16, 16);
			llDot.addView(dotsItem(i), params);
		}
		FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
		vpFace.setAdapter(mVpAdapter);
		llDot.getChildAt(0).setSelected(true);
	}

	/**
	 * 表情页切换时，底部小圆点
	 *
	 * @param position
	 * @return
	 */
	@SuppressLint("InflateParams")
	private ImageView dotsItem(int position) {
		View layout = mInflater.inflate(R.layout.dot_image, null);
		ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
		iv.setId(position);
		return iv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvMsgTitleLeft:
				// back
				mApp.back();
				break;
			case R.id.ivMsgTitleSecond:
				// phone

				break;
			case R.id.ivMsgTitleRight:
				// 个人/群/系统组 信息
				Intent intent = null;
				switch (messageType) {
					case DBColumns.MESSAGE_TYPE_CONTACT:
						intent = new Intent(this, FriendChatSettingActivity.class);
						// 传递好友ID
						break;
					case DBColumns.MESSAGE_TYPE_GROUP:
						intent = new Intent(this, GroupDetailActivity.class);
						// 传递群ID
						break;
					case DBColumns.MESSAGE_TYPE_SYS:

						return;
					// break;
				}
				intent.putExtra("account", account);
				startActivity(intent);
				UIAnimUtils.sildLeftIn(this);

				break;
			case R.id.ivMsgChatFace:
				// 表情
				if (isShowFace) {
					hide();
				} else {
					show(llFaceContainer);
					isShowFace = true;
				}
				break;
			case R.id.etMsgInput:
				hide();
				break;
			case R.id.ivMsgChatAdd:
				// 添加照片位置等
				if (isShowAdd) {
					hide();
				} else {
					show(llAddContainer);
					isShowAdd = true;
				}
				break;
			case R.id.tvMsgSend:
				// 发送按钮
				String text = etInput.getText().toString();
				if (text.trim().equals("")) {
					Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
					Animation anim = AnimationUtils.loadAnimation(this,
							R.anim.shake);
					etInput.startAnimation(anim);
				} else {
					// 添加数据并设置状态
					XMPPMessage message = new XMPPMessage();
					long time = new Date().getTime();
					message.setCreateTime(time);
					message.setMsg(text);
					message.setFrom(user.getAccount());
					message.setTo(account);
					message.setMsgType(messageType);
					message.setState(DBColumns.MESSAGE_STATE_SENDING);
					// 数据库中添加数据 ,先由engine写入数据库
					// 连接服务器service完成后由service负责写入数据
					mEngine.addMessage(message);
					message = null;

					// 更新数据,service完成后,由service广播,在receiver中更新
					switch (messageType) {
						case DBColumns.MESSAGE_TYPE_CONTACT:
							messages = mEngine.getContactMessages(account, this);
							break;
						case DBColumns.MESSAGE_TYPE_GROUP:
							messages = mEngine.getGroupMessages(account, this);
							break;
						case DBColumns.MESSAGE_TYPE_SYS:
							messages = mEngine.getSystemMessages(account, this);
							break;
					}
					mChatAdapter.setMessages(messages);
					mChatAdapter.notifyDataSetChanged();

					etInput.setText("");

					//滚动到最下方
					dlMsgList.setSelection(messages.size() - 1);

					// 关闭键盘

					// 如果网络连通,并登陆,通过smack发送
					if (mApp.isConnected()) {

					}
				}

				break;
			case R.id.tvMsgAddPic:
				// 添加图片

				break;
			case R.id.tvMsgAddCamera:
				// 从相机添加图片

				break;
			case R.id.tvMsgAddLoc:
				// 发送位置

				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 让输入框获取焦点
				etInput.requestFocus();
			}
		}, 100);
	}

	/**
	 * 监听返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			hideSoftInputView();
			if (llFaceContainer.getVisibility() == View.VISIBLE) {
				llFaceContainer.setVisibility(View.GONE);
			} else if (llAddContainer.getVisibility() == View.VISIBLE) {
				llAddContainer.setVisibility(View.GONE);
			} else {
				finish();
				UIAnimUtils.sildRightOut(this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void show(LinearLayout ll) {
		// 如果键盘弹出状态,关闭键盘
		hideSoftInputView();

		hide();
		ll.setVisibility(View.VISIBLE);
	}

	private void hide() {
		llFaceContainer.setVisibility(View.GONE);
		llAddContainer.setVisibility(View.GONE);
		isShowAdd = false;
		isShowFace = false;
	}

	@Override
	public void onRefresh() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				dlMsgList.onRefreshCompleteHeader();
				Toast.makeText(mContext, "刷新完成", Toast.LENGTH_LONG).show();
			}

		}, 1000);
	}

	@Override
	public void onPreMessageChange(int type) {

	}

	@Override
	public void onMessageChanged(int type) {

	}

	@Override
	public void onError(int type) {

	}

}
