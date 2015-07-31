package org.gradle;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;

public class ReferenceUtilTest {

	public ReferenceUtil refutil;
	
	@BeforeClass
	public void testReferenceUtil() {
		refutil = new ReferenceUtil("somarecord");
		System.out.println("Connected to Cloudant :: SOMAReport");
		System.out.println("Server Version: " + refutil.client.serverVersion());
		System.out.println("DB Info. \n" + refutil.db.info().toString());
		assertTrue(true);
	}

	@Test
	public void testGetMyProjects() {
		List<JsonObject> lists = refutil.getMyProjects("4c44d639b77c290955371694d3310194");
		for (JsonObject project : lists) {
			System.out.println(project.toString());
		}
	}

	@Test
	public void testGetReports() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalTotalMentoring() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllMentor() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllMentee() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentProjects() {
		fail("Not yet implemented");
	}

}
