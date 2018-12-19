package com.osastudio.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.Collection;

/**
 * @author 作者 shouyi
 * @version 创建时间：Jan 7, 2016 4:24:25 PM
 * 类说明 don't change this class
 * 请不要改变这个类
 */
public class LQImageLoader {
	public static final int OUT_WIDTH = 360;
	public static final int OUT_HEIGHT = 360;
	public static DisplayImageOptions commonOptions;
	public static DisplayImageOptions originOptions;
	public static DisplayImageOptions imageListOptions;

	/**
	 * show image use default output width 360, output height 360
	 * 
	 * 默认通用函数, output width 360, output height 360
	 * 
	 * @param uri
	 * @param imageView
	 */
	public static void displayImage(String uri, ImageView imageView) {
		if (!TextUtils.isEmpty(uri)) {
			if (!uri.startsWith("http")) {
				uri = "file://" + uri;
			}
		}
		ImageLoader.getInstance().displayImage(uri, imageView,
				getCommonOpt());
	}

	/**
	 * show image use default output width 360, output height 360 given default
	 * will show default Icon
	 * 
	 * 默认通用函数, output width 360, output height 360 给出默认图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public static void displayImage(String uri, ImageView imageView,
			int defaultIcon) {
		if (!TextUtils.isEmpty(uri)) {
			if (!uri.startsWith("http")) {
				uri = "file://" + uri;
			}
		}
		ImageLoader.getInstance().displayImage(uri, imageView,
				getOpt(OUT_WIDTH, OUT_HEIGHT, defaultIcon));
	}

	/**
	 * 显示图片, 尺寸最大限制:edgeSizeLimit
	 * 
	 * @param uri
	 * @param imageView
	 * @param edgeSizeLimit
	 * @param defaultIcon
	 */
	public static void displayImage(String uri, ImageView imageView,
			int edgeSizeLimit, int defaultIcon) {
		if (!TextUtils.isEmpty(uri)) {
			if (!uri.startsWith("http")) {
				uri = "file://" + uri;
			}
		}
		ImageLoader.getInstance().displayImage(uri, imageView,
				getOpt(edgeSizeLimit, edgeSizeLimit, defaultIcon));
	}

	/**
	 * show image according to imageView size
	 * 
	 * 根据imageView尺寸解码显示图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public static void displayImageOrigin(String uri, ImageView imageView) {
		if (!TextUtils.isEmpty(uri)) {
			if (!uri.startsWith("http")) {
				uri = "file://" + uri;
			}
		}
		ImageLoader.getInstance().displayImage(uri, imageView,
				getOriginOpt());
	}
	
	public static void displayImage(String uri, ImageView imageView, DIOptBuiderParam param) {
		if (!TextUtils.isEmpty(uri)) {
			if (!uri.startsWith("http")) {
				uri = "file://" + uri;
			}
		}
		ImageLoader.getInstance().displayImage(uri, imageView,
				getOpt(param));
	}
	
	public static ImageLoader createAnImageLoader(Context context) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(getDefaultImageLoaderConfig(context));
		return imageLoader;
	}
	
	public static void destroyImageLoader(ImageLoader imageLoader) {
		if (imageLoader != null) {
			MemoryCache cacheAware = imageLoader.getMemoryCache();
			if (cacheAware != null && cacheAware.keys() != null && !cacheAware.keys().isEmpty()) {
				Collection<String> keys = cacheAware.keys();
				Bitmap bitmap;
				for (String key : keys) {
					bitmap = cacheAware.get(key);
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
					}
				}
			}
			imageLoader.destroy();
		}
	}
	
	public static void release() {
		MemoryCache cacheAware = ImageLoader.getInstance().getMemoryCache();
		if (cacheAware != null && cacheAware.keys() != null && !cacheAware.keys().isEmpty()) {
			Collection<String> keys = cacheAware.keys();
			Bitmap bitmap;
			for (String key : keys) {
				bitmap = cacheAware.get(key);
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
		ImageLoader.getInstance().stop();
		ImageLoader.getInstance().clearMemoryCache();
//		ImageLoader.getInstance().resume();
	}

	/**
	 * don't change this function 请不要改动这个函数
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(getDefaultImageLoaderConfig(context));
	}
	
	private static ImageLoaderConfiguration getDefaultImageLoaderConfig(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
		.memoryCacheExtraOptions(OUT_WIDTH, OUT_WIDTH)
		.threadPoolSize(3)
		// 线程池内加载的数量
		// .denyCacheImageMultipleSizesInMemory()
		.memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))
		// You can pass your own memory cache
		// implementation/你可以通过自己的内存缓存实现
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.memoryCache(new WeakMemoryCache())
		.memoryCacheSize(4 * 1024 * 1024)
		.discCacheSize(50 * 1024 * 1024)
		// .discCacheFileNameGenerator(new
		// Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.discCacheFileCount(100)
		// 缓存的文件数量
		// .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
		.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
		.imageDownloader(
				new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
				// .writeDebugLogs() // Remove for release app
				.build();
		return config;
	}

	/**
	 * don't change this function 请不要改动这个函数
	 */
	public static DisplayImageOptions getCommonOpt() {
		if (commonOptions != null) {
			return commonOptions;
		}
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.outWidth = OUT_WIDTH;
		decodeOptions.outHeight = OUT_HEIGHT;
		commonOptions = getDefaultBuilder().decodingOptions(decodeOptions)
				.build();
		return commonOptions;
	}

	/**
	 * don't change this function 请不要改动这个函数
	 */
	public static DisplayImageOptions getOriginOpt() {
		if (originOptions != null) {
			return originOptions;
		}
		originOptions = getDefaultBuilder().build();
		return originOptions;
	}

	/**
	 * don't change this function 请不要改动这个函数
	 */
	public static DisplayImageOptions getOpt(int outWidth, int outHeight,
			int defaultIcon) {
		return getOptBuilder(outWidth, outHeight, defaultIcon).build();
	}
	
	/**
	 * don't change this function 请不要改动这个函数
	 */
	public static DisplayImageOptions.Builder getOptBuilder(int outWidth, int outHeight,
			int defaultIcon) {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.outWidth = outWidth;
		decodeOptions.outHeight = outHeight;
		DisplayImageOptions.Builder builder = getDefaultBuilder()
				.decodingOptions(decodeOptions);
		if (defaultIcon > 0) {
			builder.showImageForEmptyUri(defaultIcon)
					.showImageOnFail(defaultIcon)
					.showImageOnLoading(defaultIcon);
		}
		return builder;
	}
	
	public static DisplayImageOptions getOpt(DIOptBuiderParam param) {
		DisplayImageOptions.Builder builder = null;
		if (param != null) {
			builder = new DisplayImageOptions.Builder();
			BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
			decodeOptions.inSampleSize = 2;
			if (param.mOutHeight > 0 || param.mOutWidth > 0) {
				if (param.mOutHeight == 0) {
					param.mOutHeight = param.mOutWidth;
				}
				if (param.mOutWidth == 0) {
					param.mOutWidth = param.mOutHeight;
				}
				decodeOptions.outWidth = param.mOutWidth;
				decodeOptions.outHeight = param.mOutHeight;
				builder.decodingOptions(decodeOptions);
			}
			if (param.mDefaultIcon > 0) {
				builder.showImageForEmptyUri(param.mDefaultIcon)
				.showImageOnFail(param.mDefaultIcon)
				.showImageOnLoading(param.mDefaultIcon);
			}
			builder.cacheInMemory(param.mIsCacheInMemory);
			builder.cacheOnDisc(param.mIsCacheOnDisc);
			builder.considerExifParams(true)
			.imageScaleType(param.mHighQuality ?
					ImageScaleType.NONE : ImageScaleType.IN_SAMPLE_POWER_OF_2)
			.bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(true);
		}
		return builder == null ? null : builder.build();
	}

	/**
	 * don't change this function 请不要改动这个函数
	 */
	public static DisplayImageOptions.Builder getDefaultBuilder() {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inSampleSize = 2;
		return new DisplayImageOptions.Builder()
//		 .showImageOnLoading(R.drawable.icon_loading) // 设置图片在下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.default_photo)//
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.default_photo) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.decodingOptions(decodeOptions)// 设置图片的解码配置
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				// .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
				.resetViewBeforeLoading(true);// 设置图片在下载前是否重置，复位
	}

	
	public static class DIOptBuiderParam {
		public int mOutWidth;
		public int mOutHeight;
		public int mDefaultIcon;
		public boolean mIsCacheInMemory;
		public boolean mIsCacheOnDisc;
		public boolean mHighQuality;
	}
}
