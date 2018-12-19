package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.galaxyschool.app.wawaschool.ContactsClassManagementActivity;
import com.galaxyschool.app.wawaschool.ContactsMemberDetailsActivity;
import com.galaxyschool.app.wawaschool.ForbidClassMemberActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsListDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class GroupContactsListFragment extends ContactsListFragment {

    public static final String TAG = GroupContactsListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_CONTACTS_TYPE = "type";
        String EXTRA_CONTACTS_ID = "id";
        String EXTRA_CONTACTS_NAME = "name";
        String EXTRA_CONTACTS_SCHOOL_ID = "schoolId";
        String EXTRA_CONTACTS_SCHOOL_NAME = "schoolName";
        String EXTRA_CONTACTS_GRADE_ID = "gradeId";
        String EXTRA_CONTACTS_GRADE_NAME = "gradeName";
        String EXTRA_CONTACTS_CLASS_ID = "classId";
        String EXTRA_CONTACTS_CLASS_NAME = "className";
        String EXTRA_CONTACTS_HXGROUP_ID = "hxGroupId";
        String EXTRA_CONTACTS_FROM_CHAT = "fromChat";
        String EXTRA_CONTACTS_HAS_INSPECT_AUTH = "has_inspect_auth";

        String EXTRA_CONTACTS_FOR_REPORTER="reporter_list";
        int CONTACTS_TYPE_CLASS = 0;
        int CONTACTS_TYPE_SCHOOL = 1;
    }

    ContactsClassMemberInfo selectedMember;
    String groupId;
    String groupName;
    String classId;
    String className;
    String schoolId;
    String schoolName;
    String hxGroupId;
    String gradeName;
    int classStatus;
    boolean hasInspectAuth;
    Map<String, ContactsClassMemberInfo> membersMap = new LinkedHashMap<>();
    ContactsClassMemberInfo myselfInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        this.groupId = getArguments().getString(Constants.EXTRA_CONTACTS_ID);
        this.groupName = getArguments().getString(Constants.EXTRA_CONTACTS_NAME);
        this.classId = getArguments().getString(Constants.EXTRA_CONTACTS_CLASS_ID);
        this.className = getArguments().getString(Constants.EXTRA_CONTACTS_CLASS_NAME);
        this.hxGroupId = getArguments().getString(Constants.EXTRA_CONTACTS_HXGROUP_ID);
        this.schoolId = getArguments().getString(Constants.EXTRA_CONTACTS_SCHOOL_ID);
        this.schoolName = getArguments().getString(Constants.EXTRA_CONTACTS_SCHOOL_NAME);
        this.gradeName = getArguments().getString(Constants.EXTRA_CONTACTS_GRADE_NAME);
        this.hasInspectAuth = getArguments().getBoolean(Constants.EXTRA_CONTACTS_HAS_INSPECT_AUTH,false);
        StringBuilder builder = new StringBuilder();
//        if (!TextUtils.isEmpty(this.schoolName)) {
//            builder.append(this.schoolName);
//        }
        if (!TextUtils.isEmpty(this.gradeName)) {
            builder.append(this.gradeName);
        }
        this.groupName = builder.append(this.groupName).toString();

        initViews();
    }

    private void initViews() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
    }

    boolean isHeadTeacher() {
        return myselfInfo != null && myselfInfo.isHeadTeacher();
    }

    boolean isTeacher() {
        return myselfInfo != null && myselfInfo.getRole() == RoleType.ROLE_TYPE_TEACHER;
    }

    void enterMemberDetails(ContactsClassMemberInfo data) {
        if (data == null){
            return;
        }
        Bundle args = new Bundle();
        args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_TYPE,
                ContactsMemberDetailsActivity.MEMBER_TYPE_GROUP);
        args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_ROLE, data.getRole());
        args.putString(ContactsMemberDetailsActivity.EXTRA_MEMBER_ID, data.getId());
        Intent intent = new Intent(getActivity(), ContactsMemberDetailsActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addFriend(ContactsClassMemberInfo data) {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("NewFriendId", data.getMemberId());
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.friend_request_send_failed));
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        getString(R.string.friend_request_send_success));
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_ADD_FRIEND_URL, params, listener);
    }

    private void refreshAll() {
        List<AdapterViewHelper> list = getAllAdapterViewHelpers();
        if (list == null || list.size() <= 0) {
            return;
        }
        for (AdapterViewHelper helper : list) {
            helper.update();
        }
    }

    void forbidMemberChat(ContactsClassMemberInfo memberInfo) {
        Map<String, Object> params = new HashMap();
        params.put("Id", this.groupId);
        if (!memberInfo.isChatForbidden()) {
            params.put("GagPersonnel", memberInfo.getMemberId());
        } else {
            params.put("BanPersonnel", memberInfo.getMemberId());
        }
        DefaultListener listener = new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsClassMemberInfo memberInfo = (ContactsClassMemberInfo) getTarget();
                boolean isChatForbidden = memberInfo.isChatForbidden();
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
//                    TipsHelper.showToast(getActivity(), isChatForbidden ?
//                                    R.string.allow_chat_failure : R.string.forbid_chat_failure);
                    return;
                }
                TipsHelper.showToast(getActivity(), isChatForbidden ?
                        R.string.allow_chat_success : R.string.forbid_chat_success);

                memberInfo.setChatForbidden(!memberInfo.isChatForbidden());
                refreshAll();
            }
        };
        listener.setTarget(memberInfo);
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_FORBID_CLASS_MEMBER_CHAT_URL, params, listener);
    }

    void forbidClassChat() {
        Bundle args = new Bundle();
        ArrayList<ContactsClassMemberInfo> allMembers = new ArrayList();

        for (Map.Entry<String, ContactsClassMemberInfo> entry : membersMap.entrySet()) {
            if (getUserInfo().getMemberId().equals(entry.getValue().getMemberId())) {
                continue;
            }
            allMembers.add(entry.getValue());
        }

        if (allMembers.size() <= 0) {
            TipsHelper.showToast(getActivity(), R.string.operation_not_allowed);
            return;
        }

        args.putString(ForbidClassMemberActivity.EXTRA_CONTACT_ID, this.groupId);
        args.putParcelableArrayList(ForbidClassMemberActivity.EXTRA_CONTACT_LIST, allMembers);

        Intent intent = new Intent(getActivity(), ForbidClassMemberActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent,
                ForbidClassMemberActivity.REUEST_CODE_FORBID_CLASS_MEMBER);
    }

    void showMemberMenu(ContactsClassMemberInfo data) {
        this.selectedMember = data;
        UserInfo userInfo = getUserInfo();
        List<String> contentList = new ArrayList();
        if (data.getRole() == RoleType.ROLE_TYPE_TEACHER) {
            contentList.add(getString(R.string.teacher_info));
            if (isHeadTeacher()&&!data.isHeadTeacher()&&classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
                contentList.add(getString(R.string.out_of_class));
            }
//            if (userInfo.getRoles().contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))) {
//                contentList.add(getString(R.string.home_school_message));
//            }

        } else if (data.getRole() == RoleType.ROLE_TYPE_STUDENT) {
            contentList.add(getString(R.string.student_info));
//            if (userInfo.getRoles().contains(String.valueOf(RoleType.ROLE_TYPE_TEACHER))) {
//            if (!userInfo.getMemberId().equals(data.getMemberId())) {
//                contentList.add(getString(R.string.wawa_everyday_comment));
//            }
//            }
            if (isHeadTeacher()&&classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
                contentList.add(getString(R.string.out_of_class));
            }
        } else if (data.getRole() == RoleType.ROLE_TYPE_PARENT) {
            contentList.add(getString(R.string.parent_info));
            if (isHeadTeacher()&&!getMemeberId().equals(data.getMemberId())&&classStatus ==
                    ContactsClassManagementActivity
                    .CLASS_STATUS_PRESENT) {
                contentList.add(getString(R.string.out_of_class));
            }
        }

        if (!TextUtils.equals(userInfo.getMemberId(),data.getMemberId())){
            if (!data.getIsFriend()) {
                contentList.add(getString(R.string.add_as_friend));
            }
        }

        //用户校长助手权限的可以移出老师或者班主任
        if (hasInspectAuth) {
            contentList.add(getString(R.string.str_remove_person_of_address));
        }

//        if (!TextUtils.equals(userInfo.getMemberId(),data.getMemberId())){
//            if (isHeadTeacher()&& classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
//                contentList.add(getString(data.isChatForbidden() ?
//                        R.string.allow_chat : R.string.forbid_chat));
//            }
//            contentList.add(getString(R.string.start_chat));
//        }

        ContactsListDialog dialog = new ContactsListDialog(getActivity(),
                R.style.Theme_ContactsDialog, null,
                contentList, R.layout.contacts_dialog_list_text_item,
                new DataAdapter.AdapterViewCreator() {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView != null) {
                            String title = (String) convertView.getTag();
                            TextView textView = (TextView) convertView.findViewById(
                                    R.id.contacts_dialog_list_item_title);
                            if (textView != null) {
                                textView.setTextColor(Color.parseColor("#038bff"));
                                textView.setText(title);
                            }
                        }
                        return convertView;
                    }
                },
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String title = (String) view.getTag();
                        if (getString(R.string.teacher_info).equals(title)
                                || getString(R.string.student_info).equals(title)
                                || getString(R.string.parent_info).equals(title)) {
                            enterMemberDetails(selectedMember);
                        } else if (getString(R.string.add_as_friend).equals(title)) {
                            addFriend(selectedMember);
                        } else if (getString(R.string.start_chat).equals(title)) {
                            enterConversation(selectedMember);
                        } else if (getString(R.string.allow_chat).equals(title)
                                || getString(R.string.forbid_chat).equals(title)) {
                            forbidMemberChat(selectedMember);
                        } else if (getString(R.string.out_of_class).equals(title)) {
                            outOfClass(selectedMember);
                        } else if (getString(R.string.str_remove_person_of_address).equals(title)){
                            removeFromClass(selectedMember);
                        }
                    }
                }, getString(R.string.cancel), null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void outOfClass(final ContactsClassMemberInfo item) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.confirm_to_remove_from_class)
                        ,getString(R.string.cancel) ,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                removeFromClass(item);
            }
        });
        messageDialog.show();
//        Window window = messageDialog.getWindow();
//        WindowManager windowManager = getActivity().getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = (int)(display.getWidth());
//        window.setAttributes(lp);
    }

    protected  abstract void removeFromClass(ContactsClassMemberInfo info);

    void enterConversation(ContactsClassMemberInfo memberInfo) {
        if (!HXSDKHelper.getInstance().isLogined()) {
            TipsHelper.showToast(getActivity(), R.string.chat_service_not_works);
            return;
        }
        if (memberInfo == null){
            return;
        }
        String nickname = memberInfo.getRealName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = memberInfo.getNoteName();
        }
        if (TextUtils.isEmpty(nickname)) {
            nickname = memberInfo.getNickname();
        }
        String userName = "hx" + memberInfo.getMemberId();
        Bundle args = new Bundle();
        args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE);
        args.putString(ChatActivity.EXTRA_USER_ID, userName);
        args.putString(ChatActivity.EXTRA_USER_AVATAR,
                AppSettings.getFileUrl(memberInfo.getHeadPicUrl()));
        args.putString(ChatActivity.EXTRA_USER_NICKNAME, nickname);
        args.putString(ChatActivity.EXTRA_MEMBER_ID, memberInfo.getMemberId());
        args.putString(ChatActivity.EXTRA_CONTACT_ID, memberInfo.getFriendId());
        args.putBoolean(ChatActivity.EXTRA_IS_FRIEND, memberInfo.getIsFriend());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    void enterGroupConversation() {
        enterGroupConversation(this.hxGroupId, this.groupName, 0);
    }

    void enterGroupConversation(String groupId, String groupName, int fromWhere) {
        ContactsClassMemberInfo memberInfo = membersMap.get(getUserInfo().getMemberId());
        if (!isHeadTeacher() &&
                memberInfo != null && memberInfo.isChatForbidden()) {
            TipsHelper.showToast(getActivity(), R.string.chat_forbidden_alert);
            return;
        }
        if (!HXSDKHelper.getInstance().isLogined()) {
            TipsHelper.showToast(getActivity(), R.string.chat_service_not_works);
            return;
        }
        Bundle args = new Bundle();
        args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_GROUP);
        args.putString(ChatActivity.EXTRA_GROUP_ID, groupId);
        args.putString(ChatActivity.EXTRA_GROUP_NAME, groupName);
        args.putBoolean(ChatActivity.EXTRA_IS_CHAT_FORBIDDEN,
                memberInfo != null && memberInfo.isChatForbidden());
        args.putInt(ChatActivity.EXTRA_FROM_WHERE, fromWhere);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ForbidClassMemberActivity.REUEST_CODE_FORBID_CLASS_MEMBER) {
                ArrayList<ContactsClassMemberInfo> forbiddenMembers =
                        data.getExtras().getParcelableArrayList(
                                ForbidClassMemberActivity.REUEST_DATA_FORBIDDEN_MEMBERS);
                if (forbiddenMembers != null && forbiddenMembers.size() > 0) {
                    for (ContactsClassMemberInfo obj : forbiddenMembers) {
                        membersMap.get(obj.getMemberId()).setChatForbidden(true);
                    }
                }

                ArrayList<ContactsClassMemberInfo> allowedMembers =
                        data.getExtras().getParcelableArrayList(
                                ForbidClassMemberActivity.REUEST_DATA_ALLOWED_MEMBERS);
                if (allowedMembers != null && allowedMembers.size() > 0) {
                    for (ContactsClassMemberInfo obj : allowedMembers) {
                        membersMap.get(obj.getMemberId()).setChatForbidden(false);
                    }
                }
                refreshAll();
            }
        }
    }

    abstract class MyAdapterViewHelper extends AdapterViewHelper {

        public MyAdapterViewHelper(Context context, AdapterView adapterView,
                                   int itemViewLayout) {
            super(context, adapterView, itemViewLayout);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
            if (data == null) {
                return view;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
            }
            holder.data = data;
            ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
            if (imageView != null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                        R.drawable.default_user_icon);
            }
            TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
            if (textView != null) {
                if (data.getRole() == RoleType.ROLE_TYPE_PARENT) {
                    if (data.isFather()) {
                        textView.setText(getString(R.string.whose_parent, data.getStudentName(),
                                getString(R.string.dad)));
                    } else if (data.isMother()) {
                        textView.setText(getString(R.string.whose_parent, data.getStudentName(),
                                getString(R.string.mum)));
                    } else {
                        textView.setText(getString(R.string.whose_parent, data.getStudentName(),
                                getString(R.string.parent)));
                    }
                } else {
                    textView.setText(data.getNoteName());
                }
            }
            textView = (TextView) view.findViewById(R.id.contacts_item_indicator);
            if (textView != null) {
                textView.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                if (data.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                    if (data.getWorkingState() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.not_work_here));
                        textView.setBackgroundResource(R.drawable.teacher_leaved);
                        textView.setLayoutParams(params);
                    } else if (data.isHeadTeacher()) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.header_teacher));
                        textView.setBackgroundResource(R.drawable.teacher_header);
                        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        textView.setLayoutParams(params);
                    }
                } else if (data.getRole() == RoleType.ROLE_TYPE_STUDENT) {
                    if (data.getWorkingState() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.not_study_here));
                        textView.setBackgroundResource(R.drawable.teacher_leaved);
                        textView.setLayoutParams(params);
                    }
                }
            }

            imageView = (ImageView) view.findViewById(R.id.contacts_item_chat_indicator);
            if (imageView != null) {
                if (isHeadTeacher()) {
                    imageView.setVisibility(data.isChatForbidden() ? View.VISIBLE : View.GONE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            view.setTag(holder);
            return view;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                return;
            }
            showMemberMenu((ContactsClassMemberInfo) holder.data);
        }
    }

}
