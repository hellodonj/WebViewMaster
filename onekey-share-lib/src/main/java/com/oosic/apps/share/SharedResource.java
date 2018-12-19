package com.oosic.apps.share;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

public class SharedResource implements Serializable, Parcelable {

    public static final String RESOURCE_TYPE_STREAM = "stream";
    public static final String RESOURCE_TYPE_FILE = "file";
    public static final String RESOURCE_TYPE_HTML = "html";
    public static final String RESOURCE_TYPE_NOTE = "note";

    public static final int SOURCE_TYPE_HOMEWORK = 1;
    public static final int SOURCE_TYPE_NOTICE = 2;
    public static final int SOURCE_TYPE_COMMENT = 3;
    public static final int SOURCE_TYPE_COURSE = 4;
    public static final int SOURCE_TYPE_CLOUD_SPACE = 5;
    public static final int SOURCE_TYPE_PUBLIC_COURSE = 6;
    public static final int SOURCE_TYPE_WAWA_SHOW = 7;

    public static final int BOOK_SOURCE_TYPE_ALL = 1;
    public static final int BOOK_SOURCE_TYPE_SCHOOL = 2;

    public static final String FIELD_PATCHES_CLASS_SHARE_URL = "classShareUrl:classPrimaryKey,fromUserId,toUserId";
    public static final String FIELD_PATCHES_PERSON_SHARE_URL = "personShareUrl:Id";
    public static final String FIELD_PATCHES_SCHOOL_SHARE_URL = "schoolShareUrl:Id";
    public static final String FIELD_PATCHES_BOOK_SHARE_URL = "bookShareUrl:Type,OutlineId,SchoolId";

    private String primaryKey;
    private String id;
    private String type;
    private int sourceType;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private String shareUrl;
    private String authorId;
    private String authorName;
    private String knowledge;
    private String schoolId;
    private String schoolName;
    private String classId;
    private String className;
    private String classPrimaryKey;
    private String fromUserId;
    private String toUserId;
    private String fieldPatches;
    private int resourceType;
    private int screenType;
    private String createTime;
    private String updateTime;
    private int fileSize;
    ////////////////////////
    private boolean isPublicRescourse = true;
    private String parentId;
    public SharedResource() {

    }

    public SharedResource(Parcel src) {
        primaryKey = src.readString();
        id = src.readString();
        type = src.readString();
        sourceType = src.readInt();
        title = src.readString();
        description = src.readString();
        url = src.readString();
        thumbnailUrl = src.readString();
        shareUrl = src.readString();
        authorId = src.readString();
        authorName = src.readString();
        knowledge = src.readString();
        schoolId = src.readString();
        schoolName = src.readString();
        classPrimaryKey = src.readString();
        classId = src.readString();
        className = src.readString();
        fromUserId = src.readString();
        toUserId = src.readString();
        fieldPatches = src.readString();
        resourceType = src.readInt();
        screenType = src.readInt();
        createTime = src.readString();
        updateTime = src.readString();
        fileSize = src.readInt();
        isPublicRescourse = src.readByte() != 0;
        parentId = src.readString();

    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isPublicRescourse() {
        return isPublicRescourse;
    }

    public void setIsPublicRescourse(boolean publicRescourse) {
        isPublicRescourse = publicRescourse;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassPrimaryKey() {
        return classPrimaryKey;
    }

    public void setClassPrimaryKey(String classPrimaryKey) {
        this.classPrimaryKey = classPrimaryKey;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFieldPatches() {
        return fieldPatches;
    }

    public void setFieldPatches(String fieldPatches) {
        this.fieldPatches = fieldPatches;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public boolean requirePatchFields() {
        return !TextUtils.isEmpty(fieldPatches);
    }

    public void patchFields() {
        if (!requirePatchFields()) {
            return;
        }
        if (FIELD_PATCHES_CLASS_SHARE_URL.equals(fieldPatches)) {
            StringBuilder sb = new StringBuilder(shareUrl);
            sb.append("?").append("ClassPrimaryKey=").append(classPrimaryKey)
                    .append("&").append("FromUserId=").append(fromUserId);
            //只有邀请个人才加ToUserId，邀请班级不加。
            if (!TextUtils.isEmpty(toUserId)) {
                sb.append("&").append("ToUserId=").append(toUserId);
            }
            shareUrl = sb.toString();
        } else if (FIELD_PATCHES_PERSON_SHARE_URL.equals(fieldPatches)) {
            shareUrl = new StringBuilder(shareUrl).append("?")
                    .append("Id=").append(id)
                    .toString();
        } else if (FIELD_PATCHES_SCHOOL_SHARE_URL.equals(fieldPatches)) {
            shareUrl = new StringBuilder(shareUrl).append("?")
                    .append("Id=").append(id)
                    .toString();
        } else if (FIELD_PATCHES_BOOK_SHARE_URL.equals(fieldPatches)) {
            StringBuilder builder = new StringBuilder(shareUrl).append("?")
                    .append("Type=").append(sourceType)
                    .append("&").append("OutlineId=").append(id);
            if (sourceType == BOOK_SOURCE_TYPE_SCHOOL) {
                builder.append("&").append("SchoolId=").append(schoolId);
            }
            shareUrl = builder.toString();
        }
    }

    public void patchFieldShareUrlWithUserId(String userId) {
        patchFieldShareUrl("CurrentUserId", userId);
    }

    public void patchFieldShareUrlWithHideFooter() {
        patchFieldShareUrl("hidefooter", "true");
    }

    public void patchFieldShareUrlWithPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        patchFieldShareUrl("pkgname", packageName);
    }

    /**
     * 网络地址附加参数
     * @param key
     * @param value 可空
     */
    public void patchFieldShareUrl(String key, String value){
        if (TextUtils.isEmpty(shareUrl) ||TextUtils.isEmpty(key)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(key);
        builder.append("=");
        // 重复的字段不添加
        if (shareUrl.contains(builder.toString())) {
            return;
        }
        // 清空StringBuilder
        builder.delete(0, builder.length());

        builder.append(shareUrl);
        if (shareUrl.contains("?")){
            builder.append("&");
        }else {
            builder.append("?");
        }
        shareUrl = builder.append(key).append("=").append(value).toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dst, int flags) {
        dst.writeString(primaryKey);
        dst.writeString(id);
        dst.writeString(type);
        dst.writeInt(sourceType);
        dst.writeString(title);
        dst.writeString(description);
        dst.writeString(url);
        dst.writeString(thumbnailUrl);
        dst.writeString(shareUrl);
        dst.writeString(authorId);
        dst.writeString(authorName);
        dst.writeString(knowledge);
        dst.writeString(schoolId);
        dst.writeString(schoolName);
        dst.writeString(classPrimaryKey);
        dst.writeString(classId);
        dst.writeString(className);
        dst.writeString(fromUserId);
        dst.writeString(toUserId);
        dst.writeString(fieldPatches);
        dst.writeInt(resourceType);
        dst.writeInt(screenType);
        dst.writeString(createTime);
        dst.writeString(updateTime);
        dst.writeInt(fileSize);
        dst.writeByte(isPublicRescourse ? (byte) 1 : (byte) 0);
        dst.writeString(parentId);
    }

    public static final Creator<SharedResource> CREATOR = new Creator<SharedResource>() {
        @Override
        public SharedResource createFromParcel(Parcel src) {
            return new SharedResource(src);
        }

        @Override
        public SharedResource[] newArray(int size) {
            return new SharedResource[size];
        }
    };

}
