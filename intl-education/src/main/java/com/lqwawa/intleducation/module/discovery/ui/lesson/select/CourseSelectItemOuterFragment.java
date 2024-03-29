package com.lqwawa.intleducation.module.discovery.ui.lesson.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.TabLayoutUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CourseSelectItemOuterFragment extends MyBaseFragment implements ResourceSelectListener {

    private static final String KEY_EXTRA_CHAPTER_OBJECT = "KEY_EXTRA_CHAPTER_OBJECT";
    private static final String KEY_EXTRA_TASK_TYPE = "KEY_EXTRA_TASK_TYPE";
    private static final String KEY_EXTRA_MULTIPLE_CHOICE_COUNT = "KEY_EXTRA_MULTIPLE_CHOICE_COUNT";
    private static final String KEY_EXTRA_FILTER_COLLECTION = "KEY_EXTRA_FILTER_COLLECTION";
    private static final String KEY_EXTRA_ONLINE_RELEVANCE = "KEY_EXTRA_ONLINE_RELEVANCE";

    private TopBar mTopBar;
    private LinearLayout mContentLayout;
    private FrameLayout mEmptyLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    private ChapterVo mChapterVo;
    private int mTaskType;
    // 可以选择的最大条目
    private int mMultipleChoiceCount;
    private boolean isOnlineRelevance;
    private ArrayList<Integer> mFilterArray;

    private List<Fragment> mFragments;
    private static String[] mTabs = UIUtil.getStringArray(R.array.label_lesson_source_tabs);
    private List<String> mTabLists = new ArrayList<>();

    // 资源类型映射
    private SparseIntArray typeTable = new SparseIntArray();

    private CourseDetailParams mParams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_select_item_outer, container, false);
        mTopBar = (TopBar) view.findViewById(R.id.top_bar);
        mContentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
        mEmptyLayout = (FrameLayout) view.findViewById(R.id.empty_layout);
        mEmptyLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        return view;
    }

    public static Fragment newInstance(@NonNull ChapterVo vo,
                                       int taskType,
                                       int multiChoiceCount,
                                       ArrayList<Integer> filterArray,
                                       boolean isOnlineRelevance,
                                       @Nullable CourseDetailParams params){
        CourseSelectItemOuterFragment fragment = new CourseSelectItemOuterFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_EXTRA_CHAPTER_OBJECT, vo);
        arguments.putInt(KEY_EXTRA_TASK_TYPE, taskType);
        arguments.putInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT, multiChoiceCount);
        arguments.putSerializable(KEY_EXTRA_FILTER_COLLECTION, filterArray);
        arguments.putBoolean(KEY_EXTRA_ONLINE_RELEVANCE, isOnlineRelevance);
        if(EmptyUtil.isNotEmpty(params)){
            arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        }
        fragment.setArguments(arguments);
        return fragment;
    }

    public static Fragment newInstance(@NonNull ChapterVo vo,
                                       int taskType,
                                       int multiChoiceCount,
                                       ArrayList<Integer> filterArray,
                                       boolean isOnlineRelevance) {
        return newInstance(vo,taskType,multiChoiceCount,filterArray,isOnlineRelevance,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        Bundle arguments = getArguments();
        mChapterVo = (ChapterVo) arguments.getSerializable(KEY_EXTRA_CHAPTER_OBJECT);
        mTaskType = arguments.getInt(KEY_EXTRA_TASK_TYPE);
        mMultipleChoiceCount = arguments.getInt(KEY_EXTRA_MULTIPLE_CHOICE_COUNT);
        isOnlineRelevance = arguments.getBoolean(KEY_EXTRA_ONLINE_RELEVANCE);
        mFilterArray = arguments.getIntegerArrayList(KEY_EXTRA_FILTER_COLLECTION);
        mParams = (CourseDetailParams) arguments.getSerializable(FRAGMENT_BUNDLE_OBJECT);

        typeTable.append(1, CourseSelectItemFragment.KEY_WATCH_COURSE);
        typeTable.append(2, CourseSelectItemFragment.KEY_RELL_COURSE);
        typeTable.append(3, CourseSelectItemFragment.KEY_TASK_ORDER);
        typeTable.append(4, CourseSelectItemFragment.KEY_TEXT_BOOK);
        // 讲解课类型
        typeTable.append(5, CourseSelectItemFragment.KEY_LECTURE_COURSE);
        typeTable.append(6, CourseSelectItemFragment.KEY_TEXT_BOOK);

        initView();
    }


    /**
     * 初始化View
     */
    private void initView() {
        mTopBar.setBack(true);
        mTopBar.setTitle(mChapterVo.getName());
        mTopBar.findViewById(R.id.left_function1_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshUtil.getInstance().clear();
                getFragmentManager().popBackStack();
            }
        });
        mTopBar.setRightFunctionText1(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击确定
                ArrayList<SectionResListVo> resultData = (ArrayList<SectionResListVo>) getAllResourceData();

                if (resultData.size() <= 0) {
                    ToastUtil.showToast(activity, getString(R.string.str_select_tips));
                } else {
                    for (SectionResListVo vo : resultData) {
                        vo.setChapterId(vo.getId());
                    }

                    // 学程馆选取资源使用的
                    EventBus.getDefault().post(new EventWrapper(resultData, EventConstant.COURSE_SELECT_RESOURCE_EVENT));
                    //数据回传
                    getActivity().setResult(Activity.RESULT_OK,
                            new Intent().putExtra(CourseSelectItemFragment.RESULT_LIST, resultData));
                    RefreshUtil.getInstance().clear();
                    getActivity().finish();
                }

            }
        });

        // initTabLayout(null);
        String token = UserHelper.getUserId();
        String courseId = mChapterVo.getCourseId();
        String sectionId = mChapterVo.getId();

        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        //exerciseType 不传或者-1 全部
        LessonHelper.requestChapterStudyTask(languageRes, token, null, courseId, sectionId, 1,-1, new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                if (EmptyUtil.isEmpty(sectionDetailsVo)) return;
                initTabLayout(sectionDetailsVo);
            }
        });

    }

    /**
     * 初始化Tab+Fragment
     */
    private void initTabLayout(@NonNull SectionDetailsVo vo) {
        List<Fragment> fragments = new ArrayList<>();
        List<SectionTaskListVo> taskList = vo.getTaskList();
        if (EmptyUtil.isNotEmpty(taskList)) {

            for (int index = 0; index < taskList.size(); index++) {
                SectionTaskListVo listVo = taskList.get(index);
                if (EmptyUtil.isNotEmpty(listVo.getData()) && isShowType(mTaskType, listVo)) {
                    int taskType = listVo.getTaskType();
                    String taskName = listVo.getTaskName();
                    mTabLists.add(taskName);

                    CourseSelectItemFragment fragment = new CourseSelectItemFragment();
                    fragment.setOnResourceSelectListener(this);
                    Bundle arguments = getArguments();
                    Bundle cloneArguments = (Bundle) arguments.clone();
                    cloneArguments.putSerializable("ChapterVo", mChapterVo);
                    cloneArguments.putInt("tasktype", typeTable.get(taskType));
                    cloneArguments.putInt(CourseSelectItemFragment.KEY_EXTRA_MULTIPLE_CHOICE_COUNT, mMultipleChoiceCount);
                    cloneArguments.putIntegerArrayList(CourseSelectItemFragment.KEY_EXTRA_FILTER_COLLECTION, mFilterArray);
                    cloneArguments.putBoolean(CourseSelectItemFragment.KEY_EXTRA_ONLINE_RELEVANCE, isOnlineRelevance);
                    cloneArguments.putInt(CourseSelectItemFragment.KEY_EXTRA_REAL_TASK_TYPE, mTaskType);
                    cloneArguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,mParams);
                    fragment.setArguments(cloneArguments);
                    fragments.add(fragment);
                }
            }
        }

        this.mFragments = fragments;
        if (fragments.size() > 0) {
            mTopBar.findViewById(R.id.right_function1_text).setVisibility(View.VISIBLE);
            mContentLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setEnabled(false);
            mEmptyLayout.setVisibility(View.GONE);
            mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), fragments));
            mTabLayout.setupWithViewPager(mViewPager);
            // 设置Indicator长度
            if (fragments.size() > 1)
                TabLayoutUtil.setIndicatorMargin(UIUtil.getContext(), mTabLayout, 20, 20);
        } else {
            mTopBar.findViewById(R.id.right_function1_text).setVisibility(View.GONE);
            mContentLayout.setVisibility(View.GONE);
            mEmptyLayout.setEnabled(true);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
        boolean isVideoCourse =
                mParams != null && (mParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                        || (mParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && mParams.isVideoCourse()));
        mTabLayout.setVisibility(isVideoCourse ? View.GONE : View.VISIBLE);
    }

    /**
     * 判断是否需要显示
     *
     * @param realTaskType 当前选取类型
     * @param vo           资源数据集合
     * @return boolean true 需要显示
     */
    private boolean isShowType(int realTaskType, SectionTaskListVo vo) {
        int taskType = vo.getTaskType();
        if (realTaskType == CourseSelectItemFragment.KEY_RELL_COURSE) {
            // 选择复述课件
            if (taskType == 2
                    || taskType == 4
                    || taskType == 5) {
                // 听说作业 看课件 讲解课
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_TASK_ORDER) {
            if (taskType == 3
                    || taskType == 4) {
                // 读写作业 看课件
                return true;
            }
        }else if(realTaskType == CourseSelectItemFragment.KEY_TEXT_BOOK){
            // 视频课类型
            if(taskType == 1 || taskType == 6){
                return true;
            }
        } else if(realTaskType == CourseSelectItemFragment.KEY_WATCH_COURSE){
            // 看课本类型
            if (taskType == 1 || taskType == 4 || taskType == 2 || taskType == 5 || taskType == 6) {
                // 看课件 视频课
                // 新增选择讲解课 听说作业 Q配音
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onSelect(@NonNull SectionResListVo vo) {
        // 判断当前选择的有没有超过个数，如果超过个数，提示不能选择
        if (mMultipleChoiceCount == 1) {
            // 单选模式
            // 再清除所有Fragment的数据
            for (Fragment fragment : mFragments) {
                CourseSelectItemFragment _fragment = (CourseSelectItemFragment) fragment;
                CourseResListAdapter adapter = _fragment.getResourceAdapter();
                if (EmptyUtil.isNotEmpty(adapter)) {
                    List<SectionResListVo> dataArray = adapter.getData();
                    for (SectionResListVo _vo : dataArray) {
                        if (_vo.isChecked() && !TextUtils.equals(vo.getId(), _vo.getId())) {
                            _vo.setChecked(false);
                            RefreshUtil.getInstance().removeId(_vo.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            // 多选模式
            List<SectionResListVo> allResourceData = getAllResourceData();
            if (allResourceData.size() < mMultipleChoiceCount || RefreshUtil.getInstance().contains(vo.getId())) {
                // 已经不能选了
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有已经选择的资源
     *
     * @return 集合数据
     */
    private List<SectionResListVo> getAllResourceData() {
        // 获取所有已经选择的资源
        ArrayList<SectionResListVo> resultData = new ArrayList<>();
        if (mFragments == null || mFragments.size() == 0) {
            return resultData;
        }
        for (Fragment fragment : mFragments) {
            if (fragment instanceof CourseSelectItemFragment) {
                CourseSelectItemFragment itemFragment = (CourseSelectItemFragment) fragment;
                CourseResListAdapter resourceAdapter = itemFragment.getResourceAdapter();
                if (EmptyUtil.isEmpty(resourceAdapter)) continue;
                List<SectionResListVo> selectData = resourceAdapter.getSelectData();
                if (selectData == null) continue;
                resultData.addAll(selectData);
            }
        }
        return resultData;
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
            return mTabLists.get(position);
        }
    }
}
