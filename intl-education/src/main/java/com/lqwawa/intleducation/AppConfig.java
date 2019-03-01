package com.lqwawa.intleducation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;

import com.lqwawa.intleducation.base.utils.StringUtils;
import com.osastudio.apps.Config;

/**
 * Created by XChen on 2016/11/3.
 * email:man0fchina@foxmail.com
 */

public final class AppConfig {
    public AppConfig() {
    }

    public static final class CHAT {
        public static final String KEY_AVATA = "userAvatar";
        public static final String KEY_NICKNAME = "userNickname";
    }

    public static final int PAGE_SIZE = 24;

    public static final String WEIXIN_APPID = "wx8708a6401c83d49b";

    public static final class BaseConfig {
        public static final String KEY_ALLOW_4G = "allow4G";
        public static final String KEY_ALLOW_PRIVATE_MSG = "allowPrivateMsg";
        public static final String KEY_ALLOW_NOTICE = "allowNotice";
        public static final int[] ClassifybackColors = new int[]{//分类磁贴背景颜色 多于6个则循环
                Color.parseColor("#fdc300"),
                Color.parseColor("#95a3fc"),
                Color.parseColor("#98cf5a"),
                Color.parseColor("#fa9162"),
                Color.parseColor("#55d0e2"),
                Color.parseColor("#ed6492")};

        public static boolean needShowPay() {
            return getVersionCode() >= 320 || Config.DEBUG;
        }

        public static int getVersionCode() {
            Context context = MainApplication.getInstance().getApplicationContext();
            if (context != null) {
                try {
                    PackageInfo info = context.getPackageManager().getPackageInfo(
                            context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    String versionCode = String.valueOf(info.versionCode);
                    if (StringUtils.isIntString(versionCode)) {
                        return Integer.parseInt(versionCode);
                    } else {
                        return -1;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }

    public static final class ServerUrl {
        private static boolean ReleaseSever = !Config.DEBUG;
        public static final String WAWA_SERVER_TEST = "http://resop.lqwawa.com/";
        public static final String WAWA_SERVER_RELEASE = "http://mcourse.lqwawa.com/";
        public static final String WAWA_BASE_SERVER = ReleaseSever
                ? WAWA_SERVER_RELEASE : WAWA_SERVER_TEST;
        public static final String WEIKE_SERVER_TEST = "http://resop.lqwawa.com/kukewebservice/";
        public static final String WEIKE_SERVER_RELEASE = "http://mcourse.lqwawa.com/kukewebservice/";
        public static final String WEIKE_SERVER = ReleaseSever ? WEIKE_SERVER_RELEASE : WEIKE_SERVER_TEST;
        public static final String WAWATV_COURSE_DETAIL_URL = WEIKE_SERVER
                + "resource/getResourceDetailById?j=";
        public static final String COURSE_IMAGES_URL = WEIKE_SERVER
                + "resource/getCourseImageById?j=";
        public static final String IMG_ROOT_URL = WAWA_BASE_SERVER
                + "Files/Upload/Upload.aspx?UploadFiles/api/mobile/";

        public static String FILE_SERVER = ReleaseSever
                ? "http://file.lqwawa.com/"
                : "http://filetestop.lqwawa.com/";
        public static String FileSeverBase = FILE_SERVER + "UploadFiles/";

        public static final String LQWAWA_SERVER_RELEASE = "http://hdapi.lqwawa.com/";
        public static final String LQWAWA_SERVER_STAGING = "http://platformtestop.lqwawa.com/";
        public static final String LQWAWA_BASE_SERVER = ReleaseSever
                ? LQWAWA_SERVER_RELEASE : LQWAWA_SERVER_STAGING;

        //测试服务器
        public static final String URL_HOST_TEST = "http://resop.lqwawa.com";
        //正式服务器
        public static final String URL_HOST_RELEASE = "http://www.lqmooc.com";

        /**
         * ===========================================================================
         * 支付正式: http://pay.lqwawa.com/lqwawapay/
         * 模拟：http://pay.lqwawa.com/lqwawapaytest/
         * ===========================================================================
         */
        public static final String PayBase_Release = "http://pay.lqwawa.com/lqwawapay/";

        public static final String PayBase_Test = "http://pay.lqwawa.com/lqwawapaytest/";

        public static final String PayBase = ReleaseSever ? PayBase_Release : PayBase_Test;


        //当前使用接口服务器设置
        public static final String URL_HOST = ReleaseSever ? URL_HOST_RELEASE : URL_HOST_TEST;
        //上传图片 //test
        public static final String UploadImg_Test =
                "http://intlstudy.inoot.cn/uploadservice/interMaterial/uploadImg?j=";
        //上传图片 //relese
        public static final String UploadImg_Release =
                "http://lqwwupload.lqwawa.com:8080/uploadservice/interMaterial/uploadImg?j=";

        public static final String UploadImg = ReleaseSever ? UploadImg_Release : UploadImg_Test;
        //接口基础地址 测试环境+interCourse
        // @date   :2018/5/4 0004 下午 6:06
        // @func   :V5.5的坑
        public static final String ServerBase = ReleaseSever ? URL_HOST +"/": URL_HOST + "/interCourse/";
        //登录接口
        public static final String Login = ServerBase + "api/user/login?j=";
        //退出登录
        public static final String Logout = ServerBase + "api/user/logout?j=";
        //获取banner图像list
        public static final String GetBanners = ServerBase + "api/common/getPictureList?j=";
        //获取分类列表
        public static final String GetClassList = ServerBase + "api/course/getClassificationList?j=";
        //获取分类列表 老接口
        //public static final String GetConfigList = ServerBase + "api/course/getConfigList?j=";
        //获取分类列表 新接口
        public static final String GetConfigList = ServerBase + "api/config/getConfigList?j=";
        //获取基础课程分类列表
        public static final String GetNewBasicsConfigList = ServerBase + " api/course/getBasicCourseConfigList?j=";
        //获取热门推荐/最近更新/入驻机构
        public static final String GetDiscoveryItemList =
                ServerBase + "api/course/getCourseOrganList?j=";
        //入驻机构列表
        public static final String GetOrganList = ServerBase + "api/course/getOrganList?j=";
        //获取机构的课程列表/老师列表/证书列表
        public static final String GetOrganItemList =
                ServerBase + "api/course/getDataListByOrganId?j=";
        //获取更多课程列表
        public static final String GetCourseList =
                ServerBase + "api/course/getCourseListBySearch?j=";
        // 获取课程状态的接口
        public static final String GetCourseStatus =
                ServerBase + "api/courseEx/getCourseStatus?j=";
        // 加入课程的接口
        public static final String GetJoinInCourse =
                ServerBase + "api/courseEx/joinInCourse?j=";

        //获取课程详情(课程介绍，章节信息，评论回复)
        public static final String GetCourseDetailsById =
                ServerBase + "api/course/getCourseById?j=";
        //获取未购买课程的详细信息
        public static final String GetNotPurchasedChapter =
                ServerBase + "api/buy/getNotPurchasedChapter?j=";
        //获取老师详情/老师的课程/老师的资源
        public static final String GetTeacherDetailsById =
                ServerBase + "api/course/getTeacherCourseById?j=";
        //获取认证证书的详情
        public static final String GetCredentiialDetails =
                ServerBase + "api/course/getCertificationDetail?j=";
        //添加评论或回复评论
        public static final String AddCommentOrReply =
                ServerBase + "api/course/addComment?j=";
        //我开设的课程列表
        public static final String GetMyEstablishCourseList =
                ServerBase + "api/course/getTeacherCourseList?j=";
        //我的课程列表
        public static final String GetMyCourseList =
                ServerBase + "api/student/courseList?j=";
        //我的课程列表
        public static final String GetMyCertificateList =
                ServerBase + "api/student/certificate/list?j=";

        //获取我的课程详情
        public static final String GetCourseDetail =
                ServerBase + "api/student/courseDetail?j=";
        //课程通告列表
        public static final String GetCourseNoticesList =
                ServerBase + "api/student/courseNoticeList?j=";
        //课程章节列表
        public static final String GetCourseChapterList =
                ServerBase + "api/student/courseChapterList?j=";
        //获取作业/考试列表
        public static final String GetCourseHomeworkOrExamList =
                ServerBase + "api/student/courseExamList?j=";
        //老师获取学生提交的考试列表
        public static final String GetExamCommitList =
                ServerBase + "/api/courseExam/examCommitList?j=";
        //获取个人信息
        public static final String GetPersonalInfo =
                ServerBase + "api/user/getPersonInfo?j=";
        //更新个人信息
        public static final String UpdatePersonalInfo =
                ServerBase + "api/user/updatePersonInfo?j=";
        //获取关注的学校列表
        public static final String GetSchonlsOfConcernList =
                ServerBase + "api/message/getMySchoolList?j=";
        //获取省份&城市列表
        public static final String GetAreaList =
                ServerBase + "api/user/getAreaList";
        //个人通讯录
        public static final String GetPersonalContactsList =
                ServerBase + "api/user/getFriendList?j=";
        //班级通讯录
        public static final String GetClassContactsList =
                ServerBase + "api/user/getClassList?j=";
        //班级通讯录
        public static final String GetSchoolsOfConcern =
                ServerBase + "api/message/getMySchoolList?j=";

        //我的收藏列表
        public static final String GetMyCollectionList =
                ServerBase + "api/course/getMyCollectList?j=";
        //我的收藏列表
        public static final String GetNewFriendList =
                ServerBase + "api/message/getFriendRequest?j=";

        //处理好友请求
        public static final String DealFriendRequest =
                ServerBase + "api/user/dealFriendRequest?j=";
        //发送好友请求
        public static final String SendFriendRequest =
                ServerBase + "api/user/sendFriendRequest?j=";
        //搜索全平台用户
        public static final String SearchUser =
                ServerBase + "api/user/searchUser?j=";
        //收藏课程
        public static final String collectCourse =
                ServerBase + "api/course/collectCourseById?j=";
        //取消收藏课程
        public static final String deleteCollectCourse =
                ServerBase + "api/course/deleteCollectCourseById?j=";

        //成为老师的学生
        public static final String FollowTheTeacher =
                ServerBase + "api/message/addTeacherToFriend?j=";
        //关注机构
        public static final String AddAttention =
                ServerBase + "api/message/addAttention?j=";
        //点赞评论
        public static final String AddPraiseByCommentId =
                ServerBase + "api/course/addPraiseByCommentId?j=";
        //获取个人信息
        public static final String GetUserInfoById =
                ServerBase + "api/user/getUserInfo?j=";
        //获取班级人员列表
        public static final String GetClassMembersById =
                ServerBase + "api/user/getUserListClassId?j=";
        //加入证书计划
        public static final String AddCertificationPlan =
                ServerBase + "api/course/addCertificationPlan?j=";
        //申请认证
        public static final String ApplyCertification =
                ServerBase + "api/course/applyCertification?j=";
        //全平台搜索机构
        public static final String SearchOrgan =
                ServerBase + "api/user/searchOrgan?j=";
        //删除好友
        public static final String DeleteFriend =
                ServerBase + "api/message/deleteFriend?j=";
        //退出班级
        public static final String ExitGroup =
                ServerBase + "api/message/exitGroup?j=";
        //根据uuid获取群组信息
        public static final String GetGroupListByUuids =
                ServerBase + "api/user/getGroupListByUuids?j=";
        //根据hxid获取个人信息
        public static final String GetUserListByIds =
                ServerBase + "api/user/getUserListByIds?j=";
        //设置资源已读
        public static final String setReaded =
                ServerBase + "api/student/course/setReaded?j=";
        //设置资源已读
        public static final String courseChapterList =
                ServerBase + "api/student/courseChapterList?j=";
        //设置资源已读
        public static final String courseExamDetail =
                ServerBase + "api/student/courseExamDetail?j=";
        //获取题目
        public static final String cExamExerList =
                ServerBase + "api/student/cExamExerList?j=";
        //提交答案
        public static final String userExamSave =
                ServerBase + "api/student/userExamSave?j=";
        //老师完成批阅
        public static final String userExamMark =
                ServerBase + "api/courseExam/userExamMark?j=";
        //获取证书课程列表
        public static final String GetCertificateCoutseList =
                ServerBase + "api/student/certificate/courseList?j=";
        //获取验证码
        public static final String getVerificationCode =
                ServerBase + "api/user/getVerificationCode?j=";
        //重置密码
        public static final String retrievePassword =
                ServerBase + "api/user/retrievePassword?j=";
        //退出课程
        public static final String courseDelete =
                ServerBase + "api/student/courseDelete?j=";

        //获取通知列表
        public static final String getNoticeList =
                ServerBase + "api/message/getNoticeList?j=";
        //处理通知
        public static final String dealNotice =
                ServerBase + "api/message/dealNotice?j=";
        //取消关注
        public static final String cancelAttention =
                ServerBase + "api/message/cancelAttention?j=";
        //参加课程
        public static final String joinInCourse =
                ServerBase + "api/course/joinInCourse?j=";
        //重新参加课程
        public static final String reJoinInCourse =
                ServerBase + "api/course/recoverCourse?j=";
        //获取年龄学段
        public static final String getLearningSegmentAgeList =
                ServerBase + "api/course/getLearningSegmentAgeList";
        //提交订单
        public static final String createOrder =
                ServerBase + "api/user/createOrder?j=";
        //删除证书
        public static final String deleteCertification =
                ServerBase + "api/course/deleteCertification?j=";
        //根据任务单id获取章节列表
        public static final String getChapterListByResId =
                ServerBase + "api/course/getChapterListByResId?j=";
        //上传资源
        public static final String uploadAndCreate = ReleaseSever ?
                "http://lqwwupload.lqwawa.com/uploadservice/resource/uploadAndCreate?j="
                : "http://resop.lqwawa.com/uploadservice/resource/uploadAndCreate?j=";
        //提交任务单
        public static final String userTaskSave =
                ServerBase + "api/student/userTaskSave?j=";
        //获取课程小节详情
        public static final String courseSectionDetail =
                ServerBase + "api/student/courseSectionDetail?j=";

        //老师拉取课程的单元测试/考试列表 不验证token
        public static final String getCourseExamList =
                ServerBase + "api/teacher/lqwawa/courseExamList?j=";
        //老师拉取课程考试题目 不验证token
        public static final String getCourseExamItemsList =
                ServerBase + "api/teacher/lqwawa/cExamExerList?j=";

        //获取节详情中的复述微课/任务单的详情 包含自己提交的记录列表
        public static final String cwareCommitList =
                ServerBase + "api/student/cwareCommitList?j=";

        //获取节详情中的听说课/任务单的详情 包含自己提交的记录列表
        public static final String GetCommittedTask =
                LQWAWA_BASE_SERVER + "/Api/Mobile/ST/CommitTask/GetCommittedTaskByTaskId";
        // 删除学习任务
        public static final String PostRequstCommittedTask =
                LQWAWA_BASE_SERVER + "/Api/Mobile/ST/CommitTask/RemoveCommitTask";

        //获取节详情中的听说课/任务单的详情 包含自己提交的记录列表 新接口
        public static final String GetNewCommittedTask =
                LQWAWA_BASE_SERVER + "/Api/Mobile/ST/CommitTaskOnline/LoadOnlineCommittedTaskByTaskId";

        // 点击听说课/读写单 分发任务
        public static final String DispatchStudentTask =
                LQWAWA_BASE_SERVER + "/NewApi/JavaResource/MoocStudentUpdateReadTask";

        //根据任务id拉取对该任务的所有评论
        public static final String GET_TASK_COMMENT_LIST_URL =
                LQWAWA_BASE_SERVER + "Api/mobile/ST/TaskComment/GetCommentList";
        //根据任务id获取任务详情 此处只用于获取任务的评论数量
        public static final String GET_COMMITTED_TASK_BY_TASK_ID_URL =
                LQWAWA_BASE_SERVER + "api/mobile/ST/CommitTask/GetCommittedTaskByTaskId";
        //评论/回复任务单
        public static final String LQWW_TASK_COMMENT =
                LQWAWA_BASE_SERVER + "api/mobile/ST/TaskComment/AddTaskComment";
        //点赞
        public static final String LQWW_TASK_ADD_PRAISE =
                LQWAWA_BASE_SERVER + "api/mobile/ST/TaskComment/AddPraiseCount";
        //根据家长id获取某班/全部孩子列表
        public static final String LQWW_GET_STUDENT_BY_PARENT =
                LQWAWA_BASE_SERVER + "Api/Mobile/ST/TaskDetail/GetStudentByParent";
        //获取机构的精品课程VIP权限列表
        public static final String getHQCPermissionsListBySchoolId =
                ServerBase + "api/course/getConfigLimitBySchoolId?j=";
        //获取LQ精品课程列表
        public static final String getCourseListByOrganId =
                ServerBase + "api/course/getCourseListByOrganId?j=";
        //检查是否有机构课程授权
        public static final String checkAuthorization =
                ServerBase + "api/activationCode/checkAuthorization?j=";
        //提交授权码
        public static final String commitAuthorizationCode =
                ServerBase + "api/activationCode/saveAuthorization?j=";
        //表演学习课程列表接口
        public static final String getByktCourseList =
                ServerBase + "api/course/getByktCourseList?j=";


        /**
         * ===========================================================================
         * 订单支付业务
         * ===========================================================================
         */
        //提交订单
        public static final String COMMIT_ORDER =
                PayBase + "api/order/commitOrder?j=";

        //获取订单支付字符串(GET)
        public static final String GET_ORDERINFO =
                PayBase + "api/order/getOrderInfo?j=";

        //充值界面获取订单支付字符串(GET)
        public static final String GET_CHARGE_ORDER =
                PayBase + "api/pay/getCoinOrderInfo?j=";

        //我的订单列表
        public static final String GetMyOrderList =
                ServerBase + "api/order/getMyOrderList?j=";
        //删除订单
        public static final String DeleteOrderById =
                PayBase + "api/order/deleteOrderById?j=";

        //取消订单
        public static final String CancelOrderById =
                PayBase + "api/order/cancelOrderById?j=";

        //激活码支付(GET)
        public static final String PAY_USEACTIVATIONCODE =
                PayBase + "api/order/payUseActivationCode?j=";


        public static final String CHARGE_BASE = ReleaseSever ? "http://www.lqmooc.com" : "http://resop.lqwawa.com/interCourse";


        //蛙蛙币余额
        public static String GET_USER_COINS_COUNT = CHARGE_BASE + "/api/wacoin/myWacoin?j=";

        //娃娃币支付
        public static String PAY_USE_WAWA_COIN = PayBase + "api/order/payUseWawaCoin?j=";


        public static String GET_COINS_DETAIL = CHARGE_BASE + "/api/wacoin/voucherList?j=";

        //获取充值常用数量
        public static String GET_CHARGE_NORMAL_COUNT = PayBase + "api/rule/getPageCoinRuleList?j=";

        //获取充值预支付信息
        public static String GET_CHARGE_PRE_INFO = PayBase + "api/pay/aliPayCoinApp?j=";

        //充值获取微信支付请求参数
        public static String GET_WXPAY_REQEUST_PARAMS = PayBase + "api/wxpay/generateCoinPrepayOrder?j=";

        //购买课程直播获取微信支付请求参数
        public static String GET_WXPAY_BUY_PARAMS = PayBase + "api/wxpay/generatePrepayOrder?j=";


        /**
         * ===========================================================================
         * <p>
         * ===========================================================================
         */
        //助教工作台预览地址
        public static final String ASSISTANT_DESK =
                ServerBase + "teacher/eduHelp/toEduHelpViewPage?";


        /**
         * ===========================================================================
         * ©国际课程接口
         * 所有接口都为GET请求
         * 内网测试地址 http://192.168.99.181:8080/interCourse
         * 外网测试地址http://intlstudy.inoot.cn
         * 图像上传内网地址 http://192.168.99.181:8080/uploadservice
         * 图像上传外网地址http://intlstudy.inoot.cn/uploadservice
         * ===========================================================================
         */

        public static final String LiveBase_Release = "http://intlstudy.inoot.cn/";

        public static final String LiveBase_Test = "http://192.168.99.181:8080/interCourse/";

        public static final String LiveBase = ReleaseSever ? LiveBase_Release : LiveBase_Test;


        //直播列表
        public static final String GetLiveList =
                ServerBase + "api/live/liveList?j=";


        //机构列表
        public static final String GetSubscribeNoList =
                LQWAWA_BASE_SERVER + "api/mobile/SubscribeNo/SubscribeNo/SubscribeNo/GetSubscribeNoList";

        //获取直播详情
        public static final String GetLiveDetails =
                ServerBase + "/api/live/liveInfo?j=";

        //获取直播资源
        public static final String GetLiveResList =
                ServerBase + "/api/live/liveRes?j=";
        //加入我的直播
        public static final String AddToMyLive =
                ServerBase + "/api/live/liveJoin?j=";
        //获取课程节直播列表
        public static final String GetCourseChapterLiveList =
                ServerBase + "/api/live/getCourseChapterLiveList?j=";
        //获取我的直播列表
        public static final String GetMyLiveList =
                ServerBase + "/api/live/myLiveList?j=";
        //删除直播
        public static final String DeleteLive =
                ServerBase + "/api/live/deleteLive?j=";
        //提交直播资源
        public static final String CommitLiveRes =
                ServerBase + "/api/live/setReaded?j=";
        //获取直播资源提交列表
        public static final String GetLiveResCommitList =
                ServerBase + "/api/live/cwareCommitList?j=";

        //获取课程表的日历标记
        public static final String GetDateSignForAirClass =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/GetDateSignForAirClass";

        //获取我的直播下的课程表的的日历标记
        public static final String GetDateSignForMyLive =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/GetDateSignForMyLive";

        //获取课程下的直播课程表的日历标记
        public static final String GetDateSignForCourseLive =
                ServerBase + "/api/live/getDateSignForCourseLive?j=";

        //我的直播列表接口
        public static final String WAWA_GetMyLiveList =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/GetMyLiveList";

        //我的学程课程表日期标记
        public static final String getDateSignByMemberId =
                ServerBase + "api/live/getDateSignByMemberId?j=";

        //我的学程直播列表
        public static final String getMyCourseLiveList =
                ServerBase + "api/live/getMyCourseLiveList?j=";

        //两栖蛙蛙接口 删除我创建的直播
        public static final String WAWA_DeleteAirClassNew =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/DeleteAirClassNew";

        //两栖蛙蛙接口 退出我加入的直播
        public static final String WAWA_DeleteMyLive =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/DeleteMyLive";

        //获取单个课程的学习进度
        public static final String getCourseLearningProgress =
                ServerBase + "api/course/getCourseLearningProgress?j=";

        //获取学程是否绑定到班级
        public static final String GetCourseIsBindClass =
                ServerBase + "api/class/isBindClass?j=";

        //查询会员在指定学校的所有角色
        public static final String WAWA_GetMemberRolesInSchool =
                LQWAWA_BASE_SERVER + "Api/Mobile/WaWatong/ClassMailList/ClassMailList/GetMemberRolesInSchool";

        //学生考试任务单批阅之后 提交的更新MOOC批阅的接口
        public static final String GET_USER_EXAM_MARK_TASK_URL = ServerBase + "api/courseExam/userExamMarkForTask";


        /**
         * ========================
         * 机构信息的相关接口
         * ========================
         */
        public static final String PostRequestSchoolInfoUrl =
                LQWAWA_BASE_SERVER + "api/mobile/SubscribeNo/School/School/LoadSchool";
        // 获取机构星级
        public static final String GetRequestSchoolStar =
                ServerBase + "api/organ/getOrganStarLevel?j=";

        /**
         * ========================
         * 空中课堂所有接口
         * ========================
         */
        // 某机构的空中课堂列表
        public static final String GetOnlineCourseDataInSchool =
                ServerBase + "api/online/getOnlineCourseList?j=";
        // 课程关联的空中课堂列表
        public static final String GetOnlineClassDataByCourseId =
                ServerBase + "api/course/getCourseOnlineByCourseId?j=";

        // 参加在线免费班级接口
        public static final String GetJoinInOnlineGratisCourse =
                ServerBase + "api/online/joinInOnlineCourse?j=";

        // 获取空中课堂班级信息
        public static final String PostLoadClassInfo =
                LQWAWA_BASE_SERVER + "Api/AmWaWa/SchoolLife/Class/ClassInfo/LoadClassInfo";

        // 获取空中课堂详情信息
        public static final String GetClassDetailInfo =
                ServerBase + "api/online/getOnlineCourseDetail?j=";

        // 空中课堂班级详情列表课堂简介图文混排地址
        public static final String WebViewOnlineClassDetailIntroduction =
                ServerBase + "api/online/intro?id={id}";

        // 拉取空中课堂列表
        public static final String PostOnlineClassAirLiveData =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/GetAirClassList";

        // 删除空中课堂直播
        public static final String PostOnlineClassDeleteLive =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/DeleteAirClassNew";

        //判断直播有没有加入我的直播中
        public static String PostJudgeJoinLive =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/CheckIsInMyLive";

        // 删除空中课堂通知
        public static final String PostOnlineClassDeleteNotification =
                LQWAWA_BASE_SERVER + "API/AmWaWa/SchoolLife/ClassRoom/ClassRoomDetaile/DeletedRoom";

        // 获取在线班级评论数据
        public static final String GetOnlineClassCommentData =
                ServerBase + "api/online/getCommentList?j=";

        // 提交空中课堂班级评论
        public static final String GetOnlineClassCommitComment =
                ServerBase + "api/online/addComment?j=";

        // 获取空中课堂班级详情的通知数据
        public static final String PostOnlineClassNotificationData =
                LQWAWA_BASE_SERVER + "Api/AmWaWa/PublishInfo/PublishInfo/PublishInfo/PublishList";

        // 机构主页获取片段数据
        public static final String GetOnlineSchoolInfoData =
                ServerBase + "api/course/getOrganCourseList?j=";

        // 获取机构老师信息
        public static final String PostOnlineSchoolTeacherData =
                LQWAWA_BASE_SERVER + "api/Mobile/School/Teacher/LoadSchoolTeacherMailList";
        // +机构关注
        public static final String PostAddSubscribeSchool =
                LQWAWA_BASE_SERVER + "api/Mobile/SubscribeNo/SubscribeNo/SubscribeNo/SaveSubscribeNo";
        // +机构关注
        public static final String PostRemoveSubscribeSchool =
                LQWAWA_BASE_SERVER + "api/Mobile/SubscribeNo/SubscribeNo/SubscribeNo/CancelSubscribeNo";

        // 空中课堂分享
        public static final String OnlineSchoolClassShareUrl =
                ServerBase + "courseOnlineShare?id={id}";

        // 学程详情分享
        public static final String CourseDetailShareUrl =
                ServerBase + "courseDetailShare?id={id}";

        // 学程标签分享
        public static final String CourseLabelShareUrl =
                ServerBase + "courseLabelShare?level={level}&parentId={parentId}&paramTwoId={paramTwoId}&paramThreeId={paramThreeId}";

        // 空中课堂根据班级Id获取关联的课程Id
        public static final String GetClassRelevanceCourse =
                ServerBase + "api/online/getCourseIdByClassId?j=";

        // 空中课堂生成二维码Url 分享
        public static final String QRCODE_SHARE_URL = LQWAWA_BASE_SERVER+
                "mobileHtml/ShareSchoolInfo.aspx?Id={id}";
        // public static final String QRCODE_SHARE_URL = "http://platformtestop.lqwawa.com/mobileHtml/ShareSchoolInfo.aspx?Id={id}";

        // 我的课程获取在线学习数据的URL
        public static final String GetMyOnlineCourseUrl = ServerBase + "api/online/getStuCourseOnlineList?j=";

        // 获取我开设的自主学习
        public static final String GetMyGiveOnlineCourseUrl = ServerBase + "api/online/getMyCourseList?j=";

        // 获取在线学习标签数据的URL
        public static final String GetOnlineStudyLabelUrl = ServerBase + "api/online/getLabelList?j=";
        // V5.11获取在线学习标签数据的URL
        public static final String GetNewOnlineStudyLabelUrl = ServerBase + "api/config/getOnlineConfigList?j=";

        // 获取在线学习机构等相关数据的URL
        public static final String GetOnlineStudyOrganUrl = ServerBase + "api/online/getDataList?j=";
        // 获取机构列表信息
        public static final String GetOnlineStudyMoreOrganUrl = ServerBase + "api/online/getOnlineOrganList?j=";
        // 完成授课
        public static final String GetCompleteOnlineClassGiveUrl = ServerBase + "api/online/finishCourseOnline?j=";

        // 获取老师学生正在授课和即将授课班级
        public static final String GetOnlineClassIds = ServerBase + "api/online/getOnlineClassIdList?j=";

        /**
         * ========================
         * 学程馆相关API
         * =======================
         */
        // 获取学程馆分类列表
        public static final String GetOrganCourseClassifyUrl = ServerBase + "api/organCourse/getClassificationAndCourse?j=";
        // 获取学程馆分类列表,科目设置
        public static final String GetOrganCourseClassifyResourceUrl = ServerBase + "api/organCourse/getTeacherSchoolCourseLabel?j=";
        // 获取学程馆分类标签
        public static final String GetOrganCourseClassifyLabelUrl = ServerBase + "api/organCourse/getOrganClassificationListBySearch?j=";
        // 获取学程馆所有分类标签
        public static final String GetOrganCourseAllClassifyLabelUrl = ServerBase + "api/organCourse/getOrganClassificationList?j=";
        // 获取学程馆课程
        public static final String GetOrganCourseListUrl = ServerBase + "api/organCourse/getCourseListBySearch?j=";
        // 获取学程馆课程 选择资源 老师科目已设置
        public static final String GetOrganCourseListResourceUrl = ServerBase + "api/organCourse/getTeacherSchoolCourseList?j=";
        // 检查订单的接口
        public static final String GetCheckOrder = PayBase + "api/order/checkOrder?j=";

        /**
         * ======================
         * 班级学程的Url
         * ======================
         */
        // 获取班级学程列表
        public static final String GetClassCourseListUrl = ServerBase +
                "api/class/getClassCourseList?j=";
        public static final String GetStudyTaskClassCourseListUrl = ServerBase +
                "api/class/getTeacherClassCourseList?j=";
        // 老师添加班级里的学程
        public static final String GetAddCourseFromClassUrl = ServerBase +
                "api/class/addClassCourse?j=";
        // 班主任删除班级学程
        public static final String GetDeleteCourseFromClassUrl = ServerBase +
                "api/class/deleteClassCourse?j=";
        // 学程指定学程所在班级
        public static final String GetAddCourseRelevanceClass = ServerBase +
                "api/student/class/bindClass?j=";

        // 获取在线班级列表课程表标记
        public static final String PostRequestAirClassTimeTableFlags =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/LoadDateSignForAirClassByClassIdList";

        // 获取在线班级指定时间的空中课堂
        public static final String PostRequestAirClassData =
                LQWAWA_BASE_SERVER + "NewApi/AirClass/LoadAirClassByClassIdList";

        // 根据ClassId获取空中课堂Id
        public static final String GetOnlineIdByCourseId =
                ServerBase + "api/online/getOnlineIdByClassId?j=";

        // 检查在线班级的状态 是否授课结束，或者历史班
        public static String PostCheckOnlineClassStatus = ServerBase + "api/online/getCourseOnlineStatus";

        // 获取两栖蛙蛙的真实姓名
        public static final String PostRealNameWithNickUrl = LQWAWA_BASE_SERVER + "Api/Mobile/Setting/PersonalInfo/PersonalInfo/CheckExistByNickName";

        // 获取两栖蛙蛙的用户信息
        public static final String PostLoadUserInfoWithUserId = LQWAWA_BASE_SERVER + "api/mobile/Setting/PersonalInfo/PersonalInfo/Load";

        // 检测是否可以购买课程的接口
        public static final String GetCheckCourseBuyUrl = PayBase + "api/order/checkCourseBuy?j=";

        // 读写单自动批阅拿答题卡信息的接口
        public static final String GetResourceDetailByIdUrl = WEIKE_SERVER + "resource/getResourceDetailById?j=";


        // 获取老师学生班级已选定标签
        public static final String GetSetupConfigDataUrl = ServerBase +
                "api/config/getSetUpConfigList?j=";

        // 获取老师学生班级已选定标签
        public static final String GetAssignConfigDataUrl = ServerBase +
                "api/config/getAssignConfigList?j=";

        // 保存老师选定的标签
        public static final String GetSaveTeacherConfigUrl = ServerBase +
                "api/teacher/saveTeacherConfig?j=";


        // 蛙蛙币转赠的接口
        public static final String GetWaWaGiveUrl = PayBase +
                "api/coin/giveawayWawaCoin?j=";

        // V5.13新添加的接口
        public static final String GetLQRmCourseUrl = ServerBase +
                "api/courseEx/getRmCourseList?j=";

        // 班级学程老师的章节接口
        public static final String GetClassTeacherChapterList = ServerBase +
                "api/class/courseChapterList?j=";

        // 新的热门数据接口
        public static final String GetHostCourseUrl = ServerBase +
                "api/courseEx/getHotCourseList?j=";

        // 获取国家课程的标签数据
        public static final String GetBasicCourseConfigDataUrl = ServerBase +
                "api/config/getGjConfigList?j=";

        // 获取小语种，国际，国家课程空中课堂的标签数据
        public static final String GetNewOnlineClassifyConfigDataUrl = ServerBase +
                "api/config/getZxCourseConfigList?j=";

        // 获取班级学习统计列表
        public static final String GetLearningStatisticsUserArrayUrl = ServerBase +
                "api/study/courseStatisticsUserList?j=";

        // 获取班级课程统计列表
        public static final String GetCourseStatisticsUserArrayUrl = ServerBase +
                "api/study/courseStatistics?j=";

        /**
         * V5.15相关接口
         */
        // 获取符合过滤条件的机构列表
        public static final String PostRequestFilterOrganUrl =
                LQWAWA_BASE_SERVER + "NewApi/DataStatisticsForOMS/LoadFilterSchoolList";

        // 获取省市区信息
        public static final String PostRequestLocationDataUrl =
                LQWAWA_BASE_SERVER + "Api/Platform/User/Apply/SchoolSettledApply/BindLocation";

        // 学生申请机构助教
        public static final String GetRequestApplyForTutor =
                ServerBase + "api/tutor/applyOrganTutor?j=";

        // 学生助教获取作业列表时间标记
        public static final String PostRequestWorkDateFlag =
                LQWAWA_BASE_SERVER + "Api/Mobile/ST/Assist/GetDateSignForAssist";

        // 学生助教拉取作业列表
        public static final String PostRequestWorkTaskList =
                LQWAWA_BASE_SERVER + "Api/Mobile/ST/Assist/GetAssistTaskList";

        // 助教拉取帮辅的学生列表
        public static final String PostRequestPullTutorialStudents =
                LQWAWA_BASE_SERVER + "Api/Mobile/ST/Assist/GetAssistStudentList";

        // 助教拉取帮辅机构
        public static final String PostRequestTutorialOrgan =
                LQWAWA_BASE_SERVER + "Api/Mobile/Setting/PersonalInfo/PersonalInfo/LoadMemberSchoolList";

        // 根据条件查询我帮辅的课程
        public static final String GetRequestTutorialCourses =
                ServerBase + "api/tutor/getMyTutorCourses?j=";

        // 学生查询我的帮辅老师列表
        public static final String GetRequestTutorialByStudentId =
                ServerBase + "api/tutor/getMyTutorsByStuId?j=";
    }
}
