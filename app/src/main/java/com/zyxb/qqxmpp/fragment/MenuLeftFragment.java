package com.zyxb.qqxmpp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.ui.SettingsActivity;
import com.zyxb.qqxmpp.R;

public class MenuLeftFragment extends Fragment implements OnClickListener {
	private LinearLayout mSettings;
	private MainActivity mContext;
	private TextView tvName;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_layout_left, null);
		mSettings = (LinearLayout) view.findViewById(R.id.llSettings);
		tvName = (TextView) view.findViewById(R.id.tvLeftMenuName);

		mContext = (MainActivity) getActivity();
		//tvName.setText(mContext.getmApp().getmUser().getComments());

		initEvent();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		tvName.setText(mContext.getmApp().getmUser().getNickname());
	}

	private void initEvent() {
		mSettings.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llSettings:
				Intent intent = new Intent(mContext, SettingsActivity.class);
				startActivity(intent);
				mContext.overridePendingTransition(R.anim.slide_left,
						R.anim.slide_right);
				break;
		}
	}
}
