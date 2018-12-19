package com.galaxyschool.app.wawaschool.fragment;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.BookCatalogListActivity;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.BookDao;
import com.galaxyschool.app.wawaschool.db.BookStoreBookDao;
import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorView;
import com.galaxyschool.app.wawaschool.fragment.category.CategoryValue;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookListFragment extends ContactsListFragment
        implements CategorySelectorView.OnCategorySelectListener {

    public static final String TAG = BookListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_MODE = "mode";
        String EXTRA_BOOK_SOURCE = "bookSource";
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
        String EXTRA_TYPE_CODE = "typeCode";
        int UPLOAD_MODE = 1;
        int REVIEW_MODE = 2;
        int PLATFORM_BOOK = 1;
        int SCHOOL_BOOK = 2;
        int PERSONAL_BOOK = 3;
        int PLATFORM_OUTLINE = 4;
    }

    private static final int MAX_BOOKS_PER_ROW = 3;
    private LinearLayout generalLayout;
    private int mode;
    private int bookSource;
    private String schoolId;
    private String schoolName;
    private String typeCode;
    private boolean isPick;
    private boolean isImport;
    protected TextView keywordView;
    protected String keyword = "";
    private View filterLayout;
    private CategorySelectorView categoryView;
    private List<Category> allCategories;
    private List<Category> selectedCategories;
    private MyGridView generalGridView;
    private String generalGridViewTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void init() {
        this.mode = getArguments().getInt(Constants.EXTRA_MODE, Constants.REVIEW_MODE);
        this.bookSource = getArguments().getInt(Constants.EXTRA_BOOK_SOURCE);
        this.schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
        this.schoolName = getArguments().getString(Constants.EXTRA_SCHOOL_NAME);
        this.typeCode = getArguments().getString(Constants.EXTRA_TYPE_CODE);
        this.isPick = getArguments().getBoolean(MediaListFragment.EXTRA_IS_PICK);
        this.isImport = getArguments().getBoolean(MediaListFragment.EXTRA_IS_IMPORT);
        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.syllabus);
        }
        ImageView imageView = (ImageView)findViewById(R.id.contacts_header_left_btn);
        if(imageView != null) {
            imageView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if(textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.load_platform_outline);
            if(bookSource == Constants.SCHOOL_BOOK && mode == Constants.UPLOAD_MODE) {
                textView.setVisibility(View.VISIBLE);
            }
        }

        View headerView = findViewById(R.id.contacts_header_layout);
        if(headerView != null) {
            headerView.setVisibility(isImport ? View.GONE : View.VISIBLE);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        showCategoryView(false);
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
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
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
                    showCategoryView(false);
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
        generalLayout = (LinearLayout) findViewById(R.id.general_layout);
        view = findViewById(R.id.category_filter_layout);
        if (view != null) {
            view.setOnClickListener(this);
//            view.setVisibility(View.GONE);
        }
        this.filterLayout = view;
        this.categoryView = (CategorySelectorView) findViewById(R.id.category_selector_view);
        if (this.categoryView != null) {
            this.categoryView.setOnCategorySelectListener(this);
        }
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        initAllGridView();
        initGeneralGridView();
    }

    private void initAllGridView(){
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.book_grid_item) {
                @Override
                public void loadData() {
                    loadBookList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    Book data = (Book) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    //设置外层布局为A4比例
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    //之前宽 90 高 120
                    int width = DensityUtils.dp2px(getActivity(), 90);
                    layoutParams.width = width;
                    layoutParams.height = width * 297 / 210;
                    frameLayout.setLayoutParams(layoutParams);
                    ImageView imageView = (ImageView) view.findViewById(R.id.item_icon);
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
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    Book data = (Book) holder.data;
                    enterBookCatalog(data);
                }
            };

            setCurrAdapterViewHelper(gridView, gridViewHelper);
        }
    }

    private void initGeneralGridView() {
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
                    Book data = (Book) getDataAdapter().getItem(position);
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
                    Book data = (Book) holder.data;
                    enterBookCatalog(data);
                }
            };
            this.generalGridViewTag = String.valueOf(generalGridView.getId());
            addAdapterViewHelper(this.generalGridViewTag, gridViewHelper);
        }
    }

    private void loadBookGeneralList() {
        BookDao dao = null;
        try {
            dao = BookDao.getInstance(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            if (TextUtils.isEmpty(schoolId)) return;
            if (mode == Constants.UPLOAD_MODE){
                List<Book> list = null;
                if (bookSource == Constants.SCHOOL_BOOK){
                    list = dao.getBookList(getMemeberId(), schoolId.toLowerCase(),
                           Book.FromType.FROM_PLATFORM);
                } else if (bookSource == Constants.PLATFORM_OUTLINE){
                    list = dao.getBookList(getMemeberId(), schoolId.toLowerCase(),
                           Book.FromType.FROM_QUOTE_PLATFORM);
                }
                if (list != null && list.size() > 0) {
                    generalLayout.setVisibility(View.VISIBLE);
                    getAdapterViewHelper(generalGridViewTag).setData(list);
                } else {
                    generalLayout.setVisibility(View.GONE);
                }
            }
        }
    }


    private void selectFilterIndicatorView(boolean selected) {
        ImageView imageView = (ImageView) findViewById(R.id.category_filter_indicator);
        if (imageView != null) {
            imageView.setImageResource(selected ?
                    R.drawable.arrow_up_ico : R.drawable.arrow_down_ico);
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadBookList();
        }
        if (allCategories == null) {
            loadCategoryList();
        }
        loadBookGeneralList();
    }

    private void loadCategoryList() {
        String serverUrl = null;
        Map<String, Object> params = new HashMap();
        if(this.bookSource == Constants.PLATFORM_BOOK) {
            params.put("Type", mode);
            if (this.mode == Constants.REVIEW_MODE) {
                params.put("SchoolId", this.schoolId);
                params.put("ResourceCategory", this.bookSource);
                params.put("TypeCode", this.typeCode);
            }
            serverUrl = ServerUrl.LOAD_OUTLINE_ATTR_LIST_URL;
        } else if(this.bookSource == Constants.SCHOOL_BOOK){
            params.put("SchoolId", this.schoolId);
            serverUrl = ServerUrl.LOAD_SCHOOL_OUTLINE_ATTR_LIST_URL;
        } else if(this.bookSource == Constants.PLATFORM_OUTLINE){
            serverUrl = ServerUrl.LOAD_PLATFORM_OUTLINE_ATTR_LIST_URL;
        }

        if(TextUtils.isEmpty(serverUrl)) {
            return;
        }
//        Map<String, Object> params = new HashMap();
//        params.put("Type", this.mode);
//        if (this.mode == Constants.REVIEW_MODE) {
//            params.put("ResourceCategory", this.bookSource);
//            params.put("SchoolId", this.schoolId);
//            params.put("TypeCode", this.typeCode);
//        }
        DefaultDataListener listener =
                new DefaultDataListener<BookCategoryListResult>(
                        BookCategoryListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        BookCategoryListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateCategoryListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
    }


    private void updateCategoryListView(BookCategoryListResult result) {
        List<BookCategory> list = result.getModel().getData();
        if (list == null || list.size() <= 0) {
            return;
        }

        List<Category> categoryList = new ArrayList();
        Category cat = null;
        CategoryValue value = null;
        for (BookCategory type : list) {
            cat = new Category();
            cat.setType(type.getType());
            cat.setName(type.getTypeName());
            if (type.getDetailList() != null && type.getDetailList().size() > 0) {
                cat.setAllValues(new ArrayList());
                for (TypeName obj : type.getDetailList()) {
                    value = new CategoryValue();
                    value.setId(obj.getId());
                    value.setValue(obj.getName());
                    cat.getAllValues().add(value);
                }
            }
            categoryList.add(cat);
        }
        allCategories = categoryList;

        categoryView.setAllCategories(categoryList);
        categoryView.setVisibility(filterLayout.isSelected() ? View.VISIBLE : View.GONE);
        selectFilterIndicatorView(filterLayout.isSelected());
//        filterLayout.setVisibility(View.VISIBLE);
    }

    private void loadBookList() {
        selectedCategories = categoryView.getSelectedCategories();
        loadBookList(keywordView.getText().toString(), selectedCategories);
    }

    private void loadBookList(String keyword, List<Category> categories) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("KeyWord", keyword);
        String serverUrl = null;
        if(this.bookSource == Constants.PLATFORM_BOOK) {
            params.put("Type", mode);
            if (this.mode == Constants.REVIEW_MODE) {
                params.put("SchoolId", this.schoolId);
                params.put("ResourceCategory", this.bookSource);
                params.put("TypeCode", this.typeCode);
            }
            serverUrl = ServerUrl.LOAD_OUTLINE_LIST_URL;
        } else if(this.bookSource == Constants.SCHOOL_BOOK){
            params.put("SchoolId", this.schoolId);
            serverUrl = ServerUrl.LOAD_SCHOOL_OUTLINE_LIST_URL;
        } else if(this.bookSource == Constants.PLATFORM_OUTLINE){
            serverUrl = ServerUrl.LOAD_PLATFORM_OUTLINE_LIST_URL;
        }

        if(TextUtils.isEmpty(serverUrl)) {
            return;
        }

        if (categories != null && categories.size() > 0) {
            for (Category cat : categories) {
                switch (cat.getType()) {
                case BookCategory.VERSION_TYPE:
                    params.put("VerstionId", cat.getCurrValue().getId());
                    break;
                case BookCategory.STAGE_TYPE:
                    params.put("LevelId", cat.getCurrValue().getId());
                    break;
                case BookCategory.GRADE_TYPE:
                    params.put("GradeId", cat.getCurrValue().getId());
                    break;
                case BookCategory.SUBJECT_TYPE:
                    params.put("SubjectId", cat.getCurrValue().getId());
                    break;
                case BookCategory.VOLUME_TYPE:
                    params.put("VolumeId", cat.getCurrValue().getId());
                    break;
                case BookCategory.LANGUAGE_TYPE:
                    params.put("LanguageId", cat.getCurrValue().getId());
                    break;
                case BookCategory.PUBLISHER_TYPE:
                    params.put("PublisherId", cat.getCurrValue().getId());
                    break;
                }
            }
        }
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<BookListResult>(BookListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                BookListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateBookListView(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                serverUrl, params, listener);
    }

    private void updateBookListView(BookListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<Book> list = result.getModel().getData();
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
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        this.selectedCategories = categories;
        showCategoryView(false);
        getPageHelper().clear();
        getCurrAdapterViewHelper().clearData();

        loadBookList();
    }

    private void showCategoryView(boolean show) {
        filterLayout.setSelected(show);
        selectFilterIndicatorView(show);
        categoryView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.category_filter_layout) {
            v.setSelected(!v.isSelected());
            selectFilterIndicatorView(v.isSelected());

            if (allCategories == null) {
                loadCategoryList();
            } else {
                this.categoryView.setAllCategories(this.allCategories);
                this.categoryView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
            }
        }else if(v.getId() == R.id.contacts_header_right_btn) {
            enterSyllabusSelection();
        }
        else {
            super.onClick(v);
        }
    }

    private void addBook2DataBase(Book book) {
        if (TextUtils.isEmpty(schoolId)){
            return;
        }
        BookDao dao = null;
        try {
            dao = BookDao.getInstance(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.addBook(book, getMemeberId(), schoolId);
        }
    }

    private void enterBookCatalog(Book book) {
        if (mode == Constants.UPLOAD_MODE){
            //增加数据到本地常用、发送到校本资源库
            if (bookSource == Constants.PLATFORM_OUTLINE){
                book.setFromType(Book.FromType.FROM_QUOTE_PLATFORM);
                addBook2DataBase(book);
            } else if (bookSource == Constants.SCHOOL_BOOK){
                book.setFromType(Book.FromType.FROM_PLATFORM);
                addBook2DataBase(book);
            }
        }
        Bundle args = getArguments();
        args.putInt(BookCatalogListActivity.EXTRA_MODE, this.mode);
        args.putInt(BookCatalogListActivity.EXTRA_BOOK_SOURCE, this.bookSource);
        args.putString(BookCatalogListActivity.EXTRA_BOOK_ID, book.getId());
        args.putString(BookCatalogListActivity.EXTRA_BOOK_NAME, book.getBookName());
        args.putString(BookCatalogListActivity.EXTRA_BOOK_COVER, book.getCoverUrl());
        args.putString(BookCatalogListActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(BookCatalogListActivity.EXTRA_SCHOOL_NAME, this.schoolName);
        args.putString(BookCatalogListActivity.EXTRA_TYPE_CODE, this.typeCode);
        if(bookSource != Constants.PLATFORM_BOOK) {
            Intent intent = new Intent(getActivity(), BookCatalogListActivity.class);
            intent.putExtras(args);
            UploadParameter uploadParameter= (UploadParameter) args.getSerializable(UploadParameter.class
                    .getSimpleName());
            if (uploadParameter != null){
                if (uploadParameter.isTempData() && uploadParameter.isLocal()){
                    getActivity().startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
                    return;
                }
            }
            startActivity(intent);
        } else {
            BookCatalogListFragment fragment = new BookCatalogListFragment();
            fragment.setArguments(args);
            android.support.v4.app.FragmentTransaction ft;
            if(!isImport) {
                ft = getFragmentManager().beginTransaction();
            } else {
                ft = getActivity().getSupportFragmentManager().beginTransaction();
            }
            ft.replace(R.id.container, fragment, BookCatalogListFragment.TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    void enterSyllabusSelection() {
        Bundle args = getArguments();
        args.putInt(BookListActivity.EXTRA_BOOK_SOURCE, BookListActivity.PLATFORM_OUTLINE);
        Intent intent = new Intent(getActivity(), BookListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

}
