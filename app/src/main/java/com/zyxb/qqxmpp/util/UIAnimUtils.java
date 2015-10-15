package com.zyxb.qqxmpp.util;

import com.zyxb.qqxmpp.R;

import android.app.Activity;

public class UIAnimUtils {

	public static void sildLeftIn(Activity act){
		act.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	}
	
	public static void sildRightOut(Activity act){
		act.overridePendingTransition(R.anim.slide_left_out, R.anim.slide_right_out);
	}
}
