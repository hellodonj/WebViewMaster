package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

public class ContactsFriendDetails extends Model {

	private String Id;
	private String MemberId;
	private String NoteName;
	private String Nickname;
	private String RealName;
	private String HeadPicUrl;
	private String QRCode;

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

	public String getRealName() {
		return RealName;
	}

	public void setRealName(String realName) {
		RealName = realName;
	}

	public String getHeadPicUrl() {
		return HeadPicUrl;
	}

	public void setHeadPicUrl(String headPicUrl) {
		HeadPicUrl = headPicUrl;
	}

	public String getQRCode() {
		return QRCode;
	}

	public void setQRCode(String qRCode) {
		QRCode = qRCode;
	}

}
