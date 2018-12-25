package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.client.pojo.ResourceInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/6 0006 14:10
 * Describe:学习任务相关的操作
 * ======================================================
 */
public class StudyTaskUtils {
    /**
     * 学习任务发送班级的选择
     *
     * @param taskParams
     * @param data
     */
    public static void handleSchoolClassData(JSONObject taskParams, List<ShortSchoolClassInfo> data) {
        List<ShortSchoolClassInfo> groupClassList = new ArrayList<>();
        List<ShortSchoolClassInfo> schoolClassList = new ArrayList<>();
        for (int i = 0, len = data.size(); i < len; i++) {
            ShortSchoolClassInfo info = data.get(i);
            if (TextUtils.isEmpty(info.getGroupId())) {
                schoolClassList.add(info);
            } else {
                groupClassList.add(info);
            }
        }
        try {
            if (schoolClassList.size() > 0) {
                JSONArray schoolArray = new JSONArray();
                JSONObject schoolObject = null;
                for (int i = 0; i < schoolClassList.size(); i++) {
                    schoolObject = new JSONObject();
                    ShortSchoolClassInfo schoolClassInfo = schoolClassList.get(i);
                    schoolObject.put("ClassName", schoolClassInfo.getClassName());
                    schoolObject.put("ClassId", schoolClassInfo.getClassId());
                    schoolObject.put("SchoolName", schoolClassInfo.getSchoolName());
                    schoolObject.put("SchoolId", schoolClassInfo.getSchoolId());
                    schoolArray.put(schoolObject);
                }
                taskParams.put("SchoolClassList", schoolArray);
            }

            if (groupClassList.size() > 0) {
                JSONArray groupArray = new JSONArray();
                JSONObject groupObject = null;
                for (int i = 0; i < groupClassList.size(); i++) {
                    groupObject = new JSONObject();
                    ShortSchoolClassInfo groupData = groupClassList.get(i);
                    groupObject.put("GroupId", groupData.getGroupId());
                    groupObject.put("SchoolName", groupData.getSchoolName());
                    groupObject.put("SchoolId", groupData.getSchoolId());
                    groupArray.put(groupObject);
                }
                taskParams.put("SchoolStudyGroupList", groupArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 百分制转换成十分分制
     * @param score 十分制分数
     * @return 百分制
     */
    public static String percentTransformTenLevel(int score) {
        if (score >= 96 && score <= 100) {
            return "A+";
        } else if (score >= 90 && score <= 95) {
            return "A";
        } else if (score >= 85 && score <= 89) {
            return "A-";
        } else if (score >= 80 && score <= 84) {
            return "B+";
        } else if (score >= 75 && score <= 79) {
            return "B";
        } else if (score >= 70 && score <= 74) {
            return "B-";
        } else if (score >= 67 && score <= 69) {
            return "C+";
        } else if (score >= 63 && score <= 66) {
            return "C";
        } else if (score >= 60 && score <= 62) {
            return "C-";
        } else {
            return "D";
        }
    }

    public static String getPicResourceData(List<ResourceInfo> resourceInfos,
                                            boolean isUrl,
                                            boolean isAuthorId,
                                            boolean isResId) {
        if (resourceInfos != null && resourceInfos.size() > 0) {
            String resUrl = "";
            String authorId = "";
            String resId = "";
            for (int i = 0; i < resourceInfos.size(); i++) {
                ResourceInfo info = resourceInfos.get(i);
                if (i == 0) {
                    resUrl = info.getResourcePath();
                    authorId = info.getAuthorId();
                    resId = info.getResId();
                } else {
                    resUrl = resUrl + "," + info.getResourcePath();
                    authorId = authorId + "," + info.getAuthorId();
                    resId = resId + "," + info.getResId();
                }
            }
            if (isUrl) {
                return resUrl;
            } else if (isAuthorId) {
                return authorId;
            } else if (isResId) {
                return resId;
            }
        }
        return "";
    }

    public static void setTaskFinishBackgroundDetail(Activity activity,
                                                     @Nullable TextView finishView,
                                                     int finishCount,
                                                     int totalCount){
        finishView.setTextColor(ContextCompat.getColor(activity, R.color.text_white));
        finishView.setTextSize(10);
        finishView.setPadding(
                DensityUtils.dp2px(activity,5),
                DensityUtils.dp2px(activity,3),
                DensityUtils.dp2px(activity,5),
                DensityUtils.dp2px(activity,3));
        if (finishCount == 0){
            //一个未做
            finishView.setBackground(ContextCompat.getDrawable(activity,R.drawable
                    .shape_corner_red_10_dp));
        } else if (finishCount == totalCount){
            //全部做完
            finishView.setBackground(ContextCompat.getDrawable(activity,R.drawable
                    .shape_corner_green_10_dp));
        } else {
            //
            finishView.setBackground(ContextCompat.getDrawable(activity,R.drawable
                    .shape_corner_yellow_10_dp));
        }
    }

    public static boolean compareStudyTaskTime(String dataTime,String targetTime,boolean containEqual){
        int result = DateUtils.compareDate(dataTime, targetTime, DateUtils.DATE_PATTERN_yyyy_MM_dd);
        if (result == 1 || (result == 0 && containEqual)){
            return true;
        }
        return false;
    }

    public static SpannableString getCommitTaskTitle(Activity activity,
                                                     String content,
                                                     String commitTime,
                                                     String endTime){
        boolean isOverTime = StudyTaskUtils.compareStudyTaskTime(commitTime,endTime,false);
        SpannableString systemColorString = null;
        if (isOverTime){
            String makeUpString  = activity.getString(R.string.str_make_up_study_task);
            String systemContent = makeUpString + content;
            systemColorString = new SpannableString(systemContent);
            systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color
                            .red))
                    , 0, makeUpString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return systemColorString;
        } else {
            systemColorString =  new SpannableString(content);
        }
        return systemColorString;
    }

    public static void addMultipleTaskParams(JSONObject taskParams, List<LookResDto> lookResDtos){
        try {
            if (lookResDtos != null && lookResDtos.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (int j = 0; j < lookResDtos.size(); j++) {
                    JSONObject thirdObject = new JSONObject();
                    LookResDto lookDto = lookResDtos.get(j);
                    String resUrl = lookDto.getResUrl();
                    String resId = lookDto.getResId();
                    String authorId = lookDto.getAuthor();
                    List<ResourceInfo> splitInfo = lookDto.getSplitInfoList();
                    if (splitInfo != null && splitInfo.size() > 0) {
                        resUrl = StudyTaskUtils.getPicResourceData(splitInfo, true,
                                false, false);
                        resId = StudyTaskUtils.getPicResourceData(splitInfo, false,
                                false, true);
                        authorId = StudyTaskUtils.getPicResourceData(splitInfo, false,
                                true, false);
                    }
                    thirdObject.put("ResTitle", lookDto.getResTitle() == null ? "" : lookDto.getResTitle());
                    thirdObject.put("ResUrl", resUrl);
                    thirdObject.put("ResId", resId);
                    thirdObject.put("ResAuthor", authorId == null ? "" : authorId);
                    //学程馆资源的id
                    thirdObject.put("ResCourseId", lookDto.getResCourseId());
                    thirdObject.put("ResPropType", lookDto.getResPropType());
                    jsonArray.put(thirdObject);
                }
                taskParams.put("TSDXResList",jsonArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
