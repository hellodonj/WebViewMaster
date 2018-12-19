package com.galaxyschool.app.wawaschool.pojo;


import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.db.dto.CourseDTO;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.iemaker.base.data.CourseShareData;
import com.oosic.apps.iemaker.base.ooshare.ShareUtil;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareSettings;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

public class CourseInfo implements Parcelable {
    private int subject;
    private int collectioncount;
    private int courseporperty;
    private int parentid;
    private int type;
    private String createtime;
    private int id;
    private String thumbnail;
    private String resourceurl;
    private String shareurl;
    private String setexcellentcoursename;
    private String description;
    private String savename;
    private int grade;
    private int totaltime;
    private int coursetype;
    private String imgurl;
    private String setexcellentcoursedate;
    private boolean isexcellentcourse;
    private int status;
    private String nickname;
    private boolean isdelete;
    private int viewcount;
    private String originname;
    private String code;
    private int downloadtimes;
    private int size;
    private String unit;
    private String point;
    private int createid;
    private int textbooksversion;
    private int setexcellentcourseid;
    private String createname;
    private String groupscode;
    private int fascicule;
    private boolean isRead;
    private String courseTypeName;
    private int xueduan;
    private String xueduanName;
    private String microId;
    private int resourceType;

    private int sourceType;
    private String primaryKey;
    private boolean collectionStatus;
    private boolean isCollected;
    private int commentnum;
    private int praisenum;
    private boolean isPraise;
    private String shareAddress;
    private boolean isSlide;
    private int screenType;
    private boolean isHomework;
    private boolean isSplitCourse;
    private String updateTime;

    private boolean isMyBookShelf;
    private int courseSourceType;
    private String resourceId;
    private String bookId;
    private String sectionId;
    private String editResourceUrl;
    private String editMicroId;
    private String editUpdateTime;
    private ContactItem contactItem;
    private String schoolName;
    public boolean isCampusPatrolTag;//是否是校园巡查标识，手动添加的字段。
    private boolean isFromShowShow;//是否来自秀秀
    private String schoolId;
    //修改判断当前用户在班级中的角色
    private boolean isTeacher;
    private boolean isPublicRescourse = true;
    private String parentId;

    //收藏过来的原始schoolId
    private String CollectionOrigin;

    //精品资源库收藏过来的资源
    private boolean IsQualityCourse;

    private int sourceFromType;//资源的来自模块

    private boolean isOnlineSchool;
    private String resProperties;

    public String getResProperties() {
        return resProperties;
    }

    public void setResProperties(String resProperties) {
        this.resProperties = resProperties;
    }

    public boolean isOnlineSchool() {
        return isOnlineSchool;
    }

    public void setIsOnlineSchool(boolean onlineSchool) {
        isOnlineSchool = onlineSchool;
    }

    public int getSourceFromType() {
        return sourceFromType;
    }

    public void setSourceFromType(int sourceFromType) {
        this.sourceFromType = sourceFromType;
    }


    public boolean isQualityCourse() {
        return IsQualityCourse;
    }

    public void setIsQualityCourse(boolean qualityCourse) {
        IsQualityCourse = qualityCourse;
    }

    public String getCollectionOrigin() {
        return CollectionOrigin;
    }

    public void setCollectionOrigin(String collectionOrigin) {
        CollectionOrigin = collectionOrigin;
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

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setIsFromShowShow(boolean isFromShowShow){
        this.isFromShowShow=isFromShowShow;
    }
    public boolean isFromShowShow(){
        return this.isFromShowShow;
    }

    public void setCampusPatrolTag(boolean campusPatrolTag) {
        isCampusPatrolTag = campusPatrolTag;
    }

    public boolean isCampusPatrolTag() {
        return isCampusPatrolTag;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int getCollectioncount() {
        return collectioncount;
    }

    public void setCollectioncount(int collectioncount) {
        this.collectioncount = collectioncount;
    }

    public int getCourseporperty() {
        return courseporperty;
    }

    public void setCourseporperty(int courseporperty) {
        this.courseporperty = courseporperty;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getResourceurl() {
        return resourceurl;
    }

    public void setResourceurl(String resourceurl) {
        this.resourceurl = resourceurl;
    }

    public String getShareurl() {
        return shareurl;
    }

    public void setShareurl(String shareurl) {
        this.shareurl = shareurl;
    }

    public String getSetexcellentcoursename() {
        return setexcellentcoursename;
    }

    public void setSetexcellentcoursename(String setexcellentcoursename) {
        this.setexcellentcoursename = setexcellentcoursename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSavename() {
        return savename;
    }

    public void setSavename(String savename) {
        this.savename = savename;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(int totaltime) {
        this.totaltime = totaltime;
    }

    public int getCoursetype() {
        return coursetype;
    }

    public void setCoursetype(int coursetype) {
        this.coursetype = coursetype;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getSetexcellentcoursedate() {
        return setexcellentcoursedate;
    }

    public void setSetexcellentcoursedate(String setexcellentcoursedate) {
        this.setexcellentcoursedate = setexcellentcoursedate;
    }

    public boolean isexcellentcourse() {
        return isexcellentcourse;
    }

    public void setIsexcellentcourse(boolean isexcellentcourse) {
        this.isexcellentcourse = isexcellentcourse;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public String getOriginname() {
        return originname;
    }

    public void setOriginname(String originname) {
        this.originname = originname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDownloadtimes() {
        return downloadtimes;
    }

    public void setDownloadtimes(int downloadtimes) {
        this.downloadtimes = downloadtimes;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getCreateid() {
        return createid;
    }

    public void setCreateid(int createid) {
        this.createid = createid;
    }

    public int getTextbooksversion() {
        return textbooksversion;
    }

    public void setTextbooksversion(int textbooksversion) {
        this.textbooksversion = textbooksversion;
    }

    public int getSetexcellentcourseid() {
        return setexcellentcourseid;
    }

    public void setSetexcellentcourseid(int setexcellentcourseid) {
        this.setexcellentcourseid = setexcellentcourseid;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public String getGroupscode() {
        return groupscode;
    }

    public void setGroupscode(String groupscode) {
        this.groupscode = groupscode;
    }

    public int getFascicule() {
        return fascicule;
    }

    public void setFascicule(int fascicule) {
        this.fascicule = fascicule;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getCourseTypeName() {
        return courseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
    }

    public int getXueduan() {
        return xueduan;
    }

    public void setXueduan(int xueduan) {
        this.xueduan = xueduan;
    }

    public String getXueduanName() {
        return xueduanName;
    }

    public void setXueduanName(String xueduanName) {
        this.xueduanName = xueduanName;
    }

    public String getMicroId() {
        return microId;
    }

    public void setMicroId(String microId) {
        this.microId = microId;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(boolean collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public int getPraisenum() {
        return praisenum;
    }

    public void setPraisenum(int praisenum) {
        this.praisenum = praisenum;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setIsPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public String getShareAddress() {
        return shareAddress;
    }

    public void setShareAddress(String shareAddress) {
        this.shareAddress = shareAddress;
    }

    public boolean isSlide() {
        return isSlide;
    }

    public void setIsSlide(boolean isSlide) {
        this.isSlide = isSlide;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public boolean isHomework() {
        return isHomework;
    }

    public void setIsHomework(boolean isHomework) {
        this.isHomework = isHomework;
    }

    public boolean isSplitCourse() {
        return isSplitCourse;
    }

    public void setIsSplitCourse(boolean isSplitCourse) {
        this.isSplitCourse = isSplitCourse;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isMyBookShelf() {
        return isMyBookShelf;
    }

    public void setIsMyBookShelf(boolean isMyBookShelf) {
        this.isMyBookShelf = isMyBookShelf;
    }

    public int getCourseSourceType() {
        return courseSourceType;
    }

    public void setCourseSourceType(int courseSourceType) {
        this.courseSourceType = courseSourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getEditResourceUrl() {
        return editResourceUrl;
    }

    public void setEditResourceUrl(String editResourceUrl) {
        this.editResourceUrl = editResourceUrl;
    }

    public String getEditMicroId() {
        return editMicroId;
    }

    public void setEditMicroId(String editMicroId) {
        this.editMicroId = editMicroId;
    }

    public String getEditUpdateTime() {
        return editUpdateTime;
    }

    public void setEditUpdateTime(String editUpdateTime) {
        this.editUpdateTime = editUpdateTime;
    }

    public ContactItem getContactItem() {
        return contactItem;
    }

    public void setContactItem(ContactItem contactItem) {
        this.contactItem = contactItem;
    }

    public CourseDTO toCourseDTO() {
        return new CourseDTO(String.valueOf(id), isRead);
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public CollectParams getCollectParams() {
        CollectParams params = new CollectParams();
        params.microId = String.valueOf(id);
        params.thumbnail = imgurl;
        params.title = nickname;
//        params.resourceType = resourceType;
        params.resourceType = type;
        params.author = code;
        params.description = description;
        params.knowledge = point;
        params.resourceUrl = resourceurl;
        params.versionCode = 1;
        params.collectionStatus = collectionStatus;
        params.primaryKey = String.valueOf(id);
        params.isSlitCourse = isSplitCourse;
        params.updateTime = updateTime;
        params.isMyBookShelf = isMyBookShelf;
        params.courseSourceType = courseSourceType;
        params.resourceId = resourceId;
        params.bookId = bookId;
        params.sectionId = sectionId;
        params.editResourceUrl = editResourceUrl;
        params.editMicroId = editMicroId;
        params.editUpdateTime = editUpdateTime;
        if(contactItem != null) {
            params.chatType = contactItem.chatType;
            params.userId = contactItem.hxId;
            params.userName = contactItem.name;
            params.userIcon = contactItem.icon;
            params.isChatForbidden = contactItem.isChatForbidden;
        }
        params.IsQualityCourse = IsQualityCourse;
        params.CollectionOrigin = CollectionOrigin;
        params.isPublicRes = isPublicRescourse;
        params.isCollected = isCollected;
        params.totalTime = totaltime;
        params.sourceFromType = sourceFromType;
        return params;
    }

    public ShareInfo getShareInfo(Activity activity) {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(nickname);
        shareInfo.setContent(createname);
        if(TextUtils.isEmpty(shareAddress)) {
            shareAddress = shareurl;
        }
        if(type == ResType.RES_TYPE_NOTE) {
            shareInfo.setTargetUrl(shareAddress);
            UMImage umImage;
            if(!TextUtils.isEmpty(imgurl)) {
                umImage = new UMImage(activity, imgurl);
            } else {
                umImage = new UMImage(activity, R.drawable.ic_launcher);
            }
            shareInfo.setuMediaObject(umImage);
        } else {
            if(TextUtils.isEmpty(shareAddress)) {
                shareAddress = ShareSettings.WAWAWEIKE_SHARE_URL + id;
            }
            shareInfo.setTargetUrl(shareAddress);
            UMImage umImage;
            if(!TextUtils.isEmpty(imgurl)) {
                umImage = new UMImage(activity, imgurl);
            } else {
                umImage = new UMImage(activity, R.drawable.ic_launcher);
            }
            shareInfo.setuMediaObject(umImage);
//            UMVideo umVideo = new UMVideo(shareAddress);
//            umVideo.setThumb(imgurl);
//            shareInfo.setuMediaObject(umVideo);
        }
        shareInfo.setIsPublicRescourse(isPublicRescourse);
        shareInfo.setSharedResource(getSharedResource());
        shareInfo.setParentId(parentId);
        if (resourceType == ResType.RES_TYPE_ONEPAGE
                || resourceType == ResType.RES_TYPE_COURSE_SPEAKER
                || resourceType == ResType.RES_TYPE_OLD_COURSE
                || resourceType == ResType.RES_TYPE_COURSE
                || resourceType == ResType.RES_TYPE_STUDY_CARD
                || resourceType == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD)
                || resourceType == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_COURSE_SPEAKER)){
            String typeName = activity.getString(R.string.retell_course);
            if (resourceType == ResType.RES_TYPE_STUDY_CARD
                    || resourceType == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD)){
                typeName = activity.getString(R.string.make_task);
            }
            nickname = activity.getString(R.string.str_resources_tag,typeName,nickname);
            shareInfo.setTitle(nickname);
            if (!TextUtils.isEmpty(createname)) {
                String content = createname;
                if (content.length() > 10){
                    content = content.substring(0,10);
                    content = content + "...";
                }
                content = content + "\n" + activity.getString(R.string
                        .Str_view_people, String.valueOf(viewcount));
                shareInfo.setContent(content);
            }
        }
        return shareInfo;
    }

    public SharedResource getSharedResource() {
        SharedResource resource = new SharedResource();
        resource.setId(String.valueOf(id));
        int resType = type % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER
            ||resType == ResType.RES_TYPE_OLD_COURSE) {
            resource.setType(SharedResource.RESOURCE_TYPE_STREAM);
        } else if(resType == ResType.RES_TYPE_NOTE) {
            resource.setType(SharedResource.RESOURCE_TYPE_NOTE);
        } else if(resType == ResType.RES_TYPE_ONEPAGE||resType == ResType.RES_TYPE_STUDY_CARD) {
            resource.setType(SharedResource.RESOURCE_TYPE_FILE);
        }
        resource.setTitle(nickname);
        resource.setThumbnailUrl(imgurl);
        resource.setUrl(resourceurl);
        if(TextUtils.isEmpty(shareAddress)) {
            shareAddress = shareurl;
        }
        if(!TextUtils.isEmpty(shareAddress)) {
            resource.setShareUrl(shareAddress);
        }

        resource.setAuthorId(code);
        resource.setAuthorName(createname);
        if(TextUtils.isEmpty(description)) {
            resource.setDescription("");
        } else {
            resource.setDescription(description);
        }
        resource.setPrimaryKey(String.valueOf(id));
        resource.setResourceType(type);
        resource.setScreenType(screenType);
        resource.setCreateTime(createtime);
        resource.setUpdateTime(updateTime);
        resource.setIsPublicRescourse(isPublicRescourse);
        resource.setParentId(parentId);
        return resource;
    }

    public CourseShareData toCourseShareData() {
        CourseShareData shareData = new CourseShareData();
        shareData.setTitle(nickname);
        shareData.setAuthor(createname);
        shareData.setThumbnail(imgurl);
        shareData.setId(id);
        if(TextUtils.isEmpty(shareAddress)) {
            shareAddress = shareurl;
        }
        if(!TextUtils.isEmpty(shareAddress)) {
            shareData.setShareAddress(shareAddress);
        }
        shareData.setSharedResource(getSharedResource());
        return shareData;
    }

    public NewResourceInfo toNewResourceInfo() {
        NewResourceInfo resource = new NewResourceInfo();
        resource.setMicroId(String.valueOf(id));
        resource.setResourceType(type);
        resource.setTitle(nickname);
        resource.setThumbnail(imgurl);
        resource.setResourceUrl(resourceurl);
        if(TextUtils.isEmpty(shareAddress)) {
            shareAddress = shareurl;
        }
        if(!TextUtils.isEmpty(shareAddress)) {
            resource.setShareAddress(shareAddress);
        }
        resource.setAuthorId(code);
        resource.setAuthorName(createname);
        if(!TextUtils.isEmpty(description)) {
            resource.setDescription(description);
        }
        resource.setResourceType(type);
        resource.setScreenType(screenType);
        resource.setCreatedTime(createtime);
        resource.setUpdatedTime(updateTime);
        resource.setPointNumber(praisenum);
        resource.setReadNumber(viewcount);
        resource.setFileSize(size);
        resource.setIsPublicResource(isPublicRescourse);
        resource.setParentId(parentId);
        resource.setIsQualityCourse(isQualityCourse());
        resource.setCollectionOrigin(CollectionOrigin);
        resource.setTotalTime(totaltime);
        resource.setSourceFromType(sourceFromType);
        return resource;
    }

    public String getIdType() {
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        builder.append("-");
        builder.append(resourceType);
        return builder.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.subject);
        dest.writeInt(this.collectioncount);
        dest.writeInt(this.courseporperty);
        dest.writeInt(this.parentid);
        dest.writeInt(this.type);
        dest.writeString(this.createtime);
        dest.writeInt(this.id);
        dest.writeString(this.thumbnail);
        dest.writeString(this.resourceurl);
        dest.writeString(this.shareurl);
        dest.writeString(this.setexcellentcoursename);
        dest.writeString(this.description);
        dest.writeString(this.savename);
        dest.writeInt(this.grade);
        dest.writeInt(this.totaltime);
        dest.writeInt(this.coursetype);
        dest.writeString(this.imgurl);
        dest.writeString(this.setexcellentcoursedate);
        dest.writeByte(isexcellentcourse ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
        dest.writeString(this.nickname);
        dest.writeByte(isdelete ? (byte) 1 : (byte) 0);
        dest.writeInt(this.viewcount);
        dest.writeString(this.originname);
        dest.writeString(this.code);
        dest.writeInt(this.downloadtimes);
        dest.writeInt(this.size);
        dest.writeString(this.unit);
        dest.writeString(this.point);
        dest.writeInt(this.createid);
        dest.writeInt(this.textbooksversion);
        dest.writeInt(this.setexcellentcourseid);
        dest.writeString(this.createname);
        dest.writeString(this.groupscode);
        dest.writeInt(this.fascicule);
        dest.writeByte(isRead ? (byte) 1 : (byte) 0);
        dest.writeString(this.courseTypeName);
        dest.writeInt(this.xueduan);
        dest.writeString(this.xueduanName);
        dest.writeString(this.microId);
        dest.writeInt(this.resourceType);
        dest.writeInt(this.sourceType);
        dest.writeString(this.primaryKey);
        dest.writeByte(collectionStatus ? (byte) 1 : (byte) 0);
        dest.writeByte(isCollected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.commentnum);
        dest.writeInt(this.praisenum);
        dest.writeByte(isPraise ? (byte) 1 : (byte) 0);
        dest.writeString(this.shareAddress);
        dest.writeByte(isSlide ? (byte) 1 : (byte) 0);
        dest.writeInt(this.screenType);
        dest.writeByte(isHomework ? (byte) 1 : (byte) 0);
        dest.writeString(updateTime);
        dest.writeByte(isMyBookShelf ? (byte) 1 : (byte) 0);
        dest.writeInt(courseSourceType);
        dest.writeString(resourceId);
        dest.writeString(this.bookId);
        dest.writeString(this.sectionId);
        dest.writeString(this.editResourceUrl);
        dest.writeString(this.editMicroId);
        dest.writeString(this.editUpdateTime);
        dest.writeString(this.schoolName);
        dest.writeByte(isCampusPatrolTag ? (byte) 1 : (byte) 0);
        dest.writeByte(isFromShowShow ? (byte)1:(byte)0);
        dest.writeString(this.schoolId);
        dest.writeByte(isTeacher ? (byte)1:(byte)0);
        dest.writeByte(this.isPublicRescourse ? (byte)1:(byte)0);
        dest.writeString(this.parentId);
        dest.writeByte(this.IsQualityCourse ? (byte)1:(byte)0);
        dest.writeString(this.CollectionOrigin);
        dest.writeInt(this.sourceFromType);
        dest.writeByte(this.isOnlineSchool ? (byte)1:(byte)0);
        dest.writeString(this.resProperties);
    }

    public CourseInfo() {
    }

    protected CourseInfo(Parcel in) {
        this.subject = in.readInt();
        this.collectioncount = in.readInt();
        this.courseporperty = in.readInt();
        this.parentid = in.readInt();
        this.type = in.readInt();
        this.createtime = in.readString();
        this.id = in.readInt();
        this.thumbnail = in.readString();
        this.resourceurl = in.readString();
        this.shareurl = in.readString();
        this.setexcellentcoursename = in.readString();
        this.description = in.readString();
        this.savename = in.readString();
        this.grade = in.readInt();
        this.totaltime = in.readInt();
        this.coursetype = in.readInt();
        this.imgurl = in.readString();
        this.setexcellentcoursedate = in.readString();
        this.isexcellentcourse = in.readByte() != 0;
        this.status = in.readInt();
        this.nickname = in.readString();
        this.isdelete = in.readByte() != 0;
        this.viewcount = in.readInt();
        this.originname = in.readString();
        this.code = in.readString();
        this.downloadtimes = in.readInt();
        this.size = in.readInt();
        this.unit = in.readString();
        this.point = in.readString();
        this.createid = in.readInt();
        this.textbooksversion = in.readInt();
        this.setexcellentcourseid = in.readInt();
        this.createname = in.readString();
        this.groupscode = in.readString();
        this.fascicule = in.readInt();
        this.isRead = in.readByte() != 0;
        this.courseTypeName = in.readString();
        this.xueduan = in.readInt();
        this.xueduanName = in.readString();
        this.microId = in.readString();
        this.resourceType = in.readInt();
        this.sourceType = in.readInt();
        this.primaryKey = in.readString();
        this.collectionStatus = in.readByte() != 0;
        this.isCollected = in.readByte() != 0;
        this.commentnum = in.readInt();
        this.praisenum = in.readInt();
        this.isPraise = in.readByte() != 0;
        this.shareAddress = in.readString();
        this.isSlide = in.readByte() != 0;
        this.screenType = in.readInt();
        this.isHomework = in.readByte() != 0;
        this.updateTime = in.readString();
        this.isMyBookShelf = in.readByte() != 0;
        this.courseSourceType = in.readInt();
        this.resourceId = in.readString();
        this.bookId = in.readString();
        this.sectionId = in.readString();
        this.editResourceUrl = in.readString();
        this.editMicroId = in.readString();
        this.editUpdateTime = in.readString();
        this.schoolName=in.readString();
        this.isCampusPatrolTag = in.readByte() != 0;
        this.isFromShowShow=in.readByte()!=0;
        this.schoolId=in.readString();
        this.isTeacher=in.readByte()!=0;
        this.isPublicRescourse=in.readByte()!=0;
        this.parentId=in.readString();
        this.IsQualityCourse=in.readByte()!=0;
        this.CollectionOrigin=in.readString();
        this.sourceFromType = in.readInt();
        this.isOnlineSchool=in.readByte()!=0;
        this.resProperties=in.readString();
    }

    public static final Creator<CourseInfo> CREATOR = new Creator<CourseInfo>() {
        public CourseInfo createFromParcel(Parcel source) {
            return new CourseInfo(source);
        }

        public CourseInfo[] newArray(int size) {
            return new CourseInfo[size];
        }
    };


    public CourseInfo toCourseInfo(ClassNotificationEntity data){
        String microId = data.getMicroId();
        if(!TextUtils.isEmpty(microId) && microId.contains("-")) {
            microId = microId.substring(0, microId.indexOf("-"));
        }
        if (!TextUtils.isDigitsOnly(microId)) {
            return null;
        }
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setNickname(data.getTitle());
        courseInfo.setOriginname(data.getTitle());
        courseInfo.setId(Integer.valueOf(microId));
        courseInfo.setImgurl(data.getThumbnail());
        courseInfo.setCreatename(data.getAuthorName());
        courseInfo.setCode(data.getAuthorId());
        String resourceUrl = data.getResourceUrl();
        if(!TextUtils.isEmpty(resourceUrl) && resourceUrl.contains("?")) {
            resourceUrl = resourceUrl.substring(0, resourceUrl.lastIndexOf("?"));
        }
        courseInfo.setResourceurl(resourceUrl);
        courseInfo.setPrimaryKey(data.getId());
        if (!TextUtils.isEmpty(data.getResourceType())) {
            courseInfo.setResourceType(Integer.valueOf(data.getResourceType()));
            courseInfo.setType(Integer.valueOf(data.getResourceType()));
        }
        courseInfo.setCreatetime(data.getCreatedTime());
        courseInfo.setUpdateTime(data.getUpdatedTime());
        courseInfo.setShareAddress(data.getShareAddress());
        courseInfo.setIsSlide(false);
        courseInfo.setDescription(data.getDescription());
        courseInfo.setScreenType(data.getScreenType());
        if (!TextUtils.isEmpty(data.getFileSize())){
            courseInfo.setSize(Integer.valueOf(data.getFileSize()));
        }
        return courseInfo;
    }
}
