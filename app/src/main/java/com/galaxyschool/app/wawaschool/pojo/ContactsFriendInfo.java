package com.galaxyschool.app.wawaschool.pojo;

public class ContactsFriendInfo {

	private String Id;
	private String MemberId;
	private String PersonalMailListName; //好友名称
	private String HeadPicUrl;
	private String NoteName;
	private String Nickname;
	private String FirstLetter;

	private boolean isSelected; //选择状态

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public boolean isSelected() {
		return isSelected;
	}

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

	public String getPersonalMailListName() {
		return PersonalMailListName;
	}

	public void setPersonalMailListName(String personalMailListName) {
		PersonalMailListName = personalMailListName;
	}

	public String getHeadPicUrl() {
		return HeadPicUrl;
	}

	public void setHeadPicUrl(String headPicUrl) {
		HeadPicUrl = headPicUrl;
	}

	public String getNoteName() {
		return NoteName;
	}

	public void setNoteName(String noteName) {
		NoteName = noteName;
	}

	public String getNickname() {
		return Nickname;
	}

	public void setNickname(String nickname) {
		Nickname = nickname;
	}

	public String getFirstLetter() {
		return FirstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		FirstLetter = firstLetter;
	}

}
