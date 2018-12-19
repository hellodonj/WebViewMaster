package com.galaxyschool.app.wawaschool.slide;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper.SlideSaveBtnParam;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.osastudio.common.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadDialog extends Dialog {
    private Context mContext = null;
    private UploadDialogHandler mDialogHandler = null;
    private String mCoursePath = null;

    private ContainsEmojiEditText mTitleEdit = null;
    private ContainsEmojiEditText mContentEdit = null;
    private View rootView = null;
    private View mDiscardBtn = null;
    private View mDraftBtn = null;
    private View mUploadBtn = null;
    private boolean mIsLongHomework = false;

    private String mTitle = null;
    private String mContent = null;
    private boolean mbShowUpload = true;
    private SlideSaveBtnParam mButtonParam;

    public UploadDialog(
        Context context, String title, String content, boolean isLongHomework,
        UploadDialogHandler handler) {
        super(context, R.style.Theme_PageDialog);
        // TODO Auto-generated constructor stub
        mContext = context;
        mTitle = title;
        mContent = content;
        mIsLongHomework = isLongHomework;
        mDialogHandler = handler;
    }

    public UploadDialog(
            Context context, String title, String content, boolean isLongHomework,
            UploadDialogHandler handler, boolean bShowUpload) {
        super(context, R.style.Theme_PageDialog);
        // TODO Auto-generated constructor stub
        mContext = context;
        mTitle = title;
        mContent = content;
        mIsLongHomework = isLongHomework;
        mDialogHandler = handler;
        mbShowUpload = bShowUpload;
    }
    
    public UploadDialog(
            Context context, String title, String content, boolean isLongHomework,
            UploadDialogHandler handler, SlideSaveBtnParam buttonParam) {
        super(context, R.style.Theme_PageDialog);
        // TODO Auto-generated constructor stub
        mContext = context;
        mTitle = title;
        mContent = content;
        mIsLongHomework = isLongHomework;
        mDialogHandler = handler;
        mButtonParam = buttonParam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_view);
        setupViews();
        resizeDialog((Activity) mContext,this,0.9f);
    }

    private String formatDataString() {
        long milliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(milliseconds));
    }

    private void setupViews() {
//        View baseLayout = findViewById(R.id.base_layout);
//        if (baseLayout != null) {
//            LayoutParams lp = baseLayout.getLayoutParams();
//            if (lp != null) {
//                lp.width = MyApplication.getWPixels() / 2;
//            }
//            baseLayout.setLayoutParams(lp);
//        }
        rootView = findViewById(R.id.base_layout);
        mTitleEdit = (ContainsEmojiEditText) findViewById(R.id.title_edit);
        mTitleEdit.setMaxlen(Constants.MAX_TITLE_LENGTH);
        mTitleEdit.setFilters(new InputFilter[] {
        		new InputFilter() {

        			@Override
        			public CharSequence filter(CharSequence source, int start, int end,
        					Spanned dest, int dstart, int dend) {
        				// TODO Auto-generated method stub
        				for (int i = start; i < end; i ++) {
        					if (source.charAt(i) == File.separator.charAt(0)) {
        						return "";
        					}
        				}
        				return null;
        			}
        		}
        });
        mContentEdit = (ContainsEmojiEditText) findViewById(R.id.content_edit);
        mDiscardBtn = findViewById(R.id.discard_btn);
        mDraftBtn = findViewById(R.id.draft_btn);
        mUploadBtn = findViewById(R.id.commit_btn);
        if (mButtonParam != null) {
        	((TextView)mDraftBtn).setText(R.string.save);
        	if (!mButtonParam.mIsShowGiveup) {
        		mDiscardBtn.setVisibility(View.GONE);
			}
        	if (!mButtonParam.mIsShowSave) {
				mDraftBtn.setVisibility(View.GONE);
			}
        	if (!mButtonParam.mIsShowSend) {
				mUploadBtn.setVisibility(View.GONE);
			}
		} else {
			if (!mbShowUpload) {
				if (mDraftBtn!= null) {
					((TextView)mDraftBtn).setText(R.string.save);
				}
			} else {
				((TextView)mDraftBtn).setVisibility(View.GONE);
			}
		}

        if (!TextUtils.isEmpty(mTitle)) {
            mTitleEdit.setText(mTitle);
            int length = mTitleEdit.length();
            if (length > 0) {
                mTitleEdit.setSelection(length);
            }
        }
        if (!TextUtils.isEmpty(mContent)) {
            mContentEdit.setText(mContent);
        }

        if (mDiscardBtn != null) {
            mDiscardBtn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        if (mDialogHandler != null) {
                            mDialogHandler.discard();
                        }
                        UploadDialog.this.dismiss();
                    }
                });
        }
        if (mDraftBtn != null) {
            mDraftBtn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        //保存
                        String title = mTitleEdit.getEditableText().toString().trim();
                        if (TextUtils.isEmpty(title)){
                            TipsHelper.showToast(mContext,
                                    mContext.getString(R.string.pls_input_title));
                            return;
                        }
                        if (mDialogHandler != null) {
                            mDialogHandler.saveDraft(
                                mTitleEdit.getEditableText()
                                    .toString(), mContentEdit.getEditableText()
                                    .toString());
                        }
                        UploadDialog.this.dismiss();
                    }
                });
        }
        if (mUploadBtn != null) {
            mUploadBtn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        //发送
                        String title = mTitleEdit.getEditableText().toString().trim();
                        if (TextUtils.isEmpty(title)){
                            TipsHelper.showToast(mContext,
                                    mContext.getString(R.string.pls_input_title));
                            return;
                        }
                        if (mDialogHandler != null) {
                            mDialogHandler.upload(
                                mTitleEdit.getEditableText()
                                    .toString(), mContentEdit.getEditableText()
                                    .toString());
                        }
                        UploadDialog.this.dismiss();
                    }
                });
        }

    }
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mTitleEdit!=null&&mContentEdit!=null){
            if (mTitleEdit.hasFocus()){
                imm.hideSoftInputFromWindow((mTitleEdit.getWindowToken()), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (mContentEdit.hasFocus()){
                imm.hideSoftInputFromWindow((mContentEdit.getWindowToken()), InputMethodManager
                        .HIDE_NOT_ALWAYS);
            }
        }

    }
    public EditText getEditText() {
        return mTitleEdit;
    }

    public interface UploadDialogHandler {
        public void saveDraft(String title, String content);

        public void upload(String title, String content);

        public void discard();
    }
    private void resizeDialog(Activity activity, Dialog dialog, float ratio) {
        Window window = dialog.getWindow();
        WindowManager m = ((Activity)mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        window.setGravity(Gravity.CENTER);
        p.width = (int) (d.getWidth()*ratio);
        window.setAttributes(p);
    }
}
