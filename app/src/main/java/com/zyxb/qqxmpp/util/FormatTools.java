package com.zyxb.qqxmpp.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class FormatTools {
	private static final FormatTools tools = new FormatTools();

	private FormatTools() {

	}

	public static FormatTools getInstance() {
		return tools;
	}

	public Drawable InputStream2Drawable(InputStream is) {
		Drawable drawable = new BitmapDrawable(is);
		return drawable;

	}

	public Bitmap InputStream2Bitmap(InputStream is) {
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		
		return bitmap;
	}
}
