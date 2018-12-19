package com.lqwawa.intleducation.module.discovery.ui.person.timetable;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;

import org.joda.time.DateTime;

import java.util.List;

/**
 * 我的课程表的契约类
 */
public interface TimeTableContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取老师学生正在授课和即将授课班级 老师传0
        void requestOnlineClassIds(@NonNull String curMemberId, int role);
        // 获取开始时间-结束时间的课程表有课标识
        void requestTimeTableFlags(@NonNull DateTime startDate, @NonNull DateTime endDate, @NonNull List<String> ids);
        // 获取开始时间-结束时间的空中课堂
        void requestAirClassDataWithTimeTable(int pageIndex, @NonNull String beginTime, @NonNull String endTime, @NonNull List<String> ids);
        // 删除直播
        void requestDeleteLive(int id, @NonNull String classId, boolean deleteAll);
        // 检查直播所在的班级是否是历史班或者授课结束的班级
        void requestCheckFinishWithClassId(@NonNull LiveVo vo);
    }

    interface View extends BaseContract.View<Presenter>{
        // 班级Ids回调
        void updateOnlineClassIdsView(List<String> listData);
        // 课程表信息的返回
        void updateTimeTableFlagsView(@NonNull DateTime startDate, @NonNull DateTime endDate, List<String> timeTableFlags);
        // 返回空中课堂的数据
        void updateAirClassDataView(List<LiveVo> liveList);
        void updateMoreAirClassDataView(List<LiveVo> liveList);
        // 删除直播返回
        void updateDeleteOnlineLiveView(boolean result);
        // 检查回调
        void updateCheckFinishView(boolean finishOrHistory, @NonNull LiveVo vo);
    }

}
