package com.github.devholic.SOMAReport.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import com.github.devholic.SOMAReport.Model.Projects;


@Path("/projects")
public class ProjectsController {

	
	//일단 데이터를 박아놓음
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public List<Projects> getUserList(){
		
		List<Projects> project_list = new ArrayList<Projects>();
		
		Projects pj1 = new Projects();
		Projects pj2 = new Projects();
		Projects pj3 = new Projects();
		
		try{
			
			String [] pj1_userIds = {"123","456"};
			pj1.setProjectId("pj1");
			pj1.setProjectName("somaexpensify");
			pj1.setUserIds(pj1_userIds);
			
			String [] pj2_userIds = {"456","789"};
			pj2.setProjectId("pj2");
			pj2.setProjectName("somareport");
			pj2.setUserIds(pj2_userIds);
			
			String [] pj3_userIds = {"123","789"};
			pj3.setProjectId("pj3");
			pj3.setProjectName("somareservation");
			pj3.setUserIds(pj3_userIds);

			project_list.add(pj1);
			project_list.add(pj2);
			project_list.add(pj3);
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return project_list;
	}
	
	@GET
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Projects getProjectDetails(@PathParam("projectId") String projectId){
		
		Projects project = new Projects();
		
		try{
			
			String [] userIds = {"123","456","789"};
			
			project.setProjectId(projectId);
			project.setArea("area");
			project.setCategory("category");
			project.setLevel("level");
			project.setMain_mento("main_mento");
			project.setPeriod("period");
			project.setProjectName("projectName");
			project.setSequence("sequence");
			project.setSub_mento("sub_mento");
			project.setUserIds(userIds);			

		}catch(Exception e){
			e.printStackTrace();
		}
			
		return project;
	}
}
