package com.galaxyschool.app.wawaschool.views.sortlistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;

import java.util.List;

public abstract class SortExpandDataAdapter<T> extends ExpandDataAdapter
            implements SectionIndexer {

	protected int itemLetterViewId;

    public SortExpandDataAdapter(Context context, List<T> itemList,
                int groupViewLayout, int childViewLayout, int itemLetterViewId) {
		super(context, itemList, groupViewLayout, childViewLayout);
		this.itemLetterViewId= itemLetterViewId;
	}
	
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
		View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
		if (this.itemLetterViewId > 0) {
			SortModel content = (SortModel) getGroup(groupPosition);
			TextView letterView = (TextView) view.findViewById(this.itemLetterViewId);
			if (letterView != null) {
				int section = getSectionForPosition(groupPosition);
				if (groupPosition == getPositionForSection(section)) {
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
		return ((SortModel) getGroup(position)).getSortLetters().charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getGroupCount(); i++) {
			String sortStr = ((SortModel) getGroup(i)).getSortLetters();
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