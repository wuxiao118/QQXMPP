package com.zyxb.qqxmpp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.ExAdapter;
import com.zyxb.qqxmpp.bean.FriendGroupInfo;
import com.zyxb.qqxmpp.bean.Information;
import com.zyxb.qqxmpp.db.dao.DBUserDAO.OnUserChangeListener;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.ui.FriendDtailActivity;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.view.XExpandableListView;
import com.zyxb.qqxmpp.view.XExpandableListView.IXExpandableListViewListener;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements OnItemClickListener,
		OnClickListener, IXExpandableListViewListener, OnGroupExpandListener,
		OnGroupCollapseListener, OnGroupClickListener, OnChildClickListener,
		OnUserChangeListener {
	private static final String TAG = "ContactFragment";
	private XExpandableListView lvContactList;
	private TextView tvContactAdd;

	private MainActivity mContext;
	// private DBUser mUser;

	private ExAdapter mAdapter;
	private List<FriendGroupInfo> groups;
	private DataEngine mEngine;

	// 记录展开个groupView
	// private View[] expandGroupViews;
	// private View currentExpandView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// TODO 检查登陆
		mContext = (MainActivity) getActivity();
		// mUser = mContext.getmUser();

		initData();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contact, null);
		lvContactList = (XExpandableListView) view
				.findViewById(R.id.elContactList);
		tvContactAdd = (TextView) view.findViewById(R.id.tvContactAdd);

		initEvent();

		return view;
	}

	private void initEvent() {
		lvContactList.setOnItemClickListener(this);
		tvContactAdd.setOnClickListener(this);
		lvContactList.setPullRefreshEnable(true);
		lvContactList.setPullLoadEnable(false);
		lvContactList.setExpandableListViewListener(this);
		lvContactList.setOnGroupExpandListener(this);
		lvContactList.setOnGroupCollapseListener(this);
		lvContactList.setOnGroupClickListener(this);
		lvContactList.setOnChildClickListener(this);
	}

	private void initData() {
		mContext = (MainActivity) getActivity();
		mEngine = mContext.getmEngine();
		groups = mEngine.getFriends(this);

		// groups中添加我的设备
		List<Information> friends = null;
		if (groups.size() == 0) {
			friends = new ArrayList<Information>();
			FriendGroupInfo info = new FriendGroupInfo();
			info.setFriends(friends);
			info.setName("我的设备");
			groups.add(info);
		} else {
			friends = groups.get(0).getFriends();
		}
		Information friend = new Information();
		friend.setName("我的电脑");
		friend.setState(Const.LOGIN_STATE_OFFLINE);
		friend.setChannel(Const.LOGIN_CHANNEL_UNKNOWN);
		friend.setRenew("无需数据线，手机轻松传文件到电脑。");
		friends.add(friend);
		friend = null;
		friend = new Information();
		friend.setName("所搜新设备");
		friend.setState(Const.LOGIN_STATE_ONLINE);
		friend.setChannel(Const.LOGIN_CHANNEL_UNKNOWN);
		friend.setRenew("搜索附近的设备，用QQ轻松连接设备。");
		friends.add(friend);
		friend = null;
		groups.get(0).setCount(1);

		mAdapter = new ExAdapter(mContext, groups);
		lvContactList.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		// mAdapter.setGroupClickPosition(position);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onRefresh() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				lvContactList.stopRefresh();
				Toast.makeText(mContext, "刷新完成", Toast.LENGTH_LONG).show();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onGroupExpand(int groupPosition) {
		RotateAnimation animation = new RotateAnimation(0.0f, 90.0f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setFillAfter(true);
	}

	@Override
	public void onGroupCollapse(int groupPosition) {
		RotateAnimation animation = new RotateAnimation(90.0f, 0.0f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setFillAfter(true);
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View groupView,
								int position, long id) {

		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View childView,
								int groupPosition, int childPosition, long id) {

		// TODO 传递数据

		if (groupPosition == 1) {
			// 我的电脑

			return true;
		}

		Intent intent = new Intent(mContext, FriendDtailActivity.class);
		String account = groups.get(groupPosition - 1).getFriends()
				.get(childPosition).getAccount();
		// System.out.println(TAG + ":account:" + account);
		Logger.d(TAG, account);
		intent.putExtra("account", account);
		startActivity(intent);
		mContext.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

		return true;
	}

	@Override
	public void onChanged() {
		// 数据库用户信息改变

	}

	@Override
	public void onError(int type) {
		// 数据库用户信息更改出错

	}
}
