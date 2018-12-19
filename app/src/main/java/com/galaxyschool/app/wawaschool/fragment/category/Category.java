package com.galaxyschool.app.wawaschool.fragment.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Category implements Parcelable {

    private int type;
    private String name;
    private boolean isDefault;
    private boolean fillWithDefaultValue = true;
    private List<CategoryValue> allValues;
    private CategoryValue currValue;

    public Category() {

    }

    public Category(Parcel src) {
        this.type = src.readInt();
        this.name = src.readString();
        this.currValue = src.readParcelable(CategoryValue.class.getClassLoader());
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isFillWithDefaultValue() {
        return fillWithDefaultValue;
    }

    public void setFillWithDefaultValue(boolean fillDefault) {
        this.fillWithDefaultValue = fillDefault;
    }

    public List<CategoryValue> getAllValues() {
        return allValues;
    }

    public void setAllValues(List<CategoryValue> allValues) {
        this.allValues = allValues;
    }

    public CategoryValue getCurrValue() {
        return currValue;
    }

    public void setCurrValue(CategoryValue currValue) {
        this.currValue = currValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dst, int flags) {
        dst.writeString(this.name);
        dst.writeInt(this.type);
        if (this.currValue != null) {
            dst.writeParcelable(this.currValue, flags);
        }
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel src) {
            return new Category(src);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

}
