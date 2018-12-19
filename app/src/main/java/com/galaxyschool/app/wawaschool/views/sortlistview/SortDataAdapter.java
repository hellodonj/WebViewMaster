package com.galaxyschool.app.wawaschool.views.sortlistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;

import java.util.List;

public class SortDataAdapter<T> extends DataAdapter implements SectionIndexer {

	protected int itemLetterViewId;

    public SortDataAdapter(Context context, List<T> itemList,
						   int itemViewLayout, int itemLetterViewId) {
		super(context, itemList, itemViewLayout);
		this.itemLetterViewId= itemLetterViewId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
		if (this.itemLetterViewId > 0) {
			SortModel content = (SortModel) getItem(position);
			TextView letterView = (TextView) view.findViewById(this.itemLetterViewId);
			if (letterView != null) {
				int section = getSectionForPosition(position);
				if (position == getPositionForSection(section)) {
					letterView.setVisibility(View.VISIBLE);
					letterView.setText(content.getSortLetters());
				} else {
					letterView.setVisibility(View.GONE);
				}
			}
		}

		return view;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	public int getSectionForPosition(int position) {
		return ((SortModel) getItem(position)).getSortLetters().charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = ((SortModel) getItem(i)).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

}