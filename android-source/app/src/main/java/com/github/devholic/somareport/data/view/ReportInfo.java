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
    private String topic;
    private String[] attendee;

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String[] getAttendee() {
        return attendee;
    }

    public void setAttendee(String[] attendee) {
        this.attendee = attendee;
    }

    public ReportInfo(JSONObject doc) {
        try {
            this.reportId = doc.getString("reportId");
            this.title = doc.getString("title");
            this.topic = doc.getString("topic");
            JSONArray att = doc.getJSONArray("attendee");
            this.attendee = new String[att.length()];
            for (int i=0; i<att.length(); i++) {
                this.attendee[i] = att.getJSONObject(i).getString("id");
            }
        } catch (JSONException e) {
            Log.e("ReportInfo Constructor", e.getLocalizedMessage());
        }
    }
}
