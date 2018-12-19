package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.TempShowNewResourceInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnIghT on 16-5-10.
 */
public class OrignalShowMoreChildFragment extends ContactsListFragment {
    public static final String TAG = OrignalShowMoreChildFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 2;
    private final static int PAGE_SIZE = 32;
    public static final int SOURCE_TYPE_LATEST=1;
    public static final int SOURCE_TYPE_HOT=2;
    public static final String SOURCE_TYPE="source_type_orignal_show";
    private int sourceType=SOURCE_TYPE_LATEST;
    private String keyword = "";
    private TextView keywordView;
    private boolean shouldShowHeaderView = false;
    private boolean shouldHiddenSearchView = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_original_show_more, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()!=null){
            sourceType = getArguments().getInt(SOURCE_TYPE);
            //头布局显示
            shouldShowHeaderView = getArguments().getBoolean(HappyLearningFragment
                    .SHOULD_SHOW_HEADER_VIEW);
            //隐藏搜索框
            shouldHiddenSearchView = getArguments().getBoolean(HappyLearningFragment
                    .SHOULD_HIDDEN_SEARCH_VIEW);
        }
        initViews();
        loadShowBooks();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadShowBooks() {
        JSONObject jsonObject = new JSONObject();
        try {
            String keyword = this.keywordView.getText().toString().trim();
            if (!keyword.equals(this.keyword)) {
                getCurrAdapterViewHelper().clearData();
                getPageHelper().clear();
            }
            jsonObject.put("keyword", URLEncoder.encode(keyword, "utf-8"));
            jsonObject.put("type", sourceType);
            jsonObject.put("pageIndex", getPageHelper().getFetchingPageIndex());
            jsonObject.put("pageSize", getPageHelper().getPageSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.GET_SHOW_LIST_URL+builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseShowBooks(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
        showLoadingDialog();
    }

    private void parseShowBooks(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("Code");
                    if (code == 0) {
                        getPageHelper().updateTotalCountByJsonString(jsonString);
                        JSONArray jsonArray = jsonObject.optJSONArray("Data");
                        if (jsonArray != null) {
                            List<TempShowNewResourceInfo> datas = JSON.parseArray(
                                    jsonArray.toString(), TempShowNewResourceInfo.class);
                            if (getPageHelper().isFetchingFirstPage()) {
                                getCurrAdapterViewHelper().clearData();
                            }
                            if (datas != null && datas.size() > 0) {
                                List<NewResourceInfo> items = new ArrayList<NewResourceInfo>();
                                for (TempShowNewResourceInfo tempInfo : datas) {
                                    NewResourceInfo item = TempShowNewResourceInfo.pase2NewResourceInfo(tempInfo);
                                    items.add(item);
                                }
                                getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                                if (getCurrAdapterViewHelper().hasData()) {
                                    getCurrAdapterViewHelper().getData().addAll(items);
                                    getCurrAdapterViewHelper().update();
                                } else {
                                    getCurrAdapterViewHelper().setData(items);
                                }
                            }else{
                                if (getPageHelper().isFetchingFirstPage()) {
                                    TipsHelper.showToast(getActivity(),
                                            getString(R.string.no_data));
                                } else {
                                    TipsHelper.showToast(getActivity(),
                                            getString(R.string.no_more_data));
                                }
                                return;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void initViews() {
        initHeaderView();
        getPageHelper().setPageSize(PAGE_SIZE);
        initSearchView();
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        initGridview();
    }

    private void initHeaderView() {
        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null){
            if (shouldShowHeaderView){
                headerView.setVisibility(View.VISIBLE);
            }else {
                headerView.setVisibility(View.GONE);
            }
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null){
            //创意秀
            if (sourceType == SOURCE_TYPE_LATEST){
                textView.setText(getString(R.string.latest));
            }else if (sourceType == SOURCE_TYPE_HOT){
                textView.setText(getString(R.string.recommend));
            }
        }
    }
    private void initSearchView() {
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.input_original_show_hint));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadShowBooks();
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
                    loadShowBooks();
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
                    loadShowBooks();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        //搜索框布局
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            if (shouldHiddenSearchView){
                view.setVisibility(View.GONE);
            }else {
                view.setVisibility(View.VISIBLE);
            }
        }
        //分割线
        View dividerLine = findViewById(R.id.divider_line);
        if (dividerLine != null){
            if (shouldHiddenSearchView){
                dividerLine.setVisibility(View.GONE);
            }else {
                dividerLine.setVisibility(View.VISIBLE);
            }
        }
    }
    private void initGridview(){
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadShowBooks();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        if(data.isMicroCourse()||data.isOnePage()) {
                            ActivityUtils.openPictureDetailActivity(getActivity(), data);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }
}
