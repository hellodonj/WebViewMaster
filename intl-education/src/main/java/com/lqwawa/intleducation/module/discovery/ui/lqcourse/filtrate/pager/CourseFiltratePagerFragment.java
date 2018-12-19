package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 筛选页面的布局片段
 * @date 2018/07/12 11:53
 * @history v1.0
 * **********************************
 */
public class CourseFiltratePagerFragment extends PresenterFragment<CourseFiltratePagerContract.Presenter>
    implements CourseFiltratePagerContract.View, View.OnClickListener{

    // 基础课程的Id
    private static final int BASIC_COURSE_ID = 2003;
    // 特色英语课程的Id
    private static final int CHARACTERISTICS_ENGLISH_ID = 2005;

    private static final String KEY_EXTRA_CONFIG_ENTITY = "KEY_EXTRA_CONFIG_ENTITY";
    private static final String KEY_EXTRA_SEARCH_KEY = "KEY_EXTRA_SEARCH_KEY";
    private static final String KEY_EXTRA_PARAM_ID = "KEY_EXTRA_PARAM_ID";

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private CourseEmptyView mEmptyView;
    private ListView mListView;
    // Adapter
    private CourseListAdapter courseListAdapter;


    // 分页数
    private int currentPage;
    private LQCourseConfigEntity mClassifyEntity;
    private int mParamOneId;
    private String mSearchKey;

    public static CourseFiltratePagerFragment newInstance(@NonNull LQCourseConfigEntity classifyEntity,
                                                          @NonNull int paramOneId,
                                                          @NonNull String searchKey){
        CourseFiltratePagerFragment fragment = new CourseFiltratePagerFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_EXTRA_CONFIG_ENTITY,classifyEntity);
        arguments.putInt(KEY_EXTRA_PARAM_ID,paramOneId);
        arguments.putString(KEY_EXTRA_SEARCH_KEY,searchKey);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected CourseFiltratePagerContract.Presenter initPresenter() {
        return new CourseFiltratePagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_course_filtrate_pager;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        Serializable serializable =  bundle.getSerializable(KEY_EXTRA_CONFIG_ENTITY);
        mClassifyEntity = (LQCourseConfigEntity)serializable;
        mParamOneId = bundle.getInt(KEY_EXTRA_PARAM_ID);
        mSearchKey = bundle.getString(KEY_EXTRA_SEARCH_KEY);
        if(EmptyUtil.isEmpty(mClassifyEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSearchContent = (EditText) mRootView.findViewById(R.id.et_search);
        mSearchClear = (ImageView) mRootView.findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) mRootView.findViewById(R.id.tv_filter);

        mSearchContent.setHint(R.string.search_hit);

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);

        // @date   :2018/6/13 0013 上午 11:31
        // @func   :V5.7改用键盘搜索的方式
        mSearchContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.INVISIBLE);
                }
                mSearchContent.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_DONE);
                mSearchContent.setMaxLines(1);
                mSearchContent.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        });

        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    // 搜索，收起软件盘
                    KeyboardUtil.hideSoftInput(getActivity());
                    requestCourseData(false);
                }
                return true;
            }
        });

        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mListView = (ListView) mRootView.findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(), vo.getId(), true, UserHelper.getUserId());
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                requestCourseData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                requestCourseData(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        // 请求数据
        requestCourseData(false);
    }

    /**
     * 根据筛选条件,查询课程
     */
    public void requestCourseData(boolean isMoreLoaded){
        // 获取筛选条件
        int paramOneId = mParamOneId,paramTwoId = 0,paramThreeId = 0;

        if(mClassifyEntity.getParentId() == BASIC_COURSE_ID){
            // 基础课程
            if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamTwoId())){
                paramTwoId = mClassifyEntity.getParamTwoId();
            }

            if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamThreeId())){
                paramThreeId = mClassifyEntity.getParamThreeId();
            }
        }

        if(mClassifyEntity.getParentId() == CHARACTERISTICS_ENGLISH_ID){
            if(EmptyUtil.isNotEmpty(mClassifyEntity.getParamTwoId())){
                // 特色英语
                paramTwoId = mClassifyEntity.getParamTwoId();
            }
        }

        mSearchKey = mSearchContent.getText().toString().trim();

        if(isMoreLoaded){
            currentPage++;
            mPresenter.requestMoreCourseData(currentPage,0,mClassifyEntity.getLevel(),mSearchKey,paramOneId,paramTwoId,paramThreeId);
        }else{
            currentPage = 0;
            mPresenter.requestCourseData(currentPage,0,mClassifyEntity.getLevel(),mSearchKey,paramOneId,paramTwoId,paramThreeId);
        }
    }

    @Override
    public void onCourseLoaded(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter = new CourseListAdapter(getActivity());
        courseListAdapter.setData(courseVos);
        mListView.setAdapter(courseListAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
            // 数据为空
            mRefreshLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRefreshLayout.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMoreCourseLoaded(List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        courseListAdapter.addData(courseVos);
        courseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_filter){
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(getActivity());
            requestCourseData(false);
        }else if(viewId == R.id.iv_search_clear){
            // 删除关键字
            mSearchContent.getText().clear();
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }
    }
}
