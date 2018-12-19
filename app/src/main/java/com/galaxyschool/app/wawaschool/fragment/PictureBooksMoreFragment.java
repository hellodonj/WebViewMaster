package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorView;
import com.galaxyschool.app.wawaschool.fragment.category.CategoryValue;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.PicBookCategory;
import com.galaxyschool.app.wawaschool.pojo.PicBookCategoryListResult;
import com.galaxyschool.app.wawaschool.pojo.PicBookCategoryType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KnIghT on 16-5-10.
 */
public class PictureBooksMoreFragment extends ContactsListFragment implements CategorySelectorView.OnCategorySelectListener {
    public static final String TAG = PictureBooksMoreFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 4;
    private TextView keywordView;
    private String keyword = "";
    private View filterLayout;
    private CategorySelectorView categoryView;
    private List<Category> allCategories;
    private List<Category> selectedCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_books_more, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPicBooks();
    }

    private void loadPicBooks(){
        Map<String, Object> params = new HashMap();
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        params.put("KeyWord", keyword);
        if(this.selectedCategories!=null&&this.selectedCategories.size()>0){
            for( Category category :this.selectedCategories){
                if(category.getType()==1){//1：年龄段2：语言 3：标签
                    params.put("AgeGroupId", category.getCurrValue().getId());
                }
                if(category.getType()==2){
                    params.put("LanguageId", category.getCurrValue().getId());
                }
                if(category.getType()==3){
                    params.put("TagsId", category.getCurrValue().getId());
                }
            }
        }
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updatePicBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.PICBOOKS_GET_DEFAULT_PICBOOKLIST_URL, params, listener);
    }
    private void resetPage() {
        getCurrAdapterViewHelper().clearData();
        getPageHelper().clear();
    }
    private void updatePicBookListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()){
                resetPage();
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
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

    private void loadCategoryList() {
        RequestHelper.RequestDataResultListener listener
                = new RequestHelper.RequestDataResultListener<PicBookCategoryListResult>(getActivity(), PicBookCategoryListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                PicBookCategoryListResult result = getResult();
                if (result == null || result.getModel() == null || !result.isSuccess()) {
                    return;
                }
                List<PicBookCategory> picBookCategories = result.getModel().getData();
                if (picBookCategories != null && picBookCategories.size() > 0) {
                    allCategories = parseConfigs(picBookCategories);
                    categoryView.setAllCategories(allCategories);
                }

            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOAD_PICBOOK_CATEGORY_URL, null, listener);

    }
    private List<Category> parseConfigs(List<PicBookCategory> configs) {
        if (configs != null && configs.size() > 0) {
            Category cat;
            CategoryValue value;
            List<Category> list = new ArrayList();
            for (int i = 0; i < configs.size(); i++) {
                PicBookCategory cfg = configs.get(i);
                cat = new Category();
                cat.setType(cfg.getType());
                cat.setName(cfg.getTypeName());
                cat.setAllValues(new ArrayList());
                if (cfg.getDetailList() != null && cfg.getDetailList().size() > 0) {
                    for (PicBookCategoryType item : cfg.getDetailList()) {
                        value = new CategoryValue();
                        value.setId(item.getId());
                        value.setValue(item.getName());
                        cat.getAllValues().add(value);
                    }
                }
                list.add(cat);
            }
            return list;
        }
        return null;
    }



    private void initViews() {
        TextView headTitletextView = (TextView) findViewById(R.id.contacts_header_title);
        headTitletextView.setVisibility(View.VISIBLE);
        headTitletextView.setText(R.string.read_room);
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);

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
                        loadPicBooks();
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
                    loadPicBooks();
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
                    loadPicBooks();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }


        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadPicBooks();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        enterPictureDetailActivity(data);
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
        view = findViewById(R.id.category_filter_layout);
        if (view != null) {
            view.setOnClickListener(this);
        }
        this.filterLayout = view;

        this.categoryView = (CategorySelectorView) findViewById(R.id.category_selector_view);
        if (this.categoryView != null) {
            this.categoryView.setOnCategorySelectListener(this);
        }
    }

    private void enterPictureDetailActivity(NewResourceInfo item) {
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        startActivity(intent);
    }

    private void selectFilterIndicatorView(boolean selected) {
        ImageView imageView = (ImageView) findViewById(R.id.category_filter_indicator);
        if (imageView != null) {
            imageView.setImageResource(selected ?
                    R.drawable.arrow_up_ico : R.drawable.arrow_down_ico);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.category_filter_layout) {
            v.setSelected(!v.isSelected());
            selectFilterIndicatorView(v.isSelected());
            if (allCategories == null) {
                loadCategoryList();
            } else {
                this.categoryView.setAllCategories(this.allCategories);
                this.categoryView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        this.selectedCategories = categories;
        showCategoryView(false);
        getPageHelper().clear();
        getCurrAdapterViewHelper().clearData();
        loadPicBooks();
    }
    private void showCategoryView(boolean show) {
        filterLayout.setSelected(show);
        selectFilterIndicatorView(show);
        categoryView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
