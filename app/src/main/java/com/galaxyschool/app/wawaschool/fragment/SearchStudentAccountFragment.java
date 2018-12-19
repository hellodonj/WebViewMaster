package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.PersonalContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchStudentAccountFragment extends ContactsPickerFragment {

    public static final String TAG = SearchStudentAccountFragment.class.getSimpleName();

    private int pickerMode;
    private PersonalContactsPickerListener pickerListener;
    private View selectAllView;
    private UserInfoListResult dataListResult;
    private TextView keywordView;
    private String keyword = "";
    private String myClassId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_student_account, null);
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
            getPageHelper().clear();
//            loadViews();
        }
    }

    public void setPickerListener(PersonalContactsPickerListener listener) {
        this.pickerListener = listener;
    }

    private void init() {
        myClassId = getArguments().getString(ContactsPickerActivity.EXTRA_MY_CLASS_ID);
        this.pickerMode = ContactsPickerActivity.PICKER_MODE_MULTIPLE;
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

        TextView headTitletextView = (TextView) findViewById(R.id.contacts_header_title);
        headTitletextView.setVisibility(View.VISIBLE);

        Bundle args = getArguments();
        String title = null;
        if (args != null) {
            title = args.getString("title");
            if (title != null) {
                headTitletextView.setText(title);
            }
        }

        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_acccount));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        if (v.getText().toString().trim().length() == 0) {
                            getCurrAdapterViewHelper().clearData();
                            notifyPickerBar();
                        } else {
                            loadContacts();
                        }
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    notifyPickerBar();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;
        View view1 = findViewById(R.id.search_btn);
        if (view1 != null) {
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    loadContacts();
                }
            });
            view1.setVisibility(View.VISIBLE);
        }
        view1 = findViewById(R.id.contacts_search_bar_layout);
        if (view1 != null) {
            view1.setVisibility(View.VISIBLE);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            String text = getArguments().getString(
                    ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT);
            if (TextUtils.isEmpty(text)) {
                text = getString(R.string.confirm);
            }
            textView.setText(text);
//            textView.setBackgroundResource(R.drawable.sel_nav_button_bg);
            textView.setOnClickListener(this);
            textView.setVisibility(View.INVISIBLE);
//            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
//                textView.setVisibility(View.INVISIBLE);
//            } else {
//                textView.setVisibility(View.VISIBLE);
//            }
        }

        view1 = findViewById(R.id.contacts_select_all);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllContacts(!selectAllView.isSelected());
            }
        });
        if (this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
//            view1.setVisibility(View.VISIBLE);
            view1.setVisibility(View.GONE);
            this.selectAllView = findViewById(R.id.contacts_select_all_icon);
//            this.selectAllView.setSelected(true);
            this.selectAllView.setSelected(false);
        } else {
            view1.setVisibility(View.GONE);
        }

        view1 = findViewById(R.id.contacts_picker_bar_layout);
        if (view1 != null) {
            textView = (TextView) view1.findViewById(R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAllContacts(false);
                    }
                });
            }
            textView = (TextView) view1.findViewById(R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addStudentToMyClass();
                    }
                });
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
                    UserInfo data = (UserInfo) getDataAdapter().getItem(position);
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
                        if (data.getRealName() != null && !data.getRealName().equals("")) {
                            textView.setText(data.getRealName());
                        } else {
                            textView.setText(data.getNickName());
                        }

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
                            notifyPickerBar();
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
                        notifyPickerBar();
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
        String keyword = keywordView.getText().toString().trim();
        if (keyword == null || keyword.equals("")) {
            return;
        }
        loadContacts(keyword);
    }

    private void loadContacts(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("KeyWord", keyword);
        params.put("MemberId",getMemeberId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<UserInfoListResult>(
                        UserInfoListResult.class) {
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
                        updateViews(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SEARCH_USER_INFO_LIST_URL, params, listener);
    }

    private void updateViews(UserInfoListResult result) {
        {
            if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
                List<UserInfo> list = result.getModel().getData();
                if (list == null || list.size() <= 0) {
                    if (getPageHelper().isFetchingFirstPage()) {
                        getCurrAdapterViewHelper().clearData();
                        TipsHelper.showToast(getActivity(),
                                getString(R.string.no_data));
                    } else {
                        TipsHelper.showToast(getActivity(),
                                getString(R.string.no_more_data));
                    }
                    return;
                }

                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                }
                getPageHelper().updateByPagerArgs(result.getModel().getPager());
                getPageHelper().setCurrPageIndex(
                        getPageHelper().getFetchingPageIndex());
                if (getCurrAdapterViewHelper().hasData()) {
                    int position = getCurrAdapterViewHelper().getData().size();
                    if (position > 0) {
                        position--;
                    }
                    getCurrAdapterViewHelper().getData().addAll(list);
                    getCurrAdapterView().setSelection(position);
                    dataListResult.getModel().setData(getCurrAdapterViewHelper().getData());
                } else {
                    getCurrAdapterViewHelper().setData(list);
                    dataListResult = result;
                }
            }
        }

//        List<UserInfo> list = result.getModel().getData();
//        if (list == null && list.size() <= 0) {
//            return;
//        }
//
//
//        getCurrAdapterViewHelper().setData(list);

        if (this.selectAllView != null && getCurrAdapterViewHelper().hasData()) {
            selectAllContacts(this.selectAllView.isSelected());
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }

//        dataListResult = result;
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

    private void addStudentToMyClass() {
        List<UserInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_friend_at_least);
            return;
        }

        List<String> studentIds = new ArrayList<String>();
        for (UserInfo obj : items) {
            if (obj != null && !TextUtils.isEmpty(obj.getMemberId())) {
                studentIds.add(obj.getMemberId());
            }
        }
        addStudentToMyClass(myClassId, studentIds);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            addStudentToMyClass();
        } else {
            super.onClick(v);
        }
    }

}
