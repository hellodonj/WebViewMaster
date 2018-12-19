package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.GroupMemberResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;
import com.galaxyschool.app.wawaschool.views.ContactsListDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/11/27 15:43
  * 描    述：小组成员界面,添加成员界面
  * 修订历史：
  * ================================================
  */
public class AddGroupMembersFragment extends GroupContactsListFragment {
    public static final String TAG = AddGroupMembersFragment.class.getSimpleName();
    private List<ContactsClassMemberInfo> teacherList;
    private List<ContactsClassMemberInfo> studentList;
    private TextView tvClear;


    public interface Constants  {
        String EXTRA_ISPICK = "ispick";
        String EXTRA_ISEDIT = "isedit";
        String EXTRA_CREATEID = "createId";
        String EXTRA_ISHEADTEACHER = "isHeadTeacher";

    }

    private GridView teachersGridView, studentsGridView;
    /**
     * 老师和学生栏 展开 收缩 按钮
     */
    private ImageView teachersArrow, studentsArrow;
    /**
     * 老师和学生栏 全选按钮icon
     */
    private ImageView teacherSelectAllIcon, studentSelectAllIcon;

    /**
     * 老师和学生栏 全选按钮布局
     */
    private View teacherSelectAllView, studentSelectAllView;
    /**
     * true: 添加组员模式 ; false:查看小组成员
     */
    private boolean isPick;
    /**
     * true:编辑组员模式
     */
    private boolean isEdit;
    private boolean isTeacher;
    private boolean isHeadTeacher;

    /**
     * isCreator: 是否为小组创建者
     *
     */
    private boolean isCreator;


    /**
     * groupID: 小组Id
     *
     */
    private String groupID;
    private String groupName;
    /**
     *  添加组员界面的集合
     */
    private ArrayList<ContactsClassMemberInfo> addGroupMemberList = new ArrayList<>();
    /**
     *  查看小组成员界面的集合
     */
    private ArrayList<ContactsClassMemberInfo> groupInfoList = new ArrayList<>();
    /**
     * 学校 班级 小组Id
     */
    private String schoolID;
    private String classID;
    private String createId;
    private List<String> memberIdList = new ArrayList<>();
    private SubscribeClassInfo classInfo;
    private ContactsClassMemberListResult classMemberListResult;
    private ContactsClassMemberInfo headTeacherInfo;
    private String headTeacherId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_add_groupmembers, container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initIntent();
        initViews();
        if (isPick) {
            //加载班级成员
            loadClassMembers(addGroupMemberList);
        } else {
            //加载小组成员
            loadGroupMember();
        }

    }


    private void initIntent() {
        isPick = getArguments().getBoolean(Constants.EXTRA_ISPICK);
        isEdit = getArguments().getBoolean(Constants.EXTRA_ISEDIT);
        isHeadTeacher = getArguments().getBoolean(Constants.EXTRA_ISHEADTEACHER);
        isTeacher = getArguments().getBoolean(ContactsClassGroupFragment.Constants.EXTRA_ISTEACHER);
        groupID = getArguments().getString(ContactsClassGroupFragment.Constants.EXTRA_GROUPID);
        createId = getArguments().getString(Constants.EXTRA_CREATEID);
        groupName = getArguments().getString(ContactsClassGroupFragment.Constants.EXTRA_GROUPNAME);
        schoolID = getArguments().getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID);
        classID = getArguments().getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
        ArrayList<ContactsClassMemberInfo> dataList;
        dataList = getArguments().getParcelableArrayList(ContactsClassGroupFragment.Constants.EXTRA_CLASSMAILLISTDETAILLIST);
        addGroupMemberList.addAll(dataList);
    }


    private void initViews() {

        updateTitle();
        TextView textView;

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        /**
         * ===========================================================================
         * 老师栏
         * ===========================================================================
         */

        View view = findViewById(R.id.contacts_teachers_title_layout);


        view.setEnabled(true);
        view.setOnClickListener(this);
        this.teachersArrow = (ImageView) view.findViewById(R.id.contacts_teachers_arrow);
        this.teacherSelectAllIcon = (ImageView) view.findViewById(R.id.teacher_select_all_icon);
        this.teacherSelectAllView =  view.findViewById(R.id.teacher_select_all);

         textView = (TextView) findViewById(R.id.contacts_teachers_title);
        if (textView != null) {
            textView.setText(R.string.teacher);
            textView.setVisibility(View.VISIBLE);
        }
        AdapterViewHelper gridViewHelper = null;
        GridView gridView = (GridView) findViewById(R.id.contacts_teachers);
        if (gridView != null) {
            gridView.setNumColumns(4);
            gridViewHelper = new GroupAdapter(getActivity(),
                    gridView, R.layout.contacts_grid_item);
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    gridViewHelper);
            this.teachersGridView = gridView;
        }

        /**
         * ===========================================================================
         * 学生栏
         * ===========================================================================
         */
        view = findViewById(R.id.contacts_students_title_layout);

        view.setEnabled(true);
        view.setOnClickListener(this);
        this.studentsArrow = (ImageView) view.findViewById(R.id.contacts_students_arrow);
        this.studentSelectAllIcon = (ImageView) view.findViewById(R.id.students_select_all_icon);
        this.studentSelectAllView =  view.findViewById(R.id.students_select_all);


        textView = (TextView) findViewById(R.id.contacts_students_title);
        if (textView != null) {
            textView.setText(R.string.student);
            textView.setVisibility(View.VISIBLE);
        }
        gridView = (GridView) findViewById(R.id.contacts_students);
        if (gridView != null) {
            gridView.setNumColumns(4);
            gridViewHelper = new GroupAdapter(getActivity(),
                    gridView, R.layout.contacts_grid_item) ;
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    gridViewHelper);
            this.studentsGridView = gridView;
        }


            teachersArrow.setVisibility(isPick ? View.GONE : View.VISIBLE);
            studentsArrow.setVisibility(isPick ? View.GONE : View.VISIBLE);
            teacherSelectAllView.setVisibility(isPick ?View.VISIBLE : View.GONE);
            studentSelectAllView.setVisibility(isPick ?View.VISIBLE : View.GONE);



        /**
         * ===========================================================================
         * 底部按钮
         * ===========================================================================
         */
        View bottomBtn = findViewById(R.id.fl_bottom_btn);
        if (bottomBtn != null) {
            tvClear = (TextView) bottomBtn.findViewById(R.id.contacts_picker_clear);
            if (tvClear != null) {
                tvClear.setOnClickListener(this);
                tvClear.setEnabled(true);
                tvClear.setText(R.string.clear);
//                textView.setTextColor(getResources().getColor(R.color.text_dark_gray));

            }

            textView = (TextView) bottomBtn.findViewById(R.id.contacts_picker_confirm);
            textView.setEnabled(true);
           textView.setOnClickListener(this);
            bottomBtn.setVisibility(isPick ? View.VISIBLE : View.GONE);
        }

        //解散小组
        View tvDissolveGroup = findViewById(R.id.tv_dissolve_group);
        if (isHeadTeacher || getMemeberId().equalsIgnoreCase(createId)) {

            tvDissolveGroup.setVisibility(isPick ? View.GONE : View.VISIBLE);
            tvDissolveGroup.setOnClickListener(this);
        }
    }

    private void updateTitle() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        //标题
        textView.setText(isPick ? getString(R.string.str_add_group_member) : groupName);
    }

    /**
     * 标题栏右侧按钮
     */
    private void updateHeaderRightBrn() {
        TextView textView;
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (isPick) {
                textView.setVisibility(View.GONE);
            } else {
                //修改名称 或 退出小组
                textView.setText(isCreator ? R.string.str_modify_name : R.string.str_exit_group );
                textView.setTextColor(getResources().getColor(R.color.text_green));
                textView.setOnClickListener(this);
                textView.setVisibility(isGroupMember()? View.VISIBLE : View.GONE);
            }

        }
    }

    /**
     * 新建小组
     */
    private void builderGroup() {

        if (TextUtils.isEmpty(groupID)) {
            //新建一个小组
            showBuilderDialog();
        } else {
            //添加小组成员
            uploadGroupData(groupName);

        }
    }

    /**
     *  输入小组名称对话框
     */
    private void showBuilderDialog() {
        ContactsInputBoxDialog dialog = new ContactsInputBoxDialog(getActivity(),
                getString(R.string.str_input_group_name),
                "", "", getString(R.string.cancel), null,
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = ((ContactsInputBoxDialog) dialog).getInputText();
                if (TextUtils.isEmpty(inputText)) {
                    TipMsgHelper.ShowLMsg(getActivity(),R.string.str_input_group_name);
                    return;
                }
                if (isPick) {
                    uploadGroupData(inputText);
                } else {
//                   groupRename(inputText);
                }
            }
        });
        dialog.setInputLimitNumber(20);
        dialog.show();
    }

    /**
     * 上传小组数据
     * @param groupName
     */
    private void uploadGroupData(String groupName) {
        showLoadingDialog();
        JSONObject params = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
        for (ContactsClassMemberInfo contactsClassMemberInfo : addGroupMemberList) {
            if (memberIdList.contains(contactsClassMemberInfo.getMemberId())) {
                jsonObject = new JSONObject();

                    jsonObject.put("MemberId",contactsClassMemberInfo.getMemberId());
                    jsonObject.put("Role",contactsClassMemberInfo.getRole());
                    jsonArray.put(jsonObject);

            }
        }

            if (isEdit) {
                //添加新组员
                params.put("GroupId",groupID );

            } else {
                //新建小组
                params.put("SchoolId",schoolID );
                params.put("ClassId",classID);
                params.put("GroupName", groupName);
            }


        params.put("CreateId",getMemeberId());
        params.put("MemberList",jsonArray );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(getActivity(), ModelResult.class) {
            @Override
            public void onSuccess(String json) {
                dismissLoadingDialog();
                if(getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;

                com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(json);
                int errorCode = jsonObject.getInteger("ErrorCode");
                if (errorCode == 0) {
                        TipMsgHelper.ShowLMsg(getMyApplication(), R.string.success);
                        //刷新组员数据
                        if (mLoadDataListener != null) {
                            mLoadDataListener.loadData();
                        }
                        popStack();
                    } else {
                        TipMsgHelper.ShowLMsg(getMyApplication(), R.string.str_build_group_error_tips);
                    }

            }
            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

        };
        listener.setShowErrorTips(false);
        RequestHelper.postRequest(getActivity(),isEdit ? ServerUrl.GET_MODIFYSTUDYGROUPMEMBER : ServerUrl.GET_ADDSTUDYGROUP,
                params.toString(), listener);
    }

    /**
     * 加载小组成员列表
     */
    private void loadGroupMember() {
        showLoadingDialog();
        Map<String, Object> params = new ArrayMap<>();
        params.put("GroupId", groupID);
        RequestHelper.RequestModelResultListener<GroupMemberResult> listener =
                new RequestHelper.RequestModelResultListener<GroupMemberResult>(getActivity(),
                        GroupMemberResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        dismissLoadingDialog();
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        GroupMemberResult result = getResult();

                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {

                            return;
                        }
                        ContactsClassMemberInfo info = result.getModel();

                        List<ContactsClassMemberInfo> teacherList = info.getTeacherList();
                        List<ContactsClassMemberInfo> studentList = info.getStudentList();
                        createId = info.getCreateId();
                        if (createId != null && createId.equalsIgnoreCase(getMemeberId())) {
                            //当前身份是小组创建者
                            isCreator = true;
                        }

                        groupInfoList.clear();

                        //封装数据
                        if (teacherList != null) {
                            groupInfoList.addAll(teacherList);
                        }
                        if (studentList != null) {
                            groupInfoList.addAll(studentList);
                        }
                        //更新右上角btn
                        updateHeaderRightBrn();
                        loadClassMembers(groupInfoList);

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                };
        listener.setShowErrorTips(false);
        postRequest(ServerUrl.GET_LOADSTUDYGROUPINFO, params, listener);
    }

    /**
     * 加载班级成员列表
     *  数据从班级详情页带进来
     * @param list
     */
    private void loadClassMembers(ArrayList<ContactsClassMemberInfo> list) {


        teacherList = new ArrayList<>();
        studentList = new ArrayList<>();

        for (ContactsClassMemberInfo obj : list) {

            if (obj.isHeadTeacher() || obj.isHeadMaster()) {
                this.headTeacherInfo = obj;
                if (this.headTeacherId == null) {
                    this.headTeacherId = obj.getMemberId();
                }
            }

            if (obj.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                if (obj.getWorkingState() == 1) {
                    //1== 在职状态
                    teacherList.add(obj);
                }

                if (getMemeberId().equalsIgnoreCase(obj.getMemberId())) {
                    //创建者默认选择
                    //新建小组模式
                    if (isPick) {
                        obj.setIsSelect(true);
                        memberIdList.add(getMemeberId());
                    }
                }

            } else if (obj.getRole() == RoleType.ROLE_TYPE_STUDENT) {
                if (obj.getWorkingState() == 1) {
                    //1== 在职状态
                    studentList.add(obj);
                }


            }

            if (isPick) {
                if (obj.isSelect()) {
                    //从小组详情进来
                    if (!memberIdList.contains(obj.getMemberId())) {
                        memberIdList.add(obj.getMemberId());
                    }
                }
            }

        }


        //添加成员
        if (!isPick && isCreator) {
            ContactsClassMemberInfo info = new ContactsClassMemberInfo();
            info.setMemberId(null);
            teacherList.add(info);
            studentList.add(info);

        }

        AdapterViewHelper teachersHelper = getAdapterViewHelper(
                String.valueOf(this.teachersGridView.getId()));
        AdapterViewHelper studentsHelper = getAdapterViewHelper(
                String.valueOf(this.studentsGridView.getId()));

        teachersHelper.setData(teacherList);
        studentsHelper.setData(studentList);

        if (isPick) {
            //全选按钮联动
            teacherSelectAllIcon.setSelected(isSelectAll(teacherList));
            studentSelectAllIcon.setSelected(isSelectAll(studentList));
        }

        updateBottomBtn();

    }

    /**
     * 判断当前账号是否在小组内
     * @return
     */
    private boolean isGroupMember() {
        if (groupInfoList != null && groupInfoList.size() > 0) {
            //在当前小组内的成员才显示按钮
            for (ContactsClassMemberInfo info : groupInfoList) {
                if (getMemeberId().equalsIgnoreCase(info.getMemberId())) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
           //修改名称 或 退出小组
            if (isCreator) {
                renameGroup();
            } else {
                exitGroup();
            }

        } else if (v.getId() == R.id.contacts_header_left_btn) {
            //返回
            popStack();
        }else if (v.getId() == R.id.contacts_teachers_title_layout) {
            //老师标题栏
            updateBar(v, teacherList, teachersArrow,true);

        } else if (v.getId() == R.id.contacts_students_title_layout) {
            //学生标题栏
            updateBar(v, studentList, studentsArrow,false);

        } else if (v.getId() == R.id.contacts_picker_clear) {
            //清除
            doClear();
        } else if (v.getId() == R.id.contacts_picker_confirm) {
            //确定
            builderGroup();
        } else if (v.getId() == R.id.tv_dissolve_group) {
            //解散小组
            if (isHeadTeacher || getMemeberId().equalsIgnoreCase(createId)) {
                dissolveGroup();
            } else {
                TipMsgHelper.ShowLMsg(getMyApplication(), getString(R.string.str_group_delete));
            }


        }
    }

    /**
     * 解散分组弹框
     */
    private void dissolveGroup() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.str_dissolution_group_tips)
                ,getString(R.string.cancel) ,
                null, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteGroup();
            }
        });
        messageDialog.show();
    }

    private void deleteGroup() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new ArrayMap<>();
        params.put("UpdateId", getUserInfo().getMemberId());
        params.put("GroupId", groupID);
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(jsonString);
                        int errorCode = jsonObject.getInteger("ErrorCode");
                        if (errorCode != 0) {
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.delete_failure);
                            return;
                        }
                        //刷新组员数据
                        if (mLoadDataListener != null) {
                            mLoadDataListener.loadData();
                        }
                        popStack();
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.GET_REMOVESTUDYGROUP, params, listener);
    }



    /**
     * 修改小组名称
     */
    private void renameGroup() {
        //重命名
        CommonEditTextFragment fragment = CommonEditTextFragment.newInstance( schoolID, classID,groupID,groupName,getString(R.string.str_rename_group));
        fragment.setLoadDataListener(new CommonEditTextFragment.LoadDataListener() {
            @Override
            public void loadData(String name) {
                //更新组名 标题栏
                AddGroupMembersFragment.this.groupName = name;
                updateTitle();
                if (mLoadDataListener != null) {
                    mLoadDataListener.loadData();
                }
            }
        });
        getFragmentManager().beginTransaction()
                .add(R.id.contacts_layout,fragment, CommonEditTextFragment.TAG)
                .hide(AddGroupMembersFragment.this)
                .addToBackStack(CommonEditTextFragment.TAG)
                .commit();
    }

    /**
     * 更新老师 学生栏 状态
     * @param v
     * @param list
     * @param imageView
     */
    private void updateBar(View v, List<ContactsClassMemberInfo> list, ImageView imageView,boolean isTeacherBar) {
        if (isPick) {
            //选择模式 全选
            selectAllGroup(list,isTeacherBar);

        } else {
            showOrHideGroup(v, imageView,isTeacherBar);
        }
    }

    /**
     * 更新老师,学生栏 展开 收藏状态
     * @param v
     * @param imageView
     */
    private void showOrHideGroup(View v ,ImageView imageView,boolean isTeacherBar) {
        v.setSelected(!v.isSelected());
        imageView.setImageResource(!v.isSelected() ?
                R.drawable.list_exp_up : R.drawable.list_exp_down);
        if (isTeacherBar) {
            this.teachersGridView.setVisibility(!v.isSelected() ?
                    View.VISIBLE : View.GONE);
        } else {
            this.studentsGridView.setVisibility(!v.isSelected() ?
                    View.VISIBLE : View.GONE);
        }
    }

    /**
     * 全选 老师或学生集合
     */
    private void selectAllGroup(List<ContactsClassMemberInfo> list,boolean isTeacherBar) {
        if (list != null) {
            boolean flag ;
            if (isTeacherBar) {
                //老师栏
                if (teacherList.size() == 1) {
                    //只有创建者 默认全选
                    teacherSelectAllIcon.setSelected(true);
                    flag = true;

                } else {
                    teacherSelectAllIcon.setSelected(!teacherSelectAllIcon.isSelected());
                    flag = teacherSelectAllIcon.isSelected();
                }

            } else {
                //学生栏
                studentSelectAllIcon.setSelected(!studentSelectAllIcon.isSelected());
                flag = studentSelectAllIcon.isSelected();
            }

            if (flag) {
                //全选
                for (ContactsClassMemberInfo info : list) {
                    if (!memberIdList.contains(info.getMemberId())) {
                        info.setIsSelect(true);
                        memberIdList.add(info.getMemberId());
                    }
                }
            } else {
                //全不选
                for (ContactsClassMemberInfo info : list) {
                    if (memberIdList.contains(info.getMemberId())) {
                        if (getMemeberId().equalsIgnoreCase(info.getMemberId()) || createId.equalsIgnoreCase(info.getMemberId())) {
                            //创建者一直选择状态,不能取消  老师添加成员也不能取消自己
                            continue;
                        }
                        info.setIsSelect(false);
                        memberIdList.remove(info.getMemberId());
                    }
                }
            }

            if (isTeacherBar) {
                //更新老师栏界面
                updateData(String.valueOf(teachersGridView.getId()));
            } else {
                //更新学生栏界面
                updateData(String.valueOf(studentsGridView.getId()));

            }

            updateBottomBtn();

        }


    }

    /**
     * 清除选择项
     */
    private void doClear() {
        teacherSelectAllIcon.setSelected(false);
        studentSelectAllIcon.setSelected(false);
        memberIdList.clear();
        memberIdList.add(createId);
        if (!getMemeberId().equalsIgnoreCase(createId)) {
            //创建者一直选择状态,不能取消  老师添加成员也不能取消自己
            memberIdList.add(getMemeberId());
        }

        for (ContactsClassMemberInfo info : teacherList) {
            if (createId.equalsIgnoreCase(info.getMemberId())) {
                info.setIsSelect(true);
            } else {
                info.setIsSelect(false);
            }

        }
        for (ContactsClassMemberInfo info : studentList) {
            info.setIsSelect(false);
        }

        //刷新界面

        //全选按钮联动
        teacherSelectAllIcon.setSelected(isSelectAll(teacherList));

            //老师
        updateData(String.valueOf(teachersGridView.getId()));

            //学生
        updateData(String.valueOf(studentsGridView.getId()));

        updateBottomBtn();


    }

    /**
     * 更新数据 刷新界面
     * @param tag
     */
    private void updateData(String tag) {
        AdapterViewHelper teachersHelper = getAdapterViewHelper(
                tag);
        teachersHelper.update();
    }

    /**
     *  退出小组
     */
    private void exitGroup() {
        for (ContactsClassMemberInfo info : groupInfoList) {

            if (info.getMemberId().equalsIgnoreCase( getMemeberId())) {
                removeMember(info,getString(R.string.str_exit_group_tips),true);
                break;
            }
        }

    }

    /**
     * 修改小组名称
     */
    private void groupRename(final String groupName) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("SchoolId",schoolID );
        params.put("ClassId",classID);
        params.put("UpdateId",getMemeberId());
        params.put("GroupName", groupName);
        params.put("GroupId",groupID);

        DefaultListener listener=
                new DefaultListener<ModelResult>(
                        ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(jsonString);
                        int errorCode = jsonObject.getInteger("ErrorCode");
                        if (errorCode == 0) {
                            //更新组名 标题栏
                            AddGroupMembersFragment.this.groupName = groupName;
                            updateTitle();
                            if (mLoadDataListener != null) {
                                mLoadDataListener.loadData();
                            }
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.modify_success);
                        } else {
                            TipMsgHelper.ShowLMsg(getMyApplication(), getResult().getErrorMessage());
                        }
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.GET_CHANGESTUDYGROUPNAME, params, listener);

    }



    @Override
    protected void removeFromClass(ContactsClassMemberInfo info) {

    }

    /**
     * 查看信息
     */
    private void showPopupWindow(final ContactsClassMemberInfo info) {
        List<String> contentList = new ArrayList<>();
        if (info.getRole() == 0) {
            //老师
            contentList.add(getString(R.string.teacher_info));
        } else {
            contentList.add(getString(R.string.student_info));
        }

        if (isCreator && !info.getMemberId().equalsIgnoreCase(getMemeberId())) {
            //创建者显示此项
            contentList.add(getString(R.string.str_out_of_group));
        }

        @SuppressWarnings("unchecked")
        ContactsListDialog dialog = new ContactsListDialog(getActivity(),
                R.style.Theme_ContactsDialog, null,
                contentList, R.layout.contacts_dialog_list_text_item,
                new DataAdapter.AdapterViewCreator() {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView != null) {
                            String title = (String) convertView.getTag();
                            TextView textView = (TextView) convertView.findViewById(
                                    R.id.contacts_dialog_list_item_title);
                            if (textView != null) {
                                textView.setTextColor(Color.parseColor("#038bff"));
                                textView.setText(title);
                            }
                        }
                        return convertView;
                    }
                },
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String title = (String) view.getTag();
                        if (getString(R.string.teacher_info).equals(title)) {
                            //老师信息
                            enterMemberDetails(info);
                        } else if (getString(R.string.student_info).equals(title)) {
                            //学生信息
                            enterMemberDetails(info);
                        }else if (getString(R.string.str_out_of_group).equals(title)) {
                            //移出分组
                            String realName = info.getRealName();
                            if (TextUtils.isEmpty(realName)) {
                                realName = info.getNickname();
                            }
                            removeMember(info,getString(R.string.str_remove_from_group,realName),false);

                        }
                    }
                }, getString(R.string.cancel), null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }


    /**
     * 移出分组弹框
     * @param info
     * @param message
     * @param removeOrExit true: 自己退出   false:被老师移除
     */
    private void removeMember(final ContactsClassMemberInfo info, String message, final boolean removeOrExit) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                message
                ,getString(R.string.cancel) ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                removeFromGroup(info,removeOrExit);
            }
        });
        messageDialog.show();
    }

    /**
     * 移出分组逻辑
     * @param info
     */
    private void removeFromGroup(final ContactsClassMemberInfo info, final boolean removeOrExit) {
        showLoadingDialog();
        JSONObject params = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject();
            jsonObject.put("MemberId",info.getMemberId());
            jsonObject.put("Role",info.getRole());
            jsonArray.put(jsonObject);

            params.put("GroupId",groupID);
            params.put("MemberList",jsonArray );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(getActivity(), ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (TextUtils.isEmpty(jsonString)) return;
                        dismissLoadingDialog();
                        try {
                            com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(jsonString);
                            int errorCode = jsonObject.getInteger("ErrorCode");
                            if (errorCode == 0) {

                                if (info.getRole() == 0) {
                                    //老师
                                    AdapterViewHelper teachersHelper = getAdapterViewHelper(
                                            String.valueOf(teachersGridView.getId()));
                                    teachersHelper.getData().remove(info);
                                    teachersHelper.update();

                                } else {
                                    //学生

                                    AdapterViewHelper studentsHelper = getAdapterViewHelper(
                                            String.valueOf(studentsGridView.getId()));
                                    studentsHelper.getData().remove(info);
                                    studentsHelper.update();

                                }
                                groupInfoList.remove(info);

                                if (removeOrExit) {
                                    //自己退出
                                    TipMsgHelper.ShowLMsg(getMyApplication(),getResources().getString(R.string.str_exit_group)
                                            + getResources().getString(R.string.success));
                                    popStack();

                                } else {
                                    //被老师移除
                                    TipMsgHelper.ShowLMsg(getMyApplication(),getResources().getString(R.string.str_out_of_group)
                                            + getResources().getString(R.string.success));
                                }

                            } else {
                                TipMsgHelper.ShowLMsg(getMyApplication(), R.string.Removed_from_the_failure);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.postRequest(getActivity(),ServerUrl.GET_OUTOFSTUDYGROUP, params.toString(), listener);
    }

    private class GroupAdapter extends MyAdapterViewHelper {

        public GroupAdapter(Context context, AdapterView adapterView, int itemViewLayout) {
            super(context, adapterView, itemViewLayout);
        }

        @Override
        public void loadData() {

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
            if (data == null) {
                return view;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
            }
            holder.data = data;
            ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
            TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
            TextView indicatorTextView = (TextView) view.findViewById(R.id.contacts_item_indicator);
            //根据字段来控制资源
            if (data.getMemberId() == null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                        R.drawable.create_student);
                textView.setTextColor(getResources().getColor(R.color.text_green));
                textView.setText(R.string.str_add_group_member);
                indicatorTextView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);

            } else {
                if (isPick) {
                    textView.setText(data.getNoteName());
                } else {
                    String realName = data.getRealName();
                    if (TextUtils.isEmpty(realName)) {
                        realName = data.getNickname();
                    }
                    textView.setText(realName);
                }

                textView.setTextColor(getResources().getColor(R.color.black));
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                        R.drawable.default_user_icon);
            }


            if (isPick){
                imageView= (ImageView) view.findViewById(R.id.circle_icon);
                if (imageView!=null){
                    imageView.setVisibility(View.VISIBLE);
                    if (memberIdList.size() > 0 && memberIdList.contains(data.getMemberId())) {
                        imageView.setSelected(true);
                    } else {
                        imageView.setSelected(false);
                    }

                }
            }
            return view;
        }
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            if (!isPick){
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    return;
                }
                ContactsClassMemberInfo item = (ContactsClassMemberInfo) holder.data;
                if (item.getMemberId() == null) {
                    //添加组员
                    addGroupMember();

                } else {
                    //查看信息
                    showPopupWindow(item);
                }
            }else {
                ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
                selectGroupMember(data);
            }
        }
    }

    /**
     * 选择小组成员 老师和学生多选
     * @param data
     */
    private void selectGroupMember(ContactsClassMemberInfo data) {
        if (getMemeberId().equalsIgnoreCase(data.getMemberId()) || createId.equalsIgnoreCase(data.getMemberId())) {
            //创建者一直选择状态,不能取消  老师添加成员也不能取消自己
            return;
        }
        data.setIsSelect(!data.isSelect());
        if (data.isSelect()) {
            memberIdList.add(data.getMemberId());
        } else {
            if (memberIdList.contains(data.getMemberId())) {
                memberIdList.remove(data.getMemberId());
            }
        }

        String adapterId = null;
        if (data.getRole() == 0) {
            adapterId = String.valueOf(teachersGridView.getId());
            //全选按钮联动
            teacherSelectAllIcon.setSelected(isSelectAll(teacherList));

        } else {
            adapterId = String.valueOf(studentsGridView.getId());
            //全选按钮联动
            studentSelectAllIcon.setSelected(isSelectAll(studentList));
        }
        getAdapterViewHelper(adapterId).update();
        updateBottomBtn();
    }

    /**
     * 判断是否全选了
     * @param list
     * @return
     */
     private boolean isSelectAll(List<ContactsClassMemberInfo> list) {
         boolean selectAll = true;
         for (ContactsClassMemberInfo info : list) {
             if (!info.isSelect()) {
                 selectAll = false;
                 break;
             }
         }
         return selectAll;

     }

    /**
     * 进入选择成员界面
     */
    private void addGroupMember() {
        ArrayList<ContactsClassMemberInfo> list =  getArguments().getParcelableArrayList(ContactsClassGroupFragment.Constants.EXTRA_CLASSMAILLISTDETAILLIST);
        for (ContactsClassMemberInfo classMemberInfo : list) {
            classMemberInfo.setIsSelect(false);
            for (ContactsClassMemberInfo info : groupInfoList) {
                if (classMemberInfo.getMemberId().equalsIgnoreCase(info.getMemberId())) {
                    classMemberInfo.setIsSelect(true);
                }
            }
        }
        AddGroupMembersFragment fragment = AddGroupMembersFragment.newInstance(true,list, groupID, schoolID, classID,groupName,isTeacher,createId,true,isHeadTeacher);
        fragment.setLoadDataListener(new LoadDataListener() {
            @Override
            public void loadData() {
                loadGroupMember();
            }
        });
        getFragmentManager().beginTransaction()
                .add(R.id.contacts_layout,fragment, AddGroupMembersFragment.TAG)
                .hide(AddGroupMembersFragment.this)
                .addToBackStack(AddGroupMembersFragment.TAG)
                .commit();
    }

    /**
     *
     * @param isPick 添加成员模式 新建小组
     * @param isEdit 编辑组员模式 编辑小组
     * @param list 班级成员列表
     * @param groupId 小组Id
     * @return
     */
    public static AddGroupMembersFragment newInstance(boolean isPick, ArrayList<ContactsClassMemberInfo> list,String groupId
            , String schoolId, String classId,String groupName, boolean isTeacher,String createId,boolean isEdit,boolean isHeadTeacher){
        
        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_ISPICK,isPick);
        args.putBoolean(Constants.EXTRA_ISEDIT,isEdit);
        args.putBoolean(Constants.EXTRA_ISHEADTEACHER,isHeadTeacher);
        args.putParcelableArrayList(ContactsClassGroupFragment.Constants.EXTRA_CLASSMAILLISTDETAILLIST, list);
                AddGroupMembersFragment fragment = new AddGroupMembersFragment();
        args.putString(ContactsClassGroupFragment.Constants.EXTRA_GROUPID,groupId);
        args.putString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID,schoolId);
        args.putString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_CLASS_ID,classId);
        args.putString(ContactsClassGroupFragment.Constants.EXTRA_GROUPNAME,groupName);
        args.putString(Constants.EXTRA_CREATEID,createId);
        args.putBoolean(ContactsClassGroupFragment.Constants.EXTRA_ISTEACHER,isTeacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (addGroupMemberList != null) {
            addGroupMemberList.clear();
            addGroupMemberList = null;
        }

        if (groupInfoList != null) {
            groupInfoList.clear();
            groupInfoList = null;
        }


        if (memberIdList != null) {
            memberIdList.clear();
            memberIdList = null;
        }

    }

    interface LoadDataListener {
        void loadData();
    }

    private LoadDataListener mLoadDataListener;

    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    /**
     * 更新底部按钮
     */
    private void updateBottomBtn() {

        if (memberIdList != null && memberIdList.size() > 1) {
            if (tvClear != null) {
                tvClear.setEnabled(true);
            }
        } else {
            if (tvClear != null) {
                tvClear.setEnabled(false);
            }
        }
    }
}
