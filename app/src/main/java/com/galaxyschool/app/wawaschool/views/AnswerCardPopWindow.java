package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.adapter.DoTaskOrderAnswerQuestionAdapter;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.learnTaskCardData;
import com.libs.yilib.pickimages.MediaInfo;
import com.libs.yilib.pickimages.PickMediasActivity;
import com.libs.yilib.pickimages.PickMediasFragment;
import com.libs.yilib.pickimages.PickMediasParam;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.client.pojo.LearnTaskCardType;
import com.lqwawa.tools.ResourceUtils;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.pennote.PenNoteImageActivity;
import com.osastudio.common.utils.FileUtils;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.PhotoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 答题卡
 */

public class AnswerCardPopWindow extends PopupWindow {
    private int HANDWRITING_IMAGE_LIST_REQUEST_CODE = 1111;
    private Activity mContext;
    private View mRootView;
    //题目答题list选项
    private LinearLayout testQuestionsLayout;
    private List<String> checkAnswerDataList = new ArrayList<>();
    private LearnTaskInfo taskInfo;
    private DoTaskOrderAnswerQuestionAdapter currentAdapter;
    private String cameraImageLocalPath;
    private String handwritingPathFolder = Utils.LQ_TEMP_FOLDER + "noteImageDir";//语音评测文件夹路径
    private ExerciseAnswerCardParam cardParam;
    private String[] questionTypeName;

    public AnswerCardPopWindow(Context context, ExerciseAnswerCardParam cardParam) {
        super(context);
        this.mContext = (Activity) context;
        this.cardParam = cardParam;
        this.mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_answercard_popwindow, null);
        initViews();
        loadData();
        initData();
        createFolderPath();
    }

    private void initViews() {
        setProperty();
        questionTypeName = mContext.getResources().getStringArray(R.array.array_question_type_name);
        testQuestionsLayout = (LinearLayout) mRootView.findViewById(R.id.ll_root_layout);
        String[] checkList = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        checkAnswerDataList.addAll(Arrays.asList(checkList));
        TextView titleTextV = (TextView) mRootView.findViewById(R.id.tv_answer_card_title);
        titleTextV.setOnClickListener((v) -> {
//            AnswerParsingActivity.start(mContext,learnTaskPageInfo.getExercise_items(),0);
        });
    }

    private void createFolderPath() {
        //先删除之前可能存在的folderPath 避免.mp3重复
        FileUtils.deleteDir(handwritingPathFolder);
        //创建文件夹
        FileUtils.createOrExistsDir(handwritingPathFolder);
    }

    private void loadData() {
        if (cardParam == null) {
            return;
        }
        JSONArray jsonArray = JSONObject.parseArray(cardParam.getExerciseAnswerString());
        if (jsonArray != null && jsonArray.size() > 0) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            taskInfo = JSONObject.parseObject(jsonObject.toString(), LearnTaskInfo.class);
        }
    }

    private void initData() {
        if (taskInfo != null) {
            List<ExerciseItem> exerciseItems = taskInfo.getExercise_item_list();
            if (exerciseItems != null && exerciseItems.size() > 0) {
                for (int i = 0; i < exerciseItems.size(); i++) {
                    ExerciseItem item = exerciseItems.get(i);
                    if (item != null && !TextUtils.isEmpty(item.getType())) {
                        int type = Integer.valueOf(item.getType());
                        dealDiffTypeData(type, item);
                    }
                }
            }
        }
    }

    private void dealDiffTypeData(int type, ExerciseItem data) {
        View answerCardItemLayout = LayoutInflater.from(mContext).inflate(R.layout.item_answercard_view, null);
        //试题的序号
        TextView subjectNumberTextV = (TextView) answerCardItemLayout.findViewById(R.id.tv_title_number);
        subjectNumberTextV.setText(data.getIndex() + ".");
        subjectNumberTextV.setVisibility(View.VISIBLE);
        //试题的title
        String typeName = "("+ questionTypeName[type - 1]+")";
        TextView subjectTitleTextV = (TextView) answerCardItemLayout.findViewById(R.id.tv_title_type);
        if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
            typeName = "(" + data.getType_name() + ")";
            //增加hint标识
            String typeTitle = data.getName();
            String limitCountHint = mContext.getString(R.string.str_max_select_image_count, 6);
            String titleName = typeTitle + limitCountHint + typeName;
            int startIndex = titleName.indexOf(limitCountHint);
            SpannableString systemColorString = new SpannableString(titleName);
            systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R
                    .color.text_normal)), startIndex, startIndex + limitCountHint.length(), Spannable
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            subjectTitleTextV.setText(systemColorString);
        } else {
            String questionTitle = data.getName();
            questionTitle = questionTitle + typeName;
            subjectTitleTextV.setText(questionTitle);
        }
        subjectTitleTextV.setVisibility(View.VISIBLE);
        int childCount = 0;
        if (!TextUtils.isEmpty(data.getItem_count())) {
            childCount = Integer.valueOf(data.getItem_count());
        }
        switch (type) {
            case LearnTaskCardType.SINGLE_CHOICE_QUESTION://单选题
            case LearnTaskCardType.MULTIPLE_CHOICE_QUESTIONS://多选题
            case LearnTaskCardType.LISTEN_SINGLE_SELECTION://听力单选
            case LearnTaskCardType.SINGLE_CHOICE_CORRECTION://单选改错题
                List<learnTaskCardData> choiceData = new ArrayList<>();
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        learnTaskCardData cardData = new learnTaskCardData();
                        if (type == LearnTaskCardType.MULTIPLE_CHOICE_QUESTIONS) {
                            //多选
                            cardData.setSelectType(1);
                        } else {
                            //单选
                            cardData.setSelectType(0);
                        }
                        cardData.setQuestionType(type);
                        cardData.setIsSelect(false);
                        cardData.setItemTitle(checkAnswerDataList.get(i));
                        choiceData.add(cardData);
                    }
                    data.setCardData(choiceData);
                    handleCheckChoiceAnswer(data, answerCardItemLayout);
                }
                break;
            case LearnTaskCardType.JUDGMENT_PROBLEM://判断题
            case LearnTaskCardType.HEARING_JUDGMENT://听力判断题
                List<learnTaskCardData> judgmentData = new ArrayList<>();
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        learnTaskCardData cardData = new learnTaskCardData();
                        cardData.setSelectType(0);
                        cardData.setQuestionType(type);
                        cardData.setIsSelect(false);
                        if (i == 0) {
                            cardData.setItemTitle("T");
                        } else {
                            cardData.setItemTitle("F");
                        }
                        judgmentData.add(cardData);
                    }
                    data.setCardData(judgmentData);
                    handleCheckChoiceAnswer(data, answerCardItemLayout);
                }
                break;
            case LearnTaskCardType.FILL_CONTENT://填空题
            case LearnTaskCardType.LISTEN_FILL_CONTENT://听力填空
                if (childCount > 0) {
                    handleFillInAnswerData(data, childCount, answerCardItemLayout);
                }
                break;
            case LearnTaskCardType.SUBJECTIVE_PROBLEM://主观题
                handleSubjectProblem(data, answerCardItemLayout);
                break;
        }
    }

    /**
     * 单选或者多选
     */
    private void handleCheckChoiceAnswer(ExerciseItem data, View rootLayout) {
        //多项选择的list
        MyGridView myGridView = (MyGridView) rootLayout.findViewById(R.id.gv_check_answer);
        myGridView.setVisibility(View.VISIBLE);
        DoTaskOrderAnswerQuestionAdapter answerQuestionAdapter = new DoTaskOrderAnswerQuestionAdapter(mContext, data.getCardData());
        myGridView.setAdapter(answerQuestionAdapter);
        if (TextUtils.equals(data.getType(), String.valueOf(LearnTaskCardType.SINGLE_CHOICE_CORRECTION))) {
            //单选改错
            //填空题的父layout
            LinearLayout fillInBlankLayout = (LinearLayout) rootLayout.findViewById(R.id.ll_fill);
            fillInBlankLayout.setVisibility(View.VISIBLE);
            TextView spaceTitle = (TextView) rootLayout.findViewById(R.id.tv_input_title);
            spaceTitle.setText(mContext.getString(R.string.str_revising));
        }
        testQuestionsLayout.addView(rootLayout);
    }

    /**
     * 填空
     */
    private void handleFillInAnswerData(ExerciseItem data, int itemCount, View rootLayout) {
        //填空题的父layout
        LinearLayout fillInBlankLayout = (LinearLayout) rootLayout.findViewById(R.id.ll_fill_in_blank);
        fillInBlankLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < itemCount; i++) {
            //填空题的子view
            View fillInView = LayoutInflater.from(mContext).inflate(R.layout.layout_input_answer_content, null);
            //空的title
            TextView spaceTitle = (TextView) fillInView.findViewById(R.id.tv_input_title);
            String title = mContext.getString(R.string.str_space) + (i + 1);
            spaceTitle.setText(title);
            fillInBlankLayout.addView(fillInView);
        }
        testQuestionsLayout.addView(rootLayout);
    }

    /**
     * 主观题
     */
    private void handleSubjectProblem(ExerciseItem data, View rootLayout) {
        List<learnTaskCardData> cardDatas = data.getCardData();
        if (cardDatas == null) {
            cardDatas = new ArrayList<>();
            data.setCardData(cardDatas);
        }
        //主观题的根layout
        rootLayout.findViewById(R.id.ll_subject_problem).setVisibility(View.VISIBLE);
        MyGridView myGridView = (MyGridView) rootLayout.findViewById(R.id.gv_check_answer);
        myGridView.setVisibility(View.VISIBLE);
        //主观题显示3个tab
        myGridView.setNumColumns(3);
        DoTaskOrderAnswerQuestionAdapter answerQuestionAdapter = new DoTaskOrderAnswerQuestionAdapter(mContext, cardDatas);
        myGridView.setAdapter(answerQuestionAdapter);
        rootLayout.findViewById(R.id.tv_take_photo).setOnClickListener(v -> taskPhoto(answerQuestionAdapter));
        rootLayout.findViewById(R.id.tv_photo).setOnClickListener(v -> loadPhotoPic(answerQuestionAdapter));
        rootLayout.findViewById(R.id.tv_handwriting).setOnClickListener(v -> enterHandWritingActivity(answerQuestionAdapter));
        testQuestionsLayout.addView(rootLayout);
    }

    private void taskPhoto(DoTaskOrderAnswerQuestionAdapter adapter) {
        currentAdapter = adapter;
        if (currentAdapter == null){
            return;
        }
        int size = currentAdapter.getCount();
        if (size >= 6){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(mContext,mContext.getString(R.string
                    .str_max_select_image_count_limit,6));
            return;
        }
        long dateTaken = System.currentTimeMillis();
        String rootPath = Utils.LQ_TEMP_FOLDER + "questionImage" + File.separator;
        BaseUtils.createLocalDiskPath(rootPath);
        cameraImageLocalPath = rootPath + Long.toString(dateTaken) + ".jpg";
        PhotoUtils.startTakePhoto(mContext, new File(cameraImageLocalPath), PhotoUtils.REQUEST_CODE_TAKE_PHOTO);
    }

    private void loadPhotoPic(DoTaskOrderAnswerQuestionAdapter adapter) {
        currentAdapter = adapter;
        if (currentAdapter == null){
            return;
        }
        int size = currentAdapter.getCount();
        if (size >= 6){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(mContext,mContext.getString(R.string
                    .str_max_select_image_count_limit,6));
            return;
        }
        PickMediasParam param = new PickMediasParam();
        param.mColumns = 4;
        param.mConfirmBtnName = mContext.getString(ResourceUtils.getStringId(mContext, "confirm"));
        // param.mDefaultImage = R.drawable.btn_camera;
        param.mIsActivityCalled = true;
        param.mLimitReachedTips = mContext.getString(ResourceUtils.getStringId(mContext, "media_select_full_msg"));
        param.mPickLimitCount = 6 - size;
        param.mSearchPath = "/mnt";
        param.mShowCountFormatString = mContext.getString(ResourceUtils.getStringId(mContext, "media_show_count_msg"));
        param.mShowCountMode = 1;
        Intent intent = new Intent(mContext, PickMediasActivity.class);
        intent.putExtra(PickMediasFragment.PICK_IMG_PARAM, param);
        mContext.startActivityForResult(intent, PhotoUtils.REQUEST_CODE_FETCH_PHOTO);
    }

    private void enterHandWritingActivity(DoTaskOrderAnswerQuestionAdapter adapter) {
        currentAdapter = adapter;
        if (currentAdapter == null){
            return;
        }
        int size = currentAdapter.getCount();
        if (size >= 6){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(mContext,mContext.getString(R.string
                    .str_max_select_image_count_limit,6));
            return;
        }
        currentAdapter = adapter;
        Intent intent = new Intent(mContext, PenNoteImageActivity.class);
        Bundle args = new Bundle();
        args.putString(PenNoteImageActivity.EXTRA_NOTE_IMAGE_DIR, handwritingPathFolder);
        intent.putExtras(args);
        mContext.startActivityForResult(intent, HANDWRITING_IMAGE_LIST_REQUEST_CODE);
    }

    /**
     * 获取学生提交的答案
     */
    public void commitAnswerQuestion() {
        if (taskInfo != null) {
            List<ExerciseItem> exerciseItems = taskInfo.getExercise_item_list();
            if (exerciseItems != null && exerciseItems.size() > 0) {
                for (int i = 0; i < exerciseItems.size(); i++) {
                    ExerciseItem item = exerciseItems.get(i);
                    if (item != null && !TextUtils.isEmpty(item.getType())) {
                        int type = Integer.valueOf(item.getType());
                        getStudentCommitData(type, item, i);
                    }
                }
                DoTaskOrderHelper helper = new DoTaskOrderHelper(mContext);
                helper.setExerciseAnswerCardParam(cardParam).
                        setExerciseItem(exerciseItems).
                        commit();
            }
        }
    }

    private void getStudentCommitData(int type, ExerciseItem data, int position) {
        switch (type) {
            case LearnTaskCardType.SINGLE_CHOICE_QUESTION://单选题
            case LearnTaskCardType.MULTIPLE_CHOICE_QUESTIONS://多选题
            case LearnTaskCardType.LISTEN_SINGLE_SELECTION://听力单选
            case LearnTaskCardType.SINGLE_CHOICE_CORRECTION://单选改错题
            case LearnTaskCardType.JUDGMENT_PROBLEM://判断题
            case LearnTaskCardType.HEARING_JUDGMENT://听力判断题
                getCheckChoiceAnswer(data, position);
                break;
            case LearnTaskCardType.FILL_CONTENT://填空题
            case LearnTaskCardType.LISTEN_FILL_CONTENT://听力填空
                getCheckFillInContentData(data, position);
                break;
            case LearnTaskCardType.SUBJECTIVE_PROBLEM://主观题
                break;
        }
    }

    private void getCheckChoiceAnswer(ExerciseItem data, int position) {
        View itemView = testQuestionsLayout.getChildAt(position);
        List<learnTaskCardData> list = data.getCardData();
        if (list != null && list.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                learnTaskCardData taskCardData = list.get(i);
                if (taskCardData.isSelect()) {
                    if (stringBuilder.length() == 0) {
                        stringBuilder.append(i + 1);
                    } else {
                        stringBuilder.append(",").append(i + 1);
                    }
                }
            }
            String answerString = stringBuilder.toString();
            if (!TextUtils.isEmpty(answerString)) {
                //选项的答案
                data.setStudent_answer(answerString);
                LogUtils.log("TEST", "position=" + position + " & " + "answerString ="
                        + answerString);
            } else {
                data.setStudent_answer("");
            }
        }
        if (TextUtils.equals(data.getType(), String.valueOf(LearnTaskCardType.SINGLE_CHOICE_CORRECTION))) {
            //单选改错
            LinearLayout fillInBlankLayout = (LinearLayout) itemView.findViewById(R.id.ll_fill);
            if (fillInBlankLayout != null) {
                ContainsEmojiEditText editText = (ContainsEmojiEditText) fillInBlankLayout.findViewById(R.id
                        .et_input_content);
                if (editText != null) {
                    String correctContent = editText.getText().toString().trim();
                    JSONObject studentAnswerObj = new JSONObject();
                    studentAnswerObj.put("item_index", data.getStudent_answer());
                    studentAnswerObj.put("answer_text", correctContent);
                    if (!TextUtils.isEmpty(correctContent)) {
                        LogUtils.log("TEST", "position=" + position + " & " + "correctContent ="
                                + correctContent);
                        JSONObject rightAnswer = JSONObject.parseObject(data.getRight_answer());
                        if (rightAnswer != null) {
                            String indexItem = rightAnswer.getString("item_index");
                            String answerText = rightAnswer.getString("answer_text");
                            if (TextUtils.equals(indexItem, data.getStudent_answer())
                                    && TextUtils.equals(answerText, correctContent)) {
                                //答案正确
                                data.setStudent_score(data.getScore());
                                LogUtils.log("TEST", "position=" + position + " & " + "getScore ="
                                        + data.getScore());
                            }
                        }
                    }
                    data.setStudent_answer(studentAnswerObj.toString());
                }
            }
        } else {
            if (TextUtils.equals(data.getStudent_answer(), data.getRight_answer())) {
                data.setStudent_score(data.getScore());
                LogUtils.log("TEST", "position=" + position + " & " + "getScore ="
                        + data.getScore());
            }
        }
    }

    private void getCheckFillInContentData(ExerciseItem data, int position) {
        View itemView = testQuestionsLayout.getChildAt(position);
        if (itemView != null) {
            LinearLayout fillInBlankLayout = (LinearLayout) itemView.findViewById(R.id.ll_fill_in_blank);
            int count = fillInBlankLayout.getChildCount();
            if (count > 0) {
                String[] fillString = new String[count];
                for (int i = 0; i < count; i++) {
                    View fillInView = fillInBlankLayout.getChildAt(i);
                    if (fillInView != null) {
                        ContainsEmojiEditText editText = (ContainsEmojiEditText) fillInView.findViewById(R.id
                                .et_input_content);
                        if (editText != null) {
                            String editContent = editText.getText().toString().trim();
                            fillString[i] = editContent;
                        }
                    }
                }
                //比较填空题的答案
                if (!TextUtils.isEmpty(data.getRight_answer()) && !TextUtils.isEmpty(data.getSubscore())) {
                    JSONArray rightAnswerArray = JSONObject.parseArray(data.getRight_answer());
                    String[] subScoreArray = data.getSubscore().split(",");
                    if (rightAnswerArray != null && subScoreArray.length > 0) {
                        double totalScore = 0;
                        StringBuilder stuSubScore = new StringBuilder();
                        JSONArray studentAnswerArray = new JSONArray();
                        for (int i = 0; i < count; i++) {
                            studentAnswerArray.add(fillString[i]);
                            String spaceString = rightAnswerArray.getString(i);
                            if (TextUtils.equals(spaceString, fillString[i])) {
                                //相等
                                if (stuSubScore.length() == 0) {
                                    stuSubScore.append(subScoreArray[i]);
                                } else {
                                    stuSubScore.append(",").append(subScoreArray[i]);
                                }
                                if (!TextUtils.isEmpty(subScoreArray[i])) {
                                    totalScore = totalScore + Double.valueOf(subScoreArray[i]);
                                }
                            } else {
                                if (stuSubScore.length() == 0) {
                                    stuSubScore.append(0);
                                } else {
                                    stuSubScore.append(",").append(0);
                                }
                            }
                        }
                        LogUtils.log("TEST", "position=" + position + " & " + "stuSubScore ="
                                + stuSubScore.toString() + "&" + "studentAnswerObj=" + studentAnswerArray.toString());
                        data.setStudent_score(Utils.changeDoubleToInt(String.valueOf(totalScore)));
                        data.setStudent_subscore(stuSubScore.toString());
                        data.setStudent_answer(studentAnswerArray.toString());
                    }
                }
            }
        }
    }

    public void setRequestCodeData(int requestCode, Intent data) {
        boolean beyondMaxCount = false;
        if (requestCode == PhotoUtils.REQUEST_CODE_FETCH_PHOTO) {
            //相册
            if (data != null && currentAdapter != null) {
                ArrayList<MediaInfo> imageInfos = data.getParcelableArrayListExtra(PickMediasFragment.PICK_IMG_RESULT);
                if (imageInfos != null && imageInfos.size() > 0) {
                    for (MediaInfo mediaInfo : imageInfos) {
                        String savePath = mediaInfo.mPath;
                        learnTaskCardData cardData = new learnTaskCardData();
                        cardData.setQuestionType(LearnTaskCardType.SUBJECTIVE_PROBLEM);
                        cardData.setAnswerPath(savePath);
                        String[] splitArray = savePath.split("/");
                        if (splitArray.length > 0) {
                            String title = splitArray[splitArray.length - 1];
                            if (!TextUtils.isEmpty(title)) {
                                if (title.contains(".")) {
                                    title = title.substring(0, title.indexOf("."));
                                }
                                cardData.setItemTitle(title);
                            }
                        }
                        if (currentAdapter.getData().size() < 6) {
                            currentAdapter.getData().add(cardData);
                        } else {
                            beyondMaxCount = true;
                        }
                    }
                    currentAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == PhotoUtils.REQUEST_CODE_TAKE_PHOTO) {
            //拍照
            String path = null;
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    path = PhotoUtils.getImageAbsolutePath((mContext), uri);
                }
            }
            if (path == null) {
                if (cameraImageLocalPath != null && new File(cameraImageLocalPath).exists()) {
                    path = cameraImageLocalPath;
                }
            }
            if (path != null && currentAdapter != null) {
                learnTaskCardData cardData = new learnTaskCardData();
                cardData.setQuestionType(LearnTaskCardType.SUBJECTIVE_PROBLEM);
                cardData.setAnswerPath(path);
                String[] splitArray = path.split("/");
                if (splitArray.length > 0) {
                    String title = splitArray[splitArray.length - 1];
                    if (!TextUtils.isEmpty(title)) {
                        if (title.contains(".")) {
                            title = title.substring(0, title.indexOf("."));
                        }
                        cardData.setItemTitle(title);
                    }
                }
                if (currentAdapter.getData().size() < 6) {
                    currentAdapter.getData().add(cardData);
                } else {
                    beyondMaxCount = true;
                }
                currentAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == HANDWRITING_IMAGE_LIST_REQUEST_CODE) {
            //手写板
            if (data != null && currentAdapter != null) {
                String imagePath = data.getStringExtra(PenNoteImageActivity.EXTRA_NOTE_IMAGES);
                if (imagePath != null) {
                    JSONArray jsonArray = JSONObject.parseArray(imagePath);
                    if (jsonArray != null && jsonArray.size() > 0) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            learnTaskCardData cardData = new learnTaskCardData();
                            cardData.setQuestionType(LearnTaskCardType.SUBJECTIVE_PROBLEM);
                            String path = jsonArray.get(i).toString();
                            if (path != null) {
                                cardData.setAnswerPath(path);
                                if (!TextUtils.isEmpty(path)) {
                                    String[] splitArray = path.split("/");
                                    if (splitArray.length > 0) {
                                        String title = splitArray[splitArray.length - 1];
                                        if (!TextUtils.isEmpty(title)) {
                                            if (title.contains(".")) {
                                                title = title.substring(0, title.indexOf("."));
                                            }
                                            cardData.setItemTitle(title);
                                        }
                                    }
                                }
                                if (currentAdapter.getData().size() < 6) {
                                    currentAdapter.getData().add(cardData);
                                } else {
                                    beyondMaxCount = true;
                                }
                            }
                        }
                        if (currentAdapter.getData().size() > 0) {
                            currentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        if (beyondMaxCount){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(mContext,mContext.getString(R.string
                    .str_max_select_image_count_limit,6));
        }
    }

    /**
     * 判断是否完成了所有的作业
     *
     * @return
     */
    public boolean isFinishAllQuestion() {
        if (taskInfo != null) {
            List<ExerciseItem> exerciseItems = taskInfo.getExercise_item_list();
            if (exerciseItems != null && exerciseItems.size() > 0) {
                for (int i = 0; i < exerciseItems.size(); i++) {
                    ExerciseItem item = exerciseItems.get(i);
                    if (item != null && !TextUtils.isEmpty(item.getType())) {
                        int type = Integer.valueOf(item.getType());
                        getStudentCommitData(type, item, i);
                    }
                }
                for (int m = 0; m < exerciseItems.size(); m++) {
                    ExerciseItem itemData = exerciseItems.get(m);
                    int type = Integer.valueOf(itemData.getType());
                    if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                        if (itemData.getCardData() == null || itemData.getCardData().size() == 0) {
                            return false;
                        }
                    } else if (TextUtils.isEmpty(itemData.getStudent_answer())) {
                        return false;
                    }

                    if (type == LearnTaskCardType.SINGLE_CHOICE_CORRECTION) {
                        //单选改错
                        String answerObj = itemData.getStudent_answer();
                        if (TextUtils.isEmpty(answerObj)) {
                            return false;
                        }
                        JSONObject jsonObject = JSONObject.parseObject(answerObj);
                        String itemIndex = jsonObject.getString("item_index");
                        String answerText = jsonObject.getString("answer_text");
                        if (TextUtils.isEmpty(itemIndex) || TextUtils.isEmpty(answerText)) {
                            return false;
                        }
                    }

                    if (type == LearnTaskCardType.FILL_CONTENT
                            || type == LearnTaskCardType.LISTEN_FILL_CONTENT) {
                        //填空和听力填空
                        String fillInString = itemData.getStudent_answer();
                        if (TextUtils.isEmpty(fillInString)) {
                            return false;
                        }
                        JSONArray jsonArray = JSONObject.parseArray(fillInString);
                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                if (TextUtils.isEmpty(jsonArray.get(i).toString())) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private void setProperty() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.setContentView(mRootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
    }

    public void showPopupMenu(View v) {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void showPopupMenu() {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void showPopupMenuRight() {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, ScreenUtils.getScreenWidth
                    (mContext), 0);
        } else {
            this.dismiss();
        }
    }

    /**
     * 重新设定窗口大小
     *
     * @param ratio
     */
    public void resizePopupWindowWith(float ratio) {
        if (ratio <= 0) {
            return;
        }
        Display display = mContext.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * ratio);
        setWidth(width);
    }
}
