package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.galaxyschool.app.wawaschool.BroadcastNoteActivity;
import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerTitleAdapter;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.lqwawa.client.pojo.SourceFromType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 * 快乐学习调整页面，支持tab滑动切换，刷新。
 */
public class HappyLearningFragment extends ContactsListFragment {
    public static final String TAG = HappyLearningFragment.class.getSimpleName();

    private MyViewPager myViewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private MyFragmentPagerTitleAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    public static final String SHOULD_HIDDEN_HEADER_VIEW = "should_hidden_header_view";
    public static final String SHOULD_SHOW_HEADER_VIEW = "should_show_header_view";
    public static final String SHOULD_HIDDEN_SEARCH_VIEW = "should_hidden_search_view";
    private LinearLayout tabContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_common_pager_sliding_tab_strip, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        //隐藏返回按钮
        ImageView leftIcon = (ImageView) findViewById(R.id.header_left_icon);
        if (leftIcon != null){
            leftIcon.setVisibility(View.GONE);
        }

        //右侧扫一扫按钮
        ImageView rightIcon = (ImageView) findViewById(R.id.header_right_icon);
        if (rightIcon != null){
            rightIcon.setVisibility(View.VISIBLE);
            rightIcon.setImageResource(R.drawable.scanning_ico_white);
            rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //扫一扫
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivity(intent);
                }
            });
        }
        initViewPager();
    }

    private void initViewPager() {
        myViewPager = (MyViewPager) findViewById(R.id.my_view_pager);
        //设置是否可以滑动
        myViewPager.setCanScroll(true);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
        //设置头部的布局为wrap_content
        if (pagerSlidingTabStrip != null){
            pagerSlidingTabStrip.setShouldExpand(false);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    pagerSlidingTabStrip.getLayoutParams();
            layoutParams.width = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.MarginLayoutParams.MATCH_PARENT;
            pagerSlidingTabStrip.setLayoutParams(layoutParams);
        }
        //初始化标题
        titleList.add(getString(R.string.campus_now_direct));//校园电视台
        titleList.add(getString(R.string.hint_show_book));//创意秀
        titleList.add(getString(R.string.broadcast_hall));//播报厅
//        titleList.add(getString(R.string.noc_competition));//NOC大赛

        //初始化fragment
        Fragment fragment = null;

        //校园电视台
        fragment = new BroaccastNoteFragment();
        Bundle bundle = getCommonBundle();
        bundle.putBoolean(BroadcastNoteActivity.IS_FROM_CAMPUS_ONLINE,true);
        fragment.setArguments(bundle);
        fragmentList.add(fragment);

        //创意秀
        fragment = new CreativeShowFragment();
        Bundle args = getCommonBundle();
        bundle.putBoolean(SHOULD_SHOW_HEADER_VIEW,true);
        fragment.setArguments(args);
        fragmentList.add(fragment);

        //播报厅
        fragment = new BroaccastNoteFragment();
        fragment.setArguments(getCommonBundle());
        fragmentList.add(fragment);

      /*  //NOC大赛
        fragment = new NocWorksFragment();
        fragment.setArguments(getCommonBundle());
        fragmentList.add(fragment);*/

        //适配器
        adapter = new MyFragmentPagerTitleAdapter(getChildFragmentManager(),titleList,fragmentList);
        myViewPager.setAdapter(adapter);
        myViewPager.setOffscreenPageLimit(titleList.size());
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                controlTabByPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        int index = 0;
        myViewPager.setCurrentItem(index);
        //和viewpager保持联动
        pagerSlidingTabStrip.setViewPager(myViewPager);
        tabContainer = Utils.getPagerSlidingTabStripTabContainer(pagerSlidingTabStrip);
        //初始化
        controlTabByPosition(index);
    }

    private void controlTabByPosition(int position) {
        if (position == 1){
            //创意秀
            UIUtils.currentSourceFromType = SourceFromType.CREATIVE_SHOW;
        } else {
            //其他
            UIUtils.currentSourceFromType = SourceFromType.OTHER;
        }

        if (tabContainer != null){
            int count = tabContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View itemView = tabContainer.getChildAt(i);
                if (itemView != null){
                    if (i == position){
                        itemView.setAlpha(1.0f);
                    }else {
                        itemView.setAlpha(0.6f);
                    }
                }
            }
        }
    }

    Bundle getCommonBundle(){
        Bundle args = new Bundle();
        //隐藏头布局
        args.putBoolean(SHOULD_HIDDEN_HEADER_VIEW,true);
        return args;
    }
}
