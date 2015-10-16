package com.zyxb.qqxmpp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.MainActivity.MainDrawerListener;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.MsgAdapter;
import com.zyxb.qqxmpp.adapter.MsgPhoneAdapter;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.bean.MsgPhoneInfo;
import com.zyxb.qqxmpp.db.dao.DBMessageDAO.OnMessageChangeListener;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.ui.ChatActivity;
import com.zyxb.qqxmpp.util.AppShortCutUtil;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.CustomListView;
import com.zyxb.qqxmpp.view.CustomListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MassegeFragment extends Fragment implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, OnRefreshListener,
		MainDrawerListener, OnMessageChangeListener {
	private MainActivity context;
	// private App mApp;
	private ImageView ivMenuLeft;
	private ImageView ivMenuRight;
	private ImageView ivMessage;
	private ImageView ivPhone;
	private FrameLayout flMsg;
	private FrameLayout flPhone;
	private CustomListView dlMsgList;
	private CustomListView dlPhone;
	private TextView tvMessage;
	private TextView tvPhone;

	// private DBUser mUser;
	private DataEngine mEngine;
	private MsgAdapter mAdapter;
	private List<MessageInfo> messages;

	private static final int STATE_MESSAGE = 0;
	private static final int STATE_PHONE = 1;
	private int state = STATE_MESSAGE;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		context = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initData();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.msg, null);
		ivMenuLeft = findView(view, R.id.ivMenuLeft);
		ivMenuRight = findView(view, R.id.ivMenuRight);
		flMsg = findView(view, R.id.flMsg);
		flPhone = findView(view, R.id.flPhone);
		dlMsgList = findView(view, R.id.dlList);
		dlPhone = findView(view, R.id.dlPhone);
		ivMessage = findView(view, R.id.ivMessage);
		ivPhone = findView(view, R.id.ivPhone);
		tvMessage = findView(view, R.id.tvMessage);
		tvPhone = findView(view, R.id.tvPhone);

		ivMenuLeft.setOnClickListener(this);
		ivMenuRight.setOnClickListener(this);
		flMsg.setOnClickListener(this);
		flPhone.setOnClickListener(this);
		ivMessage.setOnClickListener(this);
		ivPhone.setOnClickListener(this);

		dlMsgList.setOnItemClickListener(this);
		dlMsgList.setOnItemLongClickListener(this);
		dlMsgList.setOnRefreshListener(this);// 设置listview下拉刷新监听
		dlMsgList.setCanLoadMore(false);// 设置禁止加载更多

		dlPhone.setOnItemClickListener(this);
		dlPhone.setOnItemLongClickListener(this);
		dlPhone.setOnRefreshListener(this);// 设置listview下拉刷新监听
		dlPhone.setCanLoadMore(false);// 设置禁止加载更多

		// 监听DrawerLayout
		context.setOnMainDrawerListener(this);

		return view;
	}

	private void initData() {

		// msg_phone数据,仅供测试
		List<MsgPhoneInfo> phoneInfos = new ArrayList<MsgPhoneInfo>();
		MsgPhoneInfo info = new MsgPhoneInfo();
		info.setTitle("杨力");
		info.setContent("[视屏] 2015-5-15");
		info.setRightIcon("chat_add_camera_normal");
		phoneInfos.add(info);
		info = null;
		info = new MsgPhoneInfo();
		info.setTitle("刘星");
		info.setContent("2015-6-15");
		info.setRightIcon("qq_conversation_icon_calllist_empty");
		phoneInfos.add(info);

		MsgPhoneAdapter adapter = new MsgPhoneAdapter(context, phoneInfos);
		dlPhone.setAdapter(adapter);

		// 获取messages
		// mApp = context.getmApp();
		// mUser = mApp.getmUser();
		mEngine = context.getmEngine();
		messages = mEngine.getNewest(this);
		this.mAdapter = new MsgAdapter(getActivity(), messages);
		dlMsgList.setAdapter(this.mAdapter);
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T findView(View v, int id) {
		return (T) v.findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ivMenuLeft:
				context.OpenLeftMenu(v);
				break;
			case R.id.ivMenuRight:
				context.OpenRightMenu(v);
				break;
			case R.id.flMsg:

				break;
			case R.id.flPhone:

				break;
			case R.id.ivMessage:
				if (state == STATE_MESSAGE) {
					return;
				}
				state = STATE_MESSAGE;

				stateMessage();

				break;
			case R.id.ivPhone:
				if (state == STATE_PHONE) {
					return;
				}
				state = STATE_PHONE;

				statePhone();

				break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (state == STATE_PHONE) {
			statePhone();
		} else {
			stateMessage();
		}
	}

	private void stateMessage() {
		dlMsgList.setVisibility(View.VISIBLE);
		dlPhone.setVisibility(View.GONE);
		ivMessage.setBackgroundResource(R.drawable.msg_title_bg_normal_left);
		ivPhone.setBackgroundResource(R.drawable.msg_title_bg_pressed_right);
		tvMessage.setTextColor(getResources().getColor(R.color.bg_blue));
		tvPhone.setTextColor(getResources().getColor(
				R.color.msg_title_text_color));
	}

	private void statePhone() {
		dlMsgList.setVisibility(View.GONE);
		dlPhone.setVisibility(View.VISIBLE);
		ivMessage.setBackgroundResource(R.drawable.msg_title_bg_pressed_left);
		ivPhone.setBackgroundResource(R.drawable.msg_title_bg_normal_right);
		tvMessage.setTextColor(getResources().getColor(
				R.color.msg_title_text_color));
		tvPhone.setTextColor(getResources().getColor(R.color.bg_blue));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		// 单击listview条目
		switch (parent.getId()) {// view代表当前条目,parent才表示listview本身
			case R.id.dlList:

				// 0为显示网络是否连接
				//TODO 点击搜索
				if(position == 1){
					return;
				}

				// 更新未读消息 (0的位置显示是否连接网络，1为搜索，要-2)
				MessageInfo message = messages.get(position - 2);
				int unReadCount = message.getCount();
				if (unReadCount > 0) {
					context.updateUnReadMessageCount(context
							.getUnReadMessageCount() - unReadCount);
					message.setCount(0);
					mAdapter.notifyDataSetChanged();

					// 更新数据库
					mEngine.setReadedMessage(message.getFrom().getAccount(), message
							.getTo().getAccount(), message.getType());

					// 更新launch未读数量
					// AppShortCutUtil.installRawShortCut(context, null, true,
					// (context.getUnReadMessageCount() - unReadCount) + "",
					// true);
					AppShortCutUtil.addNumShortCut(context, null, true,
							(context.getUnReadMessageCount()) + "", true);

				}

				// 聊天详情列表
				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra("type", message.getType());
				// String name = null;
				// switch(message.getType()){
				// case DBColumns.MESSAGE_TYPE_CONTACT:
				// if(message.getFrom().getAccount().equals(mUser.getAccount())){
				// name = message.getTo().getComments();
				// }else{
				// name = message.getFrom().getComments();
				// }
				// break;
				// case DBColumns.MESSAGE_TYPE_GROUP:
				// name = message.getTo().getName();
				// break;
				// case DBColumns.MESSAGE_TYPE_SYS:
				// name = message.getFrom().getName();
				// break;
				// }
				intent.putExtra("fromAccount", message.getFrom().getAccount());
				intent.putExtra("toAccount", message.getTo().getAccount());

				startActivity(intent);
				// context.overridePendingTransition(android.R.anim.slide_in_left,
				// android.R.anim.slide_out_right);
				UIAnimUtils.sildLeftIn(context);
				break;
			case R.id.dlPhone:

				break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
								   int position, long id) {
		// 长按listview条目
		switch (parent.getId()) {
			case R.id.dlList:

				break;
			case R.id.dlPhone:

				break;
		}

		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		initListData();
	}

	private void initListData() {
		messages = mEngine.getNewest(this);

		int unReadMessageCount = 0;
		for (MessageInfo m : messages) {
			unReadMessageCount += m.getCount();
		}
		context.updateUnReadMessageCount(unReadMessageCount);

		mAdapter.setMessages(messages);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (state == STATE_MESSAGE) {
					dlMsgList.onRefreshComplete();
				} else if (state == STATE_PHONE) {
					dlPhone.onRefreshComplete();
				}

				Toast.makeText(context, "刷新完成", Toast.LENGTH_LONG).show();
			}
		}, 1000);
	}

	@Override
	public void open(int drawerGravity) {
		if (drawerGravity == Gravity.RIGHT) {
			ivMenuRight
					.setBackgroundResource(R.drawable.button_details_gohome_right);
		}

		//
		enabled(false);
	}

	@Override
	public void close() {
		ivMenuRight.setBackgroundResource(R.drawable.button_details_gohome);
		enabled(true);
	}

	private void enabled(boolean b) {

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
