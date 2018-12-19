package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.*;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupPickerFragment extends ContactsPickerFragment
        implements View.OnClickListener {

    public static final String TAG = GroupPickerFragment.class.getSimpleName();

    private int pickerType;
    private int pickerMode;
    private int groupType;
    private GroupPickerListener pickerListener;
    private View selectAllView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_picker, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    public void setPickerListener(GroupPickerListener listener) {
        this.pickerListener = listener;
    }

    private void init() {
        this.pickerType = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_TYPE);
        this.pickerType &= ContactsPickerActivity.PICKER_TYPE_MEMBER;
        this.pickerMode = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_MODE);
        this.groupType = getArguments().getInt(ContactsPickerActivity.EXTRA_GROUP_TYPE);

        initViews();
    }

    private void initViews() {
        View view = findViewById(R.id.contacts_header_layout);
        if (view != null) {
            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.select_class);
        }

        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            String text = getArguments().getString(
                    ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT);
            if (TextUtils.isEmpty(text)) {
                text = getString(R.string.confirm);
            }
            textView.setText(text);
            textView.setBackgroundResource(R.drawable.sel_nav_button_bg);
            textView.setOnClickListener(this);
            if (this.pickerType == ContactsPickerActivity.PICKER_TYPE_GROUP) {
                textView.setVisibility(View.VISIBLE);
            }
        }

        view = findViewById(R.id.contacts_select_all);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllContacts(!selectAllView.isSelected());
            }
        });
        if (this.pickerType == ContactsPickerActivity.PICKER_TYPE_GROUP
                && this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
            view.setVisibility(View.VISIBLE);
            this.selectAllView = findViewById(R.id.contacts_select_all_icon);
            this.selectAllView.setSelected(true);
        } else {
            view.setVisibility(View.GONE);
        }

        view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAllContacts(false);
                    }
                });
            }
            textView = (TextView) view.findViewById(R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completePickContacts();
                    }
                });
            }
            if ((this.pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0
                    && getArguments().getBoolean(
                    ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.contacts_list_item_with_selector) {
                @Override
                public void loadData() {
                    loadGroups();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ContactsClassInfo data = (ContactsClassInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    MyViewHolder holder = (MyViewHolder) view.getTag();
//                    if (holder == null) {
                        holder = new MyViewHolder();
//                    }
                    holder.position = position;
                    holder.data = data;
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
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        holder.selectorView = imageView;
                        imageView.setSelected(isItemSelected(position));
                        if (pickerType != ContactsPickerActivity.PICKER_TYPE_GROUP) {
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    MyViewHolder holder = (MyViewHolder) getCurrViewHolder();
                    if (holder != null) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, false);
                            holder.selectorView.setSelected(false);
//                            holder.selectorView.invalidate();
                        }
                    }

                    holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    if (pickerType == ContactsPickerActivity.PICKER_TYPE_GROUP) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(position, true);
                            setCurrViewHolder(holder);
                            holder.selectorView.setSelected(true);
                        } else if (pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                            boolean selected = !isItemSelected(position);
                            selectItem(position, selected);
                            holder.selectorView.setSelected(selected);
                            if (selectAllView != null) {
                                selectAllView.setSelected(isAllItemsSelected());
                            }
                            notifyPickerBar();
                        }
                    } else if (pickerType == ContactsPickerActivity.PICKER_TYPE_MEMBER) {
                        enterMemberContacts((ContactsClassInfo) holder.data);
                    }
//                    holder.selectorView.invalidate();
                    getDataAdapter().notifyDataSetChanged();
                }
            };
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
        if (getUserInfo() == null){
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
        if (list == null && list.size() <= 0) {
            return;
        }

        List<ContactsClassInfo> groupList = null;
        if (this.groupType == ContactsPickerActivity.GROUP_TYPE_CLASS) {
            groupList = new ArrayList<ContactsClassInfo>();
            for (ContactsClassInfo obj : list) {
                if (this.groupType == obj.getType()) {
                    groupList.add(obj);
                }
            }
        } else {
            groupList = list;
        }

        if (groupList != null) {
            getCurrAdapterViewHelper().setData((List) groupList);
        }

        if (this.selectAllView != null && getCurrAdapterViewHelper().hasData()) {
            selectAllContacts(this.selectAllView.isSelected());
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }
    }
    void notifyPickerBar() {
        notifyPickerBar(hasSelectedItems());
    }

    void notifyPickerBar(boolean selected) {
        View view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            TextView textView = (TextView) view.findViewById(
                    R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setEnabled(selected);
            }
            textView = (TextView) view.findViewById(
                    R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setEnabled(selected);
            }
        }
    }

    void selectAllContacts(boolean selected) {
        ImageView imageView = (ImageView) findViewById(
                R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setSelected(selected);
        }
        selectAllItems(selected);
        notifyPickerBar();
        getCurrAdapterViewHelper().update();
    }

    void completePickContacts() {
        List<ContactsClassInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_class_at_least);
            return;
        }

        ArrayList<ContactItem> result = new ArrayList<ContactItem>();
        ContactItem item = null;
        for (ContactsClassInfo obj : items) {
            item = new ContactItem();
            item.setId(obj.getId());
            item.setName(obj.getClassMailName());
            item.setIcon(AppSettings.getFileUrl(obj.getHeadPicUrl()));
            item.setType(ContactItem.CONTACT_TYPE_GROUP);
            item.setHxId(obj.getGroupId());
            item.setSchoolId(obj.getLQ_SchoolId());
            item.setClassId(obj.getClassId());
            result.add(item);
        }

        if (this.pickerListener != null) {
            this.pickerListener.onGroupPicked(result);
        }
    }

    private void enterMemberContacts(ContactsClassInfo data) {
        Bundle args = getArguments();
        args.putString(ContactsPickerActivity.EXTRA_GROUP_ID, data.getId());
        args.putString(ContactsPickerActivity.EXTRA_GROUP_NAME, data.getClassMailName());
        args.putString(ContactsPickerActivity.EXTRA_SCHOOL_ID, data.getLQ_SchoolId());
        args.putString(ContactsPickerActivity.EXTRA_CLASS_ID, data.getClassId());
        GroupMemberPickerFragment fragment = new GroupMemberPickerFragment();
        fragment.setArguments(getArguments());
        fragment.setPickerListener((GroupMemberPickerListener) getParentFragment());
        FragmentTransaction ft = getParentFragment().getChildFragmentManager().beginTransaction();
//        ft.replace(R.id.contacts_layout, fragment, GroupMemberPickerFragment.TAG);
        ft.add(R.id.contacts_layout, fragment, GroupMemberPickerFragment.TAG);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            if (this.pickerType == ContactsPickerActivity.PICKER_TYPE_GROUP) {
                completePickContacts();
            }
        } else {
            super.onClick(v);
        }
    }

}
