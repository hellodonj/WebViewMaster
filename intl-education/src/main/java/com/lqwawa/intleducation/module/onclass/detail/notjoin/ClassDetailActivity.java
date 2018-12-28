package com.lqwawa.intleducation.module.onclass.detail.notjoin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.TimeUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.ConfirmOrderActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayCourseDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayDialogNavigator;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.base.ClassDetailParams;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.base.comment.ClassCommentFragment;
import com.lqwawa.intleducation.module.onclass.detail.base.introduction.ClassIntroductionFragment;
import com.lqwawa.intleducation.module.onclass.detail.base.plan.ClassPlanFragment;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseListFragment;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.tools.DensityUtils;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrMedici
 * @desc 在线课堂班级详情，未加入
 */
public class ClassDetailActivity extends BaseClassDetailActivity<ClassDetailContract.Presenter>
        implements ClassDetailContract.View ,ClassDetailNavigator{

    public static final String LQWAWA_PAY_RESULT_ACTION = "android.lqwawa.action.payresult";

    @Override
    protected ClassDetailContract.Presenter initPresenter() {
        return new ClassDetailPresenter(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mBtnPay.setVisibility(View.VISIBLE);
        mBtnPay.setOnClickListener(v -> payClass());

        // setToolBar();
    }

    /*protected void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar);
        toolbar.setNavigationIcon(null);
        mToolbar.setBackgroundColor(Color.parseColor("#77545454"));
        Button leftButton = (Button) findViewById(R.id.btn_toolbar_left);
        Button rightButton = (Button) findViewById(R.id.btn_toolbar_right);
        setToolbarButton(leftButton, R.drawable.ic_back, 20);
        setToolbarButton(rightButton, R.drawable.all_classify, 32);
        if (leftButton != null) {
            leftButton.setOnClickListener(v -> finish());
        }
        if (rightButton != null) {
            rightButton.setOnClickListener(v -> showPop(v));
        }
    }*/

    private void setToolbarButton(Button button, int resId, int iconSize) {
        if (button != null) {
            ViewGroup.LayoutParams linearParams = button.getLayoutParams();
            linearParams.height = DensityUtils.dp2px(this, iconSize);
            linearParams.width = DensityUtils.dp2px(this, iconSize);
            button.setLayoutParams(linearParams);
            button.setBackgroundResource(resId);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        registerBroadcast();
    }

    @Override
    public List<EntryBean> assembleMenuData() {
        final List<EntryBean> items = new ArrayList<>();

        if (!isParent && mRole.equals(OnlineClassRole.ROLE_TEACHER)) {
            // 优先家长，不是老师就显示班级详情
            EntryBean menuBean = new EntryBean(R.drawable.ic_menu_menu, getString(R.string.label_class_detail), TYPE_MENU_MENU);
            items.add(menuBean);
        }

        // 判断是否是班主任
        ClassDetailEntity.DataBean data = mCurrentEntity.getData().get(0);
        boolean isHeadMaster = UserHelper.getUserId().equals(data.getCreateId());
        if(!isGiveHistory && isHeadMaster){
            // 不是历史班且是班主任,添加历史班功能
            EntryBean shareBean = new EntryBean(R.drawable.ic_history_course, getString(R.string.label_history_setting), TYPE_MENU_HISTORY);
            items.add(shareBean);
        }

        if(/*!isGiveFinish && */!isGiveHistory){
            // 完成授课的班级可以分享
            EntryBean shareBean = new EntryBean(R.drawable.ic_menu_share, getString(R.string.label_share), TYPE_MENU_SHARE);
            items.add(shareBean);
        }
        return items;
    }

    /**
     * 报班
     */
    private void payClass() {
        if (EmptyUtil.isEmpty(mCurrentEntity) || EmptyUtil.isEmpty(mCurrentEntity.getData())) {
            return;
        }

        // 判断有无登录
        if (!UserHelper.isLogin()) {
            LoginHelper.enterLogin(this);
            return;
        }

        List<ClassDetailEntity.DataBean> data = mCurrentEntity.getData();
        ClassDetailEntity.DataBean dataBean = data.get(0);
        if (dataBean.getPrice() == 0) {
            // 免费的在线课堂
            mPresenter.joinInOnlineGratisClass(mId);
        } else {
            // 到订单支付页面
            // 弹框
            String teacherIds = dataBean.getTeachersId();
            PayCourseDialogFragment.show(getSupportFragmentManager(),null,teacherIds,mId,PayCourseDialogFragment.TYPE_CLASS, new PayDialogNavigator() {
                @Override
                public void onChoiceConfirm(@NonNull String curMemberId) {

                    // 发送获取班级详情细信息的请求
                    OnlineCourseHelper.loadOnlineClassInfo(curMemberId, mClassId, new DataSource.Callback<JoinClassEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    UIUtil.showToastSafe(strRes);
                                }

                                @Override
                                public void onDataLoaded(JoinClassEntity joinClassEntity) {
                                    boolean isJoin = joinClassEntity.isIsInClass();
                                    if(!isJoin){
                                        // 判断为谁购买
                                        ConfirmOrderActivity.start(ClassDetailActivity.this, mCurrentEntity,curMemberId);
                                    }else{
                                        UIUtil.showToastSafe(R.string.label_online_member_in_class_warning);
                                    }
                                }
                    });
                }
            });
        }
    }

    @Override
    public void updateJoinOnlineGratisClass(boolean result) {
        if (result) {
            // 参加在线班级成功 不需要发送粘性的事件
            EventBus.getDefault().post(new EventWrapper(null, EventConstant.JOIN_IN_CLASS_EVENT));
            // 参加免费课程成功
            /*if (isCourseEnter && !isSchoolEnter) {
                // 从LQ学程进来的，但不是从机构主页进来的
                JoinClassDetailActivity.show(this, mClassId, mSchoolId, mId, isCourseEnter, mRole);
            } else if (!isCourseEnter && isSchoolEnter) {
                // 从机构主页进来的，但不是从LQ学程进来的
                JoinClassDetailActivity.show(this, mClassId, mSchoolId, mId, mRole, isSchoolEnter);
            } else if (!isCourseEnter && !isSchoolEnter) {
                // 既不是机构主页，又不是从LQ学程过来的  在线学习
                JoinClassDetailActivity.show(this, mClassId, mSchoolId, mId, mRole, false);
            }*/
            ClassDetailParams classParams = new ClassDetailParams(mJoinClassEntity,mId,mRole);
            classParams.setCourseEnter(isCourseEnter);
            classParams.setSchoolEnter(isSchoolEnter);
            classParams.setGiveFinish(isGiveFinish);
            classParams.setGiveHistory(isGiveHistory);
            JoinClassDetailActivity.show(this,classParams);
            finish();
        }
    }

    @Override
    public void updateClassDetailView(boolean refreshHeader, @NonNull ClassDetailEntity entity) {
        super.updateClassDetailView(refreshHeader,entity);
        if (!refreshHeader) {
            // 生成Fragment数组
            List<Fragment> fragments = new ArrayList<>();
            OnlineTabParams params = new OnlineTabParams(entity,mSchoolId,mRole,false);
            params.setCourseEnter(isCourseEnter);
            params.setGiveFinish(isGiveFinish);
            params.setGiveHistory(isGiveHistory);
            params.setParent(isParent,childMemberId);

            fragments.add(ClassIntroductionFragment.newInstance(params));
            fragments.add(ClassPlanFragment.newInstance(params));
            fragments.add(ClassCommentFragment.newInstance(params));

            // 点击关联课程
            RelatedCourseParams relatedParams = new RelatedCourseParams(mCurrentEntity.getParam(),mCurrentEntity.getRelatedCourse());
            fragments.add(RelatedCourseListFragment.newInstance(relatedParams));

            ClassDetailPagerAdapter mPagerAdapter = new ClassDetailPagerAdapter(getSupportFragmentManager(), fragments);
            mTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setAdapter(mPagerAdapter);
            // mViewPager.setOffscreenPageLimit(fragments.size());

            // 设置显示第二个Tab 授课计划
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onCommentChanged(boolean hidden) {
        if (hidden) {
            // 评论区域隐藏,显示购买布局
        } else {
            // 评论区域隐藏,隐藏购买布局
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConfirmOrderActivity.Rc_pay) {
            initData();
        }

        if (requestCode == LoginHelper.RS_LOGIN) {
            if (UserHelper.isLogin()) {
                // 登录成功 刷新UI
                mPresenter.requestLoadClassInfo(mClassId);
            }
        }
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        hideLoading();
        // 非空判断
        if (EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentEntity)) {
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String roles = entity.getRoles();
        String role = UserHelper.getOnlineRoleWithUserRoles(roles);
        // 进行role的更新
        mRole = role;
        if (needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)) {
            // 已经加入班级 或者是老师身份
            /*if (isCourseEnter && !isSchoolEnter) {
                // 从LQ学程进来的，但不是从机构主页进来的
                JoinClassDetailActivity.show(this, mClassId, mSchoolId, mId, isCourseEnter, mRole);
            } else if (!isCourseEnter && isSchoolEnter) {
                // 从机构主页进来的，但不是从LQ学程进来的
                JoinClassDetailActivity.show(this, mClassId, mSchoolId, mId, mRole, isSchoolEnter);
            } else if (!isCourseEnter && !isSchoolEnter) {
                // 既不是机构主页，又不是从LQ学程过来的  在线学习
                JoinClassDetailActivity.show(this, mClassId, mSchoolId, mId, mRole, false);
            }*/

            ClassDetailParams classParams = new ClassDetailParams(mJoinClassEntity,mId,mRole);
            classParams.setCourseEnter(isCourseEnter);
            classParams.setSchoolEnter(isSchoolEnter);
            classParams.setGiveFinish(isGiveFinish);
            JoinClassDetailActivity.show(this,classParams);

            finish();
        } else {
            // 未加入班级
            // 刷新UI
            mPresenter.requestClassDetail(mId, false);
            mPresenter.requestSchoolInfo(mSchoolId);
        }
    }

    @Override
    protected void setHistoryClass() {
        // 设成历史班
        // 未加入不需要实现
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LQWAWA_PAY_RESULT_ACTION);
        mManager.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 取消注册广播
     */
    private void unRegisterBroadcast() {
        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
        mManager.unregisterReceiver(mReceiver);
    }


    /**
     * 购买的课程,收到购买成功的广播,进入到课程详情页
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LQWAWA_PAY_RESULT_ACTION.equals(action)) {
                // 收到收费课程购买成功的回调
                // JoinClassDetailActivity.show(ClassDetailActivity.this,classId,schoolId,id,mRole);
                finish();
            }
        }
    };

    /**
     * 未加入班级详情页入口
     *
     * @param context       上下文对象
     * @param classId       班级Id
     * @param id            在线课堂id
     * @param role          角色信息
     * @param isSchoolEnter 是否从机构主页进入
     */
    public static void show(@NonNull Context context,
                            @NonNull String classId,
                            @NonNull String schoolId,
                            int id,
                            @NonNull @OnlineClassRole.RoleRes String role,
                            boolean isSchoolEnter) {
        Intent intent = new Intent(context, ClassDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putString(KEY_EXTRA_CLASS_ID, classId);
        bundle.putInt(KEY_EXTRA_ID, id);
        bundle.putString(KEY_EXTRA_ROLE, role);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER, isSchoolEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 供内部跳转调用，跳转至未加入在线班级
     * @param context 上下文对象
     * @param params 重要参数
     */
    public static void show(@NonNull Context context,@NonNull ClassDetailParams params){
        Intent intent = new Intent(context, ClassDetailActivity.class);
        if(params.isPushEnter()){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 新的班级详情入口，会判断用户在该班级的加入情况
     * @param context 上下文对象
     * @param params 重要参数
     */
    public static void show(@NonNull final Context context,@NonNull ClassInfoParams params){
        String memberId = UserHelper.getUserId();
        final OnlineClassEntity entity = params.getClassEntity();
        if(EmptyUtil.isEmpty(entity)) return;
        String classId = entity.getClassId();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, classId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                if(EmptyUtil.isEmpty(joinClassEntity)) return;
                // 是否已经参加该班级
                boolean needToJoin = joinClassEntity.isIsInClass();
                // 获取角色信息
                String roles = joinClassEntity.getRoles();
                String role = UserHelper.getOnlineRoleWithUserRoles(roles);
                int id = entity.getId();
                ClassDetailParams classParams = new ClassDetailParams(joinClassEntity,id,role);
                classParams.setCourseEnter(params.isCourseEnter());
                classParams.setSchoolEnter(params.isSchoolEnter());
                classParams.setGiveFinish(entity.isGiveFinish());
                classParams.setGiveHistory(entity.isGiveHistory());
                classParams.setParent(params.isParent(),params.getChildMemberId());
                params.setPushEnter(params.isPushEnter());
                params.setHome(params.isHome());

                if(needToJoin || params.isParent() || OnlineClassRole.ROLE_TEACHER.equals(role)){
                    // 家长身份
                    // 已经加入班级 或者是老师身份 引入已加入班级
                    JoinClassDetailActivity.show(context,classParams);
                }else{
                    // 进入未加入班级
                    ClassDetailActivity.show(context,classParams);
                }
            }
        });
    }

    /**
     * 精简的在线课堂入口
     * @param context 上下文对象
     * @param classId 班级ID
     */
    public static void show(@NonNull final Context context,@NonNull final String classId){
        show(context,classId,false,false);
        /*OnlineCourseHelper.requestOnlineIdByClassId(classId, new DataSource.Callback<Integer>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(Integer integer) {
                OnlineClassEntity entity = new OnlineClassEntity();
                entity.setClassId(classId);
                entity.setId(integer);
                ClassInfoParams params = new ClassInfoParams(entity);
                ClassDetailActivity.show(context,params);
            }
        });*/
    }

    /**
     * 精简的在线课堂入口
     * 推送广播进入
     * @param context 上下文对象
     * @param classId 班级ID 直播跳转调用
     */
    public static void show(@NonNull final Context context,@NonNull final String classId,boolean pushEnter,boolean isHome){
        OnlineCourseHelper.requestOnlineIdByClassId(classId, new DataSource.Callback<Integer>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(Integer integer) {
                OnlineClassEntity entity = new OnlineClassEntity();
                entity.setClassId(classId);
                entity.setId(integer);
                ClassInfoParams params = new ClassInfoParams(entity);
                params.setPushEnter(pushEnter);
                params.setHome(isHome);
                ClassDetailActivity.show(context,params);
            }
        });
    }

    /**
     * 未加入班级详情页入口
     *
     * @param context       上下文对象
     * @param classId       班级Id
     * @param id            在线课堂id
     * @param role          角色信息
     * @param isCourseEnter 是否从学程入口进入
     */
    public static void show(@NonNull Context context,
                            @NonNull String classId,
                            @NonNull String schoolId,
                            int id, boolean isCourseEnter,
                            @NonNull @OnlineClassRole.RoleRes String role) {
        Intent intent = new Intent(context, ClassDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putString(KEY_EXTRA_CLASS_ID, classId);
        bundle.putInt(KEY_EXTRA_ID, id);
        bundle.putString(KEY_EXTRA_ROLE, role);
        bundle.putBoolean(KEY_EXTRA_IS_COURSE_ENTER, isCourseEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private class ClassDetailPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public ClassDetailPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTexts[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }
}
