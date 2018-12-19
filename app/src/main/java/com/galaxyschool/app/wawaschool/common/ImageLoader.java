package com.galaxyschool.app.wawaschool.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lqwawa.lqbaselib.net.FileApi;
import com.oosic.apps.iemaker.base.BaseUtils;

import com.galaxyschool.app.wawaschool.bitmapmanager.Md5FileNameGenerator;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("JavaDoc")
public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();

    private static final int MAX_CAPACITY = 30;// 一级缓存的最大空间
    // 二级缓存，采用的是软应用，只有在内存吃紧的时候软应用才会被回收，有效的避免了oom
    private static ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
            MAX_CAPACITY / 2);
    // 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
    private static final HashMap<String, Bitmap> mFirstLevelCache = new LinkedHashMap<String, Bitmap>(
            MAX_CAPACITY / 2, 0.75f, true) {

        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
            if (size() > MAX_CAPACITY) {// 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
                mSecondLevelCache.put(eldest.getKey(),
                        new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            }
            return false;
        }
    };
    private static final long DELAY_BEFORE_PURGE = 10 * 60 * 1000;// 定时清理缓存
    private static final int BITMAP_DECODE_SIZE = 320;

    // 定时清理缓存
    private Runnable mClearCache = new Runnable() {
        @Override
        public void run() {
            clear();
        }
    };

    private Handler mPurgeHandler = new Handler();

    /**
     * 加载本地图片
     *
     * @param path
     * @return
     */
    public static Bitmap getLoacalBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 重置缓存清理的timer
    private void resetPurgeTimer() {
        mPurgeHandler.removeCallbacks(mClearCache);
        mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
    }

    /**
     * 清理缓存
     */
    private void clear() {
        mFirstLevelCache.clear();
        mSecondLevelCache.clear();
    }

    /**
     * 返回缓存，如果没有则返回null
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap;
        bitmap = getFromFirstLevelCache(url);// 从一级缓存中拿
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = getFromSecondLevelCache(url);// 从二级缓存中拿
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = getBitmapFromSD(url);// 从二级缓存中拿
        return bitmap;
    }

    /**
     * 从二级缓存中拿
     *
     * @param url
     * @return
     */
    private Bitmap getFromSecondLevelCache(String url) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> softReference = mSecondLevelCache.get(url);
        if (softReference != null) {
            bitmap = softReference.get();
            if (bitmap == null) {
                // 由于内存吃紧，软引用已经被gc回收了
                mSecondLevelCache.remove(url);
            }
        }
        return bitmap;
    }

    /**
     * 从一级缓存中拿
     *
     * @param url
     * @return
     */
    private Bitmap getFromFirstLevelCache(String url) {
        Bitmap bitmap;
        synchronized (mFirstLevelCache) {
            bitmap = mFirstLevelCache.get(url);
            if (bitmap != null) {
                // 将最近访问的元素放到链的头部，提高下一次访问该元素的检索速度（LRU算法）
                mFirstLevelCache.remove(url);
                mFirstLevelCache.put(url, bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 将url转成文件名 *
     */
    private String urlToFileName(String url) {
    	if (!TextUtils.isEmpty(url)) {
    		String fileName_md5 = Md5FileNameGenerator.generate(url);
    		String[] strs = url.split(".");
    		if (strs != null && strs.length > 0) {
    			fileName_md5 += strs[strs.length - 1];
			}
    		return fileName_md5;
		} else {
			return url;
		}
    }

    /**
     * 放入缓存
     *
     * @param url
     * @param value
     */
    public void addImage2Cache(String url, Bitmap value) {
        if (value == null || url == null) {
            return;
        }
        synchronized (mFirstLevelCache) {
            //加入内存
            mFirstLevelCache.put(url, value);
            //本地没有的话，加入到SD卡
            String path = Common.PicPath + "/" + urlToFileName(url);
            File file = new File(path);
            if (!file.exists()) {
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    file.createNewFile();
                    OutputStream outStream = new FileOutputStream(file);
                    value.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    Log.e("SavaImageToSD Error:", e.getMessage() == null ? "" : e.getMessage());
                }
            }
        }
    }

    /**
     * 获取url图片在本地的缓存地址
     * 如果缓存文件不存在，则创建一个
     *
     * @param url 远程地址
     * @return 本地缓存路径
     */
    public String getCachePath(String url) {
        String path = Common.PicPath + "/" + urlToFileName(url);
        File file = new File(path);
        if (!file.exists()) {
            Bitmap bitmap = BaseUtils.loadBitmap(path, BITMAP_DECODE_SIZE, BITMAP_DECODE_SIZE, 0);//BitmapFactory.decodeFile(path);
            //添加到内存缓存中
            addImage2Cache(url, bitmap);
        }
        return path;
    }

    public Bitmap getBitmapFromSD(String url) {
        Bitmap bitmap;

        String path = Common.PicPath + "/" + urlToFileName(url);
        File file = new File(path);
        if (file.exists()) {
            bitmap = BaseUtils.loadBitmap(path, BITMAP_DECODE_SIZE, BITMAP_DECODE_SIZE, 0);//BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                file.delete();
            } else {
                //添加到内存缓存中
                addImage2Cache(url, bitmap);
                return bitmap;
            }
        }

        return null;
    }

    public void deleteAndDownSDCard(String url) {
        try {
            String path = Common.PicPath + "/" + urlToFileName(url);
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
        getBitmapFromSD(url);
    }

    public void deleteSDCard(String url) {
        try {
            String path = Common.PicPath + "/" + urlToFileName(url);
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
    }

    public Bitmap loadImageFromInternet(String url) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        bitmap = getBitmapFromSD(url);
        if (bitmap != null) {
            return bitmap;
        }
//        HttpClient client = AndroidHttpClient.newInstance("Android");
//        HttpParams params = client.getParams();
//        //连接超时
//        HttpConnectionParams.setConnectionTimeout(params, 8000);
//        HttpConnectionParams.setSocketBufferSize(params, 8000);
//        HttpResponse response;
//        InputStream inputStream = null;
//        HttpGet httpGet = null;
//        try {
//            try {
//                httpGet = new HttpGet(url);
//            } catch (Exception e) {
//                httpGet = new HttpGet(url.replace("\"", ""));
//            }
//            response = client.execute(httpGet);
//            int stateCode = response.getStatusLine().getStatusCode();
//            if (stateCode != HttpStatus.SC_OK) {
//                Log.d(TAG, "func [loadImage] stateCode=" + stateCode);
//                return null;
//            }
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                try {
//                    inputStream = entity.getContent();
//                    return bitmap = BitmapFactory.decodeStream(inputStream);
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    entity.consumeContent();
//                }
//            }
//        } catch (ClientProtocolException e) {
//            Log.e("loadImage出现错误：", url + "-" + e.getMessage(), e);
//            httpGet.abort();
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.e("loadImage出现错误：", url + "-" + e.getMessage(), e);
//            httpGet.abort();
//            e.printStackTrace();
//        } catch (Exception e) {
//            Log.e("loadImage出现错误：", url + "-" + e.getMessage(), e);
//            if (httpGet != null) {
//                httpGet.abort();
//            }
//            e.printStackTrace();
//        } finally {
//            ((AndroidHttpClient) client).close();
//        }

        String fileName = Md5FileNameGenerator.generate(url);
        String cacheFilePath = new File(Utils.getCacheDir(), fileName).getAbsolutePath();

        try {
        	if (!new File(cacheFilePath).exists()) {
        		FileApi.getFile(url, cacheFilePath);
        	}
        	if (new File(cacheFilePath).exists()) {
        		bitmap = BaseUtils.loadBitmap(cacheFilePath, BITMAP_DECODE_SIZE, BITMAP_DECODE_SIZE, 0);
        	}
			
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        return bitmap;
    }

    public static String loadImage(String fileUrl) {
        String fileName = Md5FileNameGenerator.generate(fileUrl);
        String cacheFilePath = new File(Utils.getCacheDir(), fileName).getAbsolutePath();

        if (!new File(cacheFilePath).exists()) {
            return FileApi.getFile(fileUrl, cacheFilePath);
        }
        return cacheFilePath;
    }

    public static Bitmap decodeImage(String filePath) {
        if (new File(filePath).exists()) {
            return BaseUtils.loadBitmap(filePath, BITMAP_DECODE_SIZE, BITMAP_DECODE_SIZE, 0);
        }
        return null;
    }

    public static String getFileNameFromUrl(String fileUrl) {
        Uri uri = Uri.parse(fileUrl);
        String filePath = uri.getPath();
        Utils.logi("TEST", "URL= " + fileUrl);
        Utils.logi("TEST", "PATH= " + filePath);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Utils.logi("TEST", "NAME= " + fileName);
        return fileName;
    }

    public static String getCacheImagePath(String fileUrl) {
        String fileName = Md5FileNameGenerator.generate(fileUrl);
        return new File(Utils.getCacheDir(), fileName).getAbsolutePath();
    }

    public static String getSaveImagePath(String filename) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Pictures", filename).getAbsolutePath();
    }

    public static String saveImage(Context context, String fileUrl) {
        File srcFile = new File(getCacheImagePath(fileUrl));
        if (!srcFile.exists()) {
            return null;
        }
        File dstFile = new File(getSaveImagePath(getFileNameFromUrl(fileUrl)));
        boolean result = saveImage(context, srcFile, dstFile);
        return result ? dstFile.getAbsolutePath() : null;
    }

    public static boolean saveImage(Context context, File srcFile, File dstFile) {
        boolean result = false;
        if (dstFile.exists()) {
            dstFile.delete();
        } else {
            dstFile.getParentFile().mkdirs();
            try {
                dstFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(dstFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }
        if (result) {
            Uri uri = Uri.fromFile(new File(dstFile.getAbsolutePath()));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        }
        return result;
    }

    /**
     * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
     *
     * @param url
     * @param adapter
     * @param holder
     */
    public void loadImage(String url, BaseAdapter adapter, ImageView holder) {

        if (url == null || url.equals("")) {
            return;
        }

        resetPurgeTimer();

        // 从缓存中读取
        Bitmap bitmap = getBitmapFromCache(url);
        //从SD卡中读取
        if (bitmap == null) {
            bitmap = getBitmapFromSD(url);
        }
        //从网络读取
        if (bitmap == null) {
            ImageLoadTask imageLoadTask = new ImageLoadTask();
            try {
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    imageLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, adapter, holder);
                } else {
                    imageLoadTask.execute(url, adapter, holder);
                }
            } catch (Exception ex) {
                Log.e("ImageLoader", ex.getLocalizedMessage(), ex);
            }

        } else {
            holder.setImageBitmap(bitmap);//设为缓存图片
        }

    }

    public void loadImageUseDefault(String url, BaseAdapter adapter, ImageView holder, int resId, Context context) {

        if (url == null || url.equals("")) {
            if (resId != -1) {
                try {
                    holder.setImageDrawable(context.getResources().getDrawable(resId));
                } catch (Exception e) {

                }
            }
            return;
        }

        resetPurgeTimer();

        // 从缓存中读取
        Bitmap bitmap = getBitmapFromCache(url);
        //从SD卡中读取
        if (bitmap == null) {
            bitmap = getBitmapFromSD(url);
        }
        //从网络读取
        if (bitmap == null) {
            /*bitmap = loadImageFromInternet(url);
            if (bitmap != null) {
                holder.setImageBitmap(bitmap);
            } else {
                if (resId != -1) {
                    try {
                        holder.setImageDrawable(context.getResources().getDrawable(resId));
                    } catch (Exception e) {

                    }
                }
            }*/
            ImageLoadTaskUseDefault imageLoadTask = new ImageLoadTaskUseDefault(context, resId);
            try {
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    imageLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, adapter, holder);
                } else {
                    imageLoadTask.execute(url, adapter, holder);
                }
            } catch (Exception ex) {
                Log.e("ImageLoader", ex.getLocalizedMessage(), ex);
            }
        } else {
            holder.setImageBitmap(bitmap);//设为缓存图片
        }

    }

    class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
        String url;
        BaseAdapter adapter;
        ImageView holder;

        @Override
        protected Bitmap doInBackground(Object... params) {
            url = (String) params[0];
            adapter = (BaseAdapter) params[1];
            holder = (ImageView) params[2];
            return loadImageFromInternet(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                return;
            }
            addImage2Cache(url, result);// 放入缓存
            holder.setImageBitmap(result);//设图片
            if (adapter != null) {
                adapter.notifyDataSetChanged();// 触发getView方法执行，这个时候getView实际上会拿到刚刚缓存好的图片
            }
        }
    }


    class ImageLoadTaskUseDefault extends AsyncTask<Object, Void, Bitmap> {
        String url;
        BaseAdapter adapter;
        ImageView holder;


        Context context;
        int resId;
        public ImageLoadTaskUseDefault(Context context, int resId) {
            this.context = context;
            this.resId = resId;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            url = (String) params[0];
            adapter = (BaseAdapter) params[1];
            holder = (ImageView) params[2];
            return loadImageFromInternet(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                if (resId != -1) {
                    try {
                        holder.setImageDrawable(context.getResources().getDrawable(resId));
                    } catch (Exception e) {

                    }
                }
                return;
            }
            addImage2Cache(url, result);// 放入缓存
            holder.setImageBitmap(result);//设图片
            if (adapter != null) {
                adapter.notifyDataSetChanged();// 触发getView方法执行，这个时候getView实际上会拿到刚刚缓存好的图片
            }
        }
    }
}


