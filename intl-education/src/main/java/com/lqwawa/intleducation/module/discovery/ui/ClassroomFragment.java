package com.lqwawa.intleducation.module.discovery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.module.learn.adapter.LiveRoomAdapter;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.LiveListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;



/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/8/11 10:45
 * 描    述：直播列表
 * 修订历史：
 * ================================================
 */

public class ClassroomFragment extends MyBaseFragment implements View.OnClickListener{

    private static final String TAG = "ClassroomFragment";
    private SuperListView listView;
    private ListView listView_new;
    private OnLoadStatusChangeListener mOnLoadStatusChangeListener;
    private int pageIndex;
    //班主任
    private boolean isHeadMaster;
    //是不是老师
    private boolean isTeacher;
    private String schoolId;
    private String classId;
    private LiveRoomAdapter mLiveRoomAdapter;
    private String courseId;

    private PullToRefreshView pullToRefreshView;
    private RelativeLayout loadFailedLayout;
    //头部
    private TopBar topBar;
    private boolean showTopBar;

    private String paramCourseName;


    public interface Constants {
        String EXTRA_CONTACTS_TYPE = "type";
        String EXTRA_CONTACTS_ID = "id";
        String EXTRA_CONTACTS_NAME = "name";
        String EXTRA_CONTACTS_SCHOOL_ID = "schoolId";
        String EXTRA_CONTACTS_SCHOOL_NAME = "schoolName";
        String EXTRA_CONTACTS_GRADE_ID = "gradeId";
        String EXTRA_CONTACTS_GRADE_NAME = "gradeName";
        String EXTRA_CONTACTS_CLASS_ID = "classId";
        String EXTRA_CONTACTS_CLASS_NAME = "className";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_IS_HEADMASTER = "isHeadMaster";
        String EXTRA_IS_SCHOOLINFO = "schoolInfo";
        String EXTRA_SHOWTOPBAR = "showTopBar";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoadIntent();
        View view = inflater.inflate(showTopBar ? R.layout.fragment_course_details_item_new :
                R.layout.fragment_course_details_item, container, false);
        listView = (SuperListView) view.findViewById(R.id.listView);
        listView_new = (ListView) view.findViewById(R.id.listView_new);
        pullToRefreshView = (PullToRefreshView)view.findViewById(R.id.pull_to_refresh);
        loadFailedLayout = (RelativeLayout)view.findViewById(R.id.load_failed_layout);
        topBar = (TopBar)view.findViewById(R.id.top_bar);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = ClassroomFragment.this.getActivity();
        registerBroadcastReceiver();
        initViews();
        initData();
    }

    private void initViews() {
        mLiveRoomAdapter = new LiveRoomAdapter(activity, showTopBar);
        if (showTopBar) {
            listView_new.setAdapter(mLiveRoomAdapter);
            listView_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LiveVo vo = (LiveVo) mLiveRoomAdapter.getItem(position);
                    if (vo != null) {
                        LiveDetails.jumpToLiveDetails(getActivity(), vo, false, showTopBar,false);
                        if (vo.getState() != 0) {
                            vo.setBrowseCount(vo.getBrowseCount() + 1);
                        }
                    }
                }
            });

            topBar.setVisibility(View.VISIBLE);
            topBar.setTitle(getString(R.string.live_room));
            topBar.setBack(true);
            if (!StringUtils.isValidString(paramCourseName)) {
                topBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(activity, SearchActivity.class)
                                .putExtra("LevelName", activity.getIntent().getStringExtra("LevelName"))
                                .putExtra("Sort", activity.getIntent().getStringExtra("Sort"))
                                .putExtra("isLive", true)
                                .putExtra("isForSelRes",
                                        activity.getIntent().getBooleanExtra("isForSelRes", false)));

                    }
                });
            }

            //初始化下拉刷新
            pullToRefreshView.setLoadMoreEnable(true);
            pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
                @Override
                public void onHeaderRefresh(PullToRefreshView view) {
                    getData();
                }
            });
            pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
                @Override
                public void onFooterRefresh(PullToRefreshView view) {
                    getMore();
                }
            });

            pullToRefreshView.setLastUpdated(new Date().toLocaleString());
            pullToRefreshView.showRefresh();

        } else {
            listView.setAdapter(mLiveRoomAdapter);
            listView.setOnItemClickListener(new SuperListView.OnItemClickListener() {
                @Override
                public void onItemClick(LinearLayout parent, View view, int position) {
                    LiveVo vo = (LiveVo) mLiveRoomAdapter.getItem(position);
                    if (vo != null) {
                        LiveDetails.jumpToLiveDetailsFromCourse(getActivity(), vo,
                                activity.getIntent().getStringExtra("memberId"),false);
                        if (vo.getState() != 0) {
                            vo.setBrowseCount(vo.getBrowseCount() + 1);
                        }
                    }
                }
            });
        }
        getData();
    }

    private void getLoadIntent() {
        paramCourseName = activity.getIntent().getStringExtra("CourseName");
        Bundle bundle = getArguments();

        if (bundle != null) {
            courseId = getArguments().getString("id");
            schoolId = bundle.getString(Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classId = bundle.getString(Constants.EXTRA_CONTACTS_CLASS_ID);
            isHeadMaster = bundle.getBoolean(Constants.EXTRA_IS_HEADMASTER, false);
            isTeacher = bundle.getBoolean(Constants.EXTRA_IS_TEACHER, false);
            showTopBar = bundle.getBoolean(Constants.EXTRA_SHOWTOPBAR, false);
        }
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener onLoadStatusChangeListener) {
        mOnLoadStatusChangeListener = onLoadStatusChangeListener;
    }

    public void updateData() {
        getData();
    }
    public void updateForSearch(String keyWord) {
        this.paramCourseName = keyWord;
        getData();
    }

    private void getData(){
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }

    private void getData(int pageSize) {
        pageIndex = 0;
        RequestVo requestVo = new RequestVo();
        if (showTopBar) {
            if (paramCourseName != null && !paramCourseName.equals("")) {
                try {
                    requestVo.addParams("title", URLEncoder.encode(paramCourseName.trim(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            requestVo.addParams("courseId", courseId);
        }
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetLiveList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (mOnLoadStatusChangeListener != null) {
                    mOnLoadStatusChangeListener.onLoadSuccess();
                }
                if (showTopBar) {

                    pullToRefreshView.onHeaderRefreshComplete();
                }
                LiveListVo result = JSON.parseObject(s,
                        new TypeReference<LiveListVo>() {
                        });
                if (result.getCode() == 0) {
                    List<LiveVo> list = result.getData();
                    if (mOnLoadStatusChangeListener != null) {
                        mOnLoadStatusChangeListener.onLoadFinish(list != null &&
                                list.size() < result.getTotal());
                    }

                    if (showTopBar) {
                        pullToRefreshView.setLoadMoreEnable(list != null &&
                                list.size() < result.getTotal());
                    }
                    mLiveRoomAdapter.setData(list);
                    mLiveRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
                if (mOnLoadStatusChangeListener != null) {
                    mOnLoadStatusChangeListener.onLoadFlailed();
                }
                if (showTopBar) {

                    pullToRefreshView.onFooterRefreshComplete();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void getMore() {
        RequestVo requestVo = new RequestVo();

        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (showTopBar) {
            if (paramCourseName != null && !paramCourseName.equals("")) {
                try {
                    requestVo.addParams("title", URLEncoder.encode(paramCourseName.trim(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            requestVo.addParams("courseId", courseId);
        }

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetLiveList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (showTopBar) {

                    pullToRefreshView.onFooterRefreshComplete();
                }
                LiveListVo result = JSON.parseObject(s,
                        new TypeReference<LiveListVo>() {
                        });
                if (result.getCode() == 0) {
                    List<LiveVo> listMore = result.getData();
                    pageIndex++;
                    if (mOnLoadStatusChangeListener != null) {
                        mOnLoadStatusChangeListener.onLoadFinish(
                                result.getTotal() > (listMore.size() + mLiveRoomAdapter.getCount()));
                    }
                    if (showTopBar) {

                        pullToRefreshView.setLoadMoreEnable(
                                result.getTotal() > (listMore.size() + mLiveRoomAdapter.getCount()));
                    }
                    mLiveRoomAdapter.addData(listMore);
                    mLiveRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
                if (showTopBar) {
                    pullToRefreshView.onFooterRefreshComplete();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LiveDetails.MOOC_LIVE) {
            if(mLiveRoomAdapter != null) {
                mLiveRoomAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**BroadcastReceiver************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("LIVE_STATUS_CHANGED")){
                getData();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction("LIVE_STATUS_CHANGED");
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
