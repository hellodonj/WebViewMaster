package com.libs.yilib.pickimages;

import java.util.ArrayList;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 3, 2015 11:06:42 AM 类说明
 */
public interface PickMediaResultListener {
	public void onPickFinished(ArrayList<MediaInfo> imageInfos);
}
