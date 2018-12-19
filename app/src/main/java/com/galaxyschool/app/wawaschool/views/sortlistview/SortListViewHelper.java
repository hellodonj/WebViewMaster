package com.galaxyschool.app.wawaschool.views.sortlistview;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.views.sortlistview.SideBar.OnTouchingLetterChangedListener;

import java.util.*;

public abstract class SortListViewHelper extends AdapterViewHelper {

	protected ListView listView;
	protected SideBar sideBar;
	protected ClearEditText searchBar;

	protected List<SortModel> dataList;
	protected SortDataAdapter sortAdapter;

	protected CharacterParser characterParser;
	protected PinyinComparator pinyinComparator;

	public SortListViewHelper(Context context, ListView listView,
							  int itemViewLayout, int itemLetterViewId,
							  SideBar sideBar, TextView tipsView) {
		super(context, listView, itemViewLayout);

        this.sortAdapter = new SortDataAdapter(context, new ArrayList(),
				itemViewLayout, itemLetterViewId);
		this.sortAdapter.setItemViewCreator(this);
		this.dataAdapter = this.sortAdapter;
		this.listView = listView;
		this.sideBar = sideBar;
		if (this.sideBar != null) {
			this.sideBar.setTipsView(tipsView);
		}

		initViews();
	}

	public void showSideBar(boolean show) {
		if (this.sideBar != null) {
			this.sideBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		}
	}

    public void setSearchBar(ClearEditText view) {
        setSearchBar(view, true);
    }

	public void setSearchBar(ClearEditText view, boolean init) {
		this.searchBar = view;
		if (!init) {
			return;
		}
		this.searchBar = view;
		this.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (context instanceof Activity) {
						hideSoftKeyboard((Activity) context);
					}
					return true;
				}
				return false;
			}
		});
		this.searchBar.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
			@Override
			public void onClearClick() {
				if (context instanceof Activity) {
					hideSoftKeyboard((Activity) context);
				}
			}
		});
		this.searchBar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		this.searchBar.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
	}

	@Override
	public void setData(List list, boolean notifyDataSetChanged) {
		this.dataList = list;
		prepareData(list);
		if (list != null && list.size() > 0) {
			Collections.sort(list, this.pinyinComparator);
		}
		super.setData(list, notifyDataSetChanged);
	}

	private void internalUpdate(List list) {
		if (list != null && list.size() > 0) {
			Collections.sort(list, this.pinyinComparator);
		}
		this.sortAdapter.setData(list);
		this.sortAdapter.notifyDataSetChanged();
	}

	private void initViews() {
		this.characterParser = CharacterParser.getInstance();
		this.pinyinComparator = new PinyinComparator();
		
		this.listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Toast.makeText(context, ((SortModel) sortAdapter.getItem(
						position)).getName(), Toast.LENGTH_SHORT).show();
                onItemClick(parent, view, position, id);
			}
		});

		this.sideBar.setOnTouchingLetterChangedListener(
				new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = sortAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});

		this.listView.setAdapter(this.sortAdapter);
	}

	private void prepareData(List<SortModel> list){
		if (list != null && list.size() > 0) {
			Set<String> letterSet = new HashSet<String>();
			boolean hasNumbers = false;
			for (SortModel sortModel : list) {
				String pinyin = sortModel.getSortLetters();
				if (TextUtils.isEmpty(pinyin)) {
					pinyin = this.characterParser.getSpelling(sortModel.getName());
				}
				String sortString = pinyin.substring(0, 1).toUpperCase();

				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
					letterSet.add(sortModel.getSortLetters());
				} else {
//					letterSet.add("#");
					hasNumbers = true;
				}
			}
			List<String> letterList = new ArrayList<String>();
			Iterator<String> iterator = letterSet.iterator();
			while (iterator.hasNext()) {
				letterList.add(iterator.next());
			}
			Collections.sort(letterList);
			if (hasNumbers) {
				letterList.add("#");
			}
			String[] letters = new String[letterList.size()];
			for (int i = 0; i < letterList.size(); i++) {
				letters[i] = letterList.get(i);
			}
			sideBar.setLetters(letters);
		}
	}
	
	private void filterData(String filterStr) {
		if (this.dataList == null || this.dataList.size() <= 0) {
			return;
		}

		List<SortModel> filterDateList = new ArrayList<SortModel>();
		filterStr = filterStr.toLowerCase();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = this.dataList;
		} else {
			filterDateList.clear();
			for(SortModel sortModel : this.dataList) {
				String name = sortModel.getName().toLowerCase();
				if (name.indexOf(filterStr.toString()) != -1
						|| this.characterParser.getSpelling(name).startsWith(
                            filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		internalUpdate(filterDateList);
	}

	public static void showSoftKeyboard(Activity activity) {
		try {
			((InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInputFromInputMethod(activity.getCurrentFocus()
							.getWindowToken(), InputMethodManager.SHOW_FORCED);
		} catch (Exception e) {

		}
	}

	public static void hideSoftKeyboard(Activity activity) {
		try {
			((InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(activity.getCurrentFocus()
							.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}
	}

}
