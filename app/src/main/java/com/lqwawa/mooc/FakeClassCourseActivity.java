package com.lqwawa.mooc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.AirClassroomDetailActivity;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassResourceData;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author: wangchao
 * @date: 2019/06/26
 * @desc:
 */
public class FakeClassCourseActivity extends ClassCourseActivity {
    private Emcee onlineRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle args = getIntent().getExtras().getBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK);
            onlineRes = (Emcee) args.getSerializable(ActivityUtils.EXTRA_DATA_INFO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (TextUtils.equals(messageEvent.getUpdateAction(), MessageEventConstantUtils.SEND_HOME_WORK_LIB_SUCCESS)) {
            //作业发送成功
            if (onlineRes != null) {
                ActivityUtil.finishToActivity(AirClassroomDetailActivity.class, false);
            } else {
                ActivityUtil.finishToActivity(ClassResourceListActivity.class, false);
            }
        }
    }

    /**
     * 班级学程页面的入口, 选择学习任务的入口
     *
     * @param activity 上下文对象
     * @param params   核心参数
     * @param data     选择学习任务的筛选
     * @param extras   直播参数
     */
    public static void show(@NonNull Activity activity,
                            @NonNull ClassCourseParams params,
                            @NonNull ClassResourceData data,
                            @Nullable Bundle extras) {
        Intent intent = new Intent(activity, FakeClassCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        bundle.putBoolean(KEY_EXTRA_RESOURCE_FLAG, true);
        bundle.putSerializable(KEY_EXTRA_RESOURCE_DATA, data);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK, extras);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, data.getRequestCode());
    }
}
