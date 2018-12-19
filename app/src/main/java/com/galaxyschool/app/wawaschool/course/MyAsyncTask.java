package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;
import android.os.AsyncTask;

import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.lqwawa.lqbaselib.views.ContactsLoadingDialog;

/**
 * @author: wangchao
 * @date: 2017/08/29 13:06
 */

public class MyAsyncTask<T> extends AsyncTask<Void, Void, T> {
    protected Activity activity;
    ContactsLoadingDialog loadingDialog;
    CallbackListener listener;

    public MyAsyncTask() {

    }

    public MyAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showLoadingDialog();
    }

    @Override
    protected T doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        dismissLoadingDialog();
        if (this.listener != null) {
            this.listener.onBack(t);
        }
    }

    public void setCallbackListener(CallbackListener listener) {
        this.listener = listener;
    }

    protected void showLoadingDialog() {
        if (this.activity == null) {
            return;
        }
        if (this.loadingDialog == null) {
            this.loadingDialog = new ContactsLoadingDialog(this.activity);
        }
        this.loadingDialog.setCancelable(false);
        this.loadingDialog.show();
    }

    protected void dismissLoadingDialog() {
        try {
            if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
                this.loadingDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (this.loadingDialog != null) {
                this.loadingDialog = null;
            }
        }
    }
}
