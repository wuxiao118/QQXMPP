package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.util.Logger;

public class GroupFriendMoreActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "GroupFriendMoreActivity";
	private TextView tvBack;
	private String contactAccount;
	private String groupAccount;
	//private Contact mContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_friend_detail_more);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupFriendMoreBack);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
	}

	private void initData() {
		// 获取群account
		Intent intent = getIntent();
		contactAccount = intent.getStringExtra("account");
		groupAccount = intent.getStringExtra("groupAccount");
		Logger.d(TAG, "mUser account:" + contactAccount + ",group account:"
				+ groupAccount);

		//contact = mEngine.getGroupFriend(groupAccount, contactAccount);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvGroupFriendMoreBack:
				mApp.back();
				break;
		}
	}

}
