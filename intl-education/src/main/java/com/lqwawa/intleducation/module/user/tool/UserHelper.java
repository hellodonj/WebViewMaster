package com.lqwawa.intleducation.module.user.tool;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.common.db.DbHelper;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.io.IOException;
import java.util.List;

/**
 * Created by XChen on 2016/11/18.
 * email:man0fchina@foxmail.com
 */

public class UserHelper {

    private static UserInfoVo userInfo = null;

    public static boolean isLogin() {
        return (getUserInfo() != null) && StringUtils.isValidString(getUserInfo().getToken());
    }

    public static void setUserInfo(UserInfoVo vo) {
        DbManager db = x.getDb(DbHelper.getDaoConfig());
        userInfo = vo;
        setLastAccount(vo.getAccount());
        try {
            db.delete(UserInfoVo.class, WhereBuilder.b("userId", "=", userInfo.getId()));
            db.save(vo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static UserInfoVo getUserInfo() {
        if (userInfo == null) {
            DbManager db = x.getDb(DbHelper.getDaoConfig());
            try {
                List<UserInfoVo> list = db.findAll(UserInfoVo.class);
                if (list != null && list.size() > 0) {
                    userInfo = list.get(0);
                    LogUtil.d("User", list.size() + "");
                } else {
                    return null;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }

    public static String getUserId() {
        if (getUserInfo() == null) {
            return "";
        } else {
            if (!getUserInfo().getUserId().isEmpty()) {
                return getUserInfo().getUserId();
            } else {
                return "";
            }
        }
    }
    public static String getUserName() {
        if (getUserInfo() == null) {
            return "";
        } else {
            if (!getUserInfo().getUserId().isEmpty()) {
                return getUserInfo().getUserName();
            } else {
                return "";
            }
        }
    }


    public static String getAccount() {
        if (getUserInfo() == null) {
            return "";
        } else {
            if (!getUserInfo().getAccount().isEmpty()) {
                return getUserInfo().getAccount();
            } else {
                return "";
            }
        }
    }

    public static String getLastAccount() {
        return SharedPreferencesHelper.getString(
                MainApplication.getInstance().getApplicationContext(),
                "lastAccount");
    }

    private static void setLastAccount(String value) {
        SharedPreferencesHelper.setString(
                MainApplication.getInstance().getApplicationContext(),
                "lastAccount", value);
    }

    public static void logout() {
        userInfo = null;
        DbManager db = x.getDb(DbHelper.getDaoConfig());
        try {
            db.delete(UserInfoVo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getUserListByIds(List<String> ids, Callback.CommonCallback<String> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("ids", StringUtils.join(ids, ","));
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetUserListByIds + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, callback);
    }

    public static void getGroupListByUuids(List<String> ids, Callback.CommonCallback<String> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("groupUuids", StringUtils.join(ids, ","));
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetGroupListByUuids + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, callback);
    }
    public static void getGroupListByUuid(String uuid, Callback.CommonCallback<String> callback) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("groupUuids", uuid);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetGroupListByUuids + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, callback);
    }

    /**
     * 判断用户是否是学生角色
     * @param roles
     * @return
     */
    public static boolean isStudent(String roles){
        if(StringUtils.isValidString(roles)){
            if(roles.contains("1")){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否是老师角色
     * @param roles
     * @return
     */
    public static boolean isTeacher(String roles){
        if(StringUtils.isValidString(roles)){
            if(roles.contains("0")){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否是家长角色
     * @param roles
     * @return
     */
    public static boolean isParent(String roles){
        if(StringUtils.isValidString(roles)){
            if(roles.contains("2")){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断课程的讲师，助教，辅导老师是否包含当前用户
     * @param courseVo 课程信息
     * @param isOnlineCounselor 只有用户没有购买过,并且是空中课堂的老师,才是辅导老师身份
     * @return true 老师身份
     */
    public static boolean checkCourseAuthor(CourseVo courseVo,boolean isOnlineCounselor) {
        String memberId = getUserId();
        if (courseVo != null && !TextUtils.isEmpty(memberId)) {
            //讲师中包含当前用户
            String teachersId = courseVo.getTeachersId();
            if (!TextUtils.isEmpty(teachersId) && teachersId.contains(memberId)) {
                return true;
            }
            //助教中包含当前用户
            String tutorId = courseVo.getTutorId();
            if (!TextUtils.isEmpty(tutorId) && tutorId.contains(memberId)) {
                return true;
            }
            //中包含当前用户
            String counselorId = courseVo.getCounselorId();
            if (!TextUtils.isEmpty(counselorId) && (counselorId.contains(memberId) || isOnlineCounselor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断空中课堂的讲师，助教，辅导老师是否包含当前用户
     * @param dataBean 空中课堂班级信息
     * @return true 老师身份
     */
    public static boolean checkOnlineCourseAuthor(ClassDetailEntity.DataBean dataBean) {
        String memberId = getUserId();
        if (dataBean != null && !TextUtils.isEmpty(memberId)) {
            //讲师中包含当前用户
            String teachersId = dataBean.getTeachersId();
            if (!TextUtils.isEmpty(teachersId) && teachersId.contains(memberId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据角色信息获取角色包装对象
     * @param roles 角色信息
     * @return 角色包装对象 OnlineClassRole
     */
    public static String getOnlineRoleWithUserRoles(@NonNull String roles) {
        // 默认学生身份
        String roleType = OnlineClassRole.ROLE_STUDENT;
        if(UserHelper.isTeacher(roles)){
            // 老师身份
            roleType = OnlineClassRole.ROLE_TEACHER;
        }else if(UserHelper.isParent(roles)){
            // 家长身份
            roleType = OnlineClassRole.ROLE_PARENT;
        }else if(UserHelper.isStudent(roles)){
            // 学生身份
            roleType = OnlineClassRole.ROLE_STUDENT;
        }
        return roleType;
    }

    /**
     * 仅适用mooc
     */
    public interface MoocRoleType {
        int STUDENT = 0; //学生
        int TEACHER = 1; //老师
        int PARENT = 2;  //家长

        int EDITOR = 3; //主编, 直播使用
    }

    /**
     * 定义MOOC老师类型
     */
    public interface TeacherType{
        // 默认的,说明不是老师
        int TEACHER_DEFAULT = 0;
        // 讲师
        int TEACHER_LECTURER = 1;
        // 助教
        int TEACHER_TUTOR = 2;
        // 辅导老师
        int TEACHER_COUNSELOR = 3;
    }

    /**
     * 判断课程的用户身份 用于客户端登录用户的身份判断
     * @param memberId memberId 如果不等于自己的memberId,那就说明传的是孩子的memberId。那么该身份就是家长
     * @param courseVo 课程信息
     * @return {{@link MoocRoleType#PARENT},{@link MoocRoleType#STUDENT},{@link MoocRoleType#STUDENT}
     */
    public static int getCourseAuthorRole(String memberId, CourseVo courseVo) {
        String curMemberId = getUserId();
        if (!TextUtils.isEmpty(curMemberId) && !TextUtils.isEmpty(memberId)
                && !curMemberId.equals(memberId)) {
            return MoocRoleType.PARENT;
        }
        if (checkCourseAuthor(courseVo,false)) {
            return MoocRoleType.TEACHER;
        }
        return MoocRoleType.STUDENT;
    }

    /**
     * 判断课程的用户身份 用于客户端登录用户的身份判断
     * @param memberId memberId 如果不等于自己的memberId,那就说明传的是孩子的memberId。那么该身份就是家长
     * @param courseVo 课程信息
     * @return {{@link MoocRoleType#PARENT},{@link MoocRoleType#STUDENT},{@link MoocRoleType#STUDENT}
     */
    public static int getCourseAuthorRole(String memberId, CourseVo courseVo,boolean isOnlineTeacher) {
        String curMemberId = getUserId();
        if (!TextUtils.isEmpty(curMemberId) && !TextUtils.isEmpty(memberId)
                && !curMemberId.equals(memberId)) {
            return MoocRoleType.PARENT;
        }
        if (checkCourseAuthor(courseVo,isOnlineTeacher)) {
            return MoocRoleType.TEACHER;
        }
        return MoocRoleType.STUDENT;
    }

    /**
     * 判断某个用户在课程中的身份 用于其他用户的身份判断
     * @param userId 用户Id
     * @param courseVo 课程实体
     * @return 身份信息
     */
    public static int getRoleWithCourse(@NonNull String userId,@NonNull CourseVo courseVo){
        if(checkCourseAuthorWithUserId(userId,courseVo)){
            return MoocRoleType.TEACHER;
        }
        return MoocRoleType.STUDENT;
    }

    /**
     * 判断某个用户是否是老师身份
     * @param userId 用户Id
     * @param courseVo 课程信息
     * @return true 老师身份
     */
    public static boolean checkCourseAuthorWithUserId(@NonNull String userId,@NonNull CourseVo courseVo) {
        String memberId = userId;
        if (courseVo != null && !TextUtils.isEmpty(memberId)) {
            // 讲师中包含当前用户
            String teachersId = courseVo.getTeachersId();
            if (!TextUtils.isEmpty(teachersId) && teachersId.contains(memberId)) {
                return true;
            }
            // 助教中包含当前用户
            String tutorId = courseVo.getTutorId();
            if (!TextUtils.isEmpty(tutorId) && tutorId.contains(memberId)) {
                return true;
            }
            // 辅导老师中包含当前用户
            String counselorId = courseVo.getCounselorId();
            if (!TextUtils.isEmpty(counselorId) && counselorId.contains(memberId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断当前登录用户在课程中,是否是讲师角色
     * @param courseVo 课程信息
     * @return true 讲师角色
     */
    public static boolean isCourseTeacher(CourseVo courseVo) {
        String memberId = getUserId();
        return isCourseTeacher(memberId,courseVo);
    }

    /**
     * 判断某个用户是否是老师身份
     * @param memberId 用户Id
     * @param courseVo 课程信息
     * @return true 讲师身份
     */
    private static boolean isCourseTeacher(@NonNull String memberId,@NonNull CourseVo courseVo){
        if (courseVo != null && !TextUtils.isEmpty(memberId)) {
            //讲师中包含当前用户
            String teachersId = courseVo.getTeachersId();
            if (!TextUtils.isEmpty(teachersId) && teachersId.contains(memberId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断当前登录用户在课程中,是否是助教角色
     * @param courseVo 课程信息
     * @return true 助教
     */
    public static boolean isCourseTutor(CourseVo courseVo) {
        String memberId = getUserId();
        return isCourseTutor(memberId,courseVo);
    }

    /**
     * 判断某个用户是否是助教身份
     * @param memberId 用户Id
     * @param courseVo 课程信息
     * @return true 助教身份
     */
    public static boolean isCourseTutor(@NonNull String memberId,@NonNull CourseVo courseVo){
        if (courseVo != null && !TextUtils.isEmpty(memberId)) {
            //讲师中包含当前用户
            String teachersId = courseVo.getTeachersId();
            if (!TextUtils.isEmpty(teachersId) && teachersId.contains(memberId)) {
                return false;
            }
            //助教中包含当前用户
            String tutorId = courseVo.getTutorId();
            if (!TextUtils.isEmpty(tutorId) && tutorId.contains(memberId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断当前登录用户在课程中,是否是辅导老师角色
     * @param courseVo 课程信息
     * @return true 辅导老师角色
     */
    public static boolean isCourseCounselor(CourseVo courseVo) {
        // 获取自己的memberId
        String memberId = getUserId();
        return isCourseCounselor(memberId,courseVo,false);
    }

    /**
     * 判断当前登录用户在课程中,是否是辅导老师角色
     * @param courseVo 课程信息
     * @param isOnlineTeacher 是否是空中课堂老师
     * @return true 辅导老师角色
     */
    public static boolean isCourseCounselor(CourseVo courseVo,boolean isOnlineTeacher) {
        // 获取自己的memberId
        String memberId = getUserId();
        return isCourseCounselor(memberId,courseVo,isOnlineTeacher);
    }

    /**
     * 判断当在课程中,是否是辅导老师角色
     * @param memberId 用户Id
     * @param courseVo 课程信息
     * @param isOnlineTeacher 是否是空中课堂老师
     * @return true 辅导老师角色
     */
    public static boolean isCourseCounselor(@NonNull String memberId,@NonNull CourseVo courseVo,boolean isOnlineTeacher){
        if (courseVo != null && !TextUtils.isEmpty(memberId)) {
            // 讲师中包含当前用户
            String teachersId = courseVo.getTeachersId();
            if (!TextUtils.isEmpty(teachersId) && teachersId.contains(memberId)) {
                return false;
            }
            // 助教中包含当前用户
            String tutorId = courseVo.getTutorId();
            if (!TextUtils.isEmpty(tutorId) && tutorId.contains(memberId)) {
                return false;
            }
            // 辅导老师中包含当前用户
            String counselorId = courseVo.getCounselorId();
            if (!TextUtils.isEmpty(counselorId) && (counselorId.contains(memberId) || isOnlineTeacher)) {
                return true;
            }
        }

        return false;
    }

    /**
     * KEY_WATCH_COURSE = 9; 看课件
     * KEY_RELL_COURSE = 5;复述课件
     * KEY_TASK_ORDER = 8;任务单
     * KEY_TEXT_BOOK = 14;看课本 // V5.14兼容Q配音的类型
     *
     *
     * @param taskType mooc学习任务类型
     * @return
     */
    public static int transferResourceTypeWithMooc(int taskType){
        if(taskType == 1){
            return 14;
        }else if(taskType == 2){
            return 5;
        }else if(taskType == 3){
            return 8;
        }else if(taskType == 4){
            return 9;
        }else if(taskType == 5){
            return 5;
        }
    }

   
}
