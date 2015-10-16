package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.Contact;

public class FriendMoreActivity extends BaseActivity implements OnClickListener {
	private TextView tvBack;
	private TextView tvRemark, tvGroupName;
	private Button btDelete;

	//信息
	private Contact mContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friend_detail_more);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvFriendMoreBack);
		tvRemark = findView(R.id.tvFriendMoreRemark);
		tvGroupName = findView(R.id.tvFriendMoreGroupName);
		btDelete = findView(R.id.btFriendMoreDelete);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		btDelete.setOnClickListener(this);
	}

	private void initData() {
		Intent intent = getIntent();
		String contactAccount = intent.getStringExtra("account");
		mContact = mEngine.getUserFriend(contactAccount);

		//设置数据
		String remark = mContact.getRemark();
		if(remark == null){
			remark = mContact.getNickname();
		}
		tvRemark.setText(remark);
		tvGroupName.setText(mContact.getGroupName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvFriendMoreBack:
				mApp.back();
				break;
			case R.id.btFriendMoreDelete:
				// TODO 删除好友

				break;
		}
	}

}
