package com.lqwawa.intleducation.module.onclass.teacherlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.ActivityUtils;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.school.SchoolTeacherAdapter;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构老师列表
 * @date 2018/06/05 18:02
 * @history v1.0
 * **********************************
 */
public class SchoolTeacherListActivity extends PresenterActivity<SchoolTeacherListContract.Presenter> implements SchoolTeacherListContract.View, View.OnClickListener {

    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_SCHOOL_NAME = "KEY_EXTRA_SCHOOL_NAME";
    private static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";
    private static final String KEY_EXTRA_SCHOOL_LOGO = "KEY_EXTRA_SCHOOL_LOGO";
    private String mSchoolId;
    private String mSchoolName;
    private String mLogoUrl;

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private SchoolTeacherAdapter mAdapter;

    private FrameLayout mJoinLayout;
    // 申请加入
    private TextView mTvApplyJoin;

    private int pageIndex;
    private String mRole;

    @Override
    protected SchoolTeacherListContract.Presenter initPresenter() {
        return new SchoolTeacherListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_school_teacher_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mSchoolName = bundle.getString(KEY_EXTRA_SCHOOL_NAME);
        mLogoUrl = bundle.getString(KEY_EXTRA_SCHOOL_LOGO);
        mRole = bundle.getString(KEY_EXTRA_ROLE);
        if (EmptyUtil.isEmpty(mSchoolId) || EmptyUtil.isEmpty(mSchoolName) || EmptyUtil.isEmpty(mRole)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mSchoolName);
        mJoinLayout = (FrameLayout) findViewById(R.id.join_layout);
        mTvApplyJoin = (TextView) findViewById(R.id.tv_join_teacher);
        if(OnlineClassRole.ROLE_TEACHER.equals(mRole)) mJoinLayout.setVisibility(View.GONE);
        // 显示机构老师信息
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mRefreshLayout.setLoadMoreEnable(false);
        mRecycler = (RecyclerView) findViewById(R.id.teacher_recycler);
        mRecycler.setLayoutManager(new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new SchoolTeacherAdapter();
        mRecycler.setAdapter(mAdapter);

        mTvApplyJoin.setOnClickListener(this);

        mRefreshLayout.setOnHeaderRefreshListener(view -> initData());

        mRefreshLayout.setOnFooterRefreshListener(view -> {
            mPresenter.requestOnlineSchoolTeacherData(mSchoolId, ++pageIndex);
        });
    }

    @Override
    protected void initData() {
        super.initData();
        pageIndex = 0;
        mPresenter.requestOnlineSchoolTeacherData(mSchoolId, pageIndex);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_join_teacher) {
            /*Intent intent = new Intent();
            intent.setClassName(MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.SubscribeSearchActivity");
            intent.putExtra("subscripe_search_type", 1);
            startActivity(intent);*/
            Intent intent = new Intent();
            intent.setClassName(getPackageName(),"com.galaxyschool.app.wawaschool.QrcodeProcessActivity");
            intent.putExtra("id",mSchoolId);
            intent.putExtra("name",mSchoolName);
            intent.putExtra("logoUrl",mLogoUrl);
            intent.putExtra("isFromMooc",true);
            startActivity(intent);
        }
    }

    @Override
    public void updateSchoolTeacherView(@NonNull List<LQTeacherEntity> entities) {
        mAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
    }

    @Override
    public void updateSchoolMoreTeacherView(@NonNull List<LQTeacherEntity> entities) {
        mAdapter.add(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    /**
     * 机构老师列表的入口
     *
     * @param context    上下文对象
     * @param schoolId   机构学校id
     * @param schoolName 机构学校名称
     * @param logoUrl 机构缩略图
     */
    public static void show(@NonNull Context context,
                            @NonNull String schoolId,
                            @NonNull String schoolName,
                            @NonNull String logoUrl,
                            @NonNull @OnlineClassRole.RoleRes String role) {
        Intent intent = new Intent(context, SchoolTeacherListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putString(KEY_EXTRA_SCHOOL_NAME, schoolName);
        bundle.putString(KEY_EXTRA_SCHOOL_LOGO,logoUrl);
        bundle.putString(KEY_EXTRA_ROLE,role);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
