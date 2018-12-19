package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolTeacherInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolTeacherListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsSelectClassHeadTeacherFragment extends ContactsListFragment {

    public static final String TAG = ContactsSelectClassHeadTeacherFragment.class.getSimpleName();

    public interface Constants {
        int REQUEST_CODE_SELECT_CLASS_HEADTEACHER = 10012;

        String EXTRA_CLASS_HEADTEACHER_CHANGED = "headteacherChanged";
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CLASS_HEADTEACHER_ID = "headTeacherId";
        String EXTRA_CLASS_HEADTEACHER_NAME = "headTeacherName";
    }

    private String schoolId;
    private String classId;
    private String headTeacherId;
    private String headTeacherName;
    private TextView keywordView;
    private String keyword = "";
    private SchoolTeacherListResult dataListResult;
    private Map<String, SchoolTeacherInfo> selectedItems = new HashMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_select_class_headteacher, null);
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

    @Override
    public void finish() {
        getActivity().setResult(getResultCode(), getResultData());
        super.finish();
    }

    private void init() {
        this.schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
        this.classId = getArguments().getString(Constants.EXTRA_CLASS_ID);
        this.headTeacherId = getArguments().getString(Constants.EXTRA_CLASS_HEADTEACHER_ID);
        this.headTeacherName = getArguments().getString(Constants.EXTRA_CLASS_HEADTEACHER_NAME);

        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.select_headteacher);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_teacher));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadContacts();
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
                    loadContacts();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    String keyword = keywordView.getText().toString();
                    if (TextUtils.isEmpty(keyword)){
                        ToastUtil.showToast(getActivity(),R.string.pls_enter_keyword);
                    }else {
                        loadContacts();
                    }
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearSelectedItems();
                        notifyPickerBar();
                        getCurrAdapterViewHelper().update();
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
            view.setVisibility(View.VISIBLE);
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
                    SchoolTeacherInfo data =
                            (SchoolTeacherInfo) getDataAdapter().getItem(position);
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
                        getThumbnailManager().displayUserIcon(
                                AppSettings.getFileUrl(data.getHeadPic()), imageView);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        if (TextUtils.isEmpty(data.getRealName())){
                            textView.setText(data.getNickName());
                        } else {
                            textView.setText(data.getRealName());
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        imageView.setSelected(isItemSelected(data));
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    SchoolTeacherInfo data = (SchoolTeacherInfo) holder.data;
                    clearSelectedItems();
                    selectItem(data, true);
                    notifyPickerBar();
//                    ImageView imageView = (ImageView) view.findViewById(R.id.item_selector);
//                    if (imageView != null) {
//                        imageView.setSelected(true);
//                    }
                    getCurrAdapterViewHelper().update();
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private boolean isItemSelected(SchoolTeacherInfo data) {
        return data != null && selectedItems.containsKey(data.getMemberId());
    }

    private void selectItem(SchoolTeacherInfo data, boolean selected) {
        if (TextUtils.isEmpty(data.getMemberId())) {
            return;
        }
        if (selected) {
            selectedItems.put(data.getMemberId(), data);
        } else {
            selectedItems.remove(data.getMemberId());
        }
    }

    private boolean hasSelectedItems() {
        return selectedItems.size() > 0;
    }

    private void clearSelectedItems() {
        selectedItems.clear();
    }

    private List<SchoolTeacherInfo> getSelectedItems() {
        if (selectedItems.size() <= 0) {
            return null;
        }
        List<SchoolTeacherInfo> list = new ArrayList();
        for (Map.Entry<String, SchoolTeacherInfo> entry : selectedItems.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    private void loadViews() {
        if (this.dataListResult != null) {
            getCurrAdapterViewHelper().update();
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        loadContacts(this.keywordView.getText().toString());
    }

    private void loadContacts(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("VersionCode", 1);
        params.put("LikeName", keyword);
        params.put("SchoolId", this.schoolId);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<SchoolTeacherListResult>(
                        SchoolTeacherListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                SchoolTeacherListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.CONTACTS_SCHOOL_TEACHER_LIST_URL, params, listener);
    }

    private void updateViews(SchoolTeacherListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<SchoolTeacherInfo> list = result.getModel().getTeacherList();
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
                dataListResult.getModel().setTeacherList(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                dataListResult = result;
            }
            for (SchoolTeacherInfo data : dataListResult.getModel().getTeacherList()) {
                if (data.getMemberId().equals(headTeacherId)) {
                    selectItem(data, true);
                    break;
                }
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

    private void completePickContacts() {
        final List<SchoolTeacherInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(), R.string.pls_select_a_friend_at_least);
            return;
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.str_change_headmaster),
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
                        selectClassHeadTeacher(items.get(0));
                    }
                });
        messageDialog.show();
    }

    private void selectClassHeadTeacher(SchoolTeacherInfo data) {
        if (getUserInfo() == null){
            return;
        }
        ClassHeadTeacherParams classParams = new ClassHeadTeacherParams();
        classParams.setClassId(this.classId);
        classParams.setHeadTeacher(data.getMemberId());
        Map<String, Object> params = new HashMap();
        params.put("VersionCode", "1");
        params.put("OperateType", "Headteacher");
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", this.schoolId);
        params.put("NewModel", classParams);
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
//                    TipsHelper.showToast(getActivity(), R.string.select_class_headteacher_failed);
                    return;
                }
                TipsHelper.showToast(getActivity(), R.string.select_class_headteacher_success);

                notifyChanges((SchoolTeacherInfo) getTarget());
                  //关注/取消关注成功后，向校园空间发广播
                 MySchoolSpaceFragment.sendBrocast(getActivity());
            }
        };
        listener.setTarget(data);
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_MODIFY_CLASS_ATTRIBUTES_URL, params, listener);
    }

    private void notifyChanges(SchoolTeacherInfo data) {
        if (!this.headTeacherId.equals(data.getMemberId())) {
            Bundle args = new Bundle();
            args.putBoolean(Constants.EXTRA_CLASS_HEADTEACHER_CHANGED, true);
            args.putString(Constants.EXTRA_CLASS_ID, this.classId);
            args.putString(Constants.EXTRA_CLASS_HEADTEACHER_ID, data.getMemberId());
            args.putString(Constants.EXTRA_CLASS_HEADTEACHER_NAME, data.getRealName());
            Intent intent = new Intent();
            intent.putExtras(args);
            ContactsSelectClassHeadTeacherFragment.this.setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    public static final class ClassHeadTeacherParams
            extends ContactsClassCategorySelectorFragment.ClassParams {
        private String HeadTeacher;

        public String getHeadTeacher() {
            return HeadTeacher;
        }

        public void setHeadTeacher(String headTeacher) {
            HeadTeacher = headTeacher;
        }
    }

}
