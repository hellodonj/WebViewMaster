package com.lqwawa.intleducation.module.tutorial.teacher.courses.record;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * @desc 我的审核记录页面 审核中,已拒绝
 */
public class AuditRecordPageFragment extends PresenterFragment<AuditRecordPageContract.Presenter>
    implements AuditRecordPageContract.View,View.OnClickListener{

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_AUDIT_TYPE = "KEY_EXTRA_AUDIT_TYPE";

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private PullToRefreshView mRefreshLayout;
    private ListView mListView;
    private CourseEmptyView mEmptyView;
    // Adapter
    private CourseListAdapter mAdapter;

    private String mMemberId;
    private int mAuditType;


    // 分页数
    private int currentPage;

    /**
     * @param memberId 当前用户
     * @return 审核状态的分页
     */
    public static Fragment newInstance(@NonNull String memberId,
                                       @NonNull @AuditType.AuditTypeRes int auditType){
        Fragment fragment = new AuditRecordPageFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_EXTRA_MEMBER_ID,memberId);
        arguments.putInt(KEY_EXTRA_AUDIT_TYPE,auditType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected AuditRecordPageContract.Presenter initPresenter() {
        return new AuditRecordPagePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_audit_record_page;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mAuditType = bundle.getInt(KEY_EXTRA_AUDIT_TYPE);
        if(EmptyUtil.isEmpty(mMemberId) ||
                (mAuditType != AuditType.AUDITED_PASS && mAuditType != AuditType.AUDITED_REJECT)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();mSearchContent = (EditText) mRootView.findViewById(R.id.et_search);
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
        mListView = (ListView) mRootView.findViewById(R.id.list_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) mAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(), vo.getCourseId(), true, UserHelper.getUserId());
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
        requestCourseData(false);
    }

    /**
     * 请求课程
     * @param moreData 是否更多数据
     */
    private void requestCourseData(boolean moreData){
        String mSearchKey = mSearchContent.getText().toString().trim();
        if(moreData){
            currentPage ++;
            mPresenter.requestTutorialCoursesData(mMemberId,mSearchKey,mAuditType,currentPage);
        }else{
            currentPage = 0;
            mPresenter.requestTutorialCoursesData(mMemberId,mSearchKey,mAuditType,currentPage);
        }
    }


    @Override
    public void updateTutorialCoursesView(List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        mAdapter = new CourseListAdapter(true,getActivity());
        mAdapter.setData(courseVos);
        mListView.setAdapter(mAdapter);

        if(EmptyUtil.isEmpty(courseVos)){
            // 数据为空
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreTutorialCoursesView(List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mAdapter.addData(courseVos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
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
            requestCourseData(false);
        }else if(viewId == R.id.et_search){
            // 点击搜索框
        }
    }
}
