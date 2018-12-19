package com.lqwawa.lqbaselib.net;

import com.duowan.mobile.netroid.Listener;
import com.lqwawa.lqbaselib.net.library.MyStringRequest;
import com.osastudio.common.utils.LogUtils;

/**
 * @author 作者 shouyi
 * @version 创建时间：Aug 24, 2015 5:17:24 PM
 * 类说明
 */
public class ThisStringRequest extends MyStringRequest {

	public ThisStringRequest(int mothed, String url, Listener listener) {
		super(mothed, url, listener);
		LogUtils.log("TEST",url);
	}

}
