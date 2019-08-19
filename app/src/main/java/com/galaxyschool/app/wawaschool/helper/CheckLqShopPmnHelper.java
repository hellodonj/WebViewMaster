package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.tool.ApplyActivationHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/8/20 0020 16:29
 * E-Mail Address:863378689@qq.com
 * Describe:检验学程馆课程权限
 * ======================================================
 */
public class CheckLqShopPmnHelper {
    private Activity activity;
    private int roleType = -1;
    private String studentId;
    private String memberId;
    private int resCourseId;
    private String lqCourseChapterName;
    private int fromType = FromType.FROM_STUDYTASK_DETAIL;
    private CallbackListener listener;
    private String classId;
    private String schoolId;
    private boolean isSanxi;
    private String courseId;

    public interface FromType {
        //学习任务详细
        int FROM_STUDYTASK_DETAIL = 0;
        //lq云板发送或者个人资源库发送的任务
        int FROM_LQBLOARD_SEND = 1;
    }

    public CheckLqShopPmnHelper() {

    }

    public CheckLqShopPmnHelper setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public CheckLqShopPmnHelper setRoleType(int roleType) {
        this.roleType = roleType;
        return this;
    }

    public CheckLqShopPmnHelper setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public CheckLqShopPmnHelper setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public CheckLqShopPmnHelper setResCourseId(int resCourseId) {
        this.resCourseId = resCourseId;
        return this;
    }

    public CheckLqShopPmnHelper setCallBackListener(CallbackListener listener) {
        this.listener = listener;
        return this;
    }

    public CheckLqShopPmnHelper setFromType(int fromType) {
        this.fromType = fromType;
        return this;
    }

    public CheckLqShopPmnHelper setClassId(String classId) {
        this.classId = classId;
        return this;
    }

    public CheckLqShopPmnHelper setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        return this;
    }

    public CheckLqShopPmnHelper check() {
        checkLqCourseShopPermission();
        return this;
    }

    public void checkLqCourseShopPermission() {
        if (activity == null) {
            return;
        }
        if (resCourseId <= 0) {
            return;
        }
        if (listener == null){
            return;
        }
        if (TextUtils.isEmpty(memberId)) {
            return;
        }
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            return;
        }
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
            if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(studentId)) {
                jsonObject.put("token", studentId);
            } else {
                jsonObject.put("token", memberId);
            }
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
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                try {
                    JSONObject dataJsonObject = new JSONObject(jsonString);
                    boolean hasPermission = dataJsonObject.optBoolean("permission");
                    lqCourseChapterName = dataJsonObject.getString("courseName");
                    isSanxi = dataJsonObject.optBoolean("isSanxi");
                    courseId = dataJsonObject.getString("courseId");
                    if (fromType == FromType.FROM_STUDYTASK_DETAIL) {
                        listener.onBack(!hasPermission);
                    } else if (fromType == FromType.FROM_LQBLOARD_SEND) {
                        if (hasPermission){
                            listener.onBack(true);
                        } else {
                            popBuyLqCourseShopResource(null, null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public void popBuyLqCourseShopResource(UserInfo stuUserInfo, final CallbackListener listener) {
        String confirmButtonText = activity.getString(R.string.buy_immediately);
        if (isSanxi){
            confirmButtonText = activity.getString(R.string.str_activation);
        }
        String titleText = activity.getString(R.string.buy_course_please);
        if (isSanxi){
            titleText = activity.getString(R.string.str_activation_course_please);
        }
        if (roleType == RoleType.ROLE_TYPE_PARENT) {
            if (!TextUtils.isEmpty(lqCourseChapterName) && stuUserInfo != null) {
                titleText = activity.getString(R.string.str_no_buy_lqCourse_shop_for_parent,
                        lqCourseChapterName, stuUserInfo.getNickName());
                if (isSanxi){
                    titleText = activity.getString(R.string.str_no_activation_lqCourse_shop_for_parent,
                            lqCourseChapterName, stuUserInfo.getNickName());
                }
            }
            confirmButtonText = activity.getString(R.string.str_ok);
        } else {
            if (!TextUtils.isEmpty(lqCourseChapterName)) {
                titleText = activity.getString(R.string.str_no_buy_lqCourse_shop_for_student,
                        lqCourseChapterName);
                if (isSanxi){
                    titleText = activity.getString(R.string.str_no_activation_lqCourse_shop_for_student,
                            lqCourseChapterName);
                }
            }
            if (fromType == FromType.FROM_LQBLOARD_SEND) {
                confirmButtonText = activity.getString(R.string.str_ok);
            }
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                activity,
                null,
                titleText,
                activity.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                confirmButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (fromType == FromType.FROM_STUDYTASK_DETAIL) {
                            if (isSanxi){
                                popActivationPopDialog();
                            } else if (listener != null){
                                listener.onBack(true);
                            }
                        }
                    }
                });
        messageDialog.setMessageGravity(Gravity.START);
        messageDialog.show();
    }

    private void popActivationPopDialog(){
        if (roleType == RoleType.ROLE_TYPE_PARENT && !TextUtils.isEmpty(studentId)) {
            ApplyActivationHelper applyActivationHelper = new ApplyActivationHelper()
                    .setActivity(activity)
                    .setClassId(classId)
                    .setCourseId(courseId)
                    .setSchoolId(schoolId);
            applyActivationHelper.requestActivationPermission();
        }else {
            UIUtil.showToastSafe("请登录学生身份激活");
        }
    }
}
