package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.util.PopupUtils;
import com.zyxb.qqxmpp.util.PopupUtils.OnPopupLayoutView;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.R;

public class GroupDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvBack;
	private TextView tvName, tvAccount, tvClassification, tvVistingCard,
			tvMemberNum, tvDesp;
	private LinearLayout llGroupMembers;// ,llGroupDesp;
	private LinearLayout llPopMenu;
	private ImageView ivIcon;

	// private PopupWindow popWindow;
	private DB3Group group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_item_info);
		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupItemBack);
		llGroupMembers = findView(R.id.llGroupMembers);
		llPopMenu = findView(R.id.ivGroupItemReport);
		// llGroupDesp = findView(R.id.llGroupDesp);

		ivIcon = findView(R.id.ivGroupIcon);
		tvName = findView(R.id.tvGroupName);
		tvAccount = findView(R.id.tvGroupAccount);
		tvClassification = findView(R.id.tvGroupClassification);
		tvVistingCard = findView(R.id.tvGroupVistingCard);
		tvMemberNum = findView(R.id.tvGroupMemberNum);
		tvDesp = findView(R.id.tvGroupDesp);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		llGroupMembers.setOnClickListener(this);
		llPopMenu.setOnClickListener(this);
	}

	private void initData() {
		Intent intent = getIntent();
		String groupAccount = intent.getStringExtra("account");
		//System.out.println("groupAccount:" + groupAccount);
		group = engine.getGroupInfo(groupAccount);

		// 初始化界面
		if (group.getIcon() == null) {
			ivIcon.setBackgroundResource(R.drawable.qq_contact_list_friend_entry_icon);
		} else {
			// 加载图片
		}
		tvName.setText(group.getName());
		tvAccount.setText(group.getAccount());
		tvClassification.setText(group.getClassification());
		tvVistingCard.setText(engine.getGroupRemark(group.getAccount(),
				user.getAccount()));
		tvMemberNum.setText(engine.getGroupNum(group.getAccount()) + "");
		if (group.getDesp() == null) {
			tvDesp.setText("这个家伙很懒，什么都没留下");
		} else {
			tvDesp.setText(group.getDesp());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvGroupItemBack:
				app.back();
				break;
			case R.id.llGroupMembers:
				Intent intent = new Intent(this, GroupMembersActivity.class);
				intent.putExtra("groupAccount", group.getAccount());
				startActivity(intent);
				//overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
				UIAnimUtils.sildLeftIn(this);
				break;
			case R.id.ivGroupItemReport:
				PopupUtils.showPopupWindow(this, v, R.layout.group_popup_group,
						new MyPopupInit());
				break;
		}
	}

	private class MyPopupInit implements OnPopupLayoutView, OnClickListener {
		private PopupWindow popWindow;
		private View view;

		@Override
		public void beforeLoad() {

		}

		@Override
		public void afterLoad(PopupWindow popWindow, View view) {
			this.popWindow = popWindow;
			this.view = view;

			LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
			LinearLayout outside = (LinearLayout) view
					.findViewById(R.id.outside);
			cancel.setOnClickListener(this);
			outside.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.cancel:
				case R.id.outside:
					// popWindow.dismiss();
					dismiss();
					break;
			}
		}

		private void dismiss() {
			TranslateAnimation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f);
			animation.setDuration(400);
			animation.setFillAfter(true);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					popWindow.dismiss();
				}
			});

			view.startAnimation(animation);
		}
	}

}
