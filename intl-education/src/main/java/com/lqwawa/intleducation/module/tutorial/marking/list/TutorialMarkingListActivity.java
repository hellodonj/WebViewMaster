package com.lqwawa.intleducation.module.tutorial.marking.list;

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
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.module.tutorial.marking.list.pager.TutorialMarkingPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅批阅列表页面
 */
public class TutorialMarkingListActivity extends PresenterActivity<TutorialMarkingListContract.Presenter>
    implements TutorialMarkingListContract.View,View.OnClickListener {

    private ImageView mBtnBack;
    private CheckedTextView mUnMark,mHaveMark;
    private ViewPager mViewPager;
    private List<Fragment> mPagerFragments;
    private TabPagerAdapter mPagerAdapter;

    private TutorialMarkingParams mMarkingParams;

    @Override
    protected TutorialMarkingListContract.Presenter initPresenter() {
        return new TutorialMarkingListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_marking_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mMarkingParams = (TutorialMarkingParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        }

        if(EmptyUtil.isEmpty(mMarkingParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mUnMark = (CheckedTextView) findViewById(R.id.tv_un_mark);
        mHaveMark = (CheckedTextView) findViewById(R.id.tv_have_mark);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mPagerFragments = new ArrayList<>();
        // 未批阅
        TutorialMarkingParams notParams = mMarkingParams.clone();
        notParams.setMarkType(MarkingStateType.MARKING_STATE_NOT);
        Fragment notFragment = TutorialMarkingPagerFragment.newInstance(notParams);
        // 已批阅
        TutorialMarkingParams haveParams = mMarkingParams.clone();
        haveParams.setMarkType(MarkingStateType.MARKING_STATE_HAVE);
        Fragment haveFragment = TutorialMarkingPagerFragment.newInstance(haveParams);

        mPagerFragments.add(notFragment);
        mPagerFragments.add(haveFragment);

        mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),mPagerFragments);
        mViewPager.setAdapter(mPagerAdapter);

        mBtnBack.setOnClickListener(this);
        mUnMark.setOnClickListener(this);
        mHaveMark.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_back){
            // 返回
            finish();
        }else if(viewId == R.id.tv_un_mark){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(this);
            mUnMark.setChecked(true);
            mHaveMark.setChecked(false);
            mViewPager.setCurrentItem(0);
        }else if(viewId == R.id.tv_have_mark){
            // 切换Tab,隐藏对话框
            KeyboardUtil.hideSoftInput(this);
            mUnMark.setChecked(false);
            mHaveMark.setChecked(true);
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
     * 帮辅批阅列表页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull final Context context,@NonNull TutorialMarkingParams params){
        Intent intent = new Intent(context,TutorialMarkingListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
