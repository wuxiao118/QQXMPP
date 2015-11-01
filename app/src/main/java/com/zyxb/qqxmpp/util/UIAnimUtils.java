package com.zyxb.qqxmpp.util;

import android.app.Activity;

import com.zyxb.qqxmpp.R;

public class UIAnimUtils {

	public static void sildLeftIn(Activity act){
		act.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	}
	
	public static void sildRightOut(Activity act){
		act.overridePendingTransition(R.anim.slide_left_out, R.anim.slide_right_out);
	}

	public static void sildBottomIn(Activity act){
		act.overridePendingTransition(R.anim.slide_bottom,R.anim.slide_dark);
	}

	public static void sildTopOut(Activity act){
		act.overridePendingTransition(R.anim.slide_top,R.anim.slide_bright);
	}
}
