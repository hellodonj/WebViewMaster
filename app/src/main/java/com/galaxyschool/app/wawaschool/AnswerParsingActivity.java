package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.course.PlaybackActivityPhone;
import com.galaxyschool.app.wawaschool.fragment.AnswerParsingFragment;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.helper.ApplyMarkHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItemArea;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.TaskMarkParam;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.views.AssistantCheckMarkWayDialog;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.client.pojo.LearnTaskCardType;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.SlideManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment.REQUEST_CODE_DO_SLIDE_TOAST;

public class AnswerParsingActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = AnswerParsingActivity.class.getSimpleName();
    private ImageView lastQuestionTextV, nextQestionTextV;
    private FrameLayout applyMarkLayout;
    private TextView applyMarkTextV;
    private MyViewPager viewPager;
    private MyFragmentPagerAdapter pagerAdapter;
    private List<Fragment> fragments;
    private int currentPosition = 0;
    private ExerciseAnswerCardParam cardParam;
    private boolean fromAnswerAnalysis;
    private List<ExerciseItem> exerciseItemList;
    private boolean lookSingleDetail;
    private String pageListString;
    private String exerciseString;
    private PlaybackParam playbackParam;
    private String title;
    private int screenType;
    private boolean isOnlineMode;
    private TaskEntity taskEntity;

    public interface Constants {
        String SINGLE_QUESTION_ANSWER = "single_question_answer";
        String FROM_ANSWER_ANALYSIS = "from_answer_analysis";
        String STUDENT_ID = "student_id";
        String STUDENT_NAME = "student_name";
        String LOOK_SINGLE_QUESTION_DETAIL = "look_single_question_detail";
        String DO_COURSE_SLIDE_ONLINE_MODE = "do_course_slide_online_mode";
    }

    public static void start(Activity activity,
                             ExerciseAnswerCardParam cardParam,
                             int questionPosition,
                             boolean fromAnswerAnalysis,
                             boolean lookSingleDetail) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, AnswerParsingActivity.class);
        Bundle args = new Bundle();
        if (cardParam != null) {
            args.putSerializable(ExerciseAnswerCardParam.class.getSimpleName(), cardParam);
        }
        args.putBoolean(Constants.FROM_ANSWER_ANALYSIS, fromAnswerAnalysis);
        args.putInt("exerciseIndex", questionPosition);
        args.putBoolean(Constants.LOOK_SINGLE_QUESTION_DETAIL, lookSingleDetail);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_parsing);
        loadIntentData();
        initViews();
        initData();
        initViewPagerAdapter();
    }

    private void loadIntentData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            fromAnswerAnalysis = args.getBoolean(Constants.FROM_ANSWER_ANALYSIS, false);
            pageListString = args.getString("pageListString");
            currentPosition = args.getInt("exerciseIndex");
            exerciseString = args.getString("exerciseListString");
            currentPosition = currentPosition - 1;
            lookSingleDetail = args.getBoolean(Constants.LOOK_SINGLE_QUESTION_DETAIL, false);
            playbackParam = (PlaybackParam) args.getSerializable(PlaybackParam.class.getSimpleName());
            title = args.getString(PlaybackActivity.FILE_NAME);
            screenType = args.getInt(PlaybackActivity.ORIENTATION);
            isOnlineMode = args.getBoolean(Constants.DO_COURSE_SLIDE_ONLINE_MODE,false);
            taskEntity = (TaskEntity) args.getSerializable(TaskEntity.class.getSimpleName());
            if (fromAnswerAnalysis) {
                cardParam = (ExerciseAnswerCardParam) args.getSerializable(ExerciseAnswerCardParam.class.getSimpleName());
            } else {
                PlaybackParam mParam = (PlaybackParam) args.getSerializable(PlaybackParam.class.getSimpleName());
                if (mParam != null) {
                    cardParam = mParam.exerciseCardParam;
                }
            }
            if (cardParam == null) {
                if (!TextUtils.isEmpty(exerciseString)) {
                    JSONArray jsonArray = JSONObject.parseArray(exerciseString);
                    if (jsonArray != null && jsonArray.size() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        ExerciseItem exerciseItem = JSONObject.parseObject(jsonObject.toString(), ExerciseItem.class);
                        if (exerciseItem != null) {
                            String type = exerciseItem.getType();
                            String studentState = exerciseItem.getStudent_state();
                            if (!TextUtils.isEmpty(studentState)){
                                if (!studentState.contains(",")){
                                    exerciseItem.setEqState(Integer.valueOf(studentState));
                                }
                            }
                            if (!TextUtils.isEmpty(type)) {
                                if (Integer.valueOf(type) == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                                    List<MediaData> mediaDataList = new ArrayList<>();
                                    String studentCommitResUrl = exerciseItem.getStudent_answer_res_url();
                                    if (!TextUtils.isEmpty(studentCommitResUrl)) {
                                        if (studentCommitResUrl.contains(",")) {
                                            String[] splitArray = studentCommitResUrl.split(",");
                                            for (int i = 0; i < splitArray.length; i++) {
                                                MediaData data = new MediaData();
                                                data.resourceurl = splitArray[i];
                                                mediaDataList.add(data);
                                            }
                                        } else {
                                            MediaData mediaData = new MediaData();
                                            mediaData.resourceurl = studentCommitResUrl;
                                            mediaDataList.add(mediaData);
                                        }
                                    }
                                    exerciseItem.setDatas(mediaDataList);
                                }
                            }
                            exerciseItemList = new ArrayList<>();
                            exerciseItemList.add(exerciseItem);
                            currentPosition = 0;
                            lookSingleDetail = true;
                        }
                    }
                }
            } else {
                exerciseItemList = cardParam.getQuestionDetails();
            }

            if (lookSingleDetail) {
                fromAnswerAnalysis = false;
            }
        }
    }

    private void initViews() {
        ImageView leftBackImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (leftBackImageView != null) {
            leftBackImageView.setOnClickListener((v) -> finish());
        }
        TextView titleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (titleTextV != null) {
            titleTextV.setText(getString(R.string.str_answer_parsing));
        }
        TextView rightTextV = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (rightTextV != null && playbackParam != null && isOnlineMode) {
            rightTextV.setText(getString(R.string.read_over));
            if (playbackParam.isAssistanceModel) {
                TaskMarkParam taskMarkParam = playbackParam.taskMarkParam;
                if (exerciseItemList != null && exerciseItemList.size() > 0) {
                    ExerciseItem item = exerciseItemList.get(0);
                    if (item != null && !TextUtils.isEmpty(item.getType()) && item.getDatas() != null && item.getDatas().size() > 0) {
                        if (Integer.valueOf(item.getType()) == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                            //主观题
                            if (taskMarkParam != null && taskMarkParam.roleType == RoleType.ROLE_TYPE_EDITOR) {
                                rightTextV.setVisibility(View.VISIBLE);
                                rightTextV.setOnClickListener(v -> {
                                    showMarkWayDialog();
                                });
                            }
                        }
                    }
                }
            }
        }

        ImageView leftBackImageV = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (leftBackImageV != null) {
            leftBackImageV.setOnClickListener(this);
        }
        lastQuestionTextV = (ImageView) findViewById(R.id.tv_last_question);
        if (lastQuestionTextV != null) {
            lastQuestionTextV.setOnClickListener(this);
        }
        nextQestionTextV = (ImageView) findViewById(R.id.tv_next_question);
        if (nextQestionTextV != null) {
            nextQestionTextV.setOnClickListener(this);
        }
        applyMarkLayout = (FrameLayout) findViewById(R.id.ll_apply_mark);
        if (cardParam != null) {
            CommitTask commitTask = cardParam.getCommitTask();
            if (TextUtils.equals(DemoApplication.getInstance().getMemberId(),
                    cardParam.getStudentId()) && commitTask != null && commitTask.isHasTutorialPermission()) {
                applyMarkLayout.setVisibility(View.VISIBLE);
            }
        }
        applyMarkTextV = (TextView) findViewById(R.id.tv_apply_mark);
        applyMarkTextV.setOnClickListener(v -> {
            List<Integer> pageAreaIndex = getPageAreaIndex();
            if (pageAreaIndex == null || pageAreaIndex.size() == 0) {
                return;
            }
            cardParam.setPageIndex(pageAreaIndex.get(0));
            int exerciseIndex = currentPosition + 1;
            cardParam.setExerciseIndex(exerciseIndex);
            if (cardParam.getMarkModel() != null) {
                cardParam.getMarkModel().setT_EQId(String.valueOf(exerciseIndex));
            }
            cardParam.setExerciseListString(exerciseString);
            cardParam.setPageListString(pageListString);
            ApplyMarkHelper.loadCourseImageList(AnswerParsingActivity.this, cardParam,
                    pageAreaIndex);
        });
        viewPager = (MyViewPager) findViewById(R.id.vp_answer_parsing);
        if (lookSingleDetail) {
            lastQuestionTextV.setVisibility(View.GONE);
            nextQestionTextV.setVisibility(View.GONE);
            applyMarkLayout.setVisibility(View.GONE);
        }
    }

    private List<Integer> getPageAreaIndex() {
        List<Integer> pageAreaIndex = new ArrayList<>();
        List<ExerciseItem> items = cardParam.getQuestionDetails();
        if (items != null && items.size() > 0) {
            ExerciseItem item = items.get(currentPosition);
            if (item != null) {
                List<ExerciseItemArea> itemAreas = item.getAreaItemList();
                if (itemAreas != null && itemAreas.size() > 0) {
                    for (int i = 0; i < itemAreas.size(); i++) {
                        ExerciseItemArea area = itemAreas.get(i);
                        if (area != null) {
                            String pageIndex = area.getPage_index();
                            if (!TextUtils.isEmpty(pageIndex)) {
                                int index = Integer.valueOf(pageIndex);
                                if (pageAreaIndex.size() == 0) {
                                    pageAreaIndex.add(index);
                                } else {
                                    boolean flag = true;
                                    for (int m = 0; m < pageAreaIndex.size(); m++) {
                                        if (pageAreaIndex.get(m) == index) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        pageAreaIndex.add(index);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (pageAreaIndex.size() > 1) {
            Collections.sort(pageAreaIndex, (o1, o2) -> o1 - o2);
        }
        return pageAreaIndex;
    }

    private void initData() {
        fragments = new ArrayList<>();
        if (cardParam != null) {
            List<ExerciseItem> exerciseItems = cardParam.getQuestionDetails();
            if (exerciseItems != null && exerciseItems.size() > 0) {
                for (int i = 0; i < exerciseItems.size(); i++) {
                    AnswerParsingFragment fragment = new AnswerParsingFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.SINGLE_QUESTION_ANSWER, exerciseItems.get(i));
                    //是不是来自答题解析
                    args.putBoolean(Constants.FROM_ANSWER_ANALYSIS, fromAnswerAnalysis);
                    args.putString(Constants.STUDENT_ID, cardParam.getStudentId());
                    args.putString(Constants.STUDENT_NAME, cardParam.getStudentName());
                    fragment.setArguments(args);
                    fragments.add(fragment);
                }
            }
        } else if (exerciseItemList != null && exerciseItemList.size() > 0) {
            AnswerParsingFragment fragment = new AnswerParsingFragment();
            Bundle args = new Bundle();
            args.putSerializable(Constants.SINGLE_QUESTION_ANSWER, exerciseItemList.get(0));
            //是不是来自答题解析
            args.putBoolean(Constants.FROM_ANSWER_ANALYSIS, fromAnswerAnalysis);
            if (taskEntity != null){
                args.putString(Constants.STUDENT_ID, taskEntity.getStuMemberId());
                if (TextUtils.isEmpty(taskEntity.getStuRealName())) {
                    args.putString(Constants.STUDENT_NAME, taskEntity.getStuNickName());
                } else {
                    args.putString(Constants.STUDENT_NAME, taskEntity.getStuRealName());
                }
            }
            fragment.setArguments(args);
            fragments.add(fragment);
        }
    }

    private void initViewPagerAdapter() {
        pagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCanScroll(true);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                changeBottomEnable(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(currentPosition);
        if (lookSingleDetail) {
            viewPager.setCanScroll(false);
        }
        changeBottomEnable(currentPosition);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_last_question) {
            //上一题
            lastQuestion();
        } else if (viewId == R.id.tv_next_question) {
            //下一题
            nextQuestion();
        } else if (viewId == R.id.contacts_header_left_btn) {
            finishActivity();
        }
    }

    private void finishActivity() {
        if (exerciseItemList != null && exerciseItemList.size() > 0) {
            ExerciseItem item = exerciseItemList.get(currentPosition);
            if (item != null) {
                List<ExerciseItemArea> itemAreas = item.getAreaItemList();
                if (itemAreas != null && itemAreas.size() > 0) {
                    String pageIndex = itemAreas.get(0).getPage_index();
                    if (!TextUtils.isEmpty(pageIndex)) {
                        Intent intent = new Intent();
                        intent.putExtra("pageIndex", Integer.valueOf(pageIndex));
                        setResult(Activity.RESULT_OK, intent);
                    }
                }
            }
        }
        finish();
    }

    /**
     * 上一题
     */
    private void lastQuestion() {
        if (currentPosition == 0) {
            return;
        }
        currentPosition = currentPosition - 1;
        viewPager.setCurrentItem(currentPosition);
        changeBottomEnable(currentPosition);
    }

    /**
     * 下一题
     */
    private void nextQuestion() {
        if (currentPosition == fragments.size() - 1) {
            return;
        }
        currentPosition = currentPosition + 1;
        viewPager.setCurrentItem(currentPosition);
    }

    private void changeBottomEnable(int position) {
        if (exerciseItemList != null && exerciseItemList.size() > 0) {
            if (exerciseItemList.size() == 1) {
                lastQuestionTextV.setBackgroundResource(R.drawable.icon_gray_circle_left);
                nextQestionTextV.setBackgroundResource(R.drawable.icon_gray_circle_right);
                lastQuestionTextV.setEnabled(false);
                nextQestionTextV.setEnabled(false);
            } else {
                if (position == 0) {
                    lastQuestionTextV.setBackgroundResource(R.drawable.icon_gray_circle_left);
                    nextQestionTextV.setBackgroundResource(R.drawable.icon_green_circle_right);
                } else if (position == fragments.size() - 1) {
                    lastQuestionTextV.setBackgroundResource(R.drawable.icon_green_circle_left);
                    nextQestionTextV.setBackgroundResource(R.drawable.icon_gray_circle_right);
                } else {
                    lastQuestionTextV.setBackgroundResource(R.drawable.icon_green_circle_left);
                    nextQestionTextV.setBackgroundResource(R.drawable.icon_green_circle_right);
                }
            }
        }
    }

    private void showMarkWayDialog() {
        AssistantCheckMarkWayDialog markWayDialog =
                new AssistantCheckMarkWayDialog(AnswerParsingActivity.this, result -> {
                    if (result != null) {
                        int markWay = (int) result;
                        if (markWay == 0) {
                            //点读
                            switchMarkModel(true);
                        } else {
                            //录音
                            switchMarkModel(false);
                        }
                    }
                });
        markWayDialog.show();
    }

    private void switchMarkModel(boolean isReading) {
        int doType = DoCourseHelper.FromType.Do_Retell_Course;
        if (isReading) {
            doType = DoCourseHelper.FromType.DO_SLIDE_COURSE_TASK;
        }
        List<MediaData> list = exerciseItemList.get(0).getDatas();
        if (list != null && list.size() > 0) {
            List<String> imageList = new ArrayList<>();
            for (int m = 0; m < list.size(); m++) {
                imageList.add(list.get(m).resourceurl);
            }
            String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                    .generate(list.get(0).resourceurl);
            DoCourseHelper doCourseHelper = new DoCourseHelper(AnswerParsingActivity.this);
            doCourseHelper.setTeacherMark(true);
            doCourseHelper.doAnswerQuestionCheckMarkData(
                    null,
                    savePath,
                    imageList,
                    title,
                    screenType,
                    doType);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (playbackParam != null
                && playbackParam.isAssistanceModel
                && (requestCode == ResourceBaseFragment.REQUEST_CODE_RETELLCOURSE || requestCode == 105 || requestCode == REQUEST_CODE_DO_SLIDE_TOAST)) {
            //接口返回的路径
            String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
            String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
            ApplyMarkHelper helper = new ApplyMarkHelper();
            helper.uploadCourse(this, slidePath, coursePath,
                    Integer.valueOf(playbackParam.taskMarkParam.commitTaskId), false);
        }
    }
}
