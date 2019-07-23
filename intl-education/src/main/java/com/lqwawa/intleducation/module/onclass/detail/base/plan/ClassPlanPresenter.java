package com.lqwawa.intleducation.module.onclass.detail.base.plan;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LiveEntity;
import com.lqwawa.intleducation.factory.helper.LiveHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 授课计划的Presenter
 * @date 2018/06/01 16:05
 * @history v1.0
 * **********************************
 */
public class ClassPlanPresenter extends BasePresenter<ClassPlanContract.View>
    implements ClassPlanContract.Presenter{

    public ClassPlanPresenter(ClassPlanContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineClassLiveData(@NonNull String schoolId, @NonNull String classId,int pageIndex) {
        // 不需要支持分页
        LiveHelper.requestOnlineClassLiveData(schoolId, classId, pageIndex, Integer.MAX_VALUE,new DataSource.Callback<List<LiveEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassPlanContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LiveEntity> entities) {
                final ClassPlanContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    // 没有返回直播数据,也要回调出去
                    if(pageIndex == 0){
                        // 首次加载
                        view.updateOnlineClassLiveView(entities);
                    }else{
                        // 更多加载
                        view.updateOnlineClassMoreLiveView(entities);
                    }
                }
            }
        });
    }

    @Override
    public void requestDeleteLive(int id, @NonNull String classId,boolean deleteAll) {
        OnlineCourseHelper.requestDeletePlanLive(id,classId,deleteAll,new DataSource.Callback<Boolean>(){
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassPlanContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.updateDeleteOnlineLiveView(aBoolean);
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassPlanContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }
        });
    }

    @Override
    public void requestJudgeJoinLive(@NonNull final LiveVo liveVo, @NonNull String schoolId, @NonNull String classId, @NonNull String liveId,boolean isAudition) {
        OnlineCourseHelper.requestJudgeJoinLive(schoolId,classId,liveId,new DataSource.Callback<Boolean>(){
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassPlanContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    if(EmptyUtil.isEmpty(aBoolean)){
                        view.updateJudgeLiveView(liveVo,false,isAudition);
                    }else{
                        view.updateJudgeLiveView(liveVo,aBoolean,isAudition);
                    }
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassPlanContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }
        });
    }
}
