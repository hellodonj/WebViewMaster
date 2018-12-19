package com.galaxyschool.app.wawaschool.slide;

import com.libs.yilib.pickimages.PickMediasParam;

import java.io.Serializable;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 15, 2015 2:17:15 PM
 * 类说明
 */
public class SlideParam implements Serializable {
	public boolean mNeedCachePaintView;
	public PickImagesInputParam mPickImagesInputParam;
	public MenuActionParam mMenuActionParam;
	
	/**
	 * pass pick images param to engine
	 * if not set, will pick single image,
	 * and action is like adding audio recorder
	 */
	public static class PickImagesInputParam implements Serializable {
		public boolean mIs1Page1Image;
		public PickMediasParam mPickImagesParam;
	}
	
	public static class MenuActionParam implements Serializable {
		public boolean mIsUseRayMenu;
		public boolean mIsShowThumbnails;
	}
}
