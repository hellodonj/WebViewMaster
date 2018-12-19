package com.lqwawa.intleducation.module.discovery.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.OrganAdapter;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/10.
 * email:man0fchina@foxmail.com
 * 机构列表
 */

public class OrganListActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "OrganListActivity";
    //头部
    private TopBar topBar;
    private PullToRefreshView pullToRefreshView;
    private GridView organGridView;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;

    private List<OrganVo> organList;
    private OrganAdapter organAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_page_refrash_grid);

        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        organGridView = (GridView) findViewById(R.id.gridview);

        initViews();
        initData();
    }

    private void initViews() {
        topBar.setTitle(getString(R.string.organ_in));
        topBar.setBack(true);
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
        organGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrganDetailsActivity.start(activity,
                        ((OrganVo) organAdapter.getItem(position)).getId());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }

    private void initData() {
        organList = new ArrayList<OrganVo>();
        organAdapter = new OrganAdapter(this);
        pullToRefreshView.showRefresh();
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        pullToRefreshView.setLoadMoreEnable(false);
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetOrganList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<OrganVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<OrganVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    organList = result.getData();
                        pullToRefreshView.setLoadMoreEnable(organList != null
                                && organList.size() >= AppConfig.PAGE_SIZE);
                        organAdapter.setData(organList);
                        organGridView.setAdapter(organAdapter);
                        organAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取入驻机构列表失败:" + throwable.getMessage());
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
        pullToRefreshView.setLoadMoreEnable(false);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetOrganList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<OrganVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<OrganVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<OrganVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(listMore.size() >= AppConfig.PAGE_SIZE);
                        organList.addAll(listMore);
                        pageIndex++;
                        organAdapter.addData(listMore);
                        organAdapter.notifyDataSetChanged();
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
                LogUtil.d(TAG, "拉取入驻机构列表失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
