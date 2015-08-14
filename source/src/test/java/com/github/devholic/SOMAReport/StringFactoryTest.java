// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// StringFactory 테스트

package com.github.devholic.SOMAReport;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

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
	public void testEncryptPassword() {
		String password = "password";
		String salt = StringFactory.createSalt();
		String encrypted = StringFactory.encryptPassword(password, salt);
		String reencrypted = StringFactory.encryptPassword(password, salt);
		assertEquals(encrypted, reencrypted);
	}

}
