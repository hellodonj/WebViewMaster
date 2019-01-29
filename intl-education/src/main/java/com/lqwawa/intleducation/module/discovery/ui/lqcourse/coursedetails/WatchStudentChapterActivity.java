package com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.discovery.ui.observable.CourseVoObservable;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.Observable;

/**
 * @author mrmedici
 * @desc 老师查看学生的章节列表
 */
public class WatchStudentChapterActivity extends ToolbarActivity implements CourseDetailsNavigator {
    // 学生进度信息
    private static final String KEY_EXTRA_LEARNING_ENTITY = "KEY_EXTRA_LEARNING_ENTITY";
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";

    private TopBar mTopBar;

    private LearningProgressEntity mLearningEntity;
    private String mTitle;
    private String mStudentId;
    private CourseDetailParams mCourseParams;
    private String mCourseId;

    // 创建课程信息的观察者对象
    private CourseVoObservable courseObservable = new CourseVoObservable();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_watch_student_chapter;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mLearningEntity = (LearningProgressEntity) bundle.getSerializable(KEY_EXTRA_LEARNING_ENTITY);
        mCourseParams = (CourseDetailParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mLearningEntity) || EmptyUtil.isEmpty(mCourseParams)) return false;
        mTitle = mLearningEntity.getUserName();
        mStudentId = mLearningEntity.getUserId();
        mCourseId = bundle.getString(KEY_EXTRA_COURSE_ID);

        if(EmptyUtil.isEmpty(mTitle) ||
                EmptyUtil.isEmpty(mStudentId) ||
                EmptyUtil.isEmpty(mCourseId)){
            return false;
        }
        return super.initArgs(bundle);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mTitle);

        CourseDetailsItemFragment fragment = new CourseDetailsItemFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 2);
        bundle2.putBoolean("needFlagRead", true);
        bundle2.putString("id",mCourseId);
        bundle2.putString("memberId", mStudentId);
        bundle2.putBoolean("teacherVisitor",true);
        // bundle2.putSerializable(CourseVo.class.getSimpleName(), courseVo);
        bundle2.putBoolean(CourseDetailsItemFragment.KEY_EXTRA_ONLINE_TEACHER,false);

        // 传入核心参数
        CourseDetailItemParams params = new CourseDetailItemParams(true,mStudentId,false,mCourseId);
        params.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN);
        params.setCourseParams(mCourseParams);
        bundle2.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT,params);

        fragment.setArguments(bundle2);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_content,fragment)
                .commit();
    }

    @Override
    protected void initData() {
        super.initData();
        CourseHelper.getCourseDetailsData(mStudentId, 1, mCourseId, null, new DataSource.Callback<CourseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseVo courseVo) {
                courseObservable.triggerObservers(courseVo);
            }
        });
    }

    @Override
    public void courseCommentVisible() {

    }

    @Override
    public void otherFragmentVisible() {

    }

    @Override
    public Observable getCourseObservable() {
        return courseObservable;
    }

    @Override
    public void commitComment() {

    }

    @Override
    public void setContent(CommentDialog.CommentData data) {

    }

    @Override
    public void clearContent() {

    }

    /**
     * 老师查看学生章节列表的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull String courseId,
                            @NonNull LearningProgressEntity entity,
                            @NonNull CourseDetailParams params){
        Intent intent = new Intent(context,WatchStudentChapterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putSerializable(KEY_EXTRA_LEARNING_ENTITY,entity);
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putString("memberId",entity.getUserId());
        bundle.putBoolean("canEdit",true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
