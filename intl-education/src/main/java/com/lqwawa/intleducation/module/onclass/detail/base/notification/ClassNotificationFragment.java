package com.lqwawa.intleducation.module.onclass.detail.base.notification;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.base.introduction.ClassIntroductionFragment;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂详情的通知Fragment
 * @date 2018/06/04 14:03
 * @history v1.0
 * **********************************
 */
public class ClassNotificationFragment extends PresenterFragment<ClassNotificationContract.Presenter>
    implements ClassNotificationContract.View, View.OnClickListener{

    private static final String KEY_EXTRA_ENTITY = "KEY_EXTRA_ENTITY";
    private static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private ClassNotificationAdapter mAdapter;
    private TextView mTvNewCreate;

    private OnlineTabParams mTabParams;
    private ClassDetailEntity mClassDetailEntity;
    private String classId;
    private String mRole;
    // 是否是家长身份
    private boolean isParent;
    // 当前页码
    private int pageIndex;

    public static Fragment newInstance(@NonNull OnlineTabParams params){
        ClassNotificationFragment fragment = new ClassNotificationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ClassNotificationContract.Presenter initPresenter() {
        return new ClassNotificationPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_class_notification;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTabParams = (OnlineTabParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mTabParams)) return false;
        mClassDetailEntity = mTabParams.getDetailEntity();
        mRole = mTabParams.getRole();
        isParent = mTabParams.isParent();
        if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                EmptyUtil.isEmpty(mRole)){
            return false;
        }

        if(EmptyUtil.isEmpty(mClassDetailEntity.getData())){
            return false;
        }

        classId = mClassDetailEntity.getData().get(0).getClassId();
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mTvNewCreate = (TextView) mRootView.findViewById(R.id.tv_new_create);
        mTvNewCreate.setOnClickListener(this);

        /*if(getActivity() instanceof JoinClassDetailActivity){

        }else{
            mTvNewCreate.setVisibility(View.GONE);
        }*/

        boolean isTeacher = OnlineClassRole.ROLE_TEACHER.equals(mRole);
        if(!isParent && isTeacher && !mTabParams.isGiveFinish() && !mTabParams.isGiveHistory()){
            // 优先家长身份判断
            // 老师身份
            // 不能是授课结束或历史班
            mTvNewCreate.setVisibility(View.VISIBLE);
        }else{
            mTvNewCreate.setVisibility(View.GONE);
        }

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new ClassNotificationAdapter(getActivity(),mRole,mClassDetailEntity);
        mAdapter.setGiveFinish(mTabParams.isGiveFinish());
        mAdapter.setGiveHistory(mTabParams.isGiveHistory());
        mAdapter.setParent(isParent);
        mRecycler.setAdapter(mAdapter);

        mRefreshLayout.setOnHeaderRefreshListener(view->{
            // 下拉刷新
            mPresenter.requestNotificationData(classId,0);
        });

        mRefreshLayout.setOnFooterRefreshListener(view->{
            // 加载更多
            mPresenter.requestNotificationData(classId,++pageIndex);
        });

        // 动态设置margin,预留课程表的高度
        if(getActivity() instanceof JoinClassDetailActivity){
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecycler.getLayoutParams();
            layoutParams.bottomMargin = DisplayUtil.dip2px(UIUtil.getContext(),80);
            mRecycler.setLayoutParams(layoutParams);
        }

        mAdapter.setNavigator(entity -> {
            // 删除通知
            // 弹出对话框，提示是否删除直播
            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(UIUtil.getString(R.string.label_delete_notification_warning));
            builder.setTitle(UIUtil.getString(R.string.tip));
            builder.setPositiveButton(UIUtil.getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 删除直播
                            // @date   :2018/6/7 0007 下午 6:10
                            // @func   :entity.getType 返回总是0 所以写死2
                            mPresenter.requestDeleteNotification(entity.getId(),2);
                        }
                    });

            builder.setNegativeButton(UIUtil.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<ClassNotificationEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, ClassNotificationEntity entity) {
                super.onItemClick(holder, entity);
                // 进入通知详情
                Intent intent=new Intent();
                intent.putExtra("isOpenNote",true);
                intent.putExtra("courseData",entity);
                intent.setClassName(getActivity().getPackageName(),
                        "com.galaxyschool.app.wawaschool.OpenCourseHelpActivity");
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        pageIndex = 0;
        mPresenter.requestNotificationData(classId,pageIndex);
    }

    @Override
    public void updateNotificationView(@NonNull List<ClassNotificationEntity> entities) {
        mAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
    }

    @Override
    public void updateMoreNotificationView(@NonNull List<ClassNotificationEntity> entities) {
        mAdapter.add(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
    }

    @Override
    public void updateDeleteNotificationView(boolean result) {
        loadData();
        UIUtil.showToastSafe(R.string.label_delete_notification_succeed);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_new_create){
            if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                    EmptyUtil.isEmpty(mClassDetailEntity.getData())){
                return;
            }

            String schoolId = mClassDetailEntity.getData().get(0).getOrganId();
            // 新建通知
            Intent intent = new Intent();
            intent.setClassName(getContext().getPackageName(),"com.galaxyschool.app.wawaschool" + ".OpenCourseHelpActivity");
            Bundle args = new Bundle();
            args.putBoolean("isTeachingPlanNotice",true);
            //schoolId
            args.putString("schoolId",schoolId);
            //classId
            args.putString("classId",classId);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        String action = event.getUpdateAction();
        if("createTeachingPlanNoticeSuccess".equals(action)){
            // 创建授课计划成功,成功拉取数据
            // onResume已经调用
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
