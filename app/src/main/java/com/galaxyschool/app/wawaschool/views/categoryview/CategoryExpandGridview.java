package com.galaxyschool.app.wawaschool.views.categoryview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

import java.util.List;


/**
 * Created by Administrator on 2016/7/23.
 */
public class CategoryExpandGridview extends LinearLayout implements View.OnClickListener {
    private List<Category> categorys;
    private CategoryAdapter adapter;
    private SureSelectListener sureListener;

    public interface SureSelectListener {
        void onSureSelect();
    }
    public CategoryExpandGridview(Context context) {
        super(context);
        initView();
    }

    public CategoryExpandGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CategoryExpandGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        initView();
    }

    public void initView( ) {
        View mRootView = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_category_expandgridview, this);
        ListView listView = (ListView) mRootView.findViewById(R.id.listview);
        adapter = new CategoryAdapter(getContext(),categorys);
        listView.setAdapter(adapter);
        TextView clearBtn = (TextView) mRootView.findViewById(R.id.clear_btn);
        TextView sureBtn = (TextView) mRootView.findViewById(R.id.sure_btn);

        clearBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
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
    public void finishSelect(){
        if(categorys==null||categorys.size()==0){
            return;
        }
        for (Category category : categorys) {
            if(category!=null){
                List<CategoryValue> categoryValues = category.getDetailList();
                if(categoryValues!=null&&categoryValues.size()>0){
                    for(CategoryValue categoryValue : categoryValues){
                        categoryValue.setSelect(categoryValue.isTempSelect());
                    }
                }
            }
        }
    }
    private void clearSelects() {
        if (categorys == null) {
            return;
        }
        for (Category category : categorys) {
            List<CategoryValue> values = category.getDetailList();
            if(values!=null&&values.size()>0){
                for (CategoryValue value : values) {
                    value.setTempSelect(false);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }


    public List<Category> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<Category> categorys) {
        this.categorys = categorys;
        initTempSelect();
        adapter.setCategorys(this.categorys);
    }
    private void initTempSelect(){
        if(categorys==null||categorys.size()==0){
            return;
        }
        for (Category category : categorys) {
            if(category!=null){
                List<CategoryValue> categoryValues = category.getDetailList();
                if(categoryValues!=null&&categoryValues.size()>0){
                    for(CategoryValue categoryValue : categoryValues){
                        categoryValue.setTempSelect(categoryValue.isSelect());
                    }
                }
            }
        }
    }
    public SureSelectListener getSureListener() {
        return sureListener;
    }

    public void setSureListener(SureSelectListener sureListener) {
        this.sureListener = sureListener;
    }
}
