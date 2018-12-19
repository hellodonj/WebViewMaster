package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author 作者 shouyi
 * @version 创建时间：Sep 9, 2015 11:40:08 AM 类说明
 */
public class DownloadChwDto implements Serializable {
	@DatabaseField(id = true)
	private String mPath;
	@DatabaseField()
	private long mFileSize;
	@DatabaseField(width = 30)
	private String mOpenTime;

	public String getPath() {
		return mPath;
	}

	public void setPath(String mPath) {
		this.mPath = mPath;
	}

	public long getFileSize() {
		return mFileSize;
	}

	public void setFileSize(long mFileSize) {
		this.mFileSize = mFileSize;
	}

	public String getOpenTime() {
		return mOpenTime;
	}

	public void setOpenTime(String mPlayTime) {
		this.mOpenTime = mPlayTime;
	}
}
