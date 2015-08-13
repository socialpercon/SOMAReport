package com.github.devholic.SOMAReport.Model;

public class Report_info {
	String mentoring_num = "";
	String date = "";
	String place = "";
	String [] start_time = {};
	String [] end_time = {};
	String whole_time = "";
	String except_time = "";
	String total_time = "";
	public String getMentoring_num() {
		return mentoring_num;
	}
	public void setMentoring_num(String mentoring_num) {
		this.mentoring_num = mentoring_num;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String[] getStart_time() {
		return start_time;
	}
	public void setStart_time(String[] start_time) {
		this.start_time = start_time;
	}
	public String[] getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String[] end_time) {
		this.end_time = end_time;
	}
	public String getWhole_time() {
		return whole_time;
	}
	public void setWhole_time(String whole_time) {
		this.whole_time = whole_time;
	}
	public String getExcept_time() {
		return except_time;
	}
	public void setExcept_time(String except_time) {
		this.except_time = except_time;
	}
	public String getTotal_time() {
		return total_time;
	}
	public void setTotal_time(String total_time) {
		this.total_time = total_time;
	}
	
	
}
