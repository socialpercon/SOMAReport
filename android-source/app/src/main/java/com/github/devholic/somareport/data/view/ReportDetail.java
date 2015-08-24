package com.github.devholic.somareport.data.view;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by JaeyeonLee on 2015. 8. 23..
 */
public class ReportDetail {
    private String title;
    private JSONArray attendee;
    private JSONArray absentee;
    private int number;
    private String place;
    private String time;
    private String topic, goal, issue, solution, plan, opinion;
    private String photo;
}
