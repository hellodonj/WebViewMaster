package com.lqwawa.lqbaselib.net;

/**
 * Created by shouyi on 5/14/15.
 */
public interface NetResultListener {

    public void onSuccess(Object data);
    public void onError(String message);
    public void onFinish();

}

