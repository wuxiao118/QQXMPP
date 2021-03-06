package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.Contact;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.util.DateUtils;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.UIAnimUtils;

public class GroupFriendCardActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "GroupFriendCardActivity";
	private TextView tvBack;
	private LinearLayout llSend;
	private LinearLayout llFriend;

	private TextView tvAccount, tvInterTime, tvRemark, tvName, tvGroupTitle;
	private ImageView ivIcon;
	private LinearLayout llAdd, llPhone;

	// 数据
	private Contact mContact;
	private String groupAccount;
	private String contactAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_friend_info);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupFriendCardBack);
		llSend = findView(R.id.llGroupFriendCardSend);
		llFriend = findView(R.id.llGroupFriendCardFriend);

		tvName = findView(R.id.tvGroupFriendCardName);
		tvInterTime = findView(R.id.tvGroupFriendCardInterTime);
		tvAccount = findView(R.id.tvGroupFriendCardAccount);
		tvRemark = findView(R.id.tvGroupFriendCardRemark);
		tvGroupTitle = findView(R.id.tvGroupFriendCardGroupTitle);

		llAdd = findView(R.id.llGroupFriendCardAdd);
		llPhone = findView(R.id.llGroupFriendCardPhone);
		ivIcon = findView(R.id.ivGroupFriendCardIcon);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llSend.setOnClickListener(this);
		llFriend.setOnClickListener(this);
		llAdd.setOnClickListener(this);
		llPhone.setOnClickListener(this);
		ivIcon.setOnClickListener(this);
	}

	private void initData() {
		// 获取群account
		Intent intent = getIntent();
		contactAccount = intent.getStringExtra("account");
		groupAccount = intent.getStringExtra("groupAccount");
		// System.out.println("friendAccount:" + friendAccount);
		Logger.d(TAG, "mUser account:" + contactAccount + ",group account:"
				+ groupAccount);

		mContact = mEngine.getGroupFriend(groupAccount, contactAccount);

		// 设置数据
		String remark = mContact.getRemark();
		String time = DateUtils.format(mContact.getInterTime(), "yyyy-MM-dd")
				+ "入群";
		tvName.setText(mContact.getNickname());
		tvInterTime.setText(time);
		tvAccount.setText(mContact.getAccount());
		tvRemark.setText(remark == null ? "" : remark);
		tvGroupTitle.setText(DBColumns.GROUP_TITLES[mContact.getGroupTitle()]);
		String icon = mContact.getIcon();
		if (icon != null) {
			// TODO 加载图片
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.tvGroupFriendCardBack:
				mApp.back();
				break;
			case R.id.llGroupFriendCardSend:
				// intent = new Intent(this, GroupFriendChatActivity.class);
				// intent.putExtra("account",groupAccount);
				// startActivity(intent);
				boolean isMyFriend = mEngine.isMyFriend(contactAccount);
				if (isMyFriend) {
					// 是好友
					intent = new Intent(this, ChatActivity.class);
					intent.putExtra("type", DBColumns.MESSAGE_TYPE_CONTACT);
					intent.putExtra("fromAccount", mUser.getAccount());
					intent.putExtra("toAccount", contactAccount);
				} else {
					// 不是好友
					intent = new Intent(this, GroupFriendChatActivity.class);
					intent.putExtra("account", contactAccount);
					intent.putExtra("groupAccount", groupAccount);
				}
				startActivity(intent);
				UIAnimUtils.sildLeftIn(this);
				break;
			case R.id.llGroupFriendCardFriend:
				intent = new Intent(this, GroupFriendDetailActivity.class);
				intent.putExtra("groupAccount", groupAccount);
				intent.putExtra("account", contactAccount);
				startActivity(intent);
				break;
		}
	}
}
