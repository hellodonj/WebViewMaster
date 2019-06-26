package com.lqwawa.mooc.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.mooc.view.CustomExpandableListView;

import static com.lqwawa.intleducation.base.ui.MyBaseFragment.FRAGMENT_BUNDLE_OBJECT;

/**
 * 描述: 播放列表的fragment
 * 作者|时间: djj on 2019/6/26 0026 下午 3:45
 */
public class PlayListViewFragment extends AdapterFragment {

    private View mRootView;
    private TopBar mTopBar;
    private PullToRefreshView mPullToRefreshView;
    private CustomExpandableListView mExpandableListView;
    // 分页数据
    private int pageIndex = 0;
    // 课程详情Tab参数
    private CourseDetailItemParams mDetailItemParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = mRootView = inflater.inflate(R.layout.fragment_play_list_view, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        loadViews();
        loadData(false);
    }

    /**
     * 获取上页传过来的数据
     */
    private void loadIntentData() {
        Bundle arguments = getArguments();

        if(arguments.containsKey(FRAGMENT_BUNDLE_OBJECT)){
            mDetailItemParams = (CourseDetailItemParams) arguments.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }
    }

    /**
     * 初始化
     */
    private void loadViews() {
        mTopBar = (TopBar) mRootView.findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.label_picker_chapter);
        // 选择章节确认
        mTopBar.setRightFunctionText1(R.string.label_confirm, view -> confirm());
        mPullToRefreshView = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mExpandableListView = (CustomExpandableListView) mRootView.findViewById(R.id.expandable_list_view);

        mPullToRefreshView.setLoadMoreEnable(false);
        mPullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadData(false);
            }
        });
    }

    /**
     * 获取数据
     */
    private void loadData(boolean isMoreData) {
        String token = UserHelper.getUserId();
        if (mDetailItemParams.isParentRole()) {
            // 家长身份
            token = mDetailItemParams.getMemberId();
        }
        String courseId = mDetailItemParams.getCourseId();

        if (!isMoreData) {
            pageIndex = 0;
        } else {
            pageIndex++;
        }
        LQCourseHelper.requestCourseDetailByCourseId(
                token,
                courseId, null,
                mDetailItemParams.getDataType(),
                pageIndex, AppConfig.PAGE_SIZE,
                new Callback());

    }

    /**
     * @author mrmedici
     * @desc 获取课程详情以及课程大纲的统一回调处理
     */
    private class Callback implements DataSource.Callback<CourseDetailsVo> {

        @Override
        public void onDataLoaded(CourseDetailsVo courseDetailsVo) {

        }

        @Override
        public void onDataNotAvailable(int strRes) {
            UIUtil.showToastSafe(strRes);
        }
    }

    //确定
    private void confirm() {
        ToastUtil.showToast(getActivity(), "确认了");
    }

}
