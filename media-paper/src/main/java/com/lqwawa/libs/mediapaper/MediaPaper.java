package com.lqwawa.libs.mediapaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.libs.yilib.pickimages.MediaInfo;
import com.libs.yilib.pickimages.PickMediasActivity;
import com.libs.yilib.pickimages.PickMediasFragment;
import com.libs.yilib.pickimages.PickMediasParam;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.apps.views.MatrixImageView;
import com.lqwawa.apps.views.MatrixViewPager;
import com.lqwawa.apps.views.RayMenu;
import com.lqwawa.libs.mediapaper.KeyboardListener.IOnKeyboardStateChangedListener;
import com.lqwawa.libs.mediapaper.PaperUtils.childViewData;
import com.lqwawa.libs.mediapaper.io.Loader;
import com.lqwawa.libs.mediapaper.io.Saver;
import com.lqwawa.libs.mediapaper.player.CustomizeParams;
import com.lqwawa.libs.videorecorder.SimpleVideoRecorder;
import com.lqwawa.tools.ResourceUtils;
import com.osastudio.common.utils.PhotoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MediaPaper extends FrameLayout implements OnClickListener {
	public final static boolean LOG_OPEN = false; // rmpan false:close true:open
	public final static int CREATE_COURSE_MSG = 50;
	public final static int CAMERA_PATH = 20;
	public final static int REQUEST_CODE_PICK_IMAGE = 21;
	public final static int CREATE_NEW = 1;
	public final static int LOAD_HISTORY = 2;
	public final static int SAVE_EDIT_COMPLETE = 100;
	public static final int PAPER_TYPE_TIEBA = 0;
	public static final int PAPER_TYPE_PREPARATION = 1;

	public static final int TITLE_MAX_LEN = 40;

	final static int MENU_ID_CAMERA = 1;
	final static int MENU_ID_PHOTO = 2;
	final static int MENU_ID_TEXT = 3;
	final static int MENU_ID_RECORD = 4;
	final static int MENU_ID_VIDEO = 5;
	final static int MENU_ID_PERSONAL_SPACE = 6;
	final static int MENU_ID_CLOUD_SPACE = 7;
	private static final boolean mbDragDelete = false;
	KeyboardListener mKeyboardLayout; // added by rmpan for monitor keyboard
	public PaperSaveListener mPaperSaveListener = null;
	private SelectCloudResourceHandler mSelectCloudResourceHandler = null;

	DragLayer mDragLayer;
	private String mPaperPath = null;
	private String mPaperTitle = null;
	private String mFileName = null;
	private int mStartMode = LOAD_HISTORY;
	// private ImageView mTextButton = null;
	// private ImageView mimageButton;
	// private ImageView mvideoButton;
	// private ImageView mrecordButton;
	private ImageView mOtherFocus;
	// private ImageView mCloudResButton;
	private TextView mSaveBarTitle;
	private View     mTitleLay;
	private TextView mTitlePrompt;
	private ContainsEmojiEditText mTitleEdit;
    private TextView mTitleTextView;

	private ImageView mreturnButton;
	private LinearLayout mPaperView;
	private View mEditBar;
	private View mTopBar; //added by rmpan
	private View mAttachedGrp;
	private View mPreviewBar;
	private LinearLayout mPaperDateLinear;
	private MyScrollView mScrollView;

	private PaperManger mPaperManger;
	private Context mContext;

	static OnTouchListener touchListener;
	int point1, startX, startY, point2;
	static OnLongClickListener mLongClickListener;
	static OnClickListener mClickListener;

	private DeleteZone mDeleteZone;
	public boolean mbScrollable = true;
	public Saver mSaver = null;
	private boolean mbBegin = false;

	public static final int MENU_PREVIEW = Menu.FIRST;
	public static final int MENU_PAGEBREAK = MENU_PREVIEW + 1;
	// private boolean waitDoubleClick = false;
	// private static final int DOUBLE_CLICK_TIME = 350;

	public static final String PAPER_EMPTY_OR_NOT = "paper empty or not";
	public final static String PAPER_TITLE = "paper title";

	public final static boolean IMG_FROM_CAM = false;

	static int numEdit = -1;
	private int mInsertPos = -1;

	private boolean mbEditMode = true;

	private int mPaperType;

	int mPaperContentBg = -1;

	// wmadd date and time
	private int mYear;
	private int mMonth;
	private int mDay;
	private static final int DATE_DIALOG_ID = 0;

	private TextView day_text = null;
	private TextView week_text = null;
	private TextView mon_year = null;

	private String[] mMonth_str = null;
	private String[] mWeek_str = null;

	private boolean mbAudioBtnEnable = true;
	private boolean mbVideoBtnEnable = true;
	private String mPaperParentPath = null;
	public ResourceOpenHandler mResourceOpenHandler = null;
	private boolean isEditable;
	private boolean isOnline = false;
	private String mCacheFolder = null;
	private boolean mbTable = false;
	private FeedbackHandler mFeedbackHandler = null;
	private View mSubTitleLine;
	private TextView mSubTitle = null;
	private String mTitleStr,mSubTitleStr;
	private MediasPreviewListener mMediasPreviewListener = null;

	private ImageView mDeleteModeBtn = null;
	private MediaPaperExitHandler mMediaPaperExitHandler = null;

	private View mEditBackBtn = null;
	private View mEditSendBtn = null;

	private View chatBtn;
	private View followBtn;
	private View praiseBtn;
	private View editBtn;
	private View collectBtn;

	private RayMenu rayMenu = null;

	private Handler mHandler = null;
    private CustomizeParams customizeParams = null;//added by rmpan begin
	private View mPaperContent = null;
	private int mPaperMode = PaperUtils.COMMON_MODE;
	private AudioPopwindow audioPopwindow;
	public int getmPaperMode() {
		return mPaperMode;
	}
	public void setmPaperMode(int mode){
		mPaperMode = mode;
	}

	public MediaPaper(Context context) {
		this(context, null);
	}

	public MediaPaper(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MediaPaper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mMonth_str = context.getResources().getStringArray(R.array.month_name);
		mWeek_str = context.getResources().getStringArray(R.array.week_name);
		gd = new GestureDetector(mContext, new MyGestureListener());

		LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.mediapaper, this);

	}

	public void paperInitialize(Context context, Handler handler,
								String paperPath, int startmode,
								SelectCloudResourceHandler selectCloudResourceHandler,
								PaperSaveListener paperSaveListener,
								ResourceOpenHandler openHandler,
								MediaPaperExitHandler mediaPaperExitHandler, CustomizeParams obj) {
        customizeParams = obj;
        this.paperInitialize(context, handler, paperPath, startmode,  selectCloudResourceHandler,
          paperSaveListener, openHandler, mediaPaperExitHandler,customizeParams.getPaperType(),
                customizeParams.isbTableDevice(), customizeParams.isbEdit());
	}
	public void paperInitialize(Context context, Handler handler,
			String paperPath, int startmode,
			SelectCloudResourceHandler selectCloudResourceHandler,
			PaperSaveListener paperSaveListener,
			ResourceOpenHandler openHandler,
			MediaPaperExitHandler mediaPaperExitHandler, int paperType,
			boolean bTableDevice, boolean bEdit) {// VideoView
													// .ResourceOpenHandler
													// openHandler) {
		if (TextUtils.isEmpty(paperPath)) {
			return;
		}
		hideInput();
		// isOnline = false;
		setOnline(false);
		mHandler = handler;
		mSelectCloudResourceHandler = selectCloudResourceHandler;
		mPaperSaveListener = paperSaveListener;
		mResourceOpenHandler = openHandler;
		mMediaPaperExitHandler = mediaPaperExitHandler;
		mPaperType = paperType;

		if (!paperPath.endsWith(File.separator)) {
			paperPath = paperPath + File.separator;
		}
		mbBegin = true;
		mContext = context;
		mStartMode = startmode;
		mPaperPath = paperPath;
		mPaperParentPath = new File(paperPath).getParent();
		mPaperTitle = new File(paperPath).getName();

		setupViews(bTableDevice);

		// View feedbackGrp = findViewById(R.id.feedback_grp);
		// if (feedbackGrp != null) {
		// feedbackGrp.setVisibility(View.GONE);
		// }

		clearPaper();

		if (mStartMode == MediaPaper.LOAD_HISTORY && mPaperManger != null) {
			setEditMode(bEdit);

			LoadTask loadTask = new LoadTask();
			loadTask.execute();

		}

		if (mStartMode == MediaPaper.CREATE_NEW) {
			mbEditMode = false;
			setEditMode(true);
			File distFile = new File(mPaperPath);
			if (distFile.exists()) {
				LoadTask loadTask = new LoadTask();
				loadTask.execute();
				// LoadRunnable runable = new LoadRunnable();
				// mHandler.post(runable);
			} else {
				PaperUtils.CreateLocalDiskPath(mPaperParentPath);
				PaperUtils.CreateLocalFile(new File(mPaperParentPath,
						".nomedia"));
				PaperUtils.createNew(mContext, mPaperPath);
				View v = findViewById(R.id.pgr);
				if (v != null) {
					v.setVisibility(View.GONE);
				}
			}
		}
		switchCustomizeTitle();

		mSaver = new Saver(mContext, mPaperPath);

		mbBegin = false;
	}

	public boolean onBackPressed() {
		if (stopVideoPlay()) {
			return true;
		} else if (stopMediasPreview()) {
			return true;
		} else if (mPaperManger.isDeleteMode()) {
			setDeleteMode(false);
			return true;
		} else if (mbEditMode) {
			if (mMediaPaperExitHandler != null) {
				mMediaPaperExitHandler.back(mPaperManger.isEdit());
			}
			return true;
		}
		// mPaperManger.releaseShowThumbnail();
		mPaperManger.stopAll();

		return false;
	}

	private void setupViews(boolean bTableDevice) {
		mbTable = bTableDevice;
		if (mPaperContentBg > 0) {
			setpaperBackground(mPaperContentBg);
		}
		mKeyboardLayout = (KeyboardListener) findViewById(R.id.keyboardRelativeLayout);
		mDragLayer = (DragLayer) findViewById(R.id.dragLayer);
		mDeleteZone = (DeleteZone) findViewById(R.id.delete_zone);

		mOtherFocus = (ImageView) findViewById(R.id.otherFocus);

		mreturnButton = (ImageView) findViewById(R.id.returnbtn);
		mPaperView = (LinearLayout) findViewById(R.id.paperView);
		mDeleteModeBtn = (ImageView) findViewById(R.id.delete_mode_btn);
		mEditBackBtn = findViewById(R.id.edit_returnbtn);
		mEditSendBtn = findViewById(R.id.edit_send);
		mPaperContent = findViewById(R.id.paper_content);

		if (mPaperView != null) {
			int hPadding = (int) (mContext.getResources()
					.getDimension(R.dimen.paper_padding_default));
			int bottomPadding = (int) (mContext.getResources()
					.getDimension(R.dimen.paper_padding_bottom));
			if (mbTable) {
				hPadding = (int) (mContext.getResources()
						.getDimension(R.dimen.paper_padding_table));
			}
			mPaperView.setPadding(hPadding, 0, hPadding, 0);
			View dateTitlebar = findViewById(R.id.date_title_bar);
			if (dateTitlebar != null) {
				dateTitlebar.setPadding(hPadding, 0, hPadding, 0);
			}
			View subTitleGrp = findViewById(R.id.subtitle_grp);
			if (subTitleGrp != null) {
				subTitleGrp.setPadding(hPadding, 0, 0, 0);
			}

		}
		mTopBar = findViewById(R.id.save_bar);
		mEditBar = findViewById(R.id.edit_top);// (R.id.save_bar);
		mPreviewBar = findViewById(R.id.preview_top);
		mPaperDateLinear = (LinearLayout) findViewById(R.id.paper_date_linear);
		mPaperDateLinear.setVisibility(View.GONE);

		mScrollView = (MyScrollView) findViewById(R.id.scrollview);

		mTitleLay = (View)findViewById(R.id.diary_title_lay);
		mTitlePrompt = (TextView)findViewById(R.id.diary_title_prompt);

		mTitleTextView = (TextView) findViewById(R.id.diary_title_text_view_id);
		mTitleEdit = (ContainsEmojiEditText) findViewById(R.id.diary_title);
		mTitleEdit.setMaxlen(TITLE_MAX_LEN);
		mTitleEdit.setSingleLine();

		mSaveBarTitle = (TextView)findViewById(R.id.save_bar_center_title);

		/**
		 * don't display hint before get title from server
		 */
		if(isOnline) {
			mTitleEdit.setText(" ");
		}
		 
		mKeyboardLayout
				.setOnKeyboardStateChangedListener(new IOnKeyboardStateChangedListener() {

					public void onKeyboardStateChanged(int state) {
						if (mbEditMode) {
							switch (state) {
							case KeyboardListener.KEYBOARD_STATE_HIDE:// 软键盘隐藏
								hideInput();
								showOrHideMenu(true);
								break;

							case KeyboardListener.KEYBOARD_STATE_SHOW:// 软键盘显示
								showOrHideMenu(false);
								break;

							default:
								break;
							}
						}
					}
				});
		mTitleEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mPaperManger != null) {
					mPaperManger.setEdit(true);
					mTitleStr = s.toString();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mSubTitleLine = (View) findViewById(R.id.sub_title_letf_line);
		mSubTitle = (TextView) findViewById(R.id.sub_title);
		if(PaperUtils.COMMON_MODE == mPaperMode) {
			if (!TextUtils.isEmpty(mSubTitleStr)) {
				mSubTitle.setText(mSubTitleStr);
			}
		}else if(PaperUtils.HOMEWORK_MODE == mPaperMode){
			mSubTitle.setText(getResources().getString(R.string.paper_homework_content));
		}
		mreturnButton.setOnClickListener(this);
		mPaperView.setOnClickListener(this);
		mEditBackBtn.setOnClickListener(this);
		mEditSendBtn.setOnClickListener(this);

		mPaperManger = new PaperManger(mContext, this, mPaperView, mPaperPath,
				bTableDevice);
		mPaperManger.setmMediaPaper(this);

		mDeleteModeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rayMenu != null) {
					rayMenu.close();
				}
				if (mPaperManger != null) {
					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					boolean bDeleteMode = !mPaperManger.isDeleteMode();
					setDeleteMode(bDeleteMode);
				}
			}
		});

		if (mbDragDelete) {
			mDeleteZone.setDragController(mDragLayer);

			mDragLayer.setDragListener(mDeleteZone);
		}
		mDragLayer.setMediaPaper(this);
		mDragLayer.setPaperManger(mPaperManger);
		mPaperManger.setmScrollView(mScrollView);

		mLongClickListener = new OnLongClickListener() {
			public boolean onLongClick(View v) {
				if (!mbEditMode) {
					return false;
				}

				mDragLayer
						.startDrag(v, null, null, PaperUtils.DRAG_ACTION_VIEW);
				return true;
			}
		};

		mClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.equals(mPaperDateLinear)) {
					if (!mbEditMode) {
						return;
					}
					return;
				} else if (v instanceof EditText) {
					v.setFocusableInTouchMode(true);

					v.setFocusable(true);
					v.requestFocus();
				} else if (v instanceof MediaFrame) {
					startMediasPreview(((MediaFrame) v), v.getId());
				}
			}

		};

		mPaperDateLinear.setOnClickListener(mClickListener);

		if (!isOnline && (mStartMode == MediaPaper.CREATE_NEW || isEditMode())) {
			addRayMenu();
		}
	}

	public void switchCustomizeTitle(){
		try {
			if (PaperUtils.COMMON_MODE == mPaperMode) {
				mTitlePrompt.setVisibility(View.GONE);
				mTitleEdit.setBackgroundResource(R.drawable.title_bg);
				mTitleEdit.setGravity(Gravity.CENTER);
				mTitleEdit.setTextAppearance(mContext,R.style.paper_title);
				setmSubTitle(true);
				mSubTitleLine.setVisibility(View.VISIBLE);
			} else {
				mTitlePrompt.setVisibility(View.VISIBLE);
	//			if (mbEditMode) {
	//				mTitleEdit.setBackgroundResource(R.drawable.edit_bg_shape);
	//			} else {
	//				mTitleEdit.setBackgroundColor(android.R.color.transparent);
	//			}
				setmSubTitle(false);
				mSubTitleLine.setVisibility(View.GONE);

				if (isEditMode()) {
					mTitleEdit.setTextAppearance(mContext,R.style.paper_sub_title);
					mTitleEdit.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
					mTitleEdit.setBackgroundColor(android.R.color.transparent);
					mTitleEdit.setFocusable(true);
					mTitleEdit.setSelection(mTitleEdit.getText().length());//调整光标到最后一行
					mTitleEdit.setVisibility(View.VISIBLE);
					mTitleTextView.setVisibility(View.GONE);
				} else {
					mTitleEdit.setVisibility(View.GONE);
					mTitleTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
					mTitleTextView.setVisibility(View.VISIBLE);
					mTitleTextView.setText(mTitleStr);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setTopBarTitle(String str){
		if(null != str){
			if(str.length()>0){
				mSaveBarTitle.setVisibility(View.VISIBLE);
				mSaveBarTitle.setText(str);
			}else{
				mSaveBarTitle.setVisibility(View.GONE);
				mSaveBarTitle.setText("");
			}
		}
	}

	private void setmSubTitle(boolean isNewPaper){
		if(isNewPaper){
			//mSubTitle.setTextSize(12);
			//mSubTitle.setTextColor(Color.rgb(0x99, 0x99, 0x99));
			mSubTitle.setTextAppearance(mContext, R.style.paper_sub_small_title);
		}else{
			//mSubTitle.setTextSize(18);
			//mSubTitle.setTextColor(Color.rgb(0x66, 0x66, 0x66));
			mSubTitle.setTextAppearance(mContext, R.style.paper_sub_title);
		}
	}

	private void hideRayMenu() {
		if (rayMenu != null) {
			rayMenu.hide();
		}
	}

	private void addRayMenu() {
		FrameLayout layout = (FrameLayout) findViewById(R.id.root_layout);
		if (layout == null) {
			return;
		}
		if (rayMenu != null) {
			// rayMenu.show();
			rayMenu.showAllMenu();
			return;
		}
		rayMenu = new RayMenu(mContext, layout);
		rayMenu.setAutoCloseAfterClick(true);

		RayMenu.MenuItem menuItem = new RayMenu.MenuItem();
		menuItem.id = MENU_ID_CAMERA;
		menuItem.icon = R.drawable.composer_camera;
		rayMenu.addItem(RayMenu.MenuType.VERTICAL, menuItem);

		menuItem = new RayMenu.MenuItem();
		menuItem.id = MENU_ID_PHOTO;
		menuItem.icon = R.drawable.composer_photo;
		rayMenu.addItem(RayMenu.MenuType.VERTICAL, menuItem);

		menuItem = new RayMenu.MenuItem();
		menuItem.id = MENU_ID_RECORD;
		menuItem.icon = R.drawable.composer_music;
		rayMenu.addItem(RayMenu.MenuType.VERTICAL, menuItem);

		menuItem = new RayMenu.MenuItem();
		menuItem.id = MENU_ID_VIDEO;
		menuItem.icon = R.drawable.composer_video;
		rayMenu.addItem(RayMenu.MenuType.VERTICAL, menuItem);

		menuItem = new RayMenu.MenuItem();
		menuItem.id = MENU_ID_TEXT;
		menuItem.icon = R.drawable.composer_text;
		rayMenu.addItem(RayMenu.MenuType.VERTICAL, menuItem);
        if(null == customizeParams || customizeParams.isSupportCreateMaterialType(PaperUtils.SUPPORT_PERSONAL_MATERIAL_TYPE)) {
            menuItem = new RayMenu.MenuItem();
            menuItem.id = MENU_ID_PERSONAL_SPACE;
            menuItem.icon = R.drawable.composer_personal_space;
            rayMenu.addItem(RayMenu.MenuType.HORIZONTAL, menuItem);
        }

		if (mPaperType == PAPER_TYPE_PREPARATION) {
			//rmpan提供給第三方時放開
//			menuItem = new RayMenu.MenuItem();
//			menuItem.id = MENU_ID_PERSONAL_SPACE;
//			menuItem.icon = R.drawable.composer_personal_space;
//			rayMenu.addItem(RayMenu.MenuType.HORIZONTAL, menuItem);

			menuItem = new RayMenu.MenuItem();
			menuItem.id = MENU_ID_CLOUD_SPACE;
			menuItem.icon = R.drawable.composer_cloud_space;
			rayMenu.addItem(RayMenu.MenuType.HORIZONTAL, menuItem);
		}

		menuItem = new RayMenu.MenuItem();
		menuItem.id = 0;
		menuItem.icon = R.drawable.composer_main;
		rayMenu.addRootItem(menuItem);
		rayMenu.setMenuItemClickListener(new RayMenu.MenuItemClickListener() {
			@Override
			public void onMenuItemClick(RayMenu.MenuItem menuItem) {
				PaperUtils.outLog("", "onMenuItemClick " + menuItem.id);
				// /////////////////////////
				int havefree;
				switch (menuItem.id) {
				case 0:
					setDeleteMode(false);

					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					break;
				case MENU_ID_CAMERA:
					resetInertPos();
					if (mPaperManger.getCamImageCount() >= PaperUtils.IMAGECOUNTLIMIT) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.reach_max));
						return;
					}
					havefree = PaperUtils.haveSpace();
					if (havefree == 0) {
						cameraImageView();
					} else if (havefree > 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_full));
					} else if (havefree < 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_mount));
					}
					break;
				case MENU_ID_PHOTO:
					resetInertPos();
					if (mPaperManger.getCamImageCount() >= PaperUtils.IMAGECOUNTLIMIT) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.reach_max));
						return;
					}
					havefree = PaperUtils.haveSpace();
					if (havefree == 0) {
						showLocalImageDialog();
					} else if (havefree > 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_full));
					} else if (havefree < 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_mount));
					}

					break;
				case MENU_ID_TEXT:
					resetInertPos();
					mPaperManger.createNewEditText(mInsertPos);
					break;
				case MENU_ID_RECORD:
					resetInertPos();
					if (mPaperManger.getCamImageCount() >= PaperUtils.IMAGECOUNTLIMIT) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.reach_max));
						return;
					}
					havefree = PaperUtils.haveSpace();
					if (havefree == 0) {
						if (mbAudioBtnEnable) {
							myrecordView();
						}
					} else if (havefree > 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_full));
					} else if (havefree < 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_mount));
					}
					break;
				case MENU_ID_VIDEO:
					resetInertPos();
					if (mPaperManger.getCamImageCount() >= PaperUtils.IMAGECOUNTLIMIT) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.reach_max));
						return;
					}
					havefree = PaperUtils.haveSpace();
					if (havefree == 0) {
						if (mbVideoBtnEnable) {
							mPaperManger.stopAll();
							cameraVideo();
						}
					} else if (havefree > 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_full));
					} else if (havefree < 0) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.sdcard_mount));
					}
					break;
				case MENU_ID_PERSONAL_SPACE:
					resetInertPos();
					if (mPaperManger.getCamImageCount() >= PaperUtils.IMAGECOUNTLIMIT) {
						PaperUtils.showMessage(mContext,
								mContext.getString(R.string.reach_max));
						return;
					}
					if (mSelectCloudResourceHandler != null) {
						mSelectCloudResourceHandler
								.selectPersonlCloudResource(mHandler);
					}
					break;
				case MENU_ID_CLOUD_SPACE:
					resetInertPos();
					if (mSelectCloudResourceHandler != null) {
						mSelectCloudResourceHandler
								.selectCloudResource(mHandler);
					}
					break;
				}

				// //////////////////////////
			}
		});
		rayMenu.build();
		// showOrHideMenu(true);
		rayMenu.open();
	}

	private void showOrHideMenu(boolean show) {// added by rmpan for open or
												// close all menu
		if (null != rayMenu) {
			if (show) {
				//mKeyboardLayout.setVisibility(View.VISIBLE);
				mDeleteModeBtn.setVisibility(View.VISIBLE);
				if(!rayMenu.isMenuOpened()) {
					rayMenu.showOrHideRootItem(true);
					rayMenu.open();
				}
			} else {
				//mKeyboardLayout.setVisibility(View.INVISIBLE);
				mDeleteModeBtn.setVisibility(View.INVISIBLE);
				if (rayMenu.isMenuOpened()) {
					rayMenu.close();
					rayMenu.showOrHideRootItem(false);
				}
				rayMenu.show();
			}
		}
	}

	public void sketchSetScrollViewScrollableOrNot(boolean bScrollable,
			boolean bSketchView) {
		mbScrollable = bScrollable;
		// mScrollView.setMyScrollViewScrollableOrNot(mbScrollable);
	}

	public String getPathName() {
		return mPaperPath;
	}

	@Override
	public void onClick(View v) {

		int havefree;
		if (v.getId() == R.id.returnbtn) {
			boolean rtn = onBackPressed();
			if (!rtn) {
				((Activity) mContext).finish();
			}

		} else if (v.getId() == R.id.edit_returnbtn) {
			InputMethodManager imm = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			setDeleteMode(false);
			if (mMediaPaperExitHandler != null) {
				mMediaPaperExitHandler.back(mPaperManger.isEdit());
			}
		} else if (v.getId() == R.id.edit_send) {
			InputMethodManager imm = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			setDeleteMode(false);

			mTitleStr = mTitleEdit.getEditableText().toString();
			if (TextUtils.isEmpty(mTitleStr)) {
				Toast.makeText(mContext, R.string.no_title, Toast.LENGTH_LONG)
						.show();
			} else if (mMediaPaperExitHandler != null) {
				mMediaPaperExitHandler.backAndSend(mPaperManger.isEdit());
			}
		}
		//

	}

	public void setmInertPos(int pos) {
		this.mInsertPos = pos;
	}

	public void resetInertPos() {
		this.mInsertPos = -1;
	}

	public void addViewByNameForDrag(childViewData data) {
		if (data.mViewName.equals(PaperUtils.EDITVIEW)) {
			mPaperManger.addView(mPaperManger.createNewView(
					PaperUtils.EDITVIEW, null, -1, -1, -1, mInsertPos),
					PaperUtils.AddViewFirst, mInsertPos);
		} else if (data.mViewName.equals(PaperUtils.SKETCHPAD)) {
			mPaperManger.addView(mPaperManger.createNewView(
					PaperUtils.SKETCHPAD, null, -1, -1, -1, mInsertPos),
					PaperUtils.AddViewFirst, mInsertPos);
		} else if (data.mViewName.equals(PaperUtils.IMAGEVIEW)) {
			showLocalImageDialog();
		} else if (data.mViewName.equals(PaperUtils.VIDEOVIEW)) {
			showLocalAudioDialog();
		} else if (data.mViewName.equals(PaperUtils.RECORDVIEW)) {
			// mFormat = FileExplorer.RECORD_FILES;
			// openDialog(R.string.frommic);
			showLocalAudioDialog();
		} else if (data.mViewName.equals(PaperUtils.COURSEVIEW)) {
			if (mSelectCloudResourceHandler != null) {
				mSelectCloudResourceHandler.selectCloudResource(mHandler);
			}
		}
	}

	void setDialogPosition(Dialog dialog, int x, int y) {
		Window mWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.x = x;
		lp.y = y;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lp.dimAmount = 0.0f;
	}

	// protected void cameraVideoView() {
	// Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	//
	// ((Activity) mContext).startActivityForResult(intent, CAMERA_PATH);
	//
	// }

	// Use my record
	protected void myrecordView() {
		String path = mPaperPath + PaperUtils.SUB_RECORD + File.separator;
		// mPaperManger.addView(mPaperManger.createNewView(PaperUtils.RECORDVIEW,
		// path, -1, -1, -1, mInsertPos), PaperUtils.AddViewLoad, mInsertPos);

//		RecordDialog dialog = new RecordDialog(mContext, getWidth() * 3 / 4,
//				path, mRecordFinishListener);
//		dialog.show();
//备注：2017/8/15 任宝 修改了录音控件

		audioPopwindow = new AudioPopwindow((Activity) mContext,path, new
				AudioPopwindow.OnUploadListener() {
					@Override
					public void onUpload(String path) {
						if (TextUtils.isEmpty(path)) {
							return;
						}
						mPaperManger.addView(mPaperManger.createNewView(
								PaperUtils.RECORDVIEW, path, -1, -1, -1, mInsertPos),
								PaperUtils.AddViewFirst, mInsertPos);
					}
				},true);
		if(audioPopwindow!=null){
			audioPopwindow.setAnimationStyle(R.style.Mp_AnimBottom);
			audioPopwindow.showPopupMenu();
		}
	}

	AlertDialog.Builder dlg = null;

	protected DialogInterface.OnCancelListener dlgCancelListener = new DialogInterface.OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			dlg = null;
			mPaperManger.mergeEditTextForTollbarDrag(mInsertPos);
		}
	};

//	private RecordDialog.RecordFinishListener mRecordFinishListener = new RecordDialog.RecordFinishListener() {
//		@Override
//		public void onRecordFinish(String path) {
//			mPaperManger.addView(mPaperManger.createNewView(
//					PaperUtils.RECORDVIEW, path, -1, -1, -1, mInsertPos),
//					PaperUtils.AddViewFirst, mInsertPos);
//		}
//	};

	public void backToPaper(int requestCode, int resultCode, Intent resultData) {
		// super.onActivityResult(requestCode, resultCode, resultData);
		dlg = null;
		if (resultCode == Activity.DEFAULT_KEYS_DISABLE) {
			return;
		}

		if (resultCode != Activity.RESULT_OK) {
			mPaperManger.mergeEditTextForTollbarDrag(mInsertPos);
			return;
		}
		if (requestCode == CAMERA_PATH) {
			mPaperManger.stopAll();

			String temp = mFileName;
			if (resultData != null && resultData.getData()!=null) {

				temp = PhotoUtils.getImageAbsolutePath(((Activity) mContext), resultData.getData());

			}
			if (temp != null) {
				mPaperManger.setFromCam(true);
				if (new File(temp).exists()) {
					BitmapFactory.Options options = PaperUtils
							.loadBitmapOptions(temp);
					if (options == null
							|| options.outWidth > PaperUtils.IMAGE_LONG_SIZE
							|| options.outHeight > PaperUtils.IMAGE_LONG_SIZE) {
						String newCache = writeImageToCache(temp);
						if (newCache != null && new File(newCache).exists()) {
							new File(temp).delete();
							temp = newCache;
						}
					} else if (options != null) {

						String ext = PaperUtils.getFileExt(temp);
						String newCacheName = String.format("%d_%dx%d%s",
								System.currentTimeMillis(), options.outWidth,
								options.outHeight, ext);
						// String.valueOf(time) +
						// "_"+options.outWidth+"x"+options.outHeight+ext;
						String newCachePath = getPathName()
								+ PaperUtils.SUB_IMAGE + File.separator
								+ newCacheName;
						new File(temp).renameTo(new File(newCachePath));
						temp = newCachePath;
					}
				}

				mPaperManger.addView(mPaperManger.createNewView(
						PaperUtils.IMAGEVIEW, temp, -1, -1, -1, mInsertPos),
						PaperUtils.AddViewFirst, mInsertPos);
			}
		} else if (requestCode == SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO) {
			if (resultData == null) {
				return;
			}
			String filePath = resultData
					.getStringExtra(SimpleVideoRecorder.EXTRA_VIDEO_PATH);
			if (TextUtils.isEmpty(filePath)) {
				return;
			}
			File file = new File(filePath);
			if (file.exists()) {
				GetVideoThumbTask task = new GetVideoThumbTask(file.getPath());
				task.execute();
			}
		} else if (requestCode == REQUEST_CODE_PICK_IMAGE) {
			if (resultData == null) {
				return;
			}
			ArrayList<MediaInfo> imageInfos = resultData
					.getParcelableArrayListExtra(PickMediasFragment.PICK_IMG_RESULT);
			if (imageInfos != null && imageInfos.size() > 0) {
				int insertPos = mInsertPos;
				for (int i = 0; i < imageInfos.size(); i++) {
					if (insertPos >= 0) {
						insertPos += i;
					}
					String imagePath = writeImageToCache(imageInfos.get(i).mPath);
					if (imagePath == null) {
						imagePath = imageInfos.get(i).mPath;
					}
					mPaperManger.addView(mPaperManger.createNewView(
							PaperUtils.IMAGEVIEW, imagePath, -1, -1, -1,
							insertPos), PaperUtils.AddViewFirst, insertPos);
				}

			}
		}

	}

	public int getInsertPos() {
		return mInsertPos;
	}

	public void addCloudResouce(String viewName, String thumbnail,
			String title, String resourceUrl, String sharePlayUrl,
			int childOrientation, int insertPos) {
		if (viewName.equals(PaperUtils.IMAGEVIEW)) {
			mPaperManger.addView(mPaperManger.createNewView(
					PaperUtils.IMAGEVIEW, thumbnail, -1, -1, -1, insertPos),
					PaperUtils.AddViewFirst, insertPos);
		} else {
			mPaperManger.addView(mPaperManger.createNewView(viewName,
					resourceUrl, thumbnail, title, sharePlayUrl,
					childOrientation, -1, -1, -1, insertPos),
					PaperUtils.AddViewFirst, insertPos);
		}
	}

	private ProgressDialog mProgressDialog = null;

	private synchronized void closeProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	int getToolBarHeight() {

		return mScrollView.getTop() + ((View) mScrollView.getParent()).getTop()
				+ mPaperView.getTop();
	}

	int getScrollViewBottom() {
		return mScrollView.getBottom();
	}

	View getTitleView() {
		return mTitleEdit;
	}

	int getTitleHeight() {
		return mTitleEdit.getHeight();
	}

	void setOtherFocus() {
		mOtherFocus.setFocusableInTouchMode(true);
		mOtherFocus.setFocusable(true);
		mOtherFocus.requestFocus();
	}

	private class LoadTask extends
			AsyncTask<Void, Void, ArrayList<childViewData>> {
		Loader mLoader = null;

		@Override
		protected ArrayList<childViewData> doInBackground(Void... voids) {
			mLoader = new Loader(mContext, mPaperManger);
			if (isOnline) {
				mLoader.onlinePaperSetCacheFolder(mCacheFolder);
			}
			ArrayList<childViewData> loadData = mLoader
					.loadDataFromIndexFile(mPaperPath);

			return loadData;
		}

		@Override
		protected void onPostExecute(ArrayList<childViewData> childViewDatas) {
			boolean rtn = mLoader.restoreChildFromChildDatas(
					mLoader.getUserTitle(), childViewDatas);
			if (rtn) {
				setOtherFocus();
				mPaperManger.handleScroll(PaperUtils.SCROLL_TOP);
				mPaperManger.setEdit(false);
			}
			View v = findViewById(R.id.pgr);
			if (v != null) {
				v.setVisibility(View.GONE);
			}
			super.onPostExecute(childViewDatas);
		}
	}

	private class ExitEditRunnable extends AsyncTask<Void, Void, Void> {
		boolean bKillProcess;

		ExitEditRunnable(boolean isKillProcess) {
			bKillProcess = isKillProcess;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				while (!mPaperManger.isCacheFinish()) {

					Thread.sleep(200);
				}
			} catch (InterruptedException e) {

			}

			if (mPaperManger.isEdit()) {
				String fileName = PaperUtils
						.getFileNameFromPaperPath(mPaperPath);
				savePaperAndPreview(fileName);
				if (mPaperSaveListener != null) {
					mPaperSaveListener.onSaveFinish(mPaperPath, mTitleStr);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			closeProgressDialog();
			if (bKillProcess) {
				boolean rtn = onBackPressed();
				if (!rtn) {
					// ((Activity) mContext).finish();
					mHandler.sendEmptyMessage(SAVE_EDIT_COMPLETE);
				}
			} else {
				mEditBar.setVisibility(View.GONE);
				mPreviewBar.setVisibility(View.VISIBLE);
				// ImageView editModeBtn = (ImageView)
				// findViewById(R.id.editmodebtn);
				// if (editModeBtn != null) {
				// editModeBtn.setImageResource(R.drawable.edit_bt);
				// }
				mPaperManger.editTextShowBackground();
			}
		}
	}

	private boolean savePaperAndPreview(String fileName) {
		boolean bEmpty = false;
		LinkedList<childViewData> mLoader_data = mPaperManger.getPaperItems();
		mSaver.save(mTitleStr, mLoader_data);
		return bEmpty;
	}

	public void setEditMode() {
		setEditMode(!mbEditMode);
	}

	public void setViewMode(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public void saveEdit() {
		setEditMode(false);
	}

	public void closeAndDeletePaper() {
		clearPaper();

		hideRayMenu();
		mEditBar.setVisibility(View.GONE);
		mPreviewBar.setVisibility(View.VISIBLE);
		mDeleteModeBtn.setVisibility(View.GONE);

		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		// mPaperManger.setDeleteMode(false);
		setDeleteMode(false);

		String paperFolderPath = getPathName();
		if (paperFolderPath != null && new File(paperFolderPath).exists()) {
			PaperUtils.deleteDirectory(paperFolderPath);
		}
		mHandler.sendEmptyMessage(SAVE_EDIT_COMPLETE);
	}

	public void setEditMode(boolean bEdit) {
		mPaperManger.stopAll();

		if (mbEditMode != bEdit) {
			mbEditMode = bEdit;
			mPaperManger.EnableOrDisableAllChild(mbEditMode);
			if (mbEditMode) {
				mEditBar.setVisibility(View.VISIBLE);
				mPreviewBar.setVisibility(View.GONE);
				mPaperManger.editTextHideBackground();
				mDeleteModeBtn.setVisibility(View.VISIBLE);
				addRayMenu();
			} else {

				if (!mbBegin) {
					mTitleStr = mTitleEdit.getEditableText().toString();
					if (TextUtils.isEmpty(mTitleStr)) {
						Toast.makeText(mContext, R.string.no_title,
								Toast.LENGTH_LONG).show();
						mbEditMode = true;
						mPaperManger.EnableOrDisableAllChild(mbEditMode);
						return;
					}
					mPaperManger.EnableOrDisableAllChild(mbEditMode);
					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					// mPaperManger.setDeleteMode(false);
					setDeleteMode(false);
					int havefree = PaperUtils.haveSpace();
					if(havefree > 0){
						((Activity)mContext).runOnUiThread(
								new Runnable() {
									@Override
									public void run() {
										PaperUtils.showMessage(mContext, mContext.getString(R.string.sdcard_full));
									}
								}
						);
					} else if(havefree < 0) {
						((Activity)mContext).runOnUiThread(
								new Runnable() {
									@Override
									public void run() {
										PaperUtils.showMessage(mContext, mContext.getString(R.string.sdcard_full));
									}
								}
						);
					}else{
						if (mProgressDialog == null) {
							mProgressDialog = ProgressDialog.show(mContext, "",
									mContext.getText(R.string.save), true, false);
						}
						mTitleStr = mTitleEdit.getEditableText().toString();
						ExitEditRunnable runable = new ExitEditRunnable(true);
						runable.execute();
					}
				} else {
					// mPaperManger.editTextShowBackground();
				}

				hideRayMenu();
				mEditBar.setVisibility(View.GONE);
				mPreviewBar.setVisibility(View.VISIBLE);
				mDeleteModeBtn.setVisibility(View.GONE);
//				adjustScroolViewPosition(mPaperContent,0,0,0,0);

			}
		}

	}

	/**
	 * 强制隐藏输入法键盘
	 */
	private void hideInput() {// added by rmpan hide soft keyboard
		setDeleteMode(false);
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public boolean isEditMode() {
		return mbEditMode;
	}

	public String getPaperTitle() {
		return mPaperTitle;
	}

	public void fillUserTitle(String userTitle) {
		mTitleEdit.mySetText(userTitle);
		mTitleTextView.setText(mTitleStr);
		//mTitleEdit.setSelection(mTitleEdit.getText().length());
	}



	private void updateDisplay() {
		int year = 0;

		if (mYear > 1900) {
			year = mYear - 1900;
		}
		String s_date = PaperUtils.getDate(mDay, mMonth + 1, mYear);
		String title = PaperUtils.getNewDiaryTitleInDate(mContext, s_date);
		if (title == null) {
			return;
		}
		Date date = new Date(year, mMonth, mDay);
		int month = date.getMonth(); // 0~11
		int day = date.getDate();
		int weekday = date.getDay();
		mon_year.setText(mMonth_str[month] + " " + mYear);
		day_text.setText(String.valueOf(day));
		week_text.setText(mWeek_str[weekday]);

		mPaperTitle = title;
		String pathname = mPaperParentPath + File.separator + title
				+ File.separator;
		File file = new File(mPaperPath);
		String oldPath = mPaperPath;
		mPaperPath = pathname;
		mPaperManger.updateForPathNameChange(oldPath, pathname);
		File tempF = new File(mPaperPath);
		if (tempF.exists()) {
			PaperUtils.deleteDirectory(mPaperPath);
			// tempF.delete();
		}
		file.renameTo(new File(mPaperPath));
		mSaver.UpdatePath(mPaperPath);
	}

	public void setpaperBackground(int resid) {
		mPaperContentBg = resid;
		if (resid > 0) {
			View v = findViewById(R.id.paper_content);
			if (v != null) {
				v.setBackgroundResource(resid);
			}
		}
	}

	private void showLocalImageDialog() {
		// showImportDialog(ImportDialog.FILE_FILTER_TYPE_IMAGE);
		PickMediasParam param = new PickMediasParam();
		param.mColumns = 4;
		param.mConfirmBtnName = mContext.getString(ResourceUtils.getStringId(mContext, "confirm"));
		// param.mDefaultImage = R.drawable.btn_camera;
		param.mIsActivityCalled = true;
		param.mLimitReachedTips = mContext.getString(ResourceUtils.getStringId(mContext, "media_select_full_msg"));
		param.mPickLimitCount = 9;
		param.mSearchPath = "/mnt";
		param.mShowCountFormatString = mContext.getString(ResourceUtils.getStringId(mContext, "media_show_count_msg"));
		param.mShowCountMode = 1;
		Intent intent = new Intent(mContext, PickMediasActivity.class);
		intent.putExtra(PickMediasFragment.PICK_IMG_PARAM, param);
		if(mbTable){//added by rmpan
			intent.putExtra(PickMediasActivity.PICKMEDIA_ORIENTATION, Configuration.ORIENTATION_LANDSCAPE);
		}else{
			intent.putExtra(PickMediasActivity.PICKMEDIA_ORIENTATION, Configuration.ORIENTATION_PORTRAIT);
		}
		((Activity) mContext).startActivityForResult(intent,
				REQUEST_CODE_PICK_IMAGE);
	}

	private void showLocalAudioDialog() {
		showImportDialog(ImportDialog.FILE_FILTER_TYPE_AUIDO);
	}

	private void showLocalVideoDialog() {
		showImportDialog(ImportDialog.FILE_FILTER_TYPE_VIDEO);
	}

	private void setDialogPosition(Dialog dialog, int x, int y, int gravity) {
		Window mWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.x = x;
		lp.y = y;
		lp.gravity = gravity;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lp.dimAmount = 0.0f;
	}

	private void cameraImageView() {
		String folder = mPaperPath + PaperUtils.SUB_IMAGE ;// Utils.ROOT_PATH
		File file = new File(folder);
		if (!file.exists()) {
			file.mkdirs();
		}
		String fileName = "";
		long dateTaken = System.currentTimeMillis();
		String name = Long.toString(dateTaken) + ".jpg";// createName(dateTaken)
														// +
		// ".jpg";
		fileName = folder+ File.separator + name;
		mFileName = fileName;

//		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		intent.putExtra("output", Uri.fromFile(new File(mFileName)));
//		((Activity) mContext).startActivityForResult(intent, CAMERA_PATH);
		PhotoUtils.startTakePhoto(((Activity) mContext),new File(mFileName),CAMERA_PATH);

	}

	private void cameraVideo() {
		String folder = mPaperPath + PaperUtils.SUB_VIDEO + File.separator;// Utils.ROOT_PATH
		String fileName = String.valueOf(System.currentTimeMillis()) + ".mp4";
		// mFileName = fileName;

		Bundle args = new Bundle();
		args.putString(SimpleVideoRecorder.EXTRA_VIDEO_PATH, new File(folder,
				fileName).getAbsolutePath());
		Intent intent = new Intent(mContext, SimpleVideoRecorder.class);
		intent.putExtras(args);
		try {
			((Activity) mContext).startActivityForResult(intent,
					SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			PaperUtils.outLog("cametaVideo", e.getMessage());
		}
	}

	private GestureDetector gd = null;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		gd.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public class MyGestureListener implements GestureDetector.OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			int y = (int) e.getRawY();
			if (isEditMode()) {
				((DragLayer) findViewById(R.id.dragLayer))
						.createNewEditTextForClick(y - getTop(), true);
			}
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	public interface PaperSaveListener {
		public void onSaveFinish(String paperPath, String userTitle);
	}

	private ImportDialog mDialog = null;
	private int mNewDialogW = 0;
	private int mNewDialogH = 0;
	private int mTopBarH = 50;

	private void initDialogSize() {

		final WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		float density = dm.density;

		int wpixels = display.getWidth();
		int hpixels = display.getHeight();

		if (wpixels < hpixels) {
			int temp = hpixels;
			hpixels = wpixels;
			wpixels = temp;
		}
		mNewDialogH = wpixels >> 1;
		mNewDialogW = (int) (hpixels - 50 * density);
	}

	private void showImportDialog(int selectFileType) {
		if (mNewDialogW <= 0 || mNewDialogH <= 0) {
			initDialogSize();
		}
		int attachBtnRes = 0;
		switch (selectFileType) {
		case ImportDialog.FILE_FILTER_TYPE_IMAGE:
			attachBtnRes = R.drawable.camera;
			break;
		case ImportDialog.FILE_FILTER_TYPE_AUIDO:
			attachBtnRes = R.drawable.microphone;
			break;
		case ImportDialog.FILE_FILTER_TYPE_VIDEO:
			attachBtnRes = R.drawable.camera;
			break;
		}
		mDialog = new ImportDialog(mContext, mNewDialogW, mNewDialogH
				+ mTopBarH, selectFileType, attachBtnRes, mDialogHandler);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(true);

		Window win = mDialog.getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		Rect rect = new Rect();
		View view = ((Activity) mContext).getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame(rect);
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = dm.heightPixels - rect.top;
		win.setAttributes(lp);

		mDialog.show();
	}

	private ImportDialog.ImportDialogHandler mDialogHandler = new ImportDialog.ImportDialogHandler() {

		@Override
		public void onFileSelect(int fileType, String filePath) {
			switch (fileType) {
			case ImportDialog.FILE_FILTER_TYPE_IMAGE:
				String imagePath = writeImageToCache(filePath);
				if (imagePath == null) {
					imagePath = filePath;
				}
				mPaperManger.addView(mPaperManger
						.createNewView(PaperUtils.IMAGEVIEW, imagePath, -1, -1,
								-1, mInsertPos), PaperUtils.AddViewFirst,
						mInsertPos);
				break;
			case ImportDialog.FILE_FILTER_TYPE_AUIDO:
				childViewData viewData = mPaperManger
						.createNewView(PaperUtils.RECORDVIEW, filePath, -1, -1,
								-1, mInsertPos);
				String apath = mPaperManger.getDstFilePath(viewData.mViewData,
						viewData.mViewName);
				mPaperManger.copyAudioVideoViewDataToPath(viewData.mViewData,
						apath, viewData.mViewName, viewData.mViewId);
				viewData.mViewData = apath;
				mPaperManger.addView(viewData, PaperUtils.AddViewFirst,
						mInsertPos);
				break;
			case ImportDialog.FILE_FILTER_TYPE_VIDEO:
				GetVideoThumbTask task = new GetVideoThumbTask(filePath);
				task.execute();
				break;
			}
		}

		@Override
		public void onAttachmentButtonClick(int fileType) {
			switch (fileType) {
			case ImportDialog.FILE_FILTER_TYPE_IMAGE:
				cameraImageView();
				break;
			case ImportDialog.FILE_FILTER_TYPE_AUIDO:
				myrecordView();
				break;
			case ImportDialog.FILE_FILTER_TYPE_VIDEO:
				cameraVideo();
				break;
			}
		}
	};

	private class GetVideoThumbTask extends AsyncTask<Void, Void, String> {
		String mVideoPath = null;

		public GetVideoThumbTask(String videoPath) {
			mVideoPath = videoPath;
		}

		@Override
		protected String doInBackground(Void... voids) {
			Bitmap thumbBmp = getVideoThumbnail(mVideoPath);
			String thumbPath = null;
			if (thumbBmp != null && !thumbBmp.isRecycled()) {
				String filename = String.format("%d_%dx%d%s",
						System.currentTimeMillis(), thumbBmp.getWidth(),
						thumbBmp.getHeight(), ".jpg");// String.valueOf(System.currentTimeMillis())
														// + ".jpg";
				thumbPath = getPathName() + PaperUtils.SUB_IMAGE
						+ File.separator + filename;
				PaperUtils.writeToCacheJPEG(thumbBmp, thumbPath);
			}
			return thumbPath;
		}

		@Override
		protected void onPostExecute(String thumb_path) {
			childViewData viewData = mPaperManger.createNewView(
					PaperUtils.VIDEOVIEW, mVideoPath, -1, -1, -1, mInsertPos);
			viewData.mViewData2 = thumb_path;
			if (!viewData.mViewData.startsWith(mPaperPath)) {
				String vpath = mPaperManger.getDstFilePath(viewData.mViewData,
						viewData.mViewName);
				mPaperManger.copyAudioVideoViewDataToPath(viewData.mViewData,
						vpath, viewData.mViewName, viewData.mViewId);
			}
			mPaperManger.addView(viewData, PaperUtils.AddViewFirst, mInsertPos);

		}
	}

	public Bitmap getVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		if (bitmap != null && !bitmap.isRecycled()) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			int outputH = PaperUtils.IMAGE_LONG_SIZE;
			int outputW = PaperUtils.IMAGE_LONG_SIZE;
			boolean bNeedResize = false;
			if (w >= h && w > PaperUtils.IMAGE_LONG_SIZE) {
				outputH = outputW * h / w;
				bNeedResize = true;
			} else if (h >= w && h > PaperUtils.IMAGE_LONG_SIZE) {
				outputW = outputH * w / h;
				bNeedResize = true;
			}

			if (bNeedResize) {
				Bitmap sbmp = null;
				try {
					sbmp = Bitmap.createBitmap((int) outputW, (int) outputH,
							Bitmap.Config.ARGB_8888);
				} catch (OutOfMemoryError e) {
				}
				if (sbmp != null) {
					Canvas canvas = new Canvas(sbmp);
					RectF dst = new RectF(0, 0, outputW, outputH);
					canvas.drawBitmap(bitmap, null, dst, null);
					bitmap.recycle();
					bitmap = sbmp;
					canvas = null;
				}
			}
		}
		return bitmap;
	}
	//added by rmpan
	private PlayerVideoHandler mPlayerVideoHandler;

	public PlayerVideoHandler getmPlayerVideoHandler() {
		return mPlayerVideoHandler;
	}

	public void setmPlayerVideoHandler(PlayerVideoHandler mPlayerVideoHandler) {
		this.mPlayerVideoHandler = mPlayerVideoHandler;
	}

	public interface  PlayerVideoHandler{
		public void startPlayVideo();

		public void stopPlayVideo();
	}

	public interface SelectCloudResourceHandler {
		public void selectCloudResource(Handler mpHandler);

		public void selectPersonlCloudResource(Handler mpHandler);
	}

	public interface ResourceOpenHandler {
		public void openResource(int type, String resourcePath,
                                 String resourceTitle, int screentype, String webPlayUrl);
	}

	public interface FeedbackHandler {
		public void chat();

		public void follow();

		public void share();

		public void praise();

		public void edit();

		public void collect();
	}

	final static public int RESOURCE_TYPE_IMAGE = 1;
	final static public int RESOURCE_TYPE_VIDEO = 2;
	final static public int RESOURCE_TYPE_COURSE = 3;// wawa weike
	final static public int RESOURCE_TYPE_CHW = 4; // ONEPAGE|Sound album
	final static public int RESOURCE_TYPE_COURSE2 = 5;// wawa weike

	public String writeImageToCache(String imagePath) {
		Bitmap bmp = PaperUtils.loadBitmap(imagePath,
				PaperUtils.IMAGE_LONG_SIZE, 0);
		if (bmp != null && !bmp.isRecycled()) {
			String filename = String.valueOf(System.currentTimeMillis()) + "_"
					+ bmp.getWidth() + "x" + bmp.getHeight() + ".jpg";
			String cachePath = getPathName() + PaperUtils.SUB_IMAGE
					+ File.separator + filename;
			boolean rtn = PaperUtils.writeToCacheJPEG(bmp, cachePath, 100);
			if (rtn) {
				return cachePath;
			}
		}
		return null;
	}

	//added by rmpan
    public void onlinePaperInitialize(Context context, String paperUrl,
                                      String cacheFolder, ResourceOpenHandler openHandler,
                                      FeedbackHandler feedbackHandler,CustomizeParams obj) {
        customizeParams = obj;
        this.onlinePaperInitialize(context, paperUrl, cacheFolder,openHandler,customizeParams.isbTableDevice(), feedbackHandler);
    }

	public void onlinePaperInitialize(Context context, String paperUrl,
			String cacheFolder, ResourceOpenHandler openHandler,
			boolean bTableDevice, FeedbackHandler feedbackHandler) {
		if (TextUtils.isEmpty(paperUrl)) {
			return;
		}

		// isOnline = true;
		setOnline(true);

		mResourceOpenHandler = openHandler;
		mFeedbackHandler = feedbackHandler;
		mCacheFolder = cacheFolder;

		if (!paperUrl.endsWith(File.separator)) {
			paperUrl = paperUrl + File.separator;
		}
		mbBegin = true;
		mContext = context;
		mStartMode = MediaPaper.LOAD_HISTORY;
		mPaperPath = paperUrl;

		TextView send = (TextView) findViewById(R.id.edit_send);
		if (send != null) {
			send.setText(R.string.mp_completed);
		}

		// View feedbackGrp = findViewById(R.id.feedback_grp);
		if (mFeedbackHandler != null) {
			TextView praise = (TextView) findViewById(R.id.praise_btn);
			if (praise != null) {
				praise.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFeedbackHandler.praise();
					}
				});
			}

			View view = findViewById(R.id.share_btn);
			if (view != null) {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFeedbackHandler.share();
					}
				});
			}

			view = findViewById(R.id.follow_btn);
			if (view != null) {
				followBtn = view;
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFeedbackHandler.follow();
					}
				});
			}

			view = findViewById(R.id.chat_btn);
			if (view != null) {
				chatBtn = view;
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFeedbackHandler.chat();
					}
				});
			}

			view = findViewById(R.id.edit_btn);
			if (view != null) {
				editBtn = view;
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFeedbackHandler.edit();
					}
				});
			}

			view = findViewById(R.id.collect_btn);
			if (view != null) {
				collectBtn = view;
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFeedbackHandler.collect();
					}
				});
			}
		}

		setupViews(bTableDevice);
		mStartMode = MediaPaper.LOAD_HISTORY;

		clearPaper();

		if (mPaperManger != null) {
			mPaperManger.setOnlineCacheFolderPath(mCacheFolder);

			if(customizeParams != null) {
				setEditMode(customizeParams.isbEdit());//false
			}else{
				setEditMode(false);
			}
			LoadTask loadTask = new LoadTask();
			loadTask.execute();

		}
		switchCustomizeTitle();
		// View editModeBtn = findViewById(R.id.editmodebtn);
		// editModeBtn.setVisibility(View.INVISIBLE);
		mbBegin = false;
	}

	private MatrixViewPager mPreviewPager = null;
	private View mVideoPlay = null;

	private void startMediasPreview(MediaFrame v, int childId) {
		// if (mPaperManger != null) {
		// mPaperManger.setDeleteMode(false);
		// }
		setDeleteMode(false);
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		//added by rmpan not play video view
		if(mVideoPlay == null){
			//rmpan it's not video view, display relate views
			if (!v.getResourceType().equals(PaperUtils.VIDEOVIEW)) {
				showTopBarAndAttachedGrp(true);
			}
			if (v.getResourceType().equals(PaperUtils.IMAGEVIEW)) {
				if (mPreviewPager == null && mPaperManger != null) {
					int startIndex = 0;
					ArrayList<PaperManger.MediaItem> mediaList = mPaperManger
							.getMediasList();
					if (mediaList != null && mediaList.size() > 0) {
						for (int i = 0; i < mediaList.size(); i++) {
							if (childId == mediaList.get(i).id) {
								startIndex = i;
							}
						}
					}
					ViewGroup rootLayout = (ViewGroup) findViewById(R.id.root_layout);
					if (rootLayout != null) {
						mPreviewPager = initImagePager(mediaList, rootLayout,
								startIndex);
						if (mMediasPreviewListener != null) {
							mMediasPreviewListener.onStart();
						}
					}
				}
			} else if (v.getResourceType().equals(PaperUtils.VIDEOVIEW)) {
				ViewGroup rootView = (ViewGroup) findViewById(R.id.root_layout);
				mVideoPlay = playVideo(v.getResourcePath(), rootView);
				if (mMediasPreviewListener != null) {
					mMediasPreviewListener.onStart();
				}
//				}
			} else if (v.getResourceType().equals(PaperUtils.COURSEVIEW)) {
				if (mResourceOpenHandler != null) {
					mResourceOpenHandler.openResource(RESOURCE_TYPE_COURSE,
							v.getResourcePath(), v.getResourceTitle(),
							v.getScreenType(), v.getWebPlayUrl());
				}
			} else if (v.getResourceType().equals(PaperUtils.HWPAGEVIEW)) {
				if (mResourceOpenHandler != null) {
					mResourceOpenHandler.openResource(RESOURCE_TYPE_CHW,
							v.getResourcePath(), v.getResourceTitle(),
							v.getScreenType(), v.getWebPlayUrl());
				}
			} else if (v.getResourceType().equals(PaperUtils.COURSEVIEW2)) {
				if (mResourceOpenHandler != null) {
					mResourceOpenHandler.openResource(RESOURCE_TYPE_COURSE2,
							v.getResourcePath(), v.getResourceTitle(),
							v.getScreenType(), v.getWebPlayUrl());
				}
			}
		}

	}

	private boolean stopMediasPreview() {

		if (mPreviewPager != null) {
			ViewGroup rootLayout = (ViewGroup) findViewById(R.id.root_layout);
			if (rootLayout != null) {
				rootLayout.removeView(mPreviewPager);
				mPreviewPager = null;
//				adjustScroolViewPosition(mPaperContent,0,0,0,220);
				if (!mbEditMode && mMediasPreviewListener != null) {
					mMediasPreviewListener.onStop();
				}
			}
			return true;
		}
		return false;
	}
    //added by rmpan fix bug:can't click audio play button after preview image
	private void adjustScroolViewPosition(View v, int l, int t, int r, int b){
		if(null != v) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
			if (lp != null) {
				lp.setMargins(l, t, r, b);
				lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
				lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
				v.setLayoutParams(lp);
			}
		}
	}
	private MatrixViewPager initImagePager(
			ArrayList<PaperManger.MediaItem> mediaList, ViewGroup container,
			int startIndex) {
		MatrixViewPager imagePager = new MatrixViewPager(mContext);
		PreviewAdapter adapter = new PreviewAdapter(mediaList);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		container.addView(imagePager, lp);
		imagePager.setAdapter(adapter);
		imagePager.setCurrentItem(startIndex);

		return imagePager;
	}

	private class PreviewAdapter extends PagerAdapter {

		ArrayList<PaperManger.MediaItem> mMediaList = null;

		public PreviewAdapter(ArrayList<PaperManger.MediaItem> mediaList) {
			mMediaList = mediaList;
		}

		@Override
		public int getCount() {
			return mMediaList == null ? 0 : mMediaList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view == o;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View page = LayoutInflater.from(mContext).inflate(
					R.layout.media_item, null);
			View playBtn = page.findViewById(R.id.videoPlay);
			MatrixImageView iv = (MatrixImageView) page.findViewById(R.id.image);
			if (mMediaList.get(position).mResourceType
					.equals(PaperUtils.IMAGEVIEW)) {
				playBtn.setVisibility(View.GONE);
			} else {
				playBtn.setVisibility(View.VISIBLE);
			}

			Bitmap bmp = PaperUtils.loadBitmap(
					mMediaList.get(position).mThumbCache, getWidth(),
					getHeight());
			iv.setImageBitmap(bmp);
			iv.setDragListener(mPreviewPager);
			container.addView(page);

			playBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					PaperManger.MediaItem mediaItem = mMediaList.get(position);
					String resourceType = mediaItem.mResourceType;

					if (resourceType.equals(PaperUtils.VIDEOVIEW)) {
						ViewGroup rootView = (ViewGroup) findViewById(R.id.root_layout);
						mVideoPlay = playVideo(
								mMediaList.get(position).mResourceUrl, rootView);
						// mHandler.post(new
						// AttachVideoRun(mMediaList.get(position).mResourceUrl,
						// mContext, rootView, 0));
					} else {
						if (mResourceOpenHandler != null) {
							int typeId = -1;
							if (resourceType.equals(PaperUtils.COURSEVIEW)) {
								typeId = RESOURCE_TYPE_COURSE;
							} else if (resourceType
									.equals(PaperUtils.HWPAGEVIEW)) {
								typeId = RESOURCE_TYPE_CHW;
							}
							if (typeId >= 0) {
								mResourceOpenHandler.openResource(typeId,
										mediaItem.mResourceUrl,
										mediaItem.mTitle,
										mediaItem.mScreenType,
										mediaItem.mWebplayUrl);
							}
						}
					}

				}
			});
			return page;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;

			ImageView iv = (ImageView) view.findViewById(R.id.image);
			if (iv != null) {
				BitmapDrawable old_d = (BitmapDrawable) iv.getDrawable();
				iv.setImageBitmap(null);
				if (old_d != null) {
					Bitmap old_b = old_d.getBitmap();
					if (old_b != null && !old_b.isRecycled()) {
						old_b.recycle();
					}
				}
			}
			container.removeView(view);
		}
	}

	public View playVideo(String path, ViewGroup attachRoot) {

		View videoPlay = LayoutInflater.from(mContext).inflate(
				R.layout.video_play_view_mp, null);
		if (videoPlay != null) {
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			attachRoot.addView(videoPlay, lp);
		}
		VideoView videoView = (VideoView) videoPlay
				.findViewById(R.id.videoview);
		View closeBtn = videoPlay.findViewById(R.id.close_btn);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopVideoPlay();
			}
		});

		MediaController mc = new MediaController(mContext, false);
		mc.setMediaPlayer(videoView);
		mc.show(0);
		videoView.setMediaController(mc);

		videoView.setVideoPath(path);

		videoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						stopVideoPlay();
					}
				});

		videoView.start();
		showTopBarAndAttachedGrp(false);
		return videoPlay;
	}
	/**
	 * added by rmpan don't response click event
	 * while playing video
	 */
	private void showTopBarAndAttachedGrp(boolean show){
		if(!show){
//			mTitleLay.setVisibility(View.GONE);
//			mTopBar.setVisibility(View.GONE);
			showOrHideMenu(true);
//			mAttachedGrp.setVisibility(View.GONE);
			mTitleLay.setEnabled(false);
			mTopBar.setEnabled(false);
			mAttachedGrp.setEnabled(false);
			mDeleteModeBtn.setEnabled(false);

			if (mPlayerVideoHandler != null) {
				mPlayerVideoHandler
						.startPlayVideo();
			}
		}else{
			mTitleLay.setVisibility(View.VISIBLE);
			mTopBar.setVisibility(View.VISIBLE);

			mTitleLay.setEnabled(true);
			mTopBar.setEnabled(true);
			mAttachedGrp.setEnabled(true);
			mDeleteModeBtn.setEnabled(true);

			if(mbEditMode) {
				showOrHideMenu(true);
				mAttachedGrp.setVisibility(View.GONE);
			} else {
				//showOrHideMenu(false);
				mAttachedGrp.setVisibility(View.VISIBLE);
				if (mPlayerVideoHandler != null) {
					mPlayerVideoHandler
							.stopPlayVideo();
				}
			}
		}

	}
	// @Override
	// public void run() {
	//
	// mVideoPlay =
	// LayoutInflater.from(mContext).inflate(R.layout.video_play_view_mp,
	// mRootView);
	// VideoView videoView = (VideoView)mVideoPlay.findViewById(R.id.videoview);
	//
	// MediaController mc = new MediaController(mContext, false);
	// mc.setMediaPlayer(videoView);
	// mc.show(0);
	// videoView.setMediaController(mc);
	//
	// videoView.setVideoPath(mPath);
	//
	// videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
	// {
	// @Override
	// public void onCompletion(MediaPlayer mp) {
	// stopVideoPlay();
	// }
	// });
	//
	// videoView.start();
	// }
	//
	// }

	public boolean stopVideoPlay() {
		boolean ret = false;
		if (mVideoPlay != null) {
			try{
				VideoView videoView = (VideoView) mVideoPlay
						.findViewById(R.id.videoview);
				if (videoView.isPlaying()) {
					videoView.stopPlayback();
				}
				ViewGroup rootView = (ViewGroup) findViewById(R.id.root_layout);
				if (rootView != null) {
					rootView.removeView(mVideoPlay);
				}
				mVideoPlay = null;
				/**
				 * rmpan display comment view  under the  condition of
				 * non-edit mode
				 */
				if (!mbEditMode && mMediasPreviewListener != null) {
					mMediasPreviewListener.onStop();
				}
				ret = true;
			}catch(Exception e){
				mVideoPlay = null;
				e.printStackTrace();
			}finally {
				showTopBarAndAttachedGrp(true);
			}
		}
		return ret;
	}

	// private void stopVideo() {
	// if (mVideoView != null) {
	// if (mVideoView.isPlaying()) {
	// mVideoView.stopPlayback();
	// }
	// mHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// BaseTouchView tv = getCurrentTouchView();
	// tv.removeView(mVideoView);
	// mVideoView = null;
	// }
	// });
	// }
	// mPlayingVideoPath = null;
	// }

	public void setupSubTitle(String str) {
		mSubTitleStr = str;
		if(null == mTitleStr  && PaperUtils.HOMEWORK_MODE == mPaperMode){//set date as default title
			mTitleStr = mSubTitleStr;
		}
		if (mSubTitle != null) {
			if(PaperUtils.COMMON_MODE == mPaperMode) {
				if (!TextUtils.isEmpty(mSubTitleStr)) {
					mSubTitle.setText(mSubTitleStr);
				}
			}else if(PaperUtils.HOMEWORK_MODE == mPaperMode) {
				mSubTitle.setText(getResources().getString(R.string.paper_homework_content));
			}
		}
	}

	public void updatePraiseNumber(int number) {
		TextView praise = (TextView) findViewById(R.id.praise_btn);
		if (praise != null) {
			praise.setText(mContext.getString(R.string.praise, number));
		}
	}

	public void setChatBtnVisible(int visible) {
		if (chatBtn != null) {
			chatBtn.setVisibility(visible);
		}
	}

	public void setFollowBtnVisible(int visible) {
		if (followBtn != null) {
			followBtn.setVisibility(visible);
		}
	}

	public void setPraiseBtnVisible(int visible) {
		if (praiseBtn != null) {
			praiseBtn.setVisibility(visible);
		}
	}

	public void setEditBtnVisible(int visible) {
		if (editBtn != null) {
			editBtn.setVisibility(visible);
		}
	}

	public void setCollectBtnVisible(int visible) {
		if (collectBtn != null) {
			collectBtn.setVisibility(visible);
		}
	}

	public void setDeleteMode(boolean bDeleteMode) {
		if (mPaperManger != null) {
			mPaperManger.setDeleteMode(bDeleteMode);
			if (bDeleteMode) {
				mDeleteModeBtn.setImageResource(R.drawable.composer_delete_hl);
			} else {
				mDeleteModeBtn.setImageResource(R.drawable.composer_delete);
			}

		}
	}

	public void setMediasPreviewListener(MediasPreviewListener listener) {
		mMediasPreviewListener = listener;
	}

	public interface MediasPreviewListener {
		public void onStart();

		public void onStop();
	}

	public interface MediaPaperExitHandler {
		public void back(boolean bEdit);

		public void backAndSend(boolean bEdit);
	}

	public void clearPaper() {
		mPaperManger.releaseShowThumbnail();
		mPaperManger.stopAll();
		mPaperManger.clearAll();

	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
		View feedbackGrp = findViewById(R.id.feedback_grp);
		if (feedbackGrp != null) {
			if (isOnline) {
				feedbackGrp.setVisibility(View.VISIBLE);
			} else {
				feedbackGrp.setVisibility(View.GONE);
			}
		}
		mAttachedGrp= findViewById(R.id.attach_layout);
		if (mAttachedGrp != null) {
			if (isOnline) {
				mAttachedGrp.setVisibility(View.VISIBLE);
			} else {
				mAttachedGrp.setVisibility(View.GONE);
			}
		}

        // 设置占位符，确保删除按钮不遮挡里面的控件
		View placeHolderLayout = findViewById(R.id.placeholder_layout);
		if (placeHolderLayout != null) {
			placeHolderLayout.setVisibility(isOnline ? View.GONE : View.VISIBLE);
		}
	}


	public AudioPopwindow getAudioPopwindow(){
		return  audioPopwindow;
	}

	public boolean isEdited() {
		return mPaperManger.isEdit();
	}

	public String getmTitleStr() {
		return mTitleStr;
	}

	public void setmTitleStr(String mTitleStr) {
		this.mTitleStr = mTitleStr;
	}
}
