package com.galaxyschool.app.wawaschool.chat.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.Constant;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.NetResultListener;
import com.lqwawa.lqbaselib.net.library.MapParamsStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ConversationInfoList;
import com.galaxyschool.app.wawaschool.pojo.ConversationInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.GroupBasicInfo;
import com.galaxyschool.app.wawaschool.pojo.UserBasicInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.oosic.apps.share.SharedResource;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConversationHelper  {

    public static final String TAG = ConversationHelper.class.getSimpleName();

    private Context context;
    private List<ConversationInfo> conversations;
    private int unreadMsgCount;
    private ConversationChangedListener changedListener;
    private Handler handler = new Handler();
    EMMessageListener messageListener =new EMMessageListener (){
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            if (ConversationHelper.this.changedListener != null) {
                ConversationHelper. this.changedListener.onConversationChanged();
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            if (ConversationHelper.this.changedListener != null) {
                ConversationHelper. this.changedListener.onConversationChanged();
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    public ConversationHelper(Context context) {
        this.context = context;
        //注册连接监听
//        EMChatManager.getInstance().addConnectionListener(this);
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
//        EMClient.getInstance().setAppInited();
    }

    public List<ConversationInfo> getConversations() {
        return this.conversations;
    }

    public int getUnreadMessageCount() {
//        return calculateUnreadMessageCount();
        return calculateUnreadMessageCount(loadRecentConversations());
    }
    
    public void clear() {
    	unreadMsgCount = 0;
    	if (conversations != null) {
			conversations.clear();
		}
    }

    public void registerConversationChangedListener(ConversationChangedListener listener) {
        this.changedListener = listener;
    }

//    @Override
//    public void onEvent(EMNotifierEvent event) {
//        switch (event.getEvent()) {
//        case EventNewMessage:
//            if (this.changedListener != null) {
//                this.changedListener.onConversationChanged();
//            }
//            break;
//        case EventOfflineMessage:
//            if (this.changedListener != null) {
//                this.changedListener.onConversationChanged();
//            }
//            break;
//        case EventConversationListChanged:
//            if (this.changedListener != null) {
//                this.changedListener.onConversationChanged();
//            }
//            break;
//        }
//    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(Context context, EMMessage message) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION: // 位置消息
            if (message.direct ()== EMMessage.Direct.RECEIVE) {
                // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                // digest = EasyUtils.getAppResourceString(context,
                // "location_recv");
                digest = context.getString(R.string.location_recv);
                digest = String.format(digest, message.getFrom());
                return digest;
            } else {
                // digest = EasyUtils.getAppResourceString(context,
                // "location_prefix");
                digest = context.getString(R.string.location_prefix);
            }
            break;
        case IMAGE: // 图片消息
            EMImageMessageBody imageBody = (EMImageMessageBody) message.getBody();
            digest = context.getString(R.string.picture) + imageBody.getFileName();
            break;
        case VOICE:// 语音消息
            digest = context.getString(R.string.voice);
            break;
        case VIDEO: // 视频消息
            digest = context.getString(R.string.video_msg);
            break;
        case TXT: // 文本消息
            if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)) {
            if (ConversationHelper.isResourceMessage(message)) {
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                digest = context.getString(R.string.resource_msg) + txtBody.getMessage();
            } else {
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                digest = txtBody.getMessage();
            }
        } else {
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            digest = context.getString(R.string.voice_call) + txtBody.getMessage();
        }
            break;
        case FILE: // 普通文件消息
            digest = context.getString(R.string.file_msg);
            break;
        default:
            EMLog.e(TAG, "unknow type");
            return "";
        }

        return digest;
    }

    public void loadConversations(NetResultListener listener) {
        List<EMConversation> conversations = loadRecentConversations();
        if (conversations == null || conversations.size() <= 0) {
            if (listener != null) {
                listener.onFinish();
            }
//            return;
        }
        loadConversationInfoList(conversations, listener);
    }

    public List<EMConversation> loadRecentConversations() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations =
                EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList =
                new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    try {
                        String topic = conversation.getLastMessage()
                                .getStringAttribute(ChatActivity.EXTRA_TOPIC);
                        if (ChatActivity.TOPIC_HOMEWORK.equals(topic)) {
                            continue;
                        }
                    } catch (HyphenateException e) {

                    }
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(
                            conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationsByLastTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        unreadMsgCount = calculateUnreadMessageCount(list);
        return list;
    }

    private void sortConversationsByLastTime(List<Pair<Long,
            EMConversation>> conversationList) {
        Collections.sort(conversationList,
                new Comparator<Pair<Long, EMConversation>>() {
                    @Override
                    public int compare(final Pair<Long, EMConversation> con1,
                                       final Pair<Long, EMConversation> con2) {
                        if (con1.first == con2.first) {
                            return 0;
                        } else if (con2.first > con1.first) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
    }

    public static void enterConversation(Context context, ConversationInfo info) {
        Intent intent = getConversationIntent(context, info);
        context.startActivity(intent);
    }

    public static Intent getConversationIntent(Context context, ConversationInfo info) {
        if (!HXSDKHelper.getInstance().isLogined()) {
            TipsHelper.showToast(context, R.string.chat_service_not_works);
            return null;
        }
        String userName = info.conversation.conversationId();
        if (userName.equals(DemoApplication.getInstance().getUserName())) {
            return null;
        }
        // 进入聊天页面
        Bundle args = new Bundle();
        if (info.conversation.isGroup()) {
            if (info.conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                // it is group chat
                args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_CHATROOM);
                args.putString(ChatActivity.EXTRA_GROUP_ID, userName);
                args.putString(ChatActivity.EXTRA_GROUP_NAME, info.name);
            } else {
                // it is group chat
                args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_GROUP);
                args.putString(ChatActivity.EXTRA_GROUP_ID, userName);
                args.putString(ChatActivity.EXTRA_GROUP_NAME, info.name);
                args.putString(ChatActivity.EXTRA_GROUP_ICON, info.icon);
                args.putString(ChatActivity.EXTRA_CONTACT_ID, info.contactId);
                args.putInt(ChatActivity.EXTRA_CONTACT_TYPE, info.contactType);
                args.putString(ChatActivity.EXTRA_CLASS_ID, info.classId);
                args.putString(ChatActivity.EXTRA_SCHOOL_ID, info.schoolId);
                args.putString(ChatActivity.EXTRA_SCHOOL_NAME, info.schoolName);
                args.putBoolean(ChatActivity.EXTRA_IS_CHAT_FORBIDDEN, info.isChatForbidden);
            }
        } else {
            // it is single chat
            args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE);
            args.putString(ChatActivity.EXTRA_USER_ID, userName);
            args.putString(ChatActivity.EXTRA_USER_NICKNAME, info.name);
            args.putString(ChatActivity.EXTRA_USER_AVATAR, info.icon);
            args.putString(ChatActivity.EXTRA_MEMBER_ID, info.memberId);
            args.putString(ChatActivity.EXTRA_CONTACT_ID, info.contactId);
            args.putBoolean(ChatActivity.EXTRA_IS_FRIEND, info.isFriend);
        }
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtras(args);
        return intent;
    }


    private void loadConversationInfoList(List<EMConversation> conversations, NetResultListener listener) {
        Set<String> memberSet = new HashSet();
        Set<String> groupSet = new HashSet();
        UserInfo userInfo = ((MyApplication) this.context.getApplicationContext()).getUserInfo();
        if (userInfo == null){
            return;
        }
        String myHxId = "hx" + userInfo.getMemberId();
        for (EMConversation conversation : conversations) {
            EMMessage lastMsg = conversation.getLastMessage();
            if (conversation.getType() == EMConversation.EMConversationType.Chat) {
                if (lastMsg.getFrom().equals(myHxId)) {
                    if (lastMsg.getTo().startsWith("hx")) {
                        memberSet.add(lastMsg.getTo().substring(2));
                    } else {
                        memberSet.add(lastMsg.getTo());
                    }
                } else {
                    memberSet.add(lastMsg.getFrom().substring(2));
                    if (lastMsg.getFrom().startsWith("hx")) {
                        memberSet.add(lastMsg.getFrom().substring(2));
                    } else {
                        memberSet.add(lastMsg.getFrom());
                    }
                }
            } else if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                groupSet.add(conversation.conversationId());
                if (!lastMsg.getFrom().equals(myHxId)) {
                    if (lastMsg.getFrom().startsWith("hx")) {
                        memberSet.add(lastMsg.getFrom().substring(2));
                    } else {
                        memberSet.add(lastMsg.getFrom());
                    }
                }
            }
        }
        ConversationInfoListListener l = new ConversationInfoListListener(
                conversations, listener);
        if (this.conversations != null && this.conversations.size() > 0) {
            ConversationInfo newInfo = null;
            for (ConversationInfo oldInfo : this.conversations) {
                if (l.infoMap.containsKey(oldInfo.conversation.conversationId())) {
                    newInfo = l.infoMap.get(oldInfo.conversation.conversationId());
                    newInfo.id = oldInfo.id;
                    newInfo.name = oldInfo.name;
                    newInfo.icon= oldInfo.icon;
                    if (newInfo.lastMessageSenderId.equals(
                            oldInfo.lastMessageSenderId)) {
                        newInfo.lastMessageSenderName = oldInfo.lastMessageSenderName;
                    }
                }
            }
        }
        this.conversations = l.infoList;
        List<String> memberIds = new ArrayList<String>();
        Iterator<String> it = memberSet.iterator();
        while (it.hasNext()) {
            memberIds.add(it.next());
        }
        List<String> groupIds = new ArrayList<String>();
        it = groupSet.iterator();
        while (it.hasNext()) {
            groupIds.add(it.next());
        }
        loadConversationInfoList(memberIds, groupIds, l);
    }

    private int calculateUnreadMessageCount() {
        int count = 0;
        if (this.conversations != null && this.conversations.size() > 0) {
            for (ConversationInfo info : this.conversations) {
                count += info.conversation.getUnreadMsgCount();
            }
        }
        return count;
    }

    private int calculateUnreadMessageCount(List<EMConversation> conversations) {
        if (conversations == null || conversations.size() <= 0) {
            return 0;
        }
        int count = 0;
        if (conversations != null && conversations.size() > 0) {
            for (EMConversation conversation : conversations) {
                count += conversation.getUnreadMsgCount();
            }
        }
        return count;
    }

    private void loadConversationInfoList(List<String> memberIdList,
                                  List<String> groupIdList, Listener listener) {
        StringBuilder sb = new StringBuilder();
        if (memberIdList != null && memberIdList.size() > 0) {
            for (String str : memberIdList) {
                sb.append(str).append("#");
            }
            sb.setLength(sb.length() - 1);
        }
        String memberIds = sb.toString();
        sb.setLength(0);
        if (groupIdList != null && groupIdList.size() > 0) {
            for (String str : groupIdList) {
                sb.append(str).append("#");
            }
            sb.setLength(sb.length() - 1);
        }
        String groupIds = sb.toString();

        Map<String, Object> params = new HashMap();
        params.put("MemberId", memberIds);
        params.put("GroupId", groupIds);
        if (((MyApplication) this.context.getApplicationContext()).getUserInfo() == null){
            return;
        }
        params.put("CurrentUserId",
                ((MyApplication) this.context.getApplicationContext()).getUserInfo().getMemberId());
        params.put("VersionCode", 1);
        params.put("IsShowAllGroup", true);
        postRequest(this.context, ServerUrl.GET_CONVERSATION_INFO_LIST_URL, params, listener);
    }

    public boolean deleteConversation(ConversationInfo info) {
        this.conversations.remove(info);
        return EMClient.getInstance().chatManager().deleteConversation(
                info.conversation.conversationId(), true);
    }

    public static boolean deleteSingleConversation(String hxId) {
        if (TextUtils.isEmpty(hxId)) {
            return false;
        }
        return EMClient.getInstance().chatManager().deleteConversation(hxId, false);
    }

    public static boolean deleteGroupConversation(String hxId) {
        if (TextUtils.isEmpty(hxId)) {
            return false;
        }
        return EMClient.getInstance().chatManager().deleteConversation(hxId, true);
    }

    private class ConversationInfoListListener extends DefaultListener {
        List<ConversationInfo> infoList;
        Map<String, ConversationInfo> infoMap;
        NetResultListener listener;

        public ConversationInfoListListener(List<EMConversation> conversations,
                                            NetResultListener listener) {
            super(context);
            this.infoList = new ArrayList<ConversationInfo>();
            this.infoMap = new HashMap<String, ConversationInfo>();
            for (EMConversation c : conversations) {
                ConversationInfo info = new ConversationInfo();
                info.conversation = c;
                info.lastMessageSenderId = c.getLastMessage().getFrom();
                info.time = c.getLastMessage().getMsgTime();
                this.infoList.add(info);
                this.infoMap.put(c.conversationId().toLowerCase(), info);
            }
            this.listener = listener;
        }

        @Override
        public void onSuccess(String jsonString) {
            if (getContext() == null) {
                return;
            }
            super.onSuccess(jsonString);
            ConversationInfoListResult result = getResult();
            if (result == null || !result.isSuccess()
                    || result.getModel() == null) {
                return;
            }
            ConversationInfoList infoList = result.getModel();
            if (infoList != null) {
                Map<String, UserBasicInfo> members = new HashMap();
                if (infoList.getMemberList() != null && infoList.getMemberList().size() > 0) {
                    for (int i = 0; i < infoList.getMemberList().size(); i++) {
                        final UserBasicInfo user = infoList.getMemberList().get(i);
                        if (user != null) {
                            //如果环信的id为空,手动给user赋值
                            String hxId = user.getHXID();
                            if (TextUtils.isEmpty(hxId)){
                                hxId = "hx" + user.getMemberId();
                                user.setHXID(hxId);
                            }
                            members.put(user.getHXID().toLowerCase(), user);
                            ConversationInfo info = this.infoMap.get(user.getHXID().toLowerCase());
                            if (info != null) {
                                info.icon = user.getHeaderPic();
                                info.name = user.getRealName();
                                if (TextUtils.isEmpty(info.name)) {
                                    info.name = user.getNickName();
                                }
                                info.id = user.getHXID();
                                info.contactId = user.getFriendId();
                                info.memberId = user.getMemberId();
                                info.isFriend = user.isWhetherFriends();
                            }
                        }
                    }
                }
                if (infoList.getGroupList() != null && infoList.getGroupList().size() > 0) {
                    String groupId = null;
                    for (int i = 0; i < infoList.getGroupList().size(); i++) {
                        final GroupBasicInfo group = infoList.getGroupList().get(i);
                        if (group != null) {
                            groupId = group.getGroupId().toLowerCase();
                            ConversationInfo info = this.infoMap.get(groupId);
                            if (info != null) {
                                if (!group.isInGroup()) {
                                    this.infoMap.remove(groupId);
                                    this.infoList.remove(info);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            EMClient.getInstance().chatManager().deleteConversation(
                                                    group.getGroupId(), true);
                                        }
                                    });
                                    continue;
                                }
                                info.icon = group.getHeadPicUrl();
                                info.name = group.getGruopName();
                                info.id = group.getGroupId();
                                info.contactId = group.getId();
                                info.contactType = group.getType();
                                info.classId = group.getClassId();
                                info.schoolId = group.getLQ_SchoolId();
                                info.schoolName = group.getSchoolName();
                                info.isChatForbidden = group.isChatForbidden();
                                UserBasicInfo user = members.get(
                                        info.conversation.getLastMessage().getFrom());
                                if (user != null) {
                                    info.lastMessageSenderName = user.getRealName();
                                    if (TextUtils.isEmpty(info.lastMessageSenderName)) {
                                        info.lastMessageSenderName = user.getNickName();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (this.listener != null) {
                this.listener.onSuccess(this.infoList);
            }
        }

        @Override
        public void onError(NetroidError error) {
            if (this.listener != null) {
//                this.listener.onSuccess(this.infoList);
            }
        }

        @Override
        public void onFinish() {
            if (this.listener != null) {
                this.listener.onFinish();
            }
        }
    }

    public static void sendResource(Context context, ContactItem contactItem, SharedResource resource,
                                    EMCallBack listener) {
        sendResource(context, contactItem.getChatType(),
                contactItem.getHxId(), contactItem.getName(), contactItem.getIcon(), resource, listener);
    }

    public static void sendResource(Context context, int chatType, String toUserId,
                String toUserNickname, String toUserAvatar, SharedResource resource,
                                    EMCallBack  listener) {
        String myUserAvatar = null;
        String myUserNickname = null;
        UserInfo userInfo = ((MyApplication) context.getApplicationContext()).getUserInfo();
        if (userInfo != null) {
            myUserAvatar = AppSettings.getFileUrl(userInfo.getHeaderPic());
            myUserNickname = userInfo.getRealName();
            if (TextUtils.isEmpty(myUserNickname)) {
                myUserNickname = userInfo.getNickName();
            }
        }
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == ChatActivity.CHATTYPE_GROUP){
            message.setChatType(EMMessage.ChatType.GroupChat);
        } else if (chatType == ChatActivity.CHATTYPE_CHATROOM){
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }

        EMTextMessageBody txtBody = new EMTextMessageBody(resource.getTitle());
        // 设置消息body
        message.addBody(txtBody);
        // 设置要发给谁,用户username或者群聊groupid
        message.setTo(toUserId);
        message.setAttribute(ChatActivity.EXTRA_USER_AVATAR, myUserAvatar);
        message.setAttribute(ChatActivity.EXTRA_USER_NICKNAME, myUserNickname);
        message.setAttribute(ChatActivity.EXTRA_TO_USER_AVATAR, toUserAvatar);
        message.setAttribute(ChatActivity.EXTRA_TO_USER_NICKNAME, toUserNickname);
        try {
            // 解决环信SDK升级到3.0发送到蛙蛙好友ios资源不能打开问题
            String resourceString = JSONObject.toJSONString(resource);
            org.json.JSONObject jsonObject = new org.json.JSONObject(resourceString);
            message.setAttribute(ChatActivity.EXTRA_RESOURCE,
                    jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(listener);
    }

    public static boolean isResourceMessage(EMMessage message) {
        try {
            return !TextUtils.isEmpty(message.getStringAttribute(
                    ChatActivity.EXTRA_RESOURCE));
        } catch (HyphenateException e) {
//            e.printStackTrace();
        }
        return false;
    }

    public static SharedResource getResourceFromMessage(EMMessage message) {
        try {
            String jsonStr = message.getStringAttribute(ChatActivity.EXTRA_RESOURCE);
            if (!TextUtils.isEmpty(jsonStr)) {
                return JSONObject.parseObject(jsonStr, SharedResource.class);
            }
        } catch (HyphenateException e) {

        }
        return null;
    }

    public static boolean hasLogined() {
        return HXSDKHelper.getInstance().isLogined();
    }

    public synchronized static void login(final String memberId, final String password) {
        if (TextUtils.isEmpty(memberId) || TextUtils.isEmpty(password)) {
            return;
        }

        if (HXSDKHelper.getInstance().isLogined()) {
            return;
        }

        final String userName = "hx" + memberId;
        Log.e("环信登陆","**************************环信开始登陆*****************************");
        try {
            EMClient.getInstance().login(userName, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                   // EMClient.getInstance().setAppInited();
                    Log.e("环信登陆","**************************环信登陆成功*****************************");

                    // 登陆成功，保存用户名密码
                    DemoApplication.getInstance().setUserName(userName);
                    DemoApplication.getInstance().setPassword(password);
                    try {
                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        if (!HXSDKHelper.getInstance().isLogined()) {
            return;
        }
        try {
//            EMChatManager.getInstance().logout(new EMCallBack() {
            HXSDKHelper.getInstance().logout(new EMCallBack() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ConversationChangedListener {

        public void onConversationChanged();

    }

    public static class ConversationInfo {
        public EMConversation conversation;
        public String id;
        public String icon;
        public String name;
        public long time;
        public String memberId;
        public String contactId;
        public int contactType;
        public String classId;
        public String schoolId;
        public String schoolName;
        public boolean isChatForbidden;
        public boolean isFriend;
        public String lastMessageSenderId;
        public String lastMessageSenderName;
    }

    public static class DefaultListener
            extends RequestHelper.RequestModelResultListener<ConversationInfoListResult> {
        public DefaultListener(Context context) {
            super(context, ConversationInfoListResult.class);
        }
    }

    public static MapParamsStringRequest postRequest(Context context,
            String url, Map<String, Object> params, Listener listener) {
        return RequestHelper.sendPostRequest(context, url, params, listener);
    }

}
