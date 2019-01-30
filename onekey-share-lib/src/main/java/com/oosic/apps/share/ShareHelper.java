package com.oosic.apps.share;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.osastudio.common.library.LqBase64Helper;
import com.osastudio.common.utils.TipMsgHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMediaObject;

import java.util.ArrayList;
import java.util.List;

public class ShareHelper {

    public interface OnShareListener {
        void onShare(int position);
    }

    private SharePopupView sharePopupView;
    private Activity mContext;
    private OnShareListener mListener;
    private List<ShareItem> shareItems;

    public ShareHelper(Activity context) {
        mContext = context;
    }

    public ShareHelper(Activity context, OnShareListener listener) {
        this(context);
        mListener = listener;
    }

    public void setListener(OnShareListener listener) {
        mListener = listener;
    }

    private void initShareItems() {
        shareItems = new ArrayList<ShareItem>();
        shareItems.add(new ShareItem(R.string.wechat_friends, R.drawable.umeng_share_wechat_btn, ShareType.SHARE_TYPE_WECHAT));
        shareItems.add(new ShareItem(R.string.wxcircle, R.drawable.umeng_share_wxcircle_btn, ShareType
                .SHARE_TYPE_WECHATMOMENTS));
        shareItems.add(new ShareItem(R.string.qq_friends, R.drawable.umeng_share_qq_btn, ShareType.SHARE_TYPE_QQ));
        shareItems.add(new ShareItem(R.string.qzone, R.drawable.umeng_share_qzone_btn, ShareType.SHARE_TYPE_QZONE));
//        shareItems.add(new ShareItem(R.string.wawachat, R.drawable.umeng_share_wawachat_btn, ShareType
//                .SHARE_TYPE_CONTACTS));
    }

    public void showSharePopWindows(View rootView) {
        if (rootView == null) {
            return;
        }
        sharePopupView = new SharePopupView(mContext);
        sharePopupView.setIsNew(true);
        if(shareItems == null) {
            initShareItems();
        }
        sharePopupView.setShareItems(shareItems);
        sharePopupView.setOnItemClickListener(shareItemClickListener);
        sharePopupView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private AdapterView.OnItemClickListener shareItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (sharePopupView != null) {
                sharePopupView.dismiss();
            }
            if (position < shareItems.size()) {
                ShareItem shareItem = shareItems.get(position);
                if (shareItem != null) {
                    if (mListener != null) {
                        mListener.onShare(shareItem.getShareType());
                    }
                }
            }
        }
    };

    public void shareTo(int shareType, ShareInfo shareInfo) {
        if (shareType < ShareType.SHARE_TYPE_WECHAT) {
            return;
        }
        if (shareInfo == null) {
            return;
        }
        if(TextUtils.isEmpty(shareInfo.getContent())) {
            shareInfo.setContent(" ");
        }
        checkPackageName(shareInfo);
        //判断是否需要进行加密
        checkSharePermission(shareInfo);
        switch (shareType) {
            case ShareType.SHARE_TYPE_WECHAT:
                shareToWeChat(shareInfo);
                break;
            case ShareType.SHARE_TYPE_WECHATMOMENTS:
                shareToWeChatMoments(shareInfo);
                break;
            case ShareType.SHARE_TYPE_QQ:
                shareToQQ(shareInfo);
                break;
            case ShareType.SHARE_TYPE_QZONE:
                shareToQzone(shareInfo);
                break;
        }
    }

    public void shareTo(int shareType, ShareInfo shareInfo, UMShareListener listener) {
        if (shareType < ShareType.SHARE_TYPE_WECHAT) {
            return;
        }
        if (shareInfo == null) {
            return;
        }
        if(TextUtils.isEmpty(shareInfo.getContent())) {
            shareInfo.setContent(" ");
        }
        checkPackageName(shareInfo);
        //判断是否需要进行加密
        checkSharePermission(shareInfo);

        switch (shareType) {
            case ShareType.SHARE_TYPE_WECHAT:
                if(!isWeChatInstall(mContext)) {
                    return;
                }
                shareTo(SHARE_MEDIA.WEIXIN, shareInfo, listener);
                break;
            case ShareType.SHARE_TYPE_WECHATMOMENTS:
                if(!isWeChatInstall(mContext)) {
                    return;
                }
                shareTo(SHARE_MEDIA.WEIXIN_CIRCLE, shareInfo, listener);
                break;
            case ShareType.SHARE_TYPE_QQ:
                if(!isQQInstall(mContext)) {
                    return;
                }
                shareTo(SHARE_MEDIA.QQ, shareInfo, listener);
                break;
            case ShareType.SHARE_TYPE_QZONE:
                if(!isQQInstall(mContext)) {
                    return;
                }
                shareTo(SHARE_MEDIA.QZONE, shareInfo, listener);
                break;
        }
    }

    private void checkPackageName(ShareInfo shareInfo) {
        if (!TextUtils.isEmpty(shareInfo.getTargetUrl())) {
            String targetUrl = shareInfo.getTargetUrl().trim();
            StringBuilder builder = new StringBuilder();
            builder.append(targetUrl);
            if(mContext != null) {
                if(targetUrl.contains("?")) {
                    builder.append("&pkgname=");
                } else {
                    builder.append("?pkgname=");
                }
                builder.append(mContext.getPackageName());
            }
            shareInfo.setTargetUrl(builder.toString());
        }
    }

    private void shareTo(SHARE_MEDIA shareMedia, ShareInfo shareInfo, UMShareListener listener) {
        ShareAction shareAction = new ShareAction(mContext);
        shareAction.setPlatform(shareMedia);
        UMediaObject uMediaObject = shareInfo.getuMediaObject();

        if(TextUtils.isEmpty(shareInfo.getTargetUrl())) {
            return;
        }
        UMWeb umWeb = new UMWeb(shareInfo.getTargetUrl());
        umWeb.setTitle(shareInfo.getTitle());
        umWeb.setDescription(shareInfo.getContent());
        if(uMediaObject instanceof UMImage) {
            umWeb.setThumb((UMImage)uMediaObject);
        }
        if(listener != null) {
            shareAction.setCallback(listener);
        }
        shareAction.withMedia(umWeb);
        shareAction.share();
    }

    private void checkSharePermission(ShareInfo shareInfo){
        SharedResource sharedResource = shareInfo.getSharedResource();
        if (sharedResource != null){
            int type = sharedResource.getResourceType();
            String shareUrl = shareInfo.getTargetUrl();

            if (isLQCourse(type) || isStudyCard(type)) {
                if (shareInfo.isPublicRescourse()) {
                    //表示公开的资源
                    if (isStudyCard(type)) {
                        shareUrl = encryptionVisitUrl(shareUrl, true, type, null);
                    }
                } else {
                    shareUrl = encryptionVisitUrl(shareUrl, false, type, shareInfo.getParentId());
                }
            }

            shareInfo.setTargetUrl(shareUrl);
        }
    }

    /**
     * 是否是LQ课件（包含有声相册）
     * @return
     */
    public static boolean isLQCourse(int resourceType){
        boolean yes = false;
        resourceType = resourceType % 10000;
        if (resourceType == 5
                || resourceType == 16
                || resourceType == 19
                || resourceType == 18){
            yes = true;
        }
        return yes;
    }

    public static boolean isStudyCard(int type){
        if (type == 23 || type == 10023){
            return true;
        }else {
            return false;
        }
    }

    public static String encryptionVisitUrl(String url, boolean isPublicResource,int type,
                                            String parentId) {
        boolean isSplitCourse = type > 10000;
        if (!TextUtils.isEmpty(url)){
            int index = url.indexOf("?");
            String headUrl = url.substring(0,index);
            String bodyUrl = url.substring(index+1,url.length());
            if (isLQCourse(type)){
                if (isSplitCourse){
                    if (!TextUtils.isEmpty(parentId)) {
                        bodyUrl = bodyUrl + "&auth=true&parentId=" + parentId;
                    }else {
                        bodyUrl = bodyUrl + "&auth=true";
                    }
                }else {
                    bodyUrl = bodyUrl + "&auth=true";
                }
            }else if (isStudyCard(type)){
                if (isPublicResource) {
                    bodyUrl = bodyUrl + "&auth=false";
                }else {
                    return url;
                }
            }
            bodyUrl = LqBase64Helper.getEncoderString(bodyUrl);
            bodyUrl = bodyUrl.replaceAll("[\n\r]", "");
            url = headUrl + "?enc=" + bodyUrl;
        }
        return url;
    }
    private void shareToWeChat(ShareInfo shareInfo) {
        shareTo(SHARE_MEDIA.WEIXIN,shareInfo, null);
    }

    private void shareToWeChatMoments(ShareInfo shareInfo) {
        shareTo(SHARE_MEDIA.WEIXIN_CIRCLE, shareInfo, null);
    }

    private void shareToQQ(ShareInfo shareInfo) {
        shareTo(SHARE_MEDIA.QQ, shareInfo, null);
    }

    private void shareToQzone(ShareInfo shareInfo) {
        shareTo(SHARE_MEDIA.QZONE, shareInfo, null);
    }

    public static boolean isAppInstall(Activity activity, SHARE_MEDIA shareMedia) {
        UMShareAPI umShareAPI = UMShareAPI.get(activity);
        return umShareAPI.isInstall(activity, shareMedia);
    }

    public static boolean isQQInstall(Activity activity) {
        boolean isInstall = isAppInstall(activity, SHARE_MEDIA.QQ);
        if (!isInstall) {
            TipMsgHelper.ShowMsg(activity, R.string.install_qq);
        }
        return isInstall;
    }

    public static boolean isWeChatInstall(Activity activity) {
        boolean isInstall = isAppInstall(activity, SHARE_MEDIA.WEIXIN);
        if (!isInstall) {
            TipMsgHelper.ShowMsg(activity, R.string.install_wechat);
        }
        return isInstall;
    }
}
