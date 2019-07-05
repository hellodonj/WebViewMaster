package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.NewBookStoreActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment;
import com.galaxyschool.app.wawaschool.pojo.BookDetail;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.pojo.Calalog;
import com.galaxyschool.app.wawaschool.pojo.CalalogListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.MyWebView;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.io.Serializable;
import java.util.List;

public class BookStoreDetailBaseFragment extends ContactsExpandListFragment implements View.OnClickListener {

    public static final String TAG = BookStoreDetailBaseFragment.class.getSimpleName();
    private MyWebView introductionWebview;
    private LinearLayout introductionLayout;
    private LinearLayout catalogLayout;
    private View introductionView;
    private View catlogView;
    private ExpandableListView catalogExpandListView;
    private TextView introductionTextView;
    private TextView catalogTextView;
    private TextView storeTextView;
    private boolean storedState = false;//图书收藏状态
    private boolean createState = false;//图书建设状态
    private ImageView createStateTextview;
    private TextView bookNameTextview;
    private TextView sectionCountTextview;
    private TextView storeCountTextview;
    private TextView authorityTextview;
    private ImageView coverImageview;
    private BookDetail book;
    private int showType = SHOW_INTRO_VIEW;
    private static final int SHOW_INTRO_VIEW = 1;//展示简介
    private static final int SHOW_CATALOG_VIEW = 2;//展示目录
    public static final String ACTION_ADD_BOOK = "action_add_book";
    public TextView enterBookStoreBtn;
    public int BookType = 0;
    public String schoolId;
    public String originSchoolId;
    protected boolean isPick;
    protected boolean isPickSchoolResource;
    public int taskType;
    private String bookId;
    private TextView headTitletextView;
    //我的书架中的资源收藏来源
    protected String collectSchoolId;
    //区分是否来自精品资源库
    protected boolean isFromChoiceLib;
    //当前机构的schoolId
    protected String currentOrzSchoolId;
    //是不是vipSchool机构
    protected boolean isVipSchool = false;
    private boolean isSuperTask;
    private int superTaskType;
    private boolean isGetAppointResource;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookstore_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showType = SHOW_INTRO_VIEW;
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                if (isPick) {
                    popStack();
                } else {
                    super.onClick(v);
                }
                break;
            case R.id.contacts_header_right_btn:
                //分享
                shareBook(book);
                break;
            case R.id.introduction_layout:
                showIntroductionView();

                break;
            case R.id.catalog_layout:
                showCatlogView();

                break;
            case R.id.enter_book_store_btn:
                if (schoolId != null) {
                    enterBookStoreEvent(schoolId);
                }
                break;
        }
    }

    private void enterBookStoreEvent(String schoolId) {
        Intent intent = new Intent(getActivity(), NewBookStoreActivity.class);
        Bundle args = new Bundle();
        args.putString(NewBookStoreActivity.SCHOOL_ID, schoolId);
        intent.putExtras(args);
        startActivity(intent);
    }


    private void showIntroductionView() {
        showType = SHOW_INTRO_VIEW;
        introductionView.setVisibility(View.VISIBLE);
        catlogView.setVisibility(View.INVISIBLE);
        introductionWebview.setVisibility(View.VISIBLE);
        catalogExpandListView.setVisibility(View.GONE);
        introductionTextView.setTextColor(getResources().getColor(R.color.text_green));
        catalogTextView.setTextColor(getResources().getColor(R.color.black));
    }

    private void showCatlogView() {
        showType = SHOW_CATALOG_VIEW;
        introductionView.setVisibility(View.INVISIBLE);
        catlogView.setVisibility(View.VISIBLE);
        catalogExpandListView.setVisibility(View.VISIBLE);
        introductionWebview.setVisibility(View.GONE);
        introductionTextView.setTextColor(getResources().getColor(R.color.black));
        catalogTextView.setTextColor(getResources().getColor(R.color.text_green));
    }


    private void initViews() {
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK, false);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isPickSchoolResource = getArguments().getBoolean(ActivityUtils
                    .EXTRA_IS_PICK_SCHOOL_RESOURCE);
            superTaskType = getArguments().getInt(ActivityUtils.EXTRA_SUPER_TASK_TYPE);
            isSuperTask = getArguments().getBoolean(ActivityUtils.EXTRA_FROM_SUPER_TASK);
            isGetAppointResource = getArguments().getBoolean(ActivityUtils
                    .EXTRA_IS_GET_APPOINT_RESOURCE,false);
        }
        headTitletextView = (TextView) findViewById(R.id.contacts_header_title);
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);
        TextView textView1 = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView1 != null) {
//            textView1.setVisibility(View.VISIBLE);
            textView1.setText(R.string.share_to);
            textView1.setTextColor(getResources().getColor(R.color.text_green));
            textView1.setOnClickListener(this);
        }
        bookNameTextview = ((TextView) findViewById(R.id.book_name_textview));
        sectionCountTextview = ((TextView) findViewById(R.id.section_count_textview));
        storeCountTextview = ((TextView) findViewById(R.id.store_count_textview));
        authorityTextview = ((TextView) findViewById(R.id.authority_textview));
        coverImageview = ((ImageView) findViewById(R.id.cover_imageview));
        createStateTextview = ((ImageView) findViewById(R.id.create_state_textview));
        createStateTextview.setVisibility(View.GONE);
        introductionLayout = ((LinearLayout) findViewById(R.id.introduction_layout));
        introductionLayout.setOnClickListener(this);
        catalogLayout = ((LinearLayout) findViewById(R.id.catalog_layout));
        catalogLayout.setOnClickListener(this);
        introductionWebview = ((MyWebView) findViewById(R.id.introduction_webview));
        introductionView = ((View) findViewById(R.id.introduction_view));
        catlogView = ((View) findViewById(R.id.catalog_view));
        introductionTextView = (TextView) findViewById(R.id.introduction_text_view);
        catalogTextView = (TextView) findViewById(R.id.catalog_text_view);
        introductionWebview.setVisibility(View.VISIBLE);
        storeTextView = (TextView) findViewById(R.id.store_textview);
        storeTextView.setOnClickListener(this);
        enterBookStoreBtn = (TextView) findViewById(R.id.enter_book_store_btn);

        handleHeadImageToA4Pro();
    }

    /**
     * 设置缩略图的比例为A4的比例
     */
    private void handleHeadImageToA4Pro(){
        //设置外层布局为A4比例
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.item_book_cover);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        //之前宽 90 高 120
        int width = DensityUtils.dp2px(getActivity(),90);
        layoutParams.width = width;
        layoutParams.height = width * 297/210;
        frameLayout.setLayoutParams(layoutParams);
    }

    protected void initCataLogExpandListView(final LoadInfo loadInfo, final String bookId, final String schoolId, final int fromType, final BookStoreBook data) {
        catalogExpandListView = ((ExpandableListView) findViewById(R.id.catlog_expand_listview));
        this.bookId = bookId;
        this.schoolId = schoolId;

        if (catalogExpandListView != null) {
            catalogExpandListView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.contacts_search_expand_list_item,
                    R.layout.contacts_expand_list_child_item) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((Calalog) getData().get(groupPosition))
                            .getChildren().get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        Calalog calalog = (Calalog)
                                getData().get(groupPosition);
                        if (calalog != null && calalog.getChildren() != null) {
                            return calalog.getChildren().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    Calalog data = (Calalog) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.INVISIBLE);
                    }

                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_item_status);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    Calalog data = (Calalog) getGroup(groupPosition);
                    View headerView = view.findViewById(R.id.contacts_item_header_layout);
                    if (headerView != null) {
                        headerView.setVisibility(View.GONE);
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setImageResource(R.drawable.catalog_dot);
                        ((LinearLayout.LayoutParams) imageView.getLayoutParams()).setMargins(0, 0, 0, 0);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        if (data.getChildren() == null || data.getChildren().size() == 0) {
                            imageView.setVisibility(View.INVISIBLE);
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageResource(isExpanded ?
                                    R.drawable.list_exp_up : R.drawable.list_exp_down);
                        }
                    }

                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    view.setTag(holder);
                    holder.data = data;
                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    catalogExpandListView, dataAdapter) {
                @Override
                public void loadData() {
                    loadInfo.loadInfo();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    Calalog calalog = (Calalog) holder.data;
                    getDataAdapter().notifyDataSetChanged();
                    jump2CatalogLessonListActivity(calalog, bookId, schoolId, fromType, data);
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    Calalog calalog = (Calalog) holder.data;
                    if (calalog.getChildren() != null && calalog.getChildren().size() > 0) {
                        return false;
                    }
                    getDataAdapter().notifyDataSetChanged();
                    jump2CatalogLessonListActivity(calalog, bookId, schoolId, fromType, data);
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(catalogExpandListView, listViewHelper);
        }
    }

    interface LoadInfo {
        void loadInfo();
    }

    ;

    private void sendBrodcast(BookStoreBook data) {
        if (data == null) {
            return;
        }
        Intent broadIntent = new Intent();
        broadIntent.setAction(ACTION_ADD_BOOK);
        broadIntent.putExtra("data", data);
        getActivity().sendBroadcast(broadIntent);
    }

    private void jump2CatalogLessonListActivity(Calalog calalog, String bookId, String schoolId, int fromType, BookStoreBook data) {
        if (book == null) return;
        sendBrodcast(data);
        if (!isPick) {
            enterCatalogLessonListNormal(calalog, bookId, schoolId, fromType);
        } else {
            if (!isPickSchoolResource) {
                enterCatalogLessonList(calalog, bookId, schoolId, fromType);
            } else {
                if (superTaskType == StudyTaskType.Q_DUBBING) {
                    //Q配音
                    enterPublicResource(calalog, bookId, schoolId, fromType);
                } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.TASK_ORDER){
                    //听说 + 读写过来选取资源 或者综合任务
//                    if (isSuperTask && superTaskType == StudyTaskType.RETELL_WAWA_COURSE){
                        enterMediaTypeList(calalog, bookId, schoolId, fromType);
//                    } else {
//                        enterPublicResource(calalog, bookId, schoolId, fromType);
//                    }
                } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE){
                    enterCatalogLessonList(calalog, bookId, schoolId, fromType);
                } else if (isGetAppointResource){
                    //获取指定类型的资源
                    enterPublicResource(calalog, bookId, schoolId, fromType);
                } else {
                    enterMediaTypeList(calalog, bookId, schoolId, fromType);
                }
            }
        }
    }

    private void enterPublicResource(Calalog catalog, String bookId, String schoolId, int
            fromType){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = new SchoolResourceContainerFragment();
        Bundle args = getArguments();
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        boolean isCheckTaskOrderRes = args.getBoolean(ActivityUtils.EXTRA_CHOOSE_TASKORDER_DATA);
//        if (isCheckTaskOrderRes){
//            args.putInt(SchoolResourceContainerFragment.Constants.CURRENTINDEX,
//                    SchoolResourceContainerFragment.Constants.TASK_ORDER);
//        } else {
//            args.putInt(SchoolResourceContainerFragment.Constants.CURRENTINDEX,
//                    SchoolResourceContainerFragment.Constants.LQ_COURSE);
//        }
        //目前仅仅一种
        args.putInt(SchoolResourceContainerFragment.Constants.CURRENTINDEX,
                SchoolResourceContainerFragment.Constants.VIDEO);
        args.putBoolean(SchoolResourceContainerFragment.Constants.KEY_ISHIDETAB, true);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_PRIMARY_KEY, bookId);
        args.putString(CatalogLessonListActivity.EXTRA_SCHOOL_ID, schoolId);
        args.putString(CatalogLessonListActivity.EXTRA_ORIGIN_SCHOOL_ID, originSchoolId);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID, catalog.getId());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_NAME, catalog.getName());
        if (fromType == BookDetailActivity.FROM_BOOK_STORE) {
            args.putInt(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity
                    .SCHOOL_BOOK);
        } else {
            args.putInt(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity.PERSONAL_BOOK);
        }
        args.putSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST, (Serializable)
                getCurrListViewHelper().getData());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_NAME, book.getBookName());
        args.putBoolean(MediaListFragment.EXTRA_IS_CLOUD, true);
        args.putBoolean(MediaListFragment.EXTRA_IS_REMOTE, true);
        args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB,isFromChoiceLib);
        args.putString(BookDetailActivity.CURRENT_ORZ_SCHOOLID,currentOrzSchoolId);
        fragment.setArguments(args);
        ft.add(R.id.activity_body, fragment, SchoolResourceContainerFragment.TAG);
        ft.show(fragment);
        ft.hide(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void enterMediaTypeList(Calalog catalog, String bookId, String schoolId, int
            fromType) {
        Bundle args = getArguments();
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_PRIMARY_KEY, bookId);
        args.putString(CatalogLessonListActivity.EXTRA_SCHOOL_ID, schoolId);
        args.putString(CatalogLessonListActivity.EXTRA_ORIGIN_SCHOOL_ID, originSchoolId);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID, catalog.getId());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_NAME, catalog.getName());
        if (fromType == BookDetailActivity.FROM_BOOK_STORE) {
            args.putInt(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity
                    .SCHOOL_BOOK);
        } else {
            args.putInt(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity.PERSONAL_BOOK);
        }
        args.putSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST, (Serializable)
                getCurrListViewHelper().getData());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_NAME, book.getBookName());
        args.putBoolean(MediaListFragment.EXTRA_IS_CLOUD, true);
        args.putBoolean(MediaListFragment.EXTRA_IS_REMOTE, true);
        args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB,isFromChoiceLib);
        args.putString(BookDetailActivity.CURRENT_ORZ_SCHOOLID,currentOrzSchoolId);
        MediaTypeListFragment fragment = new SchoolMediaTypeListFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, SchoolMediaTypeListFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void enterCatalogLessonListNormal(Calalog calalog, String bookId, String schoolId, int fromType) {
        //区分是从空中课堂传过来的临时参数去从校本资源库中选取资源
        boolean flag = getArguments().getBoolean(ActivityUtils.EXTRA_TEMP_DATA, false);
        Intent intent = new Intent();
        Bundle args = getArguments();
        if (flag){
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK,true);
        }
        intent.putExtras(args);
        intent.setClass(getActivity(), CatalogLessonListActivity.class);
        intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_PRIMARY_KEY, bookId);
        intent.putExtra(CatalogLessonListActivity.EXTRA_SCHOOL_ID, schoolId);
        intent.putExtra(CatalogLessonListActivity.EXTRA_ORIGIN_SCHOOL_ID, originSchoolId);
        intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID, calalog.getId());
        intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_NAME, calalog.getName());
        intent.putExtra(ActivityUtils.IS_FROM_CHOICE_LIB,isFromChoiceLib);
        intent.putExtra(BookDetailActivity.CURRENT_ORZ_SCHOOLID,currentOrzSchoolId);
        if (fromType == BookDetailActivity.FROM_BOOK_STORE) {
            intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity.SCHOOL_BOOK);
        } else {
            intent.putExtra(BookDetailFragment.Constants.COLLECT_ORIGIN_SCHOOLID,collectSchoolId);
            intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity.PERSONAL_BOOK);
        }
        intent.putExtra(CatalogLessonListActivity.BOOK_CATALOG_LIST, (Serializable) getCurrListViewHelper().getData());
        intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_NAME, book.getBookName());
        if (flag){
            startActivityForResult(intent, TeachResourceFragment.REQUEST_PERSONAL_CLOUD_RESOURCE);
        }else {
            getActivity().startActivity(intent);
        }
    }

    private void enterCatalogLessonList(Calalog calalog, String bookId, String schoolId, int fromType) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle args = getArguments();
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_PRIMARY_KEY, bookId);
        args.putString(CatalogLessonListActivity.EXTRA_SCHOOL_ID, schoolId);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID, calalog.getId());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_NAME, calalog.getName());
        args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB,isFromChoiceLib);
        args.putString(BookDetailActivity.CURRENT_ORZ_SCHOOLID,currentOrzSchoolId);
        if (fromType == BookDetailActivity.FROM_BOOK_STORE) {
            args.putInt(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity.SCHOOL_BOOK);
        } else {
            args.putInt(CatalogLessonListActivity.EXTRA_BOOK_SOURCE, CatalogLessonListActivity.PERSONAL_BOOK);
        }
        args.putSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST, (Serializable) getCurrListViewHelper().getData());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_NAME, book.getBookName());
        args.putInt(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_STORE);
        args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, isPick);
        Fragment fragment = new CatalogLessonListFragment();
        fragment.setArguments(args);
        ft.add(R.id.activity_body, fragment, CatalogLessonListFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    private class MyViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }

    protected void updateCalalogView(CalalogListResult result) {
        List<Calalog> list = result.getModel().getData();
        if (list != null && list.size() > 0) {
            getCurrListViewHelper().setData(list);
//            getCurrListView().expandGroup(0);
        }
    }

    protected void updateStoreCountTextview() {
        storeCountTextview.setText(book.getColCount() + getString(R.string.store_count));
        SpannableStringBuilder builder = new SpannableStringBuilder(storeCountTextview.getText().toString());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.text_green));
        builder.setSpan(greenSpan, 0, String.valueOf(book.getColCount()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        storeCountTextview.setText(builder);
    }


    protected void updateMainView(BookDetail book, int fromType) {
        this.book = book;
        createState = false;
        storedState = false;
        if (book != null) {
            headTitletextView.setVisibility(View.VISIBLE);
            if (isPick) {
                String headerTitle = null;
                if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                    headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
                } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE) {
                    headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
                } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE || taskType ==
                        StudyTaskType.LISTEN_READ_AND_WRITE) {
                    headerTitle = getString(R.string.appoint_course_no_point);
                }else if (taskType == StudyTaskType.TASK_ORDER) {
                    headerTitle = getString(R.string.pls_add_work_task);
                }
                if (isPickSchoolResource) {
                    headerTitle = book.getBookName();
                }
                headTitletextView.setText(headerTitle);
            } else {
                headTitletextView.setText(book.getBookName());
            }
            bookNameTextview.setText(book.getBookName());
            sectionCountTextview.setText(getString(R.string.course_amount) + book.getMicroCount());
            storedState = book.isColStatus();
            if (fromType == BookDetailActivity.FROM_BOOK_STORE) {
                authorityTextview.setVisibility(View.VISIBLE);
                authorityTextview.setText(getString(R.string.class_right) + book.getCourseTypeName());
                updateStoreCountTextview();
                if (storedState) {
                    storeTextView.setTextColor(getResources().getColor(R.color.white));
                    storeTextView.setText(getResources().getString(R.string.stored_to_bookshelf));
                    storeTextView.setBackgroundResource(R.drawable.gray_5dp_gray);
                } else {
                    storeTextView.setTextColor(getResources().getColor(R.color.text_green));
                    storeTextView.setText(getResources().getString(R.string.store_to_bookshelf));
                    storeTextView.setBackgroundResource(R.drawable.green_5dp_white);
                }
            } else {
                storeTextView.setTextColor(getResources().getColor(R.color.text_green));
                storeTextView.setText(getResources().getString(R.string.delete));
                storeTextView.setBackgroundResource(R.drawable.green_5dp_white);
                storeCountTextview.setText(getString(R.string.source) + book.getSchoolName());
            }
            getThumbnailManager().displayUserIconWithDefault(
                    AppSettings.getFileUrl(book.getCoverUrl()), coverImageview,
                    R.drawable.default_book_cover);//加载书籍封面图片
            //introductionWebview.loadUrl(introductionUrl);
            WebSettings webSettings = introductionWebview.getSettings();
            webSettings.setTextSize(WebSettings.TextSize.LARGEST);
            introductionWebview.loadDataWithBaseURL(null, book.getBrief(), "text/html", "UTF-8", null);
            if (book.getStatus().trim().equals("1")) {
                createState = true;
            } else {
                createState = false;
            }
            if (createState) {
                createStateTextview.setImageResource(R.drawable.ywc_ico);
                introductionLayout.setVisibility(View.VISIBLE);
                catalogLayout.setVisibility(View.VISIBLE);
                if (showType == SHOW_INTRO_VIEW) {
                    showIntroductionView();
                } else {
                    showCatlogView();
                }
                // showIntroductionLayout();
            } else {
                showcatalogLayout();
            }
            if (BookType == BookDetailFragment.Constants.OTHER_BOOK) {
                enterBookStoreBtn.setVisibility(View.GONE);

            } else {
                enterBookStoreBtn.setVisibility(View.VISIBLE);
                enterBookStoreBtn.setTextColor(getResources().getColor(R.color.text_green));
                enterBookStoreBtn.setBackgroundResource(R.drawable.green_5dp_white);
                enterBookStoreBtn.setOnClickListener(this);
            }
        }

    }

    private void showIntroductionLayout() {
        createStateTextview.setImageResource(R.drawable.ywc_ico);
        introductionLayout.setVisibility(View.VISIBLE);
        catalogLayout.setVisibility(View.VISIBLE);
        introductionView.setVisibility(View.VISIBLE);
        catlogView.setVisibility(View.INVISIBLE);
        introductionWebview.setVisibility(View.VISIBLE);
        catalogExpandListView.setVisibility(View.GONE);
        introductionTextView.setTextColor(getResources().getColor(R.color.text_green));
        catalogTextView.setTextColor(getResources().getColor(R.color.black));
    }

    private void showcatalogLayout() {
        createStateTextview.setImageResource(R.drawable.jsz_ico);
        introductionLayout.setVisibility(View.GONE);
        catalogLayout.setVisibility(View.VISIBLE);
        introductionView.setVisibility(View.INVISIBLE);
        catlogView.setVisibility(View.VISIBLE);
        catalogExpandListView.setVisibility(View.VISIBLE);
        introductionWebview.setVisibility(View.GONE);
        introductionTextView.setTextColor(getResources().getColor(R.color.black));
        catalogTextView.setTextColor(getResources().getColor(R.color.text_green));
    }

    private void shareBook(BookDetail book) {
        if (book == null) {
            return;
        }
        String url = book.getShareAddress();//分享地址
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (TextUtils.isEmpty(book.getBookName())) {
            return;
        }
        //vip学校放开分享打开的权限
        if (isVipSchool){
            if(url.contains("?")) {
                url = url + "&open=true";
            } else {
                url = url + "?open=true";
            }
        }
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(book.getBookName());
        shareInfo.setContent("   ");
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(book.getCoverUrl())) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(book.getCoverUrl()));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.default_book_cover);
        }
        shareInfo.setuMediaObject(umImage);

        SharedResource resource = new SharedResource();
        resource.setShareUrl(url);
        resource.setTitle(book.getBookName());
        resource.setThumbnailUrl(AppSettings.getFileUrl(book.getCoverUrl()));
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);

        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }
}
