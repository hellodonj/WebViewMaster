package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.*;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.SlideListViewHelper;
import com.lqwawa.lqbaselib.net.NetResultListener;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MyMessage;
import com.galaxyschool.app.wawaschool.pojo.MyMessageListResult;
import com.galaxyschool.app.wawaschool.pojo.PushMessage;
import com.galaxyschool.app.wawaschool.pojo.PushMessageListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyMessageListFragment extends ContactsConversationListFragment {

    public static final String TAG = MyMessageListFragment.class.getSimpleName();

    private List allDatas = new ArrayList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_message_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.personal_message);
        }

        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setText(R.string.start_chat);
            textView.setTextColor(getResources().getColor(R.color.text_green));
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(this);
        }
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);


//        ImageView imageView = null;
//        View view = findViewById(R.id.course_item_layout);
//        if (view != null) {
//            imageView = (ImageView) view.findViewById(R.id.item_icon);
//            if (imageView != null) {
//                imageView.setImageResource(R.drawable.pub_course_ico);
//            }
//            textView = (TextView) view.findViewById(R.id.item_title);
//            if (textView != null) {
//                textView.setText(R.string.courses);
//            }
//            view.setOnClickListener(this);
//        }
//        view = findViewById(R.id.homework_item_layout);
//        if (view != null) {
//            imageView = (ImageView) view.findViewById(R.id.item_icon);
//            if (imageView != null) {
//                imageView.setImageResource(R.drawable.pub_homework_ico);
//            }
//            textView = (TextView) view.findViewById(R.id.item_title);
//            if (textView != null) {
//                textView.setText(R.string.homeworks);
//            }
//            view.setOnClickListener(this);
//        }
//        view = findViewById(R.id.notice_item_layout);
//        if (view != null) {
//            imageView = (ImageView) view.findViewById(R.id.item_icon);
//            if (imageView != null) {
//                imageView.setImageResource(R.drawable.pub_notice_ico);
//            }
//            textView = (TextView) view.findViewById(R.id.item_title);
//            if (textView != null) {
//                textView.setText(R.string.notices);
//            }
//            view.setOnClickListener(this);
//        }
//        view = findViewById(R.id.comment_item_layout);
//        if (view != null) {
//            imageView = (ImageView) view.findViewById(R.id.item_icon);
//            if (imageView != null) {
//                imageView.setImageResource(R.drawable.pub_comment_ico);
//            }
//            textView = (TextView) view.findViewById(R.id.item_title);
//            if (textView != null) {
//                textView.setText(R.string.shows);
//            }
//            view.setOnClickListener(this);
//        }

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
//            listView.setSlideMode(SlideListView.SlideMode.NONE);
//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    listView, R.layout.contacts_conversation_list_item) {
            SlideListViewHelper listViewHelper = new ConversationListViewHelper(
                    getActivity(), listView, R.layout.contacts_my_message_list_item) {
                @Override
                public void loadData() {
                    loadConversationList();
                }

                @Override
                public SlideListView.SlideMode getSlideModeInPosition(int position) {
                    Object obj = getDataAdapter().getItem(position);
                    if (obj instanceof MyMessage) {
                        return SlideListView.SlideMode.NONE;
                    }
                    return super.getSlideModeInPosition(position);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    Object obj = getDataAdapter().getItem(position);
                    if (!(obj instanceof MyMessage)) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.item_icon_circle);
                        if (imageView != null) {
                            imageView.setVisibility(View.GONE);
                        }
                        return view;
                    }
                    final MyMessage data = (MyMessage) obj;
                    ImageView imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_icon_circle);
                    if (imageView != null) {
                        imageView.setVisibility(View.VISIBLE);
                        int defaultIcon = R.drawable.ic_launcher;
                        if (data.isClassMessage()) {
                            defaultIcon = R.drawable.default_class_icon;
                            //班级消息，点击头像进入班级二维码界面。

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    enterGroupQrCode(data);
                                }
                            });

                        } else if (data.isPlatformNewsMessage()) {
                            defaultIcon = R.drawable.wawatong_news_ico;
                        } else if (data.isPlatformNoticeMessage()) {
                            defaultIcon = R.drawable.wawatong_notice_ico;
                        }
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView, defaultIcon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                    }
                    textView = (TextView) view.findViewById(R.id.item_tips);
                    if (textView != null) {
                        int newCount = data.getUnReadNumber();
                        String str = String.valueOf(newCount);
                        if (newCount > 99) {
                            str = "99+";
                        }
                        if (newCount > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(str);
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    textView = (TextView) view.findViewById(R.id.item_time);
                    if (textView != null) {
                        textView.setText(df.format(new Date(data.getTimestamp())));
                    }
                    textView = (TextView) view.findViewById(R.id.item_description);
                    if (textView != null) {
                        if (data.isClassMessage()) {
                            textView.setText(getString(R.string.class_message_tips,
                                    MyMessage.getClassMessageTypeString(getActivity(), data.getSubType()))
                                            + data.getSubTitle());
                        } else {
                            textView.setText(data.getSubTitle());
                        }
                    }
                    textView = (TextView) view.findViewById(R.id.item_icon_name);
                    if (textView != null) {
                        if (data.isClassMessage()) {
                            textView.setText(R.string.class_messages);
                            textView.setVisibility(View.VISIBLE);
                        } else if (data.isPlatformNewsMessage()) {
                            textView.setText(R.string.platform_news);
                            textView.setVisibility(View.VISIBLE);
                        } else if (data.isPlatformNoticeMessage()) {
                            textView.setText(R.string.platform_notice);
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Object obj = getDataAdapter().getItem(position);
                    if (!(obj instanceof MyMessage)) {
                        super.onItemClick(parent, view, position, id);
                        return;
                    }
                    enterMyMessage((MyMessage) obj);
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void enterGroupQrCode(MyMessage data) {

        Bundle args = new Bundle();
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                getString(R.string.class_qrcode));
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,data.getClassId());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,data.getSubTitle());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION,"测试学校");
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    private void sortData(List<Object> list) {
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                long lTime = 0, rTime = 0;
                if (lhs instanceof MyMessage) {
                    lTime = ((MyMessage) lhs).getTimestamp();
                } else if (lhs instanceof ConversationHelper.ConversationInfo) {
                    lTime = ((ConversationHelper.ConversationInfo) lhs).time;
                }
                if (rhs instanceof MyMessage) {
                    rTime = ((MyMessage) rhs).getTimestamp();
                } else if (rhs instanceof ConversationHelper.ConversationInfo) {
                    rTime = ((ConversationHelper.ConversationInfo) rhs).time;
                }
                if (lTime > rTime) {
                    return -1;
                } else if (lTime < rTime) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    private void loadMyMessageList() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<MyMessageListResult>(
                        MyMessageListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        MyMessageListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                        }else{
                            updateMyMessageListView(result);
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_MY_MESSAGE_LIST_URL, params, listener);
    }


    private void updateMyMessageListView(MyMessageListResult result) {
        List<MyMessage> messageList = result.getModel().getData();
        if (messageList == null || messageList.size() <= 0) {
        }else{
            allDatas.addAll(messageList);
            sortData(allDatas);
            getCurrAdapterViewHelper().setData(allDatas);
        }
    }

    @Override
    protected void loadConversationList() {
        conversationHelper.loadConversations(new NetResultListener() {
            @Override
            public void onSuccess(Object data) {
                if (getActivity() == null) {
                    return;
                }
                List<ConversationHelper.ConversationInfo> list =
                        (List<ConversationHelper.ConversationInfo>) data;
                if(list == null || list.size() == 0){
                    if (allDatas != null){
                        //先清空数据，再加载环信消息数据，最后加载接口数据。
                        allDatas.clear();
                        getCurrAdapterViewHelper().setData(allDatas);
                    }
                    return;
                }
                updateMyConversationListView(list);
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null) {
                    return;
                }
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                loadMyMessageList();
            }
        });
    }

    private void updateMyConversationListView(
            List<ConversationHelper.ConversationInfo> conversationList) {
        if (conversationList != null && conversationList.size() > 0) {
            if (allDatas != null){
                //先清空数据，再加载环信消息数据，最后加载接口数据。
                allDatas.clear();
                allDatas.addAll(conversationList);
                getCurrAdapterViewHelper().setData(allDatas);
            }
        }
    }

    private void loadNewMessageCount() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("VersionCode", 2);
        DefaultListener listener =
                new DefaultListener<PushMessageListResult>(
                        PushMessageListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                PushMessageListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_PUSH_MESSAGE_LIST_URL, params, listener);
    }

    private void updateViews(PushMessageListResult result) {
        List<PushMessage> list = result.getModel().getInformationList();
        if (list == null || list.size() <= 0) {
            return;
        }

        TextView textView = null;
        View view = null;
        for (PushMessage msg : list) {
            switch (msg.getType()) {
            case PushMessage.MESSAGE_TYPE_COURSE:
                view = findViewById(R.id.course_item_layout);
                break;
            case PushMessage.MESSAGE_TYPE_HOMEWORK:
                view = findViewById(R.id.homework_item_layout);
                break;
            case PushMessage.MESSAGE_TYPE_NOTICE:
                view = findViewById(R.id.notice_item_layout);
                break;
            case PushMessage.MESSAGE_TYPE_COMMENT:
                view = findViewById(R.id.comment_item_layout);
                break;
            case PushMessage.MESSAGE_TYPE_LECTURE:
                view = findViewById(R.id.lecture_item_layout);
                break;
            }
            if (view != null) {
                textView = (TextView) view.findViewById(R.id.item_tips);
                if (textView != null) {
                    if (msg.getNewCount() > 99) {
                        textView.setText("99+");
                    } else {
                        textView.setText(String.valueOf(msg.getNewCount()));
                    }
                    textView.setVisibility(msg.getNewCount() > 0 ?
                            View.VISIBLE : View.GONE);
                }
            }
            view = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            enterPersonalContacts();
        } else if (v.getId() == R.id.course_item_layout) {
            enterMyResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_COURSE);
        } else if (v.getId() == R.id.homework_item_layout) {
            enterMyResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK);
        } else if (v.getId() == R.id.notice_item_layout) {
            enterMyResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_NOTICE);
        } else if (v.getId() == R.id.comment_item_layout) {
            enterMyResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_SHOW);
        } else if (v.getId() == R.id.lecture_item_layout) {
            enterMyResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_LECTURE);
        } else {
            super.onClick(v);
        }
    }

    private void enterMyResourceByChannel(int channelType) {
        Bundle args = new Bundle();
        args.putInt(MyResourceListActivity.EXTRA_CHANNEL_TYPE, channelType);
        Intent intent = new Intent(getActivity(), MyResourceListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterMyMessage(MyMessage message) {
        if (message.isClassMessage()) {
            enterClassMessage(message);
        } else if (message.isPlatformMessage()) {
            enterPlatformMessage(message);
        }
    }

    private void enterClassMessage(MyMessage message) {
        Bundle args = new Bundle();
        args.putString(ClassSpaceActivity.EXTRA_CLASS_ID, message.getClassId());
        Intent intent = new Intent(getActivity(), ClassSpaceActivity.class);
        intent.putExtras(args);
        //班级空间
        startActivityForResult(intent,ClassSpaceFragment.Constants.REQUEST_CODE_CLASS_SPACE);
    }

    private void enterPlatformMessage(MyMessage message) {
        Bundle args = new Bundle();
        if (message.isPlatformNewsMessage()) {
            args.putInt(PlatformResourceListActivity.EXTRA_CHANNEL_TYPE,
                    PlatformResourceListActivity.CHANNEL_TYPE_NEWS);
        } else if (message.isPlatformNoticeMessage()) {
            args.putInt(PlatformResourceListActivity.EXTRA_CHANNEL_TYPE,
                    PlatformResourceListActivity.CHANNEL_TYPE_NOTICE);
        }
        Intent intent = new Intent(getActivity(), PlatformResourceListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterPersonalContacts() {
        Bundle args = new Bundle();
        args.putBoolean(PersonalContactsActivity.EXTRA_CHAT_WITH_FRIEND, true);
        Intent intent = new Intent(getActivity(), PersonalContactsActivity.class);
        intent.putExtras(args);
        //个人通讯录
        startActivityForResult(intent,PersonalContactsListFragment.
                REQUEST_CODE_PERSONAL_CONTACTS_LIST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
                if (requestCode == ClassSpaceFragment.Constants.REQUEST_CODE_CLASS_SPACE) {
                    if (ClassSpaceFragment.hasContentChanged()) {
                        ClassSpaceFragment.setHasContentChanged(false);
                        //刷新
                        refreshData();
                    }
                }else if (requestCode == ChatActivity.REQUEST_CODE_CHAT){
                    if (resultCode == Activity.RESULT_OK) {
                        //聊天环境改变
                        refreshData();
                    }
                }
        }else {
            if (requestCode == PersonalContactsListFragment.
                    REQUEST_CODE_PERSONAL_CONTACTS_LIST){
                //个人通讯录删除好友后需要刷新消息列表
                if (PersonalContactsListFragment.hasContentChanged()){
                    PersonalContactsListFragment.setHasContentChanged(false);
                    //刷新
                    refreshData();
                }

                //聊天页面辗转删除
                if (ChatActivity.hasContentChanged()){
                    ChatActivity.setHasContentChanged(false);
                    //刷新页面
                    refreshData();
                }
            }else if (requestCode == ChatActivity.REQUEST_CODE_CHAT){
                //聊天信息已读,暂时统一打开消息返回刷新。
                refreshData();
            }else if (requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE){
                //头像点击
                //删除好友
                if (PersonalSpaceFragment.hasUnbindFriendRelationship()){
                    PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
                    //刷新页面
                    refreshData();
                }

                //修改备注名
                if (PersonalSpaceFragment.hasRemarkNameChanged()){
                    PersonalSpaceFragment.setHasRemarkNameChanged(false);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }
}
