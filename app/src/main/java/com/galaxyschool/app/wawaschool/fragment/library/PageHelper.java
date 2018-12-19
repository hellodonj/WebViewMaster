package com.galaxyschool.app.wawaschool.fragment.library;

public class PageHelper {

    public static final int DEFAULT_PAGE_SIZE = 30;

    private int currIndex;
    private int totalCount;
    private int currPageIndex;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int fetchingPageIndex;

    public PageHelper() {

    }

    public PageHelper(int currIndex, int totalCount, int currPageIndex,
                      int pageSize) {
        this.currIndex = currIndex;
        this.totalCount = totalCount;
        this.currPageIndex = currPageIndex;
        this.pageSize = pageSize;
    }

    public int getCurrIndex() {
        return currIndex;
    }

    public void setCurrIndex(int currIndex) {
        this.currIndex = currIndex;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrPageIndex() {
        return currPageIndex;
    }

    public void setCurrPageIndex(int currPageIndex) {
        this.currPageIndex = currPageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFetchingPageIndex() {
        return fetchingPageIndex;
    }

    public void setFetchingPageIndex(int fetchingPageIndex) {
        this.fetchingPageIndex = fetchingPageIndex;
    }

    public PageHelper update(int currIndex, int totalCount, int currPageIndex,
                      int pageSize) {
        this.currIndex = currIndex;
        this.totalCount = totalCount;
        this.currPageIndex = currPageIndex;
        this.pageSize = pageSize;
        return this;
    }

    public void clear() {
        this.currIndex = 0;
        this.totalCount = 0;
        this.currPageIndex = 0;
        this.fetchingPageIndex = 0;
    }

    public int getLastPageIndex() {
        return currPageIndex > 0 ? currPageIndex - 1 : 0;
    }

    public void lastPage() {
        if (currPageIndex > 0) {
            currPageIndex--;
        }
    }

    public int getNextPageIndex() {
        int pageCount = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            pageCount++;
        }
        return (currPageIndex < pageCount - 1) ? currPageIndex + 1 : currPageIndex;
    }

    public void nextPage() {
        currPageIndex++;
    }

    public boolean isFirstPage() {
        return currIndex % pageSize == 0;
    }

    public boolean isLastPage() {
        return totalCount - (currIndex + 1) <= 0;
    }

    public int isTotalPageCountChanged(int newTotalCount) {
        if (newTotalCount == this.totalCount) {
            return 0;
        }
        int pageCount = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            pageCount++;
        }
        int newPageCount = newTotalCount / pageSize;
        if (newTotalCount % pageSize > 0) {
            newPageCount++;
        }

        if (newPageCount == pageCount) {
            return 0;
        } else if (newPageCount > pageCount) {
            return 1;
        } else {
            return -1;
        }
    }

}
