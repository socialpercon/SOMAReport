package org.gradle;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;

public class ReferenceUtilTest {

	public static ReferenceUtil refutil;
	
	@BeforeClass
	public static void testReferenceUtil() {
		refutil = new ReferenceUtil("somarecord");
		System.out.println("Connected to Cloudant :: SOMAReport");
		System.out.println("Server Version: " + refutil.client.serverVersion());
		System.out.println("DB Info. \n" + refutil.db.info().toString());
		assertTrue(true);
	}
//
//	@Test
//	public void testGetMyProjects() {
//		List<JsonObject> lists = refutil.getMyProjects("4c44d639b77c290955371694d3310194");
//		System.out.println("\nMy Projects: 4c44d639b77c290955371694d3310194");
//		System.out.println("has "+lists.size() +" projects");
//		for (JsonObject project : lists) {
//			System.out.println(project.get("key").toString()
//					/*+"\n"+ project.get("title").toString()*/);
//		}
//		assertTrue(true);
//	}
//
//	@Test
//	public void testGetReports() {
//		List<JsonObject> lists = refutil.getReports("4c44d639b77c290955371694d33e4fe9");
//		System.out.println("\nReports");
//		for (JsonObject report : lists) {
//			System.out.println(report.get("report_info").toString());
//		}
//		assertTrue(true);
//	}
//
//	@Test
//	public void testCalTotalMentoring() {
//		int total = refutil.calTotalMentoring("4c44d639b77c290955371694d33e4fe9");
//		assertEquals(25, total);
//	}
//
//	@Test
//	public void testGetAllMentor() {
//		List<JsonObject> lists = refutil.getAllMentor();
//		System.out.println("\nAll mentors");
//		for (JsonObject mentor : lists) {
//			System.out.println(mentor.get("name").getAsString()
//					+"\n"+mentor.toString());
//		}
//	}
//
//	@Test
//	public void testGetAllMentee() {
//		List<JsonObject> lists = refutil.getAllMentee();
//		System.out.println("\nAll mentees");
//		for (JsonObject mentee : lists) {
//			System.out.println(mentee.get("name").getAsString()
//					+"\n"+mentee.toString());
//		}		
//	}
//
//	@Test
//	public void testGetCurrentProjects() {
//		int[] current = {6, 1, 1};
//		List<JsonObject> lists = refutil.getCurrentProjects(current);
//		System.out.println("\n6, 1, 1 Projects");
//		for (JsonObject project: lists) {
//			System.out.println(project.get("title")
//					+"\n"+project.get("mentor"));
//		}
//	}
//	
//	@Test
//	public void testGetProjectInfo() {
//		String project_id = "4c44d639b77c290955371694d33e4fe9";
//		JsonObject project_info = refutil.getProjectInfo(project_id);
//		System.out.println(project_info.toString());
//	}

	@Test
	public void testGetReportWithNames () {
		String report_id = "e5508fae8ecc4729b2605496a278a884";
		System.out.println(
				refutil.getReportWithNames(report_id).toString());
	}
}
