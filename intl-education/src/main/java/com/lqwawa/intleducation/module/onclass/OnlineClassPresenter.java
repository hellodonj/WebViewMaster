package com.lqwawa.intleducation.module.onclass;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级列表的Presenter
 * @date 2018/05/31 15:05
 * @history v1.0
 * **********************************
 */
public class OnlineClassPresenter extends BasePresenter<OnlineClassContract.View>
    implements OnlineClassContract.Presenter{

    public OnlineClassPresenter(OnlineClassContract.View view) {
        super(view);
    }

    @Override
    public void onSearch(@NonNull String schoolId,@NonNull String keyword,int pageIndex) {
        /*OnlineCourseHelper.requestOnlineCourseData(schoolId, keyword,pageIndex,AppConfig.PAGE_SIZE,OnlineSortType.TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE,new DataSource.Callback<List<OnlineClassEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<OnlineClassEntity> onlineCourseEntities) {
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && onlineCourseEntities != null){
                    if(pageIndex == 0){
                        view.updateClassListView(onlineCourseEntities);
                    }else{
                        // 回调更多班级页码
                        view.updateClassMoreListView(onlineCourseEntities);
                    }
                }
            }
        });*/
    }

    @Override
    public void requestOnlineClassData(@NonNull String schoolId,int pageIndex) {
        /*OnlineCourseHelper.requestOnlineCourseData(schoolId, "",pageIndex,AppConfig.PAGE_SIZE,OnlineSortType.TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE,new DataSource.Callback<List<OnlineClassEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<OnlineClassEntity> onlineCourseEntities) {
                final OnlineClassContract.View view = getView();
                if(pageIndex == 0){
                    view.updateClassListView(onlineCourseEntities);
                }else{
                    // 回调更多班级页码
                    view.updateClassMoreListView(onlineCourseEntities);
                }
            }
        });*/
    }

    @Override
    public void requestOnlineClassByCourseId(@NonNull String courseId,int pageIndex) {
        OnlineCourseHelper.requestOnlineCourseDataByCourseId(courseId,pageIndex, AppConfig.PAGE_SIZE,new DataSource.Callback<ParamResponseVo<List<OnlineClassEntity>>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(ParamResponseVo<List<OnlineClassEntity>> vo) {
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && EmptyUtil.isNotEmpty(vo)){
                    if(pageIndex == 0){
                        view.updateClassListView(vo);
                    }else{
                        // 回调更多数据
                        view.updateClassMoreListView(vo);
                    }
                }
            }
        });
    }

    @Override
    public void requestLoadClassInfo(@NonNull String classId) {
        String memberId = UserHelper.getUserId();
        // 获取班级详情信息时候,弹出Dialog
        start();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, classId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final OnlineClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }



    @Override
    public void requestOnlineStudyLabelData() {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OnlineCourseHelper.requestNewOnlineStudyLabelData(languageRes,new DataSource.Callback<List<NewOnlineConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineClassContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<NewOnlineConfigEntity> entities) {
                final OnlineClassContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateOnlineStudyLabelView(entities);
                }
            }
        });
    }
}
