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

import com.github.devholic.SOMAReport.Model.Projects;


@Path("/projects")
public class ProjectsController {

	
	/**************************************************************************
	 * 프로젝트 리스트를 가져온다.
	 * @return List<Projects>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public List<Projects> getProjectList(){
		
		List<Projects> project_list = new ArrayList<Projects>();
		
		Projects pj1 = new Projects();
		Projects pj2 = new Projects();
		Projects pj3 = new Projects();
		
		try{
			
			String [] pj1_userIds = {"user1","user2"};
			pj1.setProjectId("pj1");
			pj1.setProjectName("somaexpensify");
			pj1.setSequence("6");
			pj1.setLevel("1-1");
			pj1.setUserIds(pj1_userIds);
			
			String [] pj2_userIds = {"user2","user3"};
			pj2.setProjectId("pj2");
			pj2.setSequence("6");
			pj2.setLevel("1-1");
			pj2.setProjectName("somareport");
			pj2.setUserIds(pj2_userIds);
			
			String [] pj3_userIds = {"user1","user3"};
			pj3.setProjectId("pj3");
			pj3.setSequence("6");
			pj3.setLevel("1-1");
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
			
			String [] userIds = {"user1","user2","user3"};
			
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
	
	/**************************************************************************
	 * 프로젝트를 입력한다
	 * @param projectName,level,sequence,main_mento,area,period,sub_mento,category
	 *************************************************************************/
	@POST
	@Path("/{projectName}/{level}/{sequence}/{main_mento}/{area}/{period}/{sub_mento}/{category}")
	public void insertProject(  @PathParam("projectName") String projectName,
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@PUT
	public void updateProject(){
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@DELETE
	public void deleteProject(){
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
