package com.zyxb.qqxmpp.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class FaceVPAdapter extends PagerAdapter {

	private List<View> views;

	public FaceVPAdapter(List<View> views) {
		this.views = views;
	}

	@Override
	public void destroyItem(View parent, int position, Object arg2) {
		((ViewPager) parent).removeView(views.get(position));
	}

	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}

		return 0;
	}

	@Override
	public Object instantiateItem(View parent, int position) {
		((ViewPager) parent).addView(views.get(position), 0);

		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

}