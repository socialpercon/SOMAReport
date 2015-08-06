package org.gradle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

public class RegistrationUtilTest {

	RegistrationUtil regit;

	@Test
	public void testRegisterMentee() {
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream("example.xlsx");
//			regit = new RegistrationUtil(fileInput);
//			regit.registerMentee();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegisterMentor() {
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream("example.xlsx");
//			regit = new RegistrationUtil(fileInput);
//			regit.registerMentor();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegisterProject() {
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream("example.xlsx");
			regit = new RegistrationUtil(fileInput);
			regit.registerProject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
