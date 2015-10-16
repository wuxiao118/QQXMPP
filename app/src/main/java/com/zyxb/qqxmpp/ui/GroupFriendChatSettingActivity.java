package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.Contact;
import com.zyxb.qqxmpp.util.UIAnimUtils;

public class GroupFriendChatSettingActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvBack;
	private TextView tvName,tvAccount;
	private ImageView ivIcon;
	private Button btAdd;
	private LinearLayout llFriend;

	private String groupContactAccount;
	private String groupAccount;
	private Contact mGroupContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_friend_chat_setting);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupFriendChatSettingBack);
		llFriend = findView(R.id.llGroupFriendChatSettingFriend);
		tvName = findView(R.id.tvCurrentSelectedName);
		tvAccount = findView(R.id.tvCurrentSelectedAccount);
		ivIcon = findView(R.id.ivCurrentSelectedIcon);

		btAdd = findView(R.id.btGroupFriendAdd);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llFriend.setOnClickListener(this);
		btAdd.setOnClickListener(this);
		ivIcon.setOnClickListener(this);
	}

	private void initData() {
		Intent intent = getIntent();
		groupContactAccount = intent.getStringExtra("account");
		groupAccount = intent.getStringExtra("groupAccount");

		mGroupContact = mEngine.getGroupFriend(groupAccount, groupContactAccount);

		//设置数据
		String remark = mGroupContact.getRemark();
		if(remark == null){
			remark = mGroupContact.getNickname();
		}
		tvName.setText(remark);
		tvAccount.setText(mGroupContact.getAccount());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvGroupFriendChatSettingBack:
				mApp.back();
				break;
			case R.id.llGroupFriendChatSettingFriend:
				Intent intent = new Intent(this,GroupFriendCardActivity.class);
				intent.putExtra("groupAccount", groupAccount);
				intent.putExtra("account", groupContactAccount);
				startActivity(intent);
				UIAnimUtils.sildLeftIn(this);
				break;
		}
	}

}
