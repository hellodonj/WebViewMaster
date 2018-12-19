package com.galaxyschool.app.wawaschool.db;

import android.content.Context;
import com.galaxyschool.app.wawaschool.db.dto.DraftDTO;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DraftData implements Serializable{
//	public final static String KEY_TITLE = "Title";
//	public final static String KEY_CONTENT = "Content";
//	public final static String KEY_THUMBNAIL = "Thumbnail";
//	public final static String KEY_CHW = "chw";
//	public final static String KEY_COMMIT_STATE = "CommitState";
//	public final static String KEY_DRAFT_TYPE = "DraftType";
//	public final static String KEY_DELETE = "IsDelete";
//	public final static String KEY_EDIT_TIME = "EditTime";
	

	final static public int TYPE_COMMENT = DraftDTO.TYPE_COMMENT; // 一日点评
	final static public int TYPE_MESSAGE = DraftDTO.TYPE_MESSAGE; // 家校留言
	final static public int TYPE_QUESTION = DraftDTO.TYPE_QUESTION; // 我问你答
	final static public int TYPE_NOTE = DraftDTO.TYPE_NOTE; // 随记随想
	final static public int TYPE_HOMEWORK = DraftDTO.TYPE_HOMEWORK; // 布置作业
	final static public int TYPE_MESSAGE_REPLAY = DraftDTO.TYPE_MESSAGE_REPLAY;
	final static public int TYPE_QUESTION_REPLAY = DraftDTO.TYPE_QUESTION_REPLAY;
	
	
    public int id;
	public String title;
	public String content;
	public String thumbnail;
	public String chw;
	public boolean isCommit;
	public int draftType;
	public boolean isDelete = false;
	public long editTime = 0;
	public long resdId;
	public String memberId;
	

	public DraftData() {

	}

	public DraftData(String title, String content, String thumbnail,
			String chw, long editTime, boolean isCommit, int draft_type) {
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.chw = chw;
		this.editTime = editTime;
		this.isCommit = isCommit;
		this.draftType = draft_type;
	}

	public DraftData(DraftDTO draftDTO) {
        this.id = draftDTO.getId();
		this.title = draftDTO.getTitle();
		this.content = draftDTO.getContent();
		this.thumbnail = draftDTO.getThumbnail();
		this.chw = draftDTO.getChw();
		this.editTime = draftDTO.getEditTime();
		this.isCommit = draftDTO.isCommit();
		this.draftType = draftDTO.getDraftType();
		this.resdId = draftDTO.getResId();
		this.memberId = draftDTO.getMemberId();
	}


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

	public long getResdId() {
		return resdId;
	}

	public void setResdId(long resdId) {
		this.resdId = resdId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
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
	
	public static DraftData saveDraft(Context context, String title, String content, String thumbnail,
			String chw, long editTime, boolean isCommit, int draft_type, String memberId) {

		DraftDao draftDao = new DraftDao(context);
		try {
			DraftDTO draftDTO = draftDao.getDraftByChwPath(chw, memberId);
			if (draftDTO != null) {
				draftDTO.set(title, content, thumbnail, chw, editTime, isCommit, draft_type);
			} else {
				draftDTO = new DraftDTO(title, content, thumbnail, chw, editTime, isCommit, draft_type);
			}
			if (draftDTO != null) {
				draftDTO.setMemberId(memberId);
				draftDao.addOrUpdateDraftDao(draftDTO);
				DraftData draftData = new DraftData(draftDTO);
				return draftData;
			}

		} catch (SQLException e) {

		}
		return null;
	}
	
	public static DraftData saveDraft(Context context, String oldchw, String title, String content, String thumbnail,
			String chw, long editTime, boolean isCommit, int draft_type, String memberId) {
		DraftData draftData = null;
		DraftDao draftDao = new DraftDao(context);
		try {
			DraftDTO draftDTO = draftDao.getDraftByChwPath(oldchw, memberId);
			if (draftDTO != null) {
				draftDTO.set(title, content, thumbnail, chw, editTime, isCommit, draft_type);
			} else {
				draftDTO = new DraftDTO(title, content, thumbnail, chw, editTime, isCommit, draft_type);
			}
			if (draftDTO != null) {
				draftDTO.setMemberId(memberId);
				draftDao.addOrUpdateDraftDao(draftDTO);
				draftData = new DraftData(draftDTO);
				return draftData;
			}

		} catch (SQLException e) {

		}
		return null;
	}
	
	
	public static DraftData getDraftByChwPath(Context context, String chwPath, String memberId) {
		DraftData draftData = null;
		DraftDao draftDao = new DraftDao(context);
		try {
			DraftDTO draftDTO = draftDao.getDraftByChwPath(chwPath, memberId);
			if (draftDTO != null) {
				draftData = new DraftData(draftDTO);
			}

		} catch (SQLException e) {

		}

		return draftData;
	
	}
	
	public static ArrayList<DraftData> getAllDraftsInType(Context context, int type, String memberId) {

		ArrayList<DraftData> draftDatas = null;
		DraftDao draftDao = new DraftDao(context);
		try {
			List<DraftDTO> draftDTOs = draftDao.getAllDraftsInType(type, memberId);
			if (draftDTOs != null && draftDTOs.size() > 0) {
				draftDatas = new ArrayList<DraftData>();
				for (int i = 0; i < draftDTOs.size(); i++) {
					DraftData draftData = new DraftData(draftDTOs.get(i));
					draftDatas.add(draftData);
				}
			}
		}catch (SQLException e) {

		}

		return draftDatas;
	}
	
	public static boolean deleteDraftById(Context context, int id, String memberId) {
		int rtn = 0;
		DraftDao draftDao = new DraftDao(context);
		try {
			rtn = draftDao.deleteDraftById(id, memberId);

		} catch (SQLException e) {

		}
		return rtn > 0;

	}

	public static boolean deleteDraftByChwPath(Context context, String path, String memberId) {
		int rtn = 0;
		DraftDao draftDao = new DraftDao(context);
		try {
			rtn = draftDao.deleteDraftByChwPath(path, memberId);

		} catch (SQLException e) {

		}
		return rtn > 0;

	}

	public static boolean updateDraftByChwPath(Context context, String path, String title, String memberId) {
		int rtn = 0;
		DraftDao draftDao = new DraftDao(context);
		DraftData draftData = new DraftData();
		draftData.setTitle(title);
		try {
			rtn = draftDao.updateDraftByChwPath(path, memberId, draftData);
		} catch (SQLException e) {

		}
		return rtn > 0;

	}

	public static boolean updateDraftByResId(Context context, long OldResId, long newResdId, String memberId) {
		int rtn = 0;
		DraftDao draftDao = new DraftDao(context);
		DraftData draftData = new DraftData();
		draftData.setResdId(newResdId);
		try {
			rtn = draftDao.updateDraftByResId(OldResId, memberId, draftData);
		} catch (SQLException e) {

		}
		return rtn > 0;

	}
	
	
}
