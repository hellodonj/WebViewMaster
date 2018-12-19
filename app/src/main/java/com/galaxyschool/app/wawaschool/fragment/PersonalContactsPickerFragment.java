package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.*;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendListResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalContactsPickerFragment extends ContactsPickerFragment {

    public static final String TAG = PersonalContactsPickerFragment.class.getSimpleName();

    private int pickerMode;
    private boolean isAddStudent;
    private String myClassId;
    private PersonalContactsPickerListener pickerListener;
    private View selectAllView;
    private ContactsFriendListResult dataListResult;

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

    public void setPickerListener(PersonalContactsPickerListener listener) {
        this.pickerListener = listener;
    }

    private void init() {
        this.pickerMode = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_MODE);
        this.isAddStudent = getArguments().getBoolean(ContactsPickerActivity.EXTRA_ADD_STUDENT);
        this.myClassId = getArguments().getString(ContactsPickerActivity.EXTRA_MY_CLASS_ID);

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
            textView.setText(R.string.select_friend);
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
//            this.selectAllView.setSelected(true);
            if(isAddStudent) {
                this.selectAllView.setSelected(false);
            } else {
                this.selectAllView.setSelected(true);
            }
        } else {
            view.setVisibility(View.GONE);
        }

        view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_picker_clear);
            if (textView != null) {
                //默认不可用
                textView.setEnabled(false);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAllContacts(false);
                    }
                });
            }
            textView = (TextView) view.findViewById(R.id.contacts_picker_confirm);
            if (textView != null) {
                //默认不可用
                textView.setEnabled(false);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        completePickContacts();
                        if(!isAddStudent) {
                            completePickContacts();
                        } else {
                            addStudentToMyClass();
                        }
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
                    ContactsFriendInfo data =
                            (ContactsFriendInfo) getDataAdapter().getItem(position);
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
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getNoteName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        holder.selectorView = imageView;
//                        imageView.setSelected(isItemSelected(position));
                        imageView.setSelected(data.isSelected());
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ContactsFriendInfo data = (ContactsFriendInfo)
                            dataAdapter.getData().get(position);
                    MyViewHolder holder = (MyViewHolder) getCurrViewHolder();
                    if (holder != null) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, false);
//                            holder.selectorView.setSelected(false);
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
//                        holder.selectorView.setSelected(true);
                        //处理联系人单选逻辑
                        controlContactsFriendInfoSingleChoiceLogic(data);
                        notifyPickerBar();
                    } else if (pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                        boolean selected = !isItemSelected(position);
                        selectItem(position, selected);
//                        holder.selectorView.setSelected(selected);
                        //设置状态
                        boolean hasSelected = !data.isSelected();
                        controlSelectStatus(data,hasSelected);
                        if (selectAllView != null) {
//                            selectAllView.setSelected(isAllItemsSelected());
                            selectAllView.setSelected(hasSelectedAllChildren());
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

    /**
     * 处理联系人单选逻辑
     * @param data
     */
    private void controlContactsFriendInfoSingleChoiceLogic(ContactsFriendInfo data) {
        if (data == null){
            return;
        }
        //设置单选状态
        List<ContactsFriendInfo> contactsFriendInfoList = getCurrAdapterViewHelper().getData();
        if (contactsFriendInfoList != null && contactsFriendInfoList.size() > 0){
            for (ContactsFriendInfo info : contactsFriendInfoList){
                if (info != null){
                    //单选状态
                    if (!TextUtils.isEmpty(info.getId())
                            && !TextUtils.isEmpty(data.getId())
                            && info.getId().equals(data.getId())){
                        //操作的那个,单选和取消。
                        controlSelectStatus(data, !data.isSelected());
                    }else {
                        //其余未操作项，均设置为未选中。
                        controlSelectStatus(info, false);
                    }
                }
            }
        }
    }

    /**
     * 判断有没有完全选中
     * @return
     */
    private boolean hasSelectedAllChildren(){
        boolean selectedAll = true;
        //设置全选/取消全选状态
        List<ContactsFriendInfo> infoList = getCurrAdapterViewHelper().getData();
        if (infoList != null && infoList.size() > 0){
            for (ContactsFriendInfo info : infoList){
                if (info != null){
                    if (!info.isSelected()){
                        //一假为假
                        selectedAll = false;
                        break;
                    }
                }
            }
        }
        return selectedAll;
    }

    /**
     * 处理选中状态
     * @param data
     * @param selected
     */
    private void controlSelectStatus(ContactsFriendInfo data, boolean selected) {
        if (data != null){
            //设置选中状态
            data.setSelected(selected);
            if (selected){
                RefreshUtil.getInstance().addId(data.getId());
            }else {
                RefreshUtil.getInstance().removeId(data.getId());
            }
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
        params.put("MemberId", getUserInfo().getMemberId());
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsFriendListResult>
                        (ContactsFriendListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ContactsFriendListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateViews(result);
                    }
                };
        postRequest(ServerUrl.CONTACTS_FRIEND_LIST_URL, params, listener);
    }

    private void updateViews(ContactsFriendListResult result) {
        List<ContactsFriendInfo> list = result.getModel().getPersonalMailListList();
        if (list == null && list.size() <= 0) {
            return;
        }
        //恢复数据
        RefreshUtil.getInstance().refresh(list);
        getCurrAdapterViewHelper().setData(list);

        if (this.selectAllView != null && getCurrAdapterViewHelper().hasData()) {
            //取消默认选择
//            selectAllContacts(this.selectAllView.isSelected());
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }

        dataListResult = result;
    }

    private void addStudentToMyClass() {
        List<ContactsFriendInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_friend_at_least);
            return;
        }

        List<String> studentIds = new ArrayList<String>();
        for (ContactsFriendInfo obj : items) {
            if(obj != null && !TextUtils.isEmpty(obj.getMemberId())) {
                studentIds.add(obj.getMemberId());
            }
        }
        addStudentToMyClass(myClassId, studentIds);
    }

    private void notifyPickerBar() {
//        notifyPickerBar(hasSelectedItems());
        notifyPickerBar(hasContactSelected());
    }

    /**
     * 是否选中了联系人
     * @return
     */
    private boolean hasContactSelected(){
        List list = getSelectedContacts();
        return list != null && list.size() > 0;
    }

    /**
     * 获得选中的联系人
     * @return
     */
    private List getSelectedContacts() {
        //设置全选/取消全选状态
        List<ContactsFriendInfo> contactsFriendInfoList = getCurrAdapterViewHelper().getData();
        if (contactsFriendInfoList != null && contactsFriendInfoList.size() > 0){
            List<ContactsFriendInfo> resultList = new ArrayList<>();
            for (ContactsFriendInfo info : contactsFriendInfoList){
                if (info != null){
                    if (info.isSelected()){
                        //选中的数据
                        resultList.add(info);
                    }
                }
            }
            return resultList;
        }
        return null;
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
        //设置全选/取消全选状态
        List<ContactsFriendInfo> infoList = getCurrAdapterViewHelper().getData();
        if (infoList != null && infoList.size() > 0){
            for (ContactsFriendInfo info : infoList){
                if (info != null){
                    //设置选中状态
                    controlSelectStatus(info,selected);
                }
            }
        }
        notifyPickerBar();
        getCurrAdapterViewHelper().update();
    }

    /**
     * 得到选中的child条目列表
     * @return
     */
    private List<ContactsFriendInfo> getSelectedFriendInfoList(){
        //设置全选/取消全选状态
        List<ContactsFriendInfo> dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0){
            List<ContactsFriendInfo> resultList = new ArrayList<>();
            for (ContactsFriendInfo friendInfo : dataList){
                if (friendInfo != null){
                    if (friendInfo.isSelected()){
                        //选择条目
                        resultList.add(friendInfo);
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    private void completePickContacts() {
//        List<ContactsFriendInfo> items = getSelectedItems();
        List<ContactsFriendInfo> items = getSelectedFriendInfoList();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_friend_at_least);
            return;
        }

        List<ContactItem> result = new ArrayList<ContactItem>();
        ContactItem item = null;
        String name = null;
        for (ContactsFriendInfo obj : items) {
            item = new ContactItem();
            item.setId(obj.getMemberId());
            name = obj.getNoteName();
            if (TextUtils.isEmpty(name)) {
                name = obj.getNickname();
            }
            item.setName(name);
            item.setType(ContactItem.CONTACT_TYPE_PERSON);
            item.setIcon(AppSettings.getFileUrl(obj.getHeadPicUrl()));
            item.setHxId("hx" + obj.getMemberId());
            result.add(item);
        }

        if (this.pickerListener != null) {
            this.pickerListener.onPersonalContactsPicked(result);
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
