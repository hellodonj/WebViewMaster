package com.galaxyschool.app.wawaschool.chat.task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.chat.activity.ShowVideoActivity;
import com.galaxyschool.app.wawaschool.chat.utils.CommonUtils;
import com.galaxyschool.app.wawaschool.chat.utils.ImageCache;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.util.ImageUtils;

import java.io.File;

public class LoadVideoImageTask extends AsyncTask<Object, Void, Bitmap> {

	private ImageView iv = null;
	String thumbnailPath = null;
	String thumbnailUrl = null;
	Activity activity;
	EMMessage message;
	BaseAdapter adapter;

	@Override
	protected Bitmap doInBackground(Object... params) {
		thumbnailPath = (String) params[0];
		thumbnailUrl = (String) params[1];
		iv = (ImageView) params[2];
		activity = (Activity) params[3];
		message = (EMMessage) params[4];
		adapter = (BaseAdapter) params[5];
		if (new File(thumbnailPath).exists()) {
			return ImageUtils.decodeScaleImage(thumbnailPath, 120, 120);
		} else {
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (result != null) {
			iv.setImageBitmap(result);
			ImageCache.getInstance().put(thumbnailPath, result);
			iv.setClickable(true);
			iv.setTag(thumbnailPath);
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (thumbnailPath != null) {
						EMVideoMessageBody videoBody = (EMVideoMessageBody) message
								.getBody();
						Intent intent = new Intent(activity,
								ShowVideoActivity.class);
						intent.putExtra("localpath", videoBody.getLocalUrl());
						intent.putExtra("secret", videoBody.getSecret());
						intent.putExtra("remotepath", videoBody.getRemoteUrl());
						if (message != null
								&& message.direct() == EMMessage.Direct.RECEIVE
								&& !message.isAcked()) {
							message.setAcked(true);
							try {
								EMClient.getInstance().chatManager().ackMessageRead(
										message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						activity.startActivity(intent);

					}
				}
			});

		} else {
			if (message.status ()== EMMessage.Status.FAIL
					|| message.direct() == EMMessage.Direct.RECEIVE) {
				if (CommonUtils.isNetWorkConnected(activity)) {
					new AsyncTask<Void, Void, Void>() {
						 
						
						@Override
						protected Void doInBackground(Void... params) {
							EMClient.getInstance().chatManager().downloadAttachment(message);
							EMClient.getInstance().chatManager().downloadThumbnail(message);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							adapter.notifyDataSetChanged();
						};
					}.execute();
				}
			}

		}
	}

}
