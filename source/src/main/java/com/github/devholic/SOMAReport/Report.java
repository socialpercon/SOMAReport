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
public class Report {

	private final static Logger Log = Logger.getLogger(Report.class);

	// API

	// View
	@GET
	@Path("/report/list/{id}")
	public Response View_Report(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			ProjectsController project = new ProjectsController();
			data.put("project", project.getDetailByProjectId(id));
			ReportsController reports = new ReportsController();
			JSONArray userReport = reports.getReportByProjectId(id);
			data.put("report", userReport);
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/user_report.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}

	@GET
	@Path("/report/{id}")
	public Response View_ReportDetail(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			ReportsController reports = new ReportsController();
			JSONObject detail = reports.getDetailByReportId(id);
			data.put("detail", detail);
			String pid = detail.getString("project");
			data.put("report", reports.getReportByProjectId(pid));
			ProjectsController project = new ProjectsController();
			data.put("project", project.getDetailByProjectId(pid));
			return Response
					.status(200)
					.entity(new Viewable("/user_report.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}
}