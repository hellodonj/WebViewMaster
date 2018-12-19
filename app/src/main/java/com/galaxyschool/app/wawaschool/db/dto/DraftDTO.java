package com.galaxyschool.app.wawaschool.db.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;


@DatabaseTable()
public class DraftDTO implements Serializable {
	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField()
	public String title;

	@DatabaseField()
	public String content;

	@DatabaseField()
	public String thumbnail;

	@DatabaseField()
	public String chw;

	@DatabaseField()
	public boolean isCommit;

	@DatabaseField()
	public int draftType;

	@DatabaseField()
	public boolean isDelete = false;

	@DatabaseField()
	public long editTime = 0;

	@DatabaseField()
	public long resId = 0;

	@DatabaseField()
	public String memberId;

	final static public int TYPE_COMMENT = 2; // 一日点评
	final static public int TYPE_MESSAGE = 3; // 家校留言
	final static public int TYPE_QUESTION = 4; // 我问你答
	final static public int TYPE_NOTE = 5; // 随记随想
	final static public int TYPE_HOMEWORK = 6; // 布置作业
	final static public int TYPE_MESSAGE_REPLAY = 7;
	final static public int TYPE_QUESTION_REPLAY = 8;






	public DraftDTO() {

	}

	public DraftDTO(int id, String title, String content, String thumbnail,
					 String chw, long editTime, boolean isCommit, int draft_type) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.chw = chw;
		this.editTime = editTime;
		this.isCommit = isCommit;
		this.draftType = draft_type;
	}

	public DraftDTO(String title, String content, String thumbnail,
					String chw, long editTime, boolean isCommit, int draft_type) {
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.chw = chw;
		this.editTime = editTime;
		this.isCommit = isCommit;
		this.draftType = draft_type;
	}

	public void set(String title, String content, String thumbnail,
					String chw, long editTime, boolean isCommit, int draft_type) {
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.chw = chw;
		this.editTime = editTime;
		this.isCommit = isCommit;
		this.draftType = draft_type;
	}

	public int  getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public void setmId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getChw() {
		return chw;
	}

	public void setChw(String chw) {
		this.chw = chw;
	}

	public boolean isCommit() {
		return isCommit;
	}

	public void setIsCommit(boolean isCommit) {
		this.isCommit = isCommit;
	}

	public int getDraftType() {
		return draftType;
	}

	public void setDraftType(int draftType) {
		this.draftType = draftType;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setIsDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public long getEditTime() {
		return editTime;
	}

	public void setEditTime(long editTime) {
		this.editTime = editTime;
	}

	public long getResId() {
		return resId;
	}

	public void setResId(long resId) {
		this.resId = resId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
}
