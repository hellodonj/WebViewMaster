package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.adapter.StudentMemberListAdapter;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StatisticBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ======================================================
 * Describe: 学生成员列表
 * ======================================================
 */
public class StudentMemberListActivity extends BaseFragmentActivity {

    public static void start(Activity activity,
                             List<StatisticBean> list,
                             String title,
                             boolean fromClassStatistic) {
        Intent intent = new Intent(activity, StudentMemberListActivity.class);
        Bundle bundle = new Bundle();
        if (list != null && list.size() > 0) {
            bundle.putSerializable(Constants.STUDENT_UMCOMPLETE_LIST, (Serializable) list);
        }
        bundle.putBoolean(Constants.FROM_CLASS_STATISTIC,fromClassStatistic);
        if (!TextUtils.isEmpty(title)){
            bundle.putString(Constants.TITLE,title);
        }
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public interface Constants {
        String STUDENT_UMCOMPLETE_LIST = "student_uncomplete_list";
        String FROM_CLASS_STATISTIC = "fromClassStatistic";
        String TITLE  = "title";
    }

    private RecyclerView recyclerView;
    private List<StatisticBean> list;
    private StudentMemberListAdapter listAdapter;
    private boolean fromClassStatistic;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_member_list);
        loadIntentData();
        initViews();
        updateAdapter();
    }

    private void loadIntentData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            list = (List<StatisticBean>) args.getSerializable(Constants.STUDENT_UMCOMPLETE_LIST);
            fromClassStatistic = args.getBoolean(Constants.FROM_CLASS_STATISTIC);
            title = args.getString(Constants.TITLE);
        }
    }

    private void initViews() {
        TextView titleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (titleTextV != null) {
            if (fromClassStatistic){
                titleTextV.setText(title);
            } else {
                titleTextV.setText(getString(R.string.str_uncomplete_statistic));
            }
        }
        ImageView backImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageView != null) {
            backImageView.setOnClickListener(v -> finish());
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void updateAdapter() {
        if (list == null || list.size() == 0){
            return;
        }
        listAdapter = new StudentMemberListAdapter(list,fromClassStatistic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnItemClick(position -> {
            if (fromClassStatistic){
                StatisticBean bean = list.get(position);
                LearningStatisticActivity.start(this,
                        bean.getCourseId(),
                        bean.getStudentName(),
                        bean.getClassId(),
                        RoleType.ROLE_TYPE_STUDENT,
                        bean.getStudentId());
            }
        });
    }

}
