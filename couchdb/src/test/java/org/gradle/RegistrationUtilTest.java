package org.gradle;

import org.junit.Test;

public class RegistrationUtilTest {

	RegistrationUtil regit;
	
	@Test
	public void testRegisterMentee() {
		regit = new RegistrationUtil();
		regit.registerMentee();
	}

	@Test
	public void testRegisterMentor() {
		regit = new RegistrationUtil();
		regit.registerMentor();
	}
}
