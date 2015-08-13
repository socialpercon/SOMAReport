package com.github.devholic.SOMAReport;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.ProjectsController;

public class ProjectControllerTest {

	ProjectsController pc = new ProjectsController();
	
	@Test
	public void testGetMyProjects() {
		String id = "9d898f7d5bfbf361939e1fafd50470e3";
		JSONArray result = pc.getMyProject(id);
		for (int i=0; i<result.length(); i++) {
			JSONObject jo = result.getJSONObject(i);
			boolean isIn = false;
			if (jo.getString("mentor").equals(id)) isIn = true;
			else {
				JSONArray ar = jo.getJSONArray("mentee");
				for (int j=0; j<ar.length(); j++) {
					if (ar.getString(j).equals(id)) isIn = true;
				}
			}
			assertTrue(isIn);
		}
	}
}
