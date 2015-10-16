package com.zyxb.qqxmpp.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.db.DBInit;
import com.zyxb.qqxmpp.R;

public class DataManagerActivity extends BaseActivity implements
		OnClickListener, OnDismissListener {
	private Context mContext;
	private TextView tvBack;
	private LinearLayout llCreate, llClear, llReCreate, llToJson;
	// private DB2DBHelper helper;
	private ProgressDialog pd;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					if (pd != null && pd.isShowing()) {
						pd.dismiss();
						// pd = null;
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.data_manager);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvSettingsBack);
		llCreate = findView(R.id.llSettingsDataManagerCreateData);
		llClear = findView(R.id.llSettingsDataManagerClearData);
		llReCreate = findView(R.id.llSettingsDataManagerReCreateData);
		llToJson = findView(R.id.llSettingsDataManagerSaveJSONData);
		pd = new ProgressDialog(this);
		pd.setOnDismissListener(this);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llCreate.setOnClickListener(this);
		llClear.setOnClickListener(this);
		llReCreate.setOnClickListener(this);
		llToJson.setOnClickListener(this);
	}

	private void initData() {
		mContext = this;
		// helper = new DB2DBHelper(this);
		pd.setTitle("重新加载数据");
		pd.setMessage("数据加载中,请稍后");
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvSettingsBack:
				mApp.back();
				break;
			case R.id.llSettingsDataManagerCreateData:
				// helper.create();
				Toast.makeText(this, "未开放该功能", Toast.LENGTH_SHORT).show();
				break;
			case R.id.llSettingsDataManagerClearData:
				// helper.clear();
				Toast.makeText(this, "未开放该功能", Toast.LENGTH_SHORT).show();
				break;
			case R.id.llSettingsDataManagerReCreateData:
				pd.show();
				new Thread() {
					public void run() {
						// helper.clear();
						// helper.create();
						DBInit.clear(mContext);
						DBInit.create(mContext);

						Message m = Message.obtain();
						m.what = 0;
						handler.sendMessage(m);

					};
				}.start();

				// pd.dismiss();
				break;
			case R.id.llSettingsDataManagerSaveJSONData:
				pd.setTitle("数据转换");
				pd.setMessage("数据转换JSON中...");
				pd.show();
				new Thread() {
					public void run() {

					};
				}.start();

				break;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// 数据重新加载完成后,重新加载数据

		// 清楚数据,重新登录
		mApp.setmUser(null);

		Intent intent = new Intent(this, SplashActivity.class);
		startActivity(intent);
		mApp.finish();
	}
}
