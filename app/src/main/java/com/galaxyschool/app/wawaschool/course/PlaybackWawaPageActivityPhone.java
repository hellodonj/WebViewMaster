package com.galaxyschool.app.wawaschool.course;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.ResType;

/**
 * @author 作者 shouyi
 * @version 创建时间：Mar 29, 2016 11:00:52 AM 类说明
 */
public class PlaybackWawaPageActivityPhone extends PlaybackActivityPhone {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onSelfCreate() {
		// TODO Auto-generated method stub
		if (taskMarkParam != null) {
			super.onSelfCreate();
		} else {
			setupPlaybackActivityHandler(this);
		}
	}

	@Override
	public void addButtonToAttachedBarHandler(LinearLayout attachedBar) {
		// 课件是受保护的，隐藏侧边工具栏, 允许试看15s
		if (mParam != null && mParam.mIsAuth) {
			attachedBar.setVisibility(View.GONE);
			handler.postDelayed(runnable, PLAY_TIMEOUT_MS);
			return;
		}

		// 隐藏工具栏
		if (mParam != null && mParam.mIsHideToolBar) {
			attachedBar.setVisibility(View.GONE);
			return;
		}
		addMenuOrView(attachedBar);
	}

	protected void addMenuOrView(LinearLayout attachedBar) {
		if (taskMarkParam != null || isAnswerCardQuestion) {
			addMarkView(attachedBar);
		} else {
			addMenu(attachedBar);
		}
	}

	private void addMenu(LinearLayout attachedBar) {
        if(collectParams != null && collectParams.getResourceType()% ResType.RES_TYPE_BASE== ResType
                .RES_TYPE_STUDY_CARD){
            return;
        }
        addMenu(attachedBar, R.string.playbackphone_menu_show,
                R.drawable.menu_icon_horn, R.string.playbackphone_menu_collect);
        addMenu(attachedBar, R.string.playbackphone_menu_share,
                R.drawable.menu_icon_share, 0);
         //根据条件判断是否显示收藏的按钮
        boolean flag = (mParam != null && mParam.mIsHideCollectTip);
        if (!flag) {
            addMenu(attachedBar, R.string.playbackphone_menu_collect,
                    R.drawable.menu_icon_collect, R.string.playbackphone_menu_share);
        }
	}

	protected void addMenu(LinearLayout attachedBar, int name, int icon,
			int preMenuId) {
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

		LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		lParams.gravity = Gravity.CENTER;
		lParams.weight = 1.0f;
		layout.setOnClickListener(this);
		attachedBar.addView(layout, lParams);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean rtn = super.dispatchTouchEvent(event);
		if (mSlideInPlayback != null) {
			return mSlideInPlayback.dispatchTouchEvent(event, false);
		}
		return rtn;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isAnswerCardQuestion){
            showCommitAnswerDialog(true);
		} else if (taskMarkParam != null) {
			super.onBackPressed();
		} else {
			baseOnBackPressed();
		}
	}
}
