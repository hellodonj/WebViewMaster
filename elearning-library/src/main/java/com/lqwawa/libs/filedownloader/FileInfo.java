package com.lqwawa.libs.filedownloader;

import android.text.TextUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.osastudio.common.library.Md5FileNameGenerator;

import java.io.File;
import java.util.UUID;

@DatabaseTable(tableName = "tbl_file_info")
public class FileInfo {

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

	@DatabaseField(id = true)
	private String id;

	@DatabaseField()
	private String userId;

	@DatabaseField(canBeNull = false)
	private String fileId;

	@DatabaseField()
	private String fileName;

	@DatabaseField()
	private String fileIcon;

	@DatabaseField(canBeNull = false)
	private String fileUrl;

	@DatabaseField(canBeNull = false)
	private long fileSize;

	@DatabaseField()
	private String fileChecksum;

	@DatabaseField(canBeNull = false)
	private String filePath;

	@DatabaseField()
	private long downloadedSize;

	@DatabaseField()
	private int downloadState = STATE_NONE;

	@DatabaseField()
	private long timestamp;

	private File rootDir;



	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public FileInfo() {
		this.timestamp = System.currentTimeMillis();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
		generateId();
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
		generateId();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileIcon() {
		return fileIcon;
	}

	public void setFileIcon(String fileIcon) {
		this.fileIcon = fileIcon;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
		generateId();
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

	public String getFilePath() {
		if (TextUtils.isEmpty(this.filePath)) {
			if (this.rootDir == null) {
				throw new RuntimeException("No root directory");
			}
			this.filePath = generateFilePath(this.rootDir, this.userId, this.fileUrl);
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setRootDir(File dir) throws NullPointerException {
		if (dir == null) {
			throw new NullPointerException();
		}
		this.rootDir = dir;
		if (TextUtils.isEmpty(this.filePath)) {
			this.filePath = generateFilePath(this.rootDir, this.userId, this.fileUrl);
		}
	}

	private String generateId() {
		if (!TextUtils.isEmpty(this.fileUrl)) {
			if (TextUtils.isEmpty(this.fileId)) {
				this.fileId = generateFileId(this.fileUrl);
			}
			this.id = generateId(this.userId, this.fileId);
		}
		return this.id;
	}

	public static String generateId(String userId, String fileId) {
		return UUID.nameUUIDFromBytes(new StringBuilder(
				userId).append(fileId).toString().getBytes()).toString();
	}

	public static String generateFileId(String fileUrl) {
		return Md5FileNameGenerator.generate(fileUrl);
	}

	public static String generateFileName(String fileUrl) {
		return Md5FileNameGenerator.generate(fileUrl);
	}

	public static String generateFilePath(File rootDir, String userId, String fileUrl) {
        File dir = rootDir;
        if (!TextUtils.isEmpty(userId)) {
            dir = new File(rootDir, userId);
            if (!dir.exists() && !dir.mkdirs()) {
                dir = rootDir;
            }
        }
        return new File(dir, generateFileName(fileUrl)).getAbsolutePath();
	}

}
