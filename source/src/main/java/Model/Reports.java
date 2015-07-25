package Model;

import java.util.Date;

public class Reports {
	
	String [] attended_mentee ;
	Integer count;
	Date date;
	String location;
	Date start_date;
	Date end_date;
	Date except_start_date;
	Date except_end_date;
	String title;
	String purpose;
	String forwarding;
	String solution;
	String plan;
	String mento_opinion;
	String etc;
	
	public Reports(){
		attended_mentee = null;
		count = 0;
		date = null;
		location = "";
		start_date = null;
		end_date = null;
		except_start_date = null;
		except_end_date = null;
		title = "";
		purpose = "";
		forwarding = "";
		solution = "";
		plan = "";
		mento_opinion = "";
		etc = "";
			
	}
	
	public String[] getAttended_mentee() {
		return attended_mentee;
	}
	public void setAttended_mentee(String[] attended_mentee) {
		this.attended_mentee = attended_mentee;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	public Date getExcept_start_date() {
		return except_start_date;
	}
	public void setExcept_start_date(Date except_start_date) {
		this.except_start_date = except_start_date;
	}
	public Date getExcept_end_date() {
		return except_end_date;
	}
	public void setExcept_end_date(Date except_end_date) {
		this.except_end_date = except_end_date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getForwarding() {
		return forwarding;
	}
	public void setForwarding(String forwarding) {
		this.forwarding = forwarding;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getMento_opinion() {
		return mento_opinion;
	}
	public void setMento_opinion(String mento_opinion) {
		this.mento_opinion = mento_opinion;
	}
	public String getEtc() {
		return etc;
	}
	public void setEtc(String etc) {
		this.etc = etc;
	}
	
	
}