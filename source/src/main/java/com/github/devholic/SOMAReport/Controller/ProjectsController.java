package com.github.devholic.SOMAReport.Controller;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.github.devholic.SOMAReport.Model.Projects;


@Path("/projects")
public class ProjectsController {

	Logger logger = Logger.getLogger();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	@Encoded
	public Projects getUserList(){
		
		Projects project = new Projects();
		
		try{
			logger.debug("GET projects");
			
			String [] userIds = {"dd","cc"};
			project.setProjectId("projectId");
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
