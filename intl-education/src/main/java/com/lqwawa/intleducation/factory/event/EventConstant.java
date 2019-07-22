package com.lqwawa.intleducation.factory.event;

/**
 * @author medici
 * @desc EventBus 事件类型 汇总
 */
public abstract class EventConstant {
	/**
	 * 刷新列表的EVENT
	 */
	public static final String TRIGGER_UPDATE_LIST_DATA = "TRIGGER_UPDATE_LIST_DATA";
	/**
	 * 学习任务选择学程馆,班级学程资源的Event
	 */
	public static final String COURSE_SELECT_RESOURCE_EVENT = "COURSE_SELECT_RESOURCE_EVENT";

	/**
	 * 完成授课的Event
	 */
	public static final String ONLINE_CLASS_COMPLETE_GIVE_EVENT = "ONLINE_CLASS_COMPLETE_GIVE_EVENT";
	/**
	 * 参加空中课堂
	 */
	public static final String JOIN_IN_CLASS_EVENT = "JOIN_IN_CLASS_EVENT";

	/**
	 * 班级学程添加学程的Event
	 */
	public static final String CLASS_COURSE_ADD_COURSE_EVENT = "CLASS_COURSE_ADD_COURSE_EVENT";

	/**
	 * 指定学程所在班级成功的Event
	 */
	public static final String APPOINT_COURSE_IN_CLASS_EVENT = "APPOINT_COURSE_IN_CLASS_EVENT";


	/**
	 * 生成播放列表的Event
	 */
	public static final String GENERATE_PLAY_LIST_EVENT = "GENERATE_PLAY_LIST_EVENT";

	/**
	 * 生成帮辅订单的Event
	 */
	public static final String CREATE_TUTOR_ORDER = "CREATE_TUTOR_ORDER";

	/**
	 * 创建班级付费订单的Event
	 */
	public static final String CREATE_CLASS_ORDER = "CREATE_CLASS_ORDER";

	/**
	 * 触发更新第一章,或者全本
	 */
	public static final String TRIGGER_UPDATE_COURSE = "TRIGGER_UPDATE_COURSE";

	/**
	 * 触发搜索回调的Event
	 */
	public static final String TRIGGER_SEARCH_CALLBACK_EVENT = "TRIGGER_SEARCH_CALLBACK_EVENT";

	/**
	 * 退出课程
	 */
	public static final String TRIGGER_EXIT_COURSE = "TRIGGER_EXIT_COURSE";

	/**
	 * 触发切换模式
	 */
	public static final String TRIGGER_SWITCH_APPLICATION_MODE = "TRIGGER_SWITCH_APPLICATION_MODE";


	/**
	 * 帮辅关注状态的更新
	 */
	public static final String TRIGGER_ATTENTION_TUTORIAL_UPDATE = "TRIGGER_ATTENTION_TUTORIAL_UPDATE";

	/**
	 * 视频详情评论列表更新
	 */
	public static final String TRIGGER_VIDEO_DETAIL_COMMENTS_UPDATE =
			"TRIGGER_VIDEO_DETAIL_COMMENTS_UPDATE";

	/**
	 * 加帮辅（班级帮辅）
	 */
	public static final String TRIGGER_ADD_TUTOR_UPDATE = "TRIGGER_ADD_TUTOR_UPDATE";
}
