package com.galaxyschool.app.wawaschool.pojo.weike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchao on 1/19/16.
 */
public class MediaUploadList {
    public List<MediaData> data;
    public int code;

    public List<MediaData> getData() {
        return data;
    }

    public void setData(List<MediaData> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ShortCourseInfo> getShortCourseInfoList() {
        List<ShortCourseInfo> shortCourseInfos = new ArrayList<ShortCourseInfo>();
        if(data != null && data.size() > 0) {
            for(MediaData item: data) {
                if(item != null) {
                    ShortCourseInfo info = new ShortCourseInfo();
                    info.setMicroId(item.getIdType());
                    info.setTitle(item.originname);
                    shortCourseInfos.add(info);
                }
            }
        }
        return shortCourseInfos;
    }
}
