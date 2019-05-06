package com.lqwawa.intleducation.module.onclass.school;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.DiscoveryHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseNavigator;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.onclass.teacherlist.SchoolTeacherListActivity;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构信息片段 课程，在线课堂，老师相关信息显示
 * @date 2018/06/05 10:33
 * @history v1.0
 * **********************************
 */
public class SchoolInfoFragment extends PresenterFragment<SchoolInfoContract.Presenter>
        implements SchoolInfoContract.View {

    private static final String KEY_EXTRA_SCHOOL_NAME = "KEY_EXTRA_SCHOOL_NAME";
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_SCHOOL_LOGO = "KEY_EXTRA_SCHOOL_LOGO";
    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    private static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";

    private static SchoolInfoFragment INSTANCE = null;

    private LinearLayout mTitleLayout;
    // 课程
    private DiscoveryHolder mDiscoveryHolder;
    private RecyclerView mRecycler;
    private OnlineClassAdapter mClassAdapter;

    private LinearLayout mTeacherLayout;
    private RecyclerView mTeacherRecycler;
    private SchoolTeacherAdapter mTeacherAdapter;

    // 当前点击的在线课堂班级
    private OnlineClassEntity mCurrentClickEntity;
    private String mSchoolId;
    private String mSchoolName;
    private String mLogoUrl;
    // 是否是在线课堂跳转
    private boolean isSchoolEnter;
    // 角色
    private String mRole;

    public static Fragment newInstance(@NonNull String schoolId,
                                       @NonNull String schoolName,
                                       @NonNull String logoUrl,
                                       boolean isSchoolEnter,
                                       @NonNull @OnlineClassRole.RoleRes String role) {

        if (INSTANCE == null || true) {
            // 因为要切换机构的原因，所以不管怎么样，都创建新的实例
            INSTANCE = new SchoolInfoFragment();
            Bundle extras = new Bundle();
            extras.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
            extras.putString(KEY_EXTRA_SCHOOL_NAME, schoolName);
            extras.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER, isSchoolEnter);
            extras.putString(KEY_EXTRA_SCHOOL_LOGO, logoUrl);
            extras.putString(KEY_EXTRA_ROLE, role);
            INSTANCE.setArguments(extras);
        }
        return INSTANCE;
    }

    @Override
    protected SchoolInfoContract.Presenter initPresenter() {
        return new SchoolInfoPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_online_school_info;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID, null);
        mSchoolName = bundle.getString(KEY_EXTRA_SCHOOL_NAME, null);
        mLogoUrl = bundle.getString(KEY_EXTRA_SCHOOL_LOGO);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        mRole = bundle.getString(KEY_EXTRA_ROLE);
        if (EmptyUtil.isEmpty(mSchoolId) || EmptyUtil.isEmpty(mSchoolName) || EmptyUtil.isEmpty(mRole)) {
            return false;
        }
        return super.initArgs(bundle);
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleLayout = (LinearLayout) mRootView.findViewById(R.id.title_layout);
        mTeacherLayout = (LinearLayout) mRootView.findViewById(R.id.teacher_title_layout);
        mTeacherRecycler = (RecyclerView) mRootView.findViewById(R.id.teacher_recycler);
        mDiscoveryHolder = (DiscoveryHolder) mRootView.findViewById(R.id.discovery_holder);
        mDiscoveryHolder.setDiscoveryTitle(getString(R.string.label_study_course));
        mDiscoveryHolder.setNavigator(new LQCourseNavigatorImpl() {
            @Override
            public void onClickCourseTitleLayout() {
                super.onClickCourseTitleLayout();
                // 进入课程列表 标题是课堂
                // LQCourseListActivity.show(getActivity(), HideSortType.TYPE_SORT_ONLINE_COURSE,getString(R.string.label_course_shop),mSchoolId,true,false);
                // CourseShopListActivity.show(getActivity(),HideSortType.TYPE_SORT_ONLINE_COURSE,getString(R.string.label_study_course), mSchoolId, true, false);
                CourseShopListActivity.show(getActivity(), HideSortType.TYPE_SORT_ONLINE_COURSE, mSchoolName, mSchoolId, true, false);
            }

            @Override
            public void onClickCourse(@NonNull CourseVo courseVo) {
                super.onClickCourse(courseVo);
                // 进入课程详情
                CourseDetailsActivity.start(getActivity(), courseVo.getId(), true,
                        UserHelper.getUserId(), true, false, false);
            }
        });
        // 设置标题点击事件
        mTitleLayout.setOnClickListener(Void -> {
            OnlineClassListActivity.show(getActivity(), mSchoolId, mSchoolName, true);
        });

        mTeacherLayout.setOnClickListener(Void -> {
            SchoolTeacherListActivity.show(getActivity(), mSchoolId, mSchoolName, mLogoUrl, mRole);
        });

        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2, 8, false));
        mClassAdapter = new OnlineClassAdapter();
        mRecycler.setAdapter(mClassAdapter);

        mClassAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineClassEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity onlineClassEntity) {
                super.onItemClick(holder, onlineClassEntity);
                onClickClass(onlineClassEntity);
            }
        });

        // 显示机构老师信息
        mTeacherRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mTeacherAdapter = new SchoolTeacherAdapter();
        mTeacherRecycler.setAdapter(mTeacherAdapter);

    }

    @Override
    protected void initData() {
        super.initData();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mPresenter.requestOnlineSchoolInfoData(mSchoolId);
        mPresenter.requestOnlineSchoolTeacherData(mSchoolId);
    }

    /**
     * 点击在线课堂班级
     *
     * @param onlineClassEntity 班级实体
     */
    private void onClickClass(OnlineClassEntity onlineClassEntity) {
        mCurrentClickEntity = onlineClassEntity;
        String classId = onlineClassEntity.getClassId();
        // mPresenter.requestLoadClassInfo(classId);

        ClassInfoParams params = new ClassInfoParams(onlineClassEntity, true, false);
        ClassDetailActivity.show(getActivity(), params);
    }

    @Override
    public void updateOnlineSchoolCourseView(@NonNull List<CourseVo> courseVos) {
        mDiscoveryHolder.updateHotCourseData(courseVos);
    }

    @Override
    public void updateOnlineSchoolClassView(@NonNull List<OnlineClassEntity> entities) {
        mClassAdapter.replace(entities);
    }

    @Override
    public void updateSchoolTeacherView(@NonNull List<LQTeacherEntity> entities) {
        mTeacherAdapter.replace(entities);
    }

    @Override
    public void onClassCheckSucceed(JoinClassEntity entity) {
        // 非空判断
        if (EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentClickEntity)) {
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String role = getOnlineClassRoleInfo(entity);
        if (needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)) {
            // 已经加入班级 或者是老师身份
            // 从机构主页跳转,不显示机构关注
            JoinClassDetailActivity.show(getActivity(), entity.getClassId(), entity.getSchoolId(), mCurrentClickEntity.getId(), role, true);
        } else {
            // 未加入班级
            ClassDetailActivity.show(getActivity(), entity.getClassId(), entity.getSchoolId(), mCurrentClickEntity.getId(), role, true);
        }
    }

    /**
     * 获取在线课堂角色信息
     *
     * @param entity 数据实体
     * @return 判断顺序 老师->家长->学生
     */
    private String getOnlineClassRoleInfo(@NonNull JoinClassEntity entity) {
        String roles = entity.getRoles();
        // 默认学生身份
        String roleType = OnlineClassRole.ROLE_STUDENT;
        if (UserHelper.isTeacher(roles)) {
            // 老师身份
            roleType = OnlineClassRole.ROLE_TEACHER;
        } else if (UserHelper.isParent(roles)) {
            // 家长身份
            roleType = OnlineClassRole.ROLE_PARENT;
        } else if (UserHelper.isStudent(roles)) {
            // 学生身份
            roleType = OnlineClassRole.ROLE_STUDENT;
        }
        return roleType;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.ONLINE_CLASS_COMPLETE_GIVE_EVENT)) {
            // 刷新UI
            mPresenter.requestOnlineSchoolInfoData(mSchoolId);
            mPresenter.requestOnlineSchoolTeacherData(mSchoolId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
