package com.lqwawa.mooc.modle.tutorial.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.mooc.modle.tutorial.TutorialParams;

import com.galaxyschool.app.wawaschool.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

/**
 * @author mrmedici
 * 帮辅主页帮辅课程页面
 */
public class TutorialCourseListFragment extends PresenterFragment<TutorialCourseListContract.Presenter>
        implements TutorialCourseListContract.View, View.OnClickListener {

    private PullToRefreshView mRefreshLayout;
    private ListView mListView;
    private CourseListAdapter mAdapter;
    private CourseEmptyView mEmptyLayout;
    private LinearLayout mBottomLayout;
    private Button mBtnAddTutorial;

    private TutorialParams mTutorialParams;
    private String mTutorMemberId;
    private String mTutorName;
    private int mPageIndex;
    private String mClassId;

    public static Fragment newInstance(@NonNull TutorialParams params) {
        Fragment fragment = new TutorialCourseListFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT, params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected TutorialCourseListContract.Presenter initPresenter() {
        return new TutorialCourseListPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_course_list;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)) {
            mTutorialParams = (TutorialParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
            if (EmptyUtil.isNotEmpty(mTutorialParams)) {
                mTutorMemberId = mTutorialParams.getTutorMemberId();
                mTutorName = mTutorialParams.getTutorName();
                mClassId = mTutorialParams.getClassId();
            }
        }

        if (EmptyUtil.isEmpty(mTutorMemberId)) {
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mListView = (ListView) mRootView.findViewById(R.id.list_view);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        mBottomLayout = (LinearLayout) mRootView.findViewById(R.id.bottom_layout);
        mBtnAddTutorial = (Button) mRootView.findViewById(R.id.btn_add_tutorial);
        mBtnAddTutorial.setOnClickListener(this);
        mBtnAddTutorial.setText(!TextUtils.isEmpty(mClassId) ? R.string.add_class_tutor :
                R.string.label_add_tutorial);

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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!UserHelper.isLogin()) {
                    LoginHelper.enterLogin(getActivity());
                    return;
                }

                CourseVo vo = (CourseVo) mAdapter.getItem(position);
                CourseDetailsActivity.start(getActivity(), vo.getCourseId(), true, UserHelper.getUserId());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestCourseData(false);
        // 如果当前帮辅老师与自己是同一个人，隐藏按钮
        boolean tutorialMode = MainApplication.isTutorialMode();
        boolean isHide =
                tutorialMode  || TextUtils.equals(mTutorMemberId,UserHelper.getUserId());
        if (!TextUtils.isEmpty(mClassId)) {
            isHide = false;
        }
        if (isHide) {
            mBottomLayout.setVisibility(View.GONE);
        } else {
            // 不相等，不是查看自己个人主页
            // 查询是否已经加入该帮辅
            mPresenter.requestQueryAddedTutorState(UserHelper.getUserId(), mTutorMemberId, mClassId);
        }
    }

    /**
     * 请求列表数据
     *
     * @param moreData 是否加载更多
     */
    private void requestCourseData(boolean moreData) {
        if (moreData) {
            mPageIndex++;
            mPresenter.requestTutorialCourseData(mTutorMemberId, "", 1, mPageIndex);
        } else {
            mPageIndex = 0;
            mPresenter.requestTutorialCourseData(mTutorMemberId, "", 1, mPageIndex);
        }
    }

    @Override
    public void updateTutorialCourseView(@Nullable List<CourseVo> courseVos) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        mAdapter = new CourseListAdapter(getActivity());
        mAdapter.setData(courseVos);
        mListView.setAdapter(mAdapter);

        if (EmptyUtil.isEmpty(courseVos)) {
            // 数据为空
            mListView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            // 数据不为空
            mListView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreTutorialCourseView(@Nullable List<CourseVo> courseVos) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(courseVos.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mAdapter.addData(courseVos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateQueryAddedTutorStateView(boolean added) {
        // 广播出去,判断是否显示评论框
        // 需求更改,只有批阅过后的入口显示评论框
        // EventBus.getDefault().post(new EventWrapper(added,EventConstant.TRIGGER_ATTENTION_TUTORIAL_UPDATE));
        if (added) {
            mBottomLayout.setVisibility(View.GONE);
        } else {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateAddTutorView(boolean result) {
        if (result) {
            // 加帮辅成功
            mBottomLayout.setVisibility(View.GONE);
            // 广播出去,判断是否显示评论框
            // EventBus.getDefault().post(new EventWrapper(result,EventConstant.TRIGGER_ATTENTION_TUTORIAL_UPDATE));

            // 添加帮辅（班级帮辅）成功后，刷新班级帮辅列表
            MessageEvent messageEvent = new MessageEvent(EventConstant.TRIGGER_ADD_TUTOR_UPDATE);
            EventBus.getDefault().post(messageEvent);

            UIUtil.showToastSafe(R.string.label_added_tutorial_succeed);
        } else {
            UIUtil.showToastSafe(R.string.label_added_tutorial_failed);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_add_tutorial) {
            // 先判断是否登录
            if (!UserHelper.isLogin()) {
                LoginHelper.enterLogin(getActivity());
                return;
            }

            // 加帮辅(班级帮辅)
            mPresenter.requestAddTutor(UserHelper.getUserId(), mTutorMemberId,
                    mTutorName, mClassId);
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }
}
