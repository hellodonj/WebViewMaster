package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.UFormat;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.adapter.ColorSortAdapter;
import com.galaxyschool.app.wawaschool.fragment.LearningStatisticFragment;
import com.galaxyschool.app.wawaschool.helper.StatisticNetHelper;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;
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
     * (修改进入课程统计页面)
     */
    public static void start(Activity activity,
                             int courseId,
                             String courseName,
                             String classId,
                             Bundle bundle){
        Intent intent = new Intent(activity,DataStatisticActivity.class);
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

    /**
     * 老师查看学生学习统计、班级成绩统计
     */
    public static void start(Activity activity,
                             Bundle args,
                             boolean isClassStatistic){
        if (activity == null || args == null){
            return;
        }
        Intent intent = new Intent(activity,LearningStatisticActivity.class);
        if (isClassStatistic){
            args.putInt(Constants.STATISTIC_TYPE, STATISTIC_TYPE.CLASS_STATISTIC_TYPE);
        } else {
            args.putInt(Constants.STATISTIC_TYPE, STATISTIC_TYPE.LEARNING_TYPE);
            args.putInt(Constants.ROLE_TYPE, RoleType.ROLE_TYPE_TEACHER);
        }
        args.putBoolean(Constants.FROM_DATA_STATISTIC,false);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    /**
     * 老师打开布置完成率 指定下标的position
     */
    public static void start(Activity activity,
                             int positionIndex,
                             Bundle args){
        if (activity == null || args == null){
            return;
        }
        Intent intent = new Intent(activity,LearningStatisticActivity.class);
        args.putInt(Constants.STATISTIC_TYPE,STATISTIC_TYPE.COURSE_TYPE);
        args.putInt(Constants.POSITION_INDEX,positionIndex);
        args.putBoolean(Constants.FROM_DATA_STATISTIC,true);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    /**
     * 学习统计
     */
    public static void start(Activity activity,
                             int courseId,
                             String courseName,
                             String classId,
                             int roleType,
                             String studentId){
        Intent intent = new Intent(activity,LearningStatisticActivity.class);
        Bundle args = new Bundle();
        args.putInt(Constants.STATISTIC_TYPE,STATISTIC_TYPE.LEARNING_TYPE);
        args.putString(Constants.CLASS_ID,classId);
        args.putInt(Constants.COURSE_ID,courseId);
        args.putString(Constants.COURSE_NAME,courseName);
        args.putInt(Constants.ROLE_TYPE,roleType);
        args.putString(Constants.STUDENT_ID,studentId);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public interface Constants{
        String LEARNING_TYPE = "learning_type";
        String STATISTIC_TYPE = "statistic_type";
        String COURSE_ID = "course_id";
        String CLASS_ID = "classId";
        String COURSE_NAME = "course_name";
        String ROLE_TYPE = "role_type";
        String STUDENT_ID = "student_id";
        String FROM_DATA_STATISTIC = "from_data_statistic";
        String POSITION_INDEX = "position_index";
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
        int CLASS_STATISTIC_TYPE = 2;//班级统计成绩
    }

    private LinearLayout rootTablayout;
    private LinearLayout taskDetailLayout;
    private TextView completeRateView;
    private TextView alreadyIntroTextV;
    private TextView alreadyCompleteTextV;
    private TabLayout mTabTl;
    private ViewPager mContentVp;
    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;
    private RecyclerView headRecyclerView;
    private int statisticType;//统计的类型
    private String classId;
    private int courseId;
    private String courseName;
    private int roleType = -1;
    private String studentId;
    private boolean fromDataStatistic;//是否来自数据统计
    private int positionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_statistics);
        loadIntentData();
        initViews();
        handleHeadColorView();
        initViewPagerContent();
    }

    private void loadIntentData(){
        Bundle args = getIntent().getExtras();
        if (args != null){
            statisticType = args.getInt(Constants.STATISTIC_TYPE);
            classId = args.getString(Constants.CLASS_ID);
            courseId = args.getInt(Constants.COURSE_ID);
            courseName = args.getString(Constants.COURSE_NAME);
            roleType = args.getInt(Constants.ROLE_TYPE,-1);
            studentId = args.getString(Constants.STUDENT_ID);
            fromDataStatistic = args.getBoolean(Constants.FROM_DATA_STATISTIC,false);
            positionIndex = args.getInt(Constants.POSITION_INDEX,0);
        }
    }

    private void initViews(){
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getBackView().setOnClickListener(v -> finish());
            if (statisticType == STATISTIC_TYPE.COURSE_TYPE){
                if (fromDataStatistic){
                    String title = null;
                    if (positionIndex == 0) {
                        title = getString(R.string.str_homework_intro_rate);
                    } else if (positionIndex == 1){
                        title = getString(R.string.str_homework_mark_rate);
                    } else if (positionIndex == 2){
                        title = getString(R.string.str_homework_complete_rate);
                    }
                    toolbarTopView.getTitleView().setText(title);
                } else {
                    toolbarTopView.getTitleView().setText(courseName);
                }
            } else if (statisticType == STATISTIC_TYPE.LEARNING_TYPE){
                if (isTeacherLook()) {
                    toolbarTopView.getTitleView().setText(R.string.str_learning_statistic);
                } else {
                    toolbarTopView.getTitleView().setText(courseName);
                }
            } else if (statisticType == STATISTIC_TYPE.CLASS_STATISTIC_TYPE){
                //班级成绩统计
                toolbarTopView.getTitleView().setText(getString(R.string.str_class_achieve_statistics));
            }
        }
        rootTablayout = (LinearLayout) findViewById(R.id.ll_root_tab);
        mTabTl = (TabLayout) findViewById(R.id.tab_layout);
        if (fromDataStatistic){
            mTabTl.setVisibility(View.GONE);
        }
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mContentVp.setOffscreenPageLimit(3);
        taskDetailLayout = (LinearLayout) findViewById(R.id.ll_task_detail);
        completeRateView = (TextView) findViewById(R.id.tv_complete_rate);
        alreadyIntroTextV = (TextView) findViewById(R.id.tv_already_intro);
        alreadyCompleteTextV = (TextView) findViewById(R.id.tv_already_complete);
        headRecyclerView = (RecyclerView) findViewById(R.id.recycler_head_view);
        if (isTeacherLook()){
            //修改tab样式
            headRecyclerView.setVisibility(View.VISIBLE);
            rootTablayout.setBackground(null);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rootTablayout.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);
            mTabTl.setBackgroundColor(ContextCompat.getColor(this,R.color.main_bg_color));
        }
        updateHomeWorkRecordView(null);
    }

    public void updateHomeWorkRecordView(StatisticBean bean){
        if (statisticType == STATISTIC_TYPE.LEARNING_TYPE && !TextUtils.isEmpty(studentId)){
            taskDetailLayout.setVisibility(View.VISIBLE);
            String completeRate = "0%";
            int workCount = 0;
            int testCount = 0;
            int examCount = 0;
            int finishCount = 0;
            if (bean != null){
                completeRate = bean.getStudentCompletedRate();
                workCount = bean.getTeacherSetNormalTaskNum();
                testCount = bean.getTeacherSetTestTaskNum();
                examCount = bean.getTeacherSetExamTaskNum();
                finishCount = bean.getStudentCompletedNum();
            }
            completeRateView.setText(completeRate);

            String completeString = getString(R.string.str_task_num,finishCount);
            SpannableString completeSS = new SpannableString(completeString);
            int startIndex = completeString.indexOf(String.valueOf(finishCount));
            int endIndex = startIndex + String.valueOf(finishCount).length();
            completeSS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.text_green)),
                    startIndex, endIndex , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alreadyCompleteTextV.setText(completeSS);

            String introString = getString(R.string.str_intro_detail,workCount,testCount,examCount);
            SpannableString introSS = new SpannableString(introString);
            int index = introString.indexOf(String.valueOf(workCount));
            endIndex = index + String.valueOf(workCount).length();
            introSS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.text_green)),
                    index, endIndex , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = introString.indexOf(String.valueOf(testCount),endIndex);
            endIndex = index + String.valueOf(testCount).length();
            introSS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.text_green)),
                    index, endIndex , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = introString.indexOf(String.valueOf(examCount),endIndex);
            endIndex = index + String.valueOf(examCount).length();
            introSS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.text_green)),
                    index, endIndex , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alreadyIntroTextV.setText(introSS);

        }
    }

    private void initViewPagerContent(){
        tabIndicators = new ArrayList<>();
        tabFragments = new ArrayList<>();

        if (statisticType == STATISTIC_TYPE.LEARNING_TYPE
                || statisticType == STATISTIC_TYPE.CLASS_STATISTIC_TYPE){
            //学习统计
            tabIndicators.add(getString(R.string.str_normal_homework_statistic));
            tabIndicators.add(getString(R.string.str_test_statistic));
            tabIndicators.add(getString(R.string.str_exam_statistic));
        } else if (statisticType == STATISTIC_TYPE.COURSE_TYPE){
            //课程统计
            if (fromDataStatistic){
                 if (positionIndex == 0){
                     tabIndicators.add(getString(R.string.str_homework_intro_rate));
                 } else if (positionIndex == 1){
                     tabIndicators.add(getString(R.string.str_homework_mark_rate));
                 } else if (positionIndex == 2){
                     tabIndicators.add(getString(R.string.str_homework_complete_rate));
                 }
            } else {
                tabIndicators.add(getString(R.string.str_homework_intro_rate));
                tabIndicators.add(getString(R.string.str_homework_mark_rate));
                tabIndicators.add(getString(R.string.str_homework_complete_rate));
            }
        }

        if (fromDataStatistic){
            if (positionIndex == 0){
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.HOMEWORK_INTRO_RATE)));
            } else if (positionIndex == 1){
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.HOMEWORK_MARK_RATE)));
            } else if (positionIndex == 2){
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.HOMEWORK_COMPLETE_RATE)));
            }
        } else {
            if (statisticType == STATISTIC_TYPE.LEARNING_TYPE
                    || statisticType == STATISTIC_TYPE.CLASS_STATISTIC_TYPE){
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.NORMAL_STATISTIC)));
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.TEST_STATISTIC)));
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.EXAM_STATISTIC)));
            } else if (statisticType == STATISTIC_TYPE.COURSE_TYPE){
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.HOMEWORK_INTRO_RATE)));
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.HOMEWORK_MARK_RATE)));
                tabFragments.add(LearningStatisticFragment.newInstance(getBundleInfo(LEARNING_TYPE.HOMEWORK_COMPLETE_RATE)));
            }
        }
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
        }
        if (args == null) {
            args = new Bundle();
        }
        args.putInt(Constants.LEARNING_TYPE,learningType);
        args.putInt(Constants.STATISTIC_TYPE,statisticType);
        args.putInt(Constants.COURSE_ID,courseId);
        args.putString(Constants.CLASS_ID,classId);
        args.putString(Constants.STUDENT_ID,studentId);
        args.putInt(Constants.ROLE_TYPE,roleType);
        return args;
    }

    private void handleHeadColorView(){
        if (isTeacherLook()) {
            List<StatisticBean> list = StatisticNetHelper.getColorValueData(this);
            if (list != null) {
                ColorSortAdapter adapter = new ColorSortAdapter(list);
                headRecyclerView.setLayoutManager(new GridLayoutManager(this, 6));
                headRecyclerView.setAdapter(adapter);
            }
        }
    }

    /**
     * @return 老师查看学生统计
     */
    private boolean isTeacherLook(){
        boolean isTeacherLook = false;
        if (roleType == RoleType.ROLE_TYPE_TEACHER
                && TextUtils.isEmpty(studentId)
                && statisticType == STATISTIC_TYPE.LEARNING_TYPE){
            isTeacherLook = true;
        }
        return isTeacherLook;
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
