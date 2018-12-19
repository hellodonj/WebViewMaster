package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author medici
 * @desc 购买课程，显示的章节信息
 */
public class PayChapterEntity extends BaseVo{

    private int id;
    private int sort;
    private long price;
    private String name;
    private boolean buyed;
    private boolean select;
    private boolean highlight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBuyed() {
        return buyed;
    }

    public void setBuyed(boolean buyed) {
        this.buyed = buyed;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
}
