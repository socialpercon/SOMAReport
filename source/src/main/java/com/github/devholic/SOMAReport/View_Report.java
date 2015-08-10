package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Path("/report")
public class View_Report {

	@GET
	@Path("/list/{id}")
	public Response report(@Context Request request, @PathParam("id") String id)
			throws URISyntaxException {
		ReportsController r = new ReportsController();
		JSONArray ja = r.getReportByProjectId(id);
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			DocumentUtil dutil = new DocumentUtil("somarecord");
			JsonObject project = dutil.getDoc(id).getAsJsonObject();
			boolean checked = false;
			if (project.get("mentor").getAsString()
					.equals(session.getAttribute("user_id"))) {
				checked = true;
			} else {
				for (int i = 0; i < project.get("mentee").getAsJsonArray()
						.size(); i++) {
					if (project.get("mentee").getAsJsonArray().get(i)
							.getAsString()
							.equals(session.getAttribute("user_id"))) {
						checked = true;
						break;
					}
				}
			}
			if (checked) {
				JSONObject jo = new JSONObject();
				jo.put("reportList", ja);
				for (int i = 0; i < ja.length(); i++) {
					JSONArray attendee = new JSONArray();
					JsonArray gja = (JsonArray) ja.getJSONObject(i).get(
							"attendee");
					for (int j = 0; j < gja.size(); j++) {
						attendee.put(gja.get(j).getAsString());
					}
					ja.getJSONObject(i).remove("attendee");
					ja.getJSONObject(i).put("attendee", attendee);
				}
				jo.put("pid", id);
				jo.put("pname", dutil.getDoc(id).getAsJsonObject().get("title")
						.getAsString());
				return Response.ok(
						new Viewable("/reportlist.mustache", MustacheHelper
								.toMap(jo))).build();
			} else {
				return Response.seeOther(
						new URI("http://localhost:8080/project/list")).build();
			}
		} else {
			return Response.seeOther(new URI("http://localhost:8080/login"))
					.build();
		}
	}

	@GET
	@Path("/{id}")
	@Produces("text/html")
	public Response reportDetail(@Context Request request,
			@PathParam("id") String id) throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			DocumentUtil dutil = new DocumentUtil("");
			ReferenceUtil rutil = new ReferenceUtil("");
			JSONObject jo = new JSONObject();
			JSONObject report = rutil.getReportWithNames(id);
			JsonObject project_raw = dutil.getDoc(
					report.get("project").toString()).getAsJsonObject();
			JSONObject project = new JSONObject(dutil
					.getDoc(report.get("project").toString()).getAsJsonObject()
					.toString());
			boolean auth = false;
			if (project.getString("mentor").equals(
					session.getAttribute("user_id").toString())) {
				jo.put("isMentor", true);
				auth = true;
				report.getJSONObject("report_details").put("opinion-public",
						"true");
			} else {
				JsonArray menteeList = project_raw.get("mentee")
						.getAsJsonArray();
				for (int i = 0; i < menteeList.size(); i++) {
					if (menteeList.get(i).getAsString()
							.equals(session.getAttribute("user_id").toString())) {
						auth = true;
						break;
					}
				}
			}
			if (!auth) {
				return Response.seeOther(
						new URI("http://localhost:8080/project/list")).build();
			} else {
				jo.put("rid", id);
				jo.put("title", report.getJSONObject("report_info").get("date")
						.toString().replaceAll("-", ""));
				jo.put("pid", project.get("_id").toString());
				jo.put("pname", project.get("title").toString());
				jo.put("report", report);
				return Response.ok(
						new Viewable("/reportdetail.mustache", MustacheHelper
								.toMap(jo))).build();
			}
		} else {
			return Response.seeOther(new URI("http://localhost:8080/login"))
					.build();
		}
	}

	@GET
	@Path("/write/{id}")
	@Produces("text/html")
	public Response writeReport(@Context Request request,
			@PathParam("id") String id) throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			DocumentUtil dutil = new DocumentUtil("somarecord");
			JSONObject project = new JSONObject(dutil.getDoc(id)
					.getAsJsonObject().toString());
			JSONObject jo = new JSONObject();
			jo.put("pid", id);
			jo.put("pname", project.get("title").toString());
			jo.put("mentee", project.get("mentee"));
			return Response.ok(
					new Viewable("/reportwrite.mustache", MustacheHelper
							.toMap(jo))).build();
		} else {
			return Response.seeOther(
					new URI("http://localhost:8080/project/list")).build();
		}
	}

	@POST
	@Path("/write")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html")
	public Response doWriteReport(@FormDataParam("projectid") String pid,
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
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition formData)
			throws URISyntaxException, IOException, ParseException {
		String fileLocation = formData.getFileName();
		try {
			if (fileLocation.length() != 0) {
				saveFile(is, fileLocation);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		ReportsController r = new ReportsController();
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
		String rid = r.insertReport(jo);
		if (fileLocation.length() != 0) {
			if (rid != null) {
				View_Drive.driveUploadImage("0", rid, new File(fileLocation));
			} else {
				// Error
			}
		}
		return Response.seeOther(
				new URI("http://localhost:8080/report/list/" + pid)).build();
	}

	@GET
	@Path("/update/{id}")
	@Produces("text/html")
	public Response updateReport(@PathParam("id") String id) {
		DocumentUtil dutil = new DocumentUtil("somarecord");
		JSONObject report = new JSONObject(dutil.getDoc(id).getAsJsonObject()
				.toString());
		JSONObject jo = new JSONObject();
		jo.put("pid", id);
		jo.put("rid", report.get("project"));
		jo.put("date", report.getJSONObject("report_info").get("date"));
		jo.put("topic", report.getJSONObject("report_details").get("topic"));
		jo.put("goal", report.getJSONObject("report_details").get("goal"));
		jo.put("issue", report.getJSONObject("report_details").get("issue"));
		jo.put("solution",
				report.getJSONObject("report_details").get("solution"));
		jo.put("plan", report.getJSONObject("report_details").get("plan"));
		jo.put("opinion", report.getJSONObject("report_details").get("opinion"));
		return Response
				.ok(new Viewable("/reportupdate.mustache", MustacheHelper
						.toMap(jo))).build();
	}

	@POST
	@Path("/update/{id}")
	@Produces("text/html")
	public Response doUpdateReport(@PathParam("id") String id,
			@FormDataParam("topic") String date,
			@FormDataParam("goal") String goal,
			@FormDataParam("date") String issue,
			@FormDataParam("date") String solution,
			@FormDataParam("date") String plan,
			@FormDataParam("date") String opinion) throws URISyntaxException {
		DocumentUtil dutil = new DocumentUtil("somarecord");
		JSONObject report = new JSONObject(dutil.getDoc(id).getAsJsonObject()
				.toString());
		return Response.seeOther(new URI("http://localhost:8080/project/list"))
				.build();
	}

	private void saveFile(InputStream is, String fileLocation)
			throws IOException {
		OutputStream os = new FileOutputStream(new File(fileLocation));
		byte[] buffer = new byte[256];
		int bytes = 0;
		while ((bytes = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
		os.close();
	}
}
