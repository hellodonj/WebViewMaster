package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.AnswerParsingActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.BarChartHelper;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.BarChartDataInfo;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.github.mikephil.charting.charts.BarChart;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.LearnTaskCardType;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.osastudio.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerParsingFragment extends ContactsListFragment {
    public static final String TAG = AnswerParsingFragment.class.getSimpleName();
    //学生答题的详情layout
    private LinearLayout answerDetailLayout;
    private LinearLayout llBarLayout;
    private LinearLayout llDetailLayout;
    private Context mContext;
    private ExerciseItem exerciseData;//任务单答题的数据
    private String[] questionTypeName;
    private boolean fromAnswerAnalysis;//是不是来自答题分析
    private List<String> checkAnswerDataList = new ArrayList<>();
    private List<BarChartDataInfo> barChartDataInfoList;
    private int questionType;
    private String studentName;
    private String studentId;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answer_parsing, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        if (fromAnswerAnalysis) {
            loadBarChartData();
        }
    }

    private void loadIntentData() {
        questionTypeName = mContext.getResources().getStringArray(R.array.array_question_type_name);
        Bundle args = getArguments();
        if (args != null) {
            exerciseData = (ExerciseItem) args.getSerializable(AnswerParsingActivity.Constants
                    .SINGLE_QUESTION_ANSWER);
            fromAnswerAnalysis = args.getBoolean(AnswerParsingActivity.Constants
                    .FROM_ANSWER_ANALYSIS, false);
            studentId = args.getString(AnswerParsingActivity.Constants.STUDENT_ID);
            studentName = args.getString(AnswerParsingActivity.Constants.STUDENT_NAME);
        }
        String[] checkList = new String[]{"","A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        checkAnswerDataList.addAll(Arrays.asList(checkList));
    }

    private void initViews() {
        if (exerciseData == null) {
            return;
        }
        questionType = Integer.valueOf(exerciseData.getType());
        //柱状图的父layout
        llBarLayout = (LinearLayout) findViewById(R.id.ll_bar_detail);
        //听力原文
        llDetailLayout = (LinearLayout) findViewById(R.id.ll_listen_detail);
        //题目名称
        TextView questionTitleTextV = (TextView) findViewById(R.id.tv_question_title);
        questionTitleTextV.setText(exerciseData.getName());
        //题型
        TextView questionTypeTextV = (TextView) findViewById(R.id.tv_question_type);
        String rightScore = "(" + getString(R.string.str_eval_score, Utils.changeDoubleToInt(exerciseData.getScore())) + ")";
        String typeTitle = null;
        if (questionType == LearnTaskCardType.SUBJECTIVE_PROBLEM){
            //主观题
            typeTitle = exerciseData.getType_name() + rightScore;
        } else {
            typeTitle = questionTypeName[questionType - 1] + rightScore;
        }
        questionTypeTextV.setText(typeTitle);
        //得分
        TextView markScoreTextV = (TextView) findViewById(R.id.tv_mark_score);
        String studentScore = getString(R.string.str_eval_score, Utils.changeDoubleToInt(exerciseData.getStudent_score()));
        markScoreTextV.setText(studentScore);
        if (fromAnswerAnalysis) {
            markScoreTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            String errorRate = getString(R.string.str_answer_error_rate) +
                    String.valueOf(exerciseData.getErrorRate()) + "%";
            if (questionType == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                //主观题
                errorRate = getString(R.string.str_question_average_score, Utils.changeDoubleToInt
                        (String.valueOf(exerciseData.getAverageScore())));
            }
            markScoreTextV.setText(errorRate);
        } else {
            if (exerciseData.isAnswerRight()) {
                //答对 绿色标识
                markScoreTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
            } else {
                //答错 红色标识
                if (questionType == LearnTaskCardType.SUBJECTIVE_PROBLEM && exerciseData
                        .getEqState() == 3){
                    //没有批阅 不显示分数
                    markScoreTextV.setVisibility(View.GONE);
                } else {
                    markScoreTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }
            }
        }
        //学生答题的详情layout
        answerDetailLayout = (LinearLayout) findViewById(R.id.ll_answer_detail);
        handleStudentAnswerDetail();
        //答案解析
        TextView answerParsingTextV = (TextView) findViewById(R.id.tv_answer_parsing);
        String analyseString = null;
        if (TextUtils.isEmpty(exerciseData.getAnalysis())) {
            analyseString = "【" + getString(R.string.str_answer_parsing) + "】 " + getString(R
                    .string.str_no_analyse_tip);
        } else {
            analyseString = "【" + getString(R.string.str_answer_parsing) + "】 " + exerciseData
                    .getAnalysis();
        }
        int index = analyseString.indexOf("】");
        SpannableString systemColorString = new SpannableString(analyseString);
        systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R
                .color.text_black)), 0, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        answerParsingTextV.setText(systemColorString);
    }

    private void handleStudentAnswerDetail() {
        switch (questionType) {
            case LearnTaskCardType.SINGLE_CHOICE_QUESTION://单选题
            case LearnTaskCardType.LISTEN_SINGLE_SELECTION://听力单选
            case LearnTaskCardType.MULTIPLE_CHOICE_QUESTIONS://多选题
            case LearnTaskCardType.SINGLE_CHOICE_CORRECTION://单选改错题
            case LearnTaskCardType.JUDGMENT_PROBLEM://判断题
            case LearnTaskCardType.HEARING_JUDGMENT://听力判断题
                showChoiceAnswerData(questionType);
                break;
            case LearnTaskCardType.FILL_CONTENT://填空题
            case LearnTaskCardType.LISTEN_FILL_CONTENT://听力填空
                showFillInData();
                break;
            case LearnTaskCardType.SUBJECTIVE_PROBLEM://主观题
                showSubjectData();
                break;
        }
        showListenData();
    }

    private void showChoiceAnswerData(int questionType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_parsing_detail, null);
        //学生的答案
        LinearLayout studentAnswerLayout = (LinearLayout) view.findViewById(R.id.ll_my_answer);
        LinearLayout answerRightLayout = (LinearLayout) view.findViewById(R.id.ll_reference_answer);
        if (fromAnswerAnalysis) {
            //隐藏学生的作答选项
            answerRightLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.text_white));
            studentAnswerLayout.setVisibility(View.GONE);
            answerRightLayout.setPadding(0,
                    DensityUtils.dp2px(mContext, 5),
                    DensityUtils.dp2px(mContext, 5),
                    DensityUtils.dp2px(mContext, 5));
        }
        TextView leftRightTextV = (TextView) view.findViewById(R.id.tv_left_right_title);
        String referenceAnswer = null;
        String studentAnswer = null;
        //参考答案
        TextView referenceAnswerTextV = (TextView) view.findViewById(R.id.tv_reference_answer);
        //我的答案
        TextView answerTextV = (TextView) view.findViewById(R.id.tv_my_answer_text);
        //不是自己的显示xx的答案
        if (!TextUtils.equals(getMemeberId(),studentId) && !TextUtils.isEmpty(studentName)){
          answerTextV.setText(getString(R.string.str_some_body_answer,studentName));
        }
        TextView myAnswerTextV = (TextView) view.findViewById(R.id.tv_my_answer);
        //订正
        TextView correctAnswerTextV = (TextView) view.findViewById(R.id.tv_correct_answer);
        TextView correctRightTextV = (TextView) view.findViewById(R.id.tv_correct_right_answer);
        //判断题的状态
        ImageView answerRighImageV = (ImageView) view.findViewById(R.id.iv_question_right_answer);
        ImageView studentAnswerImageV = (ImageView) view.findViewById(R.id.iv_question_student_answer);
        if (questionType == LearnTaskCardType.SINGLE_CHOICE_CORRECTION) {
            //单选改错
            correctAnswerTextV.setVisibility(View.VISIBLE);
            correctRightTextV.setVisibility(View.VISIBLE);
            JSONObject jsonObject = JSONObject.parseObject(exerciseData.getRight_answer());
            String rightItemIndex = jsonObject.getString("item_index");
            String rightAnswerText = jsonObject.getString("answer_text");
            //正确的订正答案
            rightAnswerText = getString(R.string.str_revising) + ": " + rightAnswerText;
            if (fromAnswerAnalysis) {
                referenceAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                leftRightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                String leftRightTitle = "【" + getString(R.string.str_reference_answer_no_point) + "】";
                leftRightTextV.setText(leftRightTitle);
                rightAnswerText = "【" + getString(R.string.str_revising) + "】 " + jsonObject.getString("answer_text");
                correctRightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                //常见错误
                TextView commonMistakesTextV = (TextView) view.findViewById(R.id.tv_commit_mistakes);
                commonMistakesTextV.setVisibility(View.VISIBLE);
                String commonMistakes = getString(R.string.str_common_mistakes);
                String commonError = exerciseData.getCommonError();
                if (TextUtils.isEmpty(commonError)) {
                    //没有
                    commonMistakes = commonMistakes + " " + getString(R.string.str_no_analyse_tip);
                } else {
                    JSONObject errorObj = JSONObject.parseObject(commonError);
                    if (errorObj != null) {
                        String index = errorObj.getString("item_index");
                        String text = errorObj.getString("answer_text");
                        if (!TextUtils.isEmpty(index) && !TextUtils.isEmpty(text)) {
                            commonMistakes = commonMistakes + " " + getChoiceAnswerData(index);
                        } else if (!TextUtils.isEmpty(index)) {
                            commonMistakes = commonMistakes + " " + getChoiceAnswerData(index);
                        } else {
                            commonMistakes = commonMistakes + " " + getString(R.string.str_no_analyse_tip);
                        }
                    } else {
                        commonMistakes = commonMistakes + " " + getString(R.string.str_no_analyse_tip);
                    }
                }
                commonMistakesTextV.setText(commonMistakes);
            } else {
                JSONObject studentAnswerObj = JSONObject.parseObject(exerciseData.getStudent_answer());
                String studentItemIndex = studentAnswerObj.getString("item_index");
                String studentAnswerText = studentAnswerObj.getString("answer_text");
                //学生的订正
                if (TextUtils.isEmpty(studentAnswerText)) {
                    studentAnswerText = "";
                }
                studentAnswerText = getString(R.string.str_revising) + ": " + studentAnswerText;
                correctAnswerTextV.setText(studentAnswerText);
                if (TextUtils.equals(rightItemIndex, studentItemIndex)) {
                    //答对
                    myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                    answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                } else {
                    //错误
                    myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }
                myAnswerTextV.setText(changAnswerDataType(studentItemIndex));
                //订正
                if (TextUtils.equals(rightAnswerText, studentAnswerText)) {
                    correctAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                } else {
                    correctAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }
            }
            //显示正确的答案
            referenceAnswerTextV.setText(changAnswerDataType(rightItemIndex));
            correctRightTextV.setText(rightAnswerText);
        } else if (questionType == LearnTaskCardType.JUDGMENT_PROBLEM
                || questionType == LearnTaskCardType.HEARING_JUDGMENT) {
            //判断题、听力判断题
            myAnswerTextV.setVisibility(View.VISIBLE);
            referenceAnswerTextV.setVisibility(View.VISIBLE);
            answerRighImageV.setVisibility(View.GONE);
            studentAnswerImageV.setVisibility(View.GONE);
            //正确答案
            if (TextUtils.equals(exerciseData.getRight_answer(), "1")) {
                referenceAnswerTextV.setText(getString(R.string.str_right));
            } else {
                referenceAnswerTextV.setText(getString(R.string.str_wrong));
            }
            if (fromAnswerAnalysis) {
                leftRightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                String leftRightTitle = "【" + getString(R.string.str_reference_answer_no_point) + "】";
                leftRightTextV.setText(leftRightTitle);
            } else {
                //学生的作答
                if (TextUtils.equals(exerciseData.getStudent_answer(), "1")) {
                    //答对
                    myAnswerTextV.setText(getString(R.string.str_right));
                } else if (TextUtils.isEmpty(exerciseData.getStudent_answer())){
                    //未做
                    myAnswerTextV.setText("");
                } else {
                    //答错
                    myAnswerTextV.setText(getString(R.string.str_wrong));
                }
                if (exerciseData.isAnswerRight()) {
                    myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                    answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                } else {
                    myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }
            }
        } else {
            referenceAnswer = exerciseData.getRight_answer();
            studentAnswer = exerciseData.getStudent_answer();
            //转化数据
            referenceAnswerTextV.setText(changAnswerDataType(referenceAnswer));
            if (fromAnswerAnalysis) {
                referenceAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                leftRightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                String leftRightTitle = "【" + getString(R.string.str_reference_answer_no_point) + "】";
                leftRightTextV.setText(leftRightTitle);
            } else {
                myAnswerTextV.setText(changAnswerDataType(studentAnswer));
                if (exerciseData.isAnswerRight()) {
                    //正确
                    myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                    answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                } else {
                    //错误
                    myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }
            }
        }
        answerDetailLayout.addView(view);
    }

    private String getChoiceAnswerData(String itemIndex){
        StringBuilder stringBuilder = new StringBuilder();
        if (itemIndex.contains(";")){
            String [] splitArray = itemIndex.split(";");
            for (int i = 0; i < splitArray.length; i++){
                int index = Integer.valueOf(splitArray[i]);
                if (i == 0){
                    stringBuilder.append(checkAnswerDataList.get(index));
                } else {
                    stringBuilder.append(";").append(checkAnswerDataList.get(index));
                }
            }
        } else {
            stringBuilder.append(checkAnswerDataList.get(Integer.valueOf(itemIndex)));
        }
        return stringBuilder.toString();
    }

    private String changAnswerDataType(String answer) {
        StringBuilder scoreBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(answer)) {
            if (answer.contains(",")) {
                String[] splitArray = answer.split(",");
                if (splitArray.length > 0) {
                    for (int i = 0; i < splitArray.length; i++) {
                        String answerData = splitArray[i];
                        if (!TextUtils.isEmpty(answerData)) {
                            if (scoreBuilder.length() == 0) {
                                scoreBuilder.append(checkAnswerDataList.get(Integer.valueOf
                                        (answerData)));
                            } else {
                                scoreBuilder.append(",").append(checkAnswerDataList.get(Integer
                                        .valueOf(answerData)));
                            }
                        }
                    }
                }
            } else {
                scoreBuilder.append(checkAnswerDataList.get(Integer.valueOf(answer)));
            }
        }
        return scoreBuilder.toString();
    }

    private void showFillInData() {
        String rightAnswerData = exerciseData.getRight_answer();
        if (rightAnswerData != null) {
            JSONArray rightAnswerArray = JSONObject.parseArray(rightAnswerData);
            JSONArray studentAnswerArray = JSONObject.parseArray(exerciseData.getStudent_answer());
            String[] subScoreArray = exerciseData.getSubScoreArray();
            String[] subStudentScoreArray = exerciseData.getStudentSubScoreArray();
            List<String> commonErrorData = exerciseData.getSubCommonError();
            if (rightAnswerArray != null && rightAnswerArray.size() > 0) {
                for (int i = 0; i < rightAnswerArray.size(); i++) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_parsing_detail, null);
                    //填空题学生的得分
                    TextView studentFillScoreView = (TextView) view.findViewById(R.id.tv_fill_in_score);
                    //填空题的title
                    LinearLayout fillInLayout = (LinearLayout) view.findViewById(R.id.ll_fill_in);
                    fillInLayout.setVisibility(View.VISIBLE);
                    //填空题的title
                    TextView fillInTitleTextV = (TextView) view.findViewById(R.id.tv_fill_in_number);
                    //参考答案
                    TextView referenceAnswerTextV = (TextView) view.findViewById(R.id.tv_reference_answer);
                    //我的答案
                    TextView answerTextV = (TextView) view.findViewById(R.id.tv_my_answer_text);
                    TextView myAnswerTextV = (TextView) view.findViewById(R.id.tv_my_answer);
                    //参考答案左边的title
                    TextView leftTitleTextV = (TextView) view.findViewById(R.id.tv_left_right_title);
                    String studentAnswer = null;
                    if (studentAnswerArray != null && studentAnswerArray.size() > 0) {
                        studentAnswer = studentAnswerArray.getString(i);
                    }
                    String rightAnswer = rightAnswerArray.getString(i);
                    if (fromAnswerAnalysis) {
                        //参考答案
                        answerTextV.setText(getString(R.string.str_reference_answer));
                        myAnswerTextV.setText(rightAnswer);
                        //常见错误
                        leftTitleTextV.setText(getString(R.string.str_common_mistakes));
                        leftTitleTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                        referenceAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                        if (commonErrorData != null && commonErrorData.size() > i) {
                            String singleError = commonErrorData.get(i);
                            if (TextUtils.isEmpty(singleError)) {
                                referenceAnswerTextV.setText(getString(R.string.str_no_analyse_tip));
                            } else {
                                referenceAnswerTextV.setText(commonErrorData.get(i));
                            }
                        } else {
                            referenceAnswerTextV.setText(getString(R.string.str_no_analyse_tip));
                        }
                        studentFillScoreView.setText(getString(R.string.str_eval_score, Utils
                                .changeDoubleToInt(subScoreArray[i])));
                    } else {
                        //不是自己的显示xx的答案
                        if (!TextUtils.equals(getMemeberId(),studentId) && !TextUtils.isEmpty(studentName)){
                            answerTextV.setText(getString(R.string.str_some_body_answer,studentName));
                        }
                        if (subStudentScoreArray != null && subStudentScoreArray.length > 0) {
                            studentFillScoreView.setText(getString(R.string.str_eval_score,
                                    Utils.changeDoubleToInt(subStudentScoreArray[i])));
                        } else {
                            studentFillScoreView.setText(getString(R.string.str_eval_score, "0"));
                        }
                        if (TextUtils.equals(studentAnswer, rightAnswer)) {
                            //正确
                            myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                            answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                            studentFillScoreView.setTextColor(ContextCompat.getColor(mContext, R.color
                                    .text_green));
                        } else {
                            //错误
                            myAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                            answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                            studentFillScoreView.setTextColor(ContextCompat.getColor(mContext, R.color
                                    .red));
                        }
                        myAnswerTextV.setText(studentAnswer);
                        referenceAnswerTextV.setText(rightAnswer);
                    }
                    //题号
                    String fillInTitle = getString(R.string.str_space) + (i + 1) + "( " +
                            getString(R.string.str_eval_score, Utils.changeDoubleToInt(subScoreArray[i])) + ")";
                    fillInTitleTextV.setText(fillInTitle);
                    answerDetailLayout.addView(view);
                }
            }
        }
    }

    private void showSubjectData() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_parsing_detail, null);
        //学生的答案
        LinearLayout studentAnswerLayout = (LinearLayout) view.findViewById(R.id.ll_my_answer);
        LinearLayout answerRightLayout = (LinearLayout) view.findViewById(R.id.ll_reference_answer);
        if (fromAnswerAnalysis) {
            //隐藏学生的作答选项
            answerRightLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.text_white));
            studentAnswerLayout.setVisibility(View.GONE);
            answerRightLayout.setPadding(0,
                    DensityUtils.dp2px(mContext, 5),
                    DensityUtils.dp2px(mContext, 5),
                    DensityUtils.dp2px(mContext, 5));
        }
        TextView leftRightTextV = (TextView) view.findViewById(R.id.tv_left_right_title);
        //参考答案
        TextView referenceAnswerTextV = (TextView) view.findViewById(R.id.tv_reference_answer);
        referenceAnswerTextV.setVisibility(View.GONE);
        TextView myAnswerTextV = (TextView) view.findViewById(R.id.tv_my_answer);
        myAnswerTextV.setVisibility(View.GONE);
        //我的答案
        TextView answerTextV = (TextView) view.findViewById(R.id.tv_my_answer_text);
        //不是自己的显示xx的答案
        if (!TextUtils.equals(getMemeberId(),studentId) && !TextUtils.isEmpty(studentName)){
            answerTextV.setText(getString(R.string.str_some_body_answer,studentName));
        }
        TextView studentAnswerTextV = (TextView) view.findViewById(R.id.tv_correct_answer);
        studentAnswerTextV.setVisibility(View.VISIBLE);
        //显示图片
        TextView rightTextV = (TextView) view.findViewById(R.id.tv_correct_right_answer);
        //显示的文本
        TextView textTextV = (TextView) view.findViewById(R.id.tv_answer_text);
        String answerTitle = null;
        if (fromAnswerAnalysis) {
            String leftRightTitle = "【" + getString(R.string.str_reference_answer_no_point) + "】";
            leftRightTextV.setText(leftRightTitle);
            leftRightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
        } else {
            if (exerciseData.getEqState() == 5) {
                //未答题
                answerTitle = getString(R.string.str_no_answer);
                studentAnswerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
                answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
            } else {
                //提交
                answerTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
                answerTitle = getString(R.string.str_look_image);
                studentAnswerTextV.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable
                        .green_10dp_stroke_green));
                studentAnswerTextV.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_green));
                studentAnswerTextV.setPadding(DensityUtils.dp2px(getActivity(), 7),
                        DensityUtils.dp2px(getActivity(), 2),
                        DensityUtils.dp2px(getActivity(), 7),
                        DensityUtils.dp2px(getActivity(), 2));
                studentAnswerTextV.setOnClickListener(v -> {
                    //打开学生提交的答案
                    List<MediaData> mediaDataList = exerciseData.getDatas();
                    if (mediaDataList == null || mediaDataList.size() == 0) {
                        return;
                    }
                    List<ImageInfo> resourceInfoList = new ArrayList<>();
                    for (int i = 0; i < mediaDataList.size(); i++) {
                        MediaData data = mediaDataList.get(i);
                        ImageInfo newResourceInfo = new ImageInfo();
                        newResourceInfo.setResourceUrl(data.resourceurl);
                        newResourceInfo.setTitle(exerciseData.getName());
                        resourceInfoList.add(newResourceInfo);
                    }
                    GalleryActivity.newInstance(getActivity(), resourceInfoList, true, 0, false, false, false);
                });
            }
            studentAnswerTextV.setText(answerTitle);
        }
        //参考答案
        String rightAnswer = exerciseData.getRight_answer();
        if (!TextUtils.isEmpty(rightAnswer)) {
            textTextV.setText(rightAnswer);
            textTextV.setVisibility(View.VISIBLE);
        }

        String rightAnswerResUrl = exerciseData.getRight_answer_res_url();
        String rightAnswerName = exerciseData.getRight_answer_res_name();
        String rightAnswerResId = exerciseData.getRight_answer_res_id();
        if (!TextUtils.isEmpty(rightAnswerResUrl)) {
            //查看图片
            rightTextV.setVisibility(View.VISIBLE);
            rightTextV.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable
                    .green_10dp_stroke_green));
            rightTextV.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_green));
            rightTextV.setText(getString(R.string.str_look_image));
            rightTextV.setPadding(DensityUtils.dp2px(getActivity(), 7),
                    DensityUtils.dp2px(getActivity(), 2),
                    DensityUtils.dp2px(getActivity(), 7),
                    DensityUtils.dp2px(getActivity(), 2));
            rightTextV.setOnClickListener(v -> {
                //打开参考答案
                openImage(rightAnswerResUrl, rightAnswerName,rightAnswerResId);
            });
        }
        answerDetailLayout.addView(view);
    }

    /**
     * 听力原文
     */
    private void showListenData() {
        String srcText = exerciseData.getSrc_text();
        String srcResUrl = exerciseData.getSrc_res_url();
        String srcResName = exerciseData.getSrc_res_name();
        String srcResId = exerciseData.getSrc_res_id();
        if (questionType == LearnTaskCardType.LISTEN_SINGLE_SELECTION
                || questionType == LearnTaskCardType.HEARING_JUDGMENT
                || questionType == LearnTaskCardType.LISTEN_FILL_CONTENT) {
            if (!TextUtils.isEmpty(srcText) || !TextUtils.isEmpty(srcResUrl) || !TextUtils.isEmpty(srcResName)) {
                llDetailLayout.setVisibility(View.VISIBLE);
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_parsing_detail, null);
                //学生的答案
                LinearLayout studentAnswerLayout = (LinearLayout) view.findViewById(R.id.ll_my_answer);
                LinearLayout answerRightLayout = (LinearLayout) view.findViewById(R.id.ll_reference_answer);
                answerRightLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.text_white));
                studentAnswerLayout.setVisibility(View.GONE);
                if (fromAnswerAnalysis) {
                    answerRightLayout.setPadding(0,
                            DensityUtils.dp2px(mContext, 5),
                            DensityUtils.dp2px(mContext, 5),
                            DensityUtils.dp2px(mContext, 5));
                }
                TextView leftRightTextV = (TextView) view.findViewById(R.id.tv_left_right_title);
                TextView picImageTextV = (TextView) view.findViewById(R.id.tv_correct_right_answer);
                TextView textTextV = (TextView) view.findViewById(R.id.tv_answer_text);
                String leftRightTitle = "【" + getString(R.string.str_listen_article) + "】";
                leftRightTextV.setText(leftRightTitle);
                leftRightTextV.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
                if (!TextUtils.isEmpty(srcText)) {
                    textTextV.setText(srcText);
                    textTextV.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(srcResUrl)) {
                    picImageTextV.setVisibility(View.VISIBLE);
                    picImageTextV.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable
                            .green_10dp_stroke_green));
                    picImageTextV.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_green));
                    picImageTextV.setText(getString(R.string.str_look_image));
                    picImageTextV.setPadding(DensityUtils.dp2px(getActivity(), 7),
                            DensityUtils.dp2px(getActivity(), 2),
                            DensityUtils.dp2px(getActivity(), 7),
                            DensityUtils.dp2px(getActivity(), 2));
                    picImageTextV.setOnClickListener(v -> {
                        //打开参考答案
                        openImage(srcResUrl, srcResName,srcResId);
                    });
                }
                llDetailLayout.addView(view);
            }
        }
    }

    private void openImage(String resUrl, String srcResName,String srcResId) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        if (resUrl.contains(",")) {
            String[] splitArray = resUrl.split(",");
            String[] titleArray = null;
            if (!TextUtils.isEmpty(srcResName)) {
                titleArray = srcResName.split(",");
            }
            if (splitArray.length > 0) {
                for (int m = 0; m < splitArray.length; m++) {
                    ImageInfo newResourceInfo = new ImageInfo();
                    newResourceInfo.setResourceUrl(splitArray[m]);
                    if (titleArray != null && titleArray.length > m) {
                        newResourceInfo.setTitle(titleArray[m]);
                    }
                    resourceInfoList.add(newResourceInfo);
                }
            }
        } else {
            ImageInfo newResourceInfo = new ImageInfo();
            newResourceInfo.setTitle(srcResName);
            newResourceInfo.setResourceUrl(resUrl);
            resourceInfoList.add(newResourceInfo);
        }
        //判断是不是ppt以及pdf
        String analysisUrl = resourceInfoList.get(0).getResourceUrl();
        if ((analysisUrl.endsWith(".pdf")
                || analysisUrl.endsWith(".ppf")
                || analysisUrl.endsWith(".doc"))
                && !TextUtils.isEmpty(srcResId)){
            if (srcResId.contains(",")){
                srcResId = srcResId.split(",")[0];
            }
            int resourceType = MaterialResourceType.PPT;
            if (analysisUrl.endsWith("pdf")){
                resourceType = MaterialResourceType.PDF;
            } else if (analysisUrl.endsWith(".doc")){
                resourceType = MaterialResourceType.DOC;
            }
            ResourceInfoTag infoTag = new ResourceInfoTag();
            infoTag.setResId(srcResId + "-" + resourceType);
            infoTag.setResourceType(resourceType);
            infoTag.setTitle(srcResName);
            WatchWawaCourseResourceOpenUtils.openPDFAndPPTDetails(getActivity(), infoTag,
                    true, false,false);
        } else {
            GalleryActivity.newInstance(getActivity(), resourceInfoList, true, 0, false, false, false);
        }
    }

    private void loadBarChartData() {
        if (exerciseData == null) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("Id", exerciseData.getId());
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(mContext, ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
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
                                    barChartDataInfoList = JSONObject.parseArray(dataListArray.toString(), BarChartDataInfo.class);
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
                showBarData();
            }
        };
        RequestHelper.sendPostRequest(mContext, ServerUrl.LOAD_EQANSWER_ANALYSIS_DETAILBYID_BASE_URL, param, listener);
    }

    /**
     * 柱状图的数据
     */
    private void showBarData() {
        if (barChartDataInfoList != null && barChartDataInfoList.size() > 0) {
            llBarLayout.setVisibility(View.VISIBLE);
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bar_chart, null);
            BarChartHelper barChartHelper = new BarChartHelper(mContext,exerciseData,barChartDataInfoList);
            barChartHelper.initView(view);
            llBarLayout.addView(view);
        }
    }

}
