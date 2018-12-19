package com.lqwawa.mooc.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * @author mrmedici
 * @desc 学校班级选择的Activity
 */
public class SchoolClassSelectActivity extends PresenterActivity<SchoolClassSelectContract.Presenter>
    implements SchoolClassSelectContract.View{

    private TopBar mTopBar;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_school_class_select;
    }

    @Override
    protected SchoolClassSelectContract.Presenter initPresenter() {
        return new SchoolClassSelectPresenter(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.label_picker_class);
        Fragment fragment = new SchoolClassSelectFragment();
        Bundle args = getIntent().getExtras();
        if (args != null){
            //布置学习任务之选取
            boolean fromStudyTaskCheckData = args.getBoolean(SchoolClassSelectFragment.Constants
                    .FROM_STUDYTASK_CHECK_DATA,false);
            if (fromStudyTaskCheckData){
                mTopBar.setTitle(R.string.str_class_lesson);
            }
            fragment.setArguments(args);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_content,fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
