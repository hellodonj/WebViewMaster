package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyRecycleView extends RecyclerView {

	public MyRecycleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyRecycleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyRecycleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
