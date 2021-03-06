package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.DriveController;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.common.io.Files;

@Path("/")
public class Report {

	private final static Logger Log = Logger.getLogger(Report.class);

	@Context
	UriInfo uri;

	// API
	@GET
	@Path("/api/report/unconfirmed")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response API_Report_Unconfirmed(@Context Request request) {
		Session session = request.getSession();
		String userId = session.getAttribute("user_id").toString();
		ReportsController rCtrl = new ReportsController();
		JSONArray unconfirmed = rCtrl.getUnconfirmedReports(userId);
		Log.info(unconfirmed);

		if (unconfirmed.length() > 0)
			return Response.status(200)
					.type(MediaType.APPLICATION_JSON + ";charset=utf-8")
					.entity(unconfirmed.toString()).build();
		else
			// unconfirmed reports does not exists
			return Response.status(412).build();
	}

	@POST
	@Path("/api/report/update/{id}")
	public Response API_Update_Report_Photo(@Context Request request,
			@PathParam("id") String reportId, @FormParam("fileId") String fileId) {
		ReportsController rCtrl = new ReportsController();

		if (rCtrl.updateReportPhoto(reportId, fileId))
			return Response.status(200).build();
		else
			return Response.status(400).build();
	}

	@GET
	@Path("/api/report/list/{id}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response API_Report_List(@PathParam("id") String projectId) {
		ReportsController rCtrl = new ReportsController();
		JSONArray list = rCtrl.getReportByProjectId(projectId);

		if (list.length() > 0)
			return Response.status(200)
					.type(MediaType.APPLICATION_JSON + ";charset=utf-8")
					.entity(list.toString()).build();
		else
			return Response.status(412).build();
	}

	@GET
	@Path("/api/report/{id}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response API_Report_Detail(@PathParam("id") String reportId) {
		ReportsController rCtrl = new ReportsController();
		JSONObject report = rCtrl.getReportWithNames(reportId);
		return Response.status(200).type("application/json;charset=utf-8")
				.entity(report.toString()).build();
	}

	@GET
	@Path("/api/report/download/{id}")
	@Produces("application/octet-stream")
	public Response API_GenerateDoc(@PathParam("id") String id) {
		ReportsController report = new ReportsController();
		if (report.renderDocx_mentoringReport(id)) {
			try {
				return Response
						.status(200)
						.header("Content-Disposition",
								"attachment; filename=\"" + id + ".docx" + "\"")
						.entity(Files.toByteArray(new File("cache/" + id
								+ ".docx"))).build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return Response.status(500).build();
			}
		} else {
			return Response.noContent().build();
		}
	}

	// View
	@GET
	@Path("/report/list/{id}")
	public Response View_Report(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			if (session.getAttribute("role").equals("mentor")) {
				data.put("writeauth", true);
			}
			if (session.getAttribute("role").equals("admin")) {
				data.put("admin", true);
			}
			ProjectsController project = new ProjectsController();
			data.put("project", project.getDetailByProjectId(id));
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
					.entity(new Viewable("/new/new_reports.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@GET
	@Path("/report/confirm/{id}")
	public Response View_ReportConfirm(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			DatabaseController db = new DatabaseController();
			JSONObject jo = JSONFactory.inputStreamToJson(db.getDoc(id));
			jo.put("confirmed", true);
			db.updateDoc(jo);
			UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
			builder.path("/report/" + id);
			return Response.seeOther(builder.build()).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@GET
	@Path("/report/{id}")
	public Response View_ReportDetail(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			data.put("reportid", id);
			if (session.getAttribute("role").equals("mentor")) {
				data.put("writeauth", true);
			}
			if (session.getAttribute("role").equals("admin")) {
				data.put("admin", true);
			}
			ReportsController reports = new ReportsController();
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			JSONObject detail = reports.getReportDetailByReportId(id);
			if (detail != null) {
				if (UserController.getRoleById(
						session.getAttribute("user_id").toString()).equals(
						"mentor")) {
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
				String start = detail.getJSONObject("report_info").getString(
						"start_time");
				String end = detail.getJSONObject("report_info").getString(
						"end_time");
				String except = Integer.toString(detail.getJSONObject(
						"report_info").getInt("except_time"));
				String y = start.substring(0, 4);
				String m = start.substring(4, 6);
				String d = start.substring(6, 8);
				String sh = start.substring(8, 10);
				String sm = start.substring(10, 12);
				String eh = end.substring(8, 10);
				String em = end.substring(10, 12);
				detail.getJSONObject("report_info").put(
						"datetime",
						y + "년 " + m + "월 " + d + "일 " + sh + ":" + sm + " ~ "
								+ eh + ":" + em + " | 제외시간 " + except + "분");
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
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@GET
	@Path("/report/write/{id}")
	public Response View_ReportWrite(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			if (!session.getAttribute("role").equals("mentor")) {
				UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
				builder.path("project/list");
				return Response.seeOther(builder.build()).build();
			}
			ProjectsController project = new ProjectsController();
			data.put("project", project.getDetailByProjectId(id));
			ReportsController reports = new ReportsController();
			data.put("reports", reports.getReportByProjectId(id));
			data.put("name", UserController.getUserName(session.getAttribute(
					"user_id").toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			DatabaseController db = new DatabaseController();
			JSONArray drivedocs = JSONFactory.getData(JSONFactory
					.inputStreamToJson(db.getByView("_design/file",
							"projectdrivePlus", id, true, false, false)));
			JSONArray drive = new JSONArray();
			for (int i = 0; i < drivedocs.length(); i++) {
				drive.put(drivedocs.getJSONObject(i).get("doc"));
			}
			Log.info(drive.length());
			if (drive.length() != 0) {
				Log.info(drive.toString());
				data.put("driveFiles", drive);
			}
			return Response
					.status(200)
					.entity(new Viewable("/new/new_report_write.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
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
			@FormDataParam("content") String content,
			@FormDataParam("photo") String photo,
			@FormDataParam("fileList") List<String> fileList) {
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
		JSONArray reportFileArray = new JSONArray();
		for (int a = 0; a < fileList.size(); a++) {
			reportFileArray.put(fileList.get(a));
		}
		JSONObject jo = new JSONObject();
		jo.put("project", pid);
		jo.put("attendee", attendeeArray);
		jo.put("absentee", absenteeArray);
		JSONObject info = new JSONObject();
		info.put("place", place);
		String[] startArr = start.split(" ");
		String[] endArr = end.split(" ");
		String startDay = startArr[0].toString();
		startDay = startDay.replaceAll("-", "");
		info.put("date", startDay);
		String start_time = startDay + startArr[1].split(":")[0].toString()
				+ startArr[1].split(":")[1].toString();
		info.put("start_time", start_time);
		String endParsed = endArr[1].split(":")[0].toString()
				+ endArr[1].split(":")[1].toString();
		info.put("end_time", startDay + endParsed);
		info.put("except_time", except);
		jo.put("report_info", info);
		Log.info("inserted-report_info: " + jo.toString());
		JSONObject details = new JSONObject();
		details.put("topic", topic);
		details.put("goal", goal);
		details.put("issue", issue);
		details.put("solution", solution);
		details.put("plan", plan);
		details.put("opinion", opinion);
		details.put("photo", photo);
		if (opinion_public != null) {
			details.put("opinion-public", "true");
		}
		if (etc != null) {
			details.put("etc", etc);
		}
		details.put("content", content);
		jo.put("report_details", details);
		jo.put("report_attachments", reportFileArray);
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		String id = report.insertReport(jo);
		Log.info("inserted: " + id);
		if (id != null) {
			builder.path("report/" + id);
		} else {
			builder.path("report/list/" + pid);
		}
		return Response.seeOther(builder.build()).build();
	}

	@GET
	@Path("/report/update/{id}")
	public Response View_ReportUpdate(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			if (!session.getAttribute("role").equals("mentor")) {
				UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
				builder.path("project/list");
				return Response.seeOther(builder.build()).build();
			}
			data.put("name", UserController.getUserName(session.getAttribute(
					"user_id").toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			DatabaseController db = new DatabaseController();
			JSONObject target = JSONFactory.inputStreamToJson(db.getDoc(id));
			Log.info(target.toString());
			data.put("rid", target.getString("_id"));
			data.put("rrev", target.getString("_rev"));
			JSONArray people = new JSONArray();
			for (int i = 0; i < target.getJSONArray("absentee").length(); i++) {
				JSONObject person = new JSONObject();
				person.put("id",
						target.getJSONArray("absentee").getJSONObject(i)
								.getString("id"));
				person.put(
						"name",
						UserController.getUserName(target
								.getJSONArray("absentee").getJSONObject(i)
								.getString("id")));
				person.put("reason", target.getJSONArray("absentee")
						.getJSONObject(i).getString("reason"));
				people.put(person);
			}
			for (int i = 0; i < target.getJSONArray("attendee").length(); i++) {
				JSONObject person = new JSONObject();
				person.put("id",
						target.getJSONArray("attendee").getJSONObject(i)
								.getString("id"));
				person.put(
						"name",
						UserController.getUserName(target
								.getJSONArray("attendee").getJSONObject(i)
								.getString("id")));
				person.put("reason", "");
				people.put(person);
			}
			String st = target.getJSONObject("report_info").getString(
					"start_time");
			String et = target.getJSONObject("report_info").getString(
					"end_time");
			data.put("starttime", st.substring(0, 4) + "-" + st.substring(4, 6)
					+ "-" + st.substring(6, 8) + " " + st.substring(8, 10)
					+ ":" + st.substring(10, 12));
			data.put("endtime", et.substring(0, 4) + "-" + et.substring(4, 6)
					+ "-" + et.substring(6, 8) + " " + et.substring(8, 10)
					+ ":" + et.substring(10, 12));
			data.put("excepttime", target.getJSONObject("report_info")
					.getString("except_time"));
			data.put("place",
					target.getJSONObject("report_info").getString("place"));
			data.put("topic",
					target.getJSONObject("report_details").getString("topic"));
			data.put("goal",
					target.getJSONObject("report_details").getString("goal"));
			data.put("issue",
					target.getJSONObject("report_details").getString("issue"));
			data.put("solution", target.getJSONObject("report_details")
					.getString("solution"));
			data.put("plan",
					target.getJSONObject("report_details").getString("plan"));
			data.put("opinion", target.getJSONObject("report_details")
					.getString("opinion"));
			data.put("etc",
					target.getJSONObject("report_details").getString("etc"));
			data.put("content", target.getJSONObject("report_details")
					.getString("content"));
			JSONObject photo = new JSONObject();
			photo.put("id",
					target.getJSONObject("report_details").getString("photo"));
			JSONObject photoRaw = JSONFactory.inputStreamToJson(db
					.getDoc(target.getJSONObject("report_details").getString(
							"photo")));
			photo.put("name", photoRaw.getString("name"));
			data.put("photo", photo);
			JSONArray files = new JSONArray();
			for (int i = 0; i < target.getJSONArray("report_attachments")
					.length(); i++) {
				JSONObject f = new JSONObject();
				f.put("id", target.getJSONArray("report_attachments")
						.getString(i));
				JSONObject fRaw = JSONFactory.inputStreamToJson(db
						.getDoc(target.getJSONArray("report_attachments")
								.getString(i)));
				f.put("name", fRaw.getString("name"));
				files.put(f);
			}
			data.put("files", files);
			ProjectsController project = new ProjectsController();
			data.put("project",
					project.getDetailByProjectId(target.getString("project")));
			ReportsController reports = new ReportsController();
			data.put("reports",
					reports.getReportByProjectId(target.getString("project")));
			return Response
					.status(200)
					.entity(new Viewable("/new/new_report_update.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@POST
	@Path("/report/update")
	public Response API_ReportUpdate(@Context Request request,
			@FormDataParam("reportid") String rid,
			@FormDataParam("reportrev") String rev,
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
			@FormDataParam("content") String content,
			@FormDataParam("photo") String photo,
			@FormDataParam("reportFiles") List<String> reportFiles) {
		DatabaseController db = new DatabaseController();
		JSONObject target = JSONFactory.inputStreamToJson(db.getDoc(rid));
		db.updateDoc(target);
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
		JSONArray reportFileArray = new JSONArray();
		for (int a = 0; a < reportFiles.size(); a++) {
			reportFileArray.put(reportFiles.get(a));
		}
		target.remove("attendee");
		target.put("attendee", attendeeArray);
		target.remove("absentee");
		target.put("absentee", absenteeArray);
		JSONObject info = new JSONObject();
		info.put("place", place);
		String[] startArr = start.split(" ");
		String[] endArr = end.split(" ");
		String startDay = startArr[0].toString();
		startDay = startDay.replaceAll("-", "");
		info.put("date", startDay);
		String start_time = startDay + startArr[1].split(":")[0].toString()
				+ startArr[1].split(":")[1].toString();
		info.put("start_time", start_time);
		String endParsed = endArr[1].split(":")[0].toString()
				+ endArr[1].split(":")[1].toString();
		info.put("end_time", startDay + endParsed);
		info.put("except_time", except);
		target.remove("report_info");
		target.put("report_info", info);
		JSONObject details = new JSONObject();
		details.put("topic", topic);
		details.put("goal", goal);
		details.put("issue", issue);
		details.put("solution", solution);
		details.put("plan", plan);
		details.put("opinion", opinion);
		details.put("photo", photo);
		if (opinion_public != null) {
			details.put("opinion-public", "true");
		}
		if (etc != null) {
			details.put("etc", etc);
		}
		details.put("content", content);
		target.remove("report_details");
		target.put("report_details", details);
		target.remove("report_attachments");
		target.put("report_attachments", reportFileArray);
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		db.updateDoc(target);
		Log.info("inserted: " + rid);
		builder.path("report/" + rid);
		return Response.seeOther(builder.build()).build();
	}
}