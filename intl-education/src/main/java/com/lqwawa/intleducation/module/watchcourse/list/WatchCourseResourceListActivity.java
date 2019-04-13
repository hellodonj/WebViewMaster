package com.lqwawa.intleducation.module.watchcourse.list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.WatchStudentChapterActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.WatchCourseResourceActivity;
import com.lqwawa.intleducation.module.watchcourse.WatchResourceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mrmedici
 * @desc 关联学程，多个关联列表
 */
public class WatchCourseResourceListActivity extends ToolbarActivity implements View.OnClickListener {

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 1;

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private WatchCourseResourceListAdapter mWatchResourceAdapter;

    private FrameLayout mNewCartContainer;
    private TextView mTvWorkCart;
    private TextView mTvCartPoint;

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
    private Bundle mExtras;
    private String mSchoolId;
    private String mClassId;
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
        mSchoolId = mCourseResourceParams.getSchoolId();
        mClassId = mCourseResourceParams.getClassId();
        mExtras = bundle.getBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK);
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
                    WatchCourseResourceActivity.show(
                            WatchCourseResourceListActivity.this,
                            courseId,
                            mTaskType,
                            mMultipleChoiceCount,
                            mFilterArray,
                            mInitiativeTrigger,
                            mExtras,
                            mSchoolId,mClassId,
                            mRequestCode);
                }else{
                    WatchCourseResourceActivity.show(
                            WatchCourseResourceListActivity.this,
                            courseId,
                            mTaskType,
                            mMultipleChoiceCount,
                            mInitiativeTrigger,
                            mExtras,
                            mSchoolId,mClassId,
                            mRequestCode);
                }
            }
        });

        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mTvWorkCart = (TextView) findViewById(R.id.tv_work_cart);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);

        if(EmptyUtil.isNotEmpty(mCourseResourceParams) &&
                mCourseResourceParams.isInitiativeTrigger()) {
            mNewCartContainer.setVisibility(View.VISIBLE);
            mNewCartContainer.setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartPoint();
    }

    /**
     * 刷新红点
     */
    private void refreshCartPoint() {
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
            mTvCartPoint.setText(Integer.toString(count));
            if (count == 0) {
                mTvCartPoint.setVisibility(View.GONE);
            } else {
                mTvCartPoint.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        String[] courseIds = mCourseIds.split(",");
        mWatchResourceAdapter.replace(Arrays.asList(courseIds));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.new_cart_container){
            // 点击作业库
            handleSubjectSettingData(this,UserHelper.getUserId());
        }
    }

    public void handleSubjectSettingData(Context context,
                                         String memberId) {
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(memberId, SetupConfigType.TYPE_TEACHER, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //没有数据
                popChooseSubjectDialog(context);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                if (entities == null || entities.size() == 0) {
                    popChooseSubjectDialog(context);
                } else {
                    //有数据
                    if(EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)){
                        Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                        TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(WatchCourseResourceListActivity.this,mSchoolId,mClassId,extras);
                    }
                }
            }
        });
    }

    private static void popChooseSubjectDialog(Context context) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                context,
                null,
                context.getString(R.string.label_unset_choose_subject),
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                context.getString(R.string.label_choose_subject),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddSubjectActivity.show((Activity) context, false, SUBJECT_SETTING_REQUEST_CODE);
                    }
                });
        messageDialog.show();
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

    public static void show(@NonNull Activity activity, @NonNull CourseResourceParams params){
        show(activity,params,null);
    }
    /**
     * 关联学程的入口
     * @param activity 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Activity activity, @NonNull CourseResourceParams params, @Nullable Bundle extras){
        Intent intent = new Intent(activity,WatchCourseResourceListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,params.getRequestCode());
    }

    /**
     * 关联学程的入口
     * @param fragment 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Fragment fragment, @NonNull CourseResourceParams params, @Nullable Bundle extras){
        Intent intent = new Intent(fragment.getContext(),WatchCourseResourceListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putBundle(Common.Constance.KEY_EXTRAS_STUDY_TASK,extras);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent,params.getRequestCode());
    }
}
