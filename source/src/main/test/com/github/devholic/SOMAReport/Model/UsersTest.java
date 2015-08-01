package com.github.devholic.SOMAReport.Model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class UsersTest {

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

	@Test
	public void testGetMyProjects() {
		JsonArray resultList = user.getMyProjects();
		for (JsonElement project : resultList) {
			System.out.println(project.toString());
		}
		assertFalse(resultList.isJsonNull());
	}

}
