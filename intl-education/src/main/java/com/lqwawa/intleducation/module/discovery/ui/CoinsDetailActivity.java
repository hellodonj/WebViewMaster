package com.lqwawa.intleducation.module.discovery.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.CoinsDetailAdapter;
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

import java.util.List;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/8 10:59
 * desp: 描 述：余额明细界面
 * ================================================
 */

public class CoinsDetailActivity extends BaseFragmentActivity {


    private TopBar topBar;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coins_detail);
        topBar = (TopBar) findViewById(R.id.top_bar);


        initData();

        initView();

    }

    private void initData() {


        RequestVo requestVo = new RequestVo();

        UserInfoVo userInfo = UserHelper.getUserInfo();
        requestVo.addParams("token", userInfo.getToken());
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GET_COINS_DETAIL + requestVo.getParams());

        x.http().get(params, new Callback.CommonCallback<String>() {
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
                    JSONObject jsonCoins = jsonObject.optJSONObject("data");
                    JSONArray jsonArray = jsonCoins.optJSONArray("rows");
                    List<CoinsDetailInfo> coinsDetailInfos = JSON.parseArray(jsonArray.toString(), CoinsDetailInfo.class);

                    listView.setAdapter(new CoinsDetailAdapter(CoinsDetailActivity.this, coinsDetailInfos));
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


        listView = (ListView) findViewById(R.id.coins_detail_listview);


    }

}
