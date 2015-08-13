package com.github.devholic.SOMAReport.Model;

import java.util.ArrayList;

public class Report {
	
	String _id="";
	String _rev="";
	String type="";
	String project="";
	ArrayList<Report_info> report_info = new ArrayList<Report_info>();
	ArrayList<Attendee> attendee = new ArrayList<Attendee>();
	ArrayList<Absentee> absentee = new ArrayList<Absentee>();
	ArrayList<Report_details> report_details = new ArrayList<Report_details>();
	ArrayList<Report_attachments> report_attachments = new ArrayList<Report_attachments>();
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_rev() {
		return _rev;
	}
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public ArrayList<Report_info> getReport_info() {
		return report_info;
	}
	public void setReport_info(ArrayList<Report_info> report_info) {
		this.report_info = report_info;
	}
	public ArrayList<Attendee> getAttendee() {
		return attendee;
	}
	public void setAttendee(ArrayList<Attendee> attendee) {
		this.attendee = attendee;
	}
	public ArrayList<Absentee> getAbsentee() {
		return absentee;
	}
	public void setAbsentee(ArrayList<Absentee> absentee) {
		this.absentee = absentee;
	}
	public ArrayList<Report_details> getReport_details() {
		return report_details;
	}
	public void setReport_details(ArrayList<Report_details> report_details) {
		this.report_details = report_details;
	}
	public ArrayList<Report_attachments> getReport_attachments() {
		return report_attachments;
	}
	public void setReport_attachments(
			ArrayList<Report_attachments> report_attachments) {
		this.report_attachments = report_attachments;
	}
	
	
	
}
