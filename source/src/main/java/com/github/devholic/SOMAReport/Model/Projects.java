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
		
	
	public Projects() {
		super();
		this.projectId = "";
		this.projectTitle = "";
		this.projectType = "";
		this.projectMentor = null;
		this.projectSection = "";
		this.projectStage = null;
		this.projectCategory = "";
		this.projectMentee = null;
	}
	
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

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String[] getProjectMentor() {
		return projectMentor;
	}

	public void setProjectMentor(String[] projectMentor) {
		this.projectMentor = projectMentor;
	}

	public String getProjectSection() {
		return projectSection;
	}

	public void setProjectSection(String projectSection) {
		this.projectSection = projectSection;
	}

	public int[] getProjectStage() {
		return projectStage;
	}

	public void setProjectStage(int[] projectStage) {
		this.projectStage = projectStage;
	}

	public String getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(String projectCategory) {
		this.projectCategory = projectCategory;
	}

	public String[] getProjectMentee() {
		return projectMentee;
	}

	public void setProjectMentee(String[] projectMentee) {
		this.projectMentee = projectMentee;
	}
	
	
	
	
}
