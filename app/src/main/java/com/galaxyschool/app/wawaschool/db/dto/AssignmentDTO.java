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
public class AssignmentDTO implements Serializable {
	@DatabaseField(id = true)
	protected String mId;

	@DatabaseField()
	protected boolean mIsRead;

	public AssignmentDTO() {

	}

	public AssignmentDTO(String id, boolean isRead) {
		mId = id;
		mIsRead = isRead;
	}

	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public boolean getIsRead() {
		return mIsRead;
	}

	public void setIsRead(boolean mIsRead) {
		this.mIsRead = mIsRead;
	}
}
