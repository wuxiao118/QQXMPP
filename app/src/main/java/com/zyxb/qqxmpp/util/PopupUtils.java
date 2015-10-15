package com.zyxb.qqxmpp.util;

import com.zyxb.qqxmpp.view.ActionItem;
import com.zyxb.qqxmpp.view.QuickAction;
import com.zyxb.qqxmpp.view.QuickAction.OnActionItemClickListener;
import com.zyxb.qqxmpp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class PopupUtils {

	@SuppressLint("InflateParams")
	public static void showPopupWindow(Context context, View parent, int resId,OnPopupLayoutView listener) {
		listener.beforeLoad();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(resId, null);
		PopupWindow popWindow = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		//initPop(view);
		listener.afterLoad(popWindow,view);

		popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}
	
	public interface OnPopupLayoutView{
		void beforeLoad();
		void afterLoad(PopupWindow popupWindow,View view);
	}

	/**
	 * @param context
	 * @param parent
	 * @param items
	 */
	@SuppressLint("InflateParams")
	public static void showPop(Context context, View parent, String[] items,
			int x, int y) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.popup_horizontal, null);

		// view.findViewById(R.id.scroller);
		LinearLayout containter = (LinearLayout) view.findViewById(R.id.tracks);
		TextView tv;
		View v;
		for (int i = 0; i < items.length; i++) {
			tv = new TextView(context);
			tv.setText(items[i]);
			tv.setTextColor(context.getResources().getColor(
					android.R.color.white));
			containter.addView(tv);
			tv = null;

			if (i != items.length - 1) {
				v = new View(context);
				ScrollView.LayoutParams params = new ScrollView.LayoutParams(1,
						LayoutParams.WRAP_CONTENT);
				params.topMargin = 5;
				params.bottomMargin = 5;
				params.leftMargin = 5;
				params.rightMargin = 5;
				v.setLayoutParams(params);
				v.setBackgroundColor(context.getResources().getColor(
						R.color.setting_dark_text_color));
				containter.addView(v);
				v = null;
			}
		}

		PopupWindow popWindow = new PopupWindow(view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.showAtLocation(parent, Gravity.CENTER, x, y);
	}

	public static void showQuickAction(Context context, View view,
			String[] items, OnActionItemClickListener listener) {
		QuickAction quickAction = new QuickAction(context,
				QuickAction.HORIZONTAL);
		for (int i = 0; i < items.length; i++) {
			quickAction.addActionItem(new ActionItem(0, items[i]));
		}
		quickAction.setOnActionItemClickListener(listener);

		quickAction.show(view);
		quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	}

}
