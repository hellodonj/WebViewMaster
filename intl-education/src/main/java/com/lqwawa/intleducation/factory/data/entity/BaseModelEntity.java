package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 后台接口返回数据接收基类 Model类型
 * @date 2018/06/05 16:48
 * @history v1.0
 * **********************************
 */
public class BaseModelEntity<T> extends BaseVo{

    private ModelBean<T> Model;
    private boolean HasError;
    private String ErrorMessage;

    public ModelBean<T> getModel() {
        return Model;
    }

    public void setModel(ModelBean<T> Model) {
        this.Model = Model;
    }

    public boolean isHasError() {
        return HasError;
    }

    public void setHasError(boolean HasError) {
        this.HasError = HasError;
    }

    public Object getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public static class ModelBean<T> extends BaseVo{

        private String QrCode;
        private PageBean Page;
        private List<T> TeacherList;

        public String getQrCode() {
            return QrCode;
        }

        public void setQrCode(String QrCode) {
            this.QrCode = QrCode;
        }

        public PageBean getPage() {
            return Page;
        }

        public void setPage(PageBean Page) {
            this.Page = Page;
        }

        public List<T> getTeacherList() {
            return TeacherList;
        }

        public void setTeacherList(List<T> TeacherList) {
            this.TeacherList = TeacherList;
        }

        public static class PageBean extends BaseVo{
            /**
             * PageIndex : 0
             * PageSize : 10
             * RowsCount : 13
             * FirstRowIndex : 0
             */

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
}
