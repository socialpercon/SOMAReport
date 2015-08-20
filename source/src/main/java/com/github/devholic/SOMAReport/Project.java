package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Project {

	private final static Logger Log = Logger.getLogger(Project.class);

	// API

	// View
	@GET
	@Path("/project")
	public Response View_Project(@Context Request request) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONArray userProject = ProjectsController.getMyProject(session
					.getAttribute("user_id").toString());
			JSONObject data = new JSONObject();
			data.put("project", userProject);
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_projectlist.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}

	@GET
	@Path("/project/{id}")
	public Response View_ProjectDetail(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			// #66C4DE : waiting
			// #FC6A6A : finish
			ProjectsController projects = new ProjectsController();
			JSONObject userProject = projects.getDetailByProjectId(id);
			JSONObject data = new JSONObject();
			data.put("project", userProject);
			ReportsController reports = new ReportsController();
			data.put("reports", reports.getReportByProjectId(id));
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_project.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}
}
