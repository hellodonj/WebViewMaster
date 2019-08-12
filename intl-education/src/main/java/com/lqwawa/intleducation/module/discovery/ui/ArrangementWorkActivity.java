package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails.CourseDetailItemParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.apps.BaseFragmentActivity;

import org.xutils.common.util.DensityUtil;

import static com.lqwawa.intleducation.base.ui.MyBaseFragment.FRAGMENT_BUNDLE_OBJECT;
import static com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity.ACTIVITY_BUNDLE_OBJECT;

/**
 * 描述: 作业布置率跳转界面
 * 作者|时间: djj on 2019/8/10 0010 下午 2:11
 */

public class ArrangementWorkActivity extends BaseFragmentActivity {

    private TopBar topBar;

    public static void start(Activity activity,
                             String courseId,
                             String classId,
                             String courseName,
                             Bundle bundle) {
        Intent intent = new Intent(activity, ArrangementWorkActivity.class);
        Bundle args = bundle;
        if (args == null) {
            args = new Bundle();
        }
        args.putString("classId", classId);
        args.putString("courseId", courseId);
        args.putString("courseName", courseName);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrangement_work);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);

        Fragment fragment = new CourseDetailsItemFragment();
        Bundle args = getIntent().getExtras();
        String id = args.getString("courseId");
        CourseVo courseVo = (CourseVo) args.getSerializable(CourseVo.class.getSimpleName());
        CourseDetailParams mDetailParams = (CourseDetailParams) args.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        String courseName = args.getString("courseName");
        topBar.setTitle(courseName);
        topBar.setTitleWide(DensityUtil.dip2px(120));
        if (courseVo != null) {
            id = courseVo.getId();
        }
        String mCurMemberId = args.getString("memberId");
        boolean isFromScan = args.getBoolean("isFromScan");
        boolean mCanEdit = TextUtils.equals(UserHelper.getUserId(), mCurMemberId);
        // 生成参数
        CourseDetailItemParams params1 = new CourseDetailItemParams(false, mCurMemberId, !mCanEdit, id);
        params1.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_INTRODUCTION);
        // 设置课程详情参数
        params1.setCourseParams(mDetailParams);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable(FRAGMENT_BUNDLE_OBJECT, params1);
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(CourseVo.class.getSimpleName(), courseVo);
        if (getIntent().getExtras().containsKey("CourseVo")) {
            CourseVo vo = (CourseVo) getIntent().getSerializableExtra("CourseVo");
            bundle2.putSerializable(CourseVo.class.getSimpleName(), vo);
        }
        bundle2.putSerializable(ACTIVITY_BUNDLE_OBJECT,mDetailParams);
        // 课程大纲传参
        CourseDetailItemParams params2 = (CourseDetailItemParams) params1.clone();
        params2.setDataType(CourseDetailItemParams.COURSE_DETAIL_ITEM_STUDY_PLAN);
        params2.setCourseParams(mDetailParams);
        bundle2.putSerializable(CourseDetailsItemFragment.FRAGMENT_BUNDLE_OBJECT, params2);
        bundle2.putBoolean("isFromScan", isFromScan);
        if (bundle2 != null) {
            fragment.setArguments(bundle2);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment);
        ft.commit();

    }
}
