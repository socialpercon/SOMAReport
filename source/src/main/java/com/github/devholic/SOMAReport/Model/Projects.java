package com.github.devholic.SOMAReport.Model;

import com.github.devholic.SOMAReport.Datbase.DocumentUtil;

public class Projects {

	private String projectId;
	private String projectTitle;
	private String projectType;
	private String[] projectMentor;
	private String projectSection;
	private int[] projectStage;
	private String projectCategory;
	private String[] projectMentee;
		
	public Projects(String id) {
		projectId = id;
		DocumentUtil docutil = new DocumentUtil("somarecord");
		
	}

	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectTitle;
	}
	public void setProjectName(String projectName) {
		this.projectTitle = projectName;
	}
	
}
