package com.galaxyschool.app.wawaschool.common;

import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
}
