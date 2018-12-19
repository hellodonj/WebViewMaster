package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.SlideListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassRequestInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassRequestListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsClassRequestListFragment extends ContactsListFragment {

    public static final String TAG = ContactsClassRequestListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
    }

    private String schoolId;
    private String classId;
    private ContactsClassRequestInfo request;
    private boolean agree;
    public static final int REQUEST_CODE_CONTACTS_CLASS_REQUEST_LIST = 608;
    private static boolean hasMessageHandled;
    private boolean hasCompeleted;//是否完成，用于避免重复点击。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_class_request_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData(){
        loadViews();
    }

    private void init() {
        if (getArguments() != null) {
            this.schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            this.classId = getArguments().getString(Constants.EXTRA_CLASS_ID);
        }
        initViews();
    }

    private void initViews() {
        final TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.approve);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
//            listView.setSlideMode(SlideListView.SlideMode.NONE);
//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    listView, R.layout.contacts_request_item) {
            SlideListViewHelper listViewHelper = new SlideListViewHelper(getActivity(),
                    listView, R.layout.contacts_request_item, 0,
                    R.layout.contacts_slide_list_item_delete) {
                @Override
                public void loadData() {
                    loadRequests();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final ContactsClassRequestInfo data =
                            (ContactsClassRequestInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_request_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnail(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView);
                        //点击头像进入个信息
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(data.getLQ_MemberId())) {
                                    ActivityUtils.enterPersonalSpace(getActivity(),
                                            data.getLQ_MemberId());
                                }
                            }
                        });
                    }
                    //显示姓名以及角色信息
                    TextView textView = (TextView) view.findViewById(R.id.contacts_request_item_title);
                    if (textView != null) {
                        StringBuilder builder = new StringBuilder();
                        if (data.isTeacher()) {
                            if (!TextUtils.isEmpty(data.getSubject())) {
                                builder.append(data.getSubject());
                            }
                            builder.append(getString(R.string.teacher));
                            if (!TextUtils.isEmpty(data.getApplicant())) {
                                builder.append(data.getApplicant());
                            }
                        } else if (data.isStudent()) {
                            builder.append(getString(R.string.student));
                            if (!TextUtils.isEmpty(data.getApplicant())) {
                                builder.append(data.getApplicant());
                            }
                        } else if (data.isParent()) {
                            if (!TextUtils.isEmpty(data.getStudentName())) {
                                builder.append(data.getStudentName());
                            }
                            if (data.isFather()) {
                                builder.append(getString(R.string.dad));
                            } else if (data.isMother()) {
                                builder.append(getString(R.string.mum));
                            } else {
                                builder.append(getString(R.string.parent));
                            }
                            if (!TextUtils.isEmpty(data.getApplicant())) {
                                builder.append(data.getApplicant());
                            }
                        }
                        textView.setText(builder.toString());
                    }
                    //显示加入的班级名称
                    textView = (TextView) view.findViewById(R.id.contacts_request_item_description);
                    if (textView != null) {
                        textView.setText(getString(R.string.apply_join) + data.getClassName());
                    }
                    //同意
                    Button agree = (Button) view.findViewById(R.id.contacts_request_item_button_agree);
                    if (agree != null) {
                        agree.setTag(holder);
                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                ViewHolder holder = (ViewHolder) v.getTag();
                                if (holder == null) {
                                    return;
                                }
                                //避免重复点击
                                if (!hasCompeleted) {
                                    hasCompeleted = true;
                                    agreeRequest((ContactsClassRequestInfo) holder.data, true);
                                }
//                                v.setEnabled(false);
//                                v.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        v.setEnabled(true);
//                                    }
//                                }, 1000);
                            }
                        });
                    }
                    //拒绝
                    Button reject = (Button) view.findViewById(R.id.contacts_request_item_button_reject);
                    if (reject != null) {
                        reject.setTag(holder);
                        reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                ViewHolder holder = (ViewHolder) v.getTag();
                                if (holder == null) {
                                    return;
                                }
                                if (!hasCompeleted){
                                    hasCompeleted = true;
                                    agreeRequest((ContactsClassRequestInfo) holder.data, false);
                                }
//                                v.setEnabled(false);
//                                v.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        v.setEnabled(true);
//                                    }
//                                }, 1000);
                            }
                        });
                    }
                    //显示申请的状态
                    textView = (TextView) view.findViewById(R.id.contacts_request_item_status);
                    if (data.getCheckState() == 0) {
                        // not approved
                        if (agree != null) {
                            agree.setVisibility(View.VISIBLE);
                            agree.setText(R.string.pass);
                        }
                        if (reject != null) {
                            reject.setVisibility(View.VISIBLE);
                            reject.setText(R.string.refuse);
                        }
                        if (textView != null) {
                            textView.setVisibility(View.GONE);
                        }
                    } else if (data.getCheckState() == 1) {
                        // approved
                        if (agree != null) {
                            agree.setVisibility(View.GONE);
                        }
                        if (reject != null) {
                            reject.setVisibility(View.GONE);
                        }
                        if (textView != null) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.passed);
                        }
                    } else if (data.getCheckState() == 2) {
                        // refused
                        if (agree != null) {
                            agree.setVisibility(View.GONE);
                        }
                        if (reject != null) {
                            reject.setVisibility(View.GONE);
                        }
                        if (textView != null) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.refused);
                        }
                    }
                    //侧滑删除
                    textView = (TextView) view.findViewById(R.id.contacts_item_delete);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            if (holder == null) {
                                return;
                            }
                            deleteRequest((ContactsClassRequestInfo) holder.data);
                            v.setEnabled(false);
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    v.setEnabled(true);
                                }
                            }, 1000);
                        }
                    });
                    textView.setTag(holder);
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadRequests();
        }
    }

    private void loadRequests() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        if (!TextUtils.isEmpty(this.schoolId)) {
            params.put("VersionCode", 1);
            params.put("SchoolId", this.schoolId);
        }
        if (!TextUtils.isEmpty(this.classId)){
            params.put("VersionCode", 2);
            params.put("ClassMailId", this.classId);
        }

        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassRequestListResult>(
                        ContactsClassRequestListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ContactsClassRequestListResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        updateViews(result);
                    }
                };
        postRequest(ServerUrl.CONTACTS_CLASS_REQUEST_LIST_URL, params, listener);
    }

    private void updateViews(ContactsClassRequestListResult result) {
        List<ContactsClassRequestInfo> list = result.getModel().getApplyJoinClassList();
        getCurrAdapterViewHelper().setData(list);
    }

    private void agreeRequest(ContactsClassRequestInfo data, boolean agree) {
        this.request = data;
        this.agree = agree;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", data.getId());
        params.put("CheckState", agree ? 1 : 2);
        params.put("VersionCode", 1);
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
//                    TipsHelper.showToast(getActivity(), R.string.process_request_failed);
                    return;
                }
                //通知校园空间去刷新数据
                MySchoolSpaceFragment.sendBrocast(getActivity());

                TipsHelper.showToast(getActivity(),
                        getString(ContactsClassRequestListFragment.this.agree ?
                                R.string.class_request_passed : R.string.class_request_refused));
                //设置标志位
                setHasMessageHandled(true);
                if (ContactsClassRequestListFragment.this.agree) {
                    ContactsClassRequestListFragment.this.request.setCheckState(1);
                } else {
                    ContactsClassRequestListFragment.this.request.setCheckState(2);
                }
                loadViews();
            }

                    @Override
                    public void onFinish() {
                        //重置
                        hasCompeleted = false;
                        super.onFinish();
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_REQUEST_PROCESS_URL, params, listener);
    }

    private void deleteRequest(final ContactsClassRequestInfo data) {
        this.request = data;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", data.getId());
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
                    return;
                }
                getCurrAdapterViewHelper().getData().remove(
                        ContactsClassRequestListFragment.this.request);
                //未审批状态删除才需要刷新
                if (data.getCheckState() == 0) {
                    //设置标志位
                    setHasMessageHandled(true);
                }
                loadViews();
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_REQUEST_DELETE_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void setHasMessageHandled(boolean hasMessageHandled) {
        ContactsClassRequestListFragment.hasMessageHandled = hasMessageHandled;
    }

    public static boolean hasMessageHandled() {
        return hasMessageHandled;
    }
}
