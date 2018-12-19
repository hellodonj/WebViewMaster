package com.galaxyschool.app.wawaschool.views.categoryview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

import java.util.List;


/**
 * Created by Administrator on 2016/7/23.
 */
public class CategoryGridview extends LinearLayout implements View.OnClickListener {
    private Category category;
    private CategoryValueAdapter adapter;
    private SureSelectListener sureListener;
    private LayoutInflater inflater;

    public interface SureSelectListener {
        void onSureSelect();
    }
    public CategoryGridview(Context context) {
        super(context);
        initView();
    }

    public CategoryGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CategoryGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        initView();
    }

    public void initView( ) {
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mRootView = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_category_gridview, this);
        GridView gridview = (GridView) mRootView.findViewById(R.id.gridview);
        gridview.setNumColumns(1);
        adapter = new CategoryValueAdapter(getContext(),category);
        gridview.setAdapter(adapter);
        TextView clearBtn = (TextView) mRootView.findViewById(R.id.clear_btn);
        TextView sureBtn = (TextView) mRootView.findViewById(R.id.sure_btn);
        clearBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
    }



    public void finishSelect(){
        List<CategoryValue> categoryValues = category.getDetailList();
        if(categoryValues!=null&&categoryValues.size()>0){
            for(CategoryValue categoryValue : categoryValues){
                categoryValue.setSelect(categoryValue.isTempSelect());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_btn:
                clearSelects();
                break;
            case R.id.sure_btn:
                sureSelects();
                break;
        }
    }

    private void sureSelects() {
        finishSelect();
        sureListener.onSureSelect();
    }

    private void clearSelects() {
        if (category == null || category.getDetailList() == null || category.getDetailList
                ().size() == 0) {
            return;
        }
        List<CategoryValue> values = category.getDetailList();
        if(values!=null&&values.size()>0){
            for (CategoryValue value : values) {
                value.setTempSelect(false);
            }
            adapter.notifyDataSetChanged();
        }
    }






    public Category getCategory() {
        return category;
    }
    private void initTempSelect(){
        if(category!=null){
            List<CategoryValue> categoryValues = category.getDetailList();
            if(categoryValues!=null&&categoryValues.size()>0){
                for(CategoryValue categoryValue : categoryValues){
                    categoryValue.setTempSelect(categoryValue.isSelect());
                }
            }
        }
    }
    public void setCategory(Category category) {
        this.category = category;
        initTempSelect();
        adapter.setCategory(this.category);
    }

    public SureSelectListener getSureListener() {
        return sureListener;
    }

    public void setSureListener(SureSelectListener sureListener) {
        this.sureListener = sureListener;
    }
}
