package com.galaxyschool.app.wawaschool.config;

import com.osastudio.apps.Config;

/**
 * @author 作者 shouyi:
 * @version 创建时间：Mar 30, 2015 5:10:25 PM 类说明
 */
public class ServerUrl {
    public static String VER_RELEASE = "release";
    public static String VER_SIMULATE = "simulate";

    public static String VERSION;
    public static String BASE_SERVER;
    public static String FILE_SERVER;
    public static String WEIKE_SERVER;
    public static String WEIKE_UPLOAD_SERVER;
    public static String WEIKE_UPLOAD_BASE_SERVER;
    public static String WEIKE_UPLOAD_CONFIG_SERVER;
    public static String ORIGNAL_SHOW_SERVER;
    public static String SHARE_BASE_SERVER;
    public static String NOC_BASE_SERVER;
    public static String LQMOOC_BASE_SERVER;
    public static String BASE_SHARE_URL;
    /**
     * 天赋密码
     */
    public static String TALENT_BASE_SERVER;

    public static String PRINCIPAL_ASSISTANT_BASE_URL;//校长助手地址
    public static String NUTRITION_RECIPES_BASE_URL;//营养膳食地址
    public static String STUDY_RECORD_BASE_URL;//学习记录的url

    static {
        if (Config.DEBUG) {
            VERSION = VER_SIMULATE;
            BASE_SERVER = "http://platformtestop.lqwawa.com/";
            BASE_SHARE_URL = "http://platformtestop.lqwawa.com/";
//            BASE_SERVER = "http://platformop.lqwawa.com/";
            FILE_SERVER = "http://filetestop.lqwawa.com/";
            WEIKE_SERVER = "http://resop.lqwawa.com/kukewebservice/";
            WEIKE_UPLOAD_SERVER = "http://resop.lqwawa.com/uploadservice/";
            WEIKE_UPLOAD_BASE_SERVER = "http://resop.lqwawa.com";
            WEIKE_UPLOAD_CONFIG_SERVER = "%s/uploadservice/";
            ORIGNAL_SHOW_SERVER = "http://wwcxop.lqwawa.com/";
            SHARE_BASE_SERVER = "http://resop.lqwawa.com";
            NOC_BASE_SERVER = "http://resop.lqwawa.com/noc/";
            LQMOOC_BASE_SERVER = "http://resop.lqwawa.com/interCourse/";
            //模拟
            PRINCIPAL_ASSISTANT_BASE_URL = "http://118.190.63.199:8090/";
            //模拟(营养膳食)
            NUTRITION_RECIPES_BASE_URL = "http://webapptest.lqwawa.com/";
            TALENT_BASE_SERVER = "http://resop.lqwawa.com/fingerprintDetection/";
            STUDY_RECORD_BASE_URL = "http://118.190.63.199:8024/#/";
        } else {
            VERSION = VER_RELEASE;
            BASE_SERVER = "http://hdapi.lqwawa.com/";
            BASE_SHARE_URL = "http://www.lqwawa.com/";
            FILE_SERVER = "http://file.lqwawa.com/";
            WEIKE_SERVER = "http://mcourse.lqwawa.com/kukewebservice/";
            WEIKE_UPLOAD_SERVER = "http://lqwwupload.lqwawa.com/uploadservice/";
            WEIKE_UPLOAD_BASE_SERVER = "http://lqwwupload.lqwawa.com";
            WEIKE_UPLOAD_CONFIG_SERVER = "%s/uploadservice/";
            ORIGNAL_SHOW_SERVER = "http://wwcx.lqwawa.com/";
            SHARE_BASE_SERVER = "http://mcourse.lqwawa.com";
            NOC_BASE_SERVER = "http://noc.lqwawa.com/";
            LQMOOC_BASE_SERVER = "https://api.lqmooc.com/";
            //正式
            PRINCIPAL_ASSISTANT_BASE_URL = "http://schoolmaster.lqwawa.com/";
            //正式（营养膳食）
            NUTRITION_RECIPES_BASE_URL = "http://webapp.lqwawa.com/";
            TALENT_BASE_SERVER = "http://talentcode.lqwawa.com/";
            STUDY_RECORD_BASE_URL = "http://studenthelper.lqwawa.com/#/";
        }
    }

    public static String SERVER = BASE_SERVER + "api/mobile/";
    public static String NEW_SERVER = BASE_SERVER + "API/AmWaWa/";
    public static String UPLOAD_BASE_URL = FILE_SERVER + "Files/Upload/Upload.aspx?";
    public static String WEB_VIEW_NEW_URL = BASE_SHARE_URL + "mobileHtml/DetailView.aspx";
    public static String IMG_ROOT_URL = FILE_SERVER + "UploadFiles/";

    // recommend app
    public static String RECOMMEND_APP_URL = BASE_SHARE_URL + "mobileHtml/app_download.aspx";
    // login
    public static String LOGIN_URL = SERVER + "Setting/Login/Login/Login";
    // get back password
    public static String GETBACK_PASSWORD_URL = SERVER + "Setting/Password/Password/Find";
    // modify password
    public static String MODIFY_PASSWORD_URL = SERVER + "Setting/Password/Password/Change";
    // register
    public static String REGISTER_URL = SERVER + "Setting/Regedit/Regedit/Save";
    // Load user info
    public static String LOAD_USERINFO_URL = SERVER
            + "Setting/PersonalInfo/PersonalInfo/Load";
    // update user info
    public static String SAVE_USERINFO_URL = SERVER
            + "Setting/PersonalInfo/PersonalInfo/Save";
    // Load info
    public static String SAVE_FEEDBACK_URL = SERVER + "Setting/FanKui/FanKui/Save";
    // reset password
    public static String RESET_PASSWORD_URL = SERVER
            + "Setting/Password/Password/ResetPassWord";
    // retrieve pw by phone
    public static String SMS_GET_VER_CODE_URL = SERVER
            + "WaWatong/InstantChat/SMSVerification/SendSMSVerification";
    // send sms change pw
    public static String SMS_CHANGE_PW_URL = SERVER
            + "WaWatong/InstantChat/SMSVerification/VerificationCode";

    // load role info
    public static String LOAD_ROLEINFOLIST_URL = SERVER
            + "Setting/SchoolApply/SchoolApply/Search";
    // load teacher role info
    public static String LOAD_TEACHER_ROLEINFO_URL = SERVER
            + "Setting/SchoolApply/TeacherApply/Load";
    // save teacher role info
    public static String SAVE_TEACHER_ROLEINFO_URL = SERVER
            + "Setting/SchoolApply/TeacherApply/Save";
    // load student role info
    public static String LOAD_STUDENT_ROLEINFO_URL = SERVER
            + "Setting/SchoolApply/StudentApply/Load";
    // save student role info
    public static String SAVE_STUDENT_ROLEINFO_URL = SERVER
            + "Setting/SchoolApply/StudentApply/Save";

    // load relation list
    public static String LOAD_RELATIONINFOLIST_URL = SERVER
            + "Setting/Relation/Relation/Search";
    // save relation info
    public static String SAVE_RELATIONINFO_URL = SERVER + "Setting/Relation/Relation/Save";
    // check relation info
    public static String CHECK_RELATIONINFO_URL = SERVER + "Setting/Relation/Relation/Check";

    // load all school list
    public static String LOAD_ALL_SCHOOLLIST_URL = SERVER + "School/School/School/Search";
    // load all school list
    public static String LOAD_GRADELIST_URL = SERVER + "School/Grade/Grade/Search";
    // load all school list
    public static String LOAD_CLASSLIST_URL = SERVER + "School/Class/Class/Search";

    public static String GET_PUSH_MESSAGE_LIST_URL = SERVER
            + "WaWatong/InstantChat/GetPushService/GetPushService";
    public static String GET_MY_MESSAGE_LIST_URL = NEW_SERVER
            + "PersonalSpace/Message/MessageList/GetMessageList";
    public static String GET_CLASS_MESSAGE_STATISTICS_LIST_URL = NEW_SERVER
            + "PublishInfo/PublishInfo/SetRead/NoReadCount";

    /**
     * contacts
     */
    public static String CONTACTS_CLASS_LIST_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailList/Init";
    public static String CONTACTS_SEARCH_MY_CLASS_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailList/Search";
    public static String CONTACTS_CLASS_MEMBER_LIST_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailListDetail/LoadClassMailListDetail";
    public static String CONTACTS_CLASS_MEMBER_DETAILS_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailListDetail/LoadPersonalDetails";
    public static String CONTACTS_CLASS_QRCODE_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailList/LoadQRCode";
    public static String CONTACTS_CLASS_REQUEST_LIST_URL = SERVER
            + "WaWatong/ApplyJoinClass/ApplyJoinClassList/InitApplyJoinClassList";
    public static String CONTACTS_CLASS_REQUEST_PROCESS_URL = SERVER
            + "WaWatong/ApplyJoinClass/ApplyJoinClassDetaile/CheckApplyJoinClass";
    public static String CONTACTS_CLASS_REQUEST_DELETE_URL = SERVER
            + "WaWatong/ApplyJoinClass/ApplyJoinClassDetaile/DeleteApply";
    public static String CONTACTS_SEARCH_CLASS_URL = SERVER
            + "School/School/School/SearchList";
    public static String CONTACTS_FORBID_CLASS_MEMBER_CHAT_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailListDetail/GagPersonnel";
    public static String CONTACTS_FORBID_CLASS_CHAT_URL = SERVER
            + "WaWatong/ClassMailList/ClassMailListDetail/SetMark";
    public static String CONTACTS_FRIEND_LIST_URL = SERVER
            + "WaWatong/PersonalMailList/PersonalMailList/InitPersonalMailList";
    public static String CONTACTS_SEARCH_MY_FRIEND_URL = SERVER
            + "WaWatong/PersonalMailList/PersonalMailList/SearchPersonalMailList";
    public static String CONTACTS_NEW_FRIEND_REQUEST_COUNT_URL = SERVER
            + "WaWatong/ApplyJoinPersonal/PersonalApplyList/NewFriendsCount";
    public static String CONTACTS_FRIEND_DETAILS = SERVER
            + "WaWatong/PersonalMailList/PersonalMailListDetaile/Load";
    public static String CONTACTS_ADD_FRIEND_URL = SERVER
            + "WaWatong/ApplyJoinPersonal/PersonalApplyDetaile/SaveApplyNewFriends";
    public static String CONTACTS_REMOVE_FRIEND_URL = SERVER
            + "WaWatong/PersonalMailList/PersonalMailListDetaile/Delete";
    public static String CONTACTS_MODIFY_FRIEND_REMARK_URL = SERVER
            + "WaWatong/PersonalMailList/PersonalMailListDetaile/Update";
    public static String CONTACTS_FRIEND_REQUEST_LIST_URL = SERVER
            + "WaWatong/ApplyJoinPersonal/PersonalApplyList/InitApplyNewFrienList";
    public static String CONTACTS_FRIEND_REQUEST_PROCESS_URL = SERVER
            + "WaWatong/ApplyJoinPersonal/PersonalApplyDetaile/CheckAApplyNewFriend";
    public static String CONTACTS_FRIEND_REQUEST_DELETE_URL = SERVER
            + "WaWatong/ApplyJoinPersonal/PersonalApplyDetaile/DeleteAApplyNewFriend";
    public static String CONTACTS_SEARCH_FRIEND_URL = SERVER
            + "WaWatong/PersonalMailList/PersonalMailListDetaile/SearchNewFriendList";
    public static String CONTACTS_CHILD_LIST_URL = SERVER
            + "Workbench/School/Student/SearchStudentByParentId";
    public static String CONTACTS_CREATE_CLASS_URL = SERVER
            + "WaWatong/ClassSetting/ClassSetting/CreateClass";
    public static String CONTACTS_CLASS_CATEGORY_URL = SERVER
            + "WaWatong/ClassSetting/ClassSetting/LoadClassSetting";
    public static String CONTACTS_NEW_REQUEST_COUNT_URL = SERVER
            + "WaWatong/ApplyJoinClass/ApplyPerentN/CountPerent";
    public static String GET_CONVERSATION_INFO_LIST_URL = SERVER
            + "WaWatong/InstantChat/InstantChat/GetBasicData";
    public static String CONTACTS_SCHOOL_TEACHER_LIST_URL = SERVER
            + "WaWatong/ClassSetting/ClassSetting/LoadTeacherList";
    public static String CONTACTS_MODIFY_CLASS_ATTRIBUTES_URL = SERVER
            + "WaWatong/ClassSetting/ClassSetting/ChangeClassInfo";
    public static String CONTACTS_CLASS_INFO_URL = NEW_SERVER
            + "SchoolLife/Class/ClassInfo/LoadClassInfo";
    public static String CONTACTS_ADD_CLASS_STUDENT_URL = NEW_SERVER
            + "ClassManage/ClassStudent/CreateStudent/ClassAddStudent";
    public static String CONTACTS_REMOVE_CLASS_STUDENT_URL = NEW_SERVER
            + "ClassManage/ClassStudent/CreateStudent/DeleteStudent";

    // Qrcode
    public static String QRCODE_SCANNING_URL = SERVER + "Scan/Scan/Scan/Scanning";
    // join class
    public static String JOIN_CLASS_URL = SERVER
            + "WaWatong/ApplyJoinClass/ApplyJoinClassDetaile/SaveApplyJoinClass";
    // join contact
    public static String JOIN_CONTACT_URL = SERVER
            + "WaWatong/ApplyJoinPersonal/PersonalApplyDetaile/SaveApplyNewFriends";

    public static String GET_PLATFORM_BOOK_LIST_URL = NEW_SERVER
            + "PlMaterial/PlMaterial/PlMaterialList/MOutlineList";
    public static String GET_PLATFORM_BOOK_TYPE_LIST_URL = NEW_SERVER
            + "PlMaterial/PlMaterial/PlMaterialList/MOutlineAtrList";
    public static String GET_PLATFORM_BOOK_CATALOG_LIST_URL = NEW_SERVER
            + "PlMaterial/PlMaterial/PlMaterialList/MCatalogList";
    public static String GET_PLATFORM_CATALOG_LESSON_LIST_URL = NEW_SERVER
            + "PlMaterial/PlMaterial/PlMaterialList/GetPMaterialList";

    public static String GET_SCHOOL_BOOK_LIST_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/SearchBookList";
    public static String GET_SCHOOL_BOOK_CATALOG_LIST_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/LoadCatalog";
    public static String GET_SCHOOL_CATALOG_LESSON_LIST_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/LoadBookResource";

    public static String GET_MY_COLLECTION_BOOK_LIST_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineList/SearchBookShelfList";
    public static String GET_MY_COLLECTION_BOOK_CATALOG_LIST_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineList/LoadCatalog";
    public static String GET_MY_COLLECTION_CATALOG_LESSON_LIST_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineList/BookShelfCourseList";
    public static String COLLECT_BOOK_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/AddMyShelf";
    public static String DELETE_COLLECTION_BOOK_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/MyShelfDelete";
    public static String UPDATE_COLLECTION_BOOK_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/MyShelfEdit";
    public static String RECOVERY_COLLECTION_BOOK_RESOURCE_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/MyShelfFrecovery";

    public static String GET_MY_COLLECTION_BOOK_DETAIL_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/MyShelfDetail";
    public static String GET_MY_COLLECTION_BOOK_CATALOG_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineList/LoadCatalog";

    public static String ADD_MY_COLLECTION_BOOK_SHELF_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/AddMyShelf";
    public static String REMOVE_MY_COLLECTION_BOOK_SHELF_URL = NEW_SERVER
            + "PersonalSpace/CollectionOutline/CollOutlineDetail/MyShelfDelete";


    public static String CS_UPDATE_VIEWCOUNT_URL = SERVER
            + "WorkSpace/CreateSpace/CreateSpace/PlayerNumber";
    public static String WAWATV_HELP_LIST_URL = WEIKE_SERVER + "wawatv/getHelpListNew";
    public static String WAWATV_UPDATE_VIEWCOUNT_URL = WEIKE_SERVER
            + "wawatv/updateViewCount";
    public static String WAWATV_COMMENT_LIST_URL = WEIKE_SERVER
            + "courseComment/getCommentList";
    public static String WAWATV_CREATE_COMMENT_URL = WEIKE_SERVER
            + "courseComment/addComment";
    public static String WAWATV_PRAISE_COURSE_URL = WEIKE_SERVER + "courseComment/addPraise";
    public static String WAWATV_COURSE_DETAIL_URL = WEIKE_SERVER
            + "resource/getResourceDetailById";

    public static String SPLIT_COURSE_LIST_URL = WEIKE_SERVER
            + "splitResource/getSplitResByPId";
    public static String SPLIT_COURSE_DETAIL_URL = WEIKE_SERVER + "splitResource/getSplitResById";
    public static String COURSE_IMAGES_URL = WEIKE_SERVER + "resource/getCourseImageById";
    public static final String GET_AUTO_MARK_COURSE_TEXT = WEIKE_SERVER + "courseCaption/getCourseCaption";
    public static String SPLIT_LEARN_CARD_DETAIL_URL = WEIKE_SERVER + "splitResource/getSplitLearnCardById";

    public static String UPDATE_PRAISE_COMMENT_NUMBER_URL = SERVER
            + "WorkSpace/IsRead/IsRead/SetPraiseOrCommentNum";

    public static String GET_SPLIT_COURSE_LIST_URL = WEIKE_SERVER
            + "splitResource/getSplitResByPId";
    public static String UPLOAD_RESOURCE_URL = WEIKE_UPLOAD_CONFIG_SERVER
            + "resource/uploadAndCreate";
    public static String UPLOAD_MEDIA_URL = WEIKE_UPLOAD_SERVER + "resource/materialUpload";

    public static String RENAME_MEDIA_URL = WEIKE_SERVER + "resource/updateResource";

    /**
     * cloud resources
     */
    public static String LOAD_RESOURCE_TYPE_URL = SERVER
            + "Material/Material/Material/LoadMaterialType";
    public static String LOAD_RESOURCE_LIST_URL = SERVER
            + "Material/Material/Material/SearchMaterial";

    public static String GET_PERSONAL_POST_BAR_LIST_URL = NEW_SERVER
            + "PersonalSpace/PostBar/PostBarList/GetPostBarList";
    public static String GET_MY_COLLECTION_LIST_URL = NEW_SERVER
            + "PersonalSpace/MyCollection/CollectionDetail/SearchCollection";
    public static String GET_SCHOOL_MESSAGE_LIST_URL = NEW_SERVER
            + "SchoolLife/NewEvent/NewEvent/SeachList";
    public static String GET_SCHOOL_COURSE_LIST_URL = NEW_SERVER
            + "SchoolLife/ClassRoom/ClassRoomList/SearchClassRoomList";
    public static String GET_CLASS_MESSAGE_LIST_URL = NEW_SERVER
            + "PublishInfo/PublishInfo/PublishInfo/RecommendList";
    public static String GET_CLASS_RESOURCE_LIST_URL = NEW_SERVER
            + "PublishInfo/PublishInfo/PublishInfo/PublishList";
    public static String GET_SCHOOL_LESSON_LIST_URL = NEW_SERVER
            + "SchoolLife/MicroClass/MicroClassList/SearchSpaceBySchool";
    public static String GET_SCHOOL_COURSE_CATEGORY_URL = NEW_SERVER
            + "SchoolLife/ClassRoom/ClassRoomList/GetClassRoomAt";
    public static String GET_PLATFORM_NEWS_LIST_URL = NEW_SERVER
            + "NoticeNews/News/News/SearchNewsList";
    public static String GET_PLATFORM_NOTICE_LIST_URL = NEW_SERVER
            + "NoticeNews/Notice/Notice/SearchNoticeList";

    public static String SUBSCRIBE_SCHOOL_LIST_URL = SERVER
            + "SubscribeNo/SubscribeNo/SubscribeNo/GetSubscribeNoList";
    public static String SUBSCRIBE_CLASS_LIST_URL = SERVER
            + "NewInterface/Institutions/Institutions/GetClassList";
    public static String SUBSCRIBE_PERSON_LIST_URL = NEW_SERVER
            + "JoinModule/PersonalAttention/PersonalAttention/SearchAttentionList";
    public static String SUBSCRIBE_SEARCH_URL = NEW_SERVER
            + "JoinModule/SearchAttention/SearchAttention/SearchList";
    public static String SUBSCRIBE_SCHOOL_INFO_URL = SERVER
            + "SubscribeNo/School/School/LoadSchool";
    public static String SUBSCRIBE_SCHOOL_INTRODUCTION = BASE_SHARE_URL
            + "mobileHtml/SchoolIntroduction.aspx";
    public static String SUBSCRIBE_ADD_SCHOOL_URL = SERVER
            + "SubscribeNo/SubscribeNo/SubscribeNo/SaveSubscribeNo";
    public static String SUBSCRIBE_REMOVE_SCHOOL_URL = SERVER
            + "SubscribeNo/SubscribeNo/SubscribeNo/CancelSubscribeNo";
    public static String SUBSCRIBE_ADD_PERSON_URL = NEW_SERVER
            + "PersonalSpace/PersonalAttention/PersonalAttentionDetail/SaveAttention";
    public static String SUBSCRIBE_REMOVE_PERSON_URL = NEW_SERVER
            + "PersonalSpace/PersonalAttention/PersonalAttentionDetail/CancelAttention";
    public static String SUBSCRIBE_SCHOOL_ADVISORY_COMMENT_LIST_URL = SERVER
            + "SubscribeNo/School/School/GetAdvisoryMessageList";
    public static String SUBSCRIBE_COMMIT_SCHOOL_ADVISORY_COMMENT_URL = SERVER
            + "SubscribeNo/School/School/SaveAdvisoryMessage";
    public static String SUBSCRIBE_SHARE_QRCODE_URL = BASE_SHARE_URL
            + "mobileHtml/ORCodeInformation.aspx?Id=%s";

    public static String WAWASHOW_RECOMMEND_LIST_URL = BASE_SERVER
            + "/Api/Platform/User/WaWaShow/ColumnResList/GetList";

    public static String UPLOAD_NOTE_POSTBAR_URL = NEW_SERVER
            + "PersonalSpace/PostBar/PostBarDetaile/UploadPostBar";
    public static String UPLOAD_NOTE_SCHOOL_COURSE_URL = NEW_SERVER
            + "SchoolLife/ClassRoom/ClassRoomDetaile/UploadClassRoom";
    public static String DELETE_OWNER_POSTBAR_URL = NEW_SERVER
            + "SchoolLife/ClassRoom/ClassRoomDetaile/DeletedRoom";
    public static String UPLOAD_NOTE_SCHOOL_MOVEMENT_URL = NEW_SERVER
            + "SchoolLife/Trends/TrendsDetaile/SaveTrends";
    public static String UPLOAD_NOTE_CLASS_SPACE_URL = NEW_SERVER
            + "PublishInfo/PublishInfo/PublishInfo/PublishSave";
    public static String PR_UPLOAD_WAWAWEIKE_URL = NEW_SERVER
            + "PersonalSpace/PersonalMaterial/PersonalMaterial/PModelAdd";
    public static String PR_LOAD_MEDIA_LIST_URL = NEW_SERVER
            + "PersonalSpace/PersonalMaterial/PersonalMaterialList/GetMaterialList";
    public static String PR_LOAD_MEDIA_NUM_URL = NEW_SERVER
            + "PersonalSpace/PersonalMaterial/PersonalMaterialList/GetMaterialType";
    public static String PR_DELETE_MEDIAS_URL = NEW_SERVER
            + "PersonalSpace/PersonalMaterial/PersonalMaterial/PModelDelete";
    public static String MARK_MY_RESOURCE_AS_READ_URL = NEW_SERVER
            + "PublishInfo/PublishInfo/SetRead/SetReaded";
    public static String MARK_PLATFORM_RESOURCE_AS_READ_URL = NEW_SERVER
            + "PublishInfo/PublishInfo/SetRead/SetReaded";
    public static String DELETE_COLLECTION_URL = NEW_SERVER
            + "PersonalSpace/MyCollection/CollectionDetail/CollectionDelete";
    public static String DELETE_POSTBAR_URL = NEW_SERVER
            + "PersonalSpace/PostBar/PostBarDetaile/PostBarDelete";
    public static String COLLECT_RESOURCE_URL = NEW_SERVER
            + "PersonalSpace/MyCollection/CollectionDetail/SaveCollection";
    public static String GET_BANNER_IMAGE_LIST_URL = NEW_SERVER
            + "JoinModule/Carousel/Carousel/CarouselList";
    public static String GET_QQ_CUSTOMER_LIST_URL = NEW_SERVER
            + "JoinModule/Carousel/QQCustomer/QQCustomerList";

    public static String SHARE_PERSONAL_SPACE_URL = BASE_SHARE_URL + "mobileHtml/UserInfo.aspx";
    public static String SHARE_PERSONAL_SPACE_PARAMS = "?Id=%s";
    public static String SHARE_SCHOOL_SPACE_URL = BASE_SHARE_URL + "mobileHtml/ShareSchoolInfo.aspx";
    public static String SHARE_SCHOOL_SPACE_PARAMS = "?Id=%s";
    public static String SHARE_CLASS_INVITATION_URL = BASE_SHARE_URL + "mobileHtml/Invitation_Apply.aspx";
    public static String SHARE_CLASS_INVITATION_PARAMS = "?FromUserId=%s&ClassPrimaryKey=%s";
    public static String SHARE_BOOK_URL = BASE_SERVER + "mobileHtml/LQ_BookShare.aspx";
    public static String SHARE_PLATFORM_BOOK_PARAMS = "?Type=%s&OutlineId=%s";
    public static String SHARE_SCHOOL_BOOK_PARAMS = "?Type=%s&OutlineId=%s&SchoolId=%s";

    public static String GET_CLOUD_RESOURCE_LIST_URL = NEW_SERVER
            + "PlMaterial/PlMaterial/PlMaterialList/GetPMaterialList";
    public static String CHECK_CLOUD_RESOURCE_TITLE_URL = NEW_SERVER
            + "PersonalSpace/PersonalMaterial/PersonalMaterial/PCheckTitle";
    public static String UPDATE_BROWSE_NUM_URL = NEW_SERVER + "SchoolLife/BrowseNum/BrowseNum/AddBrowseNum";
    //start add by renbao
    public static String BOOKSTORE_BOOK_DETAIL_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/LoadBook";//书本详细信息接口
    public static String BOOKSTORE_BOOK_DETAIL_CATALOG_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/LoadCatalog";//书本章节信息接口
    public static String BOOKSTORE_SEARCH_BOOKLIST_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/SearchBookList";//创E书屋书本列表接口
    public static String BOOKSTORE_GET_BOOKATTR_URL = NEW_SERVER
            + "ChuangEShuWu/Book/BookAttribute/GetBookAttr";//加载学段年级列表
    public static String BOOKSTORE_SEARCH_DEFAULT_BOOKLIST_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/SearchYinHeBook";//加载青岛银河小学的点读书屋信息接口
    public static String BOOKSTORE_SEARCH_DEFAULT_SCHOOK_URL = NEW_SERVER
            + "ChuangEShuWu/Book/Book/LoadSchool";//加载青岛银河小学学校信息接口
    public static String PICBOOKS_GET_CLASS_MAIL_URL = NEW_SERVER + "ClassManage/ClassMail/ClassMail/GetClassMailList";//加载学校的班级
    //end add by renbao
    public static String GET_STUDENT_TASK_LIST_URL = SERVER
            + "/ST/StudyTask/GetStudentTaskList";//拉取学生学习任务列表接口（手机端）
    public static String GET_STUDY_TASK_LIST_BY_TEACHERID_URL = SERVER
            + "/ST/StudyTask/GetStudyTaskListByTeacherId";//根据老师的id,任务类型,及布置日期和完成日期，拉取学习任务列表接口（手机端）
    public static String DELETE_TASK_BY_TEACHER_URL = SERVER
            + "/ST/StudyTask/DeleteTaskByTeacher";//老师删除自己创建的学习任务接口（手机端）
    public static String GET_STUDENT_BY_PARENT_URL = SERVER
            + "/ST/TaskDetail/GetStudentByParent";//查看某个班级里我的孩子列表接口（手机端）
    public static String STUDENT_COMMIT_HOMEWORK_URL = SERVER
            + "/ST/LookTask/UpdateTaskStateDone";//学生已查看或提交作业接口（手机端）
    public static String GET_TEACHERT_BY_CLASSID_URL = SERVER
            + "/ST/TaskDetail/GetTeachertByClassId";//查看某个班级的老师列表（手机端）
    public static String GET_COMMITTED_TASK_BY_TASK_ID_URL = SERVER
            + "/ST/CommitTask/GetCommittedTaskByTaskId";//学生、家长、老师拉取学生提交的任务列表
    public static String GET_TASK_BY_PARENT_URL = SERVER
            + "/ST/TaskDetail/GetTaskByParent";//查看某个任务下我的孩子提交的作业列表（家长）
    public static String UPDATE_TEACHER_READ_TASK_URL = SERVER
            + "/ST/LookTask/UpdateTeacherReadTask";//老师阅读作业（设置提交的作业已读）接口

    public static String GET_COMMENT_LIST_URL = SERVER
            + "/ST/TaskComment/GetCommentList";//根据任务id拉取对该任务的所有评论接口

    public static String ADD_TASK_COMMENT_URL = SERVER
            + "/ST/TaskComment/AddTaskComment";//新增任务讨论接口

    public static String ADD_PRAISE_COUNT_URL = SERVER
            + "/ST/TaskComment/AddPraiseCount";//点赞接口

    public static String GET_TASK_DETAIL_URL = SERVER
            + "/ST/TaskDetail/GetTaskDetail";//拉取微课/课件/作业/交作业详情接口

    public static String UPDATE_STUDENT_IS_READ_URL = SERVER
            + "/ST/CommitTask/UpdateStudentIsRead";//学生设置作业（提交作业和讨论话题）已读（手机端）

    public static String ADD_STUDY_TASK_URL = SERVER + "ST/StudyTask/AddStudyTask";
    public static String PUBLISH_STUDENT_HOMEWORK_URL = SERVER + "ST/LookTask/UpdateTaskStateDone";
    public static String NEW_PUBLISH_STUDENT_HOMEWORK_URL = SERVER + "ST/CommitTaskOnline/UpdateTaskStateDoneOnline";
    //上传自动批阅分数
    public static String COMMIT_AUTO_MARK_SCORE = SERVER + "ST/CommitTask/SetCommitTaskScore";

    public static String CREATE_STUDENT_INTO_CLASS_URL = NEW_SERVER
            + "ClassManage/ClassStudent/CreateStudent/CreateStudent";//新建学生

    public static String
            SEARCH_USER_INFO_LIST_URL = NEW_SERVER
            + "ClassManage/ClassMail/ClassMail/UserSearch";//搜索用户

    public static String ADD_STUDENT_TO_CLASS_URL = NEW_SERVER
            + "ClassManage/ClassStudent/CreateStudent/ClassAddStudent";//添加学生

    public static String DELETE_STUDENT_FROM_CLASS_URL = NEW_SERVER
            + "ClassManage/ClassStudent/CreateStudent/DeleteStudent";//把学生移出班级

    public static String REMOVE_CLASS_MEMBER_FROM_CLASS_URL = NEW_SERVER
            + "ClassManage/ClassStudent/CreateStudent/RemoveStudent";//把班级成员移出班级

    public static String LOAD_OUTLINE_ATR_URL = NEW_SERVER
            + "ChuangEShuWu/ChuangMicro/MicroUpload/OutlineAtr";// 上传微课至创E书屋时加载学校大纲属性
    public static String LOAD_MOUTLINE_LIST_URL = NEW_SERVER

            + "ChuangEShuWu/ChuangMicro/MicroUpload/MOutlineList";//上传微课至创E书屋时加载学校大纲
    public static String GET_FAMILY_MAIL_LIST_BY_CHILD_ID_URL = NEW_SERVER

            + "ClassManage/FamilyMailList/FamilyMailList/GetFamilyMailListByChildId";
    //根据孩子ID获取家庭所有成员和班主任
    public static String RELATION_UNBIND_BY_PARENT_URL = SERVER
            + "Setting/Relation/Relation/RelationUnbindByParent";//家长直接解除绑定

    public static String PICBOOKS_GET_DEFAULT_PICBOOKLIST_URL = NEW_SERVER + "ClassManage/PicBookHouse/" +
            "PicBookList/GetDefualPicBookList";//获取默认学校绘本屋列表

    public static String GET_SHOW_LIST_URL = ORIGNAL_SHOW_SERVER + "Product/getallproduction";//获取创意秀
    public static String DELETE_SHOW_URL = ORIGNAL_SHOW_SERVER + "Product/DeleteByMicroId";//删除创意秀
    public static String UPLOAD_SHOW_LIST_URL = ORIGNAL_SHOW_SERVER + "Product/SaveProduction";
    public static String LOAD_PICBOOK_CATEGORY_URL = NEW_SERVER + "ClassManage/PicBookHouse/PicBookAttr/GetPicBookAttr";
    public static String PICBOOKS_GET_PICBOOKLIST_URL = NEW_SERVER + "ClassManage/PicBookHouse/PicBookList/GetPicBookList";//获取绘本屋列表

    public static String UPLOAD_COURSE_BOOK_STORE_URL = NEW_SERVER +
            "ChuangEShuWu/ChuangMicro/MicroUpload/UploadMicro";
    public static String UPLOAD_PICTURE_BOOK_URL = NEW_SERVER + "ClassManage/PicBookHouse/PicBookDetail/SavePicBook";
    public static String UPLOAD_CLASS_SPACE_URL = NEW_SERVER + "PublishInfo/PublishInfo/PublishInfo/PublishSave";
    public static String GET_JOIN_SCHOOL_LIST_URL = NEW_SERVER + "JoinModule/School/School/SchoolListByUser";//加载加入的学校

    //资源开通
    public static String GET_CHOICE_SCHOOL_LIST_URL = NEW_SERVER + "Authorize/Authorize/GetCourseList";
    public static String CHECK_AUTHONRIZE_CONDITION_URL = NEW_SERVER + "Authorize/Authorize/CheckAuthorizeCondition";
    public static String AUTHONRIZE_TO_MEMBER_URL = NEW_SERVER + "Authorize/Authorize/AuthorizeToMember";

    //拉取个人空间和我的收藏素材列表集合
    public static String SEARCH_PERSONAL_SPACE_LIST_URL = BASE_SERVER + "NewApi/LqPersonalSpace/SearchPersonalSpaceList";
    //拉取导学卡
    public static String GET_GUIDANCE_CARD_URL = BASE_SERVER + "NewApi/ScGuidanceCard/GuidanceCardSearch";
    //拉取导学卡拆分页
    public static String GET_GUIDANCE_CARD_SPLIT_URL = WEIKE_SERVER + "/splitResource/getSplitResByPId";
    public static String GET_BROADCAST_LIST_URL = BASE_SERVER
            + "NewApi/Broadcast/GetBroadcasteList";
    public static String UPDATE_BROADCAST_LIST_URL = BASE_SERVER
            + "NewApi/Broadcast/UpdateBroadcast";
    public static String CREATE_BROADCAST_LIST_URL = BASE_SERVER
            + "NewApi/Broadcast/CreatBroadcast";
    public static String DELETE_BROADCAST_URL = BASE_SERVER
            + "NewApi/Broadcast/DeleteBroadcasteById";


    //新增校本资源库
    public static String LOAD_OUTLINE_ATTR_LIST_URL = NEW_SERVER + "PlMaterial/PlMaterial/PlMaterialList/MOutlineAtrList";
    public static String LOAD_SCHOOL_OUTLINE_ATTR_LIST_URL = NEW_SERVER + "ChuangEShuWu/ChuangMicro/MicroUpload/OutlineAtr";
    public static String LOAD_PLATFORM_OUTLINE_ATTR_LIST_URL = NEW_SERVER + "PlMaterial/OutlineMaterial/OutlineMaterial/MOutlineAtr";
    public static String LOAD_OUTLINE_LIST_URL = NEW_SERVER + "PlMaterial/PlMaterial/PlMaterialList/MOutlineList";
    public static String LOAD_SCHOOL_OUTLINE_LIST_URL = NEW_SERVER + "ChuangEShuWu/ChuangMicro/MicroUpload/MOutlineList";
    public static String LOAD_PLATFORM_OUTLINE_LIST_URL = NEW_SERVER + "PlMaterial/OutlineMaterial/OutlineMaterial/LoadOutline";
    public static String LOAD_SCHOOL_CATALOG_LIST_URL = NEW_SERVER + "ChuangEShuWu/ChuangMicro/MicroUpload/OutlineCatalog";
    public static String LOAD_PLATFORM_CATALOG_LIST_URL = NEW_SERVER + "PlMaterial/OutlineMaterial/OutlineMaterial/PlatformSection";
    public static String GET_STUDY_TASK_URL = SERVER + "ST/StudyTask/GetStudyTaskList";//拉取学习任务列表接口
    public static String DELETE_STUDY_TASK_URL = SERVER + "ST/StudyTask/DeleteTaskByTeacher";//老师删除自己创建的学习任务
    public static String PR_NEW_LOAD_MEDIA_LIST_URL = BASE_SERVER
            + "NewApi/LqPersonalSpace/SearchPersonalSpaceList";
    public static String GET_TASK_FINISH_INFO_URL = SERVER + "ST/TaskDetail/GetTaskDetail";//拉取微课/课件/作业/交作业详情接口
    public static String GET_TASK_COMMENT_LIST_URL = SERVER + "ST/TaskComment/GetCommentList";//根据任务id拉取对该任务的所有评论
    public static String PUSH_UNFINISH_TASKS_URL = SERVER + "ST/MsgService/PushUnreadTask";//未交提醒，老师推送未完成作业给学生
    public static String UPDATE_TASK_TEACHER_READ_URL = SERVER + "ST/LookTask/UpdateTeacherReadTask";//老师阅读作业（设置提交的作业已读）接口
    public static String GET_STUDENT_TASK_FINISH_INFO_URL = SERVER + "ST/TaskDetail/GetStudentTaskDetail";//拉取某一个学生提交的作业列表

    //校园巡查
    public static String GET_DATA_STATICS_LIST_LIST_URL = BASE_SERVER + "NewApi/DataStatics/GetDataStaticsList";//拉取教师或班级数据统计列表
    public static String GET_UPLOAD_PUBLISH_LIST_LIST_URL = NEW_SERVER +
            "PublishInfo/PublishInfo/PublishInfo/GetUploadPublishList";//加载用户或班级上传的通知、风采、翻转课堂、作业列表
    public static String GET_ST_STATICS_LIST_LIST_URL = SERVER +
            "ST/StudyTask/GetStStaticsList";//客户端拉取学习任务统计数据详情
    public static String GET_STATICS_SCHOOL_DYN_LIST_LIST_URL = NEW_SERVER +
            "SchoolLife/NewEvent/NewEvent/GetStaticsSchoolDynList";//获取校园巡查的校园动态数据详情
    public static String GET_STATICS_SCHOOL_BASED_LIST_LIST_URL = BASE_SERVER +
            "Api/Mobile/Material/Material/Material/GetStaticsSchoolBasedList";//获得校园巡查教师上传的校本资源


    //校园直播台相关的接口
    public static String GET_CAMPUS_ONLINE_LIST_BASE_URL = "http://media.womob.cn/api/lq/school.ashx?action=list&res=2";
    //根据直播的url拉取关注的机构
    public static String GET_CAMPUS_ONLINE_ORGANIZATION = BASE_SERVER + "NewApi/LqLiveShow/GetSchoolLiveShowByUrl";
    //根据学校ID拉取校园小记者列表
    public static String GET_CAMPUS_REPORTER_LIST_BASE_URL = BASE_SERVER + "NewApi/LqModulesPerm" +
            "/GetLiveShowReporterList";
    //删除小记者的权限
    public static String DELETE_REPORTER_PERMISSION_BASE_URL = BASE_SERVER + "NewApi/LqModulesPerm" +
            "/DeleteLiveShowReporter";
    //新增一个小记者的权限
    public static String ADD_REPORTER_PERMISSION_BASE_URL = BASE_SERVER + "NewApi/LqModulesPerm/AddLiveShowReporter";

    //添加老师到指定班级
    public static final String ADD_TEACHERS_TO_CLASS = BASE_SERVER + "api/Mobile/School/Teacher/BulkImportToClassByTeacherId";

    //英文写作相关的接口
    //学生提交英文写作接口
    public static String STUDENT_COMMIT_ENGLISH_WRITING_BASE_URL = BASE_SERVER + "NewApi/EnglishWriting" +
            "/UpdateEnglishWriting";

    //批改网返回批改信息同步到Server
    public static final String UPDATE_CORRECT_RESULT_URL = BASE_SERVER + "NewApi/EnglishWriting" +
            "/UpdateCorrectResult";
    //拉取打分公式的接口
    public static String GET_ENGLISH_WRITING_SCOREFORMULA_BASE_URL = BASE_SERVER + "NewApi/EnglishWriting" +
            "/GetMarkFormulaList";
    //NOC大赛相关的接口
    //拉取NOC大赛自己的参赛作品资源
    public static String GET_NOC_MY_WORK = NOC_BASE_SERVER + "api/work/getWorkList";
    //NOC大赛删除自己的作品资源
    public static String DELETE_NOC_MY_WORK = NOC_BASE_SERVER + "api/work/deleteWorkByIds";
    //拉取NOC大赛的公共参赛作品资源
    public static String GET_NOC_PUBLIC_WORK = NOC_BASE_SERVER + "api/work/getWorkListByGroupType";
    //NOC大赛报名参赛接口
    public static String NOC_SIGN_UP = NOC_BASE_SERVER + "api/work/signup";
    //NOC大赛更新阅读数
    public static String NOC_UPADTE_VIEWCOUONT = NOC_BASE_SERVER + "api/work/updateViewCount";
    //NOC大赛更新点赞数
    public static String NOC_UPADTE_PRAISECOUONT = NOC_BASE_SERVER + "api/work/updatePraiseNum";
    //空中课堂相关的接口
    //新增空中课堂
    public static String CREATE_AIR_CLASSROOM_BASE_URL = BASE_SERVER + "NewApi/AirClass/AddAirClass";
    //拉取空中课堂列表
    public static String GET_AIR_CLASSROOM_LIST_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/GetAirClassList";
    //更新空中课堂的状态
    public static String UPDATE_AIR_CLASSROOM_STATUS_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/UpdateAirClassState";
    //删除空中课堂数据
    public static String DELETE_AIR_CLASSROOM_DATA_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/DeleteAirClassById";
    //新增空中课堂的资料
    public static String ADD_AIRCLASSROOM_MATERIA_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/AddClassMateria";
    //拉取空中课堂资料列表
    public static String GET_AIRCLASSROOM_MATERIA_LIST_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/GetClassMateriaList";
    //删除空中课堂的数据
    public static String DELETE_AIRCLASSROOM_MATERIA_DATA_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/DeleteClassMateriaById";
    //空中课堂开始直播
    public static String BEGIN_AIRCLASSROOM_STATUS_ONLINE_BASE_URL = "http://lq.womob" +
            ".cn/api/lq/kzkt.ashx?action=edit&db=lqwawa";
    //青岛那边删除空中课堂的接口
    public static String DELETE_AIRCLASSROOM_MATERIA_TO_QINGDAO = "http://lq.womob" +
            ".cn/api/lq/kzkt.ashx?action=delete&db=lqwawa";
    //新增空中课堂互动交流接口
    public static String ADD_AIRCLASSROOM_INTERACTION_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/AddCommunication";
    //拉取空中课堂互动交流列表接口
    public static String GET_AIRCLASSROOM_INTERACTION_LIT_BASE_URL = BASE_SERVER + "NewApi/AirClass" +
            "/GetCommunicationList";
    //结束直播的接口
    public static String END_UP_AIRCLASSROOM_ONLINE_BASE_URL = "http://lq.womob.cn/api/lq/kzkt" +
            ".ashx?action=end";
    //更新空中课堂的浏览数接口
    public static String UPDATE_AIRCLASSROOM_BROWSE_COUNT = BASE_SERVER +
            "NewApi/AirClass/AddBrowseCount";
    //更新空中课堂结束直播之后的状态 state == 2
    public static String UPDATE_AIRCLASS_STATE_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/UpdateEbanshuLiveState";
    //观看直播会员上线离线接口
    public static String UPDATE_ONLINE_NUMBER_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/UpdateOnlineNum";
    //拉取观看直播在线会员接口
    public static String GET_ONLINE_NUMBER_LIST_BASE_URL = BASE_SERVER + "NewApi/AirClass/GetOnlineNumList";
    //获取空中课堂学习任务的列表
    public static String GET_AIRCLASS_STUDY_TASK_LIST_BASE_URL =
            BASE_SERVER + "NewApi/AirClass/GetAirTaskList";
    //删除空中课堂学习任务的列表
    public static String DELETE_AIRCLASS_STUDY_TASK_LIST_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/DeleteAirTaskById";
    //删除空中课堂直播的列表（new）
    public static String DELETE_AIRCLASS_ONLINE_LIST_NEW_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/DeleteAirClassNew";
    //布置空中课堂学习任务
    public static String ADD_AIRCLASS_STUDY_TASK_LIST_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/AddAirTask";
    //布置空中课堂看课件的学习任务
    public static String ADD_AIRCLASS_LOOK_STUDY_TASK_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/AddLookStudyTaskInAirClass";
    //通过学校的school和classId获取班级信息的列表
    public static String GET_CLASS_MEMBER_LIST_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/GetClassMemberList";
    //新增空中课堂(新)
    public static String CREATE_AIR_CLASSROOM_NEW_BASE_URL = BASE_SERVER + "NewApi/AirClass/AddAirClassNew";
    //空中课堂板书直播
    public static String GET_BLACKBOARD_LIVE_BASE_URL = BASE_SERVER + "NewApi/AirClass/GetEbanshuLiveUrl";
    //获取空中课堂以及慕课直播实时在线人数
    public static String GET_ONLINE_ALWAYS_PEOPLE_NUM_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/GetAirClassInfo";
    //通过id获取空中课堂详情页的信息
    public static String GET_AIRCLASS_DETAIL_BY_ID_BASE_URL = BASE_SERVER + "NewApi/AirClass/GetAirClassById";
    //空中课堂加入我的直播接口
    public static String ADD_MY_LIVE_FROM_AIRCLASS_BASE_URL = BASE_SERVER + "NewApi/AirClass/AddMyLive";
    //判断直播有没有加入我的直播中
    public static String JUDGE_LIVE_ADD_MY_LIVE_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/CheckIsInMyLive";
    //空中课堂课程表日期标记
    public static String GET_DATESIGN_FOR_AIRCLASS_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/GetDateSignForAirClass";
    //------------------------------表演课堂----------------------------------------------------
    //拉取表演课堂的列表
    public static String GET_ACT_CLASSROOM_LIST_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/GetLqPerformClassList";
    //删除表演课堂列表数据的item
    public static String DELETE_ACT_CLASSROOM_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/DeleteLqPerformClassById";
    //表演课堂视频点赞的接口
    public static String ACT_CLASSROOM_VIDEO_PRAISE_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/AddLqPCPraiseCount";


    //表演课堂获取AR大图
    public static String ACT_CLASSROOM_VIDEO_AR_URL = "http://albums.womob.cn/api/photoinfo";

    //增加表演课堂视频播放次数接口
    public static String ACT_ALASSROOM_VIDEO_PLAY_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/AddLqPCPlayCount";
    //新增lq表演课堂的评论接口
    public static String ACT_CLASSROOM_TOP_DISCUSS_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/AddLqPCComment";
    //表演课堂评论点赞的接口
    public static String ACT_CLASSROOM_TOP_PRAISE_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/AddCommentPraiseCount";
    //通过id拉取表演课堂的数据
    public static String GET_ACT_CLASSROOM_DETAIL_DATA_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass/GetShareLqPerformClassList";
    //获取表演课堂详情页评论的接口
    public static String GET_ACT_CLASSROOM_TOP_DIS_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass" +
            "/GetLqPCCommentList";


    //LQMOOC接口
    //根据任务单id获取全部课程章节列表
    public static String LQMOOC_COURSE_SECTION_DATA_LIST_URL = LQMOOC_BASE_SERVER +
            "api/course/getAllChapterListByResId";

    //校长助手页面url
    public static String PRINCIPAL_ASSISTANT_LIST_URL = PRINCIPAL_ASSISTANT_BASE_URL +
            "AvoidLogin/Index";

    //新增"新看课件"学习任务接口
    public static String WATCH_WAWA_COURSE_PUBLISH_STUDY_TASK_URL = SERVER +
            "ST/LookTask/AddLookStudyTask";

    //营养膳食页面url
    public static String NUTRITION_RECIPES_LIST_URL = NUTRITION_RECIPES_BASE_URL +
            "User/LoginM";

    //营养膳食拉取开通服务接口
    public static String NUTRITION_RECIPES_LOGIN_URL = SERVER +
            "Setting/Login/Login/GetYkState";

    //判断课件资源用户是否有打开的权限 lqwawa lqmooc
    public static String OPEN_LQWAWA_COURSE_PERMISSION_BASE_URL = BASE_SERVER +
            "NewApi/LqMember/" + "CheckResRightForMember";

    public static String OPEN_LQMOOC_COURSE_PERMISSION_BASE_URL = LQMOOC_BASE_SERVER +
            "api/res/judgmentAuthorityByResId";

    //删除任务单接口
    public static String PR_DELETE_TASK_ORDER_URL = BASE_SERVER + "NewApi/ScGuidanceCard/PDeleteGuidanceCard";


    //加载提问,批阅列表
    public static String GET_LOADCOMMITTASKREVIEWLIST = SERVER
            + "/ST/CommitTaskReview/LoadCommitTaskReviewList";

    //添加学生提问或老师批阅
    public static String GET_ADDCOMMITTASKREVIEW = SERVER
            + "/ST/CommitTaskReview/AddCommitTaskReview";
    /**
     * 移除批阅记录
     */
    public static String POST_REMOVE_COMMIT_TASKREVIEW = SERVER + "ST/CommitTaskReview/RemoveCommitTaskReview";

    //校验精品资源库权限的接口
    public static String CHECK_CHOICE_LIB_PERMISSION_BASE_URL = BASE_SERVER +
            "NewApi/OutlineAuthorize/CheckSchoolIsOpenAuOutline";
    //拉取精品资源库标签的接口
    public static String GET_CHOICE_LIB_TAG_BASE_URL = BASE_SERVER + "NewApi/OutlineAuthorize/GetSchoolOutlineTag";
    //获取精品资源库列表的数据
    public static String GET_CHOICE_LIB_DATA_LIST_BASE_URL = BASE_SERVER +
            "NewApi/OutlineAuthorize/GetAuthOutlineList";
    //检查会员是否有查看精品校本资源库中资源的权限
    public static String CHECK_AUTH_LIB_PERMISSION_BASE_URL = BASE_SERVER +
            "NewApi/LqMember/CheckAuthOutlineRightForMember";
    //判断资源压缩包存不存在指定ip的服务器上
    public static String CHECK_RESINFO_DO_EXIST_BODY_URL = "/resBackup/api/getResInfo";

    //判断内网的ip在当前的无线网状态通不通
    public static String CHECKOUT_INSIDE_IP_DO_EXIST_URL = "/resBackup/api/getNetWorkStatus";
    //====================班级通讯录==============================
    //班级通讯录 根据班级ID加载<历史班>班级通讯录成员列表接口
    public static String LOAD_HISTORY_CLASSMAIL_LIST_DETAIL_BASE_URL = BASE_SERVER +
            "Api/Mobile/WaWatong/ClassMailList/ClassMailListDetail/LoadHistoryClassMailListDetail";

    //////////////////---班级分组---///////////////////////////

    /**
     * 添加学习小组
     */
    public static String GET_ADDSTUDYGROUP = SERVER + "ST/StudyGroup/AddStudyGroup";

    /**
     * 修改学习小组名称
     */
    public static String GET_CHANGESTUDYGROUPNAME = SERVER + "ST/StudyGroup/ChangeStudyGroupName";

    /**
     * 拉取学习小组信息
     */
    public static String GET_LOADSTUDYGROUPINFO = SERVER + "ST/StudyGroup/LoadStudyGroupInfo";

    /**
     * 拉取班级下的老师和学生
     */
    public static String GET_LOADTEACHERANDSTUDENTSBYCLASSID = SERVER + "ST/StudyGroup/LoadTeacherAndStudentsByClassId";

    /**
     * 拉取班级的所有学习小组
     */
    public static String GET_LOADSTUDYGROUPBYCLASSID = SERVER + "ST/StudyGroup/LoadStudyGroupByClassId";

    /**
     * 删除(解散)学习小组
     */
    public static String GET_REMOVESTUDYGROUP = SERVER + "ST/StudyGroup/RemoveStudyGroup";


    /**
     * 移出学习小组
     */
    public static String GET_OUTOFSTUDYGROUP = SERVER + "ST/StudyGroup/OutOfStudyGroup";

    /**
     * 拉取老师的学习小组
     */
    public static String GET_LOADTEACHERSTUDYGROUPS = SERVER + "ST/StudyGroup/LoadTeacherStudyGroups";

    /**
     * 编辑小组成员
     */
    public static String GET_MODIFYSTUDYGROUPMEMBER = SERVER + "ST/StudyGroup/ModifyStudyGroupMember";

    /**
     * 发布学习任务到学习小组
     */
    public static String GET_ADDSTUDYTASKTOSTUDYGROUP = SERVER + "ST/StudyTask/AddStudyTaskToStudyGroup";

    /**
     * 发布学习任务（看课件）到学习小组
     */
    public static String GET_ADD_STUDY_LOOK_TASK_GROUP = SERVER + "ST/LookTask/AddLookStudyTaskToStudyGroup";

    /**
     * 发布学习任务（听说+读写）到班级
     */
    public static String GET_ADD_STUDY_LISTEN_READANDWRITE_TASK_BASE_URL = BASE_SERVER +
            "Api/Mobile/ST/StudyTask/AddTSDXStudyTask";
    /**
     * 发布学习任务（听说+读写）到学习小组
     */
    public static String GET_ADD_STUDY_LISTEN_READANDWRITE_GROUP_TASK_BASE_URL = BASE_SERVER +
            "Api/Mobile/ST/StudyTask/AddTSDXStudyTaskToStudyGroup";
    /**
     * 获取学习任务(听说+读写)任务详情数据
     */
    public static String GET_LISTEN_READANDWRITE_TASK_DETAIL_BASE_URL = BASE_SERVER +
            "Api/Mobile/ST/StudyTask/GetTSDXTaskDetail";

    /**
     * 获取学生针对已完成/未完成界面的数据
     */
    public static String GET_LISTEN_READANDWRITE_DETAIL_DATA_BASE_URL = BASE_SERVER +
            "Api/Mobile/ST/StudyTask/GetStudentChildTSDXTaskDetail";

    /**
     * 发送学习任务（听说+读写）到空中课堂的班级
     */
    public static String GET_LISTEN_READANDWRITE_AIRCLASS_TASK_BASE_URL = BASE_SERVER +
            "NewApi/AirClass/AddTSDXStudyTaskInAirClass";


    //综合任务api接口
    /**
     * 综合任务发送到班级
     */
    public static String ADD_TOGETHER_TASK_TOCLASS_BASE_URL = BASE_SERVER + "Api/Mobile/ST/StudyTask/AddTogetherTaskToClass";

    /**
     * 发送综合任务到学习小组
     */
    public static String ADD_TOGETHER_TASK_TOSTUDYGROUP_BASE_URL = BASE_SERVER + "Api/Mobile/ST/StudyTask/AddTogetherTaskToStudyGroup";

    /**
     * 新增综合任务到空中课堂
     */
    public static String ADD_TOGETHER_TASK_TOAIRCLASS_BASE_URL = BASE_SERVER + "NewApi/AirClass/AddTogetherTaskToAirClass";

    /**
     * 拉取综合任务的一级详情
     */
    public static String GET_FIRST_TOGETHER_TASK_DETAIL_BASE_URL = BASE_SERVER + "Api/Mobile/ST/StudyTask/GetFirstTogetherTaskDetail";

    /**
     * 拉取综合任务二级详情
     */
    public static String GET_SECOND_TOGETHER_TASK_DETAIL_BASE_URL = BASE_SERVER + "Api/Mobile/ST/StudyTask/GetSecondTogetherTaskDetail";

    /**
     * 拉取综合任务的任务完成情况
     */
    public static String GET_TOGETHER_TASK_STUDENT_FINISH_DETAIL = BASE_SERVER + "Api/Mobile/ST/StudyTask/GetTogetherTaskStudentFinishDetail";

    /**
     * 同时发布学习任务到班级和学习小组
     */
    public static String GET_STUDYTASK_TO_CLASS_AND_STUDYGROUP = BASE_SERVER + "Api/Mobile/ST/StudyTask/AddStudyTaskToClassAndStudyGroup";

    /**
     *同时发布《综合任务》到班级和学习小组
     */
    public static String GET_TOGETHERTASK_TO_CLASS_AND_STUDYGROUP = BASE_SERVER + "Api/Mobile/ST/StudyTask/AddTogetherTaskToClassAndStudyGroup";

    /**
     * 同时发布新看作业到班级和学习小组
     */
    public static String GET_LOOKSTUDYTASK_TO_CLASS_AND_STUDYGROUP = BASE_SERVER +
            "Api/Mobile/ST/LookTask/AddLookStudyTaskToClassAndStudyGroup";

    /**
     * ===========================================================================
     *        天赋密码
     * ===========================================================================
     */
    /**
     * 查看是否有天赋密码
     */
    public static String GET_TESTHAVEREPORT = TALENT_BASE_SERVER + "api/testHaveReport";

    /**
     * 获取报告
     */
    public static String GET_GETREPORTBYMEMBERID = TALENT_BASE_SERVER + "api/getReportByMemberId";

    /**
     * 获取解读对比
     */
    public static String GET_GETINTERPRETATION = TALENT_BASE_SERVER + "api/getInterpretation";

    /**
     * 通过ParentId查询孩子列表
     */
    public static String GET_LOADCHILDRENBYPARENTID = NEW_SERVER
            + "ClassManage/FamilyMailList/FamilyMailList/LoadChildrenByParentId";

    /**
     * 学习资源（微课）统计的接口
     */
    public static String ADD_RESOURCE_INFO_RECORD_BASE_URL = BASE_SERVER +
            "NewApi/StudentTJ/AddTJRes";

    /**
     * 学习资源（直播）统计接口
     */
    public static String ADD_ONLINE_INFO_RECORD_BASE_URL = BASE_SERVER +
            "NewApi/StudentTJ/AddTJLive";
    //判断用户有咩有退出班级
    public static String CHECK_MEMBER_ISIN_CLASS_BASE_URL = BASE_SERVER +
            "Api/Mobile/WaWatong/ClassMailList/CheckMemberIsInClass";
    //判断老师是不是班主任的角色 和 移出老师通讯录接口
    public static String CHECK_TEACHER_ROLEOFCLASS_AND_REMOVE_BASE_URL = BASE_SERVER +
            "/api/Mobile/School/Teacher/RemoveFromTeacherMailList";


    /**
     * 拉取学习任务列表日期标记
     */
    public static String GET_STUDY_TASKLIST_DATE_SIGN_URL = SERVER +
            "ST/StudyTask/GetStudyTaskListDateSign";

    /**
     * 移除提交记录
     */
    public static String POST_REMOVE_COMMIT_TASK_URL = SERVER + "ST/CommitTask/RemoveCommitTask";

    /**
     * 移除英文写作
     */
    public static String POST_REMOVE_ENGLISH_WRITING_URL = BASE_SERVER + "NewApi/EnglishWriting/RemoveEnglishWriting";

    /**
     * 加载章节下资源统计的数量
     */
    public static String GET_LOAD_BOOK_RESOURCE_COUNT_BASE_URL = BASE_SERVER + "Api/AmWaWa/ChuangEShuWu/Book/Book/LoadBookResourceCount";
    /**
     * 校验从学程馆中资源的状态
     */

    public static String CHECK_LQCOURSE_SHOP_RESOURCE_PERMISSION = LQMOOC_BASE_SERVER + "api/buy/checkPermission";

    /**
     * 删除评论的接口
     */

    public static String DELETE_COMMENT_DATA_BASE_URL = WEIKE_SERVER + "courseComment/deleteComment";

    /**
     * 第三方登录接口
     */
    public static String GET_THIRD_PARTY_AUTHORIZED_LOGIN_BASE_URL = BASE_SERVER + "Api/Mobile/Setting/Login/Login/ThirdPartyAuthorizedLogin";
    /**
     * 拉取关联账号的信息
     */
    public static String GET_LOAD_THIRDPARTY_ASSOCIATED_ACCOUNT = BASE_SERVER + "Api/Mobile/Setting/Login/Login/LoadThirdPartyAssociatedAccount";

    /**
     * 解除第三方信息的绑定
     */
    public static String GET_UNBIND_THIRDPARTY_AUTHORIZATION = BASE_SERVER + "Api/Mobile/Setting/Login/Login/UnBindThirdPartyAuthorization";

    /**
     * 第三方授权绑定
     */
    public static String GET_BIND_THIRDPARTY_AUTHORIZATION = BASE_SERVER + "Api/Mobile/Setting/Login/Login/BindThirdPartyAuthorization";

    /**
     * 获取语音评测的文本
     */

    public static String  GET_SPEECH_ASSESSMENT_TEXT_BASE_URL = WEIKE_SERVER + "courseCaption/getCourseCaption";

    /**
     * mooc提交作业的接口
     */

    public static String GET_UPDATE_TASK_STATE_DONE_ONLINE = BASE_SERVER +
            "Api/Mobile/ST/CommitTaskOnline/UpdateTaskStateDoneOnline";

    /**
     * 获取授课班级是否结束
     */
    public static String GET_CHECK_TEACHING_PLAN_BASE_URL = LQMOOC_BASE_SERVER + "api/online/getCourseOnlineStatus";

    /**
     * 我的表演拉取接口
     */
    public static String GET_MY_PERFORMANCE_BASE_URL = BASE_SERVER + "NewApi/LqPerformClass/GetMemberLqPerformClassList";

    /**
     *布置学习任务资源选取资源模块显示控制
     */
    public static String GET_STUDYTASK_CONTROL_BASE_URL = BASE_SERVER + "Api/Mobile/ST/StudyTask/GetStudyTaskResSourceControl";

    /**
     * =================================任务单批阅相关接口=================================================
     */

    /**
     * 学生提交答题卡类型的读写单(交作业)
     */
    public static String UPDATE_TASK_STATE_DONE_FOREP_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/UpdateTaskStateDoneForEP";
    /**
     * 学生提交答题卡类型的读写单(交作业)(online)
     */
    public static String ONLINE_UPDATE_TASK_STATE_DONE_FOREP_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/UpdateTaskStateDoneForEPOnline";

    /**
     * 新增学生试题得分信息
     */
    public static String ADD_EQMEMBER_INFO_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/AddEQMemberInfo";

    /**
     * 新增学生试题得分信息(online)
     */
    public static String ONLINE_ADD_EQMEMBER_INFO_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/AddEQMemberInfoOnline";
    /**
     * 根据CommitTaskId拉取学生的答题卡列表
     */
    public static String LOAD_ANSWER_BY_COMMITTASKID_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/LoadStudentEQAnswerByCommitTaskId";

    /**
     * 拉取主观题批阅和提问的列表
     */
    public static String LOAD_SUBJECTIVE_REVIEW_LIST_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/LoadSubjectiveReviewList";

    /**
     * 添加主观题的批阅和提问
     */
    public static String ADD_SUBJECTIVE_REVIEW_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/AddSubjectiveReview";

    /**
     * 根据TaskId拉取试卷的答题分析
     */
    public static String LOAD_EPANSWER_ANALYSIS_BYTASKID_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/LoadEPAnswerAnalysisByTaskId";

    /**
     * 拉取试题的答题分析详情
     */
    public static String LOAD_EQANSWER_ANALYSIS_DETAILBYID_BASE_URL = BASE_SERVER + "Api/Mobile/ST/ExamQuestion/LoadEQAnswerAnalysisDetailById";

    /**
     * 设置mooc资源已读
     */
    public static final String MOOC_SET_READED_BASE_URL = LQMOOC_BASE_SERVER + "api/student/course/setReaded";

    /**
     * 新增会员的极光设备ID
     */
    public static final String ADD_MEMBER_JREGIST_BASE_URL = BASE_SERVER + "NewApi/JPush/AddMemberJRegist";

    /**
     * 获取老师点评统计学生详情
     */
    public static final String GET_TEACHER_REVIEW_STATIS_BASE_URL = BASE_SERVER + "Api/Mobile/ST" +
            "/CommitTask/GetTeacherReviewStatis";

    /**
     * 获取老师点评统计学生作业列表
     */
    public static final String GET_TEACHER_REVIEW_STATIS_LIST_BASE_URL = BASE_SERVER + "Api/Mobile/ST/CommitTask/GetTeacherReviewStatisList";

    /**
     * 根据UnionId加载关联的账号列表
     */
    public static final String LOAD_ASSOCIATED_BY_UNIONID_BASE_URL = BASE_SERVER + "Api/Mobile/Setting/Login/Login/LoadAssociatedAccountByUnionId";

    /**
     * 一键解除当前UnionId绑定的账号并绑定新的账号
     */
    public static final String GET_BIND_NEWACCOUNT_FOR_UNIONID_BASE_URL = BASE_SERVER + "Api/Mobile/Setting/Login/Login/BindNewAccountForUnionId";

    /**
     * 拉取助教的批阅列表
     */
    public static final String GET_ASSIST_REVIEW_LIST_BASE_URL = BASE_SERVER + "Api/Mobile/ST/Assist/GetAssistReviewList";

    /**
     * 助教批阅或学生提问
     */
    public static final String GET_ADD_ASSIST_REVIEW_BASE_URL = BASE_SERVER + "Api/Mobile/ST/Assist/AddAssistReview";

    /**
     * 拉取个人空间存储统计
     */
    public static final String GET_MEMBER_STORAGE__BASE_URL = BASE_SERVER + "NewApi/LqMember/GetMemberStorageInfo";

    /**
     * 获取多个资源的详情数据
     */
    public static final String GET_RESOURSE_LIST_BYIDS_BASE_URL = WEIKE_SERVER + "account" +
            "/getResourseListByIds";
    
    /**
     * 查询mooc看课件更新已读未读的权限
     */
    public static final String GET_TASKTYPE_BY_CHAPTERID_BASE_URL = LQMOOC_BASE_SERVER + "api/chapter/getTaskTypeByChapterId";

    /**
     * 设置学生查看其他学生的作业权限
     */
    public static final String GET_VIEW_OTHERS_PERMISSION_BASE_URL = BASE_SERVER + "Api/Mobile/ST/StudyTask/SetStudentViewOthersTaskPermisson";

    /**
     * 拉取作业批改率统计
     */
    public static final String GET_TASK_CORRECT_RATE_BASE_URL = BASE_SERVER + "NewApi" +
            "/CourseStudyTaskStatis/GetTaskCorrectRate";

    /**
     * 拉取作业布置率统计
     */
    public static final String GET_COURSE_RESSET_RATE_BASE_URL = BASE_SERVER + "NewApi" +
            "/CourseStudyTaskStatis/GetCourseResSetRate";

    /**
     * 拉取作业完成率统计
     */
    public static final String GET_TASK_COMPLETE_RATE_BASE_URL = BASE_SERVER + "NewApi/CourseStudyTaskStatis/GetTaskCompleteRate";

    /**
     * 拉取详细学习成绩统计
     */
    public static final String GET_TASK_DETAIL_STATIS_BASE_URL = BASE_SERVER + "NewApi/CourseStudyTaskStatis/GetTaskDetaileStatis";

    /**
     * 拉取老师未批阅的学生提交列表
     */
    public static final String GET_TEACHER_NO_CORRECT_COMMITLIST = BASE_SERVER + "NewApi" +
            "/CourseStudyTaskStatis/GetTeacherNoCorrectCommitList";

    /**
     * 查询是否有课中实施方案
     */
    public static final String POST_IF_EXUST_PLAN = BASE_SERVER + "api/coursePlan/qryIfExistPlan";

    /**
     * 拉取班级老师所有的未批阅的学生提交列表
     */
    public static final String GET_CLASSTEACHER_NOCORRECT_COMMITLIST = BASE_SERVER + "NewApi/CourseStudyTaskStatis/GetClassTeacherNoCorrectCommitList";

    /**
     * 拉取班级平均分成绩统计
     */
    public static final String GET_CLASS_TASK_AVERAGE_STATIS = BASE_SERVER + "NewApi" +
            "/CourseStudyTaskStatis/GetClassTaskAverageStatis";
}
