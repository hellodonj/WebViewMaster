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
import com.galaxyschool.app.wawaschool.fragment.AnswerParsingFragment;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.helper.ApplyMarkHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItemArea;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private String exerciseString;

    public interface Constants {
        String SINGLE_QUESTION_ANSWER = "single_question_answer";
        String FROM_ANSWER_ANALYSIS = "from_answer_analysis";
        String STUDENT_ID = "student_id";
        String STUDENT_NAME = "student_name";
        String LOOK_SINGLE_QUESTION_DETAIL = "look_single_question_detail";
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
        args.putBoolean(Constants.LOOK_SINGLE_QUESTION_DETAIL,lookSingleDetail);
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
            currentPosition = args.getInt("exerciseIndex");
            exerciseString = args.getString("exerciseListString");
            currentPosition = currentPosition - 1;
            lookSingleDetail = args.getBoolean(Constants.LOOK_SINGLE_QUESTION_DETAIL,false);
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
            if (TextUtils.equals(DemoApplication.getInstance().getMemberId(),
                    cardParam.getStudentId())) {
                applyMarkLayout.setVisibility(View.VISIBLE);
            }
        }
        applyMarkTextV = (TextView) findViewById(R.id.tv_apply_mark);
        applyMarkTextV.setOnClickListener(v -> {
            List<Integer> pageAreaIndex= getPageAreaIndex();
            if (pageAreaIndex == null || pageAreaIndex.size() == 0){
                return;
            }
            cardParam.setPageIndex(pageAreaIndex.get(0));
            int exerciseIndex = currentPosition + 1;
            cardParam.setExerciseIndex(exerciseIndex);
            if (cardParam.getMarkModel() != null) {
                cardParam.getMarkModel().setT_EQId(String.valueOf(exerciseIndex));
            }
            ApplyMarkHelper.loadCourseImageList(AnswerParsingActivity.this,cardParam,
                    pageAreaIndex);
        });
        viewPager = (MyViewPager) findViewById(R.id.vp_answer_parsing);
        if (lookSingleDetail) {
            lastQuestionTextV.setVisibility(View.GONE);
            nextQestionTextV.setVisibility(View.GONE);
            applyMarkLayout.setVisibility(View.GONE);
        }
    }

    private List<Integer> getPageAreaIndex(){
        List<Integer> pageAreaIndex = new ArrayList<>();
        List<ExerciseItem> items = cardParam.getQuestionDetails();
        if (items != null && items.size() > 0){
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
                                if (pageAreaIndex.size() == 0){
                                    pageAreaIndex.add(index);
                                } else {
                                    boolean flag = true;
                                    for (int m = 0; m < pageAreaIndex.size(); m++){
                                        if (pageAreaIndex.get(m) == index){
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag){
                                        pageAreaIndex.add(index);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (pageAreaIndex.size() > 1){
            Collections.sort(pageAreaIndex, (o1,o2) ->  o1-o2);
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
                    args.putString(Constants.STUDENT_ID,cardParam.getStudentId());
                    args.putString(Constants.STUDENT_NAME,cardParam.getStudentName());
                    fragment.setArguments(args);
                    fragments.add(fragment);
                }
            }
        } else if (exerciseItemList != null && exerciseItemList.size() > 0){
            AnswerParsingFragment fragment = new AnswerParsingFragment();
            Bundle args = new Bundle();
            args.putSerializable(Constants.SINGLE_QUESTION_ANSWER, exerciseItemList.get(0));
            //是不是来自答题解析
            args.putBoolean(Constants.FROM_ANSWER_ANALYSIS, fromAnswerAnalysis);
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
        if (lookSingleDetail){
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
            if (exerciseItemList.size() == 1){
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
}
