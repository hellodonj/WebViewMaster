package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckReplaceIPAddressHelper;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.PlaybackActivityPhone;
import com.galaxyschool.app.wawaschool.fragment.SelectedReadingDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceActivity;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceParams;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ======================================================
 * Describe: 申请批阅辅助类
 * ======================================================
 */
public class ApplyMarkHelper {

    public static void showApplyMarkView(Context mContext,
                                         TextView textView) {
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.str_apply_mark));
            textView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable
                    .green_10dp_red));
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            textView.setTextSize(14);
            textView.setPadding(DensityUtils.dp2px(mContext, 7),
                    DensityUtils.dp2px(mContext, 2),
                    DensityUtils.dp2px(mContext, 7),
                    DensityUtils.dp2px(mContext, 2));
        }
    }

    public static void enterApplyMarkDetailActivity(Context context,
                                                    QuestionResourceModel markModel) {
        if (markModel == null) {
            return;
        }
    }


    public static void doApplyMarkTask(Context mContext,
                                       ExerciseAnswerCardParam cardParam,
                                       ExerciseItem itemData,
                                       List<String> imageList) {
        if (imageList == null) {
            if (itemData == null) {
                return;
            }
            imageList = new ArrayList<>();
            List<MediaData> dataList = itemData.getDatas();
            for (int m = 0; m < dataList.size(); m++) {
                imageList.add(dataList.get(m).resourceurl);
            }
        }
        String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator().generate(imageList.get(0));
        cardParam.setExerciseItem(itemData);
        DoCourseHelper doCourseHelper = new DoCourseHelper(mContext);
        doCourseHelper.doAnswerQuestionCheckMarkData(
                cardParam,
                savePath,
                imageList,
                cardParam.getCommitTaskTitle(),
                cardParam.getScreenType(),
                DoCourseHelper.FromType.Do_Answer_Card_Check_Course);
    }

    public static void loadCourseImageList(Context mContext,
                                           ExerciseAnswerCardParam cardParam) {
        final HashMap<String, Object> params = new HashMap<>();
        StudyTask task = cardParam.getStudyTask();
        if (task == null) {
            return;
        }
        String resId = task.getResId();
        if (!TextUtils.isEmpty(resId)) {
            params.put("courseId", resId);
        }
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(mContext, CourseImageListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                CourseImageListResult result = (CourseImageListResult) getResult();
                if (result == null || result.getCode() != 0) {
                    TipsHelper.showToast(mContext, R.string.no_course_images);
                    return;
                }
                List<CourseData> courseDatas = result.getCourse();
                if (courseDatas != null && courseDatas.size() > 0) {
                    CourseData courseData = courseDatas.get(0);
                    if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                        List<String> paths = result.getData();
                        if (paths == null || paths.size() == 0) {

                        } else {
                            checkCanReplaceIPAddress(mContext,
                                    result.getData(),
                                    courseData,
                                    cardParam);
                        }
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(mContext, ServerUrl.COURSE_IMAGES_URL, params, listener);
    }

    /**
     * 校验是否用内网的IP进行下载
     */
    private static void checkCanReplaceIPAddress(Context mContext,
                                                 final List<String> paths,
                                                 CourseData courseData,
                                                 ExerciseAnswerCardParam cardParam) {
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper((Activity) mContext);
        helper.setResId(courseData.id)
                .setResType(courseData.type)
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        List<String> imageUrl = paths;
                        if (flag) {
                            imageUrl = helper.getChangeIPUrlArray(paths);
                        }
                        doApplyMarkTask(mContext, cardParam, null, imageUrl);
                    }
                })
                .checkIP();
    }

    public static void enterApplyTeacherMarkActivity(Context mContext,
                                                     CourseData courseData,
                                                     QuestionResourceModel markModel) {
        markModel.setResId(courseData.getIdType());
        markModel.setResUrl(courseData.resourceurl);
        TutorChoiceParams choiceParams = new TutorChoiceParams();
        choiceParams.setMemberId(markModel.getStuMemberId());
        choiceParams.setChapterId(String.valueOf(markModel.getT_ResCourseId()));
        choiceParams.setCourseId(markModel.getT_CourseId());
        choiceParams.setModel(markModel);
        TutorChoiceActivity.show(mContext, choiceParams);
    }

}

