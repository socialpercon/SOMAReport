// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// StringFactory 테스트

package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class StringFactoryTest {

	@Test
	public void Salt_Random_NotEqual() {
		String a = StringFactory.createSalt();
		String b = StringFactory.createSalt();
		assertThat(a, not(equalTo(b)));
	}

	@Test
	public void Password_Encrypt_Pass() {
		String salt = StringFactory.createSalt();
		String encrypted = StringFactory.encryptPassword(
				TestData.correctPassword, salt);
		assertNotEquals(TestData.correctPassword, encrypted);
		String reencrypted = StringFactory.encryptPassword(
				TestData.correctPassword, salt);
		assertEquals(encrypted, reencrypted);
	}

}
