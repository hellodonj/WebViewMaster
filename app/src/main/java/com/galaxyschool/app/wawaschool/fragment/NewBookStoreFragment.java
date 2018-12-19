package com.galaxyschool.app.wawaschool.fragment;

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
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.BookStoreListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.BookStoreBookDao;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.pojo.BookStoreBookListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.galaxyschool.app.wawaschool.R.id.bookstore_more_layout;

/**
 * Created by Administrator on 2016/7/13.
 */
public class NewBookStoreFragment extends ContactsListFragment {
    public static final String TAG = BookStoreFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 3;
    public static final int HOME_PAGE = 0;
    public static final int ATTENTION_PAGE = 1;
    private MyBroadCastReceiver receiver;
    private BookStoreBook emptyBook = new BookStoreBook();
    private SchoolInfo schoolInfo = null;
    private String schoolId = "";
    private String originSchoolId;
    private TextView attentionBtn;
    private int fromType = HOME_PAGE;//From_Type==1 来自关注页
    private boolean isPicBookChoice=false;//是否是精品绘本
    public interface Constants {
        public static final String SCHOOL_ID = "schoolId";
        public static final String ORIGIN_SCHOOL_ID = "originSchoolId";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_bookstore, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntentData();
        initViews();
        initBroadCastReceiver();
        loadSchoolInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void loadSchoolInfo() {
        Map<String, Object> params = new HashMap();
        if (isLogin()) {
            params.put("MemberId", getUserInfo().getMemberId());
        }
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
                        updateSchoolInfoViews();
                    }
                });
    }

    private void updateSchoolInfoViews() {
        initSchoolInfoViews();
        loadBookData();
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


    //2：如果加载关注列表（第一个关注学校或者记忆的最近一次点击的学校）
    private void loadBookData() {
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

    private void updateBookListView(BookStoreBookListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<BookStoreBook> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()) {
                if (getCurrAdapterViewHelper().hasData()) {
                    getCurrAdapterViewHelper().clearData();
                }
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    if (getCurrAdapterViewHelper().hasData()) {
                        getCurrAdapterViewHelper().clearData();
                    }
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
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
        }
    }

    private void getIntentData() {
        Bundle args = getArguments();
        schoolId = args.getString(Constants.SCHOOL_ID);
        originSchoolId = args.getString(Constants.ORIGIN_SCHOOL_ID);
        isPicBookChoice=getArguments().getBoolean(ActivityUtils.IS_PIC_BOOK_CHOICE);
    }

    protected void initSchoolInfoViews() {
        ImageView schoolIconView = (ImageView) findViewById(R.id.user_icon);
        TextView schoolNameView = (TextView) findViewById(R.id.user_name);
        TextView schoolFollowersCountView = (TextView) findViewById(R.id.user_followers_count);
        schoolFollowersCountView.setVisibility(View.GONE);
        if (schoolInfo != null) {
            getThumbnailManager().displayUserIconWithDefault(
                    AppSettings.getFileUrl(schoolInfo.getSchoolLogo()),
                    schoolIconView, R.drawable.default_school_icon);
            schoolNameView.setText(schoolInfo.getSchoolName());
            schoolFollowersCountView.setText(getString(R.string.n_subscribed,
                    schoolInfo.getAttentionNumber()));
            if (schoolInfo.getState() == SchoolInfo.USER_STATE_STRANGE) {
                attentionBtn.setVisibility(View.INVISIBLE);
                attentionBtn.setText(R.string.follow);
                attentionBtn.setOnClickListener(this);
            } else if (schoolInfo.getState()==SchoolInfo.USER_STATE_SUBSCRIBED) {
                attentionBtn.setVisibility(View.INVISIBLE);
                attentionBtn.setText(R.string.cancel_follow);
                attentionBtn.setOnClickListener(this);
            }else if (schoolInfo.getState()==SchoolInfo.USER_STATE_JOINED) {
                attentionBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            schoolNameView.setText(null);
            schoolIconView.setImageBitmap(null);
            schoolFollowersCountView.setText(null);
        }
    }

    private void initViews() {
        FrameLayout  userIconLayout = (FrameLayout) findViewById(R.id.user_icon_layout);
        userIconLayout.setOnClickListener(this);
        ImageView imageView = (ImageView) findViewById(R.id.back_btn);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(this);
        attentionBtn = (TextView) findViewById(R.id.attention_btn);
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
                    jump2BookDetailByRoleActivity(data);
                }
            };
            setCurrAdapterViewHelper(gridView, gridViewHelper);
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

    private void jump2BookDetailByRoleActivity(BookStoreBook book) {
        //课程类型(1公开、2师训、3校内) 用户角色 （1老师 2学生 3家长 4 游客)
        if (book == null) {
            return;
        }
        if (!isLogin()) {
            enterAccountActivity();
        } else {
            enterBookByRole(book);
        }
    }

    private void jump2BookDetailActivity(BookStoreBook data) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.SCHOOL_ID, data.getSchoolId());
        intent.putExtra(BookDetailActivity.ORIGIN_SCHOOL_ID, originSchoolId);
        intent.putExtra(BookDetailActivity.BOOK_ID, data.getId());
        intent.putExtra(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_STORE);
        intent.putExtra("data", data);
        startActivity(intent);
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

    private void enterBookByRole(BookStoreBook book) {
        if (schoolInfo == null) {
            return;
        }
        jump2BookDetailActivity(book);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.attention_btn:
                if (isLogin()) {
                    if (schoolInfo == null) {
                        return;
                    }
                    if (schoolInfo.getState() == 0) {
                        subscribeSchool(true); //关注学校代码
                    } else if (schoolInfo.getState() == 1) {
                        subscribeSchool(false);//取消关注学校代码
                    }
                } else {
                    enterAccountActivity();
                }
                break;
            case R.id.back_btn:
                finish();
                break;
            case bookstore_more_layout:
                enterBookStoreListActivity();
                break;
            case R.id.user_icon_layout:
                if(isLogin()){
                    enterSchoolSpace(schoolInfo);
                }else{
                    enterAccountActivity();
                }
                break;
        }
    }
    private void enterSchoolSpace(SchoolInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }
    private void enterBookStoreListActivity() {
        if (schoolInfo == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), BookStoreListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("schoolInfo", schoolInfo);
        args.putSerializable("fromType", fromType);
        args.putString("originSchoolId", originSchoolId);
        args.putBoolean(ActivityUtils.IS_PIC_BOOK_CHOICE, isPicBookChoice);
        intent.putExtras(args);
        startActivity(intent);
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
                            if (subscribe) {
                                TipsHelper.showToast(getActivity(),
                                        R.string.subscribe_success);
                            } else {
                                TipsHelper.showToast(getActivity(), R.string.subscribe_cancel_success);
                            }
                            loadSchoolInfo();
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



}
