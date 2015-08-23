// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// UserController 테스트

package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.StatisticsController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;

public class UserControllerTest {

	// Detailed test @
	// LoginAPITest.class
	// LoginViewTest.class
	// Log4j setting
	private final Logger Log = Logger.getLogger(StatisticsController.class);
	UserController userC = new UserController();
	
	@Test
	public void Check_Login_Pass() {
		assertThat(UserController.login(TestData.correctEmail,
				TestData.correctPassword), not(nullValue()));
	}

	@Test
	public void Check_MenteeList() {
		JSONArray menteeList = UserController.getMenteeList();
		// Size != 0
		assertThat(menteeList.length(), not(0));
		for (int i = 0; i < menteeList.length(); i++) {
			JSONArray mentee = JSONFactory
					.getValue(menteeList.getJSONObject(i));
			assertThat(mentee.getString(1), equalTo("mentee"));
		}
	}

	@Test
	public void Check_MentorList() {
		JSONArray mentorList = UserController.getMentorList();
		// Size != 0
		assertThat(mentorList.length(), not(0));
		for (int i = 0; i < mentorList.length(); i++) {
			JSONArray mentee = JSONFactory
					.getValue(mentorList.getJSONObject(i));
			assertThat(mentee.getString(1), equalTo("mentor"));
		}
	}

	@Test
	public void testTotalMentoringInfoByProject() {
		StatisticsController st = new StatisticsController();
		JSONArray mentorTotal = st.totalMentoringInfoByStage("mentor", "9d898f7d5bfbf361939e1fafd518ea39");
		JSONArray menteeTotal = st.totalMentoringInfoByStage("mentee", "9d898f7d5bfbf361939e1fafd518ea39");
		Log.info(mentorTotal);
	//	assertEquals(ProjectsController.getProjectList().length(), mentorTotal.length());
		for (int i=0; i<mentorTotal.length(); i++) {
			assertThat(mentorTotal.getJSONObject(i).get("mentoringNum"), not(nullValue()));
			assertThat(mentorTotal.getJSONObject(i).get("mentoringSum"), not(nullValue()));
		}
		for (int i=0; i<menteeTotal.length(); i++) {
			assertThat(menteeTotal.getJSONObject(i).get("mentoringNum"), not(nullValue()));
			assertThat(menteeTotal.getJSONObject(i).get("mentoringSum"), not(nullValue()));
		}	
	}
	
	@Test
	public void testGetUserByEmail() {
		assertEquals(UserController.getUserByEmail("mentor0@gmail.com").getString("email"), "mentor0@gmail.com");
		assertThat(UserController.getUserByEmail("dfdf"), nullValue());
	}
	
	@Test
	public void testGetUsersInYear() {
		UserController user = new UserController();
		assertTrue(user.getUsersInYear(2015).length() > 0);
	}
	
	@Test
	public void testTotalMentoringInfoByMonth() {
		StatisticsController stC = new StatisticsController();
		Log.info(stC.totalMentoringInfoByMonth(2015, 8, "mentor"));
	}
	
	@Test
	public void testModifyPassword() {
		UserController userC = new UserController();
		DatabaseController dbC = new DatabaseController();
		String userId = "9d898f7d5bfbf361939e1fafd5104eb3";
		Log.info(JSONFactory.inputStreamToJson(dbC.getDoc(userId)));
		assertTrue(userC.modifyPassword(userId, "password", "ohoh", "ohoh"));
		Log.info(JSONFactory.inputStreamToJson(dbC.getDoc(userId)));		
	}
	
	@Test
	public void testNameDuplication() {
		UserController userC = new UserController();
		assertTrue(userC.nameDuplicationCheck("김멘토"));
		assertTrue(!userC.nameDuplicationCheck("뿅뿅이"));
	}
	
	@Test
	public void testUserUpdate() {
		JSONArray list =UserController.getAllUsers();
		DatabaseController dbC = new DatabaseController();
		for (int i=0; i<list.length(); i++) {
			JSONObject doc = list.getJSONObject(i);
			if (doc.has("profileFile")) {
				doc.remove("profileFile");
				Map<String, Object> m = dbC.updateDoc(doc);
				Log.info(m.toString());
			}
		}
	}
}
