package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.Contact;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO.OnMessageChangeListener;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.UIAnimUtils;

public class GroupFriendChatActivity extends BaseActivity implements
		OnClickListener, OnMessageChangeListener {
	private static final String TAG = "GroupFriendChatActivity";
	private TextView tvBack, tvTitleFrom, tvTitleName;
	private LinearLayout llAdd;
	private ImageView ivInfo;

	// 非好友
	private Contact groupContact;
	private String groupContactAccount;
	private String groupAccount;

	// private List<MessageInfo> messages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.msg_chat);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvMsgTitleLeft);
		tvTitleFrom = findView(R.id.tvMsgTitleFrom);
		llAdd = findView(R.id.llMsgAddOrDelete);
		ivInfo = findView(R.id.ivMsgTitleRight);
		tvTitleName = findView(R.id.tvMsgTitleName);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		ivInfo.setOnClickListener(this);
	}

	private void initData() {
		tvTitleFrom.setVisibility(View.VISIBLE);
		llAdd.setVisibility(View.VISIBLE);

		Intent intent = getIntent();
		groupContactAccount = intent.getStringExtra("account");
		groupAccount = intent.getStringExtra("groupAccount");

		groupContact = engine.getGroupFriend(groupAccount, groupContactAccount);
		Logger.d(TAG, "contact:" + groupContactAccount + ",group account:"
				+ groupAccount);

		// 设置数据
		String remark = groupContact.getRemark();
		if (remark == null) {
			remark = groupContact.getNickname();
		}
		tvTitleName.setText(remark);
		tvTitleFrom.setText("来自\"" + groupContact.getGroupName() + "\"群");

		// 查询消息 user和groupContactAccount消息
		// messages = engine.getGroupMessages(groupAccount, this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvMsgTitleLeft:
				app.back();
				break;
			case R.id.ivMsgTitleRight:
				Intent intent = new Intent(this,
						GroupFriendChatSettingActivity.class);
				intent.putExtra("groupAccount", groupAccount);
				intent.putExtra("account", groupContactAccount);
				startActivity(intent);
				UIAnimUtils.sildLeftIn(this);
				break;
		}
	}

	@Override
	public void onPreMessageChange(int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageChanged(int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int type) {
		// TODO Auto-generated method stub

	}

}
