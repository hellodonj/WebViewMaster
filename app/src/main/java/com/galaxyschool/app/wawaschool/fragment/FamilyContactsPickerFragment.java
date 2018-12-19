package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.*;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsChildInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsChildListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyContactsPickerFragment extends ContactsPickerFragment {

    public static final String TAG = FamilyContactsPickerFragment.class.getSimpleName();

    private int pickerMode;
    private FamilyContactsPickerListener pickerListener;
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

    public void setPickerListener(FamilyContactsPickerListener listener) {
        this.pickerListener = listener;
    }

    void init() {
        this.pickerMode = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_MODE);

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
            textView.setText(R.string.select_my_children);
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
            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                textView.setVisibility(View.INVISIBLE);
            } else {
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
        if (this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
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
            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
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
                    loadContacts();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ContactsChildInfo data =
                            (ContactsChildInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    MyViewHolder holder = (MyViewHolder) view.getTag();
//                    if (holder == null) {
                        holder = new MyViewHolder();
//                    }
                    holder.data = data;
                    holder.position = position;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIcon(
                                AppSettings.getFileUrl(data.getHeaderPic()), imageView);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getRealName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        holder.selectorView = imageView;
                        imageView.setSelected(isItemSelected(position));
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
            loadContacts();
        }
    }

    private void loadContacts() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("ParentId", getUserInfo().getMemberId());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsChildListResult>(
                        ContactsChildListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsChildListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_CHILD_LIST_URL, params, listener);
    }

    private void updateViews(ContactsChildListResult result) {
        List<ContactsChildInfo> list = result.getModel().getStuList();
        if (list == null && list.size() <= 0) {
            return;
        }
        getCurrAdapterViewHelper().setData(list);

        if (this.selectAllView != null && getCurrAdapterViewHelper().hasData()) {
            selectAllContacts(this.selectAllView.isSelected());
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }
    }

    private void notifyPickerBar() {
        notifyPickerBar(hasSelectedItems());
    }

    private void notifyPickerBar(boolean selected) {
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

    private void selectAllContacts(boolean selected) {
        ImageView imageView = (ImageView) findViewById(
                R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setSelected(selected);
        }
        selectAllItems(selected);
        notifyPickerBar();
        getCurrAdapterViewHelper().update();
    }

    private void completePickContacts() {
        List<ContactsChildInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_friend_at_least);
            return;
        }

        List<ContactItem> result = new ArrayList<ContactItem>();
        ContactItem item = null;
        String name = null;
        for (ContactsChildInfo obj : items) {
            item = new ContactItem();
            item.setId(obj.getMemberId());
            name = obj.getRealName();
            if (TextUtils.isEmpty(name)) {
                name = obj.getNickName();
            }
            item.setName(name);
            item.setType(ContactItem.CONTACT_TYPE_PERSON);
            item.setIcon(AppSettings.getFileUrl(obj.getHeaderPic()));
            item.setHxId("hx" + obj.getMemberId());
            result.add(item);
        }

        if (this.pickerListener != null) {
            this.pickerListener.onFamilyContactsPicked(result);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            completePickContacts();
        } else {
            super.onClick(v);
        }
    }

}
