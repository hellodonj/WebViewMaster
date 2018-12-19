package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.course.SlideActivityNew;
import com.galaxyschool.app.wawaschool.course.library.ImportImage;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.ContactsExpandListFragment;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInputParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;
import com.osastudio.common.library.ActivityStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;


/**
 * Author: wangchao
 * Time: 2015/11/13 14:13
 */
public abstract class ResourceBaseFragment extends ContactsExpandListFragment {

    protected ProgressDialog mProgressDialog;

    protected String savePath;

    protected String courseId;
    protected boolean autoMark;
    protected int ScoringRule = -1;

    public static final int REQUEST_CODE_SLIDE = 0;
    public static final int REQUEST_CODE_EDITCOURSE = 1;
    public static final int REQUEST_CODE_RETELLCOURSE = 2;
    public static final int REQUEST_CODE_DO_SLIDE_TOAST = 3;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ImportImage.MSG_IMPORT_FINISH:
                    dismissProcessDialog();
                    saveData(msg);
                    break;

            }
        }
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showProcessDialog(String title) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(
                    getActivity(),
                    ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(0);
        mProgressDialog.setMessage(title);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissProcessDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void enterLocalCourse(LocalCourseInfo info) {
        if (getActivity() == null) {
            return;
        }
        Utils.createLocalDiskPath(Utils.ONLINE_FOLDER);
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent intent = ActivityUtils.getIntentForPlayLocalCourse(
                getActivity(), path,
                info.mTitle, info.mDescription, info.mOrientation, resType, true, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_CODE_EDITCOURSE);
    }

    public abstract void importResource(String title, int type);

    ContactsInputBoxDialog showEditDialog(
            String dialogTitle, String dataTitle,
            DialogInterface.OnClickListener confirmButtonClickListener) {
        ContactsInputBoxDialog dialog = new ContactsInputBoxDialog(
                getActivity(), dialogTitle, dataTitle,
                getString(R.string.pls_enter_title), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, getString(R.string.confirm), confirmButtonClickListener);
        dialog.show();
        return dialog;
    }

    protected void showMessageDialog(final String message, DialogInterface.OnClickListener confirmButtonClickListener) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", message, getString(R.string.cancel), null,
                getString(R.string.ok), confirmButtonClickListener);
        dialog.show();
//        resizeDialog(dialog, 0.9f);
    }


    protected void saveData(Message msg) {
        LocalCourseInfo localCourseInfo = (LocalCourseInfo) msg.obj;
        if (localCourseInfo != null) {
            saveData(localCourseInfo);
        }
    }

    protected void saveData(LocalCourseInfo courseInfo) {
        if (courseInfo != null) {
            courseInfo.mParentPath = Utils.removeFolderSeparator(courseInfo.mParentPath);
            courseInfo.mPath = Utils.removeFolderSeparator(courseInfo.mPath);
            LocalCourseDTO localCourseDTO = courseInfo.toLocalCourseDTO();
            localCourseDTO.setmType(CourseType.COURSE_TYPE_IMPORT);
            localCourseDTO.setmMemberId(getMemeberId());
            LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
            if (localCourseDTO != null) {
                localCourseDao.addOrUpdateLocalCourseDTO(localCourseDTO);
            }
        }
    }


    protected void createStudyTaskNewRetellCourseByTaskType(int orientation, String originVoicePath,
                                                            boolean WatchWawaCourse) {
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        it.putExtra(SlideActivityNew.ISNEEDDIRECTORY, true);
        //处理新/老版看课件复述课件逻辑
        if (WatchWawaCourse) {
            it.putExtra(SlideActivityNew.COURSETYPEFROM,
                    SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        } else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM,
                    SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
        }
        SlideInputParam slideInputParam = getSlideInputParam(true, true);
        slideInputParam.mOriginVoicePath = originVoicePath;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        startActivityForResult(it, REQUEST_CODE_SLIDE);
    }

    protected void createNewRetellCourse(int orientation, String originVoicePath, String title) {
        if (getActivity() == null) {
            return;
        }
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, title);
        SlideInputParam slideInputParam = getSlideInputParam(true, true);
        slideInputParam.mOriginVoicePath = originVoicePath;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        startActivityForResult(it, REQUEST_CODE_RETELLCOURSE);
    }

    protected void createRecordCourse(int orientation, boolean isRetellCourse, String title) {
        if (getActivity() == null) {
            return;
        }
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        if (isRetellCourse) {
            it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, title);
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
        } else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
            it.putExtra(SlideActivityNew.COURSE_TYPE_FROM_LQ_BOARD, true);
        }
        SlideInputParam slideInputParam = getSlideInputParam(true, isRetellCourse);
        slideInputParam.mOriginVoicePath = null;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        startActivityForResult(it, REQUEST_CODE_SLIDE);
    }

    /**
     * 创建统一的制作页面
     *
     * @param orientation
     */
    protected void createNewRetellCourseSlidePage(int orientation) {
        if (getActivity() == null) {
            return;
        }
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        SlideInputParam slideInputParam = getSlideInputParam(true, false);
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        startActivityForResult(it, REQUEST_CODE_SLIDE);
    }

    /**
     * PictureBooksDetailFragment区分新版看课件复述课件
     *
     * @param info
     * @param type
     * @param requestCode
     * @param isRetellCourse
     * @param WatchWawaCourse
     */
    private void enterSlideNewByStudyType(LocalCourseInfo info, int type, int requestCode,
                                          boolean isRetellCourse, boolean WatchWawaCourse) {
        if (getActivity() == null) {
            return;
        }
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.LOAD_FILE_PATH, info.mPath);
        it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, info.mTitle);
        it.putExtra(SlideActivityNew.LOAD_FILE_PAGES, info.mPageCount);
        it.putExtra(SlideActivityNew.COUTSE_TYPE, type);
        it.putExtra(SlideActivityNew.ORIENTATION, info.mOrientation);
        //区分新/老版看课件
        if (isRetellCourse && !WatchWawaCourse) {
            it.putExtra(SlideActivityNew.COURSETYPEFROM,
                    SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
        } else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM,
                    SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        }
        SlideInputParam slideInputParam = getSlideInputParam(false, isRetellCourse);
        slideInputParam.mOriginVoicePath = info.mOriginVoicePath;
        //support A4 paper ratio for course maker
        if (info.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
        }
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath(getMemeberId(),
                    info.mPath);
            if (dtos != null && dtos.size() > 0) {
                it.putExtra(SlideActivityNew.ORIENTATION, dtos.get(0).getmOrientation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(it, requestCode);
    }

    /**
     * 进入统一菜单样式的制作页面
     *
     * @param info
     * @param WatchWawaCourse
     */
    protected void enterUnifyMenuStyleSlideNewPage(LocalCourseInfo info,
                                                   boolean WatchWawaCourse) {
        enterSlideNewByStudyType(info, MaterialType.RECORD_BOOK, REQUEST_CODE_SLIDE,
                true, WatchWawaCourse);
    }

    protected void enterSlideNewRetellCourse(LocalCourseInfo info, String title) {
        enterSlideNew(info, MaterialType.RECORD_BOOK, REQUEST_CODE_RETELLCOURSE, true, title);
    }

    protected void enterSlideNew(LocalCourseInfo info, int type, int requestCode,
                                 boolean isRetellCourse, int taskType, String title) {
        if (getActivity() == null) {
            return;
        }
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.LOAD_FILE_PATH, info.mPath);
        it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, info.mTitle);
        it.putExtra(SlideActivityNew.LOAD_FILE_PAGES, info.mPageCount);
        it.putExtra(SlideActivityNew.COUTSE_TYPE, type);
        it.putExtra(SlideActivityNew.ORIENTATION, info.mOrientation);
        if (isRetellCourse) {
            it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, title);
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
        } else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        }
        SlideInputParam slideInputParam = getSlideInputParam(false, isRetellCourse);
        //support A4 paper ratio for course maker
        if (info.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
        }
        slideInputParam.mOriginVoicePath = info.mOriginVoicePath;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath(getMemeberId(), info.mPath);
            if (dtos != null && dtos.size() > 0) {
                it.putExtra(SlideActivityNew.ORIENTATION, dtos.get(0).getmOrientation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(it, requestCode);
    }

    private void enterSlideNew(LocalCourseInfo info, int type, int requestCode, boolean
            isRetellCourse, String title) {
        if (getActivity() == null) {
            return;
        }
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.LOAD_FILE_PATH, info.mPath);
        it.putExtra(SlideActivityNew.LOAD_FILE_PAGES, info.mPageCount);
        it.putExtra(SlideActivityNew.COUTSE_TYPE, type);
        it.putExtra(SlideActivityNew.ORIENTATION, info.mOrientation);
        if (isRetellCourse) {
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
            it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, title);
        } else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        }
        if (autoMark) {
            it.putExtra(SlideActivityNew.COURSE_ID, courseId);
            it.putExtra(SlideActivityNew.AUTO_MARK, autoMark);
        }
        SlideInputParam slideInputParam = getSlideInputParam(false, isRetellCourse);
        slideInputParam.mOriginVoicePath = info.mOriginVoicePath;
        //support A4 paper ratio for course maker
        if (info.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
        }
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath(getMemeberId(), info.mPath);
            if (dtos != null && dtos.size() > 0) {
                it.putExtra(SlideActivityNew.ORIENTATION, dtos.get(0).getmOrientation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(it, requestCode);
    }

    protected UserInfo getStuUserInfo() {
        return null;
    }

    private SlideInputParam getSlideInputParam(boolean isNew, boolean isRetellCourse) {
        SlideInputParam param = new SlideInputParam();
        param.mCurUser = new User();
        UserInfo userInfo = getStuUserInfo();
        if (userInfo == null) {
            userInfo = getUserInfo();
        }
        if (userInfo != null) {
            param.mCurUser.mId = userInfo.getMemberId();
            if (TextUtils.isEmpty(userInfo.getRealName())) {
                param.mCurUser.mName = userInfo.getRealName();
            } else {
                param.mCurUser.mName = userInfo.getNickName();
            }
        }
        param.mNotShowShareBoxBtn = false;
        param.mIsCreateAndPassResParam = isNew;
        if (isRetellCourse) {
            int[] rayMenuV = {
                    BaseSlideManager.MENU_ID_CAMERA,
                    BaseSlideManager.MENU_ID_IMAGE,
                    BaseSlideManager.MENU_ID_WHITEBOARD,
                    BaseSlideManager.MENU_ID_AUDIO,
                    BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
            };
//            MyApplication application = (MyApplication) getActivity().getApplicationContext();
//            if (application != null){
//                UserInfo info = application.getUserInfo();
//                if (info != null && info.isTeacher()){
//                    rayMenuV= Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
//                    rayMenuV[rayMenuV.length - 1] =  BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
//                }
//            }
            param.mRayMenusV = rayMenuV;
        } else {
            int[] rayMenuV = {
                    BaseSlideManager.MENU_ID_CAMERA,
                    BaseSlideManager.MENU_ID_IMAGE,
                    BaseSlideManager.MENU_ID_WHITEBOARD,
                    BaseSlideManager.MENU_ID_AUDIO,
                    BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
            };
            MyApplication application = (MyApplication) getActivity().getApplicationContext();
            if (application != null) {
                UserInfo info = application.getUserInfo();
                if (info != null && info.isTeacher()) {
                    rayMenuV = Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
                    rayMenuV[rayMenuV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
                }
            }
            param.mRayMenusV = rayMenuV;
        }
        int[] rayMenuH = {BaseSlideManager.MENU_ID_CURVE, BaseSlideManager.MENU_ID_LASER,
                BaseSlideManager.MENU_ID_ERASER};
        param.mRayMenusH = rayMenuH;
        return param;
    }

    public void finishUtil(Class cls) {
        if (getActivity() != null) {
            MyApplication myApp = ((MyApplication) (getActivity().getApplication()));
            if (myApp != null) {
                ActivityStack activityStack = myApp.getActivityStack();
                if (activityStack != null) {
                    activityStack.finishUtil(cls);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDITCOURSE) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.save_to_lq_cloud));
//            showMessageDialog(getString(R.string.save_to_lq_cloud), new
//                    DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    enterMediaMainActivity();
//                    finishUtil(HomeActivity.class);
//                }
//            });
        } else if (requestCode == REQUEST_CODE_SLIDE) {
            if (data != null) {
                String slidePath = data.getStringExtra(SlideManager
                        .EXTRA_SLIDE_PATH);
                String coursePath = data.getStringExtra(SlideManager
                        .EXTRA_COURSE_PATH);
                LogUtils.logi("TEST", "SlidePath = " + slidePath);
                LogUtils.logi("TEST", "CoursePath = " + coursePath);
                if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                    TipMsgHelper.ShowMsg(getActivity(), getString(R.string.save_to_lq_cloud));
                    //已编辑
//                    showMessageDialog(getString(R.string.save_to_lq_cloud), new
//                            DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            enterMediaMainActivity();
//                            finishUtil(HomeActivity.class);
//                        }
//                    });
                } else if (!TextUtils.isEmpty(slidePath)) {
                    //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                    if (slidePath.endsWith(File.separator)) {
                        slidePath = slidePath.substring(0, slidePath.length() - 1);
                    }
                    LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                            slidePath, true);
                }

                //lq云板的发送
                String localCourseSend = data.getExtras().getString(SlideManagerHornForPhone.SAVE_PATH);
                if (!TextUtils.isEmpty(localCourseSend)) {
                    finish();
                }
            }
        }
    }


    protected void importLocalPicResourcesCheck(List<String> paths, String title) {

    }

    protected void importLocalPicResources(final List<String> paths, final String title) {
        if (paths == null || paths.size() == 0 || TextUtils.isEmpty(title)) {
            return;
        }
        if (savePath == null) {
            return;
        }
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }

        ImportImageTask importResourceTask = new ImportImageTask(getActivity(),
                getMemeberId(), paths, savePath, title, mHandler);
        importResourceTask.execute();
    }

}
