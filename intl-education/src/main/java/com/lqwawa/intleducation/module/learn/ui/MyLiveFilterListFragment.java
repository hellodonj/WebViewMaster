package com.lqwawa.intleducation.module.learn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.PagerArgs;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.module.learn.adapter.LiveRoomAdapter;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.tool.LiveFilter;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2017/7/18.
 * email:man0fchina@foxmail.com
 */

public class MyLiveFilterListFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "MyLiveFilterListFragment";
    private TopBar topBar;
    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private LiveRoomAdapter liveRoomAdapter;
    private String curMemberId = "";
    private String curSchoolId = "";
    private int type = 0;
    private boolean isChildren = false;
    private LiveFilter.LiveFilterFromType liveFilterFromType;
    private int filterLiveType;
    private String filterStartTimeBegin;
    private String filterStartTimeEnd;
    private String filterEndTimeBegin;
    private String filterEndTimeEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_live_filter_list, container, false);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        btnReload = (Button) view.findViewById(R.id.reload_bt);
        topBar = (TopBar) view.findViewById(R.id.top_bar);
        btnReload.setOnClickListener(this);
        topBar.setBack(true);
        Bundle args = getArguments();
        if(args != null) {
            liveFilterFromType = (LiveFilter.LiveFilterFromType)
                    args.getSerializable(LiveFilter.KEY_LIVE_FILTER_FROM_TYPE);
            curMemberId = UserHelper.getUserId();
            if (liveFilterFromType == LiveFilter.LiveFilterFromType.Type_my_join_live) {
                type = 0;
                isChildren = false;
                topBar.setTitle(new StringBuilder().append(getResources().getString(R.string.filter))
                        .append("-").append(getResources().getString(R.string.my_join_live)).toString());
            }else if (liveFilterFromType == LiveFilter.LiveFilterFromType.Type_my_host_live){
                type = 1;
                isChildren = false;
                topBar.setTitle(new StringBuilder().append(getResources().getString(R.string.filter))
                        .append("-").append(getResources().getString(R.string.my_host_live)).toString());
            }else if (liveFilterFromType == LiveFilter.LiveFilterFromType.Type_my_child_live){
                type = 0;
                isChildren = true;
                curMemberId = getArguments().getString(LiveFilter.KEY_CHILD_MEMBER_ID);
                topBar.setTitle(new StringBuilder().append(getResources().getString(R.string.filter))
                        .append("-").append(args.getString(LiveFilter.KEY_CHILD_NAME))
                        .append(getResources().getString(R.string.xx_joined_live)).toString());
            }
            filterStartTimeBegin = args.getString(LiveFilter.KEY_START_TIME_BGEIN);
            filterStartTimeEnd = args.getString(LiveFilter.KEY_START_TIME_END);
            filterEndTimeBegin = args.getString(LiveFilter.KEY_END_TIME_BGEIN);
            filterEndTimeEnd = args.getString(LiveFilter.KEY_END_TIME_END);
            filterLiveType = args.getInt(LiveFilter.KEY_LIVE_TYPE, -1);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBroadcastReceiver();
        initViews();
    }

    private void initViews() {
        liveRoomAdapter = new LiveRoomAdapter(activity, true);
        listView.setAdapter(liveRoomAdapter);
        btnReload.setOnClickListener(this);

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
        pullToRefreshView.setLoadMoreEnable(false);
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LiveVo vo = (LiveVo) liveRoomAdapter.getItem(position);
                if (vo != null) {
                    if (vo.isIsDelete()) {
                        ToastUtil.showToast(activity, activity.getResources().getString(R.string.live_is_invalid));
                    } else {
                        if(vo.isFromMooc()){
                            if (isMyLive()) {
                                if (!vo.isIsDelete()) {
                                    LiveDetails.jumpToLiveDetails(activity, vo, type == 1, true, true);
                                }
                            } else {
                                if (!vo.isIsDelete()) {
                                    LiveDetails.jumpToLiveDetails(activity, getExtrasMemberId(),
                                            vo, true, true);
                                }
                            }
                        }else{
                            LiveDetails.jumpToAirClassroomLiveDetails(activity, vo,
                                    isChildren ? curMemberId : UserHelper.getUserId());
                        }
                    }
                }
            }
        });
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (StringUtils.isValidString(curMemberId)
//                        && curMemberId.equals(UserHelper.getUserId()) && (type == 0 || type == 1)) {
//                    final LiveVo vo = (LiveVo) liveRoomAdapter.getItem(position);
//                    if (type == 1 && !TextUtils.equals(UserHelper.getUserId(), vo.getCreateId())) {
//                        return false;
//                    }
//                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
//                    String tipMsg = activity.getResources().getString(R.string.exit_live_tip) + "?";
//                    if (type == 1) {
//                        if (vo.getState() == 1) {
//                            tipMsg = activity.getResources().getString(R.string.delete_live_tip2) + "?";
//                        } else {
//                            tipMsg = activity.getResources().getString(R.string.delete_live_tip1) + "?";
//                        }
//                    }
//                    builder.setMessage(tipMsg);
//                    builder.setTitle(activity.getResources().getString(R.string.tip));
//                    builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    //exitLive(vo);
//                                }
//                            });
//
//                    builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                    builder.create().show();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else if (view.getId() == R.id.live_timetable_tv) {
            //跳转到课程表界面
            LiveTimetableActivity.start(activity,
                    isChildren ? LiveTimetableActivity.LiveSourceType.Type_my_child_live :
                            (type == 0 ? LiveTimetableActivity.LiveSourceType.Type_my_join_live
                                    : LiveTimetableActivity.LiveSourceType.Type_my_host_live),
                    "", isChildren ? getExtrasMemberId() : "", "", "");
        }
    }

    private int pageIndex = 0;

    public void getData() {
        if (loadFailedLayout == null) {
            return;
        }
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        if(isChildren) {
            requestVo.addParams("MemberId", curMemberId);
        }else{
            requestVo.addParams("MemberId", UserHelper.getUserId());
        }
        requestVo.addParams("Flag", type);
        requestVo.addParams("Pager", new PagerArgs(pageIndex, AppConfig.PAGE_SIZE), true);
        if(filterLiveType == 0 || filterLiveType == 1){
            requestVo.addParams("IsEbanshuLive", filterLiveType == 1);
        }
        requestVo.addParams("StartTimeBegin", filterStartTimeBegin);
        requestVo.addParams("StartTimeEnd", filterStartTimeEnd);
        requestVo.addParams("EndTimeBegin", filterEndTimeBegin);
        requestVo.addParams("EndTimeEnd", filterEndTimeEnd);

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWA_GetMyLiveList);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                loadFailedLayout.setVisibility(View.GONE);
                LqResponseDataVo<List<WawaLiveListVo>> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<List<WawaLiveListVo>>>() {
                        });
                if (result.getErrorCode() == 0) {
                    List<WawaLiveListVo> wawaLiveList = result.getModel().getData();
                    List<LiveVo> list = new ArrayList<>();
                    if (wawaLiveList != null && wawaLiveList.size() > 0) {
                        list = WawaLiveListVo.wawaLiveListToLiveList(wawaLiveList);
                    }
                    pullToRefreshView.setLoadMoreEnable(list != null &&
                            list.size() < result.getModel().getPager().getRowsCount());
                    liveRoomAdapter.setData(list);
                    liveRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getMore() {
        RequestVo requestVo = new RequestVo();
        if(isChildren) {
            requestVo.addParams("MemberId", curMemberId);
        }else{
            requestVo.addParams("MemberId", UserHelper.getUserId());
        }
        requestVo.addParams("Flag", type);
        requestVo.addParams("Pager", new PagerArgs(pageIndex +1, AppConfig.PAGE_SIZE), true);
        if(filterLiveType == 0 || filterLiveType == 1){
            requestVo.addParams("IsEbanshuLive", filterLiveType == 1);
        }
        requestVo.addParams("StartTimeBegin", filterStartTimeBegin);
        requestVo.addParams("StartTimeEnd", filterStartTimeEnd);
        requestVo.addParams("EndTimeBegin", filterEndTimeBegin);
        requestVo.addParams("EndTimeEnd", filterEndTimeEnd);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWA_GetMyLiveList);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                loadFailedLayout.setVisibility(View.GONE);
                LqResponseDataVo<List<WawaLiveListVo>> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<List<WawaLiveListVo>>>() {
                        });
                if (result.getErrorCode() == 0) {
                    List<WawaLiveListVo> wawaLiveList = result.getModel().getData();
                    List<LiveVo> list = new ArrayList<>();
                    if (wawaLiveList != null && wawaLiveList.size() > 0) {
                        pageIndex++;
                        list = WawaLiveListVo.wawaLiveListToLiveList(wawaLiveList);

                        liveRoomAdapter.addData(list);
                        liveRoomAdapter.notifyDataSetChanged();
                    }else{
                        ToastUtil.showToastBottom(activity, R.string.no_more_data);
                        pullToRefreshView.setLoadMoreEnable(false);
                    }
                    pullToRefreshView.setLoadMoreEnable(list != null &&
                            list.size() < result.getModel().getPager().getRowsCount());

                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private boolean isMyLive() {
        return UserHelper.isLogin() && (!StringUtils.isValidString(getExtrasMemberId())
                || TextUtils.equals(getExtrasMemberId(), UserHelper.getUserId()));
    }

    private String getCurrentMemberId() {
        if (isMyLive()) {
            return UserHelper.getUserId();
        } else {
            return getExtrasMemberId();
        }
    }

    private String getExtrasMemberId() {
        return curMemberId;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("LIVE_STATUS_CHANGED")) {
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
        getContext().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
