package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class UserController {

	public final static int ROLE_MENTOR = 0;
	public final static int ROLE_MENTEE = 1;

	private final static Logger Log = Logger.getLogger(UserController.class);

	static DatabaseController db = new DatabaseController();

	public static String login(String email, String password) {
		// _design/user/auth
		// Key : email
		// Value : salt / value
		InputStream is = db.getByView("_design/user", "auth", email, false, false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		if (result.length() != 0) {
			JSONArray parsed = JSONFactory.getValue(result.getJSONObject(0));
			if (StringFactory.encryptPassword(password, parsed.getString(0)).equals(parsed.getString(1))) {
				return result.getJSONObject(0).getString("id");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static JSONArray getMenteeList() {
		// _design/user/role
		// Key : role
		// Value : _id / role / email / name / belong / years / section
		InputStream is = db.getByView("_design/user", "role", "mentee", false, false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		return result;
	}

	public static JSONArray getMentorList() {
		// _design/user/role
		// Key : role
		// Value : _id / role / email / name / belong / years / section
		InputStream is = db.getByView("_design/user", "role", "mentor", false, false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		System.out.println(result);
		return result;
	}

	public static boolean isAlreadyRegistered(String email) {
		// 이메일을 통해 이미 등록된 사용자인지를 확인한다
		InputStream is = db.getByView("_design/user", "search_by_email", email, false, false, false);
		JSONArray results = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		if (results.length() == 0) 
			return false;
		else
			return true;
	}

	/**
	 * 모든 멘토/멘티의 프로젝트별 멘토링 시간 총합 및 시행 횟수를 리턴
	 * 프로젝트 stage별 등등 따로 만들예정이예양
	 * 미완성
	 * 
	 * @param int (mentor: ROLE_MENTOR=0, mentee: ROLE_MENTEE=1)
	 * @return JSONArray [ {id, name, projectId, stage, mentoringSum, mentoringNum} ] )
	 */
	public static JSONArray totalMentoringInfo(int role) {
		JSONArray sumList = new JSONArray();

		if (role == ROLE_MENTOR) {
			JSONArray mentor = getMentorList();
			for (int i=0; i<mentor.length(); i++) {
				JSONObject mentorDoc = new JSONObject();
				JSONArray mentorInfo = JSONFactory.getValue(mentor.getJSONObject(0));
				mentorDoc.put("id", mentor.getJSONObject(i).get("id"));
				mentorDoc.put("name", mentorInfo.get(3));
				
				JSONArray projects = ProjectsController.getMyProject(mentor.getJSONObject(i).getString("id"));
				for (int j=0; j<projects.length(); j++) {
					String projectId = projects.getJSONObject(j).getString("_id");
					mentorDoc.put("projectId", projectId);
					mentorDoc.put("stage", projects.getJSONObject(j).get("stage"));
					InputStream is = db.getByView("_design/statistics", "total_time_by_project", projectId, false, false, true);
					JSONArray a = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
					
					if (a.length()!=0) {
						System.out.println(a);
						int sum = a.getJSONObject(0).getInt("value");
						mentorDoc.put("mentoringSum", sum);
						is = db.getByView("_design/statistics", "total_time_by_project", projectId, false, false, false);
						int num = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is))).length() + 1;
						mentorDoc.put("mentoringNum", num);
						System.out.println(mentorDoc);
						sumList.put(mentorDoc);
					}
				}
			}
			Log.info(sumList);
		} 
		
		else if (role == ROLE_MENTEE) {

		} 
		
		else {
			sumList = null;
			Log.error("sumOfTotalMentoring: wrong role input");
		}

		return sumList;
	}
}
