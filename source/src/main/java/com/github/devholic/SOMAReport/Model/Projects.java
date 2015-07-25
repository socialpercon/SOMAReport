package com.github.devholic.SOMAReport.Model;

public class Projects {

	String projectName;
	String period;
	String main_mento;
	String sub_mento;
	String category;
	String sequence; // 기수
	String level; //단계
	String area; //분야
	String[] userIds; //프로젝트에 소속된 사용자
	
	public Projects(){
		projectName = "";
		period = "";
		main_mento = "";
		sub_mento = "";
		category = "";
		sequence = "";
		level = "";
		area = "";
		userIds = null;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getMain_mento() {
		return main_mento;
	}

	public void setMain_mento(String main_mento) {
		this.main_mento = main_mento;
	}

	public String getSub_mento() {
		return sub_mento;
	}

	public void setSub_mento(String sub_mento) {
		this.sub_mento = sub_mento;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String[] getUserIds() {
		return userIds;
	}

	public void setUserIds(String[] userIds) {
		this.userIds = userIds;
	}
	
	
}
