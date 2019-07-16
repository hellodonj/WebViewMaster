package com.lqwawa.libs.filedownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.lqwawa.libs.filedownloader.database.DownloadDatabaseHelper;
import com.lqwawa.libs.filedownloader.database.FileInfoDao;
import com.osastudio.common.utils.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadManager {

	public static final String ROOT_DIR_NAME = ".FileDownloader";

	private static File rootDir;

	private Context context;
	private Map<String, Map<String, DownloadListener>> listeners =
			new ConcurrentHashMap<String, Map<String, DownloadListener>>();
	private Map<String, DownloadTask> tasks = new ConcurrentHashMap<String, DownloadTask>();
	private FileInfo fileInfo;
	private Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<String, FileInfo>();
	private DownloadDatabaseHelper databaseHelper;


	public DownloadManager(Context context) {
		this.context = context;
		if (rootDir == null) {
			rootDir = new File(context.getExternalCacheDir(), ROOT_DIR_NAME);
		}
		this.databaseHelper = new DownloadDatabaseHelper(context, rootDir);
	}

	public static void setRootDir(File dir) {
		if (dir != null) {
			if (!dir.exists() && !dir.mkdirs()) {
				return;
			}
			rootDir = dir;
		}
	}

	private FileInfoDao getFileInfoDao() {
		if (this.databaseHelper != null) {
			return this.databaseHelper.getFileInfoDao();
		}
		return null;
	}

	private DownloadListener getListener(Class clazz, String id) {
		Map<String, DownloadListener> map = listeners.get(id);
		if (map != null) {
			return map.get(clazz.getSimpleName());
		}
		return null;
	}

	public void addListener(Class clazz, String id, DownloadListener listener) {
		Map<String, DownloadListener> map = listeners.get(id);
		if (map == null) {
			map = new ConcurrentHashMap<String, DownloadListener>();
		}
		map.put(clazz.getSimpleName(), listener);
		listeners.put(id, map);
	}

	public void removeListener(Class clazz, String id) {
		Map<String, DownloadListener> map = listeners.get(id);
		if (map != null) {
			map.remove(clazz.getSimpleName());
		}
	}

	public void removeListeners(Class clazz) {
		if (listeners.size() <= 0) {
			return;
		}

		String className = clazz.getSimpleName();
		Map<String, DownloadListener> map = null;
		for (Map.Entry<String, Map<String, DownloadListener>> entry : listeners.entrySet()) {
			map = entry.getValue();
			if (map != null) {
				map.remove(className);
			}
		}
	}

	public void clearListeners() {
		for (Map.Entry<String, Map<String, DownloadListener>> entry : listeners.entrySet()) {
			entry.getValue().clear();
		}
		listeners.clear();
	}

	private void notifyDownloadStateChanged(FileInfo fileInfo) {
		Message message = handler.obtainMessage();
		message.obj = fileInfo;
		handler.sendMessage(message);
	}

	private DownloadHandler handler = new DownloadHandler();

	@SuppressLint("HandlerLeak")
	private class DownloadHandler extends Handler {
		private DownloadHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			FileInfo fileInfo = (FileInfo) msg.obj;
			Map<String, DownloadListener> map = listeners.get(fileInfo.getFileId());
			if (map == null || map.size() <= 0) {
				return;
			}
			DownloadListener listener = null;
			for (Map.Entry<String, DownloadListener> entry : map.entrySet()) {
				listener = entry.getValue();
				switch (fileInfo.getDownloadState()) {
				case FileInfo.STATE_STARTED:
					listener.onStart(fileInfo);
					break;
				case FileInfo.STATE_WAITING:
					listener.onPrepare(fileInfo);
					break;
				case FileInfo.STATE_DOWNLOADING:
					listener.onProgress(fileInfo);
					break;
				case FileInfo.STATE_PAUSED:
					listener.onPause(fileInfo);
					break;
				case FileInfo.STATE_DOWNLOADED:
					listener.onFinish(fileInfo);
					break;
				case FileInfo.STATE_ERROR:
					listener.onError(fileInfo);
					break;
				case FileInfo.STATE_DELETED:
					listener.onDelete(fileInfo);
					break;
				}
			}
		}
	}

	public List<FileInfo> getFileInfoList(String userId) {
	    return getFileInfoList(userId, null, true);
    }

	public List<FileInfo> getFileInfoList(String userId, boolean fixCorruption) {
	    return getFileInfoList(userId, null, fixCorruption);
    }

	public List<FileInfo> getFileInfoList(String userId, String fileName) {
	    return getFileInfoList(userId, fileName, true);
    }

    public List<FileInfo> getFileInfoList(String userId, String fileName, boolean fixCorruption) {
		List<FileInfo> fileInfoList = getFileInfoDao().getUserFileList(userId, fileName);
		if (fileInfoList != null && fileInfoList.size() > 0) {
			for (FileInfo fileInfo : fileInfoList) {
				if (!fileInfoMap.containsKey(fileInfo.getFileId())) {
					checkFileInfoNotInCache(fileInfo, fixCorruption);
					fileInfoMap.put(fileInfo.getFileId(), fileInfo);
				} else {
					fileInfo = fileInfoMap.get(fileInfo.getFileId());
					checkFileInfoInCache(fileInfo, fixCorruption);
				}
			}
		}
		return fileInfoList;
	}

	public FileInfo getFileInfo(String userId, String fileId) {
	    return getFileInfo(userId, fileId, true);
    }

	public FileInfo getFileInfo(String userId, String fileId, boolean fixCorruption) {
//		FileInfo fileInfo = fileInfoMap.get(fileId);
//		if (fileInfo == null) {
//			if (getFileInfoDao() != null) {
//				fileInfo = getFileInfoDao().getUserFile(userId, fileId);
//			}
//			if (fileInfo != null) {
//				checkFileInfoNotInCache(fileInfo);
//			}
//		} else {
//			checkFileInfoInCache(fileInfo);
//		}
		FileInfo fileInfo = fileInfoMap.get(fileId);
		if (fileInfo == null) {
			if (getFileInfoDao() != null) {
				fileInfo = getFileInfoDao().getUserFile(userId, fileId);
			}
			if (fileInfo != null) {
				checkFileInfoNotInCache(fileInfo, fixCorruption);
				fileInfoMap.put(fileInfo.getFileId(), fileInfo);
			}
		} else {
			if(!TextUtils.isEmpty(fileInfo.getUserId()) && !TextUtils.isEmpty(userId)
					&& fileInfo.getUserId().equals(userId)){
				checkFileInfoInCache(fileInfo, fixCorruption);
			} else {
				if (getFileInfoDao() != null) {
					fileInfo = getFileInfoDao().getUserFile(userId, fileId);
				}
				if (fileInfo != null) {
					checkFileInfoNotInCache(fileInfo, fixCorruption);
					fileInfoMap.put(fileInfo.getFileId(), fileInfo);
				}
			}
		}
		return fileInfo;
	}

	private void checkFileInfoNotInCache(FileInfo fileInfo) {
		checkFileInfoNotInCache(fileInfo, true);
    }

	private void checkFileInfoNotInCache(FileInfo fileInfo, boolean fixCorruption) {
		if (!fixCorruption) {
			return;
		}

		String filePath = fileInfo.getFilePath();
		File file = new File(filePath);
		if (file.exists()) {
			fileInfo.setDownloadedSize(file.length());
			if (file.length() == fileInfo.getFileSize()) {
				fileInfo.setDownloadState(FileInfo.STATE_DOWNLOADED);
			} else {
				if (fileInfo.isDownloadWaiting() || fileInfo.isDownloadStarted()
						|| fileInfo.isDownloading()) {
					fileInfo.setDownloadState(FileInfo.STATE_PAUSED);
				}
			}
		} else {
			if (fileInfo.isDownloadStarted() || fileInfo.isDownloading()
					|| fileInfo.isDownloadPaused()) {
				fileInfo.setDownloadState(FileInfo.STATE_ERROR);
			} else if (fileInfo.isDownloaded()) {
				fileInfo.setDownloadState(FileInfo.STATE_NONE);
			} else if (fileInfo.isDownloadWaiting()) {
				fileInfo.setDownloadState(FileInfo.STATE_PAUSED);
			}
			fileInfo.setDownloadedSize(0);
		}
	}

	private void checkFileInfoInCache(FileInfo fileInfo) {
	    checkFileInfoInCache(fileInfo, true);
    }

	private void checkFileInfoInCache(FileInfo fileInfo, boolean fixCorruption) {
		if (!fixCorruption) {
			return;
		}

		String filePath = fileInfo.getFilePath();
		File file = new File(filePath);
		if (file.exists()) {
			if (file.length() == fileInfo.getFileSize()) {
				fileInfo.setDownloadState(FileInfo.STATE_DOWNLOADED);
			}
		} else {
			if (fileInfo.isDownloadStarted() || fileInfo.isDownloading()
					|| fileInfo.isDownloadPaused()) {
				if (fileInfo.getDownloadedSize() > 0) {
					fileInfo.setDownloadState(FileInfo.STATE_ERROR);
				}
			} else if (fileInfo.isDownloaded()) {
				fileInfo.setDownloadState(FileInfo.STATE_NONE);
			}
			fileInfo.setDownloadedSize(0);
		}
	}

	public void downloadFile(FileInfo fileInfo) {
		fileInfo.setRootDir(this.rootDir);
		FileInfo downloadFileInfo = getFileInfo(fileInfo.getUserId(), fileInfo.getFileId());
		if (downloadFileInfo == null) {
			File file = new File(fileInfo.getFilePath());
			if (file.exists()) {
				file.delete();
			}
			downloadFileInfo = fileInfo;
			if (getFileInfoDao() != null) {
				getFileInfoDao().addOrUpdateFile(downloadFileInfo);
			}
			fileInfoMap.put(downloadFileInfo.getFileId(), downloadFileInfo);
		}

		if (downloadFileInfo.getDownloadState() == FileInfo.STATE_NONE
				|| downloadFileInfo.getDownloadState() == FileInfo.STATE_PAUSED
				|| downloadFileInfo.getDownloadState() == FileInfo.STATE_DELETED
				|| downloadFileInfo.getDownloadState() == FileInfo.STATE_ERROR) {
			downloadFileInfo.setDownloadState(FileInfo.STATE_WAITING);
			downloadFileInfo.setTimestamp(System.currentTimeMillis());
			if (getFileInfoDao() != null) {
				getFileInfoDao().addOrUpdateFile(downloadFileInfo);
			}
			notifyDownloadStateChanged(downloadFileInfo);

			DownloadTask task = new DownloadTask(downloadFileInfo);
			tasks.put(downloadFileInfo.getFileId(), task);

			DownloadExecutor.execute(task);
		} else if (downloadFileInfo.getDownloadState() == FileInfo.STATE_STARTED
				|| downloadFileInfo.getDownloadState() == FileInfo.STATE_DOWNLOADING
				|| downloadFileInfo.getDownloadState() == FileInfo.STATE_WAITING) {
			if (tasks.containsKey(downloadFileInfo.getFileId())) {
				DownloadTask task = tasks.get(downloadFileInfo.getFileId());
				task.fileInfo.setDownloadState(FileInfo.STATE_PAUSED);
				if (getFileInfoDao() != null) {
					getFileInfoDao().addOrUpdateFile(task.fileInfo);
				}

				if (DownloadExecutor.cancel(task)) {
					Map<String, DownloadListener> map = listeners.get(fileInfo.getFileId());
					if (map == null || map.size() <= 0) {
						return;
					}
					DownloadListener listener = null;
					for (Map.Entry<String, DownloadListener> entry : map.entrySet()) {
						listener = entry.getValue();
                        listener.onPause(task.fileInfo);
					}
				}
			}
		}
	}

	private class DownloadTask implements Runnable {
		public FileInfo fileInfo;

		public DownloadTask(FileInfo fileInfo) {
			this.fileInfo = fileInfo;
		}

		@Override
		public void run() {
			if (fileInfo.getDownloadState() == FileInfo.STATE_PAUSED) {
				fileInfo.setDownloadState(FileInfo.STATE_PAUSED);
				if (getFileInfoDao() != null) {
					getFileInfoDao().addOrUpdateFile(fileInfo);
				}
				return;
			} else if (fileInfo.getDownloadState() == FileInfo.STATE_DELETED) {
				fileInfo.setDownloadState(FileInfo.STATE_DELETED);
				if (getFileInfoDao() != null) {
					getFileInfoDao().addOrUpdateFile(fileInfo);
				}
				notifyDownloadStateChanged(fileInfo);
				tasks.remove(fileInfo.getFileId());
				return;
			}

			fileInfo.setDownloadState(FileInfo.STATE_STARTED);
			if (getFileInfoDao() != null) {
				getFileInfoDao().addOrUpdateFile(fileInfo);
			}
			notifyDownloadStateChanged(fileInfo);

			long downloadedSize = 0;
			File file = new File(fileInfo.getFilePath());
			if (!file.exists()) {
				fileInfo.setDownloadedSize(0);
				try {
					file.getParentFile().mkdirs();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				downloadedSize = file.length();
			}
			try {
				URL url = new URL(fileInfo.getFileUrl());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(60 * 1000);
				connection.setReadTimeout(60 * 1000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Range", "bytes=" + downloadedSize
						+ "-" + fileInfo.getFileSize());
				connection.connect();
				if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
					url = new URL(connection.getHeaderField("Location"));
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("Connection", "close");
					connection.setConnectTimeout(30 * 1000);
					connection.setReadTimeout(30 * 1000);
					connection.connect();
				}
				Utils.log("DOWNLOADER", downloadedSize + "/" + fileInfo.getFileSize()
						+ " " + fileInfo.getFileUrl());

				// 判断是否支持断点下载
                int responseCode = connection.getResponseCode();
				if ((downloadedSize > 0 && responseCode == HttpURLConnection.HTTP_PARTIAL)
						|| (downloadedSize == 0 && (responseCode == HttpURLConnection.HTTP_PARTIAL
                            || responseCode == HttpURLConnection.HTTP_OK))) {
					@SuppressWarnings("resource")
					OutputStream outputStream = new FileOutputStream(file, true);
					InputStream is = connection.getInputStream();
					byte[] buffer = new byte[102400];
					int length = -1;
//					if (connection.getContentLength() > 0
//							&& fileInfo.getFileSize() != connection.getContentLength()) {
//						fileInfo.setFileSize(connection.getContentLength());
//					}
					fileInfo.setDownloadState(FileInfo.STATE_DOWNLOADING);
					if (getFileInfoDao() != null) {
						getFileInfoDao().addOrUpdateFile(fileInfo);
					}
					notifyDownloadStateChanged(fileInfo);

					long lastMillis = System.currentTimeMillis();
					while ((length = is.read(buffer)) != -1) {
						if (fileInfo.getDownloadState() == FileInfo.STATE_PAUSED) {
							Utils.log("DOWNLOADER", "Downloading paused");
							fileInfo.setDownloadState(FileInfo.STATE_PAUSED);
							if (getFileInfoDao() != null) {
								getFileInfoDao().addOrUpdateFile(fileInfo);
							}
							notifyDownloadStateChanged(fileInfo);
							return;
						} else if (fileInfo.getDownloadState() == FileInfo.STATE_DELETED) {
							Utils.log("DOWNLOADER", "Downloading deleted");
							fileInfo.setDownloadState(FileInfo.STATE_DELETED);
							fileInfo.setDownloadedSize(0);
							if (getFileInfoDao() != null) {
								getFileInfoDao().addOrUpdateFile(fileInfo);
							}
							notifyDownloadStateChanged(fileInfo);
							tasks.remove(fileInfo.getFileId());
							return;
						} else if (fileInfo.getDownloadState() == FileInfo.STATE_ERROR) {
							Utils.log("DOWNLOADER", "Downloading error: "
									+ downloadedSize + "/" + fileInfo.getFileSize());
							fileInfo.setDownloadState(FileInfo.STATE_ERROR);
							fileInfo.setDownloadedSize(downloadedSize);
							if (getFileInfoDao() != null) {
								getFileInfoDao().addOrUpdateFile(fileInfo);
							}
							notifyDownloadStateChanged(fileInfo);
							tasks.remove(fileInfo.getFileId());
							return;
						}
						DownloadManager.this.fileInfo = fileInfo;
						outputStream.write(buffer, 0, length);
						downloadedSize += length;
						fileInfo.setDownloadedSize(downloadedSize);
						long currMillis = System.currentTimeMillis();
						if (currMillis - lastMillis > 500) {
							lastMillis = currMillis;
							notifyDownloadStateChanged(fileInfo);
						}
					}
					if (fileInfo.getFileSize() == fileInfo.getDownloadedSize()) {
						Utils.log("DOWNLOADER", "File downloaded");
						fileInfo.setDownloadState(FileInfo.STATE_DOWNLOADED);
						if (getFileInfoDao() != null) {
							getFileInfoDao().addOrUpdateFile(fileInfo);
						}
						notifyDownloadStateChanged(fileInfo);
					} else {
						Utils.log("DOWNLOADER", "File not entirely downloaded: "
								+ downloadedSize + "/" + fileInfo.getFileSize());
						fileInfo.setDownloadState(FileInfo.STATE_ERROR);
						fileInfo.setDownloadedSize(downloadedSize);
						if (getFileInfoDao() != null) {
							getFileInfoDao().addOrUpdateFile(fileInfo);
						}
						notifyDownloadStateChanged(fileInfo);
					}
				} else {
					Utils.log("DOWNLOADER", "Partial downloading not supported: "
							+ connection.getResponseCode());
				}
			} catch (IOException e) {
				Utils.log("DOWNLOADER", "IOException happened in downloading");
				fileInfo.setDownloadState(FileInfo.STATE_ERROR);
				if (getFileInfoDao() != null) {
					getFileInfoDao().addOrUpdateFile(fileInfo);
				}
				notifyDownloadStateChanged(fileInfo);
			}
			tasks.remove(fileInfo.getFileId());
		}
	}

	public void deleteFile(FileInfo fileInfo) {
		if (tasks.containsKey(fileInfo.getFileId())) {
			DownloadTask task = tasks.get(fileInfo.getFileId());
			task.fileInfo.setDownloadState(FileInfo.STATE_DELETED);
		}

		fileInfo.setDownloadState(FileInfo.STATE_DELETED);
		notifyDownloadStateChanged(fileInfo);

		if (getFileInfoDao() != null) {
			getFileInfoDao().deleteUserFile(fileInfo.getUserId(), fileInfo.getFileId());
		}
		
		File file = new File(fileInfo.getFilePath());
		if (file.exists()) {
			file.delete();
		}
	}

	public void destroy() {
		DownloadExecutor.stop();
		listeners.clear();
		tasks.clear();
		if (fileInfo != null) {
			fileInfo.setDownloadState(FileInfo.STATE_PAUSED);
			if (getFileInfoDao() != null) {
				getFileInfoDao().addOrUpdateFile(fileInfo);
			}
			fileInfo = null;
		}
		fileInfoMap.clear();
		databaseHelper.close();
		databaseHelper = null;
	}

}
