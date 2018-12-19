package com.lqwawa.intleducation.module.learn.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.detail.base.plan.ClassPlanFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * Created by XChen on 2017/11/27.
 * email:man0fchina@foxmail.com
 */

public class LiveDetails {
    public static final String KEY_IS_FROM_MY_LIVE = "isFromMyLive";
    public static final int MOOC_LIVE = 1000;
    public static void jumpToLiveDetails(Activity activity, String memberId, LiveVo vo, boolean showTopBar, boolean isFromMyLive){
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.galaxyschool.app.wawaschool.AirClassroomDetailActivity");
        intent.putExtra(KEY_IS_FROM_MY_LIVE, isFromMyLive);
        Bundle bundle = new Bundle();
        bundle.putSerializable("emeccBean", vo);
        bundle.putString("memberId", memberId);
        bundle.putBoolean("isMooc", true);
        if (showTopBar) {
            bundle.putBoolean("showBtn", true);
        }
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, MOOC_LIVE);
    }
    public static void jumpToLiveDetails(Activity activity, LiveVo vo, boolean isHost, boolean showTopBar, boolean isFromMyLive){
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.galaxyschool.app.wawaschool.AirClassroomDetailActivity");
        intent.putExtra(KEY_IS_FROM_MY_LIVE, isFromMyLive);
        Bundle bundle = new Bundle();
        bundle.putSerializable("emeccBean", vo);
        bundle.putBoolean("isMooc", true);
        bundle.putBoolean("isFromCourse", false);
        bundle.putBoolean("isHost", isHost);
        if (showTopBar) {

            bundle.putBoolean("showBtn", true);
        }
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, MOOC_LIVE);
    }

    public static void jumpToLiveDetailsFromCourse(Activity activity, LiveVo vo, String memberId, boolean isFromMyLive){
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.galaxyschool.app.wawaschool.AirClassroomDetailActivity");
        intent.putExtra(KEY_IS_FROM_MY_LIVE, isFromMyLive);
        Bundle bundle = new Bundle();
        bundle.putSerializable("isFromCourse",true);
        bundle.putSerializable("emeccBean", vo);
        bundle.putBoolean("isMooc", true);
        if(StringUtils.isValidString(memberId) && UserHelper.isLogin()
                && !TextUtils.equals(UserHelper.getUserId(), memberId)) {
            bundle.putString("memberId", memberId);
        }
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, MOOC_LIVE);
    }

    public static void jumpToAirClassroomLiveDetails(Activity activity, LiveVo vo, String memberId){
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.galaxyschool.app.wawaschool.AirClassroomDetailActivity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("emeccBean", vo);
        bundle.putBoolean("isMooc", true);
        bundle.putBoolean("isAirClassRoomLive", true);
        bundle.putString("memberId", memberId);
        if(UserHelper.isLogin()) {
            // 不能使用该角色
            bundle.putString("Roles", UserHelper.getUserInfo().getRoles());
        }
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, MOOC_LIVE);
    }

    /**
     * 在线课堂进入空中课堂入口
     * @param activity
     * @param vo
     * @param memberId
     * @param role
     * @param isOnline 是不是在线课堂数据
     * @param isHeadMaster 是否是班主任
     * @param isFromMyLive 是否从我的直播进来的
     * @param result 是否已经加入该直播
     * @param isMyCourseChildOnline 孩子的在线班级true
     * @param isGiveFinish (结束授课的班级)
     * @param isGiveHistory 是否是历史班级
     * @param showSource 是否显示来源
     */
    public static void jumpToAirClassroomLiveDetails(Activity activity,
                                                     LiveVo vo,
                                                     String memberId,
                                                     String role,
                                                     boolean isOnline,
                                                     boolean isHeadMaster,
                                                     boolean isFromMyLive,
                                                     boolean result,
                                                     String classId,
                                                     boolean isMyCourseChildOnline,
                                                     boolean isGiveFinish,
                                                     boolean isGiveHistory,
                                                     boolean showSource){
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.galaxyschool.app.wawaschool.AirClassroomDetailActivity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("emeccBean", vo);
        bundle.putBoolean("isMooc", true);
        bundle.putBoolean("isAirClassRoomLive", true);
        bundle.putString("memberId", memberId);
        bundle.putBoolean("isOnline",isOnline);
        bundle.putBoolean("isHeadMaster",isHeadMaster);
        bundle.putBoolean("isFromMyLive",isFromMyLive);
        bundle.putString("role",role);
        bundle.putString("classId",classId);
        bundle.putBoolean("result",result);
        bundle.putBoolean("isMyCourseChildOnline",isMyCourseChildOnline);
        bundle.putBoolean("is_histroy_class",isGiveHistory);
        bundle.putBoolean("is_finish_lecture",isGiveFinish);
        bundle.putBoolean("display_source_data",showSource);
        if(UserHelper.isLogin()) {
            bundle.putString("Roles", role);
        }
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, MOOC_LIVE);
    }


    /**
     * @author mrmedici
     * @desc 跳转至空中学校创建直播
     */
    public static void jumpToCreateLive(@NonNull Context context,
                                        @NonNull String schoolId,
                                        @NonNull String schoolName,
                                        @NonNull String className,
                                        @NonNull String classMailId,
                                        @NonNull String classId){
        Intent intent = new Intent();
        intent.setClassName(context.getPackageName(),"com.galaxyschool.app.wawaschool" + ".OpenCourseHelpActivity");
        Bundle args = new Bundle();
        args.putBoolean("isCreateOnline",true);
        //来自授课计划
        args.putBoolean("isFromTeachingPlan",true);
        //schoolId
        args.putString("schoolId",schoolId);
        //classId
        args.putString("classId",classId);
        //groupId(班级通讯录id-classMailId)
        args.putString("id",classMailId);
        //schoolName
        args.putString("schoolName",schoolName);
        //className
        args.putString("className",className);
        intent.putExtras(args);
        context.startActivity(intent);
    }
}
