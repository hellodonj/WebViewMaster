package com.osastudio.common.library;

import android.content.Context;
import android.os.Handler;

public abstract class AsyncTask<Params, Progress, Result> extends Thread {

   private Context context;
   private Handler handler;

   public AsyncTask() {

   }

   public AsyncTask(Context context) {
      this.context = context;
   }

   public AsyncTask(Context context, Handler handler) {
      this(context);
      this.handler = handler;
   }

   public Context getContext() {
      return this.context;
   }

   @Override
   public void run() {
      final Result result = doInBackground();

      if (this.handler != null) {
         this.handler.post(new Runnable() {
            public void run() {
               onPostExecute(result);
            }
         });
      }
   }

   protected void onPreExecute() {

   }

   protected abstract Result doInBackground(Params... params);

   protected final void publishProgress(final Progress... values) {
      if (this.handler != null) {
         this.handler.post(new Runnable() {
            public void run() {
               onProgressUpdate(values);
            }
         });
      }
   }

   protected void onProgressUpdate(Progress... values) {

   }

   public void onPostExecute(Result result) {

   }

}
