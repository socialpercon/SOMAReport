package com.github.devholic.somareport.data.view;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JaeyeonLee on 2015. 8. 23..
 */
public class ReportInfo {

    public static final int UNCONFIRMED = 1;
    public static final int BYPROJECT = 2;

    private String reportId;
    private String title;
    private String date;
    private String[] attendee;
    private String projectId;
    private String projectTitle;
    private boolean confirmed;

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String[] getAttendee() {
        return attendee;
    }

    public void setAttendee(String[] attendee) {
        this.attendee = attendee;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public ReportInfo(JSONObject doc) {
        try {
            this.reportId = doc.getString("_id");
            this.title = doc.getString("topic");
            this.date = doc.getString("date");
            JSONArray att = doc.getJSONArray("attendee");
            this.attendee = new String[att.length()];
            if (doc.has("confirmed")) this.confirmed = true;
            else this.confirmed = false;

            for (int i=0; i<att.length(); i++) {
                this.attendee[i] = att.getJSONObject(i).getString("id");
            }

            this.projectId = doc.getString("project");
            this.projectTitle = doc.getString("projectTitle");

        } catch (JSONException e) {
            Log.e("ReportInfo Constructor", e.getLocalizedMessage());
        }
    }
}
