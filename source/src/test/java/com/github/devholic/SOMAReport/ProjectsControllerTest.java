package com.github.devholic.SOMAReport;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.ProjectsController;

public class ProjectsControllerTest {

	private final Logger Log = Logger.getLogger(ProjectsControllerTest.class);
	
	@Test
	public void testGetMyProject() {
		JSONArray result = ProjectsController.getMyProject("9d898f7d5bfbf361939e1fafd50470e3");
		for(int i=0; i<result.length(); i++) {
			Log.info(result.get(i));
			assertEquals(result.getJSONObject(i).getString("mentor"), "9d898f7d5bfbf361939e1fafd50470e3");
		}
	}
	
	@Test
	public void testProjectsInStage() {
		JSONArray res = ProjectsController.projectsInStage(new int[]{6,1,1});
		Log.info(res);
		Log.info(res.length());
		for(int i=0; i<res.length(); i++) {
			assertEquals(res.getJSONObject(i).getJSONObject("doc").get("stage").toString(), "[6,1,1]");
		}
	}
	
	@Test
	public void testExistingStages() {
		JSONArray res = ProjectsController.existingStages();
		Log.info(res);
		String a = "6기 1단계 1차";
	}
}
