package com.lqwawa.intleducation.module.discovery.ui.person.timetable;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.AirClassHelper;
import com.lqwawa.intleducation.factory.helper.LiveHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;
import com.lqwawa.intleducation.module.onclass.detail.base.plan.ClassPlanContract;

import org.joda.time.DateTime;

import java.util.List;

/**
 * 我的课程表的Presenter
 */
public class TimeTablePresenter extends BasePresenter<TimeTableContract.View>
    implements TimeTableContract.Presenter{

    public TimeTablePresenter(TimeTableContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineClassIds(@NonNull String curMemberId, int role) {
        OnlineCourseHelper.requestOnlineClassIdsWithRole(curMemberId, role, new DataSource.Callback<List<String>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                TimeTableContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<String> strings) {
                TimeTableContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateOnlineClassIdsView(strings);
                }
            }
        });
    }

    @Override
    public void requestTimeTableFlags(@NonNull final DateTime startDate, final @NonNull DateTime endDate, @NonNull List<String> ids) {
        String beginTime = startDate.toString(DateUtils.YYYYMMDD);
        String endTime = endDate.toString(DateUtils.YYYYMMDD);
        AirClassHelper.requestTimeTableFlags(ids, beginTime, endTime, new DataSource.Callback<List<String>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                TimeTableContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<String> strings) {
                TimeTableContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateTimeTableFlagsView(startDate,endDate,strings);
                }
            }
        });
    }

    @Override
    public void requestAirClassDataWithTimeTable(int pageIndex,@NonNull String beginTime, @NonNull String endTime, @NonNull List<String> ids) {
        AirClassHelper.requestAirClassDataWithTimeTable(pageIndex, AppConfig.PAGE_SIZE,ids, beginTime, endTime, new DataSource.Callback<List<LiveVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                TimeTableContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LiveVo> listVos) {
                TimeTableContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateAirClassDataView(listVos);
                    }else{
                        view.updateMoreAirClassDataView(listVos);
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
                final TimeTableContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.updateDeleteOnlineLiveView(aBoolean);
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                final TimeTableContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }
        });
    }

    @Override
    public void requestCheckFinishWithClassId(@NonNull final LiveVo vo) {
        String classId = vo.getClassId();
        LiveHelper.requestCheckClassStatusWithClassId(classId, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TimeTableContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TimeTableContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.updateCheckFinishView(aBoolean,vo);
                }
            }
        });
    }
}
