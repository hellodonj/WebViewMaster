/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.galaxyschool.app.wawaschool.chat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.galaxyschool.app.wawaschool.ContactsChatDialogActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.chat.applib.model.HXNotifier;
import com.galaxyschool.app.wawaschool.chat.applib.model.HXSDKModel;
import com.galaxyschool.app.wawaschool.chat.domain.User;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.chat.receiver.CallReceiver;
import com.galaxyschool.app.wawaschool.chat.utils.CommonUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.lqwawa.interaction.message.PenMessageDriver;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * @author easemob
 *
 */
public class DemoHXSDKHelper extends HXSDKHelper {

    private static final String TAG = "DemoHXSDKHelper";
    
    /**
     * EMEventListener
     */
    protected EMMessageListener eventListener = null;

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    private CallReceiver callReceiver;
    
    /**
     * 用来记录foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();
    
    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity); 
        }
    }
    
    public void popActivity(Activity activity){
        activityList.remove(activity);
    }
    
    @Override
    protected void initHXOptions(){
        super.initHXOptions();

        // you can also get EMChatOptions to set related SDK options
//        EMChatOptions options = new EMChatOptions();
//        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
    }

    @Override
    protected void initListener(){
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager()
                .getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }

        //注册通话广播接收者
        appContext.registerReceiver(callReceiver, callFilter);    
        //注册消息事件监听
        initEventListener();
    }
    
    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    public void initEventListener() {
        eventListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                for (EMMessage message : list) {
                    try {
                        String topic = message.getStringAttribute(ChatActivity.EXTRA_TOPIC);
                        if (topic!=null&&ChatActivity.TOPIC_HOMEWORK.equals(topic)) {
                            continue;
                        }
                        EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                        getNotifier().onNewMsg(message);
                    } catch (HyphenateException e) {
                        getNotifier().onNewMsg(message);
                    }

                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

                for (EMMessage message : list) {
                    EMLog.d(TAG, "收到透传消息");
                    //获取消息body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action

                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
                    final String str = appContext.getString(R.string.receive_the_passthrough);

                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
//                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };

                        //注册通话广播接收者
                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                    appContext.sendBroadcast(broadcastIntent, null);

                }


            }

            @Override
            public void onMessageRead(List<EMMessage> list) {
                for (EMMessage message : list) {
                    message.setAcked(true);
                }
            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {
                for (EMMessage message : list) {
                    message.setDelivered(true);
                }
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }

            private BroadcastReceiver broadCastReceiver = null;
            
//            @Override
//            public void onEvent(EMNotifierEvent event) {
//                EMMessage message = null;
//                if(event.getData() instanceof EMMessage){
//                    message = (EMMessage)event.getData();
//
//                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
//                }
//
//                switch (event.getEvent()) {
//                case EventNewMessage:
//                    //应用在后台，不需要刷新UI,通知栏提示新消息
//                    if(activityList.size() <= 0){
//                        getInstance().getNotifier().onNewMsg(message);
//                    }
//                    break;
//                case EventOfflineMessage:
//                    if(activityList.size() <= 0){
//                        EMLog.d(TAG, "received offline messages");
//                        List<EMMessage> messages = (List<EMMessage>) event.getData();
//                        getInstance().getNotifier().onNewMesg(messages);
//                    }
//                    break;
//                // below is just giving a example to show a cmd toast, the app should not follow this
//                // so be careful of this
//                case EventNewCMDMessage:
//                {
//                    /*if (PenMessageDriver.isInterationMessage(message)) {
//                        InteractionHelper.initInteraction();
//                        InteractionHelper.setInteractionParams();
//                        if (PenMessageDriver.receiveMessage(message,
//                                DemoApplication.getInstance().getActivityStack().getTop())) {
//                        }
//                        break;
//                    }*/
//
//                    EMLog.d(TAG, "收到透传消息");
//                    //获取消息body
//                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
//                    final String action = cmdMsgBody.action();//获取自定义action
//
//                    //获取扩展属性 此处省略
//                    //message.getStringAttribute("");
//                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
//                    final String str = appContext.getString(R.string.receive_the_passthrough);
//
//                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
//                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
//
//                    if(broadCastReceiver == null){
//                        broadCastReceiver = new BroadcastReceiver(){
//
//                            @Override
//                            public void onReceive(Context context, Intent intent) {
//                                // TODO Auto-generated method stub
////                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
//                            }
//                        };
//
//                      //注册通话广播接收者
//                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
//                    }
//
//                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
//                    broadcastIntent.putExtra("cmd_value", str+action);
//                    appContext.sendBroadcast(broadcastIntent, null);
//
//                    break;
//                }
//                case EventDeliveryAck:
//                    message.setDelivered(true);
//                    break;
//                case EventReadAck:
//                    message.setAcked(true);
//                    break;
//                // add other events in case you are interested in
//                default:
//                    break;
//                }
//
//            }
        };

        EMClient.getInstance().chatManager().addMessageListener(eventListener);

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(new EMChatRoomChangeListener() {
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;

            private void showToast(String value) {
                if (!registered) {
                    //注册通话广播接收者
                    appContext.registerReceiver(new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Toast.makeText(appContext, intent.getStringExtra("value"), Toast.LENGTH_SHORT).show();
                        }

                    }, filter);

                    registered = true;
                }

                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                appContext.sendBroadcast(broadcastIntent, null);
            }

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                Log.i("info", "onChatRoomDestroyed=" + roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showToast("member : " + participant + " join the room : " + roomId);
                Log.i("info", "onmemberjoined=" + participant);

            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberExited=" + participant);

            }



            @Override
            public void onMuteListAdded(String s, List<String> list, long l) {

            }

            @Override
            public void onMuteListRemoved(String s, List<String> list) {

            }

            @Override
            public void onAdminAdded(String s, String s1) {

            }

            @Override
            public void onAdminRemoved(String s, String s1) {

            }

            @Override
            public void onOwnerChanged(String s, String s1, String s2) {

            }

            @Override
            public void onAnnouncementChanged(String s, String s1) {

            }

            @Override
            public void onRemovedFromChatRoom(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberKicked=" + participant);

            }

        });
    }

    /**
     * 自定义通知栏提示内容
     * @return
     */
    @Override
    protected HXNotifier.HXNotificationInfoProvider getNotificationListener() {
        //可以覆盖默认的设置
        return new HXNotifier.HXNotificationInfoProvider() {
            
            @Override
            public String getTitle(EMMessage message) {
              //修改标题,这里使用默认
                return null;
            }
            
            @Override
            public int getSmallIcon(EMMessage message) {
              //设置小图标，这里为默认
                return 0;
            }
            
            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                if(message.getType() == EMMessage.Type.TXT
                        && !ConversationHelper.isResourceMessage(message)){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }

                String userName = null;
                try {
                    userName = message.getStringAttribute(ChatActivity.EXTRA_USER_NICKNAME);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(userName)) {
                    return userName + ": " + ticker;
                }
                return ticker;
//                return message.getFrom() + ": " + ticker;
            }
            
            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }
            
            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Long.toString(System.currentTimeMillis()));
                EMMessage.ChatType chatType = message.getChatType();
                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    try {
                        intent.putExtra(ChatActivity.EXTRA_USER_AVATAR,
                                message.getStringAttribute(ChatActivity.EXTRA_USER_AVATAR));
                        intent.putExtra(ChatActivity.EXTRA_USER_NICKNAME,
                                message.getStringAttribute(ChatActivity.EXTRA_USER_NICKNAME));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    if(chatType == EMMessage.ChatType.GroupChat){
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                    }else{
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                    }
                }
                intent.putExtra(ChatActivity.EXTRA_FROM_WHERE, ChatActivity.FROM_NOTIFICATION);
                return intent;
            }
        };
    }
    
    
    
    @Override
    protected void onConnectionConflict(){
        super.onConnectionConflict();
//        Intent intent = new Intent(appContext, CityPopwindow.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("conflict", true);
//        appContext.startActivity(intent);
        alertAccountInfo(ContactsChatDialogActivity.DIALOG_TYPE_ACCOUNT_CONFLICT);
    }
    
    @Override
    protected void onCurrentAccountRemoved(){
        super.onCurrentAccountRemoved();
//    	Intent intent = new Intent(appContext, CityPopwindow.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
//        appContext.startActivity(intent);
        alertAccountInfo(ContactsChatDialogActivity.DIALOG_TYPE_ACCOUNT_REMOVED);
    }

    private void alertAccountInfo(int infoType) {
        // Manually clear chat account information first
        ((MyApplication) this.appContext.getApplicationContext()).clearLoginUserInfo();
        ConversationHelper.logout();
        UserHelper.logout();//退出mooc
        Intent intent = new Intent(this.appContext, ContactsChatDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putInt(ContactsChatDialogActivity.EXTRA_DIALOG_TYPE, infoType);
        intent.putExtras(args);
        try {
            this.appContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }
    
    @Override
    public HXNotifier createNotifier(){
        return new HXNotifier(){
            public synchronized void onNewMsg(final EMMessage message) {
                if((message.getBooleanAttribute("em_ignore_notification", false))){
                    return;
                }
                
                String chatUsename = null;
                List<String> notNotifyIds = null;
                // 获取设置的不提示新消息的用户或者群组ids
                if (message.getChatType() == EMMessage.ChatType.Chat) {
                    chatUsename = message.getFrom();
                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledGroups();
                } else {
                    chatUsename = message.getTo();
                    notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledIds();
                }

                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                    // 判断app是否在后台
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        EMLog.d(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);

                    }
                    
                    viberateAndPlayTone(message);
                }
            }
        };
    }
    
    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel(){
        return (DemoHXSDKModel) hxModel;
    }
    
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((DemoHXSDKModel) getModel()).getContactList();
        }
        
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }
    
    @Override
    public void logout(final EMCallBack callback){
        endCall();
        super.logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setContactList(null);
                getModel().closeDB();
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }   
    
    void endCall(){
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
