package com.galaxyschool.app.wawaschool.slide;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyAttendedSchoolListActivity;
import com.galaxyschool.app.wawaschool.MyTaskListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CommitCourseHelper;
import com.galaxyschool.app.wawaschool.common.CommitHelper;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadCourseHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaTypeListFragment;
import com.galaxyschool.app.wawaschool.fragment.MyTaskListFragment;
import com.galaxyschool.app.wawaschool.fragment.PublishResourceFragment;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.helper.UserInfoHelper;
import com.galaxyschool.app.wawaschool.net.course.UploadCourseManager;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper.SlideSaveBtnParam;
import com.galaxyschool.app.wawaschool.slide.UploadDialog.UploadDialogHandler;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.SlideManagerForHorn;
import com.oosic.apps.iemaker.base.data.NormalProperty;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 作者 shouyi
 * @version 创建时间：Apr 6, 2016 11:17:34 AM 类说明
 */
public class SlideManagerHornForPhone extends SlideManagerForHorn
        implements CommitHelper.NoteCommitListener, CommitHelper.PopWindowDismissListener {


    public static final int MSG_SAVE_FINISH = 0;

    public final static String SAVE_PATH = "save_path";

    private String mSaveTitle;
    private String mSaveContent;
    private String mContent;
    private boolean mIsShare;
    private String mTaskId;
    private int mShareType = -1;
    private boolean mIsScanTask;
    private UserInfo mStuUserInfo;
    private UserInfo mUserInfo;
    private String mMemberId;
    private boolean mIsIntroductionTask;
    private CommitCourseHelper helper = null;
    UploadDialog uploadDialog;
    private DoTaskOrderHelper taskOrderHelper;

    private int fromType;
    private ExerciseAnswerCardParam cardParam;
    private boolean isEntityTeacher;//实体机构的老师

    public interface FromWhereData {
        //来自lq云板的资源
        int FROM_LQCLOUD_COURSE = 10;
        int FROM_STUDY_TASK_COURSE = 11;
        int FROM_DO_ONEPAGE_COUSRSE = 12;
        int FROM_DO_TASKORDER_COUSRSE = 13;
        //学习任务的其他类型的作业
        int FROM_STUDY_TASK_OTHER_COURSE = 14;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_FINISH:
                    showUploadView();
                    break;
            }
        }
    };

    public SlideManagerHornForPhone(Activity activity,
                                    SlidesHandler slidesHandler, CourseHandler courseHandler,
                                    String defaultCourseSaveRoot) {
        // TODO Auto-generated constructor stub
        super(activity, slidesHandler, courseHandler, defaultCourseSaveRoot);
        if (mSlidesHandler == null) {
            mSlidesHandler = mThisSlidesHandler;
        }
        setOnlySavePageFile(true);
        setIsNeedUpdateResource(false);
        mIsScanTask = mContext.getIntent().getBooleanExtra(SlideWawaPageActivity.IS_SCAN_TASK, false);
        mIsIntroductionTask = mContext.getIntent().getBooleanExtra(SlideWawaPageActivity
                .IS_INTRODUCTION_TASK, false);
        cardParam = (ExerciseAnswerCardParam) mContext.getIntent().getSerializableExtra(ExerciseAnswerCardParam.class
                .getSimpleName());

        fromType = mContext.getIntent().getIntExtra(SlideWawaPageActivity.COURSE_FROM_TYPE, 0);
        mStuUserInfo = (UserInfo) mContext.getIntent().getSerializableExtra(UserInfo.class.getSimpleName());
        mUserInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
        if (mUserInfo != null) {
            mMemberId = mUserInfo.getMemberId();
        }
        loadUserInfoData();
    }

    private SlidesHandler mThisSlidesHandler = new SlidesHandler() {

        @Override
        public boolean saveSlide(String savePath, String title, String description, boolean saveAsCourse) {
            // TODO Auto-generated method stub
            if (savePath != null) {
                addOrUpdateDB(mSaveTitle, mSaveContent, savePath);
//				if(!mIsScanTask) {
//					if (isRenameTitle(savePath)) {
//						processDBDuetoRename(mOpenSlidePath, savePath);
//					}
//				}git
//				checkToUpload(savePath);
                if (mIsIntroductionTask == true) {
                    mIsShare = false;
                }
                if (mIsScanTask && mIsShare && !mIsIntroductionTask) {
                    mHandler.sendEmptyMessage(MSG_SAVE_FINISH);
                    //来自lq云板的发送
                } else if (fromType == FromWhereData.FROM_LQCLOUD_COURSE && mIsShare) {
                    mHandler.sendEmptyMessage(MSG_SAVE_FINISH);
                } else if (fromType == FromWhereData.FROM_STUDY_TASK_COURSE) {
                    if (cardParam != null && taskOrderHelper != null) {
                        taskOrderHelper.setSlidePath(savePath);
                        taskOrderHelper.commitCheckMarkData();
                    } else {
                        //来自学习任务
                        if (mResultIntent == null) {
                            mResultIntent = new Intent();
                        }
                        if (mIsShare) {
                            //发送
                            mResultIntent.putExtra(EXTRA_SLIDE_PATH, savePath);
                            mResultIntent.putExtra(SAVE_PATH, savePath);
                            mResultIntent.putExtra(SlideManager.LOAD_FILE_TITLE, mSaveTitle);
                            mResultIntent.putExtra(EXTRA_SLIDE_PATH_TYPE, commitModelValue);
                            mContext.setResult(mContext.RESULT_OK, mResultIntent);
                        } else {
                            //保存
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TipMsgHelper.ShowLMsg(mContext, mContext.getResources().getString(R.string.lqcourse_save_local));
                                }
                            });
                        }
                        //如果是保存直接放在本地
                        mContext.finish();
                    }
                } else {
                    checkToUpload(savePath, mSaveTitle);
                }
                mOpenSlidePath = savePath;
            }
            return false;
        }

        @Override
        public void sendSlide(String path, String title, String description) {

        }

        @Override
        public void pickSpaceMaterial(int spaceType) {
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

        @Override
        public ArrayList<NormalProperty> getSlidesListByFolderPath(
                String folderPath) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ArrayList<NormalProperty> getSlidesFolderList() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    /**
     * 从个人素材库选取图片，PDF， PPT
     */
    private void pickPersonalMaterial() {
        if (mContext == null) {
            return;
        }
        UserInfo userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(mContext, R.string.pls_login);
            ActivityUtils.enterLogin(mContext);
            return;
        }
        Intent intent = new Intent();
        intent.setClass(mContext, ShellActivity.class);
        intent.putExtra("Window", "media_type_list");
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        intent.putExtra(ShellActivity.EXTRA_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //0 landscape, 1
        //过滤资源条目显示
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.PICTURE);
//		mediaTypes.add(MediaType.VIDEO);
//		mediaTypes.add(MediaType.AUDIO);
        mediaTypes.add(MediaType.PPT);
        mediaTypes.add(MediaType.PDF);
        mediaTypes.add(MediaType.DOC);
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES,
                mediaTypes);
        mContext.startActivityForResult(intent, SlideManager.REQUEST_PICK_SPACE_MATERAIL);
    }

    /**
     * 从校本资源库选取素材
     */
    private void pickSchoolMaterial() {
        Intent intent = new Intent();
        intent.setClass(mContext, MyAttendedSchoolListActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.SCHOOL_PICTURE);
        mediaTypes.add(MediaType.SCHOOL_PPT);
        mediaTypes.add(MediaType.SCHOOL_PDF);
        mediaTypes.add(MediaType.SCHOOL_DOC);
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES,
                mediaTypes);
        mContext.startActivityForResult(intent, SlideManager.REQUEST_PICK_SPACE_MATERAIL);
    }


    @Override
    protected void showSaveResourceDialog() {
        // TODO Auto-generated method stub
        stopPageAudio(mCurrentPageIndex);

        String saveName = mSaveTitle;
        String saveContent = mSaveContent;
        if (mSlideInputParam != null) {
            saveName = mSlideInputParam.mTitle;
            saveContent = mSlideInputParam.mContent;
        }
//		if (mSlideInputParam != null && mSlideInputParam.mIsCreateAndPassResParam) {
//			saveName = "";
//			saveContent="";
//		} else {
//			if (mSlideInputParam != null) {
//				saveName = mSlideInputParam.mTitle;
//				saveContent=mSlideInputParam.mContent;
//			}
//			if (saveName == null) {
//				saveName = mSaveTitle;
//				saveContent=mSaveContent;
//			}
//		}
//		String thumbPath = null;
//		if (mPageList != null && mPageList.size() > 0) {
//			thumbPath = mPageList.get(0).mPath;
//		}

//		UploadDialog uploadDialog;
        SlideSaveBtnParam param = (SlideSaveBtnParam) mContext.getIntent().
                getSerializableExtra(SlideSaveBtnParam.class.getSimpleName());
//		if(mIsScanTask) {
//			param = new SlideSaveBtnParam(true, true, false);
//		}

        // set a default title for onepage
        if (TextUtils.isEmpty(saveName)) {
            saveName = DateUtils.millSecToDateStr(System.currentTimeMillis());
        }
        if (cardParam != null) {
            //任务单答题卡的弹框
            if (taskOrderHelper == null) {
                taskOrderHelper = new DoTaskOrderHelper(this.mContext);
                taskOrderHelper.setUploadDialogHandler(mUploadDialogHandler);
                taskOrderHelper.setExerciseAnswerCardParam(cardParam);
            }
            taskOrderHelper.showCommitDialog();
        } else {
            if (param != null) {
                uploadDialog = new UploadDialog(mContext, saveName, saveContent,
                        false, mUploadDialogHandler, param);
            } else {
                uploadDialog = new UploadDialog(mContext, saveName, saveContent,
                        false, mUploadDialogHandler, false);
            }
            uploadDialog.setCanceledOnTouchOutside(true);
            uploadDialog.setCancelable(true);
            uploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    uploadDialog.hideSoftKeyboard();
                }
            });
            uploadDialog.show();
        }
    }

    private void saveSlide(String title, String content) {
        title = title.trim();
        content = content.trim();
//		if (TextUtils.isEmpty(title)) {
//			long milliseconds = System.currentTimeMillis();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//			title = sdf.format(new Date(milliseconds));
//		}
        //答题卡批阅不要title不校验
        if (cardParam == null) {
            boolean isValid = Utils.checkEditTextValid(mContext, title);
            if (!isValid) {
                return;
            }
            isValid = Utils.checkEditTextValid(mContext, content);
            if (!isValid) {
                return;
            }
        }
//		String savePath = BaseUtils.joinFilePath(mDefaultCourseSaveRoot, title);
////		if(!mIsScanTask) {
//			if (isOtherFileExist(savePath)) {
//				showRenameDialog(title);
//				return;
//			}
//		}
        String savePath = mOpenSlidePath;
        if (!TextUtils.isEmpty(savePath)) {
            savePath = Utils.removeFolderSeparator(savePath);
            if (savePath.equals(SlideManager.INSERT_IMAGES)) {
                savePath = null;
            }
        }
        if (TextUtils.isEmpty(savePath)) {
            savePath = BaseUtils.joinFilePath(mDefaultCourseSaveRoot, DateUtils.millSecToDateStr
                    (System.currentTimeMillis()));
        }
        saveChw(savePath);
        mSaveTitle = title;
        mSaveContent = content;
    }

    private void addOrUpdateDB(String title, String content, String path) {
        UserInfo userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
//		String savePath = BaseUtils.joinFilePath(mDefaultCourseSaveRoot, title);
        String savePath = Utils.removeFolderSeparator(path);
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
//			String parentPath = mDefaultCourseSaveRoot;
            String parentPath = new File(path).getParent();
            if (!TextUtils.isEmpty(parentPath) && parentPath.endsWith(File.separator)) {
//				parentPath = parentPath.substring(0, parentPath.length() - 1);
                parentPath = Utils.removeFolderSeparator(parentPath);
            }
            LocalCourseDTO localCourseDTO = new LocalCourseDTO(savePath, parentPath,
                    "", content, System.currentTimeMillis(), 0, 0, 0, CourseType.COURSE_TYPE_LOCAL);
            if (localCourseDTO != null) {
                localCourseDTO.setmOrientation(mOrientation);
                localCourseDTO.setmTitle(title);
            }
            LocalCourseDTO.saveLocalCourse(mContext, mMemberId, localCourseDTO);
        }
    }

    private boolean isAutoUpload() {
        PrefsManager prefsManager = ((MyApplication) mContext.getApplication()).getPrefsManager();
        boolean isAutoUpload = false;
        if (prefsManager != null) {
            isAutoUpload = prefsManager.getAutoUploadResource();
        }
        return isAutoUpload;
    }

    private boolean isOtherFileExist(String savePath) {
        UserInfo userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
        LocalCourseDTO data = null;
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
            data = LocalCourseDTO.getLocalCourse(mContext, userInfo.getMemberId(), savePath);
        }
        if (data != null) {
            if (TextUtils.isEmpty(mOpenSlidePath)) {
                return true;
            } else {
                String openPath = mOpenSlidePath;
                if (openPath.endsWith(File.separator)) {
                    openPath = openPath.substring(0, openPath.length() - 1);
                }
                if (!savePath.equals(openPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRenameTitle(String savePath) {
        if (!TextUtils.isEmpty(mOpenSlidePath)) {
            if (savePath.endsWith(File.separator)) {
                savePath = savePath.substring(0, savePath.length() - 1);
            }
            String originSlidePath = mOpenSlidePath;
            if (originSlidePath.endsWith(File.separator)) {
                String pathString = originSlidePath.substring(0,
                        originSlidePath.length() - 1);
                originSlidePath = pathString;
            }
            if (!savePath.equals(originSlidePath)) {
                return true;
            }
        }
        return false;
    }

    private void processDBDuetoRename(String originSlidePath, String savePath) {
        if (originSlidePath.endsWith(File.separator)) {
            originSlidePath = originSlidePath.substring(0, originSlidePath.length() - 1);
        }
        UserInfo userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
            //Update old resource Id
            LocalCourseDTO data = LocalCourseDTO.getLocalCourse(mContext, userInfo.getMemberId(),
                    originSlidePath);
            LocalCourseDTO.deleteLocalCourseByPath(mContext, userInfo.getMemberId(),
                    originSlidePath, true);
        }
    }

    private void checkToUpload(String savePath, String title) {
        mTaskId = mContext.getIntent().getStringExtra(SlideWawaPageActivity.TASK_ID);
        if (!TextUtils.isEmpty(mTaskId) && mIsShare) {
            uploadWawaPage(savePath, title);
        } else {
//			if(!mIsScanTask) {
            if (isAutoUpload() || mIsShare) {
                uploadWawaPage(savePath, title);
            } else {
                if (mContext != null) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsScanTask || !TextUtils.isEmpty(mTaskId)) {
                                TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string.lqcourse_save_local));
                            }
                        }
                    });
                }
                if (mResultIntent == null) {
                    mResultIntent = new Intent();
                }
                mResultIntent.putExtra(EXTRA_SLIDE_PATH, savePath);
                mResultIntent.putExtra(SAVE_PATH, savePath);
                mResultIntent.putExtra(EXTRA_SLIDE_PATH_TYPE, commitModelValue);
                mContext.setResult(mContext.RESULT_OK, mResultIntent);
                mContext.finish();
            }
//			} else {
//				if(mShareType < 0) {
//					mContext.finish();
//				} else {
//					uploadWawaPageToTask(savePath);
//				}
//			}
        }
    }

    private UploadDialogHandler mUploadDialogHandler = new UploadDialogHandler() {

        @Override
        public void upload(String title, String content) {
            // TODO Auto-generated method stub
            mIsShare = true;
            commitModelValue = CommitModel.sendMessage;
            saveSlide(title, content);
            if (uploadDialog != null) {
                UIUtils.hideSoftKeyboardValid(mContext, uploadDialog.getEditText());
            }
        }

        @Override
        public void saveDraft(String title, String content) {
            if (uploadDialog != null) {
                UIUtils.hideSoftKeyboardValid(mContext, uploadDialog.getEditText());
            }
            // TODO Auto-generated method stub
            mIsShare = false;
            commitModelValue = CommitModel.saveMessage;
//			if(mIsScanTask) {
//				title = Utils.getFileNameFromPath(mOpenSlidePath);
//			}
            saveSlide(title, content);
        }

        @Override
        public void discard() {
            if (uploadDialog != null) {
                UIUtils.hideSoftKeyboardValid(mContext, uploadDialog.getEditText());
            }
            // TODO Auto-generated method stub
            //扫码识任务选择放弃删除数据库记录及本地文件夹
            if (mIsScanTask) {
                String path = mOpenSlidePath;
                if (path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }
                LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(mContext, mMemberId, path);
                if (dto != null) {
                    LocalCourseDTO.deleteLocalCourseByPath(mContext, mMemberId, path, true);
                } else {
                    Utils.safeDeleteDirectory(path);
                }
            }
            mContext.setResult(Activity.RESULT_OK);
            mContext.finish();
        }
    };

    private void showRenameDialog(String title) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
                mContext, null, mContext.getString(R.string.save_file_exist, title),
                mContext.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "", null);
        dialog.show();
    }


    private DialogHelper.LoadingDialog loadingDialog;

    private void uploadWawaPage(final String path, String title) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        final UserInfo userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(mContext, R.string.pls_login);
            return;
        }

        final LocalCourseDTO data = LocalCourseDTO.getLocalCourse(mContext, mMemberId, path);
        final MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setPath(data.getmPath());
        mediaInfo.setTitle(title);
        mediaInfo.setThumbnail(BaseUtils.joinFilePath(data.getmPath(), BaseUtils.RECORD_HEAD_IMAGE_NAME));
        mediaInfo.setMicroId(String.valueOf(data.getmMicroId()));
        mediaInfo.setMediaType(MediaType.ONE_PAGE);
        mediaInfo.setDescription(data.getmDescription());
        List<String> titles = new ArrayList<String>();
        titles.add(title);
        if (!TextUtils.isEmpty(mTaskId)) {
            uploadResource(mediaInfo, userInfo, data);
        } else {
            if (data.getmMicroId() > 0) {
                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(mContext);
                wawaCourseUtils.loadCourseDetail(String.valueOf(data.getmMicroId()));
                wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                    @Override
                    public void onCourseDetailFinish(CourseData courseData) {
                        if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                            mediaInfo.setResourceUrl(courseData.resourceurl);
                            if (!mIsScanTask) {
                                uploadResource(mediaInfo, userInfo, data);
                            } else {
                                uploadWawaPageToTask(mediaInfo, userInfo, data);
                            }
                        }
                    }
                });
            } else {
                //来自lq云板的分享的数据
                if (fromType == FromWhereData.FROM_LQCLOUD_COURSE) {
                    shareLocalCourse(data.toLocalCourseInfo());
                } else {
//					checkResourceTitle(mediaInfo, titles, userInfo, data);
                    if (mIsScanTask) {
                        uploadWawaPageToTask(mediaInfo, userInfo, data);
                    } else {
                        uploadResource(mediaInfo, userInfo, data);
                    }
                }
            }
        }
    }

    private void shareLocalCourse(LocalCourseInfo localCourseInfo) {
        final UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(mContext);
            return;
        }
        //更新MicroId
        String path = null;
        if (localCourseInfo != null) {
            path = localCourseInfo.mPath;
            if (!TextUtils.isEmpty(path) && path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(mContext, userInfo.getMemberId(), path);
            if (dto != null && dto.getmMicroId() > 0) {
                localCourseInfo.mMicroId = dto.getmMicroId();
            }
            helper.setIsLocal(true);
            helper.setIsTempData(true);
            helper.uploadCourse(userInfo, localCourseInfo, null, mShareType);
        }
    }

    private void uploadWawaPageToTask(MediaInfo mediaInfo, final UserInfo userInfo, LocalCourseDTO
            data) {

        final String schoolId = mContext.getIntent().getStringExtra
                (SlideWawaPageActivity.SCHOOL_ID);
        final String classId = mContext.getIntent().getStringExtra
                (SlideWawaPageActivity.CLASS_ID);

        if (mediaInfo != null) {
            final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaInfo, 1);
            if (uploadParameter != null) {
                uploadParameter.setFromType(fromType);
                uploadParameter.setScreenType(mOrientation);
                if (mShareType == ShareType.SHARE_TYPE_STUDY_TASK) {
                    List<ShortSchoolClassInfo> schoolClassInfos = new ArrayList<ShortSchoolClassInfo>();
                    ShortSchoolClassInfo schoolClassInfo = new ShortSchoolClassInfo();
                    schoolClassInfo.setSchoolId(schoolId);
                    schoolClassInfo.setClassId(classId);
                    schoolClassInfos.add(schoolClassInfo);
                    uploadParameter.setShortSchoolClassInfos(schoolClassInfos);
                    uploadParameter.setLocalCourseDTO(data);
                    if (TextUtils.isEmpty(schoolId) && TextUtils.isEmpty(classId)) {
                        uploadParameter.setIsScanTask(true);
                        UploadUtils.enterContactsPicker(mContext, uploadParameter);
                        mContext.finish();
                    } else {
                        Intent intent = new Intent(mContext, MyTaskListActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
                        args.putBoolean(MyTaskListFragment.EXTRA_IS_SCAN_TASK, true);
                        intent.putExtras(args);
                        if (mContext != null) {
                            mContext.startActivity(intent);
                            mContext.finish();
                        }
                    }
                    return;
                } else if (mShareType == ShareType.SHARE_TYPE_CLASSROOM) {
                    if (TextUtils.isEmpty(schoolId) && TextUtils.isEmpty(classId)) {
                        uploadParameter.setShareType(mShareType);
                        uploadParameter.setType(mShareType);
                        uploadParameter.setLocalCourseDTO(data);
                        UploadUtils.enterContactsPicker(mContext, uploadParameter, mShareType);
                        mContext.finish();
                    }
                    return;
                } else if (mShareType == ShareType.SHARE_TYPE_PUBLIC_COURSE) {
                    uploadParameter.setShareType(mShareType);
                    uploadParameter.setTempData(true);
                    uploadParameter.setType(mShareType);
                    uploadParameter.setLocalCourseDTO(data);
                    enterContactsPicker(uploadParameter, mediaInfo.getTitle(), null, mShareType);
                    mContext.finish();
                    return;
                } else if (mShareType == ShareType.SHARE_TYPE_CLOUD_COURSE) {
                    uploadParameter.setShareType(mShareType);
                    uploadParameter.setType(mShareType);
                    uploadParameter.setLocalCourseDTO(data);
                    publishToPersonal(uploadParameter, mediaInfo.getTitle(), mShareType);
                    mContext.finish();
                    return;
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog = DialogHelper.getIt(mContext).GetLoadingDialog(0);
                        loadingDialog.setCancelable(false);
                    }
                });
                FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                        mediaInfo.getPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(mediaInfo.getPath()) + Utils.COURSE_SUFFIX);
                FileZipHelper.zip(param,
                        new FileZipHelper.ZipUnzipFileListener() {
                            @Override
                            public void onFinish(
                                    FileZipHelper.ZipUnzipResult result) {
                                // TODO Auto-generated method stub
                                if (result != null && result.mIsOk) {
                                    uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                    UploadUtils.uploadResource(mContext, uploadParameter, new CallbackListener() {
                                        @Override
                                        public void onBack(Object result) {
                                            mContext.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (loadingDialog != null) {
                                                        loadingDialog.dismiss();
                                                    }
                                                }
                                            });
                                            if (result != null) {
                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                if (uploadResult.code != 0) {
                                                    mContext.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_failed);
                                                        }
                                                    });
                                                    return;
                                                }
                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                    final CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        switch (mShareType) {
                                                            case ShareType.SHARE_TYPE_CONTACTS:
                                                                shareToContacts(courseData, userInfo);
                                                                break;
                                                            case ShareType.SHARE_TYPE_CLASSROOM:
                                                                shareToClassRoom(courseData,
                                                                        userInfo, uploadParameter);
                                                                break;
                                                            case ShareType.SHARE_TYPE_WECHAT:
                                                            case ShareType
                                                                    .SHARE_TYPE_WECHATMOMENTS:
                                                            case ShareType.SHARE_TYPE_QQ:
                                                            case ShareType.SHARE_TYPE_QZONE:
                                                                mContext.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        shareToType(courseData);
                                                                    }
                                                                });

                                                                break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
    }

    void publishToPersonal(UploadParameter uploadParameter, final String dataTitle, final int shareType) {
        if (uploadParameter == null) {
            return;
        }

        if (uploadParameter != null && !TextUtils.isEmpty(dataTitle)) {
            uploadParameter.setFileName(dataTitle);
        }

        if (uploadParameter != null) {
            new UploadCourseHelper(mContext).uploadResource(uploadParameter, shareType);
        }
    }

    public void enterContactsPicker(UploadParameter uploadParameter, String dataTitle, String dataDesc,
                                    int shareType) {
        if (uploadParameter == null) {
            return;
        }

        Bundle args = new Bundle();
        args.putString("Title", dataTitle);
        args.putString("Content", dataDesc);
        args.putInt("TargetType", shareType);
        if (uploadParameter != null && !TextUtils.isEmpty(dataTitle)) {
            uploadParameter.setFileName(dataTitle);
        }
        args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_TYPE, ContactsPickerActivity.PICKER_TYPE_GROUP);
        if (shareType == ShareType.SHARE_TYPE_CLASSROOM) {
            args.putInt(ContactsPickerActivity.EXTRA_GROUP_TYPE,
                    ContactsPickerActivity.GROUP_TYPE_CLASS);
        } else if (shareType == ShareType.SHARE_TYPE_PICTUREBOOK ||
                shareType == ShareType.SHARE_TYPE_PUBLIC_COURSE) {
            args.putInt(ContactsPickerActivity.EXTRA_GROUP_TYPE,
                    ContactsPickerActivity.GROUP_TYPE_SCHOOL);
            args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE,
                    ContactsPickerActivity.ROLE_TYPE_TEACHER);
        }
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_MODE,
                ContactsPickerActivity.PICKER_MODE_SINGLE);
        args.putString(ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, mContext.getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);

        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        Intent intent = new Intent(mContext, ContactsPickerActivity.class);
        intent.putExtras(args);
        mContext.startActivity(intent);
    }

    private void commitCourseToClassroom(CourseData courseData, String schoolId, String classId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", courseData.code);
        params.put("SchoolId", schoolId);
        params.put("MicroID", courseData.getIdType());
        params.put("ClassId", classId);
        params.put("ActionType", String.valueOf(6));
        params.put("Title", courseData.nickname);
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(mContext, DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataResult result = (DataResult) getResult();
                if (result != null && result.isSuccess()) {
                    TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
                    if (mContext != null) {
                        mContext.finish();
                    }
                } else {
                    TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
                }
            }
        };
        listener.setTimeOutMs(15000);
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(mContext, ServerUrl.UPLOAD_CLASS_SPACE_URL, params, listener);
    }

    private void shareOnePage(CourseData courseData) {
        ShareUtils shareUtils = new ShareUtils(mContext);
        shareUtils.setIsFinish(true);
        shareUtils.share(getRootView(), courseData.getShareInfo(mContext));
    }

    private void checkResourceTitle(final MediaInfo mediaInfo, final List<String> titles, final
    UserInfo userInfo, final LocalCourseDTO data) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(mContext, R.string.pls_login);
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
//		params.put("MType", String.valueOf(MediaType.ONE_PAGE));
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(MediaType.MICROCOURSE));
        builder.append(",");
        builder.append(String.valueOf(MediaType.ONE_PAGE));
        params.put("MTypes", builder.toString());
        params.put("Title", titles);
        RequestHelper.RequestDataResultListener listener = new RequestHelper.RequestDataResultListener<ResourceTitleResult>(
                mContext, ResourceTitleResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                ResourceTitleResult result = getResult();
                if (result == null || result.getModel() == null || !result.isSuccess()) {
                    return;
                }
                ResourceTitle resourceTitle = result.getModel().getData();
                List<String> titleList;
                if (resourceTitle != null) {
                    titleList = resourceTitle.getTitle();
                    if (titleList != null && titleList.size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        int size = titleList.size();
                        String title;
                        for (int i = 0; i < size - 1; i++) {
                            title = titleList.get(i);
                            builder.append(title + ",");
                        }
                        title = titleList.get(size - 1);
                        builder.append(title);
                        showUploadInfoDialog(builder.toString());
                        mOpenSlidePath = BaseUtils.joinFilePath(mDefaultCourseSaveRoot, mSaveTitle);
                    }
                } else {
                    if (mIsScanTask) {
                        uploadWawaPageToTask(mediaInfo, userInfo, data);
                    } else {
                        uploadResource(mediaInfo, userInfo, data);
                    }
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(mContext, ServerUrl.CHECK_CLOUD_RESOURCE_TITLE_URL,
                params, listener);
    }

    private void commitStudentHomework(String taskId, UserInfo userInfo, CourseData courseData) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", userInfo.getMemberId());
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }

        RequestHelper.RequestDataResultListener listener = new RequestHelper.RequestDataResultListener(mContext,
                DataResult.class) {
            @Override
            public void onSuccess(String json) {
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        TipMsgHelper.ShowLMsg(mContext, R.string.publish_course_ok);
                        if (mContext != null) {
                            mContext.finish();
                        }
                    } else {
                        String errorMessage = mContext.getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(mContext, errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(mContext, ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL, params, listener);
    }

    private void uploadResource(MediaInfo mediaInfo, final UserInfo myUserInfo, final
    LocalCourseDTO data) {
        if (mediaInfo != null) {
            final UserInfo userInfo;
            if (mStuUserInfo != null) {
                userInfo = mStuUserInfo;
            } else {
                userInfo = mUserInfo;
            }
            final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaInfo, 1);
            if (uploadParameter != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog = DialogHelper.getIt(mContext).GetLoadingDialog(0);
                        loadingDialog.setCancelable(false);
                    }
                });
                FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                        mediaInfo.getPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(mediaInfo.getPath()) + Utils.COURSE_SUFFIX);
                FileZipHelper.zip(param,
                        new FileZipHelper.ZipUnzipFileListener() {
                            @Override
                            public void onFinish(
                                    FileZipHelper.ZipUnzipResult result) {
                                // TODO Auto-generated method stub
                                if (result != null && result.mIsOk) {
                                    uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                    UploadUtils.uploadResource(mContext, uploadParameter, new CallbackListener() {
                                        @Override
                                        public void onBack(Object result) {
                                            mContext.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (loadingDialog != null) {
                                                        loadingDialog.dismiss();
                                                    }
                                                }
                                            });
                                            if (result != null) {
                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                if (uploadResult.code != 0) {
                                                    mContext.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_failed);
                                                        }
                                                    });
                                                    return;
                                                }
                                                if (TextUtils.isEmpty(mTaskId)) {
                                                    if (data.getmMicroId() == 0) {
                                                        MediaListFragment.updateMedia(mContext, userInfo, uploadResult.getShortCourseInfoList(), MediaType.ONE_PAGE);
                                                    } else {
                                                        mContext.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                TipMsgHelper.ShowLMsg(mContext, R.string.update_success);
                                                            }
                                                        });
                                                    }
                                                }
                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                    final CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        if (TextUtils.isEmpty(mTaskId)) {
                                                            if (mIsShare) {
                                                                mContext.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        shareOnePage(courseData);
                                                                    }
                                                                });
                                                            } else {
                                                                mContext.finish();
                                                            }
                                                        } else {
                                                            commitStudentHomework(mTaskId, userInfo, courseData);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
    }

    private void showUploadInfoDialog(String info) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
                mContext, null, mContext.getString(R.string.cloud_resource_upload_exist, info),
                mContext.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "", null);
        dialog.show();
    }

    public void showUploadView() {
        helper = new CommitCourseHelper(mContext);
        if (mIsScanTask) {
            helper.setIsLocal(true);
            helper.setIsStudyTask(true);
            UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
            if (userInfo != null && userInfo.isTeacher()) {
                helper.setIsTeacher(isEntityTeacher);
            }

            if (userInfo != null && userInfo.isStudent()) {
                helper.setIsStudent(true);
            }
        }
        if (fromType == FromWhereData.FROM_LQCLOUD_COURSE) {
            UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
            if (userInfo != null && userInfo.isTeacher()) {
                helper.setIsTeacher(isEntityTeacher);
            }
            if (userInfo != null && userInfo.isStudent()) {
                helper.setIsStudent(true);
            }
            helper.setIsLocal(true);
        }
        helper.commit(mTouchView, null);
        helper.setNoteCommitListener(this);
        helper.setPopWindowDissmissListener(this);
    }

    @Override
    public void noteCommit(int shareType) {
        mShareType = shareType;
        //解决标题为空的问题
        if (!TextUtils.isEmpty(mCourseTitle) && TextUtils.isEmpty(mSaveTitle)) {
            mSaveTitle = mCourseTitle;
        }
        uploadWawaPage(mOpenSlidePath, mSaveTitle);
    }

    @Override
    public void popWindowDismiss() {
        mShareType = -1;
    }

    public void shareToContacts(final CourseData courseData, UserInfo userInfo) {
        MediaListFragment.updateMedia(mContext, userInfo, courseData.getShortCourseInfoList(),
                MediaType.ONE_PAGE, new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        PublishResourceFragment.enterContactsPicker(mContext, courseData.getSharedResource());
                        if (mContext != null) {
                            mContext.finish();
                        }
                    }
                });
    }

    public void shareToClassRoom(CourseData courseData, UserInfo userInfo, UploadParameter uploadParameter) {
        String schoolId = mContext.getIntent().getStringExtra(SlideWawaPageActivity.SCHOOL_ID);
        String classId = mContext.getIntent().getStringExtra(SlideWawaPageActivity.CLASS_ID);
        if (uploadParameter != null) {
            uploadParameter.setSchoolIds(schoolId);
            uploadParameter.setClassId(classId);
        }
        UploadCourseManager.commitCourseToClassSpace(mContext, courseData, uploadParameter, true);
    }

    public void shareToType(CourseData courseData) {
        CourseInfo courseInfo = courseData.getNewResourceInfo().getCourseInfo();
        if (courseInfo != null) {
            ShareInfo shareInfo = courseInfo.getShareInfo(mContext);
            if (shareInfo != null && helper != null) {
                helper.shareTo(mShareType, shareInfo);
            }
        }
    }

    private void loadUserInfoData() {
        UserInfoHelper userInfoHelper = new UserInfoHelper(mContext);
        userInfoHelper.setCallBackListener((obj) -> {
            if (obj != null && obj instanceof UserInfo) {
                UserInfo info = (UserInfo) obj;
                isEntityTeacher = Utils.isRealTeacher(info.getSchoolList());
            }
        });
        userInfoHelper.check();
    }


}
