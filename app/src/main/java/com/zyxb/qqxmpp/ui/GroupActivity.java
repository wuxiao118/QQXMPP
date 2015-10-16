package com.zyxb.qqxmpp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.GroupAdapter;
import com.zyxb.qqxmpp.bean.vo.GroupInfo;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.view.CustomListView;
import com.zyxb.qqxmpp.view.CustomListView.OnRefreshListener;

import java.util.List;

public class GroupActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener, OnItemClickListener {
	private TextView tvBack;
	private ImageView ivMenu;
	private LinearLayout llMenu;
	private LinearLayout llDisContainer;
	private CustomListView clList;
	private View vBg;

	private LinearLayout llGroup;
	private LinearLayout llDiscussion;
	private TextView tvGroup;
	private TextView tvDiscussion;
	private View vGroupLine;
	private View vGroupLineGray;
	private View vDiscussionLine;
	private View vDiscussionLineGray;

	private boolean isMenuShowing = false;

	private GroupAdapter mAdapter;
	private List<GroupInfo> groups;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_list);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupListBack);
		ivMenu = findView(R.id.ivGroupMenu);
		llMenu = findView(R.id.llGroupMenu);
		vBg = findView(R.id.vGroupTransBg);
		llDisContainer = findView(R.id.llDiscussionContainer);
		clList = findView(R.id.lvGroups);

		llGroup = findView(R.id.llMyGroup);
		llDiscussion = findView(R.id.llDiscussion);
		tvGroup = findView(R.id.tvMyGroup);
		tvDiscussion = findView(R.id.tvDiscussion);
		vGroupLine = findView(R.id.vMyGroupLine);
		vGroupLineGray = findView(R.id.vMyGroupLineGray);
		vDiscussionLine = findView(R.id.vDiscussionLine);
		vDiscussionLineGray = findView(R.id.vDiscussionLineGray);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);
		ivMenu.setOnClickListener(this);
		llGroup.setOnClickListener(this);
		llDiscussion.setOnClickListener(this);

		clList.setCanRefresh(true);
		clList.setCanLoadMore(false);
		clList.setOnRefreshListener(this);
		clList.setOnItemClickListener(this);

		vBg.setOnClickListener(this);
	}

	private void initData() {
		llMenu.setVisibility(View.GONE);
		vBg.setVisibility(View.GONE);

		groups = mEngine.getGroups();
		mAdapter = new GroupAdapter(this);
		mAdapter.setGroups(groups);
		clList.setAdapter(mAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvGroupListBack:
				mApp.back();
				break;
			case R.id.ivGroupMenu:
				if (!isMenuShowing) {
					ivMenu.setBackgroundResource(R.drawable.skin_conversation_title_right_btn_selected);
					startAnimation(true);
				} else {
					ivMenu.setBackgroundResource(R.drawable.skin_conversation_title_right_btn);
					startAnimation(false);
				}

				isMenuShowing = !isMenuShowing;
				break;
			case R.id.llMyGroup:
				show(v.getId());
				break;
			case R.id.llDiscussion:
				show(v.getId());
				break;
			case R.id.vGroupTransBg:
				ivMenu.setBackgroundResource(R.drawable.skin_conversation_title_right_btn);
				startAnimation(false);
				isMenuShowing = false;

				break;
		}
	}

	private void show(int id) {
		// 所有相关设置为非点击状态
		tvGroup.setTextColor(getResources().getColor(R.color.group_color_gray));
		vGroupLine.setVisibility(View.INVISIBLE);
		clList.setVisibility(View.GONE);
		vGroupLineGray.setBackgroundColor(getResources().getColor(
				R.color.group_item_devide_line));

		tvDiscussion.setTextColor(getResources().getColor(
				R.color.group_color_gray));
		vDiscussionLine.setVisibility(View.INVISIBLE);
		llDisContainer.setVisibility(View.GONE);
		vDiscussionLineGray.setBackgroundColor(getResources().getColor(
				R.color.group_item_devide_line));

		switch (id) {
			case R.id.llMyGroup:
				tvGroup.setTextColor(getResources().getColor(
						R.color.group_color_blue));
				vGroupLine.setVisibility(View.VISIBLE);
				clList.setVisibility(View.VISIBLE);
				vGroupLineGray.setBackgroundColor(getResources().getColor(
						R.color.group_color_blue));
				break;

			case R.id.llDiscussion:
				tvDiscussion.setTextColor(getResources().getColor(
						R.color.group_color_blue));
				vDiscussionLine.setVisibility(View.VISIBLE);
				llDisContainer.setVisibility(View.VISIBLE);
				vDiscussionLineGray.setBackgroundColor(getResources().getColor(
						R.color.group_color_blue));
				break;
		}
	}

	private void startAnimation(final boolean t) {
		AlphaAnimation alpha = null;
		TranslateAnimation trans = null;
		if (t) {
			// 出现动画
			alpha = new AlphaAnimation(0.0f, 1.0f);
			alpha.setDuration(600);
			alpha.setFillAfter(true);

			trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, -1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			trans.setDuration(600);
			trans.setFillAfter(true);

		} else {
			// 消失动画
			alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(200);
			alpha.setFillAfter(true);

			trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, -1.0f);
			trans.setDuration(200);
			trans.setFillAfter(true);
		}

		alpha.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (!t) {
					vBg.setVisibility(View.GONE);
					vBg.clearAnimation();
					vBg.setClickable(false);
					vBg.setEnabled(false);
				}
			}
		});

		trans.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (!t) {
					llMenu.setVisibility(View.GONE);
					llMenu.clearAnimation();
					llMenu.setClickable(false);
					llMenu.setEnabled(false);
				}
			}
		});

		vBg.setVisibility(View.VISIBLE);
		vBg.setEnabled(true);
		vBg.setClickable(true);
		llMenu.setVisibility(View.VISIBLE);
		llMenu.setClickable(true);
		llMenu.setEnabled(true);
		vBg.startAnimation(alpha);
		llMenu.startAnimation(trans);
	}

	@Override
	public void onRefresh() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				clList.onRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("toAccount", groups.get(position - 2).getAccount());
		intent.putExtra("type", DBColumns.MESSAGE_TYPE_GROUP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	}
}
