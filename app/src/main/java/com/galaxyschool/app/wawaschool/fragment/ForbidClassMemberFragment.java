package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForbidClassMemberFragment extends ContactsPickerFragment
        implements View.OnClickListener {

    public static final String TAG = ForbidClassMemberFragment.class.getSimpleName();

    public interface Constants {
        public static final int REUEST_CODE_FORBID_CLASS_MEMBER = 5901;
        public static final String REUEST_DATA_FORBIDDEN_MEMBERS = "forbiddenMembers";
        public static final String REUEST_DATA_ALLOWED_MEMBERS = "allowedMembers";

        public static final String EXTRA_CONTACT_ID = "contactId";
        public static final String EXTRA_CONTACT_LIST = "contactList";
    }

    private String contactId;
    private GridView teachersGridView, studentsGridView, parentsGridView;
    private View selectAllTeachersView, selectAllStudentsView, selectAllParentsView;
    private Map<String, ContactsClassMemberInfo> membersMap = new HashMap();
    private Map<String, ContactsClassMemberInfo> defaultSelectedMembers = new HashMap();
    private Map<Integer, Boolean> defaultSelectedItems = new HashMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_forbid_class_member, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        finish();
        return true;
    }

    @Override
    public void finish() {
        getActivity().setResult(getResultCode(), getResultData());
        super.finish();
    }

    private void initViews() {
        this.contactId = getArguments().getString(Constants.EXTRA_CONTACT_ID);

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.forbid_class_chat);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.contacts_teachers_title);
        if (textView != null) {
            textView.setText(R.string.teacher);
            textView.setVisibility(View.VISIBLE);
        }
        View view = findViewById(R.id.contacts_teachers_select_all);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllTeachers(!selectAllTeachersView.isSelected());
            }
        });
        this.selectAllTeachersView = view.findViewById(R.id.contacts_select_all_icon);
        MyAdapterViewHelper adapterViewHelper = null;
        GridView gridView = (GridView) findViewById(R.id.contacts_teachers);
        if (gridView != null) {
            adapterViewHelper = new MyAdapterViewHelper(getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadContacts();
                }
            };
            adapterViewHelper.setPositionOffset(0);
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    adapterViewHelper);
        }
        gridView.setTag(this.selectAllTeachersView);
        this.teachersGridView = gridView;

        textView = (TextView) findViewById(R.id.contacts_students_title);
        if (textView != null) {
            textView.setText(R.string.student);
            textView.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_students_select_all);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllStudents(!selectAllStudentsView.isSelected());
            }
        });
        this.selectAllStudentsView = view.findViewById(R.id.contacts_select_all_icon);
        gridView = (GridView) findViewById(R.id.contacts_students);
        if (gridView != null) {
            adapterViewHelper = new MyAdapterViewHelper(getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadContacts();
                }
            };
            adapterViewHelper.setPositionOffset(10000);
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    adapterViewHelper);
        }
        gridView.setTag(this.selectAllStudentsView);
        this.studentsGridView = gridView;

        textView = (TextView) findViewById(R.id.contacts_parents_title);
        if (textView != null) {
            textView.setText(R.string.parent);
            textView.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_parents_select_all);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllParents(!selectAllParentsView.isSelected());
            }
        });
        this.selectAllParentsView = view.findViewById(R.id.contacts_select_all_icon);
        gridView = (GridView) findViewById(R.id.contacts_parents);
        if (gridView != null) {
            adapterViewHelper = new MyAdapterViewHelper(getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadContacts();
                }
            };
            adapterViewHelper.setPositionOffset(20000);
            addAdapterViewHelper(String.valueOf(gridView.getId()),
                    adapterViewHelper);
        }
        gridView.setTag(this.selectAllParentsView);
        this.parentsGridView = gridView;

        setTreatAllHelpersAsOne(true);

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
                        forbidMembers();
                    }
                });
            }
            view.setVisibility(View.VISIBLE);
        }
    }

    private void loadViews() {
        AdapterViewHelper teachersHelper = getAdapterViewHelper(
                String.valueOf(this.teachersGridView.getId()));
        AdapterViewHelper studentsHelper = getAdapterViewHelper(
                String.valueOf(this.studentsGridView.getId()));
        AdapterViewHelper parentsHelper = getAdapterViewHelper(
                String.valueOf(this.parentsGridView.getId()));
        if (teachersHelper.hasData() || studentsHelper.hasData() || parentsHelper.hasData()) {
            teachersHelper.update();
            studentsHelper.update();
            parentsHelper.update();
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        List<ContactsClassMemberInfo> list = getArguments().getParcelableArrayList(
                Constants.EXTRA_CONTACT_LIST);
        if (list == null && list.size() <= 0) {
            return;
        }

        List teachers = new ArrayList();
        List students = new ArrayList();
        List parents = new ArrayList();
        List l = null;

        UserInfo userInfo = getUserInfo();
        MyAdapterViewHelper teachersHelper = (MyAdapterViewHelper) getAdapterViewHelper(
                String.valueOf(this.teachersGridView.getId()));
        MyAdapterViewHelper studentsHelper = (MyAdapterViewHelper) getAdapterViewHelper(
                String.valueOf(this.studentsGridView.getId()));
        MyAdapterViewHelper parentsHelper = (MyAdapterViewHelper) getAdapterViewHelper(
                String.valueOf(this.parentsGridView.getId()));
        MyAdapterViewHelper helper = null;
        for (ContactsClassMemberInfo obj : list) {
            if (userInfo != null && userInfo.getMemberId().equals(obj.getMemberId())) {
                continue;
            }

            if (obj.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                teachers.add(obj);
                l = teachers;
                helper = teachersHelper;
            } else if (obj.getRole() == RoleType.ROLE_TYPE_STUDENT) {
                students.add(obj);
                l = students;
                helper = studentsHelper;
            } else if (obj.getRole() == RoleType.ROLE_TYPE_PARENT) {
                parents.add(obj);
                l = parents;
                helper = parentsHelper;
            } else {
                continue;
            }
            this.membersMap.put(obj.getMemberId(), obj);

            if (obj.isChatForbidden()) {
                this.defaultSelectedMembers.put(obj.getMemberId(), obj);
                int position = l.size() - 1 + helper.getPositionOffset();
                selectItem(position, true);
                this.defaultSelectedItems.put(position, true);
            }
        }

        teachersHelper.setData(teachers);
        studentsHelper.setData(students);
        parentsHelper.setData(parents);

        if (teachers.size() > 0) {
            findViewById(R.id.contacts_teachers_layout).setVisibility(View.VISIBLE);
            this.selectAllTeachersView.setSelected(isAllTeachersSelected());
        }
        if (students.size() > 0) {
            findViewById(R.id.contacts_students_layout).setVisibility(View.VISIBLE);
            this.selectAllStudentsView.setSelected(isAllStudentsSelected());
        }
        if (parents.size() > 0) {
            findViewById(R.id.contacts_parents_layout).setVisibility(View.VISIBLE);
            this.selectAllParentsView.setSelected(isAllParentsSelected());
        }

        notifyPickerBar(hasSelectedItems(), false);
    }

    private void notifyPickerBar() {
        if (this.defaultSelectedItems.size() != getSelectedItemsCount()) {
            notifyPickerBar(hasSelectedItems(), true);
            return;
        }

        boolean changed = false;
        for (Map.Entry<Integer, Boolean> entry : this.defaultSelectedItems.entrySet()) {
            if (!isItemSelected(entry.getKey())) {
                changed = true;
            }
        }
        notifyPickerBar(hasSelectedItems(), changed);
    }

    private void notifyPickerBar(boolean hasSelectedItems,
                                 boolean defaultSelectedItemsChanged) {
        View view = findViewById(R.id.contacts_picker_bar_layout);
        if (view != null) {
            TextView textView = (TextView) view.findViewById(
                    R.id.contacts_picker_clear);
            if (textView != null) {
                textView.setEnabled(hasSelectedItems);
            }
            textView = (TextView) view.findViewById(
                    R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setEnabled(defaultSelectedItemsChanged);
            }
        }
    }

    private void selectAllTeachers(boolean selected) {
        selectAllContacts(this.teachersGridView, selected);
    }

    private void selectAllStudents(boolean selected) {
        selectAllContacts(this.studentsGridView, selected);
    }

    private void selectAllParents(boolean selected) {
        selectAllContacts(this.parentsGridView, selected);
    }

    private void selectAllContacts(boolean selected) {
        selectAllTeachers(selected);
        selectAllStudents(selected);
        selectAllParents(selected);
    }

    private void selectAllContacts(AdapterView adapterView, boolean selected) {
        View view = (View) adapterView.getTag();
        if (view != null) {
            view.setSelected(selected);
        }
        MyAdapterViewHelper helper = (MyAdapterViewHelper) getAdapterViewHelper(
                String.valueOf(adapterView.getId()));
        selectAllItems(helper, selected);
        helper.update();
        notifyPickerBar();
    }

    private boolean isAllTeachersSelected() {
        return isAllContactsSelected(this.teachersGridView);
    }

    private boolean isAllStudentsSelected() {
        return isAllContactsSelected(this.studentsGridView);
    }

    private boolean isAllParentsSelected() {
        return isAllContactsSelected(this.parentsGridView);
    }

    private boolean isAllContactsSelected(AdapterView adapterView) {
        MyAdapterViewHelper helper = (MyAdapterViewHelper) getAdapterViewHelper(
                String.valueOf(adapterView.getId()));

        return isAllItemsSelected(helper);
    }

    private void forbidMembers() {
        List<ContactsClassMemberInfo> selectedItems = getSelectedItems();
//        if (selectedItems == null || selectedItems.size() <= 0) {
//            TipsHelper.showToast(getActivity(),
//                    R.string.pls_select_a_student_at_least);
//            return;
//        }

        ArrayList<ContactsClassMemberInfo> forbidMembers = new ArrayList();
        ArrayList<ContactsClassMemberInfo> allowMembers = new ArrayList();
        Map<String, ContactsClassMemberInfo> selectedMembers = new HashMap();
        for (ContactsClassMemberInfo item : selectedItems) {
            selectedMembers.put(item.getMemberId(), item);
            if (!this.defaultSelectedMembers.containsKey(item.getMemberId())) {
                forbidMembers.add(item);
            }
        }

        for (Map.Entry<String, ContactsClassMemberInfo> entry : this.defaultSelectedMembers.entrySet()) {
            if (!selectedMembers.containsKey(entry.getValue().getMemberId())) {
                allowMembers.add(entry.getValue());
            }
        }

        StringBuilder sb = new StringBuilder();
        if (forbidMembers.size() > 0) {
            for (ContactsClassMemberInfo obj : forbidMembers) {
                sb.append(obj.getMemberId()).append("#");
            }
            sb.setLength(sb.length() - 1);
        }
        String forbidIds = sb.toString();
        sb.setLength(0);
        if (allowMembers.size() > 0) {
            for (ContactsClassMemberInfo obj : allowMembers) {
                sb.append(obj.getMemberId()).append("#");
            }
            sb.setLength(sb.length() - 1);
        }
        String allowIds = sb.toString();

        Map<String, Object> params = new HashMap();
        params.put("Id", this.contactId);
        params.put("GagPersonnel", forbidIds);
        params.put("BanPersonnel", allowIds);
        DefaultListener listener = new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    TipsHelper.showToast(getActivity(), R.string.operation_failure);
                } else {
                    TipsHelper.showToast(getActivity(), R.string.operation_success);
                }
                Intent intent = new Intent();
                intent.putExtras((Bundle) getTarget());
                ForbidClassMemberFragment.this.setResult(Activity.RESULT_OK, intent);
                finish();
            }
        };
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.REUEST_DATA_FORBIDDEN_MEMBERS, forbidMembers);
        args.putParcelableArrayList(Constants.REUEST_DATA_ALLOWED_MEMBERS, allowMembers);
        listener.setTarget(args);
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_FORBID_CLASS_MEMBER_CHAT_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            forbidMembers();
        } else {
            super.onClick(v);
        }
    }

    private abstract class MyAdapterViewHelper extends AdapterViewHelper {

        public MyAdapterViewHelper(Context context, AdapterView adapterView) {
            super(context, adapterView, R.layout.contacts_grid_item_with_selector);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ContactsClassMemberInfo data = (ContactsClassMemberInfo) getDataAdapter().getItem(position);
            if (data == null) {
                return view;
            }
            MyViewHolder holder = (MyViewHolder) view.getTag();
//            if (holder == null) {
                holder = new MyViewHolder();
//            }
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
            textView = (TextView) view.findViewById(R.id.contacts_item_indicator);
            if (textView != null) {
                textView.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                if (data.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                    if (data.getWorkingState() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.not_work_here));
                        textView.setBackgroundResource(R.drawable.teacher_leaved);
                        textView.setLayoutParams(params);
                    } else if (data.isHeadTeacher()) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.header_teacher));
                        textView.setBackgroundResource(R.drawable.teacher_header);
                        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        textView.setLayoutParams(params);
                    }
                } else if (data.getRole() == RoleType.ROLE_TYPE_STUDENT) {
                    if (data.getWorkingState() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(getString(R.string.not_study_here));
                        textView.setBackgroundResource(R.drawable.teacher_leaved);
                        textView.setLayoutParams(params);
                    }
                }
            }
            imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
            if (imageView != null) {
                holder.selectorView = imageView;
                imageView.setSelected(isItemSelected(position + getPositionOffset()));
            }
            view.setTag(holder);
            return view;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            MyViewHolder holder = (MyViewHolder) view.getTag();
            if (holder == null) {
                return;
            }
            boolean selected = !isItemSelected(position + getPositionOffset());
            selectItem(position + getPositionOffset(), selected);
            holder.selectorView.setSelected(selected);
            View selectorAllView = (View) parent.getTag();
            if (selectorAllView != null) {
                selectorAllView.setSelected(isAllContactsSelected(parent));
            }
            notifyPickerBar();
            getDataAdapter().notifyDataSetChanged();
        }
    };

}
