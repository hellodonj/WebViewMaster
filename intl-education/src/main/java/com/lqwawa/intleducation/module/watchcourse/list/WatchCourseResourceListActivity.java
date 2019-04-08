package com.lqwawa.intleducation.module.watchcourse.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.watchcourse.WatchCourseResourceActivity;
import com.lqwawa.intleducation.module.watchcourse.WatchResourceType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author mrmedici
 * @desc 关联学程，多个关联列表
 */
public class WatchCourseResourceListActivity extends ToolbarActivity {

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private WatchCourseResourceListAdapter mWatchResourceAdapter;

    private CourseResourceParams mCourseResourceParams;
    private String mParentName;
    private String mCourseIds;
    // 查看类型
    private int mTaskType;
    // 选择条目个数
    private int mMultipleChoiceCount;
    // 听读课 限制显示的资源类型集合
    private ArrayList<Integer> mFilterArray;
    private boolean mInitiativeTrigger;
    // 请求码
    private int mRequestCode;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_watch_course_resource_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mCourseResourceParams = (CourseResourceParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mCourseResourceParams)) return false;
        mParentName = mCourseResourceParams.getParentName();
        mCourseIds = mCourseResourceParams.getCourseIds();
        mTaskType = mCourseResourceParams.getTaskType();
        mMultipleChoiceCount = mCourseResourceParams.getMultipleChoiceCount();
        mFilterArray = mCourseResourceParams.getFilterArray();
        mInitiativeTrigger = mCourseResourceParams.isInitiativeTrigger();
        mRequestCode = mCourseResourceParams.getRequestCode();
        if(EmptyUtil.isEmpty(mParentName) || EmptyUtil.isEmpty(mCourseIds)) return false;

        if(mTaskType == WatchResourceType.TYPE_RETELL_COURSE && mFilterArray == null){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mParentName);

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                // 假刷新
                mRefreshLayout.onHeaderRefreshComplete();
            }
        });

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mWatchResourceAdapter = new WatchCourseResourceListAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setNestedScrollingEnabled(false);
        mRecycler.setAdapter(mWatchResourceAdapter);

        mWatchResourceAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<String>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, String courseId) {
                super.onItemClick(holder, courseId);
                if(mTaskType == WatchResourceType.TYPE_RETELL_COURSE){
                    WatchCourseResourceActivity.show(WatchCourseResourceListActivity.this,courseId,mTaskType,mMultipleChoiceCount,mFilterArray,mInitiativeTrigger,mRequestCode);
                }else{
                    WatchCourseResourceActivity.show(WatchCourseResourceListActivity.this,courseId,mTaskType,mMultipleChoiceCount,mInitiativeTrigger,mRequestCode);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        String[] courseIds = mCourseIds.split(",");
        mWatchResourceAdapter.replace(Arrays.asList(courseIds));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mRequestCode){
                setResult(Activity.RESULT_OK,data);
                finish();
            }
        }
    }

    /**
     * 关联学程的入口
     * @param activity 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Activity activity, @NonNull CourseResourceParams params){
        Intent intent = new Intent(activity,WatchCourseResourceListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,params.getRequestCode());
    }

    /**
     * 关联学程的入口
     * @param fragment 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Fragment fragment, @NonNull CourseResourceParams params){
        Intent intent = new Intent(fragment.getContext(),WatchCourseResourceListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent,params.getRequestCode());
    }
}
