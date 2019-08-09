package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;
import com.galaxyschool.app.wawaschool.pojo.StatisticBeanListResult;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static List<Integer> getColorsList(){
        List<Integer> colors = new ArrayList<>();
        //优秀
        colors.add(Color.parseColor("#89c73d"));
        //良好
        colors.add(Color.parseColor("#64bfdc"));
        //及格
        colors.add(Color.parseColor("#fbe956"));
        //不及格
        colors.add(Color.parseColor("#e24d45"));
        //未批阅
        colors.add(Color.parseColor("#7f7f7f"));
        //未完成
        colors.add(Color.parseColor("#cdcdcd"));
        return colors;
    }

    public static List<StatisticBean> getColorValueData(Activity activity){
        List<StatisticBean> list = new ArrayList<>();
        List<Integer> colors = getColorsList();
        String [] itemDataArray = activity.getResources().getStringArray(R.array.array_grade_detail);
        for (int i = 0; i < itemDataArray.length; i++){
            StatisticBean bean = new StatisticBean();
            bean.setColor(colors.get(i));
            bean.setTitle(itemDataArray[i]);
            list.add(bean);
        }
        //未批阅
        StatisticBean bean = new StatisticBean();
        bean.setColor(colors.get(4));
        bean.setTitle(activity.getString(R.string.str_un_mark));
        list.add(bean);

        //未完成
        bean = new StatisticBean();
        bean.setColor(colors.get(5));
        bean.setTitle(activity.getString(R.string.str_un_complete));
        list.add(bean);
        return list;
    }

    public static int[] getProgressColorsValue(StatisticBean bean, boolean isWeight){
        int total = bean.getTaskTotalNum();
        if (total == 0){
            return null;
        }
        List<Integer> allColors = getColorsList();
        ArrayList<Integer> colors = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        int percent = 0;
        if (bean.getExcellentNum() > 0){
            percent = bean.getExcellentNum() * 100 / total;
            if (percent > 0) {
                colors.add(allColors.get(0));
                weights.add(percent);
            }
        }
        if (bean.getGoodNum() > 0){
            percent = bean.getGoodNum()  * 100 / total;
            if (percent > 0) {
                colors.add(allColors.get(1));
                weights.add(percent);
            }
        }
        if (bean.getFairNum() > 0){
            percent = bean.getFairNum()  * 100 / total;
            if (percent > 0) {
                colors.add(allColors.get(2));
                weights.add(percent);
            }
        }
        if (bean.getFailNum() > 0){
            percent = bean.getFailNum()  * 100 / total;
            if (percent > 0) {
                colors.add(allColors.get(3));
                weights.add(percent);
            }
        }
        if (bean.getNoCorrectNum() > 0){
            percent = bean.getNoCorrectNum()  * 100 / total;
            if (percent > 0) {
                colors.add(allColors.get(4));
                weights.add(percent);
            }
        }
        if (bean.getNotCompletedNum() > 0){
            percent = bean.getNotCompletedNum()  * 100 / total;
            if (percent > 0) {
                colors.add(allColors.get(5));
                weights.add(percent);
            }
        }
        if (weights.size() > 1){
            int percentCount = 0;
            for (int j = 0; j < weights.size() - 1; j++){
                percentCount = weights.get(j) + percentCount;
            }
            weights.set(weights.size() - 1,100 - percentCount);
        }
        int [] array = new int[colors.size()];
        if (isWeight){
            for (int i = 0;i < weights.size() ;i++){
                array[i] = weights.get(i);
            }
        } else {
            for (int i = 0;i < colors.size() ;i++){
                array[i] = colors.get(i);
            }
        }
        return array;
    }

    /**
     * 拉取作业布置率统计
     */
    public void getCourseResSetRate(int courseId,
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
    public void getTaskCorrectRate(int courseId,
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
    public void getTaskCompleteRate(int courseId,
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

    /**
     * 拉取详细学习成绩统计
     */
    public void getTaskDetailStatistic(int courseId,
                                    String classId,
                                    int courseTaskType,
                                    String studentId) {
        if (TextUtils.isEmpty(classId)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("CourseId", courseId);
        params.put("ClassId", classId);
        params.put("CourseTaskType",courseTaskType);
        params.put("StudentId",studentId);
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<StatisticBeanListResult>(context, StatisticBeanListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        StatisticBeanListResult result = getResult();
                        if (result == null || !result.isSuccess() || result.getModel() == null){
                            return;
                        }
                        if (callbackListener != null){
                            callbackListener.onBack(result);
                        }
                    }
                };
        RequestHelper.sendPostRequest(context, ServerUrl.GET_TASK_DETAIL_STATIS_BASE_URL, params, listener);
    }
}
