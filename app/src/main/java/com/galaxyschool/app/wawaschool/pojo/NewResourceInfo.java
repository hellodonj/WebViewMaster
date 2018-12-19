package com.galaxyschool.app.wawaschool.pojo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.libs.filedownloader.FileInfo;
import com.oosic.apps.share.SharedResource;

public class NewResourceInfo implements Parcelable{

    public static final int TYPE_SCHOOL_ACTIVITY = 1;
    public static final int TYPE_SCHOOL_SIGN_ACTIVITY = 2;
    public static final int TYPE_SCHOOL_VOTE_ACTIVITY = 3;
    public static final int TYPE_SCHOOL_NEWS = 4;
    public static final int TYPE_SCHOOL_CAMPUS_COMMUNITY = 5;//校园社区
    public static final int TYPE_SCHOOL_RESOURCE_SHARING = 6;//资源共享
    public static final int TYPE_SCHOOL_RESEARCH_AND_DISCUSSION = 7;//研学讨论
    public static final int TYPE_SCHOOL_LEISURE_TIME= 8;//休闲时光

    public static final int TYPE_CLASS_HOMEWORK = 1;//作业
    public static final int TYPE_CLASS_NOTICE = 2;//通知
    public static final int TYPE_CLASS_SHOW = 3;//秀秀
    public static final int TYPE_CLASS_COURSE = 4;//翻转课堂
    public static final int TYPE_CLASS_LECTURE = 6;//创意学堂
    public static final int TYPE_CLASS_STUDY_TASK = 7;
    public static final int TYPE_CLASS_SCHOOL_MOVEMENT = 8;//校园动态

    public static final int TYPE_PLATFORM_NOTICE = 1;
    public static final int TYPE_PLATFORM_NEWS = 2;

    public static final int STATE_GOING = 1;
    public static final int STATE_OVER = 2;

    private String Id;
    private String Title;
    private int Type;
    private int State;
    private String MicroId;
    private String Thumbnail;
    private int ResourceType;
    private String ResourceUrl;
    private String ShareAddress;
    private int ReadNumber;
    private int CommentNumber;
    private int PointNumber;
    private String AuthorId;
    private String AuthorName;
    private String Subject;
    private String CreatedTime;
    private int FileSize;
    private int SourceType;
    private String GroupId;
    private int ScreenType;
    private boolean IsRead;
    private String UpdatedTime;
    private boolean IsMyBookShelf;
    private String OutlineId;
    private String SectionId;
    private String BookId;
    private String OldMicroId;
    private boolean isSelect;
    private boolean isAdded;
    private String Description;
    private String ResourceId;
    private String localZipPath;
    private boolean isDownloaded;
    private int Role;
    //创意秀显示学校和班级名称
     private String SchoolName;
     private String ClassName;
    private String parentMicroId;
    //是否是来自lq的任务单
    private boolean isFromLqTask;
    //来自校本资源库
    private boolean isFromSchoolResource;
    //来自空中课堂
    private boolean isFromAirClass;
    //是不是扫码是任务 扫描lq慕课 任务单 如果判断是否有查看课程的选项
    private boolean isScanTask;
    /******************** noc大赛*************************/
    private  String vuid;//乐视播放用到

    private String  nocCreateTime;//noc时间
    private String       nocRemark;//noc简介
    private String   nocEntryNum;//noc编号
    private String       nocOrgName;//noc来源
    private int nocNameForType;//noc 参赛名义
    private int nocPraiseNum;//noc点赞数

    //学习任务
    private StudyTaskInfo studyTaskInfo;

    //乐视返回leValue

    private String LeValue;
    private int LeStatus;

    private boolean isPublicResource = true;
    private String parentId;

    private boolean isHasPermission;

    //收藏过来的原始schoolId
    private String CollectionOrigin;

    //精品资源库收藏过来的资源
    private boolean IsQualityCourse;
    //根据条件判断是否隐藏收藏的按钮
    private boolean isHideCollectBtn = true;

    private int splitFlag;//判断是否需要拆分 0 (不需要) 1(需要)

    private int totalTime;//资源的总时长

    private int sourceFromType;//资源的来自模块

    private boolean isVipSchool;//是不是vipSchool中的资源
    private boolean hideDownLoadBtn = true;
    private String ResProperties;
    private String UpdateTime;
    private String Point;//总分

    public String getPoint() {
        return Point;
    }

    public void setPoint(String point) {
        Point = point;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }


    public String getResProperties() {
        return ResProperties;
    }

    public void setResProperties(String resProperties) {
        ResProperties = resProperties;
    }

    public boolean isVipSchool() {
        return isVipSchool;
    }

    public void setIsVipSchool(boolean vipSchool) {
        isVipSchool = vipSchool;
    }

    public boolean isHideDownLoadBtn() {
        return hideDownLoadBtn;
    }

    public void setHideDownLoadBtn(boolean hideDownLoadBtn) {
        this.hideDownLoadBtn = hideDownLoadBtn;
    }

    public int getSourceFromType() {
        return sourceFromType;
    }

    public void setSourceFromType(int sourceFromType) {
        this.sourceFromType = sourceFromType;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getSplitFlag() {
        return splitFlag;
    }

    public void setSplitFlag(int splitFlag) {
        this.splitFlag = splitFlag;
    }

    public boolean isHideCollectBtn() {
        return isHideCollectBtn;
    }

    public void setIsHideCollectBtn(boolean hideCollectBtn) {
        isHideCollectBtn = hideCollectBtn;
    }

    public int getLeStatus() {
        return LeStatus;
    }

    public void setLeStatus(int leStatus) {
        LeStatus = leStatus;
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


    public boolean isHasPermission() {
        return isHasPermission;
    }

    public void setIsHasPermission(boolean hasPermission) {
        isHasPermission = hasPermission;
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isPublicResource() {
        return isPublicResource;
    }

    public void setIsPublicResource(boolean publicResource) {
        isPublicResource = publicResource;
    }

    public void setStudyTaskInfo(StudyTaskInfo studyTaskInfo) {
        this.studyTaskInfo = studyTaskInfo;
    }

    public boolean isScanTask() {
        return isScanTask;
    }

    public void setIsScanTask(boolean scanTask) {
        isScanTask = scanTask;
    }

    public String getLeValue() {
        return LeValue;
    }

    public void setLeValue(String leValue) {
        LeValue = leValue;
    }

    public boolean isFromAirClass() {
        return isFromAirClass;
    }

    public void setIsFromAirClass(boolean fromAirClass) {
        isFromAirClass = fromAirClass;
    }

    public StudyTaskInfo getStudyTaskInfo() {
        return studyTaskInfo;
    }

    public int getNocNameForType() {
        return nocNameForType;
    }

    public void setNocNameForType(int nocNameForType) {
        this.nocNameForType = nocNameForType;
    }


    /******************** noc大赛*************************/
    private boolean IsMyCollection;
    public boolean isFromLqTask() {
        return isFromLqTask;
    }

    public boolean isMyCollection() {
        return IsMyCollection;
    }

    public void setIsMyCollection(boolean myCollection) {
        IsMyCollection = myCollection;
    }

    public void setIsFromLqTask(boolean fromLqTask) {
        isFromLqTask = fromLqTask;
    }

    public boolean isFromSchoolResource() {
        return isFromSchoolResource;
    }

    public void setIsFromSchoolResource(boolean fromType) {
        isFromSchoolResource = fromType;
    }

    public void setIsAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    public boolean getIsAdded() {
        return isAdded;
    }

    public int getNocPraiseNum() {
        return nocPraiseNum;
    }

    public void setNocPraiseNum(int nocPraiseNum) {
        this.nocPraiseNum = nocPraiseNum;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getMicroId() {
        if (!TextUtils.isEmpty(MicroId)){
            if (MicroId.contains("-")){
                return MicroId.split("-")[0];
            }
        }
        return MicroId;
    }

    public void setMicroId(String microId) {
        MicroId = microId;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public int getResourceType() {
        return ResourceType;
    }

    public void setResourceType(int resourceType) {
        ResourceType = resourceType;
    }

    public String getResourceUrl() {
        return ResourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        ResourceUrl = resourceUrl;
    }

    public String getShareAddress() {
        return ShareAddress;
    }

    public void setShareAddress(String shareAddress) {
        ShareAddress = shareAddress;
    }

    public int getReadNumber() {
        return ReadNumber;
    }

    public void setReadNumber(int readNumber) {
        ReadNumber = readNumber;
    }

    public int getCommentNumber() {
        return CommentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        CommentNumber = commentNumber;
    }

    public int getPointNumber() {
        return PointNumber;
    }

    public void setPointNumber(int pointNumber) {
        PointNumber = pointNumber;
    }

    public String getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(String authorId) {
        AuthorId = authorId;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public int getFileSize() {
        return FileSize;
    }

    public void setFileSize(int fileSize) {
        FileSize = fileSize;
    }

    public int getSourceType() {
        return SourceType;
    }

    public void setSourceType(int sourceType) {
        SourceType = sourceType;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public int getScreenType() {
        return ScreenType;
    }

    public void setScreenType(int screenType) {
        ScreenType = screenType;
    }

    public boolean isRead() {
        return IsRead;
    }

    public void setIsRead(boolean isRead) {
        IsRead = isRead;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.UpdatedTime = updatedTime;
    }

    public boolean isMyBookShelf() {
        return IsMyBookShelf;
    }

    public void setIsMyBookShelf(boolean isMyBookShelf) {
        IsMyBookShelf = isMyBookShelf;
    }

    public String getOutlineId() {
        return OutlineId;
    }

    public void setOutlineId(String outlineId) {
        OutlineId = outlineId;
    }

    public String getSectionId() {
        return SectionId;
    }

    public void setSectionId(String sectionId) {
        SectionId = sectionId;
    }

    public String getBookId() {
        return BookId;
    }

    public void setBookId(String bookId) {
        BookId = bookId;
    }

    public String getOldMicroId() {
        return OldMicroId;
    }

    public void setOldMicroId(String oldMicroId) {
        OldMicroId = oldMicroId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getResourceId() {
        if (TextUtils.isEmpty(ResourceId)) {
            ResourceId = new StringBuilder(MicroId).append("-").append(ResourceType).toString();
        }
        return ResourceId;
    }

    public void setResourceId(String resourceId) {
        ResourceId = resourceId;
    }

    public String getLocalZipPath() {
        return localZipPath;
    }

    public void setLocalZipPath(String localZipPath) {
        this.localZipPath = localZipPath;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getParentMicroId() {
        return parentMicroId;
    }

    public void setParentMicroId(String parentMicroId) {
        this.parentMicroId = parentMicroId;
    }

    public String getNocOrgName() {
        return nocOrgName;
    }

    public void setNocOrgName(String nocOrgName) {
        this.nocOrgName = nocOrgName;
    }

    public String getNocEntryNum() {
        return nocEntryNum;
    }

    public void setNocEntryNum(String nocEntryNum) {
        this.nocEntryNum = nocEntryNum;
    }

    public String getNocRemark() {
        return nocRemark;
    }

    public void setNocRemark(String nocRemark) {
        this.nocRemark = nocRemark;
    }

    public String getNocCreateTime() {
        return nocCreateTime;
    }

    public void setNocCreateTime(String nocCreateTime) {
        this.nocCreateTime = nocCreateTime;
    }

    public String getVuid() {
        return vuid;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }
    public String getIdType() {
        return MicroId + "-" + ResourceType;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public CourseInfo getCourseInfo() {
        String microId = MicroId;
        if(!TextUtils.isEmpty(microId) && microId.contains("-")) {
            microId = microId.substring(0, microId.indexOf("-"));
        }
        if (!TextUtils.isDigitsOnly(microId)) {
            return null;
        }
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setNickname(Title);
        courseInfo.setId(Integer.valueOf(microId));
        courseInfo.setImgurl(Thumbnail);
        courseInfo.setCreatename(AuthorName);
        courseInfo.setCode(AuthorId);
        String resourceUrl = ResourceUrl;
        if(!TextUtils.isEmpty(resourceUrl) && resourceUrl.contains("?")) {
            resourceUrl = resourceUrl.substring(0, resourceUrl.lastIndexOf("?"));
        }
        courseInfo.setResourceurl(resourceUrl);
        courseInfo.setPrimaryKey(Id);
        courseInfo.setResourceType(ResourceType);
        courseInfo.setType(ResourceType);
        courseInfo.setCreatetime(CreatedTime);
        courseInfo.setUpdateTime(UpdatedTime);
        courseInfo.setShareAddress(ShareAddress);
        courseInfo.setIsSlide(false);
        courseInfo.setDescription(Description);
        int resType = ResourceType % ResType.RES_TYPE_BASE;
        if (resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            courseInfo.setIsSlide(true);
        }
        if (ResourceType > ResType.RES_TYPE_BASE && resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            courseInfo.setIsSplitCourse(true);
        } else {
            courseInfo.setIsSplitCourse(false);
        }
        courseInfo.setScreenType(ScreenType);
        courseInfo.setIsMyBookShelf(IsMyBookShelf);
        courseInfo.setSize(FileSize);
        courseInfo.setSchoolName(SchoolName);
        courseInfo.setIsPublicRescourse(isPublicResource);
        courseInfo.setParentId(parentId);
        courseInfo.setIsQualityCourse(IsQualityCourse);
        courseInfo.setCollectionOrigin(CollectionOrigin);
        courseInfo.setTotaltime(totalTime);
        courseInfo.setSourceFromType(sourceFromType);
        courseInfo.setResProperties(ResProperties);
        courseInfo.setViewcount(ReadNumber);
        return courseInfo;
    }

    public SharedResource getSharedResource() {
        SharedResource resource = new SharedResource();
        int resType = ResourceType % ResType.RES_TYPE_BASE;
        if (resType == ResType.RES_TYPE_ONEPAGE) {
            resource.setType(SharedResource.RESOURCE_TYPE_FILE);
        } else if (resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER
                || resType == ResType.RES_TYPE_OLD_COURSE) {
            resource.setType(SharedResource.RESOURCE_TYPE_STREAM);
        } else if (resType == ResType.RES_TYPE_NOTE) {
            resource.setType(SharedResource.RESOURCE_TYPE_NOTE);
        }

        resource.setId(MicroId);
        resource.setThumbnailUrl(Thumbnail);
        resource.setTitle(Title);
        resource.setUrl(ResourceUrl);
        resource.setShareUrl(ShareAddress);
        resource.setAuthorId(AuthorId);
        resource.setAuthorName(AuthorName);
        resource.setDescription(Description);
        resource.setPrimaryKey(Id);
        resource.setSourceType(0);
        if (TextUtils.isEmpty(resource.getDescription())
                || resource.getTitle().equals(resource.getDescription())) {
            resource.setDescription(resource.getAuthorName());
        }
        resource.setIsPublicRescourse(isPublicResource);
        resource.setParentId(parentId);
        return resource;
    }

    public static String getSchoolResourceTypeString(Context context, int type) {
        if (type == TYPE_SCHOOL_ACTIVITY) {
            return context.getString(R.string.school_style);
        } else if (type == TYPE_SCHOOL_SIGN_ACTIVITY) {
            return context.getString(R.string.sign_activity);
        } else if (type == TYPE_SCHOOL_SIGN_ACTIVITY) {
            return context.getString(R.string.vote_activity);
        } else if (type == TYPE_SCHOOL_NEWS) {
            return context.getString(R.string.news);
        }
        return "";
    }

    public static String getClassResourceTypeString(Context context, int type) {
        if (type == TYPE_CLASS_HOMEWORK) {
            return context.getString(R.string.homeworks);
        } else if (type == TYPE_CLASS_NOTICE) {
            return context.getString(R.string.notices);
        } else if (type == TYPE_CLASS_SHOW) {
            return context.getString(R.string.shows);
        } else if (type == TYPE_CLASS_COURSE) {
            return context.getString(R.string.courses);
        } else if (type == TYPE_CLASS_LECTURE) {
            return context.getString(R.string.lectures);
        }
        return "";
    }

    public static String getResourceStateString(Context context, int state) {
        if (state == STATE_GOING) {
            return context.getString(R.string.going);
        } else if (state == STATE_OVER) {
            return context.getString(R.string.over);
        }
        return "";
    }

    public boolean isMicroCourse() {
        int resType = ResourceType % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_OLD_COURSE || resType == ResType.RES_TYPE_COURSE
                || resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            return  true;
        }

        return false;
    }


    public NewResourceInfo() {
    }


    public FileInfo toFileInfo(String userId) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUserId(userId);
        fileInfo.setFileId(getResourceId());
        fileInfo.setFileName(Title);
        fileInfo.setFileUrl(ResourceUrl);
        fileInfo.setFileSize(FileSize);
        return fileInfo;
    }
    public boolean isOnePage() {
        int resType = ResourceType % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_ONEPAGE) {
            return  true;
        }
        return false;
    }

    public boolean isStudyCard() {
        int resType = ResourceType % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_STUDY_CARD) {
            return  true;
        }
        return false;
    }

    public boolean isMicroCourse19() {
        int resType = ResourceType % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            return  true;
        }
        return false;
    }

    public int getIntegerFormatMicroId(){
        String microId = MicroId;
        if(!TextUtils.isEmpty(microId)) {
            if (microId.contains("-")) {
                microId = microId.substring(0, microId.indexOf("-"));
                if (!TextUtils.isDigitsOnly(microId)) {
                    return -1;
                }
            }
        }else {
            return -1;
        }
        return Integer.valueOf(microId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.Title);
        dest.writeInt(this.Type);
        dest.writeInt(this.State);
        dest.writeString(this.MicroId);
        dest.writeString(this.Thumbnail);
        dest.writeInt(this.ResourceType);
        dest.writeString(this.ResourceUrl);
        dest.writeString(this.ShareAddress);
        dest.writeInt(this.ReadNumber);
        dest.writeInt(this.CommentNumber);
        dest.writeInt(this.PointNumber);
        dest.writeString(this.AuthorId);
        dest.writeString(this.AuthorName);
        dest.writeString(this.Subject);
        dest.writeString(this.CreatedTime);
        dest.writeInt(this.FileSize);
        dest.writeInt(this.SourceType);
        dest.writeString(this.GroupId);
        dest.writeInt(this.ScreenType);
        dest.writeByte(this.IsRead ? (byte) 1 : (byte) 0);
        dest.writeString(this.UpdatedTime);
        dest.writeByte(this.IsMyBookShelf ? (byte) 1 : (byte) 0);
        dest.writeString(this.OutlineId);
        dest.writeString(this.SectionId);
        dest.writeString(this.BookId);
        dest.writeString(this.OldMicroId);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAdded ? (byte) 1 : (byte) 0);
        dest.writeString(this.Description);
        dest.writeString(this.ResourceId);
        dest.writeString(this.localZipPath);
        dest.writeByte(this.isDownloaded ? (byte) 1 : (byte) 0);
        dest.writeString(this.SchoolName);
        dest.writeString(this.ClassName);
        dest.writeString(this.parentMicroId);
        dest.writeByte(this.isFromLqTask ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFromSchoolResource ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFromAirClass ? (byte) 1 : (byte) 0);
        dest.writeString(this.vuid);
        dest.writeString(this.nocCreateTime);
        dest.writeString(this.nocRemark);
        dest.writeString(this.nocEntryNum);
        dest.writeString(this.nocOrgName);
        dest.writeInt(this.nocNameForType);
        dest.writeByte(this.IsMyCollection ? (byte) 1 : (byte) 0);
        dest.writeSerializable(studyTaskInfo);
        dest.writeString(this.LeValue);
        dest.writeByte(this.isScanTask ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPublicResource ? (byte) 1 : (byte) 0);
        dest.writeString(this.parentId);
        dest.writeByte(this.isHasPermission ? (byte) 1 : (byte) 0);
        dest.writeString(this.CollectionOrigin);
        dest.writeByte(this.IsQualityCourse ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHideCollectBtn ? (byte) 1 : (byte) 0);
        dest.writeInt(this.splitFlag);
        dest.writeInt(this.totalTime);
        dest.writeInt(this.sourceFromType);
        dest.writeByte(this.isVipSchool ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hideDownLoadBtn ? (byte) 1 : (byte) 0);
        dest.writeInt(this.Role);
        dest.writeString(this.UpdateTime);
        dest.writeString(this.ResProperties);
    }

    protected NewResourceInfo(Parcel in) {
        this.Id = in.readString();
        this.Title = in.readString();
        this.Type = in.readInt();
        this.State = in.readInt();
        this.MicroId = in.readString();
        this.Thumbnail = in.readString();
        this.ResourceType = in.readInt();
        this.ResourceUrl = in.readString();
        this.ShareAddress = in.readString();
        this.ReadNumber = in.readInt();
        this.CommentNumber = in.readInt();
        this.PointNumber = in.readInt();
        this.AuthorId = in.readString();
        this.AuthorName = in.readString();
        this.Subject = in.readString();
        this.CreatedTime = in.readString();
        this.FileSize = in.readInt();
        this.SourceType = in.readInt();
        this.GroupId = in.readString();
        this.ScreenType = in.readInt();
        this.IsRead = in.readByte() != 0;
        this.UpdatedTime = in.readString();
        this.IsMyBookShelf = in.readByte() != 0;
        this.OutlineId = in.readString();
        this.SectionId = in.readString();
        this.BookId = in.readString();
        this.OldMicroId = in.readString();
        this.isSelect = in.readByte() != 0;
        this.isAdded = in.readByte() != 0;
        this.Description = in.readString();
        this.ResourceId = in.readString();
        this.localZipPath = in.readString();
        this.isDownloaded = in.readByte() != 0;
        this.SchoolName = in.readString();
        this.ClassName = in.readString();
        this.parentMicroId = in.readString();
        this.isFromLqTask = in.readByte() != 0;
        this.isFromSchoolResource = in.readByte() != 0;
        this.isFromAirClass = in.readByte() != 0;
        this.vuid = in.readString();
        this.nocCreateTime = in.readString();
        this.nocRemark = in.readString();
        this.nocEntryNum = in.readString();
        this.nocOrgName = in.readString();
        this.nocNameForType = in.readInt();
        this.IsMyCollection = in.readByte() != 0;
        this.studyTaskInfo = (StudyTaskInfo) in.readSerializable();
        this.LeValue = in.readString();
        this.isScanTask = in.readByte() != 0;
        this.isPublicResource = in.readByte() != 0;
        this.parentId = in.readString();
        this.isHasPermission = in.readByte() != 0;
        this.CollectionOrigin = in.readString();
        this.IsQualityCourse = in.readByte() != 0;
        this.isHideCollectBtn = in.readByte() != 0;
        this.splitFlag = in.readInt();
        this.totalTime = in.readInt();
        this.sourceFromType = in.readInt();
        this.isVipSchool = in.readByte() != 0;
        this.hideDownLoadBtn = in.readByte() != 0;
        this.Role = in.readInt();
        this.UpdateTime = in.readString();
        this.ResProperties = in.readString();
    }

    public static final Creator<NewResourceInfo> CREATOR = new Creator<NewResourceInfo>() {
        @Override
        public NewResourceInfo createFromParcel(Parcel source) {
            return new NewResourceInfo(source);
        }

        @Override
        public NewResourceInfo[] newArray(int size) {
            return new NewResourceInfo[size];
        }
    };


    public String getResourceSingleId() {
        return ResourceId;
    }
}
