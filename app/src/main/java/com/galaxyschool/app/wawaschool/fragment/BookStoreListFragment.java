package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.db.BookStoreBookDao;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment;
import com.galaxyschool.app.wawaschool.pojo.BookStoreBookListResult;
import com.galaxyschool.app.wawaschool.pojo.CategoryListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.categoryview.Category;
import com.galaxyschool.app.wawaschool.views.categoryview.CategoryExpandGridviewPopwindow;
import com.galaxyschool.app.wawaschool.views.categoryview.CategoryGridViewPopwindow;
import com.galaxyschool.app.wawaschool.views.categoryview.CategoryGridview.SureSelectListener;
import com.galaxyschool.app.wawaschool.views.categoryview.CategoryValue;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.search.SearchActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.Tab;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 首先用上个页面传过来的schoolId来加载班级，然后找到第一个班级，去查询该班级下的图书
 */
public class BookStoreListFragment extends ContactsListFragment {

    public static final String TAG = BookStoreListFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 3;
    private static final int MAX_CATEGORYS_PER_ROW = 4;
    private TextView headTitletextView;
    private MyGridView generalGridView, allGridView;
    private LinearLayout generalLayout;
    private LinearLayout labelLayout;
    private String generalGridViewTag;
    //SchoolId BookName 要从上个页面传过来
    private TextView keywordView;
    private String keyword = "";
    private SchoolInfo schoolInfo;
    private int fromType;
    private String originSchoolId;
    private BookStoreBook clickBook = null;
    private MyBroadCastReceiver receiver;
    private boolean popwindowClickable = true;
    private String categoryGridviewTag;
    private GridView categoryGridview;
    private List<Category> categoriesAll;
    private boolean isPicBookChoice = false;//是否是精品绘本
    private String schoolId;
    private boolean isPick;
    private boolean isPickSchoolResource;
    private int taskType;

    private boolean isFromAirClass = false;
    //区分是否来自精品资源库
    private boolean isFromChoiceLib;
    private String headerTitle;
    private boolean fromIntroStudyTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookstore_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getIntentData();
        initViews();
        initBroadCastReceiver();
        if (isFromChoiceLib) {
            //如果来自精品资源库
            checkChoiceLibPermission();
        } else {
            loadCategory();
            //判断用户有没有登录
            if (isLogin()) {
                loadSchoolInfo();
            } else {
                loadDatas();
            }
        }
    }

    protected void loadSchoolInfo() {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolInfo != null ? schoolInfo.getSchoolId() : schoolId);
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
                        if (schoolInfo == null) {
                            schoolInfo = getResult().getModel();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadDatas();
                    }
                });
    }

    private void initBroadCastReceiver() {
        receiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter(BookStoreDetailBaseFragment.ACTION_ADD_BOOK);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    private final class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BookStoreDetailBaseFragment.ACTION_ADD_BOOK)) {
                BookStoreBook data = (BookStoreBook) intent.getSerializableExtra("data");
                if (data != null) {
                    if (!TextUtils.isEmpty(getMemeberId())) {
                        addBook2DataBase(data);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addBook2DataBase(BookStoreBook book) {
        if (schoolInfo == null || isFromChoiceLib) {
            return;
        }
        BookStoreBookDao dao = null;
        try {
            dao = BookStoreBookDao.getInstance(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
//            dao.addBook(book, getMemeberId(),schoolInfo.getSchoolId());
            dao.addBook(book, getMemeberId(), book.getSchoolId());
        }
    }

    private boolean loadLoginState() {
        boolean loginState = false;
        if (!TextUtils.isEmpty(getMemeberId())) {
            loginState = true;
        }
        return loginState;
    }

    private void jump2BookDetailByRoleActivity(BookStoreBook book) {
        //课程类型(1公开、2师训、3校内) 用户角色 （1老师 2学生 3家长 4 游客)
        if (book == null) {
            return;
        }
        if (!loadLoginState()) {
            //登录
            enterAccountActivity();
        } else {
            enterBookByRole();
        }
    }

    private void enterAccountActivity() {
        //登录
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }


    private void enterBookByRole() {
        if (schoolInfo == null) {
            return;
        }
        if (clickBook.getCourseType() == 2) {
            if (schoolInfo.isTeacher() || VipConfig.isVip(getActivity())) {
                jump2BookDetailActivity(clickBook);
            } else {
                ToastUtil.showToast(getActivity(), R.string.only_teacher_can_see_the_course);
            }
            return;
        }
        jump2BookDetailActivity(clickBook);
/*
        if (clickBook.getCourseType() == 2) {
            //师训课程只有该机构的老师才能查看
            List<SchoolInfo> schoolInfoList = DemoApplication.getInstance().getPrefsManager()
                    .getJoinSchoolList(getMemeberId());
            if (schoolInfoList != null && schoolInfoList.size() != 0) {
                for (SchoolInfo info : schoolInfoList) {
                    Log.v("ashin","id 1 = "+info.getSchoolId());
                    Log.v("ashin","id 2 = "+clickBook.getSchoolId());
                    Log.v("ashin","role = "+info.getRoles());
                    if (info.getSchoolId().equals(clickBook.getSchoolId()) && info.getRoles().contains(
                            String.valueOf(RoleType.ROLE_TYPE_TEACHER))) {
                        jump2BookDetailActivity(clickBook);
                        return;
                    }
                }
                ToastUtil.showToast(getActivity(), R.string.only_teacher_can_see_the_course);
                return;
            }
        }*/
        //jump2BookDetailActivity(clickBook);
    }

    private void jump2BookDetailActivity(BookStoreBook data) {
        if (data == null) {
            return;
        }
        if (!TextUtils.isEmpty(getMemeberId())) {
            addBook2DataBase(data);
        }
        Bundle args = getArguments();
        args.putString(CatalogLessonListActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(BookDetailActivity.CURRENT_ORZ_SCHOOLID, schoolInfo != null ? schoolInfo
                .getSchoolId() : schoolId);
        args.putString(BookDetailActivity.ORIGIN_SCHOOL_ID, originSchoolId);
        args.putString(BookDetailActivity.BOOK_ID, data.getId());
        args.putInt(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_STORE);
        args.putSerializable("data", data);
        args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB, isFromChoiceLib);
        args.putBoolean(BookDetailFragment.IS_TEACHER, schoolInfo.isTeacher());
        args.putInt(BookDetailFragment.COURSE_TYPE, data.getCourseType());

        //从空中课堂进来作区分，故意把isPick设置false 为了区分跳转选出临时变量
        boolean tempData = args.getBoolean(ActivityUtils.EXTRA_TEMP_DATA);
        if (tempData) {
            isPick = true;
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK, false);
        }
        if (!isPick) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), BookDetailActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = new BookDetailFragment();
            fragment.setArguments(args);
            ft.hide(this);
            ft.add(R.id.activity_body, fragment, BookDetailFragment.TAG);
            ft.show(fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

    }


    private void getIntentData() {
        Bundle args = getArguments();
        if (args != null){
            schoolInfo = (SchoolInfo) args.getSerializable("schoolInfo");
            fromType = getActivity().getIntent().getIntExtra("fromType", 0);
            originSchoolId = args.getString(BookDetailActivity.ORIGIN_SCHOOL_ID);
            schoolId = args.getString(BookDetailActivity.SCHOOL_ID);
            isPick = args.getBoolean(ActivityUtils.EXTRA_IS_PICK);
            taskType = args.getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isPicBookChoice = args.getBoolean(ActivityUtils.IS_PIC_BOOK_CHOICE);
            isPickSchoolResource = args.getBoolean(ActivityUtils
                    .EXTRA_IS_PICK_SCHOOL_RESOURCE);

            //判断是否来自空中课堂的字段
            isFromAirClass = args.getBoolean(SchoolResourceContainerFragment.Constants
                    .FROM_AIRCLASS_ONLINE, false);
            //判断是不是来精品资源库的资源
            isFromChoiceLib = args.getBoolean(ActivityUtils.IS_FROM_CHOICE_LIB, false);
            //来自学习任务的布置
            fromIntroStudyTask = args.getBoolean(ActivityUtils.EXTRA_FROM_INTRO_STUDY_TASK, false);
        }

    }

    private void initCategoryGridview() {
        categoryGridview = (GridView) findViewById(R.id.category_gridview);
        categoryGridview.setNumColumns(MAX_CATEGORYS_PER_ROW);
        if (categoryGridview != null) {
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    categoryGridview, R.layout.item_category_gridview) {
                @Override
                public void loadData() {
                    loadCategory();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    Category data = (Category) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    TextView textView = (TextView) view.findViewById(R.id.value_view);
                    if (textView != null) {
                        String value = "";
                        if (TextUtils.isEmpty(data.getValue())) {
                            value = data.getTypeName();
                        } else {
                            value = data.getValue();
                        }
                        if (value.length() > Category.CATEGOTY_WORD_COUNT_PHONE) {
                            value = value.substring(0, Category.CATEGOTY_WORD_COUNT_PHONE) + "...";
                        }
                        textView.setText(value);
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.select_view);
                    if ((position == Category.CATEGOTY_ROW_COUNT_PHONE - 1) && !isFromChoiceLib) {
                        if (data.isSlide()) {
                            imageView.setImageResource(R.drawable.categoty_filter_pre_ico);
                            textView.setTextColor(context.getResources().getColor(R.color
                                    .text_green));
                        } else {
                            imageView.setImageResource(R.drawable.category_filter_ico);
                            textView.setTextColor(context.getResources().getColor(R.color
                                    .text_dark_gray));
                        }
                    } else {
                        if (imageView != null) {
                            if (data.isSlide()) {
                                imageView.setImageResource(R.drawable.select_up_green_icon);
                                textView.setTextColor(context.getResources().getColor(R.color
                                        .text_green));
                            } else {
                                imageView.setImageResource(R.drawable.select_down_gray_icon);
                                textView.setTextColor(context.getResources().getColor(R.color
                                        .text_dark_gray));
                            }
                        }
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    Category data = (Category) holder.data;
                    data.setSlide(!data.isSlide());
                    getAdapterViewHelper(categoryGridviewTag).update();
                    if ((position == Category.CATEGOTY_ROW_COUNT_PHONE - 1) && !isFromChoiceLib) {
//                        List<Category> categories = categoriesAll.subList(Category
//                                .CATEGOTY_ROW_COUNT_PHONE , categoriesAll.size());
                        popCategoryExpandListView(categoriesAll);
                    } else {
                        popCategoryListView(data);
                    }
                }
            };
            this.categoryGridviewTag = String.valueOf(categoryGridview.getId());
            addAdapterViewHelper(this.categoryGridviewTag, gridViewHelper);
        }
    }

    private void initAllGridview() {
        allGridView = (MyGridView) findViewById(R.id.booksore_list_all_gridview);
        if (allGridView != null) {
            allGridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    allGridView, R.layout.book_store_main_item) {
                @Override
                public void loadData() {
                    loadBookData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    BookStoreBook data = (BookStoreBook) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }

                    //设置外层布局为A4比例
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.item_book_cover);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    //之前宽 90 高 120
                    int width = DensityUtils.dp2px(getActivity(), 90);
                    layoutParams.width = width;
                    layoutParams.height = width * 297 / 210;
                    frameLayout.setLayoutParams(layoutParams);


                    ImageView imageView = (ImageView) view.findViewById(R.id.item_shelf);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getCoverUrl()), imageView,
                                R.drawable.default_book_cover);
                    }

                    ImageView ivCourseType = (ImageView) view.findViewById(R.id.item_course_flag);
                    if (data.getCourseType() == 2) {
                        ivCourseType.setVisibility(View.VISIBLE);
                    } else {
                        ivCourseType.setVisibility(View.GONE);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getBookName());
                    }
                    ImageView imageView_desc = (ImageView) view.findViewById(R.id.item_description);
                    if (imageView_desc != null) {
                        imageView_desc.setVisibility(View.GONE);
//                        if (data.getStatus() == 1) {
//                            imageView_desc.setImageResource(R.drawable.ywc_ico);
//                        } else {
//                            imageView_desc.setImageResource(R.drawable.jsz_ico);
//                        }
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    BookStoreBook data = (BookStoreBook) holder.data;
                    if (TextUtils.isEmpty(data.getId())) {
                        return;
                    }
                    clickBook = null;
                    clickBook = data;
                    jump2BookDetailByRoleActivity(data);
                }
            };
            setCurrAdapterViewHelper(allGridView, gridViewHelper);
        }
    }


    private void initGeneralGridview() {
        generalGridView = (MyGridView) findViewById(R.id.booksore_list_general_gridview);
        if (generalGridView != null) {
            generalGridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    generalGridView, R.layout.book_store_main_item) {
                @Override
                public void loadData() {
                    loadBookGeneralList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    BookStoreBook data = (BookStoreBook) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //设置外层布局为A4比例
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.item_book_cover);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    //之前宽 90 高 120
                    int width = DensityUtils.dp2px(getActivity(), 90);
                    layoutParams.width = width;
                    layoutParams.height = width * 297 / 210;
                    frameLayout.setLayoutParams(layoutParams);

                    ImageView imageView = (ImageView) view.findViewById(R.id.item_shelf);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getCoverUrl()), imageView,
                                R.drawable.default_book_cover);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getBookName());
                    }
                    ImageView ivCourseType = (ImageView) view.findViewById(R.id.item_course_flag);
                    if (data.getCourseType() == 2) {
                        ivCourseType.setVisibility(View.VISIBLE);
                    } else {
                        ivCourseType.setVisibility(View.GONE);
                    }
                    ImageView imageView_desc = (ImageView) view.findViewById(R.id.item_description);
                    if (imageView_desc != null) {
                        imageView_desc.setVisibility(View.GONE);
//                        if (data.getStatus() == 1) {
//                            imageView_desc.setImageResource(R.drawable.ywc_ico);
//                        } else {
//                            imageView_desc.setImageResource(R.drawable.jsz_ico);
//                        }
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    BookStoreBook data = (BookStoreBook) holder.data;
                    if (TextUtils.isEmpty(data.getId())) {
                        return;
                    }
                    clickBook = null;
                    clickBook = data;
                    jump2BookDetailByRoleActivity(data);
                }
            };
            this.generalGridViewTag = String.valueOf(generalGridView.getId());
            addAdapterViewHelper(this.generalGridViewTag, gridViewHelper);
        }
    }


    private void initViews() {
        //title的搜索
        ImageView rightSearchImageV = (ImageView) findViewById(R.id.contacts_header_right_ico);
        rightSearchImageV.setImageResource(R.drawable.search);
        rightSearchImageV.setVisibility(View.VISIBLE);
        rightSearchImageV.setOnClickListener(v -> search());

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadBookData();
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
                    getPageHelper().clear();
                    loadBookData();
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
                    loadBookData();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            //隐藏搜索框
            view.setVisibility(View.GONE);
        }
        generalLayout = (LinearLayout) findViewById(R.id.general_layout);
        generalLayout.setVisibility(View.GONE);
        headTitletextView = (TextView) findViewById(R.id.contacts_header_title);
        headTitletextView.setVisibility(View.VISIBLE);
        labelLayout = (LinearLayout) findViewById(R.id.ll_label);
        if (isPick) {
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
            } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE) {
                headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
            } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE || taskType ==
                    StudyTaskType.LISTEN_READ_AND_WRITE) {
                headerTitle = getString(R.string.appoint_course_no_point);
            } else if (taskType == StudyTaskType.TASK_ORDER) {
                headerTitle = getString(R.string.pls_add_work_task);
            }
            if (isPickSchoolResource) {
                if (isFromChoiceLib) {
                    headerTitle = getString(R.string.choice_books);
                } else {
                    headerTitle = getString(R.string.public_course);
                }
            }
            headTitletextView.setText(headerTitle);
        } else {
            if (isPicBookChoice || isFromChoiceLib) {
                headerTitle = getString(R.string.choice_books);
            } else {
                headerTitle = getString(R.string.public_course);
            }
            headTitletextView.setText(headerTitle);
        }
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);
        TextView textView1 = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView1 != null) {
            textView1.setVisibility(View.INVISIBLE);
        }
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh_all);
        setPullToRefreshView(pullToRefreshView);
        initAllGridview();
        initGeneralGridview();
        initCategoryGridview();
    }

    /**
     * 进入搜索页面
     */
    private void search(){
        NewOnlineStudyFiltrateParams mFiltrateParams = new NewOnlineStudyFiltrateParams(getString
                (R.string.public_course),new NewOnlineConfigEntity());
        SearchActivity.show(getActivity(), HideSortType.TYPE_SORT_TEACH_ONLINE_CLASS,mFiltrateParams);
    }

    private void loadDatas() {
        if (isFromChoiceLib) {
            loadChoiceData();
            return;
        }
        //这里还要写从本地加载常用数据（!!!!）
        loadBookGeneralList();
        getPageHelper().clear();
        loadBookData();
    }


    private void loadBookGeneralList() {
        //如果是筛选选取资源不显示常用
//        if (isPick && generalLayout != null){
//            generalLayout.setVisibility(View.GONE);
//            return;
//        }
        BookStoreBookDao dao = null;
        try {
            dao = BookStoreBookDao.getInstance(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            if (schoolInfo == null) return;
            List<BookStoreBook> list = dao.getBookList(getMemeberId(), schoolInfo.getSchoolId().
                    toLowerCase());
            if (list != null && list.size() > 0) {
                generalLayout.setVisibility(View.VISIBLE);
                filterData(list);
                getAdapterViewHelper(generalGridViewTag).setData(list);
            } else {
                generalLayout.setVisibility(View.GONE);
            }
        }
    }

    private void loadBookData() {
        if (isFromChoiceLib) {
            loadChoiceDataList();
            return;
        }
//        String keyword = this.keywordView.getText().toString().trim();
//        if (!keyword.equals(this.keyword)) {
//            getCurrAdapterViewHelper().clearData();
//            getPageHelper().clear();
//        }
//        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("BookName", this.keyword);
//        params.put("SchoolId", schoolInfo.getSchoolId() != null ? schoolInfo.getSchoolId() : "");
        String tempSchoolId = "";
        if (schoolInfo != null && !TextUtils.isEmpty(schoolInfo.getSchoolId())) {
            tempSchoolId = schoolInfo.getSchoolId();
        } else {
            tempSchoolId = schoolId;
        }
        params.put("SchoolId", tempSchoolId);
        handleLabelData(params);
//        if (categoriesAll != null && categoriesAll.size() > 0) {
//            for (Category category : categoriesAll) {
//                if (category != null) {
//                    switch (category.getType()) {
//                        case Category.CATEGOTY_TYPE_VERSION:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("VersionId", category.getIds());
//                            }
//                            break;
//                        case Category.CATEGOTY_TYPE_LEVEL:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("LevelId", category.getIds());
//                            }
//                            break;
//                        case Category.CATEGOTY_TYPE_GRADE:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("GradeId", category.getIds());
//                            }
//                            break;
//                        case Category.CATEGOTY_TYPE_SUBJECT:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("SubjectId", category.getIds());
//                            }
//                            break;
//                        case Category.CATEGOTY_TYPE_VOLUME:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("VolumeId", category.getIds());
//                            }
//                            break;
//                        case Category.CATEGOTY_TYPE_LANGUAGE:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("LanguageId", category.getIds());
//                            }
//                            break;
//                        case Category.CATEGOTY_TYPE_PUBLISHER:
//                            if (!TextUtils.isEmpty(category.getIds())) {
//                                params.put("PublisherId", category.getIds());
//                            }
//                            break;
//                    }
//                }
//            }
//        }
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        String schoolMaterialType = "1,2,3,4,5,6,7,8,9,10,11,12";
        if (isPickSchoolResource) {
            if (getArguments() != null && getArguments().containsKey(MediaListFragment
                    .EXTRA_SHOW_MEDIA_TYPES)) {
                List<Integer> mediaTypes = getArguments().getIntegerArrayList(MediaListFragment
                        .EXTRA_SHOW_MEDIA_TYPES);
                if (mediaTypes != null && mediaTypes.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0, size = mediaTypes.size(); i < mediaTypes.size(); i++) {
                        Integer item = mediaTypes.get(i);
                        builder.append(item);
                        if (i != size - 1) {
                            builder.append(",");
                        }
                    }
                    schoolMaterialType = builder.toString();
                }
            }
        }
        params.put("SchoolMaterialType", schoolMaterialType);
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<BookStoreBookListResult>(
                        BookStoreBookListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        BookStoreBookListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateAllBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_SCHOOL_BOOK_LIST_URL, params, listener);
    }

    private void handleLabelData(Map<String,Object> params){
        if (categoriesAll != null && categoriesAll.size() > 0){
            outer: for (Category category : categoriesAll){
                List<CategoryValue> valueList = category.getDetailList();
                if (valueList != null){
                    for (CategoryValue value : valueList){
                        if (value != null
                                && !TextUtils.isEmpty(value.getId())
                                && value.isSelect()){
                            switch (category.getType()) {
                                case Category.CATEGOTY_TYPE_VERSION:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("VersionId", value.getId());
                                    }
                                    break;
                                case Category.CATEGOTY_TYPE_LEVEL:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("LevelId", value.getId());
                                    }
                                    break;
                                case Category.CATEGOTY_TYPE_GRADE:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("GradeId", value.getId());
                                    }
                                    break;
                                case Category.CATEGOTY_TYPE_SUBJECT:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("SubjectId", value.getId());
                                    }
                                    break;
                                case Category.CATEGOTY_TYPE_VOLUME:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("VolumeId", value.getId());
                                    }
                                    break;
                                case Category.CATEGOTY_TYPE_LANGUAGE:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("LanguageId", value.getId());
                                    }
                                    break;
                                case Category.CATEGOTY_TYPE_PUBLISHER:
                                    if (!TextUtils.isEmpty(value.getId())) {
                                        params.put("PublisherId", value.getId());
                                    }
                                    break;
                            }
                            continue outer;
                        }
                    }
                }
            }
        }
    }

    private void updateAllBookListView(BookStoreBookListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<BookStoreBook> list = result.getModel().getData();
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
                if (getCurrAdapterViewHelper().hasData()) {
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().clear();
                }
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                filterData(list);
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
            } else {
                filterData(list);
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    /**
     * 筛选数据
     */
    private void filterData(List<BookStoreBook> data) {
        if (schoolInfo != null && !schoolInfo.isTeacher()) {
            Iterator<BookStoreBook> iterator = data.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getCourseType() == 2) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            if (isPick) {
                if (isFromAirClass || fromIntroStudyTask){
                    super.onClick(v);
                } else {
                    popStack();
                }
            } else {
                super.onClick(v);
            }
        }
    }

    private void loadCategory() {
        if (isFromChoiceLib) {
            loadChoiceCategory();
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo != null ? schoolInfo.getSchoolId() : schoolId);
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<CategoryListResult>(getActivity(),
                        CategoryListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        CategoryListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        List<Category> categoriesTemp = result.getModel().getData();
                        if (categoriesTemp == null || categoriesTemp.size() == 0) {
                            return;
                        }
                        categoriesAll = categoriesTemp.subList(1, 4);
                        // categoriesAll= result.getModel().getData();
                        List<Category> categories = new ArrayList<Category>();
                        if (categoriesAll.size() > Category.CATEGOTY_ROW_COUNT_PHONE - 1) {
                            for (int i = 0; i < Category.CATEGOTY_ROW_COUNT_PHONE - 1; i++) {
                                categories.add(categoriesAll.get(i));
                            }
                            Category category1 = new Category();
                            category1.setTypeName(getString(R.string.screening));
                            categories.add(category1);
                        } else {
                            categoryGridview.setNumColumns(categoriesAll.size());
                            categories.addAll(categoriesAll);
                        }
                        getAdapterViewHelper(categoryGridviewTag).setData(categories);
                        initCategoryLabelView();
                    }
                };
        postRequest(ServerUrl.LOAD_OUTLINE_ATR_URL, params, listener);
    }

    /**
     * 检验精品资源库是否开通权限
     */
    private void checkChoiceLibPermission() {
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo != null ? schoolInfo.getSchoolId() : schoolId);
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener(getActivity(),
                        ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(jsonString);
                            boolean flag = jsonObject.optBoolean("Model");
                            if (flag) {
                                findViewById(R.id.layout_school_res).setVisibility(View.VISIBLE);
                                findViewById(R.id.layout_no_vip_permissions_lay).setVisibility(View.GONE);
                                loadSchoolInfo();
                            } else {
                                findViewById(R.id.layout_school_res).setVisibility(View.GONE);
                                findViewById(R.id.layout_no_vip_permissions_lay).setVisibility
                                        (View.VISIBLE);
                                findViewById(R.id.tv_phone_number).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        phoneClick();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
        postRequest(ServerUrl.CHECK_CHOICE_LIB_PERMISSION_BASE_URL, params, listener);
    }

    /**
     * 加载精品资源库的数据
     */
    private void loadChoiceData() {
        loadChoiceCategory();
        getPageHelper().clear();
        loadChoiceDataList();
    }

    /**
     * 获取精品资源库的标签
     */
    private void loadChoiceCategory() {
        Map<String, Object> params = new HashMap<>();
        params.put("SchoolId", schoolInfo != null ? schoolInfo.getSchoolId() : schoolId);
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<CategoryListResult>(getActivity(),
                        CategoryListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        CategoryListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        List<Category> categoriesTemp = result.getModel().getData();
                        if (categoriesTemp == null || categoriesTemp.size() == 0) {
                            return;
                        }
                        //精品资源库去掉第一tab标签
                        categoriesAll = categoriesTemp.subList(1, 4);
                        // categoriesAll= result.getModel().getData();
                        List<Category> categories = new ArrayList<Category>();
                        categoryGridview.setNumColumns(categoriesAll.size());
                        categories.addAll(categoriesAll);
                        getAdapterViewHelper(categoryGridviewTag).setData(categories);
                        initCategoryLabelView();
                    }
                };
        postRequest(ServerUrl.GET_CHOICE_LIB_TAG_BASE_URL, params, listener);
    }

    /**
     * 加载精品资源库的list
     */
    private void loadChoiceDataList() {
//        String keyword = this.keywordView.getText().toString().trim();
//        if (!keyword.equals(this.keyword)) {
//            getCurrAdapterViewHelper().clearData();
//            getPageHelper().clear();
//        }
//        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("BookName", this.keyword);
        String tempSchoolId = "";
        if (schoolInfo != null && !TextUtils.isEmpty(schoolInfo.getSchoolId())) {
            tempSchoolId = schoolInfo.getSchoolId();
        } else {
            tempSchoolId = schoolId;
        }
        params.put("SchoolId", tempSchoolId);
        //书本标签的集合
        params.put("OutlineTag", getSelectCondition());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<BookStoreBookListResult>(
                        BookStoreBookListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        BookStoreBookListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateAllBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CHOICE_LIB_DATA_LIST_BASE_URL, params, listener);
    }

    /**
     * 获取筛选的条件
     */
    private String getSelectCondition() {
        StringBuilder builder = new StringBuilder();
        if (categoriesAll != null && categoriesAll.size() > 0) {
            for (int i = 0, len = categoriesAll.size(); i < len; i++) {
                Category category = categoriesAll.get(i);
                if (category != null) {
                    List<CategoryValue> values = category.getDetailList();
                    if (values != null && values.size() > 0) {
                        for (int j = 0, length = values.size(); j < length; j++) {
                            CategoryValue value = values.get(j);
                            if (value != null
                                    && !TextUtils.isEmpty(value.getId())
                                    && value.isSelect()) {
                                if (builder.length() == 0) {
                                    builder.append(value.getId());
                                } else {
                                    builder.append(",").append(value.getId());
                                }
                            }
                        }
                    }
                }
            }
        }
        return builder.toString();

    }

    /**
     * 打电话
     */
    public void phoneClick() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:400-1007-727"));
        startActivity(intent);
    }

    private void popCategoryListView(final Category category) {
        View view = findViewById(R.id.category_split_view);
        if (isFromChoiceLib && category != null) {
            //精品资源库标签课堂分类 和 学科仅支持单选
            if (category.getType() == 1 || category.getType() == 4) {
                category.setSelectMode(Category.SELECT_SINGLE_MODE);
            }
        }
        final CategoryGridViewPopwindow popwindow = new CategoryGridViewPopwindow(getActivity(), category, new
                SureSelectListener() {
                    @Override
                    public void onSureSelect() {
                        List<CategoryValue> values = category.getDetailList();
                        String selectVaules = "";
                        String selectIds = "";
                        if (values != null && values.size() > 0) {
                            for (CategoryValue value : values) {
                                if (value.isSelect()) {
                                    selectVaules += value.getName() + ",";
                                    selectIds += value.getId() + ",";
                                }
                            }
                            if (selectVaules.length() > 0) {
                                selectVaules = selectVaules.substring(0, selectVaules.length() - 1);
                                category.setValue(selectVaules);
                            } else {
                                category.setValue("");
                            }
                            if (selectIds.length() > 0) {
                                selectIds = selectIds.substring(0, selectIds.length() - 1);
                                category.setIds(selectIds);
                            } else {
                                category.setIds("");
                            }
                            getAdapterViewHelper(categoryGridviewTag).update();
                            getPageHelper().clear();
                            loadBookData();
                        }
                    }
                });
        // popwindow.setAnimationStyle(R.style.popwindow_up_in_up_out);
        if (popwindowClickable) {
            popwindow.showPopupMenu(view);
            popwindowClickable = false;
        }
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popwindowClickable = true;
                category.setSlide(!category.isSlide());
                getAdapterViewHelper(categoryGridviewTag).update();
            }
        });

    }

    private void popCategoryExpandListView(final List<Category> categories) {
        View view = findViewById(R.id.category_split_view);
        final CategoryExpandGridviewPopwindow popwindow = new CategoryExpandGridviewPopwindow(getActivity(),
                categories, new
                CategoryExpandGridviewPopwindow.SureSelectListener() {
                    @Override
                    public void onSureSelect() {

                        for (Category category1 : categories) {
                            String selectVaules = "";
                            String selectIds = "";
                            List<CategoryValue> values = category1.getDetailList();
                            if (values != null && values.size() > 0) {
                                for (CategoryValue value : values) {
                                    if (value.isSelect()) {
                                        selectVaules += value.getName() + ",";
                                        selectIds += value.getId() + ",";
                                    }
                                }
                                if (selectVaules.length() > 0) {
                                    selectVaules = selectVaules.substring(0, selectVaules.length() - 1);
                                    category1.setValue(selectVaules);
                                } else {
                                    category1.setValue("");
                                }
                                if (selectIds.length() > 0) {
                                    selectIds = selectIds.substring(0, selectIds.length() - 1);
                                    category1.setIds(selectIds);
                                } else {
                                    category1.setIds("");
                                }
                            }
                        }
                        getPageHelper().clear();
                        loadBookData();
                    }
                });
        popwindow.setAnimationStyle(R.style.popwindow_right_in_right_out);
        if (popwindowClickable) {
            popwindow.showPopupMenu(view);
            popwindowClickable = false;
        }
        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popwindowClickable = true;
                List<Category> categoryList = getAdapterViewHelper(categoryGridviewTag).getData();
                categoryList.get(categoryList.size() - 1).setSlide(!categoryList.get(categoryList
                        .size() - 1).isSlide());
                getAdapterViewHelper(categoryGridviewTag).update();
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateBookDao(MessageEvent messageEvent) {
        if (messageEvent != null) {
            String updateAction = messageEvent.getUpdateAction();
            if (TextUtils.equals(updateAction, "update_local_book_dao")) {
                String type = messageEvent.getBundle().getString("book_type");
                updateBook(clickBook, Integer.valueOf(type));
                loadBookGeneralList();
            }
        }
    }


    private void updateBook(BookStoreBook book, int type) {
        if (schoolInfo == null || isFromChoiceLib) {
            return;
        }
        BookStoreBookDao dao = null;
        try {
            dao = BookStoreBookDao.getInstance(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.updateBook(book, getMemeberId(), book.getSchoolId(), type);
        }
    }

    /**
     * 更新最近的标签数据
     */
    private void initCategoryLabelView() {
        if (categoriesAll != null && categoriesAll.size() > 0){
            labelLayout.removeAllViews();
            for (int i = 0; i < categoriesAll.size(); i++){
                Category category = categoriesAll.get(i);
                View labelView = LayoutInflater.from(getActivity()).inflate(R.layout
                        .layout_label_view,null);
                TextView labelTitleView = (TextView) labelView.findViewById(R.id.tv_label_type);
                //类型名
                labelTitleView.setText(category.getTypeName());
                //tabel的content
                List<CategoryValue> valueList = category.getDetailList();
                TabLayout labelContentView = (TabLayout) labelView.findViewById(R.id.tl_label_content);
                if (valueList != null && valueList.size() > 0){
                    CategoryValue allValue = new CategoryValue();
                    allValue.setName(getString(R.string.all));
                    allValue.setId("");
                    allValue.setSelect(true);
                    valueList.add(0,allValue);
                    for (int j = 0; j < valueList.size(); j++){
                        CategoryValue value = valueList.get(j);
                        View tableItemView = LayoutInflater.from(getActivity()).inflate(R.layout
                                .item_tab_control_layout,null);
                        TextView tvContent = (TextView) tableItemView.findViewById(com.lqwawa.intleducation.R.id.tv_content);
                        tvContent.setText(value.getName());
                        TabLayout.Tab newTab = labelContentView.newTab().setCustomView(tableItemView).setTag(value);
                        labelContentView.addTab(newTab);
                    }
                }
                labelContentView.addOnTabSelectedListener(new TabSelectedAdapter(){
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        // 全部发生数据联动
                        CategoryValue value = (CategoryValue) tab.getTag();
                        onClickLabelItem(value,valueList);
                        getPageHelper().clear();
                        loadBookData();
                    }
                });
                labelLayout.addView(labelView);
            }
        }
    }

    private void onClickLabelItem(CategoryValue value,List<CategoryValue> valueList){
        for (int i = 0; i < valueList.size(); i++){
            if (valueList.get(i) == value){
                valueList.get(i).setSelect(true);
            } else {
                valueList.get(i).setSelect(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewOnlineStudyFiltrateActivity.SEARCH_REQUEST_CODE){
            String searchTitle = null;
            if (data != null){
                searchTitle = data.getStringExtra(SearchActivity.KEY_EXTRA_SEARCH_KEYWORD);
            }
            if (TextUtils.isEmpty(searchTitle)){
                headTitletextView.setText(headerTitle);
            } else {
                headTitletextView.setText(searchTitle);
            }
            if (TextUtils.isEmpty(this.keyword) && TextUtils.isEmpty(searchTitle)){

            } else {
                this.keyword = searchTitle;
                getPageHelper().clear();
                loadBookData();
            }
        }
    }
}
