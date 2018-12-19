package com.lqwawa.lqbaselib.net.library;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkError;
import com.duowan.mobile.netroid.request.StringRequest;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqresviewlib.R;

public class MyStringRequest extends StringRequest {

	private Context context;
	private Handler handler;
	private View hostView;
	private Listener listener;
	private boolean alwaysNotifyError;

	public MyStringRequest(int mothed, String url, Listener listener) {
		super(mothed, url, listener);
		this.handler = new Handler(Looper.getMainLooper());
		this.listener = listener;
		this.alwaysNotifyError = true;
	}

	public Context getContext() {
		return this.context;
	}

	public View getHostView() {
		return hostView;
	}

	public void setHostView(View hostView) {
		this.hostView = hostView;
	}

	public boolean isAlwaysNotifyError() {
		return alwaysNotifyError;
	}

	public void setAlwaysNotifyError(boolean alwaysNotifyError) {
		this.alwaysNotifyError = alwaysNotifyError;
	}

	public Listener getListener() {
		return listener;
	}

	public void run(Context context) {
		this.context = context;
		if (context == null) {
			return;
		}
		if (!isNetworkConnected(context)) {
			if (this.listener != null) {
				this.listener.onFinish();
				if (this.alwaysNotifyError) {
					this.listener.onError(new NetworkError(
							new Exception("Network not available")));
				}
			}
			Toast.makeText(context, R.string.lqbase_network_unavailable, Toast.LENGTH_SHORT).show();
			return;
		}

		// Disabled host view to avoid repeated clicking, re-enabled it after request finished
		if (this.hostView != null) {
			this.hostView.setEnabled(false);
		}

		Netroid.newRequestQueue(context).add(this);
	}

	public void start(Context context) {
		run(context);
	}

	@Override
	public void finish(String tag) {
		super.finish(tag);

		// Re-enabled host view to allow user clicking again
        if (this.hostView != null) {
            this.handler.post(new Runnable() {
                @Override
                public void run() {
					if (getContext() == null) {
						return;
					}
                    getHostView().setEnabled(true);
                }
            });
        }
	}

    /**
     * 网络是否已经连接，可能是手机网络也可能是WIFI
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
        }
        return false;
    }

}
