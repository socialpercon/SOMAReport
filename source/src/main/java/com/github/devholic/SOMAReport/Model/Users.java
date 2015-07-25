package com.github.devholic.SOMAReport.Model;

public class Users {

	String userId;
	String userName;
	Integer userAge;
	String userSex;
	String userYear;
	
	public Users(){
		userId = "";
		userName = "";
		userAge = 0;
		userSex = "";
		userYear = "";
	}
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getUserAge() {
		return userAge;
	}
	public void setUserAge(Integer userAge) {
		this.userAge = userAge;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public String getUserYear() {
		return userYear;
	}
	public void setUserYear(String userYear) {
		this.userYear = userYear;
	}
	
	
}
