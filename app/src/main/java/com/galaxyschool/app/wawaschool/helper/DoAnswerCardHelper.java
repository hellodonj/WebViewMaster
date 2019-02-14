package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.adapter.DoTaskOrderAnswerQuestionAdapter;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.learnTaskCardData;
import com.galaxyschool.app.wawaschool.views.MyGridView;
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
 * ======================================================
 * Describe:自动批阅辅助类
 * ======================================================
 */
public class DoAnswerCardHelper {
    private Activity activity;
    private List<String> checkAnswerDataList = new ArrayList<>();
    private String[] questionTypeName;
    private LinearLayout answerRootLayout;
    private DoTaskOrderAnswerQuestionAdapter currentAdapter;
    private String cameraImageLocalPath;
    private int HANDWRITING_IMAGE_LIST_REQUEST_CODE = 1111;
    private String handwritingPathFolder = Utils.LQ_TEMP_FOLDER + "noteImageDir";//语音评测文件夹路径

    public DoAnswerCardHelper(Activity activity,LinearLayout answerRootLayout) {
        this.activity = activity;
        this.answerRootLayout = answerRootLayout;
        questionTypeName = activity.getResources().getStringArray(R.array.array_question_type_name);
        String[] checkList = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        checkAnswerDataList.addAll(Arrays.asList(checkList));
        createFolderPath();
    }

    private void createFolderPath() {
        //先删除之前可能存在的folderPath 避免.mp3重复
        FileUtils.deleteDir(handwritingPathFolder);
        //创建文件夹
        FileUtils.createOrExistsDir(handwritingPathFolder);
    }

    public void dealDiffTypeData(int type, ExerciseItem data) {
        View answerCardItemLayout = LayoutInflater.from(activity).inflate(R.layout.item_answercard_view, null);
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
            String limitCountHint = activity.getString(R.string.str_max_select_image_count, 6);
            String titleName = typeTitle + limitCountHint + typeName;
            int startIndex = titleName.indexOf(limitCountHint);
            SpannableString systemColorString = new SpannableString(titleName);
            systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R
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
                if (childCount > 0) {
                    List<learnTaskCardData> choiceData = data.getCardData();
                    if (choiceData == null || choiceData.size() == 0){
                        choiceData = new ArrayList<>();
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
                    }
                    handleCheckChoiceAnswer(data, answerCardItemLayout);
                }
                break;
            case LearnTaskCardType.JUDGMENT_PROBLEM://判断题
            case LearnTaskCardType.HEARING_JUDGMENT://听力判断题
                if (childCount > 0) {
                    List<learnTaskCardData> judgmentData = data.getCardData();
                    if (judgmentData == null || judgmentData.size() == 0) {
                        judgmentData = new ArrayList<>();
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
                    }
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
        DoTaskOrderAnswerQuestionAdapter answerQuestionAdapter =
                new DoTaskOrderAnswerQuestionAdapter(activity, data.getCardData());
        myGridView.setAdapter(answerQuestionAdapter);
        if (TextUtils.equals(data.getType(), String.valueOf(LearnTaskCardType.SINGLE_CHOICE_CORRECTION))) {
            //单选改错
            //填空题的父layout
            LinearLayout fillInBlankLayout = (LinearLayout) rootLayout.findViewById(R.id.ll_fill);
            fillInBlankLayout.setVisibility(View.VISIBLE);
            TextView spaceTitle = (TextView) rootLayout.findViewById(R.id.tv_input_title);
            spaceTitle.setText(activity.getString(R.string.str_revising));
        }
        answerRootLayout.addView(rootLayout);
    }

    /**
     * 填空
     */
    private void handleFillInAnswerData(ExerciseItem data, int itemCount, View rootLayout) {
        //填空题的父layout
        LinearLayout fillInBlankLayout = (LinearLayout) rootLayout.findViewById(R.id.ll_fill_in_blank);
        fillInBlankLayout.setVisibility(View.VISIBLE);
        String studentAnswer = data.getStudent_answer();
        JSONArray studentArray = null;
        if (!TextUtils.isEmpty(studentAnswer)){
            studentArray = JSONArray.parseArray(studentAnswer);
        }
        for (int i = 0; i < itemCount; i++) {
            //填空题的子view
            View fillInView = LayoutInflater.from(activity).inflate(R.layout.layout_input_answer_content, null);
            //空的title
            TextView spaceTitle = (TextView) fillInView.findViewById(R.id.tv_input_title);
            String title = activity.getString(R.string.str_space) + (i + 1);
            spaceTitle.setText(title);
            //防止编辑的title为空
            ContainsEmojiEditText editText = (ContainsEmojiEditText) fillInView.findViewById(R.id.et_input_content);
            if (studentArray != null && i < studentArray.size()){
                //存在拿出来显示
                editText.setText(studentArray.get(i).toString());
            }
            fillInBlankLayout.addView(fillInView);
        }
        answerRootLayout.addView(rootLayout);
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
        DoTaskOrderAnswerQuestionAdapter answerQuestionAdapter =
                new DoTaskOrderAnswerQuestionAdapter(activity, cardDatas);
        myGridView.setAdapter(answerQuestionAdapter);
        rootLayout.findViewById(R.id.tv_take_photo).setOnClickListener(v -> taskPhoto(answerQuestionAdapter));
        rootLayout.findViewById(R.id.tv_photo).setOnClickListener(v -> loadPhotoPic(answerQuestionAdapter));
        rootLayout.findViewById(R.id.tv_handwriting).setOnClickListener(v -> enterHandWritingActivity(answerQuestionAdapter));
        answerRootLayout.addView(rootLayout);
    }

    private void taskPhoto(DoTaskOrderAnswerQuestionAdapter adapter) {
        currentAdapter = adapter;
        if (currentAdapter == null){
            return;
        }
        int size = currentAdapter.getCount();
        if (size >= 6){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(activity,activity.getString(R.string
                    .str_max_select_image_count_limit,6));
            return;
        }
        long dateTaken = System.currentTimeMillis();
        String rootPath = Utils.LQ_TEMP_FOLDER + "questionImage" + File.separator;
        BaseUtils.createLocalDiskPath(rootPath);
        cameraImageLocalPath = rootPath + Long.toString(dateTaken) + ".jpg";
        PhotoUtils.startTakePhoto(activity, new File(cameraImageLocalPath),
                PhotoUtils.REQUEST_CODE_TAKE_PHOTO);
    }

    private void loadPhotoPic(DoTaskOrderAnswerQuestionAdapter adapter) {
        currentAdapter = adapter;
        if (currentAdapter == null){
            return;
        }
        int size = currentAdapter.getCount();
        if (size >= 6){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(activity,activity.getString(R.string
                    .str_max_select_image_count_limit,6));
            return;
        }
        PickMediasParam param = new PickMediasParam();
        param.mColumns = 4;
        param.mConfirmBtnName = activity.getString(ResourceUtils.getStringId(activity, "confirm"));
        // param.mDefaultImage = R.drawable.btn_camera;
        param.mIsActivityCalled = true;
        param.mLimitReachedTips = activity.getString(ResourceUtils.getStringId(activity,
                "media_select_full_msg"));
        param.mPickLimitCount = 6 - size;
        param.mSearchPath = "/mnt";
        param.mShowCountFormatString = activity.getString(ResourceUtils.getStringId(activity,
                "media_show_count_msg"));
        param.mShowCountMode = 1;
        Intent intent = new Intent(activity, PickMediasActivity.class);
        intent.putExtra(PickMediasFragment.PICK_IMG_PARAM, param);
        activity.startActivityForResult(intent, PhotoUtils.REQUEST_CODE_FETCH_PHOTO);
    }

    private void enterHandWritingActivity(DoTaskOrderAnswerQuestionAdapter adapter) {
        currentAdapter = adapter;
        if (currentAdapter == null){
            return;
        }
        int size = currentAdapter.getCount();
        if (size >= 6){
            //超过最大的数量给于一个提示
            TipMsgHelper.ShowMsg(activity,activity.getString(R.string
                    .str_max_select_image_count_limit,6));
            return;
        }
        currentAdapter = adapter;
        Intent intent = new Intent(activity, PenNoteImageActivity.class);
        Bundle args = new Bundle();
        args.putString(PenNoteImageActivity.EXTRA_NOTE_IMAGE_DIR, handwritingPathFolder);
        intent.putExtras(args);
        activity.startActivityForResult(intent, HANDWRITING_IMAGE_LIST_REQUEST_CODE);
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
                    path = PhotoUtils.getImageAbsolutePath((activity), uri);
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
            TipMsgHelper.ShowMsg(activity,activity.getString(R.string
                    .str_max_select_image_count_limit,6));
        }
    }

    public void getStudentCommitData(int type, ExerciseItem data, int position) {
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
        View itemView = answerRootLayout.getChildAt(position);
        if (itemView == null){
            return;
        }
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
        View itemView = answerRootLayout.getChildAt(position);
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
}
