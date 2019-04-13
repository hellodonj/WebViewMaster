package com.lqwawa.intleducation.module.watchcourse;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectFragment;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.ArrayList;

/**
 * @author MrMedici
 * @desc 选取学程课程课件
 */
public class WatchCourseResourceActivity extends PresenterActivity<WatchCourseResourceContract.Presenter>
    implements WatchCourseResourceContract.View{
    // 课程编号 Bundle Key
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    // 任务类型 Bundle Key
    private static final String KEY_EXTRA_TASK_TYPE = "KEY_EXTRA_TASK_TYPE";
    // 选择资源个数 Bundle Key
    private static final String KEY_EXTRA_MULTIPLE_CHOICE_COUNT = "KEY_EXTRA_MULTIPLE_CHOICE_COUNT";
    // 需要显示的复述课件，听说课类型集合
    private static final String KEY_EXTRA_FILTER_COLLECTION = "KEY_EXTRA_FILTER_COLLECTION";
    // 是否主动选择关联学程
    private static final String KEY_EXTRA_INITIATIVE_TRIGGER = "KEY_EXTRA_INITIATIVE_TRIGGER";
    // 学校ID
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    // ClassId
    private static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";
    // 课程编号
    private String mCourseId;
    // 查看类型
    private int mTaskType;
    // 选择条目个数
    private int mMultipleChoiceCount;
    // 听读课 限制显示的资源类型集合
    private ArrayList<Integer> mFilterArray;
    // 是否主动选择作业库资源
    private boolean initiativeTrigger;

    private String mSchoolId;
    private String mClassId;
    private Bundle mExtras;



    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_watch_course_resource;
    }

    @Override
    protected WatchCourseResourceContract.Presenter initPresenter() {
        return new WatchCourseResourcePresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mCourseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        mTaskType = bundle.getInt(KEY_EXTRA_TASK_TYPE);
        mMultipleChoiceCount = bundle.getInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
        mFilterArray = bundle.getIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION);
        initiativeTrigger = bundle.getBoolean(KEY_EXTRA_INITIATIVE_TRIGGER);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mClassId = bundle.getString(KEY_EXTRA_CLASS_ID);
        mExtras = bundle.getBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK);

        if(mTaskType == WatchResourceType.TYPE_RETELL_COURSE && mFilterArray == null){
            return false;
        }

        if(EmptyUtil.isEmpty(mCourseId)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getCourseDetailWithCourseId(mCourseId);
    }



    @Override
    public void updateLoadedCourseDetailView(@NonNull CourseVo vo) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CourseSelectFragment fragment = new CourseSelectFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("CourseVo",vo);
        arguments.putInt("tasktype",mTaskType);
        arguments.putBoolean(CourseSelectFragment.KEY_EXTRA_ONLINE_RELEVANCE,true);
        arguments.putBoolean(CourseSelectFragment.KEY_EXTRA_INITIATIVE_TRIGGER,initiativeTrigger);
        arguments.putInt(CourseSelectItemFragment.KEY_EXTRA_MULTIPLE_CHOICE_COUNT,mMultipleChoiceCount);
        arguments.putString(CourseSelectFragment.KEY_EXTRA_SCHOOL_ID,mSchoolId);
        arguments.putString(CourseSelectFragment.KEY_EXTRA_CLASS_ID,mClassId);
        arguments.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,mExtras);
        if(mTaskType == WatchResourceType.TYPE_RETELL_COURSE){
            arguments.putIntegerArrayList(CourseSelectFragment.KEY_EXTRA_FILTER_COLLECTION,mFilterArray);
        }
        fragment.setArguments(arguments);
        fragmentManager.beginTransaction()
                .replace(R.id.root_fragment_container,fragment)
                .commit();
    }

    /**
     * 选取学程课程课件的入口 Activity调用入口
     * @param activity 上下文对象
     * @param courseId 课程Id
     * @param taskType 查看类型
     * @param multipleChoiceCount 多选条目个数
     * @param requestCode 请求Code
     * <p>onActivityResult回调选择数据,resultCode = {@link Activity.RESULT_OK}</p>
     * <p>data 为List<SectionResListVo> Key = {@link CourseSelectItemFragment.RESULT_LIST}</p>
     */
    public static void show(@NonNull Activity activity,
                            @NonNull String courseId,
                            @WatchResourceType.WatchResourceRes int taskType,
                            @IntRange(from = 1,to = Integer.MAX_VALUE) int multipleChoiceCount,
                            boolean initiativeTrigger,
                            @Nullable Bundle extras,
                            @Nullable String schoolId,
                            @Nullable String classId,
                            int requestCode){
        Intent intent = new Intent(activity,WatchCourseResourceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putString(KEY_EXTRA_CLASS_ID,classId);
        bundle.putInt(KEY_EXTRA_TASK_TYPE,taskType);
        bundle.putInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT,multipleChoiceCount);
        bundle.putBoolean(KEY_EXTRA_INITIATIVE_TRIGGER,initiativeTrigger);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 选取学程课程课件的入口 Activity调用入口 复述课件类型的调用入口，必须传filterArray对象
     * @param activity 上下文对象
     * @param courseId 课程Id
     * @param taskType 查看类型
     * @param filterArray 复述课件类型显示的过滤类型集合
     * @param multipleChoiceCount 多选条目个数
     * @param requestCode 请求Code
     * <p>onActivityResult回调选择数据,resultCode = {@link Activity.RESULT_OK}</p>
     * <p>data 为List<SectionResListVo> Key = {@link CourseSelectItemFragment.RESULT_LIST}</p>
     */
    public static void show(@NonNull Activity activity,
                            @NonNull String courseId,
                            @WatchResourceType.WatchResourceRes int taskType,
                            @IntRange(from = 1,to = Integer.MAX_VALUE) int multipleChoiceCount,
                            ArrayList<Integer> filterArray,
                            boolean initiativeTrigger,
                            @Nullable Bundle extras,
                            @Nullable String schoolId,
                            @Nullable String classId,
                            int requestCode){
        Intent intent = new Intent(activity,WatchCourseResourceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putString(KEY_EXTRA_CLASS_ID,classId);
        bundle.putInt(KEY_EXTRA_TASK_TYPE,taskType);
        bundle.putInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT,multipleChoiceCount);
        bundle.putIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION,filterArray);
        bundle.putBoolean(KEY_EXTRA_INITIATIVE_TRIGGER,initiativeTrigger);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 选取学程课程课件的入口 Fragment调用入口
     * @param fragment 上下文对象
     * @param courseId 课程Id
     * @param taskType 查看类型
     * @param multipleChoiceCount 多选条目个数
     * @param requestCode 请求Code
     * <p>onActivityResult回调选择数据,resultCode = {@link Activity.RESULT_OK}</p>
     * <p>data 为List<SectionResListVo> Key = {@link CourseSelectItemFragment.RESULT_LIST}</p>
     */
    public static void show(@NonNull Fragment fragment,
                            @NonNull String courseId,
                            @WatchResourceType.WatchResourceRes int taskType,
                            @IntRange(from = 1,to = Integer.MAX_VALUE) int multipleChoiceCount,
                            boolean initiativeTrigger,
                            @Nullable Bundle extras,
                            @Nullable String schoolId,
                            @Nullable String classId,
                            int requestCode){
        Intent intent = new Intent(fragment.getContext(),WatchCourseResourceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putString(KEY_EXTRA_CLASS_ID,classId);
        bundle.putInt(KEY_EXTRA_TASK_TYPE,taskType);
        bundle.putInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT,multipleChoiceCount);
        bundle.putBoolean(KEY_EXTRA_INITIATIVE_TRIGGER,initiativeTrigger);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent,requestCode);
    }

    /**
     * 选取学程课程课件的入口 Fragment调用入口
     * @param fragment 上下文对象
     * @param courseId 课程Id
     * @param taskType 查看类型
     * @param multipleChoiceCount 多选条目个数
     * @param requestCode 请求Code
     * <p>onActivityResult回调选择数据,resultCode = {@link Activity.RESULT_OK}</p>
     * <p>data 为List<SectionResListVo> Key = {@link CourseSelectItemFragment.RESULT_LIST}</p>
     */
    public static void show(@NonNull Fragment fragment,
                            @NonNull String courseId,
                            @WatchResourceType.WatchResourceRes int taskType,
                            @IntRange(from = 1,to = Integer.MAX_VALUE) int multipleChoiceCount,
                            ArrayList<Integer> filterArray,
                            boolean initiativeTrigger,
                            @Nullable Bundle extras,
                            @Nullable String schoolId,
                            @Nullable String classId,
                            int requestCode){
        Intent intent = new Intent(fragment.getContext(),WatchCourseResourceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putString(KEY_EXTRA_CLASS_ID,classId);
        bundle.putInt(KEY_EXTRA_TASK_TYPE,taskType);
        bundle.putInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT,multipleChoiceCount);
        bundle.putIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION,filterArray);
        bundle.putBoolean(KEY_EXTRA_INITIATIVE_TRIGGER,initiativeTrigger);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent,requestCode);
    }
}
