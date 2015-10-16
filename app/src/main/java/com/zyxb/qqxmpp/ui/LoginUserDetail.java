package com.zyxb.qqxmpp.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;

public class LoginUserDetail extends BaseActivity implements OnClickListener {
	private TextView tvBack;
	private TextView tvName, tvAccount, tvGL, tvSpace, tvDaren;
	private LinearLayout llDaren, llEdit, llCard;
	private ImageView ivIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_user_detail);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvUserDetailBack);
		tvName = findView(R.id.tvUserName);
		tvAccount = findView(R.id.tvUserAccount);
		tvGL = findView(R.id.tvUserGL);
		tvSpace = findView(R.id.tvUserSpace);
		tvDaren = findView(R.id.tvUserDarenDays);

		llDaren = findView(R.id.llUserDaren);
		llEdit = findView(R.id.llUserEdit);
		llCard = findView(R.id.llUserCard);

		ivIcon = findView(R.id.ivUserIcon);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llEdit.setOnClickListener(this);
		llCard.setOnClickListener(this);
		ivIcon.setOnClickListener(this);
	}

	private void initData() {
		tvName.setText(mUser.getNickname());
		tvAccount.setText(mUser.getAccount());
		tvSpace.setText(mUser.getNickname() + "的空间");

		if (mUser.getExportDays() <= 0) {
			llDaren.setVisibility(View.GONE);
		} else {
			tvDaren.setText(mUser.getExportDays() + "天");
		}

		// 性别 年龄 位置
		StringBuilder sb = new StringBuilder();
		String gender = mUser.getGender();
		if (gender != null && gender.equalsIgnoreCase("M")) {
			sb.append("男 ");
		} else if (gender != null && gender.equalsIgnoreCase("F")) {
			sb.append("女 ");
		}
		gender = null;

		Integer ageInt = mUser.getAge();
		if (ageInt != null) {
			sb.append(ageInt.toString() + " ");
		}
		ageInt = null;

		String location = mUser.getLocation();
		if (location != null) {
			sb.append(location);
		}
		location = null;
		tvGL.setText(sb.toString());
		sb = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvUserDetailBack:
				mApp.back();
				break;
		}
	}
}
