package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.chat.EMConversation;
//import com.hyphenate.chat.EMMessage;
//import com.hyphenate.util.EMLog;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
//import com.lqwawa.intleducation.module.chat.EaseHelper;
//import com.lqwawa.intleducation.module.chat.db.InviteMessgeDao;
//import com.lqwawa.intleducation.module.chat.ui.ChatActivity;
import com.lqwawa.intleducation.module.user.adapter.PrivateMsgListAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.HXConversationVo;
import com.lqwawa.intleducation.module.user.vo.MyClassVo;
import com.lqwawa.intleducation.module.user.vo.PersonalInfo;
import org.xutils.common.Callback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 我的私信列表
 */

public class MyPrivateMsgListFragment extends MyBaseFragment {
    private static final String TAG = "MyPrivateMsgListFragment";


    private PullToRefreshView pullToRefreshView;
    private ListView listView;

//    private ContactSyncListener contactSyncListener;
//    private BlackListSyncListener blackListSyncListener;
//    private ContactInfoSyncListener contactInfoSyncListener;

    List<String> userIds;
    List<String> groupIds;
    List<HXConversationVo> hxConversationVoList;
    List<HXConversationVo> groupHxConversationVoList;
    private PrivateMsgListAdapter privateMsgListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_refresh_list, container, false);

        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

//        EaseHelper.getInstance().addNewMessageListener(newMessageListener);
        //pullToRefreshView.showRefresh();
        refreshList();
        registerBoradcastReceiver();
    }

    private void initViews() {

        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                refreshList();
            }
        });

        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        privateMsgListAdapter = new PrivateMsgListAdapter(activity);
        listView.setAdapter(privateMsgListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HXConversationVo vo = (HXConversationVo) privateMsgListAdapter.getItem(position);
                if (vo != null) {
                    activity.setResult(Activity.RESULT_OK);
                    if (vo.isIsGroup()) {
//                        ChatActivity.startForResult(activity, true,
//                                vo.getGroup().getGroupUuid(),
//                                "", false);
                    } else {
//                        ChatActivity.startForResult(activity, false,
//                                vo.getConversation().conversationId(),
//                                vo.getPersonal().getName(), false);
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final HXConversationVo vo = (HXConversationVo) privateMsgListAdapter.getItem(position);
                if (vo != null) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.delete_message_tip));
                    builder.setTitle(activity.getResources().getString(R.string.tip));
                    builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
//                                    deleteMessage(vo.getConversation());
                                }
                            });

                    builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                }
                return true;
            }
        });

//        contactSyncListener = new ContactSyncListener();
//        EaseHelper.getInstance().addSyncContactListener(contactSyncListener);
//
//        blackListSyncListener = new BlackListSyncListener();
//        EaseHelper.getInstance().addSyncBlackListListener(blackListSyncListener);
//
//        contactInfoSyncListener = new ContactInfoSyncListener();
//        EaseHelper.getInstance().getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);
    }

//    private void deleteMessage(EMConversation conversation) {
//        // delete conversation
//        EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
//        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(activity);
//        inviteMessgeDao.deleteMessage(conversation.conversationId());
//        refreshList();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (contactSyncListener != null) {
//            EaseHelper.getInstance().removeSyncContactListener(contactSyncListener);
//            contactSyncListener = null;
//        }
//
//        if (blackListSyncListener != null) {
//            EaseHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
//        }
//
//        if (contactInfoSyncListener != null) {
//            EaseHelper.getInstance().getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
//        }
//    }


    private void refreshList() {
//        hxConversationVoList = new ArrayList<HXConversationVo>();
//        groupHxConversationVoList = new ArrayList<HXConversationVo>();
//        Map<String, EMConversation> newMsgs =
//                EMClient.getInstance().chatManager().getAllConversations();
//        if (newMsgs.size() == 0) {
//            pullToRefreshView.onHeaderRefreshComplete();
//            privateMsgListAdapter.setData(null);
//            privateMsgListAdapter.notifyDataSetChanged();
//            return;
//        }
//        for (String key : newMsgs.keySet()) {
//            EMConversation conversation = newMsgs.get(key);
//            if (conversation.getLastMessage() == null) {
//                try {
//                    // delete conversation
//                    EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
//                    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(activity);
//                    inviteMessgeDao.deleteMessage(conversation.conversationId());
//                    newMsgs.remove(key);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        userIds = new ArrayList<String>();
//        groupIds = new ArrayList<String>();
//        for (EMConversation conversation : newMsgs.values()) {
//            HXConversationVo hxConversationVo = new HXConversationVo();
//            hxConversationVo.setConversation(conversation);
//            if (conversation.getType() == EMConversation.EMConversationType.GroupChat
//                    || conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
//                groupIds.add(conversation.conversationId());
//                hxConversationVo.setIsGroup(true);//群聊
//                groupHxConversationVoList.add(hxConversationVo);
//            } else if (conversation.getType() == EMConversation.EMConversationType.Chat) {
//                hxConversationVo.setIsGroup(false);//单聊
//                userIds.add(conversation.conversationId().substring(2));
//                hxConversationVoList.add(hxConversationVo);
//            }
//        }
//
//        if (userIds.size() > 0) {
//            getUserInfos();
//        } else {
//            if (groupIds.size() > 0) {
//                getGroupInfos();
//            } else {
//                pullToRefreshView.onHeaderRefreshComplete();
//            }
//        }
    }

    private void getUserInfos() {
//        UserHelper.getUserListByIds(userIds, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String s) {
//                ResponseVo<List<PersonalInfo>> result = JSON.parseObject(s,
//                        new TypeReference<ResponseVo<List<PersonalInfo>>>() {
//                        });
//                if (result.getCode() == 0) {
//                    List<PersonalInfo> personalInfos = result.getData();
//                    if (personalInfos != null && personalInfos.size() > 0
//                            && personalInfos.size() == hxConversationVoList.size()) {
//                        for (int i = 0; i < hxConversationVoList.size(); i++) {
//                            for (int j = 0; j < personalInfos.size(); j++) {
//                                if (hxConversationVoList.get(i).getConversation().conversationId()
//                                        .equals("hx" + personalInfos.get(j).getId())) {
//                                    hxConversationVoList.get(i).setPersonal(personalInfos.get(j));
//                                    break;
//                                }
//                            }
//                        }
//                        if (groupIds.size() > 0) {
//                            getGroupInfos();
//                        } else {
//                            notifyList();
//                        }
//                    }
//                } else {
//                    pullToRefreshView.onHeaderRefreshComplete();
//                    ToastUtil.showToast(activity, "获取个人信息失败：" + result.getMessage());
//                }
//            }
//
//            @Override
//            public void onError(Throwable throwable, boolean b) {
//                pullToRefreshView.onHeaderRefreshComplete();
//                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException e) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

    private void getGroupInfos() {
//        UserHelper.getGroupListByUuids(groupIds, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String s) {
//                ResponseVo<List<MyClassVo>> result = JSON.parseObject(s,
//                        new TypeReference<ResponseVo<List<MyClassVo>>>() {
//                        });
//                if (result.getCode() == 0) {
//                    List<MyClassVo> groupInfos = result.getData();
//                    if (groupInfos != null && groupInfos.size() > 0
//                            && groupInfos.size() == groupHxConversationVoList.size()) {
//                        for (int i = 0; i < groupHxConversationVoList.size(); i++) {
//                            String uuid = groupHxConversationVoList.get(i).
//                                    getConversation().conversationId();
//                            for (int j = 0; j < groupInfos.size(); j++) {
//                                String id = groupInfos.get(j).getGroupUuid();
//                                if (uuid.equals(id)) {
//                                    groupHxConversationVoList.get(i).setGroup(groupInfos.get(j));
//                                    break;
//                                }
//                            }
//                        }
//                        notifyList();
//                    }
//                } else {
//                    pullToRefreshView.onHeaderRefreshComplete();
//                    ToastUtil.showToast(activity, "获取组信息失败：" + result.getMessage());
//                }
//            }
//
//            @Override
//            public void onError(Throwable throwable, boolean b) {
//                pullToRefreshView.onHeaderRefreshComplete();
//                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
//            }
//
//            @Override
//            public void onCancelled(CancelledException e) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

    private void notifyList() {
        pullToRefreshView.onHeaderRefreshComplete();
        if (hxConversationVoList != null) {
            if (groupHxConversationVoList != null && groupHxConversationVoList.size() > 0) {
                hxConversationVoList.addAll(groupHxConversationVoList);
            }
        }
        privateMsgListAdapter.setData(hxConversationVoList);
        privateMsgListAdapter.notifyDataSetChanged();
    }
//
//    class ContactSyncListener implements EaseHelper.DataSyncListener {
//        @Override
//        public void onSyncComplete(final boolean success) {
//            EMLog.d(TAG, "on contact list sync success:" + success);
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    getActivity().runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if (success) {
//                                refreshList();
//                            } else {
//                                String s1 = getResources().getString(R.string.get_failed_please_check);
//                                Toast.makeText(getActivity(), s1, Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                    });
//                }
//            });
//        }
//    }
//
//    class BlackListSyncListener implements EaseHelper.DataSyncListener {
//
//        @Override
//        public void onSyncComplete(boolean success) {
//            getActivity().runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    refreshList();
//                }
//            });
//        }
//
//    }
//
//    class ContactInfoSyncListener implements EaseHelper.DataSyncListener {
//
//        @Override
//        public void onSyncComplete(final boolean success) {
//            EMLog.d(TAG, "on contactinfo list sync success:" + success);
//            getActivity().runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    if (success) {
//                        refreshList();
//                    }
//                }
//            });
//        }
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        activity.unregisterReceiver(mBroadcastReceiver);
//        EaseHelper.getInstance().removeNewMessageListener(newMessageListener);
//    }
//
//    private EaseHelper.NewMessageListener newMessageListener = new EaseHelper.NewMessageListener() {
//        @Override
//        public void onReceived(EMMessage message) {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    //pullToRefreshView.showRefresh();
//                    refreshList();
//                }
//            });
//        }
//
//        @Override
//        public void onReaded() {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    //pullToRefreshView.showRefresh();
//                    refreshList();
//                }
//            });
//        }
//    };

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.DeleteFriend)
                    || action.equals(AppConfig.ServerUrl.ExitGroup)) {
                refreshList();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.DeleteFriend);//
        myIntentFilter.addAction(AppConfig.ServerUrl.ExitGroup);//
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
