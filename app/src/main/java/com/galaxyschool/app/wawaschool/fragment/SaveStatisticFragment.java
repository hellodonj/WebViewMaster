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

import java.text.DecimalFormat;
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
        long personalSize = getSpaceLongSize(info.getTotalKb_Personal());
        long postBarSize = getSpaceLongSize(info.getTotalKb_PostBar());
        long performSize = getSpaceLongSize(info.getTotalKb_Perform());
        long totalSize = personalSize + postBarSize + performSize;
        totalSizeView.setText(getString(R.string.str_save_total_size,getSpaceSize(totalSize)));
        float personalPercent = personalSize / (float)totalSize;
        float postBarPercent =  postBarSize / (float)totalSize;
        float performPercent = 0f;
        if (performSize > 0){
            performPercent = 1 - personalPercent - postBarPercent;
        }
        if (totalSize > 0) {
            float finalPerformPercent = performPercent;
            pieView.postDelayed(() -> {
                totalSizeView.setVisibility(View.VISIBLE);
                loadingContentView.setVisibility(View.GONE);
                ArrayList<PieHelper> pieHelpers = new ArrayList<PieHelper>();
                pieHelpers.add(new PieHelper(100 * personalPercent, getSpaceSize(personalSize),
                        Color.parseColor(
                                "#75c905")));
                pieHelpers.add(new PieHelper(100 * postBarPercent, " \n" + getSpaceSize(postBarSize),
                        Color.parseColor(
                                "#38c2e0")));
                pieHelpers.add(new PieHelper(100 * finalPerformPercent, getSpaceSize(performSize),
                        Color.parseColor(
                                "#fe9f22")));
                pieView.setDate(pieHelpers);
            }, 10);
        } else {
            loadingContentView.setText(getString(R.string.str_save_statistic_unuser));
        }
    }

    public long getSpaceLongSize(long kbSize){
        if (kbSize <= 1024 && kbSize > 0){
            return 1;
        }
        long remainder = kbSize % 1014;
        kbSize = kbSize / 1024;
        if (remainder > 0){
            kbSize = kbSize + 1;
        }
        return kbSize;
    }

    public String getSpaceSize(long mSize){
        if (mSize <= 0){
            return " ";
        } else if (mSize < 1024){
            return String.valueOf(mSize) + "M";
        }
        float gSize = mSize / (float)1024;
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(gSize) + "G";
    }
}
