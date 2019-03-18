package com.galaxyschool.app.wawaschool.slide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import cn.robotpen.pen.IRemoteRobotService;
import cn.robotpen.pen.model.RemoteState;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.AnswerParsingActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.pojo.CourseSectionData;
import com.galaxyschool.app.wawaschool.pojo.CourseSectionDataListResult;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.module.discovery.ui.SourceCourseListActivity;
import com.lqwawa.mooc.common.MOOCHelper;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.data.NodeOwner;
import com.oosic.apps.iemaker.base.pen.PenServiceActivity;
import com.oosic.apps.iemaker.base.record.Recorder;
import com.osastudio.common.utils.TimerUtils;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.util.List;

/**
 * @author 作者 shouyi
 * @version 创建时间：Mar 31, 2016 3:15:31 PM 类说明
 */
public class SlideWawaPageActivity extends PenServiceActivity implements OnClickListener,
		BaseSlideManager.ExerciseNodeClickListener {

	public final static String LOAD_FILE_PATH = SlideManager.LOAD_FILE_PATH;
	public final static String LOAD_FILE_PAGES = SlideManager.LOAD_FILE_PAGES;
	public final static String TITLE = SlideManager.LOAD_FILE_TITLE;
	public final static String ORIENTATION = "orientation";
	public final static String COUTSE_TYPE = "course_type";
	public final static String TASK_ID = "task_id";
	public final static String IS_SCAN_TASK = "is_scan_task";
	public final static String IS_INTRODUCTION_TASK="is_introducation_task";
	public final static String COURSE_SECTION_DATA_STRING="course_section_data_string";
	public final static String COURSE_FROM_TYPE="course_from_type";
	public final static String MODEL_SOURCE_FROM = "model_source_from";
	public final static String IS_FROM_TEACHER_MARK = "is_from_teacher_mark";
	public final static String EXTRA_EXERCISE_STRING = "exerciseString";
	public final static String EXTRA_EXERCISE_ANSWER_STRING = "exerciseAnswerString";
	public final static String EXTRA_PAGE_INDEX = "pageIndex";
	public final static String EXTRA_EDIT_EXERCISE = "editExercise";
	public final static String EXTRA_EXERCISE_INDEX = "exerciseIndex";

	//扫码识任务需要以下两个字段
	public final static String SCHOOL_ID = "school_id";
	public final static String CLASS_ID = "class_id";
	private SlideManager mSlideManager = null;
	private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	private int mCourseType = MaterialType.AUDIO_BOOK;
	private boolean mIsScanTask;
	private boolean mIsIntroductionTask;
	private String courseSectionDataString;
	private int fromType;
	private int mUserType;
	private boolean isFromMoocModel;
	private ExerciseAnswerCardParam cardParam;
	private boolean isTeacherMark;
	private Handler mHandler = new Handler();

	protected void setUserType(int roleType) {
		mUserType = NodeOwner.toNewUserType(roleType);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mOrientation = getIntent().getIntExtra(ORIENTATION,
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mCourseType = getIntent().getIntExtra(COUTSE_TYPE,
				MaterialType.AUDIO_BOOK);
		mIsScanTask = getIntent().getBooleanExtra(IS_SCAN_TASK, false);
		mIsIntroductionTask = getIntent().getBooleanExtra(IS_INTRODUCTION_TASK,false);
		courseSectionDataString = getIntent().getStringExtra(COURSE_SECTION_DATA_STRING);
		//区分进来的入口
		fromType = getIntent().getIntExtra(SlideWawaPageActivity.COURSE_FROM_TYPE,0);
		isFromMoocModel = getIntent().getBooleanExtra(MODEL_SOURCE_FROM,false);
		cardParam = (ExerciseAnswerCardParam) getIntent().getSerializableExtra(ExerciseAnswerCardParam.class.getSimpleName());
		isTeacherMark = getIntent().getBooleanExtra(IS_FROM_TEACHER_MARK,false);
		int mCurrentOrientation = getResources().getConfiguration().orientation;
		if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				&& mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				&& mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		if (mSlideManager == null) {
			String memberId = ((MyApplication)getApplication()).getMemberId();
			String rootPath = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_LOCAL,
					false);
			if (mCourseType == MaterialType.ONEPAGE_BOOK) {
//				mSlideManager = new SlideManagerHornForPhone(this, null, null,
//						Common.Draft);
//				String rootPath = Common.Draft;
				if(mIsScanTask) {
					String loadPath = getIntent().getStringExtra(LOAD_FILE_PATH);
					if(!TextUtils.isEmpty(loadPath)) {
						rootPath = new File(loadPath).getParent();
					}
				}
				mSlideManager = new SlideManagerHornForPhone(this, null, null,
						rootPath);
			} else {
				mSlideManager = new SlideManager(this, null, null,
						rootPath, mCourseType == MaterialType.AUDIO_BOOK);
			}
		}

		if (fromType == SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE
				|| fromType == SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_OTHER_COURSE
				|| mIsIntroductionTask){
			if (cardParam != null){
				//任务单的批阅
				if (cardParam.isOnlineReporter()){
					setUserType(RoleType.ROLE_TYPE_EDITOR);
				} else if (cardParam.isOnlineHost()){
					setUserType(RoleType.ROLE_TYPE_TEACHER);
				} else if (cardParam.getRoleType() == RoleType.ROLE_TYPE_STUDENT){
					setUserType(RoleType.ROLE_TYPE_STUDENT);
				}
			} else if (isTeacherMark) {
				setUserType(RoleType.ROLE_TYPE_TEACHER);
			} else {
				setUserType(RoleType.ROLE_TYPE_STUDENT);
			}
//			if (!isFromMoocModel) {
//			}
		}

		mSlideManager.setPenUserServiceHelper(getPenUserServiceHelper());
		mSlideManager.setUserType(mUserType);
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
		initTitle();
		mSlideManager.setTitleColor(getResources().getColor(
				R.color.toolbar_bg_color));
		mSlideManager.setClipMusicHandler(mClipMusicHandler);

		//开始对于学习任务制作的时间记录
		if (isNeedTimerRecorder()){
			TimerUtils.getInstance().startTimer();
		}
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
		if (isNeedTimerRecorder()){
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

	private void initTitle() {
		if (mSlideManager != null) {
			if(isAddViewCourse()) {
				mSlideManager.getViewCourseBtn().setOnClickListener(this);
				mSlideManager.getViewCourseBtn().setVisibility(View.VISIBLE);
			}
		}
	}

	private boolean isAddViewCourse() {
		boolean isAdd = false;
		if(!TextUtils.isEmpty(courseSectionDataString)) {
            CourseSectionDataListResult result = JSONObject.parseObject(courseSectionDataString,
                    CourseSectionDataListResult.class);
            if(result != null && result.isSuccess()) {
                if(result.getData() != null && result.getData().size() > 0) {
                    CourseSectionData courseSectionData = result.getData().get(0);
                    if(courseSectionData != null && courseSectionData.getChapList() != null &&
                            courseSectionData.getChapList().size() > 0) {
                        isAdd = true;
                    }
                }
            }
		}
		return isAdd;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.drawable.ecourse_base_back) {
			onBackPressed();
		} else if (v.getId() == R.string.playbackphone_menu_complete) {
			onBackPressed();
//			if(mIsScanTask) {
//				if(mSlideManager instanceof SlideManagerHornForPhone) {
//					((SlideManagerHornForPhone)mSlideManager).showUploadView();
//				}
//			} else {
//				onBackPressed();
//			}
		} else if (v.getId() == mSlideManager.getViewCourseBtn().getId()) {
			if(!TextUtils.isEmpty(courseSectionDataString)) {
				MOOCHelper.init(((MyApplication)MainApplication.getInstance()).getUserInfo());
				SourceCourseListActivity.start(this, courseSectionDataString);
			}
		}
	}

	protected void addMenu(LinearLayout attachedBar, int name, int icon, boolean isPortrait) {
		RelativeLayout layout = new RelativeLayout(this);
		layout.setId(name);
//		TextView textView = new TextView(this);
//		textView.setText(name);
//		try {
//			Drawable drawable = getResources().getDrawable(icon);
//			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//					drawable.getMinimumHeight());
//			textView.setCompoundDrawables(null, drawable, null, null);
//		} catch (NotFoundException e) {
//			// TODO: handle exception
//		}
//		textView.setGravity(Gravity.CENTER);
//		textView.setTextColor(getResources().getColor(R.color.white));
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(icon);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.addView(imageView, layoutParams);
		LayoutParams lParams;
		if(!isPortrait) {
			lParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT);
		} else {
			lParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			lParams.weight = 1.0f;
		}
		lParams.gravity = Gravity.CENTER;
		layout.setOnClickListener(this);
		attachedBar.addView(layout, lParams);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultData) {
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, resultData);
		if (resultCode == RESULT_OK) {
			if (requestCode == SlideManager.REQUEST_PICK_SPACE_MATERAIL) {
				List<ResourceInfo> resourceInfos = resultData
						.getParcelableArrayListExtra("resourseInfoList");
				if (resourceInfos != null && resourceInfos.size() > 0) {
					for (ResourceInfo info : resourceInfos) {
						if (info != null) {
							if (info.getImgPath() != null
									&& !info.getImgPath().startsWith("http")) {
								info.setImgPath(AppSettings.getFileUrl(info
										.getImgPath()));
							}
							if (info.getResourcePath() != null
									&& !info.getResourcePath().startsWith(
											"http")) {
								info.setResourcePath(AppSettings
										.getFileUrl(info.getResourcePath()));
							}
						}
					}
				}
			}else if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH){
				if (resultData != null){
					Intent intent =new Intent();
					String filePath=resultData.getExtras().getString(SlideManagerHornForPhone.SAVE_PATH);
					intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,filePath);
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
			return mSlideManager.dispatchTouchEvent(event, rtn);
		}
		return rtn;
	}

	private Recorder.ClipMusicHandler mClipMusicHandler = new Recorder.ClipMusicHandler() {

		@Override
		public void clipMusic(File src, File dst, long startMs, long endMs) {
			// Shorten.startTrim(src, dst, 0, endMs);
		}
	};

	/**
	 * @return 判断返回记录观看时间的条件
	 */
	private boolean isNeedTimerRecorder(){
		UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
		if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
			if (fromType == SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE || mIsIntroductionTask){
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
		if (cardParam == null){
			return;
		}
		List<ExerciseItem> exerciseItems = cardParam.getQuestionDetails();
		cardParam.setQuestionDetails(exerciseItems);
		AnswerParsingActivity.start(this,this.cardParam,exerciseIndex,true,true);
	}

	protected void reviewExerciseDetails(int exerciseIndex) {
		if (mSlideManager != null) {
			mSlideManager.reviewExerciseDetails(exerciseIndex);
		}
	}

}
