package com.lqwawa.intleducation.module.discovery.ui.classcourse.history;

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
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;

import java.util.List;
import java.util.ListIterator;

/**
 * @author mrmedici
 * @desc 班级历史学程的Presenter
 */
public class HistoryClassCoursePresenter extends BasePresenter<HistoryClassCourseContract.View>
    implements HistoryClassCourseContract.Presenter {

    public HistoryClassCoursePresenter(HistoryClassCourseContract.View view) {
        super(view);
    }

    @Override
    public void requestHistoryClassConfigData(@NonNull String hostId) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(hostId, SetupConfigType.TYPE_HISTORY_COURSE,languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateHistoryClassConfigView(entities);
                }
            }
        });
    }

    @Override
    public void requestHistoryClassCourseData(@NonNull String classId, int role, @NonNull String name, @NonNull String level, int paramOneId, int paramTwoId, int pageIndex) {
        int status = 1;
        ClassCourseHelper.requestClassCourseData(classId,status, role, name, level,paramOneId,paramTwoId,pageIndex, AppConfig.PAGE_SIZE,new DataSource.Callback<List<ClassCourseEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ClassCourseEntity> classCourseEntities) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        // 下拉刷新
                        view.updateHistoryClassCourseView(classCourseEntities);
                    }else{
                        // 加载更多
                        view.updateMoreHistoryClassCourseView(classCourseEntities);
                    }
                }
            }
        });
    }

    @Override
    public void requestDeleteCourseFromHistoryClass(@NonNull String token, @NonNull String classId, @NonNull String ids) {
        getView().triggerUpdate();

        ClassCourseHelper.requestDeleteCourseFromClass(token, classId, ids, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateDeleteCourseFromClassView(aBoolean);
                }
            }
        });
    }

    @Override
    public void requestAddHistoryCourseFromClass(@NonNull String schoolId, @NonNull String classId, @NonNull List<ClassCourseEntity> entities) {
        getView().triggerUpdate();

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
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateHistoryCourseFromClassView(aBoolean);
                }
            }
        });
    }

    @Override
    public void requestRemoveHistoryCourseFromClass(@NonNull String schoolId, @NonNull String classId, @NonNull List<ClassCourseEntity> entities) {
        getView().triggerUpdate();

        StringBuilder courseIds = new StringBuilder();
        ListIterator<ClassCourseEntity> iterator = entities.listIterator();
        while (iterator.hasNext()){
            ClassCourseEntity entity = iterator.next();
            courseIds.append(entity.getCourseId());
            if(iterator.hasNext()){
                courseIds.append(",");
            }
        }

        ClassCourseHelper.requestRemoveClassHistoryCourse(classId,courseIds.toString(),new DataSource.SucceedCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final HistoryClassCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateHistoryCourseFromClassView(aBoolean);
                }
            }
        });
    }
}
