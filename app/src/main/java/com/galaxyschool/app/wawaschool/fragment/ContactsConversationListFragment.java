package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ClassDetailsActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.chat.utils.SmileUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.SlideListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.NetResultListener;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ContactsConversationListFragment extends ContactsListFragment {

    public static final String TAG = ContactsConversationListFragment.class.getSimpleName();

    protected ConversationHelper conversationHelper;
    private ConversationHelper.ConversationInfo conversationInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_conversation_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        refreshData();
    }

    protected void refreshData() {
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        conversationHelper = new ConversationHelper(getActivity());
        conversationHelper.registerConversationChangedListener(
                new ConversationHelper.ConversationChangedListener() {
            @Override
            public void onConversationChanged() {
                loadConversationList();
            }
        });
        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.conversation_list);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
//            listView.setSlideMode(SlideListView.SlideMode.NONE);
//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    listView, R.layout.contacts_conversation_list_item) {
            SlideListViewHelper listViewHelper = new ConversationListViewHelper(
                    getActivity(), listView);
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    protected void loadViews() {
            loadConversationList();
    }

    protected void loadConversationList() {
        conversationHelper.loadConversations(new NetResultListener() {
            @Override
            public void onSuccess(Object data) {
                if (getActivity() == null) {
                    return;
                }
                List<ConversationHelper.ConversationInfo> list =
                        (List<ConversationHelper.ConversationInfo>) data;

                getCurrAdapterViewHelper().setData(list);
            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
            }
        });
    }
    protected class ConversationListViewHelper extends SlideListViewHelper {
        public ConversationListViewHelper(Activity activity, SlideListView slideListView) {
            this(activity, slideListView, R.layout.contacts_conversation_list_item);
        }

        public ConversationListViewHelper(Activity activity, SlideListView slideListView,
                                          int itemViewLayout) {
            super(activity, slideListView, itemViewLayout,
                    0, R.layout.contacts_slide_list_item_delete);
        }

        @Override
        public void loadData() {
            loadConversationList();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Object obj = getDataAdapter().getItem(position);
            if (!(obj instanceof ConversationHelper.ConversationInfo)) {
                return view;
            }
            final ConversationHelper.ConversationInfo data = (ConversationHelper.ConversationInfo) obj;
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
            }
            holder.data = data;
            ImageView imageView = (ImageView) view.findViewById(R.id.item_icon);

            if (imageView != null) {

//                        UserUtils.setUserAvatar(getActivity(), data.getUserName(), imageView);
                if (TextUtils.isEmpty(data.icon)) {
                    if (data.conversation.getType() == EMConversation.EMConversationType.Chat) {
                        //单聊
                        imageView.setImageResource(R.drawable.default_avatar);
                    } else {
                        imageView.setImageResource(R.drawable.groups_icon);
                    }
                } else {
                    getThumbnailManager().displayThumbnail(AppSettings.getFileUrl(data.icon), imageView);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isSingle = false;
                        if (data.conversation.getType() == EMConversation.EMConversationType.Chat) {
                            //单聊
                            isSingle = true;
                        } else if (data.conversation.getType() == EMConversation.
                                EMConversationType.GroupChat){
                            isSingle = false;
                        }
                        //点击个人头像单聊进入详情页面
                        if (isSingle) {
                            enterPersonalSpace(data.memberId);
                        }else {
//                            //群聊进入班级二维码页面
//                          enterGroupQrCode(data);
                            //群聊进入班级名片页面
                            enterClassDetailsActivity(data);
                        }
                    }
                });
            }
            EMMessage lastMessage = null;
            if (data.conversation.getAllMessages().size() != 0) {
                // 把最后一条消息的内容作为item的message内容
                lastMessage = data.conversation.getLastMessage();
                TextView textView = (TextView) view.findViewById(R.id.item_title);
                if (textView != null) {
                    textView.setText(data.name);
                }
                textView = (TextView) view.findViewById(R.id.item_tips);
                if (textView != null) {
                    int newCount = data.conversation.getUnreadMsgCount();
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
                    textView.setText(df.format(new Date(lastMessage.getMsgTime())));
                }
                textView = (TextView) view.findViewById(R.id.item_description);
                if (textView != null) {
                    StringBuilder sb = new StringBuilder();
                    if (!TextUtils.isEmpty(data.lastMessageSenderName)) {
                        sb.append(data.lastMessageSenderName).append(": ");
                    }
                    sb.append(ConversationHelper.getMessageDigest(getActivity(), lastMessage));
                    textView.setText(SmileUtils.getSmiledText(getActivity(), sb.toString()),
                            TextView.BufferType.SPANNABLE);
                }
            }
            TextView textView = (TextView) view.findViewById(R.id.contacts_item_delete);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHolder holder = (ViewHolder) v.getTag();
                        if (holder == null) {
                            return;
                        }
                        ConversationHelper.ConversationInfo data =
                                (ConversationHelper.ConversationInfo) holder.data;
                        getCurrAdapterViewHelper().getData().remove(data);
                        getCurrAdapterViewHelper().update();
                        conversationHelper.deleteConversation(data);
                    }
                });
                textView.setTag(holder);
            }
            view.setTag(holder);
            return view;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Object obj = getDataAdapter().getItem(position);
            if (!(obj instanceof ConversationHelper.ConversationInfo)) {
                return;
            }
            ConversationHelper.ConversationInfo data = (ConversationHelper.ConversationInfo) obj;
            if (TextUtils.isEmpty(data.id)) {
                return;
            }
            Intent intent = ConversationHelper.getConversationIntent(getActivity(), data);
            if (intent != null) {
                data.conversation.markAllMessagesAsRead();
                startActivityForResult(intent, ChatActivity.REQUEST_CODE_CHAT);
                conversationInfo = data;
            }
        }
    }

    /**
     * 进入班级名片页面
     * @param data
     */
    private void enterClassDetailsActivity(ConversationHelper.ConversationInfo data) {
        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), ClassDetailsActivity.class);
        Bundle args = new Bundle();
        String classId = data.classId;
        int contactType = data.contactType;
        int fromType = 0;
        if (contactType == 0){
            fromType = ClassDetailsActivity.FROM_TYPE_CLASS_HEAD_PIC;
            //班级
            int classState = 1;//开课班
            args.putInt(ClassDetailsActivity.CLASS_STATE, classState);
        }else if (contactType == 1){
            //老师通讯录
            fromType = ClassDetailsActivity.FROM_TYPE_TEACHER_CONTACT;
        }
        boolean hasJoin = true;
        String schoolName = data.schoolName;
        String contactId = data.contactId;
        String schoolId = data.schoolId;
        String groupId = data.id;
        args.putString(ClassDetailsActivity.CLASS_ID, classId);
        args.putInt(ClassDetailsActivity.FROM_TYPE, fromType);
        args.putBoolean(ClassDetailsActivity.IS_JOIN_CLASS, hasJoin);
        args.putString(ClassDetailsActivity.SCHOOL_NAME, schoolName);
        args.putString(ClassDetailsActivity.CONTACT_ID, contactId);
        args.putString(ClassDetailsActivity.SCHOOL_ID, schoolId);
        args.putString(ClassDetailsActivity.GROUP_ID, groupId);

        intent.putExtras(args);
        //班级二维码
        startActivityForResult(intent, ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS);
    }

    public void enterPersonalSpace(String memberId) {
        if (TextUtils.isEmpty(memberId)){
            return;
        }
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, memberId);
        Intent intent = new Intent(getActivity(), PersonalSpaceActivity.class);
        intent.putExtras(args);
        //个人信息页面
        startActivityForResult(intent, PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE);
    }

    /**
     * 进入班级二维码页面
     * @param data
     */
    private void enterGroupQrCode(ConversationHelper.ConversationInfo data) {

        Bundle args = new Bundle();
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                getString(R.string.class_qrcode));
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,data.contactId);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,data.name);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION,data.schoolName);
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK
                || requestCode != ChatActivity.REQUEST_CODE_CHAT
                || data == null) {
            return;
        }
        boolean classNameChanged = data.getBooleanExtra(
                ChatActivity.EXTRA_CLASS_NAME_CHANGED, false);
        if (classNameChanged) {
            conversationInfo.name = data.getStringExtra(ChatActivity.EXTRA_CLASS_NAME);
            getCurrAdapterViewHelper().update();
        }
    }

}
