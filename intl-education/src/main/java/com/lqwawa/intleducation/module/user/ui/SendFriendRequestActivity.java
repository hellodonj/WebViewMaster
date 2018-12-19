package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class SendFriendRequestActivity extends MyBaseActivity implements
        View.OnClickListener {
    public static final int Rc_sendFriendRequest = 1008;
    private static String TAG = "SendFriendRequestActivity";
    private TopBar topBar;
    private ContainsEmojiEditText editTextValidationInfo;
    private String id;

    public static void start(Activity activity, String id){
        activity.startActivity(new Intent(activity, SendFriendRequestActivity.class)
                .putExtra("id", id));
    }

    public static void startForResult(Activity activity, String id){
        activity.startActivityForResult(new Intent(activity, SendFriendRequestActivity.class)
                .putExtra("id", id), Rc_sendFriendRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friend_request);
        topBar  = (TopBar)findViewById(R.id.top_bar);
        editTextValidationInfo = (ContainsEmojiEditText)findViewById(R.id.validation_info_et);
        id = getIntent().getStringExtra("id");
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.add_new_friend));
        topBar.setRightFunctionText1(getResources().getString(R.string.send),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doSend();
                    }
                });
        topBar.setRightFunctionText1TextColor(getResources().getColor(R.color.com_text_green));
        editTextValidationInfo.setHint(getResources().getString(R.string.i_am)
        + UserHelper.getUserInfo().getUserName() + "," +
                getResources().getString(R.string.want_to_make_friend));
    }

    @Override
    public void onClick(View view) {

    }

    private void doSend() {
        showProgressDialog(getResources().getString(R.string.loading));
        final RequestVo requestVo = new RequestVo();
        String content = editTextValidationInfo.getText().toString();
        if (content.equals("")){
            content = editTextValidationInfo.getHint().toString();
        }
        content = content.trim();
        requestVo.addParams("friendId", id);
        requestVo.addParams("content", content);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.SendFriendRequest + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.send_friend_request)
                                    + activity.getResources().getString(R.string.success));
                    finish();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                LogUtil.d("Test", "处理好友请求失败:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
