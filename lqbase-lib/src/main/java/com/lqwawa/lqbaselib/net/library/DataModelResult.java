package com.lqwawa.lqbaselib.net.library;

public class DataModelResult<T> extends DataResult {

    private DataModel<T> Model;

    public DataModel<T> getModel() {
        return Model;
    }

    public void setModel(DataModel<T> model) {
        Model = model;
    }

    public DataModelResult<T> parse(String jsonString) {
        return this;
    }

}
