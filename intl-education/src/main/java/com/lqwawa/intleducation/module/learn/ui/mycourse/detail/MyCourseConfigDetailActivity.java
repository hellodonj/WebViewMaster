package com.lqwawa.intleducation.module.learn.ui.mycourse.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment;

/**
 * @author MrMedici
 * @desc 我的习课程标签课程详情列表
 */
public class MyCourseConfigDetailActivity extends ToolbarActivity {


    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_BOOLEAN_TEACHER = "KEY_EXTRA_BOOLEAN_TEACHER";
    private static final String KEY_EXTRA_CONFIG_ENTITY = "KEY_EXTRA_CONFIG_ENTITY";
    private static final String KEY_EXTRA_CONFIG_LEVEL = "KEY_EXTRA_CONFIG_LEVEL";
    private static final String KEY_EXTRA_CONFIG_ONEID = "KEY_EXTRA_CONFIG_ONEID";
    private static final String KEY_EXTRA_CONFIG_TWOID = "KEY_EXTRA_CONFIG_TWOID";

    private TopBar mTopBar;

    private LQCourseConfigEntity mConfigEntity;
    private String mCurSchoolId;
    private String mCurMemberId;
    private boolean isTeacher;
    private String mLevel;
    private int mParamOneId;
    private int mParamTwoId;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_my_course_config_detail;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mConfigEntity = (LQCourseConfigEntity) bundle.getSerializable(KEY_EXTRA_CONFIG_ENTITY);
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mCurSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        isTeacher = bundle.getBoolean(KEY_EXTRA_BOOLEAN_TEACHER);
        mLevel = bundle.getString(KEY_EXTRA_CONFIG_LEVEL);
        mParamOneId = bundle.getInt(KEY_EXTRA_CONFIG_ONEID);
        mParamTwoId = bundle.getInt(KEY_EXTRA_CONFIG_TWOID);

        if (EmptyUtil.isEmpty(mCurMemberId) ||
                EmptyUtil.isEmpty(mConfigEntity) ||
                EmptyUtil.isEmpty(mLevel)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mConfigEntity.getConfigValue());

        MyCourseListPagerFragment fragment = new MyCourseListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MemberId", mCurMemberId);
        bundle.putString("SchoolId", mCurSchoolId);
        bundle.putBoolean(MyCourseListPagerFragment.KEY_IS_TEACHER, isTeacher);
        bundle.putSerializable(MyCourseListPagerFragment.KEY_CONFIG_ENTITY, mConfigEntity);
        bundle.putString(MyCourseListPagerFragment.KEY_EXTRA_CONFIG_LEVEL, mLevel);
        bundle.putInt(MyCourseListPagerFragment.KEY_EXTRA_CONFIG_ONEID, mParamOneId);
        bundle.putInt(MyCourseListPagerFragment.KEY_EXTRA_CONFIG_TWOID, mParamTwoId);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_content,fragment)
                .commit();
    }

    public static void show(@NonNull Context context,
                            @NonNull LQCourseConfigEntity entity,
                            @Nullable String schoolId,
                            @NonNull String memberId,
                            boolean isTeacher,
                            String level,
                            int paramOneId,
                            int paramTwoId) {
        Intent intent = new Intent(context,MyCourseConfigDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putString(KEY_EXTRA_MEMBER_ID, memberId);
        bundle.putBoolean(KEY_EXTRA_BOOLEAN_TEACHER, isTeacher);
        bundle.putSerializable(KEY_EXTRA_CONFIG_ENTITY, entity);
        bundle.putString(KEY_EXTRA_CONFIG_LEVEL, level);
        bundle.putInt(KEY_EXTRA_CONFIG_ONEID, paramOneId);
        bundle.putInt(KEY_EXTRA_CONFIG_TWOID, paramTwoId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
