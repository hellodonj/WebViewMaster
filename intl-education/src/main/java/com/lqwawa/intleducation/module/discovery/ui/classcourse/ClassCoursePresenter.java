package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.helper.ClassCourseHelper;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionPresenter;

import java.util.List;
import java.util.ListIterator;

/**
 * @author mrmedici
 * @desc 班级学程页面的Presenter
 */
public class ClassCoursePresenter extends SchoolPermissionPresenter<ClassCourseContract.View>
    implements ClassCourseContract.Presenter{

    public ClassCoursePresenter(ClassCourseContract.View view) {
        super(view);
    }


    @Override
    public void requestClassConfigData(@NonNull String hostId) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(hostId, SetupConfigType.TYPE_CLASS,languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateClassConfigView(entities);
                }
            }
        });
    }

    @Override
    public void requestClassCourseData(@NonNull String classId,
                                       int role,@NonNull String name,
                                       @NonNull String level,
                                       int paramOneId,int paramTwoId,
                                       final int pageIndex,int courseType) {
        // 测试数据
        // classId = "164cdc60-243e-4318-92dd-a91f00a85974";
        // token = "4f961e8f-778a-4cf1-97a4-09fe25a183ff";
        // role = 1;
        ClassCourseHelper.requestClassCourseData(classId,0, role, name, level,paramOneId,paramTwoId,pageIndex, AppConfig.PAGE_SIZE,courseType,new DataSource.Callback<List<ClassCourseEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ClassCourseEntity> classCourseEntities) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        // 下拉刷新
                        view.updateClassCourseView(classCourseEntities);
                    }else{
                        // 加载更多
                        view.updateMoreClassCourseView(classCourseEntities);
                    }
                }
            }
        });
    }

    @Override
    public void requestStudyTaskClassCourseData(@NonNull String classId, @NonNull String name, int pageIndex) {
        ClassCourseHelper.requestStudyTaskClassCourseData(classId, name, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<ClassCourseEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ClassCourseEntity> classCourseEntities) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        // 下拉刷新
                        view.updateStudyTaskClassCourseView(classCourseEntities);
                    }else{
                        // 加载更多
                        view.updateMoreStudyTaskClassCourseView(classCourseEntities);
                    }
                }
            }
        });
    }

    @Override
    public void requestDeleteCourseFromClass(@NonNull String token, @NonNull String classId, @NonNull String ids) {
        ClassCourseHelper.requestDeleteCourseFromClass(token, classId, ids, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateDeleteCourseFromClassView(aBoolean);
                }
            }
        });
    }

    @Override
    public void requestAddCourseFromClass(@NonNull String schoolId,@NonNull String classId, @NonNull String courseIds) {
        ClassCourseHelper.requestAddCourseFromClass(schoolId,classId, courseIds, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateAddCourseFromClassView(aBoolean);
                }
            }
        });
    }

    @Override
    public void requestAddHistoryCourseFromClass(@NonNull String schoolId, @NonNull String classId, @NonNull List<ClassCourseEntity> entities) {
        StringBuilder courseIds = new StringBuilder();
        ListIterator<ClassCourseEntity> iterator = entities.listIterator();
        while (iterator.hasNext()){
            ClassCourseEntity entity = iterator.next();
            courseIds.append(entity.getCourseId());
            if(iterator.hasNext()){
                courseIds.append(",");
            }
        }

        ClassCourseHelper.requestAddClassHistoryCourse(classId, courseIds.toString(), new DataSource.SucceedCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateHistoryCourseFromClassView(aBoolean);
                }
            }
        });
    }
}
