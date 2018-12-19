package com.lqwawa.intleducation.module.discovery.ui.lqcourse.livelist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.ClassroomFragment;

import static com.lqwawa.intleducation.module.discovery.ui.ClassroomFragment.Constants.EXTRA_SHOWTOPBAR;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程直播列表的Activity
 * @date 2018/05/03 15:59
 * @history v1.0
 * **********************************
 */
public class LiveListActivity extends ToolbarActivity{
    // 类型标注 直播类型
    private static final String KEY_EXTRA_SORT = "Sort";
    // 显示标题,搜索页面需要 ClassroomFragment
    private static final String KEY_EXTRA_TITLE = "LevelName";

    private String mSortType;
    private String mLevelName;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_live_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSortType = bundle.getString(KEY_EXTRA_SORT);
        mLevelName = bundle.getString(KEY_EXTRA_TITLE);
        if(EmptyUtil.isEmpty(mSortType) || EmptyUtil.isEmpty(mLevelName)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ClassroomFragment mClassroomFragment = new ClassroomFragment();//直播
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SHOWTOPBAR,true);
        mClassroomFragment.setArguments(bundle);
        // 显示直播列表
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.lay_content, mClassroomFragment);
        fragmentTransaction.show(mClassroomFragment);
        fragmentTransaction.commit();
    }

    /**
     * LQ学程直播列表入口
     * @param context 上下文对象
     * @param sortType 直播类型
     * @param levelName 标题文本
     */
    public static void show(@NonNull Context context,
                            @NonNull String sortType,
                            @NonNull String levelName){
        Intent intent = new Intent(context,LiveListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SORT,sortType);
        bundle.putString(KEY_EXTRA_TITLE,levelName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
