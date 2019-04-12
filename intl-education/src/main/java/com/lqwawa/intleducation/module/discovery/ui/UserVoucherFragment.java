package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class UserVoucherFragment extends MyBaseFragment {

    private TextView voucherAmount;
    private TextView detailBtn;
    private TextView voucherExplain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_voucher, container, false);
        voucherAmount = (TextView) view.findViewById(R.id.voucher_amount);
        // 文字说明可滑动
        voucherExplain = (TextView) view.findViewById(R.id.voucher_explain);
        voucherExplain.setMovementMethod(new ScrollingMovementMethod());
        detailBtn = (TextView) view.findViewById(R.id.detail_btn);
        detailBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), VoucherDetailActivity.class)));
        detailBtn.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initData() {
        RequestVo requestVo = new RequestVo();

        UserInfoVo userInfo = UserHelper.getUserInfo();
        requestVo.addParams("memberId", userInfo.getUserId());
        // dataType:1只获取总额  2获取总额及明细
        requestVo.addParams("dataType", 1);
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
                    String integralCount = jsonObject.optString("integralCount", "0");
                    voucherAmount.setText(integralCount);
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
}
