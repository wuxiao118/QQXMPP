package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.Contact;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.UIAnimUtils;

public class FriendDtailActivity extends BaseActivity implements
		OnClickListener {
	public static final String TAG = "FriendDtailActivity";
	private TextView tvBack, tvMore;
	private TextView tvName, tvAccount, tvGL, tvNickname,
			tvPersonalitySignature, tvDaren;
	private ImageView ivIcon;
	private LinearLayout llPhone, llSendMsg, llDaren;

	// private DB3User contact;

	// 当前用户
	private String contactAccount;
	private Contact contact;

	private boolean isFromeChat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friend_detail);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvFriendDetailBack);
		tvMore = findView(R.id.tvFriendMore);
		tvName = findView(R.id.tvFriendName);
		tvAccount = findView(R.id.tvFriendAccount);
		tvGL = findView(R.id.tvFriendGL);
		tvNickname = findView(R.id.tvFriendNickname);
		tvPersonalitySignature = findView(R.id.tvFriendPersonalitySignature);
		tvDaren = findView(R.id.tvFriendDetailDarenDays);

		ivIcon = findView(R.id.ivFriendIcon);
		llPhone = findView(R.id.llFriendPhone);
		llSendMsg = findView(R.id.llFriendSendMsg);
		llDaren = findView(R.id.llFriendDetailDaren);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		tvMore.setOnClickListener(this);
		llPhone.setOnClickListener(this);
		llSendMsg.setOnClickListener(this);
	}

	private void initData() {
		// 获取好友account
		Intent intent = getIntent();
		contactAccount = intent.getStringExtra("account");
		// System.out.println(TAG + ":" + contactAccount);
		isFromeChat = intent.getBooleanExtra("isFromChat", false);
		Logger.d(TAG, contactAccount);
		// contact = engine.getUserInfo(contactAccount);
		contact = engine.getUserFriend(contactAccount);

		// 设置数据
		// 名字
		String name = contact.getRemark();
		if (name == null) {
			name = contact.getNickname();
		}
		tvName.setText(name);

		// 图标
		String icon = contact.getIcon();
		if (icon != null) {
			// 加载icon
			Bitmap bmp = BitmapFactory.decodeFile(icon);
			ivIcon.setImageBitmap(bmp);
		}

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

		// account
		tvAccount.setText(contact.getAccount());

		// 昵称
		tvNickname.setText(contact.getNickname());

		// 个性签名
		String ps = contact.getPersonalitySignature();
		if (ps == null) {
			ps = "这个家伙什么也没留下";
		}
		tvPersonalitySignature.setText(ps);

		// 达人
		Integer days = contact.getExportDays();
		if (days == null) {
			llDaren.setVisibility(View.GONE);
		} else {
			tvDaren.setText(days + "天");
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.tvFriendDetailBack:
				app.back();
				break;
			case R.id.tvFriendMore:
				intent = new Intent(this, FriendMoreActivity.class);
				intent.putExtra("account", contactAccount);
				startActivity(intent);
				UIAnimUtils.sildLeftIn(this);
				break;
			case R.id.llFriendSendMsg:
				// 聊天界面
				if (isFromeChat) {
					finish();
					UIAnimUtils.sildRightOut(this);
				} else {
					intent = new Intent(this, ChatActivity.class);
					intent.putExtra("type", DB3Columns.MESSAGE_TYPE_CONTACT);
					intent.putExtra("fromAccount", user.getAccount());
					intent.putExtra("toAccount", contactAccount);
					startActivity(intent);
					UIAnimUtils.sildLeftIn(this);
				}
				break;
			case R.id.llFriendPhone:
				// QQ电话
				break;
		}
	}
}
