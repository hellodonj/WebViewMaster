package com.lqwawa.intleducation.module.discovery.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.tools.DialogHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by XChen on 2018/1/21.
 * email:man0fchina@foxmail.com
 */

public class CourseDetails {

    private DialogHelper.LoadingDialog mLoadingDialog;
    private boolean isOnlineTeacher;
    private boolean handleOnlineTeacher;

    public CourseDetails(boolean isOnlineTeacher) {
        this.isOnlineTeacher = isOnlineTeacher;
    }

    public CourseDetails() {
    }

    public void checkCourseDetails(Activity activity, String courseId, String memberId, final boolean isLqExcellent, String schoolIds, final OnCheckCourseStatusListener listener) {
        if (!UserHelper.isLogin()) {
            if (listener != null) {
                listener.onCheckSuccess(null, false);
            }
            return;
        }
        if (ButtonUtils.isFastDoubleClick()) {
            return;
        }

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("dataType", 1);
        requestVo.addParams("id", courseId);
        requestVo.addParams("token", UserHelper.getUserId());
        if (isLqExcellent) {//来自LQ精品学程
            requestVo.addParams("schoolIds", schoolIds);
        } else if (UserHelper.isLogin() && TextUtils.equals(memberId, UserHelper.getUserId())) {
            requestVo.addParams("schoolIds", UserHelper.getUserInfo().getSchoolIds());
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        showLoadingDialog(activity);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                hideLoadingDialog();
                CourseDetailsVo courseDetailsVo = JSON.parseObject(s, new TypeReference<CourseDetailsVo>() {
                });
                if (courseDetailsVo.getCode() == 0) {
                    List<CourseVo> voList = courseDetailsVo.getCourse();
                    if (voList != null && voList.size() > 0) {
                        CourseVo courseVo = voList.get(0);
                        boolean isLearnPermission = ((isLqExcellent || courseVo.getPrice() == 0) && courseDetailsVo.isIsJoin()) ||
                                // 过期让进入课程详情 ，由课程详情强制退出
                                (courseVo.getPrice() != 0 && courseDetailsVo.isIsBuy() && !courseDetailsVo.isIsExpire() && courseDetailsVo.isIsJoin());
                        // 没有购买,并且是在线课堂老师
                        boolean isOnlineCounselor = !isLearnPermission && isOnlineTeacher;
                        // 赋值
                        CourseDetails.this.handleOnlineTeacher = isOnlineCounselor;


                        boolean isOwner = UserHelper.checkCourseAuthor(courseVo,isOnlineCounselor);
                        boolean toLearn = isOwner || ((isLqExcellent || courseVo.getPrice() == 0) && courseDetailsVo.isIsJoin()) ||
                                (courseVo.getPrice() != 0 && courseDetailsVo.isIsBuy() && !courseDetailsVo.isIsExpire() && courseDetailsVo.isIsJoin());
                        if (listener != null) {
                            listener.onCheckSuccess(courseDetailsVo, toLearn);
                            return;
                        }
                    }
                }
                if (listener != null) {
                    listener.onCheckFailed(courseDetailsVo.getCode(), "");
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                hideLoadingDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                hideLoadingDialog();
                if (listener != null) {
                    listener.onCheckFailed(-100, "");
                }
            }

            @Override
            public void onFinished() {
                hideLoadingDialog();
            }
        });
    }

    /**
     * 触发学习任务,发送广播,进行数据刷新
     *
     * @param context 上下文对象
     * @param action  事件类型
     */
    public static void courseDetailsTriggerStudyTask(@NonNull Context context, @NonNull String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    public void showLoadingDialog(Activity activity) {
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        }
        mLoadingDialog.setCancelable(false);
    }

    private void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 返回处理过的在线课堂老师与否，如果已经购买或者能参加学习，就是学生身份进去
     * @return
     */
    public boolean getHandleOnlineTeacher() {
        return handleOnlineTeacher;
    }

    public interface OnCheckCourseStatusListener {
        void onCheckSuccess(CourseDetailsVo courseDetailsVo, boolean needToLearn);

        void onCheckFailed(int code, String message);
    }
}
