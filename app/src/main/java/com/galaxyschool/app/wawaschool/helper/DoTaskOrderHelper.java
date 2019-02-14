package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.CompletedHomeworkListFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkCommitFragment;
import com.galaxyschool.app.wawaschool.pojo.AnswerAnalysisInfo;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.learnTaskCardData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.UploadDialog;
import com.galaxyschool.app.wawaschool.views.MarkScoreDialog;
import com.lqwawa.client.pojo.LearnTaskCardType;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.common.DoubleOperationUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;
import com.osastudio.common.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ======================================================
 * Describe:任务单批阅辅助类
 * ======================================================
 */
public class DoTaskOrderHelper {
    private Context mContext;
    private DialogHelper.LoadingDialog loadingDialog;
    private ExerciseAnswerCardParam cardParam;
    private List<ExerciseItem> exerciseItems;
    private int commitTaskId;//学生提交成功之后返回的commitTaskId
    private StringBuilder flagPositionBuilder = new StringBuilder();//多个图片主观题上传的标志位
    private String slidePath;//批阅答题卡的本地路径
    private AlertDialog mCommitDialog;
    private MarkScoreDialog mMarkScoreDialog;
    private UploadDialog.UploadDialogHandler handler;
    private ExerciseItem itemData;
    private String markScore;
    private boolean isUpdateAnswerDetail;

    public DoTaskOrderHelper(Context mContext) {
        this.mContext = mContext;
    }

    public DoTaskOrderHelper setExerciseAnswerCardParam(ExerciseAnswerCardParam cardParam) {
        this.cardParam = cardParam;
        return this;
    }

    public DoTaskOrderHelper setExerciseItem(List<ExerciseItem> exerciseItems) {
        this.exerciseItems = exerciseItems;
        return this;
    }

    public DoTaskOrderHelper setSlidePath(String slidePath) {
        this.slidePath = slidePath;
        return this;
    }

    public DoTaskOrderHelper setIsUpdateAnswerDetail(boolean isUpdateAnswerDetail){
        this.isUpdateAnswerDetail = isUpdateAnswerDetail;
        return this;
    }

    public DoTaskOrderHelper setUploadDialogHandler(UploadDialog.UploadDialogHandler handler) {
        this.handler = handler;
        return this;
    }

    public void commit() {
        commitStudentAnswerData();
    }

    public String getStudentAnswerData(){
        return getStudentDataList().toJSONString();
    }

    /**
     * 解析通过commitId拉取返回的数据
     * 单选改错（jsonObject） 和 填空(JsonArray) 有 "-" 分隔符
     */
    public static void analysisStudentCommitData(JSONArray jsonArray, List<ExerciseItem> items) {
        ExerciseItem data = null;
        outer:
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject != null) {
                data = new ExerciseItem();
                String EQId = jsonObject.getString("EQId");
                int EQType = jsonObject.getInteger("EQType");
                int EQState = jsonObject.getInteger("EQState");
                double EQScore = jsonObject.getDouble("EQScore");
                String EQAnswer = jsonObject.getString("EQAnswer");
                String SubjectiveResId = jsonObject.getString("SubjectiveResId");
                String SubjectiveResUrl = jsonObject.getString("SubjectiveResUrl");
                if (EQId.contains("-")) {
                    String[] splitArray = EQId.split("-");
                    if (items != null && items.size() > 0) {
                        for (ExerciseItem indexData : items) {
                            if (TextUtils.equals(indexData.getIndex(), splitArray[0])) {
                                //题号相同 区分 单选改错和填空
                                if (EQType == LearnTaskCardType.SINGLE_CHOICE_CORRECTION) {
                                    //单选改错
                                    String singleString = indexData.getStudent_answer();
                                    JSONObject singleObj = null;
                                    if (TextUtils.isEmpty(singleString)) {
                                        singleObj = new JSONObject();
                                    } else {
                                        singleObj = JSONObject.parseObject(singleString);
                                    }
                                    //答题
                                    if (singleObj != null) {
                                        if (Integer.valueOf(splitArray[1]) == 1) {
                                            //选择答案
                                            singleObj.put("item_index", EQAnswer);
                                        } else {
                                            //订正答案
                                            singleObj.put("answer_text", EQAnswer);
                                        }
                                        indexData.setStudent_answer(singleObj.toString());
                                    }
                                } else {
                                    //填空
                                    String fillInString = indexData.getStudent_answer();
                                    String fillStuSubScore = indexData.getStudent_subscore();
                                    JSONArray fillInArray = null;
                                    if (TextUtils.isEmpty(fillInString)) {
                                        fillInArray = new JSONArray();
                                    } else {
                                        fillInArray = JSONObject.parseArray(fillInString);
                                    }
                                    if (fillInArray != null) {
                                        fillInArray.add(EQAnswer);
                                    }
                                    if (TextUtils.isEmpty(fillStuSubScore)) {
                                        fillStuSubScore = String.valueOf(EQScore);
                                    } else {
                                        fillStuSubScore = fillStuSubScore + "," + String.valueOf
                                                (EQScore);
                                    }
                                    if (fillInArray != null) {
                                        indexData.setStudent_answer(fillInArray.toString());
                                    }
                                    indexData.setStudent_subscore(fillStuSubScore);
                                }
                                continue outer;
                            }
                        }
                    }
                } else {
                    data.setType(String.valueOf(EQType));
                    data.setIndex(EQId);
                    data.setStudent_answer(EQAnswer);
                    data.setStudent_score(String.valueOf(EQScore));
                    data.setEqState(EQState);
                    if (!TextUtils.isEmpty(SubjectiveResId) && !TextUtils.isEmpty(SubjectiveResUrl)) {
                        List<MediaData> mediaDataList = new ArrayList<>();
                        MediaData mediaData = null;
                        if (SubjectiveResId.contains(",")) {
                            String[] resIdArray = SubjectiveResId.split(",");
                            String[] resUrlArray = SubjectiveResUrl.split(",");
                            for (int j = 0; j < resIdArray.length; j++) {
                                mediaData = new MediaData();
                                String resId = resIdArray[j];
                                mediaData.resourceurl = resUrlArray[j];
                                if (resId.contains("-")) {
                                    String[] array = resId.split("-");
                                    mediaData.id = Integer.valueOf(array[0]);
                                    mediaData.type = Integer.valueOf(array[1]);
                                    mediaDataList.add(mediaData);
                                }
                            }
                        } else {
                            mediaData = new MediaData();
                            mediaData.resourceurl = SubjectiveResUrl;
                            if (SubjectiveResId.contains("-")) {
                                String[] array = SubjectiveResId.split("-");
                                mediaData.id = Integer.valueOf(array[0]);
                                mediaData.type = Integer.valueOf(array[1]);
                                mediaDataList.add(mediaData);
                            }
                        }
                        data.setDatas(mediaDataList);
                    }
                    items.add(data);
                }
            }
        }
    }

    public static void analysisAllCommitData(List<AnswerAnalysisInfo> analysisInfos, List<ExerciseItem> items) {
        outer:
        for (int i = 0; i < analysisInfos.size(); i++) {
            AnswerAnalysisInfo info = analysisInfos.get(i);
            for (int j = 0; j < items.size(); j++) {
                ExerciseItem itemData = items.get(j);
                String eqId = null;
                String subId = null;
                if (info.getEQId().contains("-")) {
                    eqId = info.getEQId().split("-")[0];
                    subId = info.getEQId().split("-")[1];
                } else {
                    eqId = info.getEQId();
                }
                if (TextUtils.equals(eqId, itemData.getIndex())) {
                    float errorRate = Utils.getNumberDivData((info.getWrongNum() + info
                            .getEmptyNum()) * 100, info.getSubimtNum());
                    if (TextUtils.isEmpty(subId)) {
                        itemData.setId(info.getId());
                        itemData.setSubimtNum(info.getSubimtNum());
                        itemData.setWrongNum(info.getWrongNum());
                        itemData.setAverageScore(info.getAverageScore());
                        itemData.setCommonError(info.getCommonError());
                        itemData.setEmptyNum(info.getEmptyNum());
                        itemData.setErrorRate(errorRate);
                    } else {
                        //分页
                        itemData.getSubIds().add(info.getId());
                        itemData.getSubSubmintNum().add(info.getSubimtNum());
                        itemData.getSubWrongNum().add(info.getWrongNum());
                        itemData.getSubEmptyNum().add(info.getEmptyNum());
                        itemData.getSubAverageScore().add(info.getAverageScore());
                        itemData.getSubCommonError().add(info.getCommonError());
                        itemData.getSubErrorRate().add(errorRate);
                        if (Integer.valueOf(itemData.getType()) == LearnTaskCardType
                                .SINGLE_CHOICE_CORRECTION) {
                            //单选改错
                            String commonError = itemData.getCommonError();
                            JSONObject jsonObject = null;
                            if (TextUtils.isEmpty(commonError)) {
                                jsonObject = new JSONObject();
                                jsonObject.put("item_index", info.getCommonError());
                            } else {
                                jsonObject = JSONObject.parseObject(commonError);
                                jsonObject.put("answer_text", info.getCommonError());
                            }
                            itemData.setCommonError(jsonObject.toString());
                        }
                    }
                    continue outer;
                }
            }
        }
    }

    /**
     * 拆分数据到不同的类型
     *
     * @param item
     */
    public static void splitLearnTypeData(ExerciseItem item, List<LearnTaskInfo> learnTaskInfos) {
        int type = Integer.valueOf(item.getType());
        LearnTaskInfo learnTaskInfo = null;
        double totalScore = 0.0;
        int answerWrongCount = 0;
        switch (type) {
            case LearnTaskCardType.SINGLE_CHOICE_QUESTION://单选题
            case LearnTaskCardType.MULTIPLE_CHOICE_QUESTIONS://多选题
            case LearnTaskCardType.JUDGMENT_PROBLEM://判断题
            case LearnTaskCardType.SINGLE_CHOICE_CORRECTION://单选改错题
            case LearnTaskCardType.FILL_CONTENT://填空题
            case LearnTaskCardType.SUBJECTIVE_PROBLEM://主观题
                learnTaskInfo = getLearnTaskInfo(type, learnTaskInfos);
                if (learnTaskInfo == null) {
                    learnTaskInfo = new LearnTaskInfo();
                    learnTaskInfo.setLearnType(type);
                    learnTaskInfos.add(learnTaskInfo);
                }
                break;
            case LearnTaskCardType.LISTEN_SINGLE_SELECTION://听力单选
            case LearnTaskCardType.HEARING_JUDGMENT://听力判断题
            case LearnTaskCardType.LISTEN_FILL_CONTENT://听力填空
                learnTaskInfo = getLearnTaskInfo(LearnTaskCardType.LISTEN_SINGLE_SELECTION, learnTaskInfos);
                if (learnTaskInfo == null) {
                    learnTaskInfo = new LearnTaskInfo();
                    learnTaskInfo.setLearnType(LearnTaskCardType.LISTEN_SINGLE_SELECTION);
                    learnTaskInfos.add(learnTaskInfo);
                }
                break;
        }
        if (learnTaskInfo == null) {
            return;
        }
        List<ExerciseItem> itemList = learnTaskInfo.getExercise_item_list();
        if (itemList == null) {
            itemList = new ArrayList<>();
            learnTaskInfo.setExercise_item_list(itemList);
        }
        //同一类型的总分
        if (!TextUtils.isEmpty(item.getScore())) {
            if (TextUtils.isEmpty(learnTaskInfo.getTotal_score())) {
                learnTaskInfo.setTotal_score(item.getScore());
            } else {
                totalScore = Double.valueOf(learnTaskInfo.getTotal_score()) + Double.valueOf(item
                        .getScore());
                learnTaskInfo.setTotal_score(String.valueOf(totalScore));
            }
        }

        //答错题的数量
        if (!item.isAnswerRight()) {
            answerWrongCount = learnTaskInfo.getAnsWrongCount() + 1;
            learnTaskInfo.setAnsWrongCount(answerWrongCount);
        }
        itemList.add(item);
    }

    private static LearnTaskInfo getLearnTaskInfo(int type, List<LearnTaskInfo> learnTaskInfos) {
        LearnTaskInfo data = null;
        if (learnTaskInfos != null && learnTaskInfos.size() > 0) {
            for (int i = 0; i < learnTaskInfos.size(); i++) {
                if (learnTaskInfos.get(i).getLearnType() == type) {
                    data = learnTaskInfos.get(i);
                    break;
                }
            }
        }
        return data;
    }

    private void commitStudentAnswerData() {
        Map<String, Object> param = new HashMap<>();
        param.put("TaskId", cardParam.getTaskId());
        param.put("StudentId", cardParam.getStudentId());
        param.put("StudentResTitle", cardParam.getCommitTaskTitle());
        if (cardParam.isFromOnlineStudyTask()) {
            param.put("SchoolId", cardParam.getSchoolId());
            param.put("SchoolName", cardParam.getSchoolName());
            param.put("ClassId", cardParam.getClassId());
            param.put("ClassName", cardParam.getClassName());
        }
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
                        int code = result.getInteger("ErrorCode");
                        if (code == 0) {
                            //成功
                            JSONObject modelObj = result.getJSONObject("Model");
                            if (modelObj != null) {
                                if (cardParam.isFromOnlineStudyTask()) {
                                    commitTaskId = modelObj.getInteger("CommitTaskOnlineId");
                                } else {
                                    commitTaskId = modelObj.getInteger("CommitTaskId");
                                }
                                //成功 上传图片
                                uploadMediasToServer();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listener.setShowLoading(true);
        String serverUrl = ServerUrl.UPDATE_TASK_STATE_DONE_FOREP_BASE_URL;
        if (cardParam.isFromOnlineStudyTask()) {
            serverUrl = ServerUrl.ONLINE_UPDATE_TASK_STATE_DONE_FOREP_BASE_URL;
        }
        RequestHelper.sendPostRequest(mContext, serverUrl, param, listener);
    }

    /**
     * 上传图片到服务器
     */
    private void uploadMediasToServer() {
        List<MediaInfo> mediaInfos = getSubjectProblemImage();
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(mContext, R.string.pls_login);
            return;
        }
        if (mediaInfos == null || mediaInfos.size() == 0) {
            //没有作答主观题
            //直接提交客观题的答案
            updateCommitData();
            return;
        }
        UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, MediaType.PICTURE, 1);
        List<String> paths = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                paths.add(mediaInfo.getPath());
                builder.append(Utils.removeFileNameSuffix(mediaInfo.getTitle()) + ";");
            }
        }
        String fileName = builder.toString();
        if (!TextUtils.isEmpty(fileName) && fileName.endsWith(";")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        if (!TextUtils.isEmpty(fileName)) {
            uploadParameter.setFileName(fileName);
        }
        uploadParameter.setPaths(paths);
        showLoadingDialog();
        UploadUtils.uploadMedia((Activity) mContext, uploadParameter, (obj) -> {
            //解析返回的数据 还原数据
            dismissLoadingDialog();
            if (obj != null) {
                MediaUploadList uploadResult = (MediaUploadList) obj;
                if (uploadResult != null) {
                    if (uploadResult.getCode() == 0) {
                        List<MediaData> datas = uploadResult.getData();
                        if (datas != null && datas.size() > 0) {
                            //转化为在线数据
                            if (!flagPositionBuilder.toString().contains(",")) {
                                String index = flagPositionBuilder.toString().split("-")[0];
                                for (ExerciseItem item : exerciseItems) {
                                    if (TextUtils.equals(item.getIndex(), index)) {
                                        //相同
                                        item.setDatas(datas);
                                    }
                                }
                            } else {
                                int startPosition = 0;
                                String flagString = flagPositionBuilder.toString();
                                if (flagString.contains(",")) {
                                    String[] splitArray = flagString.split(",");
                                    for (int i = 0; i < splitArray.length; i++) {
                                        String index = splitArray[i].split("-")[0];
                                        int length = Integer.valueOf(splitArray[i].split("-")[1]);
                                        for (ExerciseItem item : exerciseItems) {
                                            if (TextUtils.equals(item.getIndex(), index)) {
                                                //相同
                                                item.setDatas(datas.subList(startPosition, startPosition + length));
                                                startPosition = startPosition + length;
                                            }
                                        }
                                    }
                                }
                            }
                            //提交学生的答题详情
                            updateCommitData();
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_failed);
                    }
                }
            }
        });
    }

    private List<MediaInfo> getSubjectProblemImage() {
        List<MediaInfo> mediaInfos = new ArrayList<>();
        MediaInfo mediaInfo = null;
        for (int i = 0; i < exerciseItems.size(); i++) {
            ExerciseItem item = exerciseItems.get(i);
            int type = Integer.valueOf(item.getType());
            if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                //主观题
                List<learnTaskCardData> learnTaskCardData = item.getCardData();
                if (learnTaskCardData != null && learnTaskCardData.size() > 0) {
                    for (learnTaskCardData data : learnTaskCardData) {
                        mediaInfo = new MediaInfo();
                        mediaInfo.setPath(data.getAnswerPath());
                        mediaInfo.setTitle(data.getAnswerPathTitle());
                        mediaInfos.add(mediaInfo);
                    }
                    if (flagPositionBuilder.length() == 0) {
                        flagPositionBuilder.append(item.getIndex() + "-" + learnTaskCardData.size());
                    } else {
                        flagPositionBuilder.append(",").append(item.getIndex() + "-" +
                                learnTaskCardData.size());
                    }
                }
            }
        }
        return mediaInfos;
    }

    private void updateCommitData() {
        Map<String, Object> param = new HashMap<>();
        if (cardParam.isFromOnlineStudyTask()) {
            param.put("CommitTaskOnlineId", commitTaskId);
        } else {
            param.put("CommitTaskId", commitTaskId);
        }
        param.put("ResId", cardParam.getResId());
        param.put("DataList", getStudentDataList());
        String totalScore = getTotalScore();
        if (!TextUtils.isEmpty(totalScore)) {
            param.put("TotalScore", totalScore);
        }
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(mContext, ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                LogUtils.logd("TEST", "updateCommitData()=" + jsonString);
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                try {
                    JSONObject result = JSONObject.parseObject(jsonString);
                    if (result != null) {
                        int code = result.getInteger("ErrorCode");
                        if (code == 0) {
                            //成功
                            if (mContext != null) {
                                TipMsgHelper.ShowMsg(mContext, R.string.commit_success);
                                Activity activity = (Activity) mContext;
                                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
                                broadcastManager.sendBroadcast(new Intent(CompletedHomeworkListFragment.ACTION_MARK_SCORE));
                                HomeworkCommitFragment.setHasCommented(true);
                                activity.finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listener.setShowLoading(true);
        String serverUrl = ServerUrl.ADD_EQMEMBER_INFO_BASE_URL;
        if (cardParam.isFromOnlineStudyTask()) {
            serverUrl = ServerUrl.ONLINE_ADD_EQMEMBER_INFO_BASE_URL;
        }
        RequestHelper.sendPostRequest(mContext, serverUrl, param, listener);
    }

    private JSONArray getStudentDataList() {
        JSONArray dataList = new JSONArray();
        for (ExerciseItem item : exerciseItems) {
            JSONObject jsonObject = new JSONObject();
            int type = Integer.valueOf(item.getType());
            int index = Integer.valueOf(item.getIndex());
            jsonObject.put("EQId", index);
            jsonObject.put("EQType", type);
            if (TextUtils.isEmpty(item.getStudent_answer())) {
                //没做
                jsonObject.put("EQState", 5);
            } else if (isUpdateAnswerDetail){
                jsonObject.put("EQState", 6);
            } else if (TextUtils.equals(item.getStudent_score(), item.getScore())) {
                //正确
                jsonObject.put("EQState", 1);
            } else {
                //错误
                jsonObject.put("EQState", 2);
            }
            jsonObject.put("EQScore", item.getStudent_score());
            if (type == LearnTaskCardType.SINGLE_CHOICE_CORRECTION) {
                //单选改错
                jsonObject.put("EQAnswer", "");
                //修改父题的EQState
                if (!TextUtils.isEmpty(item.getStudent_answer())) {
                    JSONObject object = JSONObject.parseObject(item.getStudent_answer());
                    String itemIndex = object.getString("item_index");
                    String answerText = object.getString("answer_text");
                    if (TextUtils.isEmpty(itemIndex) && TextUtils.isEmpty(answerText)) {
                        //没作答
                        jsonObject.put("EQState", 5);
                    }
                }
                dataList.add(jsonObject);
                handleSingleChoiceData(index, type, dataList, item);
            } else if (type == LearnTaskCardType.FILL_CONTENT
                    || type == LearnTaskCardType.LISTEN_FILL_CONTENT) {
                //填空、听力填空
                jsonObject.put("EQAnswer", "");
                //分析主类题
                if (!TextUtils.isEmpty(item.getStudent_answer())) {
                    JSONArray studentAnswerArray = JSONObject.parseArray(item.getStudent_answer());
                    if (studentAnswerArray != null && studentAnswerArray.size() > 0) {
                        boolean flag = false;
                        for (int k = 0; k < studentAnswerArray.size(); k++) {
                            String singleAnswer = studentAnswerArray.getString(k);
                            if (!TextUtils.isEmpty(singleAnswer)) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            //没作答
                            jsonObject.put("EQState", 5);
                        }
                    }
                }
                dataList.add(jsonObject);
                handleFillInData(index, type, dataList, item);
            } else if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                //主观题
                if (isUpdateAnswerDetail){
                    List<MediaInfo> mediaInfos = getSubjectProblemImage();
                    if (mediaInfos != null && mediaInfos.size() > 0){
                        jsonObject.put("EQState", 6);
                    } else {
                        jsonObject.put("EQState", 5);
                    }
                } else {
                    String resId = getSubjectResIdOrResUrl(true, item);
                    String resUrl = getSubjectResIdOrResUrl(false, item);
                    if (TextUtils.isEmpty(resId)) {
                        jsonObject.put("EQState", 5);
                    } else {
                        jsonObject.put("EQState", 3);
                    }
                    jsonObject.put("EQScore", 0);
                    jsonObject.put("EQAnswer", "");
                    jsonObject.put("SubjectiveResId", resId);
                    jsonObject.put("SubjectiveResUrl", resUrl);
                }
                dataList.add(jsonObject);
            } else {
                jsonObject.put("EQAnswer", item.getStudent_answer());
                dataList.add(jsonObject);
            }
        }
        LogUtils.logd("TEST", "dataList=" + dataList.toString());
        return dataList;
    }

    private String getTotalScore() {
        double totalScore = 0.0;
        boolean isHasSubjectProblem = false;
        boolean isHasObjectiveProblem = false;
        boolean isAnswerSubjectQuestion = false;
        if (exerciseItems != null && exerciseItems.size() > 0) {
            for (ExerciseItem item : exerciseItems) {
                int type = Integer.valueOf(item.getType());
                if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                    //含有主观题
                    isHasSubjectProblem = true;
                    List<MediaData> mediaDataList = item.getDatas();
                    if (mediaDataList != null && mediaDataList.size() > 0) {
                        isAnswerSubjectQuestion = true;
                    }
                } else {
                    isHasObjectiveProblem = true;
                }
                if (!TextUtils.isEmpty(item.getStudent_score())) {
                    totalScore = totalScore + Double.valueOf(item.getStudent_score());
                }
            }
        }
        if (isHasObjectiveProblem && isHasSubjectProblem && isAnswerSubjectQuestion) {
            return "";
        } else if (isHasSubjectProblem && isAnswerSubjectQuestion) {
            return "";
        }
        return Utils.changeDoubleToInt(String.valueOf(totalScore));
    }

    /**
     * @param isGetResId boolean 是否是获取主观的resId 多个以逗号拼接
     */
    private String getSubjectResIdOrResUrl(boolean isGetResId, ExerciseItem item) {
        StringBuilder resIdBuilder = new StringBuilder();
        StringBuilder resUrlBuilder = new StringBuilder();
        List<MediaData> mediaData = item.getDatas();
        if (mediaData != null && mediaData.size() > 0) {
            for (int i = 0; i < mediaData.size(); i++) {
                MediaData data = mediaData.get(i);
                if (data != null) {
                    if (resIdBuilder.length() == 0) {
                        resIdBuilder.append(data.getIdType());
                        resUrlBuilder.append(data.resourceurl);
                    } else {
                        resIdBuilder.append(",").append(data.getIdType());
                        resUrlBuilder.append(",").append(data.resourceurl);
                    }
                }
            }
        }
        return isGetResId ? resIdBuilder.toString() : resUrlBuilder.toString();
    }

    /**
     * 单选改错
     */
    private void handleSingleChoiceData(int index,
                                        int type,
                                        JSONArray dataList,
                                        ExerciseItem item) {
        try {
            String studentAnswer = item.getStudent_answer();
            String rightAnswer = item.getRight_answer();
            if (!TextUtils.isEmpty(studentAnswer) && !TextUtils.isEmpty(rightAnswer)) {
                JSONObject object = JSONObject.parseObject(studentAnswer);
                JSONObject rightObj = JSONObject.parseObject(rightAnswer);
                String itemIndex = object.getString("item_index");
                String answerText = object.getString("answer_text");
                String rightIndex = rightObj.getString("item_index");
                String rightText = rightObj.getString("answer_text");
                JSONObject jsonObject = null;
                for (int i = 0; i < 2; i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("EQId", index + "-" + (i + 1));
                    jsonObject.put("EQType", type);
                    if (i == 0) {
                        jsonObject.put("EQAnswer", itemIndex);
                    } else {
                        jsonObject.put("EQAnswer", answerText);
                    }
                    jsonObject.put("EQScore", 0);
                    if (i == 0) {
                        if (TextUtils.isEmpty(itemIndex)) {
                            //没做
                            jsonObject.put("EQState", 5);
                        } else if (isUpdateAnswerDetail) {
                            jsonObject.put("EQState", 6);
                        } else if (TextUtils.equals(itemIndex,
                                rightIndex)) {
                            //正确
                            jsonObject.put("EQState", 1);
                        } else {
                            //错误
                            jsonObject.put("EQState", 2);
                        }
                    } else {
                        if (TextUtils.isEmpty(answerText)) {
                            //没做
                            jsonObject.put("EQState", 5);
                        } else if (isUpdateAnswerDetail) {
                            jsonObject.put("EQState", 6);
                        } else if (TextUtils.equals(answerText, rightText)) {
                            //正确
                            jsonObject.put("EQState", 1);
                        } else {
                            //错误
                            jsonObject.put("EQState", 2);
                        }
                    }
                    dataList.add(jsonObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填空题
     */
    private void handleFillInData(int index,
                                  int type,
                                  JSONArray dataList,
                                  ExerciseItem item) {
        if (!TextUtils.isEmpty(item.getRight_answer()) && !TextUtils.isEmpty(item.getStudent_answer())) {
            JSONArray rightAnswerArray = JSONObject.parseArray(item.getRight_answer());
            JSONArray studentAnswerArray = JSONObject.parseArray(item.getStudent_answer());
            String[] studentSubScore = item.getStudentSubScoreArray();
            JSONObject jsonObject = null;
            for (int i = 0; i < studentAnswerArray.size(); i++) {
                String singleQuestion = studentAnswerArray.getString(i);
                String singleRight = rightAnswerArray.getString(i);
                jsonObject = new JSONObject();
                jsonObject.put("EQId", index + "-" + (i + 1));
                jsonObject.put("EQType", type);
                jsonObject.put("EQAnswer", singleQuestion);
                jsonObject.put("EQScore", studentSubScore[i]);
                if (TextUtils.isEmpty(singleQuestion)) {
                    //没做
                    jsonObject.put("EQState", 5);
                } else if (isUpdateAnswerDetail) {
                    jsonObject.put("EQState", 6);
                } else if (TextUtils.equals(singleQuestion, singleRight)) {
                    //正确
                    jsonObject.put("EQState", 1);
                } else {
                    jsonObject.put("EQState", 2);
                }
                dataList.add(jsonObject);
            }
        }
    }

    /**
     * 提交批阅相关的代码
     *
     * @return
     */
    public void commitCheckMarkData() {
        ((Activity) mContext).runOnUiThread(() -> {
            commitCheckCourse();
        });
    }

    public void showCommitDialog() {
        if (cardParam == null) {
            return;
        }
        itemData = cardParam.getExerciseItem();
        if (itemData == null) {
            return;
        }
        if (itemData.getEqState() == 4 || cardParam.getRoleType() == RoleType.ROLE_TYPE_STUDENT) {
            //已打分
            showCommitDialog(isTeacherOrEditor());
        } else {
            showMarkScoreDialog();
        }
    }

    private void showCommitDialog(boolean isTeacher) {
        if (mCommitDialog != null) {
            if (!mCommitDialog.isShowing()) {
                mCommitDialog.show();
            }
            return;
        }
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_mark_score_commit, null);
        TextView message = (TextView) inflate.findViewById(R.id.tv_tip_message);
        TextView leftBtn = (TextView) inflate.findViewById(R.id.tv_btn_left);
        TextView middleBtn = (TextView) inflate.findViewById(R.id.tv_btn_middle);
        TextView rightBtn = (TextView) inflate.findViewById(R.id.tv_btn_right);
        message.setText(R.string.commit_or_mot);
        leftBtn.setText(R.string.discard);
        middleBtn.setText(R.string.ok);
        rightBtn.setText(R.string.str_recalculate);
        //放弃
        leftBtn.setOnClickListener(v -> {
            mCommitDialog.dismiss();
            handler.discard();
        });
        //确认
        middleBtn.setOnClickListener(v -> {
            mCommitDialog.dismiss();
            handler.upload(cardParam.getCommitTaskTitle(), "");
        });
        //重新打分
        rightBtn.setOnClickListener(v -> {
            mCommitDialog.dismiss();
            showMarkScoreDialog();
        });

        if (isTeacher && itemData.getEqState() == 4 && !TextUtils.isEmpty(itemData.getStudent_score())) {
            //老师身份 并且 分数不为空才显示重新打分
            rightBtn.setVisibility(View.VISIBLE);
        }

        mCommitDialog = new AlertDialog.Builder(mContext).create();
        mCommitDialog.show();
        Window window = mCommitDialog.getWindow();
        if (window != null) {
            window.setContentView(inflate);
        }
    }

    private void showMarkScoreDialog() {
        if (mMarkScoreDialog != null) {
            if (!mMarkScoreDialog.isShowing()) {
                mMarkScoreDialog.show();
            }
            return;
        }
        String scoreContent = "";
        if (itemData.getEqState() == 4) {
            //老师已经批阅
            scoreContent = Utils.changeDoubleToInt(itemData.getStudent_score());
        }
        mMarkScoreDialog = new MarkScoreDialog(mContext,
                true,
                mContext.getString(R.string.mark),
                scoreContent,
                null,
                mContext.getString(R.string.discard),
                null,
                mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String score = ((MarkScoreDialog) dialog).getScore();
                if (TextUtils.isEmpty(score)) return;
                double totalScore = Double.valueOf(itemData.getScore());
                double scores = Double.valueOf(score);
                if (scores > totalScore) {
                    TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string
                            .str_single_question_total_score, Utils.changeDoubleToInt(String.valueOf(totalScore))));
                    return;
                }
                score = Utils.changeDoubleToInt(String.valueOf(scores));
                dialog.dismiss();
                markScore = score;
                handler.upload(cardParam.getCommitTaskTitle(), "");
            }
        }, mContext.getString(R.string.discard),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.discard();
                    }
                }, true);
        mMarkScoreDialog.show();
    }

    private void commitCheckCourse() {
        if (!TextUtils.isEmpty(slidePath)) {
            LocalCourseInfo info = getLocalCourseInfo(slidePath);
            if (info != null) {
                uploadCourse(info, slidePath);
            }
        }
    }

    /**
     * 判断是不是老师 或者 小编 或 主编
     *
     * @return
     */
    private boolean isTeacherOrEditor() {
        return cardParam != null && (cardParam.isOnlineReporter() || cardParam.isOnlineHost());
    }

    private LocalCourseInfo getLocalCourseInfo(String coursePath) {
        LocalCourseInfo result = null;
        LocalCourseDao localCourseDao = new LocalCourseDao(mContext);
        try {
            LocalCourseDTO localCourseDTO = localCourseDao.getLocalCourseDTOByPath
                    (DemoApplication.getInstance().getMemberId(), coursePath);
            if (localCourseDTO != null) {
                return localCourseDTO.toLocalCourseInfo();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void uploadCourse(final LocalCourseInfo localCourseInfo, final String slidePath) {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin((Activity) mContext);
            return;
        }
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                localCourseInfo, null, 1);
        if (uploadParameter != null) {
            //增加参数控制上传的资源是否需要拆分
            uploadParameter.setIsNeedSplit(false);
            showLoadingDialog();
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    localCourseInfo.mPath, Utils.TEMP_FOLDER + Utils.getFileNameFromPath
                    (localCourseInfo.mPath) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param,
                    new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            // TODO Auto-generated method stub
                            if (result != null && result.mIsOk) {
                                uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                UploadUtils.uploadResource((Activity) mContext, uploadParameter, new
                                        CallbackListener() {
                                            @Override
                                            public void onBack(final Object result) {
                                                if (mContext != null) {
                                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dismissLoadingDialog();
                                                            if (result != null) {
                                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                                if (uploadResult.code != 0) {
                                                                    TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_failed);
                                                                    return;
                                                                }
                                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                                    final CourseData courseData = uploadResult.data.get(0);
                                                                    if (courseData != null) {
                                                                        commitStudentCourse(userInfo, courseData);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

    }

    private void commitStudentCourse(final UserInfo userInfo, final CourseData courseData) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (cardParam.isFromOnlineStudyTask()) {
            params.put("CommitTaskOnlineId", cardParam.getCommitTaskId());
        } else {
            params.put("CommitTaskId", cardParam.getCommitTaskId());
        }
        params.put("EQId", itemData.getIndex());
        params.put("ResId", courseData.getIdType());
        params.put("ResUrl", courseData.resourceurl);
        params.put("CreateId", courseData.code);
        params.put("CreateName", courseData.createname);
        params.put("IsTeacher", isTeacherOrEditor());
        if (isTeacherOrEditor()) {
            params.put("EQScore", markScore);
            int unFinishSubjectCount = cardParam.getUnFinishSubjectCount();
            if (unFinishSubjectCount <= 1) {
                String totalScore = cardParam.getStudentTotalScore();
                if (!TextUtils.isEmpty(totalScore) && !TextUtils.isEmpty(markScore)) {
                    ExerciseItem item = cardParam.getExerciseItem();
                    double totalDoubleScore = Double.valueOf(totalScore);
                    double commitScore = Double.valueOf(markScore);
                    double studentTotalScore = DoubleOperationUtil.add(totalDoubleScore, commitScore);
                    if (unFinishSubjectCount == 0) {
                        //完成所有的作答更新列表的分数
                        if (item.getEqState() == 4) {
                            //已完成作答
                            params.put("TotalScore", DoubleOperationUtil.sub(studentTotalScore,Double.valueOf(item.getStudent_score())));
                        }
                    } else {
                        if (item != null && item.getEqState() != 4) {
                            params.put("TotalScore", studentTotalScore);
                        }
                    }
                }
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener<DataModelResult>(mContext, DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    dismissLoadingDialog();
                    return;
                }
                TipMsgHelper.ShowLMsg(mContext, R.string.commit_success);
                EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.UPDATE_LIST_DATA));
                if (!TextUtils.isEmpty(markScore)) {
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
                    broadcastManager.sendBroadcast(new Intent(CompletedHomeworkListFragment.ACTION_MARK_SCORE)
                            .putExtra(CompletedHomeworkListFragment.ACTION_MARK_SCORE, markScore));
                }
                finish();
            }
        };
        RequestHelper.sendPostRequest(mContext, ServerUrl.ADD_SUBJECTIVE_REVIEW_BASE_URL, params,
                listener);
    }

    public static boolean hasSubjectProblem(String exerciseString) {
        if (TextUtils.isEmpty(exerciseString)) {
            return false;
        }
        JSONArray jsonArray = JSONObject.parseArray(exerciseString);
        if (jsonArray != null && jsonArray.size() > 0) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            LearnTaskInfo learnTaskInfo = JSONObject.parseObject(jsonObject.toString(), LearnTaskInfo.class);
            if (learnTaskInfo != null) {
                List<ExerciseItem> exerciseItems = learnTaskInfo.getExercise_item_list();
                if (exerciseItems != null && exerciseItems.size() > 0) {
                    for (int i = 0; i < exerciseItems.size(); i++) {
                        ExerciseItem item = exerciseItems.get(i);
                        if (item != null && Integer.valueOf(item.getType()) == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void loadStudentCommitData(Activity activity,
                                             ExerciseAnswerCardParam cardParam,
                                             CallbackListener callbackListener) {
        Map<String, Object> param = new HashMap<>();
        if (cardParam.isFromOnlineStudyTask()) {
            param.put("CommitTaskOnlineId", cardParam.getCommitTaskId());
        } else {
            param.put("CommitTaskId", cardParam.getCommitTaskId());
        }
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(activity, ModelResult.class) {
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
                                    List<ExerciseItem> itemList = new ArrayList<>();
                                    DoTaskOrderHelper.analysisStudentCommitData(dataListArray, itemList);
                                    cardParam.setStudentCommitAnswerString(dataListArray.toString());
                                    JSONArray jsonArray = JSONObject.parseArray(cardParam.getExerciseAnswerString());
                                    if (jsonArray != null && jsonArray.size() > 0) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        LearnTaskInfo learnTaskInfo = JSONObject.parseObject(jsonObject.toString(),
                                                LearnTaskInfo.class);
                                        if (learnTaskInfo != null) {
                                            List<ExerciseItem> exerciseItems = learnTaskInfo.getExercise_item_list();
                                            if (exerciseItems != null && exerciseItems.size() > 0) {
                                                mergeLearnTaskData(exerciseItems,itemList);
                                                if (callbackListener != null){
                                                    callbackListener.onBack(itemList);
                                                }
                                            }
                                        }
                                    }

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
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(activity, ServerUrl.LOAD_ANSWER_BY_COMMITTASKID_BASE_URL, param, listener);
    }

    private static void mergeLearnTaskData(List<ExerciseItem> learnExerciseItem,List<ExerciseItem> exerciseItems) {
        if (exerciseItems != null && exerciseItems.size() > 0) {
            if (learnExerciseItem != null && learnExerciseItem.size() > 0) {
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
                            continue outer;
                        }
                    }
                }
            }
        }
    }

    public static void openExerciseDetail(Activity activity,
                                          String exerciseString,
                                          String TaskId,
                                          String StudentId,
                                          String courseId,
                                          String taskTitle,
                                          String schoolId,
                                          String schoolName,
                                          String classId,
                                          String className,
                                          String studentName,
                                          int commitTaskId,
                                          boolean fromOnlineStudy,
                                          boolean isDoExercise) {

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
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            courseData.setIsPublicRes(false);
                            processOpenImageData(activity,
                                    courseData,
                                    exerciseString,
                                    TaskId,
                                    StudentId,
                                    taskTitle,
                                    schoolId,
                                    schoolName,
                                    classId,
                                    className,
                                    studentName,
                                    commitTaskId,
                                    fromOnlineStudy,
                                    isDoExercise);
                        }
                    }
                }
            });
        } else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
            wawaCourseUtils.loadCourseDetail(courseId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    courseData.setIsPublicRes(false);
                    processOpenImageData(activity,
                            courseData,
                            exerciseString,
                            TaskId,
                            StudentId,
                            taskTitle,
                            schoolId,
                            schoolName,
                            classId,
                            className,
                            studentName,
                            commitTaskId,
                            fromOnlineStudy,
                            isDoExercise);
                }
            });
        }
    }

    /**
     * 打开图片逻辑
     *
     * @param courseData
     */
    private static void processOpenImageData(Activity activity,
                                             CourseData courseData,
                                             String exerciseString,
                                             String TaskId,
                                             String StudentId,
                                             String taskTitle,
                                             String schoolId,
                                             String schoolName,
                                             String classId,
                                             String className,
                                             String studentName,
                                             int commitTaskId,
                                             boolean fromOnlineStudy,
                                             boolean isDoExercise) {
        if (courseData != null) {
            PlaybackParam mParam = new PlaybackParam();
            //隐藏收藏按钮
            mParam.mIsHideCollectTip = true;
            //任务单答题操作
            ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
            cardParam.setShowExerciseNode(true);
            if (isDoExercise) {
                //做读写单
                cardParam.setShowExerciseButton(true);
            }
            cardParam.setExerciseAnswerString(exerciseString);
            cardParam.setTaskId(TaskId);
            cardParam.setResId(courseData.id + "-" + courseData.type);
            cardParam.setStudentId(StudentId);
            cardParam.setFromOnlineStudyTask(fromOnlineStudy);
            cardParam.setCommitTaskTitle(taskTitle);
            cardParam.setSchoolId(schoolId);
            cardParam.setSchoolName(schoolName);
            cardParam.setClassId(classId);
            cardParam.setClassName(className);
            cardParam.setStudentName(studentName);
            cardParam.setCommitTaskId(commitTaskId);
            if (isDoExercise) {
                mParam.exerciseCardParam = cardParam;
                ActivityUtils.openOnlineOnePage(activity, courseData.getNewResourceInfo(), true,
                        mParam);
            } else {
                loadStudentCommitData(activity, cardParam, result -> {
                    if (result != null) {
                        cardParam.setQuestionDetails((List<ExerciseItem>) result);
                        mParam.exerciseCardParam = cardParam;
                        ActivityUtils.openOnlineOnePage(activity, courseData.getNewResourceInfo(), true,
                                mParam);
                    }
                });

            }
        }
    }


    private void finish() {
        if (mContext != null) {
            ((Activity) mContext).finish();
        }
    }

    private Dialog showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return loadingDialog;
        }
        loadingDialog = DialogHelper.getIt((Activity) mContext).GetLoadingDialog(0);
        return loadingDialog;
    }

    private void dismissLoadingDialog() {
        try {
            if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
                this.loadingDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (this.loadingDialog != null) {
                this.loadingDialog = null;
            }
        }
    }
}
