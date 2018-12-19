package com.lqwawa.intleducation.ui.course.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.learn.ui.NoticesListFragment;

/**
 * @author MrMedici
 * @function 课程详情公告列表的页面
 */
public class CourseNoticeListActivity extends PresenterActivity<CourseNoticeContract.Presenter>
implements CourseNoticeContract.View{

    public static final String KEY_COURSE_ID = "KEY_COURSE_ID";
    public static final String KEY_MEMBER_ID = "KEY_COURSE_ID";
    public static final String KEY_CAN_EDIT = "KEY_CAN_EDIT";
    // 课程Id
    private String courseId;
    // Toolbar
    private TopBar mTopBar;

    @Override
    protected CourseNoticeContract.Presenter initPresenter() {
        return new CourseNoticePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_course_notice_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        courseId = bundle.getString(KEY_COURSE_ID,null);
        if(TextUtils.isEmpty(courseId)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar)findViewById(R.id.top_bar);
        mTopBar.setBack(true);

        NoticesListFragment noticesListFragment = new NoticesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",courseId);
        noticesListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.lay_content,noticesListFragment).commit();
    }

    @Override
    protected void initData() {
        super.initData();
        mTopBar.setTitle(R.string.title_course_notice);
    }

    /**
     * 公告列表页面的入口
     * @param context 上下文对象
     * @param memberId
     * @param canEdit
     */
    public static void start(@NonNull Context context,@NonNull String courseId,
                             @NonNull String memberId,boolean canEdit){
        Intent intent = new Intent(context,CourseNoticeListActivity.class);
        intent.putExtra(KEY_COURSE_ID,courseId);
        intent.putExtra("memberId",memberId);
        intent.putExtra("canEdit",canEdit);
        context.startActivity(intent);

    }
}
