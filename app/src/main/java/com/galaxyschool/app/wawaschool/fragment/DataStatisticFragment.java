package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.LearningStatisticActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.adapter.DataStatisticAdapter;
import com.galaxyschool.app.wawaschool.common.RecyclerViewSpacesItemDecoration;
import com.galaxyschool.app.wawaschool.pojo.ViewBaseBean;

import java.util.ArrayList;
import java.util.List;

public class DataStatisticFragment extends ContactsListFragment {

    private RecyclerView recyclerView;
    private List<ViewBaseBean> list;
    private DataStatisticAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_statistic, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initAdapter();
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.contacts_header_title);
        if (titleView != null) {
            titleView.setText(getString(R.string.str_data_statistics));
        }
        ImageView backImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageView != null) {
            backImageView.setOnClickListener(v -> finish());
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void initData() {
        list =  new ArrayList<>();
        ViewBaseBean bean = new ViewBaseBean();
        bean.setTitle(getString(R.string.str_class_achieve_statistics));
        bean.setDrawableId(R.drawable.icon_class_statistic);
        list.add(bean);
        bean = new ViewBaseBean();
        bean.setTitle(getString(R.string.str_student_achieve_statistics));
        bean.setDrawableId(R.drawable.icon_student_rate);
        list.add(bean);
        bean = new ViewBaseBean();
        bean.setTitle(getString(R.string.str_homework_intro_rate));
        bean.setDrawableId(R.drawable.icon_intro_rate);
        list.add(bean);
        bean = new ViewBaseBean();
        bean.setTitle(getString(R.string.str_homework_mark_rate));
        bean.setDrawableId(R.drawable.icon_mark_rate);
        list.add(bean);
        bean = new ViewBaseBean();
        bean.setTitle(getString(R.string.str_homework_complete_rate));
        bean.setDrawableId(R.drawable.icon_finish_rate);
        list.add(bean);
    }

    private void initAdapter(){
        if (adapter == null){
            adapter = new DataStatisticAdapter(list);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(10, 10, 10, 10));
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClick(this::enterStatisticDetailActivity);
        }
    }

    private void enterStatisticDetailActivity(int position){
        if (position == 0){
            //班级成绩统计
            LearningStatisticActivity.start(getActivity(),getArguments(),true);
        } else if (position == 1){
            //学生学习统计
            LearningStatisticActivity.start(getActivity(),getArguments(),false);
        } else {
            //布置作业率 作业批阅率 作业完成率
            LearningStatisticActivity.start(getActivity(),position - 2,getArguments());
        }
    }
}
