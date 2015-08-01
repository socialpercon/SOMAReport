package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/project")
public class View_Project {

	@GET
	@Path("/list")
	@Produces("text/html")
	public Viewable projectList() {
		ProjectsController p = new ProjectsController();
		JSONArray ja = new JSONArray(p.getMyProjects(
				"4c44d639b77c290955371694d3310194").toString());
		JSONObject jo = new JSONObject();
		jo.put("projectList", ja);
		return new Viewable("/projectlist.mustache", MustacheHelper.toMap(jo));
	}

}