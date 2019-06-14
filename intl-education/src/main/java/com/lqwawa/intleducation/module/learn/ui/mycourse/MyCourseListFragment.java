package com.lqwawa.intleducation.module.learn.ui.mycourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.tab.TabCourseEmptyView;
import com.lqwawa.intleducation.module.learn.ui.mycourse.detail.MyCourseConfigDetailActivity;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc V5.12版本新添加我的习课程标签筛选页面
 *
 */
public class MyCourseListFragment extends PresenterFragment<MyCourseListContract.Presenter>
    implements MyCourseListContract.View,MyCourseConfigNavigator{

    // 去在线学习
    public static final String ACTION_GO_COURSE_SHOP = "ACTION_GO_COURSE_SHOP";

    // 小语种课程
    private static final int MINORITY_LANGUAGE_COURSE_ID = 2004;
    // 英语国际课程
    private static final int ENGLISH_INTERNATIONAL_COURSE_ID = 2001;
    // 特色课程
    private static final int CHARACTERISTIC_COURSE_ID = 2005;
    // 基础课程
    private static final int COUNTRY_COURSE_ID = 2003;
    // 分类阅读
    public static final int CLASSIFIED_READING_ID = 1001;
    // 绘本
    public static final int PICTURE_BOOK_ID = 1002;
    // Q配音
    public static final int Q_DUBBING_ID = 1003;

    // LQ English Primary
    private static final int ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID = 2011;

    // RA BRAIN
    private static final int RA_BRAIN_ID = 2351;

    public static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    public static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    public static final String KEY_EXTRA_BOOLEAN_TEACHER = "KEY_EXTRA_BOOLEAN_TEACHER";


    private PullToRefreshView mRefreshLayout;
    private ExpandableListView mExpandableView;
    private MyCourseConfigAdapter mConfigAdapter;
    private TabCourseEmptyView mTabEmptyLayout;

    private String mCurSchoolId;
    private String mCurMemberId;
    private boolean isTeacher;

    public static Fragment newInstance(@Nullable String schoolId,
                                       @NonNull String memberId,
                                       boolean isTeacher){
        Fragment fragment = new MyCourseListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        arguments.putString(KEY_EXTRA_MEMBER_ID,memberId);
        arguments.putBoolean(KEY_EXTRA_BOOLEAN_TEACHER,isTeacher);

        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected MyCourseListContract.Presenter initPresenter() {
        return new MyCourseListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_mycourse_outer;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mCurSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        isTeacher = bundle.getBoolean(KEY_EXTRA_BOOLEAN_TEACHER);
        if(EmptyUtil.isEmpty(mCurMemberId)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());

        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                mPresenter.requestStudentConfigData(mCurMemberId);
            }
        });

        mExpandableView = (ExpandableListView) mRootView.findViewById(R.id.expandable_view);
        mTabEmptyLayout = (TabCourseEmptyView) mRootView.findViewById(R.id.tab_empty_layout);
        mTabEmptyLayout.setSubmitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 去百分百习课程
                Intent broadIntent = new Intent();
                broadIntent.setAction(ACTION_GO_COURSE_SHOP);
                getContext().sendBroadcast(broadIntent);
            }
        });
        mConfigAdapter = new MyCourseConfigAdapter(null);
        mConfigAdapter.setNavigator(this);
        mExpandableView.setAdapter(mConfigAdapter);
        mExpandableView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        mPresenter.requestStudentConfigData(mCurMemberId);
    }

    public void getData(){
        mPresenter.requestStudentConfigData(mCurMemberId);
    }

    @Override
    public void updateStudentConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        if(EmptyUtil.isEmpty(entities)){
            mExpandableView.setVisibility(View.GONE);
            mTabEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            mExpandableView.setVisibility(View.VISIBLE);
            mTabEmptyLayout.setVisibility(View.GONE);

            filterData(entities);
            fillData(entities);
            mConfigAdapter.setData(entities);
            // mExpandableView.getCount();
            // 返回并不是正确的groupCount();
            int groupCount = mConfigAdapter.getGroupCount();
            /*int count = mExpandableView.getCount();
            LogUtil.e(MyCourseListFragment.class,"current memberId : " + mCurMemberId);
            LogUtil.e(MyCourseListFragment.class,"ExpandableListView id : " + mExpandableView.toString());
            LogUtil.e(MyCourseListFragment.class,"ExpandableListView count : " + count);
            LogUtil.e(MyCourseListFragment.class,"ExpandableAdapter group count : " + groupCount);*/
            for (int i=0; i < groupCount; i++) {
                mExpandableView.expandGroup(i);
            }
        }
    }


    @Override
    public void onChoiceConfig(@NonNull LQCourseConfigEntity groupEntity,
                               @NonNull LQCourseConfigEntity childEntity,
                               @Nullable LQCourseConfigEntity configEntity) {
        // 点击标签
        String level = "";
        int rootId = groupEntity.getId();
        if(EmptyUtil.isNotEmpty(childEntity)) level = childEntity.getLevel();
        if(rootId == MINORITY_LANGUAGE_COURSE_ID || rootId == CLASSIFIED_READING_ID || rootId == RA_BRAIN_ID){
            level = configEntity.getLevel();
        }

        int paramOneId = 0;
        int paramTwoId = 0;

        if(rootId != MINORITY_LANGUAGE_COURSE_ID && rootId != CLASSIFIED_READING_ID && rootId != RA_BRAIN_ID){

            int rootTypeId = 0;
            if(EmptyUtil.isNotEmpty(childEntity)) rootTypeId = childEntity.getId();
            if(rootId == CHARACTERISTIC_COURSE_ID || rootId == COUNTRY_COURSE_ID){
                // 特色课程或者国家课程
                paramTwoId = configEntity.getLabelId();
            }else if(rootId == ENGLISH_INTERNATIONAL_COURSE_ID && rootTypeId == ENGLISH_INTERNATIONAL_ENGLISH_PRIMARY_ID){
                // 英语国际课程 LQ English PRIMARY
                paramTwoId = configEntity.getLabelId();
            }else{
                paramOneId = configEntity.getLabelId();
            }
        }

        MyCourseConfigDetailActivity.show(getActivity(),configEntity,mCurSchoolId,mCurMemberId,isTeacher,level,paramOneId,paramTwoId);
    }

    /**
     * 过滤专注力训练第三极数据
     * @param entities
     */
    private void filterData(List<LQCourseConfigEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            for (LQCourseConfigEntity entity : entities) {
                if (entity != null && entity.getId() == RA_BRAIN_ID) {
                    List<LQCourseConfigEntity> childList = entity.getChildList();
                    if (childList != null && !childList.isEmpty()) {
                        for (LQCourseConfigEntity configEntity : childList) {
                            if (configEntity != null) {
                                List<LQCourseConfigEntity> list = new ArrayList<>();
                                configEntity.setChildList(list);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 没有二级列表时拿一级数据填充
     *
     * @param entities
     */
    private void fillData(List<LQCourseConfigEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            for (LQCourseConfigEntity entity : entities) {
                if (entity != null && (entity.getChildList() == null
                        || entity.getChildList().isEmpty())) {
                    List<LQCourseConfigEntity> list = new ArrayList<>();
                    LQCourseConfigEntity newEntity = entity.clone();
                    entity.setSelected(false);
                    list.add(newEntity);
                    entity.setChildList(list);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event,EventConstant.TRIGGER_EXIT_COURSE)){
            getData();
        }else if(EventWrapper.isMatch(event, EventConstant.APPOINT_COURSE_IN_CLASS_EVENT)){
            getData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull MessageEvent event){
        if(TextUtils.equals(EventConstant.TRIGGER_UPDATE_COURSE,event.getUpdateAction())){
            // 发生课程信息更新，刷新UI
            getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
