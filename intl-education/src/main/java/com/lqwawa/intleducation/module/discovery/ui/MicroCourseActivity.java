package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.module.discovery.adapter.TeacherResListAdapter;
import com.lqwawa.intleducation.module.discovery.vo.ResourceVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqresviewlib.LqResViewHelper;
import com.lqwawa.lqresviewlib.image.ImageDetailActivity;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 学习微课
 */
public class MicroCourseActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {
    private static String TAG = "MicroCourseActivity";

    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private GridView gridview;

    TeacherResListAdapter teacherResListAdapter;
    private int resType;
    private String teacherId;
    private long parentId;

    public static void start(Activity activity, int resType, String teacherId, long parentId) {
        activity.startActivity(new Intent(activity, MicroCourseActivity.class)
                .putExtra("resType", resType)
                .putExtra("teacherId", teacherId)
                .putExtra("parentId", parentId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_page_refrash_grid);
        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        gridview = (GridView) findViewById(R.id.gridview);

        resType = getIntent().getIntExtra("resType", 0);
        teacherId = getIntent().getStringExtra("teacherId");
        parentId = getIntent().getLongExtra("parentId", 0);
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

        gridview.setNumColumns(2);
        gridview.setVerticalSpacing(0);
        gridview.setHorizontalSpacing(0);
        gridview.setPadding(0, 0, 0, 0);
        String titleString = "";
        switch (resType) {
            case 1:
                titleString = getResources().getString(R.string.learn_micro_course);
                break;
            case 2:
                titleString = getResources().getString(R.string.learn_video);
                break;
            case 3:
                titleString = getResources().getString(R.string.learn_audio);
                gridview.setNumColumns(1);
                break;
            case 4:
                titleString = getResources().getString(R.string.learn_image);
                gridview.setNumColumns(3);
                break;
            case 5:
                titleString = getResources().getString(R.string.learn_ppt_or_pdf);
                break;
            case 6:
                titleString = getResources().getString(R.string.other_resource);
                break;
            default:
                break;
        }
        topBar.setTitle(titleString);

        teacherResListAdapter = new TeacherResListAdapter(this, resType);
        gridview.setAdapter(teacherResListAdapter);
        gridview.setOnItemClickListener(this);
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
        final ResourceVo vo = (ResourceVo) teacherResListAdapter.getItem(position);
        if (vo != null) {
            switch (vo.getType()) {
                case 0:
                    MicroCourseActivity.start(activity, resType,
                            teacherId, vo.getId());
                    break;
                case 1:
                    ImageDetailActivity.showStatic(activity,
                            vo.getResourceUrl().trim(),vo.getOriginName());
                    break;
                case 2:
                case 6:
                case 20:
                case 24:
                case 25:
                    LqResViewHelper.playBaseRes(vo.getType(), activity,
                            vo.getResourceUrl().trim(), vo.getOriginName());
                    break;
                case 5:
                case 16:
                case 17:
                case 18:
                case 19:
                case 3:
                    if (!SharedPreferencesHelper.getBoolean(this,
                            AppConfig.BaseConfig.KEY_ALLOW_4G, false)) {
                        if (NetWorkUtils.isWifiActive(activity.getApplication().getApplicationContext())) {
                            if (vo.getType() == 3) {
                                LqResViewHelper.playBaseRes(vo.getType(), activity, vo.getLeValue().trim(), vo.getOriginName());
                            } else {
                                LqResViewHelper.playWeike(activity,
                                        UserHelper.getUserId(),
                                        UserHelper.getUserName(),
                                        vo.getResourceUrl().trim(),
                                        vo.getOriginName(),
                                        1,
                                        Utils.getCacheDir(),
                                        vo.getScreenType(),
                                        vo.getType());
                            }
                        } else {
                            ToastUtil.showToast(activity, getResources().getString(R.string.can_not_use_4g));
                        }
                    } else {
                        if (NetWorkUtils.isWifiActive(activity.getApplication().getApplicationContext())) {
                            if (vo.getType() == 3) {
                                LqResViewHelper.playBaseRes(vo.getType(), activity, vo.getLeValue().trim(), vo.getOriginName());
                            } else {
                                LqResViewHelper.playWeike(activity,
                                        UserHelper.getUserId(),
                                        UserHelper.getUserName(),
                                        vo.getResourceUrl().trim(),
                                        vo.getOriginName(),
                                        1,
                                        Utils.getCacheDir(),
                                        vo.getScreenType(),
                                        vo.getType());
                            }
                        } else {
                            CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                            builder.setMessage(activity.getResources().getString(R.string.play_use_4g) + "?");
                            builder.setTitle(activity.getResources().getString(R.string.tip));
                            builder.setPositiveButton(activity.getResources().getString(R.string.continue_play),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if (vo.getType() == 3) {
                                                LqResViewHelper.playBaseRes(vo.getType(), activity, vo.getLeValue().trim(), vo.getOriginName());
                                            } else {
                                                LqResViewHelper.playWeike(activity,
                                                        UserHelper.getUserId(),
                                                        UserHelper.getUserName(),
                                                        vo.getResourceUrl().trim(),
                                                        vo.getOriginName(),
                                                        1,
                                                        Utils.getCacheDir(),
                                                        vo.getScreenType(),
                                                        vo.getType());
                                            }
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
                    }
                    break;
                default:
                    break;
            }
        }
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
        requestVo.addParams("teacherId", teacherId);
        requestVo.addParams("dataType", 3);
        requestVo.addParams("resType", resType);
        requestVo.addParams("parentId", parentId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetTeacherDetailsById + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<ResourceVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ResourceVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<ResourceVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        teacherResListAdapter.setData(voList);
                        teacherResListAdapter.notifyDataSetChanged();
                    }
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
        requestVo.addParams("teacherId", teacherId);
        requestVo.addParams("dataType", 3);
        requestVo.addParams("resType", resType);
        requestVo.addParams("parentId", parentId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetTeacherDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<ResourceVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ResourceVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<ResourceVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        teacherResListAdapter.addData(voList);
                        teacherResListAdapter.notifyDataSetChanged();
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
