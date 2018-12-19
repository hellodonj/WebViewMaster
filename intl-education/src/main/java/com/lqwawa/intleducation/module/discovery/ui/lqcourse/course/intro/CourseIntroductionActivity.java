package com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.intro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;

/**
 * @author medici
 * @desc 课程简介的Activity
 */
public class CourseIntroductionActivity extends ToolbarActivity{

    private TopBar mTopBar;
    private CourseDetailItemParams mDetailItemParams;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_course_introduction;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mDetailItemParams = (CourseDetailItemParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mDetailItemParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.label_new_course_introduction);

        Bundle bundle = new Bundle();
        bundle.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT,mDetailItemParams);

        CourseDetailsItemFragment fragment = new CourseDetailsItemFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout,fragment).commit();
    }



    /**
     * 进入课程简介
     * @param context 上下文对象
     * @param params 课程简介需要的参数
     */
    public static void show(@NonNull Context context,@NonNull CourseDetailItemParams params){
        Intent intent = new Intent(context,CourseIntroductionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
