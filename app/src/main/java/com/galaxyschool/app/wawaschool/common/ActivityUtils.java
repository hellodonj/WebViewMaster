package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.BasicUserInfoActivity;
import com.galaxyschool.app.wawaschool.BookStoreListActivity;
import com.galaxyschool.app.wawaschool.BroadcastNoteActivity;
import com.galaxyschool.app.wawaschool.CampusPatrolPickerActivity;
import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.ClassDetailsActivity;
import com.galaxyschool.app.wawaschool.CreateOnlineActivity;
import com.galaxyschool.app.wawaschool.FileActivity;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.IntroductionForReadCourseActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolClassListActivity;
import com.galaxyschool.app.wawaschool.SchoolCourseListActivity;
import com.galaxyschool.app.wawaschool.SchoolMessageListActivity;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.SelectedReadingDetailActivity;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.StudentFinishedHomeworkListActivity;
import com.galaxyschool.app.wawaschool.TaskOrderDetailActivity;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.course.PlaybackActivityNew;
import com.galaxyschool.app.wawaschool.course.PlaybackActivityPhone;
import com.galaxyschool.app.wawaschool.course.PlaybackWawaPageActivityPhone;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ClassDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.IntroductionForReadCourseFragment;
import com.galaxyschool.app.wawaschool.fragment.PersonalSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.SchoolSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.TaskOrderFragment;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.jpush.PushUtils;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NocEnterDetailArguments;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.libs.gallery.ImageInfo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassResourceData;
import com.lqwawa.intleducation.module.organcourse.OrganCourseClassifyActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.WatchResourceType;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.data.CourseShareData;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInPlaybackParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class ActivityUtils {

    public static final int REQUEST_CODE_TAKE_PHOTO = 1;
    public static final int REQUEST_CODE_FETCH_PHOTO = 2;
    public static final int REQUEST_CODE_ZOOM_PHOTO = 3;
    public static final int REQUEST_CODE_BASIC_USER_INFO = 4;
    public static final int REQUEST_CODE_ADD_ROLE_INFO = 5;
    public static final int REQUEST_CODE_ADD_RELATION_INFO = 6;

    //用于startActivityForActivity判断是否需要刷新数据的code
    public static final int REQUEST_CODE_RETURN_REFRESH = 113;
    //用于返回作为intent的key
    public static final String REQUEST_CODE_NEED_TO_REFRESH = "need_to_refresh";
    public static final String KEY_QRCODE_STRING = "qrcode_string";
    public static final String KEY_QRCODE_CLASS_INFO = "qrcode_class_info";
    public static final String KEY_QRCODE_SCHOOL_INFO = "qrcode_school_info";

    public static final String WAWACHAT_PHONENUMBER = "4001077727";
    public static final String EXTRA_IS_PICK = "is_pick";
    //是否从校本资源库选取资源
    public static final String EXTRA_IS_PICK_SCHOOL_RESOURCE = "is_pick_school_resource";
    public static final String IS_PIC_BOOK_CHOICE = "is_pic_book_choice";//是否是精品绘本
    public static final String IS_FROM_CHOICE_LIB = "is_from_choice_lib";//是不是来自精品资源库
    public static final String IS_LQWWA_VIP_SCHOOL = "is_lqwawa_vip_school";
    public static final String EXTRA_COURSE_TYPE = "course_type";
    public static final String EXTRA_TASK_TYPE = "task_type";
    public static final String EXTRA_HEADER_TITLE = "header_title";
    public static final String EXTRA_DEFAULT_DATE = "default_date";
    public static final String EXTRA_TEMP_DATA = "temp_data";

    public static final String EXTRA_SCHOOL_INFO = "school_info";
    public static final String EXTRA_SCHOOL_INFO_LIST = "school_info_list";
    public static final String EXTRA_ACT_CLASSROOM_DATA = "act_classroom_data";

    public static final String EXTRA_IS_NEED_HIDE_COLLECT_BTN = "is_need_hide_collect_btn";
    public static final String EXTRA_STDUY_TYPE = "study_type";//空中课堂学习任务练习的类型
    public static final String EXTRA_DATA_INFO = "data_info";//传递对象
    public static final String EXTRA_SCHOOL_INFO_LIST_DATA = "school_info_list_data";
    public static final String EXTRA_IS_TEACHER = "is_teacher_role";
    public static final String EXTRA_CHOOSE_TASKORDER_DATA = "choose_taskorder_data";
    public static final String EXTRA_FROM_SUPER_TASK = "from_super_task";
    public static final String EXTRA_SELECT_MAX_COUNT = "select_max_count";
    public static final String EXTRA_SUPER_TASK_TYPE = "super_task_type";
    public static final String EXTRA_IS_ONLINE_CLASS = "is_online_class_class";
    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_SCHOOL_ID = "school_id";
    public static final String EXTRA_USER_ROLE_TYPE = "user_role_type";
    public static final String EXTRA_LQCOURSE_SHOP = "lqcourse_shop";
    public static final String EXTRA_IS_HISTORY_CLASS = "is_histroy_class";
    public static final String EXTRA_IS_FINISH_LECTURE = "is_finish_lecture";//完成授课
    public static final String EXTRA_IS_GET_APPOINT_RESOURCE = "get_appoint_resource";//获取指定的资源
    public static final String EXTRA_IS_APPLICATION_START = "isApplicationStart";
    public static CommitTask commitTask = null;

    /**
     * 新建帖子
     *
     * @param context
     * @param noteOpenParams 新建帖子参数
     * @param requestCode    大于0， 返回数据/结果
     */
    public static void openLocalNote(Context context, NoteOpenParams noteOpenParams, int
            requestCode) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent();
        intent.setClass(context, MediaPaperActivity.class);
        intent.putExtra(MediaPaperActivity.KEY_NOTE_OPEN_PARAMS, noteOpenParams);
        try {
            if (requestCode > 0) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开在线帖子
     *
     * @param context
     * @param courseInfo  帖子的相关信息
     * @param isCloudBar  是否从云贴吧打开
     * @param isStudyTask 是否从学习任务打开
     */
    public static void openOnlineNote(Context context, CourseInfo courseInfo, boolean
            isCloudBar, boolean isStudyTask) {
        if (context == null || courseInfo == null) {
            return;
        }
        if (courseInfo != null && context != null) {
            new WawaCourseUtils((Activity) context).updateVideoViewCount(
                    courseInfo.getId(), null, true);
        }
        Intent intent = new Intent();
        intent.setClass(context, OnlineMediaPaperActivity.class);
        intent.putExtra(MediaPaperActivity.KEY_IS_PAD, false);
        intent.putExtra(MediaPaperActivity.KEY_COURSE_INFO, courseInfo);
        if (courseInfo != null) {
            intent.putExtra(MediaPaperActivity.KEY_PAPER_PATH, courseInfo.getResourceurl());
            intent.putExtra(MediaPaperActivity.KEY_ORIENTATION, courseInfo.getScreenType());
        }
        intent.putExtra(MediaPaperActivity.KEY_IS_CLOUD_BAR, isCloudBar);
        intent.putExtra(MediaPaperActivity.KEY_IS_STUDY_TASK, isStudyTask);
        //用于解决二次编辑帖子返回不刷新问题
        ((Activity) context).startActivityForResult(intent, CampusPatrolPickerFragment.
                EDIT_NOTE_DETAILS_REQUEST_CODE);
    }

    /**
     * 打开在线点读课件
     *
     * @param activity
     * @param info          点读课件相关信息
     * @param isShareScreen 是否投屏
     * @param playbackParam 播放其他参数
     */
    public static void openOnlineOnePage(final Activity activity, final NewResourceInfo info, final boolean
            isShareScreen, final PlaybackParam playbackParam) {
        if (info == null || activity == null) {
            return;
        }
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper(activity);
        helper.setResId(info.getIntegerFormatMicroId())
                .setResType(info.getResourceType())
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        if (flag) {
                            //替换resUrl的ip
                            info.setResourceUrl(helper.getChangeIPUrl(info.getResourceUrl()));
                        }
                        openPlayOnlineOnePage(activity, info, isShareScreen, playbackParam);
                    }
                }).checkIP();


    }

    private static void openPlayOnlineOnePage(final Activity activity, NewResourceInfo info, boolean
            isShareScreen, PlaybackParam playbackParam) {
        //更新点读课件阅读数
        if (info.getIntegerFormatMicroId() >= 0) {
            new WawaCourseUtils(activity).updateVideoViewCount(
                    info.getIntegerFormatMicroId(), null, false);
        }

        CreateSlideHelper.OpenCHWParam param = new CreateSlideHelper.OpenCHWParam(activity, info.getResourceUrl(),
                new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        // 加载失败给提示信息
                        if (!(Boolean) result) {
                            TipMsgHelper.ShowMsg(activity, activity.getString(R.string.cs_loading_error));
                        }
                    }
                });
        param.microID = String.valueOf(info.getResourceId()); // 课件Id-Type
        param.courseInfo = info.getCourseInfo(); // 课件信息，用于获取收藏和分享数据
        param.orientation = info.getScreenType(); // 屏幕方向
        param.destActivity = PlaybackWawaPageActivityPhone.class; // 跳转Activity
        param.isShareScreen = isShareScreen;  // 是否投屏
        param.playbackParam = playbackParam;  // 播放其他参数
        CreateSlideHelper.loadAndOpenSlideByCHWUrl(param);
    }

    /**
     * 打开在线录音课件
     *
     * @param context
     * @param courseInfo    课件信息
     * @param isShareScreen 是否投屏
     * @param playbackParam 播放额外参数
     */
    public static void playOnlineCourse(final Context context, final CourseInfo courseInfo,
                                        final boolean isShareScreen, final PlaybackParam playbackParam) {
        if (context == null || courseInfo == null) {
            return;
        }
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper((Activity) context);
        helper.setResId(courseInfo.getId())
                .setResType(courseInfo.getType())
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        if (flag) {
                            //替换resUrl的ip
                            courseInfo.setResourceurl(helper.getChangeIPUrl(courseInfo.getResourceurl()));
                        }
                        openPlayOnlineCourse(context, courseInfo, isShareScreen, playbackParam);
                    }
                }).checkIP();
    }

    public static void playOnlineCourse(final Context context, final CourseInfo courseInfo, CommitTask data,
                                        final boolean isShareScreen, final PlaybackParam playbackParam) {
        if (context == null || courseInfo == null) {
            return;
        }
        commitTask = data;
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper((Activity) context);
        helper.setResId(courseInfo.getId())
                .setResType(courseInfo.getType())
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        if (flag) {
                            //替换resUrl的ip
                            courseInfo.setResourceurl(helper.getChangeIPUrl(courseInfo.getResourceurl()));
                        }
                        openPlayOnlineCourse(context, courseInfo, isShareScreen, playbackParam);
                    }
                }).checkIP();
    }


    private static void openPlayOnlineCourse(Context context, CourseInfo courseInfo,
                                             boolean isShareScreen, PlaybackParam playbackParam) {
        // 更新课件播放次数
        if (courseInfo != null && context != null) {
            new WawaCourseUtils((Activity) context).updateVideoViewCount(
                    courseInfo.getId(), null, false);
        }

        if (courseInfo != null
                && !TextUtils.isEmpty(courseInfo.getResourceurl())) {
            // 去除.zip及之后字符串, 获得课件打开路径
            String courseUrl = courseInfo.getResourceurl();
            if (courseUrl.endsWith(".zip")) {
                courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf('.'));
            } else if (courseUrl.contains(".zip?")) {
                courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf(".zip?"));
            }

            CourseShareData shareData = getCourseShareData(courseInfo);
            int resType = courseInfo.getResourceType();
            if (resType == ResType.RES_TYPE_ONEPAGE
                    || resType == ResType.RES_TYPE_COURSE_SPEAKER
                    || resType == ResType.RES_TYPE_STUDY_CARD
                    || resType == ResType.RES_TYPE_OLD_COURSE
                    || resType == ResType.RES_TYPE_COURSE
                    || resType == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD)
                    || resType == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_COURSE_SPEAKER)) {
                String typeName = context.getString(R.string.retell_course);
                if (resType == ResType.RES_TYPE_STUDY_CARD
                        || resType == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD)) {
                    typeName = context.getString(R.string.make_task);
                }
                String nickname = context.getString(R.string.str_resources_tag, typeName, courseInfo
                        .getNickname());
                shareData.setTitle(nickname);
                String createName = courseInfo.getCreatename();
                if (!TextUtils.isEmpty(createName)) {
                    if (createName.length() > 10) {
                        createName = createName.substring(0, 10);
                        createName = createName + "...";
                    }
                    createName = createName + "\n" + context.getString(R.string
                            .Str_view_people, String.valueOf(courseInfo.getViewcount()));
                    shareData.setAuthor(createName);
                }
            }
            Intent intent = getIntentForPlayCourse(context,
                    courseUrl,
                    courseInfo.getNickname(),
                    shareData,
                    courseInfo.getCollectParams(),
                    courseInfo.getScreenType(),
                    courseInfo.isSlide(),
                    courseInfo.isSplitCourse(),
                    isShareScreen,
                    courseInfo);
            if (playbackParam != null) {
                Bundle extras = intent.getExtras();
                extras.putSerializable(PlaybackParam.class.getSimpleName(), playbackParam);
                intent.putExtras(extras);
            }
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            commitTask = null;
        }
    }

    @NonNull
    private static CourseShareData getCourseShareData(CourseInfo courseInfo) {
        CourseShareData shareData = new CourseShareData();
        shareData.setTitle(courseInfo.getNickname());
        shareData.setAuthor(courseInfo.getCreatename());
        shareData.setThumbnail(courseInfo.getImgurl());
        shareData.setId(courseInfo.getId());
        if (!TextUtils.isEmpty(courseInfo.getShareAddress())) {
            shareData.setShareAddress(courseInfo.getShareAddress());
        }
        shareData.setSharedResource(courseInfo.getSharedResource());
        return shareData;
    }


    /**
     * 获取打开在线课件Intent
     *
     * @param context
     * @param courseUrl     课件路径
     * @param title         课件标题
     * @param shareData     分享数据
     * @param params        收藏数据
     * @param screenType    屏幕方向
     * @param isSlide       是否显示点读页面
     * @param isSplit       是否拆分课件
     * @param isShareScreen 是否投屏
     * @return
     */
    public static Intent getIntentForPlayCourse(
            Context context,
            String courseUrl,
            String title,
            CourseShareData shareData,
            CollectParams params,
            int screenType,
            boolean isSlide,
            boolean isSplit,
            boolean isShareScreen,
            CourseInfo courseInfo) {

        // 确保打开课件时缓存目录已创建
        Utils.createLocalDiskPath(Utils.ONLINE_FOLDER);

        Intent intent = new Intent(context, PlaybackActivityPhone.class);
        Bundle extras = new Bundle();
        extras.putString(PlaybackActivity.FILE_PATH, courseUrl);
        extras.putString(PlaybackActivity.EXTRA_TITLE, title);
        extras.putString(PlaybackActivity.CACHE_ROOT, Utils.ONLINE_FOLDER);
        extras.putInt(PlaybackActivity.ORIENTATION, screenType);
        extras.putBoolean(PlaybackActivity.IS_SHOW_SLIDE, isSlide);
        extras.putBoolean(PlaybackActivity.IS_SPLIT, isSplit);
        if (commitTask != null) {
            extras.putInt(PlaybackActivity.EXTRA_EVALUATE_SCHEME_ID, commitTask.getAutoEvalCompanyType());
            extras.putString(PlaybackActivity.EXTRA_EVALUATE_RESULT, commitTask.getAutoEvalContent());
        }
        if (shareData != null) {
            extras.putParcelable(PlaybackActivity.COURSE_SHARE_DATA, shareData);
        }
        if (params != null) {
            extras.putParcelable(PlaybackActivity.COURSE_COLLECT_PARAMS, params);
        }
        //语音评测文字的显示
        extras.putBoolean(PlaybackActivity.EXTRA_EVALUATE_MARK_ENABLED, true);
        extras.putString(PlaybackActivity.EXTRA_EVALUATE_RES_ID, String.valueOf(courseInfo.getId()));
        SlideInPlaybackParam slideInPlaybackParam = getSlideInPlaybackParam(context, isShareScreen,
                screenType);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(), slideInPlaybackParam);
        intent.putExtras(extras);
        return intent;
    }

    /**
     * 获取打开本地录音课件Intent
     *
     * @param context
     * @param courseUrl     课件路径
     * @param title         课件标题
     * @param description   课件描述
     * @param orientation   屏幕方向
     * @param resType       课件类型
     * @param isEdit        是否编辑
     * @param isShareScreen 是否投屏
     * @return
     */
    public static Intent getIntentForPlayLocalCourse(
            Context context, String courseUrl, String title, String description,
            int orientation, int resType, boolean isEdit, boolean isShareScreen) {
        Intent intent = new Intent(context, PlaybackActivityNew.class);
        Bundle extras = new Bundle();
        extras.putString(PlaybackActivity.FILE_PATH, courseUrl);
        extras.putString(PlaybackActivity.EXTRA_TITLE, title);
        extras.putString(PlaybackActivity.EXTRA_DESCRIPTION, description);
        extras.putInt(PlaybackActivity.ORIENTATION, orientation);
        extras.putInt(PlaybackActivity.PLAYBACK_TYPE, resType);
        extras.putBoolean(PlaybackActivity.EXTRA_EDIT_MODE, isEdit);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(),
                getSlideInPlaybackParam(context, isShareScreen, orientation));
        intent.putExtras(extras);
        return intent;
    }

    /**
     * @param context
     * @param isShareScreen 是否投屏
     * @param orientation   屏幕方向，用于设置制作时的屏幕宽高比例
     * @return
     */
    public static SlideInPlaybackParam getSlideInPlaybackParam(Context context, boolean
            isShareScreen, int orientation) {
        MyApplication application = ((MyApplication) context.getApplicationContext());
        SlideInPlaybackParam param = new SlideInPlaybackParam();
        param.mCurUser = new User();
        UserInfo userInfo = application.getUserInfo();
        if (userInfo != null) {
            param.mCurUser.mId = userInfo.getMemberId();
            if (!TextUtils.isEmpty(userInfo.getRealName())) {
                param.mCurUser.mName = userInfo.getRealName();
            } else {
                param.mCurUser.mName = userInfo.getNickName();
            }
        }
//        param.mIsShareScreen = isShareScreen;
        //隐藏投屏
        param.mIsShareScreen = false;

        //support A4 paper ratio
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            param.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            param.mRatioScreenWToH = 210.0f / 297.0f;
        }
        int[] rayMenuV = {
                BaseSlideManager.MENU_ID_CAMERA,
                BaseSlideManager.MENU_ID_IMAGE,
                BaseSlideManager.MENU_ID_WHITEBOARD,
                BaseSlideManager.MENU_ID_PAGE_HORN_AUDIO,
                BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
        };
        if (userInfo != null && userInfo.isTeacher()) {
            rayMenuV = Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
            rayMenuV[rayMenuV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
        }
        param.mRayMenusV = rayMenuV;
        int[] rayMenuH = {BaseSlideManager.MENU_ID_PAGE_HORN_RECORD, BaseSlideManager.MENU_ID_CURVE,
                BaseSlideManager.MENU_ID_ERASER};
        param.mRayMenusH = rayMenuH;
        return param;
    }


    public static void openCommentList(Context context, CourseInfo courseInfo, int orientation) {
        Intent intent = new Intent(context, ShellActivity.class);
        intent.putExtra("Window", "noteComment");
        intent.putExtra("isPortrait", true);
        intent.putExtra(ShellActivity.EXTRA_ORIENTAION, orientation);
        intent.putExtra(CourseInfo.class.getSimpleName(), courseInfo);
        context.startActivity(intent);
    }

    public static void openCourseDetail(Activity activity, NewResourceInfo info, int fromType) {
        openCourseDetail(activity, info, fromType, null);
    }

    public static void openCourseDetail(Activity activity, NewResourceInfo info, int fromType,
                                        Bundle bundle) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, info);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        //微课详情页面更新讨论
        activity.startActivityForResult(intent, CampusPatrolPickerFragment.
                REQUEST_CODE_DISCUSSION_COURSE_DETAILS);
    }

    //兼容noc大赛进详情
    public static void openCourseDetail(Activity activity, int fromType, String courseId, int
            resType, NocEnterDetailArguments arguments) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
        intent.putExtra(PictureBooksDetailActivity.EXTRA_COURSE_ID, courseId);
        intent.putExtra(PictureBooksDetailActivity.EXTRA_COURSE_RESTYPE, resType);
        intent.putExtra(NocEnterDetailArguments.class.getSimpleName(), arguments);
        activity.startActivity(intent);
    }

    public static void enterClassDetialActivity(Activity activity,
                                                String classId,
                                                int fromType,
                                                boolean hasJoin,
                                                String schoolName,
                                                String contactId,
                                                String schoolId,
                                                String groupId) {
        enterClassDetialActivity(activity, classId, fromType, hasJoin, schoolName, contactId, schoolId,
                groupId, false);
    }

    public static void enterClassDetialActivity(Activity activity,
                                                String classId,
                                                int fromType,
                                                boolean hasJoin,
                                                String schoolName,
                                                String contactId,
                                                String schoolId,
                                                String groupId,
                                                boolean isHasInspectAuth) {
        Intent intent = new Intent(activity, ClassDetailsActivity.class);
        Bundle args = new Bundle();
        args.putString(ClassDetailsActivity.GROUP_ID, groupId);
        args.putString(ClassDetailsActivity.SCHOOL_ID, schoolId);
        args.putString(ClassDetailsActivity.CLASS_ID, classId);
        args.putInt(ClassDetailsActivity.FROM_TYPE, fromType);
        args.putBoolean(ClassDetailsActivity.IS_JOIN_CLASS, hasJoin);
        args.putString(ClassDetailsActivity.SCHOOL_NAME, schoolName);
        args.putString(ClassDetailsActivity.CONTACT_ID, contactId);
        args.putBoolean(ClassDetailsActivity.EXTRA_CONTACTS_HAS_INSPECT_AUTH, isHasInspectAuth);
        intent.putExtras(args);
        //老师通讯录
        activity.startActivityForResult(intent, ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS);
    }

    public static void enterClassDetialActivity(Activity activity, String classId, int fromType,
                                                boolean hasJoin, String schoolName, String
                                                        contactId, String schoolId, String
                                                        groupId, int classState) {
        Intent intent = new Intent(activity, ClassDetailsActivity.class);
        Bundle args = new Bundle();
        args.putString(ClassDetailsActivity.GROUP_ID, groupId);
        args.putString(ClassDetailsActivity.SCHOOL_ID, schoolId);
        args.putString(ClassDetailsActivity.CLASS_ID, classId);
        args.putInt(ClassDetailsActivity.FROM_TYPE, fromType);
        args.putBoolean(ClassDetailsActivity.IS_JOIN_CLASS, hasJoin);
        args.putString(ClassDetailsActivity.SCHOOL_NAME, schoolName);
        args.putString(ClassDetailsActivity.CONTACT_ID, contactId);
        args.putInt(ClassDetailsActivity.CLASS_STATE, classState);
        intent.putExtras(args);
        //班级二维码
        activity.startActivityForResult(intent, ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS);
    }

    public static void enterClassDetialActivity(Activity activity, String classId, int fromType,
                                                boolean hasJoin, String schoolName, String
                                                        contactId, String schoolId, String
                                                        groupId, int classState, SchoolInfo schoolInfo) {
        Intent intent = new Intent(activity, ClassDetailsActivity.class);
        Bundle args = new Bundle();
        args.putString(ClassDetailsActivity.GROUP_ID, groupId);
        args.putString(ClassDetailsActivity.SCHOOL_ID, schoolId);
        args.putString(ClassDetailsActivity.CLASS_ID, classId);
        args.putInt(ClassDetailsActivity.FROM_TYPE, fromType);
        args.putBoolean(ClassDetailsActivity.IS_JOIN_CLASS, hasJoin);
        args.putString(ClassDetailsActivity.SCHOOL_NAME, schoolName);
        args.putString(ClassDetailsActivity.CONTACT_ID, contactId);
        args.putInt(ClassDetailsActivity.CLASS_STATE, classState);
        args.putSerializable(ActivityUtils.EXTRA_SCHOOL_INFO, schoolInfo);
        intent.putExtras(args);
        //班级二维码
        activity.startActivityForResult(intent, ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS);
    }


    public static void gotoHome(Context activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }

    public static void enterLogin(Activity activity) {
        enterLogin(activity, false);
    }

    public static void enterLogin(Activity activity, boolean isEnterHome) {
        Intent intent = new Intent(activity, AccountActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, isEnterHome);
        intent.putExtras(args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    public static void gotoCreateClass(Context context, String schoolId) {
        Intent intent = new Intent(context, ShellActivity.class);
        intent.putExtra("Window", "createClass");
        intent.putExtra("SchoolId", schoolId);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void gotoTelephone(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + WAWACHAT_PHONENUMBER));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void gotoScanQRCode(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, CaptureActivity.class);
        context.startActivity(intent);
    }

    public static void gotoQrcodeScanning(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, CaptureActivity.class);
        context.startActivity(intent);
    }


    public static void gotoTopicSpaceCourseList(Activity activity, CourseInfo courseInfo) {
        Intent intent = new Intent(activity, ShellActivity.class);
        intent.putExtra("Window", "topicSpaceCourseList");
        intent.putExtra(CourseInfo.class.getSimpleName(), courseInfo);
        activity.startActivity(intent);
    }

    public static void gotoMediaTypeList(Activity activity, List<SchoolInfo> schoolInfoList) {
        Intent intent = new Intent();
        intent.setClass(activity, ShellActivity.class);
        if (schoolInfoList != null && schoolInfoList.size() > 0) {
            intent.putExtra("Window", "media_type_list");
        }
        intent.putExtra(ActivityUtils.EXTRA_SCHOOL_INFO_LIST, (Serializable) schoolInfoList);
        activity.startActivity(intent);
    }

    public static void openNews(Activity activity, String url, String title) {
        Intent it = new Intent();
        it.setClass(activity, FileActivity.class);
        it.putExtra(FileActivity.EXTRA_CONTENT_URL, url);
        it.putExtra(FileActivity.EXTRA_TITLE, title);
        activity.startActivity(it);
    }

    public static void openImage(Activity context, List<ImageInfo> mediaInfos, boolean isPDF, int index) {
        openImage(context, mediaInfos, isPDF, index, true);
    }

    public static void openImage(Activity context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn) {
        openImage(context, mediaInfos, isPDF, index, isHideMoreBtn, false);
    }

    /**
     * @param context
     * @param mediaInfos
     * @param index
     * @param isHideMoreBtn          true:屏蔽右上角更多按钮
     * @param isShowCourseAndReading 是否显示“我要做课件”，“我要加点读”
     */

    public static void openImage(Activity context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn, boolean isShowCourseAndReading) {
        GalleryActivity.newInstance(context, mediaInfos, isPDF, index, isHideMoreBtn, isShowCourseAndReading);
    }

    public static void openImage(Activity context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn, boolean isShowCourseAndReading, boolean isShowCollect) {
        GalleryActivity.newInstance(context, mediaInfos, isPDF, index, isHideMoreBtn, isShowCourseAndReading, isShowCollect);
    }

    public static void openLocalCourseIntroducationDetail(Activity activity, LocalCourseInfo info) {
        NewResourceInfo resourceInfo = new NewResourceInfo();
        if (info != null) {
            resourceInfo.setResourceUrl(info.mPath);
            resourceInfo.setTitle(info.mTitle);
            resourceInfo.setThumbnail(info.mPath + File.separator
                    + Utils.RECORD_HEAD_IMAGE_NAME);
            resourceInfo.setDescription(info.mDescription);
        }
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(NewResourceInfo.class.getSimpleName(), resourceInfo);
        intent.putExtra(LocalCourseInfo.class.getSimpleName(), info);
        intent.putExtra(EXTRA_TASK_TYPE, true);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_MY_WORK);
        activity.startActivityForResult(intent, IntroductionForReadCourseFragment.ISLOCALCOURSECHANGE);
    }

    public static void openPictureDetailActivity(Activity activity, NewResourceInfo item) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        //微课详情页面更新讨论
        activity.startActivityForResult(intent, CampusPatrolPickerFragment.
                REQUEST_CODE_DISCUSSION_COURSE_DETAILS);
    }

    public static void openSelectedReadingDetailActivity(Activity activity, NewResourceInfo item,
                                                         StudyTaskInfo task, int roleType, String
                                                                 studentId, UserInfo stuUserInfo) {
        Intent intent = new Intent(activity, SelectedReadingDetailActivity.class);
        intent.putExtra(SelectedReadingDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(SelectedReadingDetailActivity.STUDY_TASK_INFO, task);
        intent.putExtra(SelectedReadingDetailActivity.ROLE_TYPE, roleType);
        intent.putExtra(SelectedReadingDetailActivity.STUDENT_ID, studentId);
        intent.putExtra(SelectedReadingDetailActivity.STUDENT_USERINFO, stuUserInfo);
        //刷新讨论列表
        activity.startActivityForResult(intent,
                CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_INTRODUCTION);
    }

    public static void openPictureDetailActivity(Activity activity, NewResourceInfo item, int
            fromType, boolean isFromCatalog) {
        openPictureDetailActivity(activity, item, fromType, isFromCatalog, null);
    }

    public static void openPictureDetailActivity(Activity activity, NewResourceInfo item, int
            fromType, boolean isFromCatalog, Bundle bundle) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
        intent.putExtra(PictureBooksDetailActivity.EXTRA_IS_FROM_CATALOG, isFromCatalog);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    public static void openPictureDetailActivity(Activity activity, NewResourceInfo item, boolean
            isFromCatalog) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        intent.putExtra(PictureBooksDetailActivity.EXTRA_IS_FROM_CATALOG, isFromCatalog);
        activity.startActivity(intent);
    }

    public static void openPictureDetailActivity(Activity activity, NewResourceInfo item, String
            schoolId, String feeSchoolId, boolean isFromCatelog) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        intent.putExtra(PictureBooksDetailActivity.EXTRA_IS_FROM_CATALOG, isFromCatelog);
        if (!TextUtils.isEmpty(schoolId)) {
            intent.putExtra(PictureBooksDetailActivity.EXTRA_SCHOOL_ID, schoolId);
        }

        if (!TextUtils.isEmpty(feeSchoolId)) {
            intent.putExtra(PictureBooksDetailActivity.EXTRA_FEE_SCHOOL_ID, feeSchoolId);
        }
        activity.startActivity(intent);
    }

    public static void openPictureDetailActivity(Activity activity, NewResourceInfo item, String
            schoolId, String feeSchoolId) {
        Intent intent = new Intent(activity, PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        if (!TextUtils.isEmpty(schoolId)) {
            intent.putExtra(PictureBooksDetailActivity.EXTRA_SCHOOL_ID, schoolId);
        }

        if (!TextUtils.isEmpty(feeSchoolId)) {
            intent.putExtra(PictureBooksDetailActivity.EXTRA_FEE_SCHOOL_ID, feeSchoolId);
        }
        activity.startActivity(intent);
    }

    public static void gotoPicBookCategorySelector(Activity activity, UploadParameter uploadParameter) {
        Intent intent = new Intent();
        intent.setClass(activity, ShellActivity.class);
        intent.putExtra("Window", "picbook_category_selector");
        intent.putExtra(UploadParameter.class.getSimpleName(), uploadParameter);
        activity.startActivity(intent);

    }

    /**
     * 进入机构名片
     *
     * @param activity
     * @param schoolId
     */
    public static void enterSchoolSpace(Activity activity, String schoolId) {

        if (TextUtils.isEmpty(schoolId)) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, schoolId);
        Intent intent = new Intent(activity, SchoolSpaceActivity.class);
        intent.putExtras(args);
        //机构信息
        activity.startActivityForResult(intent, SchoolSpaceFragment.REQUEST_CODE_SCHOOL_SPACE);
    }

    /**
     * 进入个人名片
     *
     * @param activity
     * @param memberId
     */
    public static void enterPersonalSpace(Activity activity, String memberId) {
        if (TextUtils.isEmpty(memberId)) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, memberId);
        Intent intent = new Intent(activity, PersonalSpaceActivity.class);
        intent.putExtras(args);

        activity.startActivityForResult(intent, PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE);

    }

    public static void exit(final Activity activity, final boolean isFinishActivity) {

        ContactsMessageDialog messageDialog = new ContactsMessageDialog(activity, null,
                activity.getString(R.string.sure_to_exit), activity.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logout(activity, isFinishActivity);
            }
        });
        messageDialog.show();
//        Window window = messageDialog.getWindow();
//        WindowManager windowManager = activity.getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = (int)(display.getWidth()*4/5);
//        window.setAttributes(lp);
    }

    private static void logout(Activity activity, boolean isFinishActivity) {
        ((MyApplication) activity.getApplication()).clearLoginUserInfo();
        ((MyApplication) activity.getApplication()).stopDownloadService();
        PushUtils.stopPush(activity);
//        ConversationHelper.logout();
        if (activity != null) {
            UserHelper.logout();
            Intent intent = new Intent(HomeActivity.ACTION_ACCOUNT_LOGOUT);
            activity.sendBroadcast(intent);
            if (isFinishActivity) {
                activity.finish();
            }
            intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
        }
    }

    public static void enterBookStoreListActivity(Activity activity, SchoolInfo schoolInfo) {
        enterBookStoreListActivity(activity, schoolInfo, false);
    }

    public static void enterBookStoreListActivity(Activity activity, SchoolInfo schoolInfo,
                                                  boolean isFromChoiceLib) {
        Intent intent = new Intent(activity, BookStoreListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("schoolInfo", schoolInfo);
        if (schoolInfo != null) {
            args.putBoolean(EXTRA_IS_TEACHER, schoolInfo.isTeacher());
        }
        args.putBoolean(IS_FROM_CHOICE_LIB, isFromChoiceLib);
        if (!isFromChoiceLib && schoolInfo != null) {
            boolean isVipSchool = VipConfig.CheckVipSchoolId(schoolInfo.getSchoolId());
            //关注的学校才赋值vipSchool的权限
            if (isVipSchool && schoolInfo.getState() != 0) {
                args.putBoolean(IS_LQWWA_VIP_SCHOOL, true);
            }
        }
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public static void enterSchoolClassListActivity(Activity activity, SchoolInfo schoolInfo) {
        if (schoolInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(SchoolClassListActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
        args.putString(SchoolClassListActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
        args.putBoolean(SchoolClassListActivity.EXTRA_IS_TEACHER, schoolInfo.isTeacher());
        Intent intent = new Intent(activity, SchoolClassListActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    //个人信息详情
    public static void enterUserDetails(Activity activity, UserInfo userInfo, String origin) {
        Intent intent = new Intent();
        intent.setClass(activity, BasicUserInfoActivity.class);
        intent.putExtra("origin", origin);
        intent.putExtra("userInfo", userInfo);
        activity.startActivityForResult(intent, ActivityUtils.REQUEST_CODE_BASIC_USER_INFO);
    }

    /**
     * 进入校园巡查----筛选页面
     *
     * @param activity
     */
    public static void enterCampusPatrolPickerActivity(Activity activity) {
        Intent intent = new Intent(activity, CampusPatrolPickerActivity.class);
        activity.startActivityForResult(intent, CampusPatrolPickerFragment.REQUEST_CODE);
    }

    public static void enterCampusDirect(Activity activity, SchoolInfo schoolInfo) {
        if (schoolInfo != null) {
            String title = null;
            String url = null;
            if (TextUtils.isEmpty(schoolInfo.getLiveShowUrl())) {
                title = activity.getString(R.string.campus_live_show_nosupport_title);
                url = "";
            } else {
                title = activity.getString(R.string.campus_now_direct);
                url = schoolInfo.getLiveShowUrl();
                enterMoreCampusOnlineEvent(activity, schoolInfo, url);
                return;
            }
            //进入校园电视台宣传页面
            WebUtils.enterCampusTVPromotionPageActivity(activity, url, null, title,
                    "campus_live_show", schoolInfo);
        }
    }

    public static void enterMoreCampusOnlineEvent(Activity activity, SchoolInfo schoolInfo, String
            url) {
        Intent intent = new Intent(activity, BroadcastNoteActivity.class);
        intent.putExtra(BroadcastNoteActivity.EXTRA_SCHOOL_INFO, schoolInfo);
        intent.putExtra(BroadcastNoteActivity.EXTRA_CONTENT_URL, url);
        intent.putExtra(BroadcastNoteActivity.IS_FROM_CAMPUS_ONLINE, true);
        activity.startActivity(intent);
    }

    public static void enterTaskOrderFragment(FragmentManager fragmentManager, Bundle args) {
        if (fragmentManager == null) return;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (args == null) {
            args = new Bundle();
        }
        args.putBoolean(TaskOrderFragment.IS_PICK, true);
        TaskOrderFragment fragment = new TaskOrderFragment();
        fragment.setArguments(args);
        ft.replace(R.id.activity_body, fragment, TaskOrderFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    //任务单详情页
    public static void enterTaskOrderDetailActivity(Activity activity, NewResourceInfo item) {
        enterTaskOrderDetailActivity(activity, item, null);
    }

    public static void enterTaskOrderDetailActivity(Activity activity, NewResourceInfo item,
                                                    PassParamhelper paramhelper) {
        Intent intent = new Intent(activity, TaskOrderDetailActivity.class);
        intent.putExtra(NewResourceInfo.class.getSimpleName(), item);
        if (paramhelper != null) {
            intent.putExtra(PassParamhelper.class.getSimpleName(), paramhelper);
        }
        activity.startActivity(intent);
    }

    public static void enterIntroductionCourseActivity(Activity activity,
                                                       String title,
                                                       int type,
                                                       SchoolInfo schoolInfo,
                                                       boolean isFromSuperTask) {
        enterIntroductionCourseActivity(activity, title, type, schoolInfo, isFromSuperTask,
                false, null, null, null);
    }

    public static void enterIntroductionCourseActivity(Activity activity,
                                                       String title,
                                                       int type,
                                                       SchoolInfo schoolInfo,
                                                       boolean isFromSuperTask,
                                                       boolean isOnlineClass,
                                                       String classId,
                                                       String schoolId,
                                                       UploadParameter uploadParameter) {
        if (activity == null) {
            return;
        }
        Intent intent = null;
        intent = new Intent(activity, IntroductionForReadCourseActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_HEADER_TITLE, title);
        if (schoolInfo != null) {
            intent.putExtra(SchoolInfo.class.getSimpleName(), schoolInfo);
        }
        intent.putExtra(ActivityUtils.EXTRA_DEFAULT_DATE, DateUtils.getCurDate());
        intent.putExtra(ActivityUtils.EXTRA_TASK_TYPE, type);
        intent.putExtra(ActivityUtils.EXTRA_FROM_SUPER_TASK, isFromSuperTask);
        intent.putExtra(ActivityUtils.EXTRA_IS_ONLINE_CLASS, isOnlineClass);
        if (!TextUtils.isEmpty(classId)) {
            intent.putExtra(ActivityUtils.EXTRA_CLASS_ID, classId);
        }
        if (!TextUtils.isEmpty(schoolId)) {
            intent.putExtra(ActivityUtils.EXTRA_SCHOOL_ID, schoolId);
        }
        if (uploadParameter != null) {
            intent.putExtra(UploadParameter.class.getSimpleName(), uploadParameter);
        }
        activity.startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }


    //未完成/已完成
    public static void enterHomeworkFinishStatusActivity(Activity activity,
                                                         HomeworkListInfo homeworkListInfo,
                                                         int roleType,
                                                         String TaskId) {
        if (activity == null || homeworkListInfo == null) {
            return;
        }
        Intent intent = new Intent(activity, HomeworkFinishStatusActivity.class);
        Bundle bundle = new Bundle();
        //角色信息
        bundle.putInt(HomeworkFinishStatusActivity.Constants.ROLE_TYPE, roleType);
        bundle.putString(HomeworkFinishStatusActivity.Constants.TASK_ID, TaskId);
        bundle.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, homeworkListInfo.getTaskTitle());
        //类型信息
        bundle.putInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE, Integer.valueOf(homeworkListInfo
                .getTaskType()));
        bundle.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER, homeworkListInfo.isOnlineReporter() || homeworkListInfo.isOnlineHost());
        bundle.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_IS_SUPER_CHILD_TASK,
                homeworkListInfo.isSuperChildTask());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


    public static void enterStudentTaskListDetail(Activity activity,
                                                  HomeworkListInfo data,
                                                  Bundle bundle,
                                                  String studentName,
                                                  String sortStudentId) {
        if (activity == null || data == null || bundle == null) {
            return;
        }
        Intent intent = new Intent(activity, StudentFinishedHomeworkListActivity.class);
        Bundle args = bundle;
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_ID, String.valueOf(data.getTaskId()));
        args.putInt(HomeworkFinishStatusActivity.Constants.TASK_TYPE, Integer.valueOf(data.getTaskType()));
        //来自学习任务完成列表
        args.putBoolean(HomeworkFinishStatusActivity.Constants.FROM_HOMEWORK_FINISH_STAUS_LIST, true);
        //名称是学生姓名
        args.putString(HomeworkFinishStatusActivity.Constants.TASK_TITLE, studentName);
        args.putString(HomeworkFinishStatusActivity.Constants.STUDENT_ID, sortStudentId);
        args.putString(HomeworkFinishStatusActivity.Constants.SORT_STUDENT_ID, sortStudentId);
        //请求自己的数据，需要过滤数据。
        args.putBoolean(HomeworkFinishStatusActivity.Constants.NEED_FILTER_DATA, true);
        args.putBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER,
                bundle.getBoolean(HomeworkFinishStatusActivity.Constants.EXTRA_ISONLINEREPORTER));
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public static void enterSchoolMessageActivity(Activity activity, SchoolInfo schoolInfo) {
        if (schoolInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(SchoolMessageListActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
        args.putString(SchoolCourseListActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
        args.putBoolean(SchoolMessageListActivity.EXTRA_IS_TEACHER, schoolInfo.isTeacher());
        args.putBoolean(SchoolMessageListActivity.EXTRA_IS_ONLINE_SCHOOL_MESSAGE, schoolInfo.isOnlineSchool());
        args.putSerializable(SchoolInfo.class.getSimpleName(), schoolInfo);
        Intent intent = new Intent(activity, SchoolMessageListActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    /**
     * 进入学程馆界面
     *
     * @param schoolInfo
     */
    public static void enterLqCourseShopActivity(Activity activity, SchoolInfo schoolInfo) {
        if (schoolInfo == null || activity == null) {
            return;
        }
        OrganCourseClassifyActivity.show(activity, schoolInfo.getSchoolId(),schoolInfo.getRoles());
    }

    /**
     * 进入创建直播的界面
     */

    public static void createOnlineData(Activity activity, Bundle bundle) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, CreateOnlineActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }

    public static void enterClassCourseDetailActivity(Activity activity,
                                                      SchoolInfo schoolInfo,
                                                      SubscribeClassInfo classInfo) {
        if (activity == null || EmptyUtil.isEmpty(classInfo) || EmptyUtil.isEmpty(schoolInfo)) {
            return;
        }
        String roles = classInfo.getRoles();
        ClassCourseParams params = new ClassCourseParams(
                classInfo.isHeadMaster(),
                schoolInfo.getSchoolId(),
                classInfo.getClassId());
        params.setRoles(roles);

        /*ClassCourseParams params = new ClassCourseParams(schoolInfo.getSchoolId(),classInfo.getClassId());

        ArrayList<Integer> filterArray = new ArrayList<Integer>();
        filterArray.add(19);

        ClassResourceData data = new ClassResourceData(
                WatchResourceType.TYPE_RETELL_COURSE,
                1,filterArray,
                0);

        ClassCourseActivity.show(activity,params,data);*/

        ClassCourseActivity.show(activity, params);
    }

    public static void enterHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }
}
