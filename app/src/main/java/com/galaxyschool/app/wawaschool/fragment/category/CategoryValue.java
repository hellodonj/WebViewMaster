package com.galaxyschool.app.wawaschool.fragment.category;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryValue implements Parcelable {

    private String id;
    private String value;
    private String newValue;
    private boolean isDefault;
    private String price;

    public CategoryValue() {

    }

    public CategoryValue(Parcel src) {
        this.id = src.readString();
        this.value = src.readString();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dst, int flags) {
        dst.writeString(this.id);
        dst.writeString(this.value);
    }

    public static final Creator<CategoryValue> CREATOR = new Creator<CategoryValue>() {
        @Override
        public CategoryValue createFromParcel(Parcel src) {
            return new CategoryValue(src);
        }

        @Override
        public CategoryValue[] newArray(int size) {
            return new CategoryValue[size];
        }
    };
}
