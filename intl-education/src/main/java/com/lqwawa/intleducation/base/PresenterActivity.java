package com.lqwawa.intleducation.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.tools.DialogHelper;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 记得写注释喲
 * @date 2018/04/08 10:27
 * @history v1.0
 * **********************************
 */
public abstract class PresenterActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    protected DialogHelper.LoadingDialog mLoadingDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        // 初始化Presenter
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 界面关闭时进行销毁的操作
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    /**
     * 初始化Presenter
     *
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 不管你怎么样，我先隐藏我
        hideDialogLoading();

        // 显示错误, 优先使用占位布局
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            UIUtil.showToastSafe(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null && mPlaceHolderView.isShowLoading()) {
            mPlaceHolderView.triggerLoading();
        } else {
            DialogHelper.LoadingDialog dialog = mLoadingDialog;
            if (dialog == null) {
                dialog = DialogHelper.getIt(this).GetLoadingDialog(0);
                dialog.setCanceledOnTouchOutside(false);
                mLoadingDialog = dialog;
            }

            dialog.setContent(getText(R.string.prompt_loading).toString());
            dialog.show();
        }
    }

    protected void hideDialogLoading() {
        DialogHelper.LoadingDialog dialog = mLoadingDialog;
        if (dialog != null) {
            mLoadingDialog = null;
            dialog.dismiss();
        }
    }

    protected void hideLoading() {
        // 不管你怎么样，我先隐藏我
        hideDialogLoading();

        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        // View中赋值Presenter
        mPresenter = presenter;
    }
}
