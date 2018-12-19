package com.libs.yilib.componets.list;

import com.lqwawa.apps.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

/**
 * @author 作者 shouyi
 * @version 创建时间：Oct 6, 2015 5:23:28 PM 类说明
 */
public abstract class LBaseGridFragment extends LBaseListFragment {
	public static final String COLUMNS = "Columns";
	protected GridView mGridView;

	@Override
	protected View getView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.common_gridview, null);
	}

	protected void initViews() {
		mGridView = (GridView) getView().findViewById(R.id.gridview);
		mGridView.setAdapter(mAdapter);
		Bundle b = getArguments();
		if (b != null) {
			int columns = b.getInt(COLUMNS, 0);
			if (columns > 0) {
				mGridView.setNumColumns(columns);
			}
		}
	}
}
