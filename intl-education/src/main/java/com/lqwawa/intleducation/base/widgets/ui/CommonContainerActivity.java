package com.lqwawa.intleducation.base.widgets.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.learn.ui.mycourse.MyCourseListFragment;

/**
 * @author mrmedici
 * @desc 统一Fragment容器的入口
 */
public class CommonContainerActivity extends ToolbarActivity {

    public static final String KEY_EXTRA_TITLE_TEXT = "KEY_EXTRA_TITLE_TEXT";
    public static final String KEY_EXTRA_CLASS_OBJECT = "KEY_EXTRA_CLASS_OBJECT";

    private TopBar mTopBar;
    private Class mClazz;
    private String mTitle;
    private Bundle args;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_common_container;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mTitle = bundle.getString(KEY_EXTRA_TITLE_TEXT);
        mClazz = (Class) bundle.getSerializable(KEY_EXTRA_CLASS_OBJECT);
        args = (Bundle) bundle.clone();
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        if (!TextUtils.isEmpty(mTitle)) {
            mTopBar.setBack(true);
            mTopBar.setTitle(mTitle);
            mTopBar.setVisibility(View.VISIBLE);
        } else {
            mTopBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (mClazz != null) {
            try {
                Fragment fragment = (Fragment) mClazz.newInstance();
                if (fragment != null) {
                    args.remove(KEY_EXTRA_TITLE_TEXT);
                    args.remove(KEY_EXTRA_CLASS_OBJECT);
                    fragment.setArguments(args);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.lay_content, fragment, mClazz.getSimpleName());
                    ft.commit();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 统一Fragment容器的入口
     *
     * @param context     上下文对象
     * @param title 标题文本
     * @param clazz   要显示的Fragment
     * @param bundle   bundle对象
     */
    public static void show(@NonNull Context context,
                            String title,
                            @NonNull Class clazz, @NonNull Bundle bundle) {
        Intent intent = new Intent(context, CommonContainerActivity.class);
        bundle.putString(KEY_EXTRA_TITLE_TEXT, title);
        bundle.putSerializable(KEY_EXTRA_CLASS_OBJECT, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
