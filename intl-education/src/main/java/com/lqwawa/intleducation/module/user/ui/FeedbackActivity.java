package com.lqwawa.intleducation.module.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.TopBar;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 意见反馈
 */
public class FeedbackActivity extends MyBaseActivity implements
        View.OnClickListener {
    private static String TAG = "FeedbackActivity";

    private TopBar topBar;
    private EditText feedback_content_et;
    private EditText connection_number_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        topBar = (TopBar)findViewById(R.id.top_bar);
        feedback_content_et  = (EditText)findViewById(R.id.feedback_content_et);
        connection_number_et  = (EditText)findViewById(R.id.connection_number_et);
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.feedback));
        topBar.setRightFunctionText1(getResources().getString(R.string.commit),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCommit();
                    }
                });
    }

    @Override
    public void onClick(View view) {

    }

    private void doCommit() {
        if (feedback_content_et.getText().toString().isEmpty()){
            ToastUtil.showToast(activity, getResources().getString(R.string.enter_feedback_please));
            return;
        }
        if (connection_number_et.getText().toString().isEmpty()){
            ToastUtil.showToast(activity, getResources().getString(R.string.enter_connect_please));
            return;
        }
        ToastUtil.showToast(activity, getResources().getString(R.string.commit)
                + getResources().getString(R.string.success));
        finish();
    }
}
