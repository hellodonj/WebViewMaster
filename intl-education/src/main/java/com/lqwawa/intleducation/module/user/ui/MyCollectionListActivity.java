package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.ui.LinePopupWindow;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.user.adapter.MyCollectionListAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.MyCollectionVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 我的收藏列表
 */
public class MyCollectionListActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener, ListView.OnItemLongClickListener{
    private static String TAG = "MyCollectionListActivity";

    private TopBar topBar;

    private TextView textViewTitleName;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private TextView textViewSort;
    private LinearLayout laywSort;


    private int sortStatus = -1;

    List<String> sortList;
    MyCollectionListAdapter myCollectionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_list);
        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        textViewSort = (TextView) findViewById(R.id.sort_tv);
        laywSort = (LinearLayout) findViewById(R.id.sort_layout);
        textViewTitleName = (TextView) findViewById(R.id.title_name) ;
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
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.my_collection));

        myCollectionListAdapter = new MyCollectionListAdapter(this);
        listView.setAdapter(myCollectionListAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.com_item_space));
        textViewTitleName.setText(getResources().getString(R.string.my_courses));
        sortList = new ArrayList<String>();
        sortList.add(getString(R.string.all));
        sortList.add(getString(R.string.course_status_0));
        sortList.add(getString(R.string.course_status_1));
        sortList.add(getString(R.string.course_status_2));
        laywSort.setOnClickListener(this);

        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.reload_bt) {
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
        MyCollectionVo vo = (MyCollectionVo) myCollectionListAdapter.getItem(position);
        if (vo != null) {
            CourseDetailsActivity.startForResult(activity, vo.getId() + "",true,
                    UserHelper.getUserId());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final MyCollectionVo vo = (MyCollectionVo) myCollectionListAdapter.getItem(position);
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.delete_collection_tip)
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


    private void exitCourse(MyCollectionVo vo){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", vo.getId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                    new RequestParams(AppConfig.ServerUrl.deleteCollectCourse + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, (getResources().getString(R.string.cancel))
                            + getResources().getString(R.string.collect)
                            + getResources().getString(R.string.success)
                            + "!");
                    pullToRefreshView.showRefresh();
                    setResult(Activity.RESULT_OK);
                    getData();
                } else {
                    ToastUtil.showToast(activity, (getResources().getString(R.string.cancel))
                            + getResources().getString(R.string.collect)
                            + getResources().getString(R.string.failed)
                            + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "收藏失败:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.load_faild_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("progressStatus", sortStatus);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyCollectionList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCollectionVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCollectionVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyCollectionVo> voList = result.getData();
                    pullToRefreshView.setLoadMoreEnable(voList != null &&
                            voList.size() >= AppConfig.PAGE_SIZE);
                    myCollectionListAdapter.setData(voList);
                    myCollectionListAdapter.notifyDataSetChanged();
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
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetMyCollectionList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<MyCollectionVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCollectionVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyCollectionVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        myCollectionListAdapter.addData(voList);
                        myCollectionListAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CourseDetailsActivity.Rs_collect && resultCode == Activity.RESULT_OK){
            pullToRefreshView.showRefresh();
            setResult(Activity.RESULT_OK);
            getData();
        }
    }
}
