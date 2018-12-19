package com.galaxyschool.app.wawaschool.pojo;

/**
 * 资源服务器资源类型
 * Author: wangchao
 * Time: 2015/11/12 13:46
 */
public interface ResType {
    int RES_TYPE_BASE = 10000; // 课件或任务单分页在原类型上加上该值
    int RES_TYPE_IMG = 1; // 图片
    int RES_TYPE_VOICE = 2; // 音频
    int RES_TYPE_VIDEO = 3; // 视频
    int RES_TYPE_PDF = 6; // PDF
    int RES_TYPE_OLD_COURSE = 5; // 旧录音课件, 5和16相同
    int RES_TYPE_COURSE = 16; // 旧录音课件, 5和16相同
    int RES_TYPE_NOTE = 17; // 帖子
    int RES_TYPE_ONEPAGE = 18; // 点读课件
    int RES_TYPE_COURSE_SPEAKER = 19; // 增强录音课件
    int RES_TYPE_PPT = 20; // PPT
    int RES_TYPE_RESOURCE = 21; // 已导入素材
    int RES_TYPE_STUDY_CARD = 23; // 任务单
    int RES_TYPE_DOC = 24;//doc
    int RES_TYPE_EVALUATE = 26;//语音评测类型
}
