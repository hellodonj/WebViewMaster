package com.lqwawa.intleducation.module.discovery.ui.subject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * 科目设置页面
 */
public class SubjectActivity extends PresenterActivity<SubjectContract.Presenter>
    implements SubjectContract.View,View.OnClickListener {

    private TopBar mTopBar;
    private ExpandableListView mExpandableView;
    private SubjectExpandableAdapter mAdapter;
    private Button mBtnSubjectAdd;

    @Override
    protected SubjectContract.Presenter initPresenter() {
        return new SubjectPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_subject;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_subject_setting);

        mBtnSubjectAdd = (Button) findViewById(R.id.btn_add_subject);
        mBtnSubjectAdd.setOnClickListener(this);

        mExpandableView = (ExpandableListView) findViewById(R.id.expandable_view);
        mAdapter = new SubjectExpandableAdapter();
        mExpandableView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        String userId = UserHelper.getUserId();
        // 测试数据
        userId = "c9a1a1ac-a72f-436a-90e4-a91600c0347a";
        mPresenter.requestTeacherConfigData(userId);
    }

    @Override
    public void updateTeacherConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        mAdapter.setData(entities);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_add_subject){
            // 点击确定
            AddSubjectActivity.show(this);
        }
    }

    /**
     * 科目设置页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,SubjectActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
