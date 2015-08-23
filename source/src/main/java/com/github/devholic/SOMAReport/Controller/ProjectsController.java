package com.github.devholic.SOMAReport.Controller;

import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;

public class ProjectsController {

	private static final Logger logger = Logger.getLogger(ProjectsController.class);

	static DatabaseController db = new DatabaseController();

	/**************************************************************************
	 * 나의 프로젝트 가져오기
	 * 
	 * @param _id
	 * @return JSONArray [{_id, title, stage, mentor, mentee[]}]
	 *************************************************************************/
	public static JSONArray getMyProject(@PathParam("id") String userId) {

		JSONArray list = new JSONArray();
		JSONObject jo = JSONFactory.inputStreamToJson(db.getByView("_design/project", "my_projects",
				new Object[] { userId + " ", " " }, new Object[] { userId, " " }, true, true, false));
		JSONArray arr = JSONFactory.getData(jo);

		for (int i = 0; i < arr.length(); i++) {
			JSONObject doc = arr.getJSONObject(i).getJSONObject("doc");
			JSONObject project = new JSONObject();
			project.put("_id", doc.get("_id"));
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
	 * @return JSONObject { project_type, _id, _rev, mentoring_num, mentor, stage, field, section, title, mentee[] }
	 *************************************************************************/
	public JSONObject getDetailByProjectId(String projectId) {
		JSONObject projectInfo = new JSONObject();
		JSONObject project = JSONFactory.inputStreamToJson(db.getDoc(projectId));
		if (project == null) return null;
		projectInfo.put("project_type", project.getString("project_type"));
		projectInfo.put("_id", projectId);
		projectInfo.put("_rev", project.getString("_rev"));
		projectInfo.put("title", project.getString("title"));
		projectInfo.put("mentor", UserController.getUserInfo(project.getString("mentor")));
		JSONArray stage = project.getJSONArray("stage");
		if (stage.length() == 2 || stage.getInt(2) == 0)
			projectInfo.put("stage", stage.get(0) + "기 " + stage.get(1) + "단계 프로젝트");
		else
			projectInfo.put("stage", stage.get(0) + "기 " + stage.get(1) + "단계 " + stage.get(2) + "차 프로젝트");

		JSONObject mentor = JSONFactory.inputStreamToJson(db.getDoc(project.getString("mentor")));
		projectInfo.put("section", mentor.getString("section"));
		projectInfo.put("field", project.getString("field"));
		JSONArray mentee = project.getJSONArray("mentee");
		JSONArray menteeDetail = new JSONArray();
		for (int i=0; i<mentee.length(); i++) {
			menteeDetail.put(UserController.getUserInfo(mentee.getString(i)));
		}
		projectInfo.put("mentee", menteeDetail);
		ReportsController rCtrl = new ReportsController();
		projectInfo.put("mentoring_num", rCtrl.getReportByProjectId(projectId).length());
		
		return projectInfo;
	}

	/**************************************************************************
	 * 프로젝트 전체 리스트를 가져온다.
	 * 
	 * @return List<JSONObject>
	 *************************************************************************/
	public static JSONArray getProjectList() {

		JSONArray projectList = JSONFactory.getData(
				JSONFactory.inputStreamToJson(db.getByView("_design/project", "all_project", true, true, false)));

		return projectList;
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
			// String id = doc_util.putDoc(document);
			// logger.debug("inserted project id = " + id);
			// result = true;
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

		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("put : 500")
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	/************************************************************************
	 * 프로젝트 아이디로 프로젝트를 삭제한다.
	 * 
	 * @param reportId
	 * @return
	 ***********************************************************************/
	public boolean deleteProject(String projectId) {
		boolean result = false;

		try {
			JSONObject jo = JSONFactory.inputStreamToJson(db.getDoc(projectId));
			String rev = jo.getString("_rev");
			result = db.deleteDoc(projectId, rev);
			logger.debug("delete | report id = " + projectId + "\n");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * 입력된 기수문서 id에 해당하는 프로젝트 목록을 불러온다.
	 * 
	 * @param stageInfo id
	 * @return JSONArray [ {project_type, _id, _rev, mentoring_num, mentor, stage, field, section, title, mentee[]} ]
	 */
	public JSONArray projectsInStageInfo(String id) {
		JSONArray projectList = new JSONArray();
		
		DatabaseController dbCtrl = new DatabaseController();
		JSONObject info = JSONFactory.inputStreamToJson(dbCtrl.getDoc(id));
		JSONArray arr = info.getJSONArray("projects");
		
		for(int i=0; i<arr.length(); i++) {
			String idd = arr.getString(i);
			projectList.put(getDetailByProjectId(idd));
		} 
		
		return projectList;
	}

	/***
	 * 현존하는 stage 정보
	 * 
	 * @return JSONArray [{infoId, stage, stageString, projectNum}]
	 */

	public JSONArray existingStage() {
		JSONArray stage = new JSONArray();
		JSONArray list = JSONFactory.getData(
				JSONFactory.inputStreamToJson(db.getByView("_design/project", "stage_info", true, true, false)));
		for (int i = 0; i < list.length(); i++) {
			JSONObject stages = new JSONObject();
			stages.put("stage", list.getJSONObject(i).get("key"));
			stages.put("stageString", list.getJSONObject(i).getJSONObject("doc").get("stageString"));
			stages.put("infoId", list.getJSONObject(i).get("value"));
			stages.put("projectNum", list.getJSONObject(i).getJSONObject("doc").getJSONArray("projects").length());
			stage.put(stages);
		}
		return stage;
	}
	
	/***
	 * 해당 기수, 단계 정보 문서를 가져온다
	 * 
	 * @param JSONArray (기수/단계/차수 정보.  ex: [6, 1, 1] )
	 * @return JSONObject {projects[], stage, stageString, _rev, _id, type}
	 */
	
	public JSONObject getStageInfo (Object[] stages) {
		JSONObject res = JSONFactory.inputStreamToJson(db.getByView("_design/project", "stage_info", stages, true, false, false));
		if (JSONFactory.getData(res).length()==0) return null;
		return JSONFactory.getData(res).getJSONObject(0).getJSONObject("doc");
	}
	
}
