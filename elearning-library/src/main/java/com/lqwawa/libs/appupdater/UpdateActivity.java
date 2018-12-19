package com.lqwawa.libs.appupdater;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lqwawa.apps.R;
import com.lqwawa.libs.appupdater.instance.DefaultUpdateService;
import com.osastudio.common.utils.Utils;

public class UpdateActivity extends com.osastudio.apps.BaseActivity {

	public static final String EXTRA_APP_INFO = "appInfo";
	public static final String EXTRA_FORCE_UPDATE = "forceUpdate";

	private AppInfo appInfo;
	private boolean forceUpdate;

	private UpdateDialog updateDialog;

	private TextView tipsView;
	private ProgressBar progressView;
	private Button retryView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.au_activity_update);

		bindUpdateService(this, updateServiceConn);

		init();
	}

	private void init() {
		Bundle args = getIntent().getExtras();
		this.appInfo = args.getParcelable(EXTRA_APP_INFO);
		this.forceUpdate = args.getBoolean(EXTRA_FORCE_UPDATE);

		initViews();
	}

	private void initViews() {
		tipsView = (TextView) findViewById(R.id.au_update_tips);
		retryView = (Button) findViewById(R.id.au_update_retry);
		progressView = (ProgressBar) findViewById(R.id.au_update_progress);

		retryView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				retryView.setVisibility(View.GONE);
				tipsView.setText(R.string.au_downloading);
				updateService.download(appInfo);
			}
		});
	}

	private void showUpdateDialog() {
		if (updateDialog == null) {
			updateDialog = new UpdateDialog(this, appInfo)
					.setOnIgnoreClickListener(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UpdateService.ignoreVersion(UpdateActivity.this, appInfo, true);
							finish();
						}
					})
					.setOnConfirmClickListener(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							updateService.download(appInfo);
							if (appInfo.isForcedUpdate()) {
								showUpdateProgress();
							} else {
								finish();
							}
						}
					})
                    .setOnCancelClickListener(new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
            updateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
			updateDialog.setCancelable(!appInfo.isForcedUpdate());
		}
		if (forceUpdate) {
			// 不显示忽略按钮
			updateDialog.getIgnoreButton().setVisibility(View.GONE);
			if (appInfo.isForcedUpdate()) {
				updateDialog.getCancelButton().setVisibility(View.GONE);
			}
		} else {
			if (appInfo.isForcedUpdate()) {
				// 仅显示立即更新按钮
				updateDialog.getIgnoreButton().setVisibility(View.GONE);
				updateDialog.getCancelButton().setVisibility(View.GONE);
			} else {
				// 显示忽略、更新、取消按钮
			}
		}
		updateDialog.show();
	}

	private void showUpdateProgress() {
		addUpdateListener();
		findViewById(R.id.au_update_progress_layout).setVisibility(View.VISIBLE);
	}

	private void addUpdateListener() {
		if (updateService != null) {
			updateService.addUpdateListener(UpdateActivity.class, updateListener);
		}
	}

	private void removeUpdateListener() {
		if (updateService != null) {
			updateService.removeUpdateListener(UpdateActivity.class);
		}
	}

	private void updateViews(AppInfo appInfo) {
		if (appInfo.isDownloadFailed()) {
			tipsView.setText(R.string.au_download_error);
			retryView.setVisibility(View.VISIBLE);
		} else {
			tipsView.setText(R.string.au_downloading);
		}
        progressView.setProgress((int) ((float) appInfo.getDownloadedSize()
                / (float) appInfo.getFileSize() * 100f));
        progressView.setMax(100);
	}

	@Override
	public void onBackPressed() {
		if (appInfo.isForcedUpdate()) {
			return;
		}

		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		removeUpdateListener();
		unbindUpdateService(updateServiceConn);
	}

	private UpdateListener updateListener = new UpdateListener() {
		@Override
		public void onPrepare(AppInfo appInfo) {
			appInfo.setDownloadedSize(0);
			updateViews(appInfo);
		}

		@Override
		public void onStart(AppInfo appInfo) {
			updateViews(appInfo);
		}

		@Override
		public void onProgress(AppInfo appInfo) {
			updateViews(appInfo);
		}

		@Override
		public void onFinish(AppInfo appInfo) {
			updateViews(appInfo);

			Utils.installApp(getApplicationContext(), appInfo.getFilePath());

			getBaseApplication().getActivityStack().finishAll();
		}

		@Override
		public void onError(AppInfo appInfo) {
			updateViews(appInfo);
		}
	};

	private UpdateService updateService;
	private ServiceConnection updateServiceConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			updateService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			updateService = ((UpdateService.UpdateBinder) service).getService();
			showUpdateDialog();
		}
	};

	private boolean bindUpdateService(Context context, ServiceConnection conn) {
		if (conn != null) {
			return bindService(new Intent(context, DefaultUpdateService.class), conn,
					Context.BIND_AUTO_CREATE);
		}
		return false;
	}

	private void unbindUpdateService(ServiceConnection conn) {
		if (conn != null) {
			unbindService(conn);
		}
	}

}
