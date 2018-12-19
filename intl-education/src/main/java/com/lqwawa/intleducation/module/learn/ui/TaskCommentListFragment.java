package com.lqwawa.intleducation.module.learn.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CommonReplyView;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.learn.adapter.LqTaskCommentAdapter;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommentListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommentVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2017/7/7.
 * email:man0fchina@foxmail.com
 */

public class TaskCommentListFragment extends MyBaseFragment{
    private static String TAG = TaskCommentListFragment.class.getSimpleName();
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;
    EditText commentEditText;
    TextView sendTextView;
    private LqTaskCommentAdapter commentAdapter;
    SectionResListVo sectionResListVo;
    private OnDataUpdateListener onDataUpdateListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_refrash_list_with_comment, container, false);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        btnReload = (Button) view.findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        //输入框
        commentEditText = (EditText) view.findViewById(R.id.edit_btn);

        if (commentEditText != null) {
            commentEditText.setFocusable(false);
            commentEditText.setFocusableInTouchMode(false);
            commentEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
                        LoginHelper.enterLogin(activity);
                        return;
                    }
                    Comment();
                }
            });
        }
        //发送按钮
        sendTextView = (TextView) view.findViewById(R.id.send_btn);
        if (sendTextView != null) {
            sendTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendComment(null);
                }
            });
        }
        return view;
    }

    public void setOnDataUpdateListener(OnDataUpdateListener listener){
        this.onDataUpdateListener = listener;
    }

    private void Comment() {
        CommonReplyView.showView(activity, null, commentEditText.getText().toString(),
                new CommonReplyView.PopupWindowListener() {
            @Override
            public void onBtnSendClickListener(String content) {
                hideKeyboard();
                if (content.isEmpty()) {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.enter_content_please));
                    return;
                }
                sendComment(content);
            }
            @Override
            public void onDismiss(String content){
                hideKeyboard();
                if(StringUtils.isValidString(content)){
                    commentEditText.setText(content);
                    commentEditText.setSelection(content.length());
                }else{
                    commentEditText.setText("");
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    
    public void initViews(){
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData(false);
            }
        });
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.reload_bt) {
                    pullToRefreshView.showRefresh();
                    getData(false);
                }
            }
        });
        commentAdapter = new LqTaskCommentAdapter(activity,
                new MyBaseAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                getData(false);
            }
        });
        listView.setAdapter(commentAdapter);
    }

    public void updateData(SectionResListVo vo){
        sectionResListVo = vo;
        pullToRefreshView.showRefresh();
        getData(false);
    }

    public void getData(boolean isComment){
        if(sectionResListVo == null){
            return;
        }
        if(null == pullToRefreshView) return;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("TaskId", sectionResListVo.getTaskId());
        requestVo.addParams("Type", 0);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GET_TASK_COMMENT_LIST_URL);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                LqResponseDataVo<LqTaskCommentListVo> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<LqTaskCommentListVo>>() {
                        });
                if(result.isHasError() == false){
                    List<LqTaskCommentVo> commentVoList = result.getModel().getData().getCommentList();
                    if(onDataUpdateListener != null){
                        int commentCount = 0;
                        if(commentVoList != null){
                            commentCount = commentVoList.size();
                            for(int i = 0; i <  commentVoList.size(); i++){
                                commentCount += commentVoList.get(i).getChildren() == null
                                        ? 0 : commentVoList.get(i).getChildren().size();
                            }
                        }
                        onDataUpdateListener.onDataUpdate(commentCount);
                    }
                    commentAdapter.setData(commentVoList);
                    commentAdapter.notifyDataSetChanged();
                    if(isComment){
                        listView.setSelection(0);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void sendComment(String content) {
        if (!UserHelper.isLogin()) {
            LoginHelper.enterLogin(activity);
        }

        if (content == null) {
            content = commentEditText.getText().toString();
        }
        if (content.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.pls_input_comment_content),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("CommentId", UserHelper.getUserId());
        requestVo.addParams("CommentName", UserHelper.getUserName());
        requestVo.addParams("CommentToId", "");
        requestVo.addParams("CommentToName", "");
        requestVo.addParams("Comments", content);
        requestVo.addParams("ParentId", 0);
        requestVo.addParams("TaskId", sectionResListVo.getTaskId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.LQWW_TASK_COMMENT);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                hideKeyboard();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.commit_comment)
                                    + activity.getResources().getString(R.string.success)
                                    + "!");
                    commentEditText.setText("");
                    getData(true);
                } else {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.commit_comment)
                                    + activity.getResources().getString(R.string.failed)
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, activity.getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public interface OnDataUpdateListener{
        void onDataUpdate(int count);
    }
}
