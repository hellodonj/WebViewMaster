package com.galaxyschool.app.wawaschool.pojo;

import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;

import java.io.Serializable;
import java.util.List;

public class UploadParameter implements Serializable{

    private String memberId;
    private String account;
    private int parentId;
    private String createName;
    private String filePath;
    private String fileName;
    private String zipFilePath;
    private String thumbPath;
    private long size;
    private long totalTime;
    private String description;
    private String knowledge;
    private UploadSchoolInfo uploadSchoolInfo;
    private String schoolIds;
    private int type;
    private int channelId;
    private String columns;
    private List<String> studentIds;
    private ContactItem contactItem;
    private long resId;
    private int resType;
    private CourseData courseData;
    private int schoolSpaceType;
    //    private UploadConfigInfo uploadConfigInfo;
    private int noteType;
    private int colType;
    private int screenType;
    private int sourceType;
    private int shareType;
    private String classId;
    private int mediaType;
    private List<String> paths;
    private String uploadUrl;
    private List<String> picBookIds;
    private String outlineId;
    private String sectionId;
    private boolean isPmaterial;

    private List<ShortSchoolClassInfo> shortSchoolClassInfos;
    private int taskType;
    private String startDate;
    private String endDate;
    private LocalCourseDTO localCourseDTO;
    private boolean isScanTask;
    private String taskId;
    private String workOrderId;
    private String workOrderUrl;
    private String disContent;
    //英文写作的字段
    private String WritingRequire;
    private int MarkFormula;
    private int WordCountMin;
    private int WordCountMax;
    //增加一个字段来区分是否来自任务单详情页 作为临时变量
    private boolean isTempData;

    private boolean isLocal;

    //看课件传递的资源对象list
    private List<LookResDto> lookResDtoList;
    private int fromType = 0;

    private ResourceInfoTag newResourceInfoTag;

    public boolean NeedScore;//是否打分  目前只有复述课件和做读写单需要  非必填
    public int ScoringRule = 1;//1:十分制 2:百分制 需要打分时必填
    private boolean isNeedSplit = true;//判断拆分服务器是否需要拆分
    private List<UploadParameter> uploadParameters;
    private int ResCourseId;
    private int ResPropType;
    private int SubmitType;
    //校本资源库的类型
    public int schoolMaterialType;


    public int getSchoolMaterialType() {
        return schoolMaterialType;
    }

    public void setSchoolMaterialType(int schoolMaterialType) {
        this.schoolMaterialType = schoolMaterialType;
    }
    public int getSubmitType() {
        return SubmitType;
    }

    public void setSubmitType(int submitType) {
        SubmitType = submitType;
    }

    public int getResPropType() {
        return ResPropType;
    }

    public void setResPropType(int resPropType) {
        ResPropType = resPropType;
    }

    public int getResCourseId() {
        return ResCourseId;
    }

    public void setResCourseId(int resCourseId) {
        ResCourseId = resCourseId;
    }

    public boolean isNeedSplit() {
        return isNeedSplit;
    }

    public void setIsNeedSplit(boolean needSplit) {
        isNeedSplit = needSplit;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public ResourceInfoTag getNewResourceInfoTag() {
        return newResourceInfoTag;
    }

    public void setNewResourceInfoTag(ResourceInfoTag newResourceInfoTag) {
        this.newResourceInfoTag = newResourceInfoTag;
    }

    public void setLookResDtoList(List<LookResDto> lookResDtoList) {
        this.lookResDtoList = lookResDtoList;
    }

    public List<LookResDto> getLookResDtoList() {
        return lookResDtoList;
    }

    public UploadParameter() {

    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean local) {
        isLocal = local;
    }

    public boolean isTempData() {
        return isTempData;
    }

    public void setTempData(boolean tempData) {
        isTempData = tempData;
    }

    public String getWritingRequire() {
        return WritingRequire;
    }

    public void setWritingRequire(String writingRequire) {
        WritingRequire = writingRequire;
    }

    public int getMarkFormula() {
        return MarkFormula;
    }

    public void setMarkFormula(int markFormula) {
        MarkFormula = markFormula;
    }

    public int getWordCountMin() {
        return WordCountMin;
    }

    public void setWordCountMin(int wordCountMin) {
        WordCountMin = wordCountMin;
    }

    public int getWordCountMax() {
        return WordCountMax;
    }

    public void setWordCountMax(int wordCountMax) {
        WordCountMax = wordCountMax;
    }

    public String getDisContent() {
        return disContent;
    }
    public void setDisContent(String disContent) {
        this.disContent = disContent;
    }
    public void setWorkOrderId(String workOrderId){
        this.workOrderId=workOrderId;
    }
    public void setWorkOrderUrl(String workOrderUrl){
        this.workOrderUrl=workOrderUrl;
    }
    public String getWorkOrderId(){
        return workOrderId;
    }
    public String getWorkOrderUrl(){
        return workOrderUrl;
    }
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getZipFilePath() {
        return zipFilePath;
    }

    public void setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UploadSchoolInfo getUploadSchoolInfo() {
        return uploadSchoolInfo;
    }

    public void setUploadSchoolInfo(UploadSchoolInfo uploadSchoolInfo) {
        this.uploadSchoolInfo = uploadSchoolInfo;
    }

    public String getSchoolIds() {
        return schoolIds;
    }

    public void setSchoolIds(String schoolIds) {
        this.schoolIds = schoolIds;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public ContactItem getContactItem() {
        return contactItem;
    }

    public void setContactItem(ContactItem contactItem) {
        this.contactItem = contactItem;
    }

    public long getResId() {
        return resId;
    }

    public void setResId(long resId) {
        this.resId = resId;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public CourseData getCourseData() {
        return courseData;
    }

    public void setCourseData(CourseData courseData) {
        this.courseData = courseData;
    }

    public int getSchoolSpaceType() {
        return schoolSpaceType;
    }

    public void setSchoolSpaceType(int schoolSpaceType) {
        this.schoolSpaceType = schoolSpaceType;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public int getColType() {
        return colType;
    }

    public void setColType(int colType) {
        this.colType = colType;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public List<String> getPicBookIds() {
        return picBookIds;
    }

    public void setPicBookIds(List<String> picBookIds) {
        this.picBookIds = picBookIds;
    }

    public String getOutlineId() {
        return outlineId;
    }

    public void setOutlineId(String outlineId) {
        this.outlineId = outlineId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public boolean isPmaterial() {
        return isPmaterial;
    }

    public void setIsPmaterial(boolean isPmaterial) {
        this.isPmaterial = isPmaterial;
    }

    public List<ShortSchoolClassInfo> getShortSchoolClassInfos() {
        return shortSchoolClassInfos;
    }

    public void setShortSchoolClassInfos(List<ShortSchoolClassInfo> shortSchoolClassInfos) {
        this.shortSchoolClassInfos = shortSchoolClassInfos;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public LocalCourseDTO getLocalCourseDTO() {
        return localCourseDTO;
    }

    public void setLocalCourseDTO(LocalCourseDTO localCourseDTO) {
        this.localCourseDTO = localCourseDTO;
    }

    public boolean isScanTask() {
        return isScanTask;
    }

    public void setIsScanTask(boolean isScanTask) {
        this.isScanTask = isScanTask;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<UploadParameter> getUploadParameters() {
        return uploadParameters;
    }

    public void setUploadParameters(List<UploadParameter> uploadParameters) {
        this.uploadParameters = uploadParameters;
    }
}
