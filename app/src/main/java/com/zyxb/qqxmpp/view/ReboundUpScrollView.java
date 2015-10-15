package com.zyxb.qqxmpp.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class ReboundUpScrollView extends ScrollView {
	@SuppressWarnings("unused")
	private static final String TAG = "ElasticUpScrollView";

	private static final float MOVE_FACTOR = 0.5f;

	private static final int ANIM_TIME = 300;

	private View contentView;

	private float startY;

	private Rect originalRect = new Rect();

	private boolean canPullUp = false;

	private boolean isMoved = false;

	public ReboundUpScrollView(Context context) {
		super(context);
	}

	public ReboundUpScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			contentView = getChildAt(0);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (contentView == null)
			return;

		originalRect.set(contentView.getLeft(), contentView.getTop(),
				contentView.getRight(), contentView.getBottom());
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (contentView == null) {
			return super.dispatchTouchEvent(ev);
		}
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setVerticalScrollBarEnabled(true);

			canPullUp = isCanPullUp();

			startY = ev.getY();
			break;

		case MotionEvent.ACTION_UP:
			setVerticalScrollBarEnabled(false);
			if (!isMoved)
				break;
			TranslateAnimation anim = new TranslateAnimation(0, 0,
					contentView.getTop(), originalRect.top);
			anim.setDuration(ANIM_TIME);
			contentView.startAnimation(anim);

			contentView.layout(originalRect.left, originalRect.top,
					originalRect.right, originalRect.bottom);

			canPullUp = false;
			isMoved = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (!canPullUp) {
				startY = ev.getY();
				canPullUp = isCanPullUp();
				break;
			}

			float nowY = ev.getY();
			int deltaY = (int) (nowY - startY);

			boolean shouldMove = canPullUp && deltaY < 0;
			if (shouldMove) {
				int offset = (int) (deltaY * MOVE_FACTOR);

				contentView.layout(originalRect.left,
						originalRect.top + offset, originalRect.right,
						originalRect.bottom + offset);
				isMoved = true;
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean isCanPullUp() {
		return contentView.getHeight() <= getHeight() + getScrollY();
	}
}
