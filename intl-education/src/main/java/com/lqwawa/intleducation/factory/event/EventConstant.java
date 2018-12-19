package com.lqwawa.intleducation.factory.event;

/**
 * @author medici
 * @desc EventBus 事件类型 汇总
 */
public abstract class EventConstant {
	/**
	 * 学习任务选择学程馆,班级学程资源的Event
	 */
	public static final String COURSE_SELECT_RESOURCE_EVENT = "COURSE_SELECT_RESOURCE_EVENT";

	/**
	 * 完成授课的Event
	 */
	public static final String ONLINE_CLASS_COMPLETE_GIVE_EVENT = "ONLINE_CLASS_COMPLETE_GIVE_EVENT";
	/**
	 * 参加在线课堂
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
	 * 触发更新第一章,或者全本
	 */
	public static final String TRIGGER_UPDATE_COURSE = "TRIGGER_UPDATE_COURSE";

	/**
	 * 触发搜索回调的Event
	 */
	public static final String TRIGGER_SEARCH_CALLBACK_EVENT = "TRIGGER_SEARCH_CALLBACK_EVENT";

}
