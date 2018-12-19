package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import java.util.ArrayList;
import java.util.List;

public class CourseMainFragment extends BaseFragment implements
    View.OnClickListener {

    public static final String TAG = CourseMainFragment.class.getSimpleName();

    private ToolbarTopView toolbarTopView;
    private TextView mLocalResourcesTab, mLocalCoursesTab, mCloudCoursesTab;
    private View tabLayout, cursorLayout;
    private MyViewPager mViewPager;
    List<Fragment> mFragments;
    MyFragmentPagerAdapter fragmentAdapter;
    ImageView cursor;
    int offset, bmpW = 10;
    int currIndex = 0;
    MediaListFragment cloudCourseFragment;

    private boolean isRemote = false;
    private boolean isPick;
    private int mediaType;
    private String mediaName;
    private boolean isFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_main, container,
            false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(MediaListFragment.EXTRA_IS_PICK);
            mediaType = getArguments().getInt(MediaListFragment.EXTRA_MEDIA_TYPE);
            mediaName = getArguments().getString(MediaListFragment.EXTRA_MEDIA_NAME);
            isFinish = getArguments().getBoolean(MediaListFragment.EXTRA_IS_FINISH);
        }
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getTitleView().setText(mediaName);
        toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getTitleView().setText(getArguments().getString(MediaListFragment.EXTRA_MEDIA_NAME));
        mLocalResourcesTab = (TextView) findViewById(R.id.local_resource_tab);
        mLocalCoursesTab = (TextView) findViewById(R.id.local_course_tab);
        mCloudCoursesTab = (TextView) findViewById(R.id.cloud_course_tab);
        mLocalResourcesTab.setOnClickListener(this);
        mLocalCoursesTab.setOnClickListener(this);
        mCloudCoursesTab.setOnClickListener(this);

        tabLayout = findViewById(R.id.tab_layout);
        cursorLayout = findViewById(R.id.cursor_layout);
        if (isPick) {
            tabLayout.setVisibility(View.GONE);
            cursorLayout.setVisibility(View.GONE);
            isRemote = true;
        } else {
            tabLayout.setVisibility(View.VISIBLE);
            cursorLayout.setVisibility(View.VISIBLE);
        }
        InitImageView();
        initFragment();

        setCommitView();

        if (isPick) {
            mViewPager.setCurrentItem(2);
        }
    }

    private void initFragment() {
        mViewPager = (MyViewPager) getView().findViewById(R.id.viewpager);
        this.mFragments = new ArrayList<Fragment>();
        Fragment fragment = new LocalCourseFragment();
        Bundle args = new Bundle();
        args.putInt(LocalCourseFragment.EXTRA_COURSE_TYPE, CourseType.COURSE_TYPE_IMPORT);
        fragment.setArguments(args);
        this.mFragments.add(fragment);
        fragment = new LocalCourseFragment();
        args = new Bundle();
        args.putInt(LocalCourseFragment.EXTRA_COURSE_TYPE, CourseType.COURSE_TYPE_LOCAL);
        fragment.setArguments(args);
        this.mFragments.add(fragment);
        cloudCourseFragment = getMediaListFragment(true);
        this.mFragments.add(cloudCourseFragment);
        this.fragmentAdapter = new MyFragmentPagerAdapter(
            getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(this.fragmentAdapter);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    private void setCommitView() {
        if (!isRemote) {
            toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
        } else {
            toolbarTopView.getCommitView().setVisibility(isPick ? View.VISIBLE : View.INVISIBLE);
            toolbarTopView.getCommitView().setText(R.string.confirm);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                if (!isFinish) {
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null) {
                        fragmentManager.popBackStack();
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
                break;
            case R.id.local_resource_tab:
                mViewPager.setCurrentItem(0);
                changeTabColor(0);
                isRemote = false;
                setCommitView();
                break;
            case R.id.local_course_tab:
                mViewPager.setCurrentItem(1);
                changeTabColor(1);
                isRemote = false;
                setCommitView();
                break;
            case R.id.cloud_course_tab:
                mViewPager.setCurrentItem(2);
                changeTabColor(2);
                isRemote = true;
                setCommitView();
                break;
            case R.id.toolbar_top_commit_btn:
                if(isPick && cloudCourseFragment != null) {
                    cloudCourseFragment.getSelectData();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private MediaListFragment getMediaListFragment(boolean isRemote) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, mediaType);
        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, mediaName);
        args.putBoolean(MediaListFragment.EXTRA_IS_PICK, isPick);
        args.putBoolean(MediaListFragment.EXTRA_IS_REMOTE, isRemote);
        fragment.setArguments(args);
        return fragment;
    }

    private void changeTabColor(int index) {
        if (index == 0) {
            mLocalResourcesTab.setTextColor(getResources().getColor(R.color.text_green));
            mLocalCoursesTab.setTextColor(getResources().getColor(R.color.text_black));
            mCloudCoursesTab
                .setTextColor(getResources().getColor(R.color.text_black));
        } else if (index == 1) {
            mLocalResourcesTab.setTextColor(getResources().getColor(R.color.text_black));
            mLocalCoursesTab.setTextColor(getResources().getColor(R.color.text_green));
            mCloudCoursesTab
                .setTextColor(getResources().getColor(R.color.text_black));
        } else if (index == 2) {
            mLocalResourcesTab.setTextColor(getResources().getColor(R.color.text_black));
            mLocalCoursesTab.setTextColor(getResources().getColor(R.color.text_black));
            mCloudCoursesTab
                .setTextColor(getResources().getColor(R.color.text_green));
        }
    }

    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        bmpW = screenW / 3;
        offset = 0;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;
        int two = one * 2;

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }


        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
