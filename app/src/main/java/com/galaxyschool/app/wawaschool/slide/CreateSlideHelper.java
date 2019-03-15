package com.galaxyschool.app.wawaschool.slide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.course.PlaybackWawaPageActivityPhone;
import com.galaxyschool.app.wawaschool.course.SlideActivityNew;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.libs.yilib.pickimages.MediaInfo;
import com.libs.yilib.pickimages.PickMediasActivity;
import com.libs.yilib.pickimages.PickMediasFragment;
import com.libs.yilib.pickimages.PickMediasParam;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.tools.ResourceUtils;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInPlaybackParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInputParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;
import com.osastudio.common.utils.ConstantSetting;
import com.osastudio.common.utils.FileProviderHelper;
import com.osastudio.common.utils.PhotoUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateSlideHelper {

	final static public int SLIDETYPE_WHITEBOARD = 0;
	final static public int SLIDETYPE_CAMERA = 1;
	final static public int SLIDETYPE_IMAGE = 2;
	final static public int SLIDETYPE_DRAFT = 3;
	final static public int SLIDETYPE_RECORD_COURSE=11;
	private static String mCameraImagePath = null;
	private static Activity mCurrentActivity = null;
	private static int mCurrentEntryType = 0;
	private static CreateSlideParam mCreateSlideParam;

	//shouyi add
	public static void createSlide(CreateSlideParam param) {
		if (param == null || (param.mActivity == null && param.mFragment == null)) {
			return;
		}
		param.mSlideParam = getDefaultSlideParam();
		Activity activity;
		if (param.mActivity != null) {
			activity = param.mActivity;
		} else {
			activity = param.mFragment.getActivity();
		}
		mCurrentActivity = activity;
		mCurrentEntryType = param.mEntryType;
		if (param.mShowSelectSlideType) {
			showSlideTypeSelectDialog(param);
		} else {
			preStartSlide(param);
		}
	}

    //shouyi add
	private static void showSlideTypeSelectDialog(final CreateSlideParam param) {
		Activity activity = param.mActivity != null ? param.mActivity : param.mFragment.getActivity();
		Dialog alertDialog = new AlertDialog.Builder(activity).setItems(
				R.array.new_attachment_type,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						param.mSlideType = which;
						preStartSlide(param);
					}
				}).create();
		alertDialog.show();
	}
	
	//shouyi add
	private static void preStartSlide(CreateSlideParam param) {
		int requestCode = Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + param.mEntryType;
		switch (param.mSlideType) {
		case 0:
			param.mIsCreateAndPassResParam = true;
			startSlide(param, requestCode);
			break;
		case 1:
			cameraImageView(param);
			break;
		case 2:
			param.mIsCreateAndPassResParam = true;
			if (param.mIsPickOneImage) {
				showImportImgBrowser(param);
			} else {
				param.mAttachmentPath = SlideManager.INSERT_IMAGES;
				pickImages(param);
			}
			break;
		case 3:
//			jumpToDraft(param);
			break;
		}
	}

	//shouyi changed
    public static boolean processActivityResule(Activity activity, Fragment fragment,
            int requestCode, int resultCode, Intent resultData) {
		boolean rtn = false;
		if (activity == null && fragment == null) {
			return false;
		}
		if (activity == null && fragment != null) {
			activity = fragment.getActivity();
		}
		if (mCurrentActivity != null && mCurrentActivity == activity) {
			int request = requestCode - mCurrentEntryType;
			if (request == Common.ACTIVITY_REQUEST_CAMERA_PATH_BASE) {
				rtn = true;
				if (resultCode == Activity.RESULT_OK) {
		            String path = null;
		            if (resultData != null) {
		               Uri uri = resultData.getData();
		               if (uri != null) {
						   path = PhotoUtils.getImageAbsolutePath(activity, uri);
		               }
		            }
		            if (path == null) {
		               if (new File(mCameraImagePath).exists()) {
		                  path = mCameraImagePath;
		               }
		            }
		            enterSlideWithOneImage(activity, fragment, path);
				}
			} else if (request == Common.ACTIVITY_REQUEST_IMAGE_PATH_BASE) {
				rtn = true;
				if (resultCode == Activity.RESULT_OK) {
		            String filePath = null;
		            Uri selectedImage = resultData.getData();
		            if (selectedImage == null)
		               return rtn;
//		            String fileurl = selectedImage.toString();
//		            File f;
//		            if (fileurl.startsWith("file://")) {
//		               f = new File(URI.create(fileurl));
//		               if (!f.exists()) {
//		                  return rtn;
//		               } else {
//		                  filePath = f.getPath();
//		               }
//		            } else if (fileurl.startsWith("content://")) {
//		               String[] filePathColumn = { MediaStore.Images.Media.DATA };
//		               Cursor cursor = activity.getContentResolver().query(
//		                     selectedImage, filePathColumn, null, null, null);
//		               if (cursor != null) {
//		                  cursor.moveToFirst();
//		                  int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//		                  filePath = cursor.getString(columnIndex);
//		                  cursor.close();
//		               }
//		            }
					filePath = PhotoUtils.getImageAbsolutePath(activity, selectedImage);
					if (TextUtils.isEmpty(filePath)) {
						return rtn;
					}
		            enterSlideWithOneImage(activity, fragment, filePath);
				}
			}
		}
		if (mCurrentActivity != null && requestCode==SlideManager.REQUEST_CODE_PICK_IMAGE && resultCode ==Activity.RESULT_OK){
			ArrayList<MediaInfo> imageInfos = resultData.getParcelableArrayListExtra(PickMediasFragment.PICK_IMG_RESULT);
			if (imageInfos != null && imageInfos.size() > 0) {
				//Intent it = getSlideIntent(mCreateSlideParam);
				//it.putParcelableArrayListExtra(PickMediasFragment.PICK_IMG_RESULT, imageInfos);
				mCreateSlideParam.mMediaInfos = imageInfos;
				mCreateSlideParam.mMediaType = MediaType.PICTURE;
				Intent it = getSlideNewIntent(mCreateSlideParam);
				int rc = Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + mCreateSlideParam.mEntryType;
				if (fragment != null) {
					requestCode = Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + mCreateSlideParam.mEntryType;
					fragment.startActivityForResult(it, requestCode);
	    		} else {
	    			activity.startActivityForResult(it, rc);
	    		}
				mCurrentActivity = null;
			}
		}///storage/emulated/0/douyu_img/27961d50942ab19fc4e3284e45e6f1f3.jpg
		//制作录音课件的回调
		if (mCurrentActivity!=null&&requestCode==SLIDETYPE_RECORD_COURSE&&resultCode==Activity.RESULT_OK){
			ArrayList<MediaInfo> imageInfos = resultData.getParcelableArrayListExtra(PickMediasFragment.PICK_IMG_RESULT);
			if (imageInfos != null && imageInfos.size() > 0) {
				mCreateSlideParam.mMediaInfos = imageInfos;
				mCreateSlideParam.mMediaType = MediaType.PICTURE;
				Intent it = getSlideNewCourseIntent(mCreateSlideParam);
				int rc = Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + mCreateSlideParam.mEntryType;
				if (fragment != null) {
					requestCode=ResourceBaseFragment.REQUEST_CODE_SLIDE;
					fragment.startActivityForResult(it, requestCode);
				} else {
					activity.startActivityForResult(it, rc);
				}
				mCurrentActivity = null;
			}
		}
		return rtn;
	}
    
    //shouyi add
    private static void enterSlideWithOneImage(Activity activity , Fragment fragment, String filePath) {
    	if (mCreateSlideParam != null && mCurrentActivity == activity) {
    		mCreateSlideParam.mMediaType = MediaType.PICTURE;
    		mCreateSlideParam.mMediaInfos = new ArrayList<MediaInfo>();
    		mCreateSlideParam.mIsCreateAndPassResParam = true;
    		MediaInfo mediaInfo = new MediaInfo(filePath);
    		mCreateSlideParam.mMediaInfos.add(mediaInfo);
    		startSlide(mCreateSlideParam, mCreateSlideParam.mEntryType);
    		mCreateSlideParam = null;
		} else {
			if (filePath != null) {
				onActivityResultStartSlide(activity, fragment, filePath, mCurrentEntryType, MediaType.PICTURE);
			}
		}
    }
    
    //shouyi add
    private static void onActivityResultStartSlide(Activity activity, Fragment fragment, String attachmentPath,
            int entryType, int mediaType) {
		int requestCode = Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + entryType;
		CreateSlideParam param = new CreateSlideParam();
		param.mSlideParam = getDefaultSlideParam();
		param.mActivity = activity;
		param.mFragment = fragment;
		//param.mAttachmentPath = attachmentPath;
		param.mEditable = true;
		param.mEntryType = entryType;
		param.mMediaType = mediaType;
		param.mMediaInfos = new ArrayList<MediaInfo>();
		param.mIsCreateAndPassResParam = true;
		MediaInfo mediaInfo = new MediaInfo(attachmentPath);
		param.mMediaInfos.add(mediaInfo);
		startSlide(param, requestCode);
	}

    private static void cameraImageView(CreateSlideParam param) {
		mCreateSlideParam = param;
    	Activity activity = param.mActivity;
    	Fragment fragment = param.mFragment;
    	int entryType = param.mEntryType;
		int requestCode = Common.ACTIVITY_REQUEST_CAMERA_PATH_BASE + entryType;// getRequestCodeByListType(Common.ACTIVITY_REQUEST_CAMERA_PATH_BASE);
		long dateTaken = System.currentTimeMillis();
		String name = Long.toString(dateTaken) + ".jpg";
		mCameraImagePath = Common.PhotoPath + name;
		File cameraImg = new File(mCameraImagePath);
		if (cameraImg != null && cameraImg.exists()) {
			cameraImg.delete();
		} else if (cameraImg != null) {
			if (cameraImg.getParentFile() != null
					&& !cameraImg.getParentFile().exists()) {
				cameraImg.getParentFile().mkdirs();
			}
		}
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra("output", FileProviderHelper.getUriForFile((activity != null ? activity: fragment.getActivity()),
				new File(mCameraImagePath)));
		if (fragment != null) {
			fragment.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}
	}
    
    private static void pickImages(CreateSlideParam param) {
    	Activity activity2 = param.mActivity != null ? param.mActivity: param.mFragment.getActivity();
    	Intent intent = new Intent(activity2, PickMediasActivity.class);
    	intent.putExtra(PickMediasFragment.PICK_IMG_PARAM, initPickImgParam().mPickImagesParam);
    	if (param.mFragment != null) {
			//0表示点读课件 1表示录音课件
			if (param.courseMode==0){
				param.mFragment.startActivityForResult(intent, SlideManager.REQUEST_CODE_PICK_IMAGE);
			}else {
				param.mFragment.startActivityForResult(intent, SLIDETYPE_RECORD_COURSE);
			}
		} else {
			if (param.courseMode==0){
				param.mActivity.startActivityForResult(intent, SlideManager.REQUEST_CODE_PICK_IMAGE);
			}else {
				param.mActivity.startActivityForResult(intent,SLIDETYPE_RECORD_COURSE);
			}

		}
    	mCreateSlideParam = param;
    	mCurrentActivity = activity2;
   }
	
    private static void showImportImgBrowser(CreateSlideParam param) {
		mCreateSlideParam = param;
    	Activity activity = param.mActivity;
    	Fragment fragment = param.mFragment;
    	int entryType = param.mEntryType;
    	int requestCode = Common.ACTIVITY_REQUEST_IMAGE_PATH_BASE + entryType;// getRequestCodeByListType(Common.ACTIVITY_REQUEST_IMAGE_PATH_BASE);
		Intent intent = new Intent();
		intent.setType("image/*");
		if (Build.VERSION.SDK_INT < 19) {
			intent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		if (fragment != null) {
			fragment.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}
	}
    
    //shouyi add
    public static void startSlide(CreateSlideParam param, int requestCode) {
    	if (param != null && (param.mActivity != null || param.mFragment != null)) {
    		if (!TextUtils.isEmpty(param.mAttachmentPath) && param.mAttachmentPath.endsWith(".chw")) {
//    			transformZipFileToFolder(param, requestCode);
			} else {
				enterSlide(param, requestCode);
			}
		}
    }
    
    private static void enterSlide(CreateSlideParam param, int requestCode) {
    	if (param != null && (param.mActivity != null || param.mFragment != null)) {
			Intent it = null;
			if (param.courseMode == 0) {
				it = getSlideNewIntent(param);//getSlideIntent(param);
				requestCode = Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + param.mEntryType;
			} else {
				//进入录音课件的界面
				it = getSlideNewCourseIntent(param);
				requestCode = ResourceBaseFragment.REQUEST_CODE_SLIDE;
			}
			if (param.mFragment != null) {
				param.mFragment.startActivityForResult(it, requestCode);
			} else {
				param.mActivity.startActivityForResult(it, requestCode);
			}
			mCurrentActivity = null;
			mCurrentEntryType = 0;
		}
    }


   	public static Intent getSlideNewCourseIntent(CreateSlideParam param){
		Intent it = new Intent();
		Activity activity = param.mActivity != null ? param.mActivity
				: param.mFragment.getActivity();
		it.setClass(activity, SlideActivityNew.class);
		it.putExtra(SlideActivityNew.ORIENTATION, param.mOrientation);
		if (param.isFromStudyTask){
			it.putExtra(SlideActivityNew.LOAD_FILE_TITLE,param.mTitle);
			it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
		}else {
			it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
			it.putExtra(SlideActivityNew.COURSE_TYPE_FROM_LQ_BOARD, param.isFromLqBoard);
		}

		if(param.mOrientation >= 0) {
			it.putExtra(SlideActivityNew.ORIENTATION, param.mOrientation);
		} else {
			it.putExtra(SlideActivityNew.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		SlideInputParam slideInputParam=getSlideInputParam(param);
		slideInputParam.mNotShowShareBoxBtn = true;
		slideInputParam.mIsCreateAndPassResParam = true;
		int[] rayMenuV = {
				BaseSlideManager.MENU_ID_CAMERA,
				BaseSlideManager.MENU_ID_IMAGE,
				BaseSlideManager.MENU_ID_WHITEBOARD,
				BaseSlideManager.MENU_ID_AUDIO,
				BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
		};
		if (!param.isFromStudyTask) {
			MyApplication application = (MyApplication) activity.getApplicationContext();
			if (application != null) {
				UserInfo userInfo = application.getUserInfo();
				if (userInfo != null && userInfo.isTeacher()) {
					rayMenuV = Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
					rayMenuV[rayMenuV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
				}
			}
		}
		slideInputParam.mRayMenusV = rayMenuV;
		int[] rayMenuH = {BaseSlideManager.MENU_ID_CURVE, BaseSlideManager.MENU_ID_LASER,
				BaseSlideManager.MENU_ID_ERASER};
		slideInputParam.mRayMenusH = rayMenuH;
		it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
		return  it;
	}

    public static Intent getSlideNewIntent(CreateSlideParam param) {
    	Intent it = new Intent();
    	Activity activity = param.mActivity != null ? param.mActivity
				: param.mFragment.getActivity();
		it.setClass(activity, SlideWawaPageActivity.class);
		it.putExtra(SlideWawaPageActivity.LOAD_FILE_PATH, param.mAttachmentPath);
		it.putExtra(SlideWawaPageActivity.TITLE, param.mTitle);
    	it.putExtra(SlideWawaPageActivity.LOAD_FILE_PAGES, 0);
        it.putExtra(SlideWawaPageActivity.COUTSE_TYPE, MaterialType.ONEPAGE_BOOK);
//    	it.putExtra(SlideWawaPageActivity.ORIENTATION, 1);
		if(param.mOrientation >= 0) {
			it.putExtra(SlideWawaPageActivity.ORIENTATION, param.mOrientation);
		} else {
			it.putExtra(SlideWawaPageActivity.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
    	SlideInputParam slideInputParam = getSlideInputParam(param);
		int[] rayMenuV = {
				BaseSlideManager.MENU_ID_CAMERA,
				BaseSlideManager.MENU_ID_IMAGE,
				BaseSlideManager.MENU_ID_WHITEBOARD,
				BaseSlideManager.MENU_ID_PAGE_HORN_AUDIO,
				BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
		};
		if (param.cardParam != null && (param.cardParam.isOnlineHost() || param.cardParam.isOnlineReporter())) {
			rayMenuV = Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
			rayMenuV[rayMenuV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
		}
		if (param.fromType != SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE
				&& param.fromType != SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_OTHER_COURSE
				&& !param.mIsIntroducationTask) {
			MyApplication application = (MyApplication) activity.getApplicationContext();
			if (application != null) {
				UserInfo userInfo = application.getUserInfo();
				if (userInfo != null && userInfo.isTeacher()) {
					rayMenuV = Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
					rayMenuV[rayMenuV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
				}
			}
		}
    	slideInputParam.mRayMenusV = rayMenuV;
		if (param.cardParam != null){
		    //任务单答题卡批阅加T
            int[] rayMenuH = {
                    BaseSlideManager.MENU_ID_TEXT_POINTER,
                    BaseSlideManager.MENU_ID_PAGE_HORN_RECORD,
                    BaseSlideManager.MENU_ID_CURVE,
                    BaseSlideManager.MENU_ID_ERASER};
            slideInputParam.mRayMenusH = rayMenuH;
        } else {
            int[] rayMenuH = {
                    BaseSlideManager.MENU_ID_PAGE_HORN_RECORD,
                    BaseSlideManager.MENU_ID_CURVE,
                    BaseSlideManager.MENU_ID_ERASER};
            slideInputParam.mRayMenusH = rayMenuH;
        }

		//support A4 paper ratio for course maker
		if(param.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
		} else {
			slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
		}
    	it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
    	it.putExtra(SlideSaveBtnParam.class.getSimpleName(), param.mSlideSaveBtnParam);
		it.putExtra(SlideWawaPageActivity.TASK_ID, param.mTaskId);
		it.putExtra(SlideWawaPageActivity.IS_SCAN_TASK, param.mIsScanTask);
		it.putExtra(SlideWawaPageActivity.SCHOOL_ID, param.mSchoolId);
		it.putExtra(SlideWawaPageActivity.CLASS_ID, param.mClassId);
		it.putExtra(SlideWawaPageActivity.IS_INTRODUCTION_TASK,param.mIsIntroducationTask);
		it.putExtra(SlideWawaPageActivity.COURSE_FROM_TYPE,param.fromType);
		//传递userInfo用来支持家长帮助孩子提交作业。
		it.putExtra(UserInfo.class.getSimpleName(),param.mUserInfo);
		it.putExtra(SlideWawaPageActivity.COURSE_SECTION_DATA_STRING, param.courseSectionDataString);
		it.putExtra(SlideWawaPageActivity.MODEL_SOURCE_FROM,param.isFromMoocModel);
		it.putExtra(ExerciseAnswerCardParam.class.getSimpleName(),param.cardParam);
		if (param.cardParam != null && param.cardParam.getMarkModel() != null) {
			it.putExtra(SlideWawaPageActivity.EXTRA_EDIT_EXERCISE,true);
			if (!TextUtils.isEmpty(param.cardParam.getExerciseAnswerString())) {
				JSONArray jsonArray = JSONObject.parseArray(param.cardParam.getExerciseAnswerString());
				if (jsonArray != null && jsonArray.size() > 0) {
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					it.putExtra(SlideWawaPageActivity.EXTRA_EXERCISE_STRING,jsonObject.toString());
				}
			}
			if (!TextUtils.isEmpty(param.cardParam.getStudentCommitAnswerString())) {
				it.putExtra(SlideWawaPageActivity.EXTRA_EXERCISE_ANSWER_STRING,param.cardParam.getStudentCommitAnswerString());
			}
			it.putExtra(SlideWawaPageActivity.EXTRA_PAGE_INDEX,param.cardParam.getPageIndex());
		}
    	return it;
    }
    
    private static SlideInputParam getSlideInputParam(CreateSlideParam cParam) {
    	SlideInputParam param = null;
    	if (cParam != null) {
    		Activity activity = cParam.mActivity != null ? cParam.mActivity
    				: cParam.mFragment.getActivity();
    		if (activity != null) {
    			param = new SlideInputParam();
    			param.mCurUser = new User();
				//家长传递的孩子信息
				UserInfo userInfo = cParam.mUserInfo;
				if (userInfo == null) {
					MyApplication application = (MyApplication) activity.getApplication();
					userInfo = application.getUserInfo();
				}
    			if (userInfo != null) {
    				param.mCurUser.mId = userInfo.getMemberId();
    				if (!TextUtils.isEmpty(userInfo.getRealName())) {
    					param.mCurUser.mName = userInfo.getRealName();
    				} else {
    					param.mCurUser.mName = userInfo.getNickName();
    				}
    			}
    			param.mIsCreateAndPassResParam = cParam.mIsCreateAndPassResParam;
    			param.mMediaType = cParam.mMediaType;
    			param.mMediaInfos = cParam.mMediaInfos;
    			param.mTitle = cParam.mTitle;
    			param.mNotShowSlideBtn = true;
				param.mContent=cParam.mContent;
    		}
		}
    	return param;
    }
    
	//shouyi add
	public static SlideParam getDefaultSlideParam() {
		SlideParam slideParam = new SlideParam();
		slideParam.mNeedCachePaintView = true;
		slideParam.mPickImagesInputParam = initPickImgParam();
		slideParam.mMenuActionParam = new SlideParam.MenuActionParam();
		slideParam.mMenuActionParam.mIsUseRayMenu = true;
		slideParam.mMenuActionParam.mIsShowThumbnails = true;
		return slideParam;
	}
	
	//shouyi add
	private static SlideParam.PickImagesInputParam initPickImgParam() {
		PickMediasParam param = new PickMediasParam();
        param.mColumns = 4;
        param.mConfirmBtnName = DemoApplication.getInstance().getString(ResourceUtils.getStringId(DemoApplication.getInstance(), "confirm"));
        // param.mDefaultImage = R.drawable.btn_camera;
        param.mIsActivityCalled = true;
        param.mLimitReachedTips = DemoApplication.getInstance().getString(ResourceUtils.getStringId(DemoApplication.getInstance(), "media_select_full_msg"));
        param.mPickLimitCount = ConstantSetting.SELECT_PICTURE_NUM;
        param.mSearchPath = "/mnt";
        param.mShowCountFormatString = DemoApplication.getInstance().getString(ResourceUtils.getStringId(DemoApplication.getInstance(), "media_show_count_msg"));
        param.mShowCountMode = 1;
        param.mSkipKeysOfFolder = AppSettings.getScanFilesSkipKeys();
        SlideParam.PickImagesInputParam param2 = new SlideParam.PickImagesInputParam();
        param2.mPickImagesParam = param;
        param2.mIs1Page1Image = true;
        return param2;
	}

	//shouyi changed
	public static void loadAndOpenSlideByCHWUrl(final OpenCHWParam param) {
		if (param == null || TextUtils.isEmpty(param.url) || param.activity == null) {
			return;
		}
		Class<? extends PlaybackActivity> destActivityClass = param.destActivity != null ? param.destActivity :
			PlaybackWawaPageActivityPhone.class;
		Intent intent = new Intent(param.activity, destActivityClass);
        Bundle extras = new Bundle();
        if (param.url.endsWith(".zip")) {
        	param.url = param.url.substring(0, param.url.lastIndexOf('.'));
        } else if (param.url.contains(".zip?")) {
        	param.url = param.url.substring(0, param.url.lastIndexOf(".zip?"));
		}
        extras.putString(PlaybackActivity.FILE_PATH, param.url);
        extras.putString(PlaybackActivity.CACHE_ROOT, Utils.getCacheDir());
        extras.putInt(PlaybackActivity.ORIENTATION, param.orientation);
        extras.putBoolean(PlaybackActivity.IS_SHOW_SLIDE, true);
        extras.putInt(PlaybackActivity.PLAYBACK_TYPE, BaseUtils.RES_TYPE_ONEPAGE);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(), getSlideInPlaybackParam
				(param.activity, param.isShareScreen, param.orientation));
		if(param.courseInfo != null) {
			extras.putParcelable(PlaybackActivity.COURSE_SHARE_DATA, param.courseInfo.toCourseShareData());
			extras.putParcelable(PlaybackActivity.COURSE_COLLECT_PARAMS, param.courseInfo.getCollectParams());
		}
		if (param.playbackParam != null) {
			extras.putSerializable(PlaybackParam.class.getSimpleName(), param.playbackParam);
			//任务单答题卡
			ExerciseAnswerCardParam cardParam = param.playbackParam.exerciseCardParam;
			if (cardParam != null) {
				extras.putBoolean(PlaybackActivity.EXTRA_SHOW_EXERCISE_BUTTON, cardParam.isShowExerciseButton());
				extras.putBoolean(PlaybackActivity.EXTRA_SHOW_EXERCISE_NODE,cardParam.isShowExerciseNode());
				extras.putString(PlaybackActivity.EXTRA_EXERCISE_ANSWER_STRING, cardParam.getStudentCommitAnswerString());
			}
			//指定的pageIndex
			if (param.playbackParam.pageIndex >= 0){
				extras.putInt(PlaybackActivity.EXTRA_PAGE_INDEX,param.playbackParam.pageIndex);
			}
		}
        intent.putExtras(extras);
        if (param.listener != null) {
        	param.listener.onBack(true);
		}
        param.activity.startActivity(intent);
	}
	
	private static SlideInPlaybackParam getSlideInPlaybackParam(Activity context, boolean
			isShareScreen, int orientation) {
		SlideInPlaybackParam result = null;
		if (context != null) {
			MyApplication application = (MyApplication) context.getApplication();
			if (application.getUserInfo() != null) {
				result = new SlideInPlaybackParam();
				result.mCurUser = new User();
				result.mCurUser.mId = application.getUserInfo().getMemberId();
				if (!TextUtils.isEmpty(application.getUserInfo().getRealName())) {
					result.mCurUser.mName = application.getUserInfo().getRealName();
				} else {
					result.mCurUser.mName = application.getUserInfo().getNickName();
				}
				result.mNotShowSlideBtn = true;
//				result.mIsShareScreen = isShareScreen;
				//隐藏投屏
				result.mIsShareScreen = false;

				//support A4 paper ratio
				if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					result.mRatioScreenWToH = 297.0f / 210.0f;
				} else {
					result.mRatioScreenWToH = 210.0f / 297.0f;
				}
			}
		}
		return result;
	}
	
	public static class OpenCHWParam {
		public Activity activity;
        public String microID;
        public String url;
		public CourseInfo courseInfo;
        public CallbackListener listener;
        public int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//shouyi add
        public Class<? extends PlaybackActivity> destActivity;//shouyi add
		public boolean isShareScreen;
		public PlaybackParam playbackParam;
		
		public OpenCHWParam(Activity activity, String url, CallbackListener listener) {
			this.activity = activity;
			this.url = url;
			this.listener = listener;
		}
	}

	//shouyi add
	public static class CreateSlideParam {
		public boolean mShowSelectSlideType;
		public Activity mActivity;
		public Fragment mFragment;
		public int mEntryType;
		public int mSlideType;
		public String mAttachmentPath;
		public String mTitle;
		public String mContent;
		public boolean mEditable;
		public SlideParam mSlideParam;
		public boolean mIsPickOneImage = true;
		//new engine
		public boolean mIsCreateAndPassResParam;
		public int mMediaType;
		public ArrayList<MediaInfo> mMediaInfos;
		public String mMemberId;
		public SlideSaveBtnParam mSlideSaveBtnParam;
		public String mTaskId; //for student to submit homework
		public int mOrientation;
		//以为三个字段为扫码识任务使用
        public boolean mIsScanTask;
        public String mSchoolId;
        public String mClassId;
		public UserInfo mUserInfo;
		public int courseMode;
		public boolean mIsIntroducationTask;
		//控制来自的类型
		public int fromType = 0;
		//判断是不是来自与学习任务里面的创作
		public boolean isFromStudyTask;

		public String courseSectionDataString; //任务单关联课程章节信息

		public boolean isFromMoocModel;
		public boolean isFromLqBoard;
		public ExerciseAnswerCardParam cardParam;

		public CreateSlideParam() {

		}

		/**
		 * 该构造方法用于打开本机课件
		 *
		 * @param activity
		 * @param fragment
		 * @param path        课件目录
		 * @param title       课件标题
		 * @param description 课件描述
		 * @param screenType  屏幕方向
		 */
		public CreateSlideParam(Activity activity, Fragment fragment, String path, String title,
								String description, int screenType) {
			this.mActivity = activity;
			this.mFragment = fragment;
			this.mAttachmentPath = path;
			this.mTitle = title;
			this.mContent = description;
			int orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			if (screenType == 1) {
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			this.mOrientation = orientation;
			// 设置默认值
			this.mEditable = true;
			this.mEntryType = Common.LIST_TYPE_SHARE;
			this.mSlideParam = CreateSlideHelper.getDefaultSlideParam();
		}

	}
	
	//shouyi add
	public static class SlideSaveBtnParam implements Serializable {
		public boolean mIsShowGiveup = true;
		public boolean mIsShowSave = true;
		public boolean mIsShowSend = true;
		
		public SlideSaveBtnParam(boolean showGiveup, boolean showSave, boolean showSend) {
			mIsShowGiveup = showGiveup;
			mIsShowSave = showSave;
			mIsShowSend = showSend;
		}
	}
}
