package com.lqwawa.intleducation.module.discovery.lessontask.lessonroot;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.lessontask.committedtask.CommittedTaskFragment;
import com.lqwawa.intleducation.module.discovery.lessontask.missionrequire.MissionRequireFragment;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 显示提交列表与任务要求
 * @date 2018/4/12 0012 下午 5:01
 * @history v1.0
 * **********************************
 */
public class LessonDetailRootFragment extends IBaseFragment {

    private static final String KEY_EXTRA_COMMITTED_TASK_VO = "KEY_EXTRA_COMMITTED_TASK_VO";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    // Tab 标题
    private String[] mTabTexts;
    // 任务数据
    private LqTaskCommitListVo mCommittedTask;


    public static Fragment getInstance(LqTaskCommitListVo committedTaskVo){
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_COMMITTED_TASK_VO,committedTaskVo);
        Fragment fragment = new MissionRequireFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lesson_detail_root;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCommittedTask = (LqTaskCommitListVo) bundle.getSerializable(KEY_EXTRA_COMMITTED_TASK_VO);
        mTabTexts = UIUtil.getStringArray(R.array.label_lesson_task_tab_array);
        if(EmptyUtil.isEmpty(mCommittedTask)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_paper);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager(),mCommittedTask));
    }

    /**
     * ViewPager显示的Adapter
     */
    public class PagerAdapter extends FragmentPagerAdapter{

        private LqTaskCommitListVo mCommittedTaskVo;

        public PagerAdapter(FragmentManager fm,LqTaskCommitListVo committedTaskVo) {
            super(fm);
            this.mCommittedTaskVo = committedTaskVo;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if(position == 0){
                fragment = MissionRequireFragment.getInstance(mCommittedTaskVo.getTaskInfo());
            }else if(position == 1){
                fragment = CommittedTaskFragment.getInstance(mCommittedTaskVo.getListCommitTaskOnline());
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabTexts.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            super.destroyItem(container, position, object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTexts[position];
        }
    }
}
