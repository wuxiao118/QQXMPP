package com.zyxb.qqxmpp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zyxb.qqxmpp.R;

public class MenuRightFragment extends Fragment {
	@SuppressWarnings("unused")
	private LinearLayout llContainer;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_layout_right, null);
		container = (LinearLayout) view.findViewById(R.id.container);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		//LayoutParams params = (LayoutParams) llContainer.getLayoutParams();
		
	}
}
