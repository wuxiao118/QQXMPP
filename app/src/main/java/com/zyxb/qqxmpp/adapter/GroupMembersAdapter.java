package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.SortModel;
import com.zyxb.qqxmpp.db.DBColumns;

import java.util.List;

public class GroupMembersAdapter extends BaseAdapter implements SectionIndexer {
	// private Context context;
	private LayoutInflater mInflater;
	private List<SortModel> friends;
	private OnClickListener mListener;

	public interface OnSearchClickListener {
		public void onClick(View v);
	}

	public GroupMembersAdapter(Context context, List<SortModel> sourceDateList,
							   OnClickListener listener) {
		// this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.friends = sourceDateList;
		this.mListener = listener;
	}

	@Override
	public int getCount() {

		return friends.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return 0;
		}

		return friends.get(position - 1);
	}

	@Override
	public long getItemId(int position) {

		return position - 1;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0) {
			return true;
		}

		if (friends.get(position - 1).getInfo() == null) {
			return false;
		}

		return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 加载搜素
		if (position == 0) {
			convertView = mInflater.inflate(R.layout.group_list_first, null);
			LinearLayout llSearch = (LinearLayout) convertView
					.findViewById(R.id.llSearch);
			// llSearch.setOnClickListener(this);
			llSearch.setOnClickListener(mListener);

			convertView.setTag(null);

			return convertView;
		}

		SortModel sm = friends.get(position - 1);
		// 字母头部
		if (sm.getInfo() == null) {
			convertView = mInflater.inflate(R.layout.group_members_item_g, null);
			TextView tv = (TextView) convertView
					.findViewById(R.id.tvGroupMembersTitle);
			tv.setText(sm.getName());
			convertView.setTag(null);

			return convertView;
		}

		// 成员部分
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = mInflater.inflate(R.layout.group_members_list_item_m,
					null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tvGroupMembersListItemName);
			holder.tvGroupTitle = (TextView) convertView
					.findViewById(R.id.tvGroupMembersListItemGroupTitle);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.ivGroupMembersListItemIcon);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		// 设置名称
		String name = sm.getInfo().getComments();
		if (name == null) {
			name = sm.getInfo().getName();
		}
		holder.tvName.setText(name);

		// 设置图标
		String icon = sm.getInfo().getIcon();
		if (icon == null) {
			holder.ivIcon.setBackgroundResource(R.drawable.h001);
		} else {
			// 加载图片
		}

		// 设置头衔并修改背景
		int level = sm.getInfo().getLevel();
		switch (level) {
			case DBColumns.GROUP_LEVEL_CREATOR:
				holder.tvGroupTitle
						.setBackgroundResource(R.drawable.group_members_list_item_group_title_orange);
				holder.tvGroupTitle.setText("群主");
				break;
			case DBColumns.GROUP_LEVEL_MASTER:
				holder.tvGroupTitle
						.setBackgroundResource(R.drawable.group_members_list_group_tilte_green);
				holder.tvGroupTitle.setText("管理员");
				break;
			default:
				holder.tvGroupTitle
						.setBackgroundResource(R.drawable.group_members_list_group_tilte_gray);
				holder.tvGroupTitle.setText(DBColumns.GROUP_TITLES[sm.getInfo()
						.getGroupTitle()]);
				break;
		}

		return convertView;
	}

	private static class ViewHolder {
		public TextView tvName, tvGroupTitle;
		public ImageView ivIcon;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public int getPositionForSection(int section) {
		// for (int i = 0; i < getCount(); i++) {
		for (int i = 1; i < getCount(); i++) {
			String sortStr = friends.get(i - 1).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position == 0) {
			return 0;
		}

		return friends.get(position - 1).getSortLetters().charAt(0);
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.llSearch:
	// // 显示search
	//
	// break;
	// }
	// }

}
