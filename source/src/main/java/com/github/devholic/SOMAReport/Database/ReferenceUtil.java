package com.github.devholic.SOMAReport.Database;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ReferenceUtil {

	public CloudantClient client;
	public Database db;

	public ReferenceUtil(String dbName) {
		client = new CloudantClient("http://somareport.cloudant.com",
				"somareport", "somasoma");
		db = client.database(dbName, true);
	}

	public JSONArray getMyProjects(String user_id) {

		// 멘토, 멘티
		// 자신이 속한 프로젝트 조회
		List<JsonObject> lists = db.view("user_view/all_my_project")
				.startKey(new Object[] { user_id + " ", " " })
				.endKey(new Object[] { user_id, " " }).descending(true)
				.includeDocs(true).reduce(false).query(JsonObject.class);
		JSONArray projectList = new JSONArray();
		for (JsonObject project : lists) {
			JSONObject jo = new JSONObject();
			jo.put("id", project.get("_id").getAsString());
			JsonArray sja = project.get("stage").getAsJsonArray();
			jo.put("stage", sja.get(0) + "기 " + sja.get(1) + "단계 " + sja.get(2)
					+ "차 프로젝트");
			jo.put("title", project.get("title").getAsString());
			jo.put("mentor", project.get("mentor"));
			// projectInfo.add("mentee", project.get("mentee"));
			projectList.put(jo);
		}
		return projectList;
	}

	public List<JsonObject> getReports(String project_id) {
		// 프로젝트에 속한 모든 레포트 조회
		return db.view("project_view/all_report")
				.startKey(new Object[] { project_id + " ", " " })
				.endKey(new Object[] { project_id, " " }).descending(true)
				.includeDocs(true).reduce(false).query(JsonObject.class);

	}

	public int calTotalMentoring(String project_id) {
		// 프로젝트의 총 멘토링 시간 합
		JsonObject totalTime = db.view("project_view/calculate_total_time")
				.key(project_id).includeDocs(false).reduce(true)
				.query(JsonObject.class).get(0);
		return totalTime.get("value").getAsInt();
	}

	public List<JsonObject> getAllMentor() {
		// 사무국
		// 모든 멘토 리스트 불러오기
		return db.view("admin_view/all_docs").key("mentor").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getAllMentee() {
		// 사무국
		// 모든 멘티 리스트 불러오기
		return db.view("admin_view/all_docs").key("mentee").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getAllProjects() {
		return db.view("admin_viewcurrent_project").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getAllReports() {
		return db.view("project_view/all_report").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getCurrentProjects(int[] current) {
		// 사무국
		// 현재 기수, 단계, 차수에 해당하는 모든 프로젝트 조회
		return db.view("admin_view/current_project").key(current)
				.includeDocs(true).reduce(false).query(JsonObject.class);
	}
	
	public JsonObject getProjectInfo (String project_id) {
		JsonObject projectInfo = new JsonObject();
		DocumentUtil docutil = new DocumentUtil("somarecord");
		JsonObject project = docutil.getDoc(project_id);

		projectInfo.add("project_type", project.get("project_type"));
		projectInfo.add("title", project.get("title"));
		projectInfo.add("mentor", project.get("mentor"));
		
		JsonObject mentor = docutil.getDoc(project.get("mentor").getAsString());
		projectInfo.add("section", mentor.get("section"));
		projectInfo.add("stage", project.get("stage"));
		projectInfo.add("field", project.get("field"));
		projectInfo.addProperty("mentoring_num", getReports(project_id).size());
		projectInfo.add("mentee", project.get("mentee"));
		
		return projectInfo;
	}
}