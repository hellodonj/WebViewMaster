package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.Iterator;
import java.util.List;

public class SourceCourseListActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = SourceCourseListActivity.class.getSimpleName();
    //头部
    private TopBar topBar;
    //数据列表
    private ListView listView;
    private static Activity scanActivity = null;

    private List<CourseVo> courseList;
    private CourseListAdapter courseListAdapter;

    public static void start(Activity activity, String courseData) {
        if (courseData == null) {
            return;
        }
        scanActivity = null;
        List<CourseVo> courseList = null;
        try {
            ResponseVo<List<CourseVo>> result = JSON.parseObject(courseData,
                    new TypeReference<ResponseVo<List<CourseVo>>>() {
                    });
            if (result.getData() != null) {
                courseList = result.getData();
            }
        } catch (Exception e) {

        }
        if (courseList == null) {
            return;
        }
        if (courseList.size() == 0) {
            return;
        }
        if (courseList.size() == 1) {
            CourseDetailsActivity.start(activity, courseList.get(0).getCourseId(), true,
                    UserHelper.getUserId(), -2, courseList.get(0));
            activity.finish();
        } else {
            activity.startActivity(new Intent(activity, SourceCourseListActivity.class)
                    .putExtra("courseData", courseData));
            scanActivity = activity;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_course_list);

        topBar = (TopBar) findViewById(R.id.top_bar);
        listView = (ListView) findViewById(R.id.listView);
        initViews();
        initData();
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.view_source_course));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getCourseId(), true,
                        UserHelper.getUserId(), -2, vo);
                if (scanActivity != null) {
                    scanActivity.finish();
                }
                activity.finish();

            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void initData() {
        courseListAdapter = new CourseListAdapter(this);
        listView.setAdapter(courseListAdapter);
        String courseData = getIntent().getStringExtra("courseData");
        try {
            ResponseVo<List<CourseVo>> result = JSON.parseObject(courseData,
                    new TypeReference<ResponseVo<List<CourseVo>>>() {
                    });
            if (result.getData() != null) {
                courseList = result.getData();
            }
        } catch (Exception e) {

        }
        
        filterData(courseList);

        courseListAdapter.setData(courseList);
        courseListAdapter.notifyDataSetChanged();
    }

    private void filterData(List<CourseVo> courseList) {
        if (courseList == null || courseList.isEmpty()) {
            return;
        }

        // 过滤三习学程馆课程
        Iterator<CourseVo> iterator = courseList.iterator();
        while (iterator.hasNext()) {
           CourseVo courseVo =  iterator.next();
           if (courseVo != null && courseVo.getType() == 3) {
               iterator.remove();
           }
        }
    }
}
