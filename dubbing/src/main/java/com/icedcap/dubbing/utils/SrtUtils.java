package com.icedcap.dubbing.utils;

import com.icedcap.dubbing.entity.SrtEntity;
import com.icedcap.dubbing.entity.SRTSubtitleEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsq on 2017/4/24.
 */

public class SrtUtils {

    public static List<SrtEntity> processSrtFromFile(String srtPath) {
        List<SrtEntity> srtEntityList = new ArrayList<>();
        File srtFil = new File(srtPath);
        try {
            FileReader fileReader = new FileReader(srtFil);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("")) {
                    stringBuffer.append(line).append("@");
                    continue;
                }
                SrtEntity srtEntity = parseSrt(stringBuffer);
                if (srtEntity != null) {
                    srtEntityList.add(srtEntity);
                }
                // 清空，否则影响下一个字幕元素的解析
                stringBuffer.delete(0, stringBuffer.length());
            }
            if (stringBuffer.length() > 0) {
                SrtEntity srtEntity = parseSrt(stringBuffer);
                if (srtEntity != null) {
                    srtEntityList.add(srtEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return srtEntityList;
    }

    /**
     * @param stringBuffer
     * @return
     * @throws UnsupportedEncodingException
     */

    private static SrtEntity parseSrt(StringBuffer stringBuffer) throws UnsupportedEncodingException {
        if (stringBuffer == null || stringBuffer.length() <= 0) {
            return null;
        }
        String[] parseStrs = stringBuffer.toString().split("@");
        if (parseStrs.length < 3) {
            // 清空，否则影响下一个字幕元素的解析
            stringBuffer.delete(0, stringBuffer.length());
            return null;
        }

        // 5
        // 00:00:07,434 --> 00:00:08,434
        // 徐有容》踏进我国土半步(规范格式为:踏进我国土半步)
        SrtEntity srt = new SrtEntity();
        String timeToTime = parseStrs[1];
        int beginTime = getBeginTime(timeToTime);
        int endTime = getEndTime(timeToTime);

        String srtBody = "";
        for (int i = 2; i < parseStrs.length; i++) {
            srtBody += parseStrs[i] + "\n";
        }

        int index = srtBody.indexOf("》");
        String role = "";
        if (index > 0) {
            role = srtBody.substring(0, index);
            srtBody = srtBody.substring(index + 1, srtBody.length() - 1);
        }
        srt.setStartTime(beginTime);
        srt.setEndTime(endTime);
        srt.setRole(role);
        srt.setContent(new String(srtBody.getBytes(), "UTF-8"));
        return srt;
    }

    private static int getEndTime(String timeToTime) {
        int endHour = Integer.parseInt(timeToTime.substring(17, 19));
        int endMinute = Integer.parseInt(timeToTime.substring(20, 22));
        int endSecond = Integer.parseInt(timeToTime.substring(23, 25));
        int endMilli = Integer.parseInt(timeToTime.substring(26, 29));
        return (endHour * 3600 + endMinute * 60 + endSecond) * 1000 + endMilli;
    }

    private static int getBeginTime(String timeToTime) {
        int beginHour = Integer.parseInt(timeToTime.substring(0, 2));
        int beginMinute = Integer.parseInt(timeToTime.substring(3, 5));
        int beginSecond = Integer.parseInt(timeToTime.substring(6, 8));
        int beginMilli = Integer.parseInt(timeToTime.substring(9, 12));
        return (beginHour * 3600 + beginMinute * 60 + beginSecond) * 1000 + beginMilli;
    }


    /**
     * Get subtitle index by current time
     *
     * @param entity subtitle set
     * @param time   current time
     * @return index of current time
     */
    public static int getIndexByTime(List<? extends SrtEntity> entity, int time) {
        int index = 0;

        for (; index < entity.size(); index++) {
            int st = entity.get(index).getStartTime();
            int et = entity.get(index).getStartTime();
            if (time < st || time < et) {
                return index > 0 ? index - 1 : 0;
            }
        }
        return index > 0 ? index - 1 : 0;
    }

    /**
     * Get the No. subtitle by time
     *
     * @param entity subtitle set
     * @param time   current time
     * @return the No. of subtitle
     */
    public static int getSubtitleNumByTime(List<? extends SrtEntity> entity, int time) {
        if (time == 0) return 0;
        int index = 0;
        for (; index < entity.size(); index++) {
            int st = entity.get(index).getStartTime();
            if (time < st) {
                return index + 1;
            }
        }
        return index > 0 ? index + 1 : 0;
    }

    /**
     * Get time by subtitle index
     *
     * @param entity subtitle set
     * @param index  subtitle index
     * @return time of current index of subtitle
     */
    public static long getTimeByIndex(List<? extends SrtEntity> entity, int index) {
        int res = 0;
        if (entity == null || entity.size() == 0 || index < 0 || index > entity.size() - 1)
            return res;
        final int s = entity.get(index).getStartTime();
        final int i = index - 1;
        if (i >= 0) {
            final int e = entity.get(i).getEndTime();
            int halfInt = (s - e) / 2;
            halfInt = halfInt > 1000 ? 1000 : halfInt;
            res = s - halfInt;
        } else {
            res = s / 2;
        }
        return res;
    }
}
