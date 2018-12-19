package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.BookStoreListActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.BookStoreBookDao;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.BookHelper;
import com.galaxyschool.app.wawaschool.pojo.BookStoreBookListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.galaxyschool.app.wawaschool.R.id.bookstore_more_layout;

public class BookStoreFragment extends ContactsListFragment {

    public static final String TAG = BookStoreFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 3;
    public static final int HOME_PAGE = 0;
    public static final int ATTENTION_PAGE = 1;
    public static final int REQUESTCODE_ATTENTION = 1001;
    public static final int REQUESTCODE_DETAIL = 1002;
    private BookStoreBook emptyBook = new BookStoreBook();
    private TextView rightHeadTextView;
    private TextView headTitleTextView;
    private TextView toggleSchoolLeftTextview;

    //1:如果未登录（登录了但是关注列表为空时）加载的数据是银河小学的数据，2：如果加载关注列表（第一个关注学校或者记忆的最近一次点击的学校）
    private List<SchoolInfo> attentionSchoolList;//关注的学校集合
    private int fromType = HOME_PAGE;//From_Type==1 来自关注页
    protected SchoolInfo schoolInfo = null;
    private ImageView headerLeftbtn;
    private BookStoreBook jumpBook;
    private boolean loginTag = false;//如果登录回调回来进入onActivityResult方法，loginTag=true，不执行onResume的加载数据方法，执行onActivityResult的相关处理和加载数据方法
    private BookHelper bookHelper = new BookHelper();

    private MyBroadCastReceiver receiver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_store, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initBroadCastReceiver();
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
        loadDatas();
    }

    private void saveLatestSchool(String memberId, String schoolId) {//记录最近点击的学校
        SharedPreferencesHelper.setString(getActivity(), memberId + "_SchoolId", schoolId);
    }

    private String getLatestSchool(String memberId) {//加载最近点击的学校
        return SharedPreferencesHelper.getString(getActivity(), memberId + "_SchoolId");
    }

    private void barViewfromAttention() {
        toggleSchoolLeftTextview.setVisibility(View.GONE);
        headerLeftbtn.setVisibility(View.VISIBLE);
        headTitleTextView.setText(schoolInfo != null ? schoolInfo.getSchoolName() : "");
        headTitleTextView.setVisibility(View.VISIBLE);
        rightHeadTextView.setVisibility(View.VISIBLE);
        rightHeadTextView.setTextColor(getResources().getColor(R.color.text_green));
        rightHeadTextView.setOnClickListener(this);
        if (schoolInfo.getState() == 0) {
            rightHeadTextView.setText(R.string.wawatong_attention);
        } else if (schoolInfo.getState() == 1) {
            rightHeadTextView.setText(R.string.cancel_follow);
        } else {
            rightHeadTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void barViewFromHomePage() {
        headTitleTextView.setVisibility(View.VISIBLE);
        headTitleTextView.setText(schoolInfo != null ? schoolInfo.getSchoolName() : "");
        rightHeadTextView.setVisibility(View.VISIBLE);
        rightHeadTextView.setTextColor(getResources().getColor(R.color.text_green));
        rightHeadTextView.setOnClickListener(this);
        if (schoolInfo.getState() == 0) {
            rightHeadTextView.setText(R.string.wawatong_attention);
            toggleSchoolLeftTextview.setVisibility(View.GONE);
        } else {
            rightHeadTextView.setVisibility(View.GONE);
            toggleSchoolLeftTextview.setVisibility(View.VISIBLE);

        }

    }

    private void loadDatas() {
        loginTag = false;
        fromType = getActivity().getIntent().getIntExtra("fromType", 0);
        if (fromType == ATTENTION_PAGE) {
            schoolInfo = null;
            schoolInfo = (SchoolInfo) getArguments().getSerializable("schoolInfo");
            barViewfromAttention();
            getPageHelper().clear();
            loadAttentionData();
        } else {
            if (loginTag) {
                loginTag = false;
            } else {
                if (!loadLoginState()) {
                    if (bookHelper.isNeedUseCache(getMemeberId())) {
                        getCurrAdapterViewHelper().setData(bookHelper.getBookList());
                        barViewFromHomePage();
                    } else {
                        loadSchoolInfo();
                    }
                } else {
                    loadAttentionSchoolInfo();
                }
            }
        }
    }

    private void loadBookData() {
        if (TextUtils.isEmpty(getMemeberId())) {
            loadDefaultData();
        } else {
            loadAttentionData();
        }
    }

    private void addBook2DataBase(BookStoreBook book) {
        if(schoolInfo==null){
            return;
        }
        BookStoreBookDao dao = null;
        try {
            dao = BookStoreBookDao.getInstance(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.addBook(book, getMemeberId(),schoolInfo.getSchoolId());
        }
    }

    private void jump2BookDetailActivity(BookStoreBook data) {
//        if (!TextUtils.isEmpty(getMemeberId())) {
//            addBook2DataBase(data);
//        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.SCHOOL_ID, data.getSchoolId());
        intent.putExtra(BookDetailActivity.BOOK_ID, data.getId());
        intent.putExtra(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_STORE);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    ;

    private void jump2BookDetailByRoleActivity() {
        //课程类型(1公开、2师训、3校内) 用户角色 （1老师 2学生 3家长 4 游客)
        if (jumpBook == null) {
            return;
        }
        if (!loadLoginState()) {
            enterAccountActivity(REQUESTCODE_DETAIL);
        } else {
            enterBookByRole();
        }
    }

    ;

    private void enterAccountActivity(int requestCode) {
        //登录
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
        intent.putExtras(args);
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
        }
    }

    ;

    private void enterBookByRole() {
        if (schoolInfo == null) {
            return;
        }
        jump2BookDetailActivity(jumpBook);
    }

    ;


    private boolean loadLoginState() {
        boolean loginState = false;
        if (!TextUtils.isEmpty(getMemeberId())) {
            loginState = true;
        }
        return loginState;
    }

    private void loadAttentionSchoolInfo() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params,
                new DefaultListener<SubscribeSchoolListResult>(
                        SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null || result.getModel().getSubscribeNoList().size() == 0) {
                            loadSchoolInfo();
                        } else {
                            initAttentionSchoolView(result);
                        }
                    }
                });
    }

    ;

    protected void loadSchoolInfo() {
        Map<String, Object> params = new HashMap();
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.BOOKSTORE_SEARCH_DEFAULT_SCHOOK_URL,
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
                        schoolInfo = null;
                        schoolInfo = getResult().getModel();
                        if (schoolInfo == null) {
                            return;
                        }
                        if (bookHelper.isNeedUseCache(getMemeberId())) {
                            getCurrAdapterViewHelper().setData(bookHelper.getBookList());
                        } else {
                            getPageHelper().clear();
                            loadDefaultData();
                        }
                        barViewFromHomePage();
                    }
                });
    }


    private void initAttentionSchoolView(SubscribeSchoolListResult result) {
        attentionSchoolList = result.getModel().getSubscribeNoList();
        if (bookHelper.isNeedUseCache2(getMemeberId(), attentionSchoolList)) {
            getCurrAdapterViewHelper().setData(bookHelper.getBookList());
            schoolInfo = bookHelper.getSchoolInfo();
        } else {
            updateSchoolInfo();
            getPageHelper().clear();
            loadAttentionData();
        }
        barViewFromHomePage();
    }

    private void updateSchoolInfo() {//加载关注列表和当前学校
        schoolInfo = null;
        if (attentionSchoolList != null && attentionSchoolList.size() > 0) {
            if (TextUtils.isEmpty(getMemeberId())) {
                return;
            }
            if (!TextUtils.isEmpty(getLatestSchool(getMemeberId()))) {
                for (SchoolInfo sc : attentionSchoolList) {
                    if (sc.getSchoolId().equals(getLatestSchool(getMemeberId()))) {
                        schoolInfo = sc;
                        break;
                    }
                }
            }
            if (schoolInfo == null) {
                schoolInfo = attentionSchoolList.get(0);
            }
        }
    }

    //1:如果未登录（登录了但是关注列表为空时）加载的数据是银河小学的数据
    private void loadDefaultData() {
        Map<String, Object> params = new HashMap();
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
                        updateBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.BOOKSTORE_SEARCH_DEFAULT_BOOKLIST_URL, params, listener);
    }

    //2：如果加载关注列表（第一个关注学校或者记忆的最近一次点击的学校）
    private void loadAttentionData() {
        if (schoolInfo == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());
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
                        updateBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.BOOKSTORE_SEARCH_BOOKLIST_URL, params, listener);
    }

    ;


    //切换学校
    private void showWaWaTongMenu(Activity activity, AdapterView.OnItemClickListener itemClickListener, View AnchorView) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList<PopupMenu.PopupMenuData>();
        if (attentionSchoolList == null || attentionSchoolList.size() == 0) {
            return;
        }
        for (int i = 0; i < attentionSchoolList.size(); i++) {
            PopupMenu.PopupMenuData data = new PopupMenu.PopupMenuData();
            if (attentionSchoolList.get(i) != null && !TextUtils.isEmpty(attentionSchoolList.get(i).getSchoolName())) {
                data.setText(attentionSchoolList.get(i).getSchoolName());
                itemDatas.add(data);
            }
        }
        PopupMenu popupMenu = new PopupMenu(activity, itemClickListener, itemDatas);
        popupMenu.showAsDropDown(AnchorView, (int) (10 * MyApplication.getDensity()), 0);
        popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                rightHeadTextView.setVisibility(View.VISIBLE);
//                rightHeadTextView.setText(R.string.toggle_school);
//                rightHeadTextView.setTextColor(getResources().getColor(R.color.text_green));
            }
        });
    }


    private AdapterView.OnItemClickListener ToggleItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {

            SchoolInfo schoolInfo1 = attentionSchoolList.get(position);
            if (schoolInfo1 != null && !TextUtils.isEmpty(schoolInfo1.getSchoolId())) {
                if (getUserInfo() == null || TextUtils.isEmpty(getUserInfo().getMemberId())) {
                    return;
                }
                saveLatestSchool(getUserInfo().getMemberId(), schoolInfo1.getSchoolId());
            }
            if (schoolInfo1.getSchoolId().equals(schoolInfo.getSchoolId())) {
                return;
            } else {
                getCurrAdapterViewHelper().clearData();
                getPageHelper().clear();
                schoolInfo = schoolInfo1;
            }
            headTitleTextView.setVisibility(View.VISIBLE);
            headTitleTextView.setText(schoolInfo.getSchoolName());
            loadAttentionData();
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.contacts_header_right_btn:
                if (fromType == ATTENTION_PAGE) {//来自关注页
                    if (schoolInfo.getState() == 0) {
                        subscribeSchool(true); //关注学校代码
                    } else if (schoolInfo.getState() == 1) {
                        subscribeSchool(false);//取消关注学校代码
                    }
                } else {//来自主页
                    if (loadLoginState()) {
                        if (schoolInfo == null) {
                            return;
                        }
                        if (schoolInfo.getState() == 0) {
                            attentionLoadSchool(true);    //关注学校代码
                        }
                    } else {
                        enterAccountActivity(REQUESTCODE_ATTENTION);
                    }
                }
                break;
            case bookstore_more_layout:
                jump2BookStoreListActivity();
                break;
            case R.id.toggle_school_left_textview:
                showWaWaTongMenu(getActivity(), ToggleItemClickListener, (View) v.getParent());
                break;
        }
    }

   @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUESTCODE_ATTENTION) {
            loginTag = true;
            getPageHelper().clear();
            attentionLoadSchool(true);
        } else if (requestCode == REQUESTCODE_DETAIL) {
            loginTag = true;
             getPageHelper().clear();
            loadAttentionSchoolInfo();
        }
    }


    //关注并且设置查询TAG，TAG==true（不是登录返回过来要查询数据）
    private void attentionLoadSchool(boolean subscribe) {
        if (schoolInfo == null) {
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolInfo.getSchoolId());
        DefaultListener<ModelResult> listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (schoolInfo == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        boolean subscribe = ((Boolean) getTarget()).booleanValue();
                        if (getResult() == null || !getResult().isSuccess()) {
                            TipsHelper.showToast(getActivity(), subscribe ?
                                    R.string.subscribe_failed : R.string.subscribe_cancel_failed);
                            return;
                        } else {
                            saveLatestSchool(getUserInfo().getMemberId(), schoolInfo.getSchoolId());
                            TipsHelper.showToast(getActivity(), subscribe ?
                                    R.string.subscribe_success : R.string.subscribe_cancel_success);
                            loadAttentionSchoolInfo();
                        }
                    }
                };
        listener.setTarget(subscribe);
        listener.setShowLoading(true);
        String serverUrl = subscribe ? ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL :
                ServerUrl.SUBSCRIBE_REMOVE_SCHOOL_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl,
                params, listener);
    }


    //关注学校
    private void subscribeSchool(boolean subscribe) {
        if (schoolInfo == null) {
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolInfo.getSchoolId());
        DefaultListener<ModelResult> listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        boolean subscribe = ((Boolean) getTarget()).booleanValue();
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            schoolInfo.setSubscribed(subscribe);
                            if (subscribe) {
                                TipsHelper.showToast(getActivity(),
                                        R.string.subscribe_success);
                                rightHeadTextView.setText(R.string.cancel_follow);
                            } else {
                                TipsHelper.showToast(getActivity(), R.string.subscribe_cancel_success);
                                rightHeadTextView.setText(R.string.wawatong_attention);
                            }
                        }
                    }
                };
        listener.setTarget(subscribe);
        listener.setShowLoading(true);
        String serverUrl = subscribe ? ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL :
                ServerUrl.SUBSCRIBE_REMOVE_SCHOOL_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl,
                params, listener);
    }

    private void jump2BookStoreListActivity() {
        if (schoolInfo == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), BookStoreListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("schoolInfo", schoolInfo);
        args.putSerializable("fromType", fromType);
        intent.putExtras(args);
        startActivity(intent);
    }

    ;


    private void initViews() {
        toggleSchoolLeftTextview = (TextView) findViewById(R.id.toggle_school_left_textview);
        toggleSchoolLeftTextview.setVisibility(View.GONE);
        toggleSchoolLeftTextview.setOnClickListener(this);
        headTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        headTitleTextView.setVisibility(View.INVISIBLE);
        headerLeftbtn = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        headerLeftbtn.setVisibility(View.GONE);
        rightHeadTextView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        rightHeadTextView.setVisibility(View.INVISIBLE);
        LinearLayout layout = (LinearLayout) findViewById(bookstore_more_layout);
        layout.setOnClickListener(this);
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.book_store_main_item) {
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
                    ImageView imageView = (ImageView) view.findViewById(R.id.item_shelf);
                    if (imageView != null) {
                        if (position % MAX_BOOKS_PER_ROW == 0) {
                            imageView.setBackgroundResource(R.drawable.book_shelf_l);
                        } else if (position % MAX_BOOKS_PER_ROW == 1) {
                            imageView.setBackgroundResource(R.drawable.book_shelf_m);
                        } else if (position % MAX_BOOKS_PER_ROW == 2) {
                            imageView.setBackgroundResource(R.drawable.book_shelf_r);
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        if (data != emptyBook) {
                            getThumbnailManager().displayThumbnailWithDefault(
                                    AppSettings.getFileUrl(data.getCoverUrl()), imageView,
                                    R.drawable.default_book_cover);
                        } else {
                            imageView.setImageBitmap(null);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getBookName());
                    }
                    ImageView imageView_desc = (ImageView) view.findViewById(R.id.item_description);
                    if (imageView_desc != null) {
                        imageView_desc.setVisibility(View.GONE);
//                        if (data != emptyBook) {
//                            imageView_desc.setVisibility(View.VISIBLE);
//                            if (data.getStatus() == 1) {
//                                imageView_desc.setImageResource(R.drawable.ywc_ico);
//                            } else {
//                                imageView_desc.setImageResource(R.drawable.jsz_ico);
//                            }
//                        } else {
//                            imageView_desc.setVisibility(View.GONE);
//                        }
                    }

                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.item_book_cover);
                    if (frameLayout != null) {
                        if (data != emptyBook) {
                            frameLayout.setBackgroundColor(Color.WHITE);
                        } else {
                            frameLayout.setBackgroundColor(Color.TRANSPARENT);
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
                    BookStoreBook data = (BookStoreBook) holder.data;
                    if (TextUtils.isEmpty(data.getId())) {
                        return;
                    }
                    jumpBook = null;
                    jumpBook = data;
                    jump2BookDetailByRoleActivity();
                }
            };
            setCurrAdapterViewHelper(gridView, gridViewHelper);
        }
    }


    private void updateBookListView(BookStoreBookListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<BookStoreBook> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    if (fromType == ATTENTION_PAGE) {
                        TipsHelper.showToast(getActivity(),
                                getString(R.string.no_data));
                    }
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                bookHelper.saveData(getCurrAdapterViewHelper().getData(), schoolInfo, getMemeberId());
                return;
            }
            if (list.size() > 12) {
                list = list.subList(0, 12);
            }
            if (list != null && list.size() > 0) {
                Iterator<BookStoreBook> iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next() == emptyBook) {
                        iterator.remove();
                    }
                }
                while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                    list.add(emptyBook);
                }
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int i = 0;
                while (getCurrAdapterViewHelper().getData().size() > 0) {
                    i = getCurrAdapterViewHelper().getData().size() - 1;
                    if (getCurrAdapterViewHelper().getData().get(i) == emptyBook) {
                        getCurrAdapterViewHelper().getData().remove(i);
                    } else {
                        break;
                    }
                }
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                while (getCurrAdapterViewHelper().getData().size() % MAX_BOOKS_PER_ROW != 0) {
                    getCurrAdapterViewHelper().getData().add(this.emptyBook);
                }
                getCurrAdapterView().setSelection(position);
            } else {
                while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                    list.add(this.emptyBook);
                }
                getCurrAdapterViewHelper().setData(list);
            }
            bookHelper.saveData(getCurrAdapterViewHelper().getData(), schoolInfo, getMemeberId());
        }
    }
}
