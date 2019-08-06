package com.galaxyschool.app.wawaschool.helper;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;
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
public class StatisticNetHelper {

    private CallbackListener callbackListener;
    private Context context = UIUtil.getContext();

    public StatisticNetHelper setCallListener(CallbackListener listener) {
        this.callbackListener = listener;
        return this;
    }

    /**
     * 拉取作业布置率统计
     */
    public void GetCourseResSetRate(int courseId,
                                    String classId) {
        if (TextUtils.isEmpty(classId)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("CourseId", courseId);
        params.put("ClassId", classId);
        RequestHelper.RequestModelResultListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(context, ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (!TextUtils.isEmpty(jsonString)) {
                            JSONObject object = JSON.parseObject(jsonString);
                            int code = object.getIntValue("ErrorCode");
                            if (code == 0) {
                                JSONObject modelObj = object.getJSONObject("Model");
                                //成功
                                if (modelObj != null){
                                    StatisticBean bean = new StatisticBean();
                                    bean.setTotalNum(modelObj.getIntValue("TotalResNum"));
                                    bean.setImportantNum(modelObj.getIntValue("AlreadySetResNum"));
                                    bean.setUnImportantNum(modelObj.getIntValue("NoSetResNum"));
                                    if (callbackListener != null) {
                                        callbackListener.onBack(bean);
                                    }
                                }
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(context, ServerUrl.GET_COURSE_RESSET_RATE_BASE_URL, params, listener);
    }

    /**
     * 拉取作业批改率统计
     */
    public void GetTaskCorrectRate(int courseId,
                                    String classId) {
        if (TextUtils.isEmpty(classId)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("CourseId", courseId);
        params.put("ClassId", classId);
        RequestHelper.RequestModelResultListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(context, ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (!TextUtils.isEmpty(jsonString)) {
                            JSONObject object = JSON.parseObject(jsonString);
                            int code = object.getIntValue("ErrorCode");
                            if (code == 0) {
                                JSONObject modelObj = object.getJSONObject("Model");
                                //成功
                                if (modelObj != null){
                                    StatisticBean bean = new StatisticBean();
                                    bean.setTotalNum(modelObj.getIntValue("TotalNum"));
                                    bean.setImportantNum(modelObj.getIntValue("AlreadyCorrectNum"));
                                    bean.setUnImportantNum(modelObj.getIntValue("NoCorrectNum"));
                                    if (callbackListener != null) {
                                        callbackListener.onBack(bean);
                                    }
                                }
                            }
                        }
                    }
                };
        RequestHelper.sendPostRequest(context, ServerUrl.GET_TASK_CORRECT_RATE_BASE_URL, params, listener);
    }

    /**
     * 拉取作业完成率统计
     */
    public void GetTaskCompleteRate(int courseId,
                                    String classId) {
        if (TextUtils.isEmpty(classId)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("CourseId", courseId);
        params.put("ClassId", classId);
        RequestHelper.RequestModelResultListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(context, ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (!TextUtils.isEmpty(jsonString)) {
                            JSONObject object = JSON.parseObject(jsonString);
                            int code = object.getIntValue("ErrorCode");
                            if (code == 0) {
                                JSONObject modelObj = object.getJSONObject("Model");
                                //成功
                                if (modelObj != null){
                                    StatisticBean bean = new StatisticBean();
                                    bean.setTotalNum(modelObj.getIntValue("NeedCompleteNum"));
                                    bean.setImportantNum(modelObj.getIntValue("CompletedNum"));
                                    bean.setUnImportantNum(modelObj.getIntValue("NotCompletedNum"));
                                    bean.setClassSize(modelObj.getIntValue("ClassSize"));
                                    bean.setAlreadySetTaskNum(modelObj.getIntValue("AlreadySetTaskNum"));
                                    if (callbackListener != null) {
                                        callbackListener.onBack(bean);
                                    }
                                }
                            }
                        }
                    }
                };
        RequestHelper.sendPostRequest(context, ServerUrl.GET_TASK_COMPLETE_RATE_BASE_URL, params, listener);
    }
}
