package com.galaxyschool.app.wawaschool.db.dto;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>
 * APP下载 超类
 * </p>
 * 
 * @author shouyi
 */
@DatabaseTable()
public class NewsDTO implements Serializable {
	@DatabaseField(id = true)
	protected String Id;
	
	@DatabaseField()
	protected String Title;
	
	@DatabaseField()
	protected String Content;
	
	@DatabaseField()
	protected boolean isRead;

	@DatabaseField()
	protected String SchoolId;

	@DatabaseField()
	protected String SchoolName;

	@DatabaseField()
	protected long time;

	@DatabaseField()
	protected String SchoolLogo;

	public NewsDTO() {

	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getSchoolLogo() {
		return SchoolLogo;
	}

	public void setSchoolLogo(String schoolLogo) {
		SchoolLogo = schoolLogo;
	}

}
