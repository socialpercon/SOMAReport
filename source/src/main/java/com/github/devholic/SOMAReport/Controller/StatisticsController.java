package com.github.devholic.SOMAReport.Controller;

import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;

@Path("/statistics")
public class StatisticsController {

	//Log4j setting
	private final Logger Log = Logger.getLogger(StatisticsController .class);
	DatabaseController db = new DatabaseController();

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response getUserList(){
		
		JSONObject jo1 = new JSONObject();
		
		try{
//			logger.info("info level");
//			logger.warn("warn level");
//			logger.debug("debug level");
//			logger.fatal("fatal level");
//			
//			jo1.put("name", "name");
//			jo1.put("age", "age");
//			jo1.put("sex", "sex");
//			jo1.put("email", "email");

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity(jo1)
				.build();
	}
	
	@PUT
	public Response updateStatistics(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("put : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity("put : 500").build();
	}
	
	@DELETE
	public Response deleteStatistics(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("delete : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity("delete : 500").build();
	}
	
	
	/**
	 * 모든 멘토/멘티의 프로젝트별 멘토링 시간 총합 및 시행 횟수를 리턴
	 * 
	 * @param int (mentor: ROLE_MENTOR=0, mentee: ROLE_MENTEE=1)
	 * @return JSONArray [ {userId, name, projectId, stage, mentoringSum, mentoringNum} ] )
	 **/
	public JSONArray totalMentoringInfo(int role) {
		JSONArray sumList = new JSONArray();

		if (role == UserController.ROLE_MENTOR) {
			JSONArray mentor = UserController.getMentorList();
			for (int i=0; i<mentor.length(); i++) {
				
				JSONArray projects = ProjectsController.getMyProject(mentor.getJSONObject(i).getString("id"));
				for (int j=0; j<projects.length(); j++) {
					JSONObject mentorDoc = new JSONObject();
					JSONArray mentorInfo = JSONFactory.getValue(mentor.getJSONObject(i));
					mentorDoc.put("userId", mentor.getJSONObject(i).get("id"));
					mentorDoc.put("name", mentorInfo.get(3));
					String projectId = projects.getJSONObject(j).getString("_id");
					mentorDoc.put("projectId", projectId);
					mentorDoc.put("stage", projects.getJSONObject(j).get("stage"));
					InputStream is = db.getByView("_design/statistics", "total_time_by_project", projectId, false, true, true);
					JSONArray a = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
					
					if (a.length() == 0) {
						mentorDoc.put("mentoringSum", 0);
						mentorDoc.put("mentoringNum", 0);
						sumList.put(mentorDoc);
					}
					else {
						int sum = a.getJSONObject(0).getInt("value");
						mentorDoc.put("mentoringSum", sum);
						is = db.getByView("_design/statistics", "total_time_by_project", projectId, false, false, false);
						int num = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is))).length();
						mentorDoc.put("mentoringNum", num);
						Log.info(mentorDoc);
						sumList.put(mentorDoc);
					}
				}
			}
		} 
		
		else if (role == UserController.ROLE_MENTEE) {
			JSONArray mentee = UserController.getMenteeList();
			for (int i=0; i<mentee.length(); i++) {
				JSONObject menteeDoc = new JSONObject();
				JSONArray menteeInfo = JSONFactory.getValue(mentee.getJSONObject(i));
				String menteeId = mentee.getJSONObject(i).getString("id");
				menteeDoc.put("userId", menteeId);
				menteeDoc.put("name", menteeInfo.get(3));
				
				JSONArray projects = ProjectsController.getMyProject(mentee.getJSONObject(i).getString("id"));
				for (int j=0; j<projects.length(); j++) {
					String projectId = projects.getJSONObject(j).getString("_id");
					menteeDoc.put("projectId", projectId);
					menteeDoc.put("stage", projects.getJSONObject(j).get("stage"));
					
					InputStream is = db.getByView("_design/statistics", "total_time_by_mentee", new Object[] {menteeId, projectId}, false, false, true);
					JSONArray a = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
					
					if (a.length() == 0) {
						menteeDoc.put("mentoringSum", 0);
						menteeDoc.put("mentoringNum", 0);
						sumList.put(menteeDoc);
					}
					else {
						int sum = a.getJSONObject(0).getInt("value");
						menteeDoc.put("mentoringSum", sum);
						is = db.getByView("_design/statistics", "total_time_by_mentee", new Object[] {menteeId, projectId}, false, false, false);
						int num = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is))).length();
						menteeDoc.put("mentoringNum", num);
						Log.info(menteeDoc);
						sumList.put(menteeDoc);
					}
				}
			}
		} 
		
		else {
			sumList = null;
			Log.error("sumOfTotalMentoring: wrong role input");
		}

		return sumList;
	}
}
