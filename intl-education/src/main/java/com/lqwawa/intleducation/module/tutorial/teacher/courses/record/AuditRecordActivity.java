package com.lqwawa.intleducation.module.tutorial.teacher.courses.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.MyGiveInstructionActivity;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.pager.MyGiveInstructionPagerFragment;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅审核记录页面
 */
public class AuditRecordActivity extends ToolbarActivity implements View.OnClickListener{

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";

    private ImageView mBtnBack;
    private CheckedTextView mTvAudition,mTvHistory;
    private ViewPager mViewPager;
    private List<Fragment> mPagerFragments;
    private TabPagerAdapter mPagerAdapter;

    private String mMemberId;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_audit_record;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        if(EmptyUtil.isEmpty(mMemberId)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mTvAudition = (CheckedTextView) findViewById(R.id.tv_audition);
        mTvHistory = (CheckedTextView) findViewById(R.id.tv_history);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mPagerFragments = new ArrayList<>();
        // 添加我开设的课程
        Fragment auditFragment = AuditRecordPageFragment.newInstance(mMemberId,AuditType.AUDITING);
        mPagerFragments.add(auditFragment);
        Fragment rejectFragment = AuditRecordPageFragment.newInstance(mMemberId,AuditType.AUDITED_REJECT);
        mPagerFragments.add(rejectFragment);

        // 添加我开设的自主学习
        MyGiveInstructionPagerFragment giveFragment = MyGiveInstructionPagerFragment.newInstance();
        mPagerFragments.add(giveFragment);

        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),mPagerFragments);
        mViewPager.setAdapter(mPagerAdapter);

        mBtnBack.setOnClickListener(this);
        mTvAudition.setOnClickListener(this);
        mTvHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_back){
            // 返回
            finish();
        }else if(viewId == R.id.tv_audition){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(this);
            mTvAudition.setChecked(true);
            mTvHistory.setChecked(false);
            mViewPager.setCurrentItem(0);
        }else if(viewId == R.id.tv_history){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(this);
            mTvAudition.setChecked(false);
            mTvHistory.setChecked(true);
            mViewPager.setCurrentItem(1);
        }
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * 帮辅审核记录页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,@NonNull String memberId){
        Intent intent = new Intent(context,AuditRecordActivity.class);
        Bundle extras = new Bundle();
        extras.putString(KEY_EXTRA_MEMBER_ID,memberId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
