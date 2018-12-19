package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;

import java.util.List;

public class SlideDataAdapter<T> extends DataAdapter {

	protected int itemLeftBackViewId;
	protected int itemRightBackViewId;

    public SlideDataAdapter(Context context, List<T> itemList,
                int itemViewLayout, int itemLeftBackViewId, int itemRightBackViewId) {
		super(context, itemList, itemViewLayout);
		this.itemLeftBackViewId= itemLeftBackViewId;
		this.itemRightBackViewId = itemRightBackViewId;
	}

	@Override
	public int getFrontViewId(int position) {
		return this.layout;
	}

	@Override
	public int getLeftBackViewId(int position) {
		return this.itemLeftBackViewId;
	}

	@Override
	public int getRightBackViewId(int position) {
		return this.itemRightBackViewId;
	}

}