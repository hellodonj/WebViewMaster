package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.libs.mediapaper.PaperUtils.childViewData;
import com.lqwawa.libs.mediapaper.player.AudioView;
import com.lqwawa.libs.mediapaper.player.AudioView.AudioBarTrackingTouchListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaperManger {
    protected static final String TAG = "PaperManger";

    LinkedList<childViewData> mPaperItems;

    Context mContext;

    // LinkedList<childViewData> mPaperItemsLL;

    private LinearLayout.LayoutParams llp;
    private MediaPaper mMediaPaper = null;

    static int viewId = 0x01;
    int mPaperBottom;

    LinearLayout mLayout;
    ScrollView mScrollView;
    private ScrollRunnable mScrollRunnable = new ScrollRunnable();
    private ViewTransRunnable mViewTransRunnable = new ViewTransRunnable();
    private boolean mFromCam = false;
    private int mMediaCount = 0;

    private AudioBarTrackingTouchListener mAudioBarTrackingTouchListener = null;
//    private VideoBarTrackingTouchListener mVideoBarTrackingTouchListener = null;
    private String mNewPath = null;

    private int mThreadCount = 0;
    private ExecutorService mExecutorService = null;
    private String mPaperSavePath = null;

    private int mChildLongSize = 0;
    private int mChildShortSize = 0;

    private String mOnlineCacheFolderPath = null;

    private boolean mbEdit = false;

    private Handler mHandler = new Handler();

    public PaperManger(Context context, MediaPaper mediaPaper, View v, String paperSavePath, boolean bTable) {
        mMediaPaper = mediaPaper;
        this.mLayout = (LinearLayout) v;
        this.mContext = context;
        this.mPaperSavePath = paperSavePath;
        mPaperItems = new LinkedList<childViewData>();

        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int wH = display.getHeight();
        int wW = display.getWidth();
        if (wH > wW) {
            wH = wW;
        }
        mChildLongSize = (int)(wH  -  (mContext.getResources().getDimension(R.dimen.view_padding) * 2));
        if (bTable) {
            mChildLongSize = (int) (mChildLongSize);//(int) (mChildLongSize - (mContext.getResources().getDimension(R.dimen.paper_padding_table) * 2));
        }  else {
            mChildLongSize = (int) (mChildLongSize - (mContext.getResources().getDimension(R.dimen.paper_padding_default) * 2));
        }

        mChildShortSize = mChildLongSize * 3 / 4;


        // loadPaperItems(context);
    }

    public void setOnlineCacheFolderPath (String onlineCacheFolderPath) {
        mOnlineCacheFolderPath = onlineCacheFolderPath;
    }

//	private Handler mHandler = new Handler() {
//      public void handleMessage(Message msg) {
//         switch (msg.what) {
//             case Utils.MSG_FILE_LOADFINISH:
//                int view_id = msg.arg1;
////                if (view_id > 0 ) {
////                   mMediaPaper.setAudioVideoEnableById(view_id);
////                }
//                mMediaPaper.copyFileEnd(view_id);
//
//                break;
//         }
//      }
//   };

    public void fillUserTitle(String userTitle) {
        mMediaPaper.fillUserTitle(userTitle);
    }

    public childViewData createNewView(String name, String data, int vMarginTop,
                                       int width, int height, int pos) {
        childViewData mchildViewData = new childViewData(name);
        mchildViewData.mViewId = viewId++;
        if (data != null) {
            mchildViewData.mViewData = data;
        }
        if (vMarginTop < 0)
            mchildViewData.mMarginTop = marginTop;
        else
            mchildViewData.mMarginTop = vMarginTop;

        mchildViewData.width = width;
        mchildViewData.height = height;

        if (pos < 0)
            mPaperItems.add(mchildViewData);
        else
            mPaperItems.add(pos, mchildViewData);

        return mchildViewData;
    }
    public childViewData createNewView(String name, String data, String data2, int vMarginTop,
                                       int width, int height, int pos) {
        childViewData mchildViewData = new childViewData(name);
        mchildViewData.mViewId = viewId++;
        if (data != null) {
            mchildViewData.mViewData = data;
        }
        if (data2 != null) {
            mchildViewData.mViewData2 = data2;
        }
        if (vMarginTop < 0)
            mchildViewData.mMarginTop = marginTop;
        else
            mchildViewData.mMarginTop = vMarginTop;

        mchildViewData.width = width;
        mchildViewData.height = height;

        if (pos < 0)
            mPaperItems.add(mchildViewData);
        else
            mPaperItems.add(pos, mchildViewData);

        return mchildViewData;
    }

    public childViewData createNewView(String name, String data, String data2, String data3, String data4, int vMarginTop,
                                       int width, int height, int pos) {
        childViewData mchildViewData = new childViewData(name);
        mchildViewData.mViewId = viewId++;
        if (data != null) {
            mchildViewData.mViewData = data;
        }
        if (data2 != null) {
            mchildViewData.mViewData2 = data2;
        }
        if (data3 != null) {
            mchildViewData.mViewData3 = data3;
        }
        if (data4 != null) {
            mchildViewData.mViewData4 = data4;
        }


        if (vMarginTop < 0)
            mchildViewData.mMarginTop = marginTop;
        else
            mchildViewData.mMarginTop = vMarginTop;

        mchildViewData.width = width;
        mchildViewData.height = height;

        if (pos < 0)
            mPaperItems.add(mchildViewData);
        else
            mPaperItems.add(pos, mchildViewData);

        return mchildViewData;
    }



    public childViewData createNewView(String name, String data, String data2, String data3, String data4, int data5, int vMarginTop,
                                       int width, int height, int pos) {
        childViewData mchildViewData = new childViewData(name);
        mchildViewData.mViewId = viewId++;
        if (data != null) {
            mchildViewData.mViewData = data;
        }
        if (data2 != null) {
            mchildViewData.mViewData2 = data2;
        }
        if (data3 != null) {
            mchildViewData.mViewData3 = data3;
        }
        if (data4 != null) {
            mchildViewData.mViewData4 = data4;
        }
        if (data5 >= 0) {
            mchildViewData.mViewData5 = data5;
        }


        if (vMarginTop < 0)
            mchildViewData.mMarginTop = marginTop;
        else
            mchildViewData.mMarginTop = vMarginTop;

        mchildViewData.width = width;
        mchildViewData.height = height;

        if (pos < 0)
            mPaperItems.add(mchildViewData);
        else
            mPaperItems.add(pos, mchildViewData);

        return mchildViewData;
    }

//    public View addTempView(int toolBarId) {
//        childViewData itemInfo = null;
////        switch (toolBarId) {
////		case R.id.textButton:s
////			itemInfo = createNewView(Utils.EDITVIEW, null, -1, -1, -1, -1);
////			break;
//        if (toolBarId == R.id.imageButton) {
//            itemInfo = createNewView(PaperUtils.IMAGEVIEW, null, -1, -1, -1, -1);
//        } else if (toolBarId == R.id.recordButton) {
//            itemInfo = createNewView(PaperUtils.RECORDVIEW, null, -1, -1, -1, -1);
//        } else if (toolBarId == R.id.videoButton) {
//            itemInfo = createNewView(PaperUtils.VIDEOVIEW, null, -1, -1, -1, -1);
//        } else if (toolBarId == R.id.emotionButton) {
//            itemInfo = createNewView(PaperUtils.COURSEVIEW, null, -1, -1, -1, -1);
//        } else if (toolBarId == R.id.textButton) {
//            itemInfo = createNewView(PaperUtils.EDITVIEW, null, -1, -1, -1, -1);
//        }
////        }
//        ImageView imageView = new ImageView(mContext);
//        imageView.setScaleType(ImageView.ScaleType.CENTER);
//        imageView.setImageResource(R.drawable.record);
//        imageView.setId(itemInfo.mViewId);
//        llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
//                LayoutParams.WRAP_CONTENT);
//        imageView.setOnLongClickListener(MediaPaper.mLongClickListener);
//        imageView.setLayoutParams(llp);
//        imageView.setVisibility(View.INVISIBLE);
//        mLayout.addView(imageView, llp);
//        return imageView;
//    }

    public void deleteTempView(View v) {

    }

    public View addView(childViewData itemInfo,
                        int FirstOrLoad, int pos) {
        if (mLayout == null) {
            return null;
        }
        if (pos > mLayout.getChildCount()) {
            pos = -1;
        }
        View v = null;
        if (itemInfo == null) {
            return v;
        }

        if (FirstOrLoad != PaperUtils.AddViewLoad) {
            mbEdit = true;
        }



        if (PaperUtils.EDITVIEW.equals(itemInfo.mViewName)) {
            v = addEditView(itemInfo, FirstOrLoad, pos);
        } else if (PaperUtils.IMAGEVIEW.equals(itemInfo.mViewName)) {
//            v = addImageView(itemInfo, FirstOrLoad, pos);
            v = addImageView(itemInfo, pos);
        } else if (PaperUtils.VIDEOVIEW.equals(itemInfo.mViewName)) {
            v = addVideoView(itemInfo, pos);
        } else if (PaperUtils.RECORDVIEW.equals(itemInfo.mViewName)) {
            v = addRecordView(itemInfo, pos);
        } else if (PaperUtils.COURSEVIEW.equals(itemInfo.mViewName) || PaperUtils.COURSEVIEW2.equals(itemInfo.mViewName)) {
            v = addCourseView(itemInfo, pos);
        } else if (PaperUtils.HWPAGEVIEW.equals(itemInfo.mViewName)) {
            v = addCHWView(itemInfo, pos);
        }

        if (pos < 0) {
            if (!PaperUtils.EDITVIEW.equals(itemInfo.mViewName)) {
                handleScroll(PaperUtils.SCROLL_BOTTOM);
            }
        } else {
            mMediaPaper.resetInertPos();
        }
        resetParamForAddView();
//		PaperUtils.outLog("-----------" + getCamImageCount());
        return v;
    }

    static int getFocusLength = -1, i;

//	public void addTitleEditText(){
//		final MyEditText editText = new MyEditText(mContext);
//
//		editText.setId(999);
//		llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.WRAP_CONTENT, 1.0f);
//		llp.bottomMargin = PaperUtils.bottomMargin;
//		editText.setTextSize(30);
//		editText.setTextColor(0x999000);
//		mLayout.addView(editText, llp);
//	}

//    private View addEditView(final childViewData mchildViewData, int FirstOrLoad, int Pos) {
//        final MyEditText editText = new MyEditText(mContext);
//
//        editText.setId(mchildViewData.mViewId);
//
//        llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
//                LayoutParams.WRAP_CONTENT, 1.0f);
//        llp.bottomMargin = PaperUtils.bottomMargin;
//        llp.topMargin = mchildViewData.mMarginTop;
//        if (mchildViewData.mViewData != null) {
//            editText.mySetText(mchildViewData.mViewData);//setText(mchildViewData.mViewData);
//        }
//
//        editText.setOnClickListener(MediaPaper.mClickListener);
//        editText.setTextSize(18);
//        editText.setTextColor(0xff333333);
////		editText.setLineSpacing(Utils.EDIT_LINE_SPACE, 1.0f);
//        editText.setGravity(Gravity.CENTER_VERTICAL);
//        editText.setLineSpacing(0.0f, 1.5f);
//
//        editText.setBackgroundResource(R.drawable.text_frame);
////        if (mMediaPaper.isEditMode()) {
////            editText.setBackgroundResource(0);
////        }
//
//        if (Pos < 0)
//            mLayout.addView(editText, llp);
//        else
//            mLayout.addView(editText, Pos, llp);
//
//        editText.setOnKeyListener(new EditText.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP) {
//                    switch (keyCode) {
//                        case KeyEvent.KEYCODE_DEL:
//                            Layout layout = editText.getLayout();
//                            getFocusLength = editText.length();
//
//                            int newSelStart = layout.getLineStart(0);
//                            int newSelEnd = layout.getLineEnd(0);
//
//                            CharSequence s = editText.getText().subSequence(layout.getLineStart(0), layout.getLineEnd(0));
//
//
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                return false;
//            }
//        });
//
//
//        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
//            public void onFocusChange(View v, boolean hasFocus) {
//                Editable etable = editText.getText();
//                if (hasFocus) {
////					llp = (android.widget.LinearLayout.LayoutParams) editText.getLayoutParams();
////					llp.height = LayoutParams.FILL_PARENT;
////					editText.setLayoutParams(llp);
//                    // editText.setMinLines(Utils.minFocusLines);
////					Selection.setSelection(etable, 0);//etable.length()/2);
//
////					mScrollView.clearFocus();
////					mScrollView.setFocusable(false);
//
////					InputMethodManager imm = (InputMethodManager) mContext
////					.getSystemService(Context.INPUT_METHOD_SERVICE);
////					if (imm != null) {
////						imm.showSoftInput(editText, 0);
//////						imm.hideSoftInputFromWindow(mLayout.getWindowToken(),
//////								0);
////					}
//
////               int newSelStart = Selection.getSelectionStart(mText);
////               int newSelEnd = Selection.getSelectionEnd(mText);
//
////					MediaPaper.numEdit++;
//                } else {
//                    getFocusLength = -1;
//                    // if( etable.subSequence(etable.length()-1, etable.length())){
//                    // }
//
//                    // editText.setMinLines(Utils.minNoFocusLines);
//                }
//            }
//        });
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                mchildViewData.mViewData = String.valueOf(s);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        if (FirstOrLoad == PaperUtils.AddViewFirst) {
//            editText.setFocusableInTouchMode(true);
//            editText.setFocusable(true);
//            editText.requestFocus();
//        } else if (FirstOrLoad == PaperUtils.AddViewLoad) {
//            editText.setFocusable(false);
//        }
//
//
//        if (!mMediaPaper.isEditMode()) {
//            editText.setEnabled(false);
//        } else {
//            editText.setEnabled(true);
//        }
//
//        return editText;
//    }
    private View addEditView(final childViewData mchildViewData,
             int FirstOrLoad, int pos) {

        Textbox textbox = new Textbox(mContext);
        if (textbox != null) {

            textbox.setId(mchildViewData.mViewId);

            if (mchildViewData.mViewData != null) {
                textbox.setText(mchildViewData.mViewData);
            }

            textbox.setOnClickListener(MediaPaper.mClickListener);
            textbox.setOnLongClickListener(MediaPaper.mLongClickListener);
            textbox.setTextChangeListener(new ContainsEmojiEditText.OnTextChangeListener() {
                @Override
                public void onTextChange(String s) {
                    mchildViewData.mViewData = String.valueOf(s);
                    mbEdit = true;
                }
            });

            textbox.setEditMode(mMediaPaper.isEditMode());
//            textbox.setBackgroundResource(0);
            if (FirstOrLoad == PaperUtils.AddViewFirst) {
                textbox.getFocus();
            }
        }


        llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        llp.bottomMargin = PaperUtils.bottomMargin;
        llp.topMargin = mchildViewData.mMarginTop;
        if (pos < 0)
            mLayout.addView(textbox, llp);
        else
            mLayout.addView(textbox, pos, llp);

        View deleteBtn = textbox.findViewById(R.id.dele);
        if (deleteBtn != null) {
            textbox.setDeleteHandler(deleteBtn, mChildDeleteHandler);
        }
        return textbox;
    }

    void createNewEditText(int pos) {
        addView(createNewView(PaperUtils.EDITVIEW, null, -1, -1, -1, pos),
                PaperUtils.AddViewFirst, pos);
    }


    private View addImageView(final childViewData mchildViewData,
                              int pos) {

        MediaFrame videoView = new MediaFrame(mContext);
        if (videoView != null) {

            videoView.setId(mchildViewData.mViewId);

            videoView.setVideoData(PaperUtils.IMAGEVIEW, mchildViewData.mViewData, mOnlineCacheFolderPath, mchildViewData.mViewData,
                    mchildViewData.mViewData3, mChildLongSize, mChildShortSize,
                    new MediaFrame.VideoViewResourceOpenHandler() {
                        @Override
                        public void openResource(String resourceUrl, String title) {
                            if (mMediaPaper.mResourceOpenHandler != null) {
                                mMediaPaper.mResourceOpenHandler.openResource(MediaPaper.RESOURCE_TYPE_VIDEO, mchildViewData.mViewData,
                                        mchildViewData.mViewData3, mchildViewData.mViewData5, mchildViewData.mViewData4);
                            }
                        }
                    }, false, mchildViewData.mViewData4);

            videoView.setOnClickListener(MediaPaper.mClickListener);
            videoView.setOnLongClickListener(MediaPaper.mLongClickListener);
        }

        stopAll();
        addToMediaList(mchildViewData.mViewId);

        llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        llp.bottomMargin = PaperUtils.bottomMargin;
        llp.topMargin = mchildViewData.mMarginTop;
        if (pos < 0)
            mLayout.addView(videoView, llp);
        else
            mLayout.addView(videoView, pos, llp);
        mMediaCount++;

        View deleteBtn = videoView.findViewById(R.id.dele);
        if (deleteBtn != null) {
            videoView.setDeleteHandler(deleteBtn, mChildDeleteHandler);
        }
        return videoView;
    }




//    private View addImageView(final childViewData childViewData, int FirstOrLoad, int pos) {
//        Bitmap bmp = null;
//        ImageHold imageView = new ImageHold(mContext);
//        imageView.setId(childViewData.mViewId);
//        llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
//                LayoutParams.WRAP_CONTENT);
//        llp.bottomMargin = PaperUtils.bottomMargin;
//        llp.topMargin = childViewData.mMarginTop;
//        int dispW = mChildLongSize;
//        int dispH = mChildShortSize;
//
//        if (childViewData.mViewData != null) {
//            bmp = PaperUtils.loadBitmap(childViewData.mViewData, PaperUtils.IMAGE_LONG_SIZE, 0, 0);
//            int bmpW = bmp.getWidth();
//            int bmpH = bmp.getHeight();
//            if (bmpW >= bmpH) {
//                dispW = mChildLongSize;
//                dispH = mChildShortSize;
//            } else {
//                dispW = mChildShortSize;
//                dispH = mChildLongSize;
//            }
//            childViewData.width = dispW;
//            childViewData.height = dispH;
//
//            imageView.setImageBitmap(bmp);
//            imageView.requestLayout();
//            imageView.invalidate();
//        } else {
//            imageView.setImageResource(R.drawable.photo_icon);
//        }
//        View baseLayout = imageView.getBaseLayout();
//        ViewGroup.LayoutParams lp = baseLayout.getLayoutParams();
//        lp.width = dispW;
//        lp.height = dispH;
//        baseLayout.setLayoutParams(lp);
//
//        if (pos < 0)
//            mLayout.addView(imageView, llp);
//        else
//            mLayout.addView(imageView, pos, llp);
//
//        mMediaCount++;
//        addToNeedFocusViewList(imageView);
//
//        imageView.setFocusable(true);
//        imageView.requestFocus();
//        imageView.setOnLongClickListener(MediaPaper.mLongClickListener);
//        imageView.setClickable(true);
//        imageView.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                if (mMediaPaper.isEditMode()) {
//                    needFocusViewRequestFocusById(v.getId());
//                } else {
////			      Intent intent = new Intent();
////			      intent.setClass(mContext, ImageBrowse.class);
////			      Bundle bundle = new Bundle();
////   		      if (childViewData.mViewData != null) {
////   		         bundle.putString(ImageBrowse.FIRST_IMAGE_PATH, childViewData.mViewData);
////			      }
////			      intent.putExtras(bundle);
////			      ((Activity)mContext).startActivityForResult(intent, FullDiary.IMAGE_BROWSE_REQUEST);
//                    MediaPaper.mClickListener.onClick(v);
//                }
//            }
//        });
//        if (FirstOrLoad == PaperUtils.AddViewFirst) {
//            needFocusViewRequestFocusById(imageView.getId());
//        }
//        String folderName = childViewData.mViewData;
//        folderName = folderName.substring(0, folderName
//                .lastIndexOf(File.separator));
//        if (!folderName.equals(mMediaPaper.getPathName()
//                + PaperUtils.SUB_IMAGE)) {
//            if (bmp != null && !bmp.isRecycled()) {
//                long time = System.currentTimeMillis();
//                String filename = String.valueOf(time) + ".jpg";
//                String cacheName = mMediaPaper.getPathName()
//                        + PaperUtils.SUB_IMAGE + File.separator + filename;
//                File file = new File(cacheName);
//                boolean exists = file.exists();
//                if (exists) {
//                    file.delete();
//                    file = new File(cacheName);
//                }
//                PaperUtils.writeToCacheJPEG(bmp, cacheName);
//                childViewData.mViewData = cacheName;
//            }
//        }
//        imageView.setOnImageLayoutChangeListener(new OnLayoutChangeListener() {
//            public void OnLayoutChange(View v, int width, int height) {
//                childViewData.width = width;
//                childViewData.height = height;
//            }
//        });
//        imageView.setOnRotateListener(new OnRotateListener() {
//            public void OnRotate(View v, Bitmap bmp, int width, int height) {
//                if (bmp == null) {
//                    return;
//                }
//                String path = null;
//                if (childViewData.mViewData != null) {
//                    path = childViewData.mViewData;
//                    File file = new File(path);
//                    if (path.contains(mMediaPaper.getPathName()) && file.exists()) {
//                        file.delete();
//                    }
//                    childViewData.mViewData = null;
//                } else {
//                    path = mMediaPaper.getPathName() + PaperUtils.SUB_IMAGE
//                            + File.separator;
//                    long dateTaken = System.currentTimeMillis();
//                    path = path + Long.toString(dateTaken) + ".jpg";
//                }
//
//                PaperUtils.writeToCacheJPEG(bmp, path);
//                childViewData.mViewData = path;
////                File pic = new File(path);
////                FileOutputStream fos;
////                try {
////                    fos = new FileOutputStream(pic);
////                    if (fos != null) {
////                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
////                    }
////                    fos.close();
////                    childViewData.mViewData = path;
////                } catch (IOException e) {
////                    // TODO Auto-generated catch block
////                    e.printStackTrace();
////                }
//                childViewData.width = width;
//                childViewData.height = height;
//            }
//        });
//
////        saveImageToDB(childViewData);
//        return imageView;
//    }

//    private void saveImageToDB(childViewData childViewData) {
//        if (Utils.getDiaryDB(mContext) == null || childViewData.mViewData == null) {
//            Utils.outLog("saveImageToDB error childViewData.mViewData=" + childViewData.mViewData);
//            return;
//        }
//        String str = childViewData.mViewData;
//        String path = str;
//        if (str == null)
//            return;
//        if (str.contains(Utils.ROOT_PATH)) {
//            path = str.substring(Utils.ROOT_PATH.length());
//        }
//        Cursor cur = Utils.getDiaryDB(mContext).getImage(str);
//        if (cur == null || cur.getCount() == 0) {
//            Cursor c = Utils.getDiaryDB(mContext).getImage(path);
//            if (c == null || c.getCount() == 0) {
//                ImageItem item = new ImageItem();
//                item.setTitle(mMediaPaper.getPaperTitle());
//                item.setPath(path);
//                Utils.getDiaryDB(mContext).insertImage(item);
//
//            }
//            if (c != null)
//                c.close();
//        }
//        if (cur != null)
//            cur.close();
//    }


    private View addRecordView(childViewData mchildViewData,
                               int pos) {

        if (mchildViewData.mViewData == null)
            return null;
        AudioView audioView = new AudioView(mContext, mchildViewData, this, mChildLongSize);
        audioView.setId(mchildViewData.mViewId);

        audioView.setOnClickListener(MediaPaper.mClickListener);
        audioView.setOnLongClickListener(MediaPaper.mLongClickListener);
        if (mAudioBarTrackingTouchListener != null) {
            audioView.setSeekbarTrackingTouchListener(mAudioBarTrackingTouchListener);
        }
        stopAll();
        addToMediaList(mchildViewData.mViewId);

        llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        llp.bottomMargin = PaperUtils.bottomMargin;
        llp.topMargin = mchildViewData.mMarginTop;
        if (pos < 0)
            mLayout.addView(audioView, llp);
        else
            mLayout.addView(audioView, pos, llp);
        mMediaCount++;

        View deleteBtn = audioView.findViewById(R.id.dele);
        if (deleteBtn != null) {
            audioView.setDeleteHandler(deleteBtn, mChildDeleteHandler);
        }
        return audioView;
    }

    /*
     *
     * ldp add
     */
    private int marginTop = 0;

    public void setParamForAddView(int marginTop) {
        this.marginTop = marginTop;
    }

    public void resetParamForAddView() {
        this.marginTop = 0;
    }

    private View addVideoView(final childViewData mchildViewData,
                              int pos) {

        MediaFrame videoView = new MediaFrame(mContext);
        if (videoView != null) {

            videoView.setId(mchildViewData.mViewId);

            videoView.setVideoData(PaperUtils.VIDEOVIEW, mchildViewData.mViewData2, mOnlineCacheFolderPath, mchildViewData.mViewData,
                    mchildViewData.mViewData3, mChildLongSize, mChildShortSize,mchildViewData.mViewData5,
                    new MediaFrame.VideoViewResourceOpenHandler() {
                        @Override
                        public void openResource(String resourceUrl, String title) {
                            if (mMediaPaper.mResourceOpenHandler != null) {
                                mMediaPaper.mResourceOpenHandler.openResource(MediaPaper.RESOURCE_TYPE_VIDEO, mchildViewData.mViewData,
                                        mchildViewData.mViewData3, mchildViewData.mViewData5, mchildViewData.mViewData4);
                            }
                        }
                    }, mchildViewData.mViewData4);

            videoView.setOnClickListener(MediaPaper.mClickListener);
            videoView.setOnLongClickListener(MediaPaper.mLongClickListener);
        }

        stopAll();
        addToMediaList(mchildViewData.mViewId);

        llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT); 
        llp.bottomMargin = PaperUtils.bottomMargin;
        llp.topMargin = mchildViewData.mMarginTop;
        if (pos < 0)
            mLayout.addView(videoView, llp);
        else
            mLayout.addView(videoView, pos, llp);
        mMediaCount++;
        View deleteBtn = videoView.findViewById(R.id.dele);
        if (deleteBtn != null) {
            videoView.setDeleteHandler(deleteBtn, mChildDeleteHandler);
        }

        return videoView;
    }

    private View addCourseView(final childViewData mchildViewData,
                              int pos) {

        MediaFrame videoView = new MediaFrame(mContext);
        if (videoView != null) {
            videoView.setId(mchildViewData.mViewId);
            videoView.setVideoData(mchildViewData.mViewName, mchildViewData.mViewData2, mOnlineCacheFolderPath, mchildViewData.mViewData,
                    mchildViewData.mViewData3, mChildLongSize, mChildShortSize, mchildViewData.mViewData5,
                    new MediaFrame.VideoViewResourceOpenHandler() {
                        @Override
                        public void openResource(String resourceUrl, String title) {
                            int resourceType = MediaPaper.RESOURCE_TYPE_COURSE;
                            if (mchildViewData.mViewName.equals(PaperUtils.COURSEVIEW2)) {
                                resourceType = MediaPaper.RESOURCE_TYPE_COURSE2;
                            }
                            if (mMediaPaper.mResourceOpenHandler != null) {
                                mMediaPaper.mResourceOpenHandler.openResource(resourceType, mchildViewData.mViewData,
                                        mchildViewData.mViewData3, mchildViewData.mViewData5, mchildViewData.mViewData4);
                            }
                        }
                    }, mchildViewData.mViewData4);
            videoView.setOnClickListener(MediaPaper.mClickListener);
            videoView.setOnLongClickListener(MediaPaper.mLongClickListener);
        }

        stopAll();
        addToMediaList(mchildViewData.mViewId);

        llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        llp.bottomMargin = PaperUtils.bottomMargin;
        llp.topMargin = mchildViewData.mMarginTop;
        if (pos < 0)
            mLayout.addView(videoView, llp);
        else
            mLayout.addView(videoView, pos, llp);
        mMediaCount++;

        View deleteBtn = videoView.findViewById(R.id.dele);
        if (deleteBtn != null) {
            videoView.setDeleteHandler(deleteBtn, mChildDeleteHandler);
        }

        return videoView;
    }

    private View addCHWView(final childViewData mchildViewData,
                               int pos) {

        MediaFrame videoView = new MediaFrame(mContext);
        if (videoView != null) {

            videoView.setId(mchildViewData.mViewId);

            videoView.setVideoData(PaperUtils.HWPAGEVIEW, mchildViewData.mViewData2, mOnlineCacheFolderPath, mchildViewData.mViewData,
                    mchildViewData.mViewData3, mChildLongSize, mChildShortSize, mchildViewData.mViewData5,
                    new MediaFrame.VideoViewResourceOpenHandler() {
                        @Override
                        public void openResource(String resourceUrl, String title) {
                            if (mMediaPaper.mResourceOpenHandler != null) {
                                mMediaPaper.mResourceOpenHandler.openResource(MediaPaper.RESOURCE_TYPE_CHW, mchildViewData.mViewData,
                                        mchildViewData.mViewData3, mchildViewData.mViewData5, mchildViewData.mViewData4);
                            }
                        }
                    }, mchildViewData.mViewData4);

            videoView.setOnClickListener(MediaPaper.mClickListener);
            videoView.setOnLongClickListener(MediaPaper.mLongClickListener);

        }


        stopAll();
        addToMediaList(mchildViewData.mViewId);

        llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        llp.bottomMargin = PaperUtils.bottomMargin;
        llp.topMargin = mchildViewData.mMarginTop;
        if (pos < 0)
            mLayout.addView(videoView, llp);
        else
            mLayout.addView(videoView, pos, llp);
        mMediaCount++;

        View deleteBtn = videoView.findViewById(R.id.dele);
        if (deleteBtn != null) {
            videoView.setDeleteHandler(deleteBtn, mChildDeleteHandler);
        }
        return videoView;
    }



    private void runInThread(Runnable r) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(3);
        }
        if (r != null) {
            mThreadCount++;
            mExecutorService.submit(r);
        }
    }

    public void copyAudioVideoViewDataToPath(final String srcFilePath, final String destFilePath,
                                             final String viewName, final int viewId) {
        copyAudioVideoViewDataToPath(srcFilePath, destFilePath, viewName, viewId, false);
    }

    public void copyAudioVideoViewDataToPath(final String srcFilePath, final String destFilePath,
                                             final String viewName, final int viewId, final boolean delsrc) {
        if (srcFilePath == null || !new File(srcFilePath).exists()) {
            return;
        }
        if (new File(destFilePath).exists()) {
            return;
        }
        runInThread(new Runnable() {
            public void run() {
                File srcFile = new File(srcFilePath);
                File dstFile = new File(destFilePath);
                int rtn = PaperUtils.copyFile(srcFile, dstFile);
                if (rtn >= 0) {
                    for (i = 0; i < mPaperItems.size(); i++) {
                        if (viewId == mPaperItems.get(i).mViewId) {
                            mPaperItems.get(i).mViewData = destFilePath;
                            break;
                        }
                    }
                    if (delsrc) {
                        srcFile.delete();
                    }
                }
                if (mThreadCount > 0) {
                    mThreadCount--;
                }
            }
        });
    }

    public boolean isCacheFinish() {
        return mThreadCount == 0;
    }

    public childViewData getDataByItemId(int id) {
        int count = mPaperItems.size();
        childViewData data = null;
        int i;

        for (i = 0; i < count; i++) {
            data = mPaperItems.get(i);
            if (id == data.mViewId) {
                break;
            }
        }
        return data;
    }

    public int getPosByItemId(int id) {
        int count = mPaperItems.size();
        childViewData data = null;
        int i;

        for (i = 0; i < count; i++) {
            data = mPaperItems.get(i);
            if (id == data.mViewId) {
                break;
            }
        }
        return i;
    }

    public int getPosByViewId(int ViewId) {
        int count = mLayout.getChildCount();
        View v = null;
        int i;

        for (i = 0; i < count; i++) {
            v = mLayout.getChildAt(i);
            if (ViewId == v.getId()) {
                break;
            }
        }
        return i;
    }

    public View getDataByViewId(int ViewId) {
        int count = mLayout.getChildCount();
        View v = null;
        int i;

        for (i = 0; i < count; i++) {
            v = mLayout.getChildAt(i);
            if (ViewId == v.getId()) {
                break;
            }
        }
        return v;
    }

    public void removeViewFromList(View v, int dragAction,
                                   boolean enteredRegionForDrag) {
//        if (v != null && (v instanceof ImageHold)) {
//            removeViewFromNeedFocusViewList(v);
//        }

        int count = mPaperItems.size();
        int i = 0;

        childViewData mData = null;
        for (i = 0; i < count; i++) {
            mData = mPaperItems.get(i);
            if (v.getId() == mData.mViewId) {
                // removeFromMediaList(v.getId());
                break;
            }
        }

        if (dragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON) {
            mPaperItems.remove(i);
            removeFromMediaList(i);
            mLayout.removeView(v);
            if (enteredRegionForDrag) {
                mMediaPaper.setmInertPos(i);
                if (!mData.mViewName.equals(PaperUtils.EDITVIEW) && getCamImageCount() >= PaperUtils.IMAGECOUNTLIMIT) {
                    PaperUtils.showMessage(mContext, mContext.getString(R.string.reach_max));
                    return;
                }
                int havefree = PaperUtils.haveSpace();
                if (havefree > 0) {
                    PaperUtils.showMessage(mContext, mContext.getString(R.string.sdcard_full));
                    return;
                } else if (havefree < 0) {
                    PaperUtils.showMessage(mContext, mContext.getString(R.string.sdcard_mount));
                    return;
                }
                mMediaPaper.addViewByNameForDrag(mData);
                // View vv = mLayout.getChildAt(i);
                // resetMargin(vv, vv.getHeight(), mMediaPaper.mDragLayer.getSp());
            } else {
                View v1, v2;
                v1 = mLayout.getChildAt(i - 1);
                v2 = mLayout.getChildAt(i);
                if (v1 instanceof EditText && v2 instanceof EditText) {
                    ((EditText) v1).getText().append("\n");
                    ((EditText) v1).getText().append(((EditText) v2).getText());
                    removeViewFromList(v2, PaperUtils.DRAG_ACTION_VIEW, false);
                }
            }

        } else if (dragAction == PaperUtils.DRAG_ACTION_VIEW) {
            removeFile(mPaperItems.get(i));
            mPaperItems.remove(i);
            removeFromMediaList(v.getId());
            mLayout.removeView(v);

            if (!mData.mViewName.equals(PaperUtils.EDITVIEW)) {
                mMediaCount--;
            }

            View v1, v2;
            v1 = mLayout.getChildAt(i - 1);
            v2 = mLayout.getChildAt(i);
            if (v1 instanceof EditText && v2 instanceof EditText) {
                ((EditText) v1).getText().append("\n");
                ((EditText) v1).getText().append(((EditText) v2).getText());
                removeViewFromList(v2, PaperUtils.DRAG_ACTION_VIEW, false);
            }
        }
        oldPosition = -2;
        oldLine = -2;

        if(mbDeleteMode && mPaperItems != null && mPaperItems.size() == 0) {
            if(mMediaPaper != null) {
                mMediaPaper.setDeleteMode(false);
            }
        }
    }

    public int getPositionByView(View v, int top) {
        int count = mLayout.getChildCount();

        int y = top;

        for (int i = 0; i < count; i++) {
            View iv = mLayout.getChildAt(i);
            llp = (LinearLayout.LayoutParams) iv.getLayoutParams();

            if (((iv.getTop() - llp.topMargin) - PaperUtils.bottomMargin / 2) < y
                    && (iv.getTop() + iv.getHeight() / 2) >= y) {
                return i - 1;
            } else if ((iv.getBottom() + PaperUtils.bottomMargin / 2) >= y
                    && (iv.getTop() + iv.getHeight() / 2) < y) {
                return i;
            }
        }

        return count;
    }

    public void getLayoutBottom() {
        View v = mLayout.getChildAt(mLayout.getChildCount() - 1);
        if (v == null)
            mPaperBottom = 0;
        else
            mPaperBottom = v.getBottom();

    }

    public void setMarginTop(View v, int top) {
        int i;
        int count = mPaperItems.size();

        for (i = 0; i < count; i++) {
            childViewData data = mPaperItems.get(i);
            if (v.getId() == data.mViewId) {
                break;
            }
        }
    }

    static int oldPosition = -2;
    private CharSequence mText;
    private CharSequence mTransformed;
    public static int first = 0;
    static int oldLine = -2;

    private boolean handleEditText(int etPos, View v, int y, int action) {
        int count = mLayout.getChildCount();
        View iv2 = null;
        int line = -1;

        boolean newFlag = false;

        for (int i = 0; i < count; i++) {
            View iv = mLayout.getChildAt(i);
            if ((iv.getTop() < y && (iv.getTop() + iv.getHeight()) >= y)) {
                if (iv instanceof EditText) {
                    int pos = getPosByViewId(v.getId());

                    Layout layout = ((EditText) iv).getLayout();
                    line = layout.getLineForVertical(y - iv.getTop());
                    mText = layout.getText();

                    if (action == PaperUtils.SLIP_ACTIONDOWN) {
                        mTransformed = mText.subSequence(0, layout.getLineEnd(line));
                        if (pos == 0) {
                            addView(createNewView(PaperUtils.EDITVIEW, null, -1, -1, -1,
                                    0), PaperUtils.AddViewFirst, 0);
                            iv2 = mLayout.getChildAt(0);
                            ((MyEditText) iv).setmFlagEnd(false);   //pp fix 20110117
                        } else {
                            iv2 = mLayout.getChildAt(pos - 1);
                            if (!(iv2 instanceof EditText)) {
                                addView(createNewView(PaperUtils.EDITVIEW, null,
                                        -1, -1, -1, pos), PaperUtils.AddViewFirst, pos);
                                iv2 = mLayout.getChildAt(pos);
                                iv2.clearFocus();
                                iv2.setFocusable(false);
                                newFlag = true;
                                ((MyEditText) iv).setmFlagEnd(false);
                            }
                        }

                        if (((MyEditText) iv).ismFlagEnd() && !newFlag) {
                            ((Editable) mTransformed).insert(0, "\n");
                            ((MyEditText) iv).setmFlagEnd(false);
                        }

                        if (mTransformed.length() > 0) {
                            CharSequence s = ((Editable) mTransformed).subSequence(
                                    mTransformed.length() - 1, mTransformed.length());
                            if (s.toString().equals("\n")) {
                                ((MyEditText) iv).setmFlagEnd(true);
                                ((Editable) mTransformed).delete(
                                        mTransformed.length() - 1, mTransformed.length());
                            }
                        } else {
                            ((MyEditText) iv).setmFlagEnd(false);
                        }

                        ((Editable) ((EditText) iv2).getText()).append(mTransformed);
                        ((Editable) mText).delete(0, layout.getLineEnd(line));
                        if (/*line == 0 && */mText.length() == 0) {
                            removeViewFromList(iv, PaperUtils.DRAG_ACTION_VIEW, false);
                            ((MyEditText) iv2).setmFlagEnd(true);
                        }
                    } else if (action == PaperUtils.SLIP_ACTIONUP) {

                        mTransformed = mText.subSequence(layout.getLineStart(line),
                                mText.length());
                        iv2 = mLayout.getChildAt(pos + 1);
                        if (!(iv2 instanceof EditText)) {
                            addView(createNewView(PaperUtils.EDITVIEW, null, -1, -1, -1,
                                    pos + 1), PaperUtils.AddViewFirst, pos + 1);
                            iv2 = mLayout.getChildAt(pos + 1);
                            iv2.clearFocus();
                            iv2.setFocusable(false);
                            newFlag = true;
                        } else {
                        }

                        if (((MyEditText) iv).ismFlagEnd() && !newFlag) {
                            ((Editable) mTransformed).append("\n");
                            ((MyEditText) iv).setmFlagEnd(false);
                        }
                        if (line == 0) {
                            ((Editable) mText).delete(layout.getLineStart(line), mText
                                    .length());
                        } else {
                            ((Editable) mText).delete(layout.getLineStart(line), mText
                                    .length());
                            CharSequence s = ((Editable) mText).subSequence(mText
                                    .length() - 1, mText.length());
                            if (s.toString().equals("\n")) {
                                ((MyEditText) iv).setmFlagEnd(true);
                                ((Editable) mText).delete(mText.length() - 1, mText
                                        .length());
                            }
                        }
                        ((Editable) ((EditText) iv2).getText()).insert(0,
                                mTransformed);
                        if (line == 0) {
                            removeViewFromList(iv, PaperUtils.DRAG_ACTION_VIEW, false);
                            ((MyEditText) iv2).setmFlagEnd(false);
                        }
                    }

                    return true;
                }
            }
        }

        return false;

    }

    public void addTempView(View v) {
        int i = 0;
        int count = mPaperItems.size();
        childViewData currentData = new childViewData();
        currentData = mPaperItems.get(i);

        for (i = 0; i < count; i++) {
            childViewData data = mPaperItems.get(i);
            if (v.getId() == data.mViewId) {
                break;
            }
        }

        mLayout.removeView(v);
        mLayout.addView(v, 0);
        mPaperItems.remove(i);
        mPaperItems.addFirst(currentData);
    }

    public int isEditText(int y) {
        int i = -1;

        int count = mLayout.getChildCount();

        for (i = 0; i < count; i++) {
            View iv = mLayout.getChildAt(i);

            if ((iv.getTop() < y && (iv.getTop() + iv.getHeight()) >= y)) {
                if (iv instanceof EditText) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void updateView(View v, int top, int action, int dragAction) {
        if (dragAction == -1 || action == -1) {
            return;
        }


        int y = top ;

        if (dragAction == PaperUtils.DRAG_ACTION_VIEW) {
            if (y < 0) {
//				changeOfPos(v, -1);
                return;
            } else if (y > mLayout.getHeight()) {
                return;
            }
        }

        int num = getPositionByView(v, y);

        int etPos = -1;//isEditText(y);
        if (etPos == -1 && num == oldPosition) {
            return;
        }
        else if (etPos >= 0) {
            if (dragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON && first == 1) {
                changeOfPos(v, etPos);
                first = 0;
            } else {
                int pos = getPosByViewId(v.getId());
//				 PaperUtils.outLog("" + pos + " " + etPos + " " + action);
                if ((etPos + 1) != pos && (etPos - 1) != pos) {
                    mergerEditText(pos);
                    if (action == PaperUtils.SLIP_ACTIONUP) {
                        changeOfPos(v, etPos);
                    } else if (action == PaperUtils.SLIP_ACTIONDOWN) {
                        changeOfPos(v, etPos - 2);
                    }
                }
            }
            if (handleEditText(0, v, y, action)) {
                return;
            }
        }

        oldPosition = num;

        changeOfPos(v, num);

        if (dragAction == PaperUtils.DRAG_ACTION_TOOLBUTTON && first == 1) {
            first = 0;
        }
    }

    private void mergerEditText(int pos) {
        View v1, v2;
        v1 = mLayout.getChildAt(pos - 1);
        v2 = mLayout.getChildAt(pos + 1);
        if (v1 instanceof EditText && v2 instanceof EditText) {
            if (((MyEditText) v1).ismFlagEnd()) {
                ((MyEditText) v1).getText().append("\n");
            }
            ((MyEditText) v1).getText().append(((MyEditText) v2).getText());
            if (((MyEditText) v2).ismFlagEnd()) {
                ((MyEditText) v1).setmFlagEnd(true);
            } else {
                ((MyEditText) v1).setmFlagEnd(false);
            }
            // PaperUtils.outLog("dddddddddddddddddddddddd");
            removeViewFromList(v2, PaperUtils.DRAG_ACTION_VIEW, false);
        }
    }


    /*	public void updateView(View v, int top, int action, int dragAction) {

            // Utils.outLog(""+dragAction + " " +first);
            // Utils.outLog(""+top);

    //		if ((dragAction == Utils.DRAG_ACTION_TOOLBUTTON && first == 1)) {
    //			first = 0;
    ////			for (int k = 0; k < mLayout.getChildCount(); k++) {
    ////				Utils.outLog("bbbbbbbbbbb" + mLayout.getChildAt(k).getId());
    ////			}
    //		} else {
    //		}

            int num = getPositionByView(v, top);

            // Utils.outLog(""+mMediaPaper.mDragLayer.getSp());
            // if (top > mPaperBottom)
            // marginTop = top - mPaperBottom;
            // else
            // marginTop = top -

            int etPos = isEditText(top);
            if (etPos == -1 && num == oldPosition){
                return;
            }else if(etPos >= 0){
                if(dragAction == Utils.DRAG_ACTION_TOOLBUTTON && first == 1){
                    changeOfPos(v, etPos);
                    first = 0;
                }
    //			changeOfPos(v, num);
                if (handleEditText(0, v, top, action))
                return;

    //			}
    //			if (handleEditText(0, v, top, action))
    //				return;
            }

            oldPosition = num;

            changeOfPos(v, num);

            if(dragAction == Utils.DRAG_ACTION_TOOLBUTTON && first == 1){
                first = 0;
            }
    //			for (int k = 0; k < mLayout.getChildCount(); k++) {
    //				Utils.outLog("aaaaaaaa" + mLayout.getChildAt(k).getId());
    //			}
    //			if (handleEditText(etPos, v, top, action))
    //				return;
    //		handleEditText(etPos, v, top, action);


             * for (int k = 0; k < mPaperItems.size(); k++) {
             * Utils.outLog("aaaaaaaaaaa" + mPaperItems.get(k).mViewId); } for (int k
             * = 0; k < mLayout.getChildCount(); k++) { Utils.outLog("bbbbbbbbbbb" +
             * mLayout.getChildAt(k).getId()); }

        }
        */
    public void changeOfPos(View v, int num) {
        mbEdit = true;
        int count = mPaperItems.size();


        int i = 0, j = 0;
        childViewData currentData = new childViewData();

        for (i = 0; i < count; i++) {
            childViewData data = mPaperItems.get(i);
            if (v.getId() == data.mViewId) {
                break;
            }
        }

        count = mLayout.getChildCount();

        for (j = 0; j < count; j++) {
            if (v.getId() == mLayout.getChildAt(j).getId()) {
                break;
            }
        }

        currentData = mPaperItems.get(i);
        // currentData.mMarginTop = marginTop + 50;
        childViewData Data = new childViewData();

        TextView view = new TextView(mContext);
        view.setHeight(v.getHeight());

        if (num >= count) {
            mLayout.removeView(v);
            mLayout.addView(v);
            mPaperItems.remove(i);
            mPaperItems.addLast(currentData);
        } else if (num < 0) {
            mLayout.removeView(v);
            mLayout.addView(v, 0);
            mPaperItems.remove(i);
            mPaperItems.addFirst(currentData);
        } else {
            mLayout.removeView(v);
            mLayout.addView(view, j);
            mLayout.addView(v, num + 1);
            mLayout.removeView(view);
            mPaperItems.remove(i);
            mPaperItems.add(i, Data);
            mPaperItems.add(num + 1, currentData);
            mPaperItems.remove(Data);
        }


    }

    public int getLocationByView(View v, int top) {

        int count = mLayout.getChildCount();

        int y = top + mScrollView.getScrollY();

        if (top < 0) {
            return -1;
        }
        // Utils.outLog("PaperManger", "" + count);
        for (int i = 0; i < count; i++) {
            View iv = mLayout.getChildAt(i);
            // View iv2 = mLayout.getChildAt(i + 1);

            // System.out.println("===>" + iv.getTop() + " " + iv.getBottom() + " "
            // + top);
            // System.out.println("===>" + iv.getTop() + " " + iv.getBottom() + " "
            // + top);

            if (iv.getTop() < y && (iv.getBottom() + PaperUtils.bottomMargin) > y) {
                return i;
            }
        }

        return count;
    }

//    public void updateViewFromList(View v, int top) {
//
//        int num = getLocationByView(v, top);
//
//        View view = new View(mContext);
//
//        int count = mPaperItems.size();
//        // System.out.println("====>" + num + " " + count);
//
//        int i = 0, j = 0;
//        childViewData currentData = new childViewData();
//
//        for (i = 0; i < count; i++) {
//            childViewData data = mPaperItems.get(i);
//            if (v.getId() == data.mViewId) {
//                break;
//            }
//        }
//
//        for (j = 0; j < mLayout.getChildCount(); j++) {
//            if (v.getId() == mLayout.getChildAt(j).getId()) {
//                break;
//            }
//        }
//
//        // for(int k = 0; k < mPaperItems.size();k++){
//        // Utils.outLog(""+ mPaperItems.get(k).mViewId);
//        // }
//
//        currentData = mPaperItems.get(i);
//        childViewData Data = new childViewData();
//
//        if (num >= count) {
//            mLayout.removeView(v);
//            mLayout.addView(v);
//            mPaperItems.remove(i);
//            mPaperItems.addLast(currentData);
//        } else if (num < 0) {
//            mLayout.removeView(v);
//            mLayout.addView(v, 0);
//            mPaperItems.remove(i);
//            mPaperItems.addFirst(currentData);
//        } else {
//            mLayout.removeView(v);
//            mLayout.addView(view, j);
//            mLayout.addView(v, num + 1);
//            mLayout.removeView(view);
//            mPaperItems.remove(i);
//            mPaperItems.add(i, Data);
//            mPaperItems.add(num + 1, currentData);
//            mPaperItems.remove(Data);
//        }
//
//        // for (int k = 0; k < mPaperItems.size(); k++) {
//        // Utils.outLog("" + mPaperItems.get(k).mViewId);
//        // }
//    }

    public LinkedList<childViewData> getPaperItems() {
        return mPaperItems;
    }

    public ScrollView getmScrollView() {
        return mScrollView;
    }

    public void setmScrollView(ScrollView mScrollView) {
        this.mScrollView = mScrollView;
    }

    public void setmMediaPaper(MediaPaper mediaPaper) {
        this.mMediaPaper = mediaPaper;
    }



    public int getCurDragViewSp(View v, int y) {
        return y - v.getTop();
    }

    public int getCurDragViewMarginTop(View v, int sp, int y) {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            if (v.getId() == mLayout.getChildAt(i).getId()) {
                break;
            }
        }

        // mLayout.getChildAt(i-1);

        return 0;
    }

    public void handleScroll(int dirction) {
        mScrollRunnable.setDirection(dirction);
        // mHandler.post(mScrollRunnable);
        mHandler.postDelayed(mScrollRunnable, 500);
    }

    public void handleScrollToPixel(int pixel) {
        mScrollRunnable.setScrollPixel(pixel);
        mHandler.postDelayed(mScrollRunnable, 500);
    }

    public int getScrollState() {
        return mScrollRunnable.getDirection();
    }

    public boolean isScrollTop() {

        if (mScrollView.getScrollY() == 0) {
            return true;
        }

        return false;
    }

    public boolean isScrollBottom() {
        int off = mLayout.getMeasuredHeight() - mScrollView.getHeight();

        if (mScrollView.getScrollY() == off) {
            return true;
        }
        return false;
    }

    private class ScrollRunnable implements Runnable {
        private int mDirection;
        private int mPixel;

        ScrollRunnable() {
        }

        public void run() {
            if (mDirection == PaperUtils.SCROLL_BOTTOM) {
//                int off = mLayout.getMeasuredHeight() - mScrollView.getHeight();
//                if (off > 0) {
//                    mScrollView.scrollTo(0, off);
//                }
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            } else if (mDirection == PaperUtils.SCROLL_DOWN) {
                // mScrollView.scrollTo(0, mScrollView.getScrollY() + 10);
                mScrollView.scrollBy(0, 8);
                int off = mLayout.getMeasuredHeight() - mScrollView.getHeight();
//				Utils.outLog(""+mScrollView.getScrollY() + " "+off);
                // mHandler.post(this);
//				 mHandler.postDelayed(this, 1000);
            } else if (mDirection == PaperUtils.SCROLL_UP) {
                mScrollView.scrollBy(0, -8);
//				Utils.outLog(""+mScrollView.getScrollY());
//				mHandler.postDelayed(this, 1000);
                // mHandler.post(this);
                // mScrollView.scrollTo(0, mScrollView.getScrollY() - 10);
            } else if (mDirection == PaperUtils.SCROLL_STOP) {
                mHandler.removeCallbacks(mScrollRunnable);
            } else if (mDirection == PaperUtils.SCROLL_TOP) {
//                mScrollView.scrollTo(0, -100);
                mScrollView.fullScroll(ScrollView.FOCUS_UP);
            } else if (mDirection == PaperUtils.SCROLL_PIXEL) {
                mScrollView.scrollTo(0, mPixel);
            }
        }

        void setDirection(int direction) {
            mDirection = direction;
        }

        int getDirection() {
            return mDirection;
        }

        void setScrollPixel(int pixel) {
            mDirection = PaperUtils.SCROLL_PIXEL;
            mPixel = pixel;
        }
    }

    public void handleViewTrans(View v, int y, boolean status) {
        if (!status) {
            mHandler.removeCallbacks(mViewTransRunnable);
            return;
        }
        mViewTransRunnable.setParam(v, y);
        mHandler.post(mViewTransRunnable);

        new Thread();

    }

    private class ViewTransRunnable implements Runnable {
        private View v;
        private int x, y;

        @Override
        public void run() {
            // getPositonByView(v, y);
        }

        void setParam(View v, int y) {
            this.v = v;
            this.y = y;
        }
    }

    void onDestroy() {
        viewId = 0x01;
    }

//	private class addEditViewForPreviewRunnable implements Runnable {
//		View v;
//
//		addEditViewForPreviewRunnable() {
//		}
//
//		public void run() {
//			// Utils.outLog("============================");
//			// v = addEditViewForPreview(mContext);
//		}
//
//		void setText(CharSequence text) {
//			((EditText) v).setText(text);
//		}
//	}

    public void mergeEditTextForTollbarDrag(int pos) {
        if (pos > 0) {
            View v1, v2;
            v1 = mLayout.getChildAt(pos - 1);
            v2 = mLayout.getChildAt(pos);
            // Utils.outLog("==================" + i);
            if (v1 instanceof EditText && v2 instanceof EditText) {
                ((EditText) v1).getText().append("\n");
                ((EditText) v1).getText().append(((EditText) v2).getText());
                removeViewFromList(v2, PaperUtils.DRAG_ACTION_VIEW, false);
            }
        }
    }

    /*
     *
     * ldp end
     */
    // //////////////////////pp add////////////////////
    // private ArrayList<SketchHold> mSketchList = new ArrayList<SketchHold>();
    private ArrayList<View> mNeedFocusList = new ArrayList<View>();

    public void addToNeedFocusViewList(View view) {
        if (view != null)
            mNeedFocusList.add(view);
    }

    public void removeViewFromNeedFocusViewList(View view) {
        if (mNeedFocusList.size() > 0) {
            int id = view.getId();
            for (int i = 0; i < mNeedFocusList.size(); i++) {
                if (mNeedFocusList.get(i).getId() == id) {
                    mNeedFocusList.remove(i);
                    break;
                }
            }
        }
    }

    public View getViewFromNeedFocusViewListById(int id) {
        if (mNeedFocusList.size() > 0) {
            for (View view : mNeedFocusList) {
                if (view.getId() == id)
                    return view;
            }
        }
        return null;
    }

//    public View getCurrentFocusViewFromNeedFocusViewList() {
//        if (mNeedFocusList.size() > 0) {
//            for (View view : mNeedFocusList) {
//                if (view instanceof ImageHold
//                        && ((ImageHold) view).getFocus()) {
//                    return view;
//                }
//            }
//        }
//        return null;
//    }


//    public void needFocusViewRequestFocusById(int id) {
//        boolean bViewFocusMode = false;
//        boolean bSketchView = false;
//        if (mNeedFocusList.size() > 0) {
//            for (View view : mNeedFocusList) {
//                if (view.getId() == id && view instanceof ImageHold
//                        && !((ImageHold) view).getFocus()) {
//                    ((ImageHold) view).setFocus(true);
//                    view.setSelected(true);
//                    bViewFocusMode = true;
//                } else if (view instanceof ImageHold
//                        && ((ImageHold) view).getFocus()) {
//                    ((ImageHold) view).setFocus(false);
//                    view.setSelected(false);
//                }
//            }
//        }
//        mMediaPaper.sketchSetScrollViewScrollableOrNot(
//                !bViewFocusMode, bSketchView);
//    }

//    public void needFocusViewClearFocusById(int id) {
//        boolean bViewFocusMode = false;
//        boolean bSketchView = false;
//        if (mNeedFocusList.size() > 0) {
//            for (View view : mNeedFocusList) {
//                if (view.getId() == id && view instanceof ImageHold
//                        && ((ImageHold) view).getFocus()) {
//                    ((ImageHold) view).setFocus(false);
//                    view.setSelected(false);
//                } else if (view instanceof ImageHold
//                        && ((ImageHold) view).getFocus()) {
//                    bViewFocusMode = true;
//                    view.setSelected(true);
//                }
//            }
//        }
//        mMediaPaper.sketchSetScrollViewScrollableOrNot(
//                !bViewFocusMode, bSketchView);
//    }

    public void ClearAllEditViewFocus() {

    }

//    public void ClearAllNeedFocusViewFocus() {
//        boolean bViewFocusMode = false;
//        boolean bSketchView = false;
//        if (mNeedFocusList.size() > 0) {
//            for (View view : mNeedFocusList) {
//                if (view instanceof ImageHold
//                        && ((ImageHold) view).getFocus()) {
//                    ((ImageHold) view).setFocus(false);
//                    view.setSelected(false);
//                }
//            }
//        }
//        mMediaPaper.sketchSetScrollViewScrollableOrNot(
//                !bViewFocusMode, bSketchView);
//    }

//    public boolean isViewFocusMode() {
//        boolean bViewFocusMode = false;
//        if (mNeedFocusList.size() > 0) {
//            for (View view : mNeedFocusList) {
//                if ((view instanceof ImageHold && ((ImageHold) view)
//                        .getFocus())) {
//                    bViewFocusMode = true;
//                }
//            }
//        }
//        return bViewFocusMode;
//
//    }

    // ///////////////////////pp end////////////////////

    /*********************
     * wm add
     ****************************/
    private ArrayList<Integer> mMediaList = new ArrayList<Integer>();

    public void addToMediaList(int id) {
        mMediaList.add(id);
    }

    public void removeFromMediaList(int id) {
        if (!mMediaList.isEmpty()) {
            for (int i = 0; i < mMediaList.size(); i++) {
                if (id == mMediaList.get(i)) {
                    View v = getDataByViewId(mMediaList.get(i));
                    mMediaList.remove(i);
                    if (v instanceof AudioView) {
                        ((AudioView) v).stopPlayAndRecord();
                    }
                    break;
                }
            }
        }
    }

    public void stopOthersExceptId(int id) {
        if (!mMediaList.isEmpty()) {
            for (int i = 0; i < mMediaList.size(); i++) {
                if (id != mMediaList.get(i)) {
                    View v = getDataByViewId(mMediaList.get(i));
                    if (v instanceof AudioView) {
                        ((AudioView) v).stopPlayAndRecord();
                    }
                }
            }
        }
    }

    public void stopAll() {
        if (!mMediaList.isEmpty()) {
            for (int i = 0; i < mMediaList.size(); i++) {
                View v = getDataByViewId(mMediaList.get(i));
                if (v instanceof AudioView) {
                    ((AudioView) v).stopPlayAndRecord();
                }

            }
        }
    }


//    public void clearMediaList() {
//        if (!mMediaList.isEmpty())
//            mMediaList.clear();
//    }

    /* for single drag */
//    public void resetMargin(View dragView, int dragViewHeight, int userSp) {
//	   if (userSp < (Utils.EDIT_LINE_HEIGHT >> 1))
//	      return;
//		int curIndex = -1;
//		View next = null;
//		View tempview = null;
//		LinearLayout.LayoutParams tempLp = null;
//		for (int j = 0; j < mLayout.getChildCount(); j++) {
//			tempview = mLayout.getChildAt(j);
//			tempLp = (LinearLayout.LayoutParams) tempview.getLayoutParams();
//			if (tempLp.bottomMargin != Utils.bottomMargin) {
//				tempLp.bottomMargin = Utils.bottomMargin;
//				tempview.setLayoutParams(tempLp);
//			}
//			if (dragView.getId() == tempview.getId()) {
//				curIndex = j;
//			}
//		}
//		if (curIndex < 0)
//		   return;
//		userSp = userSp > Utils.bottomMargin ? userSp - Utils.bottomMargin : 0;
//		int empLineNum = userSp / Utils.EDIT_LINE_HEIGHT;
////		userSp = userSp % Utils.EDIT_LINE_HEIGHT;
////		empLineNum = userSp < (Utils.EDIT_LINE_HEIGHT >> 1) ? empLineNum : empLineNum + 1;
//		View prevView = null;
//		if (curIndex > 0) {
//		   prevView = mLayout.getChildAt(curIndex - 1);
//		}
//		if (prevView != null && (prevView instanceof EditText)) {
//		   for(int i = 0; i < empLineNum; i++) {
//		      ((EditText)prevView).append("\n");
//		   }
//		} else {
//		   createNewEditText(curIndex);
//		   EditText tv = (EditText) mLayout.getChildAt(curIndex);
//		   for(int i = 1; i < empLineNum; i++) {
//		      tv.append("\n");
//         }
//		}
//    }

    public void removeFile(childViewData data) {
        if (!data.mViewName.equals(PaperUtils.EDITVIEW)) {
            String path = data.mViewData;
            if (path == null) {
                return;
            }
            File temp = new File(path);
            if (temp != null && temp.exists() && !temp.isDirectory()) {
                if (path.contains(mMediaPaper.getPathName())) {
                    boolean rtn = temp.delete();
//                    if (data.mViewName.equals(PaperUtils.IMAGEVIEW)) {
//
//                        String str = path;
//                        if (path != null && path.contains(Utils.ROOT_PATH)) {
//                            str = path.substring(Utils.ROOT_PATH.length());
//                        }
//                        Utils.getDiaryDB(mContext).deleteImageByPath(str);
//                    }
                }
            }
        }
    }


    public void EnableOrDisableAllChild(boolean bEnable) {

        mMediaPaper.getTitleView().setFocusableInTouchMode(bEnable);
        mMediaPaper.getTitleView().setFocusable(bEnable);
        mMediaPaper.getTitleView().setEnabled(bEnable);
//	   
//	 mMediaPaper.getUserTitleView().setFocusable(bEnable);
        //   mMediaPaper.getUserTitleView().setEnabled(bEnable);
//        for (int i = 0; i < mLayout.getChildCount(); i++) {
//            View v = mLayout.getChildAt(i);
//            if (v instanceof EditText) {
//                v.setFocusable(bEnable);
//                v.setEnabled(bEnable);
//            } else if (v instanceof ImageHold) {
//                ((ImageHold) v).setFocus(false);
//                v.setSelected(false);
//            }
//        }
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View v = mLayout.getChildAt(i);
            if (v instanceof Textbox) {
                ((Textbox)v).setEditMode(bEnable);
            }
        }

        mMediaPaper.sketchSetScrollViewScrollableOrNot(true, false);
    }

    public boolean getFromCam() {
        return mFromCam;
    }

    public void setFromCam(boolean fromcam) {
        mFromCam = fromcam;
    }


    public int getCamImageCount() {
        return mMediaCount;
    }

    public int getPaperBottom() {

        return mLayout.getChildAt(mLayout.getChildCount() - 1).getBottom();
    }


    public void setAudioSeekbarTrackingTouchListener(AudioBarTrackingTouchListener l) {
        mAudioBarTrackingTouchListener = l;
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View v = mLayout.getChildAt(i);
            if (v instanceof AudioView) {
                ((AudioView) v).setSeekbarTrackingTouchListener(l);
            }
        }
    }

//    public void setVideoSeekbarTrackingTouchListener(VideoBarTrackingTouchListener l) {
//        mVideoBarTrackingTouchListener = l;
//        for (int i = 0; i < mLayout.getChildCount(); i++) {
//            View v = mLayout.getChildAt(i);
//            if (v instanceof VideoPlay) {
//                ((VideoPlay) v).setSeekbarTrackingTouchListener(l);
//            }
//        }
//    }

    public boolean isEditMode() {
        return mMediaPaper.isEditMode();
    }

//    public void releaseAllImages() {
//        for (int i = 0; i < mLayout.getChildCount(); i++) {
//            View v = mLayout.getChildAt(i);
//            if (v instanceof ImageHold) {
//                Bitmap bmp = ((ImageHold) v).getImageBitmap();
//                ((ImageHold) v).setImageBitmap(null);
//                if (bmp != null) {
//                    bmp.recycle();
//                }
//            }
//        }
//    }

    private class UpdateData {
        int viewId = -1;
        Bitmap bmp = null;
    }

//    private class RestoreImageTask extends AsyncTask<Void, UpdateData, Void> {
//
//        protected Void doInBackground(Void... params) {
//            for (int i = 0; i < mPaperItems.size(); i++) {
//                childViewData data = mPaperItems.get(i);
//                if (data.mViewName.equals(PaperUtils.IMAGEVIEW)) {
//                    Bitmap bmp = PaperUtils.loadBitmap(data.mViewData, PaperUtils.IMAGE_LONG_SIZE, 0, 0);
//                    UpdateData temp = new UpdateData();
//                    temp.bmp = bmp;
//                    temp.viewId = data.mViewId;
//                    publishProgress(temp);
//                }
//            }
//            return null;
//        }
//
//        protected void onProgressUpdate(UpdateData... values) {
//            UpdateData data = values[0];
//            View child = mLayout.findViewById(data.viewId);
//            if (child instanceof ImageHold) {
//                Bitmap bmp = ((ImageHold) child).getImageBitmap();
//                if (bmp == null) {
//                    ((ImageHold) child).setImageBitmap(data.bmp);
//                }
//            }
//            super.onProgressUpdate(values);
//        }
//    }


    public void restoreImageBitmap() {
        if (mLayout == null) {
            return;
        }
//      boolean bNeedRestore = false;
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
//            if (child instanceof ImageHold) {
//                Bitmap bmp = ((ImageHold) child).getImageBitmap();
//                if (bmp == null) {
////               bNeedRestore = true;
//                    childViewData data = getDataByItemId(child.getId());
//                    bmp = PaperUtils.loadBitmap(data.mViewData, PaperUtils.IMAGE_LONG_SIZE, 0, 0);
//                    ((ImageHold) child).setImageBitmap(bmp);
//                }
//            }
        }
    }

    public void updateForPathNameChange(String oldPath, String newPath) {
        mPaperSavePath = newPath;
        for (int i = 0; i < mPaperItems.size(); i++) {
            childViewData itemData = mPaperItems.get(i);
            if (!PaperUtils.EDITVIEW.equals(itemData.mViewName)) {
                int strIndex = itemData.mViewData.indexOf(oldPath);
                if (strIndex >= 0) {
                    strIndex = strIndex + oldPath.length();
                    if (strIndex < itemData.mViewData.length()) {
                        String tempViewData = itemData.mViewData.substring(strIndex);
                        itemData.mViewData = newPath + tempViewData;
                    }
                }
            }
        }
    }

    public String getDstFilePath(String srcFilePath, String viewName) {
        int i = srcFilePath.lastIndexOf(".");

        String filename = srcFilePath.substring(i, srcFilePath.length());
        long dateTaken = System.currentTimeMillis();
        filename = Long.toString(dateTaken) + filename;
        String newPath = null;
        if (PaperUtils.VIDEOVIEW.equals(viewName)) {
            newPath = mMediaPaper.getPathName()
                    + PaperUtils.SUB_VIDEO + File.separator + filename;
        } else if (PaperUtils.RECORDVIEW.equals(viewName)) {
            newPath = mMediaPaper.getPathName()
                    + PaperUtils.SUB_RECORD + File.separator + filename;
        }
        return newPath;
    }

    public void releaseShowThumbnail() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
            if (child instanceof MediaFrame) {
                ((MediaFrame)child).releaseBitmap();
            }
        }
    }

    public void restoreShowThumbnail() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
            if (child instanceof MediaFrame) {
                ((MediaFrame)child).restoreBitmap();
            }
        }
    }

    public void editTextShowBackground() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
            if (child instanceof MyEditText) {
                ((MyEditText)child).setBackgroundResource(R.drawable.text_frame);
            }
        }
    }

    public void editTextHideBackground() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
            if (child instanceof MyEditText) {
                ((MyEditText)child).setBackgroundResource(0);
            }
        }
    }


    public class MediaItem {
        int id;
        String mResourceType = null;
        String mThumbCache = null;
        String mResourceUrl = null;
        String mWebplayUrl = null;
        String mTitle = null;
        int mScreenType = 0;
    }

    public ArrayList<MediaItem> getMediasList() {
        ArrayList<MediaItem> mediaList = null;
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
            if (child instanceof MediaFrame && ((MediaFrame)child).getResourceType().equals( PaperUtils.IMAGEVIEW)) {
                if (mediaList == null) {
                    mediaList = new ArrayList<MediaItem>();
                }
                MediaItem media = new MediaItem();
                media.id = child.getId();
                media.mResourceType = ((MediaFrame)child).getResourceType();
                media.mThumbCache = ((MediaFrame)child).getThumbCachePath();
                media.mResourceUrl = ((MediaFrame)child).getResourcePath();
                media.mTitle = ((MediaFrame)child).getResourceTitle();
                mediaList.add(media);
            }
        }
        return mediaList;
    }



//    private void hideSoftInput(View view) {
//        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    private BaseChild.DeleteHandler mChildDeleteHandler = new BaseChild.DeleteHandler() {
        @Override
        public void delete(BaseChild view) {
            removeViewFromList(view, PaperUtils.DRAG_ACTION_VIEW, false);
            mbEdit = true;
        }
    };

    private boolean mbDeleteMode = false;

    public boolean isDeleteMode() {
        return mbDeleteMode;
    }

    public void setDeleteMode(boolean bDeleteMode) {
        mbDeleteMode = bDeleteMode;


        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View v = mLayout.getChildAt(i);
            if (v instanceof BaseChild) {
                ((BaseChild)v).setDeleteMode(mbDeleteMode);
            }
        }
    }

    public void setEdit(boolean bEdit) {
        mbEdit = bEdit;
    }

    public boolean isEdit() {
        return mbEdit;
    }


    public void clearAll() {
        if (mLayout != null) {
            mLayout.removeAllViews();
        }
        viewId = 0x01;
        mbDeleteMode = false;
    }
}
