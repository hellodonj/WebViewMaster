package com.lqwawa.intleducation.module.discovery.ui.lqbasic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;

/**
 * @author mrmeidi
 * @desc 国家课程页面
 */
public class LQBasicActivity extends ToolbarActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lq_basic;
    }

    /**
     * 国家课程的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,LQBasicActivity.class);
        Bundle extras = new Bundle();
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
