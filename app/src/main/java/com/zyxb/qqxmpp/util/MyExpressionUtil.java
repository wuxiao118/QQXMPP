package com.zyxb.qqxmpp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.FaceGVAdapter;
import com.zyxb.qqxmpp.gif.AnimatedGifDrawable;
import com.zyxb.qqxmpp.gif.AnimatedImageSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyExpressionUtil {
	private static Map<String, String> names;
	private static Map<String, String> paths;

	// private static int textOrignHeigt = 0;
	// private static int bmpHeight = 0;
	// private static boolean isFirst = true;//EditText中是否存在表情

	public static SpannableStringBuilder parse(Context mContext, String content) {
		SpannableStringBuilder sb = new SpannableStringBuilder(content);
		String regex = "\\[[^\\]]+\\]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			String tempText = m.group();
			String num = names.get(tempText);
			if (num == null) {
				continue;
			}
			try {
				String png = "p/_" + num + ".png";
				Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets()
						.open(png));
				int rawHeigh = bitmap.getHeight();
				int rawWidth = bitmap.getHeight();
				int newHeight = 20;
				int newWidth = 20;
				// 计算缩放因子
				float heightScale = ((float) newHeight) / rawHeigh;
				float widthScale = ((float) newWidth) / rawWidth;
				// 新建立矩阵
				Matrix matrix = new Matrix();
				matrix.postScale(heightScale, widthScale);
				// 设置图片的旋转角度
				// matrix.postRotate(-30);
				// 设置图片的倾斜
				// matrix.postSkew(0.1f, 0.1f);
				// 将图片大小压缩
				// 压缩后图片的宽和高以及kB大小均会变化
				Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth,
						rawHeigh, matrix, true);

				sb.setSpan(new ImageSpan(mContext, newBitmap), m.start(),
						m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return sb;
	}

	/**
	 * 聊天
	 *
	 * @param mContext
	 * @param gifTextView
	 * @param content
	 * @return
	 */
	public static SpannableStringBuilder prase(Context mContext,
											   final TextView gifTextView, String content) {
		SpannableStringBuilder sb = new SpannableStringBuilder(content);
		String regex = "\\[[^\\]]+\\]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			String tempText = m.group();
			String num = names.get(tempText);
			if (num == null) {
				continue;
			}
			try {
				String gif = "g/" + num + ".gif";
				/**
				 * 如果open这里不抛异常说明存在gif，则显示对应的gif 否则说明gif找不到，则显示png\\[[^\\]]+\\]
				 * */
				InputStream is = mContext.getAssets().open(gif);
				sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,
								new AnimatedGifDrawable.UpdateListener() {
									@Override
									public void update() {
										gifTextView.postInvalidate();
									}
								})), m.start(), m.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				is.close();
			} catch (Exception e) {
				// String png = tempText.substring("[".length(),
				// tempText.length() - "]".length());
				String png = "p/_" + num + ".png";
				try {
					sb.setSpan(
							new ImageSpan(mContext, BitmapFactory
									.decodeStream(mContext.getAssets()
											.open(png))), m.start(), m.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (IOException e1) {
					// e1.printStackTrace();
				}
				// e.printStackTrace();
			}
		}
		return sb;
	}

	/**
	 * 表情对应的span
	 *
	 * @param mContext
	 * @param png
	 * @return
	 */
	public static SpannableStringBuilder getFace(Context mContext, String png,
												 boolean isScale) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		try {
			/**
			 * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
			 * 所以这里对这个tempText值做特殊处理
			 * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
			 * */
			String num = png.substring("p/_".length(),
					png.length() - ".png".length());
			String tempText = paths.get(num);
			// System.out.println("png:" + png + " num:" + num + ":name "
			// + tempText);
			sb.append(tempText);

			// 获取表情高度
			// if (bmpHeight == 0) {
			// Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets()
			// .open(png));
			// bmpHeight = bmp.getHeight();
			// }

			// 缩放图片
			Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets()
					.open(png));
			if (isScale) {
				int rawHeigh = bitmap.getHeight();
				int rawWidth = bitmap.getHeight();
				int newHeight = 40;
				int newWidth = 40;
				// 计算缩放因子
				float heightScale = ((float) newHeight) / rawHeigh;
				float widthScale = ((float) newWidth) / rawWidth;
				// 新建立矩阵
				Matrix matrix = new Matrix();
				matrix.postScale(heightScale, widthScale);
				// 设置图片的旋转角度
				// matrix.postRotate(-30);
				// 设置图片的倾斜
				// matrix.postSkew(0.1f, 0.1f);
				// 将图片大小压缩
				// 压缩后图片的宽和高以及kB大小均会变化
				Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth,
						rawHeigh, matrix, true);
				sb.setSpan(new ImageSpan(mContext, newBitmap), sb.length()
								- tempText.length(), sb.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				sb.setSpan(new ImageSpan(mContext, bitmap), sb.length()
								- tempText.length(), sb.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			// sb.setSpan(
			// new ImageSpan(mContext, BitmapFactory.decodeStream(mContext
			// .getAssets().open(png))),
			// sb.length() - tempText.length(), sb.length(),
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		} catch (Exception e) {
			// e.printStackTrace();
		}

		return sb;
	}

	/**
	 * 向输入框里添加表情
	 * */
	public static void insert(EditText input, CharSequence text) {
		int iCursorStart = Selection.getSelectionStart((input.getText()));
		int iCursorEnd = Selection.getSelectionEnd((input.getText()));
		if (iCursorStart != iCursorEnd) {
			((Editable) input.getText()).replace(iCursorStart, iCursorEnd, "");
		}
		int iCursor = Selection.getSelectionEnd((input.getText()));
		((Editable) input.getText()).insert(iCursor, text);
	}

	/**
	 * 删除图标执行事件
	 * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
	 * */
	public static void delete(EditText input) {
		if (input.getText().length() != 0) {
			int iCursorEnd = Selection.getSelectionEnd(input.getText());
			int iCursorStart = Selection.getSelectionStart(input.getText());
			if (iCursorEnd > 0) {
				if (iCursorEnd == iCursorStart) {
					if (isDeletePng(input, iCursorEnd)) {
						String st = "[大笑]";
						((Editable) input.getText()).delete(
								iCursorEnd - st.length(), iCursorEnd);
					} else {
						((Editable) input.getText()).delete(iCursorEnd - 1,
								iCursorEnd);
					}
				} else {
					((Editable) input.getText()).delete(iCursorStart,
							iCursorEnd);
				}
			}
		}
	}

	/**
	 * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
	 * **/
	public static boolean isDeletePng(EditText input, int cursor) {
		String st = "[大笑]";
		String content = input.getText().toString().substring(0, cursor);
		if (content.length() >= st.length()) {
			String checkStr = content.substring(content.length() - st.length(),
					content.length());
			String regex = "\\[[^\\]]+\\]";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(checkStr);
			return m.matches();
		}
		return false;
	}

	@SuppressLint("InflateParams")
	public static View viewPagerItem(final Context context, int position,
									 List<String> staticFacesList, int columns, int rows,
									 final EditText editText) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.face_gridview, null);
		GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
		/**
		 * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
		 * */
		List<String> subList = new ArrayList<String>();
		subList.addAll(staticFacesList
				.subList(position * (columns * rows - 1),
						(columns * rows - 1) * (position + 1) > staticFacesList
								.size() ? staticFacesList.size() : (columns
								* rows - 1)
								* (position + 1)));
		/**
		 * 末尾添加删除图标
		 * */
		subList.add("_del.png");
		FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, context);
		gridview.setAdapter(mGvAdapter);
		gridview.setNumColumns(columns);
		// 单击表情执行的操作
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				try {
					String png = ((TextView) ((LinearLayout) view)
							.getChildAt(1)).getText().toString();
					if (!png.contains("_del")) {// 如果不是删除图标
						MyExpressionUtil.insert(editText,
								MyExpressionUtil.getFace(context, png, true));

						// 调整edittext高度
						// if(isFirst){
						// isFirst = false;
						// if(textOrignHeigt == 0){
						// textOrignHeigt = editText.getHeight();
						// }
						//
						// editText.setHeight(bmpHeight);
						// float ratio = bmpHeight / textOrignHeigt;
						// Drawable background = editText.getBackground();
						// }

					} else {
						MyExpressionUtil.delete(editText);
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		});

		return gridview;
	}

	/**
	 * 根据表情数量以及GridView设置的行数和列数计算Pager数量
	 *
	 * @return
	 */
	public static int getPagerCount(int listsize, int columns, int rows) {
		return listsize % (columns * rows - 1) == 0 ? listsize
				/ (columns * rows - 1) : listsize / (columns * rows - 1) + 1;
	}

	/**
	 * 根据表情数量以及GridView设置的行数和列数计算Pager数量
	 *
	 * @return
	 */
	public static List<String> initStaticFaces(Context context) {
		List<String> facesList = null;
		try {
			facesList = new ArrayList<String>();
			String[] faces = context.getAssets().list("p");
			// 将Assets中的表情名称转为字符串一一添加进staticFacesList
			for (int i = 0; i < faces.length; i++) {
				facesList.add(faces[i]);
			}
			// 去掉删除图片
			facesList.remove("_del.png");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		// 初始化名字
		initFaceNames(context);
		// System.out.println(names.size());

		return facesList;
	}

	public static void initFaceNames(Context context) {
		try {
			names = new HashMap<String, String>();
			paths = new HashMap<String, String>();
			InputStream is = context.getAssets().open("emoji");
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"utf-8"));

			String line = null;
			while ((line = br.readLine()) != null) {
				String[] info = line.split(",");
				String name = info[1];
				String path = info[0];
				// if(names.containsKey(name)){
				// System.out.println(name + ":" + path + ",与后面重复:" +
				// names.get(name));
				// }
				names.put(name, path);
				paths.put(path, name);
				// System.out.println(name + ":" + path);
			}
			// 增加del
			// names.put("[删除]", "999");

			br.close();
			is.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
}
