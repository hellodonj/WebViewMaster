package com.lqwawa.intleducation.base;

import android.content.Context;
import android.support.v4.app.DialogFragment;

import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function Dialog Fragment MVP模式的包装
 * @date 2018/04/08 10:18
 * @history v1.0
 * **********************************
 */
public abstract class PresenterDialogFragment<Presenter extends BaseContract.Presenter> extends DialogFragment
        implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 在界面onAttach之后就触发初始化Presenter
        initPresenter();
    }

    /**
     * 初始化Presenter
     *
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 显示错误, 优先使用占位布局
        UIUtil.showToastSafe(str);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void setPresenter(Presenter presenter) {
        // View中赋值Presenter
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.destroy();
    }
}
