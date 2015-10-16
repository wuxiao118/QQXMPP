package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.Information;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.util.DateUtils;
import com.zyxb.qqxmpp.util.MyExpressionUtil;
import com.zyxb.qqxmpp.util.NetUtil;

import java.util.List;

public class MsgAdapter extends BaseAdapter {
	private List<MessageInfo> messages;
	private LayoutInflater mInflater;
	private Context mContext;

	public MsgAdapter(Context context, List<MessageInfo> messages) {
		this.messages = messages;
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}

	public void setMessages(List<MessageInfo> messages) {
		this.messages = messages;
	}

	@Override
	public int getCount() {

		return messages.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return null;
		}

		return messages.get(position - 1);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			convertView = mInflater.inflate(R.layout.msg_detail_first, null);
			LinearLayout llDisconnected = (LinearLayout) convertView
					.findViewById(R.id.llDisconncted);
			if (NetUtil.checkNet(mContext)) {
				llDisconnected.setVisibility(View.GONE);
			} else {
				llDisconnected.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = mInflater.inflate(R.layout.msg_detail, null);
			holder = new ViewHolder();
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.ivMsgIcon);
			holder.ivSending = (ImageView) convertView
					.findViewById(R.id.tvMsgSending);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tvMsgTitle);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.tvMsgContent);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvMsgTime);
			holder.tvCount = (TextView) convertView.findViewById(R.id.tvMsgNew);
			holder.flCount = (FrameLayout) convertView
					.findViewById(R.id.flMsgNew);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		MessageInfo message = messages.get(position - 1);

		if (message.getCount() > 0) {
			holder.tvCount.setText(message.getCount() + "");
			holder.flCount.setVisibility(View.VISIBLE);
		} else {
			holder.flCount.setVisibility(View.INVISIBLE);
		}

		String icon = null;
		if (message.getType() == DBColumns.MESSAGE_TYPE_GROUP) {
			icon = message.getTo().getIcon();
		} else {
			icon = message.getFrom().getIcon();
		}

		if (icon == null) {
			holder.ivIcon.setImageResource(R.drawable.h001);
		} else {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.outHeight = 48;
			options.outWidth = 48;
			Bitmap bitmap = BitmapFactory.decodeFile(icon, options);
			holder.ivIcon.setImageBitmap(bitmap);
		}

		if (message.getState() == DBColumns.MESSAGE_STATE_SENDING) {
			holder.ivSending.setVisibility(View.VISIBLE);
		} else {
			holder.ivSending.setVisibility(View.GONE);
		}

		if (message.getType() == DBColumns.MESSAGE_TYPE_CONTACT) {
			if (message.getFrom().getType() == Information.TYPE_CONTACT_USER) {
				String remark = message.getFrom().getComments();
				if (remark != null) {
					holder.tvTitle.setText(remark);
				} else {
					holder.tvTitle.setText(message.getFrom().getName());
				}
			} else {
				String remark = message.getTo().getComments();
				if (remark != null) {
					holder.tvTitle.setText(message.getTo().getComments());
				} else {
					holder.tvTitle.setText(message.getTo().getName());
				}
			}
		} else if (message.getType() == DBColumns.MESSAGE_TYPE_SYS) {
			holder.tvTitle.setText(message.getFrom().getName());
		} else if (message.getType() == DBColumns.MESSAGE_TYPE_GROUP) {
			holder.tvTitle.setText(message.getTo().getName());
		}

		if (message.getType() == DBColumns.MESSAGE_TYPE_GROUP) {
			holder.tvContent.setText(MyExpressionUtil.parse(mContext,message.getFrom().getComments() + ":"
					+ message.getMsg()));
		} else {
			holder.tvContent.setText(MyExpressionUtil.parse(mContext,message.getMsg()));
		}

		holder.tvTime.setText(DateUtils.getMsgDate(message.getCreateTime()));

		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivIcon, ivSending;
		public TextView tvTitle;
		public TextView tvContent;
		public TextView tvTime;
		public TextView tvCount;
		public FrameLayout flCount;
	}
}
