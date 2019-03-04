package com.lqwawa.intleducation.factory.data.entity.tutorial;

import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 用户相关的机构列表
 */
public class MemberSchoolEntity implements Serializable {

    /**
     * MemberId : bc4d2af9-33a9-4d75-a0c1-41499e69b7be
     * NickName : ios02
     * RealName : 老师02号
     * SchoolId : f899b66d-b7a9-4575-8b5f-2d6e47d03483
     * SchoolLogo : http://filetestop.lqwawa.com/UploadFiles/20160714104014/000000000000000/b60ce190-eb8d-4fc5-8428-3a25ef8021bd.jpg
     * SchoolName : 两栖蛙蛙测试旧学校（模拟环信）-在线课堂
     */

    private String MemberId;
    private String NickName;
    private String RealName;
    private String SchoolId;
    private String SchoolLogo;
    private String SchoolName;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String MemberId) {
        this.MemberId = MemberId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String RealName) {
        this.RealName = RealName;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String SchoolId) {
        this.SchoolId = SchoolId;
    }

    public String getSchoolLogo() {
        return SchoolLogo;
    }

    public void setSchoolLogo(String SchoolLogo) {
        this.SchoolLogo = SchoolLogo;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String SchoolName) {
        this.SchoolName = SchoolName;
    }

    public OnlineStudyOrganEntity buildOnlineStudyOrganEntitiy(){
        OnlineStudyOrganEntity entity = new OnlineStudyOrganEntity();
        entity.setId(this.getSchoolId());
        entity.setName(this.getSchoolName());
        entity.setThumbnail(this.getSchoolLogo());
        return entity;
    }
}
