package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsConversationListActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.galaxyschool.app.wawaschool.views.sortlistview.SideBar;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortListViewHelper;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortModel;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupListFragment extends ContactsListFragment implements EMMessageListener {

    public static final String TAG = GroupListFragment.class.getSimpleName();

    private TextView indicatorView;
    private ContactsClassListResult dataListResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_sortlist_with_search_bar, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    private void initViews() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
//            if (listView.getHeaderViewsCount() <= 0) {
//                View headerView = LayoutInflater.from(getActivity()).inflate(
//                        R.layout.contacts_list_item_with_indicator, null);
//                ImageView imageView = (ImageView) headerView.findViewById(R.id.contacts_item_icon);
//                imageView.setImageResource(R.drawable.new_friends);
//                TextView textView = (TextView) headerView.findViewById(R.id.contacts_item_title);
//                textView.setText(R.string.conversation_list);
//                textView = (TextView) headerView.findViewById(R.id.contacts_item_indicator);
//                this.indicatorView = textView;
//                listView.addHeaderView(headerView);
//                headerView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        enterConversations();
//                    }
//                });
//            }
//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    listView, R.layout.contacts_list_item) {
            SortListViewHelper listViewHelper = new SortListViewHelper(getActivity(),
                    listView, R.layout.contacts_sortlist_item, 0, //R.id.contacts_item_catalog,
                    (SideBar) findViewById(R.id.contacts_sort_sidebar),
                    (TextView) findViewById(R.id.contacts_sort_tips)) {
                @Override
                public void loadData() {
                    loadGroups();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    SortModel sortModel =
                            (SortModel) getDataAdapter().getItem(position);
                    if (sortModel == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = sortModel;
                    ContactsClassInfo data = (ContactsClassInfo) sortModel.getData();
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()),
                                imageView, R.drawable.default_class_icon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getClassMailName());
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
                    enterGroupMembers((ContactsClassInfo) ((SortModel) holder.data).getData());
                }
            };
            listViewHelper.setSearchBar((ClearEditText) findViewById(
                    R.id.search_keyword));
            listViewHelper.showSideBar(false);
            listViewHelper.setData(null);

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadGroups();
        }
    }

    private void loadGroups() {
        if (!((MyApplication) getActivity().getApplication()).hasLogined()) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Role", getUserInfo().getRoles());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassListResult>(
                        ContactsClassListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsClassListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_CLASS_LIST_URL, params, listener);
    }

    private void updateViews(ContactsClassListResult result) {
        List<ContactsClassInfo> list = result.getModel().getClassMailListList();
        List<SortModel> sortList = processDataList(list);
        getCurrAdapterViewHelper().setData(sortList);
        dataListResult = result;
    }

    private List<SortModel> processDataList(List<ContactsClassInfo> list) {
        if (list != null && list.size() > 0) {
            List<SortModel> sortList = new ArrayList<SortModel>();
            SortModel sortModel = null;
            for (ContactsClassInfo obj : list) {
                sortModel = new SortModel();
                sortModel.setName(obj.getClassMailName());
                sortModel.setSortLetters(obj.getFirstLetter());
                sortModel.setData(obj);
                sortList.add(sortModel);
                return sortList;
            }
        }
        return null;
    }

    private void enterGroupMembers(ContactsClassInfo data) {
        Bundle args = null;
        if (data.isClass() || data.isSchool()) {
            args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, data.getType());
            args.putString(ContactsActivity.EXTRA_CONTACTS_ID, data.getId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, data.getClassMailName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, data.getLQ_SchoolId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, data.getClassId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, data.getClassMailName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, data.getGroupId());
        }

        if (args != null) {
            Intent intent = new Intent(getActivity(), ContactsActivity.class);
            startActivity(intent);
        }
    }

//    @Override
//    public void onEvent(EMNotifierEvent event) {
//        switch (event.getEvent()) {
//        case EventNewMessage:
//            hxNotifyEvents();
//            break;
//        case EventOfflineMessage:
//            hxNotifyEvents();
//            break;
//        case EventConversationListChanged:
//            hxNotifyEvents();
//            break;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void hxNotifyEvents() {
        this.indicatorView.setVisibility(View.VISIBLE);
    }

    private void enterConversations() {
        Intent intent = new Intent(getActivity(), ContactsConversationListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        hxNotifyEvents();
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

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
}
