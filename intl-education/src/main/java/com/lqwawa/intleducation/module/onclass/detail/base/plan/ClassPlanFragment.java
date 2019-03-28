package com.lqwawa.intleducation.module.onclass.detail.base.plan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.view.LoadingFooter;
import com.github.jdsjlzx.view.SimpleViewSwitcher;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.TimeLineItemDecoration;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LiveEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailNavigator;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailNavigator;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情 授课计划Fragment
 * @date 2018/06/01 16:03
 * @history v1.0
 * **********************************
 */
public class ClassPlanFragment extends PresenterFragment<ClassPlanContract.Presenter>
        implements ClassPlanContract.View ,View.OnClickListener{

    private static final String KEY_EXTRA_ENTITY = "KEY_EXTRA_ENTITY";
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_ORIGIN_ROLE = "KEY_EXTRA_ORIGIN_ROLE";
    private static final String KEY_EXTRA_HANDLE_ROLE = "KEY_EXTRA_HANDLE_ROLE";

    // private PullToRefreshView mRefreshLayout;
    private LuRecyclerView mRecycler;
    private PlanLiveAdapter mAdapter;
    private LuRecyclerViewAdapter mLuAdapter;
    private LinearLayout mBottomLayout;
    private TextView mBtnCreateClass;
    private TextView mBtnGiveLessons;
    private TextView mTvTimeTable;

    // 原本角色信息
    private String mRole;
    private boolean isParent;
    // 是否是已经加入的
    private boolean isJoin;
    private String schoolId;
    private int pageIndex;

    private OnlineTabParams mTabParams;
    private ClassDetailEntity mClassDetailEntity;
    private JoinClassEntity mClassEntity;

    // 多个发布对象时候,选择的选项
    private RadioButton deleteAllClassRB;

    public static Fragment newInstance(@NonNull OnlineTabParams params) {
        ClassPlanFragment fragment = new ClassPlanFragment();
        Bundle extras = new Bundle();
        extras.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    protected ClassPlanContract.Presenter initPresenter() {
        return new ClassPlanPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_class_plan;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTabParams = (OnlineTabParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mTabParams)) return false;
        schoolId = mTabParams.getSchoolId();
        mClassDetailEntity = mTabParams.getDetailEntity();
        mRole = mTabParams.getRole();
        isParent = mTabParams.isParent();
        isJoin = mTabParams.isJoin();
        if (EmptyUtil.isEmpty(schoolId) ||
                EmptyUtil.isEmpty(mClassDetailEntity) ||
                EmptyUtil.isEmpty(mRole)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        // mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mRecycler = (LuRecyclerView) mRootView.findViewById(R.id.recycler);
        mBottomLayout = (LinearLayout) mRootView.findViewById(R.id.bottom_layout);
        mBtnCreateClass = (TextView) mRootView.findViewById(R.id.btn_new_class);
        mBtnGiveLessons = (TextView) mRootView.findViewById(R.id.btn_complete_give_lessons);
        mTvTimeTable = (TextView) mRootView.findViewById(R.id.tv_timetable);
        mBtnCreateClass.setOnClickListener(this);
        mBtnGiveLessons.setOnClickListener(this);
        mTvTimeTable.setOnClickListener(this);

        if(getActivity() instanceof JoinClassDetailActivity){
            // V5.11隐藏课程表功能
            mTvTimeTable.setVisibility(View.GONE);
            // 未加入页面 没有课程表相关
            // 判断身份，只有老师才显示bottom
            // 班级详情只有班主任才显示
            boolean isTeacher = OnlineClassRole.ROLE_TEACHER.equals(mRole);
            if(!isParent && isTeacher && !mTabParams.isGiveFinish() && !mTabParams.isGiveHistory()){
                // 不是家长身份
                // 是老师身份，并且并没有授课完成
                // 授课完成了，班主任自然不能再完成授课，老师也不能新开课
                mBtnCreateClass.setVisibility(View.VISIBLE);
                mBottomLayout.setVisibility(View.VISIBLE);

                // 判断是否是班主任
                ClassDetailEntity.DataBean data = mClassDetailEntity.getData().get(0);
                boolean isHeadMaster = UserHelper.getUserId().equals(data.getCreateId());
                if(!isHeadMaster){
                    // 不是班主任角色
                    // 隐藏完成授课
                    mBtnGiveLessons.setVisibility(View.GONE);
                }else{
                    mBtnGiveLessons.setVisibility(View.VISIBLE);
                }
            }else{
                mBottomLayout.setVisibility(View.GONE);
            }
        }else{
            mBottomLayout.setVisibility(View.GONE);
            mTvTimeTable.setVisibility(View.GONE);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext())/* {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        }*/;
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new TimeLineItemDecoration(
                DisplayUtil.dip2px(getContext(),20),
                DisplayUtil.dip2px(getContext(),15),
                DisplayUtil.dip2px(getContext(),2),1));
        mAdapter = new PlanLiveAdapter(getActivity(), mRole, mClassDetailEntity,isParent,isJoin);
        mAdapter.setGiveFinish(mTabParams.isGiveFinish());
        mAdapter.setGiveHistory(mTabParams.isGiveHistory());
        mLuAdapter = new LuRecyclerViewAdapter(mAdapter);
        mRecycler.setAdapter(mLuAdapter);
        // 动态设置margin,预留课程表的高度
        if (getActivity() instanceof JoinClassDetailActivity && false) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecycler.getLayoutParams();
            layoutParams.bottomMargin = DisplayUtil.dip2px(UIUtil.getContext(), 80);
            mRecycler.setLayoutParams(layoutParams);
        }

        /*mRefreshLayout.setOnHeaderRefreshListener(view ->{
            if (!EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
                String classId = mClassDetailEntity.getData().get(0).getClassId();
                mPresenter.requestOnlineClassLiveData(schoolId, classId, 0);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(view -> {
            if (!EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
                String classId = mClassDetailEntity.getData().get(0).getClassId();
                mPresenter.requestOnlineClassLiveData(schoolId, classId, ++pageIndex);
            }
        });*/

        // 如果在初始化的时候设置false footerView 就被移除了
        // mRecycler.setLoadMoreEnabled(false);
        mRecycler.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
                    String classId = mClassDetailEntity.getData().get(0).getClassId();
                    mPresenter.requestOnlineClassLiveData(schoolId, classId, ++pageIndex);
                }

            }
        });

        // 已经加入的班级,可以打开授课计划
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LiveEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LiveEntity entity) {
                super.onItemClick(holder, entity);
                LiveVo vo = entity.build();

                if (EmptyUtil.isNotEmpty(vo)) {
                    int position = holder.getAdapterPosition();
                    // 第一个位置,并且是未加入
                    boolean isAudition = (position == 0 && !isJoin);
                    if (getActivity() instanceof JoinClassDetailActivity || isAudition) {
                        /*if(!UserHelper.isLogin()){
                            LoginHelper.enterLogin(getActivity());
                            return;
                        }*/
                        //如果当前用户学生判断当前的直播有没有加入我的直播中
                        mPresenter.requestJudgeJoinLive(vo, schoolId, vo.getClassId(), vo.getId(),isAudition);
                    }else{
                        UIUtil.showToastSafe(R.string.label_no_permission);
                    }
                }

            }
        });

        mAdapter.setNavigator(entity -> {
            // popDeleteCurrentClassDialog(entity);
            String memberId = UserHelper.getUserId();
            if (TextUtils.equals(memberId,entity.getAcCreateId())
                    && entity.getPublishClassList().size() > 1){
                popCanSelectDeleteWayDialog(entity);
            } else {
                popDeleteCurrentClassDialog(entity);
            }
            // 弹出对话框，提示是否删除直播
            /*CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(UIUtil.getString(R.string.label_delete_live_warning));
            builder.setTitle(UIUtil.getString(R.string.tip));
            builder.setPositiveButton(UIUtil.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 删除直播
                    mPresenter.requestDeleteLive(entity.getId(), entity.getClassId());
                }
            });

            builder.setNegativeButton(UIUtil.getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();*/
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        // 获取到班级信息
        if(EmptyUtil.isNotEmpty(mClassDetailEntity.getData())){
            String classId = mClassDetailEntity.getData().get(0).getClassId();
            mPresenter.requestLoadClassInfo(classId);
        }
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        mClassEntity = entity;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_new_class){
            // 新开课
            if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                    EmptyUtil.isEmpty(mClassDetailEntity.getData())){
                return;
            }

            if(EmptyUtil.isNotEmpty(mClassEntity)){
                // 直播创建跳转
                String classMailId = mClassEntity.getClassMailListId();
                String schoolId = mClassEntity.getSchoolId();
                String schoolName = mClassEntity.getSchoolName();
                String className = mClassEntity.getClassName();
                String classId = mClassEntity.getClassId();
                if(EmptyUtil.isEmpty(classMailId) ||
                        EmptyUtil.isEmpty(schoolId) ||
                        EmptyUtil.isEmpty(schoolName) ||
                        EmptyUtil.isEmpty(classId) ||
                        EmptyUtil.isEmpty(className)){
                    return;
                }
                LiveDetails.jumpToCreateLive(getContext(),schoolId,schoolName,className,classMailId,classId);
            }

        }else if(viewId == R.id.btn_complete_give_lessons){
            // 完成授课
            // UIUtil.showToastSafe(R.string.label_complete_give_lessons);
            // 调用完成授课的接口
            if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                    EmptyUtil.isEmpty(mClassDetailEntity.getData())){
                return;
            }
            popGiveLessons();
        }else if(viewId == R.id.tv_timetable){
            // 课程表
            if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                    EmptyUtil.isEmpty(mClassDetailEntity.getData()) ||
                    EmptyUtil.isEmpty(mClassEntity)){
                return;
            }

            ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);

            boolean isTeacher = OnlineClassRole.ROLE_TEACHER.equals(mRole);
            boolean isHeadMaster = UserHelper.getUserId().equals(dataBean.getCreateId());
            if(isParent){
                mRole = OnlineClassRole.ROLE_PARENT;
                isHeadMaster = false;
                isTeacher = false;
            }

            Intent intent = new Intent();
            intent.putExtra("schoolId",schoolId);
            intent.putExtra("classId",dataBean.getClassId());
            intent.putExtra("role_type",Integer.parseInt(mRole));
            intent.putExtra("isHeadMaster",isHeadMaster);
            intent.putExtra("isTeacher",isTeacher);
            intent.putExtra("isOpenAirClassLiveTable",true);
            intent.putExtra("id",mClassEntity.getClassMailListId());
            intent.putExtra("className",dataBean.getName());
            intent.putExtra("schoolName",dataBean.getOrganName());
            intent.putExtra("is_online_class_class",true);
            // 传参是否是结束授课
            intent.putExtra("is_histroy_class",mTabParams.isGiveHistory());
            // 是否是历史班
            intent.putExtra("is_finish_lecture",mTabParams.isGiveFinish());
            intent.setClassName(getContext().getPackageName(),
                    "com.galaxyschool.app.wawaschool.OpenCourseHelpActivity");
            startActivity(intent);
        }
    }

    @Override
    public void updateCompleteGiveView(boolean complete) {
        if(complete){
            // 通知给所有的前一个页面，刷新UI
            EventBus.getDefault().post(new EventWrapper(null, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT));
            // 完成授课
            getActivity().finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        String action = event.getUpdateAction();
        if("createOnlineSuccess".equals(action)){
            // 创建授课计划成功,成功拉取数据
            // onResume已经调用
        }
    }

    /**
     * 有多个发布对象，调用
     * @param data 直播实体
     */
    private void popCanSelectDeleteWayDialog(final LiveEntity data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                R.layout.layout_change_selected_icon,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (deleteAllClassRB != null && deleteAllClassRB.isChecked()){
                            //删除所有的班级
                            deleteCurrentItem(data, true);
                        } else{
                            //删除当前的班级
                            deleteCurrentItem(data, false);
                        }
                    }
                });
        messageDialog.show();
        deleteAllClassRB = (RadioButton) messageDialog.getContentView().findViewById(R.id.rb_all_Class);
    }

    /**
     * 完成授课
     */
    private void popGiveLessons(){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                UIUtil.getString(R.string.label_online_class_give_lessons_tip),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mPresenter.requestCompleteGive(mClassDetailEntity.getData().get(0).getId());
                    }
                });
        messageDialog.show();
    }

    /**
     * 删除直播 只有一个发布对象时候调用
     * @param data 只有一个直播实体
     */
    private void popDeleteCurrentClassDialog(final LiveEntity data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.label_confirm_delete_online, data.getTitle()),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteCurrentItem(data, false);
                    }
                });
        messageDialog.show();
    }

    /**
     * 删除当前直播
     * @param data 直播数据
     * @param deleteAll 是否从所有班级删除
     */
    private void deleteCurrentItem(final @NonNull LiveEntity data,boolean deleteAll){
        // 删除直播
        // clsssId是创建班的Id,传发布班级的Id
        String classId = mClassDetailEntity.getData().get(0).getClassId();
        mPresenter.requestDeleteLive(data.getId(), classId,deleteAll);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (!EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
            mRecycler.setNoMore(false);
            String classId = mClassDetailEntity.getData().get(0).getClassId();
            // schoolId = "bfbba4e6-c98a-4160-bca4-540087fb1d89";
            // classId = "91288f39-d6df-490f-a611-a6a601504ddc";
            pageIndex = 0;
            mPresenter.requestOnlineClassLiveData(schoolId, classId, pageIndex);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 进入授课计划详情后返回调用
        loadData();
    }

    @Override
    public void updateDeleteOnlineLiveView(boolean result) {
        if (result) {
            // 删除直播成功
            UIUtil.showToastSafe(R.string.label_delete_live_succeed);
            loadData();
        }else{
            // 删除直播失败
            // UIUtil.showToastSafe(R.string.label_delete_live_failed);
        }
    }

    @Override
    public void updateOnlineClassLiveView(@NonNull List<LiveEntity> entities) {
        if(getActivity() instanceof BaseClassDetailActivity) {
            BaseClassDetailActivity parentActivity = (BaseClassDetailActivity) getActivity();
            parentActivity.getRefreshLayout().setRefreshing(false);
        }
        // 对集合进行反转
        Collections.reverse(entities);
        mAdapter.replace(entities);
        /*mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        // 因为排序的问题,所以不再支持分页
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);*/
    }

    @Override
    public void updateOnlineClassMoreLiveView(@NonNull List<LiveEntity> entities) {

        mAdapter.add(entities);
        // mRecycler.setLoadMoreEnabled(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
        // 如果totalCount == visibleCount 会发生UI错误
        mRecycler.refreshComplete(AppConfig.PAGE_SIZE);
        if(entities.size() < AppConfig.PAGE_SIZE){
            // 加载更多，并且没有更多数据了
            mRecycler.setNoMore(true);
        }
        /*mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);*/
    }

    @Override
    public void updateJudgeLiveView(@NonNull LiveVo vo, boolean result,boolean isAudition) {
        if (!EmptyUtil.isEmpty(mClassDetailEntity.getData())) {
            ClassDetailEntity.DataBean data = mClassDetailEntity.getData().get(0);
            String classId = data.getClassId();
            // 因为该直播的classId不一定是从在线课堂发布的,所以要动态更新classId对象
            vo.setClassId(classId);
            boolean isHeadMaster = UserHelper.getUserId().equals(data.getCreateId());
            boolean isGiveFinish = mTabParams.isGiveFinish();
            // 是否是历史班
            boolean isGiveHistory = mTabParams.isGiveHistory();
            // 是否授课结束
            boolean isFinishResult = isGiveFinish;
            boolean isHistoryResult = isGiveHistory || isAudition;

            if(isHistoryResult) isFinishResult = false;
            String curMemberId = UserHelper.getUserId();
            String liveRole = mRole;
            boolean isFromMyLive = false;
            boolean isMyCourseChildOnline = false;
            if(isParent){
                liveRole = OnlineClassRole.ROLE_PARENT;
                curMemberId = mTabParams.getChildMemberId();
                isMyCourseChildOnline = true;
                isFromMyLive = true;
                isHeadMaster = false;

                if(EmptyUtil.isEmpty(curMemberId)){
                    return;
                }
            }

            // com.lqwawa.intleducation.common.utils.LogUtil.e(ClassPlanFragment.class,JSON.toJSONString(vo));
            LiveDetails.jumpToAirClassroomLiveDetails(getActivity(), vo,
                    curMemberId, liveRole, true,
                    isHeadMaster, isFromMyLive, result,
                    classId,isMyCourseChildOnline,isFinishResult,isHistoryResult,false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 课堂简介显示
        Activity activity = getActivity();
        if (activity instanceof ClassDetailNavigator) {
            ClassDetailNavigator navigator = (ClassDetailNavigator) activity;
            navigator.onCommentChanged(getUserVisibleHint());
        }

        if(activity instanceof JoinClassDetailNavigator){
            JoinClassDetailNavigator navigator = (JoinClassDetailNavigator) activity;
            navigator.updateCommentVisibility(getUserVisibleHint());
        }

        if(getUserVisibleHint()){
            // 显示了课堂简介
            if(getActivity() instanceof BaseClassDetailActivity){
                BaseClassDetailActivity parentActivity = (BaseClassDetailActivity) getActivity();
                parentActivity.addRefreshView(mRecycler);
                parentActivity.getRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadData();
                    }
                });
            }
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
