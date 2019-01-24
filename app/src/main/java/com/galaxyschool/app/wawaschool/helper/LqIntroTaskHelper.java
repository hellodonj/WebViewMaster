package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ======================================================
 * Describe:布置任务辅助类
 * ======================================================
 */
public class LqIntroTaskHelper {

    private List<UploadParameter> uploadParameters = new ArrayList<>();
    private boolean answerAtAnyTime = true;//随时作答
    public static LqIntroTaskHelper getInstance() {
        return LqIntroTaskHelperHolder.instantce;
    }

    private static class LqIntroTaskHelperHolder {
        private static LqIntroTaskHelper instantce = new LqIntroTaskHelper();
    }

    public List<UploadParameter> getUploadParameters(){
        return uploadParameters;
    }

    /**
     * 获取当前任务的个数
     *
     * @return
     */
    public int getTaskCount() {
        return uploadParameters.size();
    }

    /**
     * 增加一个新任务到任务列表
     *
     * @param list     增加任务的个数
     * @param taskType 任务的类型 听说课（5） 读写单 8 看课件 9
     */
    public void addTask(ArrayList<SectionResListVo> list, int taskType) {
        if (list == null || list.size() == 0) {
            return;
        }
        String tasktitle = list.get(0).getName();
        if (!TextUtils.isEmpty(tasktitle) && tasktitle.length() > 40) {
            tasktitle = tasktitle.substring(0, 40);
        }
        String startTime = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        Date endDate = DateUtils.getNextDate(new Date());
        String endTime = DateUtils.getDateStr(endDate, "yyyy-MM-dd");
        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setDisContent("");
        uploadParameter.setStartDate(startTime);
        uploadParameter.setDescription("");
        uploadParameter.setEndDate(endTime);
        uploadParameter.setFileName(tasktitle);
        uploadParameter.setTaskType(taskType);
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE || taskType == StudyTaskType.TASK_ORDER) {
            uploadParameter.NeedScore = true;
            uploadParameter.ScoringRule = 2;
        }
        uploadParameter.setLookResDtoList(getLoolResDtoList(list, taskType));
        uploadParameters.add(uploadParameter);
    }

    /**
     * 清空任务的list
     */
    public void clearTaskList() {
        uploadParameters.clear();
        answerAtAnyTime = true;
    }

    /**
     * 进入已添加任务的详情页
     */
    public void enterIntroTaskDetailActivity(Activity activity,
                                             String schoolId,
                                             String classId) {
        if (activity == null || TextUtils.isEmpty(schoolId) || TextUtils.isEmpty(classId)) {
            return;
        }
        ActivityUtils.enterIntroductionCourseActivity(
                activity,
                activity.getString(R.string.str_super_task),
                StudyTaskType.SUPER_TASK,
                null,
                false,
                false,
                classId,
                schoolId,
                null,
                true);
    }

    /**
     * 更新任务listview数据
     */
    public void updateUploadParameters(List<UploadParameter> parameters) {
        if (uploadParameters != null) {
            uploadParameters = parameters;
        }
    }

    private List<LookResDto> getLoolResDtoList(ArrayList<SectionResListVo> list,
                                               int taskType) {
        List<LookResDto> lookResDtoList = new ArrayList<>();
        LookResDto lookResDto = null;
        for (int i = 0; i < list.size(); i++) {
            ResourceInfoTag info = WatchWawaCourseResourceSplicingUtils.transferLQProgramData(list.get(i));
            lookResDto = new LookResDto();
            lookResDto.setResId(info.getResId());
            lookResDto.setResTitle(info.getTitle());
            lookResDto.setAuthor(info.getAuthorId());
            lookResDto.setResUrl(info.getResourcePath());
            lookResDto.setResProperties(info.getResProperties());
            lookResDto.setTaskId(taskType);
            lookResDto.setImgPath(info.getImgPath());
            lookResDto.setSplitInfoList(info.getSplitInfoList());
            lookResDto.setAuthorName(info.getAuthorName());
            lookResDto.setCreateTime(info.getCreateTime());
            lookResDto.setResCourseId(info.getResCourseId());
            lookResDto.setIsSelect(true);
            lookResDto.setPoint(info.getPoint());
            if (!TextUtils.isEmpty(info.getPoint())) {
                lookResDto.setResPropType(1);
            }
            lookResDto.setCompletionMode(info.getCompletionMode());
            lookResDtoList.add(lookResDto);
        }
        return lookResDtoList;
    }

    public void setAnswerAtAnyTime(boolean answerAtAnyTime){
        this.answerAtAnyTime = answerAtAnyTime;
    }

    public boolean getAnswerAtAnyTimeValue(){
        return answerAtAnyTime;
    }
}
