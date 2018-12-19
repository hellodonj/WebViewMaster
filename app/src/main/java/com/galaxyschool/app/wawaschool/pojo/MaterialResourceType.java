package com.galaxyschool.app.wawaschool.pojo;

/**素材(Material)：IMG(1)-图片, VOICE(2)-音频, VIDEO(3)-视频, COURSE(5)-微课,
 * PDF(6),PPT-(20),任务单-(23)
 */
public interface MaterialResourceType {

    int PICTURE = 1; // 图片
    int AUDIO = 2; //音频
    int VIDEO = 3; //视频
    int OLD_COURSE = 5; //老微课
    int OLD_COURSE_ANOTHER = 16; //老微课
    int ONE_PAGE = 18; //有声相册
    int MICRO_COURSE = 19; //带分页的课件
    int SINGLE_MICRO_COURSE = 10019; //单页课件
    int PDF = 6; //PDF
    int PPT = 20; //PPT
    int TASK_ORDER = 23; //任务单
    int DOC = 24; //DOC
    int BASE_TYPE_NUM = 10000; //取模基数
}
