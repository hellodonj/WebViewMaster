package com.lqwawa.intleducation.module.onclass.detail.base.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;

/**
 * @author mrmedici
 * @desc 在线班级通知列表
 */
public class ClassNotificationActivity extends ToolbarActivity{

    private TopBar mTopBar;
    private OnlineTabParams mTabParams;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_class_notification;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mTabParams = (OnlineTabParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mTabParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setTitle(R.string.title_class_notice);
        mTopBar.setBack(true);

        // 授课结束还能新建通知和删除通知
        mTabParams.setGiveFinish(false);

        Fragment fragment = ClassNotificationFragment.newInstance(mTabParams);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_content,fragment)
                .commit();
    }

    /**
     * 在线班级通知列表的入口
     * @param context 上下文对象
     * @param params 班级通知参数
     */
    public static void show(@NonNull Context context, @NonNull OnlineTabParams params){
        Intent intent = new Intent(context,ClassNotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
