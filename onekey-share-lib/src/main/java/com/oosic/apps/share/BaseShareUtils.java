package com.oosic.apps.share;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.lqwawa.tools.BitmapUtils;
import com.lqwawa.tools.DialogHelper;
import com.osastudio.common.utils.TipMsgHelper;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * ======================================================
 * Created by : Brave_Qu on 2018/6/7 15:14
 * E-Mail Address:863378689@qq.com
 * Describe:
 * ======================================================
 */
public class BaseShareUtils {
    protected Activity mContext;
    private ShareHelper shareHelper;
    private SharePopupView sharePopupView;
    private ShareInfo shareInfo;
    protected List<ShareItem> shareItems;
    private Map<String, Object> contactsPickerParams;
    private boolean isFinish = false;

    public BaseShareUtils(Activity context) {
        mContext = context;
        shareHelper = new ShareHelper(context);
    }

    public void setShareItems(List<ShareItem> shareItems) {
        this.shareItems = shareItems;
    }

    protected void initShareItems() {
        shareItems = new ArrayList<ShareItem>();
        shareItems.add(new ShareItem(R.string.wechat_friends, R.drawable.umeng_share_wechat_btn, ShareType.SHARE_TYPE_WECHAT));
        shareItems.add(new ShareItem(R.string.wxcircle, R.drawable.umeng_share_wxcircle_btn, ShareType
                .SHARE_TYPE_WECHATMOMENTS));
        shareItems.add(new ShareItem(R.string.qq_friends, R.drawable.umeng_share_qq_btn, ShareType.SHARE_TYPE_QQ));
        shareItems.add(new ShareItem(R.string.qzone, R.drawable.umeng_share_qzone_btn, ShareType.SHARE_TYPE_QZONE));
    }

    private AdapterView.OnItemClickListener shareItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(
                AdapterView<?> parent, View view, int position,
                long id) {
            if (sharePopupView != null) {
                sharePopupView.dismiss();
            }
            shareTo(position, shareInfo);
        }
    };

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public void share(View parent, ShareInfo shareInfo) {
        share(parent, shareInfo, null);
    }

    public void share(View parent, ShareInfo shareInfo, Map<String, Object> contactsPickerParams) {
        if (parent == null || shareInfo == null) {
            return;
        }
        this.contactsPickerParams = contactsPickerParams;
        this.shareInfo = shareInfo;
        sharePopupView = new SharePopupView(mContext);
        sharePopupView.setIsNew(true);
        if(shareItems == null) {
            initShareItems();
        }
        sharePopupView.setShareItems(shareItems);
        sharePopupView.setOnItemClickListener(shareItemClickListener);
        sharePopupView.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    UMImage thumb ;
    public void shareTo(int position, ShareInfo shareInfo) {
        if (shareItems == null || shareItems.size() == 0) {
            return;
        }
        if (position < shareItems.size()) {
            ShareItem shareItem = shareItems.get(position);
            if (shareItem != null) {
                switch (shareItem.getShareType()) {
                    case ShareType.SHARE_TYPE_WECHAT:
                    case ShareType.SHARE_TYPE_WECHATMOMENTS:
                    case ShareType.SHARE_TYPE_QQ:
                    case ShareType.SHARE_TYPE_QZONE:
                        showLoadingDialog(mContext.getString(R.string.str_loadding), false);
                        if(shareInfo != null) {
                            UMImage umImage = (UMImage) shareInfo.getuMediaObject();
                            final String imgUrl = umImage.asUrlImage();

                            if (!TextUtils.isEmpty(imgUrl)) {
                                ThumbTask task = new ThumbTask(shareInfo, shareItem.getShareType());
                                task.execute(imgUrl);
                            } else {
                                doShare(shareInfo, shareItem.getShareType());
                            }
                        }
                        break;
                    case ShareType.SHARE_TYPE_CONTACTS:
                        //分享到好友

                        shareToContacts(shareInfo);
                        break;
                    default:
                }
            }
        }
    }

    private void doShare(ShareInfo shareInfo, int shareType) {
        shareHelper.shareTo(shareType, shareInfo, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                TipMsgHelper.ShowLMsg(mContext,mContext.getResources().getString(R.string.share_success));
                if (mContext != null && isFinish) {
                    mContext.finish();
                }
            }
            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
            }
        });
        dismissLoadingDialog();
    }

    protected void shareToContacts(ShareInfo shareInfo){

    }

    private class ThumbTask extends AsyncTask<String,Void,Bitmap> {
        ShareInfo shareInfo;
        int shareType;
        ThumbTask(ShareInfo shareInfo, int shareType) {
            this.shareInfo = shareInfo;
            this.shareType = shareType;
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Bitmap doInBackground(String... params) {

            return getImageFromNet(params[0]);
        }
        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Bitmap b) {
            if (b == null) {
                thumb = new UMImage(mContext, R.drawable.ic_launcher);
            } else {
                thumb = new UMImage(mContext, b);
            }
            shareInfo.setuMediaObject(thumb);
            doShare(shareInfo, shareType);
        }
    }

    private Bitmap getImageFromNet(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //设置请求方法
            conn.setConnectTimeout(10000); //设置连接服务器超时时间
            conn.setReadTimeout(5000);  //设置读取数据超时时间
            conn.connect(); //开始连接
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                mURL = new URL(conn.getHeaderField("Location"));
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestProperty("Connection", "close");
                conn.setConnectTimeout(30 * 1000);
                conn.setReadTimeout(30 * 1000);
                conn.connect();
            }
            int responseCode = conn.getResponseCode(); //得到服务器的响应码
            if (responseCode == 200) {
                return BitmapUtils.getBitmap(conn.getInputStream(),40,40);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //断开连接
            }
        }
        return null;
    }

    private DialogHelper.LoadingDialog loadingDialog;
    public Dialog showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return loadingDialog;
        }
        loadingDialog = DialogHelper.getIt(mContext).GetLoadingDialog(0);
        return loadingDialog;
    }
    public Dialog showLoadingDialog(String content, boolean cancelable) {
        Dialog dialog = showLoadingDialog();
        ((DialogHelper.LoadingDialog) dialog).setContent(content);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
