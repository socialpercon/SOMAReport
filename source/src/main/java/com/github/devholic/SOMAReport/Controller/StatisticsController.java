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

	// Log4j setting
	private final Logger Log = Logger.getLogger(StatisticsController.class);
	DatabaseController db = new DatabaseController();

	/**
	 * 모든 멘토/멘티의 "프로젝트별" 멘토링 시간 총합 및 시행 횟수를 리턴
	 * 
	 * @param int (mentor: ROLE_MENTOR=0, mentee: ROLE_MENTEE=1)
	 * @return JSONArray [ {userId, name, projectId, stage, mentoringSum,
	 *         mentoringNum} ] )
	 **/
	public JSONArray totalMentoringInfoByStage(String role, String stageInfoId) {
		JSONArray sumList = new JSONArray();
		JSONObject stageInfo = JSONFactory.inputStreamToJson(db
				.getDoc(stageInfoId));
		String stageString = stageInfo.getString("stageString");

		if (role.equals("mentor")) {
			JSONArray mentor = UserController.getMentorList();
			for (int i = 0; i < mentor.length(); i++) {
				JSONArray projects = ProjectsController.getMyProject(mentor
						.getJSONObject(i).getString("id"));
				for (int j = 0; j < projects.length(); j++) {
					if (projects.getJSONObject(j).getString("stage")
							.equals(stageString)) {
						JSONObject mentorDoc = new JSONObject();
						JSONArray mentorInfo = JSONFactory.getValue(mentor
								.getJSONObject(i));
						mentorDoc.put("userId",
								mentor.getJSONObject(i).get("id"));
						mentorDoc.put("userName", mentorInfo.get(3));
						String projectId = projects.getJSONObject(j).getString(
								"_id");
						mentorDoc.put("projectId", projectId);
						mentorDoc.put("stage",
								projects.getJSONObject(j).get("stage"));
						InputStream is = db.getByView("_design/statistics",
								"total_time_by_project", projectId, false,
								true, true);
						JSONArray a = JSONFactory.getData(new JSONObject(
								StringFactory.inputStreamToString(is)));

						if (a.length() == 0) {
							mentorDoc.put("mentoringSum", 0);
							mentorDoc.put("mentoringNum", 0);
							sumList.put(mentorDoc);
						} else {
							int sum = a.getJSONObject(0).getInt("value");
							mentorDoc.put("mentoringSum", sum);
							is = db.getByView("_design/statistics",
									"total_time_by_project", projectId, false,
									false, false);
							int num = JSONFactory.getData(
									new JSONObject(StringFactory
											.inputStreamToString(is))).length();
							mentorDoc.put("mentoringNum", num);
							sumList.put(mentorDoc);
						}
					}
				}
			}
		}

		else if (role.equals("mentee")) {
			JSONArray mentee = UserController.getMenteeList();
			for (int i = 0; i < mentee.length(); i++) {
				JSONObject menteeDoc = new JSONObject();
				JSONArray menteeInfo = JSONFactory.getValue(mentee
						.getJSONObject(i));
				String menteeId = mentee.getJSONObject(i).getString("id");
				menteeDoc.put("userId", menteeId);
				menteeDoc.put("userName", menteeInfo.get(3));

				JSONArray projects = ProjectsController.getMyProject(mentee
						.getJSONObject(i).getString("id"));
				for (int j = 0; j < projects.length(); j++) {
					if (projects.getJSONObject(j).getString("stage")
							.equals(stageString)) {
						String projectId = projects.getJSONObject(j).getString(
								"_id");
						menteeDoc.put("projectId", projectId);
						menteeDoc.put("stage",
								projects.getJSONObject(j).get("stage"));

						InputStream is = db.getByView("_design/statistics",
								"total_time_by_mentee", new Object[] {
										menteeId, projectId }, false, false,
								true);
						JSONArray a = JSONFactory.getData(new JSONObject(
								StringFactory.inputStreamToString(is)));

						if (a.length() == 0) {
							menteeDoc.put("mentoringSum", 0);
							menteeDoc.put("mentoringNum", 0);
							sumList.put(menteeDoc);
						} else {
							int sum = a.getJSONObject(0).getInt("value");
							menteeDoc.put("mentoringSum", sum);
							is = db.getByView("_design/statistics",
									"total_time_by_mentee", new Object[] {
											menteeId, projectId }, false,
									false, false);
							int num = JSONFactory.getData(
									new JSONObject(StringFactory
											.inputStreamToString(is))).length();
							menteeDoc.put("mentoringNum", num);
							sumList.put(menteeDoc);
						}
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

	/****
	 * 모든 멘토/멘티의 "월별" 멘토링 시간 총합 및 시행 횟수를 리턴
	 * 
	 * @param year
	 *            , month, id
	 * @return [ {userName, userId, mentoringSum, mentoringNum} ]
	 */
	public JSONArray totalMentoringInfoByMonth(int year, int month, String role) {

		UserController userC = new UserController();

		// setting search option
		String dateStartKey, dateEndKey;
		if (month >= 10) {
			dateStartKey = Integer.toString(year) + Integer.toString(month)
					+ "00";
			dateEndKey = Integer.toString(year) + Integer.toString(month)
					+ "31";
		} else {
			dateStartKey = Integer.toString(year) + "0"
					+ Integer.toString(month) + "00";
			dateEndKey = Integer.toString(year) + "0" + Integer.toString(month)
					+ "31";
		}

		// 해당 year에 속하는 userId List 가져오기
		JSONArray users = userC.getUsersInYear(year);
		JSONArray infos = new JSONArray();

		for (int i = 0; i < users.length(); i++) {
			String userId = users.getString(i);

			if (UserController.getRoleById(userId).equals(role)) {
				// userId로 소속 프로젝트 리스트 가져오기
				JSONArray projects = ProjectsController.getMyProject(userId);

				// for문: 지정된 기간(월)에 해당되는 프로젝트의 총합 시간/횟수 검색
				if (role.equals("mentor")) {
					for (int j = 0; j < projects.length(); j++) {
						String projectId = projects.getJSONObject(j).getString(
								"_id");

						// keys:: 멘토: [project._id, date]
						JSONObject jo = new JSONObject();
						jo = JSONFactory.inputStreamToJson(db.getByView(
								"_design/statistics",
								"total_time_by_project_date", new Object[] {
										projectId, dateStartKey },
								new Object[] { projectId, dateEndKey }, false,
								false, true, 1));

						JSONArray ja = JSONFactory.getData(jo);
						if (ja.length() == 1) {
							int mentoringSum = ja.getJSONObject(0).getInt(
									"value");
							jo = JSONFactory.inputStreamToJson(db.getByView(
									"_design/statistics",
									"total_time_by_project_date", new Object[] {
											projectId, dateStartKey },
									new Object[] { projectId, dateEndKey },
									false, false, true, 2));
							int mentoringNum = JSONFactory.getData(jo).length();
							UserController user = new UserController();
							JSONObject info = new JSONObject();
							info.put("userId", userId);
							info.put("userName", user.getUserName(userId));
							info.put("mentoringSum", mentoringSum);
							info.put("mentoringNum", mentoringNum);
							infos.put(info);
							break;
						}
					}
				} else if (role.equals("mentee")) {
					for (int j = 0; j < projects.length(); j++) {
						String projectId = projects.getJSONObject(j).getString(
								"_id");

						// keys:: 멘티: [user._id, project._id, date]
						JSONObject jo = new JSONObject();
						jo = JSONFactory.inputStreamToJson(db.getByView(
								"_design/statistics",
								"total_time_by_mentee_date", new Object[] {
										userId, projectId, dateStartKey },
								new Object[] { userId, projectId, dateEndKey },
								false, false, true, 2));

						JSONArray ja = JSONFactory.getData(jo);
						if (ja.length() == 1) {
							int mentoringSum = ja.getJSONObject(0).getInt(
									"value");
							jo = JSONFactory.inputStreamToJson(db
									.getByView("_design/statistics",
											"total_time_by_mentee_date",
											new Object[] { userId, projectId,
													dateStartKey },
											new Object[] { userId, projectId,
													dateEndKey }, false, false,
											true, 3));
							int mentoringNum = JSONFactory.getData(jo).length();
							UserController user = new UserController();
							JSONObject info = new JSONObject();
							info.put("userId", userId);
							info.put("userName", user.getUserName(userId));
							info.put("mentoringSum", mentoringSum);
							info.put("mentoringNum", mentoringNum);
							infos.put(info);
							break;
						}
					}
				} else {
					Log.error("sumOfTotalByMonth: wrong role input");
				}

			}
		}
		System.out.println(infos);
		return infos;
	}
}
