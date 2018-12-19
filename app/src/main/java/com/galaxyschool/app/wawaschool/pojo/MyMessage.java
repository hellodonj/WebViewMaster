package com.galaxyschool.app.wawaschool.pojo;

import android.content.Context;
import com.galaxyschool.app.wawaschool.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MyMessage {

    public static final int MESSAGE_TYPE_CLASS = 1;
    public static final int MESSAGE_TYPE_PLATFORM_NEWS = 2;
    public static final int MESSAGE_TYPE_PLATFORM_NOTICE = 3;

    public static final int CLASS_MESSAGE_TYPE_HOMEWORK = NewResourceInfo.TYPE_CLASS_HOMEWORK;
    public static final int CLASS_MESSAGE_TYPE_NOTICE = NewResourceInfo.TYPE_CLASS_NOTICE;
    public static final int CLASS_MESSAGE_TYPE_SHOW = NewResourceInfo.TYPE_CLASS_SHOW;
    public static final int CLASS_MESSAGE_TYPE_COURSE = NewResourceInfo.TYPE_CLASS_COURSE;
    public static final int CLASS_MESSAGE_TYPE_LECTURE = NewResourceInfo.TYPE_CLASS_LECTURE;
    public static final int CLASS_MESSAGE_TYPE_STUDY_TASK = NewResourceInfo.TYPE_CLASS_STUDY_TASK;

    private String ClassId;
    private String HeadPicUrl;
    private int SubType;
    private int TypeCode;
    private String TypeName;
    private int UnReadNumber;
    private String Title;
    private String SubTitle;
    private String CreateTime;
    private long timestamp;

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getSubType() {
        return SubType;
    }

    public void setSubType(int subType) {
        SubType = subType;
    }

    public int getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(int typeCode) {
        TypeCode = typeCode;
    }

    public boolean isClassMessage() {
        return TypeCode == MESSAGE_TYPE_CLASS;
    }

    public boolean isPlatformMessage() {
        return TypeCode == MESSAGE_TYPE_PLATFORM_NEWS
                || TypeCode == MESSAGE_TYPE_PLATFORM_NOTICE;
    }

    public boolean isPlatformNewsMessage() {
        return TypeCode == MESSAGE_TYPE_PLATFORM_NEWS;
    }

    public boolean isPlatformNoticeMessage() {
        return TypeCode == MESSAGE_TYPE_PLATFORM_NOTICE;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public int getUnReadNumber() {
        return UnReadNumber;
    }

    public void setUnReadNumber(int unReadNumber) {
        UnReadNumber = unReadNumber;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTitle() {
        return SubTitle;
    }

    public void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public long getTimestamp() {
        if (timestamp == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                timestamp = sdf.parse(CreateTime).getTime();
            } catch (ParseException e) {

            }
        }
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static String getClassMessageTypeString(Context context, int type) {
        if (type == CLASS_MESSAGE_TYPE_HOMEWORK) {
            return context.getString(R.string.homeworks);
        } else if (type == CLASS_MESSAGE_TYPE_NOTICE) {
            return context.getString(R.string.notices);
        } else if (type == CLASS_MESSAGE_TYPE_SHOW) {
            return context.getString(R.string.shows);
        } else if (type == CLASS_MESSAGE_TYPE_COURSE) {
            return context.getString(R.string.courses);
        } else if (type == CLASS_MESSAGE_TYPE_LECTURE) {
            return context.getString(R.string.lectures);
        } else if (type == CLASS_MESSAGE_TYPE_STUDY_TASK) {
            return context.getString(R.string.learning_tasks);
        }
        return "";
    }
}
