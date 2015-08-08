package com.github.devholic.SOMAReport.Database;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ReferenceUtil {

	// Log4j setting
	private final Logger logger = Logger.getLogger(ProjectsController.class);

	public CloudantClient client;
	public Database db;

	public ReferenceUtil(String dbname) {
		try {
			// get config value
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);

			logger.debug("xml database_name :"
					+ prop.getProperty("database_name") + "\n");
			logger.debug("xml test :" + prop.getProperty("test") + "\n");

			client = new CloudantClient(prop.getProperty("cloudant_url"),
					prop.getProperty("cloudant_id"),
					prop.getProperty("cloudant_pwd"));
			if (dbname == null || dbname.equals("")) {
				db = client.database(prop.getProperty("database_name"), true);
			} else {
				db = client.database(dbname, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray getMyProjects(String user_id) {
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
			if (sja.size() == 2) {
				jo.put("stage", sja.get(0) + "기 " + sja.get(1) + "단계 프로젝트");
			} else {
				jo.put("stage",
						sja.get(0) + "기 " + sja.get(1) + "단계 " + sja.get(2)
								+ "차 프로젝트");
			}
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
		// 모든 멘토 리스트 조회
		return db.view("admin_view/all_docs").key("mentor").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getAllMentee() {
		// 모든 멘티 리스트 조회
		return db.view("admin_view/all_docs").key("mentee").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getAllProjects() {
		// 모든 프로젝트 리스트 조회
		return db.view("admin_viewcurrent_project").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getAllReports() {
		// 모든 레포트 리스트 조회
		return db.view("project_view/all_report").includeDocs(true)
				.reduce(false).query(JsonObject.class);
	}

	public List<JsonObject> getCurrentProjects(int[] current) {
		// 현재 기수, 단계, 차수에 해당하는 모든 프로젝트 조회
		return db.view("admin_view/current_project").key(current)
				.includeDocs(true).reduce(false).query(JsonObject.class);
	}

	public JsonObject getProjectInfo(String project_id) {
		// 프로젝트의 기본 정보를 가져온다
		// project_type, title, mentor, section(by mentor), stage, field,
		// mentoring_num(횟수), mentee(리스트)
		JsonObject projectInfo = new JsonObject();
		DocumentUtil docutil = new DocumentUtil("");
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

	public String getUserName(String id) {
		// user의 id를 받아 이름을 조회
		List<JsonObject> result = db.view("get_doc/user_name_by_id").key(id)
				.includeDocs(false).reduce(false).query(JsonObject.class);
		if (result.isEmpty())
			return null;
		return result.get(0).get("value").getAsString();
	}

	public String getMentorName(String project_id) {
		// project의 id를 통해 소속 멘토의 이름을 조회
		List<JsonObject> result = db.view("admin_view/all_docs_by_id")
				.key(project_id).includeDocs(true).reduce(false)
				.query(JsonObject.class);
	logger.debug(result.toString());
		if (result.isEmpty())
			return null;
		String mentorId = result.get(0).get("mentor").getAsString();
		return getUserName(mentorId);
	}

	public String[] getMenteeName(String project_id) {
		// project의 id를 통해 소속 멘티들의 이름을 조회
		List<JsonObject> result = db.view("admin_view/all_docs_by_id")
				.key(project_id).includeDocs(true).reduce(false)
				.query(JsonObject.class);
		if (result.isEmpty())
			return null;
		JsonArray menteeList = result.get(0).get("mentee").getAsJsonArray();
		String[] mentee = new String[menteeList.size()];
		for (int i = 0; i < menteeList.size(); i++)
			mentee[i] = menteeList.get(i).getAsString();
		return mentee;
	}

	public String[] getAllMemberName(String project_id) {
		// project의 id를 통해 소속 멘토, 멘티들의 이름을 한 번에 조회
		List<JsonObject> result = db.view("admin_view/all_docs_by_id")
				.key(project_id).includeDocs(false).reduce(false)
				.query(JsonObject.class);
		if (result.isEmpty())
			return null;
		String mentorId = result.get(0).get("mentor").getAsString();
		JsonArray menteeList = result.get(0).get("mentee").getAsJsonArray();
		String[] members = new String[menteeList.size() + 1];
		members[0] = getUserName(mentorId);
		for (int i = 1; i < menteeList.size(); i++)
			members[i] = menteeList.get(i).getAsString();
		return members;
	}
	
	public JSONObject getReportWithNames (String report_id) {
		JsonObject report = db.find(JsonObject.class, report_id);
		logger.info("report: "+report);
		String name = getMentorName(report.get("project").getAsString());
		report.addProperty("mentor", name);
		JsonArray mentee = report.get("attendee").getAsJsonArray();
		JsonArray attendee = new JsonArray();
		for(int i=0; i<mentee.size(); i++) {
			JsonObject at = new JsonObject();
			at.addProperty("id", mentee.get(i).getAsString());
			at.addProperty("name", mentee.get(i).getAsString());
			attendee.add(at);
		}
		report.add("attendee", attendee);
		JsonArray absentee = new JsonArray();
		if (report.has("absentee")) {
			mentee = report.get("absentee").getAsJsonArray();
			for(int i=0; i<mentee.size(); i++) {
				JsonObject at = new JsonObject();
				at.addProperty("id", mentee.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString());
				at.addProperty("name", getUserName(mentee.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString()));
				at.addProperty("reason", mentee.getAsJsonArray().get(i).getAsJsonObject().get("reason").getAsString());
				absentee.add(at);
			}
			report.add("absentee", absentee);
		}
		return new JSONObject(report.toString());
	}
}
