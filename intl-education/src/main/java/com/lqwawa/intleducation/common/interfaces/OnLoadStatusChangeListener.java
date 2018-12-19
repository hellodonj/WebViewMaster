package com.lqwawa.intleducation.common.interfaces;

/**
 * Created by XChen on 2016/11/25.
 * email:man0fchina@foxmail.com
 */

public interface OnLoadStatusChangeListener {
    public void onLoadSuccess();

    public void onLoadFlailed();

    public void onLoadFinish(boolean canLoadMore);

    public void onCommitComment();
}