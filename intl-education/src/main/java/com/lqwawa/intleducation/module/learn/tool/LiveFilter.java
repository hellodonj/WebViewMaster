package com.lqwawa.intleducation.module.learn.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.module.learn.ui.LiveFilterListActivity;

/**
 * Created by XChen on 2018/1/16.
 * email:man0fchina@foxmail.com
 */

public class LiveFilter {
    public enum LiveFilterFromType{
        Type_my_join_live,
        Type_my_host_live,
        Type_my_child_live,
        Type_air_classroom
    }

    public static final String KEY_LIVE_FILTER_FROM_TYPE = "LiveFilterFromType";
    public static final String KEY_CHILD_MEMBER_ID = "ChildMemberId";
    public static final String KEY_CHILD_NAME = "childName";
    public static final String KEY_START_TIME_BGEIN = "StartTimeBegin";
    public static final String KEY_START_TIME_END = "StartTimeEnd";
    public static final String KEY_END_TIME_BGEIN = "EndTimeBegin";
    public static final String KEY_END_TIME_END = "EndTimeEnd";
    public static final String KEY_LIVE_TYPE = "LiveType";

    /**
     * 跳转到直播筛选界面
     * @param activity
     * @param fromType 删选哪里的直播列表
     * @param childMemberId 孩子的MemberId 仅在fromType == Type_my_child_live 有效
     */
    public static void startLiveFilter(Activity activity,
                                       LiveFilterFromType fromType,
                                       String childMemberId,
                                       String childName){
        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(),
                "com.lqwawa.mooc.modle.MyLive.LiveFilterActivity");
        Bundle args = new Bundle();
        args.putSerializable(KEY_LIVE_FILTER_FROM_TYPE, fromType);
        args.putString(KEY_CHILD_MEMBER_ID, childMemberId);
        args.putString(KEY_CHILD_NAME, childName);
        args.putInt("fromType", 1);
        args.putInt("picker_model", 1);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    /**
     * 获取筛选条件后跳转到筛选后的直播列表
     * @param activity
     * @param args
     * @param startTimeBegin
     * @param startTimeEnd
     * @param endTimeBegin
     * @param endTimeEnd
     * @param liveType
     */
    public static void jumpToFilterLiveList(Activity activity,
                                            Bundle args,
                                            String startTimeBegin,
                                            String startTimeEnd,
                                            String endTimeBegin,
                                            String endTimeEnd,
                                            int liveType){
        Intent intent = new Intent(activity, LiveFilterListActivity.class);
        args.putString(KEY_START_TIME_BGEIN, startTimeBegin);
        args.putString(KEY_START_TIME_END, startTimeEnd);
        args.putString(KEY_END_TIME_BGEIN, endTimeBegin);
        args.putString(KEY_END_TIME_END, endTimeEnd);
        args.putInt(KEY_LIVE_TYPE, liveType);
        intent.putExtras(args);
        activity.startActivity(intent);
    }
}
