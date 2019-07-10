package com.lqwawa.intleducation.module.learn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.ui.LinePopupWindow;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.vo.CourseSortType;
import com.lqwawa.intleducation.module.learn.adapter.MyCourseListAdapter;
import com.lqwawa.intleducation.module.learn.vo.MyCourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCourseListFragment extends MyBaseFragment implements View.OnClickListener {
    public static final String TAG = "MyCourseListFragment";
    private TextView textViewTitle;

    private TextView textViewCourseStatus;

    private LinearLayout statusLayout;

    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;
    private Button btnScanning;

    private List<CourseSortType> statusSortTypeList;
    private List<String> sortList;
    private String status = "";

    private MyCourseListAdapter courseListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_course_list, container, false);
        textViewTitle = (TextView) view.findViewById(R.id.title_name);
        textViewCourseStatus = (TextView) view.findViewById(R.id.course_status_tv);
        statusLayout = (LinearLayout) view.findViewById(R.id.item_title);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        btnReload = (Button) view.findViewById(R.id.reload_bt);
        btnScanning = (Button) view.findViewById(R.id.scanning_bt);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initViews();
    }

    private void initViews() {
        courseListAdapter = new MyCourseListAdapter(activity);
        listView.setAdapter(courseListAdapter);
        textViewTitle.setText(getResources().getString(R.string.learn)
                + getResources().getString(R.string.course) + "");
        statusSortTypeList = new ArrayList<CourseSortType>();
        statusSortTypeList.add(new CourseSortType("-1", getString(R.string.all), true));
        statusSortTypeList.add(new CourseSortType("0", getString(R.string.course_status_0), false));
        statusSortTypeList.add(new CourseSortType("1", getString(R.string.course_status_1), false));
        statusSortTypeList.add(new CourseSortType("2", getString(R.string.course_status_2), false));
        btnScanning.setOnClickListener(this);
        textViewCourseStatus.setOnClickListener(this);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCourseVo vo = (MyCourseVo) courseListAdapter.getItem(position);
                if (vo != null) {
                    MyCourseDetailsActivity.start(activity, vo.getCourseId(), false, true,
                            UserHelper.getUserId(), false, false, false, false,false, null, null);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final MyCourseVo vo = (MyCourseVo) courseListAdapter.getItem(position);
                CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                builder.setMessage(activity.getResources().getString(R.string.exit_course_tip)
                        + "?");
                builder.setTitle(activity.getResources().getString(R.string.tip));
                builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                exitCourse(vo);
                            }
                        });

                builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
                return true;
            }
        });

        pullToRefreshView.showRefresh();
        registerBoradcastReceiver();
        getData();
    }

    private void exitCourse(MyCourseVo vo) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", vo.getCourseId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.courseDelete + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, getResources().getString(R.string.exit_course)
                            + getResources().getString(R.string.success));
                    pullToRefreshView.showRefresh();
                    getData();
                } else {
                    ToastUtil.showToast(activity, getResources().getString(R.string.exit_course)
                            + getResources().getString(R.string.failed)
                            + ":" + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.course_status_tv) {
            showCourseStatusSort();
        } else if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        } else if (view.getId() == R.id.scanning_bt) {
        }
    }

    private void showCourseStatusSort() {
        if (sortList == null) {
            sortList = new ArrayList<String>();
            for (int i = 0; i < statusSortTypeList.size(); i++) {
                sortList.add(statusSortTypeList.get(i).getName());
            }
        }
        LinePopupWindow LinePopupWindow =
                new LinePopupWindow(activity, sortList, textViewCourseStatus.getText().toString(),
                        new com.lqwawa.intleducation.common.ui.LinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(Object object) {
                                String name = (String) object;
                                textViewCourseStatus.setText(name);
                                for (int i = 0; i < statusSortTypeList.size(); i++) {
                                    if (name.equals(statusSortTypeList.get(i).getName())) {
                                        status = statusSortTypeList.get(i).getId();
                                        statusSortTypeList.get(i).setIsSelect(true);
                                    } else {
                                        statusSortTypeList.get(i).setIsSelect(false);
                                    }
                                }
                                        /*statusTextView.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        statusImageView.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        statusSelImageView.setVisibility(View.INVISIBLE);
                                        pullToRefreshView.showRefresh();*/
                                getData();
                            }

                            @Override
                            public void onDismissListener() {
                                        /*statusTextView.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        statusImageView.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        statusSelImageView.setVisibility(View.INVISIBLE);*/
                            }
                        }, false);
        LinePopupWindow.showPopupWindow(statusLayout, true);
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);

        if (status != null && !status.equals("") && !status.equals("-1")) {
            requestVo.addParams("status", status);
        }

        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyCourseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyCourseVo> courseList = result.getData();
                    courseListAdapter.setData(courseList);
                    courseListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取我的课程列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getMore() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (status != null && !status.equals("") && !status.equals("-1")) {
            requestVo.addParams("status", status);
        }
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyCourseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyCourseVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pageIndex++;
                        courseListAdapter.addData(listMore);
                        courseListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToastBottom(activity, R.string.no_more_data);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取我的课程列表失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.APPOINT_COURSE_IN_CLASS_EVENT)) {
            // 刷新UI
            getData();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.joinInCourse)//
                    || action.equals(AppConfig.ServerUrl.Login)) {//
                getData();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.joinInCourse);//
        myIntentFilter.addAction(AppConfig.ServerUrl.Login);//登录成功
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

}
