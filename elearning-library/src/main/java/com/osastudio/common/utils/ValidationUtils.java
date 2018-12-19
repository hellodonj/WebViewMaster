package com.osastudio.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: wangchao
 * @date: 2017/03/29 10:25
 */

public class ValidationUtils {

    private static String id;

    /**
     * 根据包名请求服务器校验App是否合法
     *
     * @param context
     * @param packageName
     */
    public static void validateApp(final Context context, String packageName, boolean isDebug) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return;
        }

        String[] ds0 = {"h", "tt", "p", "://r", "eso", "p", ".lqw", "awa", ".co", "m/"};
        String[] rs0 = {"ht", "tp:", "//u", "pgr", "ade", "apk.", "ino", "o", "t.c", "n", "/"};
        //老师走起验证码
        String[] si0 = {"6", "3D", "1B6", "9C-2", "CB", "9-", "47", "D8-", "8A", "9", "6-00",
                "00A", "23", "F", "B", "D", "2E-a", "ndr", "oi", "d"};
        //表演课堂验证码
        String[] si1 = {"E", "3", "1D0", "C", "66", "-0", "B", "7B-4", "5", "33-9", "236-", "A",
                "134", "DCDE", "CA", "91", "-an", "dr", "oi", "d"};
        //LQWaWa验证码
        String[] si2 = {"187", "3393", "1-", "C58", "6", "-4", "5EE", "-B", "9", "AF", "-DE",
                "FB", "AD5", "ACE1", "1-", "AN", "DR", "OI", "D"};
        //LQWaWaHD验证码
        String[] si3 = {"7FF", "6", "48E1", "-12", "EA", "-4C2", "E-A", "794", "-D", "6F", "AC1",
                "B8CA", "C", "E-", "AND", "ROI", "D"};
        //LQWaWaHD验证码
        String[] si4 = {"68", "6C", "24", "F8-2", "9FF", "-424", "1", "-900", "8-21", "0057",
                "A", "78BD", "4-A", "NDRO", "I", "D"};
        //String[] si = {"1230639B-E8DE-4002-8D16-5B42C2A7352F"};  //return -1
        //String[] si = {"2230639B-E8DE-4002-8D16-5B42C2A7352F"};  //return -2
        if (packageName.equals("com.kfit.teachergo")) {
            id = getWhole(si0);
        } else if (packageName.equals("cn.cpaac.biaoyanketang")) {
            id = getWhole(si1);
        } else if (packageName.equals("com.galaxyschool.app.wawaschool")) {
            id = getWhole(si2);
        } else if (packageName.equals("com.lqwawa.internationalstudy")) {
            id = getWhole(si3);
        } else if (packageName.equals("com.lqwawa.homestay")) {
            id = getWhole(si4);
        }

        final String url = isDebug ? getWhole(ds0) : getWhole(rs0);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String[] s1 = {"aut", "horizat", "ionmanag", "ement/we", "bapi/lau", "nch", "erRe",
                        "s/chec", "kservi", "ce.h", "tm?i", "d="};
                String[] s2 = {"&p", "kna", "me="};
                String[] s3 = {"&a", "pkna", "me="};

                String pk = context.getApplicationContext().getPackageName();
                String nm = "";
                try {
                    PackageInfo pki = context.getPackageManager().getPackageInfo(pk, 0);
                    nm = pki.applicationInfo.loadLabel(context.getPackageManager()).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String u = url + getWhole(s1) + id + getWhole(s2) + pk.replaceAll("&",
                        "%26") + getWhole(s3) + nm.replaceAll("&", "%26");
                // u = u.replaceAll("&", "%26");
                u = u.replaceAll(" ", "%20");
                // Log.i("", "android_system log " + u);
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(u);
                try {
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        String responseStr = EntityUtils.toString(entity, "utf-8");
                        if (!TextUtils.isEmpty(responseStr)) {
                            JSONObject jsonObject = new JSONObject(responseStr);
                            if (jsonObject != null) {
                                int cd = jsonObject.optInt("co" + "de");
                                String msg = jsonObject.optString("mess" + "age");
                                processResponse(context, cd, msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();
    }

    private static String getWhole(String[] strings) {
        String s = "";
        for (String string : strings) {
            s += string;
        }
        return s;
    }

    private static long t = 0;

    private static void processResponse(final Context context, int cd, final String msg) {
        // Log.i("", "android_system log " + cd + msg);
        if (cd == 0) {
            t = System.currentTimeMillis();
        } else if (cd == -1) {
            Looper.prepare();
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(msg);
                    builder.create().show();
                }
            });
            Looper.loop();
        } else {
            System.exit(0);
        }
    }

    /**
     * 将字符串拆解成不定长度的字符串数组
     *
     * @param string     要拆解的字符串
     * @param maxItemLen 数组内单个字符串最大长度
     * @return 拆解好的字符串数组
     */
    public static String stringToArray(String string, int maxItemLen) {
        if (string == null) {
            return null;
        }

        if (maxItemLen <= 0) {
            maxItemLen = 1;
        }

        Random random = new Random();
        int startIndex = 0;
        int itemLen = (maxItemLen == 1) ? maxItemLen : random.nextInt(maxItemLen) + 1;
        int length = string.length();
        List<String> list = new ArrayList<String>();
        while (startIndex < length) {
            int endIndex = startIndex + itemLen;
            if (endIndex > length) {
                endIndex = length;
            }
            if (endIndex <= length) {
                list.add(string.substring(startIndex, endIndex));
                startIndex = startIndex + itemLen;
                if (maxItemLen != 1) {
                    itemLen = random.nextInt(maxItemLen) + 1;
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0, size = list.size(); i < size; i++) {
            String item = list.get(i);
            builder.append("\"" + item + "\"");
            if (i < size - 1) {
                builder.append(", ");
            }
        }
        builder.append("}");

        return builder.toString();
    }
}
