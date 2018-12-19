package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.FamilyContactsResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.FamilyMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.FamilyMemberInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的家庭绑定关系列表
 */

public class MyFamilyListFragment extends ContactsListFragment {

    public static final String TAG = MyFamilyListFragment.class.getSimpleName();

    private FamilyMemberInfoListResult familyMemberInfoListResult;
    private String childId;
    private String roles;
    public static final int REQUEST_CODE_MY_FAMILY_LIST = 808 ;
    private static boolean hasUnbindRelationship;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_family, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {

        if (getArguments() != null){
            childId = getArguments().getString("childId");
            roles = getArguments().getString("roles");
        }

        //头布局
        View view = findViewById(R.id.contacts_header_layout);

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.my_family_contacts);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ListView listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {

            //家庭通讯录
            AdapterViewHelper listViewHelper = new FamilyContactsResourceAdapterViewHelper(
                    getActivity(), listView ,roles,getMemeberId()) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final FamilyMemberInfo data =
                            (FamilyMemberInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //加好友
                   TextView textView = (TextView) view.findViewById(R.id.add_good_friend);
                    if (textView != null) {
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addFriend(data);
                            }
                        });
                    }

                    //聊天
                    textView = (TextView) view.findViewById(R.id.chat);
                    if (textView != null) {
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                enterConversation(data);
                            }
                        });

                    }

                    //取消绑定
                    View unbindLayout = view.findViewById(R.id.layout_unbind);
                    if (unbindLayout != null) {
                        //解绑
                        unbindLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showUnbindDialog(data);
                            }
                        });
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
                    FamilyMemberInfo data = (FamilyMemberInfo) holder.data;
                    if (data != null) {
                    }

                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }

    }

    /**
     * 加好友
     * @param data
     */
    public void addFriend(final FamilyMemberInfo data) {
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
                            return;
                        }
                        TipsHelper.showToast(getActivity(),
                                getString(R.string.friend_request_send_success));
                        getCurrAdapterViewHelper().update();
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_ADD_FRIEND_URL, params, listener);
    }

    /**
     * 聊天
     * @param memberInfo
     */
   public void enterConversation(FamilyMemberInfo memberInfo) {
        if (!HXSDKHelper.getInstance().isLogined()) {
            TipsHelper.showToast(getActivity(), R.string.chat_service_not_works);
            return;
        }
        String nickname = memberInfo.getRealName();
        if (TextUtils.isEmpty(nickname)) {
            nickname = memberInfo.getNoteName();
        }
        if (TextUtils.isEmpty(nickname)) {
            nickname = memberInfo.getNickName();
        }
        String userName = "hx" + memberInfo.getMemberId();
        Bundle args = new Bundle();
        args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE);
        args.putString(ChatActivity.EXTRA_USER_ID, userName);
        args.putString(ChatActivity.EXTRA_USER_AVATAR,
                AppSettings.getFileUrl(memberInfo.getHeadPicUrl()));
        args.putString(ChatActivity.EXTRA_USER_NICKNAME, nickname);
        args.putString(ChatActivity.EXTRA_MEMBER_ID, memberInfo.getMemberId());
       //联系人Id
        args.putString(ChatActivity.EXTRA_CONTACT_ID, getMemeberId());
//        args.putBoolean(ChatActivity.EXTRA_IS_FRIEND, memberInfo.isFriend());
       args.putBoolean(ChatActivity.EXTRA_IS_FRIEND, memberInfo.getIsFriend());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(args);
        try {
            startActivityForResult(intent,PersonalSpaceFragment.
                    REQUEST_CODE_PERSONAL_SPACE);
        } catch (ActivityNotFoundException e) {

        }
    }

    /**
     * 解绑Dialog
     *
     * @param data
     */
    private void showUnbindDialog(final FamilyMemberInfo data) {
        String name = data.getRealName();
        if (TextUtils.isEmpty(name)){
            name = data.getNickName();
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.want_to_unbind_sb,name), getString(R.string
                .cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                unBindItem(data);
            }
        });
        messageDialog.show();

//        Window window = messageDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        messageDialog.show();
//        WindowManager windowManager = getActivity().getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = (int) (display.getWidth());
//        window.setAttributes(lp);

    }

    /**
     * 解绑条目
     *
     * @param data
     */
    private void unBindItem(final FamilyMemberInfo data) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ParentId", getMemeberId());
        params.put("ChildId", data.getMemberId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            //通知校园空间去刷新数据
                            MySchoolSpaceFragment.sendBrocast(getActivity());
                            EventBus.getDefault().post(new MessageEvent("handle_class_relationship_success"));
                            //设置解绑标志位
                            setHasUnbindRelationship(true);
                            TipsHelper.showToast(getActivity(), R.string.unbind_success);
                            //直接返回
                            getActivity().finish();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.RELATION_UNBIND_BY_PARENT_URL,
                params, listener);

    }

    private void refreshData() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
                //如果用户（学生/游客等角色）自己查看通讯录，ParentId 和 ChildId 保持一致
                params.put("ParentId",getMemeberId());
                //学生id
                if (!TextUtils.isEmpty(childId)) {
                    params.put("ChildId", childId);
                }

        //非必填 可以选择家庭通讯录中包含/不包含班主任
        params.put("IsIncludeHeadMaster",false);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_FAMILY_MAIL_LIST_BY_CHILD_ID_URL, params,
                new DefaultPullToRefreshDataListener<FamilyMemberInfoListResult>(
                        FamilyMemberInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }

                        updateResourceListView(getResult());
                    }
                });

    }

    private void updateResourceListView(FamilyMemberInfoListResult result) {

            List<FamilyMemberInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                return;
            } else {
                getCurrAdapterViewHelper().setData(list);
                familyMemberInfoListResult = result;
            }
    }

    private void enterSchoolSpace(SchoolInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void setHasUnbindRelationship(boolean hasUnbindRelationship) {
        MyFamilyListFragment.hasUnbindRelationship = hasUnbindRelationship;
    }

    public static boolean hasUnbindRelationship() {
        return hasUnbindRelationship;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            if (requestCode == PersonalSpaceFragment.
                    REQUEST_CODE_PERSONAL_SPACE){
                //修改备注
                if (PersonalSpaceFragment.hasRemarkNameChanged()){
                    PersonalSpaceFragment.setHasRemarkNameChanged(false);
                    //刷新标志位
                    setHasUnbindRelationship(true);
                    //刷新当前界面数据
                    refreshData();
                }

                //删除好友
                if (PersonalSpaceFragment.hasUnbindFriendRelationship()){
                    PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
                    //刷新标志位
                    setHasUnbindRelationship(true);
                    //刷新当前界面数据
                    refreshData();
                }

                if (ChatActivity.hasContentChanged()){
                    ChatActivity.setHasContentChanged(false);
                    refreshData();
                }
            }
        }
    }
}

