package com.lqwawa.intleducation.module.tutorial.classtutor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.tutorial.course.TutorialGroupAdapter;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.TutorialFiltrateGroupActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * @desc 班级帮辅功能
 */
public class ClassTutorActivity extends PresenterActivity<ClassTutorContract.Presenter>
        implements ClassTutorContract.View, View.OnClickListener {

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";
    private static final String KEY_EXTRA_IS_HEAD_MASTER = "KEY_EXTRA_IS_HEAD_MASTER";

    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyLayout;
    private TextView mTvAddTutor;
    private PullToRefreshView pullToRefreshView;

    private TutorialGroupAdapter mGroupAdapter;

    private String mCurMemberId;
    private String mClassId;
    private boolean mIsHeaderMaster;


    public static void start(Context context, @NonNull String memberId, @NonNull String classId,
                             boolean isHeaderMaster) {
        Intent starter = new Intent(context, ClassTutorActivity.class);
        starter.putExtra(KEY_EXTRA_MEMBER_ID, memberId);
        starter.putExtra(KEY_EXTRA_CLASS_ID, classId);
        starter.putExtra(KEY_EXTRA_IS_HEAD_MASTER, isHeaderMaster);
        context.startActivity(starter);
    }

    @Override
    protected ClassTutorContract.Presenter initPresenter() {
        return new ClassTutorPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_class_tutor;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID, "");
        mClassId = bundle.getString(KEY_EXTRA_CLASS_ID, "");
        mIsHeaderMaster = bundle.getBoolean(KEY_EXTRA_IS_HEAD_MASTER, false);
        if (EmptyUtil.isEmpty(mClassId)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TopBar topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setTitle(R.string.class_tutor);
        topBar.setBack(true);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mTvAddTutor = (TextView) findViewById(R.id.tv_add_tutor);
        mTvAddTutor.setOnClickListener(this);
        mTvAddTutor.setVisibility(mIsHeaderMaster ? View.VISIBLE : View.GONE);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2, 8));
        mGroupAdapter = new TutorialGroupAdapter(null);
        mRecycler.setAdapter(mGroupAdapter);

        mGroupAdapter.setCallback(new TutorialGroupAdapter.EntityCallback() {
            @Override
            public void onAddTutorial(int position, @NonNull TutorialGroupEntity entity) {
                // 先判断是否登录
                if (!UserHelper.isLogin()) {
                    LoginHelper.enterLogin(ClassTutorActivity.this);
                    return;
                }

                // 添加帮辅,只添加自己的
                String memberId = UserHelper.getUserId();
                mPresenter.requestAddTutorByStudentId(memberId, entity.getCreateId(), entity.getCreateName());
            }
        });

        mGroupAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TutorialGroupEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TutorialGroupEntity entity) {
                super.onItemClick(holder, entity);
                if (EmptyUtil.isNotEmpty(TaskSliderHelper.onTutorialMarkingListener)) {
                    TaskSliderHelper.onTutorialMarkingListener.enterTutorialHomePager(ClassTutorActivity.this,
                            entity.getCreateId(), entity.getCreateName(), mClassId);
                }
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, TutorialGroupEntity data) {
                if (!mIsHeaderMaster) {
                    return;
                }
                if (data != null && !TextUtils.isEmpty(data.getCreateId())) {
                    ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                            ClassTutorActivity.this,
                            null,
                            getString(R.string.delete_class_totur_info),
                            getString(R.string.cancel),
                            (dialog, which) -> {
                            },
                            getString(R.string.confirm),
                            (dialog, which) -> mPresenter.requestDeleteClassTutor(data.getCreateId(), mClassId));
                    messageDialog.show();
                }
            }
        });

        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        pullToRefreshView.setOnHeaderRefreshListener(view -> mPresenter.requestClassTutors(mClassId));

    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestClassTutors(mClassId);
    }

    @Override
    public void updateTutorView(List<TutorialGroupEntity> entities) {
        pullToRefreshView.onHeaderRefreshComplete();
        if (entities != null && !entities.isEmpty()) {
            for (TutorialGroupEntity entity : entities) {
                entity.buildClassTutor();
            }
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        } else {
            mRecycler.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
        mGroupAdapter.replace(entities);
    }

    @Override
    public void updateDeleteClassTutorView(boolean result) {
        if (result) {
            UIUtil.showToastSafe(R.string.tip_delete_succeed);
            mPresenter.requestClassTutors(mClassId);
        }
    }

    @Override
    public void updateAddTutorByStudentIdView(boolean result) {
        if (result) {
            UIUtil.showToastSafe(R.string.label_added_tutorial_succeed);
            // 刷新UI
            mPresenter.requestClassTutors(mClassId);
        } else {
            UIUtil.showToastSafe(R.string.label_added_tutorial_failed);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_add_tutor) {
            TutorialFiltrateGroupActivity.show(this, mCurMemberId, mClassId,
                    getString(R.string.label_add_tutorial_line));
        }
    }
}
