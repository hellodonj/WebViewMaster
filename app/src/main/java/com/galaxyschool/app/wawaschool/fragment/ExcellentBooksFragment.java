package com.galaxyschool.app.wawaschool.fragment;

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
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.pojo.BookStoreBookListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/12.
 */
public class ExcellentBooksFragment extends ContactsListFragment{
    public static final String TAG = ExcellentBooksFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 3;
    private String keyword = "";
    private TextView keywordView;
    private BookStoreBook emptyBook = new BookStoreBook();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_excellent_books, null);
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

    private void loadViews() {
        getPageHelper().clear();
        loadBookData();
    }
    private void loadBookData() {
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("IsExcellentBook", true);
        params.put("BookName", keyword);
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
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    if(getCurrAdapterViewHelper().hasData()){
                        getCurrAdapterViewHelper().clearData();
                    }
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            if (getPageHelper().isFetchingFirstPage()) {
                if(getCurrAdapterViewHelper().hasData()){
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().clear();
                }
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
                getCurrAdapterViewHelper().getData().addAll(list);
                while (getCurrAdapterViewHelper().getData().size() % MAX_BOOKS_PER_ROW != 0) {
                    getCurrAdapterViewHelper().getData().add(this.emptyBook);
                }
                getCurrAdapterViewHelper().update();
            } else {
                while (list.size() % MAX_BOOKS_PER_ROW != 0) {
                    list.add(this.emptyBook);
                }
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    private void initViews() {
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.hint_class));
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
            view.setVisibility(View.VISIBLE);
        }
        TextView TextView = (TextView) findViewById(R.id.contacts_header_title);
        TextView.setText(R.string.excellent_course);
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);
        TextView textView1 = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView1 != null) {
            textView1.setVisibility(View.INVISIBLE);
        }
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        initGridView();
    }
    private void initGridView(){
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
                    enterBookDetailActivity(data);
                }
            };
            setCurrAdapterViewHelper(gridView, gridViewHelper);
        }
    }
    private void enterBookDetailActivity(BookStoreBook data) {
        if(data==null){
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.SCHOOL_ID, data.getSchoolId());
        intent.putExtra(BookDetailActivity.BOOK_ID, data.getId());
        intent.putExtra(BookDetailActivity.FROM_TYPE, BookDetailActivity.FROM_BOOK_STORE);
        intent.putExtra(BookDetailActivity.BOOK_SOURCE, BookDetailActivity.EXCELLENT_BOOK);
        intent.putExtra("data",data);
        startActivity(intent);
    }
}
