package com.libs.yilib.pickimages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.oosictech.library.mediaprovider.MediaListener;
import com.oosictech.library.mediaprovider.MediaProvider;
import com.osastudio.common.utils.LogUtils;

public class ScanLocalMediaController extends MediaListener {
	private MediaProvider m_MediaProvider;
	private boolean m_cancel = false;
	private int mMediaType;
	private List<String> mSkipFolderKeys;
	private ScanLocalMediaListener mListener;
	private SearchManager mSearchManager;

	public ScanLocalMediaController(int mediaType,
			List<String> mediaFormatList, ScanLocalMediaListener listener) {
		mMediaType = mediaType;
		mListener = listener;
		m_cancel = false;
		m_MediaProvider = new MediaProvider();
		m_MediaProvider.setMediaListener(this);
		m_MediaProvider.setSearchDepth(6);
		for (int i = 0; i < mediaFormatList.size(); i++) {
			m_MediaProvider.addFileExtends(mediaFormatList.get(i));
		}
	}
	
	public void setSkipKeysOfFolder(List<String> skipKeys) {
		mSkipFolderKeys = skipKeys;
	}

	public void cancel(boolean enable) {
		m_cancel = enable;
	}

	public void onFolderFound(String path, int count, String thumb_path) {
		// if (m_cancel) {
		// return;
		// }
		// // add to adapter
		// if (m_folder_adapter != null) {
		// m_folder_adapter.addFolder(path, count, thumb_path);
		// }
	}

	public void onFileFound(String path) {
		LogUtils.logi("", "onSearched file: " + path);
		if (m_cancel) {
			return;
		}
		
		if (path == null) {
			boolean scanNext = mSearchManager.scanNext();
			LogUtils.logi("", "onSearched scanNext: " + scanNext);
			if (scanNext) {
				return;
			}
		}

		if (mListener != null) {
			//if (path != null) {
			//	LogUtils.logi(null, "searchedFile: " + path);
			//}
			if (isNeedSkip(path)) {
				return;
			}
			LogUtils.logi("", "Searched one file: " + path);
			mListener.onSearched(path, mMediaType);
		}
	}
	
	protected boolean isNeedSkip(String path) {
		if (mSkipFolderKeys != null && path != null) {
			path = getFolder(path);
			for (int i = 0; i < mSkipFolderKeys.size(); i++) {
				if (path.contains(mSkipFolderKeys.get(i))) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected String getFolder(String path) {
		if (!TextUtils.isEmpty(path)) {
			int index = path.lastIndexOf("/");
			if (index > 0) {
				return path.substring(0, index);
			} else {
				return path;
			}
		}
		return null;
	}

	public void start(String path) {
		mSearchManager = new SearchManager(path);
		mSearchManager.startScan();
	}
	
	public void start(List<String> paths) {
		List<String> folders = getValidePaths(paths);
		mSearchManager = new SearchManager(folders);
		mSearchManager.startScan();
	}
	
	public void stop() {
		if (m_MediaProvider != null) {
			m_MediaProvider.cancel();
			m_MediaProvider.setMediaListener(null);
		}
		cancel(true);
		recycle();
	}
	
	private List<String> getValidePaths(List<String> paths) {
		List<String> result = new ArrayList<String>();
		if (paths != null && paths.size() > 0) {
			File folder;
			for (String path : paths) {
				folder = new File(path);
				if (folder.exists() && folder.isDirectory() && folder.canRead()) {
					result.add(path);
					LogUtils.logi("", "onSearched valide path: " + path);
				}
				
			}
		}
		return result;
	}

	private class ScanFileThread extends Thread {
		private String m_path;

		public ScanFileThread(String path) {
			m_path = path;
		}

		public void run() {
			if (m_MediaProvider != null) {
				m_MediaProvider.scanAllFiles(m_path);
			}
		}
	}
	
	private class SearchManager {
		private List<String> mPaths = new ArrayList<String>();
		private int mCurSearchIndex;
		
		public SearchManager(String path) {
			// TODO Auto-generated constructor stub
			if (!TextUtils.isEmpty(path)) {
				mPaths.add(path);
			}
		}
		
		public SearchManager(List<String> paths) {
			mPaths = paths;
		}
		
		void startScan() {
			if (mPaths != null && mPaths.size() > 0) {
				mCurSearchIndex = 0;
				scan(mPaths.get(mCurSearchIndex));
			} else {
				if (mListener != null) {
					mListener.onSearched(null, mMediaType);
				}
			}
		}
		
		boolean scanNext() {
			boolean hasNext = false;
			mCurSearchIndex++;
			LogUtils.logi("", "onSearched scanNext: " + mCurSearchIndex);
			if (mPaths != null && mCurSearchIndex < mPaths.size()) {
				hasNext = true;
				LogUtils.logi("", "onSearched scanNext: " + mPaths.get(mCurSearchIndex));
				scan(mPaths.get(mCurSearchIndex));
			}
			return hasNext;
		}
		
		private void scan(String path) {
			new ScanFileThread(path).start();
		}
	}

	public interface ScanLocalMediaListener {

		public void onSearched(String file, int mediaType);
	}
}