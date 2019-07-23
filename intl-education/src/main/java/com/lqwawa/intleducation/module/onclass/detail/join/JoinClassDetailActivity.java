package com.lqwawa.intleducation.module.onclass.detail.join;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.base.ClassDetailParams;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.base.comment.ClassCommentFragment;
import com.lqwawa.intleducation.module.onclass.detail.base.introduction.ClassIntroductionFragment;
import com.lqwawa.intleducation.module.onclass.detail.base.notification.ClassNotificationActivity;
import com.lqwawa.intleducation.module.onclass.detail.base.plan.ClassPlanFragment;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseListFragment;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.tools.ScreenUtils;
import com.osastudio.common.popmenu.EntryBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrMedici
 * @desc 已经加入班级详情页
 */
public class JoinClassDetailActivity extends BaseClassDetailActivity<JoinClassDetailContract.Presenter>
        implements JoinClassDetailContract.View, JoinClassDetailNavigator {

    // 课程表
    private TextView mTvTimeTable;

    @Override
    protected JoinClassDetailContract.Presenter initPresenter() {
        return new JoinClassDetailPresenter(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        // 设置公告显示
        mNoticeLayout.setVisibility(View.VISIBLE);
        mTvTimeTable = (TextView) findViewById(R.id.tv_timetable);
        mClassDetailBottomLayout.setVisibility(View.GONE);

        mNoticeLayout.setOnClickListener(v -> {
            OnlineTabParams params = new OnlineTabParams(mCurrentEntity, mSchoolId, mRole, true);
            params.setCourseEnter(isCourseEnter);
            params.setGiveFinish(isGiveFinish);
            params.setGiveHistory(isGiveHistory);
            params.setParent(isParent, childMemberId);
            ClassNotificationActivity.show(JoinClassDetailActivity.this, params);
        });

        mTvTimeTable.setOnClickListener(v -> {
            if (EmptyUtil.isEmpty(mCurrentEntity) && EmptyUtil.isEmpty(mCurrentEntity.getData())) {
                return;
            }

            ClassDetailEntity.DataBean dataBean = mCurrentEntity.getData().get(0);

            boolean isTeacher = OnlineClassRole.ROLE_TEACHER.equals(mRole);
            boolean isHeadMaster = UserHelper.getUserId().equals(dataBean.getCreateId());
            Intent intent = new Intent();
            intent.putExtra("schoolId", mSchoolId);
            intent.putExtra("classId", mClassId);
            intent.putExtra("role_type", Integer.parseInt(mRole));
            intent.putExtra("isHeadMaster", isHeadMaster);
            intent.putExtra("isTeacher", isTeacher);
            intent.putExtra("isOpenAirClassLiveTable", true);
            intent.setClassName(getPackageName(),
                    "com.galaxyschool.app.wawaschool.OpenCourseHelpActivity");
            startActivity(intent);
        });

        if (isSchoolEnter || true) {
            mSchoolEnter.setVisibility(View.GONE);
            mSchoolName.setEnabled(false);
        }
        mBtnCreateClass.setOnClickListener(view -> {
            // 新开课
            if (EmptyUtil.isEmpty(mCurrentEntity) ||
                    EmptyUtil.isEmpty(mCurrentEntity.getData())) {
                return;
            }
            if (EmptyUtil.isNotEmpty(mJoinClassEntity)) {
                // 直播创建跳转
                String classMailId = mJoinClassEntity.getClassMailListId();
                String schoolId = mJoinClassEntity.getSchoolId();
                String schoolName = mJoinClassEntity.getSchoolName();
                String className = mJoinClassEntity.getClassName();
                String classId = mJoinClassEntity.getClassId();
                if (EmptyUtil.isEmpty(classMailId) ||
                        EmptyUtil.isEmpty(schoolId) ||
                        EmptyUtil.isEmpty(schoolName) ||
                        EmptyUtil.isEmpty(classId) ||
                        EmptyUtil.isEmpty(className)) {
                    return;
                }
                LiveDetails.jumpToCreateLive(JoinClassDetailActivity.this, schoolId, schoolName,
                        className, classMailId, classId);
            }
        });

        mBtnGiveLessons.setOnClickListener(view -> {
            if(EmptyUtil.isEmpty(mCurrentEntity) ||
                    EmptyUtil.isEmpty(mCurrentEntity.getData())){
                return;
            }
            popGiveLessons();
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestNotificationData(mClassId, 0);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        // 处理班级详情来的消息
        String memberId = UserHelper.getUserId();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, mClassId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                showError(strRes);
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                if (EmptyUtil.isEmpty(joinClassEntity)) return;
                String roles = joinClassEntity.getRoles();
                // 更新Role信息
                mRole = UserHelper.getOnlineRoleWithUserRoles(roles);
                // 只是刷新，不创建新的片段
                mPresenter.requestClassDetail(mId, true);
                mPresenter.requestSchoolInfo(mSchoolId);
            }
        });
        // 更新通知
        mPresenter.requestNotificationData(mClassId, 0);
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
        if (!isGiveHistory && isHeadMaster && !isParent) {
            // 不是历史班且是班主任,并且不是家长身份入口,添加历史班功能
            EntryBean shareBean = new EntryBean(R.drawable.ic_history_course, getString(R.string.label_history_setting), TYPE_MENU_HISTORY);
            items.add(shareBean);
        }

        if (/*!isGiveFinish && */!isGiveHistory) {
            // 历史班不显示分享
            // 完成授课的班级可以分享
            EntryBean shareBean = new EntryBean(R.drawable.ic_menu_share, getString(R.string.label_share), TYPE_MENU_SHARE);
            items.add(shareBean);
        }
        return items;
    }

    @Override
    public void updateClassDetailView(boolean refreshHeader, @NonNull ClassDetailEntity entity) {
        super.updateClassDetailView(refreshHeader, entity);

        if (!refreshHeader) {
            // 生成Fragment数组
            List<Fragment> fragments = new ArrayList<>();

            OnlineTabParams params = new OnlineTabParams(entity, mSchoolId, mRole, true);
            params.setCourseEnter(isCourseEnter);
            params.setGiveFinish(isGiveFinish);
            params.setGiveHistory(isGiveHistory);
            params.setParent(isParent, childMemberId);

            fragments.add(ClassIntroductionFragment.newInstance(params));

            if (EmptyUtil.isNotEmpty(entity.getData())) {

                ClassDetailEntity.DataBean dataBean = entity.getData().get(0);
                if (OnlineClassRole.ROLE_TEACHER.equals(mRole) &&
                        !UserHelper.getUserId().equals(dataBean.getCreateId())) {
                    // 如果是老师,但仅仅是授课老师,那么等同于学生处理，不允许有删除权限
                    fragments.add(ClassPlanFragment.newInstance(params));
                } else {
                    fragments.add(ClassPlanFragment.newInstance(params));
                }
            }

            fragments.add(ClassCommentFragment.newInstance(params));

            // 点击关联课程
            RelatedCourseParams relatedParams = new RelatedCourseParams(mCurrentEntity.getParam(), mCurrentEntity.getRelatedCourse());
            fragments.add(RelatedCourseListFragment.newInstance(relatedParams));
            // fragments.add(ClassNotificationFragment.newInstance(params));

            ClassDetailPagerAdapter mPagerAdapter = new ClassDetailPagerAdapter(getSupportFragmentManager(), fragments);
            mTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setAdapter(mPagerAdapter);
            // mViewPager.setOffscreenPageLimit(fragments.size());

            // 设置显示第二个Tab 授课计划
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void setHistoryClass() {
        popGiveHistory();
    }

    /**
     * 完成授课
     */
    private void popGiveLessons(){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                JoinClassDetailActivity.this,
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
                        mPresenter.requestCompleteGive(mCurrentEntity.getData().get(0).getId());
                    }
                });
        messageDialog.show();
    }

    /**
     * 完成授课
     */
    private void popGiveHistory() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                this,
                null,
                UIUtil.getString(R.string.label_online_class_give_history_tip),
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
                        mPresenter.requestSettingHistory(mId);
                    }
                });
        messageDialog.show();
    }

    @Override
    public void updateSettingHistory(boolean complete) {
        if (complete) {
            // 通知给所有的前一个页面，刷新UI
            // 设置成历史班
            EventBus.getDefault().post(new EventWrapper(null, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT));
            // 完成授课
            finish();
        }
    }

    @Override
    public void updateCompleteGiveView(boolean complete) {
        if(complete){
            // 通知给所有的前一个页面，刷新UI
            EventBus.getDefault().post(new EventWrapper(null, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT));
            // 完成授课
            finish();
        }
    }

    @Override
    public void updateNotificationView(@NonNull List<ClassNotificationEntity> entities) {
        // 设置通知
        ClassNotificationEntity entity = entities.get(0);
        StringUtil.fillSafeTextView(mNoticeText, entity.getTitle());
    }

    @Override
    public void updateCommentVisibility(boolean hidden) {
        if (hidden) {
            // 隐藏,显示课程表
            // 全部都隐藏课程表
            mTvTimeTable.setVisibility(View.GONE);
            updateJoinClassDetailBottomLayout();
            mCommentLayout.setVisibility(View.GONE);
        } else {
            // 显示，隐藏课程表
            mTvTimeTable.setVisibility(View.GONE);
            mJoinClassDetailBottomLayout.setVisibility(View.GONE);
            mCommentLayout.setVisibility(View.VISIBLE);
        }

        boolean isHide =
                mJoinClassDetailBottomLayout.getVisibility() == View.GONE
                        && mCommentLayout.getVisibility() == View.GONE;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mCoordinatorLayout.getLayoutParams();
        layoutParams.bottomMargin = isHide ? 0 :
                getResources().getDimensionPixelSize(R.dimen.class_detail_bottom_height);
        mCoordinatorLayout.setLayoutParams(layoutParams);
    }

    private void updateJoinClassDetailBottomLayout() {
        mJoinClassDetailBottomLayout.setVisibility(View.GONE);
        boolean isTeacher = OnlineClassRole.ROLE_TEACHER.equals(mRole);
        if(!isParent && isTeacher && !isGiveFinish && !isGiveHistory){
            // 不是家长身份
            // 是老师身份，并且并没有授课完成
            // 授课完成了，班主任自然不能再完成授课，老师也不能新开课
            mBtnCreateClass.setVisibility(View.VISIBLE);
            mJoinClassDetailBottomLayout.setVisibility(View.VISIBLE);

            if (EmptyUtil.isEmpty(mCurrentEntity)) {
                return;
            }
            // 判断是否是班主任
            ClassDetailEntity.DataBean data = mCurrentEntity.getData().get(0);
            boolean isHeadMaster = UserHelper.getUserId().equals(data.getCreateId());
            if(!isHeadMaster){
                // 不是班主任角色
                // 隐藏完成授课
                mBtnGiveLessons.setVisibility(View.GONE);
            }else{
                mBtnGiveLessons.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 已加入班级详情页入口
     *
     * @param context       上下文对象
     * @param classId       班级Id
     * @param id            在线课堂id
     * @param role          角色信息
     * @param isSchoolEnter 机构主页进来的
     */
    public static void show(@NonNull Context context,
                            @NonNull String classId,
                            @NonNull String schoolId,
                            int id,
                            @NonNull @OnlineClassRole.RoleRes String role,
                            boolean isSchoolEnter) {
        Intent intent = new Intent(context, JoinClassDetailActivity.class);
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
     *
     * @param context 上下文对象
     * @param params  重要参数
     */
    public static void show(@NonNull Context context, @NonNull ClassDetailParams params) {
        Intent intent = new Intent(context, JoinClassDetailActivity.class);
        if (params.isPushEnter()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 已加入班级详情页入口
     *
     * @param context       上下文对象
     * @param classId       班级Id
     * @param id            在线课堂id
     * @param role          角色信息
     * @param isCourseEnter 学程入口
     */
    public static void show(@NonNull Context context,
                            @NonNull String classId,
                            @NonNull String schoolId,
                            int id,
                            boolean isCourseEnter,
                            @NonNull @OnlineClassRole.RoleRes String role) {
        Intent intent = new Intent(context, JoinClassDetailActivity.class);
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
}
