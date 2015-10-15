package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.Contact;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.CircleImageView2;

public class GroupFriendDetailActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "GroupFriendDetailActivity";
	private TextView tvBack, tvMore;
	private TextView tvRemark, tvGL, tvAccount;
	private CircleImageView2 ivIcon;

	private LinearLayout llSend, llPhone, llAdd;

	private String contactAccount;
	private String groupAccount;
	private Contact contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_friend_detail);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupFriendDetailBack);
		tvMore = findView(R.id.tvGroupFriendDetailMore);
		ivIcon = findView(R.id.ivFriendIcon);

		tvRemark = findView(R.id.tvFriendName);
		tvGL = findView(R.id.tvFriendGL);
		tvAccount = findView(R.id.tvFriendAccount);

		llSend = findView(R.id.llFriendAdd);
		llPhone = findView(R.id.llFriendPhone);
		llAdd = findView(R.id.llFriendSendMsg);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		tvMore.setOnClickListener(this);
		ivIcon.setOnClickListener(this);

		llSend.setOnClickListener(this);
		llPhone.setOnClickListener(this);
		llAdd.setOnClickListener(this);
	}

	private void initData() {
		// 获取群account
		Intent intent = getIntent();
		contactAccount = intent.getStringExtra("account");
		groupAccount = intent.getStringExtra("groupAccount");
		Logger.d(TAG, "user account:" + contactAccount + ",group account:"
				+ groupAccount);

		contact = engine.getGroupFriend(groupAccount, contactAccount);

		// 设置数据
		String remark = contact.getRemark();
		if (remark == null) {
			remark = contact.getNickname();
		}
		tvRemark.setText(remark);
		tvAccount.setText(contact.getAccount());

		// 性别 年龄 位置
		StringBuilder sb = new StringBuilder();
		String gender = contact.getGender();
		if (gender != null && gender.equalsIgnoreCase("M")) {
			sb.append("男 ");
		} else if (gender != null && gender.equalsIgnoreCase("F")) {
			sb.append("女 ");
		}
		gender = null;

		Integer ageInt = contact.getAge();
		if (ageInt != null) {
			sb.append(ageInt.toString() + " ");
		}
		ageInt = null;

		String location = contact.getLocation();
		if (location != null) {
			sb.append(location);
		}
		location = null;
		tvGL.setText(sb.toString());
		sb = null;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;

		switch (v.getId()) {
			case R.id.tvGroupFriendDetailBack:
				app.back();
				break;
			case R.id.tvGroupFriendDetailMore:
				intent = new Intent(this, GroupFriendMoreActivity.class);
				intent.putExtra("groupAccount", groupAccount);
				intent.putExtra("account", contactAccount);
				startActivity(intent);
				break;
			case R.id.llFriendSendMsg:
				boolean isMyFriend = engine.isMyFriend(contactAccount);
				if(isMyFriend){
					//是好友
					intent = new Intent(this,ChatActivity.class);
					intent.putExtra("type", DB3Columns.MESSAGE_TYPE_CONTACT);
					intent.putExtra("fromAccount", user.getAccount());
					intent.putExtra("toAccount", contactAccount);
				}else{
					//不是好友
					intent = new Intent(this, GroupFriendChatActivity.class);
					intent.putExtra("account", contactAccount);
					intent.putExtra("groupAccount", groupAccount);
				}
				startActivity(intent);
				UIAnimUtils.sildLeftIn(this);
				break;
		}
	}

}
