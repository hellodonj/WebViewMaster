package com.lqwawa.lqbaselib.net.library;

public class DataModel<T> extends Model {

    private T Data;

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

}
