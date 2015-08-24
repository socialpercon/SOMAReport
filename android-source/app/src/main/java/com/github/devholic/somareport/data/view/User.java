package com.github.devholic.somareport.data.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by devholic on 15. 7. 23..
 */
public class User implements Parcelable{

    private String id;
    private String name;
    private String email;
    private String role;
    private String belong;
    private int[] year;

    public User(Parcel par) {
        this.id = par.readString();
        this.name = par.readString();
        this.email = par.readString();
        this.role = par.readString();
        this.belong = par.readString();
        this.year = par.createIntArray();
    }

    public User(JSONObject doc) {
        try {
            this.id = doc.getString("id");
            this.name = doc.getString("name");
            this.email = doc.getString("email");
            this.role = doc.getString("role");
            this.belong = doc.getString("belong");

        } catch (JSONException e) {
            Log.e("User Constructor", e.getLocalizedMessage());
        }
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int[] getYear() {
        return year;
    }

    public void setYear(int[] year) {
        this.year = year;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.role);
        dest.writeString(this.belong);
        dest.writeIntArray(this.year);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };


}
