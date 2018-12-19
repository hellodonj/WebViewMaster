package com.lqwawa.libs.appupdater;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

public class AppInfo implements Parcelable {

	public static final int STATE_NONE = -1;
	/** 开始下载 */
	public static final int STATE_STARTED = 0;
	/** 等待中 */
	public static final int STATE_WAITING = 1;
	/** 下载中 */
	public static final int STATE_DOWNLOADING = 2;
	/** 暂停下载 */
	public static final int STATE_PAUSED = 3;
	/** 下载完毕 */
	public static final int STATE_DOWNLOADED = 4;
	/** 下载失败 */
	public static final int STATE_ERROR = 5;
	/** 删除下载 */
	public static final int STATE_DELETED = 6;

	private String id;
	private String appName;
	private String appIcon;
	private String packageName;
	private String versionName;
	private int versionCode;
	private String description;
	private boolean isForcedUpdate;
	private String fileUrl;
	private long fileSize;
	private String fileChecksum;
	private long modifiedTime;
	private String filePath;
	private long downloadedSize;
	private int downloadState = STATE_NONE;
	private File rootDir;

	public AppInfo() {

	}

	public AppInfo(Parcel src) {
		this.id = src.readString();
		this.appName = src.readString();
		this.appIcon = src.readString();
		this.packageName = src.readString();
		this.versionName = src.readString();
		this.versionCode = src.readInt();
		this.description = src.readString();
		this.isForcedUpdate = src.readInt() != 0;
		this.fileUrl = src.readString();
		this.fileSize = src.readLong();
		this.fileChecksum = src.readString();
		this.modifiedTime = src.readLong();
		this.filePath = src.readString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isForcedUpdate() {
		return isForcedUpdate;
	}

	public void setForcedUpdate(boolean isForcedUpdate) {
		this.isForcedUpdate = isForcedUpdate;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileChecksum() {
		return fileChecksum;
	}

	public void setFileChecksum(String fileChecksum) {
		this.fileChecksum = fileChecksum;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getFilePath() {
		if (TextUtils.isEmpty(this.filePath)) {
			if (this.rootDir == null) {
				throw new RuntimeException("No root directory");
			}
			this.filePath = generateFilePath();
		}
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getDownloadedSize() {
		return downloadedSize;
	}

	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

	public boolean isDownloadLapsed() {
		return this.downloadState == STATE_NONE;
	}

	public boolean isDownloadWaiting() {
		return this.downloadState == STATE_WAITING;
	}

	public boolean isDownloadStarted() {
		return this.downloadState == STATE_STARTED;
	}

	public boolean isDownloading() {
		return this.downloadState == STATE_DOWNLOADING;
	}

	public boolean isDownloadPaused() {
		return this.downloadState == STATE_PAUSED;
	}

	public boolean isDownloaded() {
		return this.downloadState == STATE_DOWNLOADED;
	}

	public boolean isDownloadDeleted() {
		return this.downloadState == STATE_DELETED;
	}

	public boolean isDownloadFailed() {
		return this.downloadState == STATE_ERROR;
	}

	public void setRootDir(File dir) throws NullPointerException {
		if (dir == null) {
			throw new NullPointerException();
		}
		this.rootDir = dir;
		if (TextUtils.isEmpty(this.filePath)) {
			this.filePath = generateFilePath();
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dst, int flags) {
		dst.writeString(this.id);
		dst.writeString(this.appName);
		dst.writeString(this.appIcon);
		dst.writeString(this.packageName);
		dst.writeString(this.versionName);
		dst.writeInt(this.versionCode);
		dst.writeString(this.description);
		dst.writeInt(this.isForcedUpdate ? 1 : 0);
		dst.writeString(this.fileUrl);
		dst.writeLong(this.fileSize);
		dst.writeString(this.fileChecksum);
		dst.writeLong(this.modifiedTime);
		dst.writeString(this.filePath);
	}

	public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
		@Override
		public AppInfo createFromParcel(Parcel src) {
			return new AppInfo(src);
		}

		@Override
		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
	};

	public String generateFileName() {
		StringBuilder builder = new StringBuilder(this.packageName);
        builder.append("-").append(this.versionCode);
		if (!TextUtils.isEmpty(this.versionName)) {
			builder.append("-").append(this.versionName);
		}
        return builder.append(".apk").toString();
	}

	public String generateFilePath() {
        return new File(this.rootDir, generateFileName()).getAbsolutePath();
	}

}
