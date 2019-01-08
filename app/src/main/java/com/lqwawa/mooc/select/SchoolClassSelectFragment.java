package com.lqwawa.mooc.select;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassResourceData;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author mrmedici
 * @desc 学校班级选择的Activity
 */
public class SchoolClassSelectFragment extends AdapterFragment
        implements View.OnClickListener {

    public interface Constants {
        String FROM_STUDYTASK_CHECK_DATA = "from_studytask_check_data";
        String CHECK_STUDY_TASK_TYPE = "check_study_task_type";
        String CHECK_STUDY_TASK_COUNT = "check_study_task_count";
        String FILTER_APPOINT_CLASS_INFO = "filter_appoint_class_info";
    }

    private View mRootView;
    private PullToRefreshView mPullToRefreshView;
    private com.lqwawa.mooc.view.CustomExpandableListView mContactsListView;
    private ListView myListView;
    private TextView mPickerClear;
    private TextView mPickerConfirm;
    private ExpandDataAdapter mDataAdapter;
    private List<ContactsSchoolInfo> mSchoolInfos;
    private ContactsClassInfo mCurrentChoiceClass;
    private boolean fromStudyTaskCheckData;//来自布置学习任务的选择
    private OnItemClickListener onItemClickListener;
    private int taskType;
    private int checkCount;
    private String schoolId;
    private boolean filterAppointClassInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = mRootView = inflater.inflate(R.layout.fragment_school_class_select, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        loadViews();
        loadGroups();
    }

    private void loadIntentData() {
        Bundle args = getArguments();
        if (args != null) {
            fromStudyTaskCheckData = args.getBoolean(Constants.FROM_STUDYTASK_CHECK_DATA, false);
            taskType = args.getInt(Constants.CHECK_STUDY_TASK_TYPE);
            checkCount = args.getInt(Constants.CHECK_STUDY_TASK_COUNT);
            schoolId = args.getString(ActivityUtils.EXTRA_SCHOOL_ID);
            filterAppointClassInfo = args.getBoolean(Constants.FILTER_APPOINT_CLASS_INFO);
        }
    }

    private void loadViews() {
        mPullToRefreshView = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mContactsListView = (com.lqwawa.mooc.view.CustomExpandableListView) mRootView.findViewById(R.id.expandable_list_view);
        myListView = (ListView) mRootView.findViewById(R.id.listview);
        if (fromStudyTaskCheckData) {
            //隐藏底部bar
            mRootView.findViewById(R.id.contacts_picker_bar_layout).setVisibility(View.GONE);
        }
        mPickerClear = (TextView) mRootView.findViewById(R.id.contacts_picker_clear);
        mPickerConfirm = (TextView) mRootView.findViewById(R.id.contacts_picker_confirm);
        mPickerConfirm.setOnClickListener(this);
        mPickerClear.setOnClickListener(this);

        mPullToRefreshView.setLoadMoreEnable(false);
        mPullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadGroups();
            }
        });

        if (filterAppointClassInfo){
            mPullToRefreshView.setVisibility(View.GONE);
            myListView.setVisibility(View.VISIBLE);
            loadSchoolClassAdapter();
        } else {
            mPullToRefreshView.setVisibility(View.VISIBLE);
            myListView.setVisibility(View.GONE);
            loadDataAdapter();
        }
    }

    private void loadSchoolClassAdapter(){
        if (myListView != null) {
            myListView.setDivider(new ColorDrawable(getResources().getColor(R.color.text_white)));
            myListView.setDividerHeight(1);
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), myListView, R.layout
                    .contacts_expand_list_child_item_with_selector) {
                @Override
                public void loadData() {
                    loadGroups();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ContactsClassInfo data = (ContactsClassInfo) getData().get(position);
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
                        imageView.setVisibility(View.GONE);
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
                    enterLqShopActivity((ContactsClassInfo) holder.data);
                }
            };
            setCurrAdapterViewHelper(myListView, helper);
        }
    }

    private void enterLqShopActivity(ContactsClassInfo classInfo){
        ClassCourseParams classCourseParams = new ClassCourseParams(schoolId,
                classInfo.getClassId());
        ClassResourceData data = null;
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
            ArrayList<Integer> selectType = new ArrayList<>();
            selectType.add(18);
            selectType.add(19);
            data = new ClassResourceData(taskType,checkCount,selectType, LQCourseCourseListActivity
                    .RC_SelectCourseRes);
        } else {
            data = new ClassResourceData(taskType,checkCount,new ArrayList<Integer>(),
                    LQCourseCourseListActivity.RC_SelectCourseRes);
        }
        ClassCourseActivity.show(getActivity(),classCourseParams,data);
    }

    private void loadDataAdapter(){
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
                    if (fromStudyTaskCheckData) {
                        imageView.setVisibility(View.GONE);
                    } else {
                        holder.selectorView = imageView;
                        //是否选中
//                        imageView.setSelected(isItemSelected(holder.position));
                        imageView.setSelected(data.isSelected());
                    /*if ((pickerType & ContactsPickerActivity.PICKER_TYPE_MEMBER) != 0) {
                        imageView.setVisibility(View.INVISIBLE);
                    }*/
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
                    imageView.setVisibility(View.VISIBLE);
                    //显示学校logo
                    getThumbnailManager().displayUserIconWithDefault(
                            AppSettings.getFileUrl(data.getLogoUrl()), imageView,
                            R.drawable.default_school_icon);
                }
                TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                if (textView != null) {
                    textView.setText(data.getSchoolName());
                    textView.setTextColor(getResources().getColor(
                            R.color.text_green));
                }
                imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                if (imageView != null) {
                    imageView.setImageResource(isExpanded ?
                            R.drawable.list_exp_up : R.drawable.list_exp_down);
                }
                imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                if (imageView != null) {
                    holder.selectorView = imageView;
                    imageView.setSelected(data.isSelected());
                }

                view.setTag(holder);
                return view;
            }
        };

        mContactsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (fromStudyTaskCheckData) {
                    checkStudyTaskData(groupPosition, childPosition);
                } else {
                    // 获取条目对象
                    ContactsClassInfo data = (ContactsClassInfo) mDataAdapter.getChild(groupPosition, childPosition);
                    // 处理班级单选逻辑
                    controlContactsClassInfoSingleChoiceLogic(data);
                    // 刷新Adapter
                    mDataAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        mDataAdapter = dataAdapter;
        mContactsListView.setAdapter(dataAdapter);
    }

    private void checkStudyTaskData(int groupPosition, int childPosition) {
        ContactsClassInfo classInfo = (ContactsClassInfo) mDataAdapter.getChild(groupPosition, childPosition);
        ContactsSchoolInfo schoolInfo = (ContactsSchoolInfo) mDataAdapter.getGroup(groupPosition);
        if (schoolInfo == null || classInfo == null){
            return;
        }
        if (onItemClickListener != null) {
            onItemClickListener.itemData(schoolInfo.getSchoolId(), classInfo.getClassId());
        } else {
            ClassCourseParams classCourseParams = new ClassCourseParams(schoolInfo.getSchoolId(),
                    classInfo.getClassId());
            ClassResourceData data = null;
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
                ArrayList<Integer> selectType = new ArrayList<>();
                selectType.add(18);
                selectType.add(19);
                data = new ClassResourceData(taskType,checkCount,selectType, LQCourseCourseListActivity
                        .RC_SelectCourseRes);
            } else {
                data = new ClassResourceData(taskType,checkCount,new ArrayList<Integer>(),
                        LQCourseCourseListActivity.RC_SelectCourseRes);
            }
            ClassCourseActivity.show(getActivity(),classCourseParams,data);
        }
    }

    /**
     * 处理班级单选逻辑
     *
     * @param data
     */
    private void controlContactsClassInfoSingleChoiceLogic(ContactsClassInfo data) {
        if (data == null) {
            return;
        }
        //设置单选状态
        final List<ContactsSchoolInfo> groupList = mSchoolInfos;
        if (groupList != null && groupList.size() > 0) {
            for (ContactsSchoolInfo schoolInfo : groupList) {
                if (schoolInfo != null) {
                    List<ContactsClassInfo> childList = schoolInfo.getClassMailList();
                    if (childList != null && childList.size() > 0) {
                        for (ContactsClassInfo classInfo : childList) {
                            if (classInfo != null) {
                                //单选状态
                                if (!TextUtils.isEmpty(classInfo.getId())
                                        && !TextUtils.isEmpty(data.getId())
                                        && classInfo.getId().equals(data.getId())) {
                                    //操作的那个,单选和取消。
                                    mCurrentChoiceClass = classInfo;
                                    controlContactsClassInfoSelectStatus(data,
                                            !data.isSelected());
                                } else {
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
     *
     * @param data
     * @param selected
     */
    private void controlContactsClassInfoSelectStatus(ContactsClassInfo data, boolean selected) {
        if (data != null) {
            //设置选中状态
            data.setSelected(selected);
        }
    }

    /**
     * 获取数据
     */
    private void loadGroups() {
        if (EmptyUtil.isEmpty(getUserInfo())) return;
        mPullToRefreshView.showRefresh();
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        if (fromStudyTaskCheckData) {
            //筛选仅仅老师的班级
            params.put("Role", "0");
        } else {
            params.put("Role", getUserInfo().getRoles());
        }
        postRequest(ServerUrl.CONTACTS_CLASS_LIST_URL, params, new DefaultListener<ContactsSchoolListResult>(ContactsSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsSchoolListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }

                updateViews(result);
            }
        });
    }

    private void updateViews(ContactsSchoolListResult result) {
        mPullToRefreshView.onHeaderRefreshComplete();
        List<ContactsSchoolInfo> list = result.getModel().getSchoolList();
        mSchoolInfos = list;
        if (true) {
            // 移除在线课堂的机构
            Utils.removeOnlineContactsSchoolInfo(list);
        }
        if (filterAppointClassInfo){
            Utils.removeSchoolInfoList(list,schoolId);
        }
        if (EmptyUtil.isEmpty(list)) return;

        if (EmptyUtil.isNotEmpty(mSchoolInfos)) {
            for (ContactsSchoolInfo schoolInfo : mSchoolInfos) {
                List<ContactsClassInfo> classMailList = schoolInfo.getClassMailList();
                if (EmptyUtil.isNotEmpty(classMailList)) {
                    ListIterator<ContactsClassInfo> contactsClassInfoListIterator = classMailList.listIterator();
                    while (contactsClassInfoListIterator.hasNext()) {
                        ContactsClassInfo classInfo = contactsClassInfoListIterator.next();
                        if (classInfo.isHistory() || classInfo.getType() == 1) {
                            // 历史班和通讯录,不显示
                            contactsClassInfoListIterator.remove();
                            continue;
                        }

                        if (fromStudyTaskCheckData) {
                            //不是老师全部remove
                            if (!classInfo.isTeacher()) {
                                contactsClassInfoListIterator.remove();
                            }
                        } else {
                            // 不是学生的全部remove
                            if (!classInfo.isStudent()) {
                                // 不是学生的全部remove
                                contactsClassInfoListIterator.remove();
                            }
                        }


                        if (EmptyUtil.isNotEmpty(mCurrentChoiceClass)) {
                            if (TextUtils.equals(classInfo.getId(), mCurrentChoiceClass.getId())) {
                                classInfo.setSelected(true);
                            }
                        }

                    }
                }

                /*for (ContactsClassInfo classInfo : schoolInfo.getClassMailList()) {
                    if(TextUtils.equals(classInfo.getId(),mCurrentChoiceClass.getId())){
                        classInfo.setSelected(true);
                    }
                }*/
            }
        }

        removeNoClassSchoolInfo(list);
        if (filterAppointClassInfo){
             AdapterViewHelper adapterViewHelper = getCurrAdapterViewHelper();
             if (adapterViewHelper != null && list.size() > 0){
                 adapterViewHelper.setData(list.get(0).getClassMailList());
             }
        } else {
            mDataAdapter.setData(list);
            if (mDataAdapter.hasData()) {
                mDataAdapter.notifyDataSetChanged();
                mDataAdapter.onGroupExpanded(0);
            }
        }
    }

    /**
     * 删除没有班级的机构
     *
     * @param list
     */
    private void removeNoClassSchoolInfo(List<ContactsSchoolInfo> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        Iterator<ContactsSchoolInfo> it = list.iterator();
        while (it.hasNext()) {
            ContactsSchoolInfo schoolInfo = it.next();
            if (schoolInfo != null && (schoolInfo.getClassMailList() == null || schoolInfo
                    .getClassMailList().size() == 0)) {
                it.remove();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.contacts_picker_confirm:
                // 确认
                if (EmptyUtil.isEmpty(mCurrentChoiceClass)) {
                    return;
                }

                confirmSelect();
                break;
            case R.id.contacts_picker_clear:
                // 取消
                // 设置全部未选中
                mCurrentChoiceClass = null;
                for (ContactsSchoolInfo schoolInfo : mSchoolInfos) {
                    if (EmptyUtil.isNotEmpty(schoolInfo.getClassMailList()))
                        for (ContactsClassInfo classInfo : schoolInfo.getClassMailList()) {
                            classInfo.setSelected(false);
                        }
                }
                // 刷新
                mDataAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 确认选择
     */
    private void confirmSelect() {
        String courseId = getActivity().getIntent().getStringExtra("courseId");
        if (EmptyUtil.isEmpty(courseId)) return;
        String schoolId = mCurrentChoiceClass.getLQ_SchoolId();
        String classId = mCurrentChoiceClass.getClassId();
        LQwawaHelper.requestCourseBindClass(courseId, schoolId, classId, new DataSource.Callback<ResponseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(ResponseVo responseVo) {
                if (responseVo.isSucceed()) {
                    // 通过EventBus通知
                    EventBus.getDefault().post(new EventWrapper(null, EventConstant.APPOINT_COURSE_IN_CLASS_EVENT));
                    // 绑定成功
                    getActivity().finish();
                } else {
                    // 绑定失败
                    int code = responseVo.getCode();
                    if (code == -2) {
                        UIUtil.showToastSafe(R.string.tip_appoint_class_not_course);
                    }
                }
            }
        });
    }

    public UserInfo getUserInfo() {
        if (getActivity().getApplication() != null) {
            return ((MyApplication) getActivity().getApplication()).getUserInfo();
        }
        return null;
    }

    protected class MyViewHolder extends ViewHolder {
        int position;
        ImageView selectorView;
    }

    public interface OnItemClickListener {
        void itemData(String schoolId, String classId);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
