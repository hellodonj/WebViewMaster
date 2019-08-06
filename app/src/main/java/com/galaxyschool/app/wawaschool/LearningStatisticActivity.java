package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.fragment.LearningStatisticFragment;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import java.util.ArrayList;
import java.util.List;

/**
 * ======================================================
 * Describe:学习统计
 * ======================================================
 */
public class LearningStatisticActivity extends BaseFragmentActivity{

    /**
     * 课程统计
     */
    public static void start(Activity activity,
                             int courseId,
                             String courseName,
                             String classId,
                             Bundle bundle){
        Intent intent = new Intent(activity,LearningStatisticActivity.class);
        Bundle args = bundle;
        if (args == null){
            args = new Bundle();
        }
        args.putInt(Constants.STATISTIC_TYPE,STATISTIC_TYPE.COURSE_TYPE);
        args.putString(Constants.CLASS_ID,classId);
        args.putInt(Constants.COURSE_ID,courseId);
        args.putString(Constants.COURSE_NAME,courseName);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public interface Constants{
        String LEARNING_TYPE = "learning_type";
        String STATISTIC_TYPE = "statistic_type";
        String COURSE_ID = "course_id";
        String CLASS_ID = "classId";
        String COURSE_NAME = "course_name";
    }

    public interface LEARNING_TYPE {
        int NORMAL_STATISTIC = 0;//正常作业
        int TEST_STATISTIC = 1;//测试
        int EXAM_STATISTIC = 2;//考试
        int HOMEWORK_INTRO_RATE = 3;//作业布置率
        int HOMEWORK_MARK_RATE = 4;//作业批阅率
        int HOMEWORK_COMPLETE_RATE = 5;//作业完成率
    }

    public interface STATISTIC_TYPE {
        int LEARNING_TYPE = 0;//学习统计
        int COURSE_TYPE = 1;//课程统计
    }

    private LinearLayout taskDetailLayout;
    private TextView taskDetailView;
    private TabLayout mTabTl;
    private ViewPager mContentVp;
    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;
    private int statisticType;//统计的类型
    private String classId;
    private int courseId;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_statistics);
        loadIntentData();
        initViews();
        initViewPagerContent();
    }

    private void loadIntentData(){
        Bundle args = getIntent().getExtras();
        if (args != null){
            statisticType = args.getInt(Constants.STATISTIC_TYPE);
            classId = args.getString(Constants.CLASS_ID);
            courseId = args.getInt(Constants.COURSE_ID);
            courseName = args.getString(Constants.COURSE_NAME);
        }
    }

    private void initViews(){
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getBackView().setOnClickListener(v -> finish());
            if (statisticType == STATISTIC_TYPE.COURSE_TYPE){
                toolbarTopView.getTitleView().setText(courseName);
            } else {
                toolbarTopView.getTitleView().setText(R.string.str_learning_statistic);
            }
        }
        mTabTl = (TabLayout) findViewById(R.id.tab_layout);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mContentVp.setOffscreenPageLimit(3);
        taskDetailLayout = (LinearLayout) findViewById(R.id.ll_task_detail);
        taskDetailView = (TextView) findViewById(R.id.tv_task_detail);
    }

    private void initViewPagerContent(){
        tabIndicators = new ArrayList<>();
        tabFragments = new ArrayList<>();

        if (statisticType == STATISTIC_TYPE.LEARNING_TYPE){
            //学习统计
            tabIndicators.add(getString(R.string.str_normal_homework_statistic));
            tabIndicators.add(getString(R.string.str_test_statistic));
            tabIndicators.add(getString(R.string.str_exam_statistic));
        } else {
            //课程统计
            tabIndicators.add(getString(R.string.str_homework_intro_rate));
            tabIndicators.add(getString(R.string.str_homework_mark_rate));
            tabIndicators.add(getString(R.string.str_homework_complete_rate));
        }
        tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(statisticType == STATISTIC_TYPE.LEARNING_TYPE ?
                LEARNING_TYPE.NORMAL_STATISTIC : LEARNING_TYPE.HOMEWORK_INTRO_RATE)));
        tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(statisticType == STATISTIC_TYPE.LEARNING_TYPE ?
                LEARNING_TYPE.TEST_STATISTIC : LEARNING_TYPE.HOMEWORK_MARK_RATE)));
        tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(statisticType == STATISTIC_TYPE.LEARNING_TYPE ?
                LEARNING_TYPE.EXAM_STATISTIC : LEARNING_TYPE.HOMEWORK_COMPLETE_RATE)));
        ViewCompat.setElevation(mTabTl, 10);
        mTabTl.setupWithViewPager(mContentVp);
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(contentAdapter);
    }

    private Bundle getBundleInfo(int learningType){
        Bundle args = null;
        if (statisticType == STATISTIC_TYPE.COURSE_TYPE
                && learningType == LEARNING_TYPE.HOMEWORK_INTRO_RATE){
            args = getIntent().getExtras();
        } else {
            args = new Bundle();
        }
        args.putInt(Constants.LEARNING_TYPE,learningType);
        args.putInt(Constants.STATISTIC_TYPE,statisticType);
        args.putInt(Constants.COURSE_ID,courseId);
        args.putString(Constants.CLASS_ID,classId);
        return args;
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
