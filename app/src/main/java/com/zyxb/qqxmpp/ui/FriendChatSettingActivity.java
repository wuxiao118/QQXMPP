package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.Contact;

public class FriendChatSettingActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvBack;
	private TextView tvRemark, tvAccount;
	private Button btDelete;
	private ImageView ivIcon;

	// 数据
	private Contact contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friend_chat_setting);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvFriendChatSettingBack);
		tvRemark = findView(R.id.tvFriendChatSettingRemark);
		tvAccount = findView(R.id.tvFriendChatSettingAccount);
		btDelete = findView(R.id.btFriendChatSettingDelete);
		ivIcon = findView(R.id.ivFriendChatSettingIcon);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		btDelete.setOnClickListener(this);
		ivIcon.setOnClickListener(this);
	}

	private void initData() {
		Intent intent = getIntent();
		String contactAccount = intent.getStringExtra("account");
		contact = engine.getUserFriend(contactAccount);

		// 设置数据
		String remark = contact.getRemark();
		if (remark == null) {
			remark = contact.getNickname();
		}
		tvRemark.setText(remark);
		tvAccount.setText(contact.getAccount());
		String icon = contact.getIcon();
		if (icon != null) {
			// TODO 加载图片

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvFriendChatSettingBack:
				app.back();
				break;
			case R.id.ivFriendChatSettingIcon:
				// TODO 删除好友

				break;
		}
	}

}
