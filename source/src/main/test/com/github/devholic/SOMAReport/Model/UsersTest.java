package com.github.devholic.SOMAReport.Model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class UsersTest {

	private final Logger logger = Logger.getLogger(UsersTest .class);
	
	public static Users user;
	
	@BeforeClass
	public static void testUsersString() {
		user = new Users("ppyong0@gmail.com");
		assertTrue(true);
	}

	@Test
	public void testIsBelongToYear() {
		assertTrue(user.isBelongToYear(2015));
		assertFalse(user.isBelongToYear(2000));
	}

	/*
	@Test
	public void testGetMyProjects() {
		JSONArray resultList = user.getMyProjects();
		for (JsonElement project : resultList) {
			logger.debug(project.toString());
		}
		assertFalse(resultList.isJsonNull());
	}
	*/

}
