package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

import java.util.List;

/**
 * Created by E450 on 2017/08/01.
 * 营养膳食
 是否开通了弋康服务标识，包括三种情况：
 1.Member表yeyid有值
 2.所在学校中yeyid有值
 3.所在学校中开通了弋康服务
 */
public class NutritionRecipeObject extends Model {

    public String MemberId; //会员ID
    public boolean IsOpenYk;//该字段暂时忽略
    public int yeyidOfMember;//会员的弋康标识
    public List<SchoolYkStateInfo> SchoolList;//所在学校列表


    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public boolean getIsOpenYk() {
        return IsOpenYk;
    }

    public void setOpenYk(boolean openYk) {
        IsOpenYk = openYk;
    }

    public int getYeyidOfMember() {
        return yeyidOfMember;
    }

    public void setYeyidOfMember(int yeyidOfMember) {
        this.yeyidOfMember = yeyidOfMember;
    }

    public List<SchoolYkStateInfo> getSchoolList() {
        return SchoolList;
    }

    public void setSchoolList(List<SchoolYkStateInfo> schoolList) {
        SchoolList = schoolList;
    }
}
