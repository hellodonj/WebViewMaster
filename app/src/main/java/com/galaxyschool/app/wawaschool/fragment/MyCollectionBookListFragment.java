package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BookCatalogListActivity;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CollectionBook;
import com.galaxyschool.app.wawaschool.pojo.CollectionBookListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyCollectionBookListFragment extends ContactsListFragment {

    public static final String TAG = MyCollectionBookListFragment.class.getSimpleName();

    private static final int MAX_BOOKS_PER_ROW = 3;

    private TextView keywordView;
    private String keyword = "";
    private Map<String, CollectionBook> selectedBooks = new HashMap();
    private CollectionBook emptyBook = new CollectionBook();
    private CollectionBook currBook;
    private boolean hasSelectedAll;
    private boolean isTeacher = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_collection_book_list, null);
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

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            String title = "";
            if (getArguments() != null) {
                title = getArguments().getString(MediaListFragment.EXTRA_MEDIA_NAME);
            }
            textView.setText(title);
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popStack();
                }
            });
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setText(R.string.my_courseware);
            textView.setTextColor(getResources().getColor(R.color.text_green));
            textView.setVisibility(View.INVISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ShellActivity.class);
                    intent.putExtra("Window", "media_list");
                    intent.putExtra(MediaListFragment.EXTRA_MEDIA_TYPE, MediaType.MICROCOURSE);
                    intent.putExtra(MediaListFragment.EXTRA_MEDIA_NAME, getString(R.string.my_courseware));
                    intent.putExtra(MediaListFragment.EXTRA_IS_PICK, false);
                    intent.putExtra(MediaListFragment.EXTRA_IS_REMOTE, true);
                    intent.putExtra(MediaListFragment.EXTRA_IS_FINISH, true);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadBookList();
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    loadBookList();
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
                    loadBookList();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.collection_book_grid_item) {
                @Override
                public void loadData() {
                    loadBookList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    CollectionBook data = (CollectionBook) getDataAdapter().getItem(position);
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
                            imageView.setVisibility(View.VISIBLE);
                        } else {
                            imageView.setImageBitmap(null);
                            imageView.setVisibility(View.GONE);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getBookName());
                    }
                    textView = (TextView) view.findViewById(R.id.item_description);
                    if (textView != null) {
                        if (data != emptyBook) {
                            textView.setText(!TextUtils.isEmpty(data.getSchoolId()) ?
                                    data.getSchoolName() : getString(R.string.app_name));
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.item_selector);
                    if (imageView != null) {
                        if (data != emptyBook && isSelecting()) {
                            imageView.setSelected(isBookSelected(data));
                            imageView.setVisibility(View.VISIBLE);
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                    }

                    ImageView ivCourseType = (ImageView) view.findViewById(R.id.item_course_flag);
                    if (data.getCourseType() == 2) {
                        ivCourseType.setVisibility(View.VISIBLE);
                    } else {
                        ivCourseType.setVisibility(View.INVISIBLE);
                    }

                    //                    FrameLayout frameLayout = (FrameLayout)view.findViewById(R.id.item_book_cover);
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
                    CollectionBook data = (CollectionBook) holder.data;
                    if (TextUtils.isEmpty(data.getId())) {
                        return;
                    }
                    if (!isSelecting()) {
                        if (!TextUtils.isEmpty(data.getCollectionOrigin())) {
                            if (data.getCourseType() == 2) {
                                //师训课程只有该机构的老师才能查看
                                if (VipConfig.isVip(getActivity())){
                                    enterBookDetails(data);
                                    return;
                                }
                                List<SchoolInfo> schoolInfoList = DemoApplication.getInstance().getPrefsManager()
                                        .getJoinSchoolList(getMemeberId());
                                if (schoolInfoList != null && schoolInfoList.size() != 0) {
                                    for (SchoolInfo info : schoolInfoList) {
                                        if (info.getSchoolId().equals(data.getCollectionOrigin()) && info.isTeacher()) {
                                            isTeacher = true;
                                            enterBookDetails(data);
                                            return;
                                        }
                                    }
                                    ToastUtil.showToast(getActivity(), R.string.only_teacher_can_see_the_course);
                                    return;
                                }
                            }else {
                                enterBookDetails(data);
                                return;
                            }
                        } else {
                            isTeacher = true;
                            enterBookDetails(data);
                            return;
                        }
                    }
                    boolean selected = isBookSelected(data);
                    selectBook(data, !selected);
                    hasSelectedAll = isAllBooksSelected();
                    notifySelectorViews();
                    //                    ImageView imageView = (ImageView) view.findViewById(R.id.item_selector);
                    //                    if (imageView != null) {
                    //                        imageView.setSelected(!selected);
                    //                    }
                    getCurrAdapterViewHelper().update();
                }
            };

            setCurrAdapterViewHelper(gridView, gridViewHelper);
        }

        initSelectorViews();
    }

    private void initSelectorViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_action_start);
        if (textView != null) {
            textView.setText(R.string.delete);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!getCurrAdapterViewHelper().hasData()) {
                        return;
                    }
                    v.setVisibility(View.GONE);
                    //                    v = findViewById(R.id.contacts_select_all_layout);
                    //                    if (v != null) {
                    //                        v.setVisibility(View.VISIBLE);
                    //                    }
                    v = findViewById(R.id.contacts_action_tools_layout);
                    if (v != null) {
                        v.setVisibility(View.VISIBLE);
                    }
                    //                    v = findViewById(R.id.contacts_select_all_icon);
                    //                    if (v != null) {
                    //                        v.setSelected(false);
                    //                    }
                    //展开选项
                    //全选/取消全选
                    selectAllBooks(false);
                    notifySelectorViews();
                    getCurrAdapterViewHelper().update();
                }
            });
        }

        //全选
        textView = (TextView) findViewById(R.id.contacts_action_select_all);
        if (textView != null) {
            textView.setText(R.string.select_all);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //全选/取消全选
                    hasSelectedAll = !hasSelectedAll;
                    selectAllBooks(hasSelectedAll);
                    notifySelectorViews();
                    getCurrAdapterViewHelper().update();
                }
            });
        }

        textView = (TextView) findViewById(R.id.contacts_action_left);
        if (textView != null) {
            textView.setText(R.string.cancel);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedBooks.clear();
                    //                    v = findViewById(R.id.contacts_select_all_layout);
                    //                    if (v != null) {
                    //                        v.setVisibility(View.GONE);
                    //                    }
                    v = findViewById(R.id.contacts_action_tools_layout);
                    if (v != null) {
                        v.setVisibility(View.GONE);
                    }
                    v = findViewById(R.id.contacts_action_start);
                    if (v != null) {
                        v.setVisibility(View.VISIBLE);
                    }
                    getCurrAdapterViewHelper().update();
                    //                    ImageView imageView = (ImageView) findViewById(R.id.contacts_select_all_icon);
                    //                    if (imageView != null) {
                    //                        imageView.setSelected(isAllBooksSelected());
                    //                    }
                }
            });
        }

        textView = (TextView) findViewById(R.id.contacts_action_right);
        if (textView != null) {
            textView.setText(R.string.confirm);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedBooks.size() <= 0) {
                        return;
                    }
                    List<CollectionBook> list = new ArrayList();
                    for (Map.Entry<String, CollectionBook> entry : selectedBooks.entrySet()) {
                        list.add(entry.getValue());
                    }
                    deleteBooks(list);
                }
            });
        }

        View view = findViewById(R.id.contacts_action_tools_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        //        view = findViewById(R.id.contacts_select_all_layout);
        //        if (view != null) {
        //            view.setBackgroundResource(android.R.color.white);
        //            view.setVisibility(View.GONE);
        //
        //            view = view.findViewById(R.id.contacts_select_all);
        //            view.setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    ImageView imageView = (ImageView) v.findViewById(R.id.contacts_select_all_icon);
        //                    if (imageView != null) {
        //                        imageView.setSelected(!imageView.isSelected());
        //                        selectAllBooks(imageView.isSelected());
        //                        notifySelectorViews();
        //                        getCurrAdapterViewHelper().update();
        //                    }
        //                }
        //            });
        //        }

        view = findViewById(R.id.contacts_action_bar_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void notifySelectorViews() {
        List<CollectionBook> list = getCurrAdapterViewHelper().getData();
        int size = 0;
        if (list != null && list.size() > 0) {
            for (CollectionBook data : list) {
                if (data != emptyBook) {
                    size++;
                }
            }
        }
        View view = findViewById(R.id.contacts_action_bar_layout);
        if (view != null) {
            view.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_action_right);
        if (textView != null) {
            textView.setEnabled(hasSelectedBooks());
        }

        //        ImageView imageView = (ImageView) findViewById(R.id.contacts_select_all_icon);
        //        if (imageView != null) {
        //            imageView.setSelected(isAllBooksSelected());
        //        }

        //更新全选/取消全选文字
        textView = (TextView) findViewById(R.id.contacts_action_select_all);

        if (textView != null) {
            if (isAllBooksSelected()) {
                textView.setText(getString(R.string.cancel_to_select_all));
            } else {
                textView.setText(getString(R.string.select_all));
            }
        }
    }

    private boolean isSelecting() {
        View v = findViewById(R.id.contacts_action_tools_layout);
        if (v != null) {
            return v.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    private void showSelectorViews(boolean show) {
        //        View v = findViewById(R.id.contacts_select_all_layout);
        //        if (v != null) {
        //            v.setVisibility(show ? View.VISIBLE : View.GONE);
        //        }
        View v = findViewById(R.id.contacts_action_tools_layout);
        if (v != null) {
            v.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        v = findViewById(R.id.contacts_action_start);
        if (v != null) {
            v.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isBookSelected(CollectionBook data) {
        return data != null && selectedBooks.containsKey(data.getId());
    }

    private void selectBook(CollectionBook data, boolean selected) {
        if (TextUtils.isEmpty(data.getId())) {
            return;
        }
        if (selected) {
            selectedBooks.put(data.getId(), data);
        } else {
            selectedBooks.remove(data.getId());
        }
    }

    private void selectAllBooks(boolean selected) {
        if (selected) {
            if (getCurrAdapterViewHelper().hasData()) {
                CollectionBook data = null;
                for (Object obj : getCurrAdapterViewHelper().getData()) {
                    data = (CollectionBook) obj;
                    if (TextUtils.isEmpty(data.getId())) {
                        continue;
                    }
                    selectedBooks.put(data.getId(), data);
                }
            }
        } else {
            selectedBooks.clear();
        }
    }

    private boolean hasSelectedBooks() {
        return selectedBooks.size() > 0;
    }

    private boolean isAllBooksSelected() {
        List<CollectionBook> list = getCurrAdapterViewHelper().getData();
        int size = 0;
        if (list != null && list.size() > 0) {
            for (CollectionBook data : list) {
                if (data != emptyBook) {
                    size++;
                }
            }
        }
        return size > 0 && size == selectedBooks.size();
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadBookList();
        }
    }

    private void loadBookList() {
        loadBookList(this.keywordView.getText().toString());
    }

    private void loadBookList(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            selectedBooks.clear();
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("KeyWord", keyword);
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<CollectionBookListResult>(
                        CollectionBookListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        CollectionBookListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_MY_COLLECTION_BOOK_LIST_URL, params, listener);
    }

    private void updateBookListView(CollectionBookListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<CollectionBook> list = result.getModel().getData();
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

                //fix bug can not delete books when update data in delete mode
                if (list != null && list.size() > 0) {
                    for (CollectionBook book : list) {
                        if (book != null && !TextUtils.isEmpty(book.getId()) && selectedBooks.containsKey(book.getId())) {
                            selectedBooks.put(book.getId(), book);
                        }
                    }
                }
            }
            notifySelectorViews();
        }
    }

    private void enterBookDetails(CollectionBook book) {
        if (book.isQualityCourse()) {
            UIUtils.currentSourceFromType = SourceFromType.CHOICE_LIBRARY;
        } else {
            UIUtils.currentSourceFromType = SourceFromType.PUBLIC_LIBRARY;
        }
        currBook = book;
        Bundle args = new Bundle();
        args.putString(BookDetailActivity.BOOK_ID, book.getId());
        args.putString(BookDetailActivity.BOOK_OUTLINE_ID, book.getOutlineId());
        args.putString(BookDetailActivity.COLLECT_ORIGIN_SCHOOLID, book.getCollectionOrigin());
        args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB, book.isQualityCourse());
        //args.putString(BookDetailActivity.SCHOOL_ID, book.getSchoolId());
        args.putInt(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_SHELF);
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        args.putBoolean(BookDetailFragment.IS_TEACHER, isTeacher);
        intent.putExtras(args);
        startActivityForResult(intent, BookDetailActivity.REQUEST_CODE_DELETE_BOOK);
    }

    private void enterBookCatalog(CollectionBook book) {
        Bundle args = new Bundle();
        args.putInt(BookCatalogListActivity.EXTRA_MODE, BookCatalogListActivity.REVIEW_MODE);
        args.putInt(BookCatalogListActivity.EXTRA_BOOK_SOURCE, BookCatalogListActivity.PERSONAL_BOOK);
        args.putString(BookCatalogListActivity.EXTRA_BOOK_PRIMARY_KEY, book.getId());
        args.putString(BookCatalogListActivity.EXTRA_BOOK_ID, book.getOutlineId());
        args.putString(BookCatalogListActivity.EXTRA_BOOK_NAME, book.getBookName());
        args.putString(BookCatalogListActivity.EXTRA_BOOK_COVER, book.getCoverUrl());
        //args.putString(BookCatalogListActivity.EXTRA_SCHOOL_ID, book.getSchoolId());
        args.putString(BookCatalogListActivity.EXTRA_SCHOOL_NAME, book.getSchoolName());
        Intent intent = new Intent(getActivity(), BookCatalogListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void showDeleteBookDialog(final CollectionBook data, String title) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(), null,
                title,
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
                        List<CollectionBook> list = new ArrayList<>();
                        list.add(data);
                        deleteBooks(list);
                    }
                });
        messageDialog.show();
    }

    private void deleteBooks(List<CollectionBook> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        Map<String, Object> params = new HashMap();
        StringBuilder builder = new StringBuilder();
        Iterator<CollectionBook> iterator = list.iterator();
        CollectionBook data = null;
        while (iterator.hasNext()) {
            data = iterator.next();
            if (TextUtils.isEmpty(data.getId())) {
                continue;
            }
            builder.append(data.getId());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        params.put("BookId", builder.toString());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        getActivity(), DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            TipsHelper.showToast(getActivity(), R.string.delete_failure);
                            return;
                        } else {
                            showSelectorViews(false);
                            TipsHelper.showToast(getActivity(), R.string.delete_success);
                            List<CollectionBook> list = (List<CollectionBook>) getTarget();
                            for (CollectionBook data : list) {
                                getCurrAdapterViewHelper().getData().remove(data);
                                selectBook(data, false);
                            }

                            list = getCurrAdapterViewHelper().getData();
                            Iterator<CollectionBook> iterator = list.iterator();
                            while (iterator.hasNext()) {
                                if (iterator.next() == emptyBook) {
                                    iterator.remove();
                                }
                            }
                            while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                                list.add(emptyBook);
                            }

                            notifySelectorViews();
                            getCurrAdapterViewHelper().update();
                        }
                    }
                };
        listener.setTarget(list);
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.DELETE_COLLECTION_BOOK_URL, params, listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BookDetailActivity.REQUEST_CODE_DELETE_BOOK) {
                if (data.getExtras().containsKey(BookDetailActivity.BOOK_ID)) {
                    String bookId = data.getExtras().getString(BookDetailActivity.BOOK_ID);
                    if (currBook != null && currBook.getId().equals(bookId)) {
                        List<CollectionBook> list = getCurrAdapterViewHelper().getData();
                        list.remove(currBook);
                        Iterator<CollectionBook> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next() == emptyBook) {
                                iterator.remove();
                            }
                        }
                        while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                            list.add(emptyBook);
                        }
                        getCurrAdapterViewHelper().update();
                    }
                }
            }
        }
    }

}
