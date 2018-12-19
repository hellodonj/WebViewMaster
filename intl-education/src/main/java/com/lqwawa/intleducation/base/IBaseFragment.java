package com.lqwawa.intleducation.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lqwawa.intleducation.common.utils.LogUtil;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function Fragment基类
 * @date 2018/04/08 10:07
 * @history v1.0
 * **********************************
 */
public abstract class IBaseFragment extends Fragment
    implements NetErrorView.OnReloadDataListener{

    protected static final String FRAGMENT_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";

    protected final String TAG = this.getClass().getCanonicalName();

    protected PlaceHolderView mPlaceHolderView;

    protected View mRootView = null;

    // 标示是否第一次初始化数据
    protected boolean mIsFirstInitData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(this.getClass()," ======== onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        LogUtil.i(this.getClass()," ======== onAttach");

        LogUtil.i(this.getClass()," ======== initArgs");
        // 初始化参数
        initArgs(getArguments());

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(this.getClass()," ======== onStart");
    }

    @Override
    public void onResume() {
        LogUtil.i(this.getClass()," ======== onResume");
        super.onResume();
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            int layId = getContentLayoutId();
            // 初始化当前的跟布局，但是不在创建时就添加到container里边
            View view = inflater.inflate(layId, container, false);
            mRootView = view;
            LogUtil.i(this.getClass(),TAG+" ======== initWidget ");
            initWidget();
        } else {
            if (mRootView.getParent() != null) {
                // 把当前Root从其父控件中移除
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
        }

        return mRootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(this.getClass()," ======== onPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(this.getClass()," ======== onStop");
    }

    @LayoutRes
    /**
     * 获取布局Id
     * @return
     */
    protected abstract int getContentLayoutId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData) {
            // 触发一次以后就不会触发
            mIsFirstInitData = false;
            // 触发
            onFirstInit();
        }
        // 当View创建完成后初始化数据
        LogUtil.i(this.getClass()," ======== initData");
        initData();
    }

    /**
     * 初始化Args数据
     * @param bundle
     * @return 初始化成功或者失败
     */
    protected boolean initArgs(Bundle bundle){
        return true;
    }

    /**
     * 初始化View
     */
    protected void initWidget(){

    }

    /**
     * 初始化数据
     */
    protected void initData(){

    }

    @Override
    /**
     * 网络错误时，点击重新加载
     */
    public void reloadData() {

    }

    /**
     * 当首次初始化数据的时候会调用的方法
     */
    protected void onFirstInit() {

    }

    /**
     * 设置占位布局
     *
     * @param placeHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
        this.mPlaceHolderView.setOnReloadDataListener(this);
    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish。
     * 返回False代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(this.getClass()," ======== onDestroyView");
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(this.getClass()," ======== onDestroy");
        mRootView = null;
    }

}
