package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean3.vo.Expression;

import java.io.IOException;
import java.util.List;

public class MyFaceGVAdapter extends BaseAdapter {
	private List<Expression> list;
	private Context mContext;

	public MyFaceGVAdapter(List<Expression> list, Context mContext) {
		super();
		this.list = list;
		this.mContext = mContext;
	}

	public void clear() {
		this.mContext = null;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHodler hodler;
		if (convertView == null) {
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.face_image, null);
			hodler.iv = (ImageView) convertView.findViewById(R.id.face_img);
			hodler.tv = (TextView) convertView.findViewById(R.id.face_text);
			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		try {
			Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets()
					.open("p/_" + list.get(position).getPath() + ".png"));
			hodler.iv.setImageBitmap(mBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		hodler.tv.setText("p/_" + list.get(position).getPath() + ".png" + ":"
				+ list.get(position).getName());

		return convertView;
	}

	static class ViewHodler {
		ImageView iv;
		TextView tv;
	}
}
