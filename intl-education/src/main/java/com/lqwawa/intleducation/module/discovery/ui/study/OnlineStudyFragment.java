package com.lqwawa.intleducation.module.discovery.ui.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;

import com.lqwawa.intleducation.base.IBaseActivity;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.classifyclass.OnlineClassClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.joinorgan.JoinOrganListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习页面
 */
public class OnlineStudyFragment extends PresenterFragment<OnlineStudyContract.Presenter>
    implements OnlineStudyContract.View, OnlineStudyNavigator, View.OnClickListener{

    private FrameLayout mSearchLayout;
    private PullToRefreshView mRefreshLayout;
    private RecyclerView mClassifyRecycler;
    private ClassifyAdapter mClassifyAdapter;
    private LinearLayout mCourseLayout;
    // 当前点击的在线课堂实体
    private OnlineClassEntity mCurrentClickEntity;

    public static OnlineStudyFragment newInstance(){
        OnlineStudyFragment fragment = new OnlineStudyFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected OnlineStudyContract.Presenter initPresenter() {
        return new OnlineStudyPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_online_study;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSearchLayout = (FrameLayout) mRootView.findViewById(R.id.search_layout);
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setLoadMoreEnable(false);
        mClassifyRecycler = (RecyclerView) mRootView.findViewById(R.id.classify_recycler);
        mCourseLayout = (LinearLayout) mRootView.findViewById(R.id.course_layout);

        mSearchLayout.setOnClickListener(this);

        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                mRefreshLayout.showRefresh();
                mCourseLayout.removeAllViews();
                mPresenter.requestOnlineStudyLabelData();
                mPresenter.requestOnlineStudyOrganData();
            }
        });

        mClassifyRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),3){
            @Override
            public boolean canScrollVertically() {
                return super.canScrollVertically();
            }
        };
        mClassifyRecycler.setLayoutManager(mLayoutManager);
        mClassifyAdapter = new ClassifyAdapter();
        mClassifyRecycler.setAdapter(mClassifyAdapter);
        mClassifyAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<NewOnlineConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, NewOnlineConfigEntity entity) {
                super.onItemClick(holder, entity);
                // OnlineStudyFiltrateActivity.show(getContext(),entity,false);
                NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(entity.getConfigValue(),entity);
                NewOnlineStudyFiltrateActivity.show(getContext(),params);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        mRefreshLayout.showRefresh();
        mCourseLayout.removeAllViews();
        mPresenter.requestOnlineStudyLabelData();
        mPresenter.requestOnlineStudyOrganData();
    }

    @Override
    public void updateOnlineStudyLabelView(@NonNull List<NewOnlineConfigEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mClassifyAdapter.replace(entities);
    }

    @Override
    public void updateOnlineStudyOrganView(@NonNull List<OnlineStudyOrganEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        OnlineStudyOrganHolder holder = new OnlineStudyOrganHolder(getContext());
        holder.updateView(OnlineStudyType.SORT_ORGAN,entities);
        holder.setOnlineStudyNavigator(this);
        mCourseLayout.addView(holder.getRootView());
    }

    @Override
    public void updateOnlineStudyLatestView(@NonNull List<OnlineClassEntity> entities) {
        OnlineStudyItemHolder holder = new OnlineStudyItemHolder(getContext());
        holder.updateView(OnlineStudyType.SORT_LATEST,entities);
        holder.setOnlineStudyNavigator(this);
        mCourseLayout.addView(holder.getRootView());
    }

    @Override
    public void updateOnlineStudyHotView(@NonNull List<OnlineClassEntity> entities) {
        OnlineStudyItemHolder holder = new OnlineStudyItemHolder(getContext());
        holder.updateView(OnlineStudyType.SORT_HOT,entities);
        holder.setOnlineStudyNavigator(this);
        mCourseLayout.addView(holder.getRootView());
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
    }

    @Override
    public void onClickTitleLayout(@NonNull int sort) {
        if(sort == OnlineStudyType.SORT_ORGAN){
            // 进入机构列表
            JoinOrganListActivity.show(getContext(),null);
        }else if(sort == OnlineStudyType.SORT_LATEST || sort == OnlineStudyType.SORT_HOT){
            // 点击标题跳转
            OnlineClassClassifyActivity.show(getContext(),sort);
        }
    }

    @Override
    public void onClickClass(@NonNull OnlineClassEntity entity) {
        mCurrentClickEntity = entity;
        // 点击班级
        // mPresenter.requestLoadClassInfo(entity.getClassId());
        ClassInfoParams params = new ClassInfoParams(entity);
        ClassDetailActivity.show(getActivity(),params);
    }

    @Override
    public void onClickOrgan(@NonNull OnlineStudyOrganEntity entity) {
        // 拉接口,获取机构信息
        if(!UserHelper.isLogin()){
            LoginHelper.enterLogin(getActivity());
            return;
        }
        mPresenter.requestSchoolInfo(entity.getId(),entity);
    }

    @Override
    public void updateSchoolInfoView(@NonNull SchoolInfoEntity infoEntity,@NonNull OnlineStudyOrganEntity entity) {
        // 关注机构
        subscribeSchool(infoEntity,entity);
    }

    /**
     * 检查关注信息
     */
    private void subscribeSchool(@NonNull SchoolInfoEntity mSchoolEntity,@NonNull OnlineStudyOrganEntity entity){
        // 点击关注
        if(EmptyUtil.isEmpty(mSchoolEntity)){
            // 已经进入机构
            return;
        }

        if(mSchoolEntity.hasJoinedSchool() || mSchoolEntity.hasSubscribed()){
            // 已关注
            // 点击机构，进入机构开课班
            OnlineClassListActivity.show(getContext(),entity.getId(),entity.getName());
        }else{
            // 如果没有关注 +关注
            SchoolHelper.requestSubscribeSchool(mSchoolEntity.getSchoolId(), new DataSource.Callback<Object>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(Object object) {
                    // 关注成功,发送广播,刷新UI
                    // 点击机构，进入机构开课班
                    OnlineClassListActivity.show(getContext(),entity.getId(),entity.getName());
                }
            });
        }
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        // 非空判断
        if(EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentClickEntity)){
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String roles = entity.getRoles();
        String role = UserHelper.getOnlineRoleWithUserRoles(roles);
        if(needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)){
            // 已经加入班级 或者是老师身份
            JoinClassDetailActivity.show(getActivity(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,false);
        }else{
            // 未加入班级
            ClassDetailActivity.show(getActivity(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),role,false);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.search_layout){
            /*Intent firstIntent = new Intent(getActivity(),NewOnlineStudyFiltrateActivity.class);
            Bundle firstBundle = new Bundle();
            ParamResponseVo.Param param = new ParamResponseVo.Param();
            param.setName(UIUtil.getString(R.string.label_search_course_name_hint));
            NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(param.getName(),param);
            firstBundle.putSerializable("ACTIVITY_BUNDLE_OBJECT",params);
            firstIntent.putExtras(firstBundle);

            Intent searchIntent = new Intent(getActivity(),SearchActivity.class);
            Bundle searchBundle = new Bundle();
            searchBundle.putString("KEY_EXTRA_SORT",HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS_SUPER);
            searchBundle.putString("KEY_EXTRA_TITLE",params.getConfigValue());
            searchBundle.putSerializable("KEY_EXTRA_TEACH_ONLINE_CLASS_PARAMS",params);
            searchIntent.putExtras(searchBundle);
            Intent[] intents = new Intent[]{firstIntent,searchIntent};
            getContext().startActivities(intents);*/
            ParamResponseVo.Param param = new ParamResponseVo.Param();
            param.setName(UIUtil.getString(R.string.label_search_course_name_hint));
            NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(param.getName(),param);
            SearchActivity.show(getActivity(),HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS_SUPER,params);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event){
        if(EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)){
            // 刷新UI
            mRefreshLayout.showRefresh();
            mPresenter.requestOnlineStudyLabelData();
            mPresenter.requestOnlineStudyOrganData();
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
