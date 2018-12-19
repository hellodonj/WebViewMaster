package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.ClassMemberInfoResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberDetails;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberDetailsListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMemberDetailsFragment extends ContactsListFragment
        implements View.OnClickListener {

    public static final String TAG = GroupMemberDetailsFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_MEMBER_ROLE = "role";
        public static final String EXTRA_MEMBER_ID = "id";
    }

    private int role;
    private String id;
    private List<ContactsClassMemberDetails> details;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.role = getArguments().getInt(Constants.EXTRA_MEMBER_ROLE);
        this.id = getArguments().getString(Constants.EXTRA_MEMBER_ID);
//        int layout = 0;
//        if (this.role == RoleType.ROLE_TYPE_TEACHER) {
//            layout = R.layout.contacts_teacher_info;
//        } else if (this.role == RoleType.ROLE_TYPE_STUDENT) {
//            layout = R.layout.contacts_student_info;
//        } else if (this.role == RoleType.ROLE_TYPE_PARENT) {
//            layout = R.layout.contacts_parent_info;
//        }

//        View view = null;
//        if (layout > 0) {
//            view = inflater.inflate(layout, null);
//        }

        return inflater.inflate(R.layout.class_member_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    private void init() {
        initTitle();
//        showViews(false);
//        loadMemberDetails();
        initBasicView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadMemberDetails();
        }
    }


    private void initBasicView() {

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ListView listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {

            AdapterViewHelper adapterViewHelper = new ClassMemberInfoResourceAdapterViewHelper(
                    getActivity(),listView) {
                @Override
                public void loadData() {

                    loadMemberDetails();
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }



    void initTitle() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (this.role == RoleType.ROLE_TYPE_TEACHER) {
            if (textView != null) {
                textView.setText(R.string.teacher_info);
            }
        } else if (this.role == RoleType.ROLE_TYPE_STUDENT) {
            if (textView != null) {
                textView.setText(R.string.student_info);
            }
        } else if (this.role == RoleType.ROLE_TYPE_PARENT) {
            if (textView != null) {
                textView.setText(R.string.parent_info);
            }
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
    }


    private void loadMemberDetails() {
        Map<String, Object> params = new HashMap();
        params.put("Id", this.id);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassMemberDetailsListResult>(
                        ContactsClassMemberDetailsListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsClassMemberDetailsListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
//        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_MEMBER_DETAILS_URL, params, listener);
    }

    private void updateViews(ContactsClassMemberDetailsListResult result) {
        List<ContactsClassMemberDetails> list = result.getModel().getPersonalList();
        if (list == null || list.size() <= 0) {
            return;
        }else {
            getCurrAdapterViewHelper().setData(list);
        }

//        this.details = list;
//        showViews(true);
//        initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            getActivity().finish();
        }
    }

}
