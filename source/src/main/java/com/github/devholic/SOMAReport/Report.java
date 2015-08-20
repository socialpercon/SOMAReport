package com.github.devholic.SOMAReport;

import java.util.List;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Report {

	private final static Logger Log = Logger.getLogger(Report.class);

	@Context
	UriInfo uri;

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
			data.put("reports", reports.getReportByProjectId(id));
			Log.info(data.toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_reports.mustache",
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
			UserController user = new UserController();
			JSONObject detail = reports.getReportDetailByReportId(id);
			if (detail != null) {
				if (user.getRoleById(session.getAttribute("user_id").toString()) == UserController.ROLE_MENTOR) {
					if (!detail.getJSONObject("report_details").has(
							"opinion-public")) {
						detail.getJSONObject("report_details").put(
								"opinion-public", "true");
					}
				}
				if (!detail.getJSONObject("report_details").has(
						"opinion-public")) {
					detail.getJSONObject("report_details").remove("opinion");
				}
				if (detail.getJSONObject("report_details").has("content")) {
					detail.getJSONObject("report_details").put(
							"content",
							detail.getJSONObject("report_details")
									.getString("content")
									.replaceAll("\r\n", "\n"));
				}
				data.put("detail", detail);
				Log.info(detail.toString());
				data.put("reports", reports.getReportByProjectId(detail
						.getString("project")));
				ProjectsController project = new ProjectsController();
				data.put("project", project.getDetailByProjectId(detail
						.getString("project")));
				return Response
						.status(200)
						.entity(new Viewable("/new/new_report_detail.mustache",
								MustacheHelper.toMap(data))).build();
			} else {
				UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
				builder.path("/project/list");
				return Response.seeOther(builder.build()).build();
			}
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}

	@GET
	@Path("/report/write/{id}")
	public Response View_ReportWrite(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			ReportsController reports = new ReportsController();
			JSONObject write = new JSONObject();
			write.put("pid", id);
			data.put("write", write);
			data.put("report", reports.getReportByProjectId(id));
			ProjectsController project = new ProjectsController();
			data.put("project", project.getDetailByProjectId(id));
			return Response
					.status(200)
					.entity(new Viewable("/user_write.mustache", MustacheHelper
							.toMap(data))).build();
		} else {
			return Response.status(401).entity(new Viewable("/login.mustache"))
					.build();
		}
	}

	@POST
	@Path("/report/write")
	public Response API_ReportWrite(@Context Request request,
			@FormDataParam("projectid") String pid,
			@FormDataParam("place") String place,
			@FormDataParam("start") String start,
			@FormDataParam("attendee") List<String> attendee,
			@FormDataParam("attendeeReason") List<String> absenteeReason,
			@FormDataParam("end") String end,
			@FormDataParam("except") String except,
			@FormDataParam("topic") String topic,
			@FormDataParam("goal") String goal,
			@FormDataParam("issue") String issue,
			@FormDataParam("solution") String solution,
			@FormDataParam("plan") String plan,
			@FormDataParam("opinion") String opinion,
			@FormDataParam("opinion-public") String opinion_public,
			@FormDataParam("etc") String etc,
			@FormDataParam("content") String content) {
		Log.info("reportWrite start!#");
		ReportsController report = new ReportsController();
		JSONArray attendeeArray = new JSONArray();
		JSONArray absenteeArray = new JSONArray();
		for (int a = 0; a < attendee.size(); a++) {
			if (absenteeReason.get(a).length() == 0) {
				JSONObject att = new JSONObject();
				att.put("id", attendee.get(a));
				attendeeArray.put(att);
			} else {
				JSONObject abs = new JSONObject();
				abs.put("id", attendee.get(a));
				abs.put("reason", absenteeReason.get(a));
				absenteeArray.put(abs);
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("project", pid);
		jo.put("attendee", attendeeArray);
		jo.put("absentee", absenteeArray);
		JSONObject info = new JSONObject();
		info.put("place", place);
		String[] startArr = start.split(" ");
		String[] endArr = end.split(":");
		String startDay = startArr[0].toString();
		info.put("date", startDay);
		String start_time = startDay + startArr[1].split(":")[0].toString()
				+ startArr[1].split(":")[1].toString();
		info.put("start_time", start_time);
		info.put("end_time", startDay + endArr[0] + endArr[1]);
		info.put("except_time", 0);
		jo.put("report_info", info);
		Log.info("inserted-report_info: " + jo.toString());
		JSONObject details = new JSONObject();
		details.put("topic", topic);
		details.put("goal", goal);
		details.put("issue", issue);
		details.put("solution", solution);
		details.put("plan", plan);
		details.put("opinion", opinion);
		if (opinion_public != null) {
			details.put("opinion-public", "true");
		}
		if (etc != null) {
			details.put("etc", etc);
		}
		details.put("content", content);
		jo.put("report_details", details);
		jo.put("report_attachments", new JSONObject());
		String id = report.insertReport(jo);
		Log.info("inserted: " + id);
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		if (id != null) {
			builder.path("report/" + id);
		} else {
			builder.path("report/list/" + pid);
		}
		return Response.seeOther(builder.build()).build();
	}
}