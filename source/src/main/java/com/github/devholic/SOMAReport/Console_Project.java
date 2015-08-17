package com.github.devholic.SOMAReport;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.RegisterController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Console_Project {

	private final static Logger Log = Logger.getLogger(Console_Project.class);

	@Context
	UriInfo uri;

	@GET
	@Path("/console/project")
	public Response consoleProject() {
		// getConsoleProject를 한 다음에 MustacheHelper.toMap으로 데이터를 넘겨주면 끗!
		ProjectsController projects = new ProjectsController();
		JSONObject jo = new JSONObject();
		jo.put("stageInfo", projects.existingStage());
		return Response
				.status(200)
				.entity(new Viewable("/console_project.mustache",
						MustacheHelper.toMap(jo))).build();
	}

	@GET
	@Path("/console/project/{id}")
	public Response consoleProjectDetail(@PathParam("id") String id) {
		// getConsoleProject를 한 다음에 MustacheHelper.toMap으로 데이터를 넘겨주면 끗!
		ProjectsController projects = new ProjectsController();
		JSONObject jo = new JSONObject();
		jo.put("stageInfo", projects.existingStage());
		return Response
				.status(200)
				.entity(new Viewable("/console_project.mustache",
						MustacheHelper.toMap(jo))).build();
	}

	@POST
	@Path("/console/project/upload")
	public Response consoleProject(@FormDataParam("file") InputStream is) {
		RegisterController register = new RegisterController(is);
		register.registerProject();
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		builder.path("console/project");
		return Response.status(200)
				.entity("{\"code\":\"1\", \"msg\":\"file upload success.\"}")
				.build();
	}
}
