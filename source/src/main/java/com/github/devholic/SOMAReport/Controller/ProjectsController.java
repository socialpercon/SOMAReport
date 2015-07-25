package com.github.devholic.SOMAReport.Controller;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import Model.Projects;


@Path("/projects")
public class ProjectsController {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	@Encoded
	public Projects getUserList(){
		
		Projects project = new Projects();
		
		try{
			

			

		}catch(Exception e){
			e.printStackTrace();
		}
			
		return project;
	}
}
