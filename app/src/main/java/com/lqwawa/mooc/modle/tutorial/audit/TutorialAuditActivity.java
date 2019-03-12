package com.lqwawa.mooc.modle.tutorial.audit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.mooc.modle.tutorial.regist.TutorialRegisterActivity;

/**
 * @author mrmedici
 * @desc 助教审核页面
 */
public class TutorialAuditActivity extends ToolbarActivity {

    private static final String KEY_EXTRA_COMMIT_RESULT = "KEY_EXTRA_COMMIT_RESULT";

    private TopBar mTopBar;
    private FrameLayout mTelephomeLayout;
    private TextView mTvTelephone;

    private boolean mCommitResult;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_audit;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mCommitResult = bundle.getBoolean(KEY_EXTRA_COMMIT_RESULT);
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_tutorial_audit);

        mTelephomeLayout = (FrameLayout) findViewById(R.id.telephone_layout);
        mTvTelephone = (TextView) findViewById(R.id.tv_telephone);
        mTvTelephone.setText(String.format(getString(R.string.label_telephone_hot_line),Common.Constance.WAWACHAT_PHONENUMBER));
    }

    /**
     * 帮辅审核提示页面
     * @param context 上下文对象
     */
    public static void show(@NonNull final Context context,boolean commitResult){
        Intent intent = new Intent(context, TutorialAuditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_EXTRA_COMMIT_RESULT,commitResult);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
