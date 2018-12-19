package com.galaxyschool.app.wawaschool.pojo;

public class SubscriptionSchool {
	public String SchoolId; // 学校ID
	public String SchoolName; // 学校名称
	public String SchoolLogo; // 学校Logo
	public int UnreadCount;
	public boolean IsAttention;
	public String yeyid;//营养膳食id
	public String yeid;
	public int LqWaWaExitFlag;//弋康：0;青岛：1
	public int State;//0：未关注|未加入 1：已关注2：已加入
	public ClassInfo ClassMailDetail;//老师通讯录
	private String QRcode;

	public int getLqWaWaExitFlag() {
		return LqWaWaExitFlag;
	}

	public void setLqWaWaExitFlag(int lqWaWaExitFlag) {
		LqWaWaExitFlag = lqWaWaExitFlag;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public ClassInfo getClassMailDetail() {
		return ClassMailDetail;
	}

	public void setClassMailDetail(ClassInfo classMailDetail) {
		ClassMailDetail = classMailDetail;
	}

	public String getYeid() {
		return yeid;
	}

	public void setYeid(String yeid) {
		this.yeid = yeid;
	}

	public String getYeyid() {
		return yeyid;
	}

	public void setYeyid(String yeyid) {
		this.yeyid = yeyid;
	}

	public boolean getIsAttention() {
		return IsAttention;
	}

	public void setIsAttention(boolean isAttention) {
		IsAttention = isAttention;
	}

	public String getSchoolId() {
		return SchoolId;
	}

	public void setSchoolId(String schoolId) {
		SchoolId = schoolId;
	}

	public String getSchoolName() {
		return SchoolName;
	}

	public void setSchoolName(String schoolName) {
		SchoolName = schoolName;
	}

	public String getSchoolLogo() {
		return SchoolLogo;
	}

	public void setSchoolLogo(String schoolLogo) {
		SchoolLogo = schoolLogo;
	}

	public int getUnreadCount() {
		return UnreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		UnreadCount = unreadCount;
	}
	
	public boolean isJoined() {
		return State == 2;
	}
	
	public boolean isAttentioned() {
		return IsAttention || (State == 1);
	}

	public String getQRcode() {
		return QRcode;
	}

	public void setQRcode(String QRcode) {
		this.QRcode = QRcode;
	}
}
