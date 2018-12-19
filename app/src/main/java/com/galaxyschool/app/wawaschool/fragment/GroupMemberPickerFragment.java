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
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMemberPickerFragment extends ContactsPickerFragment {

    public static final String TAG = GroupMemberPickerFragment.class.getSimpleName();

    private int pickerMode;
    private int memberType;
    private boolean isAddStudent;
    private String myClassId;
    private String groupId;
    private String groupName;
    private String schoolId, classId;
    private GroupMemberPickerListener pickerListener;
    private View selectAllView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_picker, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initViews();
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
    public boolean onBackPressed() {
        finish();
        return true;
    }

    @Override
    public void finish() {
//        getActivity().finish();
//        getParentFragment().getChildFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setPickerListener(GroupMemberPickerListener listener) {
        this.pickerListener = listener;
    }

    private void init() {
        this.pickerMode = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_MODE);
        this.memberType = getArguments().getInt(ContactsPickerActivity.EXTRA_MEMBER_TYPE);
        this.groupId = getArguments().getString(ContactsPickerActivity.EXTRA_GROUP_ID);
        this.groupName = getArguments().getString(ContactsPickerActivity.EXTRA_GROUP_NAME);
        this.schoolId = getArguments().getString(ContactsPickerActivity.EXTRA_SCHOOL_ID);
        this.classId = getArguments().getString(ContactsPickerActivity.EXTRA_CLASS_ID);
        this.isAddStudent = getArguments().getBoolean(ContactsPickerActivity.EXTRA_ADD_STUDENT);
        this.myClassId = getArguments().getString(ContactsPickerActivity.EXTRA_MY_CLASS_ID);

        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (this.memberType == ContactsPickerActivity.MEMBER_TYPE_TEACHER) {
                textView.setText(R.string.select_teacher);
            } else if (this.memberType == ContactsPickerActivity.MEMBER_TYPE_STUDENT) {
                textView.setText(R.string.select_student);
            } else if (this.memberType == ContactsPickerActivity.MEMBER_TYPE_PARENT) {
                textView.setText(R.string.select_parent);
            } else {
//                textView.setText(R.string.select_receiver);
                if(isAddStudent) {
                    textView.setText(R.string.add_student);
                } else {
                    textView.setText(R.string.select_receiver);
                }
            }
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

        View view = findViewById(R.id.contacts_select_all);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllContacts(!selectAllView.isSelected());
            }
        });
        if (this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
            view.setVisibility(View.VISIBLE);
            this.selectAllView = findViewById(R.id.contacts_select_all_icon);
            this.selectAllView.setSelected(false);
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
                    ContactsClassMemberInfo data =
                            (ContactsClassMemberInfo) getDataAdapter().getItem(position);
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
                        imageView.setSelected(data.isSelect());
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ContactsClassMemberInfo data = (ContactsClassMemberInfo)
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
                        controlContactsClassMemberInfoSingleChoiceLogic(data);
                        notifyPickerBar();
                    } else if (pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                        boolean selected = !isItemSelected(position);
                        selectItem(position, selected);
//                        holder.selectorView.setSelected(selected);
                        //处理选中痕迹
                        boolean hasSelected = !data.isSelect();
                        controlSelectStatus(data,hasSelected);
                        if (selectAllView != null) {
                            //设置全选
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
     * 控制单选逻辑
     * @param data
     */
    private void controlContactsClassMemberInfoSingleChoiceLogic(ContactsClassMemberInfo data) {
        if (data == null){
            return;
        }
        //设置单选状态
        List<ContactsClassMemberInfo> list = getCurrAdapterViewHelper().getData();
        if (list != null && list.size() > 0){
            for (ContactsClassMemberInfo info : list){
                if (info != null){
                    //单选状态
                    if (!TextUtils.isEmpty(info.getId())
                            && !TextUtils.isEmpty(data.getId())
                            && info.getId().equals(data.getId())){
                        //操作的那个,单选和取消。
                        controlSelectStatus(data, !data.isSelect());
                    }else {
                        //其余未操作项，均设置为未选中。
                        controlSelectStatus(info, false);
                    }
                }
            }
        }
    }

    /**
     * 处理选中状态
     * @param data
     * @param selected
     */
    private void controlSelectStatus(ContactsClassMemberInfo data, boolean selected) {
        if (data != null){
            //设置选中状态
            data.setIsSelect(selected);
            if (selected){
                RefreshUtil.getInstance().addId(data.getId());
            }else {
                RefreshUtil.getInstance().removeId(data.getId());
            }
        }
    }
    private void addStudentToMyClass() {
        List<ContactsClassMemberInfo> items = getSelectedItems();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_friend_at_least);
            return;
        }


        List<String> studentIds = new ArrayList<String>();
        for (ContactsClassMemberInfo item : items) {
            if(item != null && !TextUtils.isEmpty(item.getMemberId())) {
                studentIds.add(item.getMemberId());
            }
        }

        addStudentToMyClass(myClassId, studentIds);
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
        params.put("Id", this.groupId);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassMemberListResult>(
                        ContactsClassMemberListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsClassMemberListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_CLASS_MEMBER_LIST_URL, params, listener);
    }

    private void updateViews(ContactsClassMemberListResult result) {
        List<ContactsClassMemberInfo> list = result.getModel().getClassMailListDetailList();
        if (list == null && list.size() <= 0) {
            return;
        }

        UserInfo userInfo = getUserInfo();
        List<ContactsClassMemberInfo> memberList = null;
        boolean myselfFound = false;
        if (this.memberType == ContactsPickerActivity.MEMBER_TYPE_TEACHER
                || this.memberType == ContactsPickerActivity.MEMBER_TYPE_STUDENT
                || this.memberType == ContactsPickerActivity.MEMBER_TYPE_PARENT) {
            memberList = new ArrayList<ContactsClassMemberInfo>();
            for (ContactsClassMemberInfo obj : list) {
                if (userInfo != null
                        && obj.getMemberId().equals(userInfo.getMemberId())) {
                    myselfFound = true;
                    continue;
                }
                if (this.memberType == obj.getRole()) {
                    memberList.add(obj);
                }
            }
        } else {
            for (ContactsClassMemberInfo obj : list) {
                if (userInfo != null
                        && obj.getMemberId().equals(userInfo.getMemberId())) {
                    myselfFound = true;
                    list.remove(obj);
                    break;
                }
            }
            memberList = list;
        }

        if (memberList != null) {
            if (!myselfFound) {
                memberList.clear();
            }
            //恢复状态
            RefreshUtil.getInstance().refresh(memberList);
            getCurrAdapterViewHelper().setData((List) memberList);
        }

        if (this.selectAllView != null && getCurrAdapterViewHelper().hasData()) {
            //取消默认选项
//            selectAllContacts(this.selectAllView.isSelected());
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }
    }

    private void notifyPickerBar() {
//        notifyPickerBar(hasSelectedItems());
        notifyPickerBar(hasGroupMemberSelected());
    }

    /**
     * 是否选中了条目
     * @return
     */
    private boolean hasGroupMemberSelected() {
        List list = getSelectedGroupMembers();
        return list != null && list.size() > 0;
    }

    /**
     * 获取选中的条目
     * @return
     */
    private List getSelectedGroupMembers() {
        //设置全选/取消全选状态
        List<ContactsClassMemberInfo> list = getCurrAdapterViewHelper().getData();
        if (list != null && list.size() > 0){
            List<ContactsClassMemberInfo> resultList = new ArrayList<>();
            for (ContactsClassMemberInfo info : list){
                if (info != null){
                    if (info.isSelect()){
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

    /**
     * 判断有没有完全选中
     * @return
     */
    private boolean hasSelectedAllChildren(){
        boolean selectedAll = true;
        //设置全选/取消全选状态
        List<ContactsClassMemberInfo> infoList = getCurrAdapterViewHelper().getData();
        if (infoList != null && infoList.size() > 0){
            for (ContactsClassMemberInfo info : infoList){
                if (info != null){
                    if (!info.isSelect()){
                        //一假为假
                        selectedAll = false;
                        break;
                    }
                }
            }
        }
        return selectedAll;
    }

    private void selectAllContacts(boolean selected) {
        ImageView imageView = (ImageView) findViewById(
                R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setSelected(selected);
        }
        selectAllItems(selected);
        //设置全选/取消全选状态
        List<ContactsClassMemberInfo> infoList = getCurrAdapterViewHelper().getData();
        if (infoList != null && infoList.size() > 0){
            for (ContactsClassMemberInfo info : infoList){
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
    private List<ContactsClassMemberInfo> getSelectedMemberInfoList(){
        //设置全选/取消全选状态
        List<ContactsClassMemberInfo> dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0){
            List<ContactsClassMemberInfo> resultList = new ArrayList<>();
            for (ContactsClassMemberInfo memberInfo : dataList){
                if (memberInfo != null){
                    if (memberInfo.isSelect()){
                        //选择条目
                        resultList.add(memberInfo);
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    private void completePickContacts() {
//        List<ContactsClassMemberInfo> items = getSelectedItems();
        List<ContactsClassMemberInfo> items = getSelectedMemberInfoList();
        if (items == null || items.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    R.string.pls_select_a_student_at_least);
            return;
        }

        if (this.pickerListener != null) {
            for (ContactsClassMemberInfo obj : items) {
                obj.setSchoolId(this.schoolId);
                obj.setClassId(this.classId);
            }

            ArrayList<ContactItem> result = new ArrayList<ContactItem>();
            ContactItem item = null;
            String name = null;
            for (ContactsClassMemberInfo obj : items) {
                item = new ContactItem();
                item.setId(obj.getMemberId());
                name = obj.getRealName();
                if (TextUtils.isEmpty(name)) {
                    name = obj.getNoteName();
                }
                if (TextUtils.isEmpty(name)) {
                    name = obj.getNickname();
                }
                item.setName(name);
                item.setIcon(AppSettings.getFileUrl(obj.getHeadPicUrl()));
                item.setType(ContactItem.CONTACT_TYPE_PERSON);
                item.setHxId("hx" + obj.getMemberId());
                item.setSchoolId(obj.getSchoolId());
                item.setClassId(obj.getClassId());
                result.add(item);
            }

            this.pickerListener.onGroupMemberPicked(result);
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
