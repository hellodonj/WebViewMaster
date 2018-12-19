package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.ArrayList;

public abstract class SlideListViewHelper extends AdapterViewHelper {

	protected SlideListView listView;
	protected SlideDataAdapter slideAdapter;

	public SlideListViewHelper(Context context, SlideListView listView,
               int itemViewLayout, int itemLeftBackViewId, int itemRightBackViewId) {
		super(context, listView, itemViewLayout);

		this.slideAdapter = new SlideDataAdapter(context, new ArrayList(),
				itemViewLayout, itemLeftBackViewId, itemRightBackViewId) {
			@Override
			public SlideListView.SlideMode getSlideModeInPosition(int position) {
				return SlideListViewHelper.this.getSlideModeInPosition(position);
			}
		};
		this.slideAdapter.setItemViewCreator(this);
		this.dataAdapter = this.slideAdapter;
		this.listView = listView;
		if (this.listView != null) {
			this.listView.setAdapter(this.slideAdapter);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return this.slideAdapter.createConvertView(position);
	}

	public SlideListView.SlideMode getSlideModeInPosition(int position) {
		return slideAdapter.getSlideMode();
	}

}
