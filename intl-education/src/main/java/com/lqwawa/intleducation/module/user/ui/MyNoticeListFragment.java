package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.user.adapter.MyNoticesListAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.MyNoticeVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 * 通知列表
 */

public class MyNoticeListFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "NoticesListFragment";

    private TextView textViewCourseStatus;
    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;
    //数据列表
    private MyNoticesListAdapter myNoticesListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_notice_list, container, false);
        textViewCourseStatus = (TextView)view.findViewById(R.id.set_all_read_text);
        loadFailedLayout = (RelativeLayout)view.findViewById(R.id.load_failed_layout);
        btnReload  = (Button)view.findViewById(R.id.reload_bt);
        pullToRefreshView  = (PullToRefreshView)view.findViewById(R.id.pull_to_refresh);
        listView  = (ListView)view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        textViewCourseStatus.setOnClickListener(this);
        btnReload.setOnClickListener(this);

        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        myNoticesListAdapter = new MyNoticesListAdapter(activity);
        listView.setAdapter(myNoticesListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyNoticeVo vo = (MyNoticeVo) myNoticesListAdapter.getItem(position);
                if(vo != null){
                    switch (vo.getType()){
                        case 0://课程评论
                        case 2://评论回复
                            CourseDetailsActivity.start(activity, vo.getCourseId(),true, 2, UserHelper.getUserId());
                            break;
                        case 1://课程删除
                        case 4://课程因投诉被屏蔽
                            break;
                        case 3://老师通知学生未完成学习//跳转到学习-课程详情-学习任务
                            MyCourseDetailsActivity.start(activity, vo.getCourseId(), 1, true, UserHelper.getUserId());
                            break;
                        case 5://老师通知学生作业未完成
                            MyCourseDetailsActivity.start(activity, vo.getCourseId(), 3, true, UserHelper.getUserId());
                            break;
                        case 6://老师通知学生作业未完成
                            MyCourseDetailsActivity.start(activity, vo.getCourseId(), 4, true, UserHelper.getUserId());
                            break;
                        case 7://老师通知学生有新的任务单
                            MyCourseDetailsActivity.start(activity, vo.getCourseId(), 3, true, UserHelper.getUserId());
                            break;
                        default:
                            break;
                    }
                    doReadNotice(vo.getId());
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final MyNoticeVo vo = (MyNoticeVo) myNoticesListAdapter.getItem(position);
                if(vo != null){
                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.confirm)
                            + activity.getResources().getString(R.string.delete_notice)
                            + "?");
                    builder.setTitle(activity.getResources().getString(R.string.tip));
                    builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteNotice(vo.getId());
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
                return true;
            }
        });
        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.set_all_read_text) {
            doReadAll();
        }else if(view.getId()== R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }

    private void doReadNotice(String id) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", id);
        requestVo.addParams("optType", 2);//设置单个已读
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.dealNotice + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    activity.setResult(Activity.RESULT_OK);
                    getData();
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

    private void doReadAll() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("optType", 1);//全部已读
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.dealNotice + requestVo.getParams());

        params.setConnectTimeout(10000);
        showProgressDialog(getResources().getString(R.string.loading));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    activity.setResult(Activity.RESULT_OK);
                    getData();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void deleteNotice(String id) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", id);
        requestVo.addParams("optType", 3);//删除
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.dealNotice + requestVo.getParams());

        params.setConnectTimeout(10000);
        showProgressDialog(getResources().getString(R.string.loading));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    activity.setResult(Activity.RESULT_OK);
                    ToastUtil.showToast(activity, getResources().getString(R.string.delete)
                            + getResources().getString(R.string.success));
                    getData();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getData(){
        getData(AppConfig.PAGE_SIZE);
    }
    private void getData(int pageSize) {
        RequestVo requestVo = new RequestVo();
        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.getNoticeList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                loadFailedLayout.setVisibility(View.GONE);
                ResponseVo<List<MyNoticeVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyNoticeVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyNoticeVo> noticeList = result.getData();
                    myNoticesListAdapter.setData(noticeList);
                    myNoticesListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onHeaderRefreshComplete();

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                loadFailedLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinished() {
            }
        });
    }


}
