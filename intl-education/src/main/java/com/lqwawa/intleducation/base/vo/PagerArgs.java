package com.lqwawa.intleducation.base.vo;

/**
 * Created by XChen on 2018/1/15.
 * email:man0fchina@foxmail.com
 */

public class PagerArgs extends BaseVo{
    private int PageIndex;
    private int PageSize;
    private int RowsCount;
    private int FirstRowIndex;

    public PagerArgs() {

    }

    public PagerArgs(int pageIndex, int pageSize) {
        PageIndex = pageIndex;
        PageSize = pageSize;
    }

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int pageIndex) {
        PageIndex = pageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public int getRowsCount() {
        return RowsCount;
    }

    public void setRowsCount(int rowsCount) {
        RowsCount = rowsCount;
    }

    public int getFirstRowIndex() {
        return FirstRowIndex;
    }

    public void setFirstRowIndex(int firstRowIndex) {
        FirstRowIndex = firstRowIndex;
    }
}
