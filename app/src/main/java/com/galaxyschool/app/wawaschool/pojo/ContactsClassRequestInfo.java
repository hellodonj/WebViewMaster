package com.galaxyschool.app.wawaschool.pojo;

public class ContactsClassRequestInfo {

	private String Id;
	private String ApplyJoinName; // 申请记录名称
	private String HeadPicUrl;
	private int CheckState; // 0未审批 1已审批 2拒绝
	private String CreatedOn; // 创建时间
	private String ClassId;
	private String ClassName;
	private String Applicant;
	private int Role; // 0老师 1学生 2家长
	private int ParentType; // 0家长 1妈妈 2爸爸
	private String Subject;
	private String StudentName;
	private String LQ_MemberId;

	public void setLQ_MemberId(String LQ_MemberId) {
		this.LQ_MemberId = LQ_MemberId;
	}

	public String getLQ_MemberId() {
		return LQ_MemberId;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getApplyJoinName() {
		return ApplyJoinName;
	}

	public void setApplyJoinName(String applyJoinName) {
		ApplyJoinName = applyJoinName;
	}

	public String getHeadPicUrl() {
		return HeadPicUrl;
	}

	public void setHeadPicUrl(String headPicUrl) {
		HeadPicUrl = headPicUrl;
	}

	public int getCheckState() {
		return CheckState;
	}

	public void setCheckState(int checkState) {
		CheckState = checkState;
	}

	public String getCreatedOn() {
		return CreatedOn;
	}

	public void setCreatedOn(String createdOn) {
		CreatedOn = createdOn;
	}

	public String getClassId() {
		return ClassId;
	}

	public void setClassId(String classId) {
		ClassId = classId;
	}

	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public String getApplicant() {
		return Applicant;
	}

	public void setApplicant(String applicant) {
		Applicant = applicant;
	}

	public int getRole() {
		return Role;
	}

	public boolean isTeacher() {
		return Role == 0;
	}

	public boolean isStudent() {
		return Role == 1;
	}

	public boolean isParent() {
		return Role == 2;
	}

	public void setRole(int role) {
		Role = role;
	}

	public int getParentType() {
		return ParentType;
	}

	public void setParentType(int parentType) {
		ParentType = parentType;
	}

	public boolean isMother() {
		return ParentType == 1;
	}

	public boolean isFather() {
		return ParentType == 2;
	}

	public String getSubject() {
		return Subject;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

	public String getStudentName() {
		return StudentName;
	}

	public void setStudentName(String studentName) {
		StudentName = studentName;
	}
}
