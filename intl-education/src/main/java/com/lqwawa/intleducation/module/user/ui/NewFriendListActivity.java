package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.user.adapter.NewFriendListAdapter;
import com.lqwawa.intleducation.module.user.vo.NewFriendVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.Date;
import java.util.List;
/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 新的朋友
 */
public class NewFriendListActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {
    public static int Rs_new_friend_done = 1034;
    private static String TAG = "NewFriendListActivity";

    private TopBar topBar;

    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    NewFriendListAdapter newFriendListAdapter;

    public static void startForResult(Activity activity){
        activity.startActivityForResult(new Intent(activity, NewFriendListActivity.class), Rs_new_friend_done);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_activity_refresh_list);
        topBar = (TopBar)findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout)findViewById(R.id.load_failed_layout);
        btnReload  = (Button)findViewById(R.id.reload_bt);
        pullToRefreshView  = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
        listView  = (ListView)findViewById(R.id.listView);
        initViews();
    }

    private void initViews() {
        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
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
        topBar.setRightFunctionImage1(R.drawable.ic_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, AddFriendActivity.class));
            }
        });
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.new_friend));

        View headView = getLayoutInflater()
                .inflate(R.layout.mod_user_person_contact_list_item, null);

        newFriendListAdapter = new NewFriendListAdapter(this, new MyBaseAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                getData(Math.max(newFriendListAdapter.getCount(), AppConfig.PAGE_SIZE));
                setResult(RESULT_OK);
            }
        });
        listView.setAdapter(newFriendListAdapter);
        listView.setOnItemClickListener(this);
        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      /*  NewFriendVo vo = (NewFriendVo) newFriendListAdapter.getItem(position);
        if (vo != null) {
            //ContactDetailsActivity.start(activity, vo.getId() + "", false);
        }*/
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }
    private void getData(int pageSize) {
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetNewFriendList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<NewFriendVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<NewFriendVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<NewFriendVo> voList = result.getData();
                    pullToRefreshView.setLoadMoreEnable(voList != null && voList.size() >= AppConfig.PAGE_SIZE);
                    newFriendListAdapter.setData(voList);
                    newFriendListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取个人通讯录失败:" + throwable.getMessage());
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
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetNewFriendList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<NewFriendVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<NewFriendVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<NewFriendVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        newFriendListAdapter.addData(voList);
                        newFriendListAdapter.notifyDataSetChanged();
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
                LogUtil.d(TAG, "获取个人通讯录失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
