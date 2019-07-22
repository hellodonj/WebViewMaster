package com.galaxyschool.app.wawaschool.helper;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * ======================================================
 * Describe:学习任务网络访问帮助类
 * ======================================================
 */
public class StudyTaskNetHelper {
    private CallbackListener callbackListener;

    private Context context = UIUtil.getContext();

    public static StudyTaskNetHelper getInstance() {
        return StudyTaskNetHelperHolder.instance;
    }

    private static class StudyTaskNetHelperHolder {
        private static StudyTaskNetHelper instance = new StudyTaskNetHelper();
    }

    public StudyTaskNetHelper setCallListener(CallbackListener listener) {
        this.callbackListener = listener;
        return this;
    }

    /**
     * 设置学生的作业是否可以被查看
     */
    public void setViewOthersTaskPermission(String taskId,
                                            int viewOthersTaskPermission) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TaskId", Integer.valueOf(taskId));
        params.put("ViewOthersTaskPermisson", viewOthersTaskPermission);
        RequestHelper.RequestModelResultListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(context, ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (TextUtils.isEmpty(jsonString)){
                            return;
                        }
                        JSONObject object = JSON.parseObject(jsonString);
                        int code = object.getIntValue("ErrorCode");
                        if (code == 0){
                            TipMsgHelper.ShowMsg(context, R.string.modify_success);
                            //成功
                            if (callbackListener != null){
                                callbackListener.onBack(true);
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(context, ServerUrl.GET_VIEW_OTHERS_PERMISSION_BASE_URL, params, listener);
    }
}
