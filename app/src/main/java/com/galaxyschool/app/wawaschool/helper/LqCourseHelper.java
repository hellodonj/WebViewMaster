package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.text.TextUtils;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ======================================================
 * Describe:解决mooc交互辅助类
 * ======================================================
 */
public class LqCourseHelper {

    public static void updateLookCourseState(Activity activity,
                                             int resCourseId,
                                             String classId,
                                             String schoolId,
                                             String studentId) {
        updateBuyShopPermission(activity, resCourseId, classId, schoolId, studentId);
    }

    private static void updateBuyShopPermission(Activity activity,
                                                int resCourseId,
                                                String classId,
                                                String schoolId,
                                                String studentId) {
        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", resCourseId);
            if (!TextUtils.isEmpty(classId)) {
                jsonObject.put("classId", classId);
            }
            if (!TextUtils.isEmpty(schoolId)) {
                jsonObject.put("schoolId", schoolId);
            }
            jsonObject.put("token", studentId);
            jsonObject.put("version", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.CHECK_LQCOURSE_SHOP_RESOURCE_PERMISSION + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {

            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public static void updateSourceReadState(Activity activity,
                                             int resCourseId,
                                             String resId,
                                             String memberId) {
        getTaskTypeByChapterId(activity, resCourseId, resId, memberId);
    }

    private static void getTaskTypeByChapterId(Activity activity,
                                               int resCourseId,
                                               String resId,
                                               String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("chapterId", resCourseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestHelper.postRequest(activity, ServerUrl.GET_TASKTYPE_BY_CHAPTERID_BASE_URL,
                jsonObject.toString(), new Listener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)) {
                            return;
                        }
                        com.alibaba.fastjson.JSONObject obj =
                                com.alibaba.fastjson.JSONObject.parseObject(result);
                        if (obj != null) {
                            int code = obj.getIntValue("code");
                            if (code == 0) {
                                int taskType = obj.getIntValue("taskType");
                                if (taskType == 2 || taskType == 5) {
                                    //不调更新阅读状态的接口
                                } else {
                                    updateReadState(activity, resCourseId, resId, memberId);
                                }
                            } else {
                                updateReadState(activity, resCourseId, resId, memberId);
                            }
                        }
                    }
                });
    }

    private static void updateReadState(Activity activity,
                                        int resCourseId,
                                        String resId,
                                        String memberId) {
        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cwareId", resCourseId);
            if (!TextUtils.isEmpty(resId)) {
                if (resId.contains("-")) {
                    resId = resId.split("-")[0];
                }
                jsonObject.put("resId", resId);
            }
            jsonObject.put("token", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.MOOC_SET_READED_BASE_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {

            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }
}
