package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
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
			JSONArray userProject = ProjectsController.getMyProject(session.getAttribute(
					"user_id").toString());
			JSONObject data = new JSONObject();
			data.put("project", userProject);
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/user_project.mustache", MustacheHelper
							.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}
}
