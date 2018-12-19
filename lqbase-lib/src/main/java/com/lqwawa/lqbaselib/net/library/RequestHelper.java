package com.lqwawa.lqbaselib.net.library;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.lqwawa.lqbaselib.views.ContactsLoadingDialog;
import com.lqwawa.lqresviewlib.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Map;

public class RequestHelper {

    public static MapParamsStringRequest sendGetRequest(Context context,
                                                        String url,
                                                        Map<String, Object> params,
                                                        Listener listener) {
        return sendRequest(context, Request.Method.GET, url, params, listener);
    }

    public static MapParamsStringRequest sendPostRequest(Context context,
                                                         String url,
                                                         Map<String, Object> params,
                                                         Listener listener) {
        return sendRequest(context, Request.Method.POST, url, params, listener);
    }

    public static MapParamsStringRequest sendRequest(Context context,
                                                     int method,
                                                     String url,
                                                     Map<String, Object> params,
                                                     Listener listener) {
        MapParamsStringRequest request = null;
        if (listener != null) {
            if (listener instanceof RequestModelResultListener) {
                request = new ModelResultStringRequest(method, url, params, listener);
                request.setHostView(((RequestModelResultListener) listener).getHostView());
            } else if (listener instanceof RequestDataResultListener) {
                request = new DataResultStringRequest(method, url, params, listener);
                request.setHostView(((RequestDataResultListener) listener).getHostView());
            } else if (listener instanceof RequestResourceResultListener) {
                request = new ResourceResultStringRequest(method, url, params, listener);
                request.setHostView(((RequestResourceResultListener) listener).getHostView());
            }
        }
        if (request == null) {
            request = new MapParamsStringRequest(method, url, params, listener);
        }
        request.addHeader("Accept-Encoding", "*");
        if (listener != null) {
            if (listener instanceof RequestListener) {
                int timeOutMs = ((RequestListener) listener).getTimeOutMs();
                if (timeOutMs > 0) {
                    request.setRetryPolicy(new DefaultRetryPolicy(timeOutMs, 0, DefaultRetryPolicy
                            .DEFAULT_BACKOFF_MULT));
                } else {
                    request.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy
                            .DEFAULT_BACKOFF_MULT));
                }
            }
        }
        request.run(context);
        return request;
    }

    public static void postRequest(final Activity activity, final String url, final String
            jsonString, final Listener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpPost httpRequest = new HttpPost(url);
                    httpRequest.setHeader("content-type", "application/*");
                    StringEntity stringEntity = new StringEntity(jsonString, HTTP.UTF_8);
                    stringEntity.setContentType("application/json");
                    stringEntity.setContentEncoding("utf-8");
                    httpRequest.setEntity(stringEntity);
                    AndroidHttpClient client = AndroidHttpClient.newInstance("lqwawa");
                    HttpConnectionParams.setConnectionTimeout(client.getParams(), 60000);
                    HttpConnectionParams.setSoTimeout(client.getParams(), 60000);
                    HttpResponse httpResponse = client.execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String result = null;
                        if (entity != null) {
//                            result = EntityUtils.toString(entity, "utf-8");
//                            if (TextUtils.isEmpty(result)) {
                            InputStream inputStream = entity.getContent();
                            if (inputStream != null) {
                                result = inputStream2String(inputStream);
                            }
//                            }
                        }
                        final String finalResult = result;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(finalResult);
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new NetroidError());
                            }
                        });
                    }
                    client.close();
                } catch (SocketTimeoutException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFinish();
                        }
                    });
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getRequest(final Activity activity, final String url, final Listener
            listener, final Map<String,String> params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpGet httpRequest = new HttpGet(url);
                    httpRequest.setHeader("content-type", "application/*");
                    if (params != null){
                        for (Map.Entry<String,String> entry : params.entrySet()){
                            httpRequest.setHeader(entry.getKey(),entry.getValue());
                        }
                    }
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse httpResponse = httpClient.execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String result = null;
                        if (entity != null) {
                            InputStream inputStream = entity.getContent();
                            if (inputStream != null) {
                                result = inputStream2String(inputStream);
                            }
                        }
                        final String finalResult = result;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(finalResult);
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new NetroidError());
                            }
                        });
                    }
                } catch (SocketTimeoutException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFinish();
                        }
                    });
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static String inputStream2String(InputStream inputStream) {
        try {
            BufferedReader mReader = new BufferedReader(new InputStreamReader(inputStream, HTTP.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = mReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static class RequestListener<T> extends Listener<String> {
        private Context context;
        private View hostView;
        private boolean showLoading = false;
        private ContactsLoadingDialog loadingDialog;
        private boolean showErrorTips = true;
        private Object target;
        private Class resultClass;
        private T result;
        private int timeOutMs;

        public RequestListener(Context context, Class resultClass) {
            this.context = context;
            this.resultClass = resultClass;
        }

        public Context getContext() {
            return context;
        }

        public View getHostView() {
            return hostView;
        }

        public void setHostView(View hostView) {
            this.hostView = hostView;
        }

        public Class getResultClass() {
            return resultClass;
        }

        public boolean isShowLoading() {
            return showLoading;
        }

        public void setShowLoading(boolean showLoading) {
            this.showLoading = showLoading;
        }

        public boolean isShowErrorTips() {
            return showErrorTips;
        }

        public void setShowErrorTips(boolean showErrorTips) {
            this.showErrorTips = showErrorTips;
        }

        public void setTarget(Object object) {
            this.target = object;
        }

        public Object getTarget() {
            return target;
        }

        public T getResult() {
            return result;
        }

        public int getTimeOutMs() {
            return timeOutMs;
        }

        public void setTimeOutMs(int timeOutMs) {
            this.timeOutMs = timeOutMs;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public void showLoadingTips() {
            if (this.context instanceof Activity) {
                if (this.loadingDialog == null) {
                    this.loadingDialog = new ContactsLoadingDialog(this.context);
                }
                this.loadingDialog.setCancelable(false);
                this.loadingDialog.show();
            }
        }

        public void hideLoadingTips() {
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

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (getContext() == null) {
                return;
            }
            if (isShowLoading()) {
                showLoadingTips();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (getContext() == null) {
                return;
            }
            if (isShowLoading()) {
                hideLoadingTips();
            }
        }

        @Override
        public void onSuccess(String jsonString) {
            if (getContext() == null) {
                return;
            }
            if (TextUtils.isEmpty(jsonString)) {
                return;
            }
            setResult((T) JSONObject.parseObject(jsonString, getResultClass()));
        }

        @Override
        public void onError(NetroidError error) {
            if (getContext() == null) {
                return;
            }
            if (isShowErrorTips()) {
                Toast.makeText(getContext(), R.string.lqbase_network_error, Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    public static class RequestModelResultListener<T extends ModelResult>
            extends RequestListener<T> {
        public RequestModelResultListener(Context context, Class resultClass) {
            super(context, resultClass);
        }

        @Override
        public void onSuccess(String jsonString) {
            if (getContext() == null) {
                return;
            }
            if (TextUtils.isEmpty(jsonString)) {
                return;
            }
            setResult((T) JSONObject.parseObject(jsonString, getResultClass()));
            if (getResult() == null || !getResult().isSuccess()) {
                if (isShowErrorTips()) {
                    if (!TextUtils.isEmpty(getResult().getErrorMessage())) {
                        Toast.makeText(getContext(), getResult().getErrorMessage(), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public static class RequestDataResultListener<T extends DataModelResult>
            extends RequestListener<T> {
        public RequestDataResultListener(Context context, Class resultClass) {
            super(context, resultClass);
        }

        @Override
        public void onSuccess(String jsonString) {
            if (getContext() == null) {
                return;
            }
            if (TextUtils.isEmpty(jsonString)) {
                return;
            }
            setResult((T) JSONObject.parseObject(jsonString, getResultClass()));
            if (getResult() != null && getResult().isSuccess()) {
                setResult((T) getResult().parse(jsonString));
            } else {
                if (isShowErrorTips()) {
                    if (!TextUtils.isEmpty(getResult().getErrorMessage())) {
                        Toast.makeText(getContext(), getResult().getErrorMessage(), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public static class RequestResourceResultListener<T extends ResourceResult>
            extends RequestListener<T> {
        public RequestResourceResultListener(Context context, Class resultClass) {
            super(context, resultClass);
        }

        @Override
        public void onSuccess(String jsonString) {
            super.onSuccess(jsonString);
            if (getContext() == null) {
                return;
            }
            if (TextUtils.isEmpty(jsonString)) {
                return;
            }
            setResult((T) JSONObject.parseObject(jsonString, getResultClass()));
            if (getResult() == null || !getResult().isSuccess()) {
                if (isShowErrorTips()) {
                    if (!TextUtils.isEmpty(getResult().getMsg())) {
                        Toast.makeText(getContext(), getResult().getMsg(), Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
