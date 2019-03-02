package com.lqwawa.mooc.modle.tutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.mooc.modle.tutorial.comment.TutorialCommentFragment;
import com.lqwawa.mooc.modle.tutorial.list.TutorialCourseListContract;
import com.lqwawa.mooc.modle.tutorial.list.TutorialCourseListFragment;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页页面
 */
public class TutorialHomePageActivity extends PresenterActivity<TutorialHomePageContract.Presenter>
    implements TutorialHomePageContract.View{

    private TopBar mTopBar;
    private ViewPager mViewPager;

    private TutorialParams mTutorialParams;
    private String mTutorMemberId;


    @Override
    protected TutorialHomePageContract.Presenter initPresenter() {
        return new TutorialHomePagePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_home_page;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mTutorialParams = (TutorialParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mTutorialParams)){
                mTutorMemberId = mTutorialParams.getTutorMemberId();
            }
        }

        if(EmptyUtil.isEmpty(mTutorMemberId)){
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setTitle(R.string.title_personal_page);
        mTopBar.setBack(true);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    protected void initData() {
        super.initData();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TutorialCourseListFragment.newInstance(mTutorialParams));
        fragments.add(TutorialCommentFragment.newInstance(mTutorialParams));

        TutorialPagerAdapter adapter = new TutorialPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);
    }

    /**
     * 申请成为帮辅，注册信息的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull final Context context,@NonNull TutorialParams params){
        Intent intent = new Intent(context, TutorialHomePageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }



    class TutorialPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public TutorialPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
