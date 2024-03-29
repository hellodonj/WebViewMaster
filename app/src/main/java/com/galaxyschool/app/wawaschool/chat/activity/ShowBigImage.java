/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.galaxyschool.app.wawaschool.chat.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.task.LoadLocalBigImgTask;
import com.galaxyschool.app.wawaschool.chat.utils.ImageCache;
import com.galaxyschool.app.wawaschool.chat.widget.photoview.PhotoView;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.util.PathUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends BaseActivity {
	private static final String TAG = "ShowBigImage";
	private ProgressDialog pd;
	private PhotoView image;
	private int default_res = R.drawable.default_image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);

		image = (PhotoView) findViewById(R.id.image);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image", R.drawable.default_avatar);
		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
		EMLog.d(TAG, "show big image uri:" + uri + " remotepath:" + remotepath);

		//本地存在，直接显示本地的图片
		if (uri != null && new File(uri.getPath()).exists()) {
			isDownloaded = true;
			localFilePath = uri.getPath();
			EMLog.d(TAG, "showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = ImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
						ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) { //去服务器下载图片
			EMLog.d(TAG, "download remote image");
			Map<String, String> maps = new HashMap<String, String>();
			if (!TextUtils.isEmpty(secret)) {
				maps.put("share-secret", secret);
			}
			downloadImage(remotepath, maps);
		} else {
			image.setImageResource(default_res);
		}

//		image.setOnLongClickListener(new View.OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				if (!isDownloaded) {
//					return true;
//				}
//				startActivityForResult(new Intent(ShowBigImage.this, ContextMenu.class)
//                        .putExtra("type", EMMessage.Type.IMAGE.ordinal())
//                        .putExtra("fullscreen", true),
//						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
//				return true;
//			}
//		});
	}

	/**
	 * 通过远程URL，确定下本地下载后的localurl
	 * @param remoteUrl
	 * @return
	 */
	public String getLocalFilePath(String remoteUrl){
		String localPath;
		if (remoteUrl.contains("/")){
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
					+ remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
		}else{
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
		}
		return localPath;
	}

	public String getSaveFilePath(String filename) {
		return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/Pictures", filename).getAbsolutePath();
	}

	public boolean saveFile(File srcFile, File dstFile) {
		boolean result = false;
		if (dstFile.exists()) {
			dstFile.delete();
		} else {
			dstFile.getParentFile().mkdirs();
			try {
				dstFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(dstFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
			}
		}
		if (result) {
			Uri uri = Uri.fromFile(new File(dstFile.getAbsolutePath()));
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
		}
		return result;
	}
	
	/**
	 * 下载图片
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		String str1 = getResources().getString(R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		localFilePath = getLocalFilePath(remoteFilePath);
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(int error, String msg) {
				EMLog.e(TAG, "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}

			public void onProgress(final int progress, String status) {
				EMLog.d(TAG, "Progress: " + progress);
				final String str2 = getResources().getString(R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};

		EMClient.getInstance().chatManager().downloadFile(remoteFilePath, localFilePath, headers, callback);

	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChatActivity.REQUEST_CODE_CONTEXT_MENU) {
			if (resultCode == ChatActivity.RESULT_CODE_SAVE) {
				File srcFile = new File(localFilePath);
				String filename = getIntent().getStringExtra("filename");
				File dstFile = new File(getSaveFilePath(filename));
				boolean result = saveFile(srcFile, dstFile);
				if (result) {
					TipMsgHelper.ShowMsg(this, getString(R.string.image_saved_to,
							dstFile.getAbsolutePath()));
				} else {
					TipMsgHelper.ShowMsg(this, getString(R.string.save_failed));
				}
            }
        }
	}

}
