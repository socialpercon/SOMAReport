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
		JSONArray result = ProjectsController
				.getMyProject("9d898f7d5bfbf361939e1fafd50470e3");
		for (int i = 0; i < result.length(); i++) {
			Log.info(result.get(i));
			assertEquals(result.getJSONObject(i).getString("mentor"),
					"9d898f7d5bfbf361939e1fafd50470e3");
		}
	}

	@Test
	public void testProjectsInStage() {
		ProjectsController p = new ProjectsController();
		JSONArray res = p.projectsInStage("9d898f7d5bfbf361939e1fafd50e120b");
		
	}

	@Test
	public void testExistingStages() {
		ProjectsController project = new ProjectsController();
		JSONArray ja = project.existingStage();
		Log.info(ja.toString());
	}
}
