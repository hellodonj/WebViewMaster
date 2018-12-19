package com.galaxyschool.app.wawaschool.common;

import com.galaxyschool.app.wawaschool.R;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by shouyi.
 */
public class TipMsgHelper {
	private static TipMsgHelper mInstance;
	private Toast toast;

	public static TipMsgHelper getInstance() {
		if (mInstance == null) {
			mInstance = new TipMsgHelper();
		}
		return mInstance;
	}
	
    public static void ShowMsg(Context context, String msg) {
        if(context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    
    public static void ShowMsg(Context context, int msgId) {
        if(context == null) {
            return;
        }
        Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
    }

    public static void ShowLMsg(Context context, String msg) {
        if(context == null) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    
    public static void ShowLMsg(Context context, int msgId) {
        if(context == null) {
            return;
        }
        Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
    }
    
	public void showOneTips(Context context, String tips) {
		if (context != null) {
			if (toast != null) {
				toast.cancel();
			}
			toast = Toast.makeText(context, tips, Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private long mLastNeedShowTime = 0;
	public void showNetworkNotAvailable(Context context) {
		if (System.currentTimeMillis() - mLastNeedShowTime > 1000) {
			if (context != null) {
				if (toast != null) {
					toast.cancel();
				}
				toast = Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_SHORT);
				toast.show();
			}
			mLastNeedShowTime = System.currentTimeMillis();
		}
	}
}
