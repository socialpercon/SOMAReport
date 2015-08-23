package com.github.devholic.somareport.data.view;

/**
 * Created by JaeyeonLee on 2015. 8. 23..
 */
public class ReportInfo {

    public static final int UNCONFIRMED = 1;
    public static final int BYPROJECT = 2;

    private String reportId;
    private String title;
    private String topic;
    private String attendee;

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

    public String getAttendee() {
        return attendee;
    }

    public void setAttendee(String attendee) {
        this.attendee = attendee;
    }
}
