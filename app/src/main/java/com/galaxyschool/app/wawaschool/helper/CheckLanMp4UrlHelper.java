package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckInsideWiFiUtil;
import com.galaxyschool.app.wawaschool.common.NetworkHelper;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.fragment.ApplicationModelSettingFragment;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpConnectionParams;
import java.net.SocketTimeoutException;

/**
 * ======================================================
 * Describe: 校验局域网的mp4 url是否存在(辅助直播校本视频本地播放)
 * ======================================================
 */
public class CheckLanMp4UrlHelper {
    //http://118.190.103.67/video/cache/test.mp4
    private Context mContext;
    //校内网模式的IP地址
    private String headUrl = null;
    private String bodyUrl = "/video/cache/";
    private CallbackListener callBackListener;
    private String resTitle;
    private String mp4ResourceUrl;
    private String requestUrl = null;

    public CheckLanMp4UrlHelper(Context context) {
        this.mContext = context;
    }

    public CheckLanMp4UrlHelper setResTitle(String resTitle) {
        this.resTitle = resTitle;
        return this;
    }

    public CheckLanMp4UrlHelper setMp4ResourceUrl(String mp4ResourceUrl) {
        this.mp4ResourceUrl = mp4ResourceUrl;
        return this;
    }

    public CheckLanMp4UrlHelper setCallBackListener(CallbackListener callBackListener) {
        this.callBackListener = callBackListener;
        return this;
    }

    public void checkLanUrl(boolean isCheckTitle) {
        //判断当前WiFi是不是打开的状态
        boolean isWiFiOpen = NetworkHelper.isWifiConnected(mContext);
        if (isWiFiOpen) {
            if (judgeCurrentApplicationModel()) {
                //校内网模式
                checkTypeCondition(isCheckTitle);
            } else {
                //判断通用模式下当前的校内网有没有通的IP可以访问
                if (CheckInsideWiFiUtil.currentInsideWiFiConnect && !TextUtils.isEmpty
                        (CheckInsideWiFiUtil.insideWiFiHeadUrl)) {
                    this.headUrl = CheckInsideWiFiUtil.insideWiFiHeadUrl;
                    checkTypeCondition(isCheckTitle);
                } else {
                    callBackListener.onBack(null);
                }
            }
        } else {
            callBackListener.onBack(null);
        }
    }

    private void checkTypeCondition(boolean isCheckTitle) {
        if (isCheckTitle) {
            checkMp4FileExist(true);
        } else {
            checkMp4FileExist(false);
        }
    }

    /**
     * 校验MP4格式的文件本地是否存在
     */
    private void checkMp4FileExist(boolean checkResTitle) {
        if (checkResTitle) {
            //替换title
            requestUrl = "http://" + headUrl + bodyUrl + resTitle + ".mp4";
        } else {
            //替换mp4Url
            requestUrl = getChangeIPUrl(mp4ResourceUrl);
        }
        new Thread(() -> {
            try {
                HttpPost httpRequest = new HttpPost(requestUrl);
                httpRequest.setHeader("content-type", "application/*");
                AndroidHttpClient client = AndroidHttpClient.newInstance("lqwawa");
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
                HttpConnectionParams.setSoTimeout(client.getParams(), 10000);
                HttpResponse httpResponse = client.execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    backResultData(true, checkResTitle);
                } else {
                    backResultData(false, checkResTitle);
                }
                client.close();
            } catch (SocketTimeoutException e) {
                backResultData(false, checkResTitle);
                e.printStackTrace();
            } catch (Exception e) {
                backResultData(false, checkResTitle);
                e.printStackTrace();
            }
        }).start();
    }

    private void backResultData(boolean isUsable, boolean checkResTitle) {
        ((Activity) mContext).runOnUiThread(() -> {
            if (callBackListener != null) {
                if (checkResTitle && !TextUtils.isEmpty(mp4ResourceUrl)) {
                    checkTypeCondition(false);
                } else {
                    callBackListener.onBack(isUsable ? requestUrl : null);
                }
            }
        });
    }


    /**
     * 替换成内网的IP形式
     *
     * @param resUrl 源resUrl
     * @return 替换后的resUrl
     */
    private String getChangeIPUrl(String resUrl) {
        if (!TextUtils.isEmpty(resUrl) && (resUrl.contains("http:") || resUrl.contains("https:"))) {
            int startIndex = resUrl.indexOf("//");
            int endIndex = resUrl.indexOf("/",startIndex +2);
            String tempData = resUrl.substring(startIndex + 2,endIndex);
            if (!TextUtils.isEmpty(tempData)){
                resUrl = resUrl.replace(tempData, headUrl);
            }
        }
        return resUrl;
    }

    /**
     * 判断当前应用场景的模式
     *
     * @return model (通用模式、校内模式)
     */
    private boolean judgeCurrentApplicationModel() {
        PrefsManager prefsManager = DemoApplication.getInstance().getPrefsManager();
        String memberId = DemoApplication.getInstance().getMemberId();
        int currentModel = -1;
        if (TextUtils.isEmpty(memberId)) {
            return false;
        }
        if (prefsManager != null && !TextUtils.isEmpty(memberId)) {
            String tempData = prefsManager.getCurrentApplicationModel(mContext, memberId);
            if (!TextUtils.isEmpty(tempData)) {
                currentModel = Integer.valueOf(tempData);
            }
            if (currentModel == ApplicationModelSettingFragment.ApplicationModel.CAMPUS_MODEL) {
                //校内网模式的IP
                headUrl = prefsManager.getCampusModelIp(mContext, memberId);
                if (!TextUtils.isEmpty(headUrl)) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }
}