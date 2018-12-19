package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.BasicsCourseContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.BasicsCoursePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.NewBasicsCourseHolder;
import com.lqwawa.intleducation.module.onclass.school.LQCourseNavigatorImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author mrmedici
 * @desc 基础课程
 */
public class BasicsCourseActivity extends PresenterActivity<BasicsCourseContract.Presenter>
        implements BasicsCourseContract.View{

    private static final String KEY_EXTRA_CONFIG_ENTITY = "KEY_EXTRA_CONFIG_ENTITY";

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private FrameLayout mLayContent;

    private LQCourseConfigEntity mConfigEntity;
    private List<LQBasicsOuterEntity> mBasicsEntities;

    @Override
    protected BasicsCourseContract.Presenter initPresenter() {
        return new BasicsCoursePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_basics_course;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mConfigEntity = (LQCourseConfigEntity) bundle.getSerializable(KEY_EXTRA_CONFIG_ENTITY);
        mBasicsEntities = (List<LQBasicsOuterEntity>) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mBasicsEntities) || EmptyUtil.isEmpty(mConfigEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(mConfigEntity.getConfigValue());
        // mTopBar.setTitle(R.string.title_basics_course);
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mLayContent = (FrameLayout) findViewById(R.id.lay_content);
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                mRefreshLayout.onHeaderRefreshComplete();
            }
        });

        NewBasicsCourseHolder holder = new NewBasicsCourseHolder(this);
        holder.updateView(false,mConfigEntity,mBasicsEntities);
        holder.setCourseNavigator(new LQCourseNavigatorImpl(){
            @Override
            public void onClickBasicsSubject(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity) {
                super.onClickBasicsSubject(entity);
                // 点击科目
                mPresenter.requestConfigWithBasicsEntity(entity);
            }
        });
        mLayContent.addView(holder.getRootView());
    }

    @Override
    public void updateConfigView(@NonNull int parentId, @NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity, @NonNull List<LQCourseConfigEntity> entities) {
        if(EmptyUtil.isNotEmpty(entities)){
            for (LQCourseConfigEntity _entity:entities) {
                if(_entity.getId() == parentId){
                    // 找到该实体
                    _entity.setParamTwoId(entity.getParamTwoId());
                    _entity.setParamThreeId(entity.getParamThreeId());
                    _entity.setConfigValue(entity.getConfigValue());
                    GroupFiltrateState state = new GroupFiltrateState(_entity);
                    CourseFiltrateActivity.show(this,_entity,state);
                }
            }
        }
    }

    /**
     * 基础课程的入口
     * @param context 上下文对象
     * @param configEntity 基础课程
     * @param entities 基础课程数据
     */
    public static void show(@NonNull Context context,@NonNull LQCourseConfigEntity configEntity, @NonNull List<LQBasicsOuterEntity> entities){
        Intent intent = new Intent(context,BasicsCourseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_CONFIG_ENTITY,configEntity);
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, (Serializable) entities);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
