package com.lqwawa.intleducation.module.discovery.ui.classcourse.addHistory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseAdapter;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 添加历史学程的页面
 */
public class AddHistoryCourseActivity extends PresenterActivity<AddHistoryCourseContract.Presenter>
    implements AddHistoryCourseContract.View, View.OnClickListener{

    public static final String KEY_EXTRA_CHOICE_ENTITIES = "KEY_EXTRA_CHOICE_ENTITIES";

    public static final int MAX_SELECT_NUM = 9;

    private TopBar mTopBar;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private ClassCourseAdapter mCourseAdapter;
    private CourseEmptyView mEmptyLayout;

    private LinearLayout mBottomLayout;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    private ClassCourseParams mClassCourseParams;
    private String mSchoolId;
    private String mClassId;
    private String mClassName;
    private String mRoles;
    private boolean isTeacher;

    private int pageIndex;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_add_history_course;
    }

    @Override
    protected AddHistoryCourseContract.Presenter initPresenter() {
        return new AddHistoryCoursePresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mClassCourseParams = (ClassCourseParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mClassCourseParams)) return false;
        mSchoolId = mClassCourseParams.getSchoolId();
        mClassId = mClassCourseParams.getClassId();
        mClassName = mClassCourseParams.getClassName();
        mRoles = mClassCourseParams.getRoles();
        if(EmptyUtil.isEmpty(mSchoolId) ||
                EmptyUtil.isEmpty(mClassId) ||
                EmptyUtil.isEmpty(mRoles)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_class_course);

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);

        // 下拉刷新与加载更多
        mRefreshLayout.setOnHeaderRefreshListener(view->requestClassCourse(false));
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(view->requestClassCourse(true));

        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mCourseAdapter = new ClassCourseAdapter(mClassCourseParams.isHeadMaster(),mRoles,true);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this,3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mCourseAdapter);

        mCourseAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<ClassCourseEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, ClassCourseEntity entity) {
                super.onItemClick(holder, entity);
                if (!entity.isChecked()) {
                    List<ClassCourseEntity> selectList = getSelectData();
                    if (selectList.size() == MAX_SELECT_NUM) {
                        ToastUtil.showToast(AddHistoryCourseActivity.this,
                                getString(R.string.str_select_count_tips,
                                MAX_SELECT_NUM));
                        return;
                    }
                }
                // 添加选择,或者取消选择
                entity.setChecked(!entity.isChecked());
                mCourseAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestClassCourse(false);
    }

    /**
     * 加载数据
     * @param isMoreData 是否加载更多
     */
    private void requestClassCourse(boolean isMoreData) {
        if (!isMoreData) {
            pageIndex = 0;
        } else {
            pageIndex++;
        }

        int role = 1; // 默认学生
        // 如果是老师身份 role传0
        if(UserHelper.isTeacher(mRoles)) role = 0;
        mPresenter.requestClassCourseData(mClassId,role,"","",0,0,pageIndex);
    }

    @Override
    public void updateClassCourseView(List<ClassCourseEntity> entities) {
        mCourseAdapter.replace(entities);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(entities)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreClassCourseView(List<ClassCourseEntity> entities) {
        mCourseAdapter.add(entities);
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(entities) && entities.size() >= AppConfig.PAGE_SIZE);

        if(EmptyUtil.isEmpty(mCourseAdapter.getItems())){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_cancel){
            finish();
        }else if(viewId == R.id.btn_confirm){
            // 确认选择
            ArrayList<ClassCourseEntity> entities = getSelectData();
            
            if(EmptyUtil.isEmpty(entities)){
                // 提示选择要添加的历史学程
                UIUtil.showToastSafe(R.string.label_please_choice_add_history_course);
                return;
            }

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_EXTRA_CHOICE_ENTITIES,entities);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    private ArrayList<ClassCourseEntity> getSelectData() {
        List<ClassCourseEntity> items = mCourseAdapter.getItems();
        ArrayList<ClassCourseEntity> entities = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (ClassCourseEntity item : items) {
                if (item.isChecked()) {
                    entities.add(item);
                }
            }
        }
        return entities;
    }

    /**
     * 班级历史学程添加历史学程页面的入口
     * @param activity 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Activity activity,
                            @NonNull ClassCourseParams params,
                            int requestCode){
        Intent intent = new Intent(activity,AddHistoryCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,requestCode);
    }
}
