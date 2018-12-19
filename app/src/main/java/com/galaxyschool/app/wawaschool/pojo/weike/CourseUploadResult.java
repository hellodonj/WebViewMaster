package com.galaxyschool.app.wawaschool.pojo.weike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pp on 15/7/30.
 */
public class CourseUploadResult {
//    {
//        "data":[{
//        "subject":3, "collectioncount":0, "courseporperty":1, "parentid":0, "type":5, "id":2150, "createtime":
//        "2015-07-30 15:04:04", "level":"2150", "thumbnailurl":"", "resourceurl":
//        "http://mcourse.lqwawa.com:8080/kukewebservice/course/990193f5-2293-42db-8bc9-6a39de739ead.zip", "setexcellentcoursename":
//        "", "description":"", "savename":"990193f5-2293-42db-8bc9-6a39de739ead.zip", "grade":1, "totaltime":
//        1326, "coursetype":9, "imgurl":
//        "http://mcourse.lqwawa.com:8080/kukewebservice/course/990193f5-2293-42db-8bc9-6a39de739ead/head.jpg", "setexcellentcoursedate":
//        "", "isexcellentcourse":false, "status":1, "nickname":"Recorder-5", "isdelete":false, "viewcount":
//        0, "originname":"Recorder-5", "code":"0dea8905-f3b5-4b3b-ac4b-323068d64534", "downloadtimes":0, "size":
//        1706, "unit":"", "point":"", "createid":96164, "textbooksversion":7, "setexcellentcourseid":0, "createname":
//        "", "groupscode":"", "fascicule":1
//    }],"code":0
//    }

    public List<CourseData> data;
    public String message;
    public String exercise;//答题卡的信息
    public int code;

    public List<CourseData> getData() {
        return data;
    }


    public void setData(List<CourseData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
            for(CourseData item: data) {
                if(item != null) {
                    ShortCourseInfo info = new ShortCourseInfo();
                    info.setMicroId(item.getIdType());
                    info.setTitle(item.getNickname());
                    shortCourseInfos.add(info);
                }
            }
        }
        return shortCourseInfos;
    }
}
