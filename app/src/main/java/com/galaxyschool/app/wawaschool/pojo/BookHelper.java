package com.galaxyschool.app.wawaschool.pojo;

import com.galaxyschool.app.wawaschool.db.dto.BookStoreBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnIghT on 16-4-8.
 * ���������滺��
 */
public class BookHelper {
    private List<BookStoreBook> bookList;
    private String oldMemberId ;
    private SchoolInfo schoolInfo;

    public List<BookStoreBook> getBookList() {
        return bookList;
    }
    public String getOldMemberId() {
        return oldMemberId;
    }
    public BookHelper(){
    }
    public void setBookList(List<BookStoreBook> bookList) {
        this.bookList=new ArrayList<BookStoreBook>();
        if ( bookList != null) {
            this.bookList.addAll(bookList);
        }
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

    public void saveData(List<BookStoreBook> bookList,SchoolInfo schoolInfo,String oldMemberId){//�������
        setOldMemberId(oldMemberId);
        setBookList(bookList);
        setSchoolInfo(schoolInfo);
    }


    public boolean isBookListLoadData() {//�жϻ�������������
        return   this.bookList != null;
    }
    public boolean isSamePerson(String oldMemberId){//�ж��ǲ���ͬһ����¼��
        return this.oldMemberId.equals(oldMemberId);
    };

    public boolean isNeedUseCache(String oldMemberId){//�ж��Ƿ���Ҫ�û���(δ��¼)
        return isBookListLoadData()&&isSamePerson(oldMemberId);
    }
    public boolean isNeedUseCache2(String oldMemberId,List<SchoolInfo> schoolInfos){//�ж��Ƿ���Ҫ�û��棨��¼��
        return isNeedUseCache(oldMemberId)&&isInAttenList(schoolInfos);
    }
    public boolean isInAttenList(List<SchoolInfo> schoolInfos ){//�ж��Ƿ���Ҫ�û���
        if (schoolInfos != null && schoolInfos.size() > 0&&this.schoolInfo!=null) {
                for (SchoolInfo sc : schoolInfos) {
                    if (sc.getSchoolId().equals(this.schoolInfo.getSchoolId())) {
                        this.schoolInfo=sc;//���ﻺ���schoolInfo�п�����û��ע�ģ����������û���ʱҪ��ʱ���»����״̬
                        return true;
                    }
                }
        }
        return false;
    }
}
