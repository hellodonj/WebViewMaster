package com.lqwawa.intleducation.module.discovery.ui.subject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.github.mikephil.charting.utils.EntryXComparator;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 科目设置页面
 */
public class SubjectActivity extends PresenterActivity<SubjectContract.Presenter>
    implements SubjectContract.View,View.OnClickListener {

    private static final int SUBJECT_SETTING_REQUEST_CODE = 1 << 0;

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
        mAdapter = new SubjectExpandableAdapter(true);
        mExpandableView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    // 加载数据
    private void loadData(){
        String userId = UserHelper.getUserId();
        mPresenter.requestTeacherConfigData(userId);
    }

    @Override
    public void updateTeacherConfigView(@NonNull List<LQCourseConfigEntity> entities) {
        mAdapter.setData(entities);

        fillData(entities);
        
        // 展开所有科目
        int groupCount = mAdapter.getGroupCount();
        for (int index = 0;index < groupCount;index++) {
            mExpandableView.expandGroup(index);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_add_subject){
            // 点击确定
            AddSubjectActivity.show(this,mAdapter.getGroupCount() != 0,SUBJECT_SETTING_REQUEST_CODE);
        }
    }

    private void fillData(List<LQCourseConfigEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            for (LQCourseConfigEntity entity : entities) {
                if (entity != null && (entity.getChildList() == null
                        || entity.getChildList().isEmpty())) {
                    List<LQCourseConfigEntity> list = new ArrayList<>();
                    LQCourseConfigEntity newEntity = entity.clone();
                    list.add(newEntity);
                    entity.setChildList(list);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == SUBJECT_SETTING_REQUEST_CODE){
                // 科目设置成功的回调
                Bundle extras = data.getExtras();
                if(EmptyUtil.isNotEmpty(extras)){
                    boolean completed = extras.getBoolean(AddSubjectActivity.KEY_EXTRA_RESULT);
                    if(completed){
                        // 刷新UI
                        loadData();
                    }
                }
            }
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
