package com.galaxyschool.app.wawaschool.pojo;

public class ContactsFriendRequestInfo {

	private String Id; // 审批记录ID
	private String MemberId;// 新朋友ID
	private String ApplyJoinName; // 待审批标题
	private String HeadPicUrl;
	private String CreatedOn; // 申请时间
	private int CheckState; // 审批状态 0未审批 1通过 2拒绝

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getMemberId() {
		return MemberId;
	}

	public void setMemberId(String memberId) {
		MemberId = memberId;
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

	public String getCreatedOn() {
		return CreatedOn;
	}

	public void setCreatedOn(String createdOn) {
		CreatedOn = createdOn;
	}

	public int getCheckState() {
		return CheckState;
	}

	public void setCheckState(int checkState) {
		CheckState = checkState;
	}

}
