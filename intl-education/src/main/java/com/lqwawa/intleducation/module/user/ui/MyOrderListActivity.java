package com.lqwawa.intleducation.module.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.ExpandableTextView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.LinePopupWindow;
import com.lqwawa.intleducation.module.user.adapter.MyOrderListAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 我的订单列表
 */

public class MyOrderListActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {
    public static int Rs_deleteOrder = 2065;
    private static String TAG = "PersonalContactsActivity";

    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private TextView textViewSort;
    private LinearLayout laywSort;


    private int sortStatus = -1;

    List<String> sortList;
    MyOrderListAdapter myOrderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_list);
        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        textViewSort = (TextView) findViewById(R.id.sort_tv);
        laywSort = (LinearLayout) findViewById(R.id.sort_layout);
        initViews();
    }

    private void initViews() {
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
        //初始化顶部工具条
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.my_orders));

        myOrderListAdapter = new MyOrderListAdapter(this, new MyBaseAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                pullToRefreshView.showRefresh();
                getData();
            }
        });
        listView.setAdapter(myOrderListAdapter);
        listView.setOnItemClickListener(this);
//        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.com_item_space));

        sortList = new ArrayList<String>();
        sortList.add(getString(R.string.all));
        sortList.add(getString(R.string.order_status_0));
        sortList.add(getString(R.string.order_status_1));
        sortList.add(getString(R.string.order_status_2));
        laywSort.setOnClickListener(this);

        pullToRefreshView.showRefresh();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else if(view.getId() == R.id.user_head_iv) {
        }else if(view.getId() == R.id.sort_layout) {
            LinePopupWindow LinePopupWindow =
                    new LinePopupWindow(activity, sortList, textViewSort.getText().toString(),
                            new LinePopupWindow.PopupWindowListener() {
                        @Override
                        public void onItemClickListener(Object object) {
                            String name = (String) object;
                            textViewSort.setText(name);
                            for (int i = 0; i < sortList.size(); i++) {
                                if (name.equals(sortList.get(i))) {
                                    sortStatus = i - 1;
                                }
                            }
                            pullToRefreshView.showRefresh();
                            getData();
                        }

                        @Override
                        public void onDismissListener() {

                        }
                    }, false);

            LinePopupWindow.showPopupWindow(textViewSort, true);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyOrderVo vo = (MyOrderVo) myOrderListAdapter.getItem(position);
        if (vo != null) {
            if(vo.isIsExpire()){
                try{
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.order_expired));
                }catch (Exception e){

                }
            }
        }
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("memberId", UserHelper.getUserId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyOrderList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyOrderVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyOrderVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyOrderVo> voList = result.getData();
                    pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                    myOrderListAdapter.setData(voList);
                    myOrderListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取我的订单列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
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
        requestVo.addParams("memberId", UserHelper.getUserId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyOrderList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<MyOrderVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyOrderVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyOrderVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        myOrderListAdapter.addData(voList);
                        myOrderListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToastBottom(getApplicationContext(), R.string.no_more_data);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取我的订单列表失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }
    public static void newInstance(Context context) {
        Intent starter = new Intent(context, MyOrderListActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }
}
