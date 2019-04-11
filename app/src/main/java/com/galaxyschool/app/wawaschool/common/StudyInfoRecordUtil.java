package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CampusOnline;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.oosic.apps.aidl.CollectParams;
import com.osastudio.common.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * ======================================================
 * Created by : Brave_Qu on 2017/12/4 14:37
 * E-Mail Address:863378689@qq.com
 * Describe:学习信息情况的统计工具类
 * ======================================================
 */

public class StudyInfoRecordUtil {

    private Activity activity;
    private int currentModel = 1;
    private CollectParams collectParams;
    private int recordTime;
    private String currentSchoolId;
    private PrefsManager prefsManager;
    private UserInfo userInfo;
    private int recordType;
    private CourseData courseData;
    private int originalTotalTime;
    private Emcee onlineRes;
    private CampusOnline campusOnline;
    private int sourceType;

    /**
     * @return StudyInfoRecordUtil的实例
     */
    public static StudyInfoRecordUtil getInstance() {
        return StudyInfoRecordUtilHolder.instance;
    }

    /**
     * @param activity
     * @return this
     */
    public StudyInfoRecordUtil setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * @param currentModel 当前选中的model
     * @return
     */
    public StudyInfoRecordUtil setCurrentModel(int currentModel) {
        this.currentModel = currentModel;
        return this;
    }

    /**
     * @param collectParams 存储着send数据
     * @return
     */
    public StudyInfoRecordUtil setCollectParams(CollectParams collectParams) {
        this.collectParams = collectParams;
        return this;
    }

    /**
     * @param recordTime 观看的时间
     * @return
     */
    public StudyInfoRecordUtil setRecordTime(int recordTime) {
        this.recordTime = recordTime;
        return this;
    }

    /**
     * @param recordType 记录时间的类型 type 观看（1） 复述（2） 提交的读写单（3）
     * @return
     */
    public StudyInfoRecordUtil setRecordType(int recordType) {
        this.recordType = recordType;
        return this;
    }

    /**
     * 用户的信息
     *
     * @param userInfo
     * @return
     */
    public StudyInfoRecordUtil setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * 复述课件过来封装的数据
     *
     * @param courseData
     * @return
     */
    public StudyInfoRecordUtil setCourseData(CourseData courseData) {
        this.courseData = courseData;
        return this;
    }

    /**
     * 老师布置的原课件时长
     *
     * @param originalTotalTime
     * @return
     */
    public StudyInfoRecordUtil setOriginalTotalTime(int originalTotalTime) {
        this.originalTotalTime = originalTotalTime;
        return this;
    }

    /**
     * 空中课堂资源类
     *
     * @param onlineRes
     * @return
     */
    public StudyInfoRecordUtil setOnlineRes(Emcee onlineRes) {
        this.onlineRes = onlineRes;
        return this;
    }

    /**
     * 校园直播台的数据
     *
     * @param campusOnline
     * @return
     */
    public StudyInfoRecordUtil setCampusOnline(CampusOnline campusOnline) {
        this.campusOnline = campusOnline;
        return this;
    }
    /**
     * @param sourceType source来自哪个模块
     * @return this
     */
    public StudyInfoRecordUtil setSourceType(int  sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    /**
     * 实例唯一
     */
    private static class StudyInfoRecordUtilHolder {
        private static StudyInfoRecordUtil instance = new StudyInfoRecordUtil();
    }

    public interface RecordModel {
        //学习资源的model
        int STUDY_MODEL = 1;
        //直播资源的model
        int ONLINE_MODEL = 2;

    }

    /**
     * 清楚之前保存的数据
     */
    public StudyInfoRecordUtil clearData() {
        activity = null;
        collectParams = null;
        userInfo = null;
        courseData = null;
        onlineRes = null;
        onlineRes = null;
        campusOnline = null;
        originalTotalTime = 0;
        recordTime = 0;
        sourceType = 0;
        return this;
    }

    public void send() {
        //获取当前的schoolId
        if (prefsManager == null) {
            prefsManager = DemoApplication.getInstance().getPrefsManager();
        }
        currentSchoolId = prefsManager.getLatestSchool(UIUtil.getContext(), userInfo.getMemberId());
        if (currentModel == RecordModel.STUDY_MODEL) {
            sendStudyInfoData();
        } else if (currentModel == RecordModel.ONLINE_MODEL) {
            sendOnlineInfoData();
        }
    }

    /**
     * 发送微课资源统计的数据到Server
     */
    private void sendStudyInfoData() {
        Map<String, Object> parms = new HashMap<>();
        //非必填
        parms.put("SchoolId", currentSchoolId);
        //必填
        parms.put("MemberId", userInfo.getMemberId());
        //非必填
        parms.put("RealName", userInfo.getRealName());
        //必填 统计的类型 观看的听说课（1）复述的课件(2) 提交的读写单(3)
        parms.put("Type", recordType);
        //资源来自模块类型
        parms.put("SourceType", sourceType);
        //必填 resId-type
        if (collectParams == null) {
            parms.put("ResId", courseData.getIdType());
            //必填
            parms.put("ResTitle", courseData.nickname);
            //必填
            parms.put("ResThumbnail", AppSettings.getFileUrl(courseData.imgurl));
            //必填 资源的时长
            parms.put("ResDuration", (originalTotalTime + 500) / 1000);
            //type == 2 必填 录制的时长
            parms.put("RecordDuration", (courseData.totaltime + 500) / 1000);
        } else {
            parms.put("ResId", collectParams.getMicroId() + "-" + collectParams.getResourceType());
            //必填
            parms.put("ResTitle", collectParams.getTitle());
            //必填
            parms.put("ResThumbnail", AppSettings.getFileUrl(collectParams.getThumbnail()));
            //必填 资源的时长
            parms.put("ResDuration", (collectParams.getTotalTime() + 500) / 1000);
            //type == 2 必填 录制的时长
            parms.put("RecordDuration", 0);
        }
        //必填 观看的时长
        parms.put("ViewDuration", recordTime);
        parms.put("ClientTypeStr", Utils.getApplicationStamp(UIUtil.getContext()));
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(UIUtil.getContext(), ModelResult.class){
            @Override
            public void onSuccess(String jsonString) {
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(UIUtil.getContext(), ServerUrl.ADD_RESOURCE_INFO_RECORD_BASE_URL, parms, listener);
    }

    /**
     * 发送直播统计的数据到Server
     */
    private void sendOnlineInfoData() {
        Map<String, Object> parms = new HashMap<>();
        parms.put("SchoolId", currentSchoolId);
        parms.put("MemberId", userInfo.getMemberId());
        parms.put("RealName", userInfo.getRealName());
        //type 空中课堂(1) 两栖蛙蛙电视台(2) 慕课直播(3)
        parms.put("Type", recordType);
        //资源来自模块类型
        parms.put("SourceType", sourceType);
        if (onlineRes != null) {
            parms.put("PrimaryKey", onlineRes.getId());
            parms.put("Title", onlineRes.getTitle());
            parms.put("LiveOrigin", onlineRes.getSchoolName());
            parms.put("Emcee", onlineRes.getAcCreateRealName());
            parms.put("CoverUrl", AppSettings.getFileUrl(onlineRes.getCoverUrl()));
        } else {
            parms.put("PrimaryKey", campusOnline.getLink());
            parms.put("Title", campusOnline.getLname());
            parms.put("CoverUrl", AppSettings.getFileUrl(campusOnline.getLimg()));
        }
        parms.put("ViewDuration", recordTime);
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(UIUtil.getContext(), ModelResult.class){
            @Override
            public void onSuccess(String jsonString) {
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(UIUtil.getContext(), ServerUrl.ADD_ONLINE_INFO_RECORD_BASE_URL, parms, listener);
    }
}
