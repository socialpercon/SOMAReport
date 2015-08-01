package com.github.devholic.SOMAReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Model.Projects;

@Path("/project")
public class View_Project {

	// Mento / Mentee
	@GET
	@Path("/list")
	@Produces("text/html")
	public Viewable projectList() {
		ProjectsController p = new ProjectsController();
		List<Projects> l = p.getProjectList();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectList", l);
		return new Viewable("/projectlist.mustache", map);
	}
	// Office
	@GET
	@Path("/list/{stage}/{level}/{d}")
	@Produces("text/html")
	public Viewable officeProjectList() {
		ProjectsController p = new ProjectsController();
		List<Projects> l = p.getProjectList();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectList", l);
		return new Viewable("/office_projectlist.mustache", map);
	}
}