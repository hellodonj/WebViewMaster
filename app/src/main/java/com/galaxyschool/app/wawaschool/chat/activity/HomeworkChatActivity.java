package com.galaxyschool.app.wawaschool.chat.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.DialogHelper.LoadingDialog;
import com.galaxyschool.app.wawaschool.config.AppSettings;

public class HomeworkChatActivity extends ChatActivity {
	LoadingDialog mLoadingDialog;
    public static final String TAG = HomeworkChatActivity.class.getSimpleName();

    public static final String EXTRA_SENDER_NAME = "senderName";
    public static final String EXTRA_SENDER_AVATAR = "senderAvatar";
    public static final String EXTRA_SEND_TIME = "sendTime";
    public static final String EXTRA_THUMBNAIL = "thumbnail";
    public static final String EXTRA_HOMEWORK_ID= "homeworkId";
    public static final String EXTRA_HOMEWORK_TYPE = "homeworkType";
    public static final String EXTRA_HOMEWORK_DATA = "homeworkData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra(ChatActivity.EXTRA_LAYOUT_ID, R.layout.chat_homework);
        super.onCreate(savedInstanceState);

        initViews();
    }

    void initViews() {
        ImageView imageView = (ImageView) findViewById(R.id.contacts_item_icon);
        if (imageView != null) {
            MyApplication.getThumbnailManager(this).displayThumbnail(
                    AppSettings.getFileUrl(getIntent().getStringExtra(
                            EXTRA_SENDER_AVATAR)), imageView);
        }

        View view = findViewById(R.id.contacts_list_item_layout);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    ImageView imageView = (ImageView) v.findViewById(R.id.contacts_item_arrow);
                    imageView.setImageResource(v.isSelected() ?
                            R.drawable.list_exp_down : R.drawable.list_exp_up);
                    findViewById(R.id.topic_image).setVisibility(v.isSelected() ?
                            View.VISIBLE : View.GONE);
                }
            });
        }
        TextView textView = (TextView) findViewById(R.id.contacts_item_title);
        if (textView != null) {
            textView.setText(getIntent().getStringExtra(EXTRA_SENDER_NAME));
        }
        textView = (TextView) findViewById(R.id.contacts_item_subtitle);
        if (textView != null) {
            textView.setText(getIntent().getStringExtra(EXTRA_SEND_TIME));
        }
        imageView = (ImageView) findViewById(R.id.contacts_item_arrow);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.list_exp_down);
        }
        imageView = (ImageView) findViewById(R.id.topic_image);
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHomework();
                }
            });
            MyApplication.getThumbnailManager(this).displayThumbnail(
                    AppSettings.getFileUrl(getIntent().getStringExtra(
                            EXTRA_THUMBNAIL)), imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }
    
    private void openHomework() {
//    	MTask data = (MTask) getIntent().getSerializableExtra(EXTRA_HOMEWORK_DATA);
//    	if (data == null) {
//			return;
//		}
//    	if (data.ResourceType == ResourceType.CHW) {
//			showLoadingDialog(getString(R.string.cs_loading_wait), true);
//			OpenCHWParam param = new OpenCHWParam(this,  data.MicroID,
//					data.Resourceurl, data.getShareParams(this), false,
//					new CallbackListener() {
//
//						@Override
//						public void onBack(Object result) {
//							// TODO Auto-generated method stub
//							if (this != null) {
//								dismissLoadingDialog();
//								if (!(Boolean) result) {
//									TipMsgHelper.ShowMsg(HomeworkChatActivity.this, getString(R.string.cs_loading_error));
//								}
//							}
//						}
//					});
//            param.collectParams = data.getCollectParams();
//			param.needCommitReadCount = true;
//			param.type = ReadSourceType.HOMEWORK;
//			CreateSlideHelper.loadAndOpenSlideByCHWUrl(param);
//		} else if (data.ResourceType == ResourceType.VIDEO) {
//			CourseInfo courseInfo = data.getCourseInfo();
//			SetHasReadParam param = new SetHasReadParam();
//			param.mId = data.getTaskSenderId();
//			param.mType = ReadSourceType.HOMEWORK;
//			ActivityUtils.playCourse(this, courseInfo, param);
//		} else if (data.ResourceType == ResourceType.WEB) {
//
//		} else if(data.ResourceType == ResourceType.NOTE) {
//            ActivityUtils.openNote(HomeworkChatActivity.this, data.Resourceurl, data.getCourseInfo());
//        }
    }
    
    public Dialog showLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			return mLoadingDialog;
		}
		mLoadingDialog = DialogHelper.getIt(this).GetLoadingDialog(0);
		return mLoadingDialog;
	}
	
    public Dialog showLoadingDialog(String content, boolean cancelable) {
		Dialog dialog = showLoadingDialog();
		((LoadingDialog) dialog).setContent(content);
		dialog.setCancelable(cancelable);
		return dialog;
	}

	public void dismissLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}
	
    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

}
