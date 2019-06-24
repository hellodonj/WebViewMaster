package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.StorageInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.charts.PieHelper;
import com.lqwawa.apps.views.charts.PieView;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveStatisticFragment extends ContactsListFragment {

    private PieView pieView;
    private TextView totalSizeView;
    private TextView loadingContentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_save_statistic, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_layout);
        toolbarTopView.getTitleView().setText(getString(R.string.str_save_statistic));
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getBackView().setOnClickListener(v -> finish());
        pieView = (PieView) findViewById(R.id.pie_view);
        totalSizeView = (TextView) findViewById(R.id.tv_total_size);
        loadingContentView = (TextView) findViewById(R.id.tv_loading_default_content);
        loadingContentView.setVisibility(View.VISIBLE);
        pieView.postDelayed(() -> {
            ArrayList<PieHelper> pieHelpers = new ArrayList<PieHelper>();
            pieHelpers.add(new PieHelper(100, "  ",Color.parseColor(
                    "#bcbcbc")));
            pieView.setDate(pieHelpers);
        },10);
    }

    private void loadData(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", getMemeberId());
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener<ModelResult>(getContext(), ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()
                        || getResult().getModel() == null) {
                    return;
                }
                JSONObject obj = JSONObject.parseObject(jsonString);
                JSONObject modeObj = obj.getJSONObject("Model");
                if (modeObj != null){
                    StorageInfo info = JSONObject.parseObject(modeObj.toString(),StorageInfo.class);
                    if (info != null){
                        loadPieData(info);
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getContext(), ServerUrl.GET_MEMBER_STORAGE__BASE_URL, params, listener);
    }

    private void loadPieData(StorageInfo info) {
        totalSizeView.setText(getString(R.string.str_save_total_size,getSpaceSize(info.getTotalKb_All())));
        totalSizeView.setVisibility(View.VISIBLE);
        loadingContentView.setVisibility(View.GONE);
        pieView.postDelayed(() -> {
            float personalPercent =  (info.getTotalKb_Personal() / (float)info.getTotalKb_All());
            float postBarPercent =  (info.getTotalKb_PostBar() / (float)info.getTotalKb_All());
            float performPercent = 1 - personalPercent - postBarPercent;
            ArrayList<PieHelper> pieHelpers = new ArrayList<PieHelper>();
            pieHelpers.add(new PieHelper(100 * personalPercent, getSpaceSize(info.getTotalKb_Personal()),
                    Color.parseColor(
                    "#75c905")));
            pieHelpers.add(new PieHelper(100 * postBarPercent, getSpaceSize(info.getTotalKb_PostBar()),
                    Color.parseColor(
                    "#38c2e0")));
            pieHelpers.add(new PieHelper(100 * performPercent, getSpaceSize(info.getTotalKb_Perform()),
                    Color.parseColor(
                    "#fe9f22")));
            pieView.setDate(pieHelpers);
        }, 10);
    }

    public String getSpaceSize(long kbSize){
        kbSize = kbSize / 1024;
        return String.valueOf(kbSize) + "M";
    }
}
