package com.lqwawa.intleducation.module.discovery.ui.study.filtratelist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager.OnlineStudyFiltratePagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author medici
 * @desc 在线学习筛选的页面
 */
public class OnlineStudyFiltrateActivity extends PresenterActivity<OnlineStudyFiltrateContract.Presenter>
    implements OnlineStudyFiltrateContract.View{

    private static final String KEY_EXTRA_CONFIG_ENTITY = "KEY_EXTRA_CONFIG_ENTITY";
    private static final String KEY_EXTRA_HIDE_TOP = "KEY_EXTRA_HIDE_TOP";
    private static final String KEY_EXTRA_SEARCH_KEY = "KEY_EXTRA_SEARCH_KEY";

    private TopBar mTopBar;
    private TabLayout mTabLayout;
    private ImageView mIvSlide;
    private ViewPager mViewPager;
    private List<Fragment> mPagerFragments;
    private ClassPagerAdapter mPagerAdapter;
    // 点击的分类项
    private OnlineConfigEntity mConfigEntity;
    private List<String> mTitleArray;
    private boolean mHideTop;
    private String mSearchKey;

    @Override
    protected OnlineStudyFiltrateContract.Presenter initPresenter() {
        return new OnlineStudyFiltratePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_online_study_filtrate;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mConfigEntity = (OnlineConfigEntity) bundle.getSerializable(KEY_EXTRA_CONFIG_ENTITY);
        mHideTop = bundle.getBoolean(KEY_EXTRA_HIDE_TOP,false);
        mSearchKey = bundle.getString(KEY_EXTRA_SEARCH_KEY);
        if(EmptyUtil.isEmpty(mConfigEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mConfigEntity.getName());

        // 添加搜索
        if(!mHideTop && false){
            // 不是搜索页面过来的
            mTopBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 筛选页面 mSortType 定死的-1;
                    SearchActivity.show(OnlineStudyFiltrateActivity.this,mConfigEntity, OnlineStudyType.SORT_ONLINE_STUDY_SEARCH,mConfigEntity.getName());
                }
            });
        }

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mIvSlide = (ImageView) findViewById(R.id.iv_slide_tab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mPagerFragments = new ArrayList<>();
        mTitleArray = new ArrayList<>();

        // 添加全部
        OnlineStudyFiltratePagerFragment allFragment = OnlineStudyFiltratePagerFragment.newInstance(mConfigEntity,-1,true,mSearchKey);
        mPagerFragments.add(allFragment);
        mTitleArray.add(UIUtil.getString(R.string.all));
        List<OnlineConfigEntity.OnlineLabelEntity> childList = mConfigEntity.getChildList();
        for (int index = 0;index < childList.size(); index++) {
            OnlineConfigEntity.OnlineLabelEntity entity = childList.get(index);
            OnlineStudyFiltratePagerFragment fragment =
                    OnlineStudyFiltratePagerFragment.newInstance(mConfigEntity,index,false,mSearchKey);
            mTitleArray.add(entity.getName());
            mPagerFragments.add(fragment);
        }
        mPagerAdapter = new ClassPagerAdapter(getSupportFragmentManager(),mPagerFragments);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // 此时确定了TabLayout的数目
        mTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int tabCount = mTabLayout.getTabCount();
                if(!(mTabLayout.getChildCount() > 0)){
                    // 如果TabLayout没有子View
                    mIvSlide.setVisibility(View.GONE);
                    return;
                }

                LinearLayout slidingTabStrip = (LinearLayout) mTabLayout.getChildAt(0);
                if(!(slidingTabStrip.getChildCount() > 0)){
                    // 如果TabLayout 没有子Tab
                    mIvSlide.setVisibility(View.GONE);
                    return;
                }
                int tabWidth = slidingTabStrip.getChildAt(0).getMeasuredWidth();
                int allWidth = tabWidth * tabCount;
                if(allWidth > mTabLayout.getMeasuredWidth()){
                    // 显示滑动
                    mIvSlide.setVisibility(View.VISIBLE);
                    mIvSlide.setOnClickListener(view->{
                        // 滑动到指定位置
                        // mTabLayout.getTabAt(mTabLayout.getTabCount() - 1).select();
                        mTabLayout.smoothScrollTo(allWidth,0);
                    });
                }else{
                    // 不显示滑动
                    mIvSlide.setVisibility(View.GONE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTabLayout.getLayoutParams();
                    params.rightMargin = 0;
                    mTabLayout.setLayoutParams(params);
                }
            }
        });
    }

    private class ClassPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public ClassPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleArray.get(position);
        }
    }

    /**
     * 在线学习二级列表页面
     * @param context 上下文对象
     * @param entity 在线学习分类页面
     * @param hideTop true 说明从搜索页面过来的，关闭搜索
     */
    public static void show(@NonNull Context context, @NonNull OnlineConfigEntity entity,boolean hideTop){
        Intent intent = new Intent(context,OnlineStudyFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CONFIG_ENTITY,entity);
        bundle.putBoolean(KEY_EXTRA_HIDE_TOP,hideTop);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 在线学习二级列表页面 搜索页面过来的
     * @param context 上下文对象
     * @param entity 在线学习分类页面
     * @param hideTop true 说明从搜索页面过来的，关闭搜索
     * @param keyWord 搜索关键字
     */
    public static void show(@NonNull Context context, @NonNull OnlineConfigEntity entity,boolean hideTop,@NonNull String keyWord){
        Intent intent = new Intent(context,OnlineStudyFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CONFIG_ENTITY,entity);
        bundle.putBoolean(KEY_EXTRA_HIDE_TOP,hideTop);
        bundle.putString(KEY_EXTRA_SEARCH_KEY,keyWord);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
