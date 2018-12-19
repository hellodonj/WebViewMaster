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
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.ui.CustomRaidoSelDialog;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.adapter.LiveRoomAdapter;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.tool.LiveFilter;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
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

public class MyLiveListPagerFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "MyCourseListPagerFragment";
    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private TextView textViewLiveTimetable;//课程表按钮

    private EditText editTextSearch;
    private ImageView imageViewClear;
    private TextView textViewFilter;

    private LiveRoomAdapter liveRoomAdapter;
    private String curMemberId = "";
    private String curSchoolId = "";
    private int type = 0;
    private boolean isChildren = false;
    private String childName;
    private String searchKeyword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_children_live_list, container, false);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        btnReload = (Button) view.findViewById(R.id.reload_bt);

        editTextSearch = (EditText)view.findViewById(R.id.search_et);
        textViewFilter = (TextView) view.findViewById(R.id.filter_tv);
        imageViewClear = (ImageView) view.findViewById(R.id.search_clear_iv);

        textViewLiveTimetable = (TextView) view.findViewById(R.id.live_timetable_tv);
        textViewLiveTimetable.setOnClickListener(this);
        btnReload.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBroadcastReceiver();
        initViews();
    }

    private void initViews() {
        curMemberId = getArguments().getString("MemberId");
        curSchoolId = getArguments().getString("SchoolId");
        isChildren = getArguments().getBoolean("IsChildren");
        childName = getArguments().getString("childName");
        type = getArguments().getInt("type");
        liveRoomAdapter = new LiveRoomAdapter(activity, true);
        listView.setAdapter(liveRoomAdapter);
        btnReload.setOnClickListener(this);

        editTextSearch.setHint(UIUtil.getString(type == 1 ?
                R.string.my_host_live_search_hint :
                R.string.my_live_search_hint));

        editTextSearch.setOnClickListener(this);
        textViewFilter.setOnClickListener(this);
        editTextSearch.setImeOptions(EditorInfo.IME_ACTION_NONE);
        imageViewClear.setOnClickListener(this);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imageViewClear.setVisibility(View.VISIBLE);
                } else {
                    imageViewClear.setVisibility(View.INVISIBLE);
                }
                search();
                editTextSearch.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_NONE);
                editTextSearch.setMaxLines(1);
                editTextSearch.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (editTextSearch.getText().toString().isEmpty()){
                        return false;
                    }
                    search();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

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
                        ToastUtil.showToast(activity, UIUtil.getString(R.string.live_is_invalid));
                    } else {
                        if(vo.isFromMooc()) {
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
                            String role = OnlineClassRole.ROLE_STUDENT;
                            role = isChildren ? OnlineClassRole.ROLE_PARENT : (type == 0 ? OnlineClassRole.ROLE_STUDENT : OnlineClassRole.ROLE_TEACHER);
                            // 是否是班主任角色
                            boolean isHeadMaster = UserHelper.getUserId().equals(vo.getCreateId());
                            isHeadMaster = false;
                            LiveDetails.jumpToAirClassroomLiveDetails(getActivity(),vo,
                                    isChildren ? curMemberId : UserHelper.getUserId(), role,false,
                                    isHeadMaster,true,false,vo.getClassId(),false,false,false,false);
                            /*LiveDetails.jumpToAirClassroomLiveDetails(activity, vo,
                                    isChildren ? curMemberId : UserHelper.getUserId());*/
                        }
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (StringUtils.isValidString(curMemberId)//判断是我自己的直播
                        && curMemberId.equals(UserHelper.getUserId())
                        && (type == 0 || type == 1)) {
                    final LiveVo vo = (LiveVo) liveRoomAdapter.getItem(position);
                    if (type == 1 //我主持成直播如果是来自慕课的直播而且我是小编是不可以删除的
                            && !TextUtils.equals(UserHelper.getUserId(), vo.getAcCreateId())) {
                        return false;
                    }

                    if(type == 1 && !vo.isFromMooc()//我主持的直播 我是创建者
                            && TextUtils.equals(UserHelper.getUserId(), vo.getAcCreateId())){
                        if(vo.getPublishClassVoList() != null //该直播在多个班级发布
                                && vo.getPublishClassVoList().size() > 1){
                            CustomRaidoSelDialog.Builder builder = new CustomRaidoSelDialog.Builder(activity);
                            String tipMsg = UIUtil.getString(R.string.delete_online_list_data_title);
                            builder.setMessage(tipMsg);
                            builder.setTitle(UIUtil.getString(R.string.tip));
                            builder.setPositiveButton(UIUtil.getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            exitLive(vo, which == 1);
                                        }
                                    });

                            builder.setNegativeButton(UIUtil.getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();
                            return true;
                        }
                    }

                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    String tipMsg = UIUtil.getString(R.string.exit_live_tip) + "?";
                    if (type == 1) {
                        if (vo.getState() == 1) {
                            tipMsg = UIUtil.getString(R.string.delete_live_tip2) + "?";
                        } else {
                            tipMsg = UIUtil.getString(R.string.delete_live_tip1) + "?";
                        }
                    }
                    builder.setMessage(tipMsg);
                    builder.setTitle(UIUtil.getString(R.string.tip));
                    builder.setPositiveButton(UIUtil.getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    exitLive(vo, false);
                                }
                            });

                    builder.setNegativeButton(UIUtil.getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (StringUtils.isValidString(curMemberId)) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }

    private void search(){
        searchKeyword = editTextSearch.getText().toString();
        getData();
    }

    private void exitLive(LiveVo vo, boolean deleteAllPublic) {
        if (vo.isFromMooc()) {
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("liveId", vo.getId());
            requestVo.addParams("type", type);
            RequestParams params =
                    new RequestParams(AppConfig.ServerUrl.DeleteLive + requestVo.getParams());
            params.setConnectTimeout(10000);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    ResponseVo<String> result = JSON.parseObject(s,
                            new TypeReference<ResponseVo<String>>() {
                            });
                    if (result.getCode() == 0) {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(type == 0 ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.success));
                        pullToRefreshView.showRefresh();
                        getData();
                    } else {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(type == 0 ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.failed)
                                        + ":" + result.getMessage());
                    }
                }

                @Override
                public void onCancelled(CancelledException e) {
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ToastUtil.showToast(activity, UIUtil.getString(R.string.net_error_tip));
                }

                @Override
                public void onFinished() {
                }
            });
        }else { //这是来自空中课堂的直播 调用两栖蛙蛙的接口
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("MemberId", UserHelper.getUserId());
            if(type == 0) {
                requestVo.addParams("Id", vo.getAirLiveId());
            }else{
                requestVo.addParams("Id", vo.getId());
                if(!deleteAllPublic){
                    requestVo.addParams("ClassId", vo.getClassId());
                }
            }
            RequestParams params =
                    new RequestParams(type == 0 ? AppConfig.ServerUrl.WAWA_DeleteMyLive
                            : AppConfig.ServerUrl.WAWA_DeleteAirClassNew);
            params.setConnectTimeout(10000);
            params.setAsJsonContent(true);
            params.setBodyContent(requestVo.getParamsWithoutToken());
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    LqResponseDataVo<String> result = JSON.parseObject(s,
                            new TypeReference<LqResponseDataVo<String>>() {
                            });
                    if (result.getErrorCode() == 0) {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(type == 0 ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.success));
                        pullToRefreshView.showRefresh();
                        getData();
                    } else {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(type == 0 ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.failed)
                                        + ":" + result.getErrorMessage());
                    }
                }

                @Override
                public void onCancelled(CancelledException e) {
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ToastUtil.showToast(activity, UIUtil.getString(R.string.net_error_tip));
                }

                @Override
                public void onFinished() {
                }
            });
        }
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
        }else if(view.getId() == R.id.search_clear_iv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.search_et) {
        }else if(view.getId() == R.id.filter_tv){
            LiveFilter.startLiveFilter(activity,
                    isChildren ? LiveFilter.LiveFilterFromType.Type_my_child_live :
                            (type == 0 ? LiveFilter.LiveFilterFromType.Type_my_join_live :
                                    LiveFilter.LiveFilterFromType.Type_my_host_live),
                    isChildren ? curMemberId : "",
                    childName);
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
        if(StringUtils.isValidString(searchKeyword)){
            requestVo.addParams("Title", searchKeyword);
        }
        requestVo.addParams("Flag", type);
        requestVo.addParams("Pager", new PagerArgs(pageIndex, AppConfig.PAGE_SIZE), true);
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
        if(StringUtils.isValidString(searchKeyword)){
            requestVo.addParams("Title", searchKeyword);
        }
        requestVo.addParams("Flag", type);
        requestVo.addParams("Pager", new PagerArgs(pageIndex +1, AppConfig.PAGE_SIZE), true);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWA_GetMyLiveList);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
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
