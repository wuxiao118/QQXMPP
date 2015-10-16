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
import com.zyxb.qqxmpp.bean.MsgPhoneInfo;

import java.lang.reflect.Field;
import java.util.List;

public class MsgPhoneAdapter extends BaseAdapter {
	private List<MsgPhoneInfo> items;
	private LayoutInflater mInflater;

	public MsgPhoneAdapter(Context context, List<MsgPhoneInfo> phoneInfos) {
		this.items = phoneInfos;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size() + 3;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0 || position == 1 || position == (items.size() + 2)) {
			return null;
		}

		return items.get(position - 2);
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 1) {
			return false;
		}

		return true;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			convertView = mInflater.inflate(R.layout.msg_phone_list_first, null);

			return convertView;
		}

		if (position == 1) {
			convertView = mInflater
					.inflate(R.layout.msg_phone_list_second, null);

			return convertView;
		}

		if (position == (items.size() + 2)) {
			convertView = mInflater.inflate(R.layout.msg_phone_list_bottom, null);

			return convertView;
		}

		convertView = mInflater.inflate(R.layout.msg_phone_list_item, null);

		ImageView rightIcon = (ImageView) convertView.findViewById(R.id.ivMsgPhoneListItemRightIcon);
		TextView title = (TextView) convertView.findViewById(R.id.tvMsgPhoneListItemName);
		TextView content = (TextView) convertView.findViewById(R.id.tvMsgPhoneListItemContent);
		rightIcon.setBackgroundResource(getImageByReflect(items.get(position-2).getRightIcon()));
		title.setText(items.get(position-2).getTitle());
		content.setText(items.get(position-2).getContent());

		return convertView;
	}

	public int getImageByReflect(String imageName) {
		try {
			Field field = Class.forName("com.zyxb.qqxmpp.R$drawable").getField(
					imageName);
			return field.getInt(field);
		} catch (Exception e) {
			return -1;
		}
	}
}
