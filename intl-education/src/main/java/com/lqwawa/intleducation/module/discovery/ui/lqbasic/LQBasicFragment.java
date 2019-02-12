package com.lqwawa.intleducation.module.discovery.ui.lqbasic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.coin.donation.DonationCoinContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.LQBasicFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.SearchParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.ClassifyAdapter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.DiscoveryHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.NewBasicsCourseHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyItemHolder;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.newfiltrate.NewOnlineClassifyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.onclass.school.LQCourseNavigatorImpl;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程页面
 */
public class LQBasicFragment extends PresenterFragment<LQBasicContract.Presenter>
    implements LQBasicContract.View,OnlineStudyNavigator,View.OnClickListener {

    private FrameLayout mSearchLayout;
    private PullToRefreshView mRefreshLayout;
    private LinearLayout mRootLayout;
    private RecyclerView mClassifyRecycler;
    private ClassifyAdapter mClassifyAdapter;
    private NewBasicsCourseHolder mNewBasicHolder;
    // 热门推荐View
    private DiscoveryHolder mDiscoveryHolder;
    // 在线课堂View
    private OnlineStudyItemHolder mOnlineItemHolder;

    private List<LQCourseConfigEntity> mConfigEntities;


    public static LQBasicFragment newInstance(){
        LQBasicFragment fragment = new LQBasicFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected LQBasicContract.Presenter initPresenter() {
        return new LQBasicPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lq_basic;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSearchLayout = (FrameLayout) mRootView.findViewById(R.id.search_layout);
        mSearchLayout.setOnClickListener(this);
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadData();
            }
        });
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());

        mRootLayout = (LinearLayout) mRootView.findViewById(R.id.root_layout);
        mClassifyRecycler = (RecyclerView) mRootView.findViewById(R.id.classify_recycler);

        mClassifyRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mClassifyLayoutManager = new GridLayoutManager(getContext(),4){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mClassifyRecycler.setLayoutManager(mClassifyLayoutManager);
        mClassifyAdapter = new ClassifyAdapter();
        mClassifyRecycler.setAdapter(mClassifyAdapter);
        mClassifyAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                super.onItemClick(holder, entity);
                // 点击了分类列表数据 显示下级列表
                // UIUtil.showToastSafe("点击了分类列表数据 显示下级列表");
                int parentId = entity.getId();
                String level = entity.getLevel();
                String configValue = entity.getConfigValue();
                LQBasicFiltrateActivity.show(getActivity(),parentId,level,configValue);
            }
        });

        mNewBasicHolder = (NewBasicsCourseHolder) mRootView.findViewById(R.id.basics_holder);

        mDiscoveryHolder = (DiscoveryHolder) mRootView.findViewById(R.id.discovery_holder);
        mOnlineItemHolder = (OnlineStudyItemHolder) mRootView.findViewById(R.id.online_item_holder);

        mNewBasicHolder.setCourseNavigator(new LQCourseNavigatorImpl(){
            @Override
            public void onClickBasicsSubject(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity) {
                super.onClickBasicsSubject(entity);
                mPresenter.requestConfigWithBasicsEntity(entity);
            }
        });

        mDiscoveryHolder.setNavigator(new LQCourseNavigatorImpl(){

            @Override
            public void onClickCourseTitleLayout() {
                // 进入课程列表
                LQCourseListActivity.show(getActivity(), 1,HideSortType.TYPE_SORT_HOT_RECOMMEND,getString(R.string.hot_recommended));
            }

            @Override
            public void onClickCourse(@NonNull CourseVo courseVo) {
                // 进入课程详情
                CourseDetailsActivity.start(getActivity(),courseVo.getId(), true, UserHelper.getUserId());
            }
        });
        mOnlineItemHolder.setOnlineStudyNavigator(this);
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    private void loadData(){
        mPresenter.requestBasicCourseConfigData();
        mPresenter.requestConfigData();
        mPresenter.requestLQRmCourseData();
    }

    @Override
    public void updateBasicCourseConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        mClassifyAdapter.replace(entities);
    }

    @Override
    public void updateConfigViews(@NonNull List<LQCourseConfigEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mConfigEntities = entities;
        if(EmptyUtil.isNotEmpty(mConfigEntities) && mConfigEntities.size() > 3){
            // mPresenter.requestNewBasicConfig();
        }
    }

    @Override
    public void updateNewBasicsConfigView(@NonNull List<LQBasicsOuterEntity> entities) {
        LQCourseConfigEntity configEntity = mConfigEntities.get(3);
        mNewBasicHolder.updateView(true,configEntity,entities);
        // 隐藏基础课程的标题
        mNewBasicHolder.getRootView().findViewById(R.id.title_layout).setVisibility(View.GONE);
    }

    @Override
    public void updateLQRmCourseData(List<CourseVo> entities) {
        mDiscoveryHolder.updateHotCourseData(entities);
    }

    @Override
    public void updateLQRmOnlineCourseData(List<OnlineClassEntity> entities) {
        mOnlineItemHolder.updateView(OnlineStudyType.SORT_ONLINE_CLASS,entities);
    }

    @Override
    public void updateConfigView(@NonNull int parentId, @NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity, @NonNull List<LQCourseConfigEntity> entities) {
        if(EmptyUtil.isNotEmpty(entities)){
            for (LQCourseConfigEntity _entity:entities) {
                if(_entity.getId() == parentId){
                    // 找到该实体
                    _entity.setParamTwoId(entity.getParamTwoId());
                    _entity.setParamThreeId(entity.getParamThreeId());
                    _entity.setConfigValue(entity.getConfigValue());
                    GroupFiltrateState state = new GroupFiltrateState(_entity);
                    CourseFiltrateActivity.show(getActivity(),_entity,state);
                }
            }
        }
    }


    @Override
    public void onClickTitleLayout(@NonNull int sort) {
        if(sort == OnlineStudyType.SORT_ONLINE_CLASS){
            // 点击在线课堂更多
            // UIUtil.showToastSafe("点击在线课堂更多");
            NewOnlineClassifyFiltrateActivity.show(getActivity(),NewOnlineClassifyFiltrateActivity.DataType.BASIC_COURSE);
        }
    }

    @Override
    public void onClickClass(@NonNull OnlineClassEntity entity) {
        ClassInfoParams params = new ClassInfoParams(entity);
        ClassDetailActivity.show(getActivity(),params);
    }

    @Override
    public void onClickOrgan(@NonNull OnlineStudyOrganEntity entity) {
        // 点击更多机构
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.search_layout){
            /*ParamResponseVo.Param param = new ParamResponseVo.Param();
            param.setName(UIUtil.getString(R.string.label_search_course_name_hint));
            NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(param.getName(),param);
            SearchActivity.show(getActivity(),HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS_SUPER,params);*/

            SearchParams params = new SearchParams("2003",UIUtil.getString(R.string.label_search_course_name_hint));
            SearchActivity.show(getActivity(),HideSortType.TYPE_BASIC_COURSE_SEARCH,params);
        }
    }
}
