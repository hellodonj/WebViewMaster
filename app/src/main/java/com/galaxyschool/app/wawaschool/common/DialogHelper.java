package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

/**
 * Created by shouyi on 2015/5/7. 全局对话框的帮忙类，目前主是针对无网络对话框和加载中对话框
 */
public class DialogHelper {

	static DialogHelper helper;

	static Activity theContext;

	public static DialogHelper getIt(Activity activity) {

		theContext = activity;

		if (helper == null) {
			helper = new DialogHelper();
		}
		return helper;
	}

	public LoadingDialog GetLoadingDialog(int type) {
		if (type == 1) {
			LoadingDialog dialog = new LoadingDialog(theContext, R.style.CustomDialogLoding);
			dialog.show();
			Window window = dialog.getWindow();
			WindowManager m = theContext.getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
			window.setGravity(Gravity.TOP);
			p.width = d.getWidth();
			p.height = d.getHeight() - 110;
			// theContext.getResources().getDimensionPixelSize(R)
			window.setAttributes(p);
			return dialog;
		} else {
			LoadingDialog dialog = new LoadingDialog(theContext, R.style.CustomDialogLoding);
			dialog.show();
			Window window = dialog.getWindow();
			WindowManager m = theContext.getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
			p.width = d.getWidth(); // 宽度设置为屏幕的0.65
			p.height = d.getHeight(); // 宽度设置为屏幕的0.65
			window.setAttributes(p);
			return dialog;
		}
	}
	
	public WarningDialog getWarningDialog() {
		WarningDialog dialog = new WarningDialog(theContext, R.style.normal_dialog);
		dialog.show();
		Window window = dialog.getWindow();
		WindowManager m = theContext.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
//		window.setGravity(Gravity.TOP);
		p.width = (int) (d.getWidth() * 0.85);
//		p.height = d.getHeight() - 110;
		// theContext.getResources().getDimensionPixelSize(R)
		window.setAttributes(p);
		return dialog;
	}

	public OneButtonDialog getOneButtonDialog() {
		OneButtonDialog dialog = new OneButtonDialog(theContext, R.style.normal_dialog);
		dialog.show();
		Window window = dialog.getWindow();
		WindowManager m = theContext.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
		//		window.setGravity(Gravity.TOP);
		p.width = (int) (d.getWidth() * 0.5);
		//		p.height = d.getHeight() - 110;
		// theContext.getResources().getDimensionPixelSize(R)
		window.setAttributes(p);
		return dialog;
	}

	public CollectDialog getCollectDialog(){
		CollectDialog dialog = new CollectDialog(theContext, R.style.normal_dialog);
		dialog.show();
		Window window = dialog.getWindow();
		WindowManager m = theContext.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.8);
		window.setAttributes(p);
		return dialog;
	}
	public class LoadingDialog extends Dialog {

		Animation operatingAnim;
		Context theContext;

		public LoadingDialog(Context context) {
			super(context, R.style.CustomDialogLoding);
			theContext = context;
		}

		private LoadingDialog(Context context, int theme) {
			super(context, theme);
			theContext = context;
		}
		
		public void setContent(String content) {
			((TextView) findViewById(R.id.load_content)).setText(content);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_loading);

			operatingAnim = AnimationUtils.loadAnimation(theContext, R.anim.image_rotate);
			LinearInterpolator lin = new LinearInterpolator();
			operatingAnim.setInterpolator(lin);

			ImageView imageViewLoading = (ImageView) findViewById(R.id.imageViewLoading);
			imageViewLoading.startAnimation(operatingAnim);

		}
	}
	
	public class WarningDialog extends Dialog {

		Animation operatingAnim;
		Context theContext;

		public WarningDialog(Context context) {
			super(context, R.style.CustomDialogLoding);
			theContext = context;
		}

		private WarningDialog(Context context, int theme) {
			super(context, theme);
			theContext = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_warning);
		}
		
		public void setContent(int id) {
			((TextView) findViewById(R.id.content)).setText(id);
		}
		
		public void setContent(String content) {
			((TextView) findViewById(R.id.content)).setText(content);
		}
		
		public void setButtonNames(int cancelName, int confirmName) {
			((TextView) findViewById(R.id.cancel)).setText(cancelName);
			((TextView) findViewById(R.id.confirm)).setText(confirmName);
		}
		
		public void setOnClickListener(View.OnClickListener listener) {
			findViewById(R.id.cancel).setOnClickListener(listener);
			findViewById(R.id.confirm).setOnClickListener(listener);
		}


		public void setCancelButtonVisible(boolean visible){
			if (visible){
				((TextView) findViewById(R.id.cancel)).setVisibility(View.VISIBLE);
			}else {
				((TextView) findViewById(R.id.cancel)).setVisibility(View.GONE);
				((ImageView)findViewById(R.id.iv_line)).setVisibility(View.GONE);
			}
		}
	}
	public class CollectDialog extends Dialog{
		Animation operatingAnim;
		Context theContext;

		public CollectDialog(Context context) {
			super(context, R.style.CustomDialogLoding);
			theContext = context;
		}

		private CollectDialog(Context context, int theme) {
			super(context, theme);
			theContext = context;
		}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.layout_pop_save);
		}
		public void setOnClickListener(View.OnClickListener listener) {
			findViewById(R.id.show_collect_text).setOnClickListener(listener);
		}
	}

	public class OneButtonDialog extends Dialog {

		Animation operatingAnim;
		Context theContext;

		public OneButtonDialog(Context context) {
			super(context, R.style.CustomDialogLoding);
			theContext = context;
		}

		private OneButtonDialog(Context context, int theme) {
			super(context, theme);
			theContext = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_one_button);
		}

		public void setContent(int id) {
			((TextView) findViewById(R.id.content)).setText(id);
		}

		public void setContent(String content) {
			((TextView) findViewById(R.id.content)).setText(content);
		}

		public void setButtonNames( int confirmName) {
			((TextView) findViewById(R.id.confirm)).setText(confirmName);
		}

		public void setOnClickListener(View.OnClickListener listener) {
			findViewById(R.id.confirm).setOnClickListener(listener);
		}
	}
}
