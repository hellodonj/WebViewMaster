package com.galaxyschool.app.wawaschool.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnIghT on 16-4-8.
 */
public class PictureBooksFragmentHelper {
    private List<NewResourceInfo> bookList;
    private String oldMemberId;
    private SchoolInfo schoolInfo;
    private PicBookCategoryType selectLabelCategory;
    private PicBookCategoryType selectstudyStageCategory;
    private PicBookCategoryType selectLanguageCategory;

    public List<NewResourceInfo> getBookList() {
        return bookList;
    }

    public String getOldMemberId() {
        return oldMemberId;
    }

    public PictureBooksFragmentHelper() {
    }

    public void setBookList(List<NewResourceInfo> bookList) {
        this.bookList = new ArrayList<NewResourceInfo>();
        if (bookList != null) {
            this.bookList.addAll(bookList);
        }
    }

    public PicBookCategoryType getSelectLabelCategory() {
        return selectLabelCategory;
    }

    public void setSelectLabelCategory(PicBookCategoryType selectLabelCategory) {
        this.selectLabelCategory = selectLabelCategory;
    }

    public PicBookCategoryType getSelectstudyStageCategory() {
        return selectstudyStageCategory;
    }

    public void setSelectstudyStageCategory(PicBookCategoryType selectstudyStageCategory) {
        this.selectstudyStageCategory = selectstudyStageCategory;
    }

    public PicBookCategoryType getSelectLanguageCategory() {
        return selectLanguageCategory;
    }

    public void setSelectLanguageCategory(PicBookCategoryType selectLanguageCategory) {
        this.selectLanguageCategory = selectLanguageCategory;
    }

    public void setOldMemberId(String oldMemberId) {
        this.oldMemberId = oldMemberId;
    }

    public SchoolInfo getSchoolInfo() {
        return schoolInfo;
    }

    public void setSchoolInfo(SchoolInfo schoolInfo) {
        this.schoolInfo = schoolInfo;
    }

    public void saveData(List<NewResourceInfo> bookList, SchoolInfo schoolInfo, PicBookCategoryType selectLabelCategory, PicBookCategoryType selectstudyStageCategory, PicBookCategoryType selectLanguageCategory, String oldMemberId) {
        setOldMemberId(oldMemberId);
        setBookList(bookList);
        setSchoolInfo(schoolInfo);
        setSelectLabelCategory(selectLabelCategory);
        setSelectstudyStageCategory(selectstudyStageCategory);
        setSelectLanguageCategory(selectLanguageCategory);
    }


    public boolean isBookListLoadData() {
        return this.bookList != null;
    }

    public boolean isSamePerson(String oldMemberId) {
        if ((oldMemberId == null && this.oldMemberId == null)) {
            return true;
        } else if (oldMemberId != null && this.oldMemberId != null && this.oldMemberId.equals(oldMemberId)) {
            return true;
        }
        return false;
    }

    ;

    public boolean isNeedUseCache(String oldMemberId, List<PicBookCategoryType> labelCategorys, List<PicBookCategoryType> studyStageCategorys, List<PicBookCategoryType> languageCategorys) {
        return isBookListLoadData() && isSamePerson(oldMemberId) && isAllInCategoryList(labelCategorys, studyStageCategorys, languageCategorys);
    }

    public boolean isNeedUseCache2(String oldMemberId, List<SchoolInfo> schoolInfos, List<PicBookCategoryType> labelCategorys, List<PicBookCategoryType> studyStageCategorys, List<PicBookCategoryType> languageCategorys) {
        return isNeedUseCache(oldMemberId, labelCategorys, studyStageCategorys, languageCategorys) && isInAttenList(schoolInfos);
    }

    public boolean isAllInCategoryList(List<PicBookCategoryType> labelCategorys, List<PicBookCategoryType> studyStageCategorys, List<PicBookCategoryType> languageCategorys) {
        return isInCategoryList(labelCategorys, selectLabelCategory) && isInCategoryList(studyStageCategorys, selectstudyStageCategory) && isInCategoryList(languageCategorys, selectLanguageCategory);
    }

    public boolean isInAttenList(List<SchoolInfo> schoolInfos) {
        if (schoolInfos != null && schoolInfos.size() > 0 && this.schoolInfo != null) {
            for (SchoolInfo sc : schoolInfos) {
                if (sc.getSchoolId().equals(this.schoolInfo.getSchoolId())) {
                    this.schoolInfo = sc;
                    this.schoolInfo.setIsSelect(true);
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isInCategoryList(List<PicBookCategoryType> categoryTypes, PicBookCategoryType picBookCategoryType) {
        if (categoryTypes != null && categoryTypes.size() > 0 && picBookCategoryType != null) {
            for (PicBookCategoryType categoryType : categoryTypes) {
                if (categoryType.getId().equals(picBookCategoryType.getId())) {
                    picBookCategoryType = categoryType;
                    picBookCategoryType.setIsSelect(true);
                    return true;
                }
            }
        }
        return false;
    }
}
