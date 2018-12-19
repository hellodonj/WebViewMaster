package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.common.NocHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.NocLePlayHelper;
import com.galaxyschool.app.wawaschool.pojo.NocEnterDetailArguments;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class NocWorksMoreFragment extends ContactsListFragment {

    public static final String TAG = NocWorksMoreFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 2;
    String title;
    private int groupType;
    private TextView keywordView;
    String keyword = "";
    private PullToRefreshView pullToRefreshView;
    public   static boolean freshData=true;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_works_more, null);
    }
    private void getIntent(){
        if (getArguments()!=null){
            title=getArguments().getString("title");
            groupType=getArguments().getInt("groupType");
        }
    }
    private void initViews() {
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(title);
        pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadDatas();
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
                    getPageHelper().clear();
                    loadDatas();
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
                    loadDatas();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        initGridView();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        freshData=true;
        getIntent();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(freshData){
            freshData=false;
            refreshData();
        }
    }

    private void refreshData(){
        getPageHelper().clear();
        loadDatas();
        pullToRefreshView.showRefresh();
    }

    private void initGridView() {
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
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
                       NocHelper.prepareEnterNocDetail(data,getActivity());
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }


    private void loadDatas() {
        JSONObject jsonObject = new JSONObject();
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        try {
            jsonObject.put("name", URLEncoder.encode(keyword, "utf-8"));
            jsonObject.put("groupType", groupType);
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
                Request.Method.GET, ServerUrl.GET_NOC_PUBLIC_WORK + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseData(jsonString);
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
                pullToRefreshView.hideRefresh();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseData(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("Code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray == null || jsonArray.length() <= 0) {
                            if (getPageHelper().isFetchingFirstPage()) {
                                TipsHelper.showToast(getActivity(),
                                        getString(R.string.no_data));
                                if(getCurrAdapterViewHelper().hasData()){
                                    getCurrAdapterViewHelper().clearData();
                                    getPageHelper().clear();
                                }
                            } else {
                                TipsHelper.showToast(getActivity(),
                                        getString(R.string.no_more_data));
                            }
                            return;
                        }
                        if (getPageHelper().isFetchingFirstPage()) {
                            if(getCurrAdapterViewHelper().hasData()){
                                getCurrAdapterViewHelper().clearData();
                                getPageHelper().clear();
                            }
                        }
                        getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                        List<NewResourceInfo> list=new ArrayList<NewResourceInfo>();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object =(JSONObject)jsonArray.get(i);
                            NewResourceInfo item=  NocHelper.pase2NewResourceInfo(object);
                            list.add(item);
                        }
                        if(!getPageHelper().isFetchingFirstPage()){
                            getPageHelper().setCurrPageIndex( getPageHelper().getFetchingPageIndex());
                        }
                        if (getCurrAdapterViewHelper().hasData()) {
                            int position = getCurrAdapterViewHelper().getData().size();
                            if (position > 0) {
                                position--;
                            }
                            getCurrAdapterViewHelper().getData().addAll(list);
                            getCurrAdapterView().setSelection(position);
                        } else {
                            getCurrAdapterViewHelper().setData(list);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH) {
                Bundle bundle = data.getExtras();
                if (bundle.getBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH)){
                    pageHelper.setFetchingPageIndex(0);
                    freshData = true;
                }
            }
        }
    }
}



