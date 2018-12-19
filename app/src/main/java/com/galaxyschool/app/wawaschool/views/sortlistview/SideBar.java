package com.galaxyschool.app.wawaschool.views.sortlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;

public class SideBar extends View {

	private static final int DEFAULT_LETTER_HEIGHT = 30;

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

	public String[] letters = { };
//	public String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
//			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
//			"W", "X", "Y", "Z", "#" };

	private int choose = -1;
	private Paint paint = new Paint();

	private TextView tipsView;

	public void setTipsView(TextView tipsView) {
		this.tipsView = tipsView;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBar(Context context) {
		super(context);
	}

	public void setLetters(String[] letters) {
		this.letters = letters;
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int singleHeight = DEFAULT_LETTER_HEIGHT;
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = singleHeight * letters.length; //View.MeasureSpec.getSize(heightMeasureSpec);
		height += 10 * 2;
		setMeasuredDimension(width, height);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		int height = getHeight();
		int width = getWidth();
//		int singleHeight = height / letters.length;
		int singleHeight = DEFAULT_LETTER_HEIGHT;

		for (int i = 0; i < letters.length; i++) {
			paint.setColor(Color.rgb(33, 65, 98));
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(20);
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(letters[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(letters[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// ���y����
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
//		final int c = (int) (y / getHeight() * letters.length);// ���y������ռ�ܸ߶ȵı���*letters����ĳ��Ⱦ͵��ڵ��letters�еĸ���.
		int singleHeight = DEFAULT_LETTER_HEIGHT;
		final int c = (int) (y / singleHeight);

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (tipsView != null) {
				tipsView.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundResource(R.drawable.sidebar_bg);
			if (oldChoose != c) {
				if (c >= 0 && c < letters.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(letters[c]);
					}
					if (tipsView != null) {
						tipsView.setText(letters[c]);
						tipsView.setVisibility(View.VISIBLE);
					}
					
					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}