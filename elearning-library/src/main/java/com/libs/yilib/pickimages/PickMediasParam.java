package com.libs.yilib.pickimages;

import java.io.Serializable;
import java.util.List;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 3, 2015 11:22:42 AM 类说明
 */
public class PickMediasParam implements Serializable {
	/**
	 * set true for activity, false for fragment.
	 */
	public boolean mIsActivityCalled = true;
	/**
	 * search image path, /mnt if not set.
	 */
	public String mSearchPath;
	/**
	 * search media folder list
	 */
	public List<String> mSearchFolders;
	/**
	 * skip the searched file whose folder contains the keys.
	 */
	public List<String> mSkipKeysOfFolder;
	/**
	 * max num to pick, ignore set if no limit or set 0.
	 */
	public int mPickLimitCount;
	/**
	 * if select more image will show tips after limit reached.
	 * will not show if not set.
	 */
	public String mLimitReachedTips;
	/**
	 * pick image view tilte
	 */
	public String mTitle;
	/**
	 * confirm button name
	 */
	public String mConfirmBtnName;
	/**
	 * default image id, will show it before decode image.
	 */
	public int mDefaultImage;
	/**
	 * grid view columns, will ignore this if set column width.
	 */
	public int mColumns;
	/**
	 * grid view colum width, will ignore columns value if set it.
	 */
	public int mColumnWidth;
	/**
	 * 0 not show, 1 show left count, 2 show selected count.
	 */
	public int mShowCountMode;
	/**
	 * format string for show count, if set will ignore title value.
	 */
	public String mShowCountFormatString;
	/**
	 * media type from MediaType.java, indicate which media to pick
	 */
	public int mMediaType;
	/**
	 * show medias by the sort type, -1 not sort, 0 sort by time, other value realize later.
	 */
	public int mSortType;

	public boolean ismIsActivityCalled() {
		return mIsActivityCalled;
	}

	public void setmIsActivityCalled(boolean mIsActivityCalled) {
		this.mIsActivityCalled = mIsActivityCalled;
	}

	public int getmPickLimitCount() {
		return mPickLimitCount;
	}

	public void setmPickLimitCount(int mPickLimitCount) {
		this.mPickLimitCount = mPickLimitCount;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmConfirmBtnName() {
		return mConfirmBtnName;
	}

	public void setmConfirmBtnName(String mConfirmBtnName) {
		this.mConfirmBtnName = mConfirmBtnName;
	}

	public int getmDefaultImage() {
		return mDefaultImage;
	}

	public void setmDefaultImage(int mDefaultImage) {
		this.mDefaultImage = mDefaultImage;
	}

	public int getmColumns() {
		return mColumns;
	}

	public void setmColumns(int mColumns) {
		this.mColumns = mColumns;
	}
	
	
}
