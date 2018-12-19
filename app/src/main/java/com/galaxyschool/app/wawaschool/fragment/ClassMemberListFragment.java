package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsClassManagementActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.OnlineNumber;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClassMemberListFragment extends GroupContactsListFragment {
    public static final String TAG = ClassMemberListFragment.class.getSimpleName();
    private TextView headTitleTextV;
    private GridView teachersGridView;
    private GridView studentsGridView;
    private GridView parentsGridView;
    private ImageView teachersArrow, studentsArrow, parentsArrow;
    private TextView teacherNumTextV;
    private TextView studentNumTextV;
    private TextView parentNumTextV;

    private ContactsClassMemberInfo headTeacherInfo;
    private String headTeacherId;
    private PublishClass publishClass;//当期那发布对象的信息
    private List<OnlineNumber> onlineNumber;//直播在线人数列表
    private boolean showDeleteBtn;//判断是否显示删除的按钮
    private boolean isHistoryClass;
    public interface Constant {
        String CURRENT_CLASS_INFO = "current_class_info";
        String ONLINE_NUMBER_LIST = "online_number_list";
        String SHOW_DELETE_BTN = "show_delete_btn";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_member_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        initData();
        loadContacts();
        checkClassPlayEnd();
    }

    private void getIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            publishClass = (PublishClass) bundle.getSerializable(Constant.CURRENT_CLASS_INFO);
            onlineNumber = (List<OnlineNumber>) bundle.getSerializable(Constant.ONLINE_NUMBER_LIST);
            showDeleteBtn = bundle.getBoolean(Constant.SHOW_DELETE_BTN);
            isHistoryClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,false);
        }
    }

    private void initViews() {
        headTitleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitleTextV != null) {
            headTitleTextV.setText(getString(R.string.str_class_member_list));
        }
        ImageView headLeftImageV = (ImageView) findViewById(R.id.contacts_header_left_btn);
        headLeftImageV.setOnClickListener(this);
        if (showDeleteBtn && !isHistoryClass) {
            TextView deleteBtn = (TextView) findViewById(R.id.tv_delete_btn);
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(this);
        }

        teacherNumTextV = (TextView) findViewById(R.id.teacher_online_num);
        studentNumTextV = (TextView) findViewById(R.id.student_online_num);
        parentNumTextV = (TextView) findViewById(R.id.parent_online_num);
    }

    private void initData() {
        View view = findViewById(R.id.contacts_teachers_title_layout);
        if (view != null) {
            view.setOnClickListener(this);
            this.teachersArrow = (ImageView) view.findViewById(R.id.contacts_teachers_arrow);
        }
        TextView textView = (TextView) findViewById(R.id.contacts_teachers_title);
        if (textView != null) {
            textView.setText(R.string.teacher);
            textView.setVisibility(View.INVISIBLE);
        }
        AdapterViewHelper gridViewHelper = null;
        GridView gridView = (GridView) findViewById(R.id.contacts_teachers);
        if (gridView != null) {
            gridViewHelper = new MyAdapterViewHelper(getActivity(),
                    gridView, R.layout.contacts_grid_item) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
                    if (data != null && data.isSelect()) {
                        showOnlinePersonFlag((ImageView) view.findViewById(R.id.circle_icon));
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                }
            };
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    gridViewHelper);
            this.teachersGridView = gridView;
        }

        view = findViewById(R.id.contacts_students_title_layout);
        if (view != null) {
            view.setOnClickListener(this);
            this.studentsArrow = (ImageView) view.findViewById(R.id.contacts_students_arrow);
        }
        textView = (TextView) findViewById(R.id.contacts_students_title);
        if (textView != null) {
            textView.setText(R.string.student);
            textView.setVisibility(View.INVISIBLE);
        }
        gridView = (GridView) findViewById(R.id.contacts_students);
        if (gridView != null) {
            gridViewHelper = new MyAdapterViewHelper(getActivity(),
                    gridView, R.layout.contacts_grid_item) {
                @Override
                public void loadData() {
                }

                //重写一下父类的方法
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
                    if (data != null && data.isSelect()) {
                        showOnlinePersonFlag((ImageView) view.findViewById(R.id.circle_icon));
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                }
            };
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    gridViewHelper);
            this.studentsGridView = gridView;
        }

        view = findViewById(R.id.contacts_parents_title_layout);
        if (view != null) {
            view.setOnClickListener(this);
            this.parentsArrow = (ImageView) view.findViewById(R.id.contacts_parents_arrow);
        }
        textView = (TextView) findViewById(R.id.contacts_parents_title);
        if (textView != null) {
            textView.setText(R.string.parent);
            textView.setVisibility(View.INVISIBLE);
        }
        gridView = (GridView) findViewById(R.id.contacts_parents);
        if (gridView != null) {
            gridViewHelper = new MyAdapterViewHelper(getActivity(),
                    gridView, R.layout.contacts_grid_item) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
                    if (data != null && data.isSelect()) {
                        showOnlinePersonFlag((ImageView) view.findViewById(R.id.circle_icon));
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                }
            };
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    gridViewHelper);
            this.parentsGridView = gridView;
        }


    }

    @Override
    protected void removeFromClass(ContactsClassMemberInfo info) {

    }

    private void loadContacts() {
        if (getUserInfo() == null || publishClass == null) {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", publishClass.getClassId());
        ContactsListFragment.DefaultPullToRefreshListener listener =
                new ContactsListFragment.DefaultPullToRefreshListener<ContactsClassMemberListResult>(
                        ContactsClassMemberListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
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
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_MEMBER_LIST_URL, params, listener);
    }

    private void updateViews(ContactsClassMemberListResult result) {
        List<ContactsClassMemberInfo> list = result.getModel().getClassMailListDetailList();
        if (list == null || list.size() <= 0) {
            return;
        }

        List teachers = new ArrayList();
        List students = new ArrayList();
        List parents = new ArrayList();
        List leavedTeachers = new ArrayList();
        List leavedStudents = new ArrayList();
        List leavedParents = new ArrayList();
        this.membersMap.clear();
        for (ContactsClassMemberInfo obj : list) {
            this.membersMap.put(obj.getMemberId(), obj);

            if (obj.isHeadTeacher()) {
                this.headTeacherInfo = obj;
                if (this.headTeacherId == null) {
                    this.headTeacherId = obj.getMemberId();
                }
            }

            if (obj.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                if (obj.getWorkingState() != 0) {
                    teachers.add(obj);
                } else {
                    leavedTeachers.add(obj);
                }
            } else if (obj.getRole() == RoleType.ROLE_TYPE_STUDENT) {
                if (obj.getWorkingState() != 0) {
                    students.add(obj);
                } else {
                    leavedStudents.add(obj);
                }
            } else if (obj.getRole() == RoleType.ROLE_TYPE_PARENT) {
                if (obj.getWorkingState() != 0) {
                    parents.add(obj);
                } else {
                    leavedParents.add(obj);
                }
            }
        }

        teachers.addAll(leavedTeachers);


        if (isHeadTeacher() && classStatus == ContactsClassManagementActivity.CLASS_STATUS_PRESENT) {
            ContactsClassMemberInfo info = new ContactsClassMemberInfo();
            info.setId(null);
            leavedStudents.add(info);
        }

        students.addAll(leavedStudents);
        parents.addAll(leavedParents);

        //判断当前的班级成员有没有在线的
        if (onlineNumber != null && onlineNumber.size() > 0) {
            compareAndSortOnlinePerson(teachers, teacherNumTextV);
            compareAndSortOnlinePerson(students, studentNumTextV);
            compareAndSortOnlinePerson(parents, parentNumTextV);
        }

        getAdapterViewHelper(String.valueOf(this.teachersGridView.getId())).setData(teachers);

        getAdapterViewHelper(String.valueOf(this.studentsGridView.getId())).setData(students);

        getAdapterViewHelper(String.valueOf(this.parentsGridView.getId())).setData(parents);

        showViews(true);

        showViewsByData(students, parents);
    }

    /**
     * 判断是否在线和重新进行排序
     *
     * @param infos
     * @param numTextV
     */
    private void compareAndSortOnlinePerson(List<ContactsClassMemberInfo> infos, TextView numTextV) {
        int count = 0;
        if (infos != null && infos.size() > 0) {
            for (int i = 0, len = infos.size(); i < len; i++) {
                ContactsClassMemberInfo info = infos.get(i);
                for (int j = 0, size = onlineNumber.size(); j < size; j++) {
                    OnlineNumber number = onlineNumber.get(j);
                    if (TextUtils.equals(number.getMemberId(), info.getMemberId())) {
                        info.setIsSelect(true);
                        count++;
                    }
                }
            }
            List<ContactsClassMemberInfo> tempData = infos;
            for (int i = 0, len = tempData.size(); i < len; i++) {
                ContactsClassMemberInfo memberInfo = tempData.get(i);
                if (memberInfo.isSelect()) {
                    infos.remove(memberInfo);
                    infos.add(0, memberInfo);
                }
            }
        }
        numTextV.setVisibility(View.VISIBLE);
        numTextV.setText(getString(R.string.str_online_num, count));
    }

    private void showViews(boolean show) {
        int visible = show ? View.VISIBLE : View.INVISIBLE;
        TextView textView = (TextView) findViewById(R.id.contacts_teachers_title);
        if (textView != null) {
            textView.setVisibility(visible);
        }
        textView = (TextView) findViewById(R.id.contacts_students_title);
        if (textView != null) {
            textView.setVisibility(visible);
        }

        textView = (TextView) findViewById(R.id.contacts_parents_title);
        if (textView != null) {
            textView.setVisibility(visible);
        }
    }

    private void showViewsByData(List<ContactsClassMemberInfo> students,
                                 List<ContactsClassMemberInfo> parents) {
        //家长
        View parentLayout = findViewById(R.id.layout_parent);
        if (parentLayout != null) {
            if (parents == null || parents.size() <= 0) {
                parentLayout.setVisibility(View.GONE);
            } else {
                parentLayout.setVisibility(View.VISIBLE);
            }
        }

        //学生
        View studentLayout = findViewById(R.id.layout_student);
        if (studentLayout != null) {
            if (students == null || students.size() <= 0) {
                studentLayout.setVisibility(View.GONE);
            } else {
                studentLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showOnlinePersonFlag(ImageView flag) {
        flag.setImageResource(R.drawable.online_person_flag_icon);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) flag.getLayoutParams();
        layoutParams.width = DensityUtils.dp2px(getActivity(), 10);
        layoutParams.height = DensityUtils.dp2px(getActivity(), 10);
        layoutParams.setMargins(0, 20, 20, 0);
        flag.setLayoutParams(layoutParams);
        flag.setScaleType(ImageView.ScaleType.CENTER_CROP);
        flag.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            //返回的按钮
            getActivity().finish();
        } else if (v.getId() == R.id.contacts_teachers_title_layout) {
            v.setSelected(!v.isSelected());
            this.teachersArrow.setImageResource(!v.isSelected() ?
                    R.drawable.list_exp_up : R.drawable.list_exp_down);
            this.teachersGridView.setVisibility(!v.isSelected() ?
                    View.VISIBLE : View.GONE);
        } else if (v.getId() == R.id.contacts_students_title_layout) {
            v.setSelected(!v.isSelected());
            this.studentsArrow.setImageResource(!v.isSelected() ?
                    R.drawable.list_exp_up : R.drawable.list_exp_down);
            this.studentsGridView.setVisibility(!v.isSelected() ?
                    View.VISIBLE : View.GONE);
        } else if (v.getId() == R.id.contacts_parents_title_layout) {
            v.setSelected(!v.isSelected());
            this.parentsArrow.setImageResource(!v.isSelected() ?
                    R.drawable.list_exp_up : R.drawable.list_exp_down);
            this.parentsGridView.setVisibility(!v.isSelected() ?
                    View.VISIBLE : View.GONE);
        } else if (v.getId() == R.id.tv_delete_btn) {
            //删除当前的发布对象
            popDeletePublishObjectDialog();
        }
    }

    private void popDeletePublishObjectDialog() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.str_confirm_delete_data,publishClass.getClassName()),
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
                        deleteCurrentPublishObject();
                        dialog.dismiss();
                    }
                });
        messageDialog.show();
    }

    private void deleteCurrentPublishObject() {
        if (publishClass == null) return;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", publishClass.getExtId());
        //删除直播对象
        params.put("ClassId", publishClass.getClassId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage = getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)) {
                                TipMsgHelper.ShowMsg(getActivity(), R.string.cs_delete_success);

                                Intent intent = new Intent();
                                intent.putExtra(Constant.CURRENT_CLASS_INFO,publishClass);
                                getActivity().setResult(Activity.RESULT_OK,intent);
                                getActivity().finish();
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_AIRCLASS_ONLINE_LIST_NEW_BASE_URL, params, listener);
    }

    private void checkClassPlayEnd(){
        if (publishClass == null) return;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("classIds", publishClass.getClassId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (!TextUtils.isEmpty(jsonString)){
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if (dataArray != null && dataArray.length() > 0){
                                    findViewById(R.id.tv_delete_btn).setVisibility(View.GONE);
                                }
                                JSONArray dataHisArray = jsonObject.getJSONArray("dataHis");
                                if (dataHisArray != null && dataHisArray.length() > 0){
                                    findViewById(R.id.tv_delete_btn).setVisibility(View.GONE);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_CHECK_TEACHING_PLAN_BASE_URL, params, listener);
    }
}
