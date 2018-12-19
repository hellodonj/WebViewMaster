package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class BookCatalog {

    private String Id;
    private String Name;
    private List<BookCatalog> Children;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<BookCatalog> getChildren() {
        return Children;
    }

    public void setChildren(List<BookCatalog> children) {
        Children = children;
    }
}
