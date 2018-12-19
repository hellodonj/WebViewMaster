package com.galaxyschool.app.wawaschool.fragment.library;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.pojo.PagerArgs;

public class MyPageHelper extends PageHelper {

    public MyPageHelper() {
        super();
    }

    public MyPageHelper(int currIndex, int totalCount, int currPageIndex,
                      int pageSize) {
        super(currIndex, totalCount, currPageIndex, pageSize);
    }

    public MyPageHelper(PagerArgs args) {
        super(args.getFirstRowIndex(), args.getRowsCount(), args.getPageIndex(),
                args.getPageSize());
    }

    public PagerArgs getPagerArgs() {
        PagerArgs args = new PagerArgs();
        args.setFirstRowIndex(getCurrIndex());
        args.setRowsCount(getTotalCount());
        args.setPageIndex(getCurrPageIndex());
        args.setPageSize(getPageSize());
        return args;
    }

    public PagerArgs getFetchingPagerArgs() {
        PagerArgs args = new PagerArgs();
        args.setFirstRowIndex(getCurrIndex());
        args.setRowsCount(getTotalCount());
        args.setPageIndex(getFetchingPageIndex());
        args.setPageSize(getPageSize());
        return args;
    }

    public boolean isFetchingPageIndex(PagerArgs args) {
        if (args != null) {
            setTotalCount(args.getRowsCount());
            if (args.getPageIndex() == getFetchingPageIndex()) {
                return true;
            }
        }
        return false;
    }

    public boolean isFetchingFirstPage() {
        return getFetchingPageIndex() == 0;
    }

    public boolean isFetchingCurrPage(PagerArgs args) {
        if (args != null) {
            if (args.getPageIndex() == getCurrPageIndex()) {
                return true;
            }
        }
        return false;
    }

    public MyPageHelper updateByPagerArgs(PagerArgs args) {
        if (args != null) {
            super.update(args.getFirstRowIndex(), args.getRowsCount(),
                    args.getPageIndex(), args.getPageSize());
        }
        return this;
    }

    public MyPageHelper updateTotalCountByJsonString(String jsonStr) {
        int totalCount = parseTotalCount(jsonStr);
        if (totalCount >= 0) {
            setTotalCount(totalCount);
        }
        return this;
    }

    public static int parseTotalCount(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return -1;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            if (!jsonObject.containsKey("total")) {
                return -1;
            }
            return jsonObject.getInteger("total");
        } catch (Exception e) {
            return -1;
        }
    }

}
