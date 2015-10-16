package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.FriendGroupInfo;
import com.zyxb.qqxmpp.bean.Information;
import com.zyxb.qqxmpp.ui.GroupActivity;
import com.zyxb.qqxmpp.util.Const;

import java.util.List;

public class ExAdapter extends BaseExpandableListAdapter implements
		OnClickListener {
	private Activity mContext;
	private LayoutInflater mInflater;
	private List<FriendGroupInfo> groups;

	public void setGroupClickPosition(int position, int state) {
	}

	public ExAdapter(Context context, List<FriendGroupInfo> groups) {
		this.mContext = (Activity) context;
		mInflater = LayoutInflater.from(context);
		this.groups = groups;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (groupPosition == 0) {
			return null;
		}

		return groups.get(groupPosition - 1).getFriends().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {

		ChildHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.child, null);
			holder = new ChildHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.item_name);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.item_content);
			holder.tvState = (TextView) convertView
					.findViewById(R.id.item_state);
			holder.ivState = (ImageView) convertView
					.findViewById(R.id.item_state2);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.item_icon);

			convertView.setTag(holder);
		}
		holder = (ChildHolder) convertView.getTag();

		Information f = groups.get(groupPosition - 1).getFriends()
				.get(childPosition);
		String remark = f.getComments();
		if (remark == null) {
			remark = f.getName();
		}
		// holder.tvName.setText(f.getName());
		holder.tvName.setText(remark);
		String renew = f.getRenew();
		if(renew == null){
			renew ="";
		}
		holder.tvContent.setText("[" + Const.LOGIN_STATES[f.getState()] + "]"
				+ renew);

		int channel = f.getChannel();
		if (channel == Const.LOGIN_CHANNEL_UNKNOWN) {
			holder.tvState.setText("");
			holder.ivState.setVisibility(View.GONE);
		} else if (channel == Const.LOGIN_CHANNEL_2G
				|| channel == Const.LOGIN_CHANNEL_3G
				|| channel == Const.LOGIN_CHANNEL_4G) {
			holder.tvState.setVisibility(View.VISIBLE);
			holder.ivState.setVisibility(View.GONE);
			holder.tvState.setText(Const.LOGIN_CHANNELS[channel]);
		} else {
			int[] bgs = { R.drawable.wifi_state0, R.drawable.terminal_icon_pc,
					R.drawable.terminal_icon_mobile,
					R.drawable.terminal_icon_ios };
			holder.tvState.setVisibility(View.GONE);
			holder.ivState.setVisibility(View.VISIBLE);
			holder.ivState.setBackgroundResource(bgs[channel
					- Const.LOGIN_CHANNEL_WIFI]);
		}

		if (f.getIcon() == null) {
			holder.ivIcon.setBackgroundResource(R.drawable.h001);
		} else {
			// TODO 文件夹获取图片
		}
		// 我的设备
		if (groupPosition == 1) {
			if (childPosition == 0) {
				holder.ivIcon
						.setBackgroundResource(R.drawable.qfile_dataline_pc_fa);
			} else {
				holder.ivIcon
						.setBackgroundResource(R.drawable.qq_contact_list_friend_entry_icon);
			}
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition == 0) {
			return 0;
		}

		return groups.get(groupPosition - 1).getFriends().size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groups.get(groupPosition - 1);
	}

	@Override
	public int getGroupCount() {

		return groups.size() + 1;
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {

		if (groupPosition == 0) {
			convertView = mInflater.inflate(R.layout.contact_detail_first, null);
			LinearLayout llGroups = (LinearLayout) convertView
					.findViewById(R.id.llGroups);
			llGroups.setOnClickListener(this);

			return convertView;
		}

		ParentHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = mInflater.inflate(R.layout.group, null);
			holder = new ParentHolder();

			holder.tvName = (TextView) convertView
					.findViewById(R.id.group_name);
			holder.tvCount = (TextView) convertView
					.findViewById(R.id.group_count);
			holder.ivGroupIcon = (ImageView) convertView
					.findViewById(R.id.group_icon);

			convertView.setTag(holder);
		}
		holder = (ParentHolder) convertView.getTag();

		if (isExpanded && groups.get(groupPosition - 1).getFriends().size() > 0) {
			holder.ivGroupIcon
					.setBackgroundResource(R.drawable.skin_indicator_expanded);
		} else {
			holder.ivGroupIcon
					.setBackgroundResource(R.drawable.skin_indicator_unexpanded);
		}

		FriendGroupInfo g = groups.get(groupPosition - 1);
		holder.tvName.setText(g.getName());
		holder.tvCount.setText(g.getCount() + "/" + g.getFriends().size());

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		if (groupPosition == 0) {
			return false;
		}

		return true;
	}

	private static class ChildHolder {
		public TextView tvName, tvContent, tvState;
		public ImageView ivIcon, ivState;
	}

	private static class ParentHolder {
		public TextView tvName, tvCount;
		public ImageView ivGroupIcon;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.llGroups:
				intent = new Intent(mContext, GroupActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.slide_left,
						R.anim.slide_right);
				break;
		}
	}
}
