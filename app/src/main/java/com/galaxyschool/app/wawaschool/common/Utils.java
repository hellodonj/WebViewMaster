package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.views.ARCodeDialog;
import com.galaxyschool.app.wawaschool.views.ContactsListDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PagerSlidingTabStrip;
import com.galaxyschool.app.wawaschool.views.QRCodeDialog;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.Netroid;
import com.oosic.apps.iemaker.base.evaluate.EvaluateUtils;
import com.osastudio.common.library.LqBase64Helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Utils {
    public static final String ROOT = Environment.getExternalStorageDirectory()
            .toString();

    public static final String LQWAWA_KEY = "lqwawa";

    public static final String DATA_FOLDER = ROOT + "/" + LQWAWA_KEY + "/wawatong/";
    public static final String LQ_TEMP_FOLDER = ROOT + "/" + LQWAWA_KEY + "/temp/";
    public static final String ICON_FOLDER = DATA_FOLDER + "icon/";
    public static final String IMAGE_FOLDER = DATA_FOLDER + "image/";
    public static final String ICON_NAME = "icon.jpg";
    public static final String PNG_ICON_NAME = "icon.png";
    public static final String ZOOM_ICON_NAME = "zoom_icon.jpg";
    public static final String ZOOM_PNG_ICON_NAME = "zoom_icon.png";

    public static final String TEMP_IMAGE_NAME = "temp_image.jpg";
    public static final String TEMP_PNG_IMAGE_NAME = "temp_image.png";
    public static final String NO_MEDIA_FILE = ".nomedia";
    public static final String COURSE_SUFFIX = ".zip";
    public static final String TEMP_FOLDER = DATA_FOLDER + "temp/";
    public static final String CACHE_FOLDER = DATA_FOLDER + "cache/";
    public static final String LOG_FOLDER = DATA_FOLDER + "log/";
    public static final String PIC_TEMP_FOLDER = TEMP_FOLDER + "pic/";
    public static final String DOWNLOAD_TEMP_FOLDER = TEMP_FOLDER + "download/";

    public static final int USER_ICON_SIZE = 512;

    public final static String NOTE_FOLDER = DATA_FOLDER + "notes";
    public final static String AUDIO_FOLDER = DATA_FOLDER + "audios/";
    public final static String VIDEO_FOLDER = DATA_FOLDER + "videos/";
    public final static String PICTURE_FOLDER = DATA_FOLDER + "pictures/";
    public final static String THUMB_FOLDER = DATA_FOLDER + "thumb/";
    public final static String DRAFT_FOLDER = DATA_FOLDER + "Draft/";

    public final static String DB_FILE = DATA_FOLDER + ".wawatong.db";

    public static final int IMAGE_LONG_SIZE = 1280;

    //add for local course
    public static final String IMPORTED_FOLDER = DATA_FOLDER + "Imported/";
    public static final String RECORD_FOLDER = DATA_FOLDER + "Recorded/";
    public static final String ONLINE_FOLDER = DATA_FOLDER + "online/";
    public static final String RECORD_AUDIO = "/Audio/";
    public static final String RECORD_PDF = "/Pdf/";
    public static final String RECORD_VIDEO = "/Video/";
    public static final String FILE = "File/";
    public static final String FOLDER = "Folder/";
    public static final String PDF_PAGE_NAME = "pdf_page_";
    public static final String PDF_THUMB_NAME = "thumbnail.jpg";
    public static final String RECORD_HEAD_IMAGE_NAME = "head.jpg";
    public static final String RECORD_XML_NAME = "course_index.xml";

    public final static int IMPORT_PAGE_SIZE_WIDTH = 1280;
    public final static int IMPORT_PAGE_SIZE_HEIGHT = 800;
    public static final int DEFAULT_THUMB_HEIGHT = 240;

    public final static int MINIMUM_SPACE = 50; // 50M

    public static boolean isContainEnglish(String charaString) {
        return Pattern.compile("(?i)[a-zA-Z]").matcher(charaString).find();
    }

    public static boolean isCellularPhoneNumber(String str) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$");
        return str.matches(p.pattern());
    }

    public static boolean isCellularPhoneNumber2(String str) {
        Pattern p = Pattern.compile("^((1[0-9]))\\d{9}$");
        return str.matches(p.pattern());
    }

    public static boolean isPhoneNumber(String str) {
        Pattern p = Pattern
                .compile("(^((1[0-9]))\\d{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-?\\d{7,8}-(\\d{1,4})$)");
        return str.matches(p.pattern());
    }

    /**
     * 区号+座机号码+分机号码
     *
     * @param fixedPhone
     * @return
     */
    public static boolean isFixedPhone(String fixedPhone) {
        String reg = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        return Pattern.matches(reg, fixedPhone);
    }

    public static boolean isEmailAddress(String str) {
        Pattern p = Pattern
                .compile("^(\\w+((-\\w+)|(.\\w+))*)+\\w+((-\\w+)|(.\\w+))*\\@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+)*.[A-Za-z0-9]+$");
        return str.matches(p.pattern());
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static char[] getAcceptedCharNumChars() {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return chars.toCharArray();
    }

    public static String toHexString(byte[] data) {
        if (data != null && data.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                builder.append(Integer.toHexString((data[i] >> 4) & 0xf))
                        .append(Integer.toHexString(data[i] & 0xf));
            }
            return builder.toString();
        }
        return null;
    }

    public static byte[] decodeHexString(String hexString) {
        if (!TextUtils.isEmpty(hexString) && hexString.length() % 2 == 0) {
            byte[] data = hexString.getBytes();
            byte[] result = new byte[data.length / 2];
            try {
                int index = 0;
                for (int i = 0; i < result.length; i++) {
                    index = i * 2;
                    result[i] = Byte.decode(new StringBuilder("#")
                            .append(data[index] & 0xf)
                            .append(data[index + 1] & 0xf).toString());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    /**
     * 判断微课的目录存不在
     *
     * @param folder
     * @return 存在（true） 不存在（false）
     */
    public static boolean isCourseFolder(String folder) {
        File headFile = new File(folder, "head.jpg");
        File pageIndexFile = new File(folder, "page_index.xml");
        File courseIndexFile = new File(folder, "course_index.xml");
        if (headFile == null || pageIndexFile == null || courseIndexFile == null) {
            return false;
        }
        if (headFile.exists() || pageIndexFile.exists() || courseIndexFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 获取微课的根路径
     *
     * @param folder
     * @return rootString
     */
    public static String getCourseRootPath(String folder) {
        String result = null;
        if (!TextUtils.isEmpty(folder)) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                if (isCourseFolder(folder)) {
                    return folder;
                } else {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            result = getCourseRootPath(files[i].getPath());
                            if (result != null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean deleteFile(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    public static boolean safeDeleteDirectory(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }

        String path = dir.endsWith("/") ? dir.substring(0, dir.length() - 1)
                : dir;
        File file = new File(path);
        File to = new File(path + System.currentTimeMillis());
        if (file.renameTo(to)) {
            return deleteDirectory(to.getAbsolutePath());
        }
        return false;
    }

    private static boolean deleteDirectory(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } else {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
        }

        if (!flag) {
            System.out.println("delete dir fail");
            return false;
        }

        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getFileNameFromPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String tempPath = path;
        if (path.endsWith(File.separator)) {
            tempPath = path.substring(0, path.length() - 1);
        }
        int firstIndex = tempPath.lastIndexOf('/');
        String name = null;
        try {
            name = tempPath.subSequence(firstIndex + 1, tempPath.length())
                    .toString();
        } catch (Exception e) {
            name = null;
            e.printStackTrace();
        }
        return name;
    }

    public static String removeFileNameSuffix(String filename) {
		/*if (TextUtils.isEmpty(filename)) {
			return null;
		}
		int index = filename.lastIndexOf('.');
		String name = filename;
		if(index > 0) {
			name = filename.substring(0, index);
		}*/
        return filename;
    }

    public static String removeFolderSeparator(String folderPath) {
        String path = folderPath;
        if (!TextUtils.isEmpty(folderPath) && folderPath.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String getFileTitle(String srcPath, String destFolder,
                                      String suffix) {
        int firstIndex = srcPath.lastIndexOf('/');
        int lastIndex = srcPath.lastIndexOf('.');
        if (lastIndex <= firstIndex)
            lastIndex = srcPath.length();
        String temp = "New File";
        try {
            temp = srcPath.subSequence(firstIndex + 1, lastIndex).toString();
        } catch (Exception e) {
            temp = "New File";
            e.printStackTrace();
        }
        String name = temp;
        int i = 1;

        if (suffix == null) {
            while (new File(destFolder + name).exists()) {
                name = temp + "-" + i;
                i++;
            }
        } else {
            if (!suffix.startsWith(".")) {
                suffix = "." + suffix;
            }
            while (new File(destFolder + name + suffix).exists()) {
                name = temp + "-" + i;
                i++;
            }
            name = name + suffix;
        }

        //return zip file with suffix
        return name;
    }

    public static String getCacheDir() {
        try {
            File dir = new File(CACHE_FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLogDir() {
        try {
            File dir = new File(LOG_FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean zip(File input, File output) throws Exception {
        try {
            File outputFile = output;
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            ZipOutputStream outZip = new ZipOutputStream(outputStream);

            File inputfile = input;
            if (inputfile.exists()) {
                if (ZipFiles(inputfile.getParent() + File.separator,
                        inputfile.getName(), outZip) < 0) {
                    outZip.finish();
                    outZip.close();
                    return false;
                } else {
                    outZip.finish();
                    outZip.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static int ZipFiles(String folderString, String fileString,
                                ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return -1;

        File file = new File(folderString, fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            String fileList[] = file.list();
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i],
                        zipOutputSteam);
            }
        }
        return 0;

    }

    static public void unzip(String inputFilePath, String outputFilePath)
            throws Exception {
        if (TextUtils.isEmpty(inputFilePath) || TextUtils.isEmpty(outputFilePath)) {
            return;
        }
        if (!outputFilePath.endsWith(File.separator)) {
            outputFilePath = outputFilePath + File.separator;
        }
        ZipInputStream in = new ZipInputStream(new FileInputStream(inputFilePath));
        ZipEntry z;
        String name = "";
        int counter = 0;

        while ((z = in.getNextEntry()) != null) {
            name = z.getName();
            int index = name.indexOf(File.separator);
            if (index > 0 && ++index < name.length()) {
                name = name.substring(index);
            }
            if (z.isDirectory()) {

                name = name.substring(0, name.length() - 1);

                File folder = new File(outputFilePath + name);
                folder.mkdir();
                if (counter == 0) {
                }
                counter++;
            } else {
                File file = new File(outputFilePath + name);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int ch;
                byte[] buffer = new byte[1024];
                // read (ch) bytes into buffer
                while ((ch = in.read(buffer)) > 0) {
                    // write (ch) byte from buffer at the position 0
                    out.write(buffer, 0, ch);
                    out.flush();
                }
                out.close();
            }
        }

        in.close();
    }

    static public void unzip(InputStream inputStream, String outputFilePath)
            throws Exception {
        if (!outputFilePath.endsWith(File.separator)) {
            outputFilePath = outputFilePath + File.separator;
        }
        ZipInputStream in = new ZipInputStream(inputStream);
        ZipEntry z;
        String name = "";
        int counter = 0;

        while ((z = in.getNextEntry()) != null) {
            name = z.getName();
            int index = name.indexOf(File.separator);
            if (index > 0 && ++index < name.length()) {
                name = name.substring(index);
            }
            if (z.isDirectory()) {
                int separatorIndex = name.indexOf(File.separator);
                if (separatorIndex >= 0 && separatorIndex < name.length() - 1) {
                    name = name.substring(0, name.length() - 1);

                    File folder = new File(outputFilePath + name);
                    folder.mkdir();
                    if (counter == 0) {
                    }
                    counter++;
                }
            } else {
                File file = new File(outputFilePath + name);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int ch;
                byte[] buffer = new byte[1024];
                // read (ch) bytes into buffer
                while ((ch = in.read(buffer)) > 0) {
                    // write (ch) byte from buffer at the position 0
                    out.write(buffer, 0, ch);
                    out.flush();
                }
                out.close();
            }
        }

        in.close();
    }

    public static String formatSize(float size) {
        final long kb = 1024;
        final long mb = (kb * 1024);
        final long gb = (mb * 1024);

        if (size < kb) {
            return String.format("%d B", (int) size);
        } else if (size < mb) {
            return String.format("%.1f KB", size / kb);
        } else if (size < gb) {
            return String.format("%.1f MB", size / mb);
        } else {
            return String.format("%.1f GB", size / gb);
        }
    }

    public static void logi(String tag, String info) {
        Log.i(tag, "====>>" + info);
    }

    public static int transferRoleType(int roleType) {
        int result = -2;
        if (roleType < 0) {
            return -1;
        }
        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
            //��ʦ
            result = 2;

        } else if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            //ѧ��
            result = 0;

        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //�ҳ�
            result = 1;

        } else if (roleType == RoleType.ROLE_TYPE_VISITOR) {
            //�ο�
            result = -1;
        } else {
            result = -1;
        }
        return result;
    }

    public static String getUserCourseRootPath(String memberId, int courseType, boolean isFolder) {
        if (TextUtils.isEmpty(memberId) || courseType < 0) {
            return null;
        }
        String rootPath;
        if (courseType == CourseType.COURSE_TYPE_IMPORT) {
            rootPath = Utils.IMPORTED_FOLDER;
        } else {
            rootPath = Utils.RECORD_FOLDER;
        }

//		File rootFile = new File(rootPath);
//		if(rootFile == null || !rootFile.exists()) {
//			rootFile.mkdirs();
//		}

        StringBuilder builder = new StringBuilder();
        builder.append(rootPath);
        if (!rootPath.endsWith(File.separator)) {
            builder.append(File.separator);
        }
        builder.append(memberId);
        builder.append(File.separator);
        if (isFolder) {
            builder.append(FOLDER);
        } else {
            builder.append(FILE);
        }

        String path = builder.toString();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return builder.toString();
    }

    /**
     * create the folder with selected path.
     *
     * @param path
     */
    public static void createLocalDiskPath(String path) {
        File folder = new File(path);
        try {
            if (!folder.exists()) {

                boolean rtn = folder.mkdirs();
                logi("createLocalDiskPath", "rtn=" + rtn);
            }
        } catch (Exception e) {
            logi("createLocalDiskPath", "Exception");
        }
    }

    /**
     * writeToCache : write bitmap to path as JPG file
     */
    public static boolean writeToCacheJPEG(Bitmap bitmap, String path) {
        boolean rtn = false;
        if (bitmap != null && path != null) {
            rtn = writeToCacheJPEG(bitmap, path, 100);
        }
        return rtn;
    }

    /**
     * writeToCache : write bitmap to path as JPG file
     */
    public static boolean writeToCacheJPEG(Bitmap bitmap, String path,
                                           int quality) {
        boolean rtn = false;
        if (bitmap != null && path != null) {
            try {
                File file = new File(path);
                rtn = true;
                String parent = file.getParent();
                File parentFile = new File(parent);
                if (!parentFile.exists()) {
                    rtn = parentFile.mkdirs();
                }
                if (rtn) {
                    final FileOutputStream fos = new FileOutputStream(file);
                    final BufferedOutputStream bos = new BufferedOutputStream(fos,
                            16384);
                    rtn = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    bos.flush();
                    bos.close();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return rtn;
    }

    public synchronized static Bitmap loadBitmap(String pathName, int largeSide,
                                                 int shortSide, boolean bAccurateSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;
        float width, height;
        Bitmap bmp = null;

        if (pathName == null || pathName.equals(""))
            return null;

        File file = new File(pathName);
        if (!file.exists())
            return null;

        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            return null;

        }
        width = options.outWidth;
        height = options.outHeight;

        if (width == 0 || height == 0)
            return null;

        options.inDither = true;
        options.inJustDecodeBounds = false;

        float outputW = width;
        float outputH = height;
        float zoomFit = 1.0f;
        double zoomGal = -1.0f;

        if (width > height) {
            outputW = largeSide;
            outputH = (int) (height * outputW / width);
        } else {
            outputW = shortSide;
            outputH = (int) (height * outputW / width);
        }
        zoomFit = (float) Math.min(outputH / height, outputW / width);
        if (zoomGal < 0) {
            zoomGal = zoomFit;
        }

        int ratio1 = 0;
        ratio1 = (int) (width / outputW);
        if (ratio1 < 1) {
            ratio1 = 1;
        }
        options.inSampleSize = ratio1;
        try {
            bmp = BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            return null;
        }
        if (bmp == null) {
            return null;
        }

        ExifInterface exif = null;
        int orientation = 0;
        try {
            exif = new ExifInterface(pathName);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (exif != null) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                default:
                    orientation = 0;
            }
        }

        if (orientation != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate(orientation);

            Bitmap bmp2 = null;
            try {
                bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        bmp.getHeight(), matrix, false);
            } catch (OutOfMemoryError e) {
                // Log.e("ZiiPhoto","can not create bitmap, out of memory!");
            }
            if (bmp2 != null) {
                bmp.recycle();
                bmp = bmp2;
            }
        }

        if (bAccurateSize) {
            int bh = bmp.getHeight();
            if (outputH > 0 && bh > outputH) {
                outputW = outputH * bmp.getWidth() / bmp.getHeight();
                Bitmap sbmp = null;
                try {
                    sbmp = Bitmap.createBitmap((int) outputW, (int) outputH,
                            Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                }
                if (sbmp != null) {
                    Canvas canvas = new Canvas(sbmp);
                    RectF dst = new RectF(0, 0, outputW, outputH);
                    canvas.drawBitmap(bmp, null, dst, null);
                    bmp.recycle();
                    bmp = sbmp;
                    canvas = null;
                }
            }
        }

        return bmp;
    }

    public static boolean copyDirectory(File srcFile, File destFile) {

        boolean isOk = true;

        if (srcFile == null || destFile == null) {
            return false;
        }

        File[] srcFiles = srcFile.listFiles();
        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        for (File file : srcFiles) {
            if (file.isFile()) {
                int code = copyFile(file, new File(destFile.getAbsolutePath(), file.getName())); //调用文件拷贝的方法
                if (code != 0) {
                    isOk = false;
                }
            } else if (file.isDirectory()) {
                copyDirectory(file, new File(destFile.getAbsolutePath(), file.getName()));
            }
        }
        return isOk;
    }

    /**
     * copy the file from src to dst. 1.if src is directory or is not exist or
     * failure , return -1. 2.if copy is finished correctly ,return 0.
     *
     * @param srcFile
     * @param dstFile
     * @return
     */
    public static int copyFile(File srcFile, File dstFile) {
        int result = 0;
        if (srcFile.isDirectory()) {
            return -1;
        } else {
            if (!srcFile.exists()) {
                return -1;
            } else {
                if (dstFile.exists())
                    dstFile.delete();

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
                } catch (IOException e) {
                    result = -1;
                } finally {
                    try {
                        if (in != null)
                            in.close();
                        if (out != null)
                            out.close();
                    } catch (IOException e) {
                        result = -1;
                    }
                }
            }
        }
        return result;
    }

    /**
     * //任务类型:0-看微课,1-看课件,2-看作业,3-交作业,4-讨论话题，5-复述微课。
     * //目前隐藏：看微课，看课件，交作业。0、1、3。
     * //目前放开：看微课（“看微课”类型不变，名称改为“看课件”，原来的“看课件”类型暂时不动，仍然隐藏），交作业。
     *
     * @param list
     * @return
     */
    public static List<HomeworkListInfo> formatHomeworkListData(List<HomeworkListInfo> list) {

        if (list == null || list.size() <= 0) {
            return null;
        } else {
            List<HomeworkListInfo> resultList = new ArrayList<>();
            String type = null;
            for (HomeworkListInfo info : list) {
                type = info.getTaskType();
                if (!TextUtils.isEmpty(type)) {
                    if (type.equals("1")) {
                        continue;
                    } else {
                        resultList.add(info);
                    }
                }
            }
            return resultList;
        }
    }

    /**
     * 提示Dialog
     */
    public static void showTipsDialog(Activity activity, String tips) {
        if (activity == null) {
            return;
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(activity, null,
                tips, activity.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        messageDialog.show();

//		Window window = messageDialog.getWindow();
//		window.setGravity(Gravity.CENTER);
//		messageDialog.show();
//		WindowManager windowManager = activity.getWindowManager();
//		Display display = windowManager.getDefaultDisplay();
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.width = (int)(display.getWidth()*4/5);
//		window.setAttributes(lp);
    }

    public static int checkStorageSpace(Activity activity) {
        int havefree = Utils.haveSpace();
        if (havefree > 0) {
            showMessageDialog(activity, activity.getString(R.string.sdcard_full));
        } else if (havefree < 0) {
            showMessageDialog(activity, activity.getString(R.string.sdcard_mount));
        }

        return havefree;
    }

    private static void showMessageDialog(Activity activity, String message) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(activity, "", message,
                activity.getString(R.string.cancel), null, activity.getString(R.string.ok), null);
        dialog.show();
//		Window window = dialog.getWindow();
//		WindowManager m = activity.getWindowManager();
//		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
//		window.setGravity(Gravity.CENTER);
//		p.width = (int) (d.getWidth() * 0.5);
//		window.setAttributes(p);
    }

    public static int haveSpace() {
        long ret = readSDCard();
        int value = 0;
        long temp = ret / 1024 / 1024;
        if (temp > MINIMUM_SPACE)
            value = 0;
        else if (ret == -1)
            value = -1;
        else
            value = 1;
        return value;
    }

    static public long readSDCard() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            long freespace = availCount * blockSize;// / 1024 / 1024;
            return freespace;
        } else {
            return -1;
        }

    }

    public static boolean checkTitleValid(Activity activity, String title) {
        String regEx = "[~¥#*<>\\[\\]{}【】^@/￡¤¥|§¨「」『』￠￢￣~@#¥*——+|$_€\\\\/；;]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(title);
        if (m.find()) {
            TipMsgHelper.ShowMsg(activity, R.string.invalid_characters);
            return false;
        }
        return true;
    }

    public static boolean checkEditTextValid(Activity activity, String str) {
//        String regEx = "[~¥#*<>\\[\\]{}【】^@/￡¤¥|§¨「」『』￠￢￣~@#¥*——+|$_€\\\\/；;]";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(str);
//        if (m.find()) {
//            Toast.makeText(activity, R.string.invalid_characters, Toast.LENGTH_LONG).show();
//            return false;
//        }
        return true;
    }

    public static String changeTitleValid(String title) {
        if (!TextUtils.isEmpty(title)) {
            try {
                if (title.contains("%")) {
                    title = title.replace("%", URLEncoder.encode("%", "utf-8"));
                }
                if (title.contains("&")) {
                    title = title.replace("&", URLEncoder.encode("&", "utf-8"));
                }
                if (title.contains("=")) {
                    title = title.replace("=", URLEncoder.encode("=", "utf-8"));
                }
                if (title.contains("#")) {
                    title = title.replace("#", URLEncoder.encode("#", "utf-8"));
                }
                if (title.contains("?")) {
                    title = title.replace("?", URLEncoder.encode("?", "utf-8"));
                }
                if (title.contains("/")) {
                    title = title.replace("/", URLEncoder.encode("/", "utf-8"));
                }
                if (title.contains(" ")) {
                    title = title.replace(" ", "%20");
                }
                if (title.contains("+")) {
                    title = title.replace("+", URLEncoder.encode("+", "utf-8"));
                }
                if (title.contains("\\")) {
                    title = title.replace("\\", URLEncoder.encode("\\", "utf-8"));
                }
                if (title.contains("\"")) {
                    title = title.replace("\"", URLEncoder.encode("\"", "utf-8"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return title;
    }

    public static String analysisTitleValid(String title) {
        if (!TextUtils.isEmpty(title)) {
            if (title.contains("'")) {
                title = title.replace("'", "''");
            }
        }
        return title;
    }

    public static String transformSpecialCharacters(String title) {
        if (title != null) {
            if (title.contains("'")) {
                title = title.replace("'", "''");
            }
            if (title.contains("&")) {
                title = title.replace("&", "/&");
            }
        }
        return title;
    }

    public static String reductionTitleValid(String title) {
        if (title != null) {
            if (title.contains("/&")) {
                title = title.replace("/&", "&");
            }
        }
        return title;
    }

    /**
     * 设置dialog新的风格
     *
     * @param dialog
     */
    public static void setDialogNewlyStyle(ContactsListDialog dialog, float resize) {
        if (dialog == null) {
            return;
        }
        //默认是全屏
        if (resize > 0.0f && resize <= 1.0f) {
            dialog.resizeDialog(resize);
        }
        int height = (int) (10 * MyApplication.getDensity());
        //设置大布局
        LinearLayout bodyLayout = dialog.getBodyLayout();
        if (bodyLayout != null) {
            bodyLayout.setBackground(null);
            //设置距离底部20dp
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                    bodyLayout.getLayoutParams();
            lp.bottomMargin = height;
            bodyLayout.setLayoutParams(lp);
        }
        //设置ListView
        ListView listView = dialog.getListView();
        if (listView != null) {
            //分割线透明
            Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
            listView.setDivider(drawable);
            //分割线10dp
            listView.setDividerHeight(height);
        }
        //设置取消按钮
        Button cancelButton = dialog.getCancelButton();
        if (cancelButton != null) {
            //隐藏取消按钮
            cancelButton.setVisibility(View.GONE);
        }
    }

    /**
     * 显示查看二维码对话框，这个对话框需要和以往风格保持一致。
     */
    public static void showViewQrCodeDialog(Activity activity, NewResourceInfo newResourceInfo,
                                            String dialogTitle) {
        if (activity == null || newResourceInfo == null) {
            return;
        }
        String url = newResourceInfo.getShareAddress();
        if (TextUtils.isEmpty(url)) {
            //url为空就没得玩了
            return;
        }
        QRCodeDialog dialog = new QRCodeDialog(activity);
        dialog.show();
        String id = newResourceInfo.getMicroId();
        //dialog的标题
        if (TextUtils.isEmpty(dialogTitle)) {
            dialogTitle = activity.getString(R.string.qrcode);
        }
        //课件标题
        String courseTitle = newResourceInfo.getTitle();
        //作者名
        String authorName = newResourceInfo.getAuthorName();
        //对分享的连接增加权限设置
        int type = newResourceInfo.getResourceType();
        if (isLQCourse(type) || isStudyCard(type)) {

            if (newResourceInfo.isPublicResource()) {
                //表示公开的资源
                if (isStudyCard(type)) {
                    url = encryptionVisitUrl(url, true, type, null);
                }
            } else {
                url = encryptionVisitUrl(url, false, type, newResourceInfo.getParentId());
            }
        }

        dialog.setup(url, id, dialogTitle, courseTitle, authorName, null, null);
    }


    /**
     * 显示查看二维码对话框，这个对话框需要和以往风格保持一致。
     * 在线课堂进入
     */
    public static void showViewQrCodeDialog(Activity activity, SchoolInfo schoolInfo,
                                            String dialogTitle) {
        if (activity == null || schoolInfo == null) {
            return;
        }
        final String url = schoolInfo.getQRCode();
		/*String qrCodeUrl = AppConfig.ServerUrl.QRCODE_SHARE_URL;
		qrCodeUrl = qrCodeUrl.replace("{id}",schoolInfo.getSchoolId());*/

        if (TextUtils.isEmpty(url)) {
            //url为空就没得玩了
            return;
        }

        final QRCodeDialog dialog = new QRCodeDialog(activity);
        dialog.show();
        final String id = schoolInfo.getSchoolId();
        //dialog的标题
        if (TextUtils.isEmpty(dialogTitle)) {
            dialogTitle = activity.getString(R.string.qrcode);
        }

        final String _dialogTitle = dialogTitle;
        //课件标题
        final String courseTitle = schoolInfo.getSchoolName();
        //作者名
		/*String authorName = newResourceInfo.getAuthorName();
		//对分享的连接增加权限设置
		int type = newResourceInfo.getResourceType();
		if (isLQCourse(type) || isStudyCard(type)) {

			if (newResourceInfo.isPublicResource()) {
				//表示公开的资源
				if (isStudyCard(type)) {
					url = encryptionVisitUrl(url, true, type, null);
				}
			} else {
				url = encryptionVisitUrl(url, false, type, newResourceInfo.getParentId());
			}
		}*/

		/*File file = new File(url);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(url);
			if (bitmap != null) {
				// qrCodeImageView.setImageBitmap(bitmap);
				dialog.setup(bitmap,id,dialogTitle,courseTitle,"",null,null);
				return;
			}
			file.delete();
		}*/
        final String qrCodeImagePath = ImageLoader.getCacheImagePath(url);
        Netroid.downloadFile(UIUtil.getContext(), url, qrCodeImagePath,
                new Listener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (dialog.isShowing()) {
                            // qrCodeImageView.setImageBitmap(BitmapFactory.decodeFile(qrCodeImagePath));
                            dialog.setup(url, BitmapFactory.decodeFile(qrCodeImagePath), id, _dialogTitle, courseTitle, "", null, null);
                        }

                    }

                    @Override
                    public void onError(NetroidError error) {
                        super.onError(error);
                        UIUtil.showToastSafe(R.string.picture_download_failed);
                    }
                });
    }

    /**
     * 显示查看AR大图
     */
    public static void showViewQrCodeDialog(Activity activity, String url, String title, String courseTitle) {
        if (activity == null) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            //url为空就没得玩了
            return;
        }
        ARCodeDialog dialog = new ARCodeDialog(activity);
        dialog.show();


        dialog.setup(url, title, courseTitle, null, null);
    }


    public static boolean isStudyCard(int type) {
        if (type == ResType.RES_TYPE_STUDY_CARD || type == ResType.RES_TYPE_BASE + ResType
                .RES_TYPE_STUDY_CARD) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据类型获取类型名称
     *
     * @param activity
     * @param taskType
     * @return
     */
    public static String getTypeNameByType(Activity activity, int taskType) {
        if (activity == null) {
            return "";
        }
        String result = "";
        if (taskType == StudyTaskType.SUBMIT_HOMEWORK
                || taskType == StudyTaskType.WATCH_HOMEWORK) {
            //看作业、交作业都是其他
            result = activity.getString(R.string.other);
        } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            //复述课件
            result = activity.getString(R.string.retell_course);
        } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            //导读
            result = activity.getString(R.string.introduction);
        } else if (taskType == StudyTaskType.ENGLISH_WRITING) {
            //英文写作
            result = activity.getString(R.string.english_writing);
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            //任务单
            result = activity.getString(R.string.do_task);
        } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE) {
            //看课件
            result = activity.getString(R.string.look_through_courseware);
        } else if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
            //看课件
            result = activity.getString(R.string.look_through_courseware);
        } else if (taskType == StudyTaskType.Q_DUBBING){
            //Q配音
            result = activity.getString(R.string.str_q_dubbing);
        }
        return result;
    }

    /**
     * 初始化PagerSlidingTabStrip的Tabs
     */
    public static void initPagerSlidingTabStripItemViews(PagerSlidingTabStrip psts) {
        if (psts != null) {
            int count = psts.getTabCount();
            if (count > 0) {
                if (count == 1) {
                    LinearLayout layout = psts.getTabsContainer();
                    if (layout != null) {
                        TextView itemView = (TextView) layout.getChildAt(0);
                        if (itemView != null) {
                            itemView.setBackgroundResource(R.drawable.selector_bg_tab_task);
                        }
                    }
                } else if (count >= 2) {
                    LinearLayout layout = psts.getTabsContainer();
                    if (layout != null) {
                        for (int i = 0; i < count; i++) {
                            View itemView = layout.getChildAt(i);
                            if (itemView != null) {
                                if (i == 0) {
                                    //第一个条目
                                    itemView.setBackgroundResource(
                                            R.drawable.selector_bg_tab_task_left);
                                } else if (i == count - 1) {
                                    //最后一个条目
                                    itemView.setBackgroundResource(
                                            R.drawable.selector_bg_tab_task_right);
                                } else {
                                    //中间其他的条目
                                    itemView.setBackgroundResource(
                                            R.drawable.selector_bg_tab_task_middle);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static LinearLayout getPagerSlidingTabStripTabContainer(PagerSlidingTabStrip psts) {
        if (psts != null) {
            int count = psts.getTabCount();
            if (count > 0) {
                LinearLayout layout = psts.getTabsContainer();
                if (layout != null) {
                    return layout;
                }
            }
        }
        return null;
    }

    /**
     * 打开校长助手Web页面
     */
    public static void openPrincipalAssistantPage(Activity activity, UserInfo userInfo,
                                                  SchoolInfo schoolInfo) {
        if (activity == null) {
            return;
        }
        String url = ServerUrl.PRINCIPAL_ASSISTANT_LIST_URL;
        String title = activity.getString(R.string.principal_assistant);
        String memberId = "";
        String fullName = "";
        String schoolId = "";
        Map<String, String> map = new HashMap<>();
        if (userInfo != null) {
            memberId = userInfo.getMemberId();
            fullName = userInfo.getRealName();
            //真实姓名为空传入用户名
            if (TextUtils.isEmpty(fullName)) {
                fullName = userInfo.getNickName();
            }
        }
        map.put("memberId", memberId);
        map.put("fullname", fullName);
        if (schoolInfo != null) {
            schoolId = schoolInfo.getSchoolId();
        }
        map.put("schoolid", schoolId);
        //控制是否显示关闭按钮
        boolean showCloseLayout = true;
        WebUtils.openCommonWebView(activity, url, map, title, showCloseLayout);
    }

    /**
     * 打开营养膳食Web页面
     */
    public static void openNutritionRecipesWebPage(Activity activity, UserInfo userInfo) {
        if (activity == null) {
            return;
        }
        String url = ServerUrl.NUTRITION_RECIPES_LIST_URL;
        String title = activity.getString(R.string.health_nutrition_recipes);
        String userName = "";
        String memberId = "";
        Map<String, String> map = new HashMap<>();
        if (userInfo != null) {
            //账号
            userName = userInfo.getNickName();
            memberId = userInfo.getMemberId();
        }
        map.put("user", userName);
        map.put("mid", memberId);
        //控制是否显示关闭按钮
        boolean showCloseLayout = true;
        WebUtils.openCommonWebView(activity, url, map, title, showCloseLayout);
    }

    /**
     * 获取看课件资源list
     * 当为图集时，传值为所有图片的resid和resurl逗号分隔形式，会直接返回给客户端；其他则是单个的resid和resurl传值
     *
     * @param resourceInfoTagList
     * @return
     */
    public static List<LookResDto> getWatchWawaCourseLookResDtoList(
            List<ResourceInfoTag> resourceInfoTagList, boolean isSuperTask) {
        if (resourceInfoTagList == null || resourceInfoTagList.size() == 0) {
            return null;
        }
        List<LookResDto> resultList = new ArrayList<>();
        StringBuilder resIdBuilder = new StringBuilder();
        StringBuilder resUrlBuilder = new StringBuilder();
        StringBuilder titleBuilder = new StringBuilder();
        StringBuilder authorIdBuilder = new StringBuilder();
        for (ResourceInfoTag tag : resourceInfoTagList) {
            if (tag != null) {
                List<ResourceInfo> resourceInfoList = tag.getSplitInfoList();
                //图集,这里只是取结果数据，过滤添加数据在MediaListFragment里面控制。
                if (resourceInfoList != null && resourceInfoList.size() > 0) {
                    //清空
                    resIdBuilder.delete(0, resIdBuilder.length());
                    resUrlBuilder.delete(0, resUrlBuilder.length());
                    titleBuilder.delete(0, titleBuilder.length());
                    authorIdBuilder.delete(0, authorIdBuilder.length());
                    int size = resourceInfoList.size();
                    for (int i = 0; i < size; i++) {
                        ResourceInfo info = resourceInfoList.get(i);
                        if (info != null) {
                            if (i < size - 1) {
                                //逗号分隔
                                resIdBuilder.append(info.getResId()).append(",");
                                resUrlBuilder.append(info.getResourcePath()).append(",");
                                titleBuilder.append(info.getTitle()).append(",");
                                authorIdBuilder.append(info.getAuthorId()).append(",");
                            } else {
                                //无逗号分隔
                                resIdBuilder.append(info.getResId());
                                resUrlBuilder.append(info.getResourcePath());
                                titleBuilder.append(info.getTitle());
                                authorIdBuilder.append(info.getAuthorId());
                            }
                        }
                    }
//					"ResId":"1-1,2-2,3-3", --图集的ResId传值
//					"ResUrl":"Url1,Url2,Url3" --图集的ResUrl传值
//					"ResTitle":"标题1,标题2，标题3"
//					}
                    //装载
                    LookResDto lookResDto = new LookResDto();
                    lookResDto.setResId(resIdBuilder.toString());
                    lookResDto.setResUrl(resUrlBuilder.toString());
                    //资源标题，非必填。
                    lookResDto.setResTitle(titleBuilder.toString());
                    //资源作者id，非必填。
                    lookResDto.setAuthor(authorIdBuilder.toString());
                    if (isSuperTask) {
                        lookResDto.setSplitInfoList(resourceInfoList);
                    }
                    lookResDto.setResCourseId(tag.getResCourseId());
                    if (!TextUtils.isEmpty(tag.getPoint())){
                        lookResDto.setResPropType(1);
                    }
                    resultList.add(lookResDto);
                } else {
//					{
//						"ResId":"1-1",
//						"ResUrl":"Url"
//					    "ResTitle":"标题1"
//					}
                    //单一资源
                    LookResDto lookResDto = new LookResDto();
                    lookResDto.setResId(tag.getResId());
                    lookResDto.setResUrl(tag.getResourcePath());
                    //资源标题，非必填。
                    lookResDto.setResTitle(tag.getTitle());
                    //资源作者id，非必填。
                    lookResDto.setAuthor(tag.getAuthorId());
                    lookResDto.setResCourseId(tag.getResCourseId());
                    if (!TextUtils.isEmpty(tag.getPoint())){
                        lookResDto.setResPropType(1);
                    }
                    resultList.add(lookResDto);
                }
            }
        }
        return resultList;
    }

    /**
     * 是否是LQ课件（包含有声相册）
     *
     * @return
     */
    public static boolean isLQCourse(int resourceType) {
        boolean yes = false;
        resourceType = resourceType % MaterialResourceType.BASE_TYPE_NUM;
        if (resourceType == MaterialResourceType.OLD_COURSE
                || resourceType == MaterialResourceType.OLD_COURSE_ANOTHER
                || resourceType == MaterialResourceType.MICRO_COURSE
                || resourceType == MaterialResourceType.ONE_PAGE) {
            yes = true;
        }
        return yes;
    }

    /**
     * 获取resId里面拼接的resourceType
     *
     * @param resId
     * @return
     */
    public static int getResourceTypeBySplitingResId(String resId) {
        if (!TextUtils.isEmpty(resId)) {
            if (resId.contains("-")) {
                int resourceType = Integer.parseInt(resId.substring(resId.lastIndexOf("-") + 1));
                return resourceType;
            }
        }
        return -1;
    }

    /**
     * 过滤个人资源库数据
     *
     * @param originList
     * @return
     */
    public static List<NewResourceInfoTag> filterPersonalResourceListData(
            List<NewResourceInfoTag> originList, int mediaType) {
        if (originList != null && originList.size() > 0) {
            List<NewResourceInfoTag> resultList = new ArrayList();
            for (NewResourceInfoTag tag : originList) {
                if (tag != null) {
                    //音频、视频需要过滤类型
                    String resourceUrl = tag.getResourceUrl().toLowerCase();
                    //音频 MP3，m4a
                    if (mediaType == MediaType.AUDIO) {
                        if (!TextUtils.isEmpty(resourceUrl)) {
                            //这里用contains防止返回路径格式不统一的情况
                            if (resourceUrl.contains(".mp3")
                                    || resourceUrl.contains(".m4a")) {
                                resultList.add(tag);
                            }
                        }
                    } else if (mediaType == MediaType.VIDEO) {
                        //视频mp4
                        if (!TextUtils.isEmpty(resourceUrl)) {
                            if (resourceUrl.contains(".mp4")) {
                                resultList.add(tag);
                            }
                        }
                    } else {
                        //其他类型不过滤
                        resultList.add(tag);
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(Activity activity, String source) {
        if (activity != null && !TextUtils.isEmpty(source)) {
            int len = source.length();
            for (int i = 0; i < len; i++) {
                char codePoint = source.charAt(i);
                if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                    TipMsgHelper.ShowMsg(activity, R.string.input_valid);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    /**
     * 对需要加密的url进行加密
     *
     * @return
     */
    public static String encryptionVisitUrl(String url, boolean isPublicResource, int type, String parentId) {
        boolean isSplitCourse = type > ResType.RES_TYPE_BASE;
        if (!TextUtils.isEmpty(url)) {
            int index = url.indexOf("?");
            String headUrl = url.substring(0, index);
            String bodyUrl = url.substring(index + 1, url.length());
            if (isLQCourse(type)) {
                if (isSplitCourse) {
                    if (!TextUtils.isEmpty(parentId)) {
                        bodyUrl = bodyUrl + "&auth=true&parentId=" + parentId;
                    } else {
                        bodyUrl = bodyUrl + "&auth=true";
                    }
                } else {
                    bodyUrl = bodyUrl + "&auth=true";
                }
            } else if (isStudyCard(type)) {
                if (isPublicResource) {
                    bodyUrl = bodyUrl + "&auth=false";
                } else {
                    return url;
                }
            }
            bodyUrl = LqBase64Helper.getEncoderString(bodyUrl);
            bodyUrl = bodyUrl.replaceAll("[\n\r]", "");
            url = headUrl + "?enc=" + bodyUrl;
        }
        return url;
    }

    /**
     * 判断当前用户在不在加入的学校当中
     *
     * @param collectSchoolId
     * @param memberId
     * @return
     */
    public static boolean isJoinSchool(String collectSchoolId, String memberId) {
        List<String> joinSchoolIdList = DemoApplication.getInstance().getPrefsManager()
                .getJoinSchoolIdList(memberId);
        boolean flag = false;
        if (joinSchoolIdList != null && joinSchoolIdList.size() > 0 && !TextUtils.isEmpty
                (collectSchoolId)) {
            String[] splitArray = null;
            if (collectSchoolId.contains(",")) {
                splitArray = collectSchoolId.split(",");
            } else {
                splitArray = new String[]{collectSchoolId};
            }
            for (int i = 0, len = joinSchoolIdList.size(); i < len; i++) {
                String schoolId = joinSchoolIdList.get(i);
                for (int j = 0; j < splitArray.length; j++) {
                    String collectId = splitArray[j];
                    if (TextUtils.equals(schoolId.toLowerCase(), collectId.toLowerCase())) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 判断当前用户有没有加入学校
     *
     * @param memberId
     * @return
     */
    public static boolean isHasJoinSchool(String memberId) {
        List<String> joinSchoolIdList = DemoApplication.getInstance().getPrefsManager()
                .getJoinSchoolIdList(memberId);
        if (joinSchoolIdList == null || joinSchoolIdList.size() == 0) {
            return false;
        }
        return true;
    }

    @NonNull
    public static File getZoomFile() {
        String urlFolder = ICON_FOLDER;
        File file = new File(urlFolder + ZOOM_ICON_NAME);
        File fileFolder = new File(urlFolder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        return file;
    }

    public static String transferNumberData(int count) {
        String numData = null;
        if (count < 1000) {
            numData = String.valueOf(count);
        } else if (count < 10000) {
            numData = String.valueOf(div(count, 1000, 1)) + "千";
        } else if (count < 10000000) {
            numData = String.valueOf(div(count, 10000, 1)) + "万";
        } else {
            numData = String.valueOf(div(count, 10000000, 1)) + "千万";
        }
        return numData;
    }

    private static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 从schoolList中移除在线课堂的机构
     *
     * @param list
     */
    public static void removeOnlineSchoolInfo(List<SchoolInfo> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        Iterator<SchoolInfo> it = list.iterator();
        while (it.hasNext()) {
            SchoolInfo schoolInfo = it.next();
            if (schoolInfo != null && schoolInfo.isOnlineSchool()) {
                it.remove();
            }
        }
    }

    /**
     * 从ContactsSchoolInfoList中移除在线课堂的机构
     *
     * @param list
     */
    public static void removeOnlineContactsSchoolInfo(List<ContactsSchoolInfo> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        Iterator<ContactsSchoolInfo> it = list.iterator();
        while (it.hasNext()) {
            ContactsSchoolInfo schoolInfo = it.next();
            if (schoolInfo != null && schoolInfo.isOnlineSchool()) {
                it.remove();
            }
        }
    }

    public static void removeSchoolInfoList(List<ContactsSchoolInfo> list,String schoolId) {
        if (list == null || list.size() == 0 || TextUtils.isEmpty(schoolId)) {
            return;
        }
        Iterator<ContactsSchoolInfo> it = list.iterator();
        while (it.hasNext()) {
            ContactsSchoolInfo schoolInfo = it.next();
            if (schoolInfo != null && !TextUtils.equals(schoolId,schoolInfo.getSchoolId())) {
                it.remove();
            }
        }
    }

    /**
     * 从SubscribeUserInfoList中移除在线课堂的机构
     *
     * @param list
     */
    public static void removeOnlineSubscribeUserInfo(List<SubscribeUserInfo> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        Iterator<SubscribeUserInfo> it = list.iterator();
        while (it.hasNext()) {
            SubscribeUserInfo schoolInfo = it.next();
            if (schoolInfo != null && schoolInfo.isOnlineSchool()) {
                it.remove();
            }
        }
    }

    /**
     * @param data float类型的数据
     * @return int类型的数据
     */
    public static int floatChangeToInt(float data) {
        return Math.round(data);
    }

    public static boolean isEnglishLanguage(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (EvaluateUtils.isEnglishCharacter(ch[i])) {
                return true;
            }
            if (EvaluateUtils.isChineseCharacter(ch[i])) {
                return false;
            }
        }
        return false;
    }

    public static String changeDoubleToInt(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.contains(".")) {
            int index = data.indexOf(".");
            //前面的整数
            String integer = data.substring(0, index);
            //后面的小数点
            String decimal = data.substring(index + 1, index + 2);
            if (!TextUtils.isEmpty(decimal)) {
                int dataInt = Integer.valueOf(decimal);
                if (dataInt > 0) {
                    return data.substring(0,index + 2);
                } else {
                    return integer;
                }
            }
        }
        return data;
    }

    public static float getNumberDivData(float a,float b){
        return (float)(Math.round(a*10 / b)) / 10;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    /**
     * 是否是实体机构的老师
     *
     * @param
     */
    public static boolean isRealTeacher(List<SchoolInfo> schoolList) {
        if (schoolList != null && schoolList.size() > 0) {
            for (SchoolInfo info : schoolList) {
                if (info.isOnlineSchool()) continue;
                if (info.isTeacher()) return true;
            }
        }
        return false;
    }
}
