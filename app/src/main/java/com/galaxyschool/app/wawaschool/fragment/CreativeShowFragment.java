package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.OriginalShowMoreChildActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.TempShowNewResourceInfo;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 * 创意秀调整页面
 */
public class CreativeShowFragment extends ContactsListFragment {
    public static final String TAG = CreativeShowFragment.class.getSimpleName();
    private static final int MAX_PIC_BOOKS_PER_ROW = 2;
    private static final int MAX_SPLIT_COUNT = 4;
    private MyGridView latestGridView;
    private MyGridView recommendGridView;
    private boolean shouldHiddenHeaderView = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_creative_show, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadViews();
    }

    private void loadViews() {
         loadDatas();
    }

    private void loadDatas() {
        getPageHelper().clear();
        loadLatestShowBooks();
        loadRecommendShowBooks();
    }

    /**
     * 拉推荐
     */
    private void loadRecommendShowBooks() {
        loadShowBooks(OrignalShowMoreChildFragment.SOURCE_TYPE_HOT,recommendGridView);
    }

    /**
     * 拉最新
     */
    private void loadLatestShowBooks() {
        loadShowBooks(OrignalShowMoreChildFragment.SOURCE_TYPE_LATEST,latestGridView);
    }

    private void initViews() {
        if (getArguments()!=null){
            //头布局隐藏
            shouldHiddenHeaderView = getArguments().getBoolean(HappyLearningFragment
                    .SHOULD_HIDDEN_HEADER_VIEW);
        }
        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null){
            if (shouldHiddenHeaderView){
                headerView.setVisibility(View.GONE);
            }else {
                headerView.setVisibility(View.VISIBLE);
            }
        }
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.INVISIBLE);
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.hint_show_book);
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        initGridView();
        intTabTitle(R.id.latest_more_layout,getString(R.string.latest));
        intTabTitle(R.id.recommend_more_layout,getString(R.string.recommend));
    }

    /**
     * 初始化每个tab,包含初始化标题。
     * @param layoutId
     * @param title
     */
    private void intTabTitle(int layoutId,String title) {
        View layout = findViewById(layoutId);
        if (layout != null){
            layout.setOnClickListener(this);
            TextView titleTextView = (TextView) layout.findViewById(R.id.title_text_view);
            if (titleTextView != null){
                titleTextView.setText(title);
            }
        }
    }

    private void initGridView() {
        latestGridView = (MyGridView) findViewById(R.id.latest_gridview);
        initShowBooksGridView(latestGridView);
        recommendGridView = (MyGridView) findViewById(R.id.recommend_gridview);
        initShowBooksGridView(recommendGridView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.latest_more_layout:
                enterMoreShowBookEvent(OrignalShowMoreChildFragment.SOURCE_TYPE_LATEST);
                break;

            case R.id.recommend_more_layout:
                enterMoreShowBookEvent(OrignalShowMoreChildFragment.SOURCE_TYPE_HOT);
                break;
        }
    }

    private void enterMoreShowBookEvent(int sourceType) {
        Intent intent = new Intent(getActivity(), OriginalShowMoreChildActivity.class);
        intent.putExtra(HappyLearningFragment.SHOULD_SHOW_HEADER_VIEW,true);
        //显示搜索框
        intent.putExtra(HappyLearningFragment.SHOULD_HIDDEN_SEARCH_VIEW,false);
        intent.putExtra(OrignalShowMoreChildFragment.SOURCE_TYPE,sourceType);
        startActivity(intent);
    }

    private void initShowBooksGridView(GridView gridView) {
        if (gridView != null) {
            gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadDatas();
                }
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        if(data.isMicroCourse()||data.isOnePage()){
                        ActivityUtils.openPictureDetailActivity(getActivity(), data);
                    }
                    }
                }
            };
            AdapterViewHelper helper = getCurrAdapterViewHelper();
            if (helper == null){
                //防止刷新崩溃
                setCurrAdapterViewHelper(gridView,adapterViewHelper);
            }
            addAdapterViewHelper(String.valueOf(gridView.getId()), adapterViewHelper);
        }
    }

    private void loadShowBooks(int sourceType, final GridView gridView) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", URLEncoder.encode("", "utf-8"));
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
                Request.Method.GET, ServerUrl.GET_SHOW_LIST_URL+builder.toString(),
                new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseShowBooks(jsonString,getAdapterViewHelper(String.valueOf(gridView.getId())));
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
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseShowBooks(String jsonString,AdapterViewHelper helper) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("Code");
                    if (code == 0) {
                        getPageHelper().updateTotalCountByJsonString(jsonString);
                        JSONArray jsonArray = jsonObject.optJSONArray("Data");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            List<TempShowNewResourceInfo> datas = JSON.parseArray(
                                    jsonArray.toString(), TempShowNewResourceInfo.class);
                            if (getPageHelper().isFetchingFirstPage()) {
                                if (helper != null) {
                                    helper.clearData();
                                }
                            }
                            if (datas != null && datas.size() > 0) {
                                List<NewResourceInfo> items = new ArrayList<NewResourceInfo>();
                                for (TempShowNewResourceInfo tempInfo : datas) {
                                    NewResourceInfo item = TempShowNewResourceInfo.pase2NewResourceInfo(tempInfo);
                                    items.add(item);
                                }
                                getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                                //截取4个
                                if (items != null && items.size() >= MAX_SPLIT_COUNT){
                                    items = items.subList(0,MAX_SPLIT_COUNT);
                                }
                                if (helper != null) {
                                    if (helper.hasData()) {
                                        helper.getData().addAll(items);
                                        getCurrAdapterViewHelper().update();
                                    } else {
                                        helper.setData(items);
                                    }
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
}
