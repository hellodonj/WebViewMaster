package com.lqwawa.intleducation.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.lqwawa.intleducation.common.utils.LogUtil;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function Activity基类
 * @date 2018/04/08 09:41
 * @history v1.0
 * **********************************
 */
public abstract class IBaseActivity extends AppCompatActivity
        implements NetErrorView.OnReloadDataListener{

    protected static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";

    protected final String TAG = this.getClass().getCanonicalName();

    protected PlaceHolderView mPlaceHolderView;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(this.getClass()," ======== onCreate");

        LogUtil.i(this.getClass()," ======== initWindow");
        initWindow();

        LogUtil.i(this.getClass()," ======== initArgs");
        if(initArgs(getIntent().getExtras())){

            setContentView();

            LogUtil.i(this.getClass()," ======== initBefore");
            initBefore();
            LogUtil.i(this.getClass()," ======== initWidget");
            initWidget();
            LogUtil.i(this.getClass()," ======== initData");
            initData();
        }else{
            finish();
        }


    }

    /**
     * 初始化有关Window的设置
     */
    protected void initWindow(){}

    /**
     * 初始化Bundle数据
     * @return
     */
    protected boolean initArgs(@NonNull Bundle bundle){
        return true;
    }

    /**
     * 设置ContentView布局
     */
    protected void setContentView(){
        int layoutId = getContentLayoutId();
        setContentView(layoutId);
    }

    @LayoutRes
    /**
     * 获取内容布局Id
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化监听
     */
    protected  void initBefore(){

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
     * 设置占位布局
     *
     * @param placeHolderView 继承了占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
        this.mPlaceHolderView.setOnReloadDataListener(this);
    }

    @Override
    public void onBackPressed() {
        // 得到当前Activity下的所有Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        // 判断是否为空
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                // 判断是否为我们能够处理的Fragment类型
                if (fragment instanceof IBaseFragment) {
                    // 判断是否拦截了返回按钮
                    if (((IBaseFragment) fragment).onBackPressed()) {
                        // 如果有直接Return
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i(this.getClass()," ======== onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.i(this.getClass()," ======== onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(this.getClass()," ======== onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(this.getClass()," ======== onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(this.getClass()," ======== onStop");
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(this.getClass()," ======== onDestroy");
    }

}
