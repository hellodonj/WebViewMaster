package com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;

import java.util.List;


/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 分类列表的页面 显示英语国际课程，英语国内课程，阅读课程，小语种课程列表；
 * @date 2018/04/27 16:40
 * @history v1.0
 * **********************************
 */
public class ClassifyListActivity extends PresenterActivity<ClassifyListContract.Presenter>
    implements ClassifyListContract.View,ClassifyListNavigator{
    // 数据源bundle key
    private static final String KEY_EXTRA_CLASSIFY_ENTITY = "KEY_EXTRA_CLASSIFY_ENTITY";

    // 分类数据
    private LQCourseConfigEntity mEntity;

    private TopBar mTopBar;
    private RecyclerView mRecycler;
    private ClassifyListAdapter mAdapter;

    @Override
    protected ClassifyListContract.Presenter initPresenter() {
        return new ClassifyListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_classify_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mEntity = (LQCourseConfigEntity) bundle.get(KEY_EXTRA_CLASSIFY_ENTITY);
        if(EmptyUtil.isEmpty(mEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ClassifyListAdapter();
        mAdapter.setNavigator(this);
        mRecycler.setAdapter(mAdapter);
        mAdapter.replace(mEntity.getChildList());
    }

    @Override
    protected void initData() {
        super.initData();
        mTopBar.setTitle(mEntity.getConfigValue());
        if(EmptyUtil.isEmpty(mEntity.getChildList())){
            // 没有children，可能是学程馆二级页面入口进来的
            // 请求接口拉取
            mPresenter.requestClassifyData(mEntity.getId(),mEntity.getConfigType() + 1);
        }
    }

    @Override
    public void updateClassifyView(List<LQCourseConfigEntity> entities) {
        mAdapter.replace(entities);
    }

    public static void show(@NonNull Context context, @NonNull LQCourseConfigEntity entity){
        Intent intent = new Intent(context,ClassifyListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CLASSIFY_ENTITY,entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(@NonNull LQCourseConfigEntity entity) {
        // 点击相关分类课程,进入分类列表
        GroupFiltrateState state = new GroupFiltrateState(entity);
        CourseFiltrateActivity.show(this,entity,state);
    }
}
