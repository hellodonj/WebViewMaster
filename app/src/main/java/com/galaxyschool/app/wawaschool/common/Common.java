package com.galaxyschool.app.wawaschool.common;

public interface Common {
	public static int ACTIVITY_REQUEST_EDITTEMPLATES_BASE = 100;

	public static int ACTIVITY_REQUEST_ATTACHMENGT_EDIT = ACTIVITY_REQUEST_EDITTEMPLATES_BASE + 1;
	public static int ACTIVITY_REQUEST_IMAGE_PATH_BASE = ACTIVITY_REQUEST_ATTACHMENGT_EDIT + 1;
	public static int ACTIVITY_REQUEST_CAMERA_PATH_BASE = ACTIVITY_REQUEST_IMAGE_PATH_BASE + 1;

	final static public int TYPE_NOTE = 5; // 随记随想

	final static public int LIST_TYPE_SHARE = 5;

	public static String HomePath = Utils.DATA_FOLDER;
	public static String PicPath = HomePath + "Pic";
	public static String TempPath = Utils.TEMP_FOLDER;
	public static String PhotoPath = HomePath + "Photo/";
	public static String DownloadWeike = HomePath + "DownloadWeike/";

}
