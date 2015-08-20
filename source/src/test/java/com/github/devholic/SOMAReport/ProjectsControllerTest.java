package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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
	public void testGetProjectList() {
		JSONArray res = ProjectsController.getProjectList();
		for (int i=0; i<res.length(); i++) {
			assertThat(res.getJSONObject(i).get("doc"), not(nullValue()));
		}
	}
	
	@Test
	public void testProjectsInStageInfo() {
		ProjectsController p = new ProjectsController();
		JSONArray res = p.projectsInStageInfo("9d898f7d5bfbf361939e1fafd5193109");		
		for (int i=0; i<res.length(); i++) {
			assertThat(res.get(i), not(nullValue()));
			
		}
	}

	@Test
	public void testExistingStages() {
		ProjectsController project = new ProjectsController();
		JSONArray ja = project.existingStage();
		Log.info(ja.toString());
	}

	@Test
	public void testGetStageInfo() {
		ProjectsController project = new ProjectsController();
		JSONObject jo = project.getStageInfo(new Object[]{6, 1, 3});
		System.out.println(jo);
	}
}
