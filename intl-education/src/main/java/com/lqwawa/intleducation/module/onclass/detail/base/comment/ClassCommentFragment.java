package com.lqwawa.intleducation.module.onclass.detail.base.comment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;
import com.lqwawa.intleducation.module.discovery.adapter.CourseCommentAdapter;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailNavigator;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailNavigator;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailNavigator;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情 授课计划Fragment
 * @date 2018/06/01 16:03
 * @history v1.0
 * **********************************
 */
public class ClassCommentFragment extends PresenterFragment<ClassCommentContract.Presenter>
    implements ClassCommentContract.View{

    private static final String KEY_EXTRA_ENTITY = "KEY_EXTRA_ENTITY";
    private static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";

    // 评论列表
    private LuRecyclerView mRecycler;
    // 空布局显示
    private TextView mTvRemind;
    // 评论容器
    private LinearLayout mCommentLayout;
    // 评论内容
    private EditText mCommentContent;
    // 发送按钮
    private TextView mBtnSend;

    private OnlineTabParams mTabParams;
    private ClassDetailEntity mClassDetailEntity;
    // 在线课堂班级Id
    private int id;
    private String mRole;

    // 评论数据
    private OnlineCommentEntity mCommentEntity;

    // 评论Dialog
    private CommentDialog mCommentDialog;
    // 评论数据
    private CommentDialog.CommentData mCommentData;
    // 评论列表
    private ClassCommentAdapter mCommentAdapter;
    private LuRecyclerViewAdapter mLuCommentAdapter;

    private int pageIndex;

    public static Fragment newInstance(@NonNull OnlineTabParams params){
        ClassCommentFragment fragment = new ClassCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ClassCommentContract.Presenter initPresenter() {
        return new ClassCommentPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_student_comment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTabParams = (OnlineTabParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mTabParams)) return false;
        mClassDetailEntity = mTabParams.getDetailEntity();
        mRole = mTabParams.getRole();

        if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                EmptyUtil.isEmpty(mRole) ||
                EmptyUtil.isEmpty(mClassDetailEntity.getData())){
            return false;
        }

        id = mClassDetailEntity.getData().get(0).getId();
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (LuRecyclerView) mRootView.findViewById(R.id.recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);

        mCommentAdapter = new ClassCommentAdapter(getActivity(), new ClassCommentAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                mPresenter.requestOnlineClassCommentData(id,0);
            }
        });
        mCommentAdapter.setData(mClassDetailEntity,(vo,content)->{
            // 评论回复回调
            if (TextUtils.isEmpty(content)) {
                UIUtil.showToastSafe(R.string.enter_evaluation_content_please);
                return;
            }

            if(!EmptyUtil.isEmpty(mClassDetailEntity.getData())){
                ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);
                mPresenter.requestCommitComment(1,dataBean.getId(),vo.getId(),content,0);
            }
        });

        mLuCommentAdapter = new LuRecyclerViewAdapter(mCommentAdapter);
        mRecycler.setAdapter(mLuCommentAdapter);

        mRecycler.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                List<ClassDetailEntity.DataBean> data = mClassDetailEntity.getData();
                if(!EmptyUtil.isEmpty(data)){
                    pageIndex++;
                    mPresenter.requestOnlineClassCommentData(data.get(0).getId(),pageIndex);
                }

            }
        });

        mTvRemind = (TextView) mRootView.findViewById(R.id.tv_empty_remind);
        mCommentLayout = (LinearLayout) mRootView.findViewById(R.id.comment_layout);
        mCommentContent = (EditText) mRootView.findViewById(R.id.et_comment_content);
        mBtnSend = (TextView) mRootView.findViewById(R.id.btn_send);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提交评论
                commitComment(mCommentData);
            }
        });

        // @date   :2018/4/10 0010 下午 4:26
        // @func   :点击文本框,显示评论对话框
        mCommentContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    triggerCommentDialog(mCommentData);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<ClassDetailEntity.DataBean> data = mClassDetailEntity.getData();
        if(!EmptyUtil.isEmpty(data)){
            mPresenter.requestOnlineClassCommentData(data.get(0).getId(),0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCommentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateOnlineClassCommentView(@NonNull OnlineCommentEntity entity) {
        if(getActivity() instanceof BaseClassDetailActivity) {
            BaseClassDetailActivity parentActivity = (BaseClassDetailActivity) getActivity();
            parentActivity.getRefreshLayout().setRefreshing(false);
        }
        this.mCommentEntity = entity;
        List<CommentVo> listComment = entity.getCommentList();

        mCommentAdapter.setData(entity.getStarLevel());
        mCommentAdapter.setData(listComment);

        if(EmptyUtil.isEmpty(listComment)){
            // mRecycler.setVisibility(View.GONE);
            mTvRemind.setVisibility(View.VISIBLE);
        }else{
            // mRecycler.setVisibility(View.VISIBLE);
            mTvRemind.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateOnlineClassMoreCommentView(@NonNull OnlineCommentEntity entity) {
        this.mCommentEntity = entity;
        List<CommentVo> listComment = entity.getCommentList();

        mCommentAdapter.setData(entity.getStarLevel());
        mCommentAdapter.addData(listComment);

        mRecycler.refreshComplete(AppConfig.PAGE_SIZE);
        if(listComment.size() < AppConfig.PAGE_SIZE){
            // 加载更多，并且没有更多数据了
            mRecycler.setNoMore(true);
        }

        /*if(EmptyUtil.isEmpty(listComment)){
            // mRecycler.setVisibility(View.GONE);
            mTvRemind.setVisibility(View.VISIBLE);
        }else{
            // mRecycler.setVisibility(View.VISIBLE);
            mTvRemind.setVisibility(View.GONE);
        }*/
    }

    /**
     * 打开评论对话框
     * @param data 评论数据
     */
    private void triggerCommentDialog(CommentDialog.CommentData data){
        if(EmptyUtil.isEmpty(mCommentEntity)){
            // 数据未准备好
            return;
        }

        if (!UserHelper.isLogin()) {
            // 验证是否登录,没有登录,请求登录
            LoginHelper.enterLogin(getActivity());
            return;
        }

        int currentScort = -1;
        if (mCommentEntity.getStarLevel() > 0) {//已经评过分了
            currentScort = mCommentEntity.getStarLevel();
        }
        int commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        if (getActivity() instanceof ClassDetailActivity || OnlineClassRole.ROLE_PARENT.equals(mRole)) {
            // 如果是家长身份,也是低优先级
            commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        } else if (getActivity() instanceof JoinClassDetailActivity) {
            commentType = CommentDialog.TYPE_COMMENT_HIGH_PERMISSION;
        }
        mCommentDialog = new CommentDialog(getActivity(), currentScort, commentType,OnlineClassRole.ROLE_PARENT.equals(mRole), data, new CommentDialog.CommitCallBack() {
            @Override
            public void dismiss(CommentDialog.CommentData module) {
                // 课程评价片段显示
                Activity activity = getActivity();
                // 记录当前文本
                ClassCommentFragment.this.mCommentData = module;
                mCommentContent.setText(module.getContent());
                if (activity instanceof CourseDetailsNavigator) {
                    // 回调接口,显示课程评价,隐藏按钮
                    CourseDetailsNavigator navigator = (CourseDetailsNavigator) activity;
                    navigator.setContent(module);
                }
            }

            @Override
            public void triggerSend(CommentDialog.CommentData module) {
                ClassCommentFragment.this.mCommentData = module;
                if (mCommentDialog.isShowing()) {
                    mCommentDialog.dismiss();
                    commitComment(module);
                }
            }
        });

        if (mCommentDialog != null && !mCommentDialog.isShowing()) {
            Window window = mCommentDialog.getWindow();
            mCommentDialog.show();
            window.setGravity(Gravity.BOTTOM);
        }
    }

    /**
     * 提交评论
     *
     * @param data 评论内容和评分
     */
    public void commitComment(CommentDialog.CommentData data) {
        if (null == data || TextUtils.isEmpty(data.getContent())) {
            UIUtil.showToastSafe(R.string.enter_evaluation_content_please);
            return;
        }

        if(!EmptyUtil.isEmpty(mClassDetailEntity.getData())){
            ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);
            mPresenter.requestCommitComment(0,dataBean.getId(),null,data.getContent(),data.getScort());
        }
    }

    @Override
    public void commitCommentResult(boolean isSucceed) {
        // 清除评论区域的内容
        mCommentContent.getText().clear();
        mCommentData = null;
        // 重新加载评论
        mPresenter.requestOnlineClassCommentData(id,0);
        // 通知Activity 头部刷新UI
        if(getActivity() instanceof BaseClassDetailNavigator){
            BaseClassDetailNavigator navigator = (BaseClassDetailNavigator) getActivity();
            navigator.refreshData();
        }
        if(isSucceed){
            // 隐藏软件盘
            KeyboardUtil.hideSoftInput(getActivity());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 在线课堂班级评论显示
        Activity activity = getActivity();
        if(activity instanceof ClassDetailNavigator){
            ClassDetailNavigator navigator = (ClassDetailNavigator) activity;
            navigator.onCommentChanged(!getUserVisibleHint());
        }

        if(activity instanceof JoinClassDetailNavigator){
            JoinClassDetailNavigator navigator = (JoinClassDetailNavigator) activity;
            navigator.updateCommentVisibility(!getUserVisibleHint());
        }

        if(EmptyUtil.isNotEmpty(mCommentLayout) && getActivity() instanceof ClassDetailActivity){
            // 第一次进入的时候 评论布局还没有实例化  已经加入不需要处理评论区域的显示与隐藏
            if(getUserVisibleHint()){
                // 显示评论
                mCommentLayout.setVisibility(View.VISIBLE);
            }else{
                // 隐藏评论
                mCommentLayout.setVisibility(View.GONE);
            }
        }

        if(getUserVisibleHint()){
            // 显示了课堂简介
            if(getActivity() instanceof BaseClassDetailActivity){
                BaseClassDetailActivity parentActivity = (BaseClassDetailActivity) getActivity();
                parentActivity.addRefreshView(mRecycler);
                parentActivity.getRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initData();
                    }
                });
            }
        }
    }
}
