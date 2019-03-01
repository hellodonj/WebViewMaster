package com.lqwawa.intleducation.factory.data.entity.response;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

public class LQModelMultipleParamIncludePagerResponse<T> extends BaseVo {

    private ModelBean<T> Model;
    private boolean HasError;
    private String ErrorMessage;

    public ModelBean<T> getModel() {
        return Model;
    }

    public void setModel(ModelBean<T> model) {
        Model = model;
    }

    public boolean isHasError() {
        return HasError;
    }

    public void setHasError(boolean hasError) {
        HasError = hasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public static class ModelBean<T> extends BaseVo{
        private PageBean Pager;
        private List<T> Data;
        private List<T> DataList;

        public PageBean getPager() {
            return Pager;
        }

        public void setPager(PageBean pager) {
            Pager = pager;
        }

        public List<T> getData() {
            return Data;
        }

        public void setData(List<T> data) {
            Data = data;
        }

        public List<T> getDataList() {
            return DataList;
        }

        public void setDataList(List<T> dataList) {
            DataList = dataList;
        }
    }

    public static class PageBean extends BaseVo{

        private int PageIndex;
        private int PageSize;
        private int RowsCount;
        private int FirstRowIndex;

        public int getPageIndex() {
            return PageIndex;
        }

        public void setPageIndex(int PageIndex) {
            this.PageIndex = PageIndex;
        }

        public int getPageSize() {
            return PageSize;
        }

        public void setPageSize(int PageSize) {
            this.PageSize = PageSize;
        }

        public int getRowsCount() {
            return RowsCount;
        }

        public void setRowsCount(int RowsCount) {
            this.RowsCount = RowsCount;
        }

        public int getFirstRowIndex() {
            return FirstRowIndex;
        }

        public void setFirstRowIndex(int FirstRowIndex) {
            this.FirstRowIndex = FirstRowIndex;
        }
    }
}
