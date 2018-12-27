package com.galaxyschool.app.wawaschool.helper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.HomeworkMainFragment;
import com.galaxyschool.app.wawaschool.pojo.PushMessageInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfoResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.osastudio.common.library.ActivityStack;
import java.util.HashMap;
import java.util.Map;

/**
 * ======================================================
 * Describe:推送打开资源详情辅助类
 * ======================================================
 */
public class PushOpenResourceHelper {
    private Context context;
    private PushMessageInfo pushMessageInfo;

    public static PushOpenResourceHelper getInstance() {
        return PushOpenResourceHelperHolder.instance;
    }

    /**
     * 实力唯一
     */
    private static class PushOpenResourceHelperHolder {
        private static PushOpenResourceHelper instance = new PushOpenResourceHelper();
    }

    public PushOpenResourceHelper setContext(Context context) {
        this.context = context;
        return this;
    }

    public PushOpenResourceHelper setPushMessageInfo(PushMessageInfo messageInfo) {
        this.pushMessageInfo = messageInfo;
        return this;
    }

    public PushOpenResourceHelper open() {
        openResource();
        return this;
    }

    private void openResource() {
        if (TextUtils.isEmpty(pushMessageInfo.getAirClassId())) {
            //学习任务
            loadClassInfo();
        } else {
            //直播
        }
    }

    private void loadClassInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", DemoApplication.getInstance().getMemberId());
        params.put("ClassId", pushMessageInfo.getClassId());
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<SubscribeClassInfoResult>(
                        context, SubscribeClassInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        SubscribeClassInfoResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateClassInfo(result);
                    }
                };
        RequestHelper.sendPostRequest(context,
                ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
    }

    private void updateClassInfo(SubscribeClassInfoResult result) {
        SubscribeClassInfo classInfo = result.getModel().getData();
        if (classInfo != null) {
            int activityCount = ActivityStack.getInstance().getCount();
            if (activityCount == 0) {
                //被程序杀死
                enterClassStudyTaskDetailActivity(classInfo, false);
            } else {
                //当前界面已经显示在前台
                ClassResourceListActivity activity = (ClassResourceListActivity) ActivityStack.getInstance().getTop();
                if (activity != null) {
                    HomeworkMainFragment homeworkMainFragment = (HomeworkMainFragment) activity.getSupportFragmentManager()
                            .findFragmentByTag(HomeworkMainFragment.TAG);
                    if (homeworkMainFragment != null && homeworkMainFragment.isVisible()) {
                        //当前界面不做处理
                    } else {
                        enterClassStudyTaskDetailActivity(classInfo, true);
                    }
                }
            }
        }
    }

    private void enterClassStudyTaskDetailActivity(SubscribeClassInfo classInfo, boolean
            isApplicationStart) {
        Bundle args = new Bundle();
        args.putString(ClassResourceListActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        args.putString(ClassResourceListActivity.EXTRA_SCHOOL_ID, classInfo.getSchoolId());
        args.putInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE, ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK);
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putInt(ClassResourceListActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HEAD_MASTER, classInfo.isHeadMaster());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HISTORY, classInfo.isHistory());
        args.putBoolean(ClassResourceListActivity.EXTRA_CLASSINFO_TEMP_TYPE_DATA, classInfo.isTempData());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_ONLINE_SCHOOL_CLASS, pushMessageInfo.isOnlineSchool());
        args.putBoolean(ActivityUtils.EXTRA_IS_APPLICATION_START, isApplicationStart);
        Intent intent = new Intent(context, ClassResourceListActivity.class);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
