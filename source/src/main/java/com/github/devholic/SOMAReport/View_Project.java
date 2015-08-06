package com.github.devholic.SOMAReport;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
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
	public Response projectList(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			System.out.println("checkId: "+session.getAttribute("user_id").toString());
			ProjectsController p = new ProjectsController();
			JSONArray ja = new JSONArray(p.getMyProjects(
					session.getAttribute("user_id").toString()).toString());
			JSONObject jo = new JSONObject();
			jo.put("projectList", ja);
			System.out.println(ja.toString());
			return Response
					.status(200)
					.entity(new Viewable("/projectlist.mustache",
							MustacheHelper.toMap(jo))).build();
		} else {
			return Response.seeOther(new URI("http://localhost:8080/login"))
					.build();
		}
	}
}