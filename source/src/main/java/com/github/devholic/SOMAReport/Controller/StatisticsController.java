package com.github.devholic.SOMAReport.Controller;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;

@Path("/statistics")
public class StatisticsController {

	// Log4j setting
	private final Logger Log = Logger.getLogger(StatisticsController.class);
	DatabaseController db = new DatabaseController();

	/**
	 * 모든 멘토/멘티의 "프로젝트별" 멘토링 시간 총합 및 시행 횟수를 리턴
	 * 
	 * @param int
	 *            (mentor: ROLE_MENTOR=0, mentee: ROLE_MENTEE=1)
	 * @return JSONArray [ {userId, name, projectId, stage, mentoringSum,
	 *         mentoringNum} ] )
	 **/
	public JSONArray totalMentoringInfoByStage(String role, String stageInfoId) {
		JSONArray sumList = new JSONArray();
		JSONObject stageInfo = JSONFactory.inputStreamToJson(db.getDoc(stageInfoId));
		String stageString = stageInfo.getString("stageString");
		JSONArray stages = stageInfo.getJSONArray("stage");
		Object[] stage = new Object[stages.length()];
		for (int i=0; i<stages.length(); i++) {
			stage[i] = stages.get(i);
		}

		JSONArray userList = new JSONArray();
		if (role.equals("mentor")) {
			userList = UserController.getMentorList();
		}
		else if (role.equals("mentee")) {
			userList = UserController.getMenteeList();
		}
	
		else {
			Log.error("sumOfTotalMentoring: wrong role input");
			return null;
		}
		Log.info(userList.toString());
		for (int i=0; i<userList.length(); i++) {
			String userId = userList.getJSONObject(i).getString("id");
			String projectId = ProjectsController.getMyProject(userId, stage);
			if (projectId.equals("no")) continue;
			JSONObject doc = new JSONObject();
			doc.put("userId", userList.get(i));
			doc.put("userName", UserController.getUserName(userId));
			doc.put("stage", stages);
			JSONArray res = JSONFactory.getData(JSONFactory.inputStreamToJson(db.getByView("_design/statistics", 
					"total_time", new Object[] { userId, projectId }, false, false, true)));
			if (res.length() == 0) {
				doc.put("mentoringSum", 0);
				doc.put("mentoringNum", 0);
			} else {
				doc.put("mentoringSum", res.getJSONObject(0).get("value"));
				res = JSONFactory.getData(JSONFactory.inputStreamToJson(db.getByView("_design/statistics", 
						"total_time", new Object[] { userId, projectId }, false, false, false)));
				doc.put("mentoringNum", res.length());
			}
			sumList.put(doc);
		}

		

		return sumList;
	}

	/****
	 * 모든 멘토/멘티의 "월별" 멘토링 시간 총합 및 시행 횟수를 리턴
	 * 
	 * @param year, month, id
	 * @return [ {userName, userId, mentoringSum, mentoringNum} ]
	 */
	public JSONArray totalMentoringInfoByMonth(int year, int month, String role) {

		UserController userC = new UserController();

		// setting search option
		String dateStartKey, dateEndKey;
		if (month >= 10) {
			dateStartKey = Integer.toString(year) + Integer.toString(month) + "00";
			dateEndKey = Integer.toString(year) + Integer.toString(month) + "31";
		} else {
			dateStartKey = Integer.toString(year) + "0" + Integer.toString(month) + "00";
			dateEndKey = Integer.toString(year) + "0" + Integer.toString(month) + "31";
		}

		// 해당 year에 속하는 userId List 가져오기
		JSONArray users = userC.getUsersInYear(year);
		JSONArray infos = new JSONArray();

		for (int i = 0; i < users.length(); i++) {
			String userId = users.getString(i);

			int mentoringSum = 0;
			int mentoringNum = 0;

			if (UserController.getRoleById(userId).equals(role)) {
				// userId로 소속 프로젝트 리스트 가져오기
				JSONArray projects = ProjectsController.getMyProject(userId);

				// for문: 지정된 기간(월)에 해당되는 프로젝트의 총합 시간/횟수 검색
				if (role.equals("mentor")) {
					for (int j = 0; j < projects.length(); j++) {
						String projectId = projects.getJSONObject(j).getString("_id");

						// keys:: 멘토: [project._id, date]
						JSONObject jo = new JSONObject();
						jo = JSONFactory.inputStreamToJson(db.getByView("_design/statistics",
								"total_time_by_project_date", new Object[] { projectId, dateStartKey },
								new Object[] { projectId, dateEndKey }, false, false, true, 1));

						JSONArray ja = JSONFactory.getData(jo);
						if (ja.length() == 1) {
							mentoringSum += ja.getJSONObject(0).getInt("value");
							jo = JSONFactory.inputStreamToJson(db.getByView("_design/statistics",
									"total_time_by_project_date", new Object[] { projectId, dateStartKey },
									new Object[] { projectId, dateEndKey }, false, false, true, 2));
							mentoringNum += JSONFactory.getData(jo).length();
						} 
					}

					JSONObject info = new JSONObject();
					info.put("userId", userId);
					info.put("userName", UserController.getUserName(userId));
					info.put("mentoringSum", mentoringSum);
					info.put("mentoringNum", mentoringNum);
					infos.put(info);
					
				} else if (role.equals("mentee")) {
					for (int j = 0; j < projects.length(); j++) {
						String projectId = projects.getJSONObject(j).getString("_id");

						// keys:: 멘티: [user._id, project._id, date]
						JSONObject jo = new JSONObject();
						jo = JSONFactory.inputStreamToJson(db.getByView("_design/statistics",
								"total_time_by_mentee_date", new Object[] { userId, projectId, dateStartKey },
								new Object[] { userId, projectId, dateEndKey }, false, false, true, 2));
						JSONArray ja = JSONFactory.getData(jo);
						if (ja.length() == 1) {
							mentoringSum += ja.getJSONObject(0).getInt("value");
							jo = JSONFactory.inputStreamToJson(db.getByView("_design/statistics",
									"total_time_by_mentee_date", new Object[] { userId, projectId, dateStartKey },
									new Object[] { userId, projectId, dateEndKey }, false, false, true, 3));
							mentoringNum += JSONFactory.getData(jo).length();
						}

					}
					JSONObject info = new JSONObject();
					info.put("userId", userId);
					info.put("userName", UserController.getUserName(userId));
					info.put("mentoringSum", mentoringSum);
					info.put("mentoringNum", mentoringNum);
					infos.put(info);

				} else {
					Log.error("sumOfTotalByMonth: wrong role input");
				}

			}
		}
		return infos;
	}
}
