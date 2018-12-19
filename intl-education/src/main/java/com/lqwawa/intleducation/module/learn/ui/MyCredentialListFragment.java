package com.lqwawa.intleducation.module.learn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.module.discovery.ui.CredentialDetailsActivity;
import com.lqwawa.intleducation.module.learn.adapter.MyCredentialListAdapter;
import com.lqwawa.intleducation.module.learn.vo.MyCredentialListVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCredentialListFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "MyCourseListFragment";
    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private MyCredentialListAdapter myCredentialListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_refresh_list, container, false);
        pullToRefreshView  = (PullToRefreshView)view.findViewById(R.id.pull_to_refresh);
        listView  = (ListView)view.findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout)view.findViewById(R.id.load_failed_layout);
        btnReload = (Button)view.findViewById(R.id.reload_bt);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBoradcastReceiver();
        initViews();
    }

    private void initViews() {
        myCredentialListAdapter = new MyCredentialListAdapter(activity);
        listView.setAdapter(myCredentialListAdapter);
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
                MyCredentialListVo vo = (MyCredentialListVo) myCredentialListAdapter.getItem(position);
                CredentialDetailsActivity.start(activity
                        , vo.getCertification().getCertificationId());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MyCredentialListVo vo = (MyCredentialListVo) myCredentialListAdapter.getItem(position);
                if (vo != null) {
                    deleteCertification(vo);
                }
                return true;
            }
        });

        pullToRefreshView.showRefresh();
        getData();
    }

    private void deleteCertification(final MyCredentialListVo vo){
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.delete) +
                activity.getResources().getString(R.string.credential_simple)
                + "?");
        builder.setTitle(activity.getResources().getString(R.string.tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doDeleteCertification(vo);
                    }
                });

        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void doDeleteCertification(final MyCredentialListVo vo){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("certificationId", vo.getCertification().getCertificationId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.deleteCertification + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.delete)
                            +activity.getResources().getString(R.string.credential_simple)
                            + getResources().getString(R.string.success)
                            + "!");
                    getData();
                } else {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.delete)
                            +activity.getResources().getString(R.string.credential_simple)
                            + getResources().getString(R.string.failed)
                            + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "删除:" + throwable.getMessage());

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt){
            pullToRefreshView.showRefresh();
            getData();
        }
    }


    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);

        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyCertificateList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCredentialListVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCredentialListVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyCredentialListVo> list = result.getData();
                    myCredentialListAdapter.setData(list);
                    myCredentialListAdapter.notifyDataSetChanged();
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
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyCertificateList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCredentialListVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCredentialListVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyCredentialListVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pageIndex++;
                        myCredentialListAdapter.addData(listMore);
                        myCredentialListAdapter.notifyDataSetChanged();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**BroadcastReceiver************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.AddCertificationPlan)//加入学习计划
                || action.equals(AppConfig.ServerUrl.Login)) {//加入学习计划
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
        myIntentFilter.addAction(AppConfig.ServerUrl.AddCertificationPlan);//加入学习计划
        myIntentFilter.addAction(AppConfig.ServerUrl.Login);//加入学习计划
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
