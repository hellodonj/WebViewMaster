package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.DownloadOnePageTask;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.EvalHomeworkListFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkCommitFragment;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.helper.AudioPlayerHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTaskResult;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.EvaluationData;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SpeechAssessmentData;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.views.ContactsListDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.apps.views.HorizontalListView;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.libs.audio.RawAudioRecorder;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;
import com.lqwawa.tools.ScreenUtils;
import com.oosic.apps.iemaker.base.evaluate.EvaluateHelper;
import com.oosic.apps.iemaker.base.evaluate.EvaluateItem;
import com.oosic.apps.iemaker.base.evaluate.EvaluateItemResult;
import com.oosic.apps.iemaker.base.evaluate.EvaluateListener;
import com.oosic.apps.iemaker.base.evaluate.EvaluateManager;
import com.oosic.apps.iemaker.base.evaluate.EvaluateResult;
import com.osastudio.common.utils.FileUtils;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/12 0012 11:13
 * Describe:语音评测
 * ======================================================
 */
public class SpeechAssessmentActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static String TAG = SpeechAssessmentActivity.class.getSimpleName();
    private TextView headTitleTextV;
    private TextView headRightTextV;
    private ImageView currentPageImageV;
    private LinearLayout showTextLayout;
    private ImageView arrowImageV;
    private ImageView fundamentalToneTextV;
    private ImageView recordImageV;
    private ImageView playRecordAudio;
    private TextView audioMessageTextV;
    private TextView pageSoreTextV;
    private TextView leftMessageTextV;
    private ListView listView;
    private HorizontalListView horizontalListView;
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private String resId;
    private String taskId;
    private String taskTitle;
    private UserInfo studentUserInfo;
    private boolean isOnlinePlay;
    private String onlineResUrl;
    private List<SpeechAssessmentData> assessmentData;
    private PageThumbnailAdapter pageThumbnailAdapter;
    private int currentPagePosition = 0;
    private String folderPath = Utils.LQ_TEMP_FOLDER + "evaluation";//语音评测文件夹路径
    private String pageIndexFileName = "page_index.json";
    private String pageIndexPath = folderPath + "/" + pageIndexFileName;
    private boolean isRecording;//正在录制中
    private RawAudioRecorder audioRecorder;
    private AudioPlayerHelper mediaPlayer;
    private int scoreRule;//1:十分制  2:百分制
    private String onlinePlayTempFolder;
    private AnimationDrawable recordPlayingAnim;
    private AnimationDrawable voicePlayAnim;
    private int playType;
    private boolean fromOnlineStudyTask;
    private String schoolId;
    private String schoolName;
    private String classId;
    private String className;
    private int[] playChinaSpeed = {600, 400, 300};
    private int[] playEnglishSpeed = {833, 750, 600};
    private int playRefIndexPosition = 0;
    private Timer mTimer;
    private TimerTask mTimeTask;
    private String[] speedOfRead;
    private boolean isPressHomeKey;

    public interface PlayAudioType {
        int PLAY_RECORD_TYPE = 3;//录制的音频
        int PLAY_VOICE_TYPE = 4;//播放原音
    }

    public interface SpeedModelType {
        int SPEED_OF_SLOW = 0;
        int SPEED_OF_MEDIUM = 1;
        int SPEED_OF_HIGH = 2;
    }


    public interface Constant {
        String ORIENTATION = "orientation";
        String STUDENT_USERINFO = "student_userinfo";
        String IS_ONLINE_PLAY = "is_online_play";
        String COURSE_RESID = "course_resid";
        String TASK_TITLE = "task_title";
        String TASK_ID = "task_id";
        String ONLINE_RESURL = "online_resurl";
        String SCORE_RULE = "score_rule";

        String FROM_ONLINE_STUDY_TASK = "from_online_study_task";
        String SCHOOL_ID = "school_id";
        String SCHOOL_NAME = "school_name";
        String CLASS_ID = "class_id";
        String CLASS_NAME = "class_name";
    }

    /**
     * 语音评测的录制
     *
     * @param activity
     * @param mOrientation
     * @param studentUserInfo 存在家长身份传孩子的userInfo
     * @param resId           布置任务的resId(格式id-type)
     * @param taskId          任务的id
     * @param taskTitle       任务的title
     * @param scoreRule       打分的规则 1十分制 2 百分制
     */
    public static void start(Activity activity,
                             int mOrientation,
                             UserInfo studentUserInfo,
                             String resId,
                             String taskId,
                             String taskTitle,
                             int scoreRule) {
        Intent intent = new Intent(activity, SpeechAssessmentActivity.class);
        intent.putExtra(Constant.ORIENTATION, mOrientation);
        intent.putExtra(Constant.STUDENT_USERINFO, studentUserInfo);
        intent.putExtra(Constant.TASK_ID, taskId);
        intent.putExtra(Constant.TASK_TITLE, taskTitle);
        intent.putExtra(Constant.COURSE_RESID, resId);
        intent.putExtra(Constant.IS_ONLINE_PLAY, false);
        intent.putExtra(Constant.SCORE_RULE, scoreRule);
        activity.startActivity(intent);
    }

    /**
     * 语音评测的录制(mooc调用请用这个方法)
     *
     * @param activity
     * @param mOrientation
     * @param resId               布置任务的resId(格式id-type)
     * @param taskId              任务的id
     * @param taskTitle           任务的title
     * @param scoreRule           打分的规则 1十分制 2 百分制
     *                            (在班级中提交任务时必填一下参数必填)
     * @param schoolId
     * @param schoolName
     * @param classId
     * @param className
     * @param fromOnlineStudyTask mooc传true
     */
    public static void start(Activity activity,
                             int mOrientation,
                             String resId,
                             String taskId,
                             String taskTitle,
                             int scoreRule,
                             String schoolId,
                             String schoolName,
                             String classId,
                             String className,
                             boolean fromOnlineStudyTask) {
        Intent intent = new Intent(activity, SpeechAssessmentActivity.class);
        intent.putExtra(Constant.ORIENTATION, mOrientation);
        intent.putExtra(Constant.TASK_ID, taskId);
        intent.putExtra(Constant.TASK_TITLE, taskTitle);
        intent.putExtra(Constant.COURSE_RESID, resId);
        intent.putExtra(Constant.IS_ONLINE_PLAY, false);
        intent.putExtra(Constant.SCORE_RULE, scoreRule);
        intent.putExtra(Constant.SCHOOL_ID, schoolId);
        intent.putExtra(Constant.SCHOOL_NAME, schoolName);
        intent.putExtra(Constant.CLASS_NAME, className);
        intent.putExtra(Constant.CLASS_ID, classId);
        intent.putExtra(Constant.FROM_ONLINE_STUDY_TASK, fromOnlineStudyTask);
        activity.startActivity(intent);
    }

    /**
     * 打开语音评测
     *
     * @param activity
     * @param mOrientation
     * @param onlineResUrl studentResurl
     * @param scoreRule    打分的规则 1十分制 2 百分制
     */
    public static void start(Activity activity,
                             int mOrientation,
                             String onlineResUrl,
                             int scoreRule) {
        Intent intent = new Intent(activity, SpeechAssessmentActivity.class);
        intent.putExtra(Constant.ORIENTATION, mOrientation);
        intent.putExtra(Constant.ONLINE_RESURL, onlineResUrl);
        intent.putExtra(Constant.IS_ONLINE_PLAY, true);
        intent.putExtra(Constant.SCORE_RULE, scoreRule);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadIntent();
        initViews();
        initPlayOrRecordData();
    }

    private void loadIntent() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mOrientation = args.getInt(Constant.ORIENTATION);
            studentUserInfo = (UserInfo) args.getSerializable(Constant.STUDENT_USERINFO);
            isOnlinePlay = args.getBoolean(Constant.IS_ONLINE_PLAY, false);
            taskTitle = args.getString(Constant.TASK_TITLE);
            taskId = args.getString(Constant.TASK_ID);
            resId = args.getString(Constant.COURSE_RESID);
            onlineResUrl = args.getString(Constant.ONLINE_RESURL);
            scoreRule = args.getInt(Constant.SCORE_RULE, 2);
            fromOnlineStudyTask = args.getBoolean(Constant.FROM_ONLINE_STUDY_TASK);
            schoolId = args.getString(Constant.SCHOOL_ID);
            schoolName = args.getString(Constant.SCHOOL_NAME);
            classId = args.getString(Constant.CLASS_ID);
            className = args.getString(Constant.CLASS_NAME);
        }
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_speech_assessment_p);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_speech_assessment_l);
        }
    }

    private void initViews() {
        LinearLayout headLayout = (LinearLayout) findViewById(R.id.contacts_header_layout);
        if (headLayout != null) {
            headLayout.setBackgroundColor(getResources().getColor(R.color.text_green));
        }
        ImageView backImage = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImage != null) {
            backImage.setOnClickListener(this);
            backImage.setImageResource(R.drawable.icon_back_white);
        }
        headTitleTextV = (TextView) findViewById(R.id.contacts_header_title);
        headTitleTextV.setTextColor(ContextCompat.getColor(SpeechAssessmentActivity.this, R.color.text_white));
        //显示title
        headRightTextV = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (headRightTextV != null) {
            headRightTextV.setText(getString(R.string.commit));
            headRightTextV.setTextSize(16);
            headRightTextV.setTextColor(ContextCompat.getColor(SpeechAssessmentActivity.this, R.color.text_white));
            headRightTextV.setVisibility(View.VISIBLE);
            headRightTextV.setOnClickListener(this);
        }
        currentPageImageV = (ImageView) findViewById(R.id.iv_imageview);
        showTextLayout = (LinearLayout) findViewById(R.id.ll_left);
        if (showTextLayout != null) {
            showTextLayout.setOnClickListener(this);
        }
        //左侧布局的箭头
        arrowImageV = (ImageView) findViewById(R.id.iv_arrow_orientation);
        //左侧布局的文本
        leftMessageTextV = (TextView) findViewById(R.id.tv_left_message_text);
        if (leftMessageTextV != null) {
            if (isOnlinePlay) {
                //显示文本
                leftMessageTextV.setText(getString(R.string.str_show_text));
            } else {
                speedOfRead = this.getResources().getStringArray(R.array.str_speed_array);
                //跟读速度
                leftMessageTextV.setText(speedOfRead[1]);
            }
        }
        fundamentalToneTextV = (ImageView) findViewById(R.id.tv_fundamental_tone);
        if (fundamentalToneTextV != null) {
            fundamentalToneTextV.setOnClickListener(this);
        }
        recordImageV = (ImageView) findViewById(R.id.iv_record);
        if (recordImageV != null) {
            recordImageV.setOnClickListener(this);
        }
        playRecordAudio = (ImageView) findViewById(R.id.iv_play);
        if (playRecordAudio != null) {
            playRecordAudio.setOnClickListener(this);
        }
        //显示录音课件复述文本
        audioMessageTextV = (TextView) findViewById(R.id.tv_message);
        audioMessageTextV.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (isOnlinePlay) {
            audioMessageTextV.setVisibility(View.GONE);
        } else {
            audioMessageTextV.setVisibility(View.VISIBLE);
        }
        //单页的分数
        pageSoreTextV = (TextView) findViewById(R.id.tv_study_score);
        listView = (ListView) findViewById(R.id.lv_data_view);
        horizontalListView = (HorizontalListView) findViewById(R.id.lv_data_view_horizontal);
        recordPlayingAnim = (AnimationDrawable) ContextCompat.getDrawable(SpeechAssessmentActivity.this, R
                .drawable.eval_audio_playing);
        voicePlayAnim = (AnimationDrawable) ContextCompat.getDrawable(SpeechAssessmentActivity.this, R
                .drawable.eval_audio_playing);
    }

    private void initPlayOrRecordData() {
        if (isOnlinePlay) {
            headTitleTextV.setVisibility(View.GONE);
            headRightTextV.setVisibility(View.GONE);
            recordImageV.setVisibility(View.GONE);
            if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                findViewById(R.id.view_gray_line).setVisibility(View.GONE);
            }
            downloadOnlineEvalRes();
        } else {
            headRightTextV.setVisibility(View.VISIBLE);
            headRightTextV.setVisibility(View.VISIBLE);
            loadCourseTextData();
            createFolderPath();
        }
    }

    private void downloadOnlineEvalRes() {
        if (TextUtils.isEmpty(onlineResUrl)) {
            return;
        }
        if (onlineResUrl.endsWith("head.jpg")) {
            onlineResUrl = onlineResUrl.replace("/head.jpg", ".zip");
        }
        DownloadOnePageTask task = new DownloadOnePageTask(
                this, onlineResUrl, "",
                mOrientation, Utils.DOWNLOAD_TEMP_FOLDER, "");
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                LocalCourseDTO localCourseDTO = (LocalCourseDTO) result;
                if (localCourseDTO != null) {
                    String localFolderPath = localCourseDTO.getmPath();
                    onlinePlayTempFolder = localCourseDTO.getmParentPath();
                    if (!TextUtils.isEmpty(localFolderPath)) {
                        String pageArrayData = FileUtils.loadDataFromFile(localFolderPath + File
                                .separator + pageIndexFileName);
                        if (!TextUtils.isEmpty(pageArrayData)) {
                            assessmentData = new ArrayList<>();
                            SpeechAssessmentData evalData = null;
                            JSONObject jsonObject = JSONObject.parseObject(pageArrayData);
                            JSONArray jsonArray = JSONObject.parseArray(jsonObject.getString("page_list"));
                            int evalSchemeId = jsonObject.getInteger("eval_scheme_id");
                            if (jsonArray != null && jsonArray.size() > 0) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    evalData = new SpeechAssessmentData();
                                    JSONObject pageObj = jsonArray.getJSONObject(i);
                                    evalData.setEval_scheme_id(evalSchemeId);
                                    evalData.setMp3Url(pageObj.getString("ref_audio_url"));
                                    evalData.setStart(pageObj.getString("ref_audio_start"));
                                    evalData.setEnd(pageObj.getString("ref_audio_end"));
                                    evalData.setRef_text(pageObj.getString("ref_text"));
                                    evalData.setImageUrl(pageObj.getString("ref_image_url"));
                                    String localAudioPath = pageObj.getString("my_audio_path");
                                    if (!TextUtils.isEmpty(localAudioPath)) {
                                        evalData.setMy_audio_path(localFolderPath + File.separator + localAudioPath);
                                    }
                                    evalData.setEval_score(pageObj.getInteger("eval_score"));
                                    evalData.setEval_result(pageObj.getString("eval_result"));
                                    evalData.setSpannableString(getSpannableString(evalSchemeId, pageObj.getString("eval_result")));
                                    evalData.setIsAlreadyEval(true);
                                    evalData.setIsShowingText(true);
                                    assessmentData.add(evalData);
                                }
                            }
                            if (assessmentData.size() > 0) {
                                updateView(assessmentData);
                                updatePageState();
                            }
                        }
                    }
                }
            }
        });
        task.setDownloadEvalZip(true);
        task.execute();
    }

    private void initListAdapterData() {
        pageThumbnailAdapter = new PageThumbnailAdapter();
        if (listView != null) {
            listView.setAdapter(pageThumbnailAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onClickItemData(position);
                }
            });
        } else if (horizontalListView != null) {
            horizontalListView.setAdapter(pageThumbnailAdapter);
            horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onClickItemData(position);
                }
            });
        }
    }

    private void onClickItemData(int position) {
        if (position == currentPagePosition) {
            return;
        }
        if (isRecordingAudio()) {
            return;
        }
        resetAudioPlayer();
        currentPagePosition = position;
        for (int i = 0, len = assessmentData.size(); i < len; i++) {
            assessmentData.get(i).setIsCheck(false);
        }
        setImageViewThumbnail(currentPageImageV, assessmentData.get(position).getImageUrl());
        assessmentData.get(position).setIsCheck(true);
        pageThumbnailAdapter.notifyDataSetChanged();
        changeMessageShow(false);
        updatePageState();
    }

    private void loadCourseTextData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("courseId", resId);
        RequestHelper.RequestResourceResultListener listener = new RequestHelper.RequestResourceResultListener(
                SpeechAssessmentActivity.this, ResourceResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                super.onSuccess(jsonString);
                ResourceResult resourceResult = (ResourceResult) getResult();
                if (resourceResult != null && resourceResult.isSuccess()) {
                    try {
                        EvaluationData evaluationData = JSONObject.parseObject(resourceResult.getData().toString(), EvaluationData.class);
                        if (evaluationData != null) {
                            loadData(evaluationData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        RequestHelper.sendGetRequest(SpeechAssessmentActivity.this, ServerUrl.GET_SPEECH_ASSESSMENT_TEXT_BASE_URL,
                params, listener);
    }


    private void loadData(final EvaluationData evaluationData) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("courseId", resId);
        params.put("version", 2);
        RequestHelper.RequestResourceResultListener listener = new RequestHelper.RequestResourceResultListener(
                SpeechAssessmentActivity.this, CourseImageListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                super.onSuccess(jsonString);
                CourseImageListResult result = (CourseImageListResult) getResult();
                if (result == null || result.getCode() != 0) {
                    TipsHelper.showToast(SpeechAssessmentActivity.this, R.string.no_course_images);
                    return;
                }
                try {
                    List<SpeechAssessmentData> speechAssessmentData = JSONObject.parseArray(result
                            .getData().toString(), SpeechAssessmentData.class);
                    if (speechAssessmentData != null && speechAssessmentData.size() > 0) {
                        //过滤没有评测的文本
                        List<SpeechAssessmentData> data = new ArrayList<>();
                        List<EvaluationData.TextContent> textContent = evaluationData.getTextContent();
                        if (textContent != null && textContent.size() > 0) {
                            for (int i = 0, len = textContent.size(); i < len; i++) {
                                EvaluationData.TextContent content = textContent.get(i);
                                if (!TextUtils.isEmpty(content.getText()) && i < speechAssessmentData.size()) {
                                    SpeechAssessmentData assessmentData = speechAssessmentData.get(i);
                                    assessmentData.setRef_text(content.getText());
                                    assessmentData.setSpeedModel(SpeedModelType.SPEED_OF_MEDIUM);
                                    data.add(assessmentData);
                                }
                            }
                            updateView(data);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(SpeechAssessmentActivity.this, ServerUrl.COURSE_IMAGES_URL,
                params, listener);
    }

    private void updateView(List<SpeechAssessmentData> speechAssessmentData) {
        assessmentData = speechAssessmentData;
        assessmentData.get(0).setIsCheck(true);
        showTitleFinishCount(0, assessmentData.size());
        setImageViewThumbnail(currentPageImageV, assessmentData.get(0).getImageUrl());
        changeMessageShow(false);
        initListAdapterData();
        analysisAssessmentRefTextData();
    }

    private void analysisAssessmentRefTextData() {
        for (int i = 0, len = assessmentData.size(); i < len; i++) {
            SpeechAssessmentData data = assessmentData.get(i);
            String refText = data.getRef_text();
            if (!TextUtils.isEmpty(refText)) {
                if (Utils.isEnglishLanguage(data.getRef_text())) {
                    data.setIsEnglishLanguage(true);
                    String englishText = "。。。\n" + refText;
                    String regEx = "[。、，；？！?.,;!\\s+]";
                    String[] splitArray = englishText.split(regEx);
                    ArrayList<Integer> wordIndexArray = new ArrayList<>();
                    wordIndexArray.add(1);
                    wordIndexArray.add(2);
                    wordIndexArray.add(3);
                    if (splitArray.length > 0) {
                        int tempIndex = 0;
                        for (int j = 0; j < splitArray.length; j++) {
                            String word = splitArray[j];
                            if (!TextUtils.isEmpty(word)) {
                                int wordStartIndex = englishText.indexOf(word, tempIndex);
                                tempIndex = wordStartIndex + word.length();
                                if (tempIndex < englishText.length()) {
                                    wordIndexArray.add(tempIndex + 1);
                                } else {
                                    wordIndexArray.add(tempIndex);
                                }

                            }
                        }
                    }
                    data.setWordIndex(wordIndexArray);
                }
            }
        }
    }

    private void createFolderPath() {
        //先删除之前可能存在的folderPath 避免.mp3重复
        FileUtils.deleteDir(folderPath);
        //创建文件夹
        FileUtils.createOrExistsDir(folderPath);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.contacts_header_left_btn) {
            if (isRecordingAudio()) {
                return;
            }
            resetAudioPlayer();
            if (isOnlinePlay) {
                finish();
            } else {
                commitPageTask(true, "");
            }
        } else if (viewId == R.id.contacts_header_right_btn) {
            if (isRecordingAudio()) {
                return;
            }
            resetAudioPlayer();
            //发送
            sendSpeechAssessmentTask();
        } else if (viewId == R.id.ll_left) {
            //点击显示文本
            changeMessageShow(true);
        } else if (viewId == R.id.tv_fundamental_tone) {
            if (isRecordingAudio()) {
                return;
            }
            //原音
            playAudioData(false);
        } else if (viewId == R.id.iv_record) {
            resetAudioPlayer();
            //录音
            recordOrStopAudio();
        } else if (viewId == R.id.iv_play) {
            if (isRecordingAudio()) {
                return;
            }
            //播放录制的原音
            playAudioData(true);
        }
    }

    private void showTitleFinishCount(int finishCount, int totalCount) {
        if (isOnlinePlay) {
            return;
        }
        String finishPer = finishCount + "/" + totalCount;
        headTitleTextV.setText(getString(R.string.n_finish, finishPer));
    }

    private void sendSpeechAssessmentTask() {
        if (assessmentData != null && assessmentData.size() > 0) {
            boolean isFinishAllPageTask = true;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0, len = assessmentData.size(); i < len; i++) {
                if (TextUtils.isEmpty(assessmentData.get(i).getEval_result())) {
                    isFinishAllPageTask = false;
                    if (stringBuilder.length() == 0) {
                        //第一个
                        stringBuilder.append(i + 1);
                    } else {
                        stringBuilder.append("，").append(i + 1);
                    }
                }
            }
            if (isFinishAllPageTask) {
                commitAllPageTask();
            } else {
                commitPageTask(false, stringBuilder.toString());
            }
        }
    }

    private void commitPageTask(final boolean isBack, String unFinishPosition) {
        String messageContent = getString(R.string.str_back_show_evaluation_tip);
        if (!isBack) {
            messageContent = getString(R.string.str_commit_evaluation_unfinish_tip, unFinishPosition);
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                SpeechAssessmentActivity.this, null,
                messageContent,
                isBack ? getString(R.string.discard_save) : getString(R.string.str_continue_eval),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isBack) {
                            //返回键时back
                            finish();
                        }
                    }
                },
                getString(R.string.commit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        commitAllPageTask();
                    }
                });
        messageDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isOnlinePlay) {
            super.onBackPressed();
        } else {
            if (isRecordingAudio()) {
                return;
            }
            commitPageTask(true, null);
        }
    }

    /**
     * 提交语音评测的内容
     */
    private void commitAllPageTask() {
        //组装数据提交
        if (assessmentData != null && assessmentData.size() > 0) {
            JSONObject evalObject = new JSONObject();
            JSONArray pageArray = new JSONArray();
            for (int i = 0, len = assessmentData.size(); i < len; i++) {
                SpeechAssessmentData evalData = assessmentData.get(i);
                JSONObject pageData = new JSONObject();
                pageData.put("ref_audio_url", evalData.getMp3Url());
                pageData.put("ref_audio_start", evalData.getStart());
                pageData.put("ref_audio_end", evalData.getEnd());
                pageData.put("ref_text", evalData.getRef_text());
                pageData.put("ref_image_url", evalData.getImageUrl());
                pageData.put("my_audio_path", getLocalPath(evalData.getMy_audio_path()));
                pageData.put("eval_score", evalData.getEval_score());
                pageData.put("eval_result", evalData.getEval_result());
                pageArray.add(pageData);
            }
            evalObject.put("page_list", pageArray);
            evalObject.put("eval_scheme_id", assessmentData.get(0).getEval_scheme_id());
            FileUtils.createOrExistsFile(pageIndexPath);
            FileUtils.saveDataToFile(evalObject.toJSONString(), pageIndexPath);
            FileUtils.deleteFilterFileInDir(new File(folderPath), ".pcm");
            uploadCourse();
        }
    }

    private void uploadCourse() {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (studentUserInfo != null) {
            userInfo = studentUserInfo;
        }
        final UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        uploadParameter.setAccount(userInfo.getNickName());
        uploadParameter.setFilePath(folderPath);
        uploadParameter.setFileName(taskTitle);
        uploadParameter.setResType(ResType.RES_TYPE_EVALUATE);
        uploadParameter.setIsNeedSplit(false);
        uploadParameter.setColType(1);
        uploadParameter.setScreenType(mOrientation);
        String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
        uploadParameter.setUploadUrl(uploadUrl);
        uploadParameter.setType(1);
        showLoadingDialog();
        //打成压缩包
        FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                folderPath, Utils.TEMP_FOLDER + "evaluation_" + DateUtils.millSecToDateStr
                (SystemClock.currentThreadTimeMillis()) + Utils.COURSE_SUFFIX);
        FileZipHelper.zip(param, new FileZipHelper.ZipUnzipFileListener() {
            @Override
            public void onFinish(FileZipHelper.ZipUnzipResult result) {
                if (result != null && result.mIsOk) {
                    uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                    UploadUtils.uploadResource(SpeechAssessmentActivity.this, uploadParameter,
                            new CallbackListener() {
                                @Override
                                public void onBack(Object result) {
                                    dismissLoadingDialog();
                                    if (result != null) {
                                        runOnUiThread(() -> {
                                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                                            if (uploadResult.code != 0) {
                                                TipMsgHelper.ShowLMsg(SpeechAssessmentActivity.this, R.string.upload_file_failed);
                                                return;
                                            }
                                            if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                final CourseData courseData = uploadResult.data.get(0);
                                                if (courseData != null) {
                                                    if (fromOnlineStudyTask) {
                                                        //在线课堂提交学生的任务
                                                        commitStudentOnlineTaskList(courseData);
                                                    } else {
                                                        commitStudentTaskList(courseData);
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

    private void commitStudentOnlineTaskList(final CourseData courseData) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        if (studentUserInfo != null) {
            params.put("StudentId", studentUserInfo.getMemberId());
        } else {
            params.put("StudentId", DemoApplication.getInstance().getMemberId());
        }
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", taskTitle);
        }
        params.put("IsVoiceReview", true);
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("SchoolId", schoolId);
        }
        if (!TextUtils.isEmpty(schoolName)) {
            params.put("SchoolName", schoolName);
        }
        if (!TextUtils.isEmpty(classId)) {
            params.put("ClassId", classId);
        }
        if (!TextUtils.isEmpty(className)) {
            params.put("ClassName", className);
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(SpeechAssessmentActivity.this, DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                DataResult result = JSON.parseObject(jsonString, DataResult.class);
                CommitTaskResult taskResult = JSON.parseObject(jsonString, CommitTaskResult.class);

                if (result != null && result.isSuccess()) {
                    commitTaskScore(courseData, taskResult.Model.CommitTaskId);
                    TipMsgHelper.ShowLMsg(SpeechAssessmentActivity.this, R.string.commit_success);
                } else {
                    String errorMessage = getString(R.string.publish_course_error);
                    if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                        errorMessage = result.getErrorMessage();
                    }
                    TipMsgHelper.ShowLMsg(SpeechAssessmentActivity.this, errorMessage);
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(SpeechAssessmentActivity.this, ServerUrl
                .GET_UPDATE_TASK_STATE_DONE_ONLINE, params, listener);
    }

    private void commitStudentTaskList(final CourseData courseData) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        if (studentUserInfo != null) {
            params.put("StudentId", studentUserInfo.getMemberId());
        } else {
            params.put("StudentId", DemoApplication.getInstance().getMemberId());
        }
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", taskTitle);
        }
        params.put("IsVoiceReview", true);
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(SpeechAssessmentActivity.this, DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                DataResult result = JSON.parseObject(jsonString, DataResult.class);
                CommitTaskResult taskResult = JSON.parseObject(jsonString, CommitTaskResult.class);

                if (result != null && result.isSuccess()) {
                    commitTaskScore(courseData, taskResult.Model.CommitTaskId);
                    TipMsgHelper.ShowLMsg(SpeechAssessmentActivity.this, R.string.commit_success);
                } else {
                    String errorMessage = getString(R.string.publish_course_error);
                    if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                        errorMessage = result.getErrorMessage();
                    }
                    TipMsgHelper.ShowLMsg(SpeechAssessmentActivity.this, errorMessage);
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(SpeechAssessmentActivity.this, ServerUrl
                .PUBLISH_STUDENT_HOMEWORK_URL, params, listener);
    }

    /**
     * 上传作业的分数
     */
    private void commitTaskScore(final CourseData courseData, int commitTaskId) {
        Map<String, Object> params = new HashMap<>();
        if (fromOnlineStudyTask) {
            params.put("CommitTaskOnlineId", commitTaskId);
        } else {
            params.put("CommitTaskId", commitTaskId);
        }
        int taskScore = getAverageScore();
        if (scoreRule == 1) {
            params.put("TaskScore", StudyTaskUtils.percentTransformTenLevel(taskScore));
        } else {
            params.put("TaskScore", taskScore);
        }
        if (courseData != null) {
            params.put("ResId", courseData.getIdType());
            params.put("ResUrl", courseData.resourceurl);
        }
        params.put("AutoEvalCompanyType", 4);
        params.put("AutoEvalContent", getScoreArrayList());
        RequestHelper.RequestListener<DataResult> listener = new RequestHelper
                .RequestListener<DataResult>(SpeechAssessmentActivity.this, DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (getResult() != null && getResult().isSuccess()) {
                    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
                    broadcastManager.sendBroadcast(new Intent(EvalHomeworkListFragment
                            .ACTION_MARK_SCORE).putExtra("commit_resId", courseData.id));
                    HomeworkCommitFragment.setHasCommented(true);
                    //删除本地制作的文件夹
                    FileUtils.deleteDir(folderPath);
                    EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                    finish();
                }
            }
        };
        RequestHelper.sendPostRequest(SpeechAssessmentActivity.this, ServerUrl
                .COMMIT_AUTO_MARK_SCORE, params, listener);
    }

    private int getAverageScore() {
        int total = 0;
        for (int i = 0, len = assessmentData.size(); i < len; i++) {
            SpeechAssessmentData data = assessmentData.get(i);
            total = total + data.getEval_score();
        }
        return total / assessmentData.size();
    }

    private String getScoreArrayList() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0, len = assessmentData.size(); i < len; i++) {
            SpeechAssessmentData data = assessmentData.get(i);
            jsonArray.add(data.getEval_score());
        }
        return jsonArray.toJSONString();
    }


    private String getLocalPath(String audioPath) {
        if (TextUtils.isEmpty(audioPath)) {
            return "";
        }
        String[] splitArray = audioPath.split("/");
        return splitArray[splitArray.length - 1];
    }


    private void changeMessageShow(boolean isCheckMessageText) {
        SpeechAssessmentData selectData = assessmentData.get(currentPagePosition);
        if (isCheckMessageText) {
            selectData.setIsShowingText(!selectData.isShowingText());
        }
        if (isOnlinePlay) {
            //在线打开
            if (selectData.isShowingText()) {
                arrowImageV.setImageResource(R.drawable.arrow_gray_up_icon);
                audioMessageTextV.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(selectData.getEval_result())) {
                    //没有评测的文本
                    audioMessageTextV.setText(selectData.getRef_text());
                } else {
                    //显示评测的文本
                    audioMessageTextV.setText(selectData.getSpannableString());
                }
            } else {
                arrowImageV.setImageResource(R.drawable.arrow_gray_down_icon);
                audioMessageTextV.setVisibility(View.GONE);
            }
        } else {
            //语音评测制作
            if (isCheckMessageText) {
                if (playRefIndexPosition > 0) {
                    TipMsgHelper.ShowMsg(this, R.string.str_following_read);
                    return;
                }
                //弹出窗口选择跟读速度
                showPopWindowCheckScore();
            }
//            arrowImageV.setImageResource(R.drawable.arrow_gray_down_icon);
            arrowImageV.setVisibility(View.GONE);
            showTextLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_corner_text_green_20_dp));
            showTextLayout.setPadding(10, 10, 10, 10);
            showTextLayout.setMinimumWidth(100);
            leftMessageTextV.setTextColor(ContextCompat.getColor(this, R.color.text_white));
            leftMessageTextV.setText(speedOfRead[selectData.getSpeedModel()]);
            if (playType == PlayAudioType.PLAY_VOICE_TYPE) {
                //正在播放录音中
                return;
            }
            if (TextUtils.isEmpty(selectData.getEval_result())) {
                //没有评测的文本
                audioMessageTextV.setTextColor(ContextCompat.getColor(this, R.color.text_white));
                audioMessageTextV.setText(selectData.getRef_text());
            } else {
                //显示评测的文本
                audioMessageTextV.setText(selectData.getSpannableString());
            }
            audioMessageTextV.setVisibility(View.VISIBLE);
        }
    }

    private void recordOrStopAudio() {
        if (isRecording) {
            isRecording = false;
            recordImageV.setImageResource(R.drawable.icon_eval_recording);
            audioRecorder.stop();
        } else {
            pageSoreTextV.setVisibility(View.GONE);
            startProgressThread();
            final String savePath = folderPath + "/" + (currentPagePosition + 1) + ".mp3";
            isRecording = true;
            recordImageV.setImageResource(R.drawable.icon_eval_end_record);
            if (audioRecorder == null) {
                audioRecorder = new RawAudioRecorder(SpeechAssessmentActivity.this);
                audioRecorder.setListener(new RawAudioRecorder.RawRecorderListener() {
                    @Override
                    public void onRawRecordingStart() {

                    }

                    @Override
                    public void onRawRecordingEnd(String encodedFilePath, String rawFilePath) {
                        if (isPressHomeKey) {
                            if (!TextUtils.isEmpty(encodedFilePath)) {
                                deleteFile(encodedFilePath);
                            }
                            if (!TextUtils.isEmpty(rawFilePath)) {
                                deleteFile(rawFilePath);
                            }
                            resetRecordingData();
                            return;
                        }
                        if (!TextUtils.isEmpty(encodedFilePath) && !TextUtils.isEmpty(rawFilePath)) {
                            showLoadingDialog();
                            evaluateRecordData(encodedFilePath, rawFilePath);
                        }
                    }

                    @Override
                    public void onRawRecordingError() {
                        deleteFile(savePath);
                        TipsHelper.showToast(SpeechAssessmentActivity.this, R.string.str_record_err);
                    }
                });
            }
            deleteFile(savePath);
            audioRecorder.start(savePath);
        }
    }

    private void evaluateRecordData(final String mp3FilePath, final String rawFilePath) {
        final EvaluateManager evaluateManager = new EvaluateManager(SpeechAssessmentActivity.this);
        EvaluateItem evaluateItem = EvaluateHelper.getEvaluateItem(currentPagePosition + 1,
                assessmentData.get(currentPagePosition).getRef_text(), rawFilePath);
        List<EvaluateItem> itemList = new ArrayList<>();
        itemList.add(evaluateItem);
        evaluateManager.evaluateAsync(itemList, new EvaluateListener() {
            @Override
            public void onEvaluateResult(EvaluateResult result) {
                SpeechAssessmentData data = assessmentData.get(currentPagePosition);
                dismissLoadingDialog();
                deleteFile(rawFilePath);
                if (result == null || !result.isSuccess()) {
                    deleteFile(mp3FilePath);
                    TipsHelper.showToast(SpeechAssessmentActivity.this, R.string.str_evaluate_fail);
                    //评测失败 重置录制的内容
                    resetRecordingData();
                    return;
                }
                data.setMy_audio_path(mp3FilePath);
                data.setEval_score(Utils.floatChangeToInt(result.getScore()));
                JSONArray jsonArray = JSONObject.parseArray(result.getResult());
                if (jsonArray != null && jsonArray.size() > 0) {
                    data.setEval_result(JSONObject.parseArray(result.getResult()).get(0).toString());
                }
                data.setEval_scheme_id(result.getSchemeId());
                data.setIsAlreadyEval(true);
                data.setSpannableString(getSpannableString(result.getSchemeId(), result.getResult()));
                updatePageState();
                TipsHelper.showToast(SpeechAssessmentActivity.this, R.string.str_evaluate_success);
            }
        });
    }

    private SpannableString getSpannableString(int schemeId, String result) {
        if (!isOnlinePlay) {
            JSONArray jsonArray = JSONObject.parseArray(result);
            if (jsonArray == null || jsonArray.size() == 0) {
                return null;
            }
            result = jsonArray.get(0).toString();
        }
        EvaluateItemResult evaluateItemResult = EvaluateManager.parseItemResult(schemeId, result);
        SpannableString spannableString = null;
        if (evaluateItemResult != null) {
            spannableString = EvaluateHelper.getSpannableEvaluateItemText(evaluateItemResult);
        }
        return spannableString;
    }

    private void playAudioData(boolean isAudioRecord) {
        SpeechAssessmentData data = assessmentData.get(currentPagePosition);
        if (isAudioRecord) {
            if (TextUtils.isEmpty(data.getMy_audio_path())) {
                return;
            }
        } else {
            if (TextUtils.isEmpty(data.getMp3Url())) {
                return;
            }
        }
        if (mediaPlayer != null) {
            mediaPlayer.releaseAudio();
        }
        if (mediaPlayer == null) {
            mediaPlayer = new AudioPlayerHelper(SpeechAssessmentActivity.this);
        }
        if (isAudioRecord) {
            startProgressThread();
            //录制的音频
            mediaPlayer.setPlayUrl(data.getMy_audio_path());
            mediaPlayer.play();
        } else {
            stopProgressThread();
            //播放的原音
            mediaPlayer.setPlayUrl(data.getMp3Url());
            int startPosition = 0;
            int endPosition = 0;
            if (!TextUtils.isEmpty(data.getStart())) {
                startPosition = Integer.valueOf(data.getStart());
            }
            if (!TextUtils.isEmpty(data.getEnd())) {
                endPosition = Integer.valueOf(data.getEnd());
            }
            mediaPlayer.seekTo(startPosition, endPosition);
        }
        mediaPlayer.setCompleteListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                boolean hasComplete = (boolean) result;
                if (hasComplete) {
                    LogUtils.log(TAG, "mediaPlayer--complete");
                    if (!isAudioRecord && isOnlinePlay && playType == PlayAudioType.PLAY_VOICE_TYPE){
                        data.setIsShowingText(true);
                    }
                    resetAudioPlayer();
                }
            }
        });
        if (isAudioRecord) {
            if (playType != PlayAudioType.PLAY_RECORD_TYPE) {
                playType = PlayAudioType.PLAY_RECORD_TYPE;
                if (isOnlinePlay) {
                    changeMessageShow(false);
                } else {
                    audioMessageTextV.setVisibility(View.VISIBLE);
                }
                stopAnim();
            }
        } else {
            if (playType != PlayAudioType.PLAY_VOICE_TYPE) {
                playType = PlayAudioType.PLAY_VOICE_TYPE;
                stopAnim();
            }
        }
        startAnim();
    }

    private void setImageViewThumbnail(ImageView imageView, String url) {
        LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
        param.mDefaultIcon = R.drawable.whiteboard_color;
        param.mHighQuality = true;
        param.mIsCacheInMemory = false;
        param.mIsCacheOnDisc = true;
        param.mOutWidth = ScreenUtils.getScreenWidth(getApplicationContext());
        param.mOutHeight = ScreenUtils.getScreenHeight(getApplicationContext());
        LQImageLoader.displayImage(url, imageView, param);
    }

    private class PageThumbnailAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return assessmentData.size();
        }

        @Override
        public Object getItem(int position) {
            return assessmentData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(SpeechAssessmentActivity.this).inflate(R.layout
                        .item_assessment_thumbnail, null);
                holder = new MyViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
                holder.rlView = (RelativeLayout) convertView.findViewById(R.id.rl_thumbnail);
                holder.pagePositionTextV = (TextView) convertView.findViewById(R.id.tv_page_position);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            holder.pagePositionTextV.setText(String.valueOf(position + 1));
            int height = 0;
            int width = 0;
            if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                height = listView == null ? horizontalListView.getHeight() : listView.getHeight();
                width = height * 210 / 297;
            } else {
                width = listView == null ? horizontalListView.getWidth() : listView.getWidth();
                height = width * 210 / 297;
            }
            SpeechAssessmentData data = (SpeechAssessmentData) getItem(position);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.rlView.getLayoutParams();
            lp.width = width;
            lp.height = height;
            holder.rlView.setLayoutParams(lp);
            if (data.isCheck()) {
                convertView.setBackgroundResource(R.drawable.thumb_item_hl_bg);
            } else {
                convertView.setBackgroundResource(R.drawable.transparent_background);
            }
            MyApplication.getThumbnailManager(SpeechAssessmentActivity.this).
                    displayImageWithDefault(data.getImageUrl(), holder.imageView, R.drawable.whiteboard_color);
            return convertView;
        }

        class MyViewHolder {
            RelativeLayout rlView;
            ImageView imageView;
            TextView pagePositionTextV;
        }
    }

    public boolean deleteFile(String path) {
        if (FileUtils.isFileExists(path)) {
            return FileUtils.deleteFile(path);
        }
        return true;
    }

    private void updatePageState() {
        if (pageSoreTextV == null) {
            return;
        }
        SpeechAssessmentData data = assessmentData.get(currentPagePosition);
        if (data.isAlreadyEval()) {
            pageSoreTextV.setVisibility(View.VISIBLE);
            int pageScore = data.getEval_score();
            if (scoreRule == 1) {
                pageSoreTextV.setText(getString(R.string.str_eval_score, StudyTaskUtils
                        .percentTransformTenLevel(pageScore)));
            } else {
                pageSoreTextV.setText(getString(R.string.str_eval_score, String.valueOf(data
                        .getEval_score())));
            }
        } else {
            pageSoreTextV.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(data.getMy_audio_path())) {
            playRecordAudio.setImageResource(R.drawable.icon_eval_play_gray);
        } else {
            playRecordAudio.setImageResource(R.drawable.icon_eval_play_green);
        }
        int finishCount = 0;
        for (int i = 0; i < assessmentData.size(); i++) {
            if (!TextUtils.isEmpty(assessmentData.get(i).getMy_audio_path())) {
                finishCount++;
            }
        }
        showTitleFinishCount(finishCount, assessmentData.size());
        changeMessageShow(false);
    }

    private boolean isRecordingAudio() {
        if (isRecording) {
            TipsHelper.showToast(SpeechAssessmentActivity.this, R.string.str_recording);
            return true;
        }
        return false;
    }

    private void resetAudioPlayer() {
        LogUtils.log(TAG, "resetAudioPlayer()");
        if (mediaPlayer != null) {
            mediaPlayer.releaseAudio();
        }
        if (playType == PlayAudioType.PLAY_VOICE_TYPE) {
            //播放的原音
            if (isOnlinePlay) {
                changeMessageShow(false);
            } else {
                audioMessageTextV.setVisibility(View.VISIBLE);
            }
        }
        playType = 0;
        stopAnim();
        stopProgressThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording && audioRecorder != null) {
            //正在录制中
            isPressHomeKey = true;
            isRecording = false;
            recordImageV.setImageResource(R.drawable.icon_eval_recording);
            audioRecorder.stop();
        }
        resetAudioPlayer();
    }

    private void resetRecordingData() {
        isPressHomeKey = false;
        SpeechAssessmentData data = assessmentData.get(currentPagePosition);
        if (data != null) {
            data.setMy_audio_path(null);
            data.setEval_score(0);
            data.setIsAlreadyEval(false);
            data.setEval_result(null);
            updatePageState();
        }
    }

    @Override
    protected void onDestroy() {
        FileUtils.deleteDir(folderPath);
        resetAudioPlayer();
        if (!TextUtils.isEmpty(onlinePlayTempFolder)) {
            //删除临时的文件夹
            Utils.safeDeleteDirectory(onlinePlayTempFolder);
        }
        super.onDestroy();
    }

    /**
     * 开始动画
     */
    private void startAnim() {
        if (playType == PlayAudioType.PLAY_RECORD_TYPE) {
            //播放录制的音频
            if (recordPlayingAnim != null && !recordPlayingAnim.isRunning()) {
                playRecordAudio.setImageResource(R.drawable.icon_audio_playing_1);
                playRecordAudio.setBackgroundDrawable(recordPlayingAnim);
                recordPlayingAnim.start();
            }
        } else if (playType == PlayAudioType.PLAY_VOICE_TYPE) {
            //原音
            if (voicePlayAnim != null && !voicePlayAnim.isRunning()) {
                fundamentalToneTextV.setImageResource(R.drawable.icon_audio_playing_1);
                fundamentalToneTextV.setBackgroundDrawable(voicePlayAnim);
                voicePlayAnim.start();
                if (isOnlinePlay && assessmentData != null){
                    SpeechAssessmentData selectData = assessmentData.get(currentPagePosition);
                    if (selectData.isShowingText()) {
                        selectData.setIsShowingText(false);
                        changeMessageShow(false);
                    }
                } else if (audioMessageTextV != null){
                    //播放原音时隐藏评测的文本
                    audioMessageTextV.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 关闭动画
     */
    private void stopAnim() {
        if (playType != PlayAudioType.PLAY_RECORD_TYPE) {
            //录制的语音
            if (recordPlayingAnim != null && recordPlayingAnim.isRunning()) {
                recordPlayingAnim.stop();
                playRecordAudio.setImageResource(R.drawable.icon_eval_play_green);
                playRecordAudio.setBackgroundDrawable(null);
            }
        }

        if (playType != PlayAudioType.PLAY_VOICE_TYPE) {
            //原音
            if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                voicePlayAnim.stop();
                fundamentalToneTextV.setImageResource(R.drawable.icon_play_original_record);
                fundamentalToneTextV.setBackgroundDrawable(null);
            }
        }
    }

    private void showAudioTextProgress(SpeechAssessmentData data) {
        SpannableString refText = new SpannableString("。。。\n" + data.getRef_text());
        if (data.isEnglishLanguage()) {
            ArrayList<Integer> indexArray = data.getWordIndex();
            if (playRefIndexPosition < indexArray.size()) {
                int lastPosition = indexArray.get(indexArray.size() - 1);
                int startPosition = indexArray.get(playRefIndexPosition);
                if (startPosition <= lastPosition && startPosition != 0) {
                    audioMessageTextV.setTextColor(ContextCompat.getColor(this, R.color.text_white));
                    refText.setSpan(new ForegroundColorSpan(Color.GREEN)
                            , 0, startPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    audioMessageTextV.setText(refText);
                }
            } else {
                audioMessageTextV.setTextColor(Color.GREEN);
            }
        } else {
            if (playRefIndexPosition <= refText.length() && playRefIndexPosition != 0) {
                audioMessageTextV.setTextColor(ContextCompat.getColor(this, R.color.text_white));
                refText.setSpan(new ForegroundColorSpan(Color.GREEN)
                        , 0, playRefIndexPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                audioMessageTextV.setText(refText);
            }
        }
    }

    private void startProgressThread() {
        LogUtils.log(TAG, "startProgressThread()");
        if (isOnlinePlay) {
            return;
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
        }
        if (assessmentData != null) {
            final SpeechAssessmentData data = assessmentData.get(currentPagePosition);
            mTimeTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAudioTextProgress(data);
                            playRefIndexPosition++;
                            LogUtils.log(TAG, "playRefIndexPosition = " + playRefIndexPosition);
                        }
                    });
                }
            };
            if (mTimer != null) {
                int speed = 0;
                if (data.getSpeedModel() == SpeedModelType.SPEED_OF_SLOW) {
                    speed = data.isEnglishLanguage() ? playEnglishSpeed[0] :
                            playChinaSpeed[0];
                } else if (data.getSpeedModel() == SpeedModelType.SPEED_OF_MEDIUM) {
                    speed = data.isEnglishLanguage() ? playEnglishSpeed[1] :
                            playChinaSpeed[1];
                } else if (data.getSpeedModel() == SpeedModelType.SPEED_OF_HIGH) {
                    speed = data.isEnglishLanguage() ? playEnglishSpeed[2] :
                            playChinaSpeed[2];
                }
                mTimer.schedule(mTimeTask, 100, speed);
            }
        }
    }

    private void stopProgressThread() {
        if (isOnlinePlay) {
            return;
        }
        LogUtils.log(TAG, "stopProgressThread()");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            playRefIndexPosition = 0;
            if (!isRecording) {
                changeMessageShow(false);
            }
        }
        if (mTimeTask != null) {
            mTimeTask.cancel();
            mTimeTask = null;
        }
    }

    private void showPopWindowCheckScore() {
        List<String> speedList = Arrays.asList(speedOfRead);
        ContactsListDialog dialog = new ContactsListDialog(this,
                R.style.Theme_ContactsDialog, null,
                speedList, R.layout.contacts_dialog_list_text_item,
                new DataAdapter.AdapterViewCreator() {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView != null) {
                            String title = (String) convertView.getTag();
                            TextView textView = (TextView) convertView.findViewById(
                                    R.id.contacts_dialog_list_item_title);
                            if (textView != null) {
                                textView.setTextColor(ContextCompat.getColor(SpeechAssessmentActivity.this, R.color.text_black));
                                textView.setText(title);
                            }
                        }
                        return convertView;
                    }
                },
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (assessmentData != null && assessmentData.size() > 0) {
                            SpeechAssessmentData currentData = assessmentData.get(currentPagePosition);
                            currentData.setSpeedModel(position);
                            changeMessageShow(false);
                        }
                    }
                }, getString(R.string.cancel), null);
        Button cancelBtn = dialog.getCancelButton();
        cancelBtn.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }
}
