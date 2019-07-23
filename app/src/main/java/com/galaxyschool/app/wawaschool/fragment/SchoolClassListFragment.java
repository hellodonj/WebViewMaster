package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ClassDetailsActivity;
import com.galaxyschool.app.wawaschool.ClassSpaceActivity;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsClassRequestListActivity;
import com.galaxyschool.app.wawaschool.ContactsCreateClassActivity;
import com.galaxyschool.app.wawaschool.QrcodeProcessActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassRequestInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassRequestListResult;
import com.galaxyschool.app.wawaschool.pojo.QrcodeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.QrcodeSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeGradeInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeGradeListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SchoolClassListFragment extends ContactsExpandListFragment
        implements View.OnClickListener {

    public static final String TAG = SchoolClassListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_IS_NEED_SHOW_MENU = "isNeedShowMenu";

        int MENU_ID_CREATE_CLASS = 0;
        int MENU_ID_CLASS_REQUESTS = 1;
    }

    private String schoolId;
    private String schoolName;
    private SchoolInfo schoolInfo;
    private boolean isTeacher;
    private View headerView;
    private ListView classListView, gradeListView;
    private AdapterViewHelper classListViewHelper;
    private View classListLayout, gradeListLayout;
    private SubscribeGradeListResult gradeListResult;
    private SubscribeClassInfo classInfo;
    private boolean isNeedShowMenu;
    private TextView indicatorView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_class_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        refreshData();
        loadSchoolInfo();
        addEventBusReceiver();
    }

    private void refreshData() {
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void clearData() {
        gradeListResult = null;
    }

    private void init() {
        schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
        schoolName = getArguments().getString(Constants.EXTRA_SCHOOL_NAME);
        isTeacher = getArguments().getBoolean(Constants.EXTRA_IS_TEACHER);
        isNeedShowMenu = getArguments().getBoolean(Constants.EXTRA_IS_NEED_SHOW_MENU);
        schoolInfo = (SchoolInfo) getArguments().getSerializable(ActivityUtils.EXTRA_SCHOOL_INFO);
        initViews();
    }

    private void enterGroupQrCode(SubscribeClassInfo classInfo, boolean hasJoin) {
        if (classInfo == null) {
            return;
        }
        if (classInfo.isClass()) {
            ActivityUtils.enterClassDetialActivity(getActivity(), classInfo
                            .getClassId(), ClassDetailsActivity
                            .FROM_TYPE_CLASS_HEAD_PIC, hasJoin, schoolName, classInfo.getClassMailListId(),
                    classInfo.getSchoolId(), classInfo.getGroupId(), classInfo.getIsHistory(), schoolInfo);
        } else {
            if (hasJoin) {
                enterSchoolContact(classInfo);
            } else {
                ActivityUtils.enterClassDetialActivity(getActivity(), classInfo
                                .getClassId(), ClassDetailsActivity
                                .FROM_TYPE_TEACHER_CONTACT, hasJoin, schoolName, classInfo.getClassMailListId(),
                        classInfo.getSchoolId(), classInfo.getGroupId());
            }
//
//            if (hasJoin) {
//                enterSchoolContact(classInfo);//进入老师通讯录
//            } else {
//                joinSchool(null, classInfo);//加入学校
//            }
        }
    }

    /**etView
     * 进入老师通讯录
     *
     * @param classInfo
     */
    private void enterSchoolContact(SubscribeClassInfo classInfo) {
        Bundle args = new Bundle();
        args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, 1);
        args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolName);
        args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
        if (schoolInfo != null) {
            args.putBoolean(ContactsActivity.EXTRA_CONTACTS_HAS_INSPECT_AUTH, schoolInfo.isSchoolInspector());
        }
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(schoolName);
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setVisibility(View.INVISIBLE);
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setVisibility(View.INVISIBLE);
//            imageView.setImageResource(R.drawable.nav_more_icon);
//            imageView.setOnClickListener(this);
//            if (isNeedShowMenu && this.isTeacher) {
//                imageView.setVisibility(View.VISIBLE);
//            } else {
//                imageView.setVisibility(View.GONE);
//            }
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.grade_list_view);
        if (listView != null) {
            headerView = null;
            initListHeader(listView, null);

            listView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.contacts_search_expand_list_item,
                    R.layout.contacts_expand_list_child_item) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((SubscribeGradeInfo) getData().get(groupPosition))
                            .getClassList().get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        SubscribeGradeInfo gradeInfo = (SubscribeGradeInfo)
                                getData().get(groupPosition);
                        if (gradeInfo != null && gradeInfo.getClassList() != null) {
                            return gradeInfo.getClassList().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);

                    final SubscribeClassInfo data = (SubscribeClassInfo) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                        view.setTag(holder);
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_class_icon);
                        //点击头像进入班级二维码界面。
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                enterGroupQrCode(data, false);
                            }
                        });
                    }

                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getClassName());
                    }

                    textView = (TextView) view.findViewById(R.id.contacts_item_status);
                    if (textView != null) {
                        textView.setTag(holder);
                        if (data.getIsjoin()) {
                            textView.setText(R.string.joined);
                            textView.setTextColor(getResources().getColor(
                                    R.color.text_dark_gray));
                            textView.getPaint().setFlags(textView.getPaintFlags()
                                    & (~Paint.UNDERLINE_TEXT_FLAG));
                            textView.setOnClickListener(null);
                        } else {
//                            if (data.isClass()) {
//                                textView.setText(R.string.join_class);
//                            } else if (data.isSchool()) {
//                                textView.setText(R.string.join_school);
//                            }
                            textView.setText(R.string.join);
                            textView.setTextColor(Color.parseColor("#009039"));
                            textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);
//                            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                            textView.setOnClickListener(SchoolClassListFragment.this);
                        }
                    }

                    LinearLayout payLayout = (LinearLayout) view.findViewById(R.id.ll_pay_detail);
                    TextView wawaPayNumView = (TextView) view.findViewById(R.id.tv_wawa_coin_count);
                    if (payLayout != null && wawaPayNumView != null){
                        if (data.getPrice() > 0){
                            payLayout.setVisibility(View.VISIBLE);
                            wawaPayNumView.setText(String.valueOf(data.getPrice()));
                        } else {
                            payLayout.setVisibility(View.GONE);
                        }
                    }

                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    SubscribeGradeInfo data = (SubscribeGradeInfo) getGroup(groupPosition);
                    View headerView = view.findViewById(R.id.contacts_item_header_layout);
                    if (headerView != null) {
                        headerView.setVisibility(View.GONE);
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getLevelGName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        imageView.setImageResource(isExpanded ?
                                R.drawable.list_exp_up : R.drawable.list_exp_down);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                        view.setTag(holder);
                    }
                    holder.data = data;

                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    listView, dataAdapter) {
                @Override
                public void loadData() {
                    refreshData();
//                    initListHeader(listView, null);
//                    classListViewHelper.clearData();
//                    classListLayout.setVisibility(View.GONE);
//                    gradeListLayout.setVisibility(View.GONE);
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (holder == null) {
                        return false;
                    }
                    SubscribeGradeInfo gradeInfo = (SubscribeGradeInfo)
                            getDataAdapter().getGroup(groupPosition);
                    SubscribeClassInfo classInfo = (SubscribeClassInfo) holder.data;
                    if (classInfo.getIsjoin()) {
                        enterClassSpace(classInfo);
                    } else {
                        enterGroupQrCode(classInfo, false);
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(listView, listViewHelper);
            gradeListView = listView;
        }

        ListView classListView = (ListView) findViewById(R.id.class_list_view);
        if (classListView != null) {

            if (classListView.getHeaderViewsCount() <= 0) {
                if (this.isTeacher) {
                    //应该判断该用户是否是该学校的老师，而不是仅判断是否是老师。
                    View headerView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.contacts_list_item_with_indicator, null);
                    imageView = (ImageView) headerView.findViewById(R.id.contacts_item_icon);
                    imageView.setImageResource(R.drawable.create_class_ico);
                    textView = (TextView) headerView.findViewById(R.id.contacts_item_title);
                    textView.setText(R.string.create_class);
                    classListView.addHeaderView(headerView);
                    headerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterCreateClass();
                        }
                    });
                }

                if (this.isTeacher) {
                    //只判断是老师就行了
                    View headerView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.contacts_list_item_with_indicator, null);
                    imageView = (ImageView) headerView.findViewById(R.id.contacts_item_icon);
                    imageView.setImageResource(R.drawable.approve_class_ico);
                    textView = (TextView) headerView.findViewById(R.id.contacts_item_title);
                    textView.setText(R.string.class_request);
                    textView = (TextView) headerView.findViewById(R.id.contacts_item_indicator);
                    this.indicatorView = textView;
                    classListView.addHeaderView(headerView);
                    headerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterClassRequests();
                        }
                    });
                }
                View headerView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_school_class_joined_textview, null);
                classListView.addHeaderView(headerView);
            }


            AdapterViewHelper classListViewHelper = new AdapterViewHelper(getActivity(),
                    classListView, R.layout.contacts_list_subitem) {
                @Override
                public void loadData() {

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final SubscribeClassInfo data = (SubscribeClassInfo) getDataAdapter().getItem(position);
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_class_icon);
                        //点击头像进入班级二维码
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                enterGroupQrCode(data,true);

                                if (data.isClass()) {
                                    ActivityUtils.enterClassDetialActivity(getActivity(), data
                                                    .getClassId(), ClassDetailsActivity
                                                    .FROM_TYPE_CLASS_HEAD_PIC, true, schoolName, data.getClassMailListId(),
                                            data.getSchoolId(), data.getGroupId(), data.getIsHistory(), schoolInfo);
                                } else {
                                    ActivityUtils.enterClassDetialActivity(getActivity(), data
                                                    .getClassId(), ClassDetailsActivity
                                                    .FROM_TYPE_TEACHER_CONTACT, true, schoolName, data.getClassMailListId(),
                                            data.getSchoolId(), data.getGroupId(), schoolInfo.isSchoolInspector());

                                }
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getGradeName() != null ?
                                data.getGradeName() + data.getClassName() :
                                data.getClassName());
                    }

                    LinearLayout payLayout = (LinearLayout) view.findViewById(R.id.ll_pay_detail);
                    TextView wawaPayNumView = (TextView) view.findViewById(R.id.tv_wawa_coin_count);
                    if (payLayout != null && wawaPayNumView != null){
                        if (data.getPrice() > 0){
                            payLayout.setVisibility(View.VISIBLE);
                            wawaPayNumView.setText(String.valueOf(data.getPrice()));
                        } else {
                            payLayout.setVisibility(View.GONE);
                        }
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                        view.setTag(holder);
                    }
                    holder.data = data;
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    SubscribeClassInfo classInfo = (SubscribeClassInfo) holder.data;
                    enterGroupQrCode(classInfo, true);
//                    if (classInfo.isClass()) {
//                        enterClassSpace(classInfo);
//                    } else if (classInfo.isSchool()) {
//                        enterGroupMembers(null, classInfo);
//                    }
                    SchoolClassListFragment.this.classInfo = classInfo;
                }
            };
            this.classListView = classListView;
            this.classListViewHelper = classListViewHelper;
        }
        classListLayout = findViewById(R.id.class_list_layout);
        gradeListLayout = findViewById(R.id.grade_list_layout);
        classListLayout.setVisibility(View.GONE);
        gradeListLayout.setVisibility(View.GONE);
    }

    private void initListHeader(ListView listView, final SubscribeClassInfo classInfo) {
        View view = headerView;
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.contacts_search_expand_list_header, listView, false);
            headerView = view;
            view.setTag(new MyViewHolder());
            listView.addHeaderView(view);

        }
        if (classInfo == null) {
            view.findViewById(R.id.contacts_search_list_header_layout)
                    .setVisibility(View.GONE);
            listView.setHeaderDividersEnabled(false);
            return;
        } else {
            view.findViewById(R.id.contacts_search_list_header_layout)
                    .setVisibility(View.VISIBLE);
            listView.setHeaderDividersEnabled(true);
        }
        final MyViewHolder holder = (MyViewHolder) view.getTag();
        holder.data = classInfo;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyViewHolder holder = (MyViewHolder) v.getTag();
                if (holder == null) {
                    return;
                }
                SubscribeClassInfo classInfo = holder.data;
                if (classInfo.getIsjoin()) {
                    enterGroupMembers(null, classInfo);
                } else {
                    enterGroupQrCode(classInfo, false);
                }
            }
        });
        ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
        if (imageView != null) {
            getThumbnailManager().displayUserIconWithDefault(
                    AppSettings.getFileUrl(classInfo.getHeadPicUrl()), imageView,
                    R.drawable.default_class_icon);
            //进入班级二维码
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterGroupQrCode(classInfo, false);
                }
            });
        }
        TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
        if (textView != null) {
            if (classInfo != null) {
                textView.setText(classInfo.getClassName());
            }
        }
        textView = (TextView) view.findViewById(R.id.contacts_item_status);
        if (textView != null) {
            textView.setTag(holder);
            if (classInfo != null) {
                if (classInfo.getIsjoin()) {
                    textView.setText(R.string.joined);
                    textView.setTextColor(getResources().getColor(
                            R.color.text_dark_gray));
                    textView.getPaint().setFlags(textView.getPaintFlags()
                            & (~Paint.UNDERLINE_TEXT_FLAG));
                    textView.setOnClickListener(null);
                } else {
//                    if (classInfo.isClass()) {
//                        textView.setText(R.string.join);
//                    } else if (classInfo.isSchool()) {
//                        textView.setText(R.string.join_school);
//                    }
                    textView.setText(R.string.join);
                    textView.setTextColor(Color.parseColor("#009039"));
                    textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);
//                    textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    textView.setOnClickListener(SchoolClassListFragment.this);
                }
            }
        }
    }

    private void loadViews() {
        if (isTeacher) {
            loadRequests();
        }
        loadGrades();
    }

    private void loadRequests() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        if (!TextUtils.isEmpty(this.schoolId)) {
            params.put("VersionCode", 1);
            params.put("SchoolId", this.schoolId);
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
                        //处理未读提醒
                        List<ContactsClassRequestInfo> list = result.getModel()
                                .getApplyJoinClassList();
                        int count = 0;
                        if (list != null && list.size() > 0) {
                            for (ContactsClassRequestInfo info : list) {
                                if (info != null) {
                                    if (info.getCheckState() == 0) {
                                        //未审批
                                        count++;
                                    }
                                }
                            }
                        }
                        if (count > 0) {
                            String str = null;
                            if (count > 99) {
                                str = "99+";
                            } else {
                                str = String.valueOf(count);
                            }
                            if (indicatorView != null) {
                                indicatorView.setVisibility(View.VISIBLE);
                                indicatorView.setText(str);
                            }
                        } else {
                            if (indicatorView != null) {
                                indicatorView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                };
        listener.setShowPullToRefresh(false);
        postRequest(ServerUrl.CONTACTS_CLASS_REQUEST_LIST_URL, params, listener);
    }

    private void loadGrades() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<SubscribeGradeListResult>(
                        SubscribeGradeListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeGradeListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateGrades(result);

                    }
                };
        listener.setShowPullToRefresh(true);
        postRequest(ServerUrl.SUBSCRIBE_CLASS_LIST_URL, params, listener);
    }

    private void updateGrades(SubscribeGradeListResult result) {
        List<SubscribeClassInfo> classList = new ArrayList();
        SubscribeClassInfo classInfo = result.getModel().getTeacherBook();
        boolean showGradeList = false;
        if (classInfo != null) {
            if (classInfo.getIsjoin()) {
                classList.add(classInfo);
            } else {
                initListHeader(getCurrListView(), classInfo);
                showGradeList = true;
            }
        }
//        List<SubscribeGradeInfo> gradeList = new ArrayList();
//        List<SubscribeGradeInfo> grades = result.getModel().getLevelGList();
//        if (grades != null && grades.size() > 0) {
//            SubscribeGradeInfo gradeInfo = null;
//            Iterator<SubscribeGradeInfo> gIterator = grades.iterator();
//            while (gIterator.hasNext()) {
//                gradeInfo = gIterator.next();
//                if (gradeInfo.getClassList() == null
//                        || gradeInfo.getClassList().size() <= 0) {
//                    continue;
//                }
//                Iterator<SubscribeClassInfo> cIterator = gradeInfo.getClassList().iterator();
//                while (cIterator.hasNext()) {
//                    classInfo = cIterator.next();
//                    classInfo.setSchoolId(schoolId);
//                    classInfo.setSchoolName(schoolName);
//                    classInfo.setGradeId(gradeInfo.getLevelGId());
//                    classInfo.setGradeName(gradeInfo.getLevelGName());
//                    if (classInfo.getIsjoin()) {
//                        classList.add(classInfo);
//                        cIterator.remove();
//                    }
//                }
//                if (gradeInfo.getClassList().size() > 0) {
//                    gradeList.add(gradeInfo);
//                }
//            }
//        }
        if (result.getModel().getHbaddedClassList() != null) {
            SubscribeClassInfo tempTeachSubs = new SubscribeClassInfo();
            List<SubscribeClassInfo> haddeClassList = result.getModel().getHbaddedClassList();
            if (haddeClassList != null && haddeClassList.size() > 0) {
                for (int i = 0; i < haddeClassList.size(); i++) {
                    SubscribeClassInfo info = haddeClassList.get(i);
                    String classId = info.getClassId();
                    if (!TextUtils.isEmpty(classId)) {
                        String[] tagArray = classId.split("-");
                        String tag = tagArray[tagArray.length - 1];
                        if ("000000000000".equals(tag)) {
                            tempTeachSubs = info;
                            haddeClassList.remove(info);
                        }
                    }
                }
                if (!TextUtils.isEmpty(tempTeachSubs.getClassName()) && !TextUtils.isEmpty
                        (tempTeachSubs.getClassMailListId())) {
                    haddeClassList.add(0, tempTeachSubs);
                }
            }
            classList.addAll(haddeClassList);
        }
        if (classList != null && classList.size() > 0) {
            classListViewHelper.setData(classList);
            classListLayout.setVisibility(View.VISIBLE);
        } else {

            classListLayout.setVisibility(View.GONE);
        }
        List<SubscribeGradeInfo> gradeList = result.getModel().getNaddedClassList();
        if (gradeList != null && gradeList.size() > 0) {
            getCurrListViewHelper().setData(gradeList);
            getCurrListView().expandGroup(0);
            gradeListLayout.setVisibility(View.VISIBLE);
        } else {
            gradeListLayout.setVisibility(showGradeList ? View.VISIBLE : View.GONE);
        }

        gradeListResult = result;
    }

    private void enterClassSpace(SubscribeClassInfo classInfo) {
        classInfo.setSchoolName(schoolName);
        Bundle args = new Bundle();
        if (classInfo != null) {
            args.putString(ClassSpaceActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        }
//        args.putSerializable(SubscribeClassInfo.class.getSimpleName(), classInfo);
        Intent intent = new Intent(getActivity(), ClassSpaceActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent,
                ClassSpaceActivity.REQUEST_CODE_CLASS_SPACE);
    }

    private void enterGroupMembers(SubscribeGradeInfo gradeInfo, SubscribeClassInfo classInfo) {
        Bundle args = new Bundle();
        args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classInfo.getType());
        args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, schoolId);
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolName);
        if (gradeInfo != null) {
            args.putString(ContactsActivity.EXTRA_CONTACTS_GRADE_ID, gradeInfo.getLevelGId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_GRADE_NAME, gradeInfo.getLevelGName());
        }
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

//    private void enterGroupQrCode(SubscribeGradeInfo gradeInfo, SubscribeClassInfo classInfo) {
//        Bundle args = new Bundle();
//        if (classInfo.isSchool()) {
//            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
//                    getActivity().getString(R.string.school_qrcode));
//        } else {
//            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
//                    getActivity().getString(R.string.class_qrcode));
//        }
//        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
//                ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
//        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,
//                classInfo.getClassMailListId());
//        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ICON,
//                classInfo.getHeadPicUrl());
//        if (classInfo.isSchool()) {
//            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
//                    schoolName);
//        } else {
//            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
//                    classInfo.getClassName());
//        }
//        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
//        intent.putExtras(args);
//        startActivity(intent);
//    }

    private void joinSchool(SubscribeGradeInfo gradeInfo, SubscribeClassInfo classInfo) {
        QrcodeSchoolInfo data = new QrcodeSchoolInfo();
        data.setId(schoolId);
        data.setSname(schoolName);
        data.setLogoUrl(classInfo.getHeadPicUrl());
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_SCHOOL_INFO, data);
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    private void joinClass(SubscribeGradeInfo gradeInfo, SubscribeClassInfo classInfo) {
        QrcodeClassInfo data = new QrcodeClassInfo();
        data.setClassId(classInfo.getClassId());
        StringBuilder builder = new StringBuilder();
//        if (gradeInfo != null && !TextUtils.isEmpty(gradeInfo.getLevelGName())) {
//            builder.append(gradeInfo.getLevelGName());
//        }
        builder.append(classInfo.getClassName());
        data.setCname(builder.toString());
        data.setHeadPicUrl(classInfo.getHeadPicUrl());
        data.setSname(schoolName);
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_CLASS_INFO, data);
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    private void joinGroup(SubscribeGradeInfo gradeInfo, SubscribeClassInfo classInfo) {
        if (classInfo.isClass()) {
            joinClass(gradeInfo, classInfo);
        } else if (classInfo.isSchool()) {
//            showJoinSchoolDialog(classInfo);
            joinSchool(gradeInfo, classInfo);
        }
    }

    private void showJoinSchoolDialog(final SubscribeClassInfo classInfo) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
                getActivity(),
                null, getString(R.string.join_as_school_teacher),
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
                        joinSchool(classInfo);
                    }
                });
        dialog.show();
    }

    private void joinSchool(SubscribeClassInfo classInfo) {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("SchoolName", schoolName);
        DefaultListener listener = new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    TipsHelper.showToast(getActivity(),
                            R.string.application_commit_failure);
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        R.string.application_commit_success);
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.SAVE_TEACHER_ROLEINFO_URL, params, listener);
    }

    private void loadSchoolInfo() {
        if (getUserInfo() == null || TextUtils.isEmpty(schoolId) || schoolInfo != null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        schoolInfo = getResult().getModel();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_ico) {
            showMoreMenu(v);
        } else if (v.getId() == R.id.contacts_item_status) {
            MyViewHolder holder = (MyViewHolder) v.getTag();
            if (holder == null) {
                return;
            }

            SubscribeGradeInfo gradeInfo = null;
            if (holder.groupPosition <
                    getCurrListViewHelper().getDataAdapter().getGroupCount()) {
                gradeInfo = (SubscribeGradeInfo) getCurrListViewHelper()
                        .getDataAdapter().getGroup(holder.groupPosition);
            }
            SubscribeClassInfo classInfo = holder.data;
            joinGroup(gradeInfo, classInfo);
        } else {
            super.onClick(v);
        }
    }

    private void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> items = new ArrayList();
        PopupMenu.PopupMenuData data = null;
        UserInfo userInfo = getUserInfo();
        if (userInfo.isHeaderTeacher()) {
            data = new PopupMenu.PopupMenuData(0,
                    R.string.create_class, Constants.MENU_ID_CREATE_CLASS);
            items.add(data);
            data = new PopupMenu.PopupMenuData(0,
                    R.string.class_request, Constants.MENU_ID_CLASS_REQUESTS);
            items.add(data);
        } else if (userInfo.isTeacher()) {
            data = new PopupMenu.PopupMenuData(0,
                    R.string.create_class, Constants.MENU_ID_CREATE_CLASS);
            items.add(data);
        }

        if (items.size() <= 0) {
            return;
        }

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (view.getTag() == null) {
                            return;
                        }
                        PopupMenu.PopupMenuData data = (PopupMenu.PopupMenuData) view.getTag();
                        if (data.getId() == Constants.MENU_ID_CREATE_CLASS) {
                            enterCreateClass();
                        } else if (data.getId() == Constants.MENU_ID_CLASS_REQUESTS) {
                            enterClassRequests();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void enterClassRequests() {
        Bundle args = new Bundle();
        args.putString(ContactsClassRequestListActivity.EXTRA_SCHOOL_ID, this.schoolId);
        Intent intent = new Intent(getActivity(), ContactsClassRequestListActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, ContactsClassRequestListFragment
                .REQUEST_CODE_CONTACTS_CLASS_REQUEST_LIST);
    }

    private void enterCreateClass() {
        Intent intent = new Intent(getActivity(), ContactsCreateClassActivity.class);
        Bundle args = new Bundle();
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, schoolId);
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolName);
        intent.putExtras(args);
        startActivityForResult(intent,
                ContactsCreateClassActivity.REQUEST_CODE_CREATE_CLASS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ContactsCreateClassActivity.REQUEST_CODE_CREATE_CLASS) {
                boolean classCreated = data.getBooleanExtra(
                        ContactsCreateClassActivity.EXTRA_CLASS_CREATED, false);
                if (classCreated) {
                    String schoolId = data.getStringExtra(
                            ContactsCreateClassActivity.EXTRA_SCHOOL_ID);
                    if (this.schoolId.toLowerCase().equals(schoolId) || this.schoolId.toUpperCase
                            ().equals(schoolId)) {
                        //创建班级需要刷新
                        refreshData();
                        //通过广播的方式发送到校园空间去更新班级的信息
                        Intent broadIntent = new Intent();
                        String action = MySchoolSpaceFragment.ACTION_LOAD_DATA;
                        broadIntent.setAction(action);
                        getActivity().sendBroadcast(broadIntent);
                    }
                }
            } else if (requestCode == ClassSpaceActivity.REQUEST_CODE_CLASS_SPACE) {
                boolean classSpaceChanged = data.getBooleanExtra(
                        ClassSpaceActivity.EXTRA_CLASS_SPACE_CHANGED, false);
                if (!classSpaceChanged) {
                    return;
                }
                String classId = data.getStringExtra(
                        ClassSpaceActivity.EXTRA_CLASS_ID);
                if (this.classInfo == null || TextUtils.isEmpty(classId)
                        || !this.classInfo.getClassId().equals(classId)) {
                    return;
                }
                boolean classHeadTeacherChanged = data.getBooleanExtra(
                        ClassSpaceActivity.
                                EXTRA_CLASS_HEADTEACHER_CHANGED, false);
                boolean classStatusChanged = data.getBooleanExtra(
                        ClassSpaceActivity.EXTRA_CLASS_STATUS_CHANGED, false);
                if (classStatusChanged) {
                    this.classInfo.setIsHistory(data.getIntExtra(
                            ClassSpaceActivity.EXTRA_CLASS_STATUS,
                            ClassSpaceActivity.CLASS_STATUS_PRESENT));
                }
                if (classHeadTeacherChanged) {
                    String classHeadTeacherId = data.getStringExtra(
                            ClassSpaceActivity.EXTRA_CLASS_HEADTEACHER_ID);
                    if (getUserInfo().getMemberId().equals(classHeadTeacherId)) {
                        this.classInfo.setHeadMaster(true);
                    } else {
                        if (this.classInfo.isHeadMaster()) {
                            this.classInfo.setHeadMaster(false);
                        }
                    }
                }
                boolean classNameChanged = data.getBooleanExtra(
                        ClassSpaceActivity.EXTRA_CLASS_NAME_CHANGED, false);
                if (classNameChanged) {
                    this.classInfo.setClassName(
                            data.getStringExtra(ClassSpaceActivity.EXTRA_CLASS_NAME));
                    classListViewHelper.update();
                    getCurrListViewHelper().update();
                }

                //这里简写一下（不推荐）
                boolean hasClassContentChanged = classNameChanged || classHeadTeacherChanged ||
                        classSpaceChanged || classStatusChanged;

                if (hasClassContentChanged) {
                    //班级内容改变了，需要刷新。
                    refreshData();
                }
            } else if (requestCode == ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS) {
                boolean back = data.getBooleanExtra("back", false);
                if (back) {
                    refreshData();
                }
            }
        }

        if (data == null) {
            if (requestCode == ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS) {
                //班级详情页面数据改变（本质是ClassContactsDetailsFragment班级改变）
                if (ClassDetailsFragment.hasContentChanged()) {
                    ClassDetailsFragment.setHasContentChanged(false);
                    //刷新页面
                    refreshData();
                }
            } else if (requestCode == ContactsClassRequestListFragment
                    .REQUEST_CODE_CONTACTS_CLASS_REQUEST_LIST) {
                //审批消息处理后需要刷新
                if (ContactsClassRequestListFragment.hasMessageHandled()) {
                    ContactsClassRequestListFragment.setHasMessageHandled(false);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent){
        if (TextUtils.equals(messageEvent.getUpdateAction(), MessageEventConstantUtils.JOIN_CHARGE_CLASS_SUCCESS)){
            refreshData();
        }
    }

    private class MyViewHolder extends ViewHolder<SubscribeClassInfo> {
        int groupPosition;
        int childPosition;
    }

}
