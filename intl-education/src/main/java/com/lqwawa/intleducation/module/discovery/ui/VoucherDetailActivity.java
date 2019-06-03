package com.lqwawa.intleducation.module.discovery.ui;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.CoinsDetailAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.VoucherDetailAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CoinsDetailInfo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import com.osastudio.apps.BaseFragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class VoucherDetailActivity extends BaseFragmentActivity {


    private TopBar topBar;
    private RecyclerView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_detail);
        topBar = (TopBar) findViewById(R.id.top_bar);

        initView();
        initData();

    }

    private void initData() {
        RequestVo requestVo = new RequestVo();

        UserInfoVo userInfo = UserHelper.getUserInfo();
        requestVo.addParams("memberId", userInfo.getUserId());
        // dataType:1只获取总额  2获取总额及明细
        requestVo.addParams("dataType", 2);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GET_INTEGRAL_RECORD_LIST);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int code = jsonObject.optInt("code");

                if (code == 0) {
                    //请求成功
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    List<CoinsDetailInfo> coinsDetailInfos = JSON.parseArray(jsonArray.toString(), CoinsDetailInfo.class);
                    String integralCount = jsonObject.optString("integralCount", "0");
                    List<CoinsDetailInfo> filterData = new ArrayList<>();
                    for (CoinsDetailInfo info : coinsDetailInfos) {
                        if (info.getRecordType() == 2 && info.getType() == 1) {
                            continue;
                        }
                        filterData.add(info);
                    }
                    VoucherDetailAdapter voucherDetailAdapter =
                            new VoucherDetailAdapter(VoucherDetailActivity.this);
                    listView.setAdapter(voucherDetailAdapter);
                    voucherDetailAdapter.replace(filterData);
                }


            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    private void initView() {

        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.coin_count_detail));


        listView = (RecyclerView) findViewById(R.id.voucher_detail_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

}