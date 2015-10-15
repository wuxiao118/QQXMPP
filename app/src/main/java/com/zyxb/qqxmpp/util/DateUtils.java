package com.zyxb.qqxmpp.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	@SuppressLint("SimpleDateFormat")
	public static String format(Date d,String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(d);
	}
	
	public static String format(long d,String pattern){
		return format(new Date(d),pattern);
	}
	
	public static String getMsgDate(Date d){
		Calendar current = Calendar.getInstance();
		int year = current.get(Calendar.YEAR);
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);
		current.set(year, month, day, 0, 0, 0);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		int cyear = calendar.get(Calendar.YEAR);
		
		if(cyear < year){
			return format(d,"yyyy-MM-dd");
		}
		
		if(current.after(calendar)){
			return format(d,"MM-dd");
		}
		
		return format(d,"HH-mm");
	}
	
	public static String getMsgDate(long d){
		return getMsgDate(new Date(d));
	}
}
