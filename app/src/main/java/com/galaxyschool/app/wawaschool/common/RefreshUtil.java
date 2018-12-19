package com.galaxyschool.app.wawaschool.common;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/5/24 10:32
 * 描    述：下拉刷新保存选中的状态,支持单选/多选,单例类 : 用法参考TaskOrderFragment  ctrl+F 搜索 RefreshUtil
 * 修订历史：
 * ================================================
 */

public class RefreshUtil {

    private  ArrayList<String> IdList;


    private RefreshUtil() {

    }

    private static class HolderClass {
        private static final RefreshUtil instance = new RefreshUtil();
    }

    public static RefreshUtil getInstance() {

        return HolderClass.instance;
    }


    /**
     * 保存选中的资源
     *
     * @param id 传入资源id
     */
    public void addId(String id) {
      
        if (IdList == null) {
            IdList = new ArrayList<>();
        }
        if (!IdList.contains(id)) {

            IdList.add(id);
        }
    }

    /**
     * 删除选中的资源
     *
     * @param id 传入资源id
     */
    public void removeId(String id) {
        if (IdList == null) {
            IdList = new ArrayList<>();
        }
        if (IdList.contains(id)) {

            IdList.remove(id);
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (IdList != null) {
            IdList.clear();
            IdList = null;
        }
    }

    /**
     * 刷新后重置选中状态
     *
     * @param list
     */
    public <T>  void refresh( @NonNull List<T> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        if (IdList == null || IdList.size() <= 0) {
            return;
        }
        for (T t : list) {
            for (String s : IdList) {
                if (t instanceof NewResourceInfo) {
                    if (TextUtils.equals(((NewResourceInfo) t).getId(), s)) {
                        ((NewResourceInfo) t).setIsSelect(true);
                    }
                } else if (t instanceof MediaInfo) {
                    if (TextUtils.equals(((MediaInfo) t).getId(), s)) {
                        ((MediaInfo) t).setIsSelect(true);
                    }
                }else if (t instanceof ContactsSchoolInfo) {
                    //班级通讯录---学校
                    if (TextUtils.equals(((ContactsSchoolInfo) t).getSchoolId(), s)) {
                        ((ContactsSchoolInfo) t).setSelected(true);
                    }
                }else if (t instanceof ContactsClassInfo) {
                    //班级通讯录---班级
                    if (TextUtils.equals(((ContactsClassInfo) t).getId(), s)) {
                        ((ContactsClassInfo) t).setSelected(true);
                    }
                }else if (t instanceof ContactsClassMemberInfo) {
                    //班级通讯录---班级---学生
                    if (TextUtils.equals(((ContactsClassMemberInfo) t).getId(), s)) {
                        ((ContactsClassMemberInfo) t).setIsSelect(true);
                    }
                }else if (t instanceof ContactsFriendInfo) {
                    //个人通讯录
                    if (TextUtils.equals(((ContactsFriendInfo) t).getId(), s)) {
                        ((ContactsFriendInfo) t).setSelected(true);
                    }
                }
            }

        }
    }

}
