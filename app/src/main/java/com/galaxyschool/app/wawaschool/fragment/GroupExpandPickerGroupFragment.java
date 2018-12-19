package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.GroupMemberPickerListener;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener.GroupPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolClassDetail;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/11/27 15:45
  * 描    述:布置任务--选择小组界面
  * 修订历史：
  * ================================================
  */
public class GroupExpandPickerGroupFragment extends ContactsExpandPickerFragment {

    public static final String TAG = GroupExpandPickerGroupFragment.class.getSimpleName();

    private int pickerType;
    private int pickerMode;
    private int groupType;
    private int roleType;
    private GroupPickerListener pickerListener;
    private View selectAllView;
    //一下来自空中课堂的设置项
    private boolean tempData;
    private String classId;
    private List<SchoolClassDetail> schoolClasses;
    private List<ContactsSchoolInfo> contactsSchoolInfos;
    //Fragment的View加载完毕的标记
    private boolean isViewCreated;
    //Fragment对用户可见的标记
    private boolean isUIVisible;
    private boolean isOnlineClass;
     private boolean multiSelection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_expand_picker, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        isViewCreated = true;
        lazyLoad();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }
    }

    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
            loadDataLazy();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;
        }
    }
    public void loadDataLazy() {
        //子类去实现
        loadViews();
    }


    public void setPickerListener(GroupPickerListener listener) {
        this.pickerListener = listener;
    }

     public void setMultiSelection(boolean multiSelection){
         this.multiSelection = multiSelection;
     }

    private void init() {
        this.pickerType = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_TYPE);
        this.pickerMode = getArguments().getInt(ContactsPickerActivity.EXTRA_PICKER_MODE);
        this.groupType = getArguments().getInt(ContactsPickerActivity.EXTRA_GROUP_TYPE);
        this.roleType = getArguments().getInt(ContactsPickerActivity.EXTRA_ROLE_TYPE);
        //这个是从创建课堂时进入的来增加学校班级
        this.tempData=getArguments().getBoolean(ActivityUtils.EXTRA_TEMP_DATA,false);
        if (tempData){
            classId=getArguments().getString(AirClassroomFragment.Constants
                    .EXTRA_CONTACTS_CLASS_ID);
            schoolClasses= getArguments().getParcelableArrayList("schoolClass");
            TextView bottomConfirm= (TextView) findViewById(R.id.contacts_picker_confirm);
            if (bottomConfirm!=null){
                bottomConfirm.setText(getString(R.string.noc_sure));
            }
        }
        this.isOnlineClass = getArguments().getBoolean(ContactsPickerActivity.EXTRA_IS_ONLINE_CLASS);
        initViews();
    }

    private void initViews() {
        View view = findViewById(R.id.contacts_header_layout);
        if (view != null) {
            if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                //如果来空中课堂显示 头部布局
                if (tempData){
                    view.setVisibility(View.VISIBLE);
                }else {
                    view.setVisibility(View.GONE);
                }
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (tempData){
                textView.setText(R.string.add_publish_object_no_point);
            }else {
                //正常的发送
                if (this.groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                    textView.setText(R.string.select_school);
                } else {
                    textView.setText(R.string.select_class);
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
            if ((this.pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0) {
                if (getArguments().getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                    textView.setVisibility(View.INVISIBLE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                }
            }
        }

        view = findViewById(R.id.contacts_select_all);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllContacts(!selectAllView.isSelected());
            }
        });
        //初始化全选按钮
        this.selectAllView = findViewById(R.id.contacts_select_all_icon);
        if ((this.pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0
                && this.pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
            //来自空中课堂 不显示全选
            if (tempData){
                view.setVisibility(View.GONE);
            }else {
                view.setVisibility(View.VISIBLE);
            }
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
//            if (textView != null) {
//                textView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        completePickContacts();
//                    }
//                });
//            }
            if(textView!=null){
                //默认不可用
                textView.setEnabled(false);
                textView.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View view) {
                        completePickContacts();
                    }
                });
            }
            if ((this.pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0
                    && getArguments().getBoolean(
                    ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
                if (multiSelection){
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                if (tempData){
                    view.setVisibility(View.VISIBLE);
                }else {
                    view.setVisibility(View.GONE);
                }
            }
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        if (pullToRefreshView!=null){
            setPullToRefreshView(pullToRefreshView);
        }
        ExpandableListView listView = (ExpandableListView) findViewById(
                R.id.contacts_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(),
                    null, R.layout.contacts_expand_list_group_item_mixed,
                    R.layout.contacts_expand_list_child_item_with_selector) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    ContactsSchoolInfo data = (ContactsSchoolInfo) getGroup(groupPosition);
                    if (data.getClassMailList() != null) {
                        return data.getClassMailList().size();
                    }
                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    ContactsSchoolInfo data = (ContactsSchoolInfo) getGroup(groupPosition);
                    return data.getClassMailList().get(childPosition);
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                            boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    ContactsClassInfo data = (ContactsClassInfo) getChild(
                            groupPosition, childPosition);
                    if (data == null) {
                        return view;
                    }

                    MyViewHolder holder = (MyViewHolder) view.getTag();
//                    if (holder == null) {
                        holder = new MyViewHolder();
//                    }
                    holder.position = (int) getChildId(groupPosition, childPosition);
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_class_icon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getClassMailName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        holder.selectorView = imageView;
                        //是否选中
//                        imageView.setSelected(isItemSelected(holder.position));
                        imageView.setSelected(data.isSelected());
                        if ((pickerType & ContactsPickerActivity.PICKER_TYPE_MEMBER) != 0) {
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                            View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded,
                            convertView, parent);
                    ContactsSchoolInfo data = (ContactsSchoolInfo) getGroup(groupPosition);
                    if (data == null) {
                        return view;
                    }

                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.position = (int) getGroupId(groupPosition);
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        //显示学校logo
//                        if (groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                            getThumbnailManager().displayUserIconWithDefault(
                                    AppSettings.getFileUrl(data.getLogoUrl()), imageView,
                                    R.drawable.default_school_icon);
                            imageView.setVisibility(View.VISIBLE);
//                        } else {
//                            imageView.setVisibility(View.GONE);
//                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                        if (groupType != ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                            textView.setTextColor(getResources().getColor(
                                    R.color.text_green));
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        imageView.setImageResource(isExpanded ?
                                R.drawable.list_exp_up : R.drawable.list_exp_down);
                        if (groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                            imageView.setVisibility(View.GONE);
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        holder.selectorView = imageView;
//                        imageView.setSelected(isItemSelected(holder.position));
                        imageView.setSelected(data.isSelected());
                        if (groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                            imageView.setVisibility(View.VISIBLE);
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                    }

                    view.setTag(holder);
                    return view;
                }
            };
            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    listView, dataAdapter) {
                @Override
                public void loadData() {
                    loadGroups();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                            int groupPosition, int childPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) getCurrViewHolder();
                    //获取条目对象
                    ContactsClassInfo data = (ContactsClassInfo)
                            dataAdapter.getChild(groupPosition,childPosition);
                    if (holder != null) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, false);
//                            holder.selectorView.setSelected(false);
//                            holder.selectorView.invalidate();
                            notifyPickerBar();
                        }
                    }

                    holder = (MyViewHolder) v.getTag();
                    if (holder == null) {
                        return true;
                    }
                    if ((pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, true);
                            setCurrViewHolder(holder);
//                            holder.selectorView.setSelected(true);
                            //处理班级单选逻辑
                            controlContactsClassInfoSingleChoiceLogic(data);
                            notifyPickerBar();
                        } else if (pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                          /*  //空中课堂的设置项 选择的班级自己默认的班级不可以操作
                            ContactsClassInfo contactsClassInfo= (ContactsClassInfo) holder.data;
                            if (contactsClassInfo!=null&&contactsClassInfo.getClassId().equals(classId)){
                                return true;
                            }*/

                            boolean selected = !isItemSelected(holder.position);
                            selectItem(holder.position, selected);
//                            holder.selectorView.setSelected(selected);
                            //处理选中痕迹
                            boolean hasSelected = !data.isSelected();
                            controlContactsClassInfoSelectStatus(data,hasSelected);
                            if (selectAllView != null) {
                                //设置全选
//                                selectAllView.setSelected(isAllItemsSelected());
                                selectAllView.setSelected(hasSelectedAllChildren());
                            }
                            notifyPickerBar();
                        }
                    } else if ((pickerType & ContactsPickerActivity.PICKER_TYPE_MEMBER) != 0) {
                        enterMemberContacts((ContactsClassInfo) holder.data);
                    }
//                    holder.selectorView.invalidate();
                    getDataAdapter().notifyDataSetChanged();
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                            int groupPosition, long id) {
                    ContactsSchoolInfo data = (ContactsSchoolInfo)
                            dataAdapter.getGroup(groupPosition);
                    if (groupType != ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                        return false;
                    }
                    MyViewHolder holder = (MyViewHolder) getCurrViewHolder();
                    if (holder != null) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, false);
//                            holder.selectorView.setSelected(false);
//                            holder.selectorView.invalidate();
                            notifyPickerBar();
                        }
                    }

                    holder = (MyViewHolder) v.getTag();
                    if (holder == null) {
                        return true;
                    }
                    if ((pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0) {
                        if (pickerMode == ContactsPickerActivity.PICKER_MODE_SINGLE) {
                            selectItem(holder.position, true);
                            setCurrViewHolder(holder);
//                            holder.selectorView.setSelected(true);
                            //处理学校单选逻辑
                            controlContactsSchoolInfoSingleChoiceLogic(data);
                            notifyPickerBar();
                        } else if (pickerMode == ContactsPickerActivity.PICKER_MODE_MULTIPLE) {
                            boolean selected = !isItemSelected(holder.position);
                            selectItem(holder.position, selected);
//                            holder.selectorView.setSelected(selected);
                            //处理选中痕迹
                            boolean hasSelected = !data.isSelected();
                            controlContactsSchoolInfoSelectStatus(data,hasSelected);
                            if (selectAllView != null) {
                                //设置全选
//                                selectAllView.setSelected(isAllItemsSelected());
                                selectAllView.setSelected(hasSelectedAllGroups());
                            }
                            notifyPickerBar();
                        }
                    }
//                    holder.selectorView.invalidate();
                    getDataAdapter().notifyDataSetChanged();
                    return true;
                }
            };
            listViewHelper.setData(null);

            setCurrListViewHelper(listView, listViewHelper);
        }
    }

    /**
     * 处理学校单选逻辑
     * @param data
     */
    private void controlContactsSchoolInfoSingleChoiceLogic(ContactsSchoolInfo data) {
        if (data == null){
            return;
        }
        //设置单选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    //单选状态
                    if (!TextUtils.isEmpty(schoolInfo.getSchoolId())
                            && !TextUtils.isEmpty(data.getSchoolId())
                            && schoolInfo.getSchoolId().equals(data.getSchoolId())){
                        //操作的那个,单选和取消。
                        controlContactsSchoolInfoSelectStatus(data,
                                !data.isSelected());
                    }else {
                        //其余未操作项，均设置为未选中。
                        controlContactsSchoolInfoSelectStatus(schoolInfo, false);
                    }
                }
            }
        }
    }

    /**
     * 处理班级单选逻辑
     * @param data
     */
    private void controlContactsClassInfoSingleChoiceLogic(ContactsClassInfo data) {
        if (data == null){
            return;
        }
        //设置单选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    List<ContactsClassInfo> childList = schoolInfo.getClassMailList();
                    if (childList != null && childList.size() > 0){
                        for (ContactsClassInfo classInfo : childList){
                            if (classInfo != null){
                                //单选状态
                                if (!TextUtils.isEmpty(classInfo.getId())
                                        && !TextUtils.isEmpty(data.getId())
                                        && classInfo.getId().equals(data.getId())){
                                    //操作的那个,单选和取消。
                                    controlContactsClassInfoSelectStatus(data,
                                            !data.isSelected());
                                }else {
                                    //其余未操作项，均设置为未选中。
                                    controlContactsClassInfoSelectStatus(classInfo, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 处理child选中状态
     * @param data
     * @param selected
     */
    private void controlContactsClassInfoSelectStatus(ContactsClassInfo data, boolean selected) {
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

    /**
     * 处理group选中状态
     * @param data
     * @param selected
     */
    private void controlContactsSchoolInfoSelectStatus(ContactsSchoolInfo data, boolean selected) {
        if (data != null){
            //设置选中状态
            data.setSelected(selected);
            if (selected){
                RefreshUtil.getInstance().addId(data.getSchoolId());
            }else {
                RefreshUtil.getInstance().removeId(data.getSchoolId());
            }
        }
    }

    private void loadViews() {
        if (getCurrListViewHelper().hasData()) {
            getCurrListViewHelper().update();
        } else {
            loadGroups();
        }
    }

    private void loadGroups() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new ArrayMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        int[] ints = new int[]{0};
        params.put("RoleList",ints);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsSchoolListResult>(
                        ContactsSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                CheckMarkInfo result = JSONObject.parseObject(jsonString,
                        CheckMarkInfo.class);
                if (result.getErrorCode() != 0 || result.getModel() == null) {
                    return;

                }
                List<CheckMarkInfo.ModelBean> modelBeanList = result.getModel();
                List<ContactsSchoolInfo> list = new ArrayList<>();
                if (modelBeanList != null) {
                    for (CheckMarkInfo.ModelBean bean : modelBeanList) {
                        ContactsSchoolInfo info = new ContactsSchoolInfo();
                        info.setLogoUrl(bean.getLogoUrl());
                        info.setSchoolId(bean.getSchoolId());
                        info.setSchoolName(bean.getSchoolName());
                        info.setIsOnlineSchool(bean.isOnlineSchool());
                        List<ContactsClassInfo> ClassMailList = new ArrayList<>();
                        List<CheckMarkInfo.ModelBean> studyGroupList = bean.getStudyGroupList();
                        if (studyGroupList != null) {
                            for (CheckMarkInfo.ModelBean modelBean : studyGroupList) {
                                ContactsClassInfo classInfo = new ContactsClassInfo();
                                classInfo.setSmallGroup(modelBean.getGroupId()+"");
                                classInfo.setHeadPicUrl(modelBean.getClassHeadPicUrl());
                                classInfo.setLQ_SchoolId(bean.getSchoolId());
                                classInfo.setClassMailName(modelBean.getClassName()+modelBean.getGroupName());
                                ClassMailList.add(classInfo);

                            }
                        }

                        info.setClassMailList(ClassMailList);
                        if (isOnlineClass){
                            if (bean.isOnlineSchool()){
                                list.add(info);
                            }
                        } else {
                            list.add(info);
                        }
                    }
                }
                updateViews(list);
            }
        };
        postRequest(ServerUrl.GET_LOADTEACHERSTUDYGROUPS, params, listener);
    }

    private void updateViews(List<ContactsSchoolInfo> list) {
        Utils.removeOnlineContactsSchoolInfo(list);
        contactsSchoolInfos = list;
        if (list == null && list.size() <= 0) {
            return;
        }

        List<ContactsSchoolInfo> groupList = new ArrayList<ContactsSchoolInfo>();
        for (ContactsSchoolInfo school : list) {

            if (school.getClassMailList() == null || school.getClassMailList().size() <= 0) {
                continue;
            }

            if (school.getClassMailList().size() > 0) {
                groupList.add(school);
            }
        }

        if (groupList != null) {
            //恢复选择痕迹
            RefreshUtil.getInstance().refresh(groupList);
            getCurrListViewHelper().setData((List) groupList);
            if (getCurrListViewHelper().hasData()) {
                getCurrListView().expandGroup(0);
            }
        }
        if (this.selectAllView != null && getCurrListViewHelper().hasData()) {
            boolean selected = this.selectAllView.isSelected();
//            selectAllItems(selected);
            groupList = getCurrListViewHelper().getData();
            if (this.groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                for (int i = 0; i < groupList.size(); i++) {
                    addItem(i, selected);
                }
            } else {
                for (int i = 0; i < groupList.size(); i++) {
                    List<ContactsClassInfo> classes = groupList.get(i).getClassMailList();
                    //恢复选择痕迹
                    RefreshUtil.getInstance().refresh(classes);
                    if (classes != null && classes.size() > 0) {
                        for (int j = 0; j < classes.size(); j++) {
                            addItem((int) getCurrListViewHelper()
                                    .getDataAdapter().getChildId(i, j), selected);
                        }
                    }
                }
            }
//            notifyPickerBar(true);
            //默认
            notifyPickerBar();
        } else {
            if (this.selectAllView != null) {
                this.selectAllView.setSelected(false);
            }
            notifyPickerBar();
        }
        //来自空中课堂的设置项
        if (tempData) {
            if (groupList != null && groupList.size() > 0) {
                for (int i = 0; i < groupList.size(); i++) {
                    List<ContactsClassInfo> classes = groupList.get(i).getClassMailList();
                    //恢复选择痕迹
                    RefreshUtil.getInstance().refresh(classes);
                    if (classes != null && classes.size() > 0) {
                        for (int j = 0; j < classes.size(); j++) {
                            for(int k=0;k<schoolClasses.size();k++){
                                if (schoolClasses.get(k).getClassId().equals(classes.get(j).getClassId())) {
                                    addItem((int) getCurrListViewHelper().getDataAdapter().getChildId(i, j), true);
                                    //设置添加过的班级为选中状态
                                    controlContactsClassInfoSelectStatus(classes.get(j),true);
                                }
                            }
                        }
                    }
                }
                notifyPickerBar();
            }
        }
    }

    private void notifyPickerBar() {
        if (this.groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
//            notifyPickerBar(hasSelectedGroupItems());
            notifyPickerBar(hasGroupSelected());
        } else {
//            notifyPickerBar(hasSelectedItems());
            notifyPickerBar(hasChildrenSelected());
        }
    }

    private void notifyPickerBar(boolean selected) {
        View view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            TextView textView = (TextView) view.findViewById(
                    R.id.contacts_picker_clear);
            if (textView != null) {
                if (tempData){
                    if (getSelectedChildren() != null && getSelectedChildren().size()==1){
                        textView.setEnabled(false);
                    }else {
                        textView.setEnabled(selected);
                    }
                }else {
                    textView.setEnabled(selected);
                }
            }
            textView = (TextView) view.findViewById(
                    R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setEnabled(selected);
            }
        }
        if (multiSelection) {
           PickerClassAndGroupFragment groupFragment = (PickerClassAndGroupFragment) GroupExpandPickerGroupFragment.this
                   .getParentFragment();
           if (groupFragment != null){
               groupFragment.setItemSelected(0,selected);
           }
        }
    }

    /**
     * 判断child有没有完全选中
     * @return
     */
    private boolean hasSelectedAllChildren(){
        boolean selectedAll = true;
        //设置全选/取消全选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    List<ContactsClassInfo> childList = schoolInfo.getClassMailList();
                    if (childList != null && childList.size() > 0){
                        for (ContactsClassInfo classInfo : childList){
                            if (classInfo != null){
                                if (!classInfo.isSelected()){
                                    //一假为假
                                    selectedAll = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return selectedAll;
    }

    /**
     * 判断child有没有完全选中
     * @return
     */
    private boolean hasSelectedAllGroups(){
        boolean selectedAll = true;
        //设置全选/取消全选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    if (!schoolInfo.isSelected()){
                        //一假为假
                        selectedAll = false;
                        break;
                    }
                }
            }
        }
        return selectedAll;
    }
    public void selectAllContacts(boolean selected) {
        ImageView imageView = (ImageView) findViewById(
                R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setSelected(selected);
        }
        selectAllItems(selected);
        //设置全选/取消全选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    if (groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
                        //选学校
                        //设置选中状态
                        controlContactsSchoolInfoSelectStatus(schoolInfo,selected);
                    }else if (groupType == ContactsPickerActivity.GROUP_TYPE_CLASS) {
                        //选班级
                        List<ContactsClassInfo> childList = schoolInfo.getClassMailList();
                        if (childList != null && childList.size() > 0) {
                            for (ContactsClassInfo classInfo : childList) {
                                if (classInfo != null) {
                                    //设置选中状态
                                    controlContactsClassInfoSelectStatus(classInfo, selected);
                                }
                            }
                        }
                    }
                }
            }
        }
        //如果来自空中课堂
        if (tempData) {
            List<ContactsSchoolInfo> data=getCurrListViewHelper().getData();
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    List<ContactsClassInfo> classes = data.get(i).getClassMailList();
                    if (classes != null && classes.size() > 0) {
                        for (int j = 0; j < classes.size(); j++) {
                                if (classId.equals(classes.get(j).getClassId())) {
                                    addItem((int) getCurrListViewHelper().getDataAdapter().getChildId(i, j), true);
                                    //设置选中状态:空中课堂需要保留状态
                                    controlContactsClassInfoSelectStatus(classes.get(j), true);
                                }
                        }
                    }
                }
            }
        }
        notifyPickerBar();
        getCurrListViewHelper().update();
    }

    /**
     * 得到选中的group条目列表
     * @return
     */
    private List<ContactsSchoolInfo> getSelectedGroups(){
        //设置全选/取消全选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            List<ContactsSchoolInfo> resultList = new ArrayList<>();
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    if (schoolInfo.isSelected()){
                        //选中的数据
                        resultList.add(schoolInfo);
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    /**
     * 得到选中的child条目列表
     * @return
     */
    private List<ContactsClassInfo> getSelectedChildren(){
        //设置全选/取消全选状态
        List<ContactsSchoolInfo> groupList = getCurrListViewHelper().getData();
        if (groupList != null && groupList.size() > 0){
            List<ContactsClassInfo> resultList = new ArrayList<>();
            for (ContactsSchoolInfo schoolInfo : groupList){
                if (schoolInfo != null){
                    List<ContactsClassInfo> childList = schoolInfo.getClassMailList();
                    if (childList != null && childList.size() > 0){
                        for (ContactsClassInfo classInfo : childList){
                            if (classInfo != null){
                                if (classInfo.isSelected()){
                                    //选中的数据
                                    resultList.add(classInfo);
                                }
                            }
                        }
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    /**
     * 是否选中了班级
     * @return
     */
    private boolean hasChildrenSelected(){
        List list = getSelectedChildren();
        return list != null && list.size() > 0;
    }

    /**
     * 是否选中了学校
     * @return
     */
    private boolean hasGroupSelected(){
        List list = getSelectedGroups();
        return list != null && list.size() > 0;
    }

    protected void completePickContacts() {
        List items = null;
        if (this.groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
//            items = getSelectedGroupItems();
            items = getSelectedGroups();
        } else {
//            items = getSelectedItems();
            items = getSelectedChildren();
        }
        if (items == null || items.size() <= 0) {
            if (multiSelection){
                if (this.pickerListener != null) {
                    this.pickerListener.onGroupPicked(null);
                }
            } else {
                TipsHelper.showToast(getActivity(),
                        R.string.pls_select_a_class_at_least);
            }
            return;
        }

        ArrayList<ContactItem> result = new ArrayList<ContactItem>();
        ContactItem item = null;
        if (this.groupType == ContactsPickerActivity.GROUP_TYPE_SCHOOL) {
            ContactsSchoolInfo target = null;
            for (Object obj : items) {
                target = (ContactsSchoolInfo) obj;
                item = new ContactItem();
                item.setId(target.getSchoolId());
                item.setName(target.getSchoolName());
                item.setIcon(AppSettings.getFileUrl(target.getLogoUrl()));
                item.setType(ContactItem.CONTACT_TYPE_GROUP);
                item.setSchoolId(target.getSchoolId());
                result.add(item);
            }
        } else {
            ContactsClassInfo target = null;
            for (Object obj : items) {
                target = (ContactsClassInfo) obj;
                item = new ContactItem();
                item.setId(target.getId());
                item.setName(target.getClassMailName());
                item.setIcon(AppSettings.getFileUrl(target.getHeadPicUrl()));
                item.setType(ContactItem.CONTACT_TYPE_GROUP);
                item.setSchoolId(target.getLQ_SchoolId());
                item.setClassId(target.getClassId());
                item.setHxId(target.getGroupId());
                item.setGroupId(target.getSmallGroup());
                result.add(item);
            }
        }
        //如果是来自空中课堂 此时对数据进行处理
        if (tempData){
            ArrayList<ContactItem> resultData = new ArrayList<ContactItem>();
        if (contactsSchoolInfos!=null&&contactsSchoolInfos.size()>0){
            for (int i=0;i<contactsSchoolInfos.size();i++){
                ContactsSchoolInfo schoolInfo=contactsSchoolInfos.get(i);
                for (int j=0;j<result.size();j++){
                    ContactItem itemData=result.get(j);
                    if (schoolInfo.getSchoolId().equals(itemData.getSchoolId())){
                        itemData.setSchoolName(schoolInfo.getSchoolName());
                    }
                }
            }
        }
            CreateOnlineFragment fragment= (CreateOnlineFragment) getFragmentManager().findFragmentByTag
                    (CreateOnlineFragment.TAG);
            fragment.setCurrentClassParams(result);
            popStack();
        }else {
            if (this.pickerListener != null) {
                this.pickerListener.onGroupPicked(result);
            }
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
//        FragmentTransaction ft = getParentFragment().getChildFragmentManager().beginTransaction();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.contacts_layout, fragment, GroupMemberPickerFragment.ggTAG);
//        ft.add(R.id.contacts_layout, fragment, GroupMemberPickerFragment.TAG);
        ft.add(R.id.activity_body, fragment, GroupMemberPickerFragment.TAG);
        ft.hide(GroupExpandPickerGroupFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            if ((this.pickerType & ContactsPickerActivity.PICKER_TYPE_GROUP) != 0) {
                completePickContacts();
            }
        } else {
            if (tempData){
                popStack();
            }else {
                super.onClick(v);
            }
        }
    }
    public abstract class NoDoubleClickListener implements View.OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            }
        }

        public abstract void onNoDoubleClick(View view);
    }

}
