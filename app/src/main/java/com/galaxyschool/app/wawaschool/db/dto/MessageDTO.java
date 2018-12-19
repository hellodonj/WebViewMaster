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
public class MessageDTO implements Serializable {
	@DatabaseField(id = true)
	protected int type;

	@DatabaseField()
	protected String id;

	@DatabaseField()
	protected String title;

	@DatabaseField()
	protected String subTitle;

	@DatabaseField()
	protected long time;

	@DatabaseField()
	protected int newCount;

	@DatabaseField()
	protected boolean isRead;
	
	@DatabaseField()
	protected boolean isDelete;
	
	@DatabaseField()
	protected int IsWawaTong;
	
	@DatabaseField()
	private int Category;

	public int getCategory() {
		return Category;
	}

	public void setCategory(int category) {
		Category = category;
	}

	public int getIsWawaTong() {
		return IsWawaTong;
	}

	public void setIsWawaTong(int isWawaTong) {
		IsWawaTong = isWawaTong;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public MessageDTO() {

	}

	public MessageDTO(String id, int type, String title, String subTitle,
			long time, int newCount) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.subTitle = subTitle;
		this.time = time;
		this.newCount = newCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getNewCount() {
		return newCount;
	}

	public void setNewCount(int newCount) {
		this.newCount = newCount;
	}

}
