package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;

public class SettingsActivity extends BaseActivity implements OnClickListener {
	private TextView tvBack;
	private LinearLayout llAccountManager;
	private LinearLayout llSettingsDataManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvSettingsBack);
		llAccountManager = findView(R.id.llSettingsAccountManager);
		llSettingsDataManager = findView(R.id.llSettingsDataManager);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llAccountManager.setOnClickListener(this);
		llSettingsDataManager.setOnClickListener(this);
	}

	private void initData() {

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		
		switch (v.getId()) {
		case R.id.tvSettingsBack:
			mApp.back();
			break;
		case R.id.llSettingsAccountManager:
			intent = new Intent(this, AccountManagerActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
			break;
		case R.id.llSettingsDataManager:
			intent = new Intent(this, DataManagerActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
			break;
		}
	}

}
