package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.helper.PushOpenResourceHelper;
import com.galaxyschool.app.wawaschool.pojo.PushMessageInfo;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;

/**
 * ======================================================
 * Describe:厂商通知栏点击打开辅助类
 * ======================================================
 */
public class OpenJPushClickActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOpenClick();
    }

    private void handleOpenClick() {
        String data = null;
        if (getIntent().getData() != null) {
            data = getIntent().getData().toString();
        }
        if (TextUtils.isEmpty(data) && getIntent().getExtras() != null) {
            data = getIntent().getExtras().getString("JMessageExtra");
        }
        if (TextUtils.isEmpty(data)) return;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String msgId = jsonObject.optString("msg_id");
            byte whichPushSDK = (byte) jsonObject.optInt("rom_type");
            String extras = jsonObject.optString("n_extras");
            if (!TextUtils.isEmpty(extras)) {
                PushMessageInfo pushMessageInfo = com.alibaba.fastjson.JSONObject.parseObject(extras, PushMessageInfo.class);
                if (pushMessageInfo != null) {
                    PushOpenResourceHelper.getInstance().setContext(this)
                            .setPushMessageInfo(pushMessageInfo).open();
                }
            }
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
