package com.github.devholic.SOMAReport.Controller;

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
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;

@Path("/projects")
public class ProjectsController {

	// Log4j setting
	private final Logger logger = Logger.getLogger(ProjectsController.class);

	DatabaseController dbCtrl = new DatabaseController();

	/**************************************************************************
	 * 나의 프로젝트 가져오기
	 * 
	 * @param _id
	 * @return JSONArray [{title, stage, mentor, mentee[]}]
	 *************************************************************************/
	@GET
	@Path("/getMyProjects")
	public JSONArray getMyProject(String userId) {
		
		JSONArray list = new JSONArray();
		JSONObject jo = JSONFactory.inputStreamToJson(dbCtrl.getByView("_design/project", "all_my_project",
				new Object[] { userId + " ", " " }, new Object[] { userId, " " }, true, true));
		JSONArray arr = JSONFactory.getData(jo);
		
		for (int i = 0; i < arr.length(); i++) {
			logger.info(arr.get(i));
			JSONObject doc = arr.getJSONObject(i).getJSONObject("doc");
			JSONObject project = new JSONObject();
			project.put("title", doc.get("title"));
			JSONArray stage = doc.getJSONArray("stage");
			if (stage.length() == 2 || stage.getInt(2) == 0)
				project.put("stage", stage.get(0) + "기 " + stage.get(1) + "단계 프로젝트");
			else
				project.put("stage", stage.get(0) + "기 " + stage.get(1) + "단계 " + stage.get(2) + "차 프로젝트");
			project.put("mentor", doc.get("mentor"));
			project.put("mentee", doc.getJSONArray("mentee"));
			list.put(project);
		}
		return list;
	}

	/**************************************************************************
	 * 프로젝트 아이디로 프로젝트 상세정보 가져오기
	 * 
	 * @param projectId
	 * @return JSONObject
	 *************************************************************************/
	public JSONObject getDetailByProjectId(String projectId) {

		JSONObject detail = JSONFactory.inputStreamToJson(dbCtrl.getDoc(projectId));
		return detail;
	}

	/**************************************************************************
	 * 프로젝트 아이디로 프로젝트 상세정보 가져오기 _ URL Path GET
	 * 
	 * @param projectId
	 * @return Projects
	 *************************************************************************/
	@GET
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONObject getProjectDetails(@PathParam("projectId") String projectId) {

		JSONObject detail = JSONFactory.inputStreamToJson(dbCtrl.getDoc(projectId));
		return detail;
	}

	/**************************************************************************
	 * 프로젝트 전체 리스트를 가져온다.
	 * 
	 * @return List<JSONObject>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getProjectList() {

		JSONArray projectList = JSONFactory.getData(JSONFactory.inputStreamToJson(dbCtrl.getByView("_design/project", "all_project", true, true)));
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		return projectList;
	}

	/*********************************************************************
	 * 프로젝트 아이디로 프로젝트의 기본 정보를 가져온다
	 * 
	 * @param projectId
	 * @return
	 ********************************************************************/
	public JSONObject getProjectInfo(String projectId) {

		JSONObject projectInfo = new JSONObject();
		JSONObject project = JSONFactory.inputStreamToJson(dbCtrl.getDoc(projectId));
		project.put("project_type", project.getString("project_type"));
		project.put("title", project.getString("title"));
		project.put("mentor", project.getString("mentor"));
		JSONArray stage = project.getJSONArray("stage");
		if (stage.length() == 2 || stage.getInt(2) == 0)
			project.put("stage", stage.get(0) + "기 " + stage.get(1) + "단계 프로젝트");
		else
			project.put("stage", stage.get(0) + "기 " + stage.get(1) + "단계 " + stage.get(2) + "차 프로젝트");
		
		JSONObject mentor = JSONFactory.inputStreamToJson(dbCtrl.getDoc(project.getString("mentor")));
		project.put("section", mentor.getString("section"));
		project.put("field", project.getString("field"));
		project.put("mentee", project.getString("mentee"));
		ReportsController rCtrl = new ReportsController();
		project.put("mentoring_num", rCtrl.getReportByProjectId(projectId).length());
		
		return projectInfo;
	}

	/*************************************************************************
	 * 프로젝트를 입력한다
	 * 
	 * @param document
	 * @return boolean
	 ************************************************************************/
	public boolean insertProject(JSONObject document) {
		boolean result = false;
		try {
		//	String id = doc_util.putDoc(document);
		//	logger.debug("inserted project id = " + id);
		//	result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@PUT
	public Response updateProject() {
		try {
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("put : 200").build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("put : 500").build();
	}

	/************************************************************************
	 * 프로젝트 아이디로 프로젝트를 삭제한다.
	 * 
	 * @param reportId
	 * @return
	 ***********************************************************************/
	@DELETE
	public boolean deleteProject(String projectId) {
		boolean result = false;

		try {
			JSONObject jo = JSONFactory.inputStreamToJson(dbCtrl.getDoc(projectId));
			String rev = jo.getString("_rev");
			result = dbCtrl.deleteDoc(projectId, rev);
			logger.debug("delete | report id = " + projectId + "\n");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return result;
	}

}
