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
package com.galaxyschool.app.wawaschool.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsMemberDetailsActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.DemoHXSDKHelper;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.adapter.ExpressionAdapter;
import com.galaxyschool.app.wawaschool.chat.adapter.ExpressionPagerAdapter;
import com.galaxyschool.app.wawaschool.chat.adapter.MessageAdapter;
import com.galaxyschool.app.wawaschool.chat.adapter.VoicePlayClickListener;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.chat.utils.CommonUtils;
import com.galaxyschool.app.wawaschool.chat.utils.ImageUtils;
import com.galaxyschool.app.wawaschool.chat.utils.SmileUtils;
import com.galaxyschool.app.wawaschool.chat.widget.ExpandGridView;
import com.galaxyschool.app.wawaschool.chat.widget.PasteEditText;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ClassContactsDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.ClassDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.FriendDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.PersonInfoFragment;
import com.galaxyschool.app.wawaschool.fragment.PersonalSpaceFragment;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VoiceRecorder;

import com.osastudio.common.utils.PhotoUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天页面
 * 
 */
public class ChatActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ChatActivity";

	public static final int REQUEST_CODE_CHAT = 1009;
	public static final int REQUEST_CODE_UPDATE_FRIEND_NICKNAME = 10086;
	public static final String EXTRA_CLASS_NAME_CHANGED = "groupNameChanged";

	public static final String EXTRA_LAYOUT_ID = "layoutId";
	public static final String EXTRA_CHAT_TYPE = "chatType";
	public static final String EXTRA_USER_ID = "userId";
	public static final String EXTRA_GROUP_ID = "groupId";
	public static final String EXTRA_GROUP_NAME = "groupName";
	public static final String EXTRA_GROUP_ICON = "groupIcon";
	public static final String EXTRA_USER_AVATAR = "userAvatar";
	public static final String EXTRA_USER_NICKNAME = "userNickname";
	public static final String EXTRA_TO_USER_AVATAR = "toUserAvatar";
	public static final String EXTRA_TO_USER_NICKNAME = "toUserNickname";
	public static final String EXTRA_TOPIC = "topic";
	public static final String EXTRA_RESOURCE = "resource";
	public static final String EXTRA_MEMBER_ID= "memberId";
	public static final String EXTRA_CONTACT_ID = "contactId";
	public static final String EXTRA_CONTACT_TYPE = "contactType";
	public static final String EXTRA_CLASS_ID = "classId";
	public static final String EXTRA_CLASS_NAME = "classId";
	public static final String EXTRA_SCHOOL_ID = "schoolId";
	public static final String EXTRA_SCHOOL_NAME = "schoolName";
	public static final String EXTRA_IS_CHAT_FORBIDDEN = "isChatForbidden";
	public static final String EXTRA_IS_FRIEND = "isFriend";
	public static final String EXTRA_FROM_WHERE = "fromWhere";

	public static final String TOPIC_HOMEWORK = "homework";

	public static final int FROM_FRIEND = 1;
	public static final int FROM_CLASS = 2;
	public static final int FROM_SCHOOL = 3;
	public static final int FROM_HOMEWORK = 4;
	public static final int FROM_NOTIFICATION = 5;
	public static final int FROM_CHAT = 100;
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;
	public static final int RESULT_CODE_SAVE = 8;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;

	public static final String COPY_IMAGE = "EASEMOBIMG";
	private View bottomBar;
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	// private ViewPager expressionViewpager;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView locationImgview;
	private View more;
	private int position;
	private ClipboardManager clipboard;
	private ViewPager expressionViewpager;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private int chatType;
	private EMConversation conversation;
	public static ChatActivity activityInstance = null;
	// 给谁发送消息
	private String toChatUsername;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private File cameraFile;
	static int resendPos;

	private GroupListener groupListener;

	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private Button btnMore;
	public String playMsgId;

	private String toUserAvatar;
	private String toUserNickname;
	private String myUserAvatar;
	private String myUserNickname;
	private String topic;
	private String groupName;
	private UserBasicInfo userBasicInfo;
	private GroupBasicInfo groupBasicInfo;
	private boolean isChatForbidden;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private Handler handler = new Handler();
	public EMGroup group;
	public EMChatRoom room;
	private MyBroadCastReceiver receiver;
	private static boolean hasContentChanged;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_chat);
		int layoutId = getIntent().getIntExtra(EXTRA_LAYOUT_ID, R.layout.activity_chat);
		setContentView(layoutId);

		UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
		if (userInfo != null) {
			this.myUserAvatar = AppSettings.getFileUrl(userInfo.getHeaderPic());
			this.myUserNickname = userInfo.getRealName();
			if (TextUtils.isEmpty(this.myUserNickname)) {
				this.myUserNickname = userInfo.getNickName();
			}
		}

		activityInstance = this;
		initView();
		setUpView();
		initBroadCastReceiver();
	}

	private void initBroadCastReceiver() {
        receiver = new MyBroadCastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PersonalSpaceFragment.ACTION_MODIFY_USER_REMARK_NAME);
        registerReceiver(receiver,filter);
	}
//	/**
//	 * 事件监听
//	 *
//	 * see {@link EMNotifierEvent}
//	 */
//	@Override
//	public void onEvent(EMNotifierEvent event) {
//		switch (event.getEvent()) {
//			case EventNewMessage:
//			{
//				//获取到message
//				EMMessage message = (EMMessage) event.getData();
//
//				String username = null;
//				//群组消息
//				if(message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom){
//					username = message.getTo();
//				}
//				else{
//					//单聊消息
//					username = message.getFrom();
//				}
//
//				//如果是当前会话的消息，刷新聊天页面
//				if(username.equals(getToChatUsername())){
//					refreshUIWithNewMessage();
//					//声音和震动提示有新消息
//					HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
//				}else{
//					//如果消息不是和当前聊天ID的消息
//					HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
//				}
//
//				break;
//			}
//			case EventDeliveryAck:
//			{
//				//获取到message
//				EMMessage message = (EMMessage) event.getData();
//				refreshUI();
//				break;
//			}
//			case EventReadAck:
//			{
//				//获取到message
//				EMMessage message = (EMMessage) event.getData();
//				refreshUI();
//				break;
//			}
//			case EventOfflineMessage:
//			{
//				//a list of offline messages
//				//List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
//				refreshUI();
//				break;
//			}
//			default:
//				break;
//		}
//
//	}
	EMMessageListener messageListener =new EMMessageListener (){
	@Override
	public void onMessageReceived(List<EMMessage> list) {
		for(EMMessage message : list){
			String username = null;
			//群组消息
			if(message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom){
				username = message.getTo();
			}
			else{
				//单聊消息
				username = message.getFrom();
			}

			//如果是当前会话的消息，刷新聊天页面
			if(username.equals(getToChatUsername())){
				refreshUIWithNewMessage();

				//声音和震动提示有新消息
				HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
			}else{
				//如果消息不是和当前聊天ID的消息
				HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			}

		}
	}

	@Override
	public void onCmdMessageReceived(List<EMMessage> list) {

	}

	@Override
	public void onMessageRead(List<EMMessage> list) {
		refreshUI();
	}

	@Override
	public void onMessageDelivered(List<EMMessage> list) {
		refreshUI();
	}

	@Override
	public void onMessageRecalled(List<EMMessage> list) {

	}

	@Override
	public void onMessageChanged(EMMessage emMessage, Object o) {

	}
	};




	private final class MyBroadCastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent != null && !TextUtils.isEmpty(intent.getAction())){
				if (intent.getAction().equals(PersonalSpaceFragment.ACTION_MODIFY_USER_REMARK_NAME))
				{
					//修改好友备注名
					String remarkName = null;
					remarkName = intent.getStringExtra("remarkName");
					if (!TextUtils.isEmpty(remarkName)){
//						if (group != null) {
//							group.setGroupName(remarkName);
//						}
						TextView titleTextView = (TextView)findViewById(R.id.contacts_header_title);
						if (titleTextView != null) {
							titleTextView.setText(remarkName);
						}
					}
				}
			}
		}
	}

	/**
	 * initView
	 */
	protected void initView() {
		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (Button) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		more = findViewById(R.id.more);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
		findViewById(R.id.contacts_header_left_btn).setOnClickListener(this);

		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void setUpView() {
		findViewById(R.id.btn_set_mode_voice).setOnClickListener(this);
		findViewById(R.id.btn_set_mode_keyboard).setOnClickListener(this);
		findViewById(R.id.et_sendmessage).setOnClickListener(this);
		findViewById(R.id.btn_video_call).setOnClickListener(this);
		findViewById(R.id.btn_voice_call).setOnClickListener(this);
		findViewById(R.id.btn_location).setOnClickListener(this);
		findViewById(R.id.btn_file).setOnClickListener(this);
		findViewById(R.id.btn_video).setOnClickListener(this);
		findViewById(R.id.btn_picture).setOnClickListener(this);
		findViewById(R.id.btn_take_picture).setOnClickListener(this);
		findViewById(R.id.btn_send).setOnClickListener(this);
		findViewById(R.id.btn_more).setOnClickListener(this);
		findViewById(R.id.contacts_header_left_btn).setOnClickListener(this);
		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);
		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

		// 判断单聊还是群聊
		chatType = getIntent().getIntExtra(EXTRA_CHAT_TYPE, CHATTYPE_SINGLE);
		topic = getIntent().getStringExtra(EXTRA_TOPIC);

		if (chatType == CHATTYPE_SINGLE) { // 单聊
			toChatUsername = getIntent().getStringExtra(EXTRA_USER_ID);
			this.toUserAvatar = getIntent().getStringExtra(EXTRA_USER_AVATAR);
			this.toUserNickname = getIntent().getStringExtra(EXTRA_USER_NICKNAME);
			if (!TextUtils.isEmpty(this.toUserNickname)) {
				((TextView) findViewById(R.id.contacts_header_title)).setText(this.toUserNickname);
			}
            loadUserInfo(this.toChatUsername);
		} else {
			// 群聊
//			findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
//			findViewById(R.id.container_remove).setVisibility(View.GONE);
			findViewById(R.id.container_voice_call).setVisibility(View.GONE);
			findViewById(R.id.container_video_call).setVisibility(View.GONE);
			toChatUsername = getIntent().getStringExtra(EXTRA_GROUP_ID);

			if(chatType == CHATTYPE_GROUP){
			    onGroupViewCreation();
			}else{ 
			    onChatRoomViewCreation();
			}
			groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
			if (!TextUtils.isEmpty(groupName)) {
				((TextView) findViewById(R.id.contacts_header_title)).setText(groupName);
			}
			loadGroupInfo(this.toChatUsername);
		}
		if (this.toUserNickname == null) {
			this.toUserNickname = "";
		}
		if (this.toUserAvatar == null) {
			this.toUserAvatar = "";
		}

		// for chatroom type, we only init conversation and create view adapter on success
		if(chatType != CHATTYPE_CHATROOM){
		    onConversationInit();
	        
	        onListViewCreation();
	        
	        // show forward message if the message is not null
	        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
	        if (forward_msg_id != null) {
	            // 显示发送要转发的消息
	            forwardMessage(forward_msg_id);
	        }
		}

		isChatForbidden = getIntent().getBooleanExtra(EXTRA_IS_CHAT_FORBIDDEN, false);
		initBottomBar();
		initChatSettingsBtn();
	}

	protected void onConversationInit(){
	    if(chatType == CHATTYPE_SINGLE){
			conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
					EMConversation.EMConversationType.Chat,true);
	    }else if(chatType == CHATTYPE_GROUP){
	        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
					EMConversation.EMConversationType.GroupChat,true);
	    }else if(chatType == CHATTYPE_CHATROOM){
	        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
					EMConversation.EMConversationType.ChatRoom,true);
	    }
	     
        // 把此会话的未读数置为0
		if(conversation!=null){
			conversation.markAllMessagesAsRead();
			// 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
			// 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
			final List<EMMessage> msgs = conversation.getAllMessages();
			int msgCount = msgs != null ? msgs.size() : 0;
			if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
				String msgId = null;
				if (msgs != null && msgs.size() > 0) {
					msgId = msgs.get(0).getMsgId();
				}
				conversation.loadMoreMsgFromDB(msgId, pagesize);
//            if (chatType == CHATTYPE_SINGLE) {
//                conversation.loadMoreMsgFromDB(msgId, pagesize);
//            } else {
//                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
//            }
			}
		}


        
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(new EMChatRoomChangeListener() {

			@Override
			public void onChatRoomDestroyed(String roomId, String roomName) {
				if (roomId.equals(toChatUsername)) {
					finish();
				}
			}

			@Override
			public void onMemberJoined(String roomId, String participant) {
			}

			@Override
			public void onMemberExited(String roomId, String roomName,
									   String participant) {

			}

			@Override
			public void onRemovedFromChatRoom(String roomId, String roomName,
											  String participant) {
				if (roomId.equals(toChatUsername)) {
					String curUser = EMClient.getInstance().getCurrentUser();
					if (curUser.equals(participant)) {
						EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
						finish();
					}
				}
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

		});
	}
	
	protected void onListViewCreation(){
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType);
		adapter.setIsChatForbidden(getIntent().getExtras().getBoolean(EXTRA_IS_CHAT_FORBIDDEN));
        // 显示消息
        listView.setAdapter(adapter);
        
        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				return false;
			}
		});
	}
	
	protected void onGroupViewCreation(){
	    group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        
//        if (group != null){
//            ((TextView) findViewById(R.id.contacts_header_title)).setText(group.getGroupName());
//        }else{
//            ((TextView) findViewById(R.id.contacts_header_title)).setText(toChatUsername);
//        }
        ((TextView) findViewById(R.id.contacts_header_title)).setText(getIntent().getStringExtra(EXTRA_GROUP_NAME));

        // 监听当前会话的群聊解散被T事件
        groupListener = new GroupListener();
		EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
	}
	
	protected void onChatRoomViewCreation(){
//        findViewById(R.id.container_to_group).setVisibility(View.GONE);
        
        final ProgressDialog pd = ProgressDialog.show(this, "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new
				EMValueCallBack<EMChatRoom>() {

			@Override
			public void onSuccess(EMChatRoom value) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						room =  EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
//                        if(room !=null){
//                            ((TextView) findViewById(R.id.contacts_header_title)).setText(room.getName());
//                        }else{
//                            ((TextView) findViewById(R.id.contacts_header_title)).setText(toChatUsername);
//                        }
						((TextView) findViewById(R.id.contacts_header_title)).setText(toUserNickname);
						EMLog.d(TAG, "join room success : " + room.getName());

						onConversationInit();

						onListViewCreation();
					}
				});
			}

			@Override
			public void onError(final int error, String errorMsg) {
				// TODO Auto-generated method stub
				EMLog.d(TAG, "join room failure : " + error);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
					}
				});
				finish();
			}
		});
	}
	
	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		checkChanges(requestCode, resultCode, data);

		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if(requestCode==REQUEST_CODE_UPDATE_FRIEND_NICKNAME&&resultCode==RESULT_OK){
			groupName=data.getStringExtra(FriendDetailsFragment.Constants.EXTRA_FRIEND_REMARK);
			if (!TextUtils.isEmpty(groupName)) {
				((TextView) findViewById(R.id.contacts_header_title)).setText(groupName);
			}
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
				// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
				// ((TextMessageBody) copyMsg.getBody()).getMessage()));
				clipboard.setText(((EMTextMessageBody) copyMsg.getBody()).getMessage());
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				conversation.removeMessage(deleteMsg.getMsgId());
				adapter.refreshSeekTo(data.getIntExtra("position", adapter.getCount()) - 1);
				break;

			case RESULT_CODE_FORWARD: // 转发消息
				EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
				Intent intent = new Intent(this, ForwardMessageActivity.class);
				intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
				startActivity(intent);
				
				break;


			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMClient.getInstance().chatManager().deleteConversation(toChatUsername,true);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
//					Uri selectedImage = data.getData();
//					if (selectedImage != null) {
//						sendPicByUri(selectedImage);
//					}

					String photo_path = null;

					photo_path = PhotoUtils.getImageAbsolutePath(ChatActivity.this, data
								.getData());

					if (TextUtils.isEmpty(photo_path)) {
						return;
					}
					sendPicture(photo_path);
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					more(more);
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
					String st = getResources().getString(R.string.unable_to_get_loaction);
					Toast.makeText(this, st, Toast.LENGTH_SHORT).show();
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
					|| requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
					|| requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
				EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
				resendMessage(forwardMsg);
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				addUserToBlacklist(deleteMsg.getFrom());
			} else if (conversation.getAllMessages().size() > 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				adapter.refresh();
			}
		}

		//删除好友
		if (data == null){
			if(requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE){
				if (PersonalSpaceFragment.hasUnbindFriendRelationship()){
					PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
					//设置标志位
					setHasContentChanged(true);
					finish();
				}

				//备注名修改
				if (PersonalSpaceFragment.hasRemarkNameChanged()){
					PersonalSpaceFragment.setHasRemarkNameChanged(false);
					//设置标志位
					setHasContentChanged(true);
				}
			}
		}
	}

	private void initBottomBar() {
		if (isChatForbidden) {
			bottomBar = findViewById(R.id.rl_bottom);
			bottomBar.setVisibility(View.GONE);

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ChatActivity.this, R.string.chat_forbidden_alert,
							Toast.LENGTH_LONG).show();
				}
			}, 1000);
		}
	}

	private void initChatSettingsBtn() {
		ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
		int fromWhere = getIntent().getExtras().getInt(EXTRA_FROM_WHERE);
		if (fromWhere != 0) {
			imageView.setVisibility(View.GONE);
			return;
		}
		if (imageView != null) {
			imageView.setVisibility(View.VISIBLE);
			if (chatType == CHATTYPE_SINGLE) {
				imageView.setImageResource(R.drawable.chat_single);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
//						enterPersonInfo();
                        enterPersonalSpace(ChatActivity.this,userBasicInfo.getMemberId());
					}
				});
			} else {
				imageView.setImageResource(R.drawable.chat_group);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
//						enterGroupMembers();
						enterClassDetailsActivity();
					}
				});
			}
			if (fromWhere == FROM_NOTIFICATION) {
				imageView.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void enterClassDetailsActivity(){
		Bundle params = getIntent().getExtras();
		String classId = null;
		String schoolName = null;
		String schoolId = null;
		String contactId = null;
		String groupId = null;
		int fromType = -1;
		boolean hasJoin = false;
		if (params != null){
			classId = params.getString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID);
			if (classId != null){

				fromType = ClassDetailsFragment.Constants.FROM_TYPE_CLASS_HEAD_PIC;
			}else {
				//老师通讯录
				fromType = ClassDetailsFragment.Constants.FROM_TYPE_TEACHER_CONTACT;
			}
			hasJoin = true;
			schoolName = params.getString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME);
			contactId = params.getString("contactId");
			schoolId = params.getString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID);
			groupId = params.getString("groupId");
		}
		ActivityUtils.enterClassDetialActivity(ChatActivity.this,classId,fromType,hasJoin,schoolName
				,contactId,schoolId,groupId);
	}

	public static void setHasContentChanged(boolean hasContentChanged){
		ChatActivity.hasContentChanged = hasContentChanged;
	}

	public static boolean hasContentChanged(){
		return hasContentChanged;
	}

	/**
	 * 进入个人名片
	 * @param activity
	 * @param memberId
	 */
	public void enterPersonalSpace(Activity activity,String memberId) {
		if (TextUtils.isEmpty(memberId)){
			return;
		}
		Bundle args = new Bundle();
		args.putString(PersonalSpaceActivity.EXTRA_USER_ID, memberId);
		args.putInt(EXTRA_FROM_WHERE,FROM_CHAT);
		Intent intent = new Intent(activity, PersonalSpaceActivity.class);
		intent.putExtras(args);
		//个人页面
		activity.startActivityForResult(intent,PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE);
	}

	void enterPersonInfo() {
		Bundle params = getIntent().getExtras();
		Bundle args = new Bundle();
		if (params.getBoolean(EXTRA_IS_FRIEND)
				|| (userBasicInfo != null && userBasicInfo.isWhetherFriends())) {
			args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_TYPE,
					ContactsMemberDetailsActivity.MEMBER_TYPE_FRIEND);
			if (args.containsKey(EXTRA_CONTACT_ID)) {
				args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_ID,
						params.getString(EXTRA_CONTACT_ID));
			} else {
				args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_ID,
						userBasicInfo.getFriendId());
			}
			if (args.containsKey(EXTRA_USER_AVATAR)) {
				args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_ICON,
						params.getString(EXTRA_USER_AVATAR));
			} else {
				args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_ICON,
						userBasicInfo.getHeaderPic());
			}
			args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_NAME, "");
			if (args.containsKey(EXTRA_USER_NICKNAME)) {
				args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_NICKNAME,
						params.getString(EXTRA_USER_NICKNAME));
			} else {
				args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_NICKNAME,
						userBasicInfo.getNickName());
			}
			args.putBoolean(FriendDetailsFragment.Constants.EXTRA_FROM_CHAT, true);
		} else {
			args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_TYPE,
					ContactsMemberDetailsActivity.MEMBER_TYPE_PERSON);
			args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_ID,
					params.getString(EXTRA_MEMBER_ID));
			args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_ICON,
					params.getString(EXTRA_USER_AVATAR));
			args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_NAME, "");
			args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_NICKNAME,
					params.getString(EXTRA_USER_NICKNAME));
			args.putBoolean(PersonInfoFragment.Constants.EXTRA_IS_FRIEND,
					params.getBoolean(EXTRA_IS_FRIEND));
		}
		if (args != null) {
			Intent intent = new Intent(this, ContactsMemberDetailsActivity.class);
			intent.putExtras(args);
			try {
				startActivityForResult(intent,REQUEST_CODE_UPDATE_FRIEND_NICKNAME);
				//startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void enterGroupMembers() {
		Bundle params = getIntent().getExtras();
		Bundle args = null;
		int contactsType = params.getInt(EXTRA_CONTACT_TYPE);
		if (contactsType == ContactsActivity.CONTACTS_TYPE_CLASS
				|| contactsType == ContactsActivity.CONTACTS_TYPE_SCHOOL) {
			args = new Bundle();
			args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, contactsType);
			args.putString(ContactsActivity.EXTRA_CONTACTS_ID,
					params.getString(EXTRA_CONTACT_ID));
			args.putString(ContactsActivity.EXTRA_CONTACTS_NAME,
					params.getString(EXTRA_GROUP_NAME));
			args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID,
					params.getString(EXTRA_SCHOOL_ID));
			args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME,
					params.getString(EXTRA_SCHOOL_NAME));
			args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID,
					params.getString(EXTRA_CLASS_ID));
			args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME,
					params.getString(EXTRA_GROUP_NAME));
			args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID,
					params.getString(EXTRA_GROUP_ID));
			args.putBoolean(ContactsActivity.EXTRA_CONTACTS_FROM_CHAT, true);
		}
		if (args != null) {
			Intent intent = new Intent(this, ContactsActivity.class);
			intent.putExtras(args);
            if (contactsType == ContactsActivity.CONTACTS_TYPE_CLASS) {
				startActivityForResult(intent,
						ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS);
			} else {
				startActivity(intent);
			}
		}
	}

	/**
	 * 消息图标点击事件
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		String st1 = getResources().getString(R.string.not_connect_to_server);
		int id = view.getId();
		if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
			String s = mEditTextContent.getText().toString();
			sendText(s);
		} else if (id == R.id.btn_take_picture) {
			selectPicFromCamera();// 点击照相图标
		} else if (id == R.id.btn_picture) {
			selectPicFromLocal(); // 点击图片图标
		} else if (id == R.id.btn_location) { // 位置
//			startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
		} else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
			more.setVisibility(View.VISIBLE);
			iv_emoticons_normal.setVisibility(View.INVISIBLE);
			iv_emoticons_checked.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.GONE);
			emojiIconContainer.setVisibility(View.VISIBLE);
			hideKeyboard();
		} else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);

		} else if (id == R.id.btn_video) {
			// 点击摄像图标
			Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
		} else if (id == R.id.btn_file) { // 点击文件图标
			selectFileFromLocal();
		} else if (id == R.id.btn_voice_call) { // 点击语音电话图标
			if (!EMClient.getInstance().isConnected())
				Toast.makeText(this, st1, Toast.LENGTH_SHORT).show();
			else
				startActivity(new Intent(ChatActivity.this, VoiceCallActivity.class).putExtra("username",
						toChatUsername).putExtra("isComingCall", false));
		} else if (id == R.id.btn_video_call) { // 视频通话
			if (!EMClient.getInstance().isConnected())
				Toast.makeText(this, st1,Toast.LENGTH_SHORT).show();
			else
				startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername).putExtra(
						"isComingCall", false));
		} else if (id == R.id.btn_set_mode_voice) {
			setModeVoice(view);
		} else if (id == R.id.btn_set_mode_keyboard) {
			setModeKeyboard(view);
		} else if (id == R.id.et_sendmessage) {
			editClick(view);
		} else if (id == R.id.btn_more) {
			more(view);
		} else if (id == R.id.contacts_header_left_btn) {
			back(view);
		}
	}



	private void refreshUIWithNewMessage(){
	    if(adapter == null){
	        return;
	    }

	    runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
            }
        });
	}

	private void refreshUI() {
	    if(adapter == null){
            return;
        }

		runOnUiThread(new Runnable() {
			public void run() {
				adapter.refresh();
			}
		});
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()
				+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();

		PhotoUtils.startTakePhoto(ChatActivity.this,cameraFile,REQUEST_CODE_CAMERA);
	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			}

		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 发送文本消息
	 *
	 * @param content
	 *            message content
	 *            boolean resend
	 */
	private void sendText(String content) {

		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP){
			    message.setChatType(EMMessage.ChatType.GroupChat);
			}else if(chatType == CHATTYPE_CHATROOM){
			    message.setChatType(EMMessage.ChatType.ChatRoom);
			}

			EMTextMessageBody txtBody = new EMTextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setTo(toChatUsername);
			message.setAttribute(EXTRA_USER_AVATAR, this.myUserAvatar);
			message.setAttribute(EXTRA_USER_NICKNAME, this.myUserNickname);
			message.setAttribute(EXTRA_TO_USER_AVATAR, this.toUserAvatar);
			message.setAttribute(EXTRA_TO_USER_NICKNAME, this.toUserNickname);
			if (!TextUtils.isEmpty(topic)) {
				message.setAttribute(EXTRA_TOPIC, this.topic);
			}
			// 把messgage加到conversation中
			conversation.appendMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refreshSelectLast();
			mEditTextContent.setText("");
			//成功标志位
			setHasContentChanged(true);

			setResult(RESULT_OK);

		}
	}

	/**
	 * 发送语音
	 *
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP){
				message.setChatType(EMMessage.ChatType.GroupChat);
				}else if(chatType == CHATTYPE_CHATROOM){
				    message.setChatType(EMMessage.ChatType.ChatRoom);
				}
			message.setTo(toChatUsername);
			message.setAttribute(EXTRA_USER_AVATAR, this.myUserAvatar);
			message.setAttribute(EXTRA_USER_NICKNAME, this.myUserNickname);
			message.setAttribute(EXTRA_TO_USER_AVATAR, this.toUserAvatar);
			message.setAttribute(EXTRA_TO_USER_NICKNAME, this.toUserNickname);
			if (!TextUtils.isEmpty(topic)) {
				message.setAttribute(EXTRA_TOPIC, this.topic);
			}
			int len = Integer.parseInt(length);
			EMVoiceMessageBody body = new EMVoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			conversation.appendMessage(message);
			adapter.refreshSelectLast();
			//成功标志位
			setHasContentChanged(true);
			setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 *
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUsername;
		// create and add image message in view
		//imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
		final EMMessage message = EMMessage.createImageSendMessage(filePath, false, toChatUsername);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(EMMessage.ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(EMMessage.ChatType.ChatRoom);
		}

		message.setTo(to);
		message.setAttribute(EXTRA_USER_AVATAR, this.myUserAvatar);
		message.setAttribute(EXTRA_USER_NICKNAME, this.myUserNickname);
		message.setAttribute(EXTRA_TO_USER_AVATAR, this.toUserAvatar);
		message.setAttribute(EXTRA_TO_USER_NICKNAME, this.toUserNickname);
		if (!TextUtils.isEmpty(topic)) {
			message.setAttribute(EXTRA_TOPIC, this.topic);
		}
//		EMImageMessageBody body = new EMImageMessageBody(new File(filePath));
//		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
//		// body.setSendOriginalImage(true);
//		message.addBody(body);


		conversation.appendMessage(message);

		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		//成功标志位
		setHasContentChanged(true);
		setResult(RESULT_OK);
		// more(more);
	}

	/**
	 * 发送视频消息
	 */
	private void sendVideo(final String filePath, final String thumbPath, final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP){
				message.setChatType(EMMessage.ChatType.GroupChat);
			}else if(chatType == CHATTYPE_CHATROOM){
			    message.setChatType(EMMessage.ChatType.ChatRoom);
			}
			String to = toChatUsername;
			message.setTo(to);
			message.setAttribute(EXTRA_USER_AVATAR, this.myUserAvatar);
			message.setAttribute(EXTRA_USER_NICKNAME, this.myUserNickname);
			message.setAttribute(EXTRA_TO_USER_AVATAR, this.toUserAvatar);
			message.setAttribute(EXTRA_TO_USER_NICKNAME, this.toUserNickname);
			if (!TextUtils.isEmpty(topic)) {
				message.setAttribute(EXTRA_TOPIC, this.topic);
			}
			EMVideoMessageBody body = new EMVideoMessageBody(filePath, thumbPath, length, videoFile
					.length());
			message.addBody(body);
			conversation.appendMessage(message);
			listView.setAdapter(adapter);
			adapter.refreshSelectLast();
			//成功标志位
			setHasContentChanged(true);
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据图库图片uri发送图片
	 *
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * 发送位置信息
	 *
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(EMMessage.ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(EMMessage.ChatType.ChatRoom);
		}
		EMLocationMessageBody locBody = new EMLocationMessageBody(locationAddress, latitude,
				longitude);
		message.addBody(locBody);
		message.setTo(toChatUsername);
		message.setAttribute(EXTRA_USER_AVATAR, this.myUserAvatar);
		message.setAttribute(EXTRA_USER_NICKNAME, this.myUserNickname);
		message.setAttribute(EXTRA_TO_USER_AVATAR, this.toUserAvatar);
		message.setAttribute(EXTRA_TO_USER_NICKNAME, this.toUserNickname);
		if (!TextUtils.isEmpty(topic)) {
			message.setAttribute(EXTRA_TOPIC, this.topic);
		}
		conversation.appendMessage(message);
		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		//成功标志位
		setHasContentChanged(true);
		setResult(RESULT_OK);

	}

	/**
	 * 发送文件
	 *
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			String st7 = getResources().getString(R.string.File_does_not_exist);
			Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			String st6 = getResources().getString(R.string.The_file_is_not_greater_than_10_m);
			Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(EMMessage.ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(EMMessage.ChatType.ChatRoom);
		}

		message.setTo(toChatUsername);
		message.setAttribute(EXTRA_USER_AVATAR, this.myUserAvatar);
		message.setAttribute(EXTRA_USER_NICKNAME, this.myUserNickname);
		if (!TextUtils.isEmpty(topic)) {
			message.setAttribute(EXTRA_TOPIC, this.topic);
		}
		// add message body
		EMNormalFileMessageBody body = new EMNormalFileMessageBody(new File(filePath));
		message.addBody(body);
		conversation.appendMessage(message);
		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		//成功标志位
		setHasContentChanged(true);
		setResult(RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage(EMMessage msg) {
		msg = conversation.getMessage(msg.getMsgId(),false);
		// msg.setBackSend(true);
		msg.setStatus(EMMessage.Status.CREATE);
		EMClient.getInstance().chatManager().sendMessage(msg);
		adapter.refreshSeekTo(resendPos);

	}

	/**
	 * 显示语音图标按钮
	 *
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}


	/**
	 * 显示键盘图标
	 *
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 点击清空聊天记录
	 *
	 * @param view
	 */
	public void emptyHistory(View view) {
		String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
		startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
				.putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 点击进入群组详情
	 *
	 * @param view
	 */
	public void toGroupDetails(View view) {
		if (room == null && group == null) {
			Toast.makeText(getApplicationContext(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
			return;
		}
		if(chatType == CHATTYPE_GROUP){
			startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
					REQUEST_CODE_GROUP_DETAIL);
//		}else{
//			startActivityForResult((new Intent(this, ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername)),
//					REQUEST_CODE_GROUP_DETAIL);
		}
	}

	/**
	 * 显示或隐藏图标按钮页
	 *
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			EMLog.d(TAG, "more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * 点击文字输入框
	 *
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	private PowerManager.WakeLock wakeLock;

	/**
	 * 按住说话listener
	 *
	 */
	class PressToSpeakListen implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
					Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
									Integer.toString(length), false);
						} else if (length == 0) {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * 获取表情的gridview的子view
	 *
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.galaxyschool.app.wawaschool.chat.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this,
									(String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											mEditTextContent.getEditableText().delete(i, selectionStart);
										else
											mEditTextContent.getEditableText().delete(selectionStart - 1,
													selectionStart);
									} else {
										mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityInstance = null;
		if(groupListener != null){
		    EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
		}
		if (receiver != null){
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (group != null)
			((TextView) findViewById(R.id.contacts_header_title)).setText(group.getGroupName());

		 if(adapter != null){
		     adapter.refresh();
	     }

		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		sdkHelper.pushActivity(this);
		// register the event listener when enter the foreground
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	@Override
	protected void onStop() {
		// unregister this event listener when this activity enters the
		// background
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);

		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();

		// 把此activity 从foreground activity 列表里移除
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 加入到黑名单
	 *
	 * @param username
	 */
	private void addUserToBlacklist(final String username) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.Is_moved_into_blacklist));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().contactManager().addUserToBlackList(username, false);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), R.string
									.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
						}
					});
				} catch (HyphenateException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 返回
	 *
	 * @param view
	 */
	public void back(View view) {
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		if(chatType == CHATTYPE_CHATROOM){
			EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
		}
		notifyChanges();
		finish();
	}

	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		} else {
			notifyChanges();
			super.onBackPressed();
			if(chatType == CHATTYPE_CHATROOM){
				EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
			}
		}
	}

	/**
	 * listview滑动监听listener
	 *
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
					isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					EMMessage firstMsg = conversation.getAllMessages().get(0);
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
						else
							messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						if (messages.size() > 0) {
							adapter.refreshSeekTo(messages.size() - 1);
						}

						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}

	}

	/**
	 * 转发消息
	 *
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage
				(forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
			sendText(content);
			break;
		case IMAGE:
			// 发送图片
			String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// 不存在大图发送缩略图
					filePath = ImageUtils.getThumbnailImagePath(filePath);
				}
				sendPicture(filePath);
			}
			break;
		default:
			break;
		}

		if(forward_msg.getChatType() == EMMessage.ChatType.ChatRoom){
			EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
		}
	}
	
	/**
	 * 监测群组解散或者被T事件
	 * 
	 */
	class GroupListener implements EMGroupChangeListener {

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			runOnUiThread(new Runnable() {
//				String st13 = getResources().getString(R.string.you_are_group);

				public void run() {
					if (toChatUsername.equals(groupId)) {
//						Toast.makeText(ChatActivity.this, st13, 1).show();
						if (GroupDetailsActivity.instance != null)
							GroupDetailsActivity.instance.finish();
						finish();
					}
				}
			});
		}



		@Override
		public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

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
		public void onMemberJoined(String s, String s1) {

		}

		@Override
		public void onMemberExited(String s, String s1) {

		}

		@Override
		public void onAnnouncementChanged(String s, String s1) {

		}

		@Override
		public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

		}

		@Override
		public void onSharedFileDeleted(String s, String s1) {

		}

		@Override
		public void onGroupDestroyed(final String groupId, String groupName) {
			// 群组解散正好在此页面，提示群组被解散，并finish此页面
			runOnUiThread(new Runnable() {
//				String st14 = getResources().getString(R.string.the_current_group);

				public void run() {
					if (toChatUsername.equals(groupId)) {
//						Toast.makeText(ChatActivity.this, st14, 1).show();
						if (GroupDetailsActivity.instance != null)
							GroupDetailsActivity.instance.finish();
						finish();
					}
				}
			});
		}

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                String inviter, String reason) {
        }

		@Override
		public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {

		}

		@Override
		public void onRequestToJoinAccepted(String s, String s1, String s2) {

		}

		@Override
		public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {

		}

		@Override
		public void onInvitationAccepted(String s, String s1, String s2) {

		}

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                String reason) {
        }

	}

	public String getToChatUsername() {
		return toChatUsername;
	}

	public ListView getListView() {
		return listView;
	}

	private void loadUserInfo(String userId) {
		if (((MyApplication) this.getApplication()).getUserInfo() == null){
			return;
		}
		Map<String, Object> params = new HashMap();
		params.put("MemberId", userId.startsWith("hx") ?
				userId.substring(2) : userId);
		params.put("CurrentUserId",
				((MyApplication) this.getApplication()).getUserInfo().getMemberId());
		params.put("VersionCode", 1);
		ConversationHelper.DefaultListener listener =
				new ConversationHelper.DefaultListener(this) {
            @Override
            public void onSuccess(String jsonString) {
                if (isFinishing()) {
                    return;
                }
                super.onSuccess(jsonString);
                ConversationInfoListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                ConversationInfoList infoList = result.getModel();
                if (infoList != null && infoList.getMemberList() != null
                        && infoList.getMemberList().size() > 0) {
                    UserBasicInfo info = infoList.getMemberList().get(0);
                    ((TextView) findViewById(R.id.contacts_header_title))
                            .setText(info.getRealName());
					userBasicInfo = info;
                }
            }
        };
		ConversationHelper.postRequest(this,
				ServerUrl.GET_CONVERSATION_INFO_LIST_URL, params, listener);
	}

	private void loadGroupInfo(String groupId) {
		if (((MyApplication) this.getApplication()).getUserInfo() == null){
			return;
		}
		Map<String, Object> params = new HashMap();
		params.put("GroupId", groupId);
		params.put("MemberId", new ArrayList<String>());
		params.put("CurrentUserId",
				((MyApplication) this.getApplication()).getUserInfo().getMemberId());
		params.put("VersionCode", 1);
		params.put("IsShowAllGroup", true);
		ConversationHelper.DefaultListener listener =
				new ConversationHelper.DefaultListener(this) {
            @Override
            public void onSuccess(String jsonString) {
                if (isFinishing()) {
                    return;
                }
                super.onSuccess(jsonString);
                ConversationInfoListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                ConversationInfoList infoList = result.getModel();
                if (infoList != null && infoList.getGroupList() != null
                        && infoList.getGroupList().size() > 0) {
                    GroupBasicInfo info = infoList.getGroupList().get(0);
					if (info != null) {
						groupName = info.getGruopName();
						((TextView) findViewById(R.id.contacts_header_title))
								.setText(info.getGruopName());
						groupBasicInfo = info;
						isChatForbidden = info.isChatForbidden();
					}
					initBottomBar();
                }
            }
        };
		ConversationHelper.postRequest(this,
				ServerUrl.GET_CONVERSATION_INFO_LIST_URL, params, listener);
	}

	private void notifyChanges() {
		if (chatType != CHATTYPE_GROUP) {
			return;
		}
		Bundle args = getIntent().getExtras();
		boolean classNameChanged = args != null
				&& args.containsKey(EXTRA_GROUP_NAME)
				&& !args.getString(EXTRA_GROUP_NAME).equals(groupName);
		if (classNameChanged) {
			Bundle data = new Bundle();
//			data.putString(EXTRA_CLASS_ID, args.getString(EXTRA_CLASS_ID));
			data.putBoolean(EXTRA_CLASS_NAME_CHANGED, classNameChanged);
            data.putString(EXTRA_CLASS_NAME, groupName);
			Intent intent = new Intent();
			intent.putExtras(data);
			setResult(Activity.RESULT_OK, intent);
		}
//		finish();
	}

	public void checkChanges(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK
				|| requestCode != ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS) {
			return;
		}
        boolean classDetailsChanged = data.getBooleanExtra(
                ClassContactsDetailsFragment.Constants.EXTRA_CLASS_DETAILS_CHANGED, false);
        if (!classDetailsChanged) {
            return;
        }
//        Bundle args = getIntent().getExtras();
//        String classId = data.getStringExtra(
//                ClassContactsDetailsFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
//        if (TextUtils.isEmpty(classId)
//                || !args.getString(EXTRA_CLASS_ID).equals(classId)) {
//            return;
//        }
        boolean classNameChanged = data.getBooleanExtra(
                ClassContactsDetailsFragment.Constants.EXTRA_CLASS_NAME_CHANGED, false);
        if (classNameChanged) {
			groupName = data.getStringExtra(
					ClassContactsDetailsFragment.Constants.EXTRA_CONTACTS_CLASS_NAME);
            TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
            if (textView != null) {
                textView.setText(groupName);
            }
		}
	}

}
