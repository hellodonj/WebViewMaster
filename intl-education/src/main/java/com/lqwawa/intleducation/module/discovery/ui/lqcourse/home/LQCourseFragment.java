package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.BannerHeaderView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqbasic.LQBasicActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.BasicsCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist.ClassifyListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.common.NewCommonHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.livelist.LiveListActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyItemHolder;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyNavigator;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.newfiltrate.NewOnlineClassifyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程主页面 V5.5版本添加
 * @date 2018/04/27 14:52
 * @history v1.0
 * **********************************
 */
public class LQCourseFragment extends PresenterFragment<LQCourseContract.Presenter>
    implements LQCourseContract.View,LQCourseNavigator,OnlineStudyNavigator,
        View.OnClickListener{
    /**
     * 下拉刷新布局
     */
    private PullToRefreshView mRefreshLayout;
    /**
     * 轮播布局
     */
    private BannerHeaderView mBannerView;
    /**
     * 分类列表 分类列表的Adapter
     */
    private RecyclerView mClassifyRecycler;
    private ClassifyAdapter mClassifyAdapter;
    // 中段课程容器
    private LinearLayout mCourseLayout;
    // 英语国际课程
    // private EnglishCourseInternationalHolder mEnglishInternationalHolder;
    private NewCommonHolder mEnglishInternationalHolder;
    // 英语国内课程
    // private EnglishCourseInlandHolder mEnglishInlandHolder;
    private NewCommonHolder mEnglishInlandHolder;
    // 阅读课程
    private ReadCourseHolder mReadCourseHolder;
    // 小语种课程
    // private NewMinorityLanguageHolder mMinorityLanguageHolder;
    private NewCommonHolder mMinorityLanguageHolder;
    // 基础课程
    private NewBasicsCourseHolder mBasicsHolder;

    // 直播数据
    private LiveHolder mLiveHolder;
    // 所有课程信息View
    private DiscoveryHolder mDiscoveryHolder;
    // 小语种空中课堂
    private OnlineStudyItemHolder mMinorityLanguageClassHolder;
    // 国际空中课堂
    private OnlineStudyItemHolder mInternationalClassHolder;

    // 我的学程按钮
    private TextView mTvMyCourse;

    // 分类数据
    // @date   :2018/5/3 0003 下午 3:39
    // @func   :V5.5 该数据已经舍弃
    // private List<ClassifyVo> mClassifyVos;
    // V5.5分类数据
    private List<LQCourseConfigEntity> mConfigEntities;
    // 基础课程数据
    private List<LQBasicsOuterEntity> mBasicsEntities;

    @Override
    protected LQCourseContract.Presenter initPresenter() {
        return new LQCoursePresenter(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mBannerView = (BannerHeaderView) mRootView.findViewById(R.id.banner_view);
        // 定义分类列表数据
        mClassifyRecycler = (RecyclerView) mRootView.findViewById(R.id.classify_recycler);
        mClassifyRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mClassifyLayoutManager = new GridLayoutManager(getContext(),3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mClassifyRecycler.setLayoutManager(mClassifyLayoutManager);
        // mClassifyRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(3,8));
        mClassifyAdapter = new ClassifyAdapter();
        mClassifyRecycler.setAdapter(mClassifyAdapter);
        mClassifyAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                super.onItemClick(holder, entity);
                // 点击了分类列表数据 显示下级列表
                if(entity.getId() == CourseFiltrateActivity.BASIC_COURSE_ID){
                    // 进入基础课程列表
                    // SubjectActivity.show(getActivity());
                    // LQBasicActivity.show(getActivity());
                    if(EmptyUtil.isNotEmpty(mConfigEntities)){
                        if(mConfigEntities.size() > 3){
                            LQCourseConfigEntity configEntity = mConfigEntities.get(3);
                            BasicsCourseActivity.show(getActivity(),configEntity,mBasicsEntities);
                        }
                    }
                }else{
                    ClassifyListActivity.show(getActivity(),entity);
                }
                // EmptyActivity.show(getActivity());
            }
        });

        mCourseLayout = (LinearLayout) mRootView.findViewById(R.id.course_layout);

        // 添加中段课程
        // 小语种课程
        /*mMinorityLanguageHolder = new NewMinorityLanguageHolder(getContext());
        mMinorityLanguageHolder.setCourseNavigator(this);
        mCourseLayout.addView(mMinorityLanguageHolder.getRootView());*/

        mMinorityLanguageHolder = new NewCommonHolder(getContext());
        mMinorityLanguageHolder.setCourseNavigator(this);
        mCourseLayout.addView(mMinorityLanguageHolder.getRootView());

        /*mEnglishInternationalHolder = new EnglishCourseInternationalHolder(getContext());
        mEnglishInternationalHolder.setCourseNavigator(this);
        mCourseLayout.addView(mEnglishInternationalHolder.getRootView());*/

        mEnglishInternationalHolder = new NewCommonHolder(getContext());
        mEnglishInternationalHolder.setCourseNavigator(this);
        mCourseLayout.addView(mEnglishInternationalHolder.getRootView());

        // 英语国内课程
        /*mEnglishInlandHolder = new EnglishCourseInlandHolder(getContext());
        mEnglishInlandHolder.setCourseNavigator(this);
        mCourseLayout.addView(mEnglishInlandHolder.getRootView());*/
        mEnglishInlandHolder = new NewCommonHolder(getContext());
        mEnglishInlandHolder.setCourseNavigator(this);
        mCourseLayout.addView(mEnglishInlandHolder.getRootView());

        // 阅读课程 V5.5X UI修改
        /*mReadCourseHolder = new ReadCourseHolder(getContext());
        mReadCourseHolder.setCourseNavigator(this);
        mCourseLayout.addView(mReadCourseHolder);*/
        // 直播数据
        mLiveHolder = (LiveHolder) mRootView.findViewById(R.id.live_holder);
        mLiveHolder.setNavigator(this);
        // 基础课程
        mBasicsHolder = new NewBasicsCourseHolder(getContext());
        mBasicsHolder.setCourseNavigator(this);
        // mCourseLayout.addView(mBasicsHolder.getRootView());
        // 相关课程分类 热门课程等
        mDiscoveryHolder = (DiscoveryHolder) mRootView.findViewById(R.id.discovery_holder);
        mDiscoveryHolder.setNavigator(this);

        mMinorityLanguageClassHolder = (OnlineStudyItemHolder) mRootView.findViewById(R.id.minority_language_class_holder);
        mMinorityLanguageClassHolder.setOnlineStudyNavigator(this);

        mInternationalClassHolder = (OnlineStudyItemHolder) mRootView.findViewById(R.id.english_international_class_holder);
        mInternationalClassHolder.setOnlineStudyNavigator(this);

        mTvMyCourse = (TextView) mRootView.findViewById(R.id.tv_my_course);
        mTvMyCourse.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                mPresenter.start();
                // mPresenter.requestNewBasicConfig();
            }
        });
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mPresenter.start();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lq_course;
    }

    @Override
    public void updateBannerViews(List<String> urlBanners) {
        // 刷新轮播图
        mBannerView.setImgUrlData(urlBanners);
        mRefreshLayout.onHeaderRefreshComplete();
    }


    @Override
    public void updateLiveView(@NonNull List<LiveVo> liveVos) {
        mLiveHolder.updateLiveData(liveVos);
    }

    @Override
    public void updateConfigViews(@NonNull List<LQCourseConfigEntity> entities) {
        if(entities.size() > 3) entities = new ArrayList<>(entities.subList(0,3));
        this.mConfigEntities = entities;
        mRefreshLayout.onHeaderRefreshComplete();
        try{
            // 刷新分类列表
            if(!EmptyUtil.isEmpty(entities)) {
                // entities.addAll(entities);
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).setThumbnail("ic_lq_english_child_" + i % 4);
                }
            }
            mClassifyAdapter.replace(entities);
            mRefreshLayout.onHeaderRefreshComplete();

            // 0是英语国际课程
            // 测试7个
            /*LQCourseConfigEntity entity = entities.get(1);
            entity.getChildList().addAll(entity.getChildList().subList(0,2));*/
            mEnglishInternationalHolder.updateView(/*entity*/entities.get(1));
            // 1是英语国内课程
            // 测试超过8个
            /*LQCourseConfigEntity entity2 = entities.get(2);
            entity2.getChildList().addAll(entity2.getChildList().subList(0,4));*/
            mEnglishInlandHolder.updateView(/*entity2*/entities.get(2));
            // 2是阅读课程 V5.6删除阅读课程
            // mReadCourseHolder.updateView(entities.get(2));
            // 2是小语种课程
            mMinorityLanguageHolder.updateView(entities.get(0));
            // 3是基础课程
            // mBasicsHolder.updateView(entities.get(3));
            // 标签数据抓到了，获取基础课程数据
            // mPresenter.requestNewBasicConfig();
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.e(getClass(),"数组越界异常");
        }
    }

    @Override
    public void updateNewBasicsConfigView(@NonNull List<LQBasicsOuterEntity> entities) {
        mBasicsEntities = entities;
        // 获取新基础课程数据
        if(EmptyUtil.isNotEmpty(mConfigEntities)){
            if(mConfigEntities.size() > 3){
                LQCourseConfigEntity configEntity = mConfigEntities.get(3);
                mBasicsHolder.updateView(true,configEntity,entities);
            }
        }
    }

    @Override
    public void updateHotCourseView(@NonNull List<CourseVo> courseVos) {
        // 刷新热门UI
        mDiscoveryHolder.updateHotCourseData(courseVos);
        mRefreshLayout.onHeaderRefreshComplete();
    }

    @Override
    public void updateXyzOnlineClassView(@NonNull List<OnlineClassEntity> entities) {
        mMinorityLanguageClassHolder.updateView(OnlineStudyType.SORT_MINORITY_ONLINE_CLASS,entities);
    }

    @Override
    public void updateInternationalOnlineClassView(@NonNull List<OnlineClassEntity> entities) {
        mInternationalClassHolder.updateView(OnlineStudyType.SORT_INTERNATIONAL_ONLINE_CLASS,entities);
    }


    @Override
    public void onClickConfigTitleLayout(LQCourseConfigEntity entity) {
        // 点击相关分类课程标题,进入分类列表
        ClassifyListActivity.show(getActivity(),entity);
    }

    @Override
    public void onClickClassify(LQCourseConfigEntity entity) {
        GroupFiltrateState state = new GroupFiltrateState(entity);
        CourseFiltrateActivity.show(getActivity(),entity,state);
    }

    @Override
    public void onClickCourseTitleLayout() {
        // 进入课程列表
        LQCourseListActivity.show(getActivity(),0,HideSortType.TYPE_SORT_HOT_RECOMMEND,getString(R.string.hot_recommended));
    }

    @Override
    public void onClickCourse(@NonNull CourseVo courseVo) {
        // 进入课程详情
        CourseDetailsActivity.start(getActivity(),courseVo.getId(), true, UserHelper.getUserId());
    }


    @Override
    public void onClickLiveTitleLayout() {
        LiveListActivity.show(getActivity(),HideSortType.TYPE_SORT_LIVE_LIST,getString(R.string.live_room));
    }

    @Override
    public void onClickLive(@NonNull LiveVo liveVo) {
        LiveDetails.jumpToLiveDetails(getActivity(), liveVo, false, true ,false);
        if (liveVo.getState() != 0) {
            // @date   :2018/5/2 0002 下午 4:50
            // @func   :其实我不知道这句代码是干什么的，上一个这样写的，还没仔细看
            liveVo.setBrowseCount(liveVo.getBrowseCount() + 1);
        }
    }

    @Override
    public void onClickBasicsSubject(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity) {
        mPresenter.requestConfigWithBasicsEntity(entity);
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
    public void onClickBasicsLayout() {
        // 进入基础课程列表
        if(EmptyUtil.isNotEmpty(mConfigEntities)){
            if(mConfigEntities.size() > 3){
                LQCourseConfigEntity entity = mConfigEntities.get(3);
                BasicsCourseActivity.show(getActivity(),entity,mBasicsEntities);
            }
        }
    }



    @Override
    public void onClickTitleLayout(@NonNull int sort) {
        if(sort == OnlineStudyType.SORT_MINORITY_ONLINE_CLASS){
            // 点击空中课堂更多
            // UIUtil.showToastSafe("点击空中课堂更多");
            // 获取中英文数据
            int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
            OnlineCourseHelper.requestNewOnlineClassifyConfigData(NewOnlineClassifyFiltrateActivity.DataType.MINORITY_LANGUAGE.getIndex(), languageRes, new DataSource.Callback<List<NewOnlineConfigEntity>>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(List<NewOnlineConfigEntity> entities) {
                    String configValue = UIUtil.getString(R.string.label_minority_language_holder_title);
                    NewOnlineConfigEntity entity = new NewOnlineConfigEntity();
                    entity.setConfigValue(configValue);
                    entity.setId(NewOnlineStudyFiltrateActivity.MINORITY_LANGUGAE_ID);
                    entity.setChildList(entities);
                    NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(entity.getConfigValue(),entity);
                    NewOnlineStudyFiltrateActivity.show(getContext(),params);
                }
            });
        }else if(sort == OnlineStudyType.SORT_INTERNATIONAL_ONLINE_CLASS){
            // 点击空中课堂更多
            // UIUtil.showToastSafe("点击空中课堂更多");
            NewOnlineClassifyFiltrateActivity.show(getActivity(),NewOnlineClassifyFiltrateActivity.DataType.INTERNATIONAL);
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
        if(viewId == R.id.tv_my_course){
            // 我的学程
            if(!UserHelper.isLogin()){
                LoginHelper.enterLogin(getActivity());
                return;
            }

            // 进入我的学程
            // MyCourseListActivity.start(getActivity());
            // com.lqwawa.intleducation.module.discovery.ui.person.mycourse.MyCourseListActivity.start(getActivity());
        }
    }
}
