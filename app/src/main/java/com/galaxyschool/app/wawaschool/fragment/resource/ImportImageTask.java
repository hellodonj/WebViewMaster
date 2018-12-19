package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.course.library.ImportImage;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2017/08/28 14:49
 */

public class ImportImageTask extends AsyncTask<Void, Void, LocalCourseInfo> {

    Activity activity;
    String memberId;
    List<String> paths;
    String savePath;
    String title;
    Handler handler;
    ProgressDialog progressDialog;

    public ImportImageTask(Activity activity, String memberId, List<String> paths, String
            savePath, String title, Handler handler) {
        this.activity = activity;
        this.memberId = memberId;
        this.paths = paths;
        this.savePath = savePath;
        this.title = title;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProcessDialog(activity.getString(R.string.importing, title));
    }

    @Override
    protected LocalCourseInfo doInBackground(Void... params) {
        ImportImage.ImportImageRun(
                activity, memberId, paths, progressDialog, handler, savePath,
                title);
        return null;
    }

    @Override
    protected void onPostExecute(LocalCourseInfo info) {
        super.onPostExecute(info);
        dismissProcessDialog();
    }

    public void showProcessDialog(String title) {
        if (progressDialog != null)
            progressDialog.dismiss();

        if (progressDialog == null)
            progressDialog = new ProgressDialog(
                    activity, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(0);
        progressDialog.setMessage(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProcessDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (progressDialog != null) {
                progressDialog = null;
            }
        }
    }
}
