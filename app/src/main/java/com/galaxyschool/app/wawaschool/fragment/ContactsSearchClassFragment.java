package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.*;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.QrcodeProcessActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.*;

public class ContactsSearchClassFragment extends ContactsExpandListFragment {

    public static final String TAG = ContactsSearchClassFragment.class.getSimpleName();

    private TextView keywordView;
    private String keyword = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_search_expand, null);
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
            getPageHelper().clear();
            loadViews();
        }
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.join_class);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.school_name));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        searchKeyword(v.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrListViewHelper().clearData();
                    getPageHelper().clear();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            UIUtils.showSoftKeyboardValid(getActivity());
            editText.requestFocus();
        }
        this.keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(this);
            view.setVisibility(View.VISIBLE);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ExpandableListView listView = (ExpandableListView)
                findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.contacts_search_expand_list_item,
                    R.layout.contacts_expand_list_child_item) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((ContactsSearchSchoolInfo) getData().get(groupPosition))
                            .getClassList().get(childPosition);
                }

                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        ContactsSearchSchoolInfo schoolInfo = (ContactsSearchSchoolInfo)
                                getData().get(groupPosition);
                        if (schoolInfo != null && schoolInfo.getClassList() != null) {
                            return schoolInfo.getClassList().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);

                    ContactsSearchClassInfo data = (ContactsSearchClassInfo) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                        view.setTag(holder);
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_class_icon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getClassName());
                    }

                    textView = (TextView) view.findViewById(R.id.contacts_item_status);
                    if (textView != null) {
                        textView.setTag(holder);
                        if (data.hasJoined()) {
                            textView.setText(R.string.joined);
                            textView.setTextColor(getResources().getColor(
                                    R.color.text_dark_gray));
                            textView.getPaint().setFlags(textView.getPaintFlags()
                                    & (~Paint.UNDERLINE_TEXT_FLAG));
                            textView.setBackgroundResource
                                    (R.drawable.button_bg_transparent_with_round_sides);
                            textView.setOnClickListener(null);
                        } else {
//                            if (data.isClass()) {
//                                textView.setText(R.string.join_class);
//                            } else if (data.isSchool()) {
//                                textView.setText(R.string.join_school);
//                            }
                            textView.setText(R.string.join);
                            textView.setTextColor(Color.parseColor("#009039"));
                            textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);
//                            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                            textView.setOnClickListener(ContactsSearchClassFragment.this);
                        }
                    }

                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    ContactsSearchSchoolInfo data = (ContactsSearchSchoolInfo) getGroup(groupPosition);
                    View headerView = view.findViewById(R.id.contacts_item_header_layout);
                    if (headerView != null) {
                        if (data.isFirst()) {
                            headerView.setVisibility(View.VISIBLE);
                            ((TextView) headerView.findViewById(R.id.contacts_item_header))
                                    .setText(data.hasJoined() ?
                                            R.string.joined : R.string.unjoined);
                        } else {
                            headerView.setVisibility(View.GONE);
                        }
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
//                        getThumbnailManager().displayThumbnail(
//                                AppSettings.getFileUrl(data.getSchoolLogo()), imageView);
                        imageView.setVisibility(View.GONE);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        imageView.setImageResource(isExpanded ?
                                R.drawable.list_exp_up : R.drawable.list_exp_down);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                        view.setTag(holder);
                    }
                    holder.data = data;

                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    listView, dataAdapter) {
                @Override
                public void loadData() {
                    searchKeyword(keywordView.getText().toString());
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                            int groupPosition, int childPosition, long id) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (holder == null) {
                        return false;
                    }
                    ContactsSearchSchoolInfo schoolInfo = (ContactsSearchSchoolInfo)
                            getDataAdapter().getGroup(groupPosition);
                    ContactsSearchClassInfo classInfo = (ContactsSearchClassInfo) holder.data;
//                    enterClassInfo(schoolInfo, classInfo);
                    if (classInfo.hasJoined()) {
                        enterGroupMembers(schoolInfo, classInfo);
                    } else {
                        enterGroupInfo(schoolInfo, classInfo);
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                            int groupPosition, long id) {
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(listView, listViewHelper);
        }
    }

    private void loadViews() {
//        if (getCurrListViewHelper().hasData()) {
//            getCurrListViewHelper().update();
//        } else {
            searchKeyword(this.keywordView.getText().toString());
//        }
    }

    private void searchKeyword(String keyword) {
        keyword = keyword.trim();
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        if (!keyword.equals(this.keyword)) {
            getCurrListViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;

        UIUtils.hideSoftKeyboard(getActivity());
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("LikeName", keyword);
        params.put("VersionCode", 1);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsSearchSchoolListResult>(
                        ContactsSearchSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsSearchSchoolListResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    return;
                }
                updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_SEARCH_CLASS_URL, params, listener);
    }

    private void updateViews(ContactsSearchSchoolListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<ContactsSearchSchoolInfo> list = result.getModel().getSchoolList();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrListViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrListViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrListViewHelper().hasData()) {
                int position = getCurrListViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                List<ContactsSearchSchoolInfo> dataList = getCurrListViewHelper().getData();
                dataList.addAll(list);
                getCurrListViewHelper().getData().addAll(sortData(dataList));
                getCurrListView().setSelection(position);
            } else {
                getCurrListViewHelper().setData(sortData(list));
                if (getCurrListViewHelper().hasData()) {
                    getCurrListView().expandGroup(0);
                }
            }
        }
    }

    private List<ContactsSearchSchoolInfo> sortData(List<ContactsSearchSchoolInfo> list) {
        if (list == null || list.size() <= 0) {
            return list;
        }

        List<ContactsSearchSchoolInfo> result = new ArrayList();
        List<ContactsSearchSchoolInfo> temp = new ArrayList();
        for (ContactsSearchSchoolInfo obj : list) {
            if (obj.hasJoined()) {
                result.add(obj);
            } else {
                temp.add(obj);
            }
        }

        if (result.size() > 0) {
            result.get(0).setFirst(true);
        }
        if (temp.size() > 0) {
            temp.get(0).setFirst(true);
        }

        result.addAll(temp);
        return result;
    }

    private void enterGroupMembers(ContactsSearchSchoolInfo schoolInfo, ContactsSearchClassInfo classInfo) {
        Bundle args = new Bundle();
        args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classInfo.getType());
        args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListID());
        args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, schoolInfo.getSchoolId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolInfo.getSchoolName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterGroupInfo(ContactsSearchSchoolInfo schoolInfo, ContactsSearchClassInfo classInfo) {
        Bundle args = new Bundle();
        if (classInfo.isSchool()) {
            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                    getActivity().getString(R.string.school_qrcode));
        } else {
            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                    getActivity().getString(R.string.class_qrcode));
        }
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,
                classInfo.getClassMailListID());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ICON,
                classInfo.getHeadPicUrl());
        if (classInfo.isSchool()) {
            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
                    schoolInfo.getSchoolName());
        } else {
            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
                    classInfo.getClassName());
        }
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void joinGroup(ContactsSearchSchoolInfo schoolInfo, ContactsSearchClassInfo classInfo) {
        if (classInfo.isClass()) {
            enterClassInfo(schoolInfo, classInfo);
        } else if (classInfo.isSchool()) {
//            showJoinSchoolDialog(schoolInfo);
            joinSchool(schoolInfo, classInfo);
        }
    }

    private void joinSchool(ContactsSearchSchoolInfo schoolInfo, ContactsSearchClassInfo classInfo) {
        QrcodeSchoolInfo data = new QrcodeSchoolInfo();
        data.setId(schoolInfo.getSchoolId());
        data.setSname(schoolInfo.getSchoolName());
        data.setLogoUrl(classInfo.getHeadPicUrl());
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_SCHOOL_INFO, data);
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterClassInfo(ContactsSearchSchoolInfo schoolInfo, ContactsSearchClassInfo classInfo) {
        QrcodeClassInfo data = new QrcodeClassInfo();
        data.setClassId(classInfo.getClassId());
        data.setCname(classInfo.getClassName());
        data.setHeadPicUrl(classInfo.getHeadPicUrl());
        data.setSname(schoolInfo.getSchoolName());
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_CLASS_INFO, data);
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void showJoinSchoolDialog(final ContactsSearchSchoolInfo schoolInfo) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
                getActivity(),
                null, getString(R.string.join_as_school_teacher),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        joinSchool(schoolInfo);
                    }
                });
        dialog.show();
    }

    private void joinSchool(ContactsSearchSchoolInfo schoolInfo) {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolInfo.getSchoolId());
        params.put("SchoolName", schoolInfo.getSchoolName());
        DefaultListener listener = new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
//                    TipsHelper.showToast(getActivity(),
//                            R.string.application_commit_failure);
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        R.string.application_commit_success);
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.SAVE_TEACHER_ROLEINFO_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_btn) {
            searchKeyword(this.keywordView.getText().toString());
        } else if (v.getId() == R.id.contacts_item_status) {
            MyViewHolder holder = (MyViewHolder) v.getTag();
            if (holder == null) {
                return;
            }

            ContactsSearchSchoolInfo schoolInfo = (ContactsSearchSchoolInfo)
                    getCurrListViewHelper().getDataAdapter().getGroup(holder.groupPosition);
            ContactsSearchClassInfo classInfo = (ContactsSearchClassInfo) holder.data;
            joinGroup(schoolInfo, classInfo);
        } else {
            super.onClick(v);
        }
    }


    private class MyViewHolder extends ViewHolder<ContactsSearchClassInfo> {
        int groupPosition;
        int childPosition;
    }

}
