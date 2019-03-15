package com.lqwawa.intleducation.module.discovery.ui.lqcourse.course;

import android.app.Activity;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.constant.SharedConstant;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.CourseRouteEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.module.discovery.tool.CourseDetails;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.DialogHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * @author mrmedici
 * @desc 课程详情页面的路由转发
 */
public class CourseRoute {

    // 是否是空中课堂的老师
    private boolean isOnlineTeacher;
    // 是否已经作学程的辅导老师处理？优先学生购买，免费
    private boolean isOnlineCounselor;
    private DialogHelper.LoadingDialog mLoadingDialog;

    public CourseRoute() {

    }

    public CourseRoute(boolean isOnlineTeacher) {
        this();
        this.isOnlineTeacher = isOnlineTeacher;
    }

    /**
     * 路由转发的方法
     * @param activity 上一页面的上下文对象
     * @param courseId 课程Id
     * @param memberId 家长传孩子的memberId
     * @param courseParams 课程入口类型，如果大厅过来的,courseParams == null;
     * @param listener 回调对象
     */
    public void navigation(Activity activity,
                           final String courseId,
                           final String memberId,
                           final CourseDetailParams courseParams,
                           final INavigationListener listener) {

        if(!UserHelper.isLogin()){
            // 未登录
            if(EmptyUtil.isNotEmpty(listener)){
                // 进入未登录页面
                listener.route(false);
                return;
            }
        }

        showLoadingDialog(activity);
        // 获取到入口类型
        final int enterType = transferEnterType(courseParams);
        CourseHelper.requestCourseStatus(memberId, courseId, new DataSource.Callback<CourseRouteEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // 网络请求失败 不发生跳转
                UIUtil.showToastSafe(strRes);
                hideLoadingDialog();
            }

            @Override
            public void onDataLoaded(CourseRouteEntity routeEntity) {
                hideLoadingDialog();
                if(EmptyUtil.isEmpty(routeEntity) || EmptyUtil.isEmpty(listener)){
                    return;
                }

                // 再网络请求，是否是帮辅模式,并且是老师
                boolean tutorialMode = MainApplication.isTutorialMode();
                if(tutorialMode){
                    CourseHelper.isTutorCourseBycourseId(memberId, courseId, new DataSource.SucceedCallback<Boolean>() {
                        @Override
                        public void onDataLoaded(Boolean aBoolean) {
                            navigationDispatch(enterType,memberId,courseId,courseParams,routeEntity,listener,aBoolean);
                        }
                    });
                }else{
                    navigationDispatch(enterType,memberId,courseId,courseParams,routeEntity,listener,false);
                }
            }
        });
    }

    /***
     * 统一分发
     * @param routeEntity 路由实体
     */
    private void navigationDispatch(final int enterType,@NonNull final String memberId,
                                    @NonNull final String courseId,@NonNull CourseDetailParams courseParams,
                                    @NonNull final CourseRouteEntity routeEntity,
                                    @NonNull final INavigationListener listener,
                                    boolean tutorialTeacher){
        // 获取到路由对象
        if(enterType == CourseDetailType.COURSE_DETAIL_MOOC_ENTER){
            // 大厅或者我的学程进来
            navigationMooc(routeEntity,memberId,listener,tutorialTeacher);
        }else if(enterType == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER){
            // 机构学程入口进来
            navigationOrgan(routeEntity,courseId,memberId,courseParams,listener,tutorialTeacher);
        }else if(enterType == CourseDetailType.COURSE_DETAIL_CLASS_ENTER){
            // 班级学程入口进来
            navigationClass(routeEntity,courseId,memberId,courseParams,listener,tutorialTeacher);
        }
    }

    /**
     * Mooc 大厅进入的课程详情
     * @param entity 课程状态实体
     * @param listener 回调对象
     */
    private void navigationMooc(@NonNull CourseRouteEntity entity,
                                @NonNull String memberId,
                                @NonNull INavigationListener listener,
                                boolean tutorialTeacher){

        isOnlineCounselor = isOnlineCounselor(entity, isOnlineTeacher);

        // 判断是否是学程的老师身份
        if(entity.isCourseTeacher(memberId) || isOnlineCounselor){
            // 如果是课程的老师
            // 并且是空中课堂的老师(空中课堂老师辅导老师身份处理)
            if(EmptyUtil.isNotEmpty(listener)){
                listener.route(true,tutorialTeacher);
                return;
            }
        }

        if(entity.getPrice() == 0){
            // 免费的
            if(entity.isJoin()){
                // 免费的已加入
                listener.route(true,tutorialTeacher);
            }else{
                // 免费的未加入
                listener.route(false,tutorialTeacher);
            }
        }else{
            // 收费的
            if(entity.isBuy()){
                // 已经购买
                if(entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_ALL ||
                        entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_CHAPTER){
                    // 娃娃币购买全本或者章节
                    if(entity.isJoin()){
                        // 已加入
                        listener.route(true,tutorialTeacher);
                    }else{
                        // 未加入
                        listener.route(false,tutorialTeacher);
                    }
                }else if(entity.getBuyType() == CourseRouteEntity.BUY_TYPE_AUTHORIZATION_ALL ||
                        entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_CHAPTER_AUTHORIZATION_ALL){
                    // 激活码授权码全本 || 章节购买后授权全本
                    if(entity.isExpire()){
                        // 过期进入未加入
                        listener.route(false,tutorialTeacher);
                    }else{
                        if(entity.isJoin()){
                            // 已经加入
                            listener.route(true,tutorialTeacher);
                        }else{
                            listener.route(false,tutorialTeacher);
                        }
                    }
                }
            }else{
                // 未购买
                listener.route(false,tutorialTeacher);
            }
        }
    }

    /**
     * 机构学程进入的课程详情
     * @param entity 课程状态实体
     * @param courseId 课程Id
     * @param courseParams 课程核心参数
     * @param listener 回调对象
     */
    private void navigationOrgan(@NonNull CourseRouteEntity entity,
                                 @NonNull String courseId,
                                 @NonNull String memberId,
                                 CourseDetailParams courseParams,
                                 @NonNull INavigationListener listener,
                                 boolean tutorialTeacher){

        boolean isAuthorized = courseParams.isAuthorized();
        String schoolId = courseParams.getSchoolId();
        String classId = courseParams.getClassId();

        isOnlineCounselor = isOnlineCounselor(entity, isOnlineTeacher);

        // 判断是否是学程的老师身份
        if(entity.isCourseTeacher(memberId) ||
                courseParams.isOrganCounselor() ||
                isOnlineCounselor){
            // 如果是机构辅导老师
            // 如果是课程的老师
            // 并且是空中课堂的老师(空中课堂老师辅导老师身份处理)
            if(EmptyUtil.isNotEmpty(listener)){
                listener.route(true,tutorialTeacher);
                return;
            }
        }

        if(entity.getPrice() == 0){
            // 免费的
            if(entity.isJoin()){
                // 免费的已加入
                listener.route(true,tutorialTeacher);
            }else{
                // 免费的未加入
                listener.route(false,tutorialTeacher);
            }
        }else{
            // 收费的课程
            if(entity.isBuy()){
                // 已经购买
                if(entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_ALL ||
                        entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_CHAPTER){

                    if(entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_CHAPTER && isAuthorized && entity.isJoin()){
                        // 娃娃币章节，更新全本
                        CourseHelper.requestJoinInCourse(memberId, courseId, schoolId, classId, new DataSource.SucceedCallback<Boolean>() {
                            @Override
                            public void onDataLoaded(Boolean aBoolean) {
                                if(aBoolean){
                                    listener.route(true,tutorialTeacher);
                                }else{
                                    listener.route(false,tutorialTeacher);
                                }
                            }
                        });
                    }else{
                        // 娃娃币购买全本或者章节
                        if(entity.isJoin()){
                            // 已加入
                            listener.route(true,tutorialTeacher);
                        }else{
                            // 未加入
                            listener.route(false,tutorialTeacher);
                        }
                    }
                }else if(entity.getBuyType() == CourseRouteEntity.BUY_TYPE_AUTHORIZATION_ALL ||
                        entity.getBuyType() == CourseRouteEntity.BUY_TYPE_MONEY_CHAPTER_AUTHORIZATION_ALL){
                    // 激活码授权码全本 || 章节购买后授权全本
                    if(entity.isExpire()){
                        // 过期进入未加入
                        listener.route(false,tutorialTeacher);
                    }else{
                        if(entity.isJoin()){
                            // 已经加入
                            listener.route(true,tutorialTeacher);
                        }else{
                            listener.route(false,tutorialTeacher);
                        }
                    }
                }
            }else{
                // 未购买
                listener.route(false,tutorialTeacher);
            }
        }
    }

    /**
     * 机构学程进入的课程详情
     * @param entity 课程状态实体
     * @param courseId 课程Id
     * @param courseParams 课程核心参数
     * @param listener 回调对象
     */
    private void navigationClass(@NonNull CourseRouteEntity entity,
                                 @NonNull String courseId,
                                 @NonNull String memberId,
                                 CourseDetailParams courseParams,
                                 @NonNull INavigationListener listener,
                                 boolean tutorialTeacher){

        boolean isAuthorized = courseParams.isAuthorized();
        String schoolId = courseParams.getSchoolId();
        String classId = courseParams.getClassId();

        isOnlineCounselor = isOnlineCounselor(entity, isOnlineTeacher);

        // 判断是否是学程的老师身份
        if(entity.isCourseTeacher(memberId) ||
                courseParams.isClassParent() ||
                courseParams.isClassTeacher() ||
                isOnlineCounselor){

            // 如果是机构辅导老师
            // 如果是课程的老师
            // 并且是空中课堂的老师(空中课堂老师辅导老师身份处理)
            if(EmptyUtil.isNotEmpty(listener)){
                listener.route(true,tutorialTeacher);
                return;
            }
        }

        if(entity.getPrice() == 0){
            // 免费的课程
            if(!entity.isJoin()){
                // 未加入，调用免费加入课程接口
                // 班级学程加传schoolId,classId
                CourseHelper.requestJoinInCourse(courseId,
                        0,true,
                        schoolId,classId,new DataSource.SucceedCallback<Boolean>(){
                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        // 可能静默加入失败
                        listener.route(aBoolean,tutorialTeacher);
                    }
                });
            }else{
                listener.route(true,tutorialTeacher);
            }
        }else{
            // 收费的课程
            if(isAuthorized){
                if(entity.isBuyAll() && !entity.isExpire()){
                    // 全部购买且未过期
                    if(entity.isJoin()){
                        listener.route(true,tutorialTeacher);
                    }else{
                        CourseHelper.requestReJoinInCourse(courseId,true,new DataSource.SucceedCallback<Boolean>(){
                            @Override
                            public void onDataLoaded(Boolean aBoolean) {
                                // 可能静默重新加入失败
                                listener.route(aBoolean,tutorialTeacher);
                            }
                        });
                    }
                }else{
                    // 未全部购买或过期 更新全本
                    CourseHelper.requestJoinInCourse(memberId, courseId, schoolId, classId, new DataSource.SucceedCallback<Boolean>() {
                        @Override
                        public void onDataLoaded(Boolean aBoolean) {
                            if(aBoolean){
                                listener.route(true,tutorialTeacher);
                            }else{
                                listener.route(false,tutorialTeacher);
                            }
                        }
                    });
                }
            }else{
                // 未授权
                if(entity.isBuyAll() && !entity.isExpire()){
                    // 全部购买并且未过期
                    if(entity.isJoin()){
                        listener.route(true,tutorialTeacher);
                    }else{
                        CourseHelper.requestReJoinInCourse(courseId,true,new DataSource.SucceedCallback<Boolean>(){
                            @Override
                            public void onDataLoaded(Boolean aBoolean) {
                                // 可能静默重新加入失败
                                listener.route(aBoolean,tutorialTeacher);
                            }
                        });
                    }
                }else{
                    // 加入到我的第一章
                    CourseHelper.requestJoinInCourse(memberId, courseId, schoolId, classId, new DataSource.SucceedCallback<Boolean>() {
                        @Override
                        public void onDataLoaded(Boolean aBoolean) {
                            if(aBoolean){
                                listener.route(true,tutorialTeacher);
                            }else{
                                listener.route(false,tutorialTeacher);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 内部调用
     * @param entity 课程信息实体
     * @param isOnlineTeacher 是否是空中课堂的老师
     * @return true 已经作为空中课堂老师处理了。
     */
    private boolean isOnlineCounselor(@NonNull CourseRouteEntity entity, boolean isOnlineTeacher){
        boolean isLearnPermission = (entity.getPrice() == 0 && entity.isJoin()) ||
                // 过期让进入课程详情 ，由课程详情强制退出
                (entity.getPrice() != 0 && entity.isBuy() && !entity.isExpire() && entity.isJoin());
        // 没有购买,并且是空中课堂老师
        boolean isOnlineCounselor = !isLearnPermission && isOnlineTeacher;
        return isOnlineCounselor;
    }

    /**
     * 判断跳转类型
     * @param params 课程详情参数
     * @return <p>{@link CourseDetailType}</p>
     */
    private int transferEnterType(@Nullable CourseDetailParams params){
        if(EmptyUtil.isEmpty(params)){
            // 从Mooc大厅进来的
            return CourseDetailType.COURSE_DETAIL_MOOC_ENTER;
        }else{
            if(params.isClassCourseEnter()){
                // 班级学程进来的
                return CourseDetailType.COURSE_DETAIL_CLASS_ENTER;
            }else{
                // 从机构学程进来的
                return CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER;
            }
        }
    }

    public void showLoadingDialog(Activity activity) {
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        }
        mLoadingDialog.setCancelable(false);
    }

    private void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 如果已经购买或者能参加学习，就是学生身份进去
     * @return true 空中课堂的老师作学程的辅导老师处理了。
     */
    public boolean isOnlineCounselor() {
        return isOnlineCounselor;
    }

    /**
     * 接口回调实现类
     */
    public static class NavigationListener implements INavigationListener{
        // 路由跳转，true，进入已加入详情页
        @Override
        public void route(boolean needToLearn){
            if(needToLearn){
                // 发送通知
                EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                // EventBus.getDefault().post(new EventWrapper(null,EventConstant.TRIGGER_UPDATE_COURSE));
            }
        }

        @Override
        public void route(boolean needToLearn, boolean isTutorialTeacher) {
            boolean tutorialMode = MainApplication.isTutorialMode();
            if(tutorialMode) {
                route(needToLearn && isTutorialTeacher);
            }else{
                route(needToLearn);
            }
        }
    }

    /**
     * 接口回调
     */
    public interface INavigationListener{
        // 路由跳转，true，进入已加入详情页
        void route(boolean needToLearn);

        void route(boolean needToLearn,boolean isTutorialTeacher);
    }

}
