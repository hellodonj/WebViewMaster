package com.lqwawa.intleducation.base.widgets.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment;

/**
 * @author mrmedici
 * @desc 统一Fragment容器的入口
 */
public class CommonContainerActivity extends ToolbarActivity {

    public static final String KEY_EXTRA_TITLE_BOOLEAN = "KEY_EXTRA_TITLE_BOOLEAN";
    public static final String KEY_EXTRA_TITLE_TEXT = "KEY_EXTRA_TITLE_TEXT";
    public static final String KEY_EXTRA_CLASS_NAME = "KEY_EXTRA_CLASS_NAME";

    public interface CourseFragmentConstance{
        String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
        String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
        String KEY_EXTRA_BOOLEAN_TEACHER = "KEY_EXTRA_BOOLEAN_TEACHER";
    }

    public interface OnlineLiveFragmentConstance{
        String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_CURRENT_MEMBER_ID";
        String KEY_EXTRA_HIDE_SEARCH = "KEY_EXTRA_HIDE_SEARCH";
    }

    private TopBar mTopBar;
    private boolean mShowTitle;
    private String mClassName;
    private String mConfigValue;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_common_container;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mShowTitle = bundle.getBoolean(KEY_EXTRA_TITLE_BOOLEAN);
        mConfigValue = bundle.getString(KEY_EXTRA_TITLE_TEXT);
        mClassName = bundle.getString(KEY_EXTRA_CLASS_NAME);
        if(mShowTitle && EmptyUtil.isEmpty(mConfigValue) ||
                EmptyUtil.isEmpty(mClassName)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        if(mShowTitle){
            mTopBar.setBack(true);
            mTopBar.setTitle(mConfigValue);
            mTopBar.setVisibility(View.VISIBLE);
        }else{
            mTopBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(MyCourseListFragment.class.getName().equals(mClassName)){
            String memberId = getIntent().getStringExtra(CourseFragmentConstance.KEY_EXTRA_MEMBER_ID);
            String schoolId = getIntent().getStringExtra(CourseFragmentConstance.KEY_EXTRA_SCHOOL_ID);
            boolean isTeacher = getIntent().getBooleanExtra(CourseFragmentConstance.KEY_EXTRA_BOOLEAN_TEACHER,false);

            Fragment fragment = MyCourseListFragment.newInstance(schoolId,memberId,isTeacher);
            fragmentTransaction.replace(R.id.lay_content,fragment);
        }else if(MyOnlinePagerFragment.class.getName().equals(mClassName)){
            String memberId = getIntent().getStringExtra(OnlineLiveFragmentConstance.KEY_EXTRA_MEMBER_ID);
            boolean isHide = getIntent().getBooleanExtra(OnlineLiveFragmentConstance.KEY_EXTRA_HIDE_SEARCH,false);
            Fragment fragment = MyOnlinePagerFragment.newInstance(memberId,isHide);
            fragmentTransaction.replace(R.id.lay_content,fragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 统一Fragment容器的入口
     * @param context 上下文对象
     * @param showTitle 是否显示标题
     * @param configValue 标题文本
     * @param className 要显示的Fragment
     * @param bundle bundle对象
     */
    public static void show(@NonNull Context context,
                            boolean showTitle,
                            @Nullable String configValue,
                            @NonNull String className,@NonNull Bundle bundle){
        Intent intent = new Intent(context,CommonContainerActivity.class);
        bundle.putBoolean(KEY_EXTRA_TITLE_BOOLEAN,showTitle);
        bundle.putString(KEY_EXTRA_TITLE_TEXT,configValue);
        bundle.putString(KEY_EXTRA_CLASS_NAME,className);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
