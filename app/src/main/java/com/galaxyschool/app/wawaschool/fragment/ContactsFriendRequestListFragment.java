package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.SlideListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendRequestInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendRequestListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsFriendRequestListFragment extends ContactsListFragment {

    public static final String TAG = ContactsFriendRequestListFragment.class.getSimpleName();

    private ContactsFriendRequestInfo request;
    private boolean agree;
    public static final int REQUEST_CODE_CONTACTS_FRIEND_REQUEST_LIST = 708;
    private static boolean hasMessageHandled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_friend_request_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    private void refreshData() {
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.new_friends);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
//            listView.setSlideMode(SlideListView.SlideMode.NONE);
//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    listView, R.layout.contacts_request_item) {
            SlideListViewHelper listViewHelper = new SlideListViewHelper(getActivity(),
                    listView, R.layout.contacts_request_item, 0,
                    R.layout.contacts_slide_list_item_delete) {
                @Override
                public void loadData() {
                    loadRequests();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final ContactsFriendRequestInfo data =
                            (ContactsFriendRequestInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_request_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnail(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView);
                        //点击头像进入个人详情
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //游客之类的memberId为空的不给点击。
                                if (!TextUtils.isEmpty(data.getMemberId())) {
                                    ActivityUtils.enterPersonalSpace(getActivity(),
                                            data.getMemberId());
                                }
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_request_item_title);
                    if (textView != null) {
                        textView.setText(data.getApplyJoinName());
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_request_item_description);
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                    }
                    Button agree = (Button) view.findViewById(R.id.contacts_request_item_button_agree);
                    if (agree != null) {
                        agree.setTag(holder);
                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                ViewHolder holder = (ViewHolder) v.getTag();
                                if (holder == null) {
                                    return;
                                }
                                agreeRequest((ContactsFriendRequestInfo) holder.data, true);
                                v.setEnabled(false);
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.setEnabled(true);
                                    }
                                }, 1000);
                            }
                        });
                    }
                    Button reject = (Button) view.findViewById(R.id.contacts_request_item_button_reject);
                    if (reject != null) {
                        reject.setTag(holder);
                        reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                ViewHolder holder = (ViewHolder) v.getTag();
                                if (holder == null) {
                                    return;
                                }
                                agreeRequest((ContactsFriendRequestInfo) holder.data, false);
                                v.setEnabled(false);
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.setEnabled(true);
                                    }
                                }, 1000);
                            }
                        });
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_request_item_status);
                    if (data.getCheckState() == 0) {
                        // not approved
                        if (agree != null) {
                            agree.setVisibility(View.VISIBLE);
                            agree.setText(R.string.accept);
                        }
                        if (reject != null) {
                            reject.setVisibility(View.VISIBLE);
                            reject.setText(R.string.refuse);
                        }
                        if (textView != null) {
                            textView.setVisibility(View.GONE);
                        }
                    } else if (data.getCheckState() == 1) {
                        // approved
                        if (agree != null) {
                            agree.setVisibility(View.GONE);
                        }
                        if (reject != null) {
                            reject.setVisibility(View.GONE);
                        }
                        if (textView != null) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.added);
                        }
                    } else if (data.getCheckState() == 2) {
                        // refused
                        if (agree != null) {
                            agree.setVisibility(View.GONE);
                        }
                        if (reject != null) {
                            reject.setVisibility(View.GONE);
                        }
                        if (textView != null) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.refused);
                        }
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_item_delete);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            if (holder == null) {
                                return;
                            }
                            deleteRequest((ContactsFriendRequestInfo) holder.data);
                            v.setEnabled(false);
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    v.setEnabled(true);
                                }
                            }, 1000);
                        }
                    });
                    textView.setTag(holder);
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadRequests();
        }
    }

    private void loadRequests() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsFriendRequestListResult>(
                        ContactsFriendRequestListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsFriendRequestListResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    return;
                }
                updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_FRIEND_REQUEST_LIST_URL, params, listener);
    }

    private void updateViews(ContactsFriendRequestListResult result) {
        List<ContactsFriendRequestInfo> list = result.getModel().getApplyJoinPersonalList();
        getCurrAdapterViewHelper().setData(list);
    }

    private void agreeRequest(ContactsFriendRequestInfo data, boolean agree) {
        this.request = data;
        this.agree = agree;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", data.getId());
        params.put("CheckState", agree ? 1 : 2);
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
//                    TipsHelper.showToast(getActivity(), R.string.process_request_failed);
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        getString(ContactsFriendRequestListFragment.this.agree ?
                                R.string.friend_request_agreed : R.string.friend_request_refused));
                //设置标志位
                setHasMessageHandled(true);
                if (ContactsFriendRequestListFragment.this.agree) {
                    ContactsFriendRequestListFragment.this.request.setCheckState(1);
                } else {
                    ContactsFriendRequestListFragment.this.request.setCheckState(2);
                }
                loadViews();
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_FRIEND_REQUEST_PROCESS_URL, params, listener);
    }

    private void deleteRequest(final ContactsFriendRequestInfo data) {
        this.request = data;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", data.getId());
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
                getCurrAdapterViewHelper().getData().remove(
                        ContactsFriendRequestListFragment.this.request);
                //未审批状态删除才需要刷新
                if (data.getCheckState() == 0) {
                    //设置标志位
                    setHasMessageHandled(true);
                }
                loadViews();
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_FRIEND_REQUEST_DELETE_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void setHasMessageHandled(boolean hasMessageHandled) {
        ContactsFriendRequestListFragment.hasMessageHandled = hasMessageHandled;
    }

    public static boolean hasMessageHandled() {
        return hasMessageHandled;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if (requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE) {

                //修改备注
                if (PersonalSpaceFragment.hasRemarkNameChanged()) {
                    PersonalSpaceFragment.setHasRemarkNameChanged(false);
                    //刷新标志位
                    setHasMessageHandled(true);
                }

                //删除好友
                if (PersonalSpaceFragment.hasUnbindFriendRelationship()) {
                    PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
                    //刷新标志位
                    setHasMessageHandled(true);
                }
            }
        }
    }
}
