package com.lqwawa.intleducation.module.discovery.ui.subject.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ExpandableListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.subject.SubjectExpandableAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * 添加科目页面
 */
public class AddSubjectActivity extends PresenterActivity<AddSubjectContract.Presenter>
        implements AddSubjectContract.View{

    private TopBar mTopBar;
    private ExpandableListView mExpandableView;
    private SubjectExpandableAdapter mAdapter;

    @Override
    protected AddSubjectContract.Presenter initPresenter() {
        return new AddSubjectPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_add_subject;
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

        });

        mExpandableView = (ExpandableListView) findViewById(R.id.expandable_view);
        mAdapter = new SubjectExpandableAdapter();
        mExpandableView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        String memberId = UserHelper.getUserId();
        memberId = "4eecb23d7-7312-4445-8720-b7ae24b2c33e";
        mPresenter.requestAssignConfigData(memberId);
    }

    @Override
    public void updateAssignConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        mAdapter.setData(entities);
    }

    /**
     * 添加科目页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,AddSubjectActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
