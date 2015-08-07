package org.gradle;

import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ReferenceUtil {

	CloudantClient client;
	Database db;
	
	public ReferenceUtil (String dbName) 
	{
		client = new CloudantClient("http://somareport.cloudant.com", "somareport", "somasoma");
		db = client.database(dbName, true);
	}
	
	List<JsonObject> getMyProjects (String user_id)
    {
        //멘토, 멘티
        //자신이 속한 프로젝트 조회
		return db.view("user_view/all_my_project")
				.startKey(new Object[]{user_id+" ", " "})
				.endKey(new Object[]{user_id, " "})
				.descending(true)
				.includeDocs(false).reduce(false)
				.query(JsonObject.class);
    }

    List<JsonObject> getReports (String project_id)
    {
        //프로젝트에 속한 모든 레포트 조회    
    	return db.view("project_view/all_report")
    			.startKey(new Object[]{project_id+" ", " "})
    			.endKey(new Object[]{project_id, " "})
    			.descending(true)
    			.includeDocs(true).reduce(false)
    			.query(JsonObject.class);
    }
    
    int calTotalMentoring (String project_id) 
    {
    	//프로젝트의 총 멘토링 시간 합
    	JsonObject totalTime = db.view("project_view/calculate_total_time")
    			.key(project_id).includeDocs(false).reduce(true)
    			.query(JsonObject.class).get(0);
    	return totalTime.get("value").getAsInt();
    }

    List<JsonObject> getAllMentor ()
    {
        //사무국
        //모든 멘토 리스트 불러오기
    	return db.view("admin_view/all_docs")
    			.key("mentor").includeDocs(true).reduce(false)
    			.query(JsonObject.class);
    }

    List<JsonObject> getAllMentee ()
    {
        //사무국
        //모든 멘티 리스트 불러오기  
    	return db.view("admin_view/all_docs")
    			.key("mentee").includeDocs(true).reduce(false)
    			.query(JsonObject.class);
    }

    List<JsonObject> getCurrentProjects (int[] current)
    {
        //사무국
        //현재 기수, 단계, 차수에 해당하는 모든 프로젝트 조회
    	return db.view("admin_view/current_project")
    			.key(current).includeDocs(true).reduce(false)
    			.query(JsonObject.class);
    }
    
    public JsonObject getProjectInfo (String project_id) 
    {
    	
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
		if (result.isEmpty())
			return null;
		String mentorId = result.get(0).get("mentor").getAsString();
		return getUserName(mentorId);
	}
	
	public JsonObject getReportWithNames (String report_id) {
		JsonObject report = db.find(JsonObject.class, report_id);
		String name = getMentorName(report.get("project").getAsString());
		report.addProperty("mentor", name);
		JsonArray mentee = report.get("attendee").getAsJsonArray();
		JsonArray names = new JsonArray();
		for(int i=0; i<mentee.size(); i++) {
			names.add(new JsonPrimitive(getUserName(mentee.get(i).getAsString())));
		}
		report.add("attendee", names);
		names = new JsonArray();
		mentee = report.get("absentee").getAsJsonArray();
		if (mentee.size() > 0) {
			for(int i=0; i<mentee.size(); i++) {
				JsonObject abs = new JsonObject();
				abs.addProperty("id", getUserName(mentee.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString()));
				abs.add("reason", mentee.getAsJsonArray().get(i).getAsJsonObject().get("reason"));
				names.add(abs);
			}
			report.add("absentee", names);
		}
			
		return report;
	}
}
