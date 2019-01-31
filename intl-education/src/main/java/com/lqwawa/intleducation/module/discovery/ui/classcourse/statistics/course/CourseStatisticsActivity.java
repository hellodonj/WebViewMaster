package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.course;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lqwawa.apps.views.charts.PieHelper;
import com.lqwawa.apps.views.charts.PieView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.CourseStatisticsEntity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn.LearningStatisticsActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;

import java.util.ArrayList;
import java.util.List;

public class CourseStatisticsActivity extends PresenterActivity<CourseStatisticsContract.Presenter>
    implements CourseStatisticsContract.View{

    private int[] mPieColors = {
            UIUtil.getColor(R.color.statistics_color_1),
            UIUtil.getColor(R.color.statistics_color_2),
            UIUtil.getColor(R.color.statistics_color_3),
            UIUtil.getColor(R.color.statistics_color_4),
    };

    private TopBar mTopBar;
    private PieView mPieView;
    private RecyclerView mRecycler;
    private CourseStatisticsAdapter mAdapter;

    private CourseStatisticsParams mStatisticsParams;
    private String configValue;
    private String mClassId;
    private String mCourseId;
    private CourseDetailParams mCourseParams;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_course_statistics;
    }


    @Override
    protected CourseStatisticsContract.Presenter initPresenter() {
        return new CourseStatisticsPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mStatisticsParams = (CourseStatisticsParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mStatisticsParams)) return false;

        configValue = mStatisticsParams.getCourseName();
        mClassId = mStatisticsParams.getClassId();
        mCourseId = mStatisticsParams.getCourseId();
        mCourseParams = mStatisticsParams.getCourseParams();

        if(EmptyUtil.isEmpty(configValue) ||
                EmptyUtil.isEmpty(mClassId) ||
                EmptyUtil.isEmpty(mCourseId) ||
                EmptyUtil.isEmpty(mCourseParams)){
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mPieView = (PieView) findViewById(R.id.pie_view);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);

        mTopBar.setBack(true);
        mTopBar.setTitle(configValue);

        ViewGroup.LayoutParams layoutParams = mPieView.getLayoutParams();

        int width = DisplayUtil.getMobileWidth(this) * 3 / 4;
        layoutParams.width = width;
        layoutParams.height = width;
        mPieView.setLayoutParams(layoutParams);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new CourseStatisticsAdapter();
        mRecycler.setAdapter(mAdapter);

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<CourseStatisticsEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, CourseStatisticsEntity entity) {
                super.onItemClick(holder, entity);
                LearningStatisticsActivity.show(CourseStatisticsActivity.this,mClassId,mCourseId,entity.getType(),mCourseParams);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    private void loadData() {
        mPresenter.requestCourseStatisticsData(mClassId,mCourseId);
    }

    @Override
    public void updateLearningStatisticsView(@NonNull List<CourseStatisticsEntity> entities) {
        loadTypeList(entities);
    }

    protected void loadTypeList(@NonNull List<CourseStatisticsEntity> entities) {
        if (EmptyUtil.isEmpty(entities)) return;


        final ArrayList<PieHelper> pieHelperArrayList = new ArrayList();

        int totalCount = 0;
        for (CourseStatisticsEntity entity : entities) {
            totalCount += entity.getCount();
        }

        for (CourseStatisticsEntity entity : entities) {
            int entityType = entity.getType();
            entity.setColor(mPieColors[entityType - 1]);
            if (entity.getCount() > 0) {
                pieHelperArrayList.add(new PieHelper(100f * entity.getCount() / totalCount, mPieColors[entityType - 1]));
            }
        }

        mPieView.setDate(pieHelperArrayList);
        mAdapter.replace(entities);
    }

    /**
     * 课程统计的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull CourseStatisticsParams params){
        Intent intent = new Intent(context,CourseStatisticsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
