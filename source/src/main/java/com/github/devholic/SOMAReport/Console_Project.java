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
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.RegisterController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Console_Project {

	private final static Logger Log = Logger.getLogger(Console_Project.class);

	@Context
	UriInfo uri;

	@GET
	@Path("/console/stage")
	public Response consoleProject(@Context Request request) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			ProjectsController projects = new ProjectsController();
			JSONObject data = new JSONObject();
			data.put("stages", projects.existingStage());
			Log.info(data);
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_console_stagelist.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/new/new_login.mustache"))
					.build();
		}
	}

	@GET
	@Path("/console/project/list/{id}")
	public Response consoleProjectDetail(@PathParam("id") String id) {
		ProjectsController projects = new ProjectsController();
		DatabaseController db = new DatabaseController();
		JSONObject jo = new JSONObject();
		jo.put("stage",
				(JSONFactory.inputStreamToJson(db.getDoc(id)).toString()));
		jo.put("projects", projects.projectsInStageInfo(id));
		Log.info(jo.toString());
		return Response
				.status(200)
				.entity(new Viewable("/new/new_console_projectlist.mustache",
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
