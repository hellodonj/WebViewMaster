package com.lqwawa.intleducation.module.discovery.ui.subject.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ExpandableListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.subject.SubjectExpandableAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * 添加科目页面
 */
public class AddSubjectActivity extends PresenterActivity<AddSubjectContract.Presenter>
        implements AddSubjectContract.View{

    // 配置结果
    public static final String KEY_EXTRA_RESULT = "KEY_EXTRA_RESULT";
    // 是否配置过标签
    public static final String KEY_EXTRA_HAVE_CONFIG = "KEY_EXTRA_HAVE_CONFIG";

    private TopBar mTopBar;
    private ExpandableListView mExpandableView;
    private SubjectExpandableAdapter mAdapter;

    private boolean mHaveConfig;
    private int mConfigSize;

    @Override
    protected AddSubjectContract.Presenter initPresenter() {
        return new AddSubjectPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_add_subject;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mHaveConfig = bundle.getBoolean(KEY_EXTRA_HAVE_CONFIG);
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_subject_setting);
        mTopBar.setRightFunctionText1(R.string.label_confirm,view->{
            // 点击确定
            List<LQCourseConfigEntity> items = mAdapter.getItems();
            String selectedIds = mPresenter.getSelectedIds(items);

            mConfigSize = EmptyUtil.isNotEmpty(selectedIds) ? selectedIds.length() : 0;

            // 发生请求
            String memberId = UserHelper.getUserId();
            mPresenter.requestSaveTeacherConfig(memberId,selectedIds);
        });

        mExpandableView = (ExpandableListView) findViewById(R.id.expandable_view);
        mAdapter = new SubjectExpandableAdapter(false);
        mExpandableView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        String memberId = UserHelper.getUserId();
        showLoading();
        mPresenter.requestAssignConfigData(memberId);
    }

    @Override
    public void updateAssignConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        hideLoading();
        mAdapter.setData(entities);

        // 展开所有科目
        int groupCount = mAdapter.getGroupCount();
        for (int index = 0;index < groupCount;index++) {
            mExpandableView.expandGroup(index);
        }
    }

    @Override
    public void updateSaveTeacherConfigView(boolean completed) {
        // 保存标签的回调
        if(mHaveConfig || mConfigSize != 0) {
            if (completed) {
                UIUtil.showToastSafe(R.string.tip_subject_setting_succeed);
            } else {
                UIUtil.showToastSafe(R.string.tip_subject_setting_failed);
            }
        }

        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putBoolean(KEY_EXTRA_RESULT,completed);
        intent.putExtras(extras);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    /**
     * 添加科目页面的入口
     * @param activity 上下文对象
     */
    public static void show(@NonNull Activity activity,
                            boolean haveConfig,int requestCode){
        Intent intent = new Intent(activity,AddSubjectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_EXTRA_HAVE_CONFIG,haveConfig);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,requestCode);
    }
}
