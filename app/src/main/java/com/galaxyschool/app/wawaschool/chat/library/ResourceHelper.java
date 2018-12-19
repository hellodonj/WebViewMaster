package com.galaxyschool.app.wawaschool.chat.library;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.DialogHelper.LoadingDialog;
import com.galaxyschool.app.wawaschool.common.WebUtils;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.library.LqBase64Helper;

public class ResourceHelper {

    private Activity activity;
    private LoadingDialog loadingDialog;

    public ResourceHelper(Activity activity) {
        this.activity = activity;
    }

    public Dialog showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return loadingDialog;
        }
        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        return loadingDialog;
    }

    public Dialog showLoadingDialog(String content, boolean cancelable) {
        Dialog dialog = showLoadingDialog();
        ((LoadingDialog) dialog).setContent(content);
        dialog.setCancelable(cancelable);
        return dialog;
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void openResourceByMessage(EMConversation conversation, EMMessage message) {
        openResourceByMessage(conversation, message, false);
    }

    public void openResourceByMessage(EMConversation conversation, EMMessage message,
            boolean isChatForbidden) {
        UserInfo userInfo = ((MyApplication) activity.getApplicationContext()).getUserInfo();
        String myHxId = "hx" + userInfo.getMemberId();
        ContactItem contactItem = new ContactItem();
        contactItem.setHxId(conversation.conversationId());
        if (message.getChatType() == EMMessage.ChatType.Chat) {
            contactItem.setChatType(ChatActivity.CHATTYPE_SINGLE);
            try {
                if (message.getFrom().equals(myHxId)) {
                    contactItem.setHxId(message.getTo());
                    contactItem.setName(
                            message.getStringAttribute(ChatActivity.EXTRA_TO_USER_NICKNAME));
                    contactItem.setIcon(
                            message.getStringAttribute(ChatActivity.EXTRA_TO_USER_AVATAR));
                } else {
                    contactItem.setHxId(message.getFrom());
                    contactItem.setName(
                            message.getStringAttribute(ChatActivity.EXTRA_USER_NICKNAME));
                    contactItem.setIcon(
                            message.getStringAttribute(ChatActivity.EXTRA_USER_AVATAR));
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            contactItem.setChatType(ChatActivity.CHATTYPE_GROUP);
            contactItem.setIsChatForbidden(isChatForbidden);
            contactItem.setName("");
            contactItem.setIcon("");
        }
        openResource(ConversationHelper.getResourceFromMessage(message), contactItem);
    }

    public void openResource(SharedResource resource) {
        openResource(resource, null);
    }

    public void openResource(SharedResource resource, ContactItem contactItem) {
        if (SharedResource.RESOURCE_TYPE_STREAM.equals(resource.getType())) {
            openStreamResource(resource, contactItem);
        } else if (SharedResource.RESOURCE_TYPE_FILE.equals(resource.getType())) {
            openFileResource(resource, contactItem);
        } else if (SharedResource.RESOURCE_TYPE_HTML.equals(resource.getType())) {
            openHtmlResource(resource);
        } else if(SharedResource.RESOURCE_TYPE_NOTE.equals(resource.getType())) {
            openNoteResource(resource, contactItem);
        }
    }

    public void openFileResource(SharedResource resource) {
        openFileResource(resource, null);
    }

    public void openFileResource(SharedResource resource, ContactItem contactItem) {
//        showLoadingDialog(activity.getString(R.string.loading_and_wait), true);
//        OpenCHWParam data = new OpenCHWParam(activity, resource.getId(),
//                resource.getUrl(), ResourceUtils.toShareParams(resource), false,
//                new CallbackListener() {
//                    @Override
//                    public void onBack(Object result) {
//                        if (activity == null) {
//                            return;
//                        }
//                        dismissLoadingDialog();
//                        if (((Boolean) result).booleanValue()) {
//
//                        } else {
//                            TipsHelper.showToast(activity, R.string.load_failed);
//                        }
//                    }
//                });
//        data.collectParams = ResourceUtils.toCollectParams(resource);
//        data.needCommitReadCount = false;
//        if(resource != null) {
//            data.orientation = resource.getScreenType();
//        }
//        data.courseInfo = ResourceUtils.toCourseInfo(resource);
//
//        CreateSlideHelper.loadAndOpenSlideByCHWUrl(data);
        NewResourceInfo newResourceInfo = ResourceUtils.toNewResourceInfo(resource);
        if(newResourceInfo != null) {
            if(newResourceInfo != null) {
                String shareUrl = newResourceInfo.getShareAddress();
                if (shareUrl.contains("enc=")) {
                    //加密的分享url
                    String headUrl = shareUrl.substring(0, shareUrl.indexOf("?") + 1);
                    String bodyUrl = transformResultUrl(shareUrl);
                    newResourceInfo.setShareAddress(headUrl + bodyUrl);
                    checkResourcePermission(newResourceInfo,bodyUrl);
                } else if (newResourceInfo.isStudyCard()){
                    int index = shareUrl.indexOf("?");
                    checkResourcePermission(newResourceInfo,shareUrl.substring((index+1),shareUrl.length()));
                } else {
//                if (newResourceInfo.isStudyCard()) {//任务单
//                    ActivityUtils.enterTaskOrderDetailActivity(activity, newResourceInfo);
//                } else {
                    ActivityUtils.openPictureDetailActivity(activity, newResourceInfo);
//                }
                }
            }
//            if(newResourceInfo.isStudyCard()){//任务单
//                ActivityUtils.enterTaskOrderDetailActivity(activity,newResourceInfo);
//            }else{
//                ActivityUtils.openPictureDetailActivity(activity, newResourceInfo);
//            }
        }
    }

    private void checkResourcePermission(final NewResourceInfo newResourceInfo,String bodyUrl){
        String shareUrl = newResourceInfo.getShareAddress();
        String parentId = null;
        if (shareUrl.contains("auth=false")){
            newResourceInfo.setIsFromSchoolResource(true);
            newResourceInfo.setIsFromAirClass(true);
            newResourceInfo.setIsHasPermission(true);
            ActivityUtils.enterTaskOrderDetailActivity(activity, newResourceInfo);
        } else {
            if (isMySelfCourse(newResourceInfo) || VipConfig.isVip(activity)) {
                if (newResourceInfo.isStudyCard()) {
                    newResourceInfo.setIsFromSchoolResource(true);
                    newResourceInfo.setIsFromAirClass(true);
                    ActivityUtils.enterTaskOrderDetailActivity(activity, newResourceInfo);
                } else {
                    ActivityUtils.openPictureDetailActivity(activity, newResourceInfo);
                }
            } else {
                //私有的资源
                PlaybackParam playbackParam = new PlaybackParam();
                playbackParam.mIsAuth = true;
                if (newResourceInfo.isStudyCard() || newResourceInfo.isOnePage()) {
                    ActivityUtils.openOnlineOnePage(activity, newResourceInfo, true, playbackParam);
                } else {
                    ActivityUtils.playOnlineCourse(activity, newResourceInfo.getCourseInfo(),
                            false, playbackParam);
                }
            }

//            if (bodyUrl.contains("parentId")){
//                String [] splitArray = bodyUrl.split("&");
//                if (splitArray != null && splitArray.length > 0){
//                    for (int i = 0;i<splitArray.length;i++){
//                        String value = splitArray[i];
//                        if (value.contains("parentId")){
//                            parentId = value.split("=")[1];
//                        }
//                    }
//                }
//            }
//            CheckResPermissionHelper permissionHelper = new
//                    CheckResPermissionHelper(activity);
//            permissionHelper.setResType(newResourceInfo.getResourceType())
//                    .setCouseId(newResourceInfo.getIdType())
//                    .setMemberId(DemoApplication.getInstance().getMemberId())
//                    .setParentId(parentId)
//                    .setCheckListener(new CheckResPermissionHelper.CheckResourceResultListener() {
//                        @Override
//                        public void onCheckResult(int resType, String courseId, boolean isPublicResource) {
//                            if (isPublicResource || isMySelfCourse(newResourceInfo)){
//                                if (newResourceInfo.isStudyCard()){
//                                    newResourceInfo.setIsFromAirClass(true);
//                                    newResourceInfo.setIsFromSchoolResource(true);
//                                    ActivityUtils.enterTaskOrderDetailActivity(activity, newResourceInfo);
//                                }else {
//                                    ActivityUtils.openPictureDetailActivity(activity, newResourceInfo);
//                                }
//                            }else {
//                                //私有的资源
//                                if (newResourceInfo.isStudyCard()){
//                                    PlaybackParam playbackParam = new PlaybackParam();
//                                    playbackParam.mIsAuth = true;
//                                    ActivityUtils.openOnlineOnePage(activity,newResourceInfo, true,playbackParam);
//                                }else {
//                                    PlaybackParam playbackParam = new PlaybackParam();
//                                    playbackParam.mIsAuth = true;
//                                    ActivityUtils.playCourse(activity, newResourceInfo.getCourseInfo(),
//                                            null,null,playbackParam);
//                                }
//                            }
//                        }
//                    })
//                    .checkResource();
        }

    }

    /**
     * 判断课件的作者是不是自己
     * @param newResourceInfo
     * @return
     */
    private boolean isMySelfCourse(NewResourceInfo newResourceInfo){
        if (newResourceInfo != null){
            String memberId = DemoApplication.getInstance().getMemberId();
            if (!TextUtils.isEmpty(memberId)){
                if (memberId.equals(newResourceInfo.getAuthorId())){
                    return true;
                }
            }
        }
        return false;
    }
    private String transformResultUrl(String url) {
        int index = url.indexOf("enc=") + 4;
        url = url.substring(index, url.length());
        url = LqBase64Helper.getDecodeString(url);
        return url;
    }

    public void openStreamResource(SharedResource resource) {
        openStreamResource(resource, null);
    }

    public void openStreamResource(SharedResource resource, ContactItem contactItem) {
//        CourseInfo courseInfo = ResourceUtils.toCourseInfo(resource);
//        if(courseInfo != null) {
//            courseInfo.setContactItem(contactItem);
//        }
//        ActivityUtils.playCourse(activity, courseInfo, null);
        NewResourceInfo newResourceInfo = ResourceUtils.toNewResourceInfo(resource);
        if(newResourceInfo != null) {
            if(newResourceInfo != null) {
                String shareUrl = newResourceInfo.getShareAddress();
                if (shareUrl.contains("enc=")) {
                    //加密的分享url
                    String headUrl = shareUrl.substring(0, shareUrl.indexOf("?") + 1);
                    String bodyUrl = transformResultUrl(shareUrl);
                    newResourceInfo.setShareAddress(headUrl + bodyUrl);
                    checkResourcePermission(newResourceInfo,bodyUrl);
                } else if (newResourceInfo.isStudyCard()){
                    int index = shareUrl.indexOf("?");
                    checkResourcePermission(newResourceInfo,shareUrl.substring((index+1),shareUrl.length()));
                } else {
//                if (newResourceInfo.isStudyCard()) {//任务单
//                    ActivityUtils.enterTaskOrderDetailActivity(activity, newResourceInfo);
//                } else {
                    ActivityUtils.openCourseDetail(activity, newResourceInfo, PictureBooksDetailActivity.FROM_OTHRE);
//                }
                }
            }

//        NewResourceInfo newResourceInfo = ResourceUtils.toNewResourceInfo(resource);
//        if(newResourceInfo != null) {
//            ActivityUtils.openCourseDetail(activity, newResourceInfo, PictureBooksDetailActivity.FROM_OTHRE);
        }
    }

    protected void openHtmlResource(SharedResource resource) {
        if (TextUtils.isEmpty(resource.getShareUrl())) {
            return;
        }
        String memberId = ((MyApplication) activity.getApplication()).getUserInfo().getMemberId();
        if (resource.getShareUrl().contains("/Invitation_Apply.aspx")
                || resource.getShareUrl().contains("/UserInfo.aspx")
                || resource.getShareUrl().contains("/SchoolInfo.aspx")
                || resource.getShareUrl().contains("/LQ_BookShare.aspx")) {
            resource.patchFieldShareUrlWithUserId(memberId);
        }
        if (!resource.getShareUrl().contains("hidefooter=true")) {
            resource.patchFieldShareUrlWithHideFooter();
        }
        WebUtils.openWebView(activity, resource.getShareUrl(), null,
            resource.getTitle());
    }

    public void openNoteResource(SharedResource resource) {
        openNoteResource(resource, null);
    }

    public void openNoteResource(SharedResource resource, ContactItem contactItem) {
            ActivityUtils.openOnlineNote(activity, ResourceUtils.toCourseInfo
                    (resource), false, false);
    }

}
