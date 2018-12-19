package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BookCatalogListActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.CatalogSelectActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CheckAuthorizationPmnHelper;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfo;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfoResult;
import com.galaxyschool.app.wawaschool.pojo.Calalog;
import com.galaxyschool.app.wawaschool.pojo.CourseDataListResult;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseSourceType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CatalogLessonListFragment extends ContactsListFragment {

    public static final String TAG = CatalogLessonListFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_BOOK_SOURCE = BookListActivity.EXTRA_BOOK_SOURCE;
        public static final String EXTRA_SCHOOL_ID = BookListActivity.EXTRA_SCHOOL_ID;
        public static final String EXTRA_SCHOOL_NAME = BookListActivity.EXTRA_SCHOOL_NAME;
        public static final String EXTRA_BOOK_PRIMARY_KEY = BookCatalogListActivity.EXTRA_BOOK_PRIMARY_KEY;
        public static final String EXTRA_BOOK_ID = BookCatalogListActivity.EXTRA_BOOK_ID;
        public static final String EXTRA_BOOK_NAME = BookCatalogListActivity.EXTRA_BOOK_NAME;
        public static final String EXTRA_BOOK_CATALOG_ID = "bookCatalogId";
        public static final String EXTRA_BOOK_CATALOG_NAME = "bookCatalogName";
        public static final String BOOK_CATALOG_LIST = "bookCatalogList";
        public static final String BOOK_CATALOG_ITEM = "bookCatalog";
        public static final String EXTRA_ORIGIN_SCHOOL_ID = "originSchoolId";

        public static final int PLATFORM_BOOK = BookListActivity.PLATFORM_BOOK;
        public static final int SCHOOL_BOOK = BookListActivity.SCHOOL_BOOK;
        public static final int PERSONAL_BOOK = BookListActivity.PERSONAL_BOOK;
    }

    private int bookSource;
    private String bookPrimaryKey;
    private String bookId;
    private String bookName;
    private String schoolId;
    private String originSchoolId;
    private String schoolName;
    private String bookCatalogId;
    private String bookCatalogName;
    private TextView keywordView;
    private TextView headTitleView;
    private String keyword = "";
    private NewResourceInfoListResult resourceListResult;
    private DialogHelper.WarningDialog deleteDialog;
    private Map<String, CourseData> originalResources = new HashMap();
    private List<Calalog> calalogs;
    private List<Calalog> calalogsNochildren;
    private int position;
    private TextView privousTextview;
    private TextView nextTextview;
    private LinearLayout allCatalogLayout;
    private LinearLayout catalogManageLayout;
    private boolean isPick;
    private int curPosition;
    private AuthorizationInfo authorizationInfo; //保存授权信息
    private SchoolInfo schoolInfo;
    private int taskType;
    private TextView headRightBtn;
    private boolean ASSOCIATE_TASK_ORDER;//导读任务单
    private boolean isFromChoiceLib;
    private String currentOrzSchoolId;
    private int pickModel = 0;

    public interface PickModelType {
        int SINGLE_MODEL = 0;
        int MULTIPLE_MODEL = 1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog_lesson_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initCatalogsNochildren();
        getPageHelper().clear();
        loadResourceList();
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果来自校本资源库的校验权限
        if (isFromChoiceLib && !VipConfig.isVip(getActivity())) {
            checkChoicePer(currentOrzSchoolId);
        }
    }

    private void init() {
        bookSource = getArguments().getInt(Constants.EXTRA_BOOK_SOURCE);
        bookPrimaryKey = getArguments().getString(Constants.EXTRA_BOOK_PRIMARY_KEY);
        bookId = getArguments().getString(Constants.EXTRA_BOOK_ID);
        schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
        schoolName = getArguments().getString(Constants.EXTRA_SCHOOL_NAME);
        bookCatalogId = getArguments().getString(Constants.EXTRA_BOOK_CATALOG_ID);
        bookCatalogName = getArguments().getString(Constants.EXTRA_BOOK_CATALOG_NAME);
        calalogs = (List<Calalog>) getArguments().getSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST);
        bookName = getArguments().getString(Constants.EXTRA_BOOK_NAME);
        originSchoolId = getArguments().getString(Constants.EXTRA_ORIGIN_SCHOOL_ID);
        isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
        taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
        ASSOCIATE_TASK_ORDER = getArguments().getBoolean(IntroductionForReadCourseFragment.ASSOCIATE_TASK_ORDER, false);
        isFromChoiceLib = getArguments().getBoolean(ActivityUtils.IS_FROM_CHOICE_LIB);
        currentOrzSchoolId = getArguments().getString(BookDetailActivity.CURRENT_ORZ_SCHOOLID);

        initViews();
        loadSchoolInfo();
    }

    /**
     * 加载学校信息
     */
    protected void loadSchoolInfo() {

        Map<String, Object> params = new HashMap();
        if (isLogin()) {
            params.put("MemberId", getUserInfo().getMemberId());
        } else {
            return;
        }
        if (TextUtils.isEmpty(originSchoolId)) {
            return;
        }
        params.put("SchoolId", originSchoolId);
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

    private void initCatalogsNochildren() {
        calalogsNochildren = new ArrayList<Calalog>();
        for (Calalog calalog : calalogs) {
            if (calalog.getChildren() != null && calalog.getChildren().size() > 0) {
                calalogsNochildren.addAll(calalog.getChildren());
            } else {
                calalogsNochildren.add(calalog);
            }
        }
        if (bookCatalogId != null && calalogsNochildren.size() > 0) {
            for (int i = 0; i < calalogsNochildren.size(); i++) {
                if (bookCatalogId.equals(calalogsNochildren.get(i).getId())) {
                    position = i;
                    break;
                }
            }
        }

    }

    private void initViews() {
        headTitleView = ((TextView) findViewById(R.id.contacts_header_title));
        if (headTitleView != null) {
            if (headTitleView != null) {
                if (isPick) {
                    String headerTitle = null;
                    if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                        headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
                    } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE) {
                        headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
                    } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                        headerTitle = getString(R.string.appoint_course_no_point);
                    } else if (taskType == StudyTaskType.TASK_ORDER) {
                        headerTitle = getString(R.string.pls_add_work_task);
                    } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                        pickModel = PickModelType.MULTIPLE_MODEL;
                        headerTitle = getString(R.string.appoint_course_no_point);
                    }
                    headTitleView.setText(headerTitle);
                } else {
                    headTitleView.setText(bookCatalogName);
                }
            }
            headRightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
            if (headRightBtn != null) {
                headRightBtn.setVisibility(isPick ? View.VISIBLE : View.GONE);
                headRightBtn.setText(R.string.confirm);
                headRightBtn.setTextColor(getResources().getColor(R.color.text_green));
                headRightBtn.setOnClickListener(this);
            }
            ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
            if (imageView != null) {
                imageView.setOnClickListener(this);
            }

        }
        privousTextview = ((TextView) findViewById(R.id.privous_textview));
        nextTextview = ((TextView) findViewById(R.id.next_textview));
        allCatalogLayout = ((LinearLayout) findViewById(R.id.all_catalog_layout));
        privousTextview.setOnClickListener(this);
        nextTextview.setOnClickListener(this);
        allCatalogLayout.setOnClickListener(this);
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title_or_author));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadResourceList();
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    loadResourceList();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    loadResourceList();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        AdapterViewHelper adapterViewHelper = null;
        if (listView != null) {
//            if (isPick){
            listView.setNumColumns(2);
            //设置与搜索栏的距离
            int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.vertical_space);
            listView.setPadding(0, padding, 0, padding);
            listView.setBackgroundColor(getResources().getColor(R.color.text_white));
            adapterViewHelper = new NewResourcePadAdapterViewHelper(getActivity(), listView) {
                @Override
                public void loadData() {
                    loadResourceList();
                }

                @Override
                public View getView(final int position, View convertView, final ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    NewResourceInfo item = (NewResourceInfo) holder.data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.item_selector);
                    if (isPick) {
                        imageView.setVisibility(View.VISIBLE);
                        if (item.isSelect()) {
                            imageView.setSelected(true);
                        } else {
                            imageView.setSelected(false);
                        }
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (isPick) {
                        checkItem(data, position);
                    } else {
                        if (data != null) {
                            //打开任务单进入详情页
                            if (data.getResourceType() == 23) {
                                ActivityUtils.enterTaskOrderDetailActivity(getActivity()
                                        , data);
                                return;
                            }
                            if (bookSource == Constants.SCHOOL_BOOK) {
                                if (!TextUtils.isEmpty(originSchoolId) && !TextUtils.isEmpty(schoolId)) {
                                    ActivityUtils.openPictureDetailActivity(getActivity(), data,
                                            originSchoolId, schoolId, true);
                                } else {
                                    ActivityUtils.openPictureDetailActivity(getActivity(),
                                            data, schoolId, null, true);
                                }
                            } else {
                                if (data.isMicroCourse() || data.isOnePage()) {
                                    ActivityUtils.openPictureDetailActivity(getActivity(),
                                            data, PictureBooksDetailActivity
                                                    .FROM_MY_BOOK_SHELF, true);
                                }
                            }
                        }
                    }
                }
            };
//            }else{
//                 adapterViewHelper = new NewResourceAdapterViewHelper(
//                        getActivity(), listView) {
//                    @Override
//                    public void loadData() {
//                        loadResourceList();
//                    }
//
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        View view = super.getView(position, convertView, parent);
//                        NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
//                        if (data == null) {
//                            return view;
//                        }
////                    TextView textView = (TextView) view.findViewById(R.id.resource_tips);
////                    if (textView != null) {
////                        textView.setText(R.string.recovery);
////                        textView.setOnClickListener(CatalogLessonListFragment.this);
////                        if (!TextUtils.isEmpty(data.getOldMicroId())) {
////                            textView.setVisibility(View.VISIBLE);
////                        } else {
////                            textView.setVisibility(View.GONE);
////                        }
////                        textView.setTag(data);
////                    }
//                        TextView   textView = (TextView) view.findViewById(R.id.resource_read_count);
//                        textView.setVisibility(View.INVISIBLE);
////                    if (textView != null) {
////                        if (!TextUtils.isEmpty(data.getOldMicroId())) {
////                            textView.setVisibility(View.VISIBLE);
////                            textView.setText(R.string.review_original_resource);
////                            textView.setTextColor(
////                                    getActivity().getResources().getColor(R.color.text_green));
////                            textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);
////                            textView.setOnClickListener(CatalogLessonListFragment.this);
////                        } else {
////                            textView.setVisibility(View.INVISIBLE);
//////                            textView.setTextColor(
//////                                    getActivity().getResources().getColor(R.color.text_normal));
//////                            textView.setBackgroundColor(Color.parseColor("#00000000"));
//////                            textView.setOnClickListener(null);
////                        }
//                        textView.setTag(data);
//                        return view;
//                    }
//
//                    @Override
//                    public void onItemClick(AdapterView parent, View view, int position, long id) {
//                        ViewHolder holder = (ViewHolder) view.getTag();
//                        if (holder == null) {
//                            return;
//                        }
//                        NewResourceInfo data = (NewResourceInfo) holder.data;
//                        if (data != null) {
//                                if(data!= null) {
//                                    //  if(data.isMicroCourse()) {
//                                    if(bookSource == Constants.SCHOOL_BOOK) {
//                                        if(!TextUtils.isEmpty(originSchoolId) && !TextUtils.isEmpty(schoolId)) {
//                                            ActivityUtils.openPictureDetailActivity(getActivity(), data,
//                                                    originSchoolId, schoolId,true);
//                                        } else {
//                                            ActivityUtils.openPictureDetailActivity(getActivity(),
//                                                    data, schoolId, null,true);
//                                        }
//                                    } else {
//                                        if(data.isMicroCourse()||data.isOnePage()){
//                                            ActivityUtils.openPictureDetailActivity(getActivity(),
//                                                    data,PictureBooksDetailActivity
//                                                            .FROM_MY_BOOK_SHELF,true);
//                                        }
//                                    }
////                                } else if(data.getResourceType() == ResType.RES_TYPE_ONEPAGE) {
////                                    ActivityUtils.openOnlineOnePage(getActivity(), data);
////                                }
//
//
////                                int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
////                                if(resType == ResType.RES_TYPE_ONEPAGE) {
////                                    openOnlineOnePage(data);
////                                } else if(resType == ResType.RES_TYPE_COURSE || resType ==
////                                        ResType.RES_TYPE_COURSE_SPEAKER || resType == ResType.RES_TYPE_OLD_COURSE){
////                                    enterPictureDetailActivity(data);
////                                }
//                                }
//                        }
//                    }
//                };
//            }
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void enterPictureDetailActivity(NewResourceInfo item) {
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        intent.putExtra(PictureBooksDetailActivity.EXTRA_IS_FROM_CATALOG, true);
        startActivity(intent);
    }

    protected void loadResourceList() {
        loadResourceList(keywordView.getText().toString());
    }

    protected void loadResourceList(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        if (this.bookSource == Constants.PLATFORM_BOOK) {
            params.put("TypeCode", "9,10"); // fetch LQLesson & LQBook
            params.put("OutlineId", bookId);
        } else if (this.bookSource == Constants.SCHOOL_BOOK) {
            params.put("SchoolId", schoolId);
            params.put("BookId", bookPrimaryKey);
        } else if (this.bookSource == Constants.PERSONAL_BOOK) {
            params.put("SchoolId", schoolId);
            params.put("BookId", bookPrimaryKey);
            if (getUserInfo() == null) {
                return;
            }
            params.put("MemberId", getUserInfo().getMemberId());
        }
        params.put("SectionId", bookCatalogId);
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        params.put("MType", taskType == StudyTaskType.TASK_ORDER ? "7" : "6");
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateResourceListView(getResult());
                    }
                };
        String serverUrl = null;
        if (this.bookSource == Constants.PLATFORM_BOOK) {
            serverUrl = ServerUrl.GET_PLATFORM_CATALOG_LESSON_LIST_URL;
        } else if (this.bookSource == Constants.SCHOOL_BOOK) {
            serverUrl = ServerUrl.GET_SCHOOL_CATALOG_LESSON_LIST_URL;
        } else if (this.bookSource == Constants.PERSONAL_BOOK) {
            serverUrl = ServerUrl.GET_MY_COLLECTION_CATALOG_LESSON_LIST_URL;
        }

        if (serverUrl != null) {
            RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
        }
    }

    protected void updateResourceListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (isPick) {
                List<NewResourceInfo> list1 = new ArrayList<NewResourceInfo>();
                for (NewResourceInfo info : list) {
                    if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                        if (info.isMicroCourse()) {
                            list1.add(info);
                        }
                    } else if (taskType == StudyTaskType.TASK_ORDER) {
                        list1.add(info);
                    } else {
                        if (info.isMicroCourse()) {
                            list1.add(info);
                        }
                    }
                }
                list.clear();
                list.addAll(list1);
            }

            RefreshUtil.getInstance().refresh(list);

            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                resourceListResult = result;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.resource_tips) {
            showRecoveryResourceDialog((NewResourceInfo) v.getTag());
        } else if (v.getId() == R.id.resource_read_count) {
            NewResourceInfo data = (NewResourceInfo) v.getTag();
            if (originalResources.containsKey(data.getOldMicroId())) {
                openOriginalResource(originalResources.get(data.getOldMicroId()), data);
            } else {
                loadOriginalResourceInfo(data, false);
            }
        } else if (v.getId() == R.id.privous_textview) {
            if (position == 0) {
                TipsHelper.showToast(getActivity(), R.string.no_privous_note);
                return;
            } else {
                position = position - 1;
                privousNextClickEvent(position);
            }
        } else if (v.getId() == R.id.next_textview) {
            if (position == calalogsNochildren.size() - 1) {
                TipsHelper.showToast(getActivity(), R.string.no_next_note);
                return;
            } else {
                position = position + 1;
                privousNextClickEvent(position);
            }

        } else if (v.getId() == R.id.all_catalog_layout) {
            selectCatalogEvent();
        } else if (v.getId() == R.id.contacts_header_right_btn) {
            NewResourceInfo cloudSelectData = getSelectData();
            if (cloudSelectData == null) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
                return;
            }
            if (TextUtils.isEmpty(originSchoolId)) {
                enterPublishTask();
            } else {
                if (!TextUtils.isEmpty(originSchoolId) && !TextUtils.isEmpty(schoolId)) {
                    if (originSchoolId.equals(schoolId)) {
                        enterPublishTask();
                    } else {
                        checkAuthorizationCondition(originSchoolId, schoolId);
                    }
                }
            }
        } else if (v.getId() == R.id.contacts_header_left_btn) {
            if (isPick) {
                popStack();
            } else {
                super.onClick(v);
            }
        }
    }

    private boolean checkAuthorizationCondition(final String schoolId, final String feeSchoolId) {
        if (getUserInfo() == null) {
            return false;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        RequestHelper.RequestDataResultListener<AuthorizationInfoResult> listener =
                new RequestHelper.RequestDataResultListener<AuthorizationInfoResult>
                        (getActivity(), AuthorizationInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AuthorizationInfoResult result = (AuthorizationInfoResult) getResult();

                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            TipsHelper.showToast(getActivity(), getString(R.string.course_is_not_authorize));
                            return;
                        }
                        authorizationInfo = null;
                        authorizationInfo = result.getModel().getData();
                        if (authorizationInfo != null) {
                            if (authorizationInfo.isIsMemberAuthorized()) {
                                enterPublishTask();
                            } else {
                                if (authorizationInfo.isIsCanAuthorize()) {
                                    authorizeToMembers(schoolId, feeSchoolId);
                                } else {
                                    TipsHelper.showToast(getActivity(), getString(R.string.course_is_not_authorize));
                                }
                            }
                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_AUTHONRIZE_CONDITION_URL,
                params, listener);
        return false;
    }

    private void authorizeToMembers(String schoolId, String feeSchoolId) {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        if (schoolInfo != null) {
            params.put("RoleTypes", schoolInfo.getRoles());
        }
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.AUTHONRIZE_TO_MEMBER_URL,
                params, new RequestHelper.RequestModelResultListener<ModelResult>
                        (getActivity(), ModelResult.class) {
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
                        if (result.isSuccess()) {
                            if (authorizationInfo != null) {
                                authorizationInfo.setIsMemberAuthorized(true);
                            }
                            enterPublishTask();
                        } else {
                            TipsHelper.showToast(getActivity(), getString(R.string.course_is_not_authorize));
                        }
                    }
                });
    }

    private void selectCatalogEvent() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CatalogSelectActivity.class);
        intent.putExtra(CatalogLessonListActivity.BOOK_CATALOG_LIST, (Serializable) calalogs);
        intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_NAME, bookName);
        if (isPick) {
            startActivityForResult(intent, CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG);

        } else {
            getActivity().startActivityForResult(intent, CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG);
        }
    }

    private void enterPublishTask() {
        NewResourceInfo cloudSelectData = getSelectData();
        IntroductionForReadCourseFragment fragmentCourse = (IntroductionForReadCourseFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(IntroductionForReadCourseFragment.TAG);
        if (ASSOCIATE_TASK_ORDER) {
            fragmentCourse.setWorkOrderId(cloudSelectData.getMicroId());
            fragmentCourse.setResourceUrl(cloudSelectData.getResourceUrl());
            fragmentCourse.setConnectThumbnail(cloudSelectData.getThumbnail());
            getArguments().putInt(ActivityUtils.EXTRA_TASK_TYPE, StudyTaskType.INTRODUCTION_WAWA_COURSE);

        } else {


            CourseData courseData = new CourseData();
            String microId = cloudSelectData.getMicroId();
            if (microId.contains("-")) {
                int i = microId.indexOf("-");
                microId = microId.substring(0, i);
            }
            courseData.id = Integer.parseInt(microId);
            courseData.nickname = cloudSelectData.getTitle();
            courseData.type = cloudSelectData.getResourceType();
            courseData.resourceurl = cloudSelectData.getResourceUrl();
            courseData.thumbnailurl = cloudSelectData.getThumbnail();
            courseData.code = cloudSelectData.getAuthorId();
            courseData.shareurl = cloudSelectData.getShareAddress();
            courseData.screentype = cloudSelectData.getScreenType();
            UploadParameter uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(),
                    null, courseData, null, 1);
            String headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
            if (uploadParameter != null) {
                if (getArguments() != null) {
                    int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                    uploadParameter.setTaskType(taskType);
                    if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                        headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
                    }
                }
                //用于导读时获取当前的upLoadParameter的数据
                //            if (taskType==StudyTaskType.INTRODUCTION_WAWA_COURSE){

                fragmentCourse.setData(uploadParameter);
                if (taskType == StudyTaskType.TASK_ORDER) {
                    fragmentCourse.setWorkOrderId(cloudSelectData.getMicroId());

                }
            }
        }
        int num = getFragmentManager().getBackStackEntryCount();

        while (num > 1) {
            popStack();
            num = num - 1;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG) {
            Calalog calalog = (Calalog) data.getSerializableExtra(CatalogLessonListActivity.BOOK_CATALOG_ITEM);
            bookCatalogId = calalog.getId();
            bookCatalogName = calalog.getName();
            loadDataAgain();
        }
    }

    private void privousNextClickEvent(int position) {
        Calalog calalog = calalogsNochildren.get(position);
        bookCatalogId = calalog.getId();
        bookCatalogName = calalog.getName();
        loadDataAgain();
    }

    private void loadDataAgain() {
        if (headTitleView != null && !isPick) {
            headTitleView.setText(bookCatalogName);
        }
        keyword = "";
        keywordView.setText("");
        getPageHelper().clear();
        loadResourceList();
    }


    private void showRecoveryResourceDialog(final NewResourceInfo data) {
//        deleteDialog = DialogHelper.getIt(getActivity()).getWarningDialog();
//        deleteDialog.setContent(R.string.recovery_original_resource_tips);
//        deleteDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.cancel) {
//                    deleteDialog.dismiss();
//                } else if (v.getId() == R.id.confirm) {
//                    deleteDialog.dismiss();
//                    recoveryResource(data);
//                }
//            }
//        });
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), getString
                (R.string.delete), getString(R.string.recovery_original_resource_tips),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                recoveryResource(data);
            }
        });
        messageDialog.show();

    }

    private void recoveryResource(NewResourceInfo data) {
        Map<String, Object> params = new HashMap();
        params.put("CourseId", data.getId());
        params.put("BookId", this.bookPrimaryKey);
        RequestHelper.RequestListener listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        TipsHelper.showToast(getActivity(),
                                R.string.recovery_original_resource_success);
                        NewResourceInfo data = (NewResourceInfo) getTarget();
                        getCurrAdapterViewHelper().getData().remove(data);
                        getCurrAdapterViewHelper().update();
                        loadResourceList();
                    }
                };
        listener.setShowLoading(true);
        listener.setTarget(data);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.RECOVERY_COLLECTION_BOOK_RESOURCE_URL, params, listener);
    }

    private void loadOriginalResourceInfo(NewResourceInfo data, final boolean isFetch) {
        Map<String, Object> params = new HashMap();
        params.put("resId", data.getOldMicroId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestResourceResultListener<CourseDataListResult>(
                        getActivity(), CourseDataListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        List<CourseData> list = getResult().getData();
                        if (list == null || list.size() <= 0) {
                            return;
                        }
                        NewResourceInfo data = (NewResourceInfo) getTarget();
                        originalResources.put(data.getOldMicroId(), list.get(0));
                        if (!isFetch) {
                            openOriginalResource(list.get(0), data);
                        } else {
                            if (data != null && !TextUtils.isEmpty(data.getResourceUrl())) {
                                if (!TextUtils.isEmpty(data.getTitle())) {
                                    String rootFolder = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_IMPORT, false);
//                                    DownloadResourceManager downloadResourceManager = new DownloadResourceManager(getActivity(), getMemeberId(), data, rootFolder, new Handler());
//                                    downloadResourceManager.startDownload();
                                }

                            }
                        }
                    }
                };

        listener.setShowLoading(true);
        listener.setTarget(data);
        RequestHelper.sendGetRequest(getActivity(),
                ServerUrl.WAWATV_COURSE_DETAIL_URL, params, listener);
    }

    private void openOriginalResource(CourseData data, NewResourceInfo resourceInfo) {
        if (data != null) {
            CourseInfo courseInfo = data.getCourseInfo();
            if (courseInfo != null && resourceInfo != null) {
                if (bookSource == Constants.PERSONAL_BOOK) {
                    courseInfo.setEditResourceUrl(resourceInfo.getResourceUrl());
                    courseInfo.setIsMyBookShelf(resourceInfo.isMyBookShelf());
                    courseInfo.setEditMicroId(resourceInfo.getMicroId());
                    courseInfo.setEditUpdateTime(resourceInfo.getUpdatedTime());
                    courseInfo.setCourseSourceType(CourseSourceType.BOOKSHELF);
                }
            }
            ActivityUtils.playOnlineCourse(getActivity(), courseInfo, false, null);
        }

    }

    private void checkItem(NewResourceInfo info, int position) {
        if (info != null) {
            info.setIsSelect(!info.isSelect());
            if (info.isSelect()) {
                RefreshUtil.getInstance().addId(info.getId());
            } else {
                RefreshUtil.getInstance().removeId(info.getId());
            }
            curPosition = position;
        }
        if (pickModel == PickModelType.SINGLE_MODEL) {
            checkAllItems(false, position);
        }
        getCurrAdapterViewHelper().update();
    }

    private void checkAllItems(boolean isCheck, int position) {
        List<NewResourceInfo> data = getCurrAdapterViewHelper().getData();
        if (data != null && data.size() > 0) {
            int size = data.size();
            for (int i = 0; i < size; i++) {
                NewResourceInfo info = data.get(i);
                if (info != null && i != position) {
                    info.setIsSelect(isCheck);
                    RefreshUtil.getInstance().removeId(info.getId());
                }
            }
        }
    }

    public NewResourceInfo getSelectData() {
        if (getCurrAdapterViewHelper() != null) {
            List<NewResourceInfo> resourceInfos = getCurrAdapterViewHelper().getData();
            if (resourceInfos != null && resourceInfos.size() > 0) {
                if (curPosition >= 0 && curPosition < resourceInfos.size()) {
                    NewResourceInfo info = resourceInfos.get(curPosition);
                    if (info != null && info.isSelect()) {
                        return info;
                    }
                }
            }
        }
        return null;
    }

    public List<NewResourceInfo> getSelectListData() {
        List<NewResourceInfo> info = new ArrayList<>();
        if (getCurrAdapterViewHelper() != null) {
            List<NewResourceInfo> resourceInfos = getCurrAdapterViewHelper().getData();
            if (resourceInfos != null && resourceInfos.size() > 0) {
                for (NewResourceInfo newInfo : resourceInfos) {
                    if (newInfo.isSelect()) {
                        info.add(newInfo);
                    }
                }
            }
        }
        return info;
    }


    /**
     * 校验精品资源的权限
     */
    private void checkChoicePer(String schoolId) {
        if (TextUtils.isEmpty(schoolId) || TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        CheckAuthorizationPmnHelper helper = new CheckAuthorizationPmnHelper(getActivity());
        helper.setSchoolId(schoolId).setMemberId(getMemeberId()).setIsNeedInputCode(!isPick)
                .setListener(new CheckAuthorizationPmnHelper.checkResultListener() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        if (!isSuccess) {
                            if (isPick) {
                                popPermissionDialog();
                            } else {
                                getActivity().finish();
                            }
                        }
                    }
                }).check();
    }

    private void popPermissionDialog() {
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), null, getString(R.string.choice_library_resource_over_date),
                "", null, getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isPick) {
                    popStack();
                } else {
                    getActivity().finish();
                }
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }
}
