package com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;

/**
 * @author mrmedici
 * @desc 老师查看学生的章节列表
 */
public class WatchStudentChapterActivity extends ToolbarActivity {
    // 学生进度信息
    private static final String KEY_EXTRA_LEARNING_ENTITY = "KEY_EXTRA_LEARNING_ENTITY";

    private TopBar mTopBar;

    private LearningProgressEntity mLearningEntity;
    private String mTitle;
    private String mStudentId;
    private CourseDetailParams mCourseParams;

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

        if(EmptyUtil.isEmpty(mTitle) || EmptyUtil.isEmpty(mStudentId)){
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
    }

    /**
     * 老师查看学生章节列表的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull LearningProgressEntity entity,
                            @NonNull CourseDetailParams params){
        Intent intent = new Intent(context,WatchStudentChapterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_LEARNING_ENTITY,entity);
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
