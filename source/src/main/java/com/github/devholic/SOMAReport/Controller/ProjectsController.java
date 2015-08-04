package com.github.devholic.SOMAReport.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;


@Path("/projects")
public class ProjectsController {

	//Log4j setting
	private final Logger logger = Logger.getLogger(ProjectsController .class);
		
	ReferenceUtil reference_util = new ReferenceUtil("somarecord");
	DocumentUtil doc_util = new DocumentUtil("somarecord");
	
	
	/**************************************************************************
	 * 나의 프로젝트 가져오기 
	 * @param email
	 * @return
	 *************************************************************************/
	
	public JSONArray getMyProjects(String email){
		JSONArray result = new JSONArray();
		try{
			result = reference_util.getMyProjects(email);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**************************************************************************
	 * 프로젝트 아이디로 프로젝트 상세정보 가져오기 
	 *************************************************************************/
	public JsonObject getDetailByProjectId(String projectId){
		JsonObject detail = new JsonObject();
		
		try{
			detail = doc_util.getDoc(projectId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return detail;
		
	}
	
	/**************************************************************************
	 * 프로젝트 아이디로 프로젝트 상세정보 가져오기  _ URL Path GET
	 * @param projectId
	 * @return Projects
	 *************************************************************************/
	@GET
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public JsonObject getProjectDetails(@PathParam("projectId") String projectId){
		
		JsonObject detail = new JsonObject();
		
		try{
			detail = doc_util.getDoc(projectId);
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return detail;
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

	
	/*********************************************************************
	 * 프로젝트 아이디로 프로젝트의 기본 정보를 가져온다
	 * @param projectId
	 * @return
	 ********************************************************************/
	public JsonObject getProjectInfo (String projectId){
		
		JsonObject projectInfo = new JsonObject();
		
		try{
			projectInfo = reference_util.getProjectInfo(projectId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return projectInfo;
	}

	
	/*************************************************************************
	 * 프로젝트를 입력한다 
	 * @param document
	 * @return boolean
	 ************************************************************************/
	public boolean insertProject(JsonObject document){
		boolean result = false;
		try{
			String id = doc_util.putDoc(document);
			logger.debug("inserted project id = "+id);
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
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
	
	
	/************************************************************************
	 * 프로젝트 아이디로 프로젝트를 삭제한다.
	 * @param reportId
	 * @return
	 ***********************************************************************/
	@DELETE
	public boolean deleteProject(String reportId){
		boolean result = false;
		
		try{
			logger.debug("delete | project id = "+ reportId+ "\n");
			doc_util.deleteDoc(reportId);
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	
}
