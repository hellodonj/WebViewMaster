package com.lqwawa.lqbaselib.net.library;

public class ModelResult<T extends Model> extends Result {

    private T Model;

    public T getModel() {
        return Model;
    }

    public void setModel(T model) {
        Model = model;
    }

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && Model != null;
    }

}
