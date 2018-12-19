package com.galaxyschool.app.wawaschool.course;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.bitmapmanager.Md5FileNameGenerator;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.course.DownloadAttachTask.DownloadDtoBase;
import com.lqwawa.lqbaselib.net.FileApi;
import com.j256.ormlite.field.DatabaseField;
import com.oosic.apps.iemaker.base.BaseUtils;

/**
 * @author 作者 shouyi
 * @version 创建时间：Jan 26, 2016 10:04:25 AM 类说明
 */
public class DownloadAttachTask<T extends DownloadDtoBase> extends
		AsyncTask<Void, Void, String> {
	CallbackListener mListener;
	DATParam mParam;
	Class<T> mDotClass;

	public DownloadAttachTask(DATParam param, Class<T> dtoClass,
			CallbackListener listener) {
		mParam = param;
		mDotClass = dtoClass;
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Void... arg0) {
		if (checkParamOk()) {
			String path = getDownloadPath(mParam);
			path = FileApi.getFile(AppSettings.getFileUrl(mParam.mUrl), path,
					true);
			return path;
		} else {
			return null;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		DownloadChwCallbackParam<T> param = new DownloadChwCallbackParam<T>();
		boolean isOk = false;
		if (!TextUtils.isEmpty(result)) {
			File file = new File(result);
			if (file.exists()) {
				isOk = true;
				if (mListener != null) {
					try {
						param.mDownloadDto = mDotClass.newInstance();
						param.mDownloadDto.setPath(result);
						param.mDownloadDto.setFileSize(file.length());
						param.mResult = true;
					} catch (Exception e) {
						// TODO: handle exception
					}
					mListener.onBack(param);
				}
			}
		}
		if (!isOk && mListener != null) {
			param.mResult = false;
			mListener.onBack(param);
		}
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		File file = new File(getDownloadPath(mParam));
		if (file.exists()) {
			file.delete();
		}
		if (mListener != null) {
			DownloadChwCallbackParam<T> param = new DownloadChwCallbackParam<T>();
			mListener.onBack(param);
		}
	}

	private boolean checkParamOk() {
		if (mParam != null && !TextUtils.isEmpty(mParam.mUrl)
				&& !TextUtils.isEmpty(mParam.mDownloadFolder)
				&& !TextUtils.isEmpty(mParam.mFileSuffix)) {
			return true;
		}
		return false;
	}

	public static String getDownloadPath(DATParam param) {
		if (param == null || TextUtils.isEmpty(param.mDownloadFolder)
				|| TextUtils.isEmpty(param.mUrl)) {
			return null;
		}
		String fileName_md5 = Md5FileNameGenerator.generate(AppSettings
				.getFileUrl(param.mUrl + param.mUpdateTime));
		String path = param.mDownloadFolder + fileName_md5
				+ param.mFileSuffix;
		return path;
	}

	public static String getFolderName(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		return Md5FileNameGenerator.generate(AppSettings.getFileUrl(url));
	}
	
	public static final int WEIKE_CACHE_FILES_MAX_COUNT = 10;
	public static void checkWeikeFiles(String weikeFolder) {
		if (!TextUtils.isEmpty(weikeFolder)) {
			File folder = new File(weikeFolder);
			if (folder.exists()) {
				File[] files = folder.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						BaseUtils.safeDeleteDirectory(files[i].getPath());
					}
				}
				File[] files2 = folder.listFiles();
				if (files2.length > WEIKE_CACHE_FILES_MAX_COUNT) {
					Collections.sort(Arrays.asList(files2), new Comparator<File>() {
						@Override
						public int compare(File lhs, File rhs) {
							// TODO Auto-generated method stub
							if (lhs.lastModified() > rhs.lastModified()) {
								return 1;
							} else {
								return -1;
							}
						}
					});
					for (int i = 0; i < files2.length - WEIKE_CACHE_FILES_MAX_COUNT; i++) {
						files2[i].delete();
					}
				}
			}
		}
	}
	
	public static class DownloadDtoBase {
		@DatabaseField(id = true)
		private String mPath;
		@DatabaseField()
		private long mFileSize;
		@DatabaseField(width = 30)
		private String mOpenTime;

		public String getPath() {
			return mPath;
		}

		public void setPath(String path) {
			this.mPath = path;
		}

		public long getFileSize() {
			return mFileSize;
		}

		public void setFileSize(long fileSize) {
			this.mFileSize = fileSize;
		}

		public String getOpenTime() {
			return mOpenTime;
		}

		public void setOpenTime(String openTime) {
			this.mOpenTime = openTime;
		}
	}

	public static class DownloadChwCallbackParam<T extends DownloadDtoBase> {
		boolean mResult;
		T mDownloadDto;
	}

	public static class DATParam {
		String mUrl;
		String mDownloadFolder;
		String mFileSuffix;
		String mUpdateTime;
	}
}
