package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Project {

	private final static Logger Log = Logger.getLogger(Project.class);

	// API
	@GET
	@Path("/api/project/list")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response API_Project(@Context Request request) {
		Session session = request.getSession();
		JSONArray userProject = ProjectsController.getMyProject(session
				.getAttribute("user_id").toString());

		return Response.status(200).entity(userProject.toString()).build();
	}

	// View
	@GET
	@Path("/project/list")
	public Response View_Project(@Context Request request) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONArray userProject = ProjectsController.getMyProject(session
					.getAttribute("user_id").toString());
			JSONObject data = new JSONObject();
			if (session.getAttribute("role").equals("admin")) {
				data.put("admin", true);
			}
			data.put("project", userProject);
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_projectlist.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@GET
	@Path("/project/{id}")
	public Response View_ProjectDetail(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			if (session.getAttribute("role").equals("admin")) {
				data.put("admin", true);
			}
			ProjectsController projects = new ProjectsController();
			data.put("project", projects.getDetailByProjectId(id));
			ReportsController reports = new ReportsController();
			data.put("reports", reports.getReportByProjectId(id));
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_project.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}
}
