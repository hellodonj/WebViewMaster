package com.galaxyschool.app.wawaschool.common;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.FileActivity;
import com.galaxyschool.app.wawaschool.CampusTVPromotionPageActivity;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;

public class WebUtils {

    public static void openWebView(Activity context, String url,
								   Map<String, String> params, String title) {
		if(!TextUtils.isEmpty(url)){
			StringBuilder sb = new StringBuilder(url);
			if (params != null) {
				sb.append("?");
				boolean and = false;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (and) {
						sb.append("&");
					}
					sb.append(entry.getKey()).append("=").append(entry.getValue());
					and = true;
				}

			}
			url = sb.toString();
		}
		Intent it = new Intent();
		it.setClass(context, FileActivity.class);
		it.putExtra(FileActivity.EXTRA_TITLE, title);
		it.putExtra(FileActivity.EXTRA_CONTENT_URL, url);
		context.startActivity(it);
	}

	/**
	 * 控制网页头部显示“X”
	 * @param context
	 * @param url
	 * @param params
	 * @param title
	 * @param showCloseLayout 是否显示关闭布局
     */
	public static void openCommonWebView(Activity context, String url,
								   Map<String, String> params,
								   String title,
								   boolean showCloseLayout) {
		if(!TextUtils.isEmpty(url)){
			StringBuilder sb = new StringBuilder(url);
			if (params != null) {
				sb.append("?");
				boolean and = false;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (and) {
						sb.append("&");
					}
					sb.append(entry.getKey()).append("=").append(entry.getValue());
					and = true;
				}
			}
			url = sb.toString();
		}
		Intent it = new Intent();
		it.setClass(context, FileActivity.class);
		it.putExtra(FileActivity.EXTRA_TITLE, title);
		it.putExtra(FileActivity.EXTRA_CONTENT_URL, url);
		//是否显示关闭布局
		it.putExtra(FileActivity.EXTRA_SHOW_CLOSE_LAYOUT,showCloseLayout);
		context.startActivity(it);
	}

	public static void openWebView(Context context,
								   Map<String, String> params, String title) {
		String url = ServerUrl.WEB_VIEW_NEW_URL;
		StringBuilder sb = new StringBuilder(url);
		if (params != null) {
			sb.append("?");
			boolean and = false;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (and) {
					sb.append("&");
				}
				sb.append(entry.getKey()).append("=").append(entry.getValue());
				and = true;
			}

		}
		url = sb.toString();
		Intent it = new Intent();
		it.setClass(context, FileActivity.class);
		it.putExtra(FileActivity.EXTRA_CONTENT_URL, url);
		it.putExtra(FileActivity.EXTRA_TITLE, title);
		context.startActivity(it);
	}
	public static void openWebView(Activity context, String url,
								   Map<String, String> params, String title, String source,
								   SchoolInfo schoolInfo) {
		if(!TextUtils.isEmpty(url)){
			StringBuilder sb = new StringBuilder(url);
			if (params != null) {
				sb.append("?");
				boolean and = false;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (and) {
						sb.append("&");
					}
					sb.append(entry.getKey()).append("=").append(entry.getValue());
					and = true;
				}

			}
			url = sb.toString();
		}
		Intent it = new Intent();
		it.setClass(context, FileActivity.class);
		it.putExtra(FileActivity.EXTRA_TITLE, title);
		it.putExtra(FileActivity.EXTRA_CONTENT_URL, url);
		it.putExtra(FileActivity.EXTRA_SOURCE, source);
		it.putExtra(FileActivity.EXTRA_SCHOOL_INFO, schoolInfo);
		context.startActivity(it);
	}


	/**
	 *进入校园直播台宣传页
	 * @param context
	 * @param url
	 * @param params
	 * @param title
	 * @param source
     * @param schoolInfo
     */
	public static void enterCampusTVPromotionPageActivity(Activity context, String url,
								   Map<String, String> params, String title, String source,
								   SchoolInfo schoolInfo) {
		if(!TextUtils.isEmpty(url)){
			StringBuilder sb = new StringBuilder(url);
			if (params != null) {
				sb.append("?");
				boolean and = false;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (and) {
						sb.append("&");
					}
					sb.append(entry.getKey()).append("=").append(entry.getValue());
					and = true;
				}

			}
			url = sb.toString();
		}
		Intent it = new Intent();
		it.setClass(context, CampusTVPromotionPageActivity.class);
		it.putExtra(CampusTVPromotionPageActivity.EXTRA_TITLE, title);
		it.putExtra(CampusTVPromotionPageActivity.EXTRA_CONTENT_URL, url);
		it.putExtra(CampusTVPromotionPageActivity.EXTRA_SOURCE, source);
		it.putExtra(CampusTVPromotionPageActivity.EXTRA_SCHOOL_INFO, schoolInfo);
		context.startActivity(it);
	}

	public static String getShareUrl(String url, Map<String, String> params, String title) {
		StringBuilder sb = new StringBuilder(url);
		if (params != null) {
			sb.append("?");
			boolean and = false;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (and) {
					sb.append("&");
				}
				sb.append(entry.getKey()).append("=").append(entry.getValue());
				and = true;
			}

		}
		return sb.toString();
	}
	
}
