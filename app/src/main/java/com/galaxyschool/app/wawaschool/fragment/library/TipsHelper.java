package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
import com.galaxyschool.app.wawaschool.R;

public class TipsHelper {

    public static void showToast(Context context, String msg) {
        if(context == null) {
            return;
        }
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    
    public static void showToast(Context context, int msgId) {
        if(context == null) {
            return;
        }
        Toast toast = Toast.makeText(context, msgId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

	public static void showNetworkNotAvailable(Context context) {
        showToast(context, R.string.network_unavailable);
	}

}
