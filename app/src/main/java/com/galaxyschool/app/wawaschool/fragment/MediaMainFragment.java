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
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.libs.mediapaper.AudioPopwindow;

import java.util.ArrayList;
import java.util.List;

public class MediaMainFragment extends BaseFragment implements
    View.OnClickListener {

    public static final String TAG = MediaMainFragment.class.getSimpleName();

    private ToolbarTopView toolbarTopView;
    private TextView mLocalTab, mCloudTab;
    private View tabLayout, cursorLayout;
    private MyViewPager mViewPager;
    List<Fragment> mFragments;
    MyFragmentPagerAdapter fragmentAdapter;
    ImageView cursor;
    int offset, bmpW = 10;
    int currIndex = 0;

    private MediaListFragment localFragment,cloudFragment;

    private boolean isRemote = false;
    private boolean isPick;
    private int mediaType;
    private String mediaName;
    private boolean isFinish;
    private boolean isShowLQTools;
    private boolean isCampusPatrolTag;
    private String resourceCountStr;
    private TeacherDataStaticsInfo info;
    private String startDate,endDate;
    private AudioPopwindow audioPopwindow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_main, container,
            false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(localFragment==null)return;
        audioPopwindow=localFragment.getAudioPopwindow();
        if(audioPopwindow!=null && audioPopwindow.isShowing()){
            audioPopwindow.pauseAudioOutSide();
            audioPopwindow.stopRecordOutSide();
        }
    }

    private void initViews() {
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(MediaListFragment.EXTRA_IS_PICK);
            mediaType = getArguments().getInt(MediaListFragment.EXTRA_MEDIA_TYPE);
            mediaName = getArguments().getString(MediaListFragment.EXTRA_MEDIA_NAME);
            isFinish = getArguments().getBoolean(MediaListFragment.EXTRA_IS_FINISH);
            isShowLQTools = getArguments().getBoolean(MediaListFragment.EXTRA_IS_SHOW_LQ_TOOLS);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE);
        }
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setTextColor(getResources().getColor(R.color.text_green));
//        toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);
        toolbarTopView.getBackView().setOnClickListener(this);
        if (!isCampusPatrolTag) {
            toolbarTopView.getTitleView().setText(mediaName);
        }else {
            //校园巡查逻辑
            toolbarTopView.getTitleView().setText(mediaName + getString(R.string.media_num,
                    resourceCountStr));
        }
        mLocalTab = (TextView) findViewById(R.id.local_tab);
        mCloudTab = (TextView) findViewById(R.id.cloud_tab);
        mLocalTab.setOnClickListener(this);
        mCloudTab.setOnClickListener(this);
        tabLayout = findViewById(R.id.tab_layout);
        cursorLayout = findViewById(R.id.cursor_layout);
        if (isPick) {
            tabLayout.setVisibility(View.GONE);
            cursorLayout.setVisibility(View.GONE);
            isRemote = true;
        } else {
            if (mediaType == MediaType.MICROCOURSE) {
                tabLayout.setVisibility(View.GONE);
                cursorLayout.setVisibility(View.GONE);
//                isRemote = true;


            } else {
                tabLayout.setVisibility(View.GONE);
                cursorLayout.setVisibility(View.GONE);
            }
        }
        InitImageView();
        initFragment();

        setCommitView();
        if (mediaType == MediaType.MICROCOURSE || isPick) {
            mViewPager.setCurrentItem(1);
        }

        TextView infoView = (TextView)findViewById(R.id.cloud_wawaweike_info);
        if(infoView != null) {
            infoView.setVisibility((mediaType == MediaType.MICROCOURSE && !isPick)? View.VISIBLE :View.GONE);
        }
    }

    private void initFragment() {
        mViewPager = (MyViewPager) getView().findViewById(R.id.viewpager);
        this.mFragments = new ArrayList<Fragment>();
        this.localFragment = getMediaListFragment(false);
        this.cloudFragment = getMediaListFragment(true);
        this.mFragments.add(localFragment);
        //如果是从lq云板过来的只加载localFragment
        if (!isShowLQTools) {
            this.mFragments.add(cloudFragment);
        }
        this.fragmentAdapter = new MyFragmentPagerAdapter(
            getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(this.fragmentAdapter);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        if (isShowLQTools){
            mViewPager.setCurrentItem(0);
        }else {
            mViewPager.setCurrentItem(1);
        }

        localFragment.setOnViewModeChangeListener(new MediaListFragment.OnViewModeChangeListener() {
            @Override
            public void onViewModeChange(int viewMode,int editMode) {
//                toolbarTopView.getCommitView().setVisibility(viewMode == MediaListFragment
//                        .ViewMode.NORMAL ? View.VISIBLE : View.INVISIBLE);
//                if (viewMode == MediaListFragment.ViewMode.NORMAL && (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE)) {
//                    toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
//                }
                toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
                //点击取消，返回到云空间。
                if (editMode == MediaListFragment.EditMode.CANCEL){
                    if (!isCampusPatrolTag) {
                        toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
                    }else {
                        //校园巡查始终显示筛选按钮
                        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
                    }
                    if (!isShowLQTools) {
                        mViewPager.setCurrentItem(1);
                        changeTabColor(1);
                        //刷新云空间页面
                        cloudFragment.loadViews();
                    }else {
                        localFragment.initBasicLocalView();
                    }
                }
            }
        });

        cloudFragment.setOnViewModeChangeListener(new MediaListFragment.OnViewModeChangeListener() {
            @Override
            public void onViewModeChange(int viewMode,int editMode) {
                //如果是点击云空间里面的上传的话，需要切换到本机,然后进入选择状态。
                if (editMode == MediaListFragment.EditMode.UPLOAD) {
                    updateRightView();
                    mViewPager.setCurrentItem(0);
                    changeTabColor(0);
                    localFragment.clickUpload();
                }
            }
        });

        cloudFragment.setOnDataLoadedListener(new MediaListFragment.OnDataLoadedListener() {
            @Override
            public void onDataLoaded(int dataSize) {
                //更新标题的数量
                if (!isCampusPatrolTag) {
                    toolbarTopView.getTitleView().setText(mediaName);
                }else {
                    //校园巡查逻辑
                    toolbarTopView.getTitleView().setText(mediaName + getString(R.string.media_num,
                            dataSize));
                }
            }
        });
    }


    private void updateRightView() {

        toolbarTopView.getCommitView().setVisibility((mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) ?
                View.INVISIBLE : View.VISIBLE);
        if (mediaType == MediaType.PICTURE) {
            toolbarTopView.getCommitView().setText(R.string.take_photo);
        } else if (mediaType == MediaType.AUDIO) {
            toolbarTopView.getCommitView().setText(R.string.record_video);
        } else if (mediaType == MediaType.VIDEO) {
            toolbarTopView.getCommitView().setText(R.string.record_video);
        }
    }

    private void setCommitView() {
        //隐藏云空间操作按钮
        if (isRemote) {
            //选择的时候不一样
            if (isPick) {
                toolbarTopView.getCommitView().setVisibility(isPick ? View.VISIBLE : View.INVISIBLE);
                toolbarTopView.getCommitView().setText(R.string.confirm);
            } else {
                    toolbarTopView.getCommitView().setVisibility((mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) ?
                            View.INVISIBLE : View.VISIBLE);
                    if (mediaType == MediaType.PICTURE) {
                        toolbarTopView.getCommitView().setText(R.string.take_photo);
                    } else if (mediaType == MediaType.AUDIO) {
                        toolbarTopView.getCommitView().setText(R.string.record_video);
                    } else if (mediaType == MediaType.VIDEO) {
                        toolbarTopView.getCommitView().setText(R.string.record_video);
                    } else {
                        toolbarTopView.getCommitView().setVisibility(isPick ? View.INVISIBLE : View.INVISIBLE);
                    }
                }
        }

        //校园巡查逻辑
        if (isCampusPatrolTag){
            //校园巡查逻辑
            toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
            toolbarTopView.getCommitView().setText(R.string.screening);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                if(!isFinish) {
                    if (!isShowLQTools) {
                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager != null) {
                            fragmentManager.popBackStack();
                        }
                    }else {
                        if(getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                } else {
                    if(getActivity() != null) {
                        getActivity().finish();
                    }
                }
                break;
            case R.id.local_tab:
                mViewPager.setCurrentItem(0);
                changeTabColor(0);
                isRemote = false;
                setCommitView();
                if(localFragment != null) {
                    localFragment.updateViews();
                }
                break;
            case R.id.cloud_tab:
                mViewPager.setCurrentItem(1);
                changeTabColor(1);
                isRemote = true;
                setCommitView();
                if(cloudFragment != null) {
                    cloudFragment.updateViews();
                }
                break;
            case R.id.toolbar_top_commit_btn:
                if (!isPick) {
                    if (!isCampusPatrolTag) {
                        if (mediaType == MediaType.AUDIO) {
                            if (localFragment != null) {
                                localFragment.recordAudio(v);
                            }
                        } else if (mediaType == MediaType.VIDEO) {
                            if (localFragment != null) {
                                localFragment.cameraVideo();
                            }
                        } else if (mediaType == MediaType.PICTURE) {
                            if (localFragment != null) {
                                localFragment.cameraImage();
                            }
                        }
                    }else {
                        //校园巡查
                        ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
                    }
                } else {
                    if(cloudFragment != null) {
//                        cloudFragment.getSelectData();
                        cloudFragment.selectDataByMediaType();
                    }
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isCampusPatrolTag) {
            if (localFragment != null) {
                localFragment.onActivityResult(requestCode, resultCode, data);
            }
        }else {
            //校园巡查逻辑
            if (cloudFragment != null){
                cloudFragment.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    private MediaListFragment getMediaListFragment(boolean isRemote) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, mediaType);
        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, mediaName);
        args.putBoolean(MediaListFragment.EXTRA_IS_PICK, isPick);
        args.putBoolean(MediaListFragment.EXTRA_IS_REMOTE, isRemote);
        args.putBoolean(MediaListFragment.EXTRA_IS_SHOW_LQ_TOOLS,isShowLQTools);
        args.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,isCampusPatrolTag);
        args.putBoolean(MediaListFragment.EXTRA_IS_FORM_ONLINE,getArguments().getBoolean
                (MediaListFragment.EXTRA_IS_FORM_ONLINE));
        args.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        //看课件支持多类型
        if (getArguments() != null) {
            boolean support = getArguments().getBoolean(MediaListFragment
                    .EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE);
            args.putBoolean(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE, support);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private void changeTabColor(int index) {
        if (index == 0) {
            mLocalTab.setTextColor(getResources().getColor(R.color.text_green));
            mCloudTab.setTextColor(getResources().getColor(R.color.text_black));
        } else if (index == 1) {
            mLocalTab.setTextColor(getResources().getColor(R.color.text_black));
            mCloudTab.setTextColor(getResources().getColor(R.color.text_green));
        }
    }

    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        bmpW = screenW / 2;
        offset = 0;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset + bmpW;

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    }
                    break;
            }
            currIndex = arg0;
            if (animation != null) {
                animation.setFillAfter(true);
                animation.setDuration(300);
                cursor.startAnimation(animation);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
