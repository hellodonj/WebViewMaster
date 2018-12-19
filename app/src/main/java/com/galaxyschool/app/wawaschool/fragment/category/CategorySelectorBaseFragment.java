package com.galaxyschool.app.wawaschool.fragment.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectorBaseFragment extends BaseFragment
        implements CategorySelectorView.OnCategorySelectListener {

    public interface Constants {
        public static final int REQUEST_CODE_SELECT_CATEGORY = 160116;
        public static final String REQUEST_DATA_SELECT_CATEGORY = "data";
    }

    protected CategorySelectorView categoryView;
    protected List<Category> allCategories;
    protected CategorySelectorView.OnCategorySelectListener selectListener;
    protected List<Category> selectedCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_selector, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void setOnCategorySelectListener(
            CategorySelectorView.OnCategorySelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setAllCategories(List<Category> allCategories) {
        this.allCategories = allCategories;
    }

    private void initViews() {
        this.categoryView = (CategorySelectorView) getView().findViewById(
                R.id.category_selector_view);
        if (this.categoryView != null) {
            this.categoryView.setAllCategories(this.allCategories);
            this.categoryView.setOnCategorySelectListener(this);
        }
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        selectedCategories = categories;
        if (selectListener != null) {
            selectListener.onCategorySelect(categories);
        } else {
            ArrayList<Category> result = new ArrayList<Category>();
            if (categories != null && categories.size() > 0) {
                for (Category cat : categories) {
                    result.add(cat);
                }
            }
            Bundle data = new Bundle();
            data.putParcelableArrayList(Constants.REQUEST_DATA_SELECT_CATEGORY, result);
            Intent intent = new Intent();
            intent.putExtras(data);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

}
