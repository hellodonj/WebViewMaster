package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.HomeworkMainFragment;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.PushMessageInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfoResult;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.osastudio.common.library.ActivityStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ======================================================
 * Describe:推送打开资源详情辅助类
 * ======================================================
 */
public class PushOpenResourceHelper {
    private Context context;
    private PushMessageInfo pushMessageInfo;
    private String memberId;

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
        memberId = DemoApplication.getInstance().getMemberId();
        if (TextUtils.isEmpty(memberId)){
            return this;
        }
        openResource();
        return this;
    }

    private void openResource() {
        if (TextUtils.isEmpty(pushMessageInfo.getAirClassId())) {
            //学习任务
            loadClassInfo(false);
        } else {
            //直播
            if (pushMessageInfo.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                loadLiveInfoDetail();
            } else {
                if (pushMessageInfo.isOnlineSchool()) {
                    if (pushMessageInfo.getRole() == RoleType.ROLE_TYPE_PARENT){
                        loadChildInfo();
                    } else {
                        ClassDetailActivity.show(context, pushMessageInfo.getClassId(), true,
                                ActivityStack.getInstance().getCount() == 0,memberId);
                    }
                } else {
                    loadClassInfo(true);
                }
            }
        }
    }

    private void loadLiveInfoDetail() {
        Map<String, Object> params = new HashMap<>();
        params.put("ExtId", pushMessageInfo.getAirClassId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        context, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result != null && result.isSuccess()) {
                            //成功
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                                JSONObject modelObj = jsonObject.getJSONObject("Model");
                                if (modelObj != null){
                                    Emcee emcee = JSONObject.parseObject(modelObj.toString(),Emcee.class);
                                    if (emcee != null){
                                        dealEmceeClassId(emcee);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(context, ServerUrl.GET_ONLINE_ALWAYS_PEOPLE_NUM_BASE_URL,
                params, listener);
    }

    /**
     * 加载家长绑定的孩子信息
     */
    private void loadChildInfo() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ClassId", pushMessageInfo.getClassId());
        params.put("MemberId", memberId);
        RequestHelper.sendPostRequest(context,
                ServerUrl.GET_STUDENT_BY_PARENT_URL, params,
                new RequestHelper.RequestDataResultListener(context,HomeworkChildListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() != null) {
                            HomeworkChildListResult result = (HomeworkChildListResult) getResult();
                            if (result != null){
                                List<StudentMemberInfo> childList = result.getModel().getData();
                                if (childList != null && childList.size() > 0){
                                    String childId = childList.get(0).getMemberId();
                                    //家长打开在线的直播
                                    ClassDetailActivity.show(context, pushMessageInfo.getClassId(), true,
                                            ActivityStack.getInstance().getCount() == 0,childId);
                                }
                            }
                        }
                    }
                });
    }

    private void dealEmceeClassId(Emcee emcee){
        List<EmceeList> emceeLists = emcee.getEmceeList();
        String userClassId = null;
        if (TextUtils.equals(emcee.getAcCreateId(), memberId)){
            //主编
            userClassId = emcee.getClassId();
        }
        if (TextUtils.isEmpty(userClassId)){
            if (emceeLists != null && emceeLists.size() > 0){
                for (int i = 0; i < emceeLists.size(); i++) {
                    EmceeList emceeMember = emceeLists.get(i);
                    String classIds = emceeMember.getClassIds();
                    if (TextUtils.equals(memberId, emceeMember.getMemberId())
                            && !TextUtils.isEmpty(classIds)) {
                        if (classIds.contains(",")) {
                            String[] splitClassArray = classIds.split(",");
                            userClassId = splitClassArray[0];
                        } else {
                            userClassId = classIds;
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(userClassId)){
            pushMessageInfo.setClassId(userClassId);
            if (pushMessageInfo.isOnlineSchool()){
                ClassDetailActivity.show(context, pushMessageInfo.getClassId(), true,
                        ActivityStack.getInstance().getCount() == 0,memberId);
            } else {
                loadClassInfo(true);
            }
        }
    }

    private void loadClassInfo(boolean isLive) {
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", memberId);
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
                        if (isLive) {
                            dealWithLiveDetail(result);
                        } else {
                            dealWithStudyTask(result);
                        }
                    }
                };
        RequestHelper.sendPostRequest(context,
                ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
    }

    private void dealWithLiveDetail(SubscribeClassInfoResult result) {
        //当前界面已经显示在前台
        if (ActivityStack.getInstance().getCount() == 0) {
            enterAirClassroom(result, false);
        } else {
            enterAirClassroom(result, true);
        }
    }

    /**
     * 进入空中课堂的详情界面
     */
    private void enterAirClassroom(SubscribeClassInfoResult result, boolean isApplicationStart) {
        SubscribeClassInfo classInfo = result.getModel().getData();
        if (classInfo == null) return;
        Intent intent = new Intent(context, AirClassroomActivity.class);
        Bundle args = new Bundle();
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putBoolean(AirClassroomActivity.EXTRA_IS_HEADMASTER, classInfo.isHeadMaster());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        SchoolInfo schoolInfo = new SchoolInfo();
        schoolInfo.setSchoolId(classInfo.getSchoolId());
        schoolInfo.setSchoolName(classInfo.getSchoolName());
        args.putSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO, schoolInfo);
        args.putSerializable(AirClassroomActivity.ExTRA_CLASS_INFO, classInfo);
        args.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        args.putBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS, pushMessageInfo.isOnlineSchool());
        args.putBoolean(ActivityUtils.EXTRA_IS_APPLICATION_START, isApplicationStart);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void dealWithStudyTask(SubscribeClassInfoResult result) {
        SubscribeClassInfo classInfo = result.getModel().getData();
        if (classInfo != null) {
            int activityCount = ActivityStack.getInstance().getCount();
            if (activityCount == 0) {
                //被程序杀死
                enterClassStudyTaskDetailActivity(classInfo, false, false);
            } else {
                //当前界面已经显示在前台
                Activity activity = ActivityStack.getInstance().getTop();
                if (activity != null) {
                    if (activity instanceof ClassResourceListActivity) {
                        ClassResourceListActivity listActivity = (ClassResourceListActivity) activity;
                        HomeworkMainFragment homeworkMainFragment = (HomeworkMainFragment) listActivity.getSupportFragmentManager()
                                .findFragmentByTag(HomeworkMainFragment.TAG);
                        enterClassStudyTaskDetailActivity(classInfo, true, homeworkMainFragment != null);
                    } else {
                        enterClassStudyTaskDetailActivity(classInfo, true, false);
                    }
                }
            }
        }
    }

    private void enterClassStudyTaskDetailActivity(SubscribeClassInfo classInfo, boolean
            isApplicationStart, boolean isCurrentView) {
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
        if (isCurrentView) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtras(args);
        context.startActivity(intent);
    }
}
