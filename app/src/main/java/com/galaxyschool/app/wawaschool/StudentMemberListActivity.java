package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.adapter.StudentMemberListAdapter;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;

import java.util.ArrayList;
import java.util.List;

/**
 * ======================================================
 * Describe: 学生成员列表
 * ======================================================
 */
public class StudentMemberListActivity extends BaseFragmentActivity {

    public static void start(Activity activity,
                             int courseId,
                             int fromType) {
        Intent intent = new Intent(activity, StudentMemberListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.COURSE_ID, courseId);
        bundle.putInt(Constants.FROM_TYPE, fromType);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public interface Constants {
        String COURSE_ID = "course_id";
        String FROM_TYPE = "from_type";
    }

    public interface FROM_TYPE {
        int FROM_COURSE_STATISTIC = 0;//来自课程统计
    }

    private RecyclerView recyclerView;
    private int fromType;
    private int courseId;
    private StudentMemberListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_member_list);
        loadIntentData();
        initViews();
        loadData();
        updateAdapter();
    }

    private void loadIntentData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            courseId = args.getInt(Constants.COURSE_ID);
            fromType = args.getInt(Constants.FROM_TYPE);
        }
    }

    private void initViews() {
        TextView titleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (titleTextV != null) {
            if (fromType == FROM_TYPE.FROM_COURSE_STATISTIC) {
                titleTextV.setText(getString(R.string.str_uncomplete_statistic));
            }
        }
        ImageView backImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageView != null) {
            backImageView.setOnClickListener(v -> finish());
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void loadData() {

    }

    private void updateAdapter() {
        List<StatisticBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            StatisticBean bean = new StatisticBean();
            bean.setNumber(i);
            bean.setStudentName("勇者" + i + "号");
            list.add(bean);
        }
        listAdapter = new StudentMemberListAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(listAdapter);
    }

}
