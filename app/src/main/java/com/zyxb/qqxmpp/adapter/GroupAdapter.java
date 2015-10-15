package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.vo.GroupInfo;

import java.util.List;

public class GroupAdapter extends BaseAdapter {
	// private Context context;
	private LayoutInflater inflater;
	private List<GroupInfo> groups;

	// private List<GroupTitle> titles;

	public GroupAdapter(Context context) {
		// this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setGroups(List<GroupInfo> groups) {
		this.groups = groups;

		// initData();
	}

	// private void initData() {
	// // 将数据分类(我创建的群,我管理的群,我加入的群)
	//
	// }

	public List<GroupInfo> getGroups() {
		return groups;
	}

	@Override
	public int getCount() {

		return groups.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return null;
		}
		return groups.get(position - 1);
	}

	@Override
	public long getItemId(int position) {

		return position - 1;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0 || groups.get(position - 1) == null) {
			return false;
		}

		return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			// convertView = inflater.inflate(R.layout.search, null);
			// convertView.setBackgroundColor(context.getResources().getColor(R.color.search_bg_color));
			convertView = inflater.inflate(R.layout.group_list_first, null);
			convertView.setTag(null);

			return convertView;
		}

		GroupInfo group = groups.get(position - 1);
		if (group.getType() == GroupInfo.GROUP_TYPE_TITLE) {
			convertView = inflater.inflate(R.layout.group_members_item_g, null);
			TextView tv = (TextView) convertView
					.findViewById(R.id.tvGroupMembersTitle);
			tv.setText(group.getName() + "(" + group.getLevel() + ")");
			convertView.setTag(null);

			return convertView;
		}
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.group_list_item, null);
			holder = new ViewHolder();
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.ivGroupItemIcon);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tvGroupItemName);

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		// 设置数据
		// DB3Group group = groups.get(position - 1);
		holder.tvName.setText(group.getName());
		if (group.getIcon() == null) {
			holder.ivIcon.setBackgroundResource(R.drawable.h001);
		}

		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
	}

	// private class GroupTitle {
	// private String name;
	// private int count;
	// private int position;
	//
	// public int getPosition() {
	// return position;
	// }
	//
	// public void setPosition(int position) {
	// this.position = position;
	// }
	//
	// public String getName() {
	// return name;
	// }
	//
	// public void setName(String name) {
	// this.name = name;
	// }
	//
	// public int getCount() {
	// return count;
	// }
	//
	// public void setCount(int count) {
	// this.count = count;
	// }
	// }
}
