package com.galaxyschool.app.wawaschool.medias.fragment;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/11/15 9:19
 * 描    述：
 * 修订历史：
 * ================================================
 */

public interface ISchoolResource {


    /**
     * 上一节  下一节
     * @param position
     */
    void privousNextClickEvent(int position);

    /**
     * 查看目录
     */
    void selectCatalogEvent();

    /**
     * 搜索
     * @param keyword
     */
    void loadResourceList(String keyword);

    /**
     * 清除搜索数据
     */
    void clearSerachData();

    /**
     * 获取选择数据
     */
    void getSelectData();

    /**
     * 设置标题
     */
    void setTitle();

    /**
     *
     */
    String getBookCatalogName();
}
