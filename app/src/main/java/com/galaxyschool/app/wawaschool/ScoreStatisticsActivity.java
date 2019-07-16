package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.AchievementStatisticsFragment;
import com.galaxyschool.app.wawaschool.fragment.AnswerAnalysisFragment;
import com.galaxyschool.app.wawaschool.fragment.WholeClassGradeDetailFragment;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import java.util.ArrayList;
import java.util.List;

/**
 * author：ashin
 * create time：2018/9/13 0013
 * desc: ScoreStatisticsActivity成绩统计界面
 */
public class ScoreStatisticsActivity extends BaseFragmentActivity {

    private TabLayout mTabTl;
    private ViewPager mContentVp;
    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;
    private boolean hasEvalAssessment;
    private int scoreRule;
    private ExerciseAnswerCardParam cardParam;
    private Bundle args;
    private boolean showExerciseTab;
    private ArrayList<CommitTask> mData;
    private int fromType;//0 成绩统计 1 全部成绩
    private int roleType;
    /**
     * @param context
     * @param retellCourseList 复述课件的数据list
     * @param evalCourseList 语音评测的数据list
     * @param classAllMemberCount 班级的总人数
     * @param roleType 角色信息
     * @param scoreRule ScoreRule
     */
    public static void start(Context context,
                             ArrayList<CommitTask> retellCourseList,
                             ArrayList<CommitTask> evalCourseList,
                             int classAllMemberCount,
                             int roleType,
                             int scoreRule,
                             boolean hasEvalAssessment,
                             ExerciseAnswerCardParam cardParam) {
        Intent intent = new Intent(context, ScoreStatisticsActivity.class);
        Bundle args = new Bundle();
        args.putParcelableArrayList(AchievementStatisticsFragment.Constants.RETELL_DATA_LIST,retellCourseList);
        args.putParcelableArrayList(AchievementStatisticsFragment.Constants.EVAL_DATA_LIST, evalCourseList);
        args.putBoolean(AchievementStatisticsFragment.Constants.HAS_EVAL_DATA,hasEvalAssessment);
        args.putInt(ActivityUtils.EXTRA_USER_ROLE_TYPE,roleType);
        args.putInt(AchievementStatisticsFragment.Constants.CLASS_MEMBER_ALL_COUNT,classAllMemberCount);
        args.putInt(AchievementStatisticsFragment.Constants.SCORE_RULE,scoreRule);
        if (cardParam != null) {
            args.putSerializable(ExerciseAnswerCardParam.class.getSimpleName(), cardParam);
        }
        intent.putExtras(args);
        context.startActivity(intent);
    }

    /**
     * 进入全班成绩的界面
     */
    public static void start(Activity activity,
                             Bundle args){
        Intent intent = new Intent(activity,ScoreStatisticsActivity.class);
        if (args != null){
            args.putInt("fromType",1);
            intent.putExtras(args);
        }
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_statistics);
        loadIntent();
        mTabTl = (TabLayout) findViewById(R.id.tab_layout);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);

        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            if (fromType == 0) {
                toolbarTopView.getTitleView().setText(R.string.str_achievement_statistic);
                toolbarTopView.getCommitView().setText(getString(R.string.str_whole_class_grade));
                if (roleType == RoleType.ROLE_TYPE_TEACHER) {
//                    //老师显示查看全部成绩
                    toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
                }
                toolbarTopView.getCommitView().setTextColor(ContextCompat.getColor(this, R.color.text_green));
                toolbarTopView.getCommitView().setOnClickListener(v -> {
                    //查看全班的成绩
                    start(this,args);
                });
            } else {
                //全部成绩
                toolbarTopView.getTitleView().setText(R.string.str_whole_class_grade);
            }
            toolbarTopView.getBackView().setOnClickListener(v -> finish());
        }
        if (fromType == 0){
            initContent();
        } else {
            initWholeGradeContent();
        }
        initTab();
    }

    private void loadIntent(){
        args = getIntent().getExtras();
        if (args != null){
            roleType = args.getInt(ActivityUtils.EXTRA_USER_ROLE_TYPE, -1);
            fromType = args.getInt("fromType");
            hasEvalAssessment = args.getBoolean(AchievementStatisticsFragment.Constants.HAS_EVAL_DATA);
            scoreRule = args.getInt(AchievementStatisticsFragment.Constants.SCORE_RULE);
            mData = args.getParcelableArrayList(AchievementStatisticsFragment.Constants.RETELL_DATA_LIST);
            cardParam = (ExerciseAnswerCardParam) args.getSerializable(ExerciseAnswerCardParam.class.getSimpleName());
            if (cardParam != null){
                if (cardParam.getRoleType() == RoleType.ROLE_TYPE_TEACHER){
                    if (mData != null && mData.size() > 0){
                        showExerciseTab = true;
                    }
                }
            }
        }
    }

    private void initTab() {
        ViewCompat.setElevation(mTabTl, 10);
        mTabTl.setupWithViewPager(mContentVp);
        if (fromType == 0) {
            if ((scoreRule == 0 || !hasEvalAssessment) && !showExerciseTab) {
                mTabTl.setVisibility(View.GONE);
            }
        } else {
            if (scoreRule == 0 || !hasEvalAssessment){
                mTabTl.setVisibility(View.GONE);
            }
        }
    }

    private void initContent() {
        tabIndicators = new ArrayList<>();
        tabFragments = new ArrayList<>();
        if (showExerciseTab){
            //班级概况
            tabIndicators.add(getString(R.string.str_class_overview));
        } else {
            //复述课件
            tabIndicators.add(getString(R.string.retell_course_new));
        }
        if (scoreRule == 0){
            AchievementStatisticsFragment evalFragment = new AchievementStatisticsFragment();
            evalFragment.setArguments(args);
            evalFragment.setEvalAssessment(true);
            tabFragments.add(evalFragment);
        } else {
            AchievementStatisticsFragment retellFragment = new AchievementStatisticsFragment();
            retellFragment.setArguments(args);
            tabFragments.add(retellFragment);
            if (hasEvalAssessment) {
                //任务单的语音评测
                tabIndicators.add(getString(R.string.auto_mark));
                AchievementStatisticsFragment evalFragment = new AchievementStatisticsFragment();
                evalFragment.setArguments(args);
                evalFragment.setEvalAssessment(true);
                tabFragments.add(evalFragment);
            }

            if (showExerciseTab){
                //任务单答题卡---答题分析
                tabIndicators.add(getString(R.string.str_answer_analysis));
                AnswerAnalysisFragment analysisFragment = new AnswerAnalysisFragment();
                analysisFragment.setArguments(args);
                tabFragments.add(analysisFragment);
            }
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(contentAdapter);
    }

    private void initWholeGradeContent(){
        tabIndicators = new ArrayList<>();
        tabFragments = new ArrayList<>();
        //复述课件
        tabIndicators.add(getString(R.string.retell_course_new));
        if (scoreRule == 0){
            WholeClassGradeDetailFragment evalFragment = new WholeClassGradeDetailFragment();
            evalFragment.setArguments(args);
            evalFragment.setEvalAssessment(true);
            tabFragments.add(evalFragment);
        } else {
            WholeClassGradeDetailFragment retellFragment = new WholeClassGradeDetailFragment();
            retellFragment.setArguments(args);
            tabFragments.add(retellFragment);
            if (hasEvalAssessment) {
                //任务单的语音评测
                tabIndicators.add(getString(R.string.auto_mark));
                WholeClassGradeDetailFragment evalFragment = new WholeClassGradeDetailFragment();
                evalFragment.setArguments(args);
                evalFragment.setEvalAssessment(true);
                tabFragments.add(evalFragment);
            }
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(contentAdapter);
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }
    }

}