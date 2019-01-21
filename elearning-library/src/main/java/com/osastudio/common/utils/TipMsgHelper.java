package com.osastudio.common.utils;

import android.content.Context;
import android.view.Gravity;
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
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    
    public static void ShowMsg(Context context, int msgId) {
        if(context == null) {
            return;
        }
        Toast toast = Toast.makeText(context, msgId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static void ShowLMsg(Context context, String msg) {
        if(context == null) {
            return;
        }
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    
    public static void ShowLMsg(Context context, int msgId) {
        if(context == null) {
            return;
        }
        Toast toast = Toast.makeText(context, msgId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
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
	
	
}
