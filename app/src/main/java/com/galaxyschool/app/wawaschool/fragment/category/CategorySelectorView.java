package com.galaxyschool.app.wawaschool.fragment.category;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.ContactsClassCategorySelectorFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorySelectorView extends LinearLayout {

    public enum DefaultPosition {
        FIRST,
        LAST
    }

    private ListView nameListView, valueListView;
    private AdapterViewHelper nameListViewHelper, valueListViewHelper;
    private List<Category> allCategories;
    private Map<Integer, Boolean> categoryFocusedMap = new HashMap();
    private Map<Integer, Boolean> categorySelectedMap = new HashMap();
    private Map<Integer, Boolean> valueSelectedMap;
    private Map<Integer, Map<Integer, Boolean>> valueSelectedListMap = new HashMap();
    private OnCategorySelectListener selectListener;
    private OnCategoryValueSelectListener valueSelectListener;
    private boolean fillWithDefaultCategory = true;
    private boolean fillWithDefaultValue = true;
    private DefaultPosition defaultCategoryPosition = DefaultPosition.FIRST;
    private DefaultPosition defaultValuePosition = DefaultPosition.FIRST;
    private String allString;
    private String defaultCategoryName;
    private String defaultValueName;
    private Category defaultCategory;
    private CategoryValue defaultValue;
    private List<CategoryValue> defaultValues = new ArrayList();
    private boolean notifyAllSelectedValues;

    public CategorySelectorView(Context context) {
        super(context);
        initViews();
    }

    public CategorySelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public CategorySelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public void setFillWithDefault(boolean fillDefault) {
        setFillWithDefaultCategory(fillDefault);
        setFillWithDefaultValue(fillDefault);
    }

    public void setFillWithDefaultCategory(boolean fillDefault) {
        this.fillWithDefaultCategory = fillDefault;
    }

    public void setFillWithDefaultValue(boolean fillDefault) {
        this.fillWithDefaultValue = fillDefault;
    }

    public void setDefaultPosition(DefaultPosition position) {
        setDefaultCategoryPosition(position);
        setDefaultValuePosition(position);
    }

    public void setDefaultCategoryPosition(DefaultPosition position) {
        this.defaultCategoryPosition = position;
    }

    public void setDefaultValuePosition(DefaultPosition position) {
        this.defaultValuePosition = position;
    }

    public void setDefaultName(String name) {
        setDefaultCategoryName(name);
        setDefaultValueName(name);
    }

    public void setDefaultCategoryName(String name) {
        this.defaultCategoryName = name;
        this.defaultCategory.setName(name);
    }

    public void setDefaultValueName(String name) {
        this.defaultValueName = name;
        this.defaultValue.setValue(name);
        if (this.defaultValues.size() > 0) {
            for (CategoryValue value : this.defaultValues) {
                value.setValue(name);
            }
        }
    }

    public void setNotifyAllSelectedValues(boolean notify) {
        this.notifyAllSelectedValues = notify;
    }

    public void setOnCategorySelectListener(OnCategorySelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setOnCategoryValueSelectListener(OnCategoryValueSelectListener selectListener) {
        this.valueSelectListener = selectListener;
    }

    public List<Category> getAllCategories() {
        return this.allCategories;
    }

    public void setAllCategories(List<Category> allCategories) {
        setAllCategories(allCategories, true);
    }

    public void setAllCategories(List<Category> allCategories, boolean updateViews) {
        if (allCategories == this.allCategories) {
            return;
        }
        if (allCategories != null && allCategories.size() > 0) {
            if (this.fillWithDefaultCategory) {
                if (DefaultPosition.FIRST.ordinal() == this.defaultCategoryPosition.ordinal()) {
                    allCategories.add(0, this.defaultCategory);
                } else {
                    allCategories.add(this.defaultCategory);
                }
            }

            for (Category obj : allCategories) {
                if (obj.getAllValues() == null) {
                    obj.setAllValues(new ArrayList());
                }

                this.valueSelectedListMap.put(obj.getType(), new HashMap());

                if (obj != this.defaultCategory) {
                    if (this.fillWithDefaultValue && obj.isFillWithDefaultValue()) {
                        if (DefaultPosition.FIRST.ordinal() == this.defaultValuePosition.ordinal()) {
                            obj.getAllValues().add(0, newDefaultValue());
                        } else {
                            if ((obj.getType() == ContactsClassCategorySelectorFragment.Constants
                                    .SCHOOL_CATEGORY_STAGE) || (obj.getType() ==
                                    ContactsClassCategorySelectorFragment.Constants
                                    .SCHOOL_CATEGORY_GRADE)){
                            } else {
                                obj.getAllValues().add(newDefaultValue());
                            }
                        }
                    }
                    if (obj.getAllValues().size() > 0) {
                        obj.setCurrValue(obj.getAllValues().get(0));
                        this.valueSelectedListMap.get(obj.getType()).put(0, true);
                    }
                }
            }

            this.categoryFocusedMap.put(0, true);
        }
        this.allCategories = allCategories;
        this.nameListViewHelper.setData(allCategories, updateViews);

        Category data = (Category) nameListViewHelper.getDataAdapter().getItem(0);
        valueSelectedMap = valueSelectedListMap.get(data.getType());
        NameViewHolder holder = new NameViewHolder();
        holder.data = data;
        holder.position = 0;
        valueListView.setTag(holder);
        valueListViewHelper.setData(data.getAllValues(), updateViews);
    }

    protected void selectCategory(int position, boolean selected) {
        if (this.categorySelectedMap != null) {
            this.categorySelectedMap.put(position, selected);
        }
    }

    protected void clearSelectedCategories() {
        if (this.categorySelectedMap != null) {
            this.categorySelectedMap.clear();
        }

        for (Map.Entry<Integer, Map<Integer, Boolean>> entry : this.valueSelectedListMap.entrySet()) {
            entry.getValue().clear();
            if (entry.getKey() != this.defaultCategory.getType()) {
                if (this.fillWithDefaultCategory) {
                    entry.getValue().put(0, true);
                }
            }
        }

        for (Category cat : this.allCategories) {
            if (cat != this.defaultCategory) {
                if (this.fillWithDefaultValue
                        && DefaultPosition.FIRST.ordinal() == this.defaultValuePosition.ordinal()) {
                    cat.setCurrValue(cat.getAllValues().get(0));
                } else {
                    cat.setCurrValue(null);
                }
            }
        }
    }

    protected boolean isCategorySelected(int position) {
        if (this.categorySelectedMap != null) {
            if (!this.categorySelectedMap.containsKey(position)) {
                return false;
            }
            return this.categorySelectedMap.get(position);
        }
        return false;
    }

    protected void focusCategory(int position, boolean selected) {
        if (this.categoryFocusedMap != null) {
            this.categoryFocusedMap.put(position, selected);
        }
    }

    protected void clearFocusedCategoris() {
        if (this.categoryFocusedMap != null) {
            this.categoryFocusedMap.clear();
        }
    }

    protected boolean isCategoryFocused(int position) {
        if (this.categoryFocusedMap != null) {
            if (!this.categoryFocusedMap.containsKey(position)) {
                return false;
            }
            return this.categoryFocusedMap.get(position);
        }
        return false;
    }

    protected Map<Integer, Boolean> getValueSelectedMap(int type) {
        return this.valueSelectedListMap.get(type);
    }

    protected void selectValue(int position, boolean selected) {
        if (this.valueSelectedMap != null) {
            this.valueSelectedMap.put(position, selected);
        }
    }

    protected void clearSelectedValues() {
        if (this.valueSelectedMap != null) {
            this.valueSelectedMap.clear();
        }
    }

    protected boolean isValueSelected(int position) {
        if (this.valueSelectedMap != null) {
            if (!this.valueSelectedMap.containsKey(position)) {
                return false;
            }
            return this.valueSelectedMap.get(position);
        }
        return false;
    }

    public void selectValue(Category category, int position, boolean selected) {
        if (category == null || position < 0) {
            return;
        }
        Map<Integer, Boolean> valueSelectedMap =
                this.valueSelectedListMap.get(category.getType());
        if (valueSelectedMap != null) {
            valueSelectedMap.clear();
            if (valueSelectedMap != null) {
                valueSelectedMap.put(position, selected);
            }
        }
    }

    public int getSelectedValuePosition(Category category) {
        if (category == null) {
            return -1;
        }
        Map<Integer, Boolean> valueSelectedMap =
                this.valueSelectedListMap.get(category.getType());
        if (valueSelectedMap != null) {
            for (Map.Entry<Integer, Boolean> entry : valueSelectedMap.entrySet()) {
                if (entry.getValue()) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }

    private CategoryValue newDefaultValue() {
        CategoryValue value = new CategoryValue();
        value.setId("0");
        if (!TextUtils.isEmpty(this.defaultValueName)) {
            value.setValue(this.defaultValueName);
        } else {
            value.setValue(this.allString);
        }
        value.setIsDefault(true);
        this.defaultValues.add(value);
        return value;
    }

    private Category newDefaultCategory() {
        Category category = new Category();
        category.setType(0);
        if (!TextUtils.isEmpty(this.defaultCategoryName)) {
            category.setName(this.defaultCategoryName);
        } else {
            category.setName(this.allString);
        }
        category.setIsDefault(true);
        return category;
    }

    private void initViews() {
        this.allString = getContext().getString(R.string.all);
        this.defaultValue = newDefaultValue();
        this.defaultCategory = newDefaultCategory();
        List<CategoryValue> values = new ArrayList();
        values.add(this.defaultValue);
        this.defaultCategory.setAllValues(values);

        View rootView = LayoutInflater.from(getContext()).inflate(
                R.layout.category_selector_view, this);
        ListView listView = (ListView) rootView.findViewById(R.id.category_name_list);
        if (listView != null) {
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getContext(),
                    listView, R.layout.category_name_item) {
                @Override
                public void loadData() {

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    Category data = (Category) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }

                    NameViewHolder holder = (NameViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new NameViewHolder();
                    }
                    holder.position = position;
                    holder.data = data;

                    view.setBackgroundColor(Color.parseColor(isCategoryFocused(position) ?
                            "#ebebeb" : "#ffffff"));
                    TextView textView = (TextView) view.findViewById(R.id.category_name);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    textView = (TextView) view.findViewById(R.id.category_value);
                    if (textView != null) {
                        if (data.getCurrValue() != null) {
                            if (data.getCurrValue().getNewValue() != null) {
                                textView.setText(data.getCurrValue().getNewValue());
                            } else {
                                textView.setText(data.getCurrValue().getValue());
                            }
                        } else {
                            textView.setText(null);
                        }
                        textView.setTextColor(Color.parseColor(
                                (data.getCurrValue() == null
                                        || data.getCurrValue().isDefault()) ?
                                "#373636" : "#009039"));
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.category_indicator);
                    if (imageView != null) {
                        imageView.setVisibility((data.getAllValues() != null
                                && data.getAllValues().size() > 0) ? View.VISIBLE : View.INVISIBLE);
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    clearFocusedCategoris();

                    NameViewHolder holder = (NameViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    Category data = (Category) holder.data;
                    focusCategory(position, true);

                    if (data.getAllValues() != null && data.getAllValues().size() > 0) {
                        valueSelectedMap = valueSelectedListMap.get(data.getType());
                        valueListView.setTag(holder);
                        valueListViewHelper.setData(data.getAllValues());
                    } else {
                        valueSelectedMap = null;
                        valueListView.setTag(null);
                        valueListViewHelper.setData(null);
                    }
                    getDataAdapter().notifyDataSetChanged();
                }
            };
            this.nameListView = listView;
            this.nameListViewHelper = listViewHelper;
        }

        listView = (ListView) rootView.findViewById(R.id.category_value_list);
        if (listView != null) {
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getContext(),
                    listView, R.layout.category_value_item) {
                @Override
                public void loadData() {

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    CategoryValue data = (CategoryValue) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }

                    ValueViewHolder holder = (ValueViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ValueViewHolder();
                    }
                    holder.position = position;
                    holder.data = data;

                    boolean selected = isValueSelected(position);
                    TextView textView = (TextView) view.findViewById(R.id.category_value);
                    if (textView != null) {
                        textView.setText(data.getValue());
                        textView.setTextColor(Color.parseColor(selected ? "#009039" : "#373636"));
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.category_selector);
                    if (imageView != null) {
                        imageView.setSelected(selected);
                        imageView.setImageResource(selected ?
                                R.drawable.my_detail_right_ico :
                                R.drawable.my_detail_right_off_ico);
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    clearSelectedValues();

                    ValueViewHolder holder = (ValueViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    CategoryValue value = (CategoryValue) holder.data;
                    selectValue(position, true);

                    NameViewHolder nameHolder = (NameViewHolder) parent.getTag();
                    if (nameHolder != null) {
                        Category cat = (Category) nameHolder.data;
                        cat.setCurrValue(value);
                        if (cat.isDefault()) {
                            clearSelectedCategories();
                            selectValue(position, true);
                            nameListViewHelper.getDataAdapter().notifyDataSetChanged();
                        } else {
                            boolean selected = value.isDefault();
                            if (selected) {
                                defaultCategory.setCurrValue(null);
                                Map<Integer, Boolean> map = getValueSelectedMap(
                                        defaultCategory.getType());
                                if (map != null) {
                                    map.clear();
                                }
                            }
                            nameListViewHelper.getDataAdapter().notifyDataSetChanged();
                            selectCategory(nameHolder.position, selected);
                        }

                        if (valueSelectListener != null) {
                            valueSelectListener.onCategoryValueSelect(cat);
                        }
                    }
                    getDataAdapter().notifyDataSetChanged();
                }
            };
            this.valueListView = listView;
            this.valueListViewHelper = listViewHelper;
        }

        TextView textView = (TextView) rootView.findViewById(R.id.confirm_btn);
        if (textView != null) {
            textView.setText(R.string.start_to_search);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectListener != null) {
                        selectListener.onCategorySelect(getSelectedCategories());
                    }
                }
            });
        }
    }

    public TextView getConfirmButton() {
        return (TextView) findViewById(R.id.confirm_btn);
    }

    public List<Category> getSelectedCategories() {
        if (this.allCategories == null || this.allCategories.size() <= 0) {
            return null;
        }
        List<Category> result = new ArrayList();
        for (Category cat : this.allCategories) {
            if (cat.getCurrValue() != null) {
                if (this.notifyAllSelectedValues) {
                    result.add(cat);
                } else {
                    if (!cat.getCurrValue().isDefault()) {
                        result.add(cat);
                    }
                }
            }
        }
        return result;
    }

    public void updateViews() {
        updateCategoryListView();
        updateCategoryValueListView();
    }

    public void updateCategoryListView() {
        nameListViewHelper.getDataAdapter().notifyDataSetChanged();
    }

    public void updateCategoryValueListView() {
        valueListViewHelper.getDataAdapter().notifyDataSetChanged();
    }

    class NameViewHolder extends ViewHolder {
        int position;
    }

    class ValueViewHolder extends ViewHolder {
        int position;
    }

    public interface OnCategorySelectListener {

        public void onCategorySelect(List<Category> categories);

    }

    public interface OnCategoryValueSelectListener {

        public void onCategoryValueSelect(Category category);

    }

}
