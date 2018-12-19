package com.lqwawa.intleducation.module.onclass.school;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineSchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;
import java.util.Objects;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 记得写注释喲
 * @date 2018/06/05 10:38
 * @history v1.0
 * **********************************
 */
public class SchoolInfoPresenter extends BasePresenter<SchoolInfoContract.View>
    implements SchoolInfoContract.Presenter{

    public SchoolInfoPresenter(SchoolInfoContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineSchoolInfoData(@NonNull String schoolId) {
        OnlineCourseHelper.requestOnlineSchoolInfoData(schoolId, 0,Integer.MAX_VALUE,new DataSource.Callback<OnlineSchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final SchoolInfoContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(OnlineSchoolInfoEntity onlineSchoolInfoEntity) {
                final SchoolInfoContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    List<CourseVo> listCourse = onlineSchoolInfoEntity.getCourseList();
                    if(EmptyUtil.isNotEmpty(listCourse) && listCourse.size() > 3){
                        // 长度超过3个,切割
                        listCourse = listCourse.subList(0,3);
                    }
                    // 进行UI回调
                    view.updateOnlineSchoolCourseView(listCourse);


                    List<OnlineClassEntity> entities = onlineSchoolInfoEntity.getCourseOnLineList();
                    if(EmptyUtil.isNotEmpty(entities) && entities.size() > 2){
                        // 长度超过2个,切割
                        entities = entities.subList(0,2);
                    }
                    // 进行UI回调
                    view.updateOnlineSchoolClassView(entities);
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
                final SchoolInfoContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final SchoolInfoContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }

    @Override
    public void requestOnlineSchoolTeacherData(@NonNull String schoolId) {
        OnlineCourseHelper.requestOnlineSchoolTeacherData(schoolId, 0, Short.MAX_VALUE, new DataSource.Callback<List<LQTeacherEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final SchoolInfoContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQTeacherEntity> lqTeacherEntities) {
                final SchoolInfoContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    if(EmptyUtil.isNotEmpty(lqTeacherEntities) && lqTeacherEntities.size() > 4){
                        // 截断,只需要显示四个老师
                        lqTeacherEntities = lqTeacherEntities.subList(0,4);
                    }

                    view.updateSchoolTeacherView(lqTeacherEntities);
                }
            }
        });
    }
}
