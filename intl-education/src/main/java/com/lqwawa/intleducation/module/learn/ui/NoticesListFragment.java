package com.lqwawa.intleducation.module.learn.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.module.learn.adapter.NoticesListAdapter;
import com.lqwawa.intleducation.module.learn.vo.NoticeVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 * 通知列表
 */

public class NoticesListFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "NoticesListFragment";
    //数据列表
    private SuperListView listView;
    private NoticesListAdapter noticesListAdapter;
    private String courseId;
    private OnLoadStatusChangeListener onLoadStatusChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_supperlist, container, false);
        courseId = getArguments().getString("id");
        listView  = (SuperListView)view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        noticesListAdapter = new NoticesListAdapter(activity);
        listView.setAdapter(noticesListAdapter);
        listView.setOnItemClickListener(new SuperListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout parent, View view, int position) {
                NoticeVo vo = (NoticeVo)noticesListAdapter.getItem(position);
                if (vo != null){
                    NoticesDetailsActivity.start(activity, vo);
                }
            }
        });
        getData();
    }

    @Override
    public void onClick(View view) {

    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener){
        this.onLoadStatusChangeListener = listener;
    }

    public void updateData() {
        getData();
    }

    private int pageIndex = 0;
    private void getData(){
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }
    private void getData(int pageSize) {
        pageIndex = 0;
        RequestVo requestVo = new RequestVo();
        if(!activity.getIntent().getBooleanExtra("canEdit", false)){
            requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
        }
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        requestVo.addParams("courseId", courseId);

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseNoticesList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadSuccess();
                }
                ResponseVo<List<NoticeVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<NoticeVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<NoticeVo> noticeList = result.getData();
                    if (noticeList != null && noticeList.size() > 0) {
                        if (onLoadStatusChangeListener != null) {
                            onLoadStatusChangeListener.onLoadFinish(
                                    noticeList.size() >= AppConfig.PAGE_SIZE);
                        }
                    }else{
                        onLoadStatusChangeListener.onLoadFinish(false);
                    }
                    noticesListAdapter.setData(noticeList);
                    noticesListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadFlailed();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void getMore() {
        RequestVo requestVo = new RequestVo();
        if(!activity.getIntent().getBooleanExtra("canEdit", false)){
            requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
        }
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("courseId", courseId);

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseNoticesList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<NoticeVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<NoticeVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<NoticeVo> listMore = result.getData();
                    pageIndex++;
                    if (onLoadStatusChangeListener != null) {
                        onLoadStatusChangeListener.onLoadFinish(
                                listMore.size() >= AppConfig.PAGE_SIZE);
                    }
                    noticesListAdapter.addData(listMore);
                    noticesListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
