package com.lqwawa.intleducation.module.discovery.ui.classcourse.statistics.learn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.course.LearningProgressEntity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.WatchStudentChapterActivity;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 学习统计的页面
 */
public class LearningStatisticsActivity extends PresenterActivity<LearningStatisticsContract.Presenter>
    implements LearningStatisticsContract.View{

    private static final String KEY_EXTRA_TITLE = "KEY_EXTRA_TITLE";
    private static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_TYPE = "KEY_EXTRA_TYPE";

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyLayout;

    private LearningStatisticsAdapter mAdapter;
    private RecyclerItemDecoration mItemDecoration;

    private String mTitle;
    private String mClassId;
    private String mCourseId;
    private int mType;
    private CourseDetailParams mCourseParams;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_learning_statistics;
    }


    @Override
    protected LearningStatisticsContract.Presenter initPresenter() {
        return new LearningStatisticsPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mTitle = bundle.getString(KEY_EXTRA_TITLE);
        mClassId = bundle.getString(KEY_EXTRA_CLASS_ID);
        mCourseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        mType = bundle.getInt(KEY_EXTRA_TYPE);
        mCourseParams = (CourseDetailParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mClassId) ||
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
        mTopBar.setBack(true);
        mTopBar.setTitle(mTitle);

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);


        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadData();
            }
        });

        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new LearningStatisticsAdapter();
        mRecycler.setAdapter(mAdapter);
        mRecycler.addItemDecoration(mItemDecoration = new RecyclerItemDecoration(LearningStatisticsActivity.this,RecyclerItemDecoration.VERTICAL_LIST));

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LearningProgressEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LearningProgressEntity learningProgressEntity) {
                super.onItemClick(holder, learningProgressEntity);
                WatchStudentChapterActivity.show(LearningStatisticsActivity.this,mCourseId,learningProgressEntity,mCourseParams);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        mPresenter.requestLearningStatisticsData(mClassId,mCourseId,mType);
    }

    @Override
    public void updateLearningStatisticsView(@NonNull List<LearningProgressEntity> entities) {
        mRefreshLayout.onHeaderRefreshComplete();
        mAdapter.replace(entities);
        if(EmptyUtil.isEmpty(entities)){
            mEmptyLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }else{
            mEmptyLayout.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 学习统计的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull String title,
                            @NonNull String classId,
                            @NonNull String courseId,
                            int type,
                            @NonNull CourseDetailParams params){
        Intent intent = new Intent(context,LearningStatisticsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_TITLE,title);
        bundle.putString(KEY_EXTRA_CLASS_ID,classId);
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putInt(KEY_EXTRA_TYPE,type);
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
