package com.github.devholic.SOMAReport.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.devholic.SOMAReport.Datbase.ReferenceUtil;
import com.github.devholic.SOMAReport.Model.Projects;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@Path("/projects")
public class ProjectsController {

	ReferenceUtil reference_util = new ReferenceUtil("somarecord");
	
	//public JsonArray getMyProjects (String user_id)
	@GET
	public JsonArray getMyProjects(String email){
		JsonArray result = new JsonArray();
		
		try{
			result = reference_util.getMyProjects(email);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
		
	}
	/**************************************************************************
	 * 프로젝트 리스트를 가져온다.
	 * @return List<Projects>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public List<JsonObject> getProjectList(){
		
		List<JsonObject> project_list = new ArrayList<JsonObject>();
		
		
		try{
			project_list = reference_util.getAllProjects();
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return project_list;
	}
	
	/**************************************************************************
	 * 프로젝트 상세정보를 가져온다
	 * @param projectId
	 * @return Projects
	 *************************************************************************/
	@GET
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Projects getProjectDetails(@PathParam("projectId") String projectId){
		
		Projects project = new Projects();
		
		try{
					

		}catch(Exception e){
			e.printStackTrace();
		}
			
		return project;
	}
	
	/**************************************************************************
	 * 프로젝트를 입력한다
	 * @param projectName,level,sequence,main_mento,area,period,sub_mento,category
	 *************************************************************************/
	@POST
	@Path("/{projectName}/{level}")
	public Response insertProject(  @PathParam("projectName") String projectName,
								@PathParam("level") String level,
								@FormParam("sequence") String sequence,
								@FormParam("main_mento") String main_mento,
								@FormParam("area") String area,
								@FormParam("period") String period,
								@FormParam("sub_mento") String sub_mento,
								@FormParam("category") String category){
		try{
			System.out.println("post date - projectName = ["+ projectName + "]");
			System.out.println("post date - level = ["+ level + "]");
			System.out.println("post date - sequence = ["+ sequence + "]");
			System.out.println("post date - main_mento = ["+ main_mento + "]");
			System.out.println("post date - area = ["+ area + "]");
			System.out.println("post date - period = ["+ period + "]");
			System.out.println("post date - sub_mento = ["+ sub_mento + "]");
			System.out.println("post date - category = ["+ category + "]");
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("post : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("post : 500").build();
	}
	
	@PUT
	public Response updateProject(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("put : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("put : 500").build();
	}
	
	@DELETE
	public Response deleteProject(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("delete : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("delete : 500").build();
	}
	
	
}
