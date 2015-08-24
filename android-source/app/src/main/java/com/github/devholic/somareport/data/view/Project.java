package com.github.devholic.somareport.data.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by devholic on 15. 7. 23..
 */
public class Project implements Parcelable {
    private String id, mentor, title, stage;
    private String[] mentee;

    public Project(Parcel in) {
        this.id = in.readString();
        this.mentor = in.readString();
        this.title = in.readString();
        this.stage = in.readString();
        this.mentee = in.createStringArray();
    }

    public Project (JSONObject doc) {
        try {
            this.id = doc.getString("_id");
            this.mentor = doc.getString("mentor");
            this.title = doc.getString("title");
            this.stage = doc.getString("stage");
            JSONArray mentees = doc.getJSONArray("mentee");
            this.mentee = new String[mentees.length()];
            for (int i=0; i<mentees.length(); i++) {
                mentee[i] = mentees.getString(i);
            }
        } catch (JSONException e) {
            Log.e("Project", e.getLocalizedMessage());
        }
    }

    public String getMentor() {
        return mentor;
    }

    public void setMentor(String mentor) {
        this.mentor = mentor;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getMentee() {
        return mentee;
    }

    public void setMentee(String[] mentee) {
        this.mentee = mentee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.mentor);
        dest.writeString(this.title);
        dest.writeString(this.stage);
        dest.writeStringArray(this.mentee);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String toString() {
        return "id="+id+" mentor="+mentor+" title="+title+" stage="+stage+" mentee"+mentee.toString();
    }
}
