package com.zyxb.qqxmpp.ui;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.R;

public class AccountManagerActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvBack, tvCurrentName, tvCurrentUserAccount;
	private ImageView ivCurrentUserIcon;
	private LinearLayout llExit;
	private PopupWindow mPopWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_account_manager);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvAccountManagerBack);
		tvCurrentName = findView(R.id.tvCurrentSelectedName);
		tvCurrentUserAccount = findView(R.id.tvCurrentSelectedAccount);

		ivCurrentUserIcon = findView(R.id.ivCurrentSelectedIcon);
		llExit = findView(R.id.llCurrentExit);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llExit.setOnClickListener(this);
	}

	private void initData() {
		DBUser user = mApp.getmUser();

		if (user.getIcon() != null) {
			// Bitmap bmp = BitmapFactory.decodeFile(mUser.getIcon());
			// ivCurrentUserIcon.setImageBitmap(bmp);
		} else {
			ivCurrentUserIcon
					.setBackgroundResource(R.drawable.login_default_avatar);
		}
		tvCurrentName.setText(user.getNickname());
		tvCurrentUserAccount.setText(user.getAccount() + "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvAccountManagerBack:
				mApp.back();
				break;
			case R.id.llCurrentExit:
				// userExit();
				showPopupWindow(v);
				break;
		}
	}

	@SuppressLint("InflateParams")
	private void showPopupWindow(View parent) {
		if (mPopWindow == null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.pop_loginout, null);
			mPopWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			initPop(view);
		}
		mPopWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		mPopWindow.setFocusable(true);
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public void initPop(View view) {
		TextView loginout = (TextView) view.findViewById(R.id.loginout);// 退出
		LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);// 取消
		loginout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopWindow.dismiss();
				userExit();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopWindow.dismiss();
			}
		});
	}
}
