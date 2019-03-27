package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.CheckMarkActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.ApplyMarkHelper;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.ExerciseTeacherCommentDialog;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.client.pojo.LearnTaskCardType;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.lqbaselib.common.DoubleOperationUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.osastudio.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerCardDetailFragment extends ContactsListFragment implements View.OnClickListener {
    private Context mContext;
    private LinearLayout objectProblemLayout;
    private LinearLayout subjectProblemLayout;
    private LinearLayout objectiveLayout;
    private LearnTaskInfo studentAnswerInfo = new LearnTaskInfo();
    private List<ExerciseItem> exerciseItems = new ArrayList<>();
    private LearnTaskInfo learnTaskInfo;
    private List<LearnTaskInfo> splitInfoList = new ArrayList<>();
    private boolean isMarkingAllQuestion;//所有的作答都已完成
    private String[] questionTypeName;
    private ExerciseAnswerCardParam cardParam;
    private String studentCommitAnswerString;
    private boolean hasObjectiveProblem;
    private String taskScoreReMark;
    private QuestionResourceModel markModel;

    private final int[] colorList = {
            Color.parseColor("#76c905"),
            Color.parseColor("#38c2e0"),
            Color.parseColor("#ffe827"),
            Color.parseColor("#ff9f22"),
            Color.parseColor("#ff3b0d")
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answer_card_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        loadStudentCommitData();
        addEventBusReceiver();
    }

    private void loadIntentData() {
        Bundle args = getArguments();
        if (args != null) {
            cardParam = (ExerciseAnswerCardParam) args.getSerializable(ExerciseAnswerCardParam
                    .class.getSimpleName());
            if (cardParam != null) {
                markModel = cardParam.getMarkModel();
                //评语
                taskScoreReMark = cardParam.getTaskScoreRemark();
                JSONArray jsonArray = JSONObject.parseArray(cardParam.getExerciseAnswerString());
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    learnTaskInfo = JSONObject.parseObject(jsonObject.toString(), LearnTaskInfo.class);
                }
            }
        }
        questionTypeName = mContext.getResources().getStringArray(R.array.array_question_type_name);
    }

    private void loadStudentCommitData() {
        Map<String, Object> param = new HashMap<>();
        if (cardParam.isFromOnlineStudyTask()) {
            param.put("CommitTaskOnlineId", cardParam.getCommitTaskId());
        } else {
            param.put("CommitTaskId", cardParam.getCommitTaskId());
        }
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(mContext, ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                LogUtils.logd("TEST", "studentCommitDataList=" + jsonString);
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                try {
                    JSONObject result = JSONObject.parseObject(jsonString);
                    if (result != null) {
                        int code = result.getIntValue("ErrorCode");
                        if (code == 0) {
                            //成功
                            JSONObject modelObj = result.getJSONObject("Model");
                            if (modelObj != null) {
                                JSONArray dataListArray = modelObj.getJSONArray("DataList");
                                if (dataListArray != null && dataListArray.size() > 0) {
                                    studentCommitAnswerString = dataListArray.toString();
                                    isMarkingAllQuestion = true;
                                    exerciseItems.clear();
                                    splitInfoList.clear();
                                    DoTaskOrderHelper.analysisStudentCommitData(dataListArray, exerciseItems);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mergeLearnTaskData();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(mContext, ServerUrl.LOAD_ANSWER_BY_COMMITTASKID_BASE_URL, param, listener);
    }

    private void mergeLearnTaskData() {
        if (exerciseItems != null && exerciseItems.size() > 0) {
            studentAnswerInfo.setExercise_item_list(exerciseItems);
            if (learnTaskInfo != null) {
                studentAnswerInfo.setStudent_name(cardParam.getStudentName());
                studentAnswerInfo.setTotal_score(cardParam.getExerciseTotalScore());
                List<ExerciseItem> learnExerciseItem = learnTaskInfo.getExercise_item_list();
                if (learnExerciseItem != null && learnExerciseItem.size() > 0) {
                    double objectiveTotalScore = 0;//客观题的总分
                    double studentObjectiveTotalScore = 0;//学生客观题的得分
                    double subjectiveTotalScore = 0;//主观题的总分
                    double studentSubjectTotalScore = 0;//学生主观题的得分
                    int subjectUnFinishCount = 0;
                    outer:
                    for (int i = 0; i < exerciseItems.size(); i++) {
                        ExerciseItem item = exerciseItems.get(i);
                        for (int j = 0; j < learnExerciseItem.size(); j++) {
                            ExerciseItem learnItem = learnExerciseItem.get(j);
                            if (TextUtils.equals(item.getIndex(), learnItem.getIndex())) {
                                item.setScore(learnItem.getScore());
                                item.setRight_answer(learnItem.getRight_answer());
                                item.setAnalysis(learnItem.getAnalysis());
                                item.setSubscore(learnItem.getSubscore());
                                item.setItem_count(learnItem.getItem_count());
                                item.setName(learnItem.getName());
                                item.setType_name(learnItem.getType_name());
                                item.setId(learnItem.getId());
                                item.setAreaItemList(learnItem.getAreaItemList());
                                item.setRight_answer_res_id(learnItem.getRight_answer_res_id());
                                item.setRight_answer_res_name(learnItem.getRight_answer_res_name());
                                item.setRight_answer_res_url(learnItem.getRight_answer_res_url());
                                item.setSrc_res_id(learnItem.getSrc_res_id());
                                item.setSrc_res_name(learnItem.getSrc_res_name());
                                item.setSrc_res_url(learnItem.getSrc_res_url());
                                item.setSrc_text(learnItem.getSrc_text());
                                item.setAnalysis_res_id(learnItem.getAnalysis_res_id());
                                item.setAnalysis_res_name(learnItem.getAnalysis_res_name());
                                item.setAnalysis_res_url(learnItem.getAnalysis_res_url());
                                double itemScore = Double.valueOf(item.getScore());
                                double studentItemScore = Double.valueOf(item.getStudent_score());
                                if (!TextUtils.isEmpty(item.getStudent_score())) {
                                    if (Integer.valueOf(item.getType()) == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                                        //主观题
                                        subjectiveTotalScore = DoubleOperationUtil.add(subjectiveTotalScore,itemScore);
                                        studentSubjectTotalScore = DoubleOperationUtil.add(studentSubjectTotalScore,studentItemScore);
                                    } else {
                                        objectiveTotalScore = DoubleOperationUtil.add(objectiveTotalScore,itemScore);
                                        studentObjectiveTotalScore = DoubleOperationUtil.add(studentObjectiveTotalScore,studentItemScore);
                                    }
                                }

                                if (Integer.valueOf(item.getType()) == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                                    //主观题
                                    if (item.getEqState() == 3) {
                                        isMarkingAllQuestion = false;
                                        subjectUnFinishCount++;
                                    }
                                    studentAnswerInfo.setHasSubjectProblem(true);
                                } else {
                                    hasObjectiveProblem = true;
                                }

                                //数据分类
                                DoTaskOrderHelper.splitLearnTypeData(item, splitInfoList);
                                continue outer;
                            }
                        }
                    }
                    //学生得分
                    studentAnswerInfo.setObjectiveTotalScore(String.valueOf(objectiveTotalScore));
                    studentAnswerInfo.setStudentObjectiveTotalScore(String.valueOf(studentObjectiveTotalScore));
                    studentAnswerInfo.setSubjectiveTotalScore(String.valueOf(subjectiveTotalScore));
                    studentAnswerInfo.setStudentSubjectTotalScore(String.valueOf(studentSubjectTotalScore));
                    studentAnswerInfo.setStudent_score(String.valueOf(DoubleOperationUtil.add(studentObjectiveTotalScore,studentSubjectTotalScore)));
                    cardParam.setUnFinishSubjectCount(subjectUnFinishCount);
                    cardParam.setStudentTotalScore(String.valueOf(DoubleOperationUtil.add(studentObjectiveTotalScore,studentSubjectTotalScore)));
                }
            }
            initTitleView();
            initReMarkViewData();
            initView();
            initObjectProblemDetail();
            initSubjectProblemDetail();
        }
    }

    private void initTitleView() {
        TextView headTitleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitleTextV != null) {
            headTitleTextV.setText(cardParam.getCommitTaskTitle());
        }
        TextView lookAnswerParsingTextV = (TextView) findViewById(R.id.tv_look_answer_parsing);
        //查看答案解析
        if (lookAnswerParsingTextV != null) {
            lookAnswerParsingTextV.setOnClickListener(v -> lookAnswerParsingDetail());
        }
    }

    private void initReMarkViewData() {
        //老师点评btn
        TextView remarkTextV = (TextView) findViewById(R.id.tv_teacher_comment);
        if (TextUtils.isEmpty(taskScoreReMark) && (cardParam.isOnlineReporter() || cardParam.isOnlineHost())) {
            //未点评
            remarkTextV.setVisibility(View.VISIBLE);
            remarkTextV.setOnClickListener(v -> openTeacherReMarkDialog());
        } else if (TextUtils.equals(getMemeberId(),cardParam.getStudentId()) && !MainApplication.isTutorialMode()) {
            //显示老师批阅
            ApplyMarkHelper.showApplyMarkView(getActivity(),remarkTextV);
            remarkTextV.setOnClickListener(v -> lookAnswerParsingDetail());
        } else {
            remarkTextV.setVisibility(View.GONE);
        }
        //点评的详情
        LinearLayout remarkDetailLayout = (LinearLayout) findViewById(R.id.ll_teacher_comment_detail);
        if (!TextUtils.isEmpty(taskScoreReMark)) {
            remarkDetailLayout.setVisibility(View.VISIBLE);
            TextView showRemarkTextV = (TextView) findViewById(R.id.tv_show_comment_detail);
            showRemarkTextV.setText(taskScoreReMark);
        }
    }

    private void initView() {
        TextView studentNameTextV = (TextView) findViewById(R.id.tv_student_name);
        studentNameTextV.setText(cardParam.getStudentName());
        //满分字段
        TextView fullMarkScoreView = (TextView) findViewById(R.id.tv_full_score);
        LinearLayout studentMarkScoreLayout = (LinearLayout) findViewById(R.id.ll_student_mark_score);
        //显示得分/满分
        if (isMarkingAllQuestion) {
            studentMarkScoreLayout.setVisibility(View.VISIBLE);
            //学生的分数
            String score = Utils.changeDoubleToInt(studentAnswerInfo.getStudent_score());
            TextView studentScoreTextV = (TextView) findViewById(R.id.tv_student_score);
            studentScoreTextV.setText(score);
            if (!TextUtils.isEmpty(score)) {
                studentScoreTextV.setTextColor(getColorId(score));
            }
        }
        //显示满分
        fullMarkScoreView.setText(getString(R.string.str_full_marks, Utils.changeDoubleToInt(cardParam.getExerciseTotalScore())));

        //客观题的分数
        TextView objectiveScoreTextV = (TextView) findViewById(R.id.tv_objective_score);
        String objectString = getString(R.string.str_eval_score, Utils.changeDoubleToInt
                (studentAnswerInfo.getStudentObjectiveTotalScore())) +
                "/" + getString(R.string.str_total_score, Utils.changeDoubleToInt(studentAnswerInfo.getObjectiveTotalScore()));
        objectiveScoreTextV.setText(objectString);

        //主观题
        TextView studentSubjectScoreTextV = (TextView) findViewById(R.id.tv_subjective_score);
        String subjectString = null;
        if (isMarkingAllQuestion) {
            subjectString = getString(R.string.str_eval_score,
                    Utils.changeDoubleToInt(studentAnswerInfo.getStudentSubjectTotalScore())) + "/" +
                    getString(R.string.str_total_score,
                            Utils.changeDoubleToInt(studentAnswerInfo.getSubjectiveTotalScore()));
        } else {
            subjectString = getString(R.string.str_total_score, Utils.changeDoubleToInt
                    (studentAnswerInfo.getSubjectiveTotalScore()));
        }
        studentSubjectScoreTextV.setText(subjectString);
        if (studentAnswerInfo.isHasSubjectProblem()) {
            //有主观题visible
            findViewById(R.id.ll_subject_problem).setVisibility(View.VISIBLE);
        }
        objectProblemLayout = (LinearLayout) findViewById(R.id.ll_objective_detail);
        //更新显示的主观题客观题是否显示
        objectiveLayout = (LinearLayout) findViewById(R.id.ll_objective);
        if (!hasObjectiveProblem) {
            //没有客观题
            objectiveLayout.setVisibility(View.GONE);
            objectProblemLayout.setVisibility(View.GONE);
        }
    }

    private int getColorId(String studentScore) {
        int colorId = 0;
        String totalScore = cardParam.getExerciseTotalScore();
        if (!TextUtils.isEmpty(totalScore)) {
            double studentMarkScore = Double.valueOf(studentScore);
            double fullMark = Double.valueOf(totalScore);
            int[] scoreArray = new int[5];
            scoreArray[0] = (int) (fullMark * 0.9);
            scoreArray[1] = (int) (fullMark * 0.8);
            scoreArray[2] = (int) (fullMark * 0.7);
            scoreArray[3] = (int) (fullMark * 0.6);
            scoreArray[4] = (int) (fullMark * 0.5);
            if (studentMarkScore >= scoreArray[0] && studentMarkScore <= fullMark) {
                colorId = 0;
            } else if (studentMarkScore >= scoreArray[1]) {
                colorId = 1;
            } else if (studentMarkScore >= scoreArray[2]) {
                colorId = 2;
            } else if (studentMarkScore >= scoreArray[3]) {
                colorId = 3;
            } else {
                colorId = 4;
            }
        }
        return colorList[colorId];
    }

    private void initObjectProblemDetail() {
        if (splitInfoList == null || splitInfoList.size() == 0) {
            return;
        }
        Collections.sort(splitInfoList, ((o1, o2) -> o1.getLearnType() - o2.getLearnType()));
        //客观题
        objectProblemLayout.removeAllViews();
        for (int i = 0; i < splitInfoList.size(); i++) {
            LearnTaskInfo taskInfo = splitInfoList.get(i);
            if (taskInfo.getLearnType() == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                continue;
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_card_type_detail, null);
            TextView leftTitleTextV = (TextView) view.findViewById(R.id.tv_title_data);
            TextView rightTextV = (TextView) view.findViewById(R.id.tv_right_flag);
            String typeTitle = null;
            if (taskInfo.getLearnType() == LearnTaskCardType.LISTEN_SINGLE_SELECTION) {
                typeTitle = getString(R.string.str_listen_question);
            } else {
                typeTitle = questionTypeName[taskInfo.getLearnType() - 1];
            }
            String leftTitle = typeTitle + " x" + taskInfo.getExercise_item_list().size() +
                    " (" + getString(R.string.str_eval_score, Utils.changeDoubleToInt(taskInfo.getTotal_score())) + " )";
            leftTitleTextV.setText(leftTitle);
            String wrongCount = String.valueOf(taskInfo.getAnsWrongCount());
            String wrongString = getString(R.string.str_wrong_question_count, wrongCount);
            //错题的数量
            if (taskInfo.getAnsWrongCount() == 0) {
                rightTextV.setText(wrongString);
            } else {
                int startIndex = wrongString.indexOf(wrongCount);
                SpannableString systemColorString = new SpannableString(wrongString);
                systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.red)),
                        startIndex, startIndex + wrongCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                rightTextV.setText(systemColorString);
            }
            objectProblemLayout.addView(view);
        }
    }

    private void initSubjectProblemDetail() {
        if (exerciseItems == null || exerciseItems.size() == 0) {
            return;
        }
        //主观题
        if (subjectProblemLayout == null) {
            subjectProblemLayout = (LinearLayout) findViewById(R.id.ll_subjective_detail);
        }
        subjectProblemLayout.removeAllViews();
        for (int i = 0; i < exerciseItems.size(); i++) {
            ExerciseItem itemData = exerciseItems.get(i);
            if (Integer.valueOf(itemData.getType()) == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                //主观题
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_card_type_detail, null);
                TextView leftTitleTextV = (TextView) view.findViewById(R.id.tv_title_data);
                TextView rightTextV = (TextView) view.findViewById(R.id.tv_right_flag);
                ImageView arrowRightImageV = (ImageView) view.findViewById(R.id.iv_arrow_right);
                String leftTitle = itemData.getType_name() + " (" + getString(R.string.str_eval_score,
                        Utils.changeDoubleToInt(itemData.getScore())) + " )";
                leftTitleTextV.setText(leftTitle);
                String rightTitle = null;
                view.setOnClickListener(v -> {
                    if (itemData.getEqState() == 5) {
                    } else {
                        //打开进入批阅详情
                        enterCheckMarkDetailFragment(itemData);
                    }
                });
                if (itemData.getEqState() == 5) {
                    //未答题
                    rightTitle = getString(R.string.str_no_answer);
                } else if (itemData.getEqState() == 3) {
                    //没有打分
                    arrowRightImageV.setVisibility(View.VISIBLE);
                    rightTextV.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable
                            .green_10dp_red));
                    rightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    rightTextV.setTextSize(14);
                    rightTextV.setPadding(DensityUtils.dp2px(mContext, 7),
                            DensityUtils.dp2px(mContext, 2),
                            DensityUtils.dp2px(mContext, 7),
                            DensityUtils.dp2px(mContext, 2));
                    if (cardParam.isOnlineHost() || cardParam.isOnlineReporter()) {
                        //显示批阅
                        rightTitle = getString(R.string.read_over);
                        rightTextV.setOnClickListener(v -> {
                            //直接进入批阅
                            openCheckMarkActivity(itemData);
                        });
                    } else {
                        //显示未批阅
                        rightTitle = getString(R.string.str_no_read_over);
                    }
                } else {
                    //显示分数
                    rightTitle = getString(R.string.str_eval_score, Utils.changeDoubleToInt(itemData.getStudent_score()));
                    rightTextV.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable
                            .green_10dp_stroke_green));
                    rightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                    rightTextV.setTextSize(14);
                    rightTextV.setPadding(DensityUtils.dp2px(mContext, 7),
                            DensityUtils.dp2px(mContext, 2),
                            DensityUtils.dp2px(mContext, 7),
                            DensityUtils.dp2px(mContext, 2));
                    arrowRightImageV.setVisibility(View.VISIBLE);
                }
                rightTextV.setText(rightTitle);
                subjectProblemLayout.addView(view);
            }
        }
    }

    /**
     * 查看答案解析
     */
    private void lookAnswerParsingDetail() {
        openImage(cardParam.getResId(), true);
    }

    /**
     * 批阅
     *
     * @param itemData
     */
    private void openCheckMarkActivity(ExerciseItem itemData) {
        if (itemData != null) {
            List<MediaData> dataList = itemData.getDatas();
            if (dataList != null && dataList.size() > 0) {
                List<String> imageList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    imageList.add(dataList.get(i).resourceurl);
                }
                String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                        .generate(dataList.get(0).resourceurl);
                cardParam.setExerciseItem(itemData);
                cardParam.setMarkModel(null);
                DoCourseHelper doCourseHelper = new DoCourseHelper(mContext);
                doCourseHelper.doAnswerQuestionCheckMarkData(
                        cardParam,
                        savePath,
                        imageList,
                        cardParam.getCommitTaskTitle(),
                        cardParam.getScreenType(),
                        DoCourseHelper.FromType.Do_Answer_Card_Check_Course);
            }
        }
    }

    /**
     * 进入批阅详情界面
     *
     * @param itemData 作答的详情数据
     */
    private void enterCheckMarkDetailFragment(ExerciseItem itemData) {
        cardParam.setExerciseItem(itemData);
        CheckMarkActivity.start((Activity) mContext, cardParam);
    }

    @Override
    public void onMessageEvent(MessageEvent messageEvent) {
        if (TextUtils.equals(messageEvent.getUpdateAction(), MessageEventConstantUtils.UPDATE_LIST_DATA)) {
            loadStudentCommitData();
        }
    }

    /**
     * 打开图片
     */
    public void openImage(String courseId, boolean isDoTaskOrder) {
        String tempResId = courseId;
        int resType = 0;
        if (courseId.contains("-")) {
            String[] ids = courseId.split("-");
            if (ids != null && ids.length == 2) {
                tempResId = ids[0];
                if (ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        if (resType > ResType.RES_TYPE_BASE) {
            //分页信息
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            courseData.setIsPublicRes(false);
                            processOpenImageData(courseData, isDoTaskOrder);
                        }
                    }
                }
            });
        } else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadCourseDetail(courseId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    courseData.setIsPublicRes(false);
                    processOpenImageData(courseData, isDoTaskOrder);
                }
            });
        }
    }

    /**
     * 打开图片逻辑
     *
     * @param courseData
     */
    private void processOpenImageData(CourseData courseData, boolean isDoTaskOrder) {
        if (courseData != null) {
            PlaybackParam mParam = new PlaybackParam();
            //隐藏收藏按钮
            mParam.mIsHideCollectTip = true;
            if (isDoTaskOrder) {
                handleExerciseAnswerData(mParam, courseData);
            }
            int resType = courseData.type % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(getActivity(), courseData.getCourseInfo(), false,
                        mParam);
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                //直接打开，不带编辑。
                ActivityUtils.openOnlineOnePage(getActivity(), courseData.getNewResourceInfo(), true,
                        mParam);
            } else if (resType == ResType.RES_TYPE_ONEPAGE) {
                ActivityUtils.openOnlineOnePage(getActivity(), courseData.getNewResourceInfo(), true,
                        mParam);
            } else if (resType == ResType.RES_TYPE_NOTE) {
                //直接打开帖子
                if (!TextUtils.isEmpty(courseData.resourceurl)) {
                    ActivityUtils.openOnlineNote(getActivity(), courseData.getCourseInfo(), false, true);
                }
            }
        }
    }

    private void handleExerciseAnswerData(PlaybackParam mParam, CourseData courseData) {
        //任务单答题操作
        ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
        cardParam.setShowExerciseNode(true);
        cardParam.setQuestionDetails(exerciseItems);
        //server返回的学生提交的信息
        cardParam.setExerciseAnswerString(this.cardParam.getExerciseAnswerString());
        cardParam.setStudentCommitAnswerString(studentCommitAnswerString);
        cardParam.setTaskId(cardParam.getTaskId());
        cardParam.setResId(courseData.id + "-" + courseData.type);
        cardParam.setStudentId(this.cardParam.getStudentId());
        cardParam.setStudentName(this.cardParam.getStudentName());
        cardParam.setFromOnlineStudyTask(this.cardParam.isFromOnlineStudyTask());
        cardParam.setCommitTaskTitle(this.cardParam.getCommitTaskTitle());
        cardParam.setClassId(this.cardParam.getClassId());
        cardParam.setSchoolId(this.cardParam.getSchoolId());
        cardParam.setMarkModel(this.markModel);
        cardParam.setStudyTask(this.cardParam.getStudyTask());
        cardParam.setCommitTask(this.cardParam.getCommitTask());
        cardParam.setRoleType(this.cardParam.getRoleType());
        mParam.exerciseCardParam = cardParam;
    }

    private void openTeacherReMarkDialog() {
        ExerciseTeacherCommentDialog dialog = new ExerciseTeacherCommentDialog(mContext, result -> {
            commitTeacherMark((String) result);
        });
        dialog.show();
    }

    private void commitTeacherMark(final String inputContent) {
        Map<String, Object> params = new ArrayMap<>();
        if (cardParam.isFromOnlineStudyTask()) {
            params.put("CommitTaskOnlineId", cardParam.getCommitTaskId());
        } else {
            params.put("CommitTaskId", cardParam.getCommitTaskId());
        }
        params.put("IsTeacher", true);
        params.put("CreateId", getMemeberId());
        params.put("TaskScore", isMarkingAllQuestion ? Utils.changeDoubleToInt(studentAnswerInfo.getStudent_score()) : "");
        params.put("ResId", "");
        params.put("ResUrl", "");
        params.put("IsVoiceReview", true);
        params.put("TaskScoreRemark", inputContent);
        RequestHelper.RequestDataResultListener listener = new RequestHelper.RequestDataResultListener<DataModelResult>(mContext,
                DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    return;
                }
                TipMsgHelper.ShowLMsg(mContext, R.string.commit_success);
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
                broadcastManager.sendBroadcast(new Intent(CompletedHomeworkListFragment.ACTION_MARK_SCORE));
                taskScoreReMark = inputContent;
                initReMarkViewData();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(mContext, ServerUrl.GET_ADDCOMMITTASKREVIEW,
                params, listener);
    }
}
