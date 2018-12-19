package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;

import java.util.ArrayList;

import static com.lqwawa.intleducation.module.discovery.ui.ClassroomFragment.Constants.EXTRA_SHOWTOPBAR;



/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 课程列表
 */
public class LQCourseCourseListActivity extends MyBaseFragmentActivity{
    public static int RC_SelectCourseRes = 1206;
    private String mSort;
    private ClassroomFragment mClassroomFragment;

    public static void start(Activity activity, ArrayList<ClassifyVo> vos,
                             int position, String levelName, String sort) {
        if(ButtonUtils.isFastDoubleClick()){
            return;
        }
        activity.startActivity(new Intent(activity, LQCourseCourseListActivity.class)
                .putExtra("classifyList", vos)
                .putExtra("LevelName", levelName)
                .putExtra("position", position)
                .putExtra("Sort", sort));
    }
    public static void start(Activity activity, ArrayList<ClassifyVo> vos,
                             int position, String levelName, String sort,boolean hideSort) {
        if(ButtonUtils.isFastDoubleClick()){
            return;
        }
        activity.startActivity(new Intent(activity, LQCourseCourseListActivity.class)
                .putExtra("classifyList", vos)
                .putExtra("LevelName", levelName)
                .putExtra("position", position)
                .putExtra("hideSort", hideSort)
                .putExtra("Sort", sort));
    }

    public static void startForSelRes(Activity activity,int tasktype) {
        if(ButtonUtils.isFastDoubleClick()){
            return;
        }
        activity.startActivityForResult(new Intent(activity, LQCourseCourseListActivity.class)
                .putExtra("Sort", "-1")
                .putExtra("tasktype",tasktype)
                .putExtra("isForSelRes", true), RC_SelectCourseRes);
    }

    public static void startFromSearch(Activity activity, ArrayList<ClassifyVo> vos, String keyString,
                                       String levelName, int position, String sort,boolean hideSort) {
        if(ButtonUtils.isFastDoubleClick()){
            return;
        }
        activity.startActivity(new Intent(activity, LQCourseCourseListActivity.class)
                .putExtra("classifyList", vos)
                .putExtra("CourseName", keyString)
                .putExtra("LevelName", levelName)
                .putExtra("position", position)
                .putExtra("hideSort", hideSort)
                .putExtra("Sort", sort));
    }

    private LQCourseCourseFragment lqCourseCourseFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_fragment);
        mSort = getIntent().getStringExtra("Sort");
        if (TextUtils.equals(mSort, "3")) {

            mClassroomFragment = new ClassroomFragment();//直播
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_SHOWTOPBAR,true);
            mClassroomFragment.setArguments(bundle);

        } else {


        lqCourseCourseFragment = new LQCourseCourseFragment();
        lqCourseCourseFragment.setOnCourseListener(new LQCourseCourseListFragment.OnCourseSelListener() {
            @Override
            public void onCourseSel(CourseVo vo) {
                if(vo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CourseVo",vo);
                    bundle.putInt("tasktype",getIntent().getIntExtra("tasktype",1));
                    CourseSelectFragment courseSelectFragment = new CourseSelectFragment();
                    courseSelectFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(lqCourseCourseFragment);
                    fragmentTransaction.add(R.id.root_fragment_container, courseSelectFragment);
                    fragmentTransaction.show(courseSelectFragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }

            @Override
            public void onSearch() {
                if(searchFragment == null) {
                    searchFragment = new SearchFragment();
                    searchFragment.setOnSearchStatusChangeListener(onSearchStatusChangeListener);
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.root_fragment_container, searchFragment);
                    fragmentTransaction.show(searchFragment);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }else{
                    FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(lqCourseCourseFragment);
                    fragmentTransaction.show(searchFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        });
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (TextUtils.equals(mSort, "3")) {

            fragmentTransaction.add(R.id.root_fragment_container, mClassroomFragment);
            fragmentTransaction.show(mClassroomFragment);

        } else {
            fragmentTransaction.add(R.id.root_fragment_container, lqCourseCourseFragment);
            fragmentTransaction.show(lqCourseCourseFragment);
        }

        fragmentTransaction.commit();
    }

    private SearchFragment.OnSearchStatusChangeListener onSearchStatusChangeListener
            = new SearchFragment.OnSearchStatusChangeListener() {
        @Override
        public void onSearch(String keyWord) {
            hideKeyboard();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.show(lqCourseCourseFragment);
            fragmentTransaction.commitAllowingStateLoss();
            lqCourseCourseFragment.updateForSearch(keyWord);

        }

        @Override
        public void onCancel() {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.show(lqCourseCourseFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    };

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
           if(requestCode == LiveDetails.MOOC_LIVE){//直播
                if(mClassroomFragment != null){
                    mClassroomFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

}
