package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.List;

public class PlatformResourceListBaseFragment extends ContactsListFragment {

    public static final String TAG = PlatformResourceListBaseFragment.class.getSimpleName();

    protected TextView keywordView;
    protected String keyword = "";
    protected NewResourceInfoListResult resourceListResult;
    protected String itemId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resource_list_with_search_bar, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setVisibility(View.INVISIBLE);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadResourceList();
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    loadResourceList();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    loadResourceList();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    loadResourceList();
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        itemId = data.getId();
                    }
                    super.onItemClick(parent,view,position,id);
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    protected void loadResourceList() {
        loadResourceList(keywordView.getText().toString());
    }

    protected void loadResourceList(String keyword) {

    }

    protected void updateResourceListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                resourceListResult = result;
            }
        }
    }

    /**
     * 更新阅读人数
     * @param itemId
     */
    protected void updateReaderNumber(String itemId) {
        if (TextUtils.isEmpty(itemId)){
            return;
        }
        AdapterViewHelper helper = getCurrAdapterViewHelper();
        if (helper != null && helper.hasData()){
            List<NewResourceInfo> infoList = helper.getData();
            if (infoList != null && infoList.size() > 0){
                for (NewResourceInfo info : infoList){
                    if (info != null){
                        String id = info.getId();
                        if (!TextUtils.isEmpty(id) && id.equals(itemId)){
                            //找到刚才点击的那个条目(id是唯一的，position不靠谱)，增加阅读数。
                            info.setReadNumber(info.getReadNumber() + 1);
                            break;
                        }
                    }
                }
            }
            //更新一下布局
            helper.update();
        }
    }

}
