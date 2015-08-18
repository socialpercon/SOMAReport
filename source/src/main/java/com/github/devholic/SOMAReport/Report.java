package com.github.devholic.SOMAReport;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
		JsonArray attendeeArray = new JsonArray();
		JsonArray absenteeArray = new JsonArray();
		for (int a = 0; a < attendee.size(); a++) {
			if (absenteeReason.get(a).length() == 0) {
				attendeeArray.add(new JsonPrimitive(attendee.get(a)));
			} else {
				JsonObject abs = new JsonObject();
				abs.addProperty("id", attendee.get(a));
				abs.addProperty("reason", absenteeReason.get(a));
				absenteeArray.add(abs);
			}
		}
		JsonObject jo = new JsonObject();
		jo.addProperty("project", pid);
		JsonObject info = new JsonObject();
		info.addProperty("place", place);
		String[] startArr = start.split(" ");
		String[] endArr = end.split(":");
		String startDay = startArr[0].toString();
		info.addProperty("date", startDay);
		JsonArray sja = new JsonArray();
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(0, 4))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(4, 6))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(6, 8))));
		sja.add(new JsonPrimitive(Integer.parseInt(startArr[1].split(":")[0])));
		sja.add(new JsonPrimitive(Integer.parseInt(startArr[1].split(":")[1])));
		info.add("start_time", sja);
		sja = new JsonArray();
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(0, 4))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(4, 6))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(6, 8))));
		sja.add(new JsonPrimitive(Integer.parseInt(endArr[0])));
		sja.add(new JsonPrimitive(Integer.parseInt(endArr[1])));
		info.add("end_time", sja);
		jo.add("attendee", attendeeArray);
		jo.add("absentee", absenteeArray);
		info.addProperty("except_time", 0);
		jo.add("report_info", info);
		JsonObject details = new JsonObject();
		details.addProperty("topic", topic);
		details.addProperty("goal", goal);
		details.addProperty("issue", issue);
		details.addProperty("solution", solution);
		details.addProperty("plan", plan);
		details.addProperty("opinion", opinion);
		if (opinion_public != null) {
			details.addProperty("opinion-public", "true");
		}
		if (etc != null) {
			details.addProperty("etc", etc);
		}
		details.addProperty("content", content);
		jo.add("report_details", details);
		return Response.ok().build();
	}
}