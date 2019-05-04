package com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;

import java.util.ArrayList;
import java.util.List;

public class OrganLibraryTypeActivity extends PresenterActivity<OrganLibraryTypeContract.Presenter>
        implements OrganLibraryTypeContract.View, View.OnClickListener {


    private TopBar mTopBar;
    private RecyclerView mRecycler;
    private OrganLibraryAdapter mAdapter;
    private List<LQCourseConfigEntity> mEntityList = new ArrayList<>();

    private CourseShopClassifyParams mParams;

    @Override
    protected OrganLibraryTypeContract.Presenter initPresenter() {
        return new OrganLibraryTypePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_organ_library_type;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mParams = (CourseShopClassifyParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if (EmptyUtil.isEmpty(mParams)) {
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);

        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_add_course);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new OrganLibraryAdapter();
        mRecycler.setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                super.onItemClick(holder, entity);
                if (mParams != null && entity != null) {
                    mParams.setLibraryType(entity.getLibraryType());
                }
                Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                CourseShopClassifyActivity.show(OrganLibraryTypeActivity.this,  mParams,
                        extras, true);
            }
        });

        mRecycler.addItemDecoration(new RecyclerItemDecoration(this, RecyclerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void initData() {
        super.initData();
        
        initEntityList();
        mAdapter.replace(mEntityList);
    }

    private void initEntityList() {
        LQCourseConfigEntity entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_LQCOURSE_SHOP);
        entity.setThumbnail("ic_lqcourse_shop_circle");
        entity.setConfigValue(getString(R.string.common_course_shop));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_VIDEO_LIBRARY);
        entity.setThumbnail("ic_video_library_circle");
        entity.setConfigValue(getString(R.string.common_video_library));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_LIBRARY);
        entity.setThumbnail("ic_library_circle");
        entity.setConfigValue(getString(R.string.common_library));
        mEntityList.add(entity);

        entity = new LQCourseConfigEntity();
        entity.setLibraryType(OrganLibraryType.TYPE_PRACTICE_LIBRARY);
        entity.setThumbnail("ic_practice_library_circle");
        entity.setConfigValue(getString(R.string.common_practice_library));
        mEntityList.add(entity);
    }


    /**
     * 班级学程列表选择的页面
     *
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull CourseShopClassifyParams params,
                            @Nullable Bundle extras) {
        Intent intent = new Intent(context, OrganLibraryTypeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK, extras);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
