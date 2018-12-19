package com.lqwawa.intleducation.module.onclass.related;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist.LQCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 关联课程
 */
public class RelatedCourseListActivity extends PresenterActivity<RelatedCourseListContract.Presenter>
    implements RelatedCourseListContract.View{

    private TopBar mTopBar;
    private PullToRefreshView mRefreshLayout;
    private ListView mListView;
    private CourseListAdapter mCourseListAdapter;
    private CourseEmptyView mEmptyLayout;
    private Button mBtnMoreCourse;

    private RelatedCourseParams mRelatedParams;
    private List<CourseVo> mCourseVos;
    private ClassDetailEntity.ParamBean mParam;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_related_course_list;
    }

    @Override
    protected RelatedCourseListContract.Presenter initPresenter() {
        return new RelatedCourseListPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mRelatedParams = (RelatedCourseParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            mParam = mRelatedParams.getParam();
            mCourseVos = mRelatedParams.getRelatedCourse();
        }

        if(EmptyUtil.isEmpty(mParam) || EmptyUtil.isEmpty(mCourseVos)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setTitle(R.string.title_related_course);
        mTopBar.setBack(true);

        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mBtnMoreCourse = (Button) findViewById(R.id.btn_more_course);
        mBtnMoreCourse.setOnClickListener(view->{
            watchMoreCourse();
        });

        mCourseListAdapter = new CourseListAdapter(this);
        mCourseListAdapter.setData(mCourseVos);
        mListView.setAdapter(mCourseListAdapter);

        if(EmptyUtil.isEmpty(mCourseVos)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) mCourseListAdapter.getItem(position);
                CourseDetailsActivity.start(RelatedCourseListActivity.this,false,false, vo.getId(), true, UserHelper.getUserId());
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                mRefreshLayout.onHeaderRefreshComplete();
            }
        });
    }

    /**
     * 查看更多课程
     */
    private void watchMoreCourse(){
        showLoading();
        mPresenter.requestConfigWithParam(mParam);
    }

    @Override
    public void updateConfigView(@NonNull int parentId, @NonNull ClassDetailEntity.ParamBean param, @NonNull List<LQCourseConfigEntity> entities) {
        if(EmptyUtil.isNotEmpty(entities)){
            for (LQCourseConfigEntity entity:entities) {
                if(entity.getId() == parentId){
                    // 找到该实体
                    GroupFiltrateState state = new GroupFiltrateState(entity);
                    entity.setParamTwoId(Integer.parseInt(param.getParamTwoId()));
                    entity.setParamThreeId(Integer.parseInt(param.getParamThreeId()));
                    // 如果基础课程可能会，标题错误
                    CourseFiltrateActivity.show(this,entity,state);
                    break;
                }
            }
        }
    }

    /**
     * 关联课程列表的入口
     * @param context 上下文对象
     * @param params 核心参数
     */
    public static void show(@NonNull Context context,@NonNull RelatedCourseParams params){
        Intent intent = new Intent(context,RelatedCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
