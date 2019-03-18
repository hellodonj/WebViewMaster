package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;

import cn.robotpen.pen.model.RemoteState;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyAttendedSchoolListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CommitCourseHelper;
import com.galaxyschool.app.wawaschool.common.CommitHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.HomeworkCommitFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaTypeListFragment;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.MakingCourseTipsDialog;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.SlideManager.CourseHandler;
import com.oosic.apps.iemaker.base.SlideManager.SendCourseHandler;
import com.oosic.apps.iemaker.base.SlideManager.SlidesHandler;
import com.oosic.apps.iemaker.base.SlideManagerForHorn;
import com.oosic.apps.iemaker.base.data.NodeOwner;
import com.oosic.apps.iemaker.base.data.NormalProperty;
import com.oosic.apps.iemaker.base.evaluate.EvaluateParams;
import com.oosic.apps.iemaker.base.exercisenode.ExerciseNodeManager;
import com.oosic.apps.iemaker.base.pen.PenServiceActivity;
import com.osastudio.common.utils.TimerUtils;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SlideActivityNew extends PenServiceActivity implements CommitHelper.NoteCommitListener,
        BaseSlideManager.ExerciseNodeClickListener {
    public final static String LOAD_FILE_TITLE = SlideManager.LOAD_FILE_TITLE;
    public final static String LOAD_FILE_PATH = SlideManager.LOAD_FILE_PATH;
    public final static String LOAD_FILE_PAGES = SlideManager.LOAD_FILE_PAGES;
    public final static String ORIENTATION = "orientation";
    public final static String COUTSE_TYPE = "course_type";
    public final static String COURSETYPEFROM = "courseTypeFrom";
    public final static String ISNEEDDIRECTORY = "isNeedDirectory";
    public final static String MODEL_SOURCE_FROM = "model_source_from";
    public final static String IS_FROM_TEACHER_MARK = "is_from_teacher_mark";
    public final static String COURSE_TYPE_FROM_LQ_BOARD = "course_type_from_lq_board";

    public final static String COURSE_ID = "SlideActivityNew_course_id";
    public final static String AUTO_MARK = "SlideActivityNew_auto_mark";

    public final static String EXTRA_EXERCISE_STRING = "exerciseString";
    public final static String EXTRA_EXERCISE_ANSWER_STRING = "exerciseAnswerString";
    public final static String EXTRA_PAGE_INDEX = "pageIndex";
    public final static String EXTRA_EDIT_EXERCISE = "editExercise";
    public final static String EXTRA_EXERCISE_INDEX = "exerciseIndex";

    private LocalCourseDao localCourseDao;

    private SlideManager mSlideManager = null;
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private int mCourseType = MaterialType.RECORD_BOOK;
    private String memberId;
    private boolean isNeedDirectory;

    //定义自课件的来源
    private int courseTypeFrom = -1;
    private CommitCourseHelper commitCourseHelper;
    //制作的文件路径
    private String filePath;

    private boolean isSendOperation;
    private int mUserType;
    private boolean isFromMoocModel;
    private boolean isFromLqBoard;
    private Handler mHandler = new Handler();
    private String courseId;
    private boolean autoMark;
    private boolean isTeacherMark;

    /**
     * 判断当前制作的来源
     */
    public interface CourseTypeFrom {
        int FROMLQCOURSE = 100;
        int FROMSTUDYTASK = 101;
    }

    private SlidesHandler mSlidesHandler = new SlidesHandler() {

        @Override
        public boolean saveSlide(String path, String title, String description, boolean saveAsCourse) {
            // TODO Auto-generated method stub
            File slide = new File(path);
            if (slide != null && slide.exists()) {
                String parent = slide.getParentFile().toString();
                if (path != null && path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }

                if (localCourseDao != null) {
                    try {
                        List<LocalCourseDTO> slideList = localCourseDao.getLocalCourseByPath(memberId, path);
                        LocalCourseDTO localCourseDTO;
                        if (slideList == null || slideList.size() <= 0) {
                            LocalCourseInfo info = new LocalCourseInfo(
                                    path, parent, 0,
                                    0, System.currentTimeMillis());
                            info.mOrientation = mOrientation;
                            info.mMemberId = memberId;
                            info.mTitle = title;
                            info.mDescription = description;
                            localCourseDTO = info.toLocalCourseDTO();
                        } else {
                            localCourseDTO = slideList.get(0);
                            localCourseDTO.setmTitle(title);
                            localCourseDTO.setmDescription(description);
                        }
                        localCourseDTO.setmType(saveAsCourse ?
                                CourseType.COURSE_TYPE_LOCAL : CourseType.COURSE_TYPE_IMPORT);
                        localCourseDao.addOrUpdateLocalCourseDTO(localCourseDTO);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        public void sendSlide(String path, String title, String description) {
            mSendCourseHandler.sendCourse(path, path, title, description, null);
        }

        @Override
        public ArrayList<NormalProperty> getSlidesFolderList() {

            ArrayList<NormalProperty> folderAry = null;
//            String importedHome = Utils.IMPORTED_FOLDER.substring(
//                0,
//                Utils.IMPORTED_FOLDER.length() - 1);
            String importedHome = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_IMPORT, false);

            if (folderAry == null) {
                folderAry = new ArrayList<NormalProperty>();
            }
            // pp fix end
            try {
                List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalFolders(
                        memberId, CourseType.COURSE_TYPE_IMPORT);
                if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                    for (LocalCourseDTO localCourseDTO : localCourseDTOs) {
                        if (localCourseDTO != null) {
                            String path = localCourseDTO.getmParentPath();
                            if (path != null && path.endsWith(File.separator)) {
                                path = path.substring(0, path.length() - 1);
                            }
                            String title = null;
                            if (path != null && importedHome.equals(path)) {
                                title = getString(R.string.default_coursefolder);
                            } else {
                                int index = path.lastIndexOf('/');
                                title = path.substring(index + 1, path.length());
                            }
                            NormalProperty item = new NormalProperty(
                                    path, title,
                                    null, 0, 0);
                            if (folderAry == null) {
                                folderAry = new ArrayList<NormalProperty>();
                            }
                            folderAry.add(item);
                            if (new File(path).exists()) {
                                File folder = new File(path);
                                List<LocalCourseDTO> localCourseDTOsInFolder = localCourseDao.getLocalCoursesByFolder
                                        (memberId, CourseType.COURSE_TYPE_IMPORT, path);
                                int count = 0;
                                String firstcourse = null;
                                if (localCourseDTOsInFolder != null && localCourseDTOsInFolder.size() > 0) {
                                    count = localCourseDTOsInFolder.size();
                                    LocalCourseDTO localCourseDTOInFolder = localCourseDTOsInFolder.get(0);
                                    if (localCourseDTOInFolder != null) {
                                        firstcourse = localCourseDTOInFolder.getmPath();
                                    }
                                }
                                if (count > 0) {
                                    if (TextUtils.isEmpty(firstcourse)
                                            && folder.listFiles().length > 0) {
                                        firstcourse = folder.listFiles()[0].getPath();
                                    }
                                    if (!TextUtils.isEmpty(firstcourse)) {
                                        if (!path.equals(importedHome)) {
                                            count = folder.listFiles().length;
                                        }
                                        String thumbPath = firstcourse + File.separator
                                                + Utils.PDF_THUMB_NAME;
                                        if (!new File(thumbPath).exists()) {
                                            thumbPath = firstcourse + File.separator
                                                    + Utils.PDF_PAGE_NAME + "1.jpg";
                                            if (!new File(thumbPath).exists()) {
                                                thumbPath = null;
                                            }
                                        }
                                        item.mThumbPath = thumbPath;

                                        item.mCount = count;
                                    } else {
                                        item.mCount = 0;
                                    }

                                }

                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            boolean bHomeExist = false;
            int position = 0;
            if (folderAry != null && folderAry.size() > 0) {
                for (int i = 0; i < folderAry.size(); i++) {
                    NormalProperty item = folderAry.get(i);
                    if (item.mPath.equals(importedHome)) {
                        bHomeExist = true;
                        position = i;
                    }
                }
            }
            if (!bHomeExist) {
                NormalProperty home = new NormalProperty(
                        importedHome,
                        getString(R.string.default_coursefolder), null, 0l, 0l);
                folderAry.add(0, home);
            } else {
                //move root folder to first position, wangchao
                if (position > 0) {
                    NormalProperty item = folderAry.get(position);
                    folderAry.remove(item);
                    folderAry.add(0, item);
                }
            }

            return folderAry;

        }

        @Override
        public ArrayList<NormalProperty> getSlidesListByFolderPath(String folder) {
            ArrayList<NormalProperty> slidesList = null;
            if (folder == null) {
                return null;
            }

            if (folder.endsWith(File.separator)) {
                folder = folder.substring(0, folder.length() - 1);
            }

            try {
                List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCoursesByFolder
                        (memberId, CourseType.COURSE_TYPE_IMPORT, folder);
                for (LocalCourseDTO localCourseDTO : localCourseDTOs) {
                    if (localCourseDTO != null) {
                        String path = localCourseDTO.getmPath();
                        if (path != null && new File(path).exists()) {
                            NormalProperty item = new NormalProperty();
                            item.mPath = path;
                            item.mTitle = localCourseDTO.getmTitle();

                            String thumbPath = path + File.separator
                                    + Utils.PDF_THUMB_NAME;
                            if (!new File(thumbPath).exists()) {
                                thumbPath = path + File.separator + Utils.PDF_PAGE_NAME
                                        + "1.jpg";
                                if (!new File(thumbPath).exists()) {
                                    thumbPath = null;
                                }
                            }
                            item.mThumbPath = thumbPath;
                            if (slidesList == null) {
                                slidesList = new ArrayList<NormalProperty>();
                            }
                            slidesList.add(item);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return slidesList;
        }

        @Override
        public void pickSpaceMaterial(int spaceType) {
            // TODO Auto-generated method stub
            if (spaceType == 0) {
                //从个人资源库选取
                pickPersonalMaterial();
            } else if (spaceType == 1) {
                //从公共资源库选取

            } else if (spaceType == 2) {
                //从校本资源库选取
                pickSchoolMaterial();
            }
        }
    };

    /**
     * 从个人素材库选取图片，音频，视频，PDF， PPT
     */
    private void pickPersonalMaterial() {
        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(SlideActivityNew.this, R.string.pls_login);
            ActivityUtils.enterLogin(SlideActivityNew.this);
            return;
        }
        Intent intent = new Intent();
        intent.setClass(SlideActivityNew.this, ShellActivity.class);
        intent.putExtra("Window", "media_type_list");
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        intent.putExtra(ShellActivity.EXTRA_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //0 landscape, 1 portrait
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        //过滤资源条目显示
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.PICTURE);
        if (mCourseType != MaterialType.ONEPAGE_BOOK) {
            mediaTypes.add(MediaType.VIDEO);
            mediaTypes.add(MediaType.AUDIO);
        }
        mediaTypes.add(MediaType.PPT);
        mediaTypes.add(MediaType.PDF);
        mediaTypes.add(MediaType.DOC);
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES,
                mediaTypes);
        startActivityForResult(intent, SlideManager.REQUEST_PICK_SPACE_MATERAIL);
    }


    /**
     * 从校本资源库选取素材
     */
    private void pickSchoolMaterial() {
        Intent intent = new Intent();
        intent.setClass(SlideActivityNew.this, MyAttendedSchoolListActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.SCHOOL_PICTURE);
        if (mCourseType != MaterialType.ONEPAGE_BOOK) {
            mediaTypes.add(MediaType.SCHOOL_AUDIO);
            mediaTypes.add(MediaType.SCHOOL_VIDEO);
        }
        mediaTypes.add(MediaType.SCHOOL_PPT);
        mediaTypes.add(MediaType.SCHOOL_PDF);
        mediaTypes.add(MediaType.SCHOOL_DOC);
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES,
                mediaTypes);
        startActivityForResult(intent, SlideManager.REQUEST_PICK_SPACE_MATERAIL);
    }

    private CourseHandler mCourseHandler = new CourseHandler() {
        @Override
        public ArrayList<NormalProperty> getCourseFolderList() {
            ArrayList<NormalProperty> folderList = new ArrayList<NormalProperty>();
//            String courseRoot = Utils.RECORD_FOLDER.substring(
//                0,
//                Utils.RECORD_FOLDER.length() - 1);
            String courseRoot = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_LOCAL, false);
            NormalProperty home = new NormalProperty(
                    courseRoot,
                    getString(R.string.default_coursefolder), null, 0l, 0l);
            folderList.add(home);
            String folderRoot = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_LOCAL, true);
            folderRoot = folderRoot.substring(0, folderRoot.length() - 1);
            try {
                List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalFolders(memberId,
                        CourseType.COURSE_TYPE_LOCAL);
                if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                    for (LocalCourseDTO item : localCourseDTOs) {
                        if (item != null) {
                            String parent = item.getmParentPath();
                            File file = new File(parent);
                            if (file.getParent().equals(folderRoot) && new File(parent).exists()) {
                                NormalProperty folderInfo = new NormalProperty(
                                        parent, item.getmTitle(), null, 0l, 0l);
                                if (folderList == null) {
                                    folderList = new ArrayList<NormalProperty>();
                                }
                                folderList.add(folderInfo);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        @Override
        public boolean saveCourse(String path, String title, String description, long duration) {
            boolean rtn = false;
            if (path == null) {
                return false;
            }
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            String parent = new File(path).getParentFile().getPath();

            if (parent.endsWith(File.separator)) {
                parent = parent.substring(0, parent.length() - 1);
            }
            LocalCourseInfo info = new LocalCourseInfo(
                    path, parent, duration,
                    System.currentTimeMillis(), CourseType.COURSE_TYPE_LOCAL, null, description);
            info.mParentPath = parent;
            info.mOrientation = mOrientation;
            info.mTitle = title;
            try {
                List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCourseByPath(memberId, path);
                if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                    localCourseDao.updateLocalCourse(memberId, path, info);

                } else {
                    LocalCourseDTO dao = info.toLocalCourseDTO();
                    dao.setmMemberId(memberId);
                    localCourseDao.addOrUpdateLocalCourseDTO(dao);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //学习任务的保存弹toast提示
            if (courseTypeFrom == CourseTypeFrom.FROMSTUDYTASK && !isSendOperation) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipMsgHelper.ShowMsg(SlideActivityNew.this, R.string.save_to_lq_cloud);
                    }
                });
            }
            isSendOperation = false;
            return rtn;
        }

        @Override
        public boolean deleteCourse(String path) {
            boolean rtn = false;
            try {
                List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCourseByPath(memberId, path);
                if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                    localCourseDao.deleteLocalCoursesByFolder(memberId, path);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rtn;
        }

    };

    private SendCourseHandler mSendCourseHandler = new SendCourseHandler() {
        @Override
        public void sendCourse(String slidePath, String coursePath, String title, String description,
                               SlideManager.MoreParams moreParams) {
            isSendOperation = true;
            UIUtils.hideSoftKeyboard(SlideActivityNew.this);
            UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
            filePath = coursePath;

            if (moreParams != null) {
                //自动批阅分数有效
                EvaluateParams evalParams = moreParams.evalParams;
                Intent broadIntent = new Intent();
                broadIntent.setAction(HomeworkCommitFragment.COMMIT_AUTO_MARK_SCORE_ACTION);
                broadIntent.putExtra("score", String.valueOf(evalParams.getScore()));
                broadIntent.putExtra("schemeId",moreParams.evalParams.getSchemeId());
                broadIntent.putExtra("result",moreParams.evalParams.getResult());
                SlideActivityNew.this.sendBroadcast(broadIntent);
            }

            //发送相关的代码
            mCourseHandler.saveCourse(coursePath, title, description, mSlideManager.getCourseDuration());
            if (courseTypeFrom == CourseTypeFrom.FROMLQCOURSE) {
                if (commitCourseHelper == null) {
                    commitCourseHelper = new CommitCourseHelper(SlideActivityNew.this);
                }
                if (userInfo != null && userInfo.isTeacher()) {
                    commitCourseHelper.setIsTeacher(true);
                }
                if (userInfo != null && userInfo.isStudent()) {
                    commitCourseHelper.setIsStudent(true);
                }
                commitCourseHelper.setIsLocal(true);
                commitCourseHelper.setCourseTypeFrom(courseTypeFrom);
                commitCourseHelper.commit(getWindow().getDecorView(), null);
                commitCourseHelper.setNoteCommitListener(SlideActivityNew.this);
            }


//            //删除保存的素材
//            if (!TextUtils.isEmpty(slidePath)) {
//                if (slidePath.endsWith(File.separator)) {
//                    slidePath = slidePath.substring(0, slidePath.length() - 1);
//                }
//                LocalCourseDTO.deleteLocalCourseByPath(SlideActivityNew.this, userInfo.getMemberId(),
//                        slidePath, true);
//            }
        }
    };

    /**
     * 发送 选择相应的类型
     *
     * @param shareType
     */
    @Override
    public void noteCommit(int shareType) {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(this, R.string.pls_login);
            return;
        }

        final LocalCourseDTO data = LocalCourseDTO.getLocalCourse(SlideActivityNew.this, memberId,
                filePath);
        if (data == null) {
            return;
        }
        accordingConditionToShare(data.toLocalCourseInfo(), userInfo, shareType);
    }

    private void accordingConditionToShare(LocalCourseInfo localCourseInfo, UserInfo userInfo, int
            mShareType) {
        //更新MicroId
        String path = null;
        if (localCourseInfo != null) {
            path = localCourseInfo.mPath;
            if (!TextUtils.isEmpty(path) && path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(SlideActivityNew.this, userInfo
                    .getMemberId(), path);
            if (dto != null && dto.getmMicroId() > 0) {
                localCourseInfo.mMicroId = dto.getmMicroId();
            }
            commitCourseHelper.setIsTempData(true);
            commitCourseHelper.uploadCourse(userInfo, localCourseInfo, null, mShareType);
        }
    }

    protected void setUserType(int roleType) {
        mUserType = NodeOwner.toNewUserType(roleType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseId = getIntent().getStringExtra(COURSE_ID);
        autoMark = getIntent().getBooleanExtra(AUTO_MARK, false);
        courseTypeFrom = getIntent().getIntExtra(COURSETYPEFROM, -1);
        isNeedDirectory = getIntent().getBooleanExtra(ISNEEDDIRECTORY, false);
        mOrientation = getIntent().getIntExtra(ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mCourseType = getIntent().getIntExtra(COUTSE_TYPE, MaterialType.RECORD_BOOK);
        memberId = ((MyApplication) getApplication()).getMemberId();
        isFromMoocModel = getIntent().getBooleanExtra(MODEL_SOURCE_FROM, false);
        isFromLqBoard = getIntent().getBooleanExtra(COURSE_TYPE_FROM_LQ_BOARD, false);
        isTeacherMark = getIntent().getBooleanExtra(IS_FROM_TEACHER_MARK,false);
//        int mCurrentOrientation = getResources().getConfiguration().orientation;
//        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        localCourseDao = new LocalCourseDao(SlideActivityNew.this);
        String courseRoot = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_LOCAL, false);
        if (mSlideManager == null) {
            if (mCourseType == MaterialType.ONEPAGE_BOOK) {
//                mSlideManager = new SlideManagerForHorn(this, mSlidesHandler, mCourseHandler, Utils.RECORD_FOLDER);
                mSlideManager = new SlideManagerForHorn(this, mSlidesHandler, mCourseHandler, courseRoot);
            } else {
//                mSlideManager = new SlideManager(this, mSlidesHandler, mCourseHandler, Utils.RECORD_FOLDER,
//                        mCourseType == MaterialType.AUDIO_BOOK);
                mSlideManager = new SlideManager(this, mSlidesHandler, mCourseHandler, courseRoot,
                        mCourseType == MaterialType.AUDIO_BOOK);
                mSlideManager.setNeedDirectory(isNeedDirectory);
            }
        }
        //区分课程的来源
        if (courseTypeFrom == CourseTypeFrom.FROMSTUDYTASK) {
            mSlideManager.setIsStudyTask(true);
            setUserType(RoleType.ROLE_TYPE_STUDENT);
//            if (!isFromMoocModel) {
//            }
            if (isTeacherMark) {
                setUserType(RoleType.ROLE_TYPE_TEACHER);
            }
        }
        mSlideManager.setPenUserServiceHelper(getPenUserServiceHelper());
        mSlideManager.setUserType(mUserType);
        mSlideManager.setSendCourseHandler(mSendCourseHandler);
        mSlideManager.setSaveSlideInCourseMode(isFromLqBoard);
//        mSlideManager.setEvaluateResId(courseId);
//        mSlideManager.enableEvaluate(autoMark);
        if (autoMark){
            mSlideManager.enableEvaluateMark(true);
            if (!TextUtils.isEmpty(courseId)) {
                if (courseId.contains("-")){
                    courseId = courseId.split("-")[0];
                }
                mSlideManager.setEvaluateResId(courseId);
            }
        }

        boolean editExercise = getIntent().getBooleanExtra(EXTRA_EDIT_EXERCISE, false);
        if (editExercise) {
            mSlideManager.setEditExercise(editExercise);
            mSlideManager.setExerciseIndex(getIntent().getIntExtra(EXTRA_EXERCISE_INDEX, 0));
            mSlideManager.setPageIndex(getIntent().getIntExtra(EXTRA_PAGE_INDEX, 0));
            mSlideManager.setExerciseNodeClickListener(this);
            mSlideManager.getExerciseNodeManager().setExerciseString(
                    getIntent().getStringExtra(EXTRA_EXERCISE_STRING));
            mSlideManager.getExerciseNodeManager().setStudentAnswerString(
                    getIntent().getStringExtra(EXTRA_EXERCISE_ANSWER_STRING));
        }
        mSlideManager.onCreate(mOrientation);
        mSlideManager.setTitleColor(getResources().getColor(R.color.toolbar_bg_color));
//        mSlideManager.setClipMusicHandler(mClipMusicHandler);

        //如果来自学习任务开始录制时间的计时
        if (isNeedTimerRecorder()) {
            TimerUtils.getInstance().startTimer();
        }

        showMakingTipsDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSlideManager != null) {
            mSlideManager.onPause();
        }
    }

    @Override
    protected void onResume() {
        if (mSlideManager != null) {
            mSlideManager.onResume();
        }

        super.onResume();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSlideManager != null) {
                    getPenUserServiceHelper().updatePenState();
                }
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        if (mSlideManager != null) {
            mSlideManager.onDestroy();
        }
        //如果来自学习任务暂停录制时间的计时
        if (isNeedTimerRecorder()) {
            TimerUtils.getInstance().pauseTimer();
            TimerUtils.getInstance().stopTimer();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mSlideManager != null) {
            mSlideManager.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent resultData) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {
            if (requestCode == SlideManager.REQUEST_PICK_SPACE_MATERAIL) {
                List<ResourceInfo> resourceInfos = resultData.getParcelableArrayListExtra("resourseInfoList");
                if (resourceInfos != null && resourceInfos.size() > 0) {
                    for (ResourceInfo info : resourceInfos) {
                        if (info != null) {
                            if (info.getImgPath() != null && !info.getImgPath().startsWith("http")) {
                                info.setImgPath(AppSettings.getFileUrl(info.getImgPath()));
                            }
                            if (info.getResourcePath() != null && !info.getResourcePath().startsWith("http")) {
                                info.setResourcePath(AppSettings.getFileUrl(info.getResourcePath()));
                            }
                        }
                    }
                }
            } else if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH) {
                if (resultData != null) {
                    Intent intent = new Intent();
                    String filePath = resultData.getExtras().getString(SlideManagerHornForPhone.SAVE_PATH);
                    intent.putExtra(SlideManagerHornForPhone.SAVE_PATH, filePath);
                    this.setResult(Activity.RESULT_OK, intent);
                    this.finish();
                }
            }
        }
        if (mSlideManager != null) {
            mSlideManager.onActivityResult(requestCode, resultCode, resultData);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean rtn = super.dispatchTouchEvent(event);
        if (mSlideManager != null) {
            return mSlideManager.dispatchTouchEvent(event);
        }
        return rtn;
    }

    private void showMakingTipsDialog() {
        boolean show = DemoApplication.getInstance().getPrefsManager().isMakingCourseTipsEnabled();
        if (show) {
            Dialog dialog = new MakingCourseTipsDialog(this);
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    /**
     * @return 判断返回记录录制的时间的条件
     */
    private boolean isNeedTimerRecorder() {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
            if (courseTypeFrom == CourseTypeFrom.FROMSTUDYTASK) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStateChanged(int i, String s) {
        if (mSlideManager != null) {
            switch (i) {
                case RemoteState.STATE_DEVICE_INFO:
                case RemoteState.STATE_CONNECTED:
                    getPenUserServiceHelper().updatePenState(true);
                    break;
                case RemoteState.STATE_DISCONNECTED:
                case RemoteState.STATE_ERROR:
                    getPenUserServiceHelper().updatePenState(false);
                    break;
            }
        }
    }

    @Override
    public void onExerciseNodeClick(int exerciseIndex) {

    }

    protected void reviewExerciseDetails(int exerciseIndex) {
        if (mSlideManager != null) {
            mSlideManager.reviewExerciseDetails(exerciseIndex);
        }
    }

}
