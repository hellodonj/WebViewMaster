package com.lqwawa.intleducation.module.learn.vo;

import android.text.TextUtils;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryUtils;

import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCourseVo extends BaseVo {
    // 收费类型
    public static final int PAY_TYPE_CHARGE = 1;

    private int progress;// 2,
    private int weekCount;// 2,
    private String createTime;// "2016年11月10日",
    private List<MyCourseChapterVo> chapters;//
    private String thumbnailUrl;//"http://192.168.99.181/image/2016/11/02/1885f4b7-de3f-4df5-b3e7-0ef141560ac9.jpg",
    private String teachersName;// "测试01",
    private String courseId;// 182,
    // 我开设的学堂使用 == courseId
    private String id;
    private String organName;// "合肥大学",
    // 我开设的学程使用 == courseName;
    private String name;
    private String courseName;// "测试课程2"
    private String chapterName;
    private String sectionName;
    private boolean showMoreWeek = false;
    // 我开设的学堂，课程授课状态
    private int progressStatus;

    // 版本5.9新添加的字段
    private boolean buyAll;
    private int buyChapterNum;
    // 课程类型 1 收费类型
    private int payType;
    // 是否指定到班级
    private boolean inClass;
    private int assortment;
    private int type;
    private String level;
    // 学习进度
    private int learnRate;

    public boolean isShowMoreWeek() {
        return showMoreWeek;
    }

    public void setIsShowMoreWeek(boolean showMoreWeek) {
        this.showMoreWeek = showMoreWeek;
    }

    public List<MyCourseChapterVo> getChapters() {
        return chapters;
    }

    public void setChapters(List<MyCourseChapterVo> chapters) {
        this.chapters = chapters;
    }

    public String getCourseId() {
        if (EmptyUtil.isNotEmpty(courseId)) {
            return courseId;
        } else {
            return id;
        }
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        if (EmptyUtil.isNotEmpty(courseName)) {
            return courseName;
        } else {
            return name;
        }
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(int progressStatus) {
        this.progressStatus = progressStatus;
    }

    public boolean isBuyAll() {
        return buyAll;
    }

    public void setBuyAll(boolean buyAll) {
        this.buyAll = buyAll;
    }

    public int getBuyChapterNum() {
        return buyChapterNum;
    }

    public void setBuyChapterNum(int buyChapterNum) {
        this.buyChapterNum = buyChapterNum;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public boolean isInClass() {
        return inClass;
    }

    public void setInClass(boolean inClass) {
        this.inClass = inClass;
    }

    public int getAssortment() {
        return assortment;
    }

    public MyCourseVo setAssortment(int assortment) {
        this.assortment = assortment;
        return this;
    }

    public int getType() {
        return type;
    }

    public MyCourseVo setType(int type) {
        this.type = type;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public MyCourseVo setLevel(String level) {
        this.level = level;
        return this;
    }

    public int getLearnRate() {
        return learnRate;
    }

    public MyCourseVo setLearnRate(int learnRate) {
        this.learnRate = learnRate;
        return this;
    }

    /**
     * 返回是否是收费的课程
     *
     * @return true 收费课程
     */
    public boolean isCharge() {
        return this.payType == PAY_TYPE_CHARGE;
    }

    public int getLibraryType() {
        if (!TextUtils.isEmpty(level) && level.contains(OrganLibraryUtils.BRAIN_LIBRARY_LEVEL)) {
            return OrganLibraryType.TYPE_BRAIN_LIBRARY;
        }
        if (type == 0) {
            if (assortment == 0 || assortment == 1) {
                return OrganLibraryType.TYPE_LQCOURSE_SHOP;
            } else if (assortment == 2 || assortment == 3) {
                return OrganLibraryType.TYPE_PRACTICE_LIBRARY;
            }
        } else if (type == 1) {
            return OrganLibraryType.TYPE_LIBRARY;
        } else if (type == 2) {
            return OrganLibraryType.TYPE_VIDEO_LIBRARY;
        }
        return -1;
    }
}
