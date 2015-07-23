package com.github.devholic.somareport.data.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by devholic on 15. 7. 23..
 */
public class Project implements Parcelable {
    private String date, title;
    private int period, stage, project; // 기수 단계 1차/2차
    private String area, department, location; // 분야 분과 장소

    public Project(String date, String title, int period, int stage, int project, String area, String department, String location) {
        this.date = date;
        this.title = title;
        this.period = period;
        this.stage = stage;
        this.project = project;
        this.area = area;
        this.department = department;
        this.location = location;
    }

    public Project(Parcel in) {
        this.date = in.readString();
        this.title = in.readString();
        this.period = in.readInt();
        this.stage = in.readInt();
        this.project = in.readInt();
        this.area = in.readString();
        this.department = in.readString();
        this.location = in.readString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.title);
        dest.writeInt(this.period);
        dest.writeInt(this.stage);
        dest.writeInt(this.project);
        dest.writeString(this.area);
        dest.writeString(this.department);
        dest.writeString(this.location);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
