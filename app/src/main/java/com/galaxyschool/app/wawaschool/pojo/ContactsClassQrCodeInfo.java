package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

public class ContactsClassQrCodeInfo extends Model {

	private String Id;
	private String ClassMailName;
	private String HeadPicUrl;
	private String QRCode;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getClassMailName() {
		return ClassMailName;
	}

	public void setClassMailName(String classMailName) {
		ClassMailName = classMailName;
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
