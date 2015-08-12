// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// UserController 테스트

package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.json.JSONArray;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;

public class UserControllerTest {

	// Detailed test @
	// LoginAPITest.class
	// LoginViewTest.class
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

}
