package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.ui.ChatActivity;
import com.zyxb.qqxmpp.ui.FriendDtailActivity;
import com.zyxb.qqxmpp.ui.GroupFriendCardActivity;
import com.zyxb.qqxmpp.ui.LoginUserDetail;
import com.zyxb.qqxmpp.util.DateUtils;
import com.zyxb.qqxmpp.util.ImgUtil;
import com.zyxb.qqxmpp.util.MyExpressionUtil;
import com.zyxb.qqxmpp.util.PopupUtils;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.CircleImageView2;
import com.zyxb.qqxmpp.view.QuickAction;
import com.zyxb.qqxmpp.view.QuickAction.OnActionItemClickListener;

import java.util.List;

public class ChatAdapter extends BaseAdapter implements OnClickListener,
		OnLongClickListener {
	private List<MessageInfo> messages;
	private DBUser mUser;
	private LayoutInflater mInflater;
	private ChatActivity mContext;
	private int type;
	private String account;
	private String groupAccount;

	public ChatAdapter(ChatActivity context, DBUser mUser,
					   List<MessageInfo> messages) {
		this.mContext = context;
		this.messages = messages;
		this.mUser = mUser;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setMessages(List<MessageInfo> messages) {
		this.messages = messages;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chat_list_item, null);

			holder = new ViewHolder();
			holder.tvTime = findView(convertView, R.id.tvChatTime);
			// left
			holder.llLeft = findView(convertView, R.id.llChatLeft);
			holder.ivLeftIcon = findView(convertView, R.id.ciChatLeftIcon);
			holder.tvLeftName = findView(convertView, R.id.tvChatLeftName);
			holder.tvLeftContent = findView(convertView, R.id.tvChatLeftContent);
			holder.ivLeftImage = findView(convertView, R.id.ivChatLeftImage);
			holder.ivLeftLoc = findView(convertView, R.id.ivChatLeftLoc);
			// holder.ivLeftLoading = findView(convertView,
			// R.id.ivChatLeftLoading);
			holder.tvLeftGroupTitle = findView(convertView,
					R.id.tvChatLeftGroupTitle);
			holder.pbLeftLoading = findView(convertView, R.id.pbChatLeftLoading);

			// right
			holder.llRight = findView(convertView, R.id.llChatRight);
			holder.ivRightIcon = findView(convertView, R.id.ciChatRightIcon);
			holder.tvRightName = findView(convertView, R.id.tvChatRightName);
			holder.tvRightContent = findView(convertView,
					R.id.tvChatRightContent);
			holder.ivRightImage = findView(convertView, R.id.ivChatRightImage);
			holder.ivRightLoc = findView(convertView, R.id.ivChatRightLoc);
			// holder.ivRightLoading = findView(convertView,
			// R.id.ivChatRightLoading);
			holder.ivRightMessageState = findView(convertView,R.id.ivChatRightMessageState);
			holder.tvRightGroupTitle = findView(convertView,
					R.id.tvChatRightGroupTitle);
			holder.pbRightLoading = findView(convertView,
					R.id.pbChatRightLoading);

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		MessageInfo message = messages.get(position);
		// System.out.println("type:" + message.getType());
		type = message.getType();
		account = message.getFrom().getAccount();
		if (mUser.getAccount().equals(account)) {
			account = message.getTo().getAccount();
		}
		if(type == DBColumns.MESSAGE_TYPE_GROUP){
			groupAccount = message.getTo().getAccount();
		}

		holder.tvTime.setText(DateUtils.getMsgDate(message.getCreateTime()));

		// 自己发送的消息
		if (mUser.getAccount().equals(message.getFrom().getAccount())) {
			// 右边
			holder.llLeft.setVisibility(View.GONE);
			holder.llRight.setVisibility(View.VISIBLE);

			if (message.getFrom().getIcon() == null
					|| message.getFrom().getIcon().trim().equals("")) {
				// 默认
				holder.ivRightIcon
						.setBackgroundResource(R.drawable.ic_launcher);
			} else {
				// 加载图片
				// TODO
			}

			holder.tvRightName.setVisibility(View.GONE);
			//holder.tvRightContent.setText(MyExpressionUtil.prase(mContext,
			//		holder.tvRightContent, message.getMsg()));
			//holder.ivRightImage.setVisibility(View.GONE);
			holder.ivRightLoc.setVisibility(View.GONE);
			holder.tvRightGroupTitle.setVisibility(View.GONE);
			// holder.pbLeftLoading.setVisibility(View.GONE);
			if (message.getState() == DBColumns.MESSAGE_STATE_SENDING) {
				holder.ivRightMessageState.setVisibility(View.GONE);
				holder.pbRightLoading.setVisibility(View.VISIBLE);
			} else if(message.getState()==DBColumns.MESSAGE_STATE_FAIL){
				holder.pbRightLoading.setVisibility(View.GONE);
				holder.ivRightMessageState.setVisibility(View.VISIBLE);
			}else{
				holder.pbRightLoading.setVisibility(View.GONE);
				holder.ivRightMessageState.setVisibility(View.GONE);
			}

			//发送图片
			String msgType = message.getMsgType();
			if(msgType != null && message.getMsgType().equals(DBColumns.MESSAGE_MSG_TYPE_IMAGE)){
				holder.tvRightContent.setVisibility(View.GONE);
				holder.ivRightImage.setVisibility(View.VISIBLE);
				holder.ivRightImage.setImageBitmap(ImgUtil.getInstance().loadBitmapFromLocal(message.getMsg(),200,200));
			}else{
				holder.tvRightContent.setVisibility(View.VISIBLE);
				holder.ivRightImage.setVisibility(View.GONE);
				holder.tvRightContent.setText(MyExpressionUtil.prase(mContext,
						holder.tvRightContent, message.getMsg()));
			}

			//Logger.d("ChatActivity","msgType:" + msgType);

			//TODO 发送位置信息

		} else {
			// 左边
			holder.llRight.setVisibility(View.GONE);
			holder.llLeft.setVisibility(View.VISIBLE);

			String icon = null;
			switch (message.getType()) {
				case DBColumns.MESSAGE_TYPE_CONTACT:
					icon = message.getFrom().getIcon();
					break;
				case DBColumns.MESSAGE_TYPE_GROUP:
					icon = message.getTo().getIcon();
					break;
				case DBColumns.MESSAGE_TYPE_SYS:
					icon = message.getFrom().getIcon();
					break;
			}

			if (icon == null || icon.trim().equals("")) {
				// 默认
				holder.ivLeftIcon.setBackgroundResource(R.drawable.ic_launcher);
			} else {
				// 加载图片
				// TODO
			}

			// System.out.println("type:" + message.getType() + ",name:"
			// + message.getName());
			if (message.getType() == DBColumns.MESSAGE_TYPE_CONTACT
					|| message.getType() == DBColumns.MESSAGE_TYPE_SYS) {
				holder.tvLeftName.setVisibility(View.GONE);
				holder.tvLeftGroupTitle.setVisibility(View.GONE);
			} else {
				// 群
				holder.tvLeftName.setVisibility(View.VISIBLE);
				holder.tvLeftName
						.setText(message.getFrom().getComments() + ":");

				holder.tvLeftGroupTitle.setVisibility(View.VISIBLE);
				holder.tvLeftGroupTitle.setText(DBColumns.GROUP_TITLES[message
						.getFrom().getGroupTitle()]);
				// System.out.println("group title:" + message.getGroupTitle());
			}

			// holder.tvLeftContent.setText(message.getMsg());
			//holder.tvLeftContent.setText(MyExpressionUtil.prase(mContext,
			//		holder.tvLeftContent, message.getMsg()));
			holder.ivLeftImage.setVisibility(View.GONE);
			holder.ivLeftLoc.setVisibility(View.GONE);

			holder.pbLeftLoading.setVisibility(View.GONE);

			//发送图片
			String msgType = message.getMsgType();
			if(msgType != null && message.getMsgType().equals(DBColumns.MESSAGE_MSG_TYPE_IMAGE)){
				holder.tvLeftContent.setVisibility(View.GONE);
				holder.ivLeftImage.setVisibility(View.VISIBLE);
				holder.ivLeftImage.setImageBitmap(ImgUtil.getInstance().loadBitmapFromLocal(message.getMsg(),200,200));
			}else{
				holder.tvLeftContent.setVisibility(View.VISIBLE);
				holder.ivLeftImage.setVisibility(View.GONE);
				holder.tvLeftContent.setText(MyExpressionUtil.prase(mContext,
						holder.tvLeftContent, message.getMsg()));
			}
		}

		holder.ivLeftIcon.setOnClickListener(this);
		holder.tvLeftContent.setOnLongClickListener(this);
		holder.tvRightContent.setOnLongClickListener(this);
		holder.ivRightIcon.setOnClickListener(this);

		holder.ivLeftIcon.setTag(account);
		holder.tvLeftContent.setTag(account);
		holder.tvRightContent.setTag(account);
		holder.ivRightIcon.setTag(account);

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T findView(View v, int id) {
		return (T) v.findViewById(id);
	}

	private static class ViewHolder {
		public TextView tvTime, tvLeftName, tvRightName, tvLeftContent,
				tvRightContent, tvLeftGroupTitle, tvRightGroupTitle;
		public LinearLayout llLeft;
		public RelativeLayout llRight;
		public CircleImageView2 ivLeftIcon, ivRightIcon;
		public ImageView ivLeftImage, ivRightImage, ivLeftLoc, ivRightLoc,ivRightMessageState;
		// ivLeftLoading, ivRightLoading;
		public ProgressBar pbLeftLoading, pbRightLoading;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		String account = (String) v.getTag();

		switch (v.getId()) {
			case R.id.tvChatLeftContent:
			case R.id.tvChatRightContent:
				// PopupUtils.showPop(mContext, v, new String[] { "复制", "转发", "收藏",
				// "转发多条", "删除" });

				break;
			case R.id.ciChatRightIcon:
				if (type == DBColumns.MESSAGE_TYPE_CONTACT) {
					// TODO 显示自己
					intent = new Intent(mContext,LoginUserDetail.class);
					mContext.startActivity(intent);
					UIAnimUtils.sildLeftIn(mContext);
					return;
				}
			case R.id.ciChatLeftIcon:
				if (type == DBColumns.MESSAGE_TYPE_GROUP) {
					// intent = new Intent(mContext, GroupDetailActivity.class);
					intent = new Intent(mContext, GroupFriendCardActivity.class);
					intent.putExtra("groupAccount", groupAccount);
				} else if (type == DBColumns.MESSAGE_TYPE_CONTACT) {
					intent = new Intent(mContext, FriendDtailActivity.class);
				} else {
					return;
				}

				intent.putExtra("account", account);
				intent.putExtra("isFromChat", true);
				mContext.startActivity(intent);
				// mContext.overridePendingTransition(R.anim.slide_left,
				// R.anim.slide_right);
				UIAnimUtils.sildLeftIn(mContext);
				break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
			case R.id.tvChatLeftContent:
			case R.id.tvChatRightContent:
				// int[] arrayOfInt = new int[2];
				// v.getLocationInWindow(arrayOfInt);
				// int i = arrayOfInt[0];
				// int j = arrayOfInt[1];
				// // System.out.println("i=" + i + ",j=" + j);
				// int[] location = new int[2];
				// v.getLocationOnScreen(location);
				// System.out.println("x=" + location[0] + ",y=" + location[1]);
				// PopupUtils.showPop(mContext, v, new String[] { "复制", "转发", "收藏",
				// "转发多条", "删除" }, i, j);
				final String[] menus = new String[] { "复制", "转发", "收藏",
						"转发多条", "删除" };
				PopupUtils.showQuickAction(mContext, v, menus, new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos, int actionId) {
						Toast.makeText(mContext, menus[pos], Toast.LENGTH_SHORT).show();
					}
				});

				break;
		}

		return true;
	}
}
