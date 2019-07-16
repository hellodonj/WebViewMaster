package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;

/**
 * @author: wangchao
 * @date: 2019/07/02
 * @desc:
 */
public class NewOrganCourseFiltrateActivity extends ToolbarActivity {

    private Bundle args;
    private OrganCourseFiltrateFragment organCourseFiltrateFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_common_container;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        args = (Bundle) bundle.clone();
        return super.initArgs(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        organCourseFiltrateFragment = new OrganCourseFiltrateFragment();
        if (organCourseFiltrateFragment != null) {
            organCourseFiltrateFragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.lay_content, organCourseFiltrateFragment, OrganCourseFiltrateFragment.class.getSimpleName());
            ft.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (organCourseFiltrateFragment != null) {
            organCourseFiltrateFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 学程馆二级筛选页面的入口
     *
     * @param context            上下文对象
     * @param entity             实体数据对象
     * @param keyString          搜索条件
     * @param selectResource     是否是选择资源
     * @param isClassCourseEnter 是否是班级学程入口
     * @param data               选择学程馆资源的data
     * @param isHostEnter        是否直接从学程馆入口进来的
     */
    public static void showFromSearch(@NonNull Activity activity,
                                      @NonNull LQCourseConfigEntity entity,
                                      @NonNull String keyString,
                                      boolean selectResource,
                                      boolean isClassCourseEnter,
                                      @NonNull ShopResourceData data,
                                      @NonNull boolean isHostEnter) {
        show(activity, entity, selectResource, isClassCourseEnter, data, null, false,
                false, isHostEnter, null, 0, keyString);
    }

    /**
     * 学程馆二级筛选页面的入口
     *
     * @param activity           上下文对象
     * @param entity             实体数据对象
     * @param selectResource     是否是选择资源
     * @param isClassCourseEnter 是否是班级学程入口
     * @param data               选择学程馆资源的data
     * @param isAuthorized       是否已经获取到授权
     * @param isReallyAuthorized 该分类是否获取到授权了
     * @param isHostEnter        是否直接从学程馆入口进来的
     * @param libraryType        学程馆类型 0 习课程馆 1练测馆  2 图书馆  3 视频馆
     */
    public static void show(@NonNull Activity activity,
                            @NonNull LQCourseConfigEntity entity,
                            boolean selectResource,
                            boolean isClassCourseEnter,
                            @NonNull ShopResourceData data,
                            boolean isAuthorized,
                            boolean isReallyAuthorized,
                            boolean isHostEnter,
                            String roles,
                            int libraryType) {
        show(activity, entity, selectResource, isClassCourseEnter, data, null, isAuthorized,
                isReallyAuthorized, isHostEnter, roles, libraryType, "");
    }

    /**
     * 学程馆二级筛选页面的入口
     *
     * @param activity           上下文对象
     * @param entity             实体数据对象
     * @param selectResource     是否是选择资源
     * @param isClassCourseEnter 是否是班级学程入口
     * @param data               选择学程馆资源的data
     * @param extras             布置任务直播参数
     * @param isAuthorized       是否已经获取到授权
     * @param isReallyAuthorized 该分类是否获取到授权了
     * @param isHostEnter        是否直接从学程馆入口进来的
     * @param libraryType        学程馆类型 0 习课程馆 1练测馆  2 图书馆  3 视频馆
     */
    public static void show(@NonNull Activity activity,
                            @NonNull LQCourseConfigEntity entity,
                            boolean selectResource,
                            boolean isClassCourseEnter,
                            ShopResourceData data,
                            Bundle extras,
                            boolean isAuthorized,
                            boolean isReallyAuthorized,
                            boolean isHostEnter,
                            String roles,
                            int libraryType,
                            String keyString) {
        OrganCourseFiltrateParams organCourseFiltrateParams = new OrganCourseFiltrateParams();
        organCourseFiltrateParams.setLqCourseConfigEntity(entity)
                .setSelectResource(selectResource)
                .setClassCourseEnter(isClassCourseEnter)
                .setBundle(extras)
                .setAuthorized(isAuthorized)
                .setReallyAuthorized(isReallyAuthorized)
                .setHostEnter(isHostEnter)
                .setRoles(roles)
                .setLibraryType(libraryType)
                .setKeyString(keyString);
        if (selectResource) {
            organCourseFiltrateParams.setShopResourceData(data);
        }
        Intent intent = new Intent(activity, NewOrganCourseFiltrateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(OrganCourseFiltrateParams.class.getSimpleName(), organCourseFiltrateParams);
        intent.putExtras(bundle);
        if (data != null && data.getRequestCode() > 0) {
            activity.startActivityForResult(intent, data.getRequestCode());
        } else {
            activity.startActivity(intent);
        }
    }
}
