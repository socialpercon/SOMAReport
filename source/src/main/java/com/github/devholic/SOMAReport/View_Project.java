package com.github.devholic.SOMAReport;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path("/project")
public class View_Project {

	// Mento / Mentee
	@GET
	@Path("/list")
	@Produces("text/html")
	public Viewable projectList() {
		ProjectsController p = new ProjectsController();
		System.out.println(p.getMyProjects("ppyong0@gmail.com").toString());
		Map<String, Object> projectMap = new Gson().fromJson(
				p.getMyProjects("ppyong0@gmail.com"),
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectList", projectMap);
		return new Viewable("/projectlist.mustache", map);
	}
}