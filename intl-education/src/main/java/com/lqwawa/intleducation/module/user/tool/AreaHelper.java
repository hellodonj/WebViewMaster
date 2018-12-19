package com.lqwawa.intleducation.module.user.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.db.DbHelper;
import com.lqwawa.intleducation.module.user.vo.AreaListVo;
import com.lqwawa.intleducation.module.user.vo.AreaVo;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class AreaHelper {
    private static boolean inited = false;
    private static List<AreaVo> areaList = null;
    private static OnReloadAreaListListener listener;

    public static List<AreaVo> getAreaList() {
        return areaList;
    }

    public static List<AreaVo> getProvinceList() {
        if (areaList == null) {
            return null;
        }
        List<AreaVo> list = new ArrayList<AreaVo>();
        for (int i = 0; i < areaList.size(); i++) {
            if (areaList.get(i).getLevelNum() == 1) {
                list.add(areaList.get(i));
            }
        }
        return list;
    }

    public static List<AreaVo> getAreaSubList(int parentId, int parentLevelNum) {
        if (areaList == null) {
            return null;
        }
        List<AreaVo> list = new ArrayList<AreaVo>();
        for (int i = 0; i < areaList.size(); i++) {
            if (areaList.get(i).getLevelNum() == parentLevelNum
                    && areaList.get(i).getParentId() == parentId) {
                list.add(areaList.get(i));
            }
        }
        return list;
    }


    public static void reloadAreaList(OnReloadAreaListListener l) {
        listener = l;
        getAreaListFormServer(true);
    }

    public static void init() {
        if (!inited) {
            getAreaListFormServer(false);
        }
    }

    private static void getAreaListFormServer(final boolean isReload) {
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetAreaList);

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                AreaListVo result = JSON.parseObject(s,
                        new TypeReference<AreaListVo>() {
                        });
                if (result != null) {
                    if (result.getCode() == 0) {
                        if (result.getAreaList() != null && result.getAreaList().size() > 0) {
                            areaList = result.getAreaList();
                            //saveAreaList();
                            inited = true;
                            if (isReload && listener != null) {
                                listener.onSuccess();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (listener != null && isReload) {
                    listener.onFailed();
                }
                //Log.d("XChenTest", throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private static void saveAreaList() {
        DbManager db = x.getDb(DbHelper.getDaoConfig());
        try {
            for (int i = 0; i < areaList.size(); i++) {
                db.save(areaList.get(i));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private static boolean readLocalAreaList() {
        DbManager db = x.getDb(DbHelper.getDaoConfig());
        try {
            List<AreaVo> list = db.findAll(AreaVo.class);
            if (list != null && list.size() > 0) {
                areaList = list;
                inited = true;
                return true;
            } else {
                return false;
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface OnReloadAreaListListener {
        public void onSuccess();

        public void onFailed();
    }
}
