package com.zyxb.qqxmpp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.GroupMembersAdapter;
import com.zyxb.qqxmpp.bean3.Information;
import com.zyxb.qqxmpp.bean3.PinyinComparator;
import com.zyxb.qqxmpp.bean3.SortModel;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.util.CharacterParser;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.PopupUtils;
import com.zyxb.qqxmpp.util.PopupUtils.OnPopupLayoutView;
import com.zyxb.qqxmpp.view.ReboundScrollView;
import com.zyxb.qqxmpp.view.SideBar;
import com.zyxb.qqxmpp.view.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMembersActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private TextView tvBack;
	// private LinearLayout container;
	// private int position = 1;
	private LayoutInflater inflater;

	private LinearLayout llPop, llSearch, llSearchContainer, llTitle,
			llContent,llSearchBg;
	private View vBg;
	private ListView lvMembers;
	// private ListView lvResult;
	private SideBar sbBar;

	private EditText etSearch;
	private Button btCancel;
	private TextView tvCancel, tvDialog;

	// 搜索结果
	private ReboundScrollView svResult;
	private LinearLayout llResultContainer;
	private TextView tvResult;

	// 数据
	private List<Information> friends;
	private GroupMembersAdapter adapter;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> sourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	// 显示搜索框
	// private boolean isShowSearch = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_members);

		initUI();
		initEvent();
		initData();
	}

	private void initUI() {
		tvBack = findView(R.id.tvGroupMembersBack);
		// container = findView(R.id.llGroupMembersContainer);
		llPop = findView(R.id.llGroupMembersInfo);
		vBg = findView(R.id.vGroupMembersBg);
		llSearch = findView(R.id.llGroupMembersSearch);
		// lvResult = findView(R.id.lvGroupMembersResult);
		llSearchContainer = findView(R.id.llGroupMembersSearchContainer);
		llTitle = findView(R.id.llGroupMembersTitle);
		llContent = findView(R.id.llGroupMembersContent);
		llSearchBg = findView(R.id.llGroupMembersSearchBg);

		lvMembers = findView(R.id.lvGroupMembers);
		sbBar = findView(R.id.sbGroupMembers);

		etSearch = findView(R.id.etGroupMembersSearch);
		btCancel = findView(R.id.btGroupMembersSearchClear);
		tvCancel = findView(R.id.tvGroupMembersCancel);
		tvDialog = findView(R.id.tvGroupMembersDialog);

		svResult = findView(R.id.svGroupMembersResult);
		llResultContainer = findView(R.id.llGroupMemebersResultContainer);
		tvResult = findView(R.id.tvGroupMembersResult);
	}

	private void initEvent() {
		tvBack.setOnClickListener(this);

		llPop.setOnClickListener(this);
		lvMembers.setOnItemClickListener(this);
		btCancel.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		llSearchContainer.setOnClickListener(this);
		vBg.setOnClickListener(this);
		llSearchBg.setOnClickListener(this);

		// 设置右侧触摸监听
		sbBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					lvMembers.setSelection(position);
				}

			}
		});

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				// System.out.println("TextChanged:" + s);
				if (text.equals("")) {
					// lvResult.setVisibility(View.GONE);
					svResult.setVisibility(View.GONE);
					btCancel.setVisibility(View.GONE);
					//llSearchBg.setVisibility(View.INVISIBLE);
					llSearchBg.setVisibility(View.VISIBLE);
				} else {
					// lvResult.setVisibility(View.VISIBLE);
					svResult.setVisibility(View.VISIBLE);
					btCancel.setVisibility(View.VISIBLE);
					llSearchBg.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ScrollView
	 *
	 * @param filterStr
	 */
	@SuppressLint("InflateParams")
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		llResultContainer.removeAllViews();

		if (TextUtils.isEmpty(filterStr)) {
			// filterDateList = sourceDateList;
			filterDateList.clear();
		} else {
			filterDateList.clear();
			for (SortModel sortModel : sourceDateList) {
				String name = sortModel.getName();
				if ((name.indexOf(filterStr.toString()) != -1 || characterParser
						.getSelling(name).startsWith(filterStr.toString()))
						&& sortModel.getInfo() != null) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		if (filterDateList.size() > 0) {
			tvResult.setVisibility(View.GONE);
			Collections.sort(filterDateList, pinyinComparator);
			// adapter.updateListView(filterDateList);

			// LinearLayout添加View
			SortModel sm = null;
			for (int i = 0; i < filterDateList.size(); i++) {
				sm = filterDateList.get(i);

				View view = inflater.inflate(
						R.layout.group_members_list_item_m, null);
				ImageView icon = (ImageView) view
						.findViewById(R.id.ivGroupMembersListItemIcon);
				TextView tvGroupTitle = (TextView) view
						.findViewById(R.id.tvGroupMembersListItemGroupTitle);
				TextView tvName = (TextView) view
						.findViewById(R.id.tvGroupMembersListItemName);

				String name = sm.getInfo().getComments();
				if (name == null) {
					name = sm.getInfo().getName();
				}
				tvName.setText(name);

				int level = sm.getInfo().getLevel();
				switch (level) {
					case DB3Columns.GROUP_LEVEL_CREATOR:
						tvGroupTitle
								.setBackgroundResource(R.drawable.group_members_list_item_group_title_orange);
						tvGroupTitle.setText("群主");
						break;
					case DB3Columns.GROUP_LEVEL_MASTER:
						tvGroupTitle
								.setBackgroundResource(R.drawable.group_members_list_group_tilte_green);
						tvGroupTitle.setText("管理员");
						break;
					default:
						tvGroupTitle
								.setBackgroundResource(R.drawable.group_members_list_group_tilte_gray);
						tvGroupTitle.setText(DB3Columns.GROUP_TITLES[sm.getInfo()
								.getGroupTitle()]);
						break;
				}

				String in = sm.getInfo().getIcon();
				if (in == null) {
					icon.setBackgroundResource(R.drawable.h001);
				} else {
					// 加载图片
				}

				// 加载
				llResultContainer.addView(view);
			}
		} else {
			// 显示 没有结果
			// tvResult.setVisibility(View.VISIBLE);
			// llGroupContainer.removeAllViews() 删除了tvResult
			llResultContainer.removeAllViews();
			View v = inflater.inflate(R.layout.group_members_textview, null);
			llResultContainer.addView(v);
		}
	}

	@SuppressLint("InflateParams")
	private void initData() {
		//vBg.setEnabled(true);
		//vBg.setClickable(true);

		// 拼音
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		// 初始化成员数据
		inflater = LayoutInflater.from(this);

		// 获取群成员数据
		Intent intent = getIntent();
		String groupAccount = intent.getStringExtra("groupAccount");

		// 加载View
		// View view = inflater.inflate(R.layout.group_members_item_g, null);
		// view.setTag(position++);
		// findView(view,R.id.llGroupMembersItemTitle).setOnClickListener(this);
		// container.addView(view);

		sbBar.setTextView(tvDialog);
		// 查找群成员
		friends = engine.getGroupFriends(groupAccount);
		// 填充数据
		sourceDateList = filledData(friends);
		// 排序并添加管理员、[A-Z]部分
		sortData();
		// 设置数据适配器
		adapter = new GroupMembersAdapter(this, sourceDateList, this);
		lvMembers.setAdapter(adapter);
	}

	private void sortData() {
		// 将数据按字母顺序排序
		// Collections.sort(sourceDateList,pinyinComparator);

		// 将数据分类为[A-Z]、管理员
		Map<String, List<SortModel>> map = new HashMap<String, List<SortModel>>();
		// 增加管理员
		List<SortModel> manager = new ArrayList<SortModel>();
		String managerLetter = "@";
		SortModel sm = new SortModel();
		sm.setName("群主、管理员");
		sm.setSortLetters(managerLetter);
		sm.setInfo(null);
		manager.add(sm);
		sm = null;
		map.put(managerLetter, manager);
		manager = null;

		// 添入数据
		// boolean isEnd = false;
		// 当前字母下名字数量
		// int count = 0;
		for (int i = 0; i < sourceDateList.size(); i++) {
			SortModel source = sourceDateList.get(i);

			// //是否是一个字母遍历结束,可以添加数量
			// if(isEnd){
			//
			// }

			// 是否是管理员
			if (source.getInfo().getLevel() == DB3Columns.GROUP_LEVEL_CREATOR
					|| source.getInfo().getLevel() == DB3Columns.GROUP_LEVEL_MASTER) {
				source.setSortLetters(managerLetter);
				// 如果是群主,放在第二个,第一个为title
				if (source.getInfo().getLevel() == DB3Columns.GROUP_LEVEL_CREATOR) {
					map.get(managerLetter).add(1, source);
				} else {
					map.get(managerLetter).add(source);
				}
				continue;
			}

			boolean contains = map.containsKey(source.getSortLetters());
			if (contains) {
				map.get(source.getSortLetters()).add(source);
			} else {
				List<SortModel> tmp = new ArrayList<SortModel>();
				SortModel tp = new SortModel();
				tp.setName(source.getSortLetters());
				tp.setSortLetters(source.getSortLetters());
				tp.setInfo(null);
				tmp.add(tp);
				tp = null;

				// 添加当前元素
				tmp.add(source);
				map.put(source.getSortLetters(), tmp);
				tmp = null;
			}

			source = null;
		}

		// 将map按字母顺序排序
		sourceDateList.clear();
		// 将数据合并到list,并添加数量
		String[] tmps = { managerLetter, "A", "B", "C", "D", "E", "F", "G",
				"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
				"T", "U", "V", "W", "X", "Y", "Z", "#" };
		for (int i = 0; i < tmps.length; i++) {
			String letter = tmps[i];
			if (map.containsKey(letter)) {
				// 添加数量
				int count = map.get(letter).size() - 1;
				String s = map.get(letter).get(0).getName();
				map.get(letter).get(0).setName(s + "(" + count + "人)");
				sourceDateList.addAll(map.get(letter));
			}
		}
	}

	/**
	 * 为ListView填充数据
	 *
	 * @param date
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<SortModel> filledData(List<Information> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		Information info = null;
		for (int i = 0; i < date.size(); i++) {
			info = date.get(i);
			String name = info.getComments();
			if (name == null || name.trim().equals("")) {
				name = info.getName();
			}

			SortModel sortModel = new SortModel();
			sortModel.setInfo(info);
			sortModel.setName(name);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(name);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvGroupMembersBack:
				app.back();
				break;
			// case R.id.llGroupMembersItemTitle:
			//
			// break;
			case R.id.llGroupMembersInfo:
				// pop menu
				PopupUtils.showPopupWindow(this, v, R.layout.group_popup_members,
						new MyPopupInit());
				break;

			case R.id.llSearch:
				// 搜索
				showSearch();
				// isShowSearch = !isShowSearch;

				break;
			case R.id.tvGroupMembersCancel:
			case R.id.vGroupMembersBg:
			case R.id.llGroupMembersSearchBg:
				// llSearch.setVisibility(View.GONE);
				// vBg.setVisibility(View.GONE);
				hideSearch();
				hideSoftInputView();
				Logger.d(TAG, "CLICKED:");
				break;
			case R.id.btGroupMembersSearchClear:
				etSearch.setText("");
				break;
		}
	}

	private void showSearch() {
		// isShowSearch = !isShowSearch;

		// if (isShowSearch) {
		// vBg.setVisibility(View.VISIBLE);
		// vBg.setEnabled(true);
		// vBg.setClickable(true);
		// llSearch.setVisibility(View.VISIBLE);
		// llSearch.setClickable(true);
		// llSearch.setEnabled(true);

		// } else {
		// }
		startSearchAnimation();
	}

	private void startSearchAnimation() {
		// int height = llSearchContainer.getHeight();
		int height = llTitle.getHeight();
		TranslateAnimation trans = new TranslateAnimation(0.0f, 0.0f, 0.0f,
				-height);
		trans.setDuration(400);
		trans.setFillAfter(true);

		final AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
		alpha.setDuration(200);
		alpha.setFillAfter(true);

		llContent.startAnimation(trans);
		trans.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				vBg.setVisibility(View.VISIBLE);
				vBg.setEnabled(true);
				vBg.setClickable(true);
				llSearch.setVisibility(View.VISIBLE);
				llSearch.setClickable(true);
				llSearch.setEnabled(true);

				vBg.startAnimation(alpha);
				llSearch.startAnimation(alpha);
			}
		});

		alpha.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				//invisible后不响应事件
				//llSearchBg.setVisibility(View.INVISIBLE);
				llSearchBg.setVisibility(View.VISIBLE);
			}
		});
	}

	private void hideSearch() {
		llSearch.clearAnimation();
		vBg.clearAnimation();
		llSearch.setVisibility(View.GONE);
		vBg.setVisibility(View.GONE);

		int height = llTitle.getHeight();
		TranslateAnimation trans = new TranslateAnimation(0.0f, 0.0f, -height,
				0.0f);
		trans.setDuration(400);
		trans.setFillAfter(true);

		llContent.startAnimation(trans);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {

	}

	/**
	 * 下弹出群设置菜单
	 *
	 * @author 吴小雄
	 *
	 */
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

	@Override
	protected void onStart() {
		super.onStart();

		// 设置bar高度
		int height = llTitle.getHeight();
		//int bottom = llTitle.getBottom();
		// DisplayMetrics outMetrics = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		// LayoutParams params = (LayoutParams) sbBar.getLayoutParams();
		// //sbBar.getLayoutParams().height = outMetrics.heightPixels - 2 *
		// height;
		// params.height = outMetrics.heightPixels - 2 * height;
		// sbBar.setLayoutParams(params);
		//System.out.println("height:" + height + ",bottom:" + bottom);// 0
		sbBar.setPadding(0, height, 0, height);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// int height = llTitle.getHeight();
		// System.out.println("height:" + height);//0
		// sbBar.setPadding(0, height, 0, height);
	}
}
