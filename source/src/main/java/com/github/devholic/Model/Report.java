package com.github.devholic.Model;

public class Report {

    private String _id;
    private String _rev;
    private String Class;

    public void setId(String s) {
        _id = s;
    }

    public String getId() {
        return _id;
    }

    Report(String _id) {
        super();
        this._id = _id;
        Class = "mammal";
    }


    public String getclass() {
        return Class;
    }


    public Report setClass(String class1) {
        Class = class1;
        return this;
    }

    Report() {
        super();

    }
}
